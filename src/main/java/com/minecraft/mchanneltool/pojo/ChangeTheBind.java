package com.minecraft.mchanneltool.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更换绑定
 */
@Data
@Accessors(chain = true)
public class ChangeTheBind implements BotEvent{
    String msgId;       // 触发消息id
    String channelUserId;
    String lastUserId;  // 最后一次登录id
    String nowUserId;   // 现在用户id
    String msg;       // 错误
    int code;           // 0 成功 其他失败
}
