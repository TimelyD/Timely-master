package com.tg.tgt.moment.contract;

import com.tg.tgt.http.IView;
import com.tg.tgt.moment.IPresenter;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CommentConfig;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;

import java.util.List;

/**
 *
 * @author yiyang
 */
public interface MomentContract {

    interface View extends IView {
        void update2DeleteCircle(String circleId);
        void update2AddFavorite(int circlePosition, FavortItem addItem);
        void update2DeleteFavort(int circlePosition);
        void update2AddComment(int circlePosition, CommentItem addItem);
        void update2DeleteComment(int circlePosition, List<CommentItem> commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void setData(boolean isSuccess, boolean loadMore, boolean hasMore, List<CircleItem> datas);
    }
    interface Presenter extends IPresenter {
        void loadData(boolean loadMore);
        void deleteCircle(String circleId);
        void addFavort(final int circlePosition);
        void deleteFavort(int circlePosition);
        public void addComment(String content, CommentConfig config);
        void deleteComment(int circlePosition, String commentId);
        void showEditTextBody(CommentConfig commentConfig);
    }

}
