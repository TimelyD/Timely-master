package com.tg.tgt.moment.holder;

import android.view.View;
import android.view.ViewStub;

import com.tg.tgt.R;
import com.tg.tgt.moment.widgets.MultiImageView;

/**
 *
 * @author yiyang
 */
public class ImageViewHolder extends BaseHolder {

    public MultiImageView multiImageView;

    public ImageViewHolder(View view) {
        super(view);
    }

    @Override
    protected void initSubView(ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgbody);
        View subView = viewStub.inflate();
        MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multiImagView);
        if(multiImageView != null){
            this.multiImageView = multiImageView;
        }
    }
}
