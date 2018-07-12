package com.tg.tgt.moment.presenter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.moment.BasePresenter;
import com.tg.tgt.moment.IModel;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CommentConfig;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.bean.PhotoInfo;
import com.tg.tgt.moment.contract.MomentContract;
import com.tg.tgt.moment.ui.activity.MomentAct;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 *
 * @author yiyang
 */
public class MomentPresenter extends BasePresenter<IModel, MomentContract.View> implements MomentContract.Presenter{


    public static final int pageSize = 10;
    private final String mUserId;
    private List<CircleItem> mData = new ArrayList<>();

    public MomentPresenter(MomentContract.View view, String userId) {
        super(view);
        mUserId = userId;
    }

    public MomentPresenter(MomentContract.View view, CircleItem item) {
        super(view);
        mUserId = null;
        mData.add(item);
    }

    @Override
    public void loadData(final boolean loadMore) {
        Observable<HttpResult<List<CircleItem>>> observable;
        if(TextUtils.isEmpty(mUserId)) {
            observable = ApiManger2.getApiService().showFriendMoments(loadMore ? mData.size()
                    > 0 ? mData.get(mData.size() - 1).getId() : null : null, pageSize);
        }else {
            observable = ApiManger2.getApiService().showUserMoments(mUserId,loadMore ? mData.size()
                    > 0 ? mData.get(mData.size() - 1).getId() : null : null, pageSize);
        }
        observable
//                .compose(RxUtils.<List<CircleItem>>applySchedulers())
                /*.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Glide.get(App.applicationContext).clearDiskCache();
                    }
                })*/
                .compose(this.<HttpResult<List<CircleItem>>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<List<CircleItem>>() {
                    @Override
                    protected void onSuccess(List<CircleItem> friendMomentModels) {
                        boolean hasMore = false;
                        if(friendMomentModels==null || friendMomentModels.size()==0){
                            mView.setData(true, hasMore, false, null);
                            return;
                        }
                        for (int i = 0; i < friendMomentModels.size(); i++) {
                            CircleItem circleItem = friendMomentModels.get(i);
                            List<PhotoInfo> pictureEntities = circleItem.getPictureEntities();
                            if(pictureEntities!=null && pictureEntities.size() == 1){
                                pictureEntities.get(0).w = circleItem.getWidth();
                                pictureEntities.get(0).h = circleItem.getHeight();
                            }
                        }
                        if(loadMore){
                            mData.addAll(friendMomentModels);
                        }else {
                            mData.clear();
                            mData.addAll(friendMomentModels);
                        }
                        if(friendMomentModels.size() == pageSize){
                            hasMore = true;
                        }
//                        List<CircleItem> datas = DatasUtil.createCircleDatas();
                        mView.setData(true, loadMore, hasMore, mData);
                        MomentAct.mineSize = mData.size();
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        mView.setData(false, loadMore, false, null);
                    }

                });
    }

    @Override
    public void deleteCircle(final String circleId) {
        ApiManger2.getApiService().deleteMomentMine(circleId)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        mView.setDelete(true,"");
                        mView.update2DeleteCircle(circleId);
                    }

                    @Override
                    public void onFaild(HttpResult<EmptyData> result) {
                        super.onFaild(result);
                        mView.setDelete(false,result.getMsg());
                    }
                });
    }

    @Override
    public void addFavort(final int circlePosition) {
        final CircleItem circleItem = mData.get(circlePosition);
        ApiManger2.getApiService().likeMoments(circleItem.getId(), !circleItem.isLike())
                .compose(this.<HttpResult<FavortItem>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<FavortItem>() {
                    @Override
                    protected void onSuccess(FavortItem item) {
                        circleItem.setLike(!circleItem.isLike());
//                        FavortItem item = DatasUtil.createCurUserFavortItem();
                        if(mView !=null ){
                            if(circleItem.isLike())
                                mView.update2AddFavorite(circlePosition, item);
                            else
                                mView.update2DeleteFavort(circlePosition);
                        }
                    }
                });

    }

    @Override
    public void deleteFavort(final int circlePosition) {
        final CircleItem circleItem = mData.get(circlePosition);
        ApiManger2.getApiService().likeMoments(circleItem.getId(), false)
                .compose(this.<HttpResult<FavortItem>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<FavortItem>() {
                    @Override
                    protected void onSuccess(FavortItem item) {
//                        FavortItem item = DatasUtil.createCurUserFavortItem();
                        if(mView !=null ){
                            mView.update2DeleteFavort(circlePosition);
                        }
                    }
                });
    }

    @Override
    public void addComment(String content, final CommentConfig config) {
        if(config == null){
            return;
        }
        ApiManger2.getApiService()
                .momentsComment(config.id, content, config.parentId)
                .compose(this.<HttpResult<CommentItem>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<CommentItem>() {
                    @Override
                    protected void onSuccess(CommentItem item) {
                        if(mView!=null)
                            mView.update2AddComment(config.circlePosition, item);
                    }
                });
        /*CommentItem newItem = null;
        if (config.commentType == CommentConfig.Type.PUBLIC) {
//            newItem = DatasUtil.createPublicComment(content);
        } else if (config.commentType == CommentConfig.Type.REPLY) {
//            newItem = DatasUtil.createReplyComment(config.replyUser, content);
        }
        if(mView!=null){
            mView.update2AddComment(config.circlePosition, newItem);
        }*/
    }

    @Override
    public void deleteComment(final int circlePosition, String commentId) {
        ApiManger2.getApiService()
                .deleteMomentsComment(commentId)
                .compose(this.<HttpResult<List<CommentItem>>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<List<CommentItem>>() {
                    @Override
                    protected void onSuccess(List<CommentItem> commentItems) {
                        if(mView!=null)
                            mView.update2DeleteComment(circlePosition, commentItems);
                    }
                });
        /*if(mView!=null){
            mView.update2DeleteComment(circlePosition, commentId);
        }*/
    }

    @Override
    public void showEditTextBody(CommentConfig commentConfig) {
        if(mView != null){
            mView.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }
}
