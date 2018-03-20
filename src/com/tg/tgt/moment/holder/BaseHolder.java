package com.tg.tgt.moment.holder;

import android.view.View;
import android.view.ViewStub;

import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.tgt.R;
import com.tg.tgt.moment.widgets.SnsPopupWindow;

/**
 *
 * @author yiyang
 */
public abstract class BaseHolder extends BaseViewHolder {

    public SnsPopupWindow snsPopupWindow;

    public BaseHolder(View view) {
        super(view);
        ViewStub viewStub = (ViewStub) getView(R.id.viewStub);
        initSubView(viewStub);
    }

    protected abstract void initSubView( ViewStub viewStub);


}
