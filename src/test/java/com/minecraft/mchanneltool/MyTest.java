package com.minecraft.mchanneltool;

import com.minecraft.mchanneltool.bot.BotClientManage;
import com.minecraft.mchanneltool.bot.BotWebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class MyTest {
    private static class BotWebSocketClientImpl extends BotWebSocketClient {

        /**
         * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
         * channel does not attampt to connect automatically. The connection will be established once you
         * call <var>connect</var>.
         *
         * @param serverUri the server URI to connect to
         */
        public BotWebSocketClientImpl(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            this.send("你好");
        }

        @Override
        public void onMessage(String message) {
            System.out.println(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }
    }


    public void test(){
        try {
            BotWebSocketClientImpl botWebSocketClient = new BotWebSocketClientImpl(new URI("ws://43.156.231.103:8090/expand-websocket"));
            botWebSocketClient.connect();
            Thread.sleep(5000);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
