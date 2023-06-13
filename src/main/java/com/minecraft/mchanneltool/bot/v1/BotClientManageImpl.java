package com.minecraft.mchanneltool.bot.v1;

import com.minecraft.mchanneltool.MchannelTool;
import com.minecraft.mchanneltool.annotation.OnBotEvent;
import com.minecraft.mchanneltool.bot.*;
import com.minecraft.mchanneltool.pojo.BotEvent;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class BotClientManageImpl implements BotClientManage, BotConfig {

    JavaPlugin javaPlugin;
    Map<Class<? extends BotEventBase>, BotEventBase> map = new ConcurrentHashMap<>();
    List<BotEventBase> list = new CopyOnWriteArrayList<>();

    public BotClientManageImpl(JavaPlugin javaPlugin, BotConfigManage configManage) {
        this.javaPlugin = javaPlugin;
        configManage.setConfig(this);
        for (BotEventBase o : list) {
            o.init(this);
        }
    }

    /**
     * 获取插件实体对象
     */
    @Override
    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    @Override
    public Logger getLogger() {
        return javaPlugin.getLogger();
    }

    @Override
    public <T extends Event> BotContext putEvent(@NonNull Class<T> clazz, @NonNull T obj) {
        BotContext botContext = new BotContextImpl(clazz, obj, this);
        this.putBotEvent(botContext, null, null);
        return botContext;
    }

    @Override
    public <T extends BotEvent> void putBotEvent(@NonNull BotContext context, Class<T> clazz, T obj) {
        for (BotEventBase o : list) {
            Class<? extends BotEventBase> aClass = o.getClass();
            Method[] methods = aClass.getMethods();
            for (Method method : methods){
                OnBotEvent onBotEvent = method.getAnnotation(OnBotEvent.class);
                if (onBotEvent == null){
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length < 1){
                    continue;
                }
                if (!BotContext.class.isAssignableFrom(parameterTypes[0])){
                    continue;
                }
                try {
                    if (clazz == null || obj == null){
                        // 无属性事件推送
                        if (parameterTypes.length != 1){
                            continue;
                        }
                        method.invoke(o, context);
                    } else {
                        // 有属性事件推送
                        if (parameterTypes.length != 2){
                            continue;
                        }
                        if (!clazz.isAssignableFrom(parameterTypes[1])){
                            continue;
                        }
                        method.invoke(o, context, obj);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 配置事件
     *
     * @param obj
     */
    @Override
    public <T extends BotEventBase> BotConfig setConfig(T obj) {
        list.add(obj);
        return this;
    }
}
