package com.hyphenate.easeui.widget.photoselect;

import com.hyphenate.easeui.utils.photo.MediaBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author yiyang
 */
public class SelectObserable extends Observable{

    private List<MediaBean> mSelectImages;
    private List<MediaBean> mFolderAllImages;

    public void setSelectImages(List<MediaBean> selectImages) {
        mSelectImages = selectImages;
    }

    public void setFolderAllImages(List<MediaBean> folderAllImages) {
        mFolderAllImages = folderAllImages;
    }

    public List<MediaBean> getSelectImages() {
        if(null == mSelectImages){
            mSelectImages = new ArrayList<>();
        }
        return mSelectImages;
    }

    public List<MediaBean> getFolderAllImages() {
        return mFolderAllImages;
    }

    //  创建单例
    private static class SingletonHolder {
        private static final SelectObserable INSTANCE = new SelectObserable();
    }
    public static SelectObserable getInstance() {

        return SingletonHolder.INSTANCE;
    }


    public void setChange() {
        setChanged();
        notifyObservers();
    }

    /**
     * 慎用！！！该方法只提示被修改的，但由于被修改的而影响的其他条目pos发生改变，不会被
     * @param position
     */
    public void setChange(int position) {
        setChanged();
        notifyObservers(position);
    }
    public void setChange(List<MediaBean> position) {
        setChanged();
        notifyObservers(position);
    }

    public void clear(){
        mSelectImages = null;
        mFolderAllImages = null;
    }
}
