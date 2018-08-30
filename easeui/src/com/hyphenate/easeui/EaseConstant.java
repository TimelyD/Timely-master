/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui;
import java.util.ArrayList;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    public static final String MESSAGE_ATTR_IS_INVITE_INTO_GROUP = "is_invite_into_group";//是否是邀请进群消息
    public static final String MESSAGE_ATTR_IS_KICKED_GROUP = "group_owner";//群主ID(是否是踢出群消息)
    public static final String MESSAGE_ATTR_IS_FIRE = "isFire";
    public static final String MESSAGE_ATTR_NICKNAME = "nickname";
    public static ArrayList<String>list_ms=new ArrayList<>();
    public static Boolean MESSAGE_ATTR_SELECT = false;

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    
    public static final String MESSAGE_ATTR_AT_MSG = "em_at_list";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";
    public static final String MI="Encrypted";//是否加密类型
    public static final String VERSION="Version";//加密的当前最新版本
    public static final String SEND="EncryptedMySelf";//判断是否是发送方需要显示的文本
    /**
     *  撤回消息的常量
     * */
    public static final String MSG_ID="ReCallMessageID";
    public static final String MSG_NAME="ReCallMessageNickname";
    public static final String MESSAGE_TYPE_RECALL = "message_recall";
    public static final String MSG_SENDID="ReCallMessageUsername";
    /**
     * 名片的参数
     * */
    public static final String BUSSINES_ID="bussines_id";
    public static final String BUSSINES_PIC="bussines_pic";
    public static final String BUSSINES_NAME="bussines_name";
    public static final String BUSSINES_NUMBER="bussines_number";
    
	public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";

    public static boolean isCollection;
    public static android.os.Handler collectionHandler;

    public static boolean isInputHeadset = false;
    public static boolean isHandSetReciver = false;

    public static int friendsUnread;

    public static boolean isFriendsView = false;
}
