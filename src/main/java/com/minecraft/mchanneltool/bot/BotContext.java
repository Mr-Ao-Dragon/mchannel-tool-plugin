package com.minecraft.mchanneltool.bot;

import com.minecraft.mchanneltool.pojo.BotEvent;
import org.bukkit.event.Event;

public interface BotContext {
    /**
     * 获取事件类型
     * @return
     */
    Class<? extends Event> getClazz();

    <T extends Event> T getEventObj(Class<T> clazz);

    <T extends BotEvent> void putEvent(Class<T> clazz, T obj);

    BotClientManage getBotClientManage();

    <T extends BotEvent> T getCacheObj(Class<T> clazz);

    <T extends BotEvent> void putCacheObj(Class<T> clazz, T obj);
}
