package com.minecraft.mchanneltool.bot;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.nio.ByteBuffer;

public interface BotWebSocket {


    /**
     * Initiates the websocket connection. This method does not block.
     */
    void connect();


    /**
     * Convenience function which behaves like close(CloseFrame.NORMAL)
     */
    void close();


    /**
     * Send Text data to the other end.
     *
     * @param text the text data to send
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void send(String text);

    /**
     * Send Binary data (plain bytes) to the other end.
     *
     * @param bytes the binary data to send
     * @throws IllegalArgumentException       the data is null
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void send(ByteBuffer bytes);

    /**
     * Send Binary data (plain bytes) to the other end.
     *
     * @param bytes the byte array to send
     * @throws IllegalArgumentException       the data is null
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void send(byte[] bytes);


    void reconnect();
}
