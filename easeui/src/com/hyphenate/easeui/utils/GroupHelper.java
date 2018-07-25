package com.hyphenate.easeui.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.InviteMsgBean;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class GroupHelper {
    /**
     * 用来邀请人进入群时发送的提示信息
     * @param group
     * @param members
     */
    public static void sendInviteMsg(EMGroup group, String[] members){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.length; i++) {
            String member = members[i];
            if(members.length-1 == i){
                sb.append(member);
            }else {
                sb.append(member).append("-");
            }
        }
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        message.setFrom(EMClient.getInstance().getCurrentUser());
        message.setTo(group.getGroupId());
        message.addBody(new EMTextMessageBody(sb.toString()));
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_INVITE_INTO_GROUP, true);
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 用来踢人出群时发送的提示信息
     * @param group
     * @param members
     */
    public static void sendKickedMsg(EMGroup group, String[] members){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.length; i++) {
            String member = members[i];
            if(members.length-1 == i){
                sb.append(member);
            }else {
                sb.append(member).append("-");
            }
        }
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        message.setFrom(EMClient.getInstance().getCurrentUser());
        message.setTo(group.getGroupId());
        message.addBody(new EMTextMessageBody(sb.toString()));
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_KICKED_GROUP, true);
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 用来解析{@link #sendInviteMsg}发出的消息
     * 现在修改为后台发送，格式：{"inviter":{"userId":1,"nickname":123},"invitee":[{"userId":2,"nickname":123}]}
     * @return 邀请字符串
     */
    public static String parseInviteMsg(EMMessage message) {
        /*List<String> members = Arrays.asList(((EMTextMessageBody) message.getBody()).getMessage().split("-"));
        String from = message.getFrom();
        StringBuilder sb = new StringBuilder();

        if(from.equals(EMClient.getInstance().getCurrentUser())){
            sb.append("你邀请了");
        }else {
            String con = EaseUserUtils.getUserInfo(from).safeGetRemark();
            if (con == null)
                con = EaseUserUtils.getUserInfo(from).getUsername();
            sb.append(con).append("邀请了");
        }

        for (int i = 0; i < members.size(); i++) {
            String username = members.get(i);
            if(username.equals(EMClient.getInstance().getCurrentUser())){
                sb.append("你");
            }else {
                String con = EaseUserUtils.getUserInfo(username).safeGetRemark();
                if (con == null)
                    con = EaseUserUtils.getUserInfo(username).getUsername();
                sb.append(con);
            }
            if (i != members.size() - 1)
                sb.append("、 ");
        }
        sb.append("加入了群聊");
        return sb.toString();*/
        //StringBuilder sb = new StringBuilder();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            //此处bean中的userId实际上是环信id
            InviteMsgBean inviteMsgBean = new Gson().fromJson(((EMTextMessageBody) message.getBody()).getMessage(),
                    InviteMsgBean.class);
            String myUid = EMClient.getInstance().getCurrentUser();
            InviteMsgBean.InviterBean inviter = inviteMsgBean.getInviter();
            if(myUid.equals(inviter.getUserId())){
                sb.append("你邀请了");
            }else {
                EaseUser userInfo = EaseUserUtils.getUserInfo(inviter.getUserId());
                String nickname;
                if(userInfo!=null && userInfo.getRemark()!=null) {
                    nickname = userInfo.getRemark();
                }else {
                    nickname = inviter.getNickname();
                }
                sb.append(nickname).append("邀请了");
            }

            List<InviteMsgBean.InviteeBean> invitee = inviteMsgBean.getInvitee();
            for (int i = 0; i < invitee.size(); i++) {
                InviteMsgBean.InviteeBean inviteeBean = invitee.get(i);
                if(myUid.equals(inviteeBean.getUserId())){
                    sb.append("你");
                }else {
                    EaseUser userInfo = EaseUserUtils.getUserInfo(inviteeBean.getUserId());
                    String nickname;
                    if(userInfo!=null && userInfo.getRemark()!=null) {
                        nickname = userInfo.getRemark();
                    }else {
                        nickname = inviteeBean.getNickname();
                    }
                    int len = sb.length();
                    sb.append(nickname);
                   // sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")),len,sb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
                }
                if (i != invitee.size() - 1)
                    sb.append("、");
                Log.i("qqq加入",sb.toString());
            }
            sb.append("加入了群聊");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     *  被踢出群聊的提示
     * */
    public static String parseKickedMsg(EMMessage message) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            //此处bean中的userId实际上是环信id
            InviteMsgBean inviteMsgBean = new Gson().fromJson(((EMTextMessageBody) message.getBody()).getMessage(),
                    InviteMsgBean.class);
            String myUid = EMClient.getInstance().getCurrentUser();
            InviteMsgBean.InviterBean inviter = inviteMsgBean.getInviter();

            List<InviteMsgBean.InviteeBean> invitee = inviteMsgBean.getInvitee();
            for (int i = 0; i < invitee.size(); i++) {
                InviteMsgBean.InviteeBean inviteeBean = invitee.get(i);
                if(myUid.equals(inviteeBean.getUserId())){
                    sb.append("你");
                }else {
                    EaseUser userInfo = EaseUserUtils.getUserInfo(inviteeBean.getUserId());
                    String nickname;
                    if(userInfo!=null && userInfo.getRemark()!=null) {
                        nickname = userInfo.getRemark();
                    }else {
                        nickname = inviteeBean.getNickname();
                    }
                    int len = sb.length();
                    sb.append(nickname);
                    // sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")),len,sb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
                }
                if (i != invitee.size() - 1)
                    sb.append("、");
                Log.i("qqq踢出",sb.toString());
            }

            if(myUid.equals(inviter.getUserId())){
                sb.append("被你");
            }else {
                EaseUser userInfo = EaseUserUtils.getUserInfo(inviter.getUserId());
                String nickname;
                if(userInfo!=null && userInfo.getRemark()!=null) {
                    nickname = userInfo.getRemark();
                }else {
                    nickname = inviter.getNickname();
                }
                sb.append("被").append(nickname);

            }
            sb.append("踢出了群聊");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
