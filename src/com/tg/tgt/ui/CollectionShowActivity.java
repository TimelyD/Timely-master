package com.tg.tgt.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;

/**
 * Created by DELL on 2018/7/2.
 */

public class CollectionShowActivity extends BaseActivity {

    private EaseTitleBar easeTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_show);
        init();
    }

    private void init(){
        easeTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        easeTitleBar.setRightButtonVisibility(View.VISIBLE);
        easeTitleBar.setRightButtonText(getString(R.string.delete_button));
        easeTitleBar.setRightClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        easeTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
