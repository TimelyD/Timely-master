package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;
import com.tg.tgt.utils.CodeUtils;

public class EditActivity extends BaseActivity{
	private EditText editText;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_edit);
		
		editText = (EditText) findViewById(R.id.edittext);
		editText.setFilters(new InputFilter[]{CodeUtils.fter});
		String title = getIntent().getStringExtra("title");
		String data = getIntent().getStringExtra("data");
		if(title != null)
			((EaseTitleBar)findViewById(R.id.title_bar)).setTitle(title);
		if(data != null)
			editText.setText(data);
		editText.setSelection(editText.length());
		setTitleBarLeftBack();
	}
	
	
	public void save(View view){
		setResult(RESULT_OK,new Intent().putExtra("data", editText.getText().toString()));
		finish();
	}

	public void back(View view) {
		finish();
	}
}
