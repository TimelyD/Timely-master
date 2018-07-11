package com.hyphenate.easeui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.util.FileUtils;

import java.io.File;

public class EaseShowNormalFileActivity extends EaseBaseActivity {
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.hyphenate.easeui.R.layout.ease_activity_show_file);
		progressBar = (ProgressBar) findViewById(com.hyphenate.easeui.R.id.progressBar);

		final EMMessage message = getIntent().getParcelableExtra("msg");
        if (!(message.getBody() instanceof EMFileMessageBody)) {
            Toast.makeText(EaseShowNormalFileActivity.this, "Unsupported message body", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.e("Tag","路劲eeeeeeeeee"+((EMFileMessageBody)message.getBody()).getLocalUrl()+"Easycooo"+EaseConstant.isCollection);
        final File file = new File(((EMFileMessageBody)message.getBody()).getLocalUrl());

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("Tag","路劲eeeeeeeeee成功");
                        if (EaseConstant.isCollection) {
                            Intent mItent = new Intent();
                            mItent.putExtra("msg",message);
                            setResult(100, mItent);
                        }else {
                            FileUtils.openFile(file, EaseShowNormalFileActivity.this);
                        }
                        finish();
                    }
                });

            }

            @Override
            public void onError(int code, String error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(file != null && file.exists()&&file.isFile())
                            file.delete();
                        String str4 = getResources().getString(com.hyphenate.easeui.R.string.Failed_to_download_file);
                        Toast.makeText(EaseShowNormalFileActivity.this, str4+message, Toast.LENGTH_SHORT).show();
                        if (EaseConstant.isCollection) {
                            Intent mItent = new Intent();
                            mItent.putExtra("msg",message);
                            setResult(101, mItent);
                        }
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
	}


}
