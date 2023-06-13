package com.minecraft.mchanneltool;

import com.minecraft.mchanneltool.bot.BotClientManage;
import com.minecraft.mchanneltool.bot.BotConfig;
import com.minecraft.mchanneltool.bot.BotConfigManage;
import com.minecraft.mchanneltool.bot.event.BotEventVoid;
import com.minecraft.mchanneltool.bot.event.BotEventWebsocket;
import com.minecraft.mchanneltool.bot.v1.BotClientManageImpl;
import com.minecraft.mchanneltool.config.ConfigBase;
import com.minecraft.mchanneltool.event.PlayerEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MchannelTool extends JavaPlugin {

    public static BotClientManage botClientManage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[MchannelTool] 插件已加载");
        this.saveDefaultConfig();
        FileConfiguration fileConfiguration = this.getConfig();
        ConfigBase.wsUri = fileConfiguration.getString("ws-uri");
        ConfigBase.hint = fileConfiguration.getString("hint");
        if (ConfigBase.wsUri == null || ConfigBase.wsUri.equals("")){
            this.getLogger().warning("配置加载失败");
        }
        if (ConfigBase.hint == null || ConfigBase.hint.equals("")){
            ConfigBase.hint = "你好，请在qq频道内的机器人认证频道说出 /mctbind [code] 完成绑定。";
        }
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        // 创建事件池
        botClientManage = new BotClientManageImpl(this, config -> config
                .setConfig(new BotEventVoid())
                .setConfig(new BotEventWebsocket())
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
