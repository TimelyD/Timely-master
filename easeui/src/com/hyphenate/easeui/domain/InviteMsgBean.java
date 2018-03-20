package com.hyphenate.easeui.domain;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class InviteMsgBean {
    /**
     * inviter : {"userId":1,"nickname":123}
     * invitee : [{"userId":2,"nickname":123},{"userId":3,"nickname":123}]
     */

    private InviterBean inviter;
    private List<InviteeBean> invitee;

    public InviterBean getInviter() {
        return inviter;
    }

    public void setInviter(InviterBean inviter) {
        this.inviter = inviter;
    }

    public List<InviteeBean> getInvitee() {
        return invitee;
    }

    public void setInvitee(List<InviteeBean> invitee) {
        this.invitee = invitee;
    }

    public static class InviterBean {
        /**
         * userId : 1
         * nickname : 123
         */

        private String userId;
        private String nickname;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class InviteeBean {
        /**
         * userId : 2
         * nickname : 123
         */

        private String userId;
        private String nickname;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
