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
package com.hyphenate.easeui.domain;

import android.text.TextUtils;

import com.hyphenate.chat.EMContact;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class EaseUser extends EMContact {
    
    /**
     * initial letter for nickname
     */
	protected String initialLetter;
	/**
	 * avatar of the user
	 */
	protected String avatar;

	/**
	 * 是否加锁 1为加锁了， 0为没
	 */
	protected int isLock = -1;

	/**
	 * 备注
	 */
	protected String remark;

	/**
	 * 后台该用户id
	 */
	private String chatid;
	private String chatidsex;
	private String sn;
	/**
	 * 心情
	 */
	private String chatidstate;

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getChatid() {
		return chatid;
	}

	public void setChatid(String chatid) {
		this.chatid = chatid;
	}

	public String getChatidsex() {
		return chatidsex;
	}

	public void setChatidsex(String chatidsex) {
		this.chatidsex = chatidsex;
	}

	public String getChatidstate() {
		return chatidstate;
	}

	public void setChatidstate(String chatidstate) {
		this.chatidstate = chatidstate;
	}

    public String getRemark() {
        return this.remark;
    }
	public String safeGetRemark() {
		return TextUtils.isEmpty(remark)?getNickname():remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getIsLock() {
		return isLock;
	}

	public void setIsLock(int isLock) {
		this.isLock = isLock;
	}

	public EaseUser(String username){
	    this.username = username;
	}

	public String getInitialLetter() {
	    if(initialLetter == null){
            EaseCommonUtils.setUserInitialLetter(this);
        }
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}


	public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick(){
		return nick;
	}

    @Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof EaseUser)) {
			return false;
		}
		return getUsername().equals(((EaseUser) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
}
