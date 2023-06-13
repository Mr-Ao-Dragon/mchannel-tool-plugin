package com.minecraft.mchanneltool.bot.event;

import com.alibaba.fastjson2.JSONObject;
import com.minecraft.mchanneltool.annotation.OnBotEvent;
import com.minecraft.mchanneltool.bot.*;
import com.minecraft.mchanneltool.bot.v1.BotContextImpl;
import com.minecraft.mchanneltool.config.ConfigBase;
import com.minecraft.mchanneltool.config.Constant;
import com.minecraft.mchanneltool.pojo.*;
import com.minecraft.mchanneltool.utils.RSAUtils;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BotEventWebsocket implements BotEventBase {
    BotClientManage manage;

    BotWebSocket webSocket;

    Map<String, BotContext> contextMap = new ConcurrentHashMap<>();


    private static class BotWebSocketClientImpl extends BotWebSocketClient {
        BotClientManage manage;
        BotEventWebsocket websocket;

        /**
         * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
         * channel does not attampt to connect automatically. The connection will be established once you
         * call <var>connect</var>.
         *
         * @param serverUri the server URI to connect to
         */
        public BotWebSocketClientImpl(URI serverUri, BotClientManage manage, BotEventWebsocket websocket) {
            super(serverUri);
            this.manage = manage;
            this.websocket = websocket;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            manage.getLogger().info("连接机器人服务器成功");
            manage.getLogger().info("发送验证包");
            WebSocketVerify webSocketVerify = new WebSocketVerify()
                    .setT(System.currentTimeMillis() / 7)
                    .setToken(Constant.WS_TOKEN);
            BotContext context = new BotContextImpl(null, null, this.manage);
            manage.putBotEvent(context, WebSocketVerify.class, webSocketVerify);
        }

        @Override
        public void onMessage(String message) {
            try {
                WebSocketBody body = JSONObject.parseObject(message, WebSocketBody.class);
                BotContext botContext = this.websocket.getBotContext(body.getId());
                if (botContext == null) {
                    // 创建新会话
                    BotContext context = new BotContextImpl(null, null, this.manage);
                    manage.putBotEvent(context, WebSocketBody.class, body);
                    return;
                }
                botContext.putEvent(WebSocketBody.class, body);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            manage.getLogger().warning("机器人服务器连接被断开,正在尝试重连...");
            try {
                Thread.sleep(3000);
                this.websocket.reconnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
                manage.getLogger().warning("重连异常");
            }
        }

        @Override
        public void onError(Exception ex) {
            manage.getLogger().warning("连接机器人服务器异常，嵌套异常为 -> " + ex);
            manage.getLogger().warning("正在尝试重连...");
            try {
                Thread.sleep(3000);
                this.websocket.reconnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
                manage.getLogger().warning("重连异常");
            }
        }
    }

    @Override
    public void init(BotClientManage manage) {
        this.manage = manage;
        try {
            this.webSocket = new BotWebSocketClientImpl(new URI(ConfigBase.wsUri), this.manage, this);
            this.webSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            manage.getLogger().warning("连接机器人服务器异常，嵌套异常为 -> " + e);
        }
    }

    /**
     * 重连
     */
    public void reconnection() {
        try {
            this.webSocket.close();
        } catch (Throwable ignored) {

        }
        new Thread(() -> {
            try {
                BotEventWebsocket.this.webSocket.reconnect();
            } catch (Throwable ignored) {

            }
        }).start();
    }

    public void putBotContext(String id, BotContext context) {
        // todo 删除过期的context
        contextMap.put(id, context);
    }

    public BotContext getBotContext(String id) {
        return contextMap.get(id);
    }

    @OnBotEvent
    public void asyncPlayerPreLoginEvent(BotContext context, ChannelBind channelBind) {
        manage.getLogger().info("[BotEventWebsocket] 发送消息->" + channelBind);
        try {
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("ChannelBind")
                    .setData(channelBind);
            this.putBotContext(body.getId(), context);
            this.webSocket.send(JSONObject.toJSONString(body));
        } catch (Exception e) {
            manage.getLogger().warning("发送消息失败，嵌套异常为 -> " + e);
        }
    }

    @OnBotEvent
    public void linkMessageEvent(BotContext context, LinkMessage linkMessage) {
        try {
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("LinkMessage")
                    .setData(linkMessage);
            this.putBotContext(body.getId(), context);
            this.webSocket.send(JSONObject.toJSONString(body));
        } catch (Exception e) {
            manage.getLogger().warning("发送消息失败，嵌套异常为 -> " + e);
        }
    }

    /**
     * 更换绑定
     *
     * @param context
     * @param changeTheBind
     */
    @OnBotEvent
    public void changeTheBindEvent(BotContext context, ChangeTheBind changeTheBind) {
        if (changeTheBind.getLastUserId() == null || changeTheBind.getNowUserId() == null) {
            changeTheBind
                    .setCode(500)
                    .setMsg("存档继承错误，请重试!");
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("changeTheBind")
                    .setData(changeTheBind);
            this.webSocket.send(JSONObject.toJSONString(body));
            return;
        }
        try {
            String lastUserIdUrl = new File("").getAbsolutePath()
                    + ConfigBase.SEPARATOR() + "world" + ConfigBase.SEPARATOR() + "playerdata"
                    + ConfigBase.SEPARATOR() + changeTheBind.getLastUserId() + ".dat";
            String nowUserIdUrl = new File("").getAbsolutePath()
                    + ConfigBase.SEPARATOR() + "world" + ConfigBase.SEPARATOR() + "playerdata"
                    + ConfigBase.SEPARATOR() + changeTheBind.getNowUserId() + ".dat";
            File lastFile = new File(lastUserIdUrl);
            File nowFile = new File(nowUserIdUrl);
            if (!lastFile.exists()) {
                throw new RuntimeException("存档继承错误，存档不存在!");
            }
            if (nowFile.exists()){
                if (!nowFile.delete()){
                    throw new RuntimeException("存档继承错误，无法转移!");
                }
            }
            if (!lastFile.renameTo(new File(nowUserIdUrl))){
                throw new RuntimeException("存档继承错误，操作失败!");
            }
            changeTheBind
                    .setCode(0)
                    .setMsg("存档继承成功!");
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("changeTheBind")
                    .setData(changeTheBind);
            this.webSocket.send(JSONObject.toJSONString(body));
        } catch (RuntimeException e) {
            changeTheBind
                    .setCode(500)
                    .setMsg(e.getMessage());
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("changeTheBind")
                    .setData(changeTheBind);
            this.webSocket.send(JSONObject.toJSONString(body));
        } catch (Throwable e) {
            changeTheBind
                    .setCode(500)
                    .setMsg("存档继承错误，请重试!");
            WebSocketBody body = new WebSocketBody()
                    .setId(UUID.randomUUID().toString())
                    .setType("changeTheBind")
                    .setData(changeTheBind);
            this.webSocket.send(JSONObject.toJSONString(body));
        }
    }

    @OnBotEvent
    public void webSocketEvent(BotContext context, WebSocketBody body) {
        this.manage.getLogger().info("[webSocketEvent] -> " + body);
        JSONObject data = null;
        if (body.getData() instanceof JSONObject) {
            data = (JSONObject) body.getData();
        }
        if (data != null) {
            switch (body.getType()) {
                case "ChannelBindResult": {
                    ChannelBindResult channelBindResult = data.toJavaObject(ChannelBindResult.class);
                    context.putCacheObj(ChannelBindResult.class, channelBindResult);
                    break;
                }
                case "LinkMessage": {
                    LinkMessage linkMessage = data.toJavaObject(LinkMessage.class);
                    System.out.println(linkMessage);
                    this.manage.getJavaPlugin().getServer().broadcastMessage(
                            String.format("[%s] -> %s", linkMessage.getUserName(), linkMessage.getMessage())
                    );
                    break;
                }
                case "ChangeTheBind": {
                    ChangeTheBind changeTheBind = data.toJavaObject(ChangeTheBind.class);
                    context.putEvent(ChangeTheBind.class, changeTheBind);
                }
            }
        } else {
            switch (body.getType()) {
                case "McReload": {
                    // 重载配置
                    System.out.println("重载服务器");
                    this.manage.getJavaPlugin().getServer().reload();
                    break;
                }
            }
        }
    }

    @OnBotEvent
    public void webSocketEvent(BotContext context, WebSocketVerify verify) {
        String s = JSONObject.toJSONString(verify);
        WebSocketBody body = new WebSocketBody()
                .setId(UUID.randomUUID().toString())
                .setType("WebSocketVerify")
                .setData(RSAUtils.encrypt(s, Constant.PUBLIC_KEY));
        this.webSocket.send(JSONObject.toJSONString(body));
    }
}
