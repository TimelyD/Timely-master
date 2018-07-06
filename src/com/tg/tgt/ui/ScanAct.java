package com.tg.tgt.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.tg.tgt.R;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.QrCodeUtils;

/**
 *
 * @author yiyang
 */
public class ScanAct extends BaseActivity{
    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan);
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        QrCodeUtils.setFragmentArgs(captureFragment, R.layout.layout_fragment_scan);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initView();
        setTitleBarLeftBack();
    }

    @Override
    protected void initBar() {
        setTran();
    }

    public static boolean isOpen = false;

    private void initView() {
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isOpen) {
//                    QrCodeUtils.isLightEnable(true);
//                    isOpen = true;
//                } else {
//                    QrCodeUtils.isLightEnable(false);
//                    isOpen = false;
//                }
//
//            }
//        });
    }


    /**
     * 二维码解析回调函数
     */
    QrCodeUtils.AnalyzeCallback analyzeCallback = new QrCodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            if(null == result || !result.startsWith(ApiService2.BASE_URL)){
                ToastUtils.showToast(getApplicationContext(), R.string.scan_right);
                captureFragment.getHandler().sendEmptyMessageDelayed(R.id.restart_preview, 2000);
                return;
            }
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(QrCodeUtils.RESULT_TYPE, QrCodeUtils.RESULT_SUCCESS);
            bundle.putString(QrCodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanAct.this.setResult(RESULT_OK, resultIntent);
            ScanAct.this.finish();
//            ToastUtils.showToast(getApplicationContext(), result);
        }

        @Override
        public void onAnalyzeFailed() {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(QrCodeUtils.RESULT_TYPE, QrCodeUtils.RESULT_FAILED);
//            bundle.putString(QrCodeUtils.RESULT_STRING, "");
//            resultIntent.putExtras(bundle);
//            ScanAct.this.setResult(RESULT_OK, resultIntent);
//            ScanAct.this.finish();
//            ToastUtils.showToast(getApplicationContext(), "onAnalyzeFailed");
        }
    };
}
