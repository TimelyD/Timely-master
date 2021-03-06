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
package com.tg.tgt.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.easemob.redpacketsdk.constant.RPConstant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayClickListener;
import com.tg.tgt.Constant;
import com.tg.tgt.R;

import java.util.Date;

public class ContextMenuActivity extends BaseActivity {
    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_DUOFORWARD = 4;
	public static final int RESULT_CODE_RECALL = 5;
	public static final int RESULT_CODE_COLLECT = 6;
	public static final int RESULT_CODE_PLAYVOICE = 7;
	public static final int RESULT_CODE_SHARE = 8;

	private TextView voiceText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EMMessage message = getIntent().getParcelableExtra("message");
		boolean isChatroom = getIntent().getBooleanExtra("ischatroom", false); 
		
		int type = message.getType().ordinal();
		Log.i("dcz_type",type+"");
		if (type == EMMessage.Type.TXT.ordinal()) {
		    if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
					|| message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
					//red packet code : 屏蔽红包消息的转发功能
					|| message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)){
				    //end of red packet code
				setContentView(R.layout.em_context_menu_for_location);
		    }else if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
		        setContentView(R.layout.em_context_menu_for_image);
		    }else if(message.getBooleanAttribute(EaseConstant.BUSSINES_ID, false)){
				setContentView(R.layout.em_context_menu_for_location);
			}else{
		        setContentView(R.layout.em_context_menu_for_text);
		    }
		} else if (type == EMMessage.Type.LOCATION.ordinal()) {
		    setContentView(R.layout.em_context_menu_for_location);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
		    setContentView(R.layout.em_context_menu_for_image);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
		    setContentView(R.layout.em_context_menu_for_voice);
			voiceText = (TextView)findViewById(R.id.play_way);
			voiceText.setText(EaseConstant.isHandSetReciver?R.string.loudspeaker_play:R.string.receiver_play);
		} else if (type == EMMessage.Type.VIDEO.ordinal()) {
			setContentView(R.layout.em_context_menu_for_video);
		} else if (type == EMMessage.Type.FILE.ordinal()) {
		    setContentView(R.layout.em_context_menu_for_location);
		}
		if (isChatroom
				//red packet code : 屏蔽红包消息的撤回功能
				|| message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
			    //end of red packet code
			View v = (View) findViewById(R.id.forward);
	        if (v != null) {
	            v.setVisibility(View.GONE);
	        }
		}
		View recall = (View) findViewById(R.id.recall);
		if(message.direct() == EMMessage.Direct.RECEIVE ) {
			recall.setVisibility(View.GONE);
		}
		long cha = (new Date().getTime() - message.getMsgTime())/1000;
		if(cha>120||message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)||message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
			recall.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view){
		setResult(RESULT_CODE_COPY);
		finish();
	}
	public void delete(View view){
		setResult(RESULT_CODE_DELETE);
		finish();
	}
	public void forward(View view){
		setResult(RESULT_CODE_FORWARD);
		finish();
	}
	public void duo_forward(View view){
		setResult(RESULT_CODE_DUOFORWARD);
		finish();
	}
	public void recall(View view){
		setResult(RESULT_CODE_RECALL);
		finish();
	}
	public void collection(View view){
		setResult(RESULT_CODE_COLLECT);
		finish();
	}
	public void share(View view){
		setResult(RESULT_CODE_SHARE);
		finish();
	}

	public void playReciverVoice(View view){
		EaseConstant.isHandSetReciver = !EaseConstant.isHandSetReciver;
		if (EaseChatRowVoicePlayClickListener.currentPlayListener != null){
			if (EaseConstant.isHandSetReciver)
				EaseChatRowVoicePlayClickListener.currentPlayListener.setModelVoice(1);
			else
				EaseChatRowVoicePlayClickListener.currentPlayListener.setModelVoice(0);
		}
		setResult(RESULT_CODE_PLAYVOICE);
		finish();
	}
	
}
