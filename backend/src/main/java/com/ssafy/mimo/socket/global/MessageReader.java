package com.ssafy.mimo.socket.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;

import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
public class MessageReader implements Runnable {
    private Long hubId;
    private Socket socket;
    private final SocketService socketService;
    private final SocketController socketController;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    @Override
    public void run() {
        log.info("MessageReader {}: Started", hubId);
        // Initialize
        ConcurrentHashMap<String, String> messages = SocketController.getReceivedMessages();
        // Read message from the hub until the connection is closed
        while (!socket.isClosed()) {
            String message;
            JsonNode json_message;
            try { // Read the message
                message = SocketService.readMessage(socket.getInputStream());
                json_message = parseMessage(message);
            } catch (Exception e) { // Error while reading the message
//                System.out.printf("MessageReader %d: Error while reading the message\n%s\n", hubId, e.getMessage());
                socketController.closeConnection(hubId); // Clean up
                break;
            }
            if (isRequest(json_message)) { // Hub의 요청인 경우 응답 반환
                ObjectNode response = socketService.handleRequest(message);
                if (response == null) continue;
                try { // Send the response
                    socket.getOutputStream().write(response.toString().getBytes());
                    log.info("MessageReader {}: Sent response\n{}", hubId, response.toString());
                } catch (Exception e) { // Error while sending the response
                    log.error("MessageReader {}: Error while sending the response\n{}", hubId, e.getMessage());
                    socketController.closeConnection(hubId); // Clean up
                    break;
                }
            } else { // Hub의 응답인 경우 메시지 맵에 저장
                log.info("MessageReader {}: Put message in the queue\n{}", hubId, message);
                String requestId = getRequestId(json_message);
                SocketController.getRequestIds().get(hubId).add(requestId);
                // 메시지에서 requestId 제거 후 저장
                ObjectNode messageNode = (ObjectNode) json_message;
                messageNode.remove("requestId");
                messages.put(requestId, messageNode.toString());
                // Complete the future
                CompletableFuture<String> future = SocketController.getFutureReceivedMessages().get(requestId);
                if (future != null) {
                    future.complete(messageNode.toString());
                }
                // Log messages
            }
            log.info("MessageReader {}: Received message\n{}", hubId, message);
        }
        log.info("MessageReader {}: Stopped", hubId);
    }
    private static boolean isRequest(JsonNode jsonNode) {
        return !jsonNode.has("requestId");
    }
    private static JsonNode parseMessage(String message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(message);
    }
    private static String getRequestId(JsonNode request) {
        return request.get("requestId").asText();
    }
}
