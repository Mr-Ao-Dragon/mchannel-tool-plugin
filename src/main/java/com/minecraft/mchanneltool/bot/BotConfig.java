package com.minecraft.mchanneltool.bot;

public interface BotConfig {
    /**
     * 配置事件
     */
    <T extends BotEventBase> BotConfig setConfig(T obj);


}
