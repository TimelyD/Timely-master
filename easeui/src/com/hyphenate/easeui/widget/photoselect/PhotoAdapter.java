package com.hyphenate.easeui.widget.photoselect;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.PhotoUtils;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author yiyang
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {

    private List<MediaBean> mPhotos;
    private final int mHeight;
    private final int mWidth;
    //显示的图的最大宽高比
    private final int ratio = 2;
    private final Context mContext;
    //最大选择张数
    public static int maxCount = 9;
    /**
     * 标注是否是单选图片模式
     */
    private boolean mIsSelectSingleImge = false;

    public List<MediaBean> getSelectlist() {
        return mSelectlist;
    }

    /**
     * 已选图片列表
     */
    private List<MediaBean> mSelectlist;

    public PhotoAdapter(Context context, List<MediaBean> photos, OnItemClickLisener onItemClickLisener) {
        this.mPhotos = photos;
        mContext = context;
//        mHeight = (int) (mContext.getResources().getDisplayMetrics().density * 145 + .5f);
        mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.chat_menu_height)- mContext.getResources().getDimensionPixelSize(R.dimen.common_40dp);
        mWidth = mHeight * ratio;
        mOnItemClickLisener = onItemClickLisener;
        mSelectlist = new ArrayList<>();
    }

    public void setData(List<MediaBean> beans, RecyclerView recyclerView) {
        if (beans == mPhotos)
            return;
        this.mPhotos = beans;
        mSelectlist.clear();
        SelectObserable.getInstance().setChange();
//        notifyDataSetChanged();
        recyclerView.setAdapter(this);
    }

    public List<MediaBean> getData(){
        return mPhotos;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iv;
        private final TextView mSelectTv;
        private final TextView mSelectView;
        private final TextView videoDurationTv;
        private final ImageView isVideo;

        //selecttextview距离右边距离
//        private int mDw = PhoneUtil.dp2px(mContext, 28);

        MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.photo_iv);
            isVideo = (ImageView) itemView.findViewById(R.id.is_video_iv);
            View view = itemView.findViewById(R.id.photo_layout);
            view.setOnClickListener(this);
            mSelectTv = (TextView) itemView.findViewById(R.id.select_tv);
            mSelectView = (TextView) itemView.findViewById(R.id.select_tv_v);
            videoDurationTv = (TextView) itemView.findViewById(R.id.video_duration_tv);

            /*点击监听*/
            setSelectOnClickListener(mSelectView, this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickLisener != null)
                mOnItemClickLisener.onItemClick(iv, getAdapterPosition(), mPhotos.get(getAdapterPosition()));
        }

    }

    private OnItemClickLisener mOnItemClickLisener;

    public interface OnItemClickLisener {
        void onItemClick(ImageView v, int position, MediaBean bean);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_photo_select, null));
    }

    private static final int loadingRes = R.color.load_pink;

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MediaBean bean = mPhotos.get(position);
        bean.position = position;
        if (bean instanceof VideoBean) {
            ViewGroup.LayoutParams lp = holder.iv.getLayoutParams();
            lp.width = mHeight;
            lp.height = mHeight;
            //也可以通过MediaMetadataRetriever获取bitmap
            GlideApp.with(mContext).load(bean.getPath()).placeholder(loadingRes).centerCrop().into(holder.iv);
            holder.isVideo.setVisibility(View.VISIBLE);
            holder.videoDurationTv.setVisibility(View.VISIBLE);
            String time = DateUtils.toTime((int) ((VideoBean) bean).getLength());
            holder.videoDurationTv.setText(time);
        } else {
            holder.videoDurationTv.setVisibility(View.GONE);
            holder.isVideo.setVisibility(View.GONE);
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;//只读边,不读内容
            BitmapFactory.decodeFile(bean.getPath(), newOpts);
            newOpts.inJustDecodeBounds = false;
                int expectW;
                int expectH;
            if(PhotoUtils.isPicRotate(bean.getPath())){
                expectW = newOpts.outHeight;
                expectH = newOpts.outWidth;
            }else {
                expectW = newOpts.outWidth;
                expectH = newOpts.outHeight;
            }
            /*try {
                if (height / width >= 2) {
                    GlideApp.with(mContext).load(bean.getPath()).placeholder(R.drawable.default_img).centerCrop().override
                            (mHeight, mHeight).into(holder.iv);
                } else if (width / height >= ratio) {
                    GlideApp.with(mContext).load(bean.getPath()).placeholder(R.drawable.default_img).centerCrop().override
                            (mWidth, mHeight).into(holder.iv);
                } else {
                    GlideApp.with(mContext).load(bean.getPath()).placeholder(R.drawable.default_img).override(mWidth, mHeight).into(holder.iv);
                }
            } catch (Exception e) {
                //被删除了，height、width为0
                GlideApp.with(mContext).load(R.drawable.default_img).override
                        (mHeight, mHeight).into(holder.iv);
                e.printStackTrace();
            }*/
            int actualW = 0;
            int actualH = 0;
            float scale = ((float) expectH)/((float) expectW);
            if(scale >=ratio){
                actualW = (int) ((float)mHeight/(float) ratio+.5f);
                actualH = mHeight;
            }else {
                actualH = mHeight;
                actualW = (int) (mHeight / scale + .5f);
            }
            ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
            layoutParams.width = actualW>mWidth?mWidth:actualW;
            layoutParams.height = actualH;
            GlideApp.with(mContext).load(bean.getPath()).placeholder(loadingRes).centerCrop().into(holder.iv);
        }

        setSelect(holder, bean);

//        setSelectOnClickListener(holder.mSelectView, bean, holder.getAdapterPosition());
    }

    /**
     * 选择按钮更新
     */
    private void setSelect(MyViewHolder viewHolder, MediaBean imageBean) {
        if (mIsSelectSingleImge) { //单选模式，不显示选择按钮
            viewHolder.mSelectTv.setVisibility(View.INVISIBLE);
        } else {
            if (mSelectlist.contains(imageBean)) {  //当已选列表里包括当前item时，选择状态为已选，并显示在选择列表里的位置
                viewHolder.mSelectTv.setEnabled(true);
                viewHolder.mSelectTv.setText(String.valueOf(imageBean.selectPosition));
//                viewHolder.forgroundIv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mSelectTv.setEnabled(false);
                viewHolder.mSelectTv.setText("");
//                viewHolder.forgroundIv.setVisibility(View.GONE);
            }
        }
    }

    private void setSelectOnClickListener(View view, final MyViewHolder holder/*, final MediaBean bean, final int
    position*/) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MediaBean bean = mPhotos.get(position);

                if (mSelectlist.contains(bean)) { //点击的item为已选过的图片时，删除
                    mSelectlist.remove(bean);
                    subSelectPosition();
                } else { //不在选择列表里，添加
                    if (mSelectlist.size() >= maxCount) {
                        ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string
                                .publish_select_photo_max, maxCount), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mSelectlist.size() > 0) {
                        if (mSelectlist.get(0) instanceof VideoBean) {
                            if (!(bean instanceof VideoBean)) {
                                ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string.cannot_select_photo_and_video), Toast
                                        .LENGTH_SHORT).show();
                            } else {
                                ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string.only_can_select_one_movie), Toast
                                        .LENGTH_SHORT).show();
                            }
                            return;
                        } else {
                            if (bean instanceof VideoBean) {
                                ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string.cannot_select_photo_and_video), Toast
                                        .LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    mSelectlist.add(bean);
                    bean.selectPosition = mSelectlist.size();
                }

                //通知点击项发生了改变
                notifyItemChanged(position);
                SelectObserable.getInstance().setChange();
//                if (mOnClickListener != null) { //回调，页面需要展示选择的图片张数
//                    mOnClickListener.onItemClick(v, -1);
//                }
            }
        });
    }


    /**
     * 清空所有选中
     */
    public void clearSelect() {
        while (mSelectlist.size() != 0) {
            MediaBean bean = mSelectlist.get(0);
            mSelectlist.remove(bean);
            notifyItemChanged(bean.position);
        }
        SelectObserable.getInstance().setChange();
    }


    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = mSelectlist.size(); index < len; index++) {
            MediaBean folderBean = mSelectlist.get(index);
            folderBean.selectPosition = index + 1;
            notifyItemChanged(folderBean.position);
        }

    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

}