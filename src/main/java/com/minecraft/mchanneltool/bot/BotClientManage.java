package com.minecraft.mchanneltool.bot;

import com.minecraft.mchanneltool.pojo.BotEvent;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public interface BotClientManage {

    /**
     * 获取插件实体对象
     *
     */
    JavaPlugin getJavaPlugin();

    Logger getLogger();

    <T extends Event> BotContext putEvent(Class<T> clazz, T obj);

    <T extends BotEvent> void putBotEvent(BotContext context, Class<T> clazz, T obj);
}
