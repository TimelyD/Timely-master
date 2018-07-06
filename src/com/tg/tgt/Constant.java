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
package com.tg.tgt;

import com.hyphenate.easeui.EaseConstant;

public class Constant extends EaseConstant {
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String CHAT_ROOM = "item_chatroom";
	public static final String ACCOUNT_REMOVED = "account_removed";
	public static final String ACCOUNT_CONFLICT = "conflict";
	public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
	public static final String CHAT_ROBOT = "item_robots";
	public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
	public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
	public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";


//登陆信息
	public static final String MYUID = "login_myUid";
	public static final String SEX = "login_sex";
	public static final String NICKNAME = "login_nickName";
	public static final String STATE = "login_state";
	public static final String HEADIMAGE = "login_headImage";
	public static final String USERNAME = "login_userName";
	public static final String INFOCODE = "login_infoCode";
	public static final String PWD = "login_namename";
	public static final String QR = "login_qr_code";
	public static final String AGE = "login_age";
	public static final String ADDRESS = "login_address";
	public static final String SN = "";


	public static String User_Phone;
	public static String User_Nick;

	//是否加密
	public static final String ISCODE = "isCode";
	//语言选择
	public static final String LANGUAGE = "language";
	public static final String NEWS = "news";
	//邮箱后缀
	public static final String EMAIL_LAST = "email_last";
	public static final String EMAIL = "email";

	//
	public static final String CODE = "code";

	//用来记录新闻是否点进去过
	public static final String NEWS_RECODE = "news_recode";
	public static final String NOT_FOR_USER = "not_for_user";
	public static String IS_VIDEO_CAll = "is_video_call";

	public static final String IS_MINE_HOME_PAGE = "mine_home_page";
	public static final String USER_ID = "user_id";
	public static final String CIRCLE_ITEM = "circle_item";
	public static final String CIRCLE_ITEM_POS = "circle_item_pos";

	public static final String INVITE_REASON = "reason";
	public static final String INVITE_NICKNAME = "nickname";
	public static final String INVITE_AVATAR = "avatar";
	public static final String INVITE_picture = "picture";

	public static final String MOTION_ACTION_COUNT = "motion_action_count";
	public static final String CONTACT_INFO_SYNCED = "contact_info_synced";

	/**不被清除的sp名*/
	public static final String NOT_CLEAR_SP = "not_clear_sp";

	/**昵称与备注最长长度*/
	public static final int NICK_MAX_LENGTH = 10;
	/**会议id*/
	public static final String EXTRA_CONFERENCE_ID = "extra_conference_id";
	/**是否会议创建者*/
	public static final String EXTRA_CONFERENCE_PASS = "extra_conference_pass";
	public static final String EXTRA_CONFERENCE_IS_CREATOR = "extra_conference_is_creator";
	public static final String GROUP_ID = "group_id";
	public static final String MEMBERS = "members";
	public static final String EXTRA_CONFERENCE_IS_VIDEO = "extra_conference_is_video";
	public static final int MEMBERS_SIZE = 7;
}
