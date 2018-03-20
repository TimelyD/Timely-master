package com.tg.tgt.moment.holder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.tgt.R;

/**
 *
 * @author yiyang
 */
public class AdHolder extends BaseHolder {

    public ImageView mIvUrl;
    public TextView mTvUrl;
    public View mRootView;

    public AdHolder(View view) {
        super(view);
    }

    @Override
    protected void initSubView(ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_urlbody);
        View subView = viewStub.inflate();
        mRootView = subView.findViewById(R.id.layout_ad_body);
        mIvUrl = (ImageView) subView.findViewById(R.id.urlImageIv);
        mTvUrl = (TextView) subView.findViewById(R.id.urlContentTv);
    }
}
