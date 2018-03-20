package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.decoration.DividerGridItemDecoration;
import com.chad.library.adapter.base.decoration.SimpleDividerDecoration;
import com.hyphenate.easeui.utils.PhoneUtil;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoFolder;
import com.hyphenate.easeui.utils.photo.PhotoUtils;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;
import com.tg.tgt.R;
import com.tg.tgt.http.RxUtils;
import com.tg.tgt.utils.ToastUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.hyphenate.easeui.widget.photoselect.PhotoAdapter.maxCount;

/**
 *
 * @author yiyang
 */
public class AlbumsAct extends BaseActivity implements java.util.Observer {

    public static final String FOLDER = "folder";

    public static final int REQUEST_SELECT = 123;
    /**
     * 相册列数
     */
    private final int columnCout = 3;
    private RecyclerView mRecyclerView;
    private EaseTitleBar mTitleBar;
    private BaseQuickAdapter<MediaBean, BaseViewHolder> mAdapter;

    /**
     * 是否正在显示文件夹
     */
    private boolean isFolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_albums);
        setTitleBarLeftBack();
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        String folderPath = getIntent().getStringExtra(FOLDER);
        if (TextUtils.isEmpty(folderPath)) {
            isFolder = true;
            showFloders();
        } else {
            isFolder = false;
            showFolderPhoto(folderPath);
        }

        SelectObserable.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SelectObserable.getInstance().deleteObserver(this);
    }

    private void showFolderPhoto(final String folderPath) {
        Observable.create(new ObservableOnSubscribe<List<MediaBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MediaBean>> e) throws Exception {
                List<MediaBean> list = PhotoUtils.queryGalleryPicture(mContext, folderPath, mSelectlist);
                e.onNext(list);
                e.onComplete();
            }
        }).compose(RxUtils.<List<MediaBean>>applySchedulers())
                .subscribe(new Observer<List<MediaBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addRxDestroy(d);
                        showProgress(R.string.loading_msg);
                    }

                    @Override
                    public void onNext(@NonNull final List<MediaBean> list) {
                        mTitleBar.setTitle(folderPath.substring(folderPath.lastIndexOf(File.separator) + 1) + "(" +
                                list.size() + ")");
                        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, columnCout));
                        mAdapter = new PhotosAdapter(R.layout
                                .item_photo_select, list);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(PhoneUtil.dp2px(mContext, 2),
                                ContextCompat.getColor(mContext, R.color.white)));

                        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                                SelectObserable.getInstance().setFolderAllImages(list);
//                                PreviewImageActivity.startPreviewPhotoActivity(mActivity, position);
                                PreviewImageActivity.cacheMediaBean = mSelectlist;
                                PreviewImageActivity.startPreviewPhotoActivityForResult(mActivity, list, position, true, REQUEST_SELECT);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showToast(getApplicationContext(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dismissProgress();
                    }
                });


    }

    /**
     * 已选图片列表
     */
    private static List<MediaBean> mSelectlist;

    public void confirm(View view) {
        SelectObserable.getInstance().setSelectImages(mSelectlist);
        setResult(RESULT_OK);
        finish();

//        setResult(RESULT_OK, new Intent().putExtra("d", mSelectlist.toArray(new MediaBean[mSelectlist.size()])));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == REQUEST_SHOW_FOLDER_PHOTO || requestCode == REQUEST_SELECT) {
                setResult(RESULT_OK);
                finish();
            }
        else
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void update(java.util.Observable observable, Object data) {
        if (data != null) {
            int data1 = (int) data;
            if (mAdapter != null)
//                mAdapter.notifyItemChanged(data1);
            mAdapter.notifyDataSetChanged();
        }
    }

    class PhotosAdapter extends BaseQuickAdapter<MediaBean,
            BaseViewHolder> {
        private List<MediaBean> mPhotos;

        public PhotosAdapter(@LayoutRes int layoutResId, @Nullable List<MediaBean> data) {
            super(layoutResId, data);
            this.mPhotos = data;
        }

        @Override
        protected void convert(BaseViewHolder helper, MediaBean item) {
            item.position = helper.getAdapterPosition();
            ImageView imageView = (ImageView) helper.getView(R.id.photo_iv);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (mRecyclerView.getWidth() - PhoneUtil.dp2px(mContext, 2)) / columnCout;
            Glide.with(mActivity).load(item.getPath()).apply(new RequestOptions().centerCrop()).into(imageView);
            setSelectOnClickListener(helper.getView(R.id.select_tv_v), helper);

            if (item instanceof VideoBean) {

            } else {
                helper.getView(R.id.video_duration_tv).setVisibility(View.GONE);
                helper.getView(R.id.is_video_iv).setVisibility(View.GONE);
            }
            setSelect(helper, item);
        }


        /**
         * 选择按钮更新
         */
        private void setSelect(BaseViewHolder viewHolder, MediaBean imageBean) {
//            if (mIsSelectSingleImge) { //单选模式，不显示选择按钮
//                viewHolder.mSelectTv.setVisibility(View.INVISIBLE);
//            } else {
            if (mSelectlist.contains(imageBean)) {  //当已选列表里包括当前item时，选择状态为已选，并显示在选择列表里的位置
                ((TextView) viewHolder.getView(R.id.select_tv)).setEnabled(true);
                ((TextView) viewHolder.getView(R.id.select_tv)).setText(String.valueOf(imageBean.selectPosition));
//                viewHolder.forgroundIv.setVisibility(View.VISIBLE);
            } else {
                ((TextView) viewHolder.getView(R.id.select_tv)).setEnabled(false);
                ((TextView) viewHolder.getView(R.id.select_tv)).setText("");
//                viewHolder.forgroundIv.setVisibility(View.GONE);
            }
//            }
        }

        private void setSelectOnClickListener(View view, final RecyclerView.ViewHolder holder/*, final MediaBean
        bean, final int
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
                                    ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string
                                            .cannot_select_photo_and_video), Toast
                                            .LENGTH_SHORT).show();
                                } else {
                                    ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string
                                            .only_can_select_one_movie), Toast
                                            .LENGTH_SHORT).show();
                                }
                                return;
                            } else {
                                if (bean instanceof VideoBean) {
                                    ToastUtils.makeText(mContext.getApplicationContext(), mContext.getString(R.string
                                            .cannot_select_photo_and_video), Toast
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
    }
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
    private void showFloders() {
        try {
            mSelectlist = deepCopy(SelectObserable.getInstance().getSelectImages());
        } catch (Exception e) {
            e.printStackTrace();
            mSelectlist = new ArrayList<>();
        }
        Observable.create(new ObservableOnSubscribe<List<PhotoFolder>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PhotoFolder>> e) throws Exception {
                List<PhotoFolder> photoFolders = PhotoUtils.loadLocalFolderContainsImage(mContext);
                e.onNext(photoFolders);
                e.onComplete();
            }
        }).compose(RxUtils.<List<PhotoFolder>>applySchedulers())
                .subscribe(new Observer<List<PhotoFolder>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addRxDestroy(d);
                        showProgress(R.string.loading_msg);
                    }

                    @Override
                    public void onNext(@NonNull final List<PhotoFolder> photoFolders) {
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager
                                .VERTICAL, false));
                        BaseQuickAdapter<PhotoFolder, BaseViewHolder> adapter = new BaseQuickAdapter<PhotoFolder,
                                BaseViewHolder>(R
                                .layout.item_floder, photoFolders) {
                            @Override
                            protected void convert(BaseViewHolder helper, PhotoFolder item) {
                                helper.setText(R.id.tv_name, item.getName() + "(" + item.getNum() + ")");
                                Glide.with(mActivity).load(item.getPath()).apply(new RequestOptions().centerCrop()).into((ImageView) helper
                                        .getView(R.id
                                                .iv_photo));
                            }
                        };
                        mRecyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                PhotoFolder folder = photoFolders.get(position);
                                startActivityForResult(new Intent(mContext, AlbumsAct.class).putExtra(FOLDER, folder
                                        .getDirPath()), REQUEST_SHOW_FOLDER_PHOTO);
                            }
                        });
                        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(mContext));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showToast(getApplicationContext(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dismissProgress();
                    }
                });

    }

    private final int REQUEST_SHOW_FOLDER_PHOTO = 1;
}
