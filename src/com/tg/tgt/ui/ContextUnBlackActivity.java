/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tg.tgt.ui;

import android.os.Bundle;
import android.view.View;

import com.hyphenate.easeui.domain.EaseUser;
import com.tg.tgt.R;

public class ContextUnBlackActivity extends BaseActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_context_menu_for_unblack);
	}

	public void setUnBlack(View view){
		//设置黑名单，存入数据库
		setResult(BlackFriendsMsgActivity.UNBLACKSETSUCCESSFUL);
		finish();
	}

}
