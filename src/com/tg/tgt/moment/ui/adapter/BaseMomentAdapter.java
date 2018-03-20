package com.tg.tgt.moment.ui.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.tgt.R;
import com.tg.tgt.moment.bean.CircleItem;
import com.tg.tgt.moment.contract.MomentContract;
import com.tg.tgt.moment.holder.AdHolder;
import com.tg.tgt.moment.holder.BaseHolder;
import com.tg.tgt.moment.holder.ImageViewHolder;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class BaseMomentAdapter extends BaseMultiItemQuickAdapter<CircleItem, BaseViewHolder> {
    public static final int HEADVIEW_SIZE = 1;
    protected int res;
    public BaseMomentAdapter(List<CircleItem> data, @LayoutRes int res) {
        super(data);
        this.res = res;
        addItemType(CircleItem.TYPE_IMG, res);
        addItemType(CircleItem.TYPE_URL, res);
        addItemType(CircleItem.TYPE_AD, res);
    }

    protected MomentContract.Presenter mPresenter;
    public void setPresenter(MomentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder;
        View itemView = getItemView(res, parent);
        switch (viewType) {
            case CircleItem.TYPE_IMG:
                holder = new ImageViewHolder(itemView);
                break;
            case CircleItem.TYPE_AD:
                case CircleItem.TYPE_NEWS:
                holder = new AdHolder(itemView);

                break;

            default:
                holder = new BaseHolder(itemView) {
                    @Override
                    protected void initSubView(ViewStub viewStub) {
                        if(viewStub == null){
                            throw new IllegalArgumentException("viewStub is null...");
                        }
                        viewStub.setLayoutResource(R.layout.viewstub_unsupport);
                        View subView = viewStub.inflate();
                    }
                };
                break;
        }
        return holder;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = super.onCreateViewHolder(parent, viewType);
        if(viewType != BaseQuickAdapter.HEADER_VIEW){
            baseViewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
        return baseViewHolder;
    }

    @Override
    protected void convert(BaseViewHolder helper, CircleItem item) {

    }
}
