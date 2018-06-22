package com.hyphenate.easeui.widget;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.easeui.utils.rxbus2.RxBus;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.util.EMLog;

/**
 * primary menu
 *
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
    private MsgEditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonFire;
    private View buttonSend;
    private View buttonPressToSpeak;
//    private ImageView faceNormal;
//    private ImageView faceChecked;
    private ImageView buttonMore;
    private boolean ctrlPress = false;
    private ImageView mIvVideo;
    private LinearLayout linearUnder;
    private View kong;

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
        Context context1 = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        editText = (MsgEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonFire = findViewById(R.id.btn_set_fire);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
//        faceNormal = (ImageView) findViewById(R.id.iv_face_normal);
//        faceChecked = (ImageView) findViewById(R.id.iv_face_checked);
        View faceLayout =  findViewById(R.id.iv_face);
        buttonMore = (ImageView) findViewById(R.id.btn_more);
        edittext_layout.setBackgroundResource(R.drawable.editbackground);
        linearUnder= (LinearLayout) findViewById(R.id.linearUnder);
        kong=findViewById(R.id.kong);

        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonFire.setOnClickListener(this);
        buttonMore.setOnClickListener(typeOnclickListener);
        faceLayout.setOnClickListener(typeOnclickListener);
        editText.setOnClickListener(this);
//        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                System.out.println("jdddddddddddddss"+hasFocus);
//                if (hasFocus) {
//                    if(listener!=null){//暂时这样处理，避免出现输入法，但是拓展栏每消失
//                        listener.onEditTextClicked();
//                    }
//                } else {
////                    edittext_layout.setBackgroundResource(R.drawable.editbackground);
//                }
//
//            }
//        });
//        editText.requestFocus();

        // listen the text change
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //只显示发送按钮,按钮为空不可点击
                if (!TextUtils.isEmpty(s)) {
//                    buttonMore.setVisibility(View.GONE);
//                    buttonSend.setVisibility(View.VISIBLE);
                    buttonSend.setEnabled(true);buttonSend.setBackgroundResource(R.drawable.ease_chat_send_btn_selector);
                    RxBus.get().send(BusCode.CHAT_ACTION_SEND_ENABLE, true);
                } else {
//                    buttonMore.setVisibility(View.VISIBLE);
//                    buttonSend.setVisibility(View.GONE);
                    buttonSend.setEnabled(false);buttonSend.setBackgroundResource(R.drawable.ease_chat_send_btn_selector2);
                    RxBus.get().send(BusCode.CHAT_ACTION_SEND_ENABLE, false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EMLog.d("key", "keyCode:" + keyCode + " action:" + event.getAction());

                // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        ctrlPress = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        ctrlPress = false;
                    }
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    EMLog.d("key", "keyCode:" + event.getKeyCode() + " action" + event.getAction() + " ctrl:" +
                                ctrlPress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                ctrlPress == true)) {
                    sendText();
                    return true;
                } else {
                    return false;
                }
            }
        });


        //不需要语音输入press事件
//        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(listener != null){
//                    return listener.onPressToSpeakBtnTouch(v, event);
//                }
//                return false;
//            }
//        });


        //下面是自己添加的底部按钮
        ImageView ivVoice = (ImageView) findViewById(R.id.iv_voice);
        ImageView ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ImageView ivCamera = (ImageView) findViewById(R.id.iv_camera);
        mIvVideo = (ImageView) findViewById(R.id.iv_video);

        ivVoice.setOnClickListener(typeOnclickListener);
        ivPhoto.setOnClickListener(typeOnclickListener);
        ivCamera.setOnClickListener(this);

        //获取到chatactivity的fragment
        ((FragmentActivity)context).getSupportFragmentManager().findFragmentByTag("chat").registerForContextMenu(mIvVideo);

        mIvVideo.setOnClickListener(this);
    }

    @Subscribe(code = BusCode.CHAT_ACTION_SEND)
    public void sendText() {
        if(listener != null) {
            String s = editText.getText().toString();
            listener.onSendBtnClicked(s);
            editText.setText("");
        }
    }

    private View currentSelectedView;
    private OnClickListener typeOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(currentSelectedView == null){
                currentSelectedView = v;
                v.setSelected(true);
            }else if(currentSelectedView.getId() == id){
                v.setSelected(false);
                currentSelectedView = null;
            }else {
                currentSelectedView.setSelected(false);
                v.setSelected(true);
                currentSelectedView = v;
            }
            if(id == R.id.iv_voice){
                if (listener != null)
                    listener.onVoiceClick();
            }else if(id == R.id.iv_face){
                if (listener != null) {
                    listener.onToggleEmojiconClicked();
                }
            }else if(id == R.id.iv_photo){
                if (listener != null)
                    listener.onPhotoClick();
            }else if (id == R.id.btn_more) {
                if (listener != null)
                    listener.onToggleExtendClicked();
            }
        }
    };

    public void setVideoCallEnable(boolean b){
        mIvVideo.setVisibility(b?VISIBLE:GONE);
    }

    /**
     * set recorder view when speak icon is touched
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView) {
        EaseVoiceRecorderView voiceRecorderView1 = voiceRecorderView;
    }

    /**
     * append emoji icon to editText
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        editText.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }

    /**
     * on clicked event
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {
            sendText();
        } else if (id == R.id.btn_set_fire) {
            //这个按钮改成是否阅后即焚
//            setModeVoice();
//            showNormalFaceImage();
//            if(listener != null)
//                listener.onToggleVoiceBtnClicked();
            toggleFireImage();
            if (listener != null) {
                listener.onToggleReadFire();
            }
        } else if (id == R.id.btn_set_mode_keyboard) {
            setModeKeyboard();
            showNormalFaceImage();
            if (listener != null)
                listener.onToggleVoiceBtnClicked();
        }  else if (id == R.id.et_sendmessage) {
//            edittext_layout.setBackgroundResource(R.drawable.editbackground);
//            faceNormal.setVisibility(VISIBLE);
//            faceChecked.setVisibility(INVISIBLE);
            if (listener != null)
                listener.onEditTextClicked();
        } /*else if (id == R.id.rl_face) {
//            toggleFaceImage();
            if (listener != null) {
                listener.onToggleEmojiconClicked();
            }
        }*/else if(id == R.id.iv_camera){
            if (listener != null)
                listener.onCameraClick();
        }else if(id == R.id.iv_video){
            view.showContextMenu();
        } else {
        }
    }


    /**
     * show voice icon when speak bar is touched
     *
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(GONE);
        buttonFire.setVisibility(GONE);
        buttonSetModeKeyboard.setVisibility(VISIBLE);
        buttonSend.setVisibility(GONE);
        buttonMore.setVisibility(VISIBLE);
        buttonPressToSpeak.setVisibility(VISIBLE);
//        faceNormal.setVisibility(VISIBLE);
//        faceChecked.setVisibility(INVISIBLE);

    }

    /**
     * show keyboard
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(VISIBLE);
        buttonSetModeKeyboard.setVisibility(GONE);
        buttonFire.setVisibility(VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(GONE);
        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(VISIBLE);
            buttonSend.setVisibility(GONE);
        } else {
            buttonMore.setVisibility(GONE);
            buttonSend.setVisibility(VISIBLE);
        }

    }


    protected void toggleFaceImage() {
//        if (faceNormal.getVisibility() == VISIBLE) {
//            showSelectedFaceImage();
//        } else {
//            showNormalFaceImage();
//        }
    }

    protected void toggleFireImage() {
        //这个方法在5.0以后失效
//        if(buttonFire.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.fire)
// .getConstantState())){
//            buttonFire.setBackgroundResource(R.drawable.fire_checked);
//        }else {
//            buttonFire.setBackgroundResource(R.drawable.fire);
//        }
        if (buttonFire.getBackground().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.fire).getConstantState())) {
            buttonFire.setBackgroundResource(R.drawable.fire_checked);
            linearUnder.setVisibility(GONE);
            kong.setVisibility(VISIBLE);
        } else {
            buttonFire.setBackgroundResource(R.drawable.fire);
            linearUnder.setVisibility(VISIBLE);
            kong.setVisibility(GONE);
        }
    }

    private void showNormalFaceImage() {
//        faceNormal.setVisibility(VISIBLE);
//        faceChecked.setVisibility(INVISIBLE);
    }

    private void showSelectedFaceImage() {
//        faceNormal.setVisibility(INVISIBLE);
//        faceChecked.setVisibility(VISIBLE);
    }


    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
        if(currentSelectedView != null){
            currentSelectedView.setSelected(false);
            currentSelectedView = null;
        }
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        setModeKeyboard();
    }

    @Override
    public MsgEditText getEditText() {
        return editText;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        RxBus.get().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        RxBus.get().unRegister(this);
        super.onDetachedFromWindow();
    }
}
