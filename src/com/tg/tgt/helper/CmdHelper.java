package com.tg.tgt.helper;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.tg.tgt.DemoHelper;

/**
 *
 * @author yiyang
 */
public class CmdHelper {
    /**
     * 当接收到会议邀请之后，点击拒绝需要通知对方
     * @param confId 会议id
     */
    public static void notifyReject(String username, String confId){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action= DemoHelper.CMD_REJECT_+confId;
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        String toUsername = username;
        cmdMsg.setTo(toUsername);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    /**
     * 当邀请对方后挂断了电话，需要通知对方
     */
    public static void notifyCancel(String username, String confId){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action=DemoHelper.CMD_CANCEL_+confId;
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        String toUsername = username;
        cmdMsg.setTo(toUsername);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
}
