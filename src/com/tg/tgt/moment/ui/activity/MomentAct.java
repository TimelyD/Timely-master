package com.tg.tgt.moment.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.PhoneUtil;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.easeui.utils.rxbus2.ThreadMode;
import com.hyphenate.easeui.widget.CircleImageView;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CommentConfig;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.contract.MomentContract;
import com.tg.tgt.moment.presenter.MomentPresenter;
import com.tg.tgt.moment.ui.CircleRecycleViewDivider;
import com.tg.tgt.moment.ui.adapter.MomentAdapter;
import com.tg.tgt.moment.widgets.CommentInputMenu;
import com.tg.tgt.moment.widgets.CommentListView;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.ui.MainActivity;
import com.tg.tgt.ui.MsgActivity;
import com.tg.tgt.ui.NewDynamicAct;
import com.tg.tgt.utils.StatusBarUtil;
import com.tg.tgt.utils.TakePhotoUtils;
import com.tg.tgt.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class MomentAct extends BaseActivity implements MomentContract.View, View.OnClickListener {
    public static final String TAG = MomentAct.class.getSimpleName();
    private MomentPresenter mPresenter;
    private com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView recyclerView;
    private SmartRefreshLayout mSwipeRefreshLayout;
//    private android.widget.EditText editText;
//    private android.widget.ImageView sendIv;
//    private android.widget.LinearLayout edittextbody;
    private LinearLayoutManager layoutManager;
    private MomentAdapter mAdapter;
    private ArrayList<CircleItem> mData;
    private boolean mIsHomePage;
    private CommentInputMenu mCommentInputMenu;
    private EaseUser mUserInfo;
    private View mTitleBar;
    private View mTitleBarDivider;
    private TextView mTitle;
    private ImageView mLeftImage;
    private ImageView mRightImage;
    private ProgressBar mProgressBar;
    private ImageView mParallax;
    private int mCurrentItem = -1;
   // private String signature;

    public static int mineSize;

    public static Handler mCollectHandler;

    public static String isFromId;

    private RelativeLayout newRelative;
    private CircleImageView friendsHead;
    private TextView msgNumberText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_moment);

        String userId = getIntent().getStringExtra(Constant.USER_ID);
        //signature=getIntent().getStringExtra("signature");
        mPresenter = new MomentPresenter(this, userId);
        Log.i("user_id",userId+"q");
        mIsHomePage = getIntent().getBooleanExtra(Constant.IS_MINE_HOME_PAGE, false);
        if(TextUtils.isEmpty(userId) || App.getMyUid().equals(userId)){
            mUserInfo = EaseUserUtils.getUserInfo(SpUtils.get(mContext, Constant.USERNAME, ""));
           // mUserInfo.setChatidstate(SpUtils.get(mContext, Constant.STATE,"").equals("")?this.getString(R.string.nox):SpUtils.get(mContext, Constant.STATE,""));
        }else {
            mUserInfo = EaseUserUtils.getUserInfo(getIntent().getStringExtra(Constant.USERNAME));
        }
        initView();
        initEvent();
        mCollectHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1001:
                        mPresenter.loadData(false);
                        if (mIsHomePage)
                            Constant.isMineHome = true;
                        else
                            Constant.isMineHome = false;
//                        }
//                        Log.e("Tag2222222",">>>>>>>>1002");
                        break;
                    case 1002:
                        mPresenter.loadData(false);
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCommentInputMenu.onRegister();
        if (!mIsHomePage){
            EaseConstant.isFriendsView = true;
            if (Constant.isMineHome){
                mPresenter.loadData(false);
                Constant.isMineHome = false;
            }
        }
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCommentInputMenu.onUnRegister();
    }

    @Override
    protected void initBar() {
        initBar(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mCommentInputMenu.getVisibility() == View.VISIBLE)
            updateEditTextBodyVisible(View.GONE, null);
    }

    private int maxOffest;
    private void setRefreshing(boolean refreshing){
        mProgressBar.setVisibility(refreshing?View.VISIBLE:View.GONE);
    }
    private boolean isRefreshing(){
        return mProgressBar.getVisibility() == View.VISIBLE;
    }
    private void initEvent() {
        Observable<Object> observable = RxView.clicks(mTitleBar).share();
        observable.buffer(observable.debounce(200, TimeUnit.MILLISECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addRxDestroy(disposable);
                    }
                })
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(@NonNull List<Object> objects) throws Exception {
//                        ToastUtils.showToast(getApplicationContext(), "" + objects.size());
                        if (objects.size() >= 2) {
                            if (0 == layoutManager.findFirstVisibleItemPosition()) {
                                //刷新
                                if(!isRefreshing()) {
                                    setRefreshing(true);
                                    mPresenter.loadData(false);
                                }
                            } else {
                                toTop();
                            }
                        }
                    }
                });
/*        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    String content = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(mContext, "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPresenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });*/

        mCommentInputMenu.setListener(new CommentInputMenu.CommentInputMenuListener() {
            @Override
            public void onSendMessage(String content) {
                if (mPresenter != null) {
                    content = content.trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(mContext, R.string.comment_cannot_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (content.length()>=1000) {
                        Toast.makeText(mContext, R.string.comment_size_more, Toast.LENGTH_LONG).show();
                        return;
                    }
                    mPresenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {

            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentInputMenu.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if(dy < 50){
                    Glide.with(mActivity).resumeRequests();
                } else {
                    Glide.with(mActivity).pauseRequests();
                }*/
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(mActivity).resumeRequests();
                    } else {
                        Glide.with(mActivity).pauseRequests();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mSwipeRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int
                    extendHeight) {
                mParallax.setTranslationY(offset/2);
                maxOffest = offset;
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int footerHeight, int
                    extendHeight) {
                mParallax.setTranslationY(offset/2);
                if (maxOffest > PhoneUtil.dp2px(mContext, 60)) {
                    if(isRefreshing())
                        return;
                    maxOffest = 0;
                    setRefreshing(true);
                    mPresenter.loadData(false);
                }
            }
        });
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPresenter.loadData(false);
//            }
//        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadData(true);
            }
        }, recyclerView);
//        mAdapter.setPreLoadNumber(2);
        setViewTreeObserver();
        ActivityCompat.setExitSharedElementCallback(mActivity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                if(mCurrentItem >=0 && mAdapter!=null && mAdapter.mStartView!=null){
                    names.clear();
                    sharedElements.clear();
//                    for (int i = 0; i < mAdapter.mStartView.mViews.size(); i++) {
//                        names.add(i+"");
//                        sharedElements.put(i+"", mAdapter.mStartView.mViews.get(i));
//                    }
                    names.add(mCurrentItem+"");
                    sharedElements.put(mCurrentItem+"", mAdapter.mStartView.mViews.get(mCurrentItem));
                    mCurrentItem = -1;
                }
            }
        });
    }

    @Override
    protected void initBar(boolean enableKeyBoard) {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
                .transparentStatusBar()
//                .statusBarDarkFont(true,.5f)
                .keyboardEnable(true)
                .init();
    }

    private void toTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    private void toBack() {
//        if (mIsHomePage) {
//            Log.e("Tag---","开始返回回调");
//            if (isDeleteInHome) {
//                isDeleteInHome = false;
//                Log.e("Tag---","返回true");
//                setResult(DELETECIRCLESUCCESS);
//            }else {
//                Log.e("Tag---","返回false");
//                setResult(DELETECIRCLEFAIL);
//            }
//        }
//        startActivity(new Intent(mActivity, MainActivity.class));
        finish();
//        moveTaskToBack(true);
    }

        private int headHeight;
    private int dividerColor;
    private void initView() {
        headHeight = getResources().getDimensionPixelSize(R.dimen.head_moment_layout_height);
        headHeight = headHeight + getResources().getDimensionPixelSize(R.dimen.common_76dp);
        dividerColor = ContextCompat.getColor(mContext, R.color.divider_t);
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBarDivider = findViewById(R.id.title_bar_divider);
        mTitle = (TextView) findViewById(R.id.title);
        mLeftImage = (ImageView) findViewById(R.id.left_image);
        mRightImage = (ImageView) findViewById(R.id.right_image);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mParallax = (ImageView) findViewById(R.id.parallax);
        findViewById(R.id.left_layout).setOnClickListener(this);
        View rightLayout = findViewById(R.id.right_layout);
        rightLayout.setOnClickListener(this);
//        this.edittextbody = (LinearLayout) findViewById(R.id.editTextBodyLl);
//        this.sendIv = (ImageView) findViewById(sendIv);
//        this.editText = (EditText) findViewById(R.id.circleEt);
        this.mCommentInputMenu = (CommentInputMenu) findViewById(R.id.conmment_input_menu);
        this.recyclerView = (ObservableRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                if(scrollY<0){
                    return;
                }
                float alpha = Math.min(1, (float) scrollY / headHeight);
      //          Log.e("Tag","高度=="+alpha);
                if(alpha>.4f){
                    StatusBarUtil.darkMode(mActivity);
                    mTitle.setTextColor(Color.BLACK);
                    mLeftImage.setImageResource(R.drawable.back_black);
                    mRightImage.setImageResource(R.drawable.add_moment);
                    mTitleBar.setBackgroundColor(Color.WHITE);
                    mTitleBarDivider.setBackgroundColor(dividerColor);
                }else {
                    StatusBarUtil.darkMode(mActivity,false);
                    mTitle.setTextColor(Color.WHITE);
                    mLeftImage.setImageResource(R.drawable.back_white);
                    mRightImage.setImageResource(R.drawable.add_moment_white);
                    mTitleBar.setBackgroundColor(Color.TRANSPARENT);
                    mTitleBarDivider.setBackgroundColor(Color.TRANSPARENT);
                }
                if (alpha>0.95f){
                    mParallax.setVisibility(View.INVISIBLE);
                }else {
                    mParallax.setVisibility(View.VISIBLE);
                }
//                if (alpha>=0.1f){
//                  //  newRelative.setBackgroundColor(getResources().getColor(R.color.transparent));
//                }else {
//                    //newRelative.setBackgroundColor(getResources().getColor(R.color.white));
//                }
//                if (alpha>0.2f){
//                    if (newRelative.getVisibility() != View.GONE){
//                        newRelative.setBackgroundColor(getResources().getColor(R.color.transparent));
//                    }
//                }else {
//                    if (newRelative.getVisibility() != View.GONE){
//                        newRelative.setBackgroundColor(getResources().getColor(R.color.white));
//                    }
//                }
//                mTitleBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, Color.WHITE));
//                mTitleBarDivider.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, dividerColor));
                L.d("onScrollChanged","alpha:"+alpha+"\tscrollY:"+scrollY+"\tfirstScroll:"+firstScroll+"\tdragging:"+dragging);
                mParallax.setTranslationY(-scrollY/2);
            }

            @Override
            public void onDownMotionEvent() {
                L.d("onScrollChanged","onDownMotionEvent");
            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                L.d("onScrollChanged","scrollState:"+scrollState);
            }
        });
        this.mSwipeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        int start = getStatusBarHeight() + getResources().getDimensionPixelSize(R.dimen.title_bar_height);
//        mSwipeRefreshLayout.setProgressViewOffset(false, start+getResources().getDimensionPixelSize(R.dimen.common_10dp), getResources().getDimensionPixelSize(R.dimen.common_130dp));
        mData = new ArrayList<>();
            View header;
        header = LayoutInflater.from(mContext).inflate(R.layout.head_circle, null);
        if(mIsHomePage){
            mAdapter = new MomentAdapter(mData,R.layout.adapter_circle_item_mine);
            rightLayout.setVisibility(View.INVISIBLE);
            rightLayout.setClickable(false);
            rightLayout.setEnabled(false);
         //   recyclerView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_t));
         //   mAdapter.getFooterLayout().setBackgroundColor(mContext.getResources().getColor(R.color.bg_t));
            mTitle.setText(R.string.home_page);
            //recyclerView.setBackgroundColor(Color.rgb(128,128,128));
          //  mAdapter.getFooterLayout()
          //  mSwipeRefreshLayout.get
//            header = LayoutInflater.from(mContext).inflate(R.layout.head_circle_mine, null);
        }else {
         //   recyclerView.addItemDecoration(new SimpleDividerDecoration(mContext, true));
            recyclerView.addItemDecoration(new CircleRecycleViewDivider(mContext,LinearLayoutManager.VERTICAL,
                    R.drawable.divider,0));
            mAdapter = new MomentAdapter(mData,R.layout.adapter_circle_item);
            header.findViewById(R.id.iv_avatar).setOnClickListener(this);
        }
        mAdapter.addHeaderView(header);
        TextView stateTv = (TextView) header.findViewById(R.id.tv_state);
//        newRelative = (RelativeLayout) header.findViewById(R.id.new_relative_show) ;
//        if (!TextUtils.isEmpty(mUserInfo.getChatidstate()) && mUserInfo.getChatidstate().length()>28) {
//            stateTv.setTextSize(13);
//            stateTv.setText(TextUtils.isEmpty(mUserInfo.getChatidstate())?this.getString(R.string.nox):mUserInfo.getChatidstate());
//        }else
        stateTv.setText(TextUtils.isEmpty(mUserInfo.getChatidstate())?this.getString(R.string.nox):mUserInfo.getChatidstate());
        TextView nameTv = (TextView) header.findViewById(R.id.tv_name);
        newRelative = (RelativeLayout) header.findViewById(R.id.new_relative_show);
        friendsHead = (CircleImageView) header.findViewById(R.id.last_user);
        msgNumberText = (TextView) header.findViewById(R.id.new_numbers);
        newRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EaseConstant.friendsUnread = 0;
                DBManager.getInstance().saveUnreadMotionActionCount(0);
                MainActivity.Handler.sendEmptyMessage(0);
                newRelative.setVisibility(View.GONE);
                Intent intent=new Intent(MomentAct.this, MsgActivity.class);
                startActivity(intent);
            }
        });
        if (EaseConstant.friendsUnread > 0){
            if (!mIsHomePage) {
                newRelative.setVisibility(View.VISIBLE);
                msgNumberText.setText(EaseConstant.friendsUnread+"新消息");
                Glide.with(MomentAct.this)
                        .load((String)SpUtils.get(MomentAct.this,"lastHeadImage",""))
                        .into(friendsHead);
                //ImageUtils.show(MomentAct.this,(String)SpUtils.get(MomentAct.this,"lastHeadImage",""),(ImageView)friendsHead);
            }
        }else {
            newRelative.setVisibility(View.GONE);
        }
        nameTv.setText(mUserInfo.safeGetRemark());
        ImageView avatarIv = (ImageView) header.findViewById(R.id.iv_avatar);
 //       GlideApp.with(mActivity).load(mUserInfo.getAvatar()).placeholder(R.drawable.default_avatar).into(avatarIv);
        ImageUtils.show(MomentAct.this, mUserInfo.getAvatar(), R.drawable.photo1, avatarIv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter.setPresenter(mPresenter);
        recyclerView.setAdapter(mAdapter);

//        View view = new View(mActivity);
//        view.setBackgroundColor(Color.WHITE);
        View view = getLayoutInflater().inflate(R.layout.empty_moment, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhoneUtil.getScreenHeight(mActivity)-getResources().getDimensionPixelSize(R.dimen.head_moment_layout_height)));
        mAdapter.setEmptyView(view);
        mAdapter.setHeaderAndEmpty(true);
        mSwipeRefreshLayout.measure(0, 0);
        setRefreshing(true);
        mPresenter.loadData(false);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                startActivityForResult(new Intent(mActivity, MomentDetailAct.class).putExtra(Constant.CIRCLE_ITEM, (Serializable) mAdapter.getItem(position)), REQ_DETAIL);
                MomentDetailAct.show(mActivity, mAdapter.getItem(position), position,REQ_DETAIL);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
//                    case R.id.like_relative:
//                        mPresenter.addFavort(position);
//                        break;
                    case R.id.comment_out_relative:
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = position;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        config.id = mData.get(position).getId();
                        config.hint = "";
                        updateEditTextBodyVisible(View.VISIBLE, config);
                        break;
                    case R.id.friends_pull:
                        showDialog(view,mData.get(position).getUserId(),mData.get(position).getId());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showDialog(View view, String id, final String itemId){
        // 用于PopupWindow的View
        View contentView=LayoutInflater.from(MomentAct.this).inflate(R.layout.pop_delete_friends, null, false);
        RelativeLayout relativeLayoutDelete = (RelativeLayout) contentView.findViewById(R.id.delete_relative) ;
        RelativeLayout relativeLayoutReport = (RelativeLayout) contentView.findViewById(R.id.feedback_relative);
        if (Constant.myUserIdZww.equals(id)){
            relativeLayoutDelete.setVisibility(View.VISIBLE);
        }
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
        final PopupWindow window=new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置PopupWindow的背景
      //  window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        window.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        window.setTouchable(true);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        window.showAsDropDown(view);
        backgroundAlpha(0.5f);
        window.setOnDismissListener(new poponDismissListener());
        relativeLayoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.delete_comment_title)
                        .setMessage(getString(R.string.delete_moment_promit_message))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.delete_moment_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //update(mActivity, model.getUrl(), model.getVersion());
                                mPresenter.deleteCircle(itemId);
                                window.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.delete_moment_cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        relativeLayoutReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TagTag","点击了举报");
                window.dismiss();
            }
        });
        // 或者也可以调用此方法显示PopupWindow，其中：
        // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
        // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
        // window.showAtLocation(parent, gravity, x, y);
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    public static final int REQ_DETAIL = 1231;
    public static final int REQ_NEW = 1232;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETECIRCLE){
            if (resultCode == DELETECIRCLESUCCESS){
                mPresenter.loadData(false);
            }
        }
        if(requestCode == REQ_DETAIL){
            int intExtra = data.getIntExtra(Constant.CIRCLE_ITEM_POS, -1);
            if(intExtra != -1) {
                CircleItem item = (CircleItem) data.getSerializableExtra(Constant.CIRCLE_ITEM);
                mData.remove(intExtra);
                mData.add(intExtra, item);
                mAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQ_NEW){
            if(resultCode == RESULT_OK) {
                layoutManager.scrollToPosition(0);
                mPresenter.loadData(false);
            }
        }else {
            if (resultCode == DELETECIRCLESUCCESS){
                mPresenter.loadData(false);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mCommentInputMenu != null && mCommentInputMenu.getVisibility() == View.VISIBLE) {
                //edittextbody.setVisibility(View.GONE);
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
            toBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.recycle();
        if (EaseConstant.isFriendsView)
            EaseConstant.isFriendsView = false;
        super.onDestroy();
    }

    @Subscribe(code = BusCode.FRIENDVIEW_ACTION, threadMode = ThreadMode.MAIN)
    public void onMomentAction(){
        EaseConstant.friendsUnread = DBManager.getInstance().getUnreadMotionActionCount();
        Glide.with(MomentAct.this)
                .load((String)SpUtils.get(MomentAct.this,"lastHeadImage",""))
                .into(friendsHead);
        newRelative.setVisibility(View.VISIBLE);
        msgNumberText.setText(EaseConstant.friendsUnread+"新消息");
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        Message msg = new Message();
        MomentAct.mCollectHandler.sendEmptyMessage(1001);
    }

    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {
        if (addItem != null) {
            CircleItem item = mAdapter.getItem(circlePosition);
            item.setLike(true);
            List<FavortItem> likeModels = item.getLikeModels();
            if(likeModels == null) {
                likeModels = new ArrayList<>();
                item.setLikeModels(likeModels);
            }
            likeModels.add(addItem);
//            mAdapter.notifyItemChanged(circlePosition+mAdapter.getHeaderLayoutCount());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void update2DeleteFavort(int circlePosition) {
        CircleItem item = (CircleItem) mAdapter.getItem(circlePosition);
        List<FavortItem> items = item.getLikeModels();
            String myUid = App.getMyUid();
        for (int i = 0; i < items.size(); i++) {
            if (myUid.equals(String.valueOf(items.get(i).getUserId()))) {
                item.setLike(false);
                items.remove(i);
//                mAdapter.notifyItemChanged(circlePosition+mAdapter.getHeaderLayoutCount());
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {
        if (addItem != null) {
            CircleItem item = (CircleItem) mAdapter.getItem(circlePosition);
            List<CommentItem> commentModels = item.getCommentModels();
            if(commentModels == null) {
                commentModels = new ArrayList<>();
                item.setCommentModels(commentModels);
            }
            commentModels.add(addItem);

            mAdapter.notifyDataSetChanged();
        }else {
            if(mAdapter.getItem(circlePosition).getItemType() == CircleItem.TYPE_AD){
                ToastUtils.showToast(getApplicationContext(), R.string.comment_checking);
            }
        }
        //清空评论文本
//        editText.setText("");
    }

    @Override
    public void update2DeleteMoment(CircleItem circleItem) {

    }

    @Override
    public void update2DeleteComment(int circlePosition, List<CommentItem> commentId) {
        CircleItem item = (CircleItem) mAdapter.getItem(circlePosition);
        List<CommentItem> items = item.getCommentModels();
        //本来是删除一条评论只删除本身，现在改成删除所有与该评论相关的
        /*for(int i=0; i<items.size(); i++){
            if(commentId.equals(items.get(i).getId())){
                items.remove(i);
                mAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }*/
        Toast.makeText(mContext, getString(R.string.delete_comment_fail),Toast.LENGTH_LONG).show();
        items.clear();
        if(null != commentId)
        items.addAll(commentId);
        mAdapter.notifyDataSetChanged();
    }

    private CommentConfig commentConfig;

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        if(visibility == View.VISIBLE && mCommentInputMenu.getVisibility() == View.GONE){
            if(commentConfig!=null && !TextUtils.isEmpty(commentConfig.hint)){
                mCommentInputMenu.setHint(commentConfig.hint);
            }else {
                mCommentInputMenu.setHint("");
            }
        }
        mCommentInputMenu.setVisibility(View.VISIBLE == visibility&&mCommentInputMenu.getVisibility()==View.VISIBLE?View.GONE:visibility);

        measureCircleItemHighAndCommentItemOffset(commentConfig);

        /*if (View.VISIBLE == visibility) {
            editText.requestFocus();
            //弹出键盘
            KeybordUtils.showSoftInput(editText.getContext(), editText);

        } else if (View.GONE == visibility) {
            //隐藏键盘
            KeybordUtils.hideSoftInput(editText.getContext(), editText);
        }*/
        mCommentInputMenu.updateKeyBoard(visibility);
    }

    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + MomentAdapter.HEADVIEW_SIZE -
                firstPosition);

        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    private RelativeLayout bodyLayout;
    private int statusBarH;
    private void setViewTreeObserver() {
        bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            statusBarH = getStatusBarHeight();
        }

        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
//                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
//                if (r.top != statusBarH) {
//                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
//                    r.top = statusBarH;
//                }
                r.top = statusBarH;
                int keyboardH = screenH - (r.bottom - r.top);
                Log.d(TAG, "screenH＝ " + screenH + " &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top="
                        + r.top + " &statusBarH=" + statusBarH);

                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                currentKeyboardH = keyboardH;

                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = mCommentInputMenu.getHeight();

                if (keyboardH < 150 && !mCommentInputMenu.isEmojiShow()) {//说明是隐藏键盘的情况并且表情没有被展示
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if (layoutManager != null && commentConfig != null) {
                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + MomentAdapter
                            .HEADVIEW_SIZE, getListviewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 测量偏移量
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - mCommentInputMenu.getHeight1()/* - mTitleBar
                .getHeight()*/;
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        Log.i(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void setData(boolean isSuccess, boolean loadMore, boolean hasMore, List<CircleItem> datas) {
        if(!isSuccess){
            if(loadMore)
                mAdapter.loadMoreFail();
            else
                setRefreshing(false);
            return;
        }
        if (!loadMore) {
            setRefreshing(false);
        }
        if(null == datas || datas.size() == 0){
            mAdapter.loadMoreEnd();
            return;
        }

        mData.clear();
        mData.addAll(datas);
        if(!hasMore)
            mAdapter.loadMoreEnd();
        else
            mAdapter.loadMoreComplete();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_layout:
                toBack();
                break;
            case R.id.right_layout:
//                startActivityForResult(new Intent(mActivity, NewDynamicAct.class), REQ_NEW);
                TakePhotoUtils.showDialog(mActivity, new TakePhotoUtils.CallBack() {
                    @Override
                    public void onActivityResult(String path) {
//                        Toast.makeText(mActivity, path, Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(mActivity, NewDynamicAct.class).putExtra("path", path), REQ_NEW);
                    }
                });
                break;
            case R.id.iv_avatar:
                Log.e("Tag---","设置回调方法");
                startActivity(new Intent(mActivity, MomentAct.class).putExtra(Constant.USERNAME, EMClient.getInstance().getCurrentUser())
                        .putExtra(Constant.USER_ID, App.getMyUid()).putExtra(Constant.IS_MINE_HOME_PAGE, true));
                break;

            default:
                break;
        }
    }

    private final int DELETECIRCLE = 666;
    private final int DELETECIRCLESUCCESS = 668;
    private final int DELETECIRCLEFAIL = 688;
    private boolean isDeleteInHome = false;

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mCurrentItem = data.getExtras().getInt("currentItem");
    }

    @Override
    public void setDelete(boolean isSuccess, String toast) {
        if (isSuccess){
            Toast.makeText(MomentAct.this, R.string.delete_moment_successful,Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MomentAct.this,toast,Toast.LENGTH_LONG).show();
        }
    }
}
