package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.utils.AESCodeer;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.RSAUtil;
import com.hyphenate.easeui.utils.RandomUtil;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoUtils;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.easeui.utils.videocompress.CompressListener;
import com.hyphenate.easeui.utils.videocompress.Compressor;
import com.hyphenate.easeui.utils.videocompress.InitListener;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseChatPrimaryMenu;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.zibin.luban.Luban;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId 
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 *
 */
public class EaseChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    public static final int REQUEST_CODE_PREVIEW = 4;

    /**
     * params to fragment
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUsername;
    protected LinearLayout ll_zhuan;
    protected RelativeLayout zhuan;
    protected RelativeLayout del;
    protected RelativeLayout collect;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected ChatRoomListener chatRoomListener;

    protected EMMessage contextMenuMessage;

    public static final int ITEM_TAKE_PICTURE = 1;
    public static final int ITEM_PICTURE = 2;
    public static final int ITEM_LOCATION = 3;
    public static final int ITEM_BUSINESS = 4;
    public static final int ITEM_COLLECTION = 5;

    protected int[] itemStrings = {com.hyphenate.easeui.R.string.attach_take_pic, com.hyphenate.easeui.R.string
            .attach_picture, com.hyphenate.easeui.R.string.attach_location};
    protected int[] itemdrawables = {com.hyphenate.easeui.R.drawable.ease_chat_takepic_selector, com.hyphenate.easeui
            .R.drawable.ease_chat_image_selector,
            com.hyphenate.easeui.R.drawable.ease_chat_location_selector};
    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION};
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected View mReadFireView;
    private String mVideoCachePath;

    private ImageView headImg;
    private TextView titleTxt;
    public static Boolean enu=false;
    public static Handler Handler ;
    private void handler(){
        Handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1://视频
                        chatFragmentHelper.onVideoClick(1);
                        break;
                    case 2://语音
                        chatFragmentHelper.onVideoClick(2);
                        break;
                }
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(com.hyphenate.easeui.R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * init view
     */
    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(com.hyphenate.easeui.R.id.voice_recorder);
        handler();
        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(com.hyphenate.easeui.R.id.message_list);
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(com.hyphenate.easeui.R.id.input_menu);
        ll_zhuan=(LinearLayout) getView().findViewById(R.id.ll_zhuan);
        zhuan=(RelativeLayout)getView().findViewById(R.id.zhuan);
        del=(RelativeLayout)getView().findViewById(R.id.del);
        collect=(RelativeLayout)getView().findViewById(R.id.collect);
        EaseConstant.MESSAGE_ATTR_SELECT=false;//EaseConstant.list_ms.clear();
        inputMenu.setVisibility(View.VISIBLE);ll_zhuan.setVisibility(View.GONE);titleBar.setRightLayoutVisibility(View.VISIBLE);

        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        //TODO 开放多人视频
//        ((EaseChatPrimaryMenu)inputMenu.getPrimaryMenu()).setVideoCallEnable(chatType == EaseConstant.CHATTYPE_SINGLE);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView
                        .EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onToggleReadFire() {
                if (mReadFireView.getVisibility() == View.VISIBLE) {
                    setIsFire(false);
                } else {
                    setIsFire(true);
                }
            }

            @Override
            public void onSendVoice(String voiceFilePath, int voiceTimeLength) {
                sendVoiceMessage(voiceFilePath, voiceTimeLength);
            }

            @Override
            public void onCameraClick() {
                selectPicFromCamera();
            }

            @Override
            public void onVideoClick() {
                if (null != chatFragmentHelper)
                    chatFragmentHelper.onVideoClick(1);
            }

            @Override
            public void onPhotoSend(final MediaBean[] beans) {
//                new EaseAlertDialog(getContext(), getString(R.string.prompt), "是否发送"/* + beans.length + "张图片"*/,
//                        null, new EaseAlertDialog
//                        .AlertDialogUser() {
//                    @Override
//                    public void onResult(boolean confirmed, Bundle bundle) {
//                        if (!confirmed)
//                            return;
//
//                    }
//                }, true).show();

                for (final MediaBean bean : beans) {

                    if (bean instanceof VideoBean) {
                        sendVideo((VideoBean) bean);
                    } else {
                        sendImageMessage(bean.getPath());
                    }
                }
                inputMenu.photoSelectMenu.clearSelect();
            }

            @Override
            public void onPhotoClick() {
                selectPicFromLocal();
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
//        swipeRefreshLayout.setColorSchemeResources(com.hyphenate.easeui.R.color.holo_blue_bright, com.hyphenate
// .easeui.R.color.holo_green_light,
//                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mReadFireView = getView().findViewById(com.hyphenate.easeui.R.id.read_and_fire_view);
    }

    protected void sendVideo(final VideoBean bean) {
        if (((VideoBean) bean).getSize() > 1024 * 1024 * 10) {
            if(((VideoBean) bean).getSize() > 1024 * 1024 * 200){
                ToastUtils.makeText(getActivity(), getString(R.string.not_allow_video), Toast.LENGTH_SHORT).show();
                return;
            }
//                                    String st = getResources().getString(R.string.temporary_does_not);
//                                    ToastUtils.makeText(getActivity(), st, Toast.LENGTH_SHORT).show();
//                                    continue;
            mProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string
                    .compress_progress, "0.00%"));
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    boolean cancel = mCompressor.cancel();
                    Toast.makeText(getContext().getApplicationContext(), R.string.cancel_compress,
                            Toast.LENGTH_SHORT).show();
                    if(new File(mVideoCachePath).exists())
                        new File(mVideoCachePath).delete();
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaMetadataRetriever retr = new MediaMetadataRetriever();
                    retr.setDataSource(bean.getPath());
                    Bitmap frameAtTime = retr.getFrameAtTime();
                    int w = frameAtTime.getWidth();
                    int h = frameAtTime.getHeight();
                    String time = retr.extractMetadata(MediaMetadataRetriever
                            .METADATA_KEY_DURATION);//获取视频时长
                    try {
                        videoLength = Double.parseDouble(time) / 1000.00;
                    } catch (Exception e) {
                        e.printStackTrace();
                        videoLength = 0.00;
                    }
                    int s = 1;
                    while (w > 600 || h > 600) {
                        w *= 0.5;
                        h *= 0.5;
                        s++;
                    }

//                                            mVideoCachePath = FilesUtils.getCacheDirectory(getContext(), "video")
//                                                    .getAbsolutePath() + File.separator + System.currentTimeMillis()
//                                                    + "out.mp4";
                    if(!PathUtil.getInstance().getVideoPath().exists())
                        PathUtil.getInstance().getVideoPath().mkdirs();
                    mVideoCachePath = PathUtil.getInstance().getVideoPath() + "/"
                            + System.currentTimeMillis() + "out.mp4";
//                                            final int finalS = s;
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                            ToastUtils.showToast(getContext().getApplicationContext(), "压缩倍数:"+ finalS);
//
//                                                }
//                                            });
                    String cmd = "-y -i " + bean.getPath() + " -vcodec libx264 -preset ultrafast -vf scale=iw/" + s + ":ih/"
                            + s + " -flags +loop -cmp chroma -crf 24 -bt 256k -refs 1 -coder " +
                            "0 -me_range 16 -subq 5 -partitions parti4x4+parti8x8+partp8x8 -g" +
                            " 250 -keyint_min 25 -level 30 -qmin 10 -qmax 51 -trellis 2 " +
                            "-sc_threshold 40 -i_qfactor 0.71 -acodec copy " + mVideoCachePath;
                    startCompress(cmd, PhotoUtils.getVideoThumbPath(bean.getPath()), (int) ((VideoBean)
                            bean).getLength());
                }
            }).start();
        } else {

            sendVideoMessage(bean.getPath(),  PhotoUtils.getVideoThumbPath(bean.getPath()), (int) (
                    (VideoBean)

                    bean).getLength());
        }
    }

    private Compressor mCompressor;
    private ProgressDialog mProgressDialog;

    private void startCompress(final String cmd, final String thumbPath, final int length) {
        if (mCompressor == null) {
            mCompressor = new Compressor(getActivity().getApplicationContext());
            mCompressor.loadBinary(new InitListener() {
                @Override
                public void onLoadSuccess() {
//                    Toast.makeText(getContext().getApplicationContext(), "load library succeed", Toast
// .LENGTH_SHORT).show();
                    execCommand(cmd, thumbPath, length);
                }

                @Override
                public void onLoadFail(String reason) {
                    Toast.makeText(getContext().getApplicationContext(), getString(R.string.compress_failed) + reason, Toast
                            .LENGTH_SHORT).show();
                    mCompressor = null;
                    dismissDialog();
                }
            });
        } else {
            execCommand(cmd, thumbPath, length);
        }
    }

    private void dismissDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
    /**
     * 根据命令压缩视频,然后发送
     * @param cmd
     * @param thumbPath
     * @param length
     */
    private void execCommand(final String cmd, final String thumbPath, final int length) {
        mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(final String message) {
                Log.i(TAG, "success " + message);

//                String result = getString(R.string.compress_result_input_output, currentInputVideoPath
               dismissDialog();
                sendVideoMessage(mVideoCachePath, thumbPath, length);
            }

            @Override
            public void onExecFail(final String reason) {
                Log.i(TAG, "fail " + reason);
                if(new File(mVideoCachePath).exists())
                    new File(mVideoCachePath).delete();
                Toast.makeText(getContext().getApplicationContext(), R.string.compress_failed, Toast.LENGTH_LONG).show();
                dismissDialog();
            }

            @Override
            public void onExecProgress(String message) {
                Log.i(TAG, "progress " + message);
//                textAppend(getString(R.string.compress_progress, message));
                mProgressDialog.setMessage(getString(R.string.compress_progress, getProgress(message)));

            }
        });
    }


    private double videoLength;
    DecimalFormat df;

    /**
     * 获得视频压缩进度
     * @param source
     * @return
     */
    private String getProgress(String source) {
        //progress frame=   28 fps=0.0 q=24.0 size= 107kB time=00:00:00.91 bitrate= 956.4kbits/s
        Pattern p = Pattern.compile("00:\\d{2}:\\d{2}");
        Matcher m = p.matcher(source);
        if (m.find()) {
            //00:00:00
            String result = m.group(0);
            String temp[] = result.split(":");
            Double seconds = Double.parseDouble(temp[1]) * 60 + Double.parseDouble(temp[2]);
            Log.v(TAG, "current second = " + seconds);
            if (0 != videoLength) {
                if (df == null)
                    df = new DecimalFormat("#0.00");
                return df.format(seconds / videoLength * 100) + "%";
            }
            return "0.00%";
        }
        return "0.00%";
    }

    protected void setUpView() {
//        titleBar.setTitle(toChatUsername);
        ImmersionBar mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 0.5f)
                .init();
        titleBar.setTitleColor(getResources().getColor(R.color.title_black));
        titleBar.setLeftImageResource(R.drawable.back_black);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if (EaseUserUtils.getUserInfo(toChatUsername) != null) {
                EaseUser user = EaseUserUtils.getUserInfo(toChatUsername);
                if (user != null) {
                    titleBar.setTitle(user.safeGetRemark());
                }
            }
            titleBar.setRightImageResource(R.drawable.chat_single);
            titleBar.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            titleBar.setRightImageResource(R.drawable.chat_group);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                //group chat
                EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
                if (group != null)
                    titleBar.setTitle(group.getGroupName());
                // listen the event that user moved out group or group is dismissed
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
                chatRoomListener = new ChatRoomListener();
                EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
                onChatRoomViewCreation();
            }

        }
        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//                    emptyHistory();
                    setChatPerson();
                } else {
                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        Log.i("dcz_msg_id ",forward_msg_id+"qqq");
        if (forward_msg_id != null) {
            if(forward_msg_id.equals("duo")){
                for(String mes:EaseConstant.list_ms){
                    Log.i("dcz_duo",mes+"qqq");
                    forwardMessage(mes);
                }
            }else {
                Log.i("dcz_dan",forward_msg_id+"qqq");
                forwardMessage(forward_msg_id);
            }
        }

        SelectObserable.getInstance().addObserver(inputMenu.photoSelectMenu);
    }

    protected void setChatPerson() {

    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit() {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils
                .getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    protected void onMessageListInit() {
        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    public static Handler mHandler ;
    private void initHandler(){
        //下线通知
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        EMMessage message=(EMMessage)msg.obj;
                        contextMenuMessage = message;
                        if (chatFragmentHelper != null) {
                            chatFragmentHelper.onMessageBubbleLongClick(message);
                        }
                        break;
                }
            }
        };
    }

    protected void setListItemClickListener() {
        initHandler();
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onResendClick(final EMMessage message) {
                new EaseAlertDialog(getActivity(), com.hyphenate.easeui.R.string.resend, com.hyphenate.easeui.R
                        .string.confirm_resend, null, new
                        EaseAlertDialog.AlertDialogUser() {
                            @Override
                            public void onResult(boolean confirmed, Bundle bundle) {
                                if (!confirmed) {
                                    return;
                                }
                                resendMessage(message);
                            }
                        }, true).show();
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                Log.i("dcz","长按");
                if(enu==true){
                    enu=false;
                    return;
                }
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            if(!isDetached())
                            Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string
                                            .no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.unable_to_get_loaction, Toast
                            .LENGTH_SHORT).show();
                }

            } else if(requestCode == REQUEST_CODE_PREVIEW){
                List<MediaBean> selectImages = SelectObserable.getInstance().getSelectImages();
                for (final MediaBean bean : selectImages) {
                    sendImageMessage(bean.getPath());
                }
                inputMenu.photoSelectMenu.clearSelect();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        if (chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        }

        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
        }

        SelectObserable.getInstance().deleteObserver(inputMenu.photoSelectMenu);

//        handler.removeCallbacksAndMessages(null);
    }

    public void onBackPressed() {
        hideKeyboard();
        if (inputMenu.onBackPressed()) {
            if(EaseConstant.MESSAGE_ATTR_SELECT==true){
                Log.i("dcz","取消选中");
                EaseConstant.MESSAGE_ATTR_SELECT=false;
                inputMenu.setVisibility(View.VISIBLE);ll_zhuan.setVisibility(View.GONE);
                titleBar.setRightLayoutVisibility(View.VISIBLE);
                EaseConstant.list_ms.clear();
                ((BaseAdapter) messageList.getListView().getAdapter()).notifyDataSetChanged();
                return;
            }
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
            }
            //去除所有阅后即焚消息
            messageList.removeAllFireMsg();
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                        if (room != null) {
                            titleBar.setTitle(room.getName());
                            EMLog.d(TAG, "join room success : " + room.getName());
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                        addChatRoomChangeListenr();
                        onConversationInit();
                        onMessageListInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    protected void addChatRoomChangeListenr() {
        /*
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    showChatroomToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showChatroomToast("member : " + participant + " join the room : " + roomId);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " +
                roomName);
            }

            @Override
            public void onRemovedFromChatRoom(String roomId, String roomName, String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                    	EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
                        getActivity().finish();
                    }else{
                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + "
                        room name : " + roomName);
                    }
                }
            }


            // ============================= group_reform new add api begin
            @Override
            public void onMuteListAdded(String chatRoomId, Map<String, Long> mutes) {}

            @Override
            public void onMuteListRemoved(String chatRoomId, List<String> mutes) {}

            @Override
            public void onAdminAdded(String chatRoomId, String admin) {}

            @Override
            public void onAdminRemoved(String chatRoomId, String admin) {}

            @Override
            public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {}

            // ============================= group_reform new add api end

        };
        
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
        */
    }

    protected void showChatroomToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (final EMMessage message : messages) {
            String username = null;
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }
            // if the message is for current conversation
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername)) {
              //  messageList.refreshSelectLast();
                messageList.refresh();
                //TODO 在当前界面不需要声音以及震动
                conversation.markMessageAsRead(message.getMsgId());
                //这里收到信息，如果是阅后即焚，那么在n秒后删除
//                if ("1".equals(message.getStringAttribute(EaseConstant.MESSAGE_ATTR_IS_FIRE, "0"))) {
//                    int fireTime = 10;//收到信息10秒后销毁
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            conversation.removeMessage(message.getMsgId());
//                            messageList.refresh();
//                        }
//                    }, fireTime * 1000);
//                }
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }


    /**
     * handle the click event for extend menu
     *
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * input @
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol, String nick) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        if (autoAddAtSymbol)
            inputMenu.getPrimaryMenu().getEditText().addAtSpan("@", nick, username);
        else
            inputMenu.insertText(username + " ");
    }


    /**
     * input @
     * @param username
     */
    protected void inputAtUsername(String username, String nick) {
        inputAtUsername(username, true, nick);
    }

    public static HashMap<String,List<KeyBean>> toMap(String string){
        Type type = new TypeToken<HashMap<String,List<KeyBean>>>(){}.getType();
        HashMap<String,List<KeyBean>> b = new Gson().fromJson(string,type);
        return b;
    }

    //send message
    protected void sendTextMessage(String content) {
        if (inputMenu.getPrimaryMenu().getEditText().hasAt()) {
            Log.i("dcz","发送@文本");
            sendAtMessage(content);
        } else {
            Log.i("dcz","发送文本2"+content+"+id:"+toChatUsername);
            String random = RandomUtil.generateString(16);
            String sign = AESCodeer.AESEncode(random,content);
            EMMessage message = EMMessage.createTxtSendMessage(sign, toChatUsername);       //发送的文本已经过random加密
            String pri = EaseApp.sf.getString("pri_key", "");//得到登录时生成的私钥
            String key=chatType == EaseConstant.CHATTYPE_GROUP?EaseApp.map_group:EaseApp.map_receiver;
            //String id =chatType == EaseConstant.CHATTYPE_GROUP?toChatUsername:toChatUsername;
            String z = EaseApp.sf.getString(key, null);//得到总map
            HashMap<String, List<KeyBean>> map = toMap(z);
            List<KeyBean> list = map.get(toChatUsername);
            if(list==null){
                EMMessage mes = EMMessage.createTxtSendMessage(content, toChatUsername);
                sendMessage(mes);
                return;
            }
            for(KeyBean bean:list){
                if(list.size()>1){
                    if(bean.isNewest()){
                        EaseApp.receiver_pub=bean;
                    }
                }else {
                    EaseApp.receiver_pub=bean;
                }
            }
            try {
                String aeskey = RSAUtil.decryptBase64ByPrivateKey(EaseApp.receiver_pub.getAesKey(), pri);//用我的RSA私钥对接收方的aes解密
                String pubkey = AESCodeer.AESDncode(aeskey,EaseApp.receiver_pub.getChatPubKey());       //对接收方的公钥进行解密
                String s = EaseApp.sf.getString(EaseApp.keyBean, ""); //得到登录时获取的我的最新版本聊天私钥（解密消息用）
                KeyBean bean = new Gson().fromJson(s, KeyBean.class);//我的聊天私钥的实体类
                String jmh = RSAUtil.encryptByPublicKey(random,pubkey);//用接收方的公钥对我方的random进行加密
                //发送给自己显示
                String aeskey2 = RSAUtil.decryptBase64ByPrivateKey(bean.getAesKey(), pri);   //用我的私钥对我自己的aeskey解密
                String pubkey2= AESCodeer.AESDncode(aeskey2,bean.getChatPubKey());             //用我的aeskey解密我的公钥
                String jmh2 = RSAUtil.encryptByPublicKey(random,pubkey2);//用自己的公钥对我方的random进行加密
                message.setAttribute(EaseConstant.VERSION,bean.getVersion());
                message.setAttribute(EaseConstant.MI,jmh);
                message.setAttribute(EaseConstant.SEND,jmh2);Log.i("zzzzz2","SEND"+jmh2);
                sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * send @ message, only support group chat message
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get()
                .containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, inputMenu.getPrimaryMenu().getEditText().getAtUserId());
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
      //  try {
            File file = new File(imagePath);
       //     File file = Luban.with(getActivity()).load(imagePath).setTargetDir(com.hyphenate.easeui.utils.PhotoUtils.getTempDirPath(getActivity())).get(imagePath);
            EMMessage message = EMMessage.createImageSendMessage(file.getPath(), false, toChatUsername);
            sendMessage(message);
        //} catch (IOException e) {
         //   e.printStackTrace();
       // }
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath,String name) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        ( (EMFileMessageBody)message.getBody()).setFileName(name);
        sendMessage(message);
    }


    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }


    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageList.refresh();
    }

    //===================================================================================


    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.cant_find_pictures, Toast
                        .LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.cant_find_pictures, Toast
                        .LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
            filePath = uri.getPath();
            Log.i("path1",filePath+"///");
            //return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
            filePath = getPath(getActivity(), uri);
            Log.i("path2",filePath+"///");
        } else {//4.4以下下系统调用方法
            filePath = getRealPathFromURI(uri);
            Log.i("path3",filePath+"///");
        }
        /*if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }*/
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.The_file_is_not_greater_than_10_m, Toast
                    .LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * clear the conversation history
     *
     */
    protected void emptyHistory() {
        String msg = getResources().getString(com.hyphenate.easeui.R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    messageList.refresh();
                }
            }
        }, true).show();
    }

    /**
     * open group detail
     *
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    protected List<KeyBean> toArray(String string){
        Type type = new TypeToken<List<KeyBean>>(){}.getType();
        List<KeyBean> b = new Gson().fromJson(string,type);
        return b;
    }
    protected String mi(EMMessage message){
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String text = txtBody.getMessage();
        Log.i("dcz_mi",text);
        String version = message.getStringAttribute(EaseConstant.VERSION, null);
        String mi = message.getStringAttribute(EaseConstant.MI, null);       //获得用对方的公钥RSA加密后的random
        String send_msg= message.getStringAttribute(EaseConstant.SEND, null);//获得用我的公钥RSA加密后的random
        String pri = EaseApp.sf.getString("pri_key", "");//获得私钥（可解密aesKey）
        String s = EaseApp.sf.getString(EaseApp.keyBean, ""); //得到登录时获取的我的最新版本聊天私钥（解密消息用）
        KeyBean bean = new Gson().fromJson(s, KeyBean.class);//我的聊天私钥的实体类
        String a = EaseApp.sf.getString(EaseApp.keylist, "");//得到登录时获取的我的所有版本聊天私钥
        if(mi!=null){
            try {
                if(message.getChatType()==ChatType.Chat){
                    Log.i("dcz对话","单聊");
                    if(message.direct() == EMMessage.Direct.RECEIVE){
                        List<KeyBean> list = toArray(a);
                        for(KeyBean be:list){
                            if(version.equals(be.getVersion())){//获得对方发送消息的对应版本
                                bean=be;
                                break;
                            }
                        }
                    }
                }
                if(message.getChatType()==ChatType.GroupChat){
                    Log.i("dcz对话","群聊");
                    if(message.direct() == EMMessage.Direct.RECEIVE){
                        String z = EaseApp.sf.getString(EaseApp.map_group, null);
                        Log.i("dcz对话2",z+"/");
                        HashMap<String, List<KeyBean>> map = toMap(z);
                        Log.i("dcz对话3",map.size()+"/");
                        List<KeyBean> list = map.get(message.conversationId());
                        Log.i("dcz对话3",list.size()+"/");
                        for(KeyBean be:list){
                            if(version.equals(be.getVersion()+"")){//获得对方发送消息的对应版本
                                bean=be;
                                break;
                            }
                        }
                    }
                }
                String Key=message.direct() == EMMessage.Direct.RECEIVE?mi:send_msg;
                String aeskey = RSAUtil.decryptBase64ByPrivateKey(bean.getAesKey(), pri);
                String prikey = AESCodeer.AESDncode(aeskey,bean.getChatSKey());       //对我的私钥进行解密
                String random = RSAUtil.decryptBase64ByPrivateKey(Key,prikey);
                text = AESCodeer.AESDncode(random,text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(text==null){
            text="";
        }
        Log.i("dcz_mi2",text);
        return text;
    }
    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        Log.i("dcz_type",type+"");
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String mi = forward_msg.getStringAttribute(EaseConstant.MI, null);
                    if(mi!=null){
                        String content = mi(forward_msg);
                        sendTextMessage(content);
                    }else {
                        String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                        sendTextMessage(content);
                    }

                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            case VIDEO:
                String path = (( EMVideoMessageBody) forward_msg.getBody()).getLocalUrl();
                long len = ((EMVideoMessageBody) forward_msg.getBody()).getVideoFileLength();
                if (path != null) {
                    File file = new File(path);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        Toast.makeText(getActivity(),"请先下载该视频", Toast.LENGTH_LONG).show();
                        break;
                    }
                    int dur = ((EMVideoMessageBody) forward_msg.getBody()).getDuration();
                    Log.i("dczq",((EMVideoMessageBody) forward_msg.getBody()).getLocalThumb());
                    Log.i("dcze",((EMVideoMessageBody) forward_msg.getBody()).getLocalUrl());
                    VideoBean videoBean = new VideoBean(path, null,dur);
                    videoBean.setSize(len);
                    sendVideo(videoBean);
                }
                break;
            case FILE:
                String url = ((EMFileMessageBody) forward_msg.getBody()).getLocalUrl();
                if (url != null) {
                    File file = new File(url);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        Toast.makeText(getActivity(),"请先下载文件", Toast.LENGTH_LONG).show();
                        break;
                    }
                    sendFileMessage(url,((EMFileMessageBody) forward_msg.getBody()).getFileName());
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
     *
     */
    class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    Log.i("www",toChatUsername+"++"+groupId);
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(EaseApp.applicationContext, com.hyphenate.easeui.R.string.you_are_group, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            // prompt group is dismissed and finish this activity
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(mContext, com.hyphenate.easeui.R.string.the_current_group_destroyed,
                                Toast.LENGTH_LONG).show();
                        Activity activity = (Activity) mContext;
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
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
    }

    /**
     * listen chat room event
     */
    class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.the_current_chat_room_destroyed,
                                Toast.LENGTH_LONG)
                                .show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final String roomId, final String roomName, final String participant) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.quiting_the_chat_room, Toast
                                .LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member join:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member exit:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


    }

    /**
     * 判断是否阅后即焚的标记
     */
    protected String isFire = "0";

    /**
     * 设置是否阅后即焚
     */
    private void setIsFire(boolean flag) {
        isFire = flag ? "1" : "0";
        mReadFireView.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentListener(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();

        void onVideoClick(int i);
    }

    /**
     *  以下所有是修改后的选择文件方法
     * */
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);cursor.close();
        }
        return res;
    }
    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     *  以上
     * */
}
