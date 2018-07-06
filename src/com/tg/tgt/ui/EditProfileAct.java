package com.tg.tgt.ui;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.PhotoUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.helper.UserHelper;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import top.zibin.luban.Luban;

/**
 *
 * @author yiyang
 */
public class EditProfileAct extends BaseActivity implements View.OnClickListener {
    private ImageView image_sex;
    private ZQImageViewRoundOval head;
    private TextView name, mood, sex;
    private LinearLayout linear_name, linear_mood, linear_sex;
    private EaseTitleBar mTitleBar;

    private String photoSaveName;//图片名
    private String photoSavePath;//保存路径
    private String path;//图片全路径

    public static final int PHOTOZOOM = 0; // 相册/拍照
    public static final int PHOTOTAKE = 1; // 相册/拍照
    public static final int IMAGE_COMPLETE = 2; //  结果
    public static final int CROPREQCODE = 3; // 截取

    public static final int REQ_NICK = 55;
    public static final int REQ_MOOD = 56;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);
        initView();
        initEvent();
        File file = new File(Environment.getExternalStorageDirectory(), "ClipHeadPhoto/cache");
        if (!file.exists())
            file.mkdirs();
        photoSavePath = Environment.getExternalStorageDirectory() + "/ClipHeadPhoto/cache/";
        photoSaveName = System.currentTimeMillis() + ".png";
    }

    private void initEvent() {
        linear_name.setOnClickListener(this);
        linear_mood.setOnClickListener(this);
        linear_sex.setOnClickListener(this);
        head.setOnClickListener(this);
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        head = (ZQImageViewRoundOval) findViewById(R.id.head);
        head.setType(ZQImageViewRoundOval.TYPE_ROUND);head.setRoundRadius(10);
        image_sex = (ImageView) findViewById(R.id.image_sex);
        name = (TextView) findViewById(R.id.edit_profile_name);
        mood = (TextView) findViewById(R.id.edit_profile_mood);
        sex = (TextView) findViewById(R.id.edit_profile_sex);
        TextView tNumTv = (TextView) findViewById(R.id.t_number_tv);
        tNumTv.setText(App.getMyUid());
        TextView tEmailTv = (TextView) findViewById(R.id.tv_email);
        tEmailTv.setText(SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "")+SpUtils.get(mContext, Constant.EMAIL_LAST, ""));
        tEmailTv.setText(App.xin);
        tEmailTv.setText(SharedPreStorageMgr.getIntance().getStringValue(this, Constant.SN));
        linear_name = (LinearLayout) findViewById(R.id.linear_name);
        linear_mood = (LinearLayout) findViewById(R.id.linear_mood);
        linear_sex = (LinearLayout) findViewById(R.id.linear_sex);
        refreshUi();

    }

    private void refreshUi() {
        String nickName = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.NICKNAME);
        String headImage = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.HEADIMAGE);
        String sex = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.SEX);
        String state = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.STATE);

        name.setText(nickName);
        this.sex.setText(UserHelper.getGender(App.applicationContext));
        /*if (sex.equals("女")) {
            image_sex.setImageResource(R.drawable.woman);
        } else if (sex.equals("男")) {
            image_sex.setImageResource(R.drawable.man);
        } else if (sex.equals("保密")) {
            image_sex.setVisibility(View.GONE);
        }*/
        int genderDrawableRes = UserHelper.getGenderDrawableRes(App.applicationContext);
        if(genderDrawableRes ==-1){
            image_sex.setVisibility(View.GONE);
        }else {
            image_sex.setVisibility(View.VISIBLE);
            image_sex.setImageResource(genderDrawableRes);
        }
        mood.setText(state.equals("")?this.getString(R.string.nox):state);
//        Glide.with(this).load(headImage).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(head);
        ImageUtils.show(mActivity, headImage, R.drawable.default_avatar, head);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                showPhotoDialog();
                break;
            case R.id.linear_name:
                setNickName();
                break;
            case R.id.linear_sex:
                setSex();
                break;
            case R.id.linear_mood:
                setMood();
            default:
                break;
        }
    }
    private void setSex() {
        final ArrayList<String> gender = new ArrayList<>();
        gender.add(getString(R.string.men));
        gender.add(getString(R.string.women));
        gender.add(getString(R.string.popSex_secret));
        OptionsPickerView agePickerView = new OptionsPickerView.Builder(mActivity, new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String finalSexText = gender.get(options1);
                ApiManger2.getApiService()
                        .modifyInfo(null, null, HttpHelper.toTextPlain(UserHelper.getGenderChar(App.applicationContext, finalSexText)),null,null)
                        .compose(EditProfileAct.this.<HttpResult<String>>bindToLifeCyclerAndApplySchedulers())
                        .subscribe(new Consumer<HttpResult<String>>() {
                            @Override
                            public void accept(@NonNull HttpResult<String> emptyDataHttpResult) throws Exception {
                                ToastUtils.showToast(getApplicationContext(), R.string.modify_success);
                                EditProfileAct.this.sex.setText(finalSexText);
                                SharedPreStorageMgr.getIntance().saveStringValue(EditProfileAct.this, Constant
                                        .SEX, UserHelper.getGenderChar(App.applicationContext, finalSexText));
                                int genderDrawableRes = UserHelper.getGenderDrawableRes(mContext);
                                if(genderDrawableRes == -1){
                                    image_sex.setVisibility(View.GONE);
                                }else {
                                    image_sex.setVisibility(View.VISIBLE);
                                    image_sex.setImageResource(genderDrawableRes);
                                }
                            }
                        });
            }
        }).setSubmitText(mActivity.getString(R.string.confirm))
                .setSubmitColor(ContextCompat.getColor(mActivity,R.color.tx_black_1))
                .setSubCalSize(16)
                .setCancelText(mActivity.getString(R.string.cancel))
                .setCancelColor(ContextCompat.getColor(mActivity,R.color.tx_black_1))
                .setTitleBgColor(ContextCompat.getColor(mActivity, R.color.white))
                .setBgColor(/*ContextCompat.getColor(activity, R.color.gray_bg)*/Color.WHITE)
                .setTextColorCenter(ContextCompat.getColor(mActivity,R.color.tx_black_1))
                .setTextColorOut(ContextCompat.getColor(mActivity,R.color.tx_black_3))
                .setContentTextSize(24)
                .build();
        agePickerView.setPicker(gender);
        agePickerView.show();
    }
    private void setSpan(@DrawableRes int res, TextView tv) {
        String text = tv.getText().toString();
        SpannableString ss = new SpannableString(text);
        Drawable d = getResources().getDrawable(res);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BASELINE);
        ss.setSpan(span, text.length() - 1, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(ss);
    }
    private void setNickName() {
        SetProfileInfoAct.set(mActivity, SetProfileInfoAct.InfoType.NickName, REQ_NICK, SpUtils.get(mContext, Constant.NICKNAME, ""), Constant.NICK_MAX_LENGTH);
    }
    private void setMood() {
        SetProfileInfoAct.set(mActivity, SetProfileInfoAct.InfoType.Mood, REQ_MOOD, SpUtils.get(mContext, Constant.STATE, ""));
    }
    /**
     * 修改头像
     */
    private void showPhotoDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_photo_choose, null);
        final Dialog dialog = new Dialog(this, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Button photograph = (Button) view.findViewById(R.id.photograph);
        Button albums = (Button) view.findViewById(R.id.albums);
        Button cancel = (Button) view.findViewById(R.id.photo_cancel);

        photograph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, PHOTOTAKE);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, PHOTOZOOM);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
    }

    /**
     * 图片选择及拍照结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case REQ_NICK:
                String modifiedNick = data.getStringExtra(SetProfileInfoAct.INFO);
                name.setText(modifiedNick);
                DemoHelper.getInstance().getUserProfileManager().setCurrentUserNick(modifiedNick);
//                SharedPreStorageMgr.getIntance().saveStringValue(EditProfileAct.this, Constant
//                        .NICKNAME, modifiedNick);
                SharedPreStorageMgr.getIntance().saveStringValue(App.applicationContext, DemoHelper.getInstance().getCurrentUsernName(), EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser()).getAvatar()+"-"+ modifiedNick);
                break;
            case REQ_MOOD:
                String modifiedMood = data.getStringExtra(SetProfileInfoAct.INFO);
                mood.setText(modifiedMood);
                DemoHelper.getInstance().getUserProfileManager().setCurrentUserState(modifiedMood);
//                SharedPreStorageMgr.getIntance().saveStringValue(EditProfileAct.this, Constant
//                        .STATE, modifiedMood);
                break;
            case PHOTOZOOM://相册
                if (data == null) {
                    return;
                }
                uri = data.getData();
//                if(uri == null){
                    uri = geturi(data);
//                }
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);// 图片在的路径
                /*Intent intent3 = new Intent(EditProfileAct.this, ClipPicAct.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);*/
                updateAvatar(path);
                break;
            case PHOTOTAKE://拍照
                path = photoSavePath + photoSaveName;
                /*uri = Uri.fromFile(new File(path));
                Intent intent2 = new Intent(EditProfileAct.this, ClipPicAct.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE)*/;
                updateAvatar(path);
                break;
            case IMAGE_COMPLETE:
                String temppath = data.getStringExtra("path");
                //head.setImageBitmap(getLoacalBitmap(temppath));
                Glide.with(EditProfileAct.this).load(temppath).into(head);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAvatar(final String path) {
        Observable.just(path)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String path) throws Exception {
                        return Luban.with(mActivity).load(path).setTargetDir(PhotoUtils.getTempDirPath
                                (mContext)).get(path).getPath();
                    }
                })
                .flatMap(new Function<String, ObservableSource<HttpResult<String>>>() {
                    @Override
                    public ObservableSource<HttpResult<String>> apply(@NonNull String path) throws Exception {
                        return ApiManger2.getApiService().modifyInfo(HttpHelper.getPicPart("picture", path), null,null,null,null);
                    }
                })
                .compose(this.<HttpResult<String>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<String>() {
                    @Override
                    protected void onSuccess(String path) {
                        ToastUtils.showToast(getApplicationContext(), R.string.modify_success);
                        ImageUtils.show(mActivity, path, R.drawable.default_avatar, head);
//                        SpUtils.put(mContext, Constant.HEADIMAGE, path);
                        DemoHelper.getInstance().getUserProfileManager().setCurrentUserAvatar(path);
                        SharedPreStorageMgr.getIntance().saveStringValue(mContext, EMClient.getInstance().getCurrentUser(), path+"-"+name.getText().toString());
                    }
                });
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     * @param intent
     * @return
     */
    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}
