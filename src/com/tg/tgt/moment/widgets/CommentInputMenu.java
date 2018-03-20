package com.tg.tgt.moment.widgets;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.KeybordUtils;
import com.hyphenate.easeui.utils.rxbus2.BusCode;
import com.hyphenate.easeui.utils.rxbus2.RxBus;
import com.hyphenate.easeui.utils.rxbus2.Subscribe;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.tg.tgt.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author yiyang
 */
public class CommentInputMenu extends LinearLayout {

    private FrameLayout emojiconMenuContainer;
    private LayoutInflater layoutInflater;
    private EaseEmojiconMenuBase emojiconMenu;
    private EditText editText;
    private Button mSendBtn;
    private View mEtContainer;
    private View btnIcon;

    public CommentInputMenu(Context context) {
        this(context, null, 0);
    }

    public CommentInputMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        layoutInflater = LayoutInflater.from(context);
        View rootView = layoutInflater.inflate(R.layout.widget_comment_input_layout, this, true);
        editText = (EditText) rootView.findViewById(R.id.et_comment);
        emojiconMenuContainer = (FrameLayout) rootView.findViewById(R.id.emojicon_menu_container);
        btnIcon = rootView.findViewById(R.id.btn_icon);
        btnIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEmojicon();
            }
        });
        mEtContainer = rootView.findViewById(R.id.rl_bottom);
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    currentHeight = 0;
                    hideEmojiMenu();
                }
                return false;
            }
        });
        mSendBtn = (Button) rootView.findViewById(R.id.btn_send);
        mSendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onSendMessage(editText.getText().toString());
                }
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    mSendBtn.setEnabled(true);
                    RxBus.get().send(BusCode.CHAT_ACTION_SEND_ENABLE, true);
                }else {
                    mSendBtn.setEnabled(false);
                    RxBus.get().send(BusCode.CHAT_ACTION_SEND_ENABLE, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendText();
                    return true;
                } else {
                    return false;
                }
            }
        });
        init(null);
    }

    @Subscribe(code = BusCode.CHAT_ACTION_SEND)
    public void sendText() {
        String s = editText.getText().toString();
        if(mListener!=null)
            mListener.onSendMessage(s);
        editText.setText("");
    }

    public void onRegister(){
        RxBus.get().register(this);
        ((EaseEmojiconMenu)emojiconMenu).tabBar.onRegister();
    }
    public void onUnRegister(){
        RxBus.get().unRegister(this);
        ((EaseEmojiconMenu)emojiconMenu).tabBar.onUnRegister();
    }


    private boolean inited;

    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if (inited) {
            return;
        }
        // emojicon menu, use default if no customized one
        if (emojiconMenu == null) {
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(com.hyphenate.easeui.R.layout
                    .ease_layout_emojicon_menu, null);
            if (emojiconGroupList == null) {
                emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
                emojiconGroupList.add(new EaseEmojiconGroupEntity(com.hyphenate.easeui.R.drawable.ee_1, Arrays.asList
                        (EaseDefaultEmojiconDatas.getData())));
            }
            ((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
            emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuBase.EaseEmojiconMenuListener() {

                @Override
                public void onExpressionClicked(EaseEmojicon emojicon) {
                    if (emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION) {
                        if (emojicon.getEmojiText() != null) {
                            editText.append(EaseSmileUtils.getSmiledText(getContext(), emojicon
                                    .getEmojiText()));
                        }
                    } else {

                    }
                }

                @Override
                public void onDeleteImageClicked() {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent
                                .KEYCODE_ENDCALL);
                        editText.dispatchKeyEvent(event);
                    }
                }
            });
        }
        emojiconMenuContainer.addView(emojiconMenu);
        emojiconMenu.setVisibility(GONE);
        inited = true;
    }

    private Handler handler = new Handler();
    /**
     * 整个控件的高度
     */
    private int currentHeight;
    private boolean needGone;
    protected void toggleEmojicon() {
        if (emojiconMenu.getVisibility() == VISIBLE) {
            editText.requestFocus();
            //弹出键盘
            KeybordUtils.showSoftInput(editText.getContext(), editText);

            handler.postDelayed(new Runnable() {
                public void run() {
                    currentHeight = mEtContainer.getHeight();
                    emojiconMenu.setVisibility(GONE);
                    btnIcon.setSelected(false);

                }
            }, 50);
        } else {
            needGone = true;
            hideKeyboard();
            if(heightWithEmoji == 0) {
                emojiconMenu.measure(0, 0);
                heightWithEmoji = emojiconMenu.getMeasuredHeight();
            }
            currentHeight = heightWithEmoji +mEtContainer.getHeight();
            handler.postDelayed(new Runnable() {
                public void run() {
                    emojiconMenu.setVisibility(VISIBLE);
                    btnIcon.setSelected(true);
                    needGone = false;
                }
            }, 50);
        }
    }
    private int heightWithEmoji;

    public void hideEmojiMenu() {
        if (emojiconMenu.getVisibility() == VISIBLE)
            handler.postDelayed(new Runnable() {
                public void run() {
                    btnIcon.setSelected(false);
                    emojiconMenu.setVisibility(GONE);
                }
            }, 50);
    }

    public void updateKeyBoard(int visibility){
        if (View.VISIBLE == visibility) {
            isKeyBoardShow = true;
            editText.requestFocus();
            //弹出键盘
            currentHeight = 0;
            if(emojiconMenu.getVisibility()!=VISIBLE)
            KeybordUtils.showSoftInput(editText.getContext(), editText);
            emojiconMenu.setVisibility(GONE);
            btnIcon.setSelected(false);
        } else if (View.GONE == visibility) {
            isKeyBoardShow = false;
            //隐藏键盘
            KeybordUtils.hideSoftInput(editText.getContext(), editText);
            currentHeight =0;
            hideEmojiMenu();
        }
    }

    public boolean isEmojiShow(){
        return emojiconMenu.getVisibility() == VISIBLE || needGone;
    }

    private boolean isKeyBoardShow;
    public void setKeyBoardShow(boolean flag){
        isKeyBoardShow = flag;
    }
    public boolean isKeyBoardShow(){
        return isKeyBoardShow;
    }

    public int getHeight1(){
        return currentHeight == 0 ? getHeight() : currentHeight;
    }
    public EditText getEditText(){
        return editText;
    }
    public void showKeyboard(){

    }
    public void hideKeyboard() {
        KeybordUtils.hideSoftInput(getContext(), editText);
    }

    public void setListener(CommentInputMenuListener listener) {
        mListener = listener;
    }

    private CommentInputMenuListener mListener;

    public void setHint(String hint) {
        editText.setHint(hint);
    }

    public interface CommentInputMenuListener {
        void onSendMessage(String content);
        void onBigExpressionClicked(EaseEmojicon emojicon);
    }
}
