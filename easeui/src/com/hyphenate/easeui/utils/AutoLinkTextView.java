package com.hyphenate.easeui.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by DELL on 2018/6/19.
 */

public class AutoLinkTextView extends TextView {
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public AutoLinkTextView(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);


        SpannableString span = new SpannableString(getText());
        ClickableSpan[] links = span.getSpans(getSelectionStart(),
                getSelectionEnd(), ClickableSpan.class);
        if (links.length != 0) {
            return true;
        }
        return false;

    }
}
