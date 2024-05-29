package com.ssafy.mimo.socket.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.mimo.socket.global.dto.HubConnectionRequestDto;
import com.ssafy.mimo.socket.global.dto.HubConnectionResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class SocketController {
    private final SocketService socketService;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private static ConcurrentHashMap<Long, Socket> connections;
    @Getter
    private static ConcurrentHashMap<String, String> receivedMessages;
    @Getter
    private static ConcurrentHashMap<String, CompletableFuture<String>> futureReceivedMessages;
    private static ConcurrentHashMap<Long, Thread> messageWriterThreads;
    @Getter
    private static ConcurrentHashMap<Long, MessageWriter> messageWriters;
    @Getter
    private static ConcurrentHashMap<Long, List<String>> requestIds;
    public void start(int port) {
        try {
            // Initialize
            ServerSocket serverSocket = new ServerSocket(port);
            connections = new ConcurrentHashMap<>();
            receivedMessages = new ConcurrentHashMap<>();
            futureReceivedMessages = new ConcurrentHashMap<>();
            messageWriterThreads = new ConcurrentHashMap<>();
            messageWriters = new ConcurrentHashMap<>();
            requestIds = new ConcurrentHashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            // Accept connections from the hub
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                HubConnectionRequestDto request;
                String req = SocketService.readMessage(socket.getInputStream());
                try {
                    request = objectMapper.readValue(req, HubConnectionRequestDto.class);
                } catch (IOException e) { // Error parsing the connection request
                    System.out.printf("Socket Controller: Error parsing the connection request\n%s\n", e.getMessage());
                    socket.close();
                    continue;
                }
                // Hub validation
                if (request.getType().equals("hub") && request.getRequestName().equals("setConnect")) {
                    System.out.printf("Socket Controller: Connection request from hub(%s)\n%s\n", request.getHubSerialNumber(), req);
                    Long hubId = socketService.getHubId(request);
                    if (hubId == null) {
                        System.out.println("Invalid hub ID");
                        socket.close();
                        continue;
                    }
                    log.info("Connected hub {}", hubId);
                    List<String> idList = new ArrayList<>();
                    requestIds.put(hubId, idList);
                    // Add the connection to the map
                    connections.put(hubId, socket);
                    // Start the message reader and writer
                    MessageWriter messageWriter = new MessageWriter(hubId, socket, new LinkedBlockingQueue<>());
                    messageWriters.put(hubId, messageWriter);
                    Thread reader = new Thread(new MessageReader(hubId, socket, socketService, this));
                    Thread writer = new Thread(messageWriter);
                    messageWriterThreads.put(hubId, writer);
                    reader.start();
                    writer.start();
                    // Send the connection response
                    HubConnectionResponseDto response = HubConnectionResponseDto.builder()
                            .type("hub")
                            .requestName("setConnect")
                            .hubId(hubId)
                            .build();
                    socket.getOutputStream().write(objectMapper.writeValueAsString(response).getBytes());
                } else {
                    log.info("Invalid connection request: {}", req);
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.info("Error starting the server: {}", e.getMessage());
        }
    }
    // Close the connection with the hub
    public void closeConnection(Long hubId) {
        log.info("Closing connection with hub {}", hubId);
        // Remove all the data related to the hub
        messageWriters.remove(hubId);
        List<String> idList = requestIds.get(hubId);
        for (String id : idList) {
            receivedMessages.remove(id);
        }
        requestIds.remove(hubId);
        Thread writer = messageWriterThreads.get(hubId);
        if (writer != null) {
            writer.interrupt();
        }
        messageWriterThreads.remove(hubId);
        // Close the connection
        try {
            Socket socket = connections.get(hubId);
            connections.remove(hubId);
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("Error closing the connection with hub {}: {}", hubId, e.getMessage());
        }
        log.info("Connection with hub {} removed", hubId);
    }
    // Get message
    public static String getMessage(Long hubId, String requestId) {
        Logger log = org.slf4j.LoggerFactory.getLogger(SocketController.class);
        if (requestId == null) {
            return null;
        }
        String message = receivedMessages.get(requestId);
        if (message != null)
            return receivedMessages.remove(requestId);
        // If the message is not present, create a CompletableFuture and put it in the futureMap
        CompletableFuture<String> future = new CompletableFuture<>();
        futureReceivedMessages.put(requestId, future);
        try {
            // Wait for the message to be added with a timeout of 3 seconds
            String response = future.get(3, TimeUnit.SECONDS);
            futureReceivedMessages.remove(requestId);
            receivedMessages.remove(requestId);
            requestIds.get(hubId).remove(requestId);
            return response;
        } catch (Exception e) {
            // If the timeout expires, remove the future from the map and rethrow the exception
            futureReceivedMessages.remove(requestId);
            log.error("Error while getting the message: {}", e.getMessage());
            return null;
        }
    }
    // Send message
    public static String sendMessage(Long hubId, String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode messageNode = objectMapper.readValue(message, ObjectNode.class);
            String requestId = UUID.randomUUID().toString();
            messageNode.put("requestId", requestId);
            MessageWriter messageWriter = messageWriters.get(hubId);
            if (messageWriter != null && messageWriter.enqueueMessage(messageNode.toString())) {
                // Add the request ID to the list
                requestIds.computeIfAbsent(hubId, v -> new ArrayList<>());
                requestIds.get(hubId).add(requestId);
                return requestId;
            }
            return null;
        } catch (IOException e) {
            Logger log = org.slf4j.LoggerFactory.getLogger(SocketController.class);
            log.error("Error while sending the message: {}", e.getMessage());
            return null;
        }
    }
}
