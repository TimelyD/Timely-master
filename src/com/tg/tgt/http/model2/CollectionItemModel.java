package com.tg.tgt.http.model2;

/**
 * Created by DELL on 2018/7/5.
 */

public class CollectionItemModel {
    private String content;//": "string", 文本内容
    private String createTime;//": "2018-07-05T09:50:26.728Z",
    private String crtTime;//": "2018-07-05T09:50:26.728Z",创建时间
    private String filePath;//": "string",收藏文件路径
    private int id;//": 0, ID ,
    private String isFrom;//": "string",文件来源
    private String picturePath;//": "string",展示图片路径
    private String remark;//": "string",备注
    private int type;//": 0,收藏类型(1:图片 2:视频 3:音频 4:文件 5:文本) ,
    private String updateTime;//": "2018-07-05T09:50:26.728Z",
    private int userId;//": 0收藏用户ID

    private boolean isSelect;//是否被选中删除

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(String crtTime) {
        this.crtTime = crtTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(String isFrom) {
        this.isFrom = isFrom;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
