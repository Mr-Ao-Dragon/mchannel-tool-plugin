package com.minecraft.mchanneltool.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WebSocketBody implements BotEvent{
    String id;  // 消息id
    String type;    // 消息类型
    Object data;    // 消息数据
}
