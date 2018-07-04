package com.tg.tgt.moment.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.tg.tgt.App;
import com.tg.tgt.R;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.spannable.CircleMovementMethod;
import com.tg.tgt.moment.spannable.SpannableClickable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yiwei on 16/7/9.
 */
public class CommentListView extends LinearLayout {
    private int itemColor ;
    private int itemSelectorColor ;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<CommentItem> mDatas;
    private LayoutInflater layoutInflater ;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setDatas(List<CommentItem> datas){
        if(datas == null ){
            datas = new ArrayList<CommentItem>();
        }
        mDatas = datas;
        notifyDataSetChanged();
    }

    public List<CommentItem> getDatas(){
        return mDatas;
    }

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
//        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0);
//        try {
//            //textview的默认颜色
//            itemColor = typedArray.getColor(R.styleable.PraiseListView_item_color, getResources().getColor(R.color.praise_item_default));
//            itemSelectorColor = typedArray.getColor(R.styleable.PraiseListView_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));
//
//        }finally {
//            typedArray.recycle();
//        }
        itemColor = ContextCompat.getColor(App.applicationContext, R.color.blue);
        itemSelectorColor = ContextCompat.getColor(App.applicationContext, R.color.press);
    }

    public void notifyDataSetChanged(){

        removeAllViews();
        if(mDatas == null || mDatas.size() == 0){
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(int i=0; i<mDatas.size(); i++){
            final int index = i;
            View view = getView(index);
            if(view == null){
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            addView(view, index, layoutParams);
        }

    }

    private View getView(final int position){
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(getContext());
        }
        View convertView = layoutInflater.inflate(R.layout.item_comment, null, false);

        TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(itemSelectorColor, itemSelectorColor);

        final CommentItem bean = mDatas.get(position);
        String name = bean.safeGetRemark();
        String id = bean.getId();
        String toReplyName = bean.safeGetParentRemark();

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, bean.getUserId(), bean.getUsername()));

        if (!TextUtils.isEmpty(toReplyName)) {

            builder.append(getContext().getString(R.string.reply_to));
            builder.append(setClickableSpan(toReplyName, bean.getParentUserId(), bean.getParentUsername()));
        }
        builder.append(":  ");
        //转换表情字符
        String contentBodyStr = bean.getContent();
        //暂时不需要
//        builder.append(UrlUtils.formatUrlString(contentBodyStr));
        builder.append(contentBodyStr);

        commentTv.setText(EaseSmileUtils.getSmiledText(getContext(), builder));

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(position);
                    }
                }
            }
        });
        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if(onItemLongClickListener!=null){
                        onItemLongClickListener.onItemLongClick(position);
                    }
                    return true;
                }
                return false;
            }
        });

        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(final String textStr, final String id, final String username) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor){
                                    @Override
                                    public void onClick(View widget) {
                                        if(mOnUserClickListener!=null)
                                            mOnUserClickListener.onUserClick(username, id);
//                                        getContext().startActivity(new Intent(getContext(), MomentAct.class).putExtra(Constant.USER_ID, id).putExtra(Constant.IS_MINE_HOME_PAGE, true));
//                                        Toast.makeText(App.applicationContext, textStr + " &id = " + id, Toast.LENGTH_SHORT).show();
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }
    private OnUserClickListener mOnUserClickListener;
    public void setOnUserClickListener(OnUserClickListener listener){
        mOnUserClickListener = listener;
    }
    public interface OnUserClickListener{
        void onUserClick(String username, String userId);
    }
    public static interface OnItemClickListener{
        public void onItemClick(int position);
    }

    public static interface OnItemLongClickListener{
        public void onItemLongClick(int position);
    }



}
