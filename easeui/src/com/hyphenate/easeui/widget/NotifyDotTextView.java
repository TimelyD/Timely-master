package com.hyphenate.easeui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author yiyang
 */
public class NotifyDotTextView extends TextView {
    public NotifyDotTextView(Context context) {
        super(context);
    }

    public NotifyDotTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyDotTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST){
            width = (int) (getPaint().measureText(getText(), 0, getText().length())+.5f);
            width += getPaddingLeft()+getPaddingRight();
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            height = (int) (fontMetrics.descent - fontMetrics.ascent+.5f);
            height += getPaddingTop()+getPaddingBottom();
        }
        if(height > width){
            width = height;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        RectF rect = new RectF();
////        getPaint().getTextBounds(getText().toString().toCharArray(), 0, getText().length(), rect);
//        rect.left = 0;
//        rect.top = 0;
//        rect.right = getPaint().measureText(getText(), 0, getText().length());
//        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
//        rect.bottom = fontMetrics.descent - fontMetrics.ascent;
//        Log.d("NotifyDotTextView", getText().toString());
//        Log.d("NotifyDotTextView", "rect:" + rect);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        int width = getWidth();
        int height = getHeight();
//        if(width<height){
//            width = height;
//        }
//        canvas.drawOval(new RectF(rect), paint);
        RectF rectF = new RectF(0, 0, width, height);
        float radius = rectF.height()/2;
        canvas.drawRoundRect(rectF, radius, radius, paint);
        super.onDraw(canvas);
    }
}
