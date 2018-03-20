package com.tg.tgt.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 *
 * @author yiyang
 */
public class SquareFrameLayout extends FrameLayout {

    private float mRatio = 0f;
    private boolean mDependWidth = false;

    public SquareFrameLayout(Context context) {
        this(context, null);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取自定义的属性
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareFrameLayout);
//
//        mRatio = typedArray.getFloat(R.styleable.SquareFrameLayout_ratio, 1);
//        mDependWidth = typedArray.getBoolean(R.styleable.SquareFrameLayout_dependWidth, mDependWidth);

//        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        if(width > height){
//            setMeasuredDimension(getDefaultSize(0, heightMeasureSpec), getDefaultSize(0, heightMeasureSpec));
//        }else {
//            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, widthMeasureSpec));
//        }
//
//
//        // Children are just made to fill our space.
//        int childWidthSize = getMeasuredWidth();
//        int childHeightSize = getMeasuredHeight();
//        //高度和宽度一样
//        if(mRatio!=0f){
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
//            heightMeasureSpec = (int) (widthMeasureSpec/mRatio + .5f);
//        }else {
//            heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到RatioLayout在xml中定义的Layout_width的模式
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        //得到RatioLayout在xml中定义的Layout_height的模式
        int selfHeightMode = MeasureSpec.getMode(heightMeasureSpec);


        if (selfHeightMode == MeasureSpec.EXACTLY && !mDependWidth) {//已知了高度
            //得到自身的高度
            int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

            //动态计算应有的宽度
            int selfWidth;
            if (mRatio != 0f) {
                selfWidth = (int) (mRatio * selfHeight + .5f);
            } else {
                selfWidth = selfHeight;
            }


            //计算孩子应有高度和宽度

            int childWith = selfWidth - getPaddingLeft() - getPaddingRight();
            int childHeigh = selfHeight - getPaddingTop() - getPaddingBottom();

            //请求孩子测绘自身,按照指定MeasureSpec(mode+size)
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWith, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeigh, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);

            setMeasuredDimension(selfWidth, selfHeight);

        } else if (selfWidthMode == MeasureSpec.EXACTLY) {//已知宽度
            //得到RatioLayout测绘的宽度
            int selfWidth;
            selfWidth = MeasureSpec.getSize(widthMeasureSpec);

            //动态计算RatioLayout自身应有的高度
            int selfHeight;
            if (mRatio != 0f) {
                selfHeight = (int) (selfWidth / mRatio + .5f);
            } else {
                selfHeight = selfWidth;
            }

            //计算孩子应有高度和宽度

            int childWith = selfWidth - getPaddingLeft() - getPaddingRight();
            int childHeigh = selfHeight - getPaddingTop() - getPaddingBottom();

            //请求孩子测绘自身,按照指定MeasureSpec(mode+size)
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWith, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeigh, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);

            //保存测绘结果
            setMeasuredDimension(selfWidth, selfHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
