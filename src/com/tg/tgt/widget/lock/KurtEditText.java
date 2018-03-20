package com.tg.tgt.widget.lock;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.easeui.utils.KeybordUtils;


public class KurtEditText extends LinearLayout {
    private EditText et;
    private ImageView iv1, iv2, iv3, iv4, iv5, iv6;
    private StringBuffer buffer;
    private LayoutInflater inflate;
    private ImageView[] images;
    private View view;
    public KurtListener listener;

    public KurtEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate = LayoutInflater.from(context);
        buffer = new StringBuffer();
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(KeybordUtils.isSoftInputVisible((Activity) getContext())){
            KeybordUtils.toggleSoftInput(getContext());
        }
        super.onDetachedFromWindow();
    }

    private void initView() {
        view = inflate.inflate(com.tg.tgt.R.layout.kurt_layout, null);
        et = (EditText) view.findViewById(com.tg.tgt.R.id.et);
        iv1 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv1);
        iv2 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv2);
        iv3 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv3);
        iv4 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv4);
        iv5 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv5);
        iv6 = (ImageView) view.findViewById(com.tg.tgt.R.id.iv6);
        images = new ImageView[]{iv1, iv2, iv3, iv4, iv5, iv6};
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(view, lp);
        et.addTextChangedListener(watch);
        et.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    removeText();
                    return true;
                }
                return false;
            }

        });
    }

    public void clearText(){
        int length = buffer.length();
        if(length == 0)
            return;
        buffer.delete(0, length);
        for (int i = 0; i < length; i++) {
            images[i].setVisibility(INVISIBLE);
        }
        if(listener!=null){
            listener.keyword("");
        }
    }

    private void removeText() {
        String str = buffer.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len <= 6) {
            buffer.delete(len - 1, len);
            images[len - 1].setVisibility(View.INVISIBLE);
            if(listener!=null){
                listener.keyword(str);
            }
        }
    }

    TextWatcher watch = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (s.toString().length() == 0) {
                return;
            }
            if (buffer.length() < 6) {
                buffer.append(s.toString());
                addtext();
            }
            s.delete(0, s.length());
        }

        private void addtext() {
            String str = buffer.toString();
            int len = str.length();
            if (len <= 6) {
                images[len - 1].setVisibility(View.VISIBLE);
            }
            if (len == 6) {
                if (listener != null) {
                    listener.keyword(str);
                }
            }

        }
    };

    public interface KurtListener {
        void keyword(String str);
    }

    public void setKurtListener(KurtListener listener) {
        this.listener = listener;
    }
}
