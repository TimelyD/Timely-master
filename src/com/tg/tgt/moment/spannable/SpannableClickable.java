package com.tg.tgt.moment.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.tg.tgt.App;
import com.tg.tgt.R;


/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:32
 */
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private float textSize;
    /**
     * text颜色
     */
    private int textColor ;

    public SpannableClickable() {
        this(App.applicationContext.getResources().getColor(R.color.praise_item));
    }

    public SpannableClickable(int textColor){
        this.textColor = textColor;
        this.textSize = App.applicationContext.getResources().getDimension(R.dimen.common_14sp);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setTextSize(textSize);
        ds.setColor(textColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
