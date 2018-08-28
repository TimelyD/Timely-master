package com.tg.tgt.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.hyphenate.util.EMLog;
import com.tg.tgt.http.model2.GroupUserModel;

import java.util.ArrayList;
import java.util.List;

public class EaseContact2Adapter extends ArrayAdapter<GroupUserModel> implements SectionIndexer{
    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<GroupUserModel> userList;
    List<GroupUserModel> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;
    private Context context;

    public EaseContact2Adapter(Context context, int resource, List<GroupUserModel> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        this.context=context;
        copyUserList = new ArrayList<GroupUserModel>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    public static class ViewHolder {
        ZQImageViewRoundOval avatar;
        TextView nameView;
        TextView headerView;
        ImageView lockImg;
        View longDivider;
        View shortDivider;
        View headDivider;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            if(res == 0)
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, parent, false);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (ZQImageViewRoundOval) convertView.findViewById(R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.headerView = (TextView) convertView.findViewById(R.id.header);
            holder.lockImg = (ImageView) convertView.findViewById(R.id.iv_msg_lock);
            holder.longDivider = convertView.findViewById(R.id.divider_long);
            holder.shortDivider = convertView.findViewById(R.id.divider_short);
            holder.headDivider = convertView.findViewById(R.id.divider_header);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.avatar.setType(ZQImageViewRoundOval.TYPE_ROUND);holder.avatar.setRoundRadius(10);
        GroupUserModel user = getItem(position);
        if(user == null)
            Log.d("ContactAdapter", position + "");
        String username = user.getUsername();
        EaseUser userInfo = EaseUserUtils.getUserInfo(user.getUsername());
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getRemark()))
            username=userInfo.getRemark();
        else
            username=TextUtils.isEmpty(user.getNickname())?user.getUsername():user.getNickname();

        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
                holder.headDivider.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
                holder.headDivider.setVisibility(View.VISIBLE);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
            holder.headDivider.setVisibility(View.GONE);
        }
        if(position == 0){
            holder.longDivider.setVisibility(View.VISIBLE);
            holder.shortDivider.setVisibility(View.GONE);
        }else if(holder.headerView.getVisibility() == View.VISIBLE){
            holder.longDivider.setVisibility(View.VISIBLE);
            holder.shortDivider.setVisibility(View.GONE);
        }else {
            holder.longDivider.setVisibility(View.GONE);
            holder.shortDivider.setVisibility(View.VISIBLE);
        }
        Log.i("zzz", toJson(user,1));
        EaseUserUtils.setUserNick(username, holder.nameView);
        GlideApp.with(context).load(user.getPicture()).placeholder(com.tg.tgt.R.drawable.default_avatar2)
                .into(holder.avatar);
        EaseUserUtils.setIsLock(username, holder.lockImg);

        if(primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if(primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if(initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        //这样会闪现
//        holder.lockImg.setVisibility(View.GONE);
        //通过tag来表面加载状态
        holder.lockImg.setTag(R.id.msg_lock_tag, false);
        if(mEaseContactListHelper != null){
            mEaseContactListHelper.onSetIsMsgClock(username, holder.lockImg, holder.avatar, holder.nameView);
        }

        return convertView;
    }

    private String toJson(Object obj,int method) {
        // TODO Auto-generated method stub
        if (method==1) {
            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        }else if(method==2){
            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2=gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    @Override
    public GroupUserModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }
    
    @Override
    public Filter getFilter() {
        if(myFilter==null){
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }
    
    protected class  MyFilter extends Filter{
        List<GroupUserModel> mOriginalList = null;
        
        public MyFilter(List<GroupUserModel> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if(mOriginalList==null){
                mOriginalList = new ArrayList<GroupUserModel>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());
            
            if(prefix==null || prefix.length()==0){
                results.values = copyUserList;
                results.count = copyUserList.size();
            }else{
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<GroupUserModel> newValues = new ArrayList<GroupUserModel>();
                for(int i=0;i<count;i++){
                    final GroupUserModel user = mOriginalList.get(i);
                    String username = user.getUsername();
                    
                    if(username.startsWith(prefixString)){
                        newValues.add(user);
                    } else{
                         final String[] words = username.split(" ");
                         final int wordCount = words.length;
    
                         // Start at index 0, in case valueText star·ts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values=newValues;
                results.count=newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                FilterResults results) {
            userList.clear();
            userList.addAll((List<GroupUserModel>)results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
    
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }
    
    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public EaseContact2Adapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public EaseContact2Adapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public EaseContact2Adapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public EaseContact2Adapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }

    private EaseContactList.EaseContactListHelper mEaseContactListHelper;
    public void setEaseContactListHelper(EaseContactList.EaseContactListHelper easeContactListHelper){
        this.mEaseContactListHelper = easeContactListHelper;
    }
}
