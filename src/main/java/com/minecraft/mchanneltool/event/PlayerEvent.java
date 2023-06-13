package com.minecraft.mchanneltool.event;

import com.minecraft.mchanneltool.MchannelTool;
import com.minecraft.mchanneltool.bot.BotContext;
import com.minecraft.mchanneltool.config.ConfigBase;
import com.minecraft.mchanneltool.pojo.ChannelBindResult;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {

    /**
     * 玩家聊天事件
     * @param event
     */
    @EventHandler
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        MchannelTool.botClientManage.putEvent(AsyncPlayerChatEvent.class, event);
    }


    /**
     * 玩家即将进入服务器事件
     * @param event
     */
    @EventHandler
    public void AsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        BotContext botContext = MchannelTool.botClientManage.putEvent(AsyncPlayerPreLoginEvent.class, event);
        // 5秒等待，如果未响应则直接拒绝用户进入
        for (int i = 0; i < 6; i++) {
            try {
                if (i == 0){
                    Thread.sleep(200);
                } else {
                    Thread.sleep(1000);
                }
                ChannelBindResult channelBindResult = botContext.getCacheObj(ChannelBindResult.class);
                if (channelBindResult == null){
                    continue;
                }
                if (channelBindResult.getClose()){
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            "当前认证功能已关闭"
                    );
                } else if (!channelBindResult.getPass()){
                    String hint = ConfigBase.hint;
                    if (hint == null || hint.isEmpty()){
                        hint = "你好，请在qq频道内的机器人认证频道说出 /mctbind [code] 完成绑定。";
                    }
                    hint = hint.replaceAll("\\[code]", channelBindResult.getVerificationCode());
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            hint
                    );
                } else {
                    event.allow();
                }
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 等待超时
        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                "机器人服务器信息校验超时，请联系管理员!"
        );
    }


    /**
     * 玩家进入服务器事件
     * @param event
     */
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        MchannelTool.botClientManage.putEvent(PlayerJoinEvent.class, event);
    }

    /**
     * 玩家进入服务器事件
     * @param event
     */
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event){
        MchannelTool.botClientManage.putEvent(PlayerQuitEvent.class, event);
    }

}
