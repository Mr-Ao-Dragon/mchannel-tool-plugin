package com.minecraft.mchanneltool.bot;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;

public abstract class BotWebSocketClient extends WebSocketClient implements BotWebSocket {
    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri the server URI to connect to
     */
    public BotWebSocketClient(URI serverUri) {
        super(serverUri);
    }
}
