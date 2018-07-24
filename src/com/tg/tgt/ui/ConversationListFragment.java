package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketsdk.constant.RPConstant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.GroupInterface;
import com.hyphenate.easeui.GroupInterfaceUtil;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.util.NetUtils;
import com.tg.tgt.App;
import com.tg.tgt.BuildConfig;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.IView;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.NewsModel;
import com.tg.tgt.moment.bean.CollectBean;
import com.tg.tgt.moment.ui.activity.MomentAct;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import okhttp3.MultipartBody;

public class ConversationListFragment extends EaseConversationListFragment implements GroupInterface{

    private TextView errorText;
    private TextView mTvNewsTitle;
    private TextView mTvNewsText;
    private TextView mTvNewsTime;

    private TextView mTvNewDot;
    private NewsModel mNewsModel;

    @Override
    public void refresh() {
        super.refresh();
        conversationListView.refresh();
    }

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), com.tg.tgt.R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(com.tg.tgt.R.id.tv_connect_errormsg);
        GroupInterfaceUtil.setInstance(this);
        View newsView = LayoutInflater.from(mContext).inflate(R.layout.ease_row_chat_history, null);
        newsView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.common_65dp)));
        conversationListView.addHeaderView(newsView);
        View a = newsView.findViewById(R.id.list_itease_layout);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsListAct.class);
                startActivity(intent);
                if(mNewsModel != null){
                    SharedPreStorageMgr.getIntance().saveStringValue(getContext(), Constant.NEWS_RECODE, App.getMyUid()+mNewsModel.getId());
                    mTvNewDot.setVisibility(View.GONE);
                }
            }
        });
        ImageView avatar = (ImageView) newsView.findViewById(R.id.avatar);
        avatar.setImageResource(R.drawable.news);
        mTvNewsTitle = (TextView) newsView.findViewById(R.id.name);
        mTvNewsText = (TextView) newsView.findViewById(R.id.message);
        mTvNewsTime = (TextView) newsView.findViewById(R.id.time);
        mTvNewDot = (TextView) newsView.findViewById(R.id.unread_msg_number);
        /*ApiManger.getApiService()
                .getDataNews(1)
                .compose(RxUtils.<NewsResult>applySchedulers())
                .retry(3)
                .subscribe(new BaseObserver<NewsResult>((IView) getActivity(),false) {

                    @Override
                    public void onNext(NewsResult result) {
                        super.onNext(result);
                        ConversationListFragment.this.mNewsResult = result;
                        NewsResult.MoreBean moreBean = result.getMore().get(0);
                        mTvNewsTitle.setText(moreBean.getTitle());
                        mTvNewsText.setText(moreBean.getDescripe());
                        mTvNewsTime.setText(moreBean.getAddtime());

                        //该状态用来判断小红点是否显示
                        if(moreBean.getAddtime().equals(SharedPreStorageMgr.getIntance().getStringValue(getContext(), Constant.NEWS_RECODE))){
                            mTvNewDot.setVisibility(View.GONE);
                        }else {
                            mTvNewDot.setVisibility(View.VISIBLE);
                        }
                    }

                    //接口没状态码，自己处理
                    @Override
                    protected void onSuccess(NewsResult newsResult) {

                    }
                });*/
        loadNews();
        mCollectEHandler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        Log.e("Tag","用户id=="+MomentAct.isFromId+"图片地址"+msg.obj.toString());
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM);//表单类型
                        //    File file=new File(pathFile);
//        if (type == 2) {
//            builder.addFormDataPart("image", file.getName(), );
//        }
                        builder.addFormDataPart("filePath",msg.obj.toString());
                        builder.addFormDataPart("fromUid", MomentAct.isFromId);
                        builder.addFormDataPart("isFrom","2");
                        builder.addFormDataPart("type",String.valueOf(1));
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
                        break;
                }
            }
        };
    }

    private void loadNews() {
        ApiManger2.getApiService()
                .showNews(null,1)
                .compose(((BaseActivity)mContext).<HttpResult<List<NewsModel>>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<List<NewsModel>>() {
                    @Override
                    protected void onSuccess(List<NewsModel> newsModels) {
                        if(newsModels!=null && newsModels.size()>0){
                            mNewsModel = newsModels.get(0);
                            mTvNewsTitle.setText(mNewsModel.getTitle());
                            mTvNewsText.setText(mNewsModel.getBrief());
                            //mTvNewsTime.setText(mNewsModel.getCreateTime());
                            //该状态用来判断小红点是否显示
                            if((App.getMyUid()+mNewsModel.getId()).equals(SharedPreStorageMgr.getIntance().getStringValue(getContext(), Constant.NEWS_RECODE))){
                                mTvNewDot.setVisibility(View.GONE);
                            }else {
                                /*mTvNewDot.setVisibility(View.VISIBLE);
                                mTvNewDot.setText("N");*/
                            }
                        }
                    }
                });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setLeftImageResource(R.drawable.menu);
        titleBar.setLeftLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).toggleMenu();
            }
        });
        titleBar.setRightImageResource(R.drawable.more);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //((MainActivity)getActivity()).showMenu(titleBar);
                if(MainActivity.pup.getVisibility()==View.GONE){
                    MainActivity.tan();
                }else {
                    MainActivity.shou();
                }
            }
        });
        /*titleBar.setSecondRightImageResource(R.drawable.search);
        titleBar.setSecondRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).search();
            }
        });*/
        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //增加了一个头布局
                position = position - 1;
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), com.tg.tgt.R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    View lock = view.findViewById(com.hyphenate.easeui.R.id.iv_msg_lock);
                    if(!(boolean)lock.getTag(R.id.msg_lock_tag)){
                        //没有初始化，需要判断
//                        checkIsCode(conversation, username);
                        if(BuildConfig.DEBUG)
                        ToastUtils.showToast(mContext, "未初始化");
                    }else if(lock.getVisibility() == View.INVISIBLE){
                        //没有加密
                        toChatAct(conversation, username, (EaseUser) lock.getTag(R.id.msg_result_tag));
                    }else {
                        //加密
                        showSecurity(conversation, username, (EaseUser) lock.getTag(R.id.msg_result_tag));
                    }
                }
            }
        });
        //red packet code : 红包回执消息在会话列表最后一条消息的展示
        conversationListView.setConversationListHelper(new EaseConversationList.EaseConversationListHelper() {
            @Override
            public String onSetItemSecondaryText(EMMessage lastMessage) {
                if (lastMessage.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
                    String sendNick = lastMessage.getStringAttribute(RPConstant.EXTRA_RED_PACKET_SENDER_NAME, "");
                    String receiveNick = lastMessage.getStringAttribute(RPConstant.EXTRA_RED_PACKET_RECEIVER_NAME, "");
                    String msg;
                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                        msg = String.format(getResources().getString(com.tg.tgt.R.string.msg_someone_take_red_packet),
                                receiveNick);
                    } else {
                        if (sendNick.equals(receiveNick)) {
                            msg = getResources().getString(com.tg.tgt.R.string.msg_take_red_packet);
                        } else {
                            msg = String.format(getResources().getString(com.tg.tgt.R.string.msg_take_someone_red_packet),
                                    sendNick);
                        }
                    }
                    return msg;
                }
                return null;
            }

            @Override
            public void onSetIsMsgClock(final String username, final ImageView msgClock, ImageView avatar, TextView name) {
//                Logger.d("username:"+username);
                //给imageview设置lock，避免错位
                CodeUtils.setIsMsgClock((IView) getActivity(),username, msgClock, avatar, name);

            }

            @Override
            public boolean onSetGroup(String username, TextView name) {
                GroupModel group = GroupManger.getGroup(username);
                if(group!=null){
                    name.setText(group.getGroupName());
                    return true;
                }
                return false;
            }
        });
        super.setUpView();
        //end of red packet code
    }

    private void checkIsCode(final EMConversation conversation, final String username) {
        /*CodeUtils.checkIsCode((IView) getActivity(), username, new CodeUtils.CodeUtilsHelper() {
            @Override
            public void onResult(IsCodeResult result) {
                if (result.getIscode() == 1) {//加密
                    showSecurity(conversation, username, result);
                } else {
                    toChatAct(conversation, username, result);
                }
            }
        });*/
    }

//    String infocode;

    private void showSecurity(final EMConversation conversation, final String username, final EaseUser result) {

        SecurityDialog.show(getActivity(),mContext.getString(R.string.security_title),new SecurityDialog.OnSecurityListener(){
            @Override
            public void onPass() {
                toChatAct(conversation, username, result);
            }
        });

//        infocode = null;
//
//        View view = getLayoutInflater(getArguments()).inflate(R.layout.pop_security_password, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
//        final AlertDialog dialog = builder.show();
//
//        Button cancel = (Button) view.findViewById(R.id.pop_security_cancel);
//        Button confirm = (Button) view.findViewById(R.id.pop_security_confirm);
//        final KurtEditText kurtedit = (KurtEditText) view.findViewById(R.id.pop_kurtet);
//        kurtedit.setKurtListener(new KurtEditText.KurtListener() {
//
//            @Override
//            public void keyword(String str) {
//                Log.e("NIRVANA", str);
//                infocode = str;
//                //Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
//            }
//        });
//        confirm.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(infocode) || infocode.length() < 6) {
//                    Toast.makeText(App.applicationContext, "请输入正确的六位数密码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ApiManger.getApiService().isRight(App.getMyUid(), infocode)
//                        .compose(RxUtils.<BaseHttpResult>applySchedulers())
//                        .subscribe(new BaseObserver<BaseHttpResult>((IView) getActivity()) {
//                            @Override
//                            protected void onSuccess(BaseHttpResult baseHttpResult) {
//                                dialog.dismiss();
//
//                            }
//
//                            @Override
//                            public void onFaild(int code, String message) {
//                                super.onFaild(code, message);
//                                if(code == 0){
//                                    kurtedit.clearText();
//                                    Toast.makeText(App.applicationContext, "密码错误，请重试", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
    }
   /* private void getApiService(){//群聊
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
    }*/
    private void toChatAct(EMConversation conversation, final String username, EaseUser result) {
        // start chat acitivity
        final Intent intent = new Intent(getActivity(), ChatActivity.class);
        if (conversation.isGroup()) {
            if (conversation.getType() == EMConversationType.ChatRoom) {
                // it's group chat
                intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
            } else {
                GroupManger.toChat(((BaseActivity)mContext), username);
                return;
                /*intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                //如果没用群资料先拉取群资料
                if(GroupManger.getGroup(username) == null || GroupManger.getGroup(username).getGroupUserModels()==null || GroupManger.getGroup(username).getGroupUserModels().size()<1 || GroupManger.getGroup(username).getGroupUserModels().get(0).getPicture()==null){
                    Observable.fromCallable(new Callable<GroupModel>() {
                        @Override
                        public GroupModel call() throws Exception {
                            return GroupManger.fetchGroup(username);
                        }
                    }).compose(((BaseActivity)mContext).<GroupModel>bindToLifeCyclerAndApplySchedulers())
                            .subscribe(new Consumer<GroupModel>() {
                                @Override
                                public void accept(@NonNull GroupModel groupModel) throws Exception {
                                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                                    startActivity(intent);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    ToastUtils.showToast(mContext.getApplicationContext(), HttpHelper.handleException(throwable));
                                }
                            });
                    return;
                }*/
            }

        }
        // it's single chat
        intent.putExtra(Constant.EXTRA_USER_ID, username);
        startActivity(intent);
    }

    @Override
    protected void onConnectionConnected() {
        super.onConnectionConnected();
        loadNews();
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(com.tg.tgt.R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(com.tg.tgt.R.string.the_current_network);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(com.tg.tgt.R.menu.em_delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == com.tg.tgt.R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == com.tg.tgt.R.id.delete_conversation) {
            deleteMessage = false;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo())
                .position-1);
        if (tobeDeleteCons == null) {
            return true;
        }
        if (tobeDeleteCons.getType() == EMConversationType.GroupChat) {
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
        ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }

    public static List<KeyBean> fetch(String id){
        final HttpResult<List<KeyBean>> httpResult = ApiManger2.getApiService().getGroupChatKey(id).blockingFirst();
        if (HttpHelper.isHttpSuccess(httpResult)) {
            List<KeyBean> data = httpResult.getData();
            if(data==null){
                return null;
            }
            return data;
        }else {
            return null;
        }
    }
    private HashMap<String, List<KeyBean>> map = CodeUtils.toMap(EaseApp.sf.getString(EaseApp.map_group, null));
    private String ID;
    @Override
    public void GetGroupDate(String id) {
        ID=id;
        ApiManger2.getApiService()
                .getGroupChatKey(id)
                .compose(((BaseActivity)mContext).<HttpResult<List<KeyBean>>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<List<KeyBean>>() {
                    @Override
                    protected void onSuccess(List<KeyBean> list) {
                        if(map==null){
                            map=new HashMap<>();
                        }
                        map.put(ID,list);
                        String string = CodeUtils.toJson(map, 1);
                        EaseApp.sf.edit().putString(EaseApp.map_group,string).commit();
                        conversationListView.refresh();
                    }
                });
    }
}
