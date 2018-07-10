package com.tg.tgt.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tg.tgt.R;
import com.tg.tgt.adapter.ViewPagerApdater;
import com.tg.tgt.utils.DisplayUtil;
import com.tg.tgt.utils.Status2BarUtil;
import com.tg.tgt.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity implements ViewPager.OnPageChangeListener{
    //引导图片资源
    private static final int[] pics = {R.mipmap.w1, R.mipmap.w2,R.mipmap.w3};
    //底部小点图片
    private ImageView[] dots;
    //记录当前选中位置
    private int currentIndex;
    private ViewPager viewPager;
    private ViewPagerApdater adapter;
    private List<View> views;           //创建的所有图片对象的集合

    private RelativeLayout zong;
    private LinearLayout pointContainer;
    private ImageView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Status2BarUtil.setImgTransparent(this);      //这行是让标题沉浸
        setViews();
        setListeners();
    }

    private void setViews() {
        views = new ArrayList<>();
        tv = (ImageView) findViewById(R.id.welcome_tv);//得到图片控件
        zong = (RelativeLayout) findViewById(R.id.welcome_Rl);
        pointContainer = (LinearLayout) findViewById(R.id.ll_viewpage_image);
        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);//初始化ViewPager对象
        /**
         * 下面是添加欢迎页面的图片对象部分
         * */
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);//用代码直接创建一个线性布局
        //初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {//给所有图片设置参数
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);        //给图片对象设置布局参数
//            iv.setScaleType(ImageView.ScaleType.CENTER);//设置比例类型（铺满屏幕，去除白色边框）
//            iv.setImageResource(pics[i]);//设置图片资源
            scaleImage(WelcomeActivity.this, iv, pics[i]);//处理所有的图片
            views.add(iv);              //向集合里添加设置完成的图片对象
            /**
             * 下面是添加小圆点部分布局
             * */
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (i == 0) {
                imageView.setImageResource(R.drawable.dot_focused);
            } else {
                imageView.setImageResource(R.drawable.dot_white_222);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(DisplayUtil.getPointRadius(this), DisplayUtil.getPointRadius(this)));
            layoutParams.leftMargin = DisplayUtil.getPointRadius(this) - 3;
            layoutParams.rightMargin = DisplayUtil.getPointRadius(this) - 3;
            pointContainer.addView(imageView, layoutParams);
        }

        adapter = new ViewPagerApdater(views);          //新建适配器对象
        viewPager.setAdapter(adapter);              //给ViewPage设置适配器
        viewPager.setOnPageChangeListener(this);    //给ViewPage设置页面更改监听
    }
    private void setListeners() {
        views.get(2).setOnDragListener(new View.OnDragListener() {//给第四个图片设置拖拽监听
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {//（控件和拖拽事件）
                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED && dragEvent.getY() < -20) {//如果拖拽事件的动作是拖拽结束并且y比例小于20
                    Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);//跳转到登录页面
                    finish();
                }
                return false;
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//给图片控件设置点击监听
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);           //跳转到登录页面
                finish();
            }
        });
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < pointContainer.getChildCount(); i++) {
            ((ImageView) pointContainer.getChildAt(i)).setImageResource(i == position ? R.drawable.dot_focused : R.drawable.dot_white_222);
        }
        switch (position) {
            case 0:
                zong.setBackgroundColor(getResources().getColor(R.color.bg_color));
                break;
            case 1:
                zong.setBackgroundColor(getResources().getColor(R.color.tx_black_2));
                break;
            case 2:
                zong.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case 3:
                zong.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
        }
        if (position < 3) {
            tv.setVisibility(View.GONE);//将图片控件设置为隐藏不显示
            pointContainer.setVisibility(View.VISIBLE);
        } else {
            pointContainer.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);//将图片控件设置为显示
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
    public static void scaleImage(final Activity activity, final View view, int drawableResId) {
        // 获取屏幕的高宽
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);
        // 解析将要被处理的图片
        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);
        if (resourceBitmap == null) {
            return;
        }
        // 开始对图片进行拉伸或者缩放
        // 使用图片的缩放比例计算将要放大的图片的高度
        int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());
        // 以屏幕的宽度为基准，如果图片的宽度比屏幕宽，则等比缩小，如果窄，则放大
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, bitmapScaledHeight, false);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这里防止图像的重复创建，避免申请不必要的内存空间
                if (scaledBitmap.isRecycled())
                    //必须返回true
                    return true;
                // 当UI绘制完毕，我们对图片进行处理
                int viewHeight = view.getMeasuredHeight();
                // 计算将要裁剪的图片的顶部以及底部的偏移量
                int offset = (scaledBitmap.getHeight() - viewHeight) / 2;
                // 对图片以中心进行裁剪，裁剪出的图片就是非常适合做引导页的图片了
                Bitmap finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(),
                        scaledBitmap.getHeight() - offset * 2);
                if (!finallyBitmap.equals(scaledBitmap)) {//如果返回的不是原图，则对原图进行回收
                    scaledBitmap.recycle();
                    System.gc();
                }
                // 设置图片显示
                view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), finallyBitmap));
                return true;
            }
        });
    }
}
