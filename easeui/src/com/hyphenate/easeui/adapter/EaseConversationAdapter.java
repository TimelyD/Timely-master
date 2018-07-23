package com.hyphenate.easeui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.utils.AESCodeer;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GroupHelper;
import com.hyphenate.easeui.utils.RSAUtil;
import com.hyphenate.easeui.widget.EaseConversationList.EaseConversationListHelper;
import com.hyphenate.easeui.widget.chatrow.timeUtil;
import com.hyphenate.util.DateUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * conversation list adapter
 *
 */
public class EaseConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    
    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

    public EaseConversationAdapter(Context context, int resource,
                                   List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(com.hyphenate.easeui.R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.message);
            holder.time = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(com.hyphenate.easeui.R.id.avatar);
            holder.msgState = convertView.findViewById(com.hyphenate.easeui.R.id.msg_state);
            holder.longDivider = convertView.findViewById(R.id.divider_long);
            holder.shortDivider = convertView.findViewById(R.id.divider_short);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(com.hyphenate.easeui.R.id.list_itease_layout);
            holder.motioned = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.mentioned);
            holder.block = convertView.findViewById(R.id.block);
            holder.msgLock = (ImageView) convertView.findViewById(com.hyphenate.easeui.R.id.iv_msg_lock);

            convertView.setTag(holder);
        }
        //暂时不要点击效果
//        holder.list_itease_layout.setBackgroundResource(com.hyphenate.easeui.R.drawable.ease_mm_listitem);

        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String username = conversation.conversationId();
        
        if (conversation.getType() == EMConversationType.GroupChat) {
            String groupId = conversation.conversationId();
            if(EMClient.getInstance().groupManager().getGroup(groupId).isMsgBlocked()){
                holder.block.setVisibility(View.VISIBLE);
            }else {
                holder.block.setVisibility(View.GONE);
            }
            if(EaseAtMessageHelper.get().hasAtMeMsg(groupId)){
                holder.motioned.setVisibility(View.VISIBLE);
            }else{
                holder.motioned.setVisibility(View.GONE);
            }

            // group message, show group avatar
            holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);

            if(cvsListHelper != null){
                cvsListHelper.onSetGroup(username, holder.name);
            }

            //群组暂时默认不加锁
            holder.msgLock.setTag(R.id.msg_lock_tag, true);
            holder.msgLock.setTag(R.id.msg_result_tag, null);
            holder.msgLock.setVisibility(View.INVISIBLE);
        } else if(conversation.getType() == EMConversationType.ChatRoom){
            holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            holder.motioned.setVisibility(View.GONE);

            //聊天室暂时默认不加锁
            holder.msgLock.setTag(R.id.msg_lock_tag, true);
            holder.msgLock.setTag(R.id.msg_result_tag, null);
            holder.msgLock.setVisibility(View.INVISIBLE);
        }else {
            //设置用户头像
            EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
            EaseUserUtils.setUserNick(username, holder.name);
            EaseUserUtils.setIsLock(username, holder.msgLock);
            holder.motioned.setVisibility(View.GONE);

            holder.msgLock.setTag(R.id.msg_lock_tag, false);
            //在这里加入是否显示lock
            if(cvsListHelper != null){
                cvsListHelper.onSetIsMsgClock(username, holder.msgLock, holder.avatar, holder.name);
            }
        }

        int unreadMsgCount = conversation.getUnreadMsgCount();
        if (unreadMsgCount > 0) {
            // show unread message count
            holder.unreadLabel.setText(String.valueOf(unreadMsgCount>99?"···":unreadMsgCount));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.GONE);
        }
        if (conversation.getAllMsgCount() != 0) {
        	// show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if(cvsListHelper != null){
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            //如果是进群邀请则显示邀请信息，如果不是则按原来
            if(lastMessage.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_INVITE_INTO_GROUP, false)){
                holder.message.setText(GroupHelper.parseInviteMsg(lastMessage));
            }else {
                if(EaseUserUtils.getUserInfo(username).getIsLock() == 1){
                    holder.message.setText("******");
                }else {
                    //holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),BufferType.SPANNABLE);
                    holder.message.setText(mi(lastMessage));
                }
            }
            if(content != null){
                holder.message.setText(content);
            }
            //holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            holder.time.setText(timeUtil.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }else {
            //holder.message.setText("");
        }

        if(position  == getCount() - 1){
            holder.longDivider.setVisibility(View.VISIBLE);
            holder.shortDivider.setVisibility(View.GONE);
        }else {
            holder.longDivider.setVisibility(View.GONE);
            holder.shortDivider.setVisibility(View.VISIBLE);
        }
        
        //set property
        /*holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if(primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if(timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);*/

        return convertView;
    }

    protected List<KeyBean> toArray(String string){
        Type type = new TypeToken<List<KeyBean>>(){}.getType();
        List<KeyBean> b = new Gson().fromJson(string,type);
        return b;
    }

    protected String mi(EMMessage message){
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String text = txtBody.getMessage();
        String version = message.getStringAttribute(EaseConstant.VERSION, null);
        String mi = message.getStringAttribute(EaseConstant.MI, null);       //获得用对方的公钥RSA加密后的random
        String send_msg= message.getStringAttribute(EaseConstant.SEND, null);//获得用我的公钥RSA加密后的random
        String pri = EaseApp.sf.getString("pri_key", "");//获得私钥（可解密aesKey）
        String s = EaseApp.sf.getString(EaseApp.keyBean, ""); //得到登录时获取的我的最新版本聊天私钥（解密消息用）
        KeyBean bean = new Gson().fromJson(s, KeyBean.class);//我的聊天私钥的实体类
        String a = EaseApp.sf.getString(EaseApp.keylist, "");//得到登录时获取的我的所有版本聊天私钥
        if(mi!=null){
            try {
                if(message.getChatType()== EMMessage.ChatType.Chat){
                    Log.i("对话","单聊");
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
                if(message.getChatType()== EMMessage.ChatType.GroupChat){
                    Log.i("对话","群聊");
                    if(message.direct() == EMMessage.Direct.RECEIVE){
                        for(KeyBean be:EaseApp.group_pub){
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
        return text;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }
    
    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }
    

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();
                    
                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if(group != null){
                        username = group.getGroupName();
                    }else{
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        // TODO: not support Nick anymore
//                        if(user != null && user.getNick() != null)
//                            username = user.getNick();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else{
                          final String[] words = username.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationListHelper cvsListHelper){
        this.cvsListHelper = cvsListHelper;
    }
    
    private static class ViewHolder {
        /** who you chat with */
        TextView name;
        /** unread message count */
        TextView unreadLabel;
        /** content of last message */
        TextView message;
        /** time of last message */
        TextView time;
        /** avatar */
        ImageView avatar;
        /** status of last message */
        View msgState;
        /** layout */
        RelativeLayout list_itease_layout;
        TextView motioned;
        View block;
        /**消息是否加锁*/
        ImageView msgLock;
        View longDivider;
        View shortDivider;
    }
}

