package com.tg.tgt.http.model2;

import com.tg.tgt.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 *
 * @author yiyang
 */
@Entity
public class GroupModel {

    /**
     * affiliationsCont : 0
     * allowInvites : false
     * blocks : false
     * createTime : 2017-11-03T06:51:49.087Z
     * groupDescription : string
     * groupName : string
     * groupSn : string
     * groupUserModels : [{"groupAdmin":false,"groupNickname":"string","groupOwner":false,"mute":false,
     * "nickname":"string","picture":"string","showNickname":false,"userId":0,"username":"string"}]
     * id : 0
     * inviteNeedConffirm : false
     * maxUsers : 0
     * updateTime : 2017-11-03T06:51:49.087Z
     * userId : 0
     */

    private int affiliationsCont;
    private boolean allowInvites;
    private boolean blocks;
    private String createTime;
    private String groupDescription;
    private String groupName;
    private String groupSn;
    @Id
    private Long id;
    private boolean inviteNeedConffirm;
    private int maxUsers;
    private String updateTime;
    private long userId;
    private Boolean groupOwner;

    @ToMany(referencedJoinProperty = "groupId")
    private List<GroupUserModel> groupUserModels;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 676544898)
    private transient GroupModelDao myDao;

    @Generated(hash = 2010397577)
    public GroupModel(int affiliationsCont, boolean allowInvites, boolean blocks, String createTime,
            String groupDescription, String groupName, String groupSn, Long id,
            boolean inviteNeedConffirm, int maxUsers, String updateTime, long userId,boolean groupOwner) {
        this.affiliationsCont = affiliationsCont;
        this.allowInvites = allowInvites;
        this.blocks = blocks;
        this.createTime = createTime;
        this.groupDescription = groupDescription;
        this.groupName = groupName;
        this.groupSn = groupSn;
        this.id = id;
        this.inviteNeedConffirm = inviteNeedConffirm;
        this.maxUsers = maxUsers;
        this.updateTime = updateTime;
        this.userId = userId;
        this.groupOwner=groupOwner;
    }

    public void setGroupUserModels(List<GroupUserModel> groupUserModels) {
        this.groupUserModels = groupUserModels;
    }

    @Generated(hash = 955178835)

    public GroupModel() {
    }

    public int getAffiliationsCont() {
        return this.affiliationsCont;
    }

    public void setAffiliationsCont(int affiliationsCont) {
        this.affiliationsCont = affiliationsCont;
    }

    public boolean getAllowInvites() {
        return this.allowInvites;
    }

    public void setAllowInvites(boolean allowInvites) {
        this.allowInvites = allowInvites;
    }

    public boolean getBlocks() {
        return this.blocks;
    }

    public void setBlocks(boolean blocks) {
        this.blocks = blocks;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupSn() {
        return this.groupSn;
    }

    public void setGroupSn(String groupSn) {
        this.groupSn = groupSn;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getInviteNeedConffirm() {
        return this.inviteNeedConffirm;
    }

    public void setInviteNeedConffirm(boolean inviteNeedConffirm) {
        this.inviteNeedConffirm = inviteNeedConffirm;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Boolean getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(Boolean groupOwner) {
        this.groupOwner = groupOwner;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1171329532)
    public List<GroupUserModel> getGroupUserModels() {
        if (groupUserModels == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupUserModelDao targetDao = daoSession.getGroupUserModelDao();
            List<GroupUserModel> groupUserModelsNew = targetDao._queryGroupModel_GroupUserModels(id);
            synchronized (this) {
                if (groupUserModels == null) {
                    groupUserModels = groupUserModelsNew;
                }
            }
        }
        return groupUserModels;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 16180179)
    public synchronized void resetGroupUserModels() {
        groupUserModels = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 359726128)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGroupModelDao() : null;
    }

}
