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
package com.hyphenate.easeui.widget.chatrow;

import java.io.File;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EMLog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 语音row播放点击事件监听
 *
 */
public class EaseChatRowVoicePlayClickListener implements View.OnClickListener {
	private static final String TAG = "VoicePlayClickListener";
	EMMessage message;
	EMVoiceMessageBody voiceBody;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private ChatType chatType;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static EaseChatRowVoicePlayClickListener currentPlayListener = null;
	public static String playMsgId;

	private int voiceModel;


	private SensorManager mManager;//传感器管理对象
	private Sensor mProximiny;
	private MySensorEventListener eventListener;
	private boolean isTouchPhone = false;
	private boolean isChangeTouchStatus = false;

	public EaseChatRowVoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity context) {
		this.message = message;
		voiceBody = (EMVoiceMessageBody) message.getBody();
		this.iv_read_status = iv_read_status;
		this.adapter = adapter;
		voiceIconView = v;
		this.activity = context;
		this.chatType = message.getChatType();
		mManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
		eventListener = new MySensorEventListener();
	}

	public void stopPlayVoice() {
		Log.i("语音","停了");
		voiceAnimation.stop();
		if (message.direct() == EMMessage.Direct.RECEIVE) {
//			voiceIconView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
			voiceIconView.setImageResource(R.anim.voice_receive);
		} else {
//			voiceIconView.setImageResource(R.drawable.ease_chatto_voice_playing);
			voiceIconView.setImageResource(R.anim.voice_send);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		playMsgId = null;
		adapter.notifyDataSetChanged();
		mManager.unregisterListener(eventListener,mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
	}

	public void setModelVoice(int flag){
		voiceModel = flag;
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		EaseUI.getInstance().setSettingsProvider(new EaseUI.EaseSettingsProvider() {
			@Override
			public boolean isMsgNotifyAllowed(EMMessage message) {
				return true;
			}

			@Override
			public boolean isMsgSoundAllowed(EMMessage message) {
				return true;
			}

			@Override
			public boolean isMsgVibrateAllowed(EMMessage message) {
				return true;
			}

			@Override
			public boolean isSpeakerOpened() {
				switch (voiceModel){
					case 0:
						if (EaseConstant.isHandSetReciver)
							return false;
						return true;
					case 1:
					case 2:
						return false;
					default:
						return true;
				}
			}
		});
		if (isPlaying){
			currentPlayListener.stopPlayVoice();
			palyVoiceMessage();
		}else {
			if (mediaPlayer != null) {
				switch (voiceModel) {
					case 0://默认扬声器
						if (EaseConstant.isHandSetReciver){
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
								audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
								//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
								audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
										audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
							} else {
								audioManager.setMode(AudioManager.MODE_IN_CALL);
								audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
										audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
							}
						}else {
							audioManager.setSpeakerphoneOn(true);
							audioManager.setMode(AudioManager.MODE_NORMAL);
							//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
							audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
									audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);
						}
//						audioManager.setMode(AudioManager.MODE_NORMAL);
//						audioManager.setSpeakerphoneOn(true);
//						mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
						break;
					case 1://听筒模式
//						audioManager.setSpeakerphoneOn(false);// 关闭扬声器
//						// 把声音设定成Earpiece（听筒）出来，设定为正在通话中MODE_IN_CALL
//						audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//						mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
						audioManager.setSpeakerphoneOn(false);
						//5.0以上
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
							audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
							//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
							audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
									audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
						} else {
							audioManager.setMode(AudioManager.MODE_IN_CALL);
							audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
									audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
						}
						break;
					case 2://耳机模式
						audioManager.setSpeakerphoneOn(false);
						break;
				}
			}
		}
	}


	public void playVoice(String filePath) {
		Log.i("语音","播放");
		if (!(new File(filePath).exists())) {
			return;
		}
		mManager.registerListener(eventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
				SensorManager.SENSOR_DELAY_NORMAL);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型
		mProximiny = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		playMsgId = message.getMsgId();
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EaseUI.getInstance().getSettingsProvider().isSpeakerOpened()) {
			if(voiceModel == 2 ||  EaseConstant.isInputHeadset) {
				audioManager.setSpeakerphoneOn(false);
			}else {
				if (EaseConstant.isHandSetReciver) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
						//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
						audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
								audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
					} else {
						audioManager.setMode(AudioManager.MODE_IN_CALL);
						audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
								audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
					}
				} else{
//				audioManager.setMode(AudioManager.MODE_NORMAL);
//				audioManager.setSpeakerphoneOn(true);
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
					audioManager.setSpeakerphoneOn(true);
					audioManager.setMode(AudioManager.MODE_NORMAL);
				//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FX_KEY_CLICK);
				}
			}
		} else {
			if(voiceModel == 2 ||  EaseConstant.isInputHeadset){
				audioManager.setSpeakerphoneOn(false);
			}else {
//				audioManager.setSpeakerphoneOn(false);// 关闭扬声器
//				// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
//				audioManager.setMode(AudioManager.MODE_IN_CALL);
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				audioManager.setSpeakerphoneOn(false);
				//5.0以上
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
					audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
					//设置音量，解决有些机型切换后没声音或者声音突然变大的问题
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
							audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
				} else {
					audioManager.setMode(AudioManager.MODE_IN_CALL);
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
							audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
				}
			}
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.release();
					mediaPlayer = null;
					stopPlayVoice(); // stop animation
				}

			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();

			// 如果是接收的消息
			if (message.direct() == EMMessage.Direct.RECEIVE) {
			    if (!message.isAcked() && chatType == ChatType.Chat) {
	                    // 告知对方已读这条消息
			            EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
			    }
				if (!message.isListened() && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
					// 隐藏自己未播放这条语音消息的标志
					iv_read_status.setVisibility(View.INVISIBLE);
					message.setListened(true);
					EMClient.getInstance().chatManager().setVoiceMessageListened(message);
				}

			}

		} catch (Exception e) {
		    System.out.println();
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (message.direct() == EMMessage.Direct.RECEIVE) {
//			voiceIconView.setImageResource(R.anim.voice_from_icon);
			voiceIconView.setImageResource(R.anim.voice_receive);
		} else {
			voiceIconView.setImageResource(R.anim.voice_send);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	class MySensorEventListener implements SensorEventListener {


		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub


		}


		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			float[] its = event.values;
			if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
				if (its[0] < mProximiny.getMaximumRange()) {// 贴近手机
					if (!isTouchPhone) {
						isChangeTouchStatus = true;
						EaseUI.getInstance().setSettingsProvider(new EaseUI.EaseSettingsProvider() {
							@Override
							public boolean isMsgNotifyAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isMsgSoundAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isMsgVibrateAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isSpeakerOpened() {
								return false;
							}
						});
					}
					isTouchPhone = true;
				} else {// 远离手机
					if (isTouchPhone) {
						isChangeTouchStatus = true;
						EaseUI.getInstance().setSettingsProvider(new EaseUI.EaseSettingsProvider() {
							@Override
							public boolean isMsgNotifyAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isMsgSoundAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isMsgVibrateAllowed(EMMessage message) {
								return true;
							}

							@Override
							public boolean isSpeakerOpened() {
								return true;
							}
						});
					}
					isTouchPhone = false;
				}
				if(isPlaying && isChangeTouchStatus){
					currentPlayListener.stopPlayVoice();
					isChangeTouchStatus = false;
					palyVoiceMessage();
				}
			}


		}
	}

	private void palyVoiceMessage() {
		String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
		if (message.direct() == EMMessage.Direct.SEND) {
			// for sent msg, we will try to play the voice file directly
			playVoice(voiceBody.getLocalUrl());
		} else {
			if (message.status() == EMMessage.Status.SUCCESS) {
				File file = new File(voiceBody.getLocalUrl());
				if (file.exists() && file.isFile())
					playVoice(voiceBody.getLocalUrl());
				else
					EMLog.e(TAG, "file not exist");
			} else if (message.status() == EMMessage.Status.INPROGRESS) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			} else if (message.status() == EMMessage.Status.FAIL) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
				new AsyncTask<Void, Void, Void>() {


					@Override
					protected Void doInBackground(Void... params) {
						EMClient.getInstance().chatManager().downloadAttachment(message);
						return null;
					}


					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
					}


				}.execute();


			}


		}
	}

	@Override
	public void onClick(View v) {
		String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
		if (isPlaying) {
			if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}

		if (message.direct() == EMMessage.Direct.SEND) {
			// for sent msg, we will try to play the voice file directly
			playVoice(voiceBody.getLocalUrl());
		} else {
			if (message.status() == EMMessage.Status.SUCCESS) {
				File file = new File(voiceBody.getLocalUrl());
				if (file.exists() && file.isFile())
					playVoice(voiceBody.getLocalUrl());
				else
					EMLog.e(TAG, "file not exist");

			} else if (message.status() == EMMessage.Status.INPROGRESS) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			} else if (message.status() == EMMessage.Status.FAIL) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						EMClient.getInstance().chatManager().downloadAttachment(message);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
					}

				}.execute();

			}

		}
	}
}
