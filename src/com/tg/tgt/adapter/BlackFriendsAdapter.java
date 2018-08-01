package com.tg.tgt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.ImageUtils;
import com.tg.tgt.R;

import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 2018/7/31.
 */

public class BlackFriendsAdapter extends BaseAdapter{

    private List<EaseUser> list;
    private Context mContext;

    public BlackFriendsAdapter(List<EaseUser>list,Context context){
        this.list = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null:list.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.em_row_black_msg,null);
            viewHolder.headImg = (ImageView)convertView.findViewById(R.id.avatar);
            viewHolder.msgText = (TextView)convertView.findViewById(R.id.message);
            viewHolder.nickName = (TextView)convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageUtils.show(mContext, list.get(position).getAvatar(), R.drawable.default_avatar, viewHolder.headImg);
        viewHolder.nickName.setText(list.get(position).getNick());
        viewHolder.msgText.setText(list.get(position).getRemark());
        return convertView;
    }

    private static class ViewHolder{
        ImageView headImg;
        TextView nickName;
        TextView msgText;
    }

}
