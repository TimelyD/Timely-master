package com.hyphenate.easeui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;
import com.hyphenate.easeui.widget.photoselect.PhotoSelectMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * input menu
 *
 * including below component:
 *    EaseChatPrimaryMenu: main menu bar, text input, send button
 *    EaseChatExtendMenu: grid menu with image, file, location, etc
 *    EaseEmojiconMenu: emoji icons
 */
public class EaseChatInputMenu extends LinearLayout {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected EaseChatPrimaryMenuBase chatPrimaryMenu;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected EaseChatExtendMenu chatExtendMenu;
    protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;
    private VoiceRecorderView sendVoiceMenu;
    private View developingMenu;
    public PhotoSelectMenu photoSelectMenu;

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(com.hyphenate.easeui.R.layout.ease_widget_chat_input_menu, this);
        primaryMenuContainer = (FrameLayout) findViewById(com.hyphenate.easeui.R.id.primary_menu_container);
        emojiconMenuContainer = (FrameLayout) findViewById(com.hyphenate.easeui.R.id.emojicon_menu_container);
        chatExtendMenuContainer = (FrameLayout) findViewById(com.hyphenate.easeui.R.id.extend_menu_container);

        // extend menu
        chatExtendMenu = (EaseChatExtendMenu) findViewById(com.hyphenate.easeui.R.id.extend_menu);


    }

    /**
     * init view 
     *
     * This method should be called after registerExtendMenuItem(), setCustomEmojiconMenu() and setCustomPrimaryMenu().
     * @param emojiconGroupList --will use default if null
     */
    @SuppressLint("InflateParams")
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if (inited) {
            return;
        }
        // primary menu, use default if no customized one
        if (chatPrimaryMenu == null) {
            chatPrimaryMenu = (EaseChatPrimaryMenu) layoutInflater.inflate(com.hyphenate.easeui.R.layout
                    .ease_layout_chat_primary_menu, null);
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

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
        }
        emojiconMenuContainer.addView(emojiconMenu);

        //加入发送语音的view
        initSendVoiceMenu();

        initPhotoSelectMenu();

        initDevelopingMenu();

        processChatMenu();
        chatExtendMenu.init();

        inited = true;
    }

    //开发中。。。
    private void initDevelopingMenu() {
        if(developingMenu == null) {
            developingMenu = layoutInflater.inflate(R.layout.layout_developing_menu, null);
            emojiconMenuContainer.addView(developingMenu);
            developingMenu.setVisibility(GONE);
        }

    }

    /**
     * 加入发送语音的view，并将事件传递给上一层
     */
    private void initSendVoiceMenu() {
        if (sendVoiceMenu == null) {
//            sendVoiceMenu = layoutInflater.inflate(R.layout.layout_send_voice_menu, null);
//            sendVoiceMenu.findViewById(R.id.send_voice_iv).setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (listener != null) {
//                        return listener.onPressToSpeakBtnTouch(v, event);
//                    }
//                    return false;
//                }
//            });
            sendVoiceMenu = new VoiceRecorderView(getContext());
            emojiconMenuContainer.addView(sendVoiceMenu);
            sendVoiceMenu.setCallback(new VoiceRecorderView.EaseVoiceRecorderCallback() {
                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    listener.onSendVoice(voiceFilePath, voiceTimeLength);
                }
            });
            sendVoiceMenu.setVisibility(GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((EaseEmojiconMenu)emojiconMenu).tabBar.onRegister();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((EaseEmojiconMenu)emojiconMenu).tabBar.onUnRegister();
    }

    /**
     * 加入发送图片的view
     */
    private void initPhotoSelectMenu() {
        if (photoSelectMenu == null) {
            photoSelectMenu = new PhotoSelectMenu(getContext());
            emojiconMenuContainer.addView(photoSelectMenu);
            photoSelectMenu.setVisibility(GONE);
            photoSelectMenu.setOnPhotoMenuListener(new PhotoSelectMenu.OnPhotoMenuListener() {
                @Override
                public void onPhotoSend(MediaBean[] bean) {
                    if(listener!=null)
                        listener.onPhotoSend(bean);
                }
            });
            photoSelectMenu.setAlbumClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                        listener.onPhotoClick();
                }
            });
        }
    }

    public void init() {
        init(null);
    }

    /**
     * set custom emojicon menu
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EaseEmojiconMenuBase customEmojiconMenu) {
        this.emojiconMenu = customEmojiconMenu;
    }

    /**
     * set custom primary menu
     * @param customPrimaryMenu
     */
    public void setCustomPrimaryMenu(EaseChatPrimaryMenuBase customPrimaryMenu) {
        this.chatPrimaryMenu = customPrimaryMenu;
    }

    public EaseChatPrimaryMenuBase getPrimaryMenu() {
        return chatPrimaryMenu;
    }

    public EaseChatExtendMenu getExtendMenu() {
        return chatExtendMenu;
    }

    public EaseEmojiconMenuBase getEmojiconMenu() {
        return emojiconMenu;
    }


    /**
     * register menu item
     *
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
                                       EaseChatExtendMenu.EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * register menu item
     *
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
                                       EaseChatExtendMenu.EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }


    protected void processChatMenu() {
        // send message button
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
//                toggleMore();
//                toggleEmojicon(developingMenu);
//                mCurrentMenu = developingMenu;
                toggleEmojicon(chatExtendMenu);
                mCurrentMenu = chatExtendMenu;
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon(emojiconMenu);
                mCurrentMenu = emojiconMenu;
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleReadFire() {
                listener.onToggleReadFire();
            }

            @Override
            public void onVoiceClick() {
                toggleEmojicon(sendVoiceMenu);
                mCurrentMenu = sendVoiceMenu;
            }

            @Override
            public void onPhotoClick() {
                toggleEmojicon(photoSelectMenu);
                mCurrentMenu = photoSelectMenu;
//                if(null != listener){
//                    listener.onPhotoClick();
//                }
            }

            @Override
            public void onCameraClick() {
//                toggleEmojicon(developingMenu);
//                mCurrentMenu = developingMenu;
                if(null != listener){
                    listener.onCameraClick();
                }
            }

            @Override
            public void onVideoClick() {
//                toggleEmojicon(developingMenu);
//                mCurrentMenu = developingMenu;
                if(null != listener){
                    listener.onVideoClick();
                }
            }


            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (listener != null) {
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if (emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION) {
                    if (emojicon.getEmojiText() != null) {
                        chatPrimaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context, emojicon
                                .getEmojiText()));
                    }
                } else {
                    if (listener != null) {
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }


    /**
     * insert text
     * @param text
     */
    public void insertText(String text) {
        getPrimaryMenu().onTextInsert(text);
    }

    /**
     * show or hide extend menu
     *
     */
    protected void toggleMore() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.VISIBLE);
                    emojiconMenu.setVisibility(View.GONE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                chatExtendMenuContainer.setVisibility(View.GONE);
            }
        }
    }

    //当前emojiconMenuContainer中显示的view
    private View mCurrentMenu;

    /**
     * show or hide emojicon
     */
    protected void toggleEmojicon(final View view) {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(GONE);
                    view.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            if (view.getVisibility() == View.VISIBLE) {
                chatExtendMenuContainer.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            } else {
                chatExtendMenu.setVisibility(View.GONE);
                if (view != mCurrentMenu) {
                    mCurrentMenu.setVisibility(GONE);
                }
                view.setVisibility(VISIBLE);
            }
        }
    }
//    protected void toggleEmojicon() {
//        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
//            hideKeyboard();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
//                    chatExtendMenu.setVisibility(View.GONE);
//                    emojiconMenu.setVisibility(View.VISIBLE);
//                }
//            }, 50);
//        } else {
//            if (emojiconMenu.getVisibility() == View.VISIBLE) {
//                chatExtendMenuContainer.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.GONE);
//            } else {
//                chatExtendMenu.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.VISIBLE);
//            }
//
//        }
//    }

        /**
         * hide keyboard
         */

    private void hideKeyboard() {
        chatPrimaryMenu.hideKeyboard();
    }

    /**
     * hide extend menu
     */
    public void hideExtendMenuContainer() {
        chatExtendMenu.setVisibility(View.GONE);
//        emojiconMenu.setVisibility(View.GONE);
        if(mCurrentMenu!=null)
        mCurrentMenu.setVisibility(GONE);
        chatExtendMenuContainer.setVisibility(View.GONE);
        chatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * when back key pressed
     *
     * @return false--extend menu is on, will hide it first
     *         true --extend menu is off 
     */
    public boolean onBackPressed() {
        if (chatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }


    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuListener {
        /**
         * when send message button pressed
         *
         * @param content
         *            message content
         */
        void onSendMessage(String content);

        /**
         * when big icon pressed
         * @param emojicon
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * when speak button is touched
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        /**
         * 点击阅后即焚，切换状态
         */
        void onToggleReadFire();

        /**
         * 发送语音
         * @param voiceFilePath
         * @param voiceTimeLength
         */
        void onSendVoice(String voiceFilePath, int voiceTimeLength);

        /**
         * 点击拍照
         */
        void onCameraClick();

        /**
         * 点击视频聊天
         */
        void onVideoClick();

        /**
         * 点击进入相册
         */
        void onPhotoClick();
        /**
         * 点击发送图片
         */
        void onPhotoSend(MediaBean[] path);
    }

}
