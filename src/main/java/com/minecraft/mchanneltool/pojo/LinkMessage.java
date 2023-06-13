package com.minecraft.mchanneltool.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LinkMessage implements BotEvent{
    String userId;
    String userName;
    String message;
}
