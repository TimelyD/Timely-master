package com.tg.tgt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tg.tgt.R;
import com.tg.tgt.moment.bean.MsgBean;

import java.util.List;

/**
 * Created by DELL on 2018/8/30.
 */

public class MsgAdapter extends BaseAdapter{
    private List<MsgBean> list;
    private Context mContext;

    public MsgAdapter(Context context,List<MsgBean>list){
        this.list = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
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
            viewHolder.headImg = (ImageView)convertView.findViewById(R.id.avatar);
            viewHolder.msgText = (TextView)convertView.findViewById(R.id.message);
            viewHolder.nickName = (TextView)convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }


    private static class ViewHolder{
        ImageView headImg;
        TextView nickName;
        TextView msgText;
    }
}
