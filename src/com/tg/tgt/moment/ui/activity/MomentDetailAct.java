package com.tg.tgt.moment.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.utils.KeybordUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CommentConfig;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.contract.MomentContract;
import com.tg.tgt.moment.presenter.MomentPresenter;
import com.tg.tgt.moment.ui.adapter.MomentAdapter;
import com.tg.tgt.moment.widgets.CommentInputMenu;
import com.tg.tgt.moment.widgets.CommentListView;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yiyang
 */
public class MomentDetailAct extends BaseActivity implements MomentContract.View, View.OnClickListener {
    private static final String TAG = "MomentDetailAct";
    private CircleItem circleItem;
    private RecyclerView mRecyclerView;
    /** 说点什么... */
    private EditText mCircleEt;
    private ImageView mSendIv;
    private LinearLayout mEditTextBodyLl;
    private EaseTitleBar mTitleBar;
    private CommentInputMenu mCommentInputMenu;
    private MomentAdapter mAdapter;
    private MomentPresenter mPresenter;
    private LinearLayoutManager layoutManager;

    public static void show(Activity activity, CircleItem item, int position, int requestCode){
        activity.startActivityForResult(new Intent(activity, MomentDetailAct.class).putExtra(Constant.CIRCLE_ITEM_POS, position).putExtra(Constant.CIRCLE_ITEM, item), requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_moment_detail);

        circleItem = (CircleItem) getIntent().getSerializableExtra(Constant.CIRCLE_ITEM);

        initView();
        initEvent();
        setViewTreeObserver();
    }

    @Override
    protected void initBar() {
        initBar(true);
    }
    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(Constant.CIRCLE_ITEM_POS, getIntent().getIntExtra(Constant.CIRCLE_ITEM_POS, -1)).putExtra(Constant.CIRCLE_ITEM, circleItem));
        super.finish();
    }

    private void initEvent() {
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentInputMenu.isEmojiShow() || mCommentInputMenu.isKeyBoardShow()) {
                    updateEditTextBodyVisible(View.GONE, commentConfig);
                    return true;
                }
                return false;
            }
        });
        mCommentInputMenu.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCommentInputMenu.isEmojiShow() && isKeyBoardShow()) {
                    if(commentConfig == null) {
                        CommentConfig config = new CommentConfig();
                        config.commentType = CommentConfig.Type.PUBLIC;
                        config.id = circleItem.getId();
                        config.hint = "";
                        commentConfig = config;
                    }
                    measureCircleItemHighAndCommentItemOffset(commentConfig);
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.btn_add_like:
                        mPresenter.addFavort(position);
                        break;
                    case R.id.btn_add_comment:
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = position;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        config.id = circleItem.getId();
                        config.hint = "";
                        updateEditTextBodyVisible(View.VISIBLE, config);
                        break;
                    case R.id.delete_item:
                        new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.delete_comment_title)
                                .setMessage(getString(R.string.delete_moment_promit_message))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.delete_moment_sure), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //update(mActivity, model.getUrl(), model.getVersion());
                                        mPresenter.deleteCircle(circleItem.getId());
                                    }
                                })
                                .setNegativeButton(getString(R.string.delete_moment_cancle), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case R.id.friends_pull:
                        showDialog(view,circleItem.getUserId(),circleItem.getId());
                        break;
                    case R.id.comment_out_relative:
                        CommentConfig con = new CommentConfig();
                        con.circlePosition = position;
                        con.commentType = CommentConfig.Type.PUBLIC;
                        con.id = circleItem.getId();
                        con.hint = "";
                        updateEditTextBodyVisible(View.VISIBLE, con);
                        break;
                    default:
                        break;
                }
            }
        });

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

    private void showDialog(View view, String id, final String itemId){
        // 用于PopupWindow的View
        View contentView= LayoutInflater.from(MomentDetailAct.this).inflate(R.layout.pop_delete_friends, null, false);
        RelativeLayout relativeLayoutDelete = (RelativeLayout) contentView.findViewById(R.id.delete_relative) ;
        RelativeLayout relativeLayoutReport = (RelativeLayout) contentView.findViewById(R.id.feedback_relative);
        if (Constant.myUserIdZww.equals(id)){
            relativeLayoutDelete.setVisibility(View.VISIBLE);
            relativeLayoutReport.setVisibility(View.GONE);
        }else {
            relativeLayoutDelete.setVisibility(View.GONE);
            relativeLayoutReport.setVisibility(View.VISIBLE);
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
                jubao(itemId);
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
    private void jubao(String id){
        ApiManger2.getApiService()
                .momentReport(id,"1",null,null,null,null)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData list1) {
                    }

                    @Override
                    public void onNext(HttpResult<EmptyData> result) {
                        super.onNext(result);
                        if(result.getCode()==0){
                            Toast.makeText(App.applicationContext,"成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private int mCurrentItem = -1;
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mCurrentItem = data.getExtras().getInt("currentItem");
    }
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mCommentInputMenu = (CommentInputMenu) findViewById(R.id.conmment_input_menu);
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
                    CommentConfig config = new CommentConfig();
                    config.commentType = CommentConfig.Type.PUBLIC;
                    config.id = circleItem.getId();
                    config.hint="";
                    updateEditTextBodyVisible(View.GONE, config);
                }
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {

            }
        });

        setTitleBarLeftBack();

        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<CircleItem> circleItems = new ArrayList<>();
        circleItems.add(circleItem);
        mAdapter = new MomentAdapter(circleItems, R.layout.adapter_circle_item);
        mPresenter = new MomentPresenter(this, circleItem);
        mAdapter.setPresenter(mPresenter);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCommentInputMenu.onRegister();
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCommentInputMenu.onUnRegister();
    }


    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.sendIv:
                break;
        }*/
    }

    private CommentConfig commentConfig;

    @Override
    public void update2DeleteCircle(String circleId) {
        Message msg = new Message();
        MomentAct.mCollectHandler.sendEmptyMessage(1001);
      //  ToastUtils.showToast(getApplicationContext(), circleId);
        finish();
    }

    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {
        circleItem.setLike(true);
        List<FavortItem> likeModels = circleItem.getLikeModels();
        if(likeModels == null) {
            likeModels = new ArrayList<>();
            circleItem.setLikeModels(likeModels);
        }
        likeModels.add(addItem);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void update2DeleteFavort(int circlePosition) {
        List<FavortItem> items = circleItem.getLikeModels();
        String myUid = App.getMyUid();
        for (int i = 0; i < items.size(); i++) {
            if (myUid.equals(String.valueOf(items.get(i).getUserId()))) {
                circleItem.setLike(false);
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
            List<CommentItem> commentModels = circleItem.getCommentModels();
            if(commentModels == null) {
                commentModels = new ArrayList<>();
                circleItem.setCommentModels(commentModels);
            }
            commentModels.add(addItem);
            mAdapter.notifyDataSetChanged();
//        ToastUtils.showToast(getApplicationContext(), R.string.comment_added);
        }else {
            if(circleItem.getItemType() == CircleItem.TYPE_AD){
                ToastUtils.showToast(getApplicationContext(), R.string.comment_checking);
            }
        }
    }

    @Override
    public void update2DeleteComment(int circlePosition, List<CommentItem> commentId) {
        List<CommentItem> items = circleItem.getCommentModels();
        items.clear();
        if(null != commentId)
            items.addAll(commentId);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        if(commentConfig!=null && null != commentConfig.hint){
            mCommentInputMenu.setHint(commentConfig.hint);
        }else {
           // mCommentInputMenu.setHint("");
        }
        measureCircleItemHighAndCommentItemOffset(commentConfig);
        mCommentInputMenu.updateKeyBoard(View.VISIBLE);
 //       mCommentInputMenu.setKeyBoardShow(true);
        mCommentInputMenu.updateKeyBoard(visibility);
    }

    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private boolean currentEmojishow;
    private static int maxKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(firstPosition);

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

    private boolean isKeyBoardShow() {
        boolean b;
        b = maxKeyboardH != 0 && currentKeyboardH == maxKeyboardH;
        mCommentInputMenu.setKeyBoardShow(b);
        return b;
    }
    private RelativeLayout bodyLayout;

    private void setViewTreeObserver() {
        bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                L.d(TAG, "screenH＝ " + screenH + " &keyboardH = " + keyboardH + " &currentKeyboardH = " + currentKeyboardH + " &r.bottom=" + r.bottom + " &top="
                        + r.top + " &statusBarH=" + statusBarH);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
                if(mCommentInputMenu.isEmojiShow() || isKeyBoardShow()) {
                    layoutParams.bottomMargin = mCommentInputMenu.getHeight1();
                }else {
                    layoutParams.bottomMargin = mCommentInputMenu.getEditText().getHeight();
                }

                if (keyboardH == currentKeyboardH && currentEmojishow == mCommentInputMenu.isEmojiShow()) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                currentKeyboardH = keyboardH;
                currentEmojishow = mCommentInputMenu.isEmojiShow();
                if(currentKeyboardH>maxKeyboardH){
                    maxKeyboardH = currentKeyboardH;
                }

                screenHeight = screenH;//应用屏幕的高度

                if (!isKeyBoardShow() && !mCommentInputMenu.isEmojiShow()) {//说明是隐藏键盘的情况并且表情没有被展示
//                    updateEditTextBodyVisible(View.GONE, null);
//                    return;
                }
                if(commentConfig==null){
                    CommentConfig config = new CommentConfig();
                    config.commentType = CommentConfig.Type.PUBLIC;
                    config.id = circleItem.getId();
                    config.hint="";
                    commentConfig = config;
                }
                //偏移listview
                if (layoutManager != null && commentConfig != null) {
                    layoutManager.scrollToPositionWithOffset(0, getListviewOffset(commentConfig));
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
        if(selectCircleItemH == 0){
            selectCircleItemH = selectCircleItemH==0?layoutManager.getChildAt(0).getHeight():selectCircleItemH;
        }

        L.d(TAG, "selectCircleItemH:"+selectCircleItemH+"currentKeyboardH:"+currentKeyboardH+"mCommentInputMenu.getHeight1():"
                +mCommentInputMenu.getHeight1()+"mTitleBar" + ".getHeight():"+mTitleBar.getHeight());
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - mCommentInputMenu.getHeight1() - mTitleBar
                .getHeight();
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

    }

    @Override
    public void setDelete(boolean isSuccess,String toast) {
        if (isSuccess){
            Toast.makeText(MomentDetailAct.this, R.string.delete_moment_successful,Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MomentDetailAct.this,toast,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void update2DeleteMoment(CircleItem circleItem) {

    }
}
