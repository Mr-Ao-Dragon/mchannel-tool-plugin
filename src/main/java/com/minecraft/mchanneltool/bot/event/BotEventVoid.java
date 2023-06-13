package com.minecraft.mchanneltool.bot.event;

import com.minecraft.mchanneltool.annotation.OnBotEvent;
import com.minecraft.mchanneltool.bot.BotClientManage;
import com.minecraft.mchanneltool.bot.BotContext;
import com.minecraft.mchanneltool.bot.BotEventBase;
import com.minecraft.mchanneltool.pojo.ChannelBind;
import com.minecraft.mchanneltool.pojo.LinkMessage;
import com.minecraft.mchanneltool.utils.TokenUtil;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BotEventVoid implements BotEventBase {
    BotClientManage manage;

    @Override
    public void init(BotClientManage manage) {
        this.manage = manage;
    }

    @OnBotEvent
    public void commander(BotContext context){
        if (context.getClazz() == AsyncPlayerPreLoginEvent.class){
            // 用户请求进入服务器
            AsyncPlayerPreLoginEvent event = context.getEventObj(AsyncPlayerPreLoginEvent.class);
            this.manage.getLogger().info("用户请求进入服务器事件");
            ChannelBind channelBind = new ChannelBind()
                    .setAddress(event.getAddress().getHostAddress())
                    .setUserId(event.getUniqueId().toString())
                    .setVerificationCode(TokenUtil.randomHexadecimalNumber(8));
            context.putEvent(ChannelBind.class, channelBind);
        }
        if (context.getClazz() == AsyncPlayerChatEvent.class){
            // 用户发送消息
            AsyncPlayerChatEvent event = context.getEventObj(AsyncPlayerChatEvent.class);
            LinkMessage linkMessage = new LinkMessage()
                    .setUserId(event.getPlayer().getUniqueId().toString())
                    .setMessage(event.getMessage());
            context.putEvent(LinkMessage.class, linkMessage);
        }
        if (context.getClazz() == PlayerJoinEvent.class){
            // 用户进入服务器
            PlayerJoinEvent event = context.getEventObj(PlayerJoinEvent.class);
            event.getPlayer().loadData();
            LinkMessage linkMessage = new LinkMessage()
                    .setUserId(event.getPlayer().getUniqueId().toString())
                    .setMessage("进入服务器");
            context.putEvent(LinkMessage.class, linkMessage);
        }
        if (context.getClazz() == PlayerQuitEvent.class){
            // 用户退出服务器
            PlayerQuitEvent event = context.getEventObj(PlayerQuitEvent.class);
            LinkMessage linkMessage = new LinkMessage()
                    .setUserId(event.getPlayer().getUniqueId().toString())
                    .setMessage("退出服务器");
            context.putEvent(LinkMessage.class, linkMessage);
        }
    }
}
