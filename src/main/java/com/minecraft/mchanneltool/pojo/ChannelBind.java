package com.minecraft.mchanneltool.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChannelBind implements BotEvent{
    String userId;  // 用户唯一id
    String Address; // 用户IP
    String verificationCode;    // 验证码
}
