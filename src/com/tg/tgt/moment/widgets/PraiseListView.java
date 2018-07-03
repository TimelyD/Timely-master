package com.tg.tgt.moment.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.tg.tgt.R;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.spannable.CircleMovementMethod;
import com.tg.tgt.moment.spannable.SpannableClickable;

import java.util.List;

/**
 * Created by yiwei on 16/7/9.
 */
public class PraiseListView extends TextView {


    private int itemColor;
    private int itemSelectorColor;
    private List<FavortItem> datas;
    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PraiseListView(Context context) {
        super(context);
    }

    public PraiseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public PraiseListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        itemColor = getResources().getColor(R.color.praise_item);
        itemSelectorColor = getResources().getColor(R.color.praise_item_selector_default);
//        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0);
//        try {
//            //textview的默认颜色
//            itemColor = typedArray.getColor(R.styleable.PraiseListView_item_color, getResources().getColor(R.color.praise_item_default));
//            itemSelectorColor = typedArray.getColor(R.styleable.PraiseListView_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));
//
//        }finally {
//            typedArray.recycle();
//        }
    }

    public List<FavortItem> getDatas() {
        return datas;
    }
    public void setDatas(List<FavortItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    public void notifyDataSetChanged(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(datas != null && datas.size() > 0){
            //添加点赞图标
            builder.append(setImageSpan());
            FavortItem item = null;
            int size = datas.size();
            for (int i = 0; i< size; i++){
                item = datas.get(i);
                if(item != null){
                    builder.append(setClickableSpan(item.safeGetRemark(), i));
                    if(i != size -1){
                        builder.append(", ");
                    }
                }
            }
            if(size >1)
                builder.append(String.format(getContext().getString(R.string.more_like), ""+size));
            else
                builder.append(getContext().getString(R.string.one_like));
        }

        setText(builder);
        setMovementMethod(new CircleMovementMethod(itemSelectorColor));
    }


    private SpannableString setImageSpan(){
        String text = "  ";
        SpannableString imgSpanText = new SpannableString(text);
        imgSpanText.setSpan(new ImageSpan(getContext(), R.drawable.add_like_selected, DynamicDrawableSpan.ALIGN_BOTTOM),
                0 , 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return imgSpanText;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, final int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor){
                                    @Override
                                    public void onClick(View widget) {
                                        if(onItemClickListener!=null){
                                            onItemClickListener.onClick(position);
                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }


    public static interface OnItemClickListener{
        public void onClick(int position);
    }
}
