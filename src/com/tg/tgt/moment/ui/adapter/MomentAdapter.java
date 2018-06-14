package com.tg.tgt.moment.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.TimeUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoBean;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.ActionItem;
import com.tg.tgt.http.IView;
import com.tg.tgt.moment.CommentDialog;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.bean.CommentConfig;
import com.tg.tgt.moment.bean.CommentItem;
import com.tg.tgt.moment.bean.FavortItem;
import com.tg.tgt.moment.bean.PhotoInfo;
import com.tg.tgt.moment.holder.AdHolder;
import com.tg.tgt.moment.holder.ImageViewHolder;
import com.tg.tgt.moment.ui.activity.MomentAct;
import com.tg.tgt.moment.utils.UrlUtils;
import com.tg.tgt.moment.widgets.CommentListView;
import com.tg.tgt.moment.widgets.ExpandTextView;
import com.tg.tgt.moment.widgets.MultiImageView;
import com.tg.tgt.moment.widgets.PraiseListView;
import com.tg.tgt.moment.widgets.SnsPopupWindow;
import com.tg.tgt.ui.NewsDetail;
import com.tg.tgt.utils.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class MomentAdapter extends BaseMomentAdapter {

    public static final int HEADVIEW_SIZE = 1;

    public MomentAdapter(List<CircleItem> data, @LayoutRes int res) {
        super(data, res);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final CircleItem circleItem) {
        final int position = helper.getAdapterPosition() - getHeaderLayoutCount();
        final String circleId = circleItem.getId();
        String name = circleItem.safeGetRemark();
        String headImg = circleItem.getPicture();
        final String content = circleItem.getContent();
        String createTime = circleItem.getCreateTime();
        final List<FavortItem> favortDatas = circleItem.getLikeModels();
        final List<CommentItem> commentsDatas = circleItem.getCommentModels();
        boolean hasFavort = circleItem.hasFavort();
        boolean hasComment = circleItem.hasComment();

        ImageView ivAvatar = (ImageView) helper.getView(R
                .id.headIv);
        if(ivAvatar!=null)
        GlideApp.with(mContext).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable
                .default_avatar).into(ivAvatar);
        try {
            helper.setText(R.id.nameTv, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(res == R.layout.adapter_circle_item_mine){
                helper.setText(R.id.timeTv, TimeUtils.getMomentDateShortString(createTime));
            }else {
                helper.setText(R.id.timeTv, TimeUtils.getMomentDateString(createTime));
            }
        } catch (Exception e) {
            helper.setText(R.id.timeTv, "");
            e.printStackTrace();
            L.e("circleItem"+circleItem);
        }
        if (!TextUtils.isEmpty(content)) {
            ExpandTextView contentTv = (ExpandTextView) helper.getView(R.id.contentTv);
            contentTv.setExpand(circleItem.isExpand());
            contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                @Override
                public void statusChange(boolean isExpand) {
                    circleItem.setExpand(isExpand);
                }
            });
            contentTv.setText(UrlUtils.formatUrlString(content));
        }
        helper.setVisible(R.id.contentTv, !TextUtils.isEmpty(content));

        try {
            helper.setOnClickListener(R.id.headIv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = circleItem.getUsername();
                    String userId = circleItem.getUserId();
    //                Toast.makeText(mContext, userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                    if (R.layout.adapter_circle_item == res)
                        toHomePage(username, userId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.addOnClickListener(R.id.btn_add_like);
        helper.addOnClickListener(R.id.btn_add_comment);
        try {
            helper.setImageResource(R.id.btn_add_like, circleItem.isLike()?R.drawable.add_like_selected:R.drawable.add_like_normal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            View digCommentBody = helper.getView(R.id.digCommentBody);
            if (hasFavort || hasComment) {
                PraiseListView praiseListView = (PraiseListView) helper.getView(R.id.praiseListView);
                if (hasFavort) {//处理点赞列表
                    praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            String username = favortDatas.get(position).getUsername();
                            String userId = String.valueOf(favortDatas.get(position).getUserId());
//                            Toast.makeText(mContext, userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                            toHomePage(username, userId);
                        }
                    });
                    praiseListView.setDatas(favortDatas);
                    praiseListView.setVisibility(View.VISIBLE);
                } else {
                    praiseListView.setVisibility(View.GONE);
                }

                final CommentListView commentList = (CommentListView) helper.getView(R.id.commentList);
                if (hasComment) {//处理评论列表
                    commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int commentPosition) {
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            if (App.getMyUid().equals(commentItem.getUserId())) {
                                //复制或者删除自己的评论
                                CommentDialog dialog = new CommentDialog(commentList.getContext(), mPresenter,
                                        commentItem, position);
                                dialog.show();
                            } else {//回复别人的评论
                                if (mPresenter != null) {
                                    CommentConfig config = new CommentConfig();
                                    config.circlePosition = position;
                                    config.commentPosition = commentPosition;
                                    config.commentType = CommentConfig.Type.REPLY;
                                    config.id = circleId;
                                    config.parentId = commentItem.getId();
                                    config.hint = String.format(mContext.getString(R.string.reply_to_one), commentItem.safeGetRemark());
                                    mPresenter.showEditTextBody(config);
                                }
                            }
                        }
                    });
                    commentList.setOnUserClickListener(new CommentListView.OnUserClickListener() {
                        @Override
                        public void onUserClick(String username, String userId) {
                            toHomePage(username, userId);
                        }
                    });
                    commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int commentPosition) {
                            //长按进行复制或者删除
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            CommentDialog dialog = new CommentDialog(commentList.getContext(), mPresenter,
                                    commentItem, position);
                            dialog.show();
                        }
                    });
                    commentList.setDatas(commentsDatas);
                    commentList.setVisibility(View.VISIBLE);
                } else {
                    commentList.setVisibility(View.GONE);
                }

                digCommentBody.setVisibility(View.VISIBLE);
                helper.setVisible(R.id.divider, true);
            } else {
                digCommentBody.setVisibility(View.GONE);
                helper.setVisible(R.id.divider, false);
            }
//            helper.setVisible(R.id.lin_dig, hasFavort && hasComment);


            /*helper.setOnClickListener(R.id.snsBtn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseHolder holder = (BaseHolder) helper;
                    if (holder.snsPopupWindow == null) {
                        holder.snsPopupWindow = new SnsPopupWindow(mContext);
                    }
                    //判断是否已点赞
                    if (circleItem.isLike()) {
                        holder.snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
                    } else {
                        holder.snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
                    }
                    holder.snsPopupWindow.update();
                    holder.snsPopupWindow.setmItemClickListener(new PopupItemClickListener(position, circleItem));
                    holder.snsPopupWindow.showPopupWindow(v);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (circleItem.getItemType()) {
            case CircleItem.TYPE_IMG:
                final ImageViewHolder holder = (ImageViewHolder) helper;
                final List<PhotoInfo> photos = circleItem.getPictureEntities();
                if (photos != null && photos.size() > 0) {
                    holder.multiImageView.setVisibility(View.VISIBLE);
                    holder.multiImageView.setList(photos);
                    holder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            List<MediaBean> beans = new ArrayList<MediaBean>();
                            for (int i = 0; i < photos.size(); i++) {
                                PhotoBean e = new PhotoBean(photos.get(i).picture);
                                e.setThumbnail(photos.get(i).thumbnail);
                                beans.add(e);
                            }
                            SelectObserable.getInstance().setFolderAllImages(beans);
                            mStartView = holder.multiImageView;
                            PreviewImageActivity.startPreviewPhotoActivity(mContext, position, view,false);
                        }
                    });
                } else {
                    holder.multiImageView.setVisibility(View.GONE);
                }

                break;
            case CircleItem.TYPE_AD:
                AdHolder adHolder = (AdHolder) helper;
                GlideApp.with(mContext).load(circleItem.getUrlPicture()).placeholder(R.drawable.default_img).into(adHolder.mIvUrl);
                adHolder.mTvUrl.setText(circleItem.getUrlTitle());
                adHolder.mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (circleItem.getUrl() == null)
                            return;
                        Uri uri = Uri.parse(circleItem.getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case CircleItem.TYPE_NEWS:
                AdHolder newsHolder = (AdHolder) helper;
                GlideApp.with(mContext).load(circleItem.getUrlPicture()).placeholder(R.drawable.default_img).into(newsHolder.mIvUrl);
                newsHolder.mTvUrl.setText(circleItem.getUrlTitle());
                newsHolder.mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (circleItem.getUrl() == null)
                            return;
                        mContext.startActivity(new Intent(mContext, NewsDetail.class).putExtra(Constant.NEWS, circleItem.getUrl()));
                    }
                });
                break;
            default:
                break;
        }
    }

    private void toHomePage(final String username, final String userId) {
        EaseUser userInfo = EaseUserUtils.getUserInfo(username);
        if(userInfo.getChatidstate() == null){
            CodeUtils.fetchUser((IView) mContext, userId,false, new Consumer<EaseUser>() {
                @Override
                public void accept(@NonNull EaseUser easeUser) throws Exception {
                    mContext.startActivity(new Intent(mContext, MomentAct.class)
                            .putExtra(Constant.USERNAME, username)
                            .putExtra(Constant.USER_ID, userId)
                            .putExtra("signature",easeUser.getChatidstate())
                            .putExtra(Constant.IS_MINE_HOME_PAGE, true));
                }
            });
        }else {
            mContext.startActivity(new Intent(mContext, MomentAct.class).putExtra(Constant.USERNAME, username).putExtra
                    (Constant.USER_ID, userId).putExtra(Constant.IS_MINE_HOME_PAGE, true));
        }
    }

    //开始共享动画的view
    public MultiImageView mStartView;

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private CircleItem mCircleItem;

        public PopupItemClickListener(int circlePosition, CircleItem circleItem) {
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (mPresenter != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            mPresenter.addFavort(mCirclePosition);
                        } else {//取消点赞
                            mPresenter.deleteFavort(mCirclePosition);
                        }
                    }
                    break;
                case 1://发布评论
                    if (mPresenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        config.id = mCircleItem.getId();
                        config.hint = "";
                        mPresenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
