package com.tg.tgt.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.redpacket.utils.RedPacketUtil;
import com.easemob.redpacket.widget.ChatRowRandomPacket;
import com.easemob.redpacket.widget.ChatRowRedPacket;
import com.easemob.redpacket.widget.ChatRowRedPacketAck;
import com.easemob.redpacketsdk.RPSendPacketCallback;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.easemob.redpacketui.utils.RPRedPacketUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.easeui.utils.rxbus2.RxBus;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.easeui.utils.rxbus2.ThreadMode;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.EasyUtils;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.conference.ConferenceActivity;
import com.tg.tgt.conference.ConferenceInviteJoinActivity;
import com.tg.tgt.domain.RobotUser;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.widget.ChatRowVoiceCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper {
    public static final String TAG = "ChatFragment";

    // constant start from 11 to avoid conflict with constant in base class
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final int REQUEST_CODE_SELECT_CONFERENCE = 16;
    private static final int REQUEST_CODE_SELECT_CONFERENCE_VIDEO = 17;

    //设置密码的requestcode
    private static final int REQUEST_CODE_SET_PWD = 78;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    //red packet code : 红包功能使用的常量
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    private static final int MESSAGE_TYPE_RECV_RANDOM = 11;
    private static final int MESSAGE_TYPE_SEND_RANDOM = 12;

    private static final int MESSAGE_TYPE_INVITE_INTO_GROUP = 13;

    //阅后即焚
//    private static final int MESSAGE_TYPE_RECV_FIRE = 22;
//    private static final int MESSAGE_TYPE_SEND_FIRE = 23;

    private static final int ITEM_RED_PACKET = 16;

    //end of red packet code

    /**
     * if it is chatBot 
     */
    private boolean isRobot;
    private Map<String, GroupUserModel> mGroupUsers;

    private static final int CODE_DETAIL = BusCode.GROUP_DETAIL;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RxBus.get().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private long lastTime = 0;
    @Subscribe(code = CODE_DETAIL, threadMode = ThreadMode.MAIN)
    public void onDetail(){
        if(System.currentTimeMillis()-lastTime>500){
            lastTime = System.currentTimeMillis();
            L.i(TAG, "onDetail:fetchGroup:"+toChatUsername);
            GroupManger.fetchGroup((BaseActivity) mContext, toChatUsername, new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if(aBoolean) {
                        mGroupUsers = GroupManger.getGroupUsers(toChatUsername);
                        ((BaseAdapter) messageList.getListView().getAdapter()).notifyDataSetChanged();
                    }
                }
            }, false);
        }
    }

    @Override
    public void onDestroyView() {
        RxBus.get().unRegister(this);
        super.onDestroyView();
    }

    /**
     * 已经查询过的user，防止多次查询
     */
    private List<String> queriedUser;
    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        messageList.setInfoListener(new EaseChatRow.IInfoListener() {
            @Override
            public boolean setInfo(EMMessage message, ImageView avatar, TextView nick) {
                if(mGroupUsers == null){
                    mGroupUsers = GroupManger.getGroupUsers(toChatUsername);
                }
                EaseUser userInfo = EaseUserUtils.getUserInfo(message.getFrom());
                if(userInfo != null && !TextUtils.isEmpty(userInfo.getRemark())){
                    nick.setText(userInfo.safeGetRemark());
                    GlideApp.with(ChatFragment.this).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy
                            .ALL).placeholder(R.drawable.default_avatar).into(avatar);
                }else {
                    GroupUserModel model = mGroupUsers.get(message.getFrom());
                    if(model == null){
                        L.i(TAG, message.getFrom()+":数据为空，准备加载中");
                        if(queriedUser == null){
                            queriedUser = new ArrayList<String>();
                        }
                        if(!queriedUser.contains(message.getFrom())) {
                            queriedUser.add(message.getFrom());
                            RxBus.get().send(CODE_DETAIL);
                        }
                        return false;
                    }else {
                        nick.setText(model.getNickname());
                        GlideApp.with(ChatFragment.this).load(model.getPicture()).diskCacheStrategy(DiskCacheStrategy
                            .ALL).placeholder(R.drawable.default_avatar).into(avatar);
                    }
                }



                return true;
            }
        });
        super.setUpView();
        // set click listener
//        titleBar.setTitle(mIsCodeResult.safeGetRemark());
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                onBackPressed();
            }
        });
        //TODO 在这里添加表情
//        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        //TODO 暂时屏蔽@功能
//                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
//                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        iv_zhuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                intent.putExtra("forward_msg_id","duo");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            titleBar.setTitle(GroupManger.getGroup(toChatUsername).getGroupName());
        }
    }

    private int isCode;

    @Override
    protected void setChatPerson() {
        onAvatarClick(toChatUsername);
    }

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
//        super.registerExtendMenuItem();
        //extend menu items
        inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_video, com.tg.tgt.R.drawable.em_chat_video_selector, ITEM_VIDEO,
                extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, ITEM_TAKE_PICTURE, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_file, com.tg.tgt.R.drawable.em_chat_file_selector, ITEM_FILE,
                extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, ITEM_LOCATION, extendMenuItemClickListener);
        /*if (chatType == Constant.CHATTYPE_SINGLE) {
            inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_voice_call, com.tg.tgt.R.drawable.em_chat_voice_call_selector,
                    ITEM_VOICE_CALL, extendMenuItemClickListener);
            inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_video_call, com.tg.tgt.R.drawable.em_chat_video_call_selector,
                    ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }
        //聊天室暂时不支持红包功能
        //red packet code : 注册红包菜单选项
        if (chatType != Constant.CHATTYPE_CHATROOM) {
            inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_red_packet, com.tg.tgt.R.drawable.em_chat_red_packet_selector,
                    ITEM_RED_PACKET, extendMenuItemClickListener);
        }*/
        //end of red packet code
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // copy
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Log.i("dcz","RESULT_CODE_FORWARD");
                    EaseConstant.list_ms.clear();
                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    break;
                case ContextMenuActivity.RESULT_CODE_DUOFORWARD:
                    EaseConstant.MESSAGE_ATTR_SELECT=true;
                    inputMenu.setVisibility(View.GONE);zhuan.setVisibility(View.VISIBLE);
                    titleBar.setRightLayoutVisibility(View.INVISIBLE);
                    Log.i("dcz","点击了多条转发按钮");
                    EaseConstant.list_ms.clear();
                    EaseConstant.list_ms.add(contextMenuMessage.getMsgId());
                    ((BaseAdapter) messageList.getListView().getAdapter()).notifyDataSetChanged();
                    /*conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils
                            .getConversationType(chatType), true);*/
                    break;
                default:
                    break;
            }
        }else if(requestCode == UserProfileActivity.REQUEST_CODE_ISCODE){
            if(resultCode == UserProfileActivity.RESULT_OK){
                if(chatType == Constant.CHATTYPE_SINGLE) {
                    titleBar.setTitle(EaseUserUtils.getUserInfo(toChatUsername).safeGetRemark());
                }else {
                    titleBar.setTitle(GroupManger.getGroup(toChatUsername).getGroupName());
                }
            }
        }else if(requestCode == REQUEST_CODE_GROUP_DETAIL){
            if(resultCode == Activity.RESULT_OK)
                titleBar.setTitle(GroupManger.getGroup(toChatUsername).getGroupName());
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        int size = data.getIntExtra("size", -1);
                        /*File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System
                                .currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        VideoBean videoBean = new VideoBean(videoPath, null, duration);
                        videoBean.setSize(size);
                        sendVideo(videoBean);
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false, GroupManger.getGroupUsers(toChatUsername).get(username).getNickname());
                    }
                    break;
                case REQUEST_CODE_SELECT_CONFERENCE:
                    startActivity(new Intent(getActivity(), ConferenceActivity.class)
                            .putExtra(Constant.GROUP_ID, toChatUsername)
                            .putExtra(Constant.MEMBERS, data.getStringArrayExtra(Constant.MEMBERS))
                            .putExtra(Constant.EXTRA_CONFERENCE_IS_CREATOR, true));
                    break;
                case REQUEST_CODE_SELECT_CONFERENCE_VIDEO:
                    startActivity(new Intent(getActivity(), ConferenceActivity.class)
                            .putExtra(Constant.GROUP_ID, toChatUsername)
                            .putExtra(Constant.MEMBERS, data.getStringArrayExtra(Constant.MEMBERS))
                            .putExtra(Constant.EXTRA_CONFERENCE_IS_VIDEO, true)
                            .putExtra(Constant.EXTRA_CONFERENCE_IS_CREATOR, true));
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }

            //除了文本其他不需要阅后即焚
        EMMessage.Type type = message.getType();
        switch (type) {
            case TXT:
                if(!(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false) || RedPacketUtil.isRandomRedPacket(message)))
                    message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_FIRE, isFire);
                break;
            case VOICE:
            case IMAGE:
                message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_FIRE, isFire);
                break;
            default:
                break;
        }

        if(chatType != EaseConstant.CHATTYPE_SINGLE){
            message.setAttribute(Constant.MESSAGE_ATTR_NICKNAME, SpUtils.get(mContext, Constant.NICKNAME, ""));
        }
    }


    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            GroupModel group = GroupManger.getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), com.tg.tgt.R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity2.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == Constant.CHATTYPE_CHATROOM) {
            startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId",
                    toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(final String username) {
        //handling when user click avatar
        if(TextUtils.isEmpty(username)/* || username.equals(EMClient.getInstance().getCurrentUser())*/){
            return;
        }

        final Intent intent = new Intent(getActivity(), UserProfileActivity.class);

        if(chatType != EaseConstant.CHATTYPE_SINGLE){
//            ToastUtils.showToast(App.applicationContext, "!CHATTYPE_SINGLE");
//            return;
            //判断是否群主，不是群主点击无效
            /*if(!App.getMyUid().equals(GroupManger.getGroup(toChatUsername).getUserId().toString())){
                return;
            }*/
            //TODO 群组点击头像都无效
            if(true)
                return;
            try {
            String userId = GroupManger.getGroupUsers(toChatUsername).get(username).getUserId().toString();
            intent.putExtra("userId", userId);

            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }


        intent.putExtra("username", username);
        startActivityForResult(intent, UserProfileActivity.REQUEST_CODE_ISCODE);
    }
    /**
     *  长按头像走这个方法
     * */
    @Override
    public void onAvatarLongClick(String username) {
        if(EaseConstant.MESSAGE_ATTR_SELECT==false){
            Map<String, GroupUserModel> bean = GroupManger.getGroupUsers(toChatUsername);
            if(bean!=null){
                inputAtUsername(username,bean.get(username).getNickname());
            }
        }
    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        //red packet code : 拆红包页面
        Log.i("dcz","onMessageBubbleClick");
        if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
            RedPacketUtil.openRedPacket(getActivity(), chatType, message, toChatUsername, messageList);
            return true;
        }
        //end of red packet code
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //red packet code : 处理红包回执透传消息
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action
            if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                RedPacketUtil.receiveRedPacketAckMessage(message);
                messageList.refresh();
            }
        }
        //end of red packet code
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        // no message forward when in chat room
        Log.i("dcz","onMessageBubbleLongClick");
        if(EaseConstant.MESSAGE_ATTR_SELECT==false){
            startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message)
                            .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                    REQUEST_CODE_CONTEXT_MENU);
        }
    }



    @Override
    public void onVideoClick() {
//        startVideoCall();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.chat_type, menu);
    }

    @Override
    public boolean
    onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.voice_chat:
                startVoiceCall();
                break;
            case R.id.video_chat:
                startVideoCall();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO:
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1231);
                break;
            case ITEM_FILE: //file
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            //red packet code : 进入发红包页面
            case ITEM_RED_PACKET:
                //注意：不再支持原有的startActivityForResult进入红包相关页面
                int itemType;
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    itemType = RPConstant.RP_ITEM_TYPE_SINGLE;
                    //小额随机红包
                    //itemType = RPConstant.RP_ITEM_TYPE_RANDOM;
                } else {
                    itemType = RPConstant.RP_ITEM_TYPE_GROUP;
                }
                RedPacketUtil.startRedPacket(getActivity(), itemType, toChatUsername, new RPSendPacketCallback() {
                    @Override
                    public void onGenerateRedPacketId(String redPacketId) {

                    }

                    @Override
                    public void onSendPacketSuccess(RedPacketInfo redPacketInfo) {
                        sendMessage(RedPacketUtil.createRPMessage(getActivity(), redPacketInfo, toChatUsername));
                    }
                });
                break;
//end of red packet code
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), com.tg.tgt.R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else if(chatType == EaseConstant.CHATTYPE_SINGLE){
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }else {
            startActivityForResult(new Intent(getActivity(), ConferenceInviteJoinActivity.class).putExtra(Constant.GROUP_ID, toChatUsername), REQUEST_CODE_SELECT_CONFERENCE);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), com.tg.tgt.R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else if(chatType == EaseConstant.CHATTYPE_SINGLE) {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }else {
            startActivityForResult(new Intent(getActivity(), ConferenceInviteJoinActivity.class).putExtra(Constant.GROUP_ID, toChatUsername), REQUEST_CODE_SELECT_CONFERENCE_VIDEO);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * chat row provider
     *
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 10/*+1*/;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL :
                            MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL :
                            MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
                //red packet code : 红包消息、红包回执消息以及转账消息的chatrow type
                else if (RedPacketUtil.isRandomRedPacket(message)) {
                    //小额随机红包
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RANDOM :
                            MESSAGE_TYPE_SEND_RANDOM;
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
                    //发送红包消息
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET :
                            MESSAGE_TYPE_SEND_RED_PACKET;
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
                    //领取红包消息
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ACK :
                            MESSAGE_TYPE_SEND_RED_PACKET_ACK;
                }
                //end of red packet code
                //加入群聊显示的一条 您邀请xx、xx加入了群聊
//                else if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_INVITE_INTO_GROUP, false)){
//                    return MESSAGE_TYPE_INVITE_INTO_GROUP;
//                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }
                //red packet code : 红包消息、红包回执消息以及转账消息的chat row
                else if (RedPacketUtil.isRandomRedPacket(message)) {//小额随机红包
                    return new ChatRowRandomPacket(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {//红包消息
                    return new ChatRowRedPacket(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false))
                {//红包回执消息
                    return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
                }
                //end of red packet code
            }
            return null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //调用该方法可防止红包SDK引起的内存泄漏
        RPRedPacketUtil.getInstance().detachView();

    }

}