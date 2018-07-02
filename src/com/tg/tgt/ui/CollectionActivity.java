package com.tg.tgt.ui;


import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;

/**
 * Created by DELL on 2018/7/2.
 */

public class CollectionActivity extends BaseActivity{

    private EaseTitleBar easeTitleBar;
    private RelativeLayout bottomRelative;

    private boolean isEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        init();
    }

    private void init(){
        isEditor = false;
        easeTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        bottomRelative = (RelativeLayout) findViewById(R.id.layout_bottom);
        easeTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        easeTitleBar.setRightButtonVisibility(View.VISIBLE);
        easeTitleBar.setRightButtonText(getString(R.string.editor));
        easeTitleBar.setRightClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditor){
                    easeTitleBar.setRightButtonText(getString(R.string.editor));
                    bottomRelative.setVisibility(View.GONE);
                }else {
                    easeTitleBar.setRightButtonText(getString(R.string.cancel_collection));
                    bottomRelative.setVisibility(View.VISIBLE);
                }
                isEditor = !isEditor;
            }
        });
    }

}
