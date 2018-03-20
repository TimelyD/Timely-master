package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class GroupEntity {

    /**
     * affiliationsCont : 0
     * allowInvites : false
     * createTime : 2017-11-03T01:45:26.846Z
     * groupDescription : string
     * groupName : string
     * groupSn : string
     * id : 0
     * inviteNeedConffirm : false
     * maxUsers : 0
     * updateTime : 2017-11-03T01:45:26.846Z
     * userId : 0
     */

    /**
     * 现有成员人数
     */
    private int affiliationsCont;
    /**
     * 是否允许群成员邀请入群
     */
    private boolean allowInvites;
    private String createTime;
    private String groupDescription;
    private String groupName;
    private String groupSn;
    private long id;
    /**
     * 是否确认入群
     */
    private boolean inviteNeedConffirm;
    private int maxUsers;
    private String updateTime;
    /**
     * 群主编号
     */
    private long userId;

    public int getAffiliationsCont() {
        return affiliationsCont;
    }

    public void setAffiliationsCont(int affiliationsCont) {
        this.affiliationsCont = affiliationsCont;
    }

    public boolean isAllowInvites() {
        return allowInvites;
    }

    public void setAllowInvites(boolean allowInvites) {
        this.allowInvites = allowInvites;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupSn() {
        return groupSn;
    }

    public void setGroupSn(String groupSn) {
        this.groupSn = groupSn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isInviteNeedConffirm() {
        return inviteNeedConffirm;
    }

    public void setInviteNeedConffirm(boolean inviteNeedConffirm) {
        this.inviteNeedConffirm = inviteNeedConffirm;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
