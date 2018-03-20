package com.tg.tgt.moment.bean;

/**
 * 评论相关属性
 * @author yiyang
 */
public class CommentConfig {
    public static enum Type{
        /**
         * 公开评论
         */
        PUBLIC("public"),
        /**
         * 回复某人
         */
        REPLY("reply");

        private String value;
        private Type(String value){
            this.value = value;
        }

    }

    public int circlePosition;
    public int commentPosition;
    public String id;
    public Type commentType;
    public String parentId;
    public String hint;
}
