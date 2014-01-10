package org.ednovo.goorusearchwidget;

/*
 * HomeScreenActivity.java 
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
import java.util.concurrent.TimeUnit;

import org.ednovo.R;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.flurry.android.FlurryAgent;

public class HomeScreenActivity extends Activity {
	ImageView imgViewSections;
	ImageView imgViewGooruSearch;
	ImageView imgViewSettings;
	ImageView imageViewClose;
	ImageView imageViewSearch;
	Switch switchResColl;
	RelativeLayout headerSearch;
	EditText editTexthome;
	ProgressDialog dialog;
	InputMethodManager imm;
	String searchKeyword;
	boolean searchCriteria = false;

	long timeInMill_startAppSession;
	long timeInMill_endAppSession;

	private SharedPreferences prefsPrivate;
	public static final String PREFS_PRIVATE = "PREFS_PRIVATE";

	@Override
	protected void onStart() {
		super.onStart();

		Log.i("Flurry", "onStart");
		FlurryAgent.onStartSession(this, "RV7QKV6K8TFHRTXVJ99W");
		FlurryAgent.setLogEnabled(true);

		// Flurry : Number of Sessions
		FlurryAgent.logEvent("AppSessionStart");

		// Take Start Time for FLurry
		timeInMill_startAppSession = System.currentTimeMillis();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		prefsPrivate = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);
		headerSearch = (RelativeLayout) findViewById(R.id.layoutHeader);
		imgViewGooruSearch = (ImageView) findViewById(R.id.ivGooruLogo);
		imgViewSections = (ImageView) findViewById(R.id.imgViewSections);
		imgViewSettings = (ImageView) findViewById(R.id.imgViewSettings);
		imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
		imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
		editTexthome = (EditText) findViewById(R.id.textViewSearch);
		switchResColl = (Switch) findViewById(R.id.switchResColl);

		dialog = new ProgressDialog(this);
		imm = (InputMethodManager) this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		new Loginform().execute();
		switchResColl.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Log.i("Hello", "True");
					searchCriteria = true;
				} else {
					Log.i("Hello", "False");
					searchCriteria = false;
				}
			}
		});

		editTexthome.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						searchKeyword = editTexthome.getText().toString()
								.trim();
						if (searchKeyword.isEmpty()
								|| searchKeyword.length() == 0) {

						} else {
							if (!searchCriteria) {
								Log.i("Resources", searchKeyword);
								Intent intentResResults = new Intent(
										getBaseContext(),
										SearchResults_resource.class);

								Bundle extras = new Bundle();
								extras.putString("keyWord", searchKeyword);
								intentResResults.putExtras(extras);
								startActivity(intentResResults);
								finish();
							}
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

		imgViewGooruSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intentResResults = new Intent(getBaseContext(),
						SearchDialogpopup.class);

				startActivity(intentResResults);

			}
		});

	}

	private class Loginform extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			try {

				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/account/signin.json");

				String data = "apiKey=b5cd1620-0c14-11e3-b8f5-12313b0af644&isGuestUser=true";

				responsedata = webdata.webInvoke(null, data, null);

				Log.i("responsedata ", ": " + responsedata);
			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {

			Log.i("LOGGER", "...Done");

			dialog.dismiss();
			if (result == null) {
				showDialog("Please Check Internet connection");

			} else if (result.equals("Please try again")) {
				showDialog("Please try again");
			} else {

				try {
					Editor prefsPrivateEditor = prefsPrivate.edit();
					JSONObject jsonobject = new JSONObject(result);

					String token = jsonobject.getString("token");
					prefsPrivateEditor.putString("token", token);
					prefsPrivateEditor.commit();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Log.i("result :", "" + result);

		}

	}

	public void showDialog(String data) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setMessage(data).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// dialog.cancel();

						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialog.create();
		// Title for AlertDialog
		alert.setTitle(" Info ");

		alert.show();
	}

	@Override
	public void onBackPressed() {

		// Take End Time for FLurry
		timeInMill_endAppSession = System.currentTimeMillis();

		long time_spentInAppSession = timeInMill_endAppSession
				- timeInMill_startAppSession;

		String time_hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(time_spentInAppSession),
				TimeUnit.MILLISECONDS.toMinutes(time_spentInAppSession)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(time_spentInAppSession)),
				TimeUnit.MILLISECONDS.toSeconds(time_spentInAppSession)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(time_spentInAppSession)));

		Log.i("Flurry", "onStop");
		Log.i("TimeSpentInApp :", time_hms);

		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("SessionTime", time_hms); // Capture author info

		// Flurry : Session Time
		FlurryAgent.logEvent("AppSessionStart", articleParams);

		FlurryAgent.onEndSession(this);

		finish();

	}
}
