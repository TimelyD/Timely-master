package com.tg.tgt.moment.ui;

/**
 * Created by DELL on 2018/6/13.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 实现圆形、圆角，椭圆等自定义图片View。
 * @author zhangqie
 *
 */
public class ZQImageViewRoundOval1 extends ImageView {

    private Paint mPaint;

    private int mWidth;

    private int mHeight;

    private int mRadius;//圆半径

    private RectF mRect;//矩形凹行大小

    private int mRoundRadius;// 圆角大小

    private BitmapShader mBitmapShader;//图形渲染

    private Matrix mMatrix;

    private int mType;// 记录是圆形还是圆角矩形

    public static final int TYPE_CIRCLE = 0;// 圆形
    public static final int TYPE_ROUND = 1;// 圆角矩形
    public static final int TYPE_OVAL = 2;//椭圆形
    public static final int DEFAUT_ROUND_RADIUS = 10;//默认圆角大小

    private Paint paintBorder;

    public ZQImageViewRoundOval1(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public ZQImageViewRoundOval1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public ZQImageViewRoundOval1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        setup();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMatrix = new Matrix();
        mRoundRadius = DEFAUT_ROUND_RADIUS;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果是绘制圆形，则强制宽高大小一致
        if (mType == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (null == getDrawable()) {
            return;
        }
        setBitmapShader();
        if (mType == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
            drawBorder(canvas,this.getWidth(),this.getHeight());
        } else if (mType == TYPE_ROUND) {
            mPaint.setColor(Color.RED);
            canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint);
        }else if(mType == TYPE_OVAL){
            canvas.drawOval(mRect, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mRect = new RectF(0, 0, getWidth(), getHeight());
    }

    /**
     * 设置BitmapShader
     */
    private void setBitmapShader() {
        Drawable drawable = getDrawable();
        if (null == drawable) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        // 将bitmap作为着色器来创建一个BitmapShader
        mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        float scale = 1.0f;
        if (mType == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (mType == TYPE_ROUND || mType == TYPE_OVAL) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);
    }

    private void setup() {
        // init paint
        paintBorder = new Paint();
        setBorderColor(Color.WHITE);
        paintBorder.setAntiAlias(true);
        //   this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);

        this.invalidate();
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * 单位dp转单位px
     */
    public int dpTodx(int dp){

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    public int getType() {
        return mType;
    }
    /**
     * 设置图片类型：圆形、圆角矩形、椭圆形
     * @param mType
     */
    public void setType(int mType) {
        if(this.mType != mType){
            this.mType = mType;
            invalidate();
        }

    }
    public int getRoundRadius() {
        return mRoundRadius;
    }
    /**
     * 设置圆角大小
     * @param mRoundRadius
     */
    public void setRoundRadius(int mRoundRadius) {
        if(this.mRoundRadius != mRoundRadius){
            this.mRoundRadius = mRoundRadius;
            invalidate();
        }

    }


    private int mBorderWidth = 1;       //描边的宽度
    private int mBorderColor = Color.parseColor("#FFFFFF");//描边颜色
    /**
     * 绘制最外面的边框
     *
     * @param canvas
     * @param width
     *
     */
    private void drawBorder(Canvas canvas, final int width,final int hight) {
        if (mBorderWidth == 0) {
            return;
        }
        final Paint mBorderPaint = new Paint();//画刷
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);//空心样式
        mBorderPaint.setColor(mBorderColor);
        this.setLayerType(LAYER_TYPE_SOFTWARE, mBorderPaint);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        /**
         * 坐标x：view宽度的一半 坐标y：view高度的一半 半径r：因为是view宽度的一半-border
         */
        canvas.drawCircle(width >> 1, hight >> 1, (width >> 1), mBorderPaint);
        canvas = null;
    }
}
