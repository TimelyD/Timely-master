package com.tg.tgt.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.DeviceUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.NotificationsUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.jakewharton.rxbinding2.view.RxView;
import com.tg.tgt.App;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.helper.UserHelper;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.IView;
import com.tg.tgt.http.RxUtils;
import com.tg.tgt.http.interceptor.AddTokenInterceptor;
import com.tg.tgt.moment.ui.activity.MomentAct;
import com.tg.tgt.ui.base.BaseFragment;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.hyphenate.easeui.utils.rxbus2.RxBus;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.easeui.utils.rxbus2.ThreadMode;
import com.uuzuche.lib_zxing.activity.QrCodeUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 *
 * @author yiyang
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_SCAN = 111;
    private com.hyphenate.easeui.widget.EaseTitleBar titlebar;
    private com.hyphenate.easeui.widget.CircleImageView ivhead;
    private android.widget.TextView tvname;
    private android.widget.ImageView ivsex;
    private android.widget.TextView tvemail;
    private android.widget.LinearLayout editlayout;
    private android.widget.LinearLayout settinglayout;
    private LinearLayout friendsCircleLayout;
    private LinearLayout scanLayout;
    private LinearLayout collectionLayout;
    private ImageView ivQrCode;
    private LinearLayout profilelayout;
    private TextView tvUnreadMoment;
    private LinearLayout friends_me;
    private ZQImageViewRoundOval iv1;
    private ZQImageViewRoundOval iv2;
    private ZQImageViewRoundOval iv3;
    private ZQImageViewRoundOval iv4;

    @Override
    protected void initView(View view) {
        this.settinglayout = (LinearLayout) view.findViewById(R.id.setting_layout);
        this.profilelayout = (LinearLayout) view.findViewById(R.id.layout_profile);
        this.friendsCircleLayout = (LinearLayout) view.findViewById(R.id.friends_circle_layout);
        this.scanLayout = (LinearLayout) view.findViewById(R.id.scan_layout);
        this.collectionLayout = (LinearLayout) view.findViewById(R.id.collection);
        this.editlayout = (LinearLayout) view.findViewById(R.id.layout_come);
        this.tvemail = (TextView) view.findViewById(R.id.tv_email);
        this.tvUnreadMoment = (TextView) view.findViewById(R.id.unread_moment_number);
        this.ivsex = (ImageView) view.findViewById(R.id.iv_sex);
        this.ivQrCode = (ImageView) view.findViewById(R.id.iv_qr_code);
        this.tvname = (TextView) view.findViewById(R.id.tv_name);
        this.ivhead = (CircleImageView) view.findViewById(R.id.iv_head);
        this.titlebar = (EaseTitleBar) view.findViewById(R.id.title_bar);
        this.friends_me = (LinearLayout) view.findViewById(R.id.friends_me);
        this.iv1=(ZQImageViewRoundOval)view.findViewById(R.id.iv1);
        this.iv2=(ZQImageViewRoundOval)view.findViewById(R.id.iv2);
        this.iv3=(ZQImageViewRoundOval)view.findViewById(R.id.iv3);
        this.iv4=(ZQImageViewRoundOval)view.findViewById(R.id.iv4);

        iv1.setType(ZQImageViewRoundOval.TYPE_ROUND);iv1.setRoundRadius(10);//矩形凹行大小
        iv2.setType(ZQImageViewRoundOval.TYPE_ROUND);iv2.setRoundRadius(10);
        iv3.setType(ZQImageViewRoundOval.TYPE_ROUND);iv3.setRoundRadius(10);
        iv4.setType(ZQImageViewRoundOval.TYPE_ROUND);iv4.setRoundRadius(10);
        ImageUtils.show(getContext(), SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.HEADIMAGE), R.drawable.photo1, iv1);
        ImageUtils.show(getContext(), SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.HEADIMAGE), R.drawable.photo1, iv2);
        ImageUtils.show(getContext(), SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.HEADIMAGE), R.drawable.photo1, iv3);
        ImageUtils.show(getContext(), SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.HEADIMAGE), R.drawable.photo1, iv4);

        titlebar.setLeftImageResource(R.drawable.sao);
        titlebar.setBackgroundColor(Color.parseColor("#00000000"));
        titlebar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrCode();
                //((MainActivity)getActivity()).toggleMenu();
            }
        });
    }

    //手动开启相机权限
    private void quan(){
        if(NotificationsUtils.cameraIsCanUse()==true){
            Log.i("dcz2","有权限");
            startActivityForResult(new Intent(mContext, ScanAct.class), REQUEST_SCAN);
        }else {
            Log.i("dcz2","没有权限");
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("dcz2","获取权限");
        if(requestCode == 1) {
            if(NotificationsUtils.cameraIsCanUse()==true){
                Log.i("dcz2","有权限");
                startActivityForResult(new Intent(mContext, ScanAct.class), REQUEST_SCAN);
            }else {
                Log.i("dcz2","没有权限");
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        RxBus.get().unRegister(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Subscribe(code = BusCode.MOMENT_ACTION, threadMode = ThreadMode.MAIN)
    public void onMomentAction(){
        refreshMotionUnread();

    }

    private void refreshMotionUnread() {
        int motionUnread = getMotionUnread();
        if(motionUnread>0){
            //tvUnreadMoment.setVisibility(View.VISIBLE);
            tvUnreadMoment.setVisibility(View.GONE);
            tvUnreadMoment.setText(""+motionUnread);
        }else {
            tvUnreadMoment.setVisibility(View.GONE);
        }
    }

    private int getMotionUnread(){
        return DBManager.getInstance().getUnreadMotionActionCount();
    }

    private void init() {
        ImageUtils.show(getContext(), SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext,
                Constant.HEADIMAGE), R.drawable.photo1, ivhead);
//        Glide.with(this).load(SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant
// .HEADIMAGE))/*.diskCacheStrategy(DiskCacheStrategy.RESULT)*/.into(mHeadIv);
        tvname.setText(SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.NICKNAME));
        String last = SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.EMAIL_LAST);
        if (TextUtils.isEmpty(last)) {
            /*tvemail.setText(SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "") + "@qeveworld.com");
            int size = tvemail.getText().length();
            String xin="";
            for(int i=0;i<size;i++){
                xin=xin+"*";
            }
            tvemail.setText(xin);*/
        } else {
            /*tvemail.setText(SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "") + last);
            int size = tvemail.getText().length();
            String xin="";
            for(int i=0;i<size;i++){
                xin=xin+"*";
            }
            tvemail.setText(xin);*/
        }
        tvemail.setText(App.xin);
        tvemail.setText(this.getString(R.string.ti6)+SharedPreStorageMgr.getIntance().getStringValue(getActivity(), Constant.SN));
//        String sex = SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.SEX);
        int genderDrawableRes = UserHelper.getGenderDrawableRes(mContext);
        if(genderDrawableRes > 0){
            ivsex.setVisibility(View.VISIBLE);
            ivsex.setImageResource(genderDrawableRes);
        }else {
            ivsex.setVisibility(View.INVISIBLE);
        }
        /*if (sex.equals("女")) {
            ivsex.setImageDrawable(getResources().getDrawable(R.drawable.woman));
        } else if (sex.equals("男")) {
            ivsex.setImageDrawable(getResources().getDrawable(R.drawable.man));
        } else if (sex.equals("保密")) {
            ivsex.setVisibility(View.INVISIBLE);
        }*/
        refreshMotionUnread();
    }

    @Override
    protected void initEvent() {
        titlebar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((MainActivity) getActivity()).showMenu(titlebar);
                if(MainActivity.pup.getVisibility()==View.GONE){
                    MainActivity.tan();
                }else {
                    MainActivity.shou();
                }
            }
        });
        settinglayout.setOnClickListener(this);
        profilelayout.setOnClickListener(this);
        editlayout.setOnClickListener(this);
        friendsCircleLayout.setOnClickListener(this);
        scanLayout.setOnClickListener(this);
        collectionLayout.setOnClickListener(this);
        ivQrCode.setOnClickListener(this);
        friends_me.setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int setLayoutResouceId() {
        return R.layout.layout_setting;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_profile:
                startActivity(new Intent(mContext, EditProfileAct.class));
                break;
            case R.id.layout_come:
                startActivity(new Intent(mContext, MomentsLogAct.class));
                break;
            case R.id.setting_layout:
                startActivity(new Intent(mContext, SetUpAct.class));
                break;
            case R.id.scan_layout:
                quan();
                break;
            case R.id.iv_qr_code:
                showQrCode();
                break;
            case R.id.friends_circle_layout:
                DBManager.getInstance().saveUnreadMotionActionCount(0);
                startActivity(new Intent(mContext, MomentAct.class));
                break;
            case R.id.friends_me:
                toHomePage(SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.NICKNAME),
                        SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext, Constant.MYUID));
                break;
            case R.id.collection:
                startActivity(new Intent(mContext,CollectionActivity.class));
                break;

        }
    }

    private void toHomePage(final String username, final String userId) {
        EaseUser userInfo = EaseUserUtils.getUserInfo(username);
        if(userInfo.getChatidstate() == null){
            CodeUtils.fetchUser((IView) mContext, userId,false, new Consumer<EaseUser>() {
                @Override
                public void accept(@NonNull EaseUser easeUser) throws Exception {
                    mContext.startActivity(new Intent(mContext, MomentAct.class)
                            .putExtra(Constant.USERNAME, username)
                            .putExtra(Constant.USER_ID, userId)
                            // .putExtra("signature",easeUser.getChatidstate())
                            .putExtra(Constant.IS_MINE_HOME_PAGE, true));
                }
            });
        }else {
            mContext.startActivity(new Intent(mContext, MomentAct.class).putExtra(Constant.USERNAME, username).putExtra
                    (Constant.USER_ID, userId).putExtra(Constant.IS_MINE_HOME_PAGE, true));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            String chatId = data.getStringExtra(QrCodeUtils.RESULT_STRING);
            startActivity(new Intent(mContext, AddContactByQrCodeActivity.class).putExtra(Constant.USERNAME, chatId));
        }

    }

    public void initDialog(){
        dialog = new Dialog(mContext, R.style.shapeDialogTheme);
        View root = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_qr, null);
        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
     //   dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
//        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private Dialog dialog;

    private void showQrCode() {
//        View inflate = View.inflate(mContext, R.layout.dialog_qr, null);
//        new AlertDialog.Builder(getActivity())
//                .setView(inflate)
//                .show();
        dialog = new Dialog(mContext, R.style.shapeDialogTheme);
        View root = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_qr, null);
        root.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        //   dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
//        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
        final ImageView ivQr = (ImageView) root.findViewById(R.id.iv_qr_code);
        GlideUrl glideUrl = new GlideUrl(ApiService2.BASE_URL+ ApiService2.QR_CODE, new LazyHeaders.Builder()
                .addHeader("Accept", "image/png")
                .addHeader("token", SpUtils.get(mContext, AddTokenInterceptor.TOKEN, ""))
                .addHeader("uuid", DeviceUtils.getUniqueId(mContext))
                .build()
        );
        String qr_url = SpUtils.get(mContext, Constant.QR, "");
        //ImageUtils.show(getContext(),glideUrl, R.drawable.photo1,ivQr);
        GlideApp.with(this).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                ivQr.setImageDrawable(resource);
            }
        });
        String nickName = SharedPreStorageMgr.getIntance().getStringValue(mContext, Constant.NICKNAME);
        String headImage = SharedPreStorageMgr.getIntance().getStringValue(mContext, Constant.HEADIMAGE);

        ImageView ivAvatar = (ImageView) root.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) root.findViewById(R.id.tv_name);
        TextView tvEmail = (TextView) root.findViewById(R.id.tv_email);
        tvName.setText(nickName);
        ImageUtils.show(mContext, headImage, R.drawable.default_avatar, ivAvatar);
        tvEmail.setText(SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME,"")/*+SpUtils.get(mContext, Constant.EMAIL_LAST, "")*/);
        tvEmail.setText(App.xin);
        tvEmail.setText(this.getString(R.string.ti6)+SharedPreStorageMgr.getIntance().getStringValue(mContext, Constant.SN));
//ivQr.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        ApiManger2.getApiService()
//                .resetQrCode("1")
//                .compose(((BaseActivity)mContext).<HttpResult<String>>bindToLifeCyclerAndApplySchedulers(null))
//                .subscribe(new BaseObserver2<String>() {
//                    @Override
//                    protected void onSuccess(String url) {
//                        SpUtils.put(mContext, Constant.QR, url);
//                        GlideApp.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Drawable>() {
//                            @Override
//                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                                ivQr.setImageDrawable(resource);
//                            }
//                        });
//                    }
//                });
//    }
//});
        RxView.clicks(ivQr)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        ivQr.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.color.white));
                    }
                })
                .flatMap(new Function<Object, ObservableSource<HttpResult<String>>>() {
                    @Override
                    public ObservableSource<HttpResult<String>> apply(@NonNull Object o) throws Exception {
                        return ApiManger2.getApiService()
                                .resetQrCode("1")
                                .compose(RxUtils.<HttpResult<String>>applySchedulers());

                    }
                })
                .subscribe(new BaseObserver2<String>() {
                    @Override
                    protected void onSuccess(String url) {
                        SpUtils.put(mContext, Constant.QR, url);
                        GlideApp.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                ivQr.setImageDrawable(resource);
                            }
                        });
                    }
                });
        /*if(true)
            return;
        int w = (int) (PhoneUtil.getScreenWidth(mContext) * 0.75 + .5f);
//        Bitmap image = QrCodeUtils.createImage(SharedPreStorageMgr.getIntance().getStringValue(App
//                .applicationContext, Constant.USERNAME), w, w, null);
        final ImageView imageView = new ImageView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w,w);
        layoutParams.gravity= Gravity.CENTER;
        imageView.setLayoutParams(layoutParams);
//        imageView.setImageBitmap(image);
        GlideUrl glideUrl = new GlideUrl(ApiService2.BASE_URL+ ApiService2.QR_CODE, new LazyHeaders.Builder()
                .addHeader("Accept", "image/png")
                .addHeader("token", SpUtils.get(mContext, AddTokenInterceptor.TOKEN, ""))
                .addHeader("uuid", DeviceUtils.getUniqueId(mContext))
                .build()
        );
        String qr_url = SpUtils.get(mContext, Constant.QR, "");
        GlideApp.with(this).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
            }
        });
        CommonDialog.show(mContext, "二维码", "重置", imageView, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                ApiManger2.getApiService()
                        .resetQrCode("1")
                        .compose(((BaseActivity)mContext).<HttpResult<String>>bindToLifeCyclerAndApplySchedulers(null))
                        .subscribe(new BaseObserver2<String>() {
                            @Override
                            protected void onSuccess(String url) {
                                SpUtils.put(mContext, Constant.QR, url);
                                GlideApp.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        imageView.setImageDrawable(resource);
                                    }
                                });
                            }
                        });

            }
        });*/
//        new AlertDialog.Builder(mContext)
//                .setView(imageView)
//                .show();
    }
}
