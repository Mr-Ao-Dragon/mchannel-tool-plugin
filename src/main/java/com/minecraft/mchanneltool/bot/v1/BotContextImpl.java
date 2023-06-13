package com.minecraft.mchanneltool.bot.v1;

import com.minecraft.mchanneltool.bot.BotClientManage;
import com.minecraft.mchanneltool.bot.BotContext;
import com.minecraft.mchanneltool.pojo.BotEvent;
import lombok.NonNull;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotContextImpl implements BotContext {

    Class<? extends Event> clazz;
    Event eventObj;
    BotClientManage botClientManage;

    public BotContextImpl(Class<? extends Event> clazz, Event eventObj, BotClientManage botClientManage) {
        this.clazz = clazz;
        this.eventObj = eventObj;
        this.botClientManage = botClientManage;
    }

    /**
     * 获取事件类型
     *
     * @return
     */
    @Override
    public Class<? extends Event> getClazz() {
        return clazz;
    }

    @Override
    public <T extends Event> T getEventObj(@NonNull Class<T> clazz) {
        return (T) eventObj;
    }

    @Override
    public <T extends BotEvent> void putEvent(Class<T> clazz, T obj) {
        botClientManage.putBotEvent(this, clazz, obj);
    }

    @Override
    public BotClientManage getBotClientManage() {
        return botClientManage;
    }


    // === 缓存

    private final Map<Class<? extends BotEvent>, BotEvent> cache = new ConcurrentHashMap<>();

    @Override
    public <T extends BotEvent> T getCacheObj(Class<T> clazz) {
        return (T) cache.get(clazz);
    }

    @Override
    public <T extends BotEvent> void putCacheObj(Class<T> clazz, T obj) {
        cache.put(clazz, obj);
    }
}
