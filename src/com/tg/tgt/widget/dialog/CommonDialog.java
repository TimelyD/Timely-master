package com.tg.tgt.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.tgt.R;

/**
 *
 * @author yiyang
 */
public class CommonDialog {
    public interface OnConfirmListener {
        void onConfirm(AlertDialog dialog);
    }

    public static void show(Context context, String text, View content, final OnConfirmListener listener) {
        View view = View.inflate(context, R.layout.dialog_common, null);
        Button btnlangconfirm = (Button) view.findViewById(R.id.btn_lang_confirm);

        ImageView ivclosedialog = (ImageView) view.findViewById(R.id.iv_close_dialog);
        TextView tvdialogtitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        tvdialogtitle.setText(text);
        FrameLayout container = (FrameLayout) view.findViewById(R.id.dialog_content_container);

        container.addView(content);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .show();

        ivclosedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnlangconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null)
                    listener.onConfirm(dialog);
            }
        });
    }

    public static void show(Context context, String text,String confirmTx, View content, final OnConfirmListener listener) {
        View view = View.inflate(context, R.layout.dialog_common, null);
        Button btnlangconfirm = (Button) view.findViewById(R.id.btn_lang_confirm);
        btnlangconfirm.setText(confirmTx);
        ImageView ivclosedialog = (ImageView) view.findViewById(R.id.iv_close_dialog);
        TextView tvdialogtitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        tvdialogtitle.setText(text);
        FrameLayout container = (FrameLayout) view.findViewById(R.id.dialog_content_container);

        container.addView(content);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .show();


        ivclosedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnlangconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onConfirm(dialog);
            }
        });
    }
}
