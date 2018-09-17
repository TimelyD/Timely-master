package com.tg.tgt.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.tg.tgt.App;
import com.tg.tgt.R;
import com.tg.tgt.moment.bean.MsgBean;
import com.tg.tgt.moment.spannable.CircleMovementMethod;
import com.tg.tgt.moment.spannable.SpannableClickable;

import java.util.List;

/**
 * Created by DELL on 2018/8/30.
 */

public class MsgAdapter extends BaseAdapter{
    private int itemColor ;
    private List<MsgBean.ListBean> list;
    private Context mContext;
    private String url;

    public MsgAdapter(Context context,List<MsgBean.ListBean>list,String url){
        this.list = list;
        this.url=url;
        mContext = context;
        initAttrs();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_msg,null);
            viewHolder.headImg = (ImageView)convertView.findViewById(R.id.avatar_iv);
            viewHolder.time = (TextView)convertView.findViewById(R.id.time_tv);
            viewHolder.name = (TextView)convertView.findViewById(R.id.name_tv);
            viewHolder.content = (TextView)convertView.findViewById(R.id.content);
            viewHolder.photo = (ZQImageViewRoundOval) convertView.findViewById(R.id.photo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getNickname());
        viewHolder.time.setText(list.get(position).getCreateTime());
        viewHolder.photo.setType(ZQImageViewRoundOval.TYPE_ROUND);viewHolder.photo.setRoundRadius(10);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(list.get(position).getNoticeType().equals("1")){//评论
            //builder.append(setClickableSpan(mContext.getString(R.string.msg3)));
            builder.append(list.get(position).getCommentMsg());
            viewHolder.content.setText(EaseSmileUtils.getSmiledText(mContext, builder));
        }else if(list.get(position).getNoticeType().equals("2")){//点赞
            viewHolder.content.setText(R.string.msg1);
        }else if(list.get(position).getNoticeType().equals("3")){//回复
            //builder.append(setClickableSpan(mContext.getString(R.string.msg2)));
            builder.append(list.get(position).getCommentMsg());
            viewHolder.content.setText(EaseSmileUtils.getSmiledText(mContext, builder));
        }else {
            viewHolder.content.setText("no data");
        }
        try {
            ImageUtils.show(mContext,url+"/"+list.get(position).getPicture(), R.drawable.default_avatar,viewHolder.headImg);
        } catch (Exception e) {
            ImageUtils.show(mContext, "", R.drawable.default_avatar,viewHolder.headImg);
            e.printStackTrace();
        }
        if(list.get(position).getMomentImg()==null){
            viewHolder.photo.setVisibility(View.GONE);
        }else {
            GlideApp.with(mContext).load(url+"/"+list.get(position).getMomentImg()).into( viewHolder.photo);
        }
        return convertView;
    }
    protected void initAttrs() {
        itemColor = ContextCompat.getColor(App.applicationContext, R.color.tx_black_3);
    }

    @NonNull
    private SpannableString setClickableSpan(final String textStr) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor){
                                    @Override
                                    public void onClick(View widget) {

                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        subjectSpanText.setSpan(new AbsoluteSizeSpan(46), 0,textStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public void notify(List<MsgBean.ListBean> list){
        this.list=list;
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        ImageView headImg;
        TextView name;
        TextView time;
        TextView content;
        ZQImageViewRoundOval photo;
    }
}
