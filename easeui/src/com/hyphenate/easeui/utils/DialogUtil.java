package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.EaseChatFragment;


/**
 * Created by DELL on 2018/7/5.
 */

public class DialogUtil {
    public static void showDia(Activity context) {
        View view = context.getLayoutInflater().inflate(R.layout.pup3, null);
        final Dialog dialog = new Dialog(context, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        RelativeLayout video = (RelativeLayout) view.findViewById(R.id.rl1);
        RelativeLayout voice = (RelativeLayout) view.findViewById(R.id.rl2);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                EaseChatFragment.Handler.sendEmptyMessage(1);
            }
        });
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                EaseChatFragment.Handler.sendEmptyMessage(2);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }
}
