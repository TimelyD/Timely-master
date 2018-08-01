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
package com.tg.tgt.db;

import android.content.ContentValues;
import android.content.Context;

import com.hyphenate.easeui.domain.EaseUser;
import com.tg.tgt.domain.InviteMessage;

import java.util.List;
import java.util.Map;

public class BlackMessageDao {
	static final String TABLE_NAME = "black_friends_msgs";
//	static final String COLUMN_NAME_ID = "id";
//	static final String COLUMN_NAME_FROM = "username";
//	static final String COLUMN_NAME_GROUP_ID = "groupid";
//	static final String COLUMN_NAME_GROUP_Name = "groupname";
//
//	static final String COLUMN_NAME_TIME = "time";
//	static final String COLUMN_NAME_REASON = "reason";
//	static final String COLUMN_NAME_MSG_ID = "messageId";
//	public static final String COLUMN_NAME_STATUS = "status";
//	static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
//	static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

	static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	//自己添加的锁标识
	public static final String COLUMN_NAME_ISLOCK = "islock";
	public static final String COLUMN_NAME_CON = "remark";

	public static final String COLUMN_NAME_CHATID = "chatid";
	public static final String COLUMN_NAME_SEX = "sex";
	public static final String COLUMN_NAME_STATE = "state";
	public static final String SN = "sn";



	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

	public static final String ROBOT_TABLE_NAME = "robots";
	public static final String ROBOT_COLUMN_NAME_ID = "username";
	public static final String ROBOT_COLUMN_NAME_NICK = "nick";
	public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

	public BlackMessageDao(Context context){
	}
	
	/**
	 * save message
	 * @param message
	 * @return  return cursor of the message
	 */
	public void saveBlack(EaseUser message){
		DemoDBManager.getInstance().saveBlack(message);
	}

	/**
	 * get messges
	 * @return
	 */
	public List<EaseUser> getBlackList(){
		return DemoDBManager.getInstance().getBlackList();
	}

	public void deleteBlack(String username){
		DemoDBManager.getInstance().deleteBlack(username);
	}


}
