package com.hyphenate.easeui.widget.photoselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.decoration.DividerGridItemDecoration;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.PhoneUtil;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoUtils;
import com.hyphenate.easeui.utils.photo.VideoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author yiyang
 */
public class PhotoSelectMenu extends LinearLayout implements Observer {
    public static final String DATA_CHANGE = "com.tg.tgt.data_change";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OnClickListener mAlbumClickListener;
    private PhotoAdapter mAdapter;
    private TextView mTypeTv;
    private List<MediaBean> mPhotos;
    private List<MediaBean> mVideos;
    private View mContaniner;
    private Button mSendBtn;
    private ArrayList<MediaBean> mAllMedia;

    //照片加载第几页
    private int mPhotoPageIndex = 1;
    private int mVideoPageIndex = 1;

    //每次加载个数
    private int mPageSize = 50;
    //    private getMediaInfoTask mGetMediaInfoTask;

    public PhotoSelectMenu(Context context) {
        super(context);
        init(context);
    }

    public PhotoSelectMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotoSelectMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_photo_select_menu, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.photo_rv);
        findViewById(R.id.album_tv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlbumClickListener != null) {
                    mAlbumClickListener.onClick(v);
                }
            }
        });

        mSendBtn = (Button) findViewById(R.id.send_btn);
        mSendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoMenuListener != null) {
                    if (mAdapter.getSelectlist().size() == 0) {
                        ToastUtils.showToast(mContext.getApplicationContext(), R.string.photo_no_select);
                        return;
                    }
                    mOnPhotoMenuListener.onPhotoSend(mAdapter.getSelectlist().toArray(new MediaBean[mAdapter
                            .getSelectlist().size()]));
                }
            }
        });

        mTypeTv = (TextView) findViewById(R.id.type_tv);
        mTypeTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeWindow();
            }
        });

        mContaniner = findViewById(R.id.ctrl_container);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isDataSetChanged) {
                    mAdapter.notifyDataSetChanged();
                    isDataSetChanged = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                isNeedLoadData(lastVisibleItemPosition);
            }

        });
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(PhoneUtil.dp2px(mContext, 2), ContextCompat.getColor(mContext, R.color.white)));


        new Thread(new Runnable() {
            @Override
            public void run() {


                mPhotos = PhotoUtils.getPhotos(mContext, mPageSize, mPhotoPageIndex).get("all").getPhotoList();
                mVideos = PhotoUtils.getVideo(mContext, mPageSize, mVideoPageIndex).get("all").getPhotoList();

                mAllMedia = new ArrayList<>(mPhotos);
                mAllMedia.addAll(mVideos);
                Collections.sort(mAllMedia);

                mAdapter = new PhotoAdapter(mContext, mAllMedia, new PhotoAdapter.OnItemClickLisener() {
                    @Override
                    public void onItemClick(ImageView v, int position, MediaBean bean) {
                        Log.i("sendPhoto", "pos:" + position + "\npath:" + bean.getPath());
                        if (bean instanceof VideoBean) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(bean.getPath())),
                                    "video/mp4");
                            mContext.startActivity(intent);
                            return;
                        }
//                        if (mOnPhotoMenuListener != null)
//                            mOnPhotoMenuListener.onPhotoSend(new MediaBean[]{bean});

                        SelectObserable.getInstance().setFolderAllImages(mPhotos);
                        SelectObserable.getInstance().setSelectImages(mAdapter.getSelectlist());
                        PreviewImageActivity.startPreviewPhotoActivityForResult(((FragmentActivity) mContext).getSupportFragmentManager().findFragmentByTag("chat"), mPhotos.indexOf(bean), true, EaseChatFragment.REQUEST_CODE_PREVIEW);
                    }
                });
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();

//        mGetMediaInfoTask = new getMediaInfoTask();
//        mGetMediaInfoTask.execute();
    }

    private void isNeedLoadData(int lastVisibleItemPosition) {
        if (isLoading)
            return;

        if (type == 1) {
            //当当前页数最大值等于目前值时，说明还有数据。 滑动到一般就开始加载数据
            if (mPhotoPageIndex * mPageSize == mAdapter.getItemCount() && lastVisibleItemPosition > mAdapter
                    .getItemCount() / 2) {
                Log.i("onScrolled:", "lastVisibleItemPosition:" + lastVisibleItemPosition + "\tItemCount:" + mAdapter
                        .getItemCount());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }).start();
            }/* else if (lastVisibleItemPosition == mAdapter.getItemCount()-1) {
                ToastUtils.showToast(mContext.getApplicationContext(), "没有更多了: mPhotoPageIndex = " + mPhotoPageIndex);
            }*/
        } else if (type == 2) {
            //当当前页数最大值等于目前值时，说明还有数据。 滑动到一般就开始加载数据
            if (mVideoPageIndex * mPageSize == mAdapter.getItemCount() && lastVisibleItemPosition > mAdapter
                    .getItemCount() / 2) {
                Log.i("onScrolled:", "lastVisibleItemPosition:" + lastVisibleItemPosition + "\tItemCount:" + mAdapter
                        .getItemCount());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }).start();
            }/* else if (lastVisibleItemPosition == mAdapter.getItemCount()-1) {
                ToastUtils.showToast(mContext.getApplicationContext(), "没有更多了: mVideoPageIndex = " + mVideoPageIndex);
            }*/
        } else {
            if (mVideoPageIndex * mPageSize + mPhotos.size() == mAdapter.getItemCount() || mPhotoPageIndex *
                    mPageSize + mVideos.size() == mAdapter.getItemCount() && lastVisibleItemPosition > mAdapter
                    .getItemCount() / 2) {
                Log.i("onScrolled:", "lastVisibleItemPosition:" + lastVisibleItemPosition + "\tItemCount:" + mAdapter
                        .getItemCount());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }).start();
            }/* else if (lastVisibleItemPosition == mAdapter.getItemCount()-1) {
                ToastUtils.showToast(mContext.getApplicationContext(), "没有更多了: mVideoPageIndex = " + mVideoPageIndex);
            }*/
        }
    }

    private boolean isLoading;
    private boolean isDataSetChanged;

    private synchronized void loadMore() {

        isLoading = true;
        if (type == 1) {
            List<MediaBean> photos = PhotoUtils.getPhotos(mContext, mPageSize, ++mPhotoPageIndex).get("all")
                    .getPhotoList();
            mAllMedia.addAll(photos);
            Collections.sort(mAllMedia);
            mPhotos.addAll(photos);
            Log.i("加载更多:", " mPhotoPageIndex = " + mPhotoPageIndex);
        } else if (type == 2) {
            List<MediaBean> videos = PhotoUtils.getVideo(mContext, mPageSize, ++mVideoPageIndex).get("all")
                    .getPhotoList();
            mVideos.addAll(videos);
            mAllMedia.addAll(videos);
            Collections.sort(mAllMedia);
            Log.i("加载更多:", " mVideoPageIndex = " + mVideoPageIndex);
        } else {
            //TODO 这里还是有排序问题
            List<MediaBean> photos = PhotoUtils.getPhotos(mContext, mPageSize, ++mPhotoPageIndex).get("all")
                    .getPhotoList();
            List<MediaBean> videos = PhotoUtils.getVideo(mContext, mPageSize, ++mVideoPageIndex).get("all")
                    .getPhotoList();
            Log.i("加载更多:", "photos:" + photos + "\nvideos:" + videos);
            ArrayList<MediaBean> medias = new ArrayList<>(photos);
            medias.addAll(videos);
            Collections.sort(videos);
            mPhotos.addAll(photos);
            mVideos.addAll(videos);
            mAllMedia.addAll(medias);
        }

//                mAdapter.notifyDataSetChanged();
        //通知PreviewImageViewAdapter数据发生变化
        mContext.sendBroadcast(new Intent(DATA_CHANGE));
        isDataSetChanged = true;
        isLoading = false;


    }

//    class getMediaInfoTask extends AsyncTask{
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            mPhotos = PhotoUtils.getPhotos(mContext).get("all").getPhotoList();
//            mVideos = PhotoUtils.getVideo(mContext).get("all").getPhotoList();
//
//            SystemClock.sleep(3333);
//
//            mAllMedia = new ArrayList<>(mPhotos);
//            mAllMedia.addAll(mVideos);
//            Collections.sort(mAllMedia);
//            mAdapter = new PhotoAdapter(mContext, mPhotos, new PhotoAdapter.OnItemClickLisener() {
//                @Override
//                public void onItemClick(int position, MediaBean bean) {
//                    Log.i("sendPhoto","pos:"+position+"\npath:"+bean.getPath());
//                    if(mOnPhotoMenuListener != null)
//                        mOnPhotoMenuListener.onPhotoSend(new MediaBean[]{bean});
//                }
//            });
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            mRecyclerView.setAdapter(mAdapter);
//        }
//    }

    /**
     * 0 所有 1 照片 2视频
     */
    private int type = 0;

    private void showTypeWindow() {
        if (mAdapter == null) {
            return;
        }
        View view = View.inflate(mContext, R.layout.pop_select_media_type, null);
        RelativeLayout allLayout = (RelativeLayout) view.findViewById(R.id.all_layout);
        ImageView typeAllIv = (ImageView) view.findViewById(R.id.type_all_iv);
        final TextView allSelectTv = (TextView) view.findViewById(R.id.all_select_tv);
        RelativeLayout photoLayout = (RelativeLayout) view.findViewById(R.id.photo_layout);
        ImageView typePhotoIv = (ImageView) view.findViewById(R.id.type_photo_iv);
        final TextView photoSelectTv = (TextView) view.findViewById(R.id.photo_select_tv);
        RelativeLayout videoLayout = (RelativeLayout) view.findViewById(R.id.video_layout);
        ImageView typeVideoIv = (ImageView) view.findViewById(R.id.type_video_iv);
        final TextView videoSelecctTv = (TextView) view.findViewById(R.id.video_select_tv);

        Glide.with(mContext).load(mAllMedia.size() > 0 ?
//                mAllMedia.get(0) instanceof VideoBean ? ((VideoBean) mAllMedia.get(0)).getThumbPath() : mAllMedia.get
//                        (0).getPath() :
                mAllMedia.get(0).getPath() :
                R.drawable.default_img).apply(new RequestOptions().placeholder(R.drawable.default_img).centerCrop()).into(typeAllIv);
        Glide.with(mContext).load(mPhotos.size() > 0 ? mPhotos.get(0).getPath() : R.drawable.default_img).apply(new RequestOptions().placeholder
                (R.drawable.default_img).centerCrop
                ()).into(typePhotoIv);
        Glide.with(mContext).load(mVideos.size() > 0 ? mVideos.get(0).getPath() : R.drawable
                .default_img).apply(new RequestOptions().placeholder(R.drawable.default_img).centerCrop()).into(typeVideoIv);

        final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable(0));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.mypopwindow_anim_style);
        pop.showAtLocation(mContaniner, Gravity.BOTTOM, 0, mContaniner.getHeight() + PhoneUtil.getNavigationBarHeight
                ((Activity) mContext));
//        pop.showAsDropDown(mContaniner, 0, mContaniner.getHeight());

        if (type == 0) {
            setViewEnable(allSelectTv, photoSelectTv, videoSelecctTv);
        } else if (type == 1) {
            setViewEnable(photoSelectTv, videoSelecctTv, allSelectTv);
        } else {
            setViewEnable(videoSelecctTv, photoSelectTv, allSelectTv);

        }
        allLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeTv.setText(R.string.photo_or_video);
                type = 0;
                setViewEnable(allSelectTv, photoSelectTv, videoSelecctTv);
                pop.dismiss();
                mAdapter.setData(mAllMedia, mRecyclerView);
            }
        });
        photoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeTv.setText(R.string.attach_picture);
                type = 1;
                setViewEnable(photoSelectTv, videoSelecctTv, allSelectTv);
                pop.dismiss();
                mAdapter.setData(mPhotos, mRecyclerView);
            }
        });
        videoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeTv.setText(R.string.attach_video);
                type = 2;
                setViewEnable(videoSelecctTv, photoSelectTv, allSelectTv);
                pop.dismiss();
                mAdapter.setData(mVideos, mRecyclerView);
            }
        });
    }

    public void setViewEnable(View v, View... views) {
        v.setEnabled(true);
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    private OnPhotoMenuListener mOnPhotoMenuListener;

    public void clearSelect() {
        mAdapter.clearSelect();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            int data1 = (int) data;
            //如果是照片和视频列表，传过去的只是所有图片，所以必须将position设置为在AllMedia中的position
            if (type == 0) {
                data1 = mAllMedia.indexOf(mPhotos.get(data1));
            }
//            mAdapter.notifyItemChanged(data1);
            mAdapter.notifyDataSetChanged();
            isNeedLoadData(data1);
        }
        if (mAdapter.getSelectlist().size() == 0) {
            mSendBtn.setText(R.string.button_send);
            mSendBtn.setEnabled(false);
        } else {
            mSendBtn.setEnabled(true);
            mSendBtn.setText(String.format(mContext.getString(R.string.send_with_num), mAdapter.getSelectlist().size
                    ()));
        }
    }

    public interface OnPhotoMenuListener {
        void onPhotoSend(MediaBean[] bean);
    }

    public void setOnPhotoMenuListener(OnPhotoMenuListener listener) {
        mOnPhotoMenuListener = listener;
    }

    public void setAlbumClickListener(OnClickListener listener) {
        mAlbumClickListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SelectObserable.getInstance().clear();
    }
}
