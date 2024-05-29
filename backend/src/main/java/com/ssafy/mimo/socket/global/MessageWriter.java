package com.ssafy.mimo.socket.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

@Getter
@AllArgsConstructor
public class MessageWriter implements Runnable {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private Long hubId;
    private Socket socket;
    private BlockingQueue<String> messageQueue;
    @Override
    public void run() {
        log.info("MessageWriter {}: Started", hubId);
        while (!socket.isClosed()) {
            try {
                // Take the message from the queue
                String message = messageQueue.take();
                log.info("MessageWriter {}: Took message from the queue\n{}", hubId, message);
                socket.getOutputStream().write(message.getBytes());
            } catch (InterruptedException e) {
                // Error while taking the message
                log.error("MessageWriter {}: Error while taking the message from queue\n{}", hubId, e.getMessage());
                break;
            } catch (Exception e) {
                // Error while writing the message
                log.error("MessageWriter {}: Error while writing the message\n{}", hubId, e.getMessage());
                break;
            }
        }
        log.info("MessageWriter {}: Stopped", hubId);
    }
    // Send a message to the hub through the message queue
    public boolean enqueueMessage(String message) {
        return messageQueue.offer(message);
    }
}
