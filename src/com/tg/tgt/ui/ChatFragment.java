package com.tg.tgt.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.ui.EaseShowNormalFileActivity;
import com.hyphenate.easeui.utils.AESCodeer;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.RSAUtil;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.utils.photo.PhotoUtils;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.easeui.utils.rxbus2.RxBus;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.easeui.utils.rxbus2.ThreadMode;
import com.hyphenate.easeui.utils.videocompress.CompressListener;
import com.hyphenate.easeui.utils.videocompress.Compressor;
import com.hyphenate.easeui.utils.videocompress.InitListener;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoice;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.conference.ConferenceActivity;
import com.tg.tgt.conference.ConferenceInviteJoinActivity;
import com.tg.tgt.domain.RobotUser;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.moment.bean.CollectBean;
import com.tg.tgt.moment.bean.User;
import com.tg.tgt.utils.AMRToWAV;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.widget.ChatRowVoiceCall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    private static final int REQUEST_CODE_SELECT_BUSINESS = 18;

    //设置密码的requestcode
    private static final int REQUEST_CODE_SET_PWD = 78;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_SENT_BUSSINES = 5;
    private static final int MESSAGE_RECV_BUSSINES = 6;

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

    private static final int COLLECT_DOWNLOAD = 99;
    private static final int COLLECT_SUCCESS = 100;
    private static final int COLLECT_FAIL = 101;

    //end of red packet code

    /**
     * if it is chatBot 
     */
    private boolean isRobot;
    private Map<String, GroupUserModel> mGroupUsers;

    private static final int CODE_DETAIL = BusCode.GROUP_DETAIL;


    private EMMessage.Type typeSelect;
    private EMMessage messageDownLoad;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void getRecvChatKey(){//单聊
        Log.i("www1",toChatUsername);
        ApiManger2.getApiService()
                .getRecvChatKey(toChatUsername)
                .compose(((BaseActivity)mContext).<HttpResult<KeyBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<KeyBean>() {
                    @Override
                    protected void onSuccess(KeyBean bean) {
                        EaseApp.receiver_pub=bean;
                        List<KeyBean>list = new ArrayList<KeyBean>();list.add(bean);
                        if(map==null){
                            map=new HashMap<>();
                        }
                        map.put(toChatUsername,list);
                        String string = CodeUtils.toJson(map, 1);
                        EaseApp.sf.edit().putString(EaseApp.map_receiver,string).commit();
                       /* String z = EaseApp.sf.getString(EaseApp.map_receiver, null);
                        HashMap<String,List<KeyBean>> b = CodeUtils.toMap(z);
                        Log.i("www",string+"加"+ b.size());*/
                    }
                });
    }
    private void getApiService(){//群聊
        Log.i("www2",toChatUsername);
        ApiManger2.getApiService()
                .getGroupChatKey(toChatUsername)
                .compose(((BaseActivity)mContext).<HttpResult<List<KeyBean>>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<List<KeyBean>>() {
                    @Override
                    protected void onSuccess(List<KeyBean> list) {
                        //EaseApp.group_pub=list;
                        if(map==null){
                            map=new HashMap<>();
                        }
                        map.put(toChatUsername,list);
                        String string = CodeUtils.toJson(map, 1);
                        EaseApp.sf.edit().putString(EaseApp.map_group,string).commit();
                    }
                });
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
    private HashMap<String,List<KeyBean>> map=new HashMap<>();
    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        if(chatType == Constant.CHATTYPE_GROUP){
            String z = EaseApp.sf.getString(EaseApp.map_group, null);//得到总map
            map.clear();map = CodeUtils.toMap(z);
            if(z==null||map.get(toChatUsername)==null){
                getApiService();
            }
        }else {
            String z = EaseApp.sf.getString(EaseApp.map_receiver, null);//得到总map
            map.clear();map = CodeUtils.toMap(z);
            if(z==null||map.get(toChatUsername)==null){
                getRecvChatKey();
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
        titleBar.setBackgroundColor(getResources().getColor(com.hyphenate.easeui.R.color.white));
        //TODO 在这里添加表情
//        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        //TODO 暂时屏蔽@功能
                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
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
        zhuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                yan();
            }
        });
        del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delate();
            }
        });
        collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collect(1);
            }
        });
    }
    protected void yan() {
        for(int i=0;i<EaseConstant.list_ms.size();i++){
            final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(EaseConstant.list_ms.get(i));
            EMMessage.Type type = forward_msg.getType();
            /*String ty = type + "";
            Log.i("dcz_type",type+"");*/
            if(type== EMMessage.Type.VIDEO){
                String path = ((EMVideoMessageBody) forward_msg.getBody()).getLocalUrl();
                if (path != null) {
                    File file = new File(path);
                    if (!file.exists()) {
                        Toast.makeText(getActivity(),getActivity().getString(R.string.con),Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            if(type==EMMessage.Type.VOICE||type==EMMessage.Type.FILE||type==EMMessage.Type.LOCATION){
                Toast.makeText(getActivity(),getActivity().getString(R.string.ti10),Toast.LENGTH_LONG).show();
                return;
            }
            if(type==EMMessage.Type.TXT){
                if(forward_msg.getBooleanAttribute(EaseConstant.BUSSINES_ID, false)){
                    Toast.makeText(getActivity(),getActivity().getString(R.string.ti10),Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if(i==EaseConstant.list_ms.size()-1){
                Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                intent.putExtra("forward_msg_id","duo");
                startActivity(intent);
            }
        }
    }
    protected void delate(){
        showDia(getActivity(),getString(R.string.delete_ti),1);
    }
    protected void collect(int state){
        if(state==1){//禁掉多选
            onBackPressed();
        }else {
            downLoadFile();
        }
    }



    private int type ;
    private void createBody(File file,int type){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型
    //    File file=new File(pathFile);
        if (file != null) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);//表单类型
            builder.addFormDataPart("file", file.getName(), body);
        }
        builder.addFormDataPart("fromUid", contextMenuMessage.getFrom());
        builder.addFormDataPart("type",String.valueOf(type));
        builder.addFormDataPart("isFrom","1");
        if (type == 5){
            builder.addFormDataPart("content",((EMTextMessageBody) contextMenuMessage.getBody()).getMessage());
        }
        ApiManger2.getApiService()
                .collection(builder.build().parts())
                .compose(((BaseActivity)mContext).<HttpResult<CollectBean>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<CollectBean>() {
                    @Override
                    protected void onSuccess(CollectBean emptyData) {
                        Toast.makeText(mContext,"成功",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        Toast.makeText(mContext,"失败",Toast.LENGTH_LONG).show();
                    }
                });
        EaseConstant.isCollection = false;
    }

    /**
     * 执行ffmpeg命令行
     * @param commandLine commandLine
     */
    private void executeFFmpegCmd(final String[] commandLine){
//        if(commandLine == null){
//            return;
//        }
//        FFmpegCmd.execute(commandLine, new FFmpegCmd.OnHandleListener() {
//            @Override
//            public void onBegin() {
//                Log.e(TAG, "handle audio onBegin...");
//                //mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
//            }
//
//            @Override
//            public void onEnd(int result) {
//                Log.e(TAG, "handle audio onEnd...");
//               // mHandler.obtainMessage(MSG_FINISH).sendToTarget();
//            }
//        });
    }

    private void ffmpegCommandAmr2Wav(final String source, final String target) {

        final String cmd = "-i "+source+" "+target;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCompressor == null) {
                    mCompressor = new Compressor(getActivity().getApplicationContext());
                    mCompressor.loadBinary(new InitListener() {
                        @Override
                        public void onLoadSuccess() {
                            Log.e("Tag","加载成功");
                            execCommand(cmd,target,source);
                        }

                        @Override
                        public void onLoadFail(String reason) {
                            Toast.makeText(getContext().getApplicationContext(), getString(R.string.compress_failed) + reason, Toast
                                    .LENGTH_SHORT).show();
                            Log.e("Tag","加载失败"+reason);
                            mCompressor = null;
                        }
                    });
                } else {
                    execCommand(cmd,target,source);
                }
            }
        }).start();
    }

    private Compressor mCompressor;
    private void execCommand(final String cmd,final String target,final String source) {
        mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(final String message) {
                Log.i(TAG, "success " + message);
                Log.e("Tag","转码成功"+source);
                Log.e("Tag","lllllllllllllllllllll"+target);
//                if (target.endsWith(".mp3")){
//                    Log.e("Tag","ooooooooooooooooooooooooooooo"+"          "+"-i " + source.substring(0,source.length()-5)+".mp3" + " -f wav" +
//                            target.substring(0, target.length() - 5) + ".wav");
//                    if (source.endsWith(".amr")) {
//                        execCommand("-i " + source.substring(0,source.length()-5)+".mp3" + " -f wav " +
//                                        target.substring(0, target.length() - 5) + ".wav",
//                                target.substring(0, target.length() - 5) + ".wav", source);
//                    }else {
//                        execCommand("-i " + source+".mp3" + " -f wav" +
//                                        target.substring(0, target.length() - 5) + ".wav",
//                                target.substring(0, target.length() - 5) + ".wav", source);
//                    }
//                }else {
                    Log.e("Tag","pppppppppppppppppppppppp");
                    createBody(new File(target.substring(0,target.length()-5)+".mp3"),type);
               // }
            }

            @Override
            public void onExecFail(final String reason) {
                Log.e("Tag","转码失败"+reason);
            }

            @Override
            public void onExecProgress(String message) {
                Log.i(TAG, "progress " + message);
            }
        });
    }

    private void openMyFile(int type){
        File file = null;
        if (type != 5) {
            if ((new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl())).exists()){
                Log.e("Tag","存在:"+((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl());
                if (type == 1 && !((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().endsWith(".jpg")) {
                    copyFile(((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl(),((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl()+"add.jpg");
                    file = new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl()+"add.jpg");
                    createBody(file,type);
                }else if (type == 3) {
                    if (((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().endsWith(".amr")) {
                        if (!(new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().substring(0,
                                ((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().length()-5)+".mp3")).exists()){
                        ffmpegCommandAmr2Wav(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl(),
                                ((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().substring(0,
                                        ((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().length() - 5) + ".mp3");
                        }else {
                            createBody(new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().substring(0,
                                    ((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl().length()-5)+".mp3"),type);
                        }
                    }else {
                        if (!(new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl()+".mp3")).exists()) {
                            ffmpegCommandAmr2Wav(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl(),
                                    ((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl() + ".mp3");
                        }else {
                            createBody(new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl()+".mp3"),type);
                        }
                    }
                }else {
                    file = new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl());
                    createBody(file,type);
                }
            }else {
                EaseConstant.isCollection = true;
                Intent mIntent = new Intent(mContext, EaseShowNormalFileActivity.class);
                mIntent.putExtra("msg",messageDownLoad);
                startActivityForResult(mIntent,COLLECT_DOWNLOAD);
            }
        }
    }
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()){
                newFile.createNewFile();
            }
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void downLoadFile(){
        switch (typeSelect){
            case TXT:
                createBody(null,5);
                type = 5;
                break;
            case FILE:
                openMyFile(4);
                type =4;
                break;
            case IMAGE:
                type = 1;
                openMyFile(1);
                break;
            case VIDEO:
                type=2;
                openMyFile(2);
                break;
            case VOICE:
                type = 3;
                openMyFile(3);
                break;
            case LOCATION:
                break;
        }
    }


    protected void zheng(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        if(type== EMMessage.Type.VIDEO){
            String path = ((EMVideoMessageBody) forward_msg.getBody()).getLocalUrl();
            if (path != null) {
                File file = new File(path);
                if (!file.exists()) {
                    Toast.makeText(getActivity(),getActivity().getString(R.string.noxz), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        EaseConstant.list_ms.clear();
        Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
        intent.putExtra("forward_msg_id",forward_msg_id);
        startActivity(intent);
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
        /*inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_video, com.tg.tgt.R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, ITEM_TAKE_PICTURE, extendMenuItemClickListener);*/
        inputMenu.registerExtendMenuItem(com.tg.tgt.R.string.attach_file, com.tg.tgt.R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.ease_chat_location_selector, ITEM_LOCATION, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.collection, R.drawable.collection,ITEM_COLLECTION, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.Business, R.drawable.ease_chat_bussines, ITEM_BUSINESS, extendMenuItemClickListener);
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
                    String message = mi(contextMenuMessage);
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,message));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    showDia(getActivity(),getString(R.string.delete_ti),0);
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Log.i("dcz","RESULT_CODE_FORWARD");
                    zheng( contextMenuMessage.getMsgId());
                    break;
                case ContextMenuActivity.RESULT_CODE_DUOFORWARD:
                    EaseConstant.MESSAGE_ATTR_SELECT=true;
                    inputMenu.setVisibility(View.GONE);ll_zhuan.setVisibility(View.VISIBLE);
                    titleBar.setRightLayoutVisibility(View.INVISIBLE);
                    Log.i("dcz","点击了多条转发按钮");
                    EaseConstant.list_ms.clear();
                    EaseConstant.list_ms.add(contextMenuMessage.getMsgId());
                    ((BaseAdapter) messageList.getListView().getAdapter()).notifyDataSetChanged();
                    /*conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils
                            .getConversationType(chatType), true);*/
                    break;
                case ContextMenuActivity.RESULT_CODE_RECALL:
                    che(getActivity(),getString(R.string.che_ti));
                    break;
                case ContextMenuActivity.RESULT_CODE_COLLECT:
                    collect(0);
                    break;
                case ContextMenuActivity.RESULT_CODE_PLAYVOICE:
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
        }else if (requestCode == COLLECT_DOWNLOAD){//收藏时没下载的情况
            if (resultCode == COLLECT_SUCCESS){
                if (type == 1 && !((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl().endsWith(".jpg")) {
                    copyFile(((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl(),((EMFileMessageBody)messageDownLoad.getBody()).getLocalUrl()+"add.jpg");
                    createBody(new File(((EMFileMessageBody) messageDownLoad.getBody()).getLocalUrl()+"add.jpg"),type);
                }else {
                    Log.e("Tag", "下载后路径" + ((EMFileMessageBody) ((EMMessage) data.getParcelableExtra("msg")).getBody()).getLocalUrl());
                    createBody(new File(((EMFileMessageBody) ((EMMessage) data.getParcelableExtra("msg")).getBody()).getLocalUrl()), type);
                }
            }
            if (resultCode == COLLECT_FAIL){
               // Toast.makeText(mContext,"失败",Toast.LENGTH_LONG).show();
            }
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
                        if(GroupManger.getGroupUsers(toChatUsername).get(username)==null){
                            inputAtUsername(username,true,username);
                        }else {
                            if(GroupManger.getGroupUsers(toChatUsername).get(username).getNickname()==null){
                                inputAtUsername(username,true,username);
                            }else {
                                inputAtUsername(username,true, GroupManger.getGroupUsers(toChatUsername).get(username).getNickname());
                            }
                        }
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
                case REQUEST_CODE_SELECT_BUSINESS:
                    Log.i("dcz","发送名片"+toChatUsername);
                    EMMessage message = EMMessage.createTxtSendMessage("名片",toChatUsername);
                    message.setAttribute(Constant.BUSSINES_ID,data.getStringExtra(Constant.BUSSINES_ID));
                    message.setAttribute(Constant.BUSSINES_NAME,data.getStringExtra(Constant.BUSSINES_NAME));
                    message.setAttribute(Constant.BUSSINES_NUMBER,data.getStringExtra(Constant.BUSSINES_NUMBER));
                    message.setAttribute(Constant.BUSSINES_PIC,data.getStringExtra(Constant.BUSSINES_PIC));
                    sendMessage(message);
                    //EMClient.getInstance().chatManager().sendMessage(message);
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
         /*   if(true)
                return;*/
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
        if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)==true){
            startVoiceCall();
            return true;
        }
        if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL,false)==true){
            startVideoCall();
            return true;
        }
        if(message.getBooleanAttribute(Constant.BUSSINES_ID,false)==true){
            String id = message.getStringAttribute(Constant.BUSSINES_ID, null);
            String name=message.getStringAttribute(Constant.BUSSINES_NAME,null);
            Log.i("ddd",id+"+"+name);
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            intent.putExtra("username",id);
            startActivity(intent);
        }
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
        Log.i("dcz","onMessageBubbleLongClick"+message.ext());
        typeSelect = message.getType();
        messageDownLoad = message;
        if(EaseConstant.MESSAGE_ATTR_SELECT==false){
            startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message)
                            .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                    REQUEST_CODE_CONTEXT_MENU);
        }
    }



    @Override
    public void onVideoClick(int i) {
//        startVideoCall();
        if(i==1){
            startVideoCall();
        }else {
            startVoiceCall();
        }
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
            case ITEM_COLLECTION:
                Toast.makeText(getActivity(),"开发中。。。",Toast.LENGTH_LONG).show();
                break;
            case ITEM_BUSINESS:
                startBusiness();
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
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
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
     *   发送名片
     * */
    protected void startBusiness(){
        startActivityForResult(new Intent(getActivity(),BusinessActivity.class).putExtra("type","1"),REQUEST_CODE_SELECT_BUSINESS);
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
                }else  if(message.getBooleanAttribute(EaseConstant.BUSSINES_ID, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_RECV_BUSSINES :
                            MESSAGE_SENT_BUSSINES;
                }
                /*else if(message.ext().get("VoiceOrVideoImage").equals("ease_chat_voice_call_receive")){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL :
                            MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if(message.ext().get("VoiceOrVideoImage").equals("ease_chat_video_call_receive")){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL :
                            MESSAGE_TYPE_SENT_VIDEO_CALL;
                }*/
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
                Log.i("dcz","getCustomChatRow");
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }
                /*if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BUSSINES, false)) {
                    return new ChatRowBussines(getActivity(), message, position, adapter);
                }*/
                Log.i("dcz",message.ext().toString());
                if(message.ext().get("VoiceOrVideoImage")!=null){
                    if(message.ext().get("VoiceOrVideoImage").equals("ease_chat_voice_call_receive")||message.ext().get("VoiceOrVideoImage").equals("ease_chat_video_call_receive")){
                        message.setAttribute(message.ext().get("VoiceOrVideoImage").
                                equals("ease_chat_voice_call_receive")?Constant.MESSAGE_ATTR_IS_VOICE_CALL:Constant.MESSAGE_ATTR_IS_VIDEO_CALL, true);
                        return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                    }
                }
               /* if(message.ext().get("bussines_id")!=null){
                    message.setAttribute(Constant.MESSAGE_ATTR_IS_BUSSINES, true);
                    message.setAttribute(Constant.BUSSINES_NUMBER,message.ext().get(Constant.BUSSINES_NUMBER).toString());
                    return new ChatRowBussines(getActivity(), message, position, adapter);
                }*/
                //red packet code : 红包消息、红包回执消息以及转账消息的chat row
                else if (RedPacketUtil.isRandomRedPacket(message)) {//小额随机红包
                    return new ChatRowRandomPacket(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {//红包消息
                    return new ChatRowRedPacket(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false))
                {//红包回执消息
                    return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
                }
            }
            return null;
        }
    }
    protected void showDia(Activity context, String content, final int state) {
        View view = context.getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.pup2, null);
        final Dialog dialog = new Dialog(context, com.hyphenate.easeui.R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView ti = (TextView) view.findViewById(com.hyphenate.easeui.R.id.ti);
        ti.setText(content);
        TextView ok = (TextView) view.findViewById(com.hyphenate.easeui.R.id.ok);
        TextView cancle = (TextView) view.findViewById(com.hyphenate.easeui.R.id.cancle);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                if(state==0){
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                }else {
                    for(String mes:EaseConstant.list_ms){
                        conversation.removeMessage(mes);
                    }
                    onBackPressed();
                }
                messageList.refresh();

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    protected void che(Activity context,String content) {
        View view = context.getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.pup2, null);
        final Dialog dialog = new Dialog(context, com.hyphenate.easeui.R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView ti = (TextView) view.findViewById(com.hyphenate.easeui.R.id.ti);
        ti.setText(content);
        TextView ok = (TextView) view.findViewById(com.hyphenate.easeui.R.id.ok);
        TextView cancle = (TextView) view.findViewById(com.hyphenate.easeui.R.id.cancle);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMMessage msgNotification = EMMessage.createTxtSendMessage(" ",contextMenuMessage.getTo());
                            EMTextMessageBody txtBody = new EMTextMessageBody(getResources().getString(R.string.msg_recall_by_self));
                            msgNotification.addBody(txtBody);
                            msgNotification.setMsgTime(contextMenuMessage.getMsgTime());
                            msgNotification.setLocalTime(contextMenuMessage.getMsgTime());
                            msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                            msgNotification.setStatus(EMMessage.Status.SUCCESS);
                            EMClient.getInstance().chatManager().recallMessage(contextMenuMessage);
                            EMClient.getInstance().chatManager().saveMessage(msgNotification);
                            messageList.refresh();
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                // Delete group-ack data according to this message.
                EaseDingMessageHelper.get().delete(contextMenuMessage);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //调用该方法可防止红包SDK引起的内存泄漏
        RPRedPacketUtil.getInstance().detachView();
    }
}
