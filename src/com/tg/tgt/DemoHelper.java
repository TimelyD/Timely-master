package com.tg.tgt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.redpacket.utils.RedPacketUtil;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.tg.tgt.conference.ConferenceActivity;
import com.tg.tgt.db.DemoDBManager;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.db.UserDao;
import com.tg.tgt.domain.EmojiconExampleGroupData;
import com.tg.tgt.domain.InviteMessage;
import com.tg.tgt.domain.RobotUser;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.model.IsCodeResult;
import com.tg.tgt.http.model2.UserFriendModel;
import com.tg.tgt.parse.UserProfileManager;
import com.tg.tgt.receiver.CallReceiver;
import com.tg.tgt.receiver.HeadsetReceiver;
import com.tg.tgt.ui.ChatActivity;
import com.tg.tgt.ui.MainActivity;
import com.tg.tgt.ui.VideoCallActivity;
import com.tg.tgt.ui.VoiceCallActivity;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.PreferenceManager;
import com.hyphenate.easeui.utils.rxbus2.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DemoHelper {
    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private Map<String, EaseUser> contactList;

    private Map<String, RobotUser> robotList;

    private UserProfileManager userProManager;

    private static DemoHelper instance = null;

    private DemoModel demoModel = null;

    /**
     * sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;

    private String username;

    private Context appContext;

    private CallReceiver callReceiver;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

    private DemoHelper() {
    }

    public synchronized static DemoHelper getInstance() {
        if (instance == null) {
            instance = new DemoHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context
     *            application context
     */
    public void init(Context context) {
        demoModel = new DemoModel(context);
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(BuildConfig.IS_DEBUG);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            getUserProfileManager().init(context);

            // TODO: set Call options
            // min video kbps
            int minBitRate = PreferenceManager.getInstance().getCallMinVideoKbps();
            if (minBitRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(minBitRate);
            }

            // max video kbps
            int maxBitRate = PreferenceManager.getInstance().getCallMaxVideoKbps();
            if (maxBitRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(maxBitRate);
            }

            // max frame rate
            int maxFrameRate = PreferenceManager.getInstance().getCallMaxFrameRate();
            if (maxFrameRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(maxFrameRate);
            }

            // audio sample rate
            int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
            if (audioSampleRate != -1) {
                EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(audioSampleRate);
            }

            /**
             * This function is only meaningful when your app need recording
             * If not, remove it.
             * This function need be called before the video stream started, so we set it in onCreate function.
             * This method will set the preferred video record encoding codec.
             * Using default encoding format, recorded file may not be played by mobile player.
             */
            //EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);

            // resolution
            String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
            if (resolution.equals("")) {
                resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
            }
            String[] wh = resolution.split("x");
            if (wh.length == 2) {
                try {
                    EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0])
                            .intValue(), new Integer(wh[1]).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // enabled fixed sample rate
            boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
            EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);

            // Offline call push
            //TODO  默认离线推送通话
            EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(/*getModel().isPushCall()
            */false);
            setCallOptions();
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
    }


    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        //you need apply & set your own id if you want to use google cloud messaging.
        options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
//        options.setHuaweiPushAppId("10492024");

        //set custom servers, commonly used in private deployment
        if (demoModel.isCustomServerEnable() && demoModel.getRestServer() != null && demoModel.getIMServer() != null) {
            options.setRestServer(demoModel.getRestServer());
            options.setIMServer(demoModel.getIMServer());
            if (demoModel.getIMServer().contains(":")) {
                options.setIMServer(demoModel.getIMServer().split(":")[0]);
                options.setImPort(Integer.valueOf(demoModel.getIMServer().split(":")[1]));
            }
        }

        if (demoModel.isCustomAppkeyEnabled() && demoModel.getCutomAppkey() != null && !demoModel.getCutomAppkey()
                .isEmpty()) {
            options.setAppKey(demoModel.getCutomAppkey());
        }

        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());

        return options;
    }

    private void setCallOptions() {
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        appContext.registerReceiver(headsetReceiver, headsetFilter);

        // min video kbps
        int minBitRate = PreferenceManager.getInstance().getCallMinVideoKbps();
        if (minBitRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(minBitRate);
        }

        // max video kbps
        int maxBitRate = PreferenceManager.getInstance().getCallMaxVideoKbps();
        if (maxBitRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(maxBitRate);
        }

        // max frame rate
        int maxFrameRate = PreferenceManager.getInstance().getCallMaxFrameRate();
        if (maxFrameRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(maxFrameRate);
        }

        // audio sample rate
        int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
        if (audioSampleRate != -1) {
            EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(audioSampleRate);
        }

        /**
         * This function is only meaningful when your app need recording
         * If not, remove it.
         * This function need be called before the video stream started, so we set it in onCreate function.
         * This method will set the preferred video record encoding codec.
         * Using default encoding format, recorded file may not be played by mobile player.
         */
        //EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);

        // resolution
        String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
        if (resolution.equals("")) {
            resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
        }
        String[] wh = resolution.split("x");
        if (wh.length == 2) {
            try {
                EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0]).intValue(), new Integer(wh[1]).intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // enabled fixed sample rate
        boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);

        // Offline call push
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(getModel().isPushCall());

        // 设置会议模式
       /* if (PreferenceManager.getInstance().isLargeConferenceMode()) {
            EMClient.getInstance().conferenceManager().setConferenceMode(EMConferenceListener.ConferenceMode.LARGE);
        }else{
            EMClient.getInstance().conferenceManager().setConferenceMode(EMConferenceListener.ConferenceMode.NORMAL);
        }*/
    }

    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //set options
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                Log.e("Tag","震动"+demoModel.getSettingMsgVibrate());
                return demoModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                Log.e("Tag","声音"+demoModel.getSettingMsgSound());
                return demoModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                Log.e("Tag","是否通知"+demoModel.getSettingMsgNotification());
                if (message == null) {
                    return demoModel.getSettingMsgNotification();
                }
                if (!demoModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return R.drawable.icon_alpha;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
//                EaseUser user = getUserInfo(message.getFrom());
                EaseUser user = getUserInfo(message);
                if (user != null && user.safeGetRemark() != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(com.tg.tgt.R.string.at_your_in_group), user.getNick
                                ());
                    }
                    if (user.getIsLock() == 1 && message.getChatType().equals(ChatType.Chat)) {
                        return user.safeGetRemark() + ": ******"/* + ticker*/;
                    } else {
                        return user.safeGetRemark() + ": " + ticker;
                    }
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(com.tg.tgt.R.string.at_your_in_group), message
                                .getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, ChatActivity.class);
                // open calling activity if there is call
                if (isVideoCalling) {
                    intent = new Intent(appContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    intent = new Intent(appContext, VoiceCallActivity.class);
                } else {
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // single chat message
//                        IsCodeResult isCodeResult = CodeUtils.getIsCodeResult(appContext, message.getFrom());
//                        if (isCodeResult == null || isCodeResult.getIscode() == 1) {
//                            return null;
//                        }
                        EaseUser userInfo = EaseUserUtils.getUserInfo(message.getFrom());
                        if(userInfo.getIsLock() == 1)
                            return null;
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
//                        intent.putExtra(Constant.ISCODE, isCodeResult);
                    } else { // group chat message
                        // message.getTo() is the group id
                        if(GroupManger.hasGroupUserInfo(message.getTo())) {
                            intent.putExtra("userId", message.getTo());
                            if (chatType == ChatType.GroupChat) {
                                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                            } else {
                                intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                            }
                        }else {
                            return null;
                        }

                    }
                }
                return intent;
            }
        });
    }

    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        syncContactsListeners = new ArrayList<DataSyncListener>();
        syncBlackListListeners = new ArrayList<DataSyncListener>();

        isGroupsSyncedWithServer = demoModel.isGroupsSynced();
        isContactsSyncedWithServer = demoModel.isContactSynced();
        isBlackListSyncedWithServer = demoModel.isBacklistSynced();

//        isContactsSyncedWithServer = false;

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(Constant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {//下线通知走这个
                    onUserException(Constant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(Constant.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {
                EMLog.d("global listener", "onDisconnect/");
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive
                // the events
                if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
                    EMLog.d(TAG, "group and contact already synced with servre");
                } else {
                    if (!isGroupsSyncedWithServer) {
                        asyncFetchGroupsFromServer(null);
                    }

                    if (!isContactsSyncedWithServer) {
                        asyncFetchContactsFromServer(null);
                    }

                    if (!isBlackListSyncedWithServer) {
                        asyncFetchBlackListFromServer(null);
                    }
                }
            }
        };

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        EMClient.getInstance().conferenceManager().addConferenceListener(new EMConferenceListener() {
            @Override public void onMemberJoined(String username) {
                EMLog.i(TAG, String.format("member joined username: %s, member: %d", username,
                        EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
            }

            @Override public void onMemberExited(String username) {
                EMLog.i(TAG, String.format("member exited username: %s, member size: %d", username,
                        EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
            }

            @Override public void onStreamAdded(EMConferenceStream stream) {
                EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));

            }

            @Override public void onStreamRemoved(EMConferenceStream stream) {
                EMLog.i(TAG, String.format("Stream removed streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
            }

            @Override public void onStreamUpdate(EMConferenceStream stream) {
                EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
            }

            @Override public void onPassiveLeave(int error, String message) {
                EMLog.i(TAG, String.format("passive leave code: %d, message: %s", error, message));
            }

            @Override public void onConferenceState(ConferenceState state) {
                EMLog.i(TAG, String.format("State code=%d", state.ordinal()));
            }

            @Override public void onStreamSetup(String streamId) {
                EMLog.i(TAG, String.format("Stream id - %s", streamId));
            }

            @Override public void onReceiveInvite(String confId, String password, String extension) {
                EMLog.i(TAG, String.format("Receive conference invite confId: %s, password: %s, extension: %s", confId, password, extension));

                try {
                    if(easeUI.getTopActivity().getClass().getSimpleName().equals("ConferenceActivity")) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject(extension);
                    String groupId = (String) jsonObject.get("groupId");
                    String creater = (String) jsonObject.get("creater");
                    String pic = (String) jsonObject.get("pic");
                    String pics = (String) jsonObject.get("pics");
                    String nickname = (String) jsonObject.get("nickname");
                    Intent conferenceIntent = new Intent(appContext, ConferenceActivity.class);
                    conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_ID, confId);
                    conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_PASS, password);
                    conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_IS_CREATOR, false);
                    conferenceIntent.putExtra(Constant.GROUP_ID, groupId);
                    conferenceIntent.putExtra("nickname", nickname);
                    conferenceIntent.putExtra("pic", pic);
                    conferenceIntent.putExtra("pics", pics);
                    conferenceIntent.putExtra("creater", creater);
                    conferenceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(conferenceIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //register incoming call receiver
        appContext.registerReceiver(callReceiver, callFilter);
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();

    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * group change listener
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            new InviteMessgeDao(appContext).deleteMessage(groupId);

            // user invite you to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            Log.d(TAG, "receive invitation to join the group：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {

            new InviteMessgeDao(appContext).deleteMessage(groupId);

            //user accept your invitation
            boolean hasGroup = false;
            EMGroup _group = null;
            for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    _group = group;
                    break;
                }
            }
            if (!hasGroup)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(_group == null ? groupId : _group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            Log.d(TAG, invitee + "Accept to join the group：" + _group == null ? groupId : _group.getGroupName());
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

            new InviteMessgeDao(appContext).deleteMessage(groupId);

            //user declined your invitation
            EMGroup group = null;
            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (_group.getGroupId().equals(groupId)) {
                    group = _group;
                    break;
                }
            }
            if (group == null)
                return;

            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            Log.d(TAG, invitee + "Declined to join the group：" + group.getGroupName());
            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //user is removed from group
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            // group is dismissed,
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

            // user apply to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " Apply to join group：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            String st4 = appContext.getString(com.tg.tgt.R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
            msg.setStatus(Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            // your application was declined, we do nothing here in demo
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(final String groupId, String inviter, String inviteMessage) {
            /*final List<String> data = new ArrayList<>();
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                    try {

                        EMCursorResult<String> result = EMClient.getInstance().groupManager().fetchGroupMembers
                                (groupId, "", Integer.MAX_VALUE);
                        data.addAll(result.getData());
                        String owner = EMClient.getInstance().groupManager().getGroupFromServer(groupId).getOwner();
                        e.onNext(owner);
                    } catch (HyphenateException ee) {
                        e.onError(ee);
                        ee.printStackTrace();
                    }
                    e.onComplete();

                }
            }).subscribeOn(Schedulers.newThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(@NonNull Disposable disposable) throws Exception {
                            try {
                                ((IHttpReact) ActMgrs.getActManager().currentActivity()).addRxDestroy(disposable);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {

                        }
                    })
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s) throws Exception {
                            EMMessage msg = saveCreateGroupHint(s, groupId, data);
                            getNotifier().vibrateAndPlayTone(msg);
                            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
                        }
                    });*/
            //TODO  进群消息


            // got an invitation
//            String st3 = appContext.getString(com.tg.tgt.R.string.Invite_you_to_join_a_group_chat);
//            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
//            msg.setChatType(ChatType.GroupChat);
//            msg.setFrom(inviter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new EMTextMessageBody(inviter + " " +st3));
//            msg.setStatus(EMMessage.Status.SUCCESS);
//            // save invitation as messages
//            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify invitation message
//            getNotifier().vibrateAndPlayTone(msg);
//            EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
//            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        private EMMessage saveCreateGroupHint(String owner, String groupId, List<String> members) {
            String str;
            if (EMClient.getInstance().getCurrentUser().equals(owner)) {
                str = String.format("你邀请了%s进群", getMembers(members));
            } else {
                members.remove(EMClient.getInstance().getCurrentUser());
                if (members.size() > 0)
                    str = String.format("%s邀请了你、%s进群", owner, getMembers(members));
                else
                    str = owner + "邀请了你进群";
            }
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setTo(groupId);
            message.addBody(new EMTextMessageBody(str));
            message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_INVITE_INTO_GROUP, true);
            message.setMsgId(UUID.randomUUID().toString());
            message.setChatType(EMMessage.ChatType.GroupChat);
            message.setStatus(EMMessage.Status.SUCCESS);
            EMClient.getInstance().chatManager().saveMessage(message);
            return message;
        }

        public String getMembers(List<String> members) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < members.size(); i++) {
                String con = EaseUserUtils.getUserInfo(members.get(i)).safeGetRemark();
                if (con == null)
                    con = EaseUserUtils.getUserInfo(members.get(i)).getUsername();
                sb.append(con);
                if (i != members.size() - 1)
                    sb.append("、");
            }
            return sb.toString();
        }

        // ============================= group_reform new add api begin
        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast("onMuterListAdded: " + sb.toString());
        }


        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            showToast("onMuterListRemoved: " + sb.toString());
        }


        @Override
        public void onAdminAdded(String groupId, String administrator) {
            showToast("onAdminAdded: " + administrator);
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            showToast("onAdminRemoved: " + administrator);
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            showToast("onOwnerChanged new:" + newOwner + " old:" + oldOwner);
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            EMLog.d(TAG, "onMemberJoined");
            showToast("onMemberJoined: " + member);
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            EMLog.d(TAG, "onMemberJoined");
            showToast("onMemberExited: " + member);
            GroupManger.deleteGroupUser(groupId, member);
        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
        // ============================= group_reform new add api end
    }

    void showToast(final String message) {
        if(!BuildConfig.DEBUG){
            return;
        }
        Message msg = Message.obtain(handler, 0, message);
        handler.sendMessage(msg);
    }

    protected android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            Toast.makeText(appContext, str, Toast.LENGTH_LONG).show();
        }
    };

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // save contact
            /*Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);

            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);*/

            App.needRefresh = username;

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            //TODO 不删除
//            invteMessgeDao.deleteMessage(username);

            //TODO 删除好友时所有消息清空
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
            if (conversation != null) {
                conversation.clearAllMessages();
            }
            EMClient.getInstance().chatManager().deleteConversation(username, false);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "apply to be your friend,reason: " + reason);
            // set invitation status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onFriendRequestAccepted(String username) {
//            Log.d(TAG, username + "accept your request");
            /*List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "accept your request");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));*/
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            // your request was refused
            Log.d(username, username + " refused to your request");
        }
    }

    /**
     * save and notify invitation message
     * @param msg
     */
    private void notifyNewInviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        Log.e("Tag","msg=notifyNewInviteMessage");
        inviteMessgeDao.saveMessage(msg);
        Log.e("Tag","msg=notifyNewInviteMessage");
        //increase the unread message count
        Log.i("qqq","加了");
        inviteMessgeDao.saveUnreadMessageCount(inviteMessgeDao.getUnreadMessagesCount()+1);
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        if (user == null && getRobotList() != null) {
            user = getRobotList().get(username);
        }

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    private EaseUser getUserInfo(EMMessage msg) {
        EaseUser userInfo = getUserInfo(msg.getFrom());
        if(msg.getChatType().equals(ChatType.Chat)){
            return userInfo;
        }else {
            if (!TextUtils.isEmpty(userInfo.getRemark())){
                return userInfo;
            }
            String nick = msg.getStringAttribute(Constant.MESSAGE_ATTR_NICKNAME,"");
            if(!TextUtils.isEmpty(nick)){
                userInfo.setNickname(nick);
                return userInfo;
            }
            return userInfo;
        }
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    // in background, do not refresh UI, notify it in notification bar
        //            EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                    if (!easeUI.hasForegroundActivies()) {
                        if (MainActivity.messageCountHandler != null)
                            MainActivity.messageCountHandler.sendEmptyMessage(1);
                        getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message"+message.conversationId()+"+from"+message.getFrom());
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    //red packet code : 处理红包回执透传消息
                    if (!easeUI.hasForegroundActivies()) {
                        if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                            RedPacketUtil.receiveRedPacketAckMessage(message);
                            broadcastManager.sendBroadcast(new Intent(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION));
                        }
                    }

                    if (action.equals("__Call_ReqP2P_ConferencePattern")) {
                        String title = message.getStringAttribute("em_apns_ext", "conference call");
                        Toast.makeText(appContext, title, Toast.LENGTH_LONG).show();
                    }
                    if(action.equals(EaseConstant.MSG_ID)){
                     /*   if(ActMgrs.getActManager().currentActivity()instanceof ChatActivity){
                        }else {*/
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
                            String id = message.getStringAttribute(EaseConstant.MSG_ID, null);
                            String name =message.getStringAttribute(EaseConstant.MSG_NAME, null);
                            long time = 0;
                            for(EMMessage msg:conversation.getAllMessages()){
                                if(msg.getMsgId().equals(id)){
                                    time=msg.getMsgTime();
                                }
                            }
                            String nickname="";String name2="";
                            if(conversation.getType()== EMConversation.EMConversationType.GroupChat){
                                nickname=appContext.getString(R.string.msg_recall_by_user,name);
                                name2=name;
                            }else {
                                nickname=appContext.getString(R.string.msg_recall_by_user,appContext.getString(R.string.other));
                                name2=appContext.getString(R.string.other);
                            }
                            EMMessage msgNotification = EMMessage.createTxtSendMessage(nickname,message.conversationId());
                            EMTextMessageBody txtBody = new EMTextMessageBody(appContext.getResources().getString(R.string.msg_recall_by_user,name2));
                            msgNotification.addBody(txtBody);
                            msgNotification.setMsgTime(time);
                            msgNotification.setLocalTime(time);
                            msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                            msgNotification.setAttribute(EaseConstant.MSG_NAME,name);
                            msgNotification.setStatus(EMMessage.Status.SUCCESS);
                            EMClient.getInstance().chatManager().saveMessage(msgNotification);
                            conversation.removeMessage(id);
                        if (MainActivity.messageCountHandler != null){
                            for(EMMessage msg:conversation.getAllMessages()){
                                if(msg.getMsgId().equals(id)){
                                    if(msg.isUnread()==true){
                                        MainActivity.messageCountHandler.sendEmptyMessage(2);
                                    }
                                }
                            }
                        }
                     //   }
                    }else {
                        handleCmdAction(message);
                    }
                    //end of red packet code
                    //获取扩展属性 此处省略
                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:");
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    private static final String CMD_SPLIT = "_";
    private static final String CMD_USERADD_ = "USERADD_";
    private static final String CMD_USERPASS_ = "USERPASS_";
    private static final String CMD_USERNOTPASS_ = "USERNOTPASS_";
    public static final String CMD_CANCEL_ = "CANCEL_";
    public static final String CMD_REJECT_ = "REJECT_";

    private static Gson sGson;

    /**
     * 处理通过透彻发送的添加好友，删除好友等操作，其中：
     * USERADD_{messageId}_{message}		申请加好友
     * USERPASS							同意好友申请
     * USERNOTPASS_{message}				拒绝好友申请
     * @param message
     */
    private void handleCmdAction(EMMessage message) {
        if (!(message.getBody() instanceof EMCmdMessageBody)) {
            return;
        }
        EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
        String action = cmdMsgBody.action();
        Log.i("qqq","哈哈"+action);
        final String username = message.getFrom();
        Log.e("Tag","action="+action);
        if(action.startsWith(CMD_USERADD_)){
            //头的长度
            int head_length = CMD_USERADD_.length();
            //除了头，第一个分割符的index
            int i = action.indexOf(CMD_SPLIT, head_length);
            long messageId = Long.parseLong(action.substring(head_length, i));
            //reason是被拼接的json，例如action:USERADD_98_{"nickname":"ufuf","reason":"hh","avatar":"http://192.168.2.78:9998/group1/M00/00/01/wKgCTln2zzCATNoGAAA00JG5ens821.jpg"}
            String json = action.substring(i+1);

//            saveUserInfoFromJson(json);

            /*List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username) && inviteMessage.getStatus().ordinal() == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal()) {
                    inviteMessgeDao.deleteMessage(username);
                    break;
                }
            }*/
            inviteMessgeDao.deleteMessage(username, true);
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(json);
            msg.setMessageId(String.valueOf(messageId));
            Log.d(TAG, username + "apply to be your friend,json: " + json);
            // set invitation status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }else if(action.startsWith(CMD_USERPASS_)){
            //json是被拼接的json，例如action:USERADD_98_{"nickname":"ufuf","json":"hh","avatar":"http://192.168.2.78:9998/group1/M00/00/01/wKgCTln2zzCATNoGAAA00JG5ens821.jpg"}
            String json = action.substring(CMD_USERPASS_.length());
            Log.e("Tag","msg="+json);
            saveUserInfoFromJson(json);
            Log.e("Tag","msg="+json);
            //saveUserInfoFromJson(json);

            /*List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }*/
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setReason(json);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "accept your request");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            Log.e("Tag","msg="+json);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            saveUserInfoFromJson(json);
            /*CodeUtils.fetchUser(null, userId, new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if(!aBoolean){
                        //这里为了防止拉取信息失败，保存下用户的id
                        EaseUser userInfo = EaseUserUtils.getUserInfo(username);
                        if(userInfo==null || TextUtils.isEmpty(userInfo.getChatid())){
                            EaseUser easeUser = new EaseUser(username);
                            easeUser.setChatid(userId);
                            DemoHelper.getInstance().updateContact(easeUser);
                        }
                    }
                    List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
                    for (InviteMessage inviteMessage : msgs) {
                        if (inviteMessage.getFrom().equals(username)) {
                            inviteMessgeDao.deleteMessage(username);
                        }
                    }
                    // save invitation as message
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(username);
                    msg.setTime(System.currentTimeMillis());
                    Log.d(TAG, username + "accept your request");
                    msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
                    notifyNewInviteMessage(msg);
                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                }
            });*/
        }else if(action.startsWith(CMD_USERNOTPASS_)){
            //json是被拼接的json，例如action:USERADD_98_{"nickname":"ufuf","reason":"hh","avatar":"http://192.168.2.78:9998/group1/M00/00/01/wKgCTln2zzCATNoGAAA00JG5ens821.jpg"}
            String json = action.substring(CMD_USERNOTPASS_.length());

//            saveUserInfoFromJson(json);

            /*List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }*/
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setReason(json);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "refuse your request");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }else if(action.startsWith(CMD_CANCEL_)){
            try {
                if(easeUI.getTopActivity().getClass().getSimpleName().equals("ConferenceActivity")){
                    ((ConferenceActivity)easeUI.getTopActivity()).userCancelInvite(message.getFrom(),action.substring(CMD_CANCEL_.length()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(action.startsWith(CMD_REJECT_)){
            try {
                if(easeUI.getTopActivity().getClass().getSimpleName().equals("ConferenceActivity")){
                    ((ConferenceActivity)easeUI.getTopActivity()).userRejectInvite(message.getFrom(),action.substring(CMD_REJECT_.length()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                ActionBean actionBean = getGson().fromJson(action, ActionBean.class);
                DBManager.getInstance().insertMomentAction(actionBean);
                DBManager.getInstance().saveUnreadMotionActionCount(1);
                if (!TextUtils.isEmpty(actionBean.getPicture()))
                    SpUtils.put(appContext,"lastHeadImage",actionBean.getPicture());
                if (EaseConstant.isFriendsView){
                    RxBus.get().send(BusCode.FRIENDVIEW_ACTION);
                }else {
                    RxBus.get().send(BusCode.MOMENT_ACTION);
                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private Gson getGson(){
        if(sGson == null)
            sGson = new Gson();
        return sGson;
    }

    /**
     * 用来保存用户信息
     * @param json 格式类似于{"nickname":"ufuf","reason":"hh","avatar":"http://192.168.2.78:9998/group1/M00/00/01/wKgCTln2zzCATNoGAAA00JG5ens821.jpg"}
     * @return
     */
    private boolean saveUserInfoFromJson(String json){
        boolean success = false;
        Log.e("Tag","msg3="+json);
        if(sGson == null)
            sGson = new Gson();
        try {
            Log.e("Tag","msg2="+json);
            UserFriendModel userFriendModel = sGson.fromJson(json, UserFriendModel.class);
            EaseUser easeUser = CodeUtils.wrapUser(userFriendModel);
            saveContact(easeUser);
            success = true;
        } catch (JsonSyntaxException e) {
            Log.e("Tag","msg=");
            e.printStackTrace();
        }
        return success;
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken
     *            whether you need unbind your device token
     * @param callback
     *            callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * get instance of EaseNotifier
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public DemoModel getModel() {
        return (DemoModel) demoModel;
    }

    /**
     * update contact list
     *
     * @param aContactList
     */
    public void setContactList(Map<String, EaseUser> aContactList) {
        if (aContactList == null) {
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    private String toJson(Object obj,int method) {
        // TODO Auto-generated method stub
        if (method==1) {
            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        }else if(method==2){
            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2=gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    /**
     * save single contact
     */
    public void saveContact(EaseUser user) {
        Log.i("id",toJson(user,1));
        contactList.put(user.getUsername().toLowerCase(), user);
        demoModel.saveContact(user);
        Log.i("idd",toJson(demoModel.getContactList(),1));
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = demoModel.getContactList();
        }
        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }

    /**
     * set current username
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }

    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    public Map<String, RobotUser> getRobotList() {
        if (isLoggedIn() && robotList == null) {
            robotList = demoModel.getRobotList();
        }
        return robotList;
    }

    /**
     * update user list to cache and database
     *
     * @param contactInfoList
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        if(contactList == null)
            contactList = new Hashtable<>();
        contactList.clear();
        for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername().toLowerCase(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        demoModel.saveContactList(mList);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.remove(listener);
        }
    }

    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }

    public void addSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.add(listener);
        }
    }

    public void removeSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.remove(listener);
        }
    }

    /**
     * Get group list from server
     * This method will save the sync state
     * @throws HyphenateException
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    try {
                        GroupManger.getInstance().fetchAllGroup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isGroupsSyncedWithServer = false;
                        isSyncingGroupsWithServer = false;
                        noitifyGroupSyncListeners(false);
                        return;
                    }

                    demoModel.setGroupsSynced(true);

                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    //notify sync group list success
                    noitifyGroupSyncListeners(true);

                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (HyphenateException e) {
                    demoModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchContactsFromServer(final EMValueCallBack<List<EaseUser>> callback) {
        if (isSyncingContactsWithServer) {
            return;
        }

        isSyncingContactsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                List<String> usernames = null;
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isContactsSyncedWithServer = false;
                        isSyncingContactsWithServer = false;
                        notifyContactsSyncListener(false);
                        return;
                    }

                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
                        EaseCommonUtils.setUserInitialLetter(user);
                        userlist.put(username, user);
                    }
                    // save the contact list to cache
                    if(getContactList().size() == 0 && userlist.size()>0) {
                        getContactList().clear();
                        getContactList().putAll(userlist);

                        // save the contact list to database
                        UserDao dao = new UserDao(appContext);
                        List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                        dao.saveContactList(users);
                    }

                    demoModel.setContactSynced(true);
                    EMLog.d(TAG, "set contact syn status to true");

                    isContactsSyncedWithServer = true;
                    isSyncingContactsWithServer = false;

                    //notify sync success
                    notifyContactsSyncListener(true);

                    getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new
                            EMValueCallBack<List<EaseUser>>() {

                                @Override
                                public void onSuccess(List<EaseUser> uList) {
                                    updateContactList(uList);
                                    getUserProfileManager().notifyContactInfosSyncListener(true);
                                    if (callback != null) {
                                        callback.onSuccess(uList);
                                    }
                                }

                                @Override
                                public void onError(int error, String errorMsg) {
                                    if (callback != null) {
                                        callback.onError(error, errorMsg);
                                    }
                                }
                            });

                } catch (HyphenateException e) {
                    demoModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    notifyContactsSyncListener(false);
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyContactsSyncListener(boolean success) {
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {

        if (isSyncingBlackListWithServer) {
            return;
        }

        isSyncingBlackListWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();

                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isBlackListSyncedWithServer = false;
                        isSyncingBlackListWithServer = false;
                        notifyBlackListSyncListener(false);
                        return;
                    }

                    demoModel.setBlacklistSynced(true);

                    isBlackListSyncedWithServer = true;
                    isSyncingBlackListWithServer = false;

                    notifyBlackListSyncListener(true);
                    if (callback != null) {
                        callback.onSuccess(usernames);
                    }
                } catch (HyphenateException e) {
                    demoModel.setBlacklistSynced(false);

                    isBlackListSyncedWithServer = false;
                    isSyncingBlackListWithServer = true;
                    e.printStackTrace();

                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyBlackListSyncListener(boolean success) {
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }

    synchronized void reset() {
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        getUserProfileManager().reset();
        DemoDBManager.getInstance().closeDB();
        GroupManger.getInstance().reset();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

}