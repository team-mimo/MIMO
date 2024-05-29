package com.ssafy.mimo.socket.global;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SocketServerRunner implements CommandLineRunner {

    private final SocketController server;

    public SocketServerRunner(SocketController server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start(65432); // 65432 포트에서 서버 시작
    }
}
