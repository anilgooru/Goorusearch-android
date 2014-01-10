package org.ednovo.goorusearchwidget;

/*
 * SearchDialogpopup.java 
 *
 * 
 * Created by Gooru 
 * Copyright (c) 2013 Gooru. All rights reserved.
 * http://www.goorulearning.org/
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

import java.util.HashMap;
import java.util.Map;

import org.ednovo.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class SearchDialogpopup extends Activity {

	EditText editTexthome;
	Dialog dialog;
	InputMethodManager imm;
	String searchKeyword;
	ImageView imageViewClose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		dialog = new Dialog(this);
		imageViewClose = (ImageView) findViewById(R.id.popupClose);
		editTexthome = (EditText) findViewById(R.id.popupEditText);
		editTexthome.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						searchKeyword = editTexthome.getText().toString()
								.trim();
						if (searchKeyword.length() > 0) {
							Log.i("Resources", searchKeyword);
							Intent intentResResults = new Intent(
									getBaseContext(),
									SearchResults_resource.class);

							Bundle extras = new Bundle();
							extras.putString("keyWord", searchKeyword);

							Map<String, String> articleParamsFlurry = new HashMap<String, String>();
							articleParamsFlurry
									.put("SearchTerm", searchKeyword); // Capture
																		// author
																		// info

							intentResResults.putExtras(extras);
							startActivity(intentResResults);
							finish();
						} else {
							dialog.setTitle("Please enter a Search keyword");
							dialog.show();
						}
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});

		imageViewClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public void onBackPressed() {

		finish();
	}

}
