package com.jy.message;

/**
 * 消息处理器，针对不同的 messageType
 */
public interface MessageHandler {

    /**
     * 消息处理
     * @param message 消息
     */
    void execute(MessageWrapper message);

    /**
     * 消息类型
     * @return 消息类型
     */
    String getType();

}
