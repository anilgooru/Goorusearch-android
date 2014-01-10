package org.ednovo.goorusearchwidget;

/*
 * ResourcePlayer.java 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ednovo.R;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

/**
 * 
 */

/**
 * @fileName : ResourcePlayer.java
 * 
 * @description :
 * 
 * 
 * @version : 1.0
 * 
 * @date: Nov 20, 2012
 * 
 * @Author Gooru team
 * 
 * @Reviewer:
 */
public class ResourcePlayer extends Activity {

	private WebView wvPlayer;

	private MyWebChromeClient mWebChromeClient = new MyWebChromeClient();
	private View mCustomView;
	private RelativeLayout mContentView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;

	private ImageView ivResourceIcon, ivCloseIcon, ivmoveforward, ivmoveback;
	private ImageView webViewBack, webViewForward, webViewRefresh, tvAbout,
			imageshare;
	private TextView tvDescription,tvDescription1;
	EditText edittext_copyurl;
	TextView tvDescriptionn1;
	String url1;

	private TextView tvTitle;
	String surl = "shortenUrl";
	private TextView tvLearn;
	private TextView tvViewsNLikes;
	RelativeLayout setContentViewLayout;
	RelativeLayout header;
	RelativeLayout subheader;
	ProgressDialog dialog;
	int flag = 0;
	int flag1 = 0;
	int check = 0;
	int urlcheck = 0;
	int desc;
	int count = 0;
	int fla = 0;
	int mk = 0;
	int jk = 0;
	String gooruOID;
	String token = "";
	String searchkeyword;
	TextView tvDescriptionn;
	int value;
	int limit;
	String imeicode;
	ProgressDialog dialog1;
	ArrayList<String> gooruOID1 = new ArrayList<String>();
	String description = "null";
	String type;
	boolean videoFlag = false;
	private SharedPreferences prefsPrivate;
	public static final String PREFS_PRIVATE = "PREFS_PRIVATE";

	// Flurry Variables
	String resourceType = "";
	String resourceGooruId = "";
	Long total_pass_time;
	Long start_time;
	Long end_time;
	Boolean flag_isPlayerTransition = false;

	@Override
	protected void onStart() {
		super.onStart();
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		imeicode = tm.getDeviceId();
		new httppoststart().execute();
		start_time = System.currentTimeMillis();
		if (flag_isPlayerTransition) {
			flag_isPlayerTransition = false;
			resourceType = "";
			resourceGooruId = "";
		}

		Log.i("onStartSearchRes", "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();

		new httppoststop().execute();

		end_time = System.currentTimeMillis();
		total_pass_time = end_time - start_time;
		Log.i("onStopSearchRes", "ResourceType = " + resourceType + ":::"
				+ "ResourceId = " + resourceGooruId);

		Map<String, String> articleParamsFlurry = new HashMap<String, String>();
		articleParamsFlurry.put("ResourceType", resourceType); // Capture author
																// info

		// Flurry : ResourceType Log
		FlurryAgent.logEvent("ResourceTypeLog", articleParamsFlurry);

		Map<String, String> articleParamsFlurry1 = new HashMap<String, String>();
		articleParamsFlurry1.put("ResourceId", resourceGooruId); // Capture
																	// author
																	// info

		// Flurry : ResourceType Log
		FlurryAgent.logEvent("ResourceIdLog", articleParamsFlurry1);

		Map<String, String> articleParamsFlurry2 = new HashMap<String, String>();
		articleParamsFlurry2.put("passedtime", "" + total_pass_time); // Capture
																		// author
																		// info

		// Flurry : ResourceType Log
		FlurryAgent.logEvent("passedtimeLog", articleParamsFlurry2);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog = new ProgressDialog(this);
		prefsPrivate = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentViewLayout = new RelativeLayout(ResourcePlayer.this);
		prefsPrivate = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

		token = prefsPrivate.getString("token", "");

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		Bundle extra = getIntent().getExtras();

		if (extra != null) {
			value = extra.getInt("key");
			gooruOID1 = extra.getStringArrayList("goor");

			searchkeyword = extra.getString("searchkey");
			limit = gooruOID1.size();
			gooruOID = gooruOID1.get(value);
			resourceGooruId = gooruOID;

			if (!gooruOID.isEmpty() || !gooruOID.equalsIgnoreCase("")
					|| gooruOID != null) {
				if (checkInternetConnection()) {
					dialog = new ProgressDialog(ResourcePlayer.this);
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getResourcesInfo().execute();
				} else {

					dialog = new ProgressDialog(ResourcePlayer.this);
					dialog.setTitle("gooru");
					dialog.setMessage("No internet connection");
					dialog.show();
				}

			}
		}
		Editor prefsPrivateEditor = prefsPrivate.edit();

		// Authentication details
		prefsPrivateEditor.putString("searchkeyword", searchkeyword);
		prefsPrivateEditor.commit();

		wvPlayer = new WebView(ResourcePlayer.this);
		wvPlayer.resumeTimers();
		wvPlayer.getSettings().setJavaScriptEnabled(true);
		wvPlayer.getSettings().setPluginState(PluginState.ON);
		wvPlayer.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wvPlayer.setWebViewClient(new HelloWebViewClient());

		wvPlayer.setWebChromeClient(new MyWebChromeClient() {
		});
		wvPlayer.getSettings().setPluginsEnabled(true);
		new getResourcesInfo().execute();

		RelativeLayout temp = new RelativeLayout(ResourcePlayer.this);
		temp.setId(668);
		temp.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));

		header = new RelativeLayout(ResourcePlayer.this);
		header.setId(1);

		header.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.navbar));
		RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 53);
		headerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		headerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);

		ivCloseIcon = new ImageView(ResourcePlayer.this);
		ivCloseIcon.setId(130);
		ivCloseIcon.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams ivCloseIconIconParams = new RelativeLayout.LayoutParams(
				50, 50);
		ivCloseIconIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		ivCloseIconIconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		ivCloseIcon.setPadding(0, 0, 0, 0);

		ivCloseIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		ivCloseIcon.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.close_corner));
		header.addView(ivCloseIcon, ivCloseIconIconParams);

		ivmoveforward = new ImageView(ResourcePlayer.this);
		ivmoveforward.setId(222);
		if (value == limit - 1) {
			ivmoveforward.setVisibility(View.GONE);
		}
		ivmoveforward.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams ivmoveforwardIconIconParams = new RelativeLayout.LayoutParams(
				21, 38);

		ivmoveforwardIconIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				-1);
		ivmoveforwardIconIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		ivmoveforwardIconIconParams.setMargins(0, 0, 30, 0);

		imageshare = new ImageView(ResourcePlayer.this);
		imageshare.setId(440);
		imageshare.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams imageshareIconParams = new RelativeLayout.LayoutParams(
				50, 50);
		imageshareIconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
		imageshareIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		imageshareIconParams.setMargins(0, 10, 100, 0);
		tvDescriptionn = new TextView(ResourcePlayer.this);
		tvDescriptionn1 = new TextView(ResourcePlayer.this);
		edittext_copyurl = new EditText(ResourcePlayer.this);
		imageshare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (desc == 0) {
					new getShortUrl().execute();

					imageshare.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.share_selected));

					subheader.setVisibility(View.VISIBLE);
					subheader.removeAllViews();

					tvDescriptionn.setVisibility(View.VISIBLE);
					tvDescriptionn1.setVisibility(View.VISIBLE);
					edittext_copyurl.setVisibility(View.VISIBLE);
					tvDescriptionn
							.setText("Share this with other by copying and pasting these links");
					tvDescriptionn.setId(221);

					tvDescriptionn.setTextSize(18);
					tvDescriptionn.setTypeface(null, Typeface.BOLD);
					tvDescriptionn.setTextColor(getResources().getColor(
							android.R.color.white));
					RelativeLayout.LayoutParams tvDescriptionParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					tvDescriptionParams.setMargins(20, 10, 0, 20);
					subheader.addView(tvDescriptionn, tvDescriptionParams);

					tvDescriptionn1.setText("Collections");
					tvDescriptionn1.setId(226);

					tvDescriptionn1.setTextSize(18);
					tvDescriptionn1.setTypeface(null, Typeface.BOLD);
					tvDescriptionn1.setTextColor(getResources().getColor(
							android.R.color.white));
					RelativeLayout.LayoutParams tvDescriptionParams1 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					tvDescriptionParams1.setMargins(20, 42, 0, 20);
					subheader.addView(tvDescriptionn1, tvDescriptionParams1);

					edittext_copyurl.setId(266);

					edittext_copyurl.setTextSize(18);
					edittext_copyurl.setTypeface(null, Typeface.BOLD);
					edittext_copyurl.setTextColor(getResources().getColor(
							android.R.color.white));
					RelativeLayout.LayoutParams tvDescriptionParams11 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					tvDescriptionParams11.setMargins(130, 35, 0, 20);
					subheader.addView(edittext_copyurl, tvDescriptionParams11);
					desc = 1;
					flag = 0;

				} else {

					imageshare.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.share_normal));
					subheader.removeAllViews();
					subheader.setVisibility(View.GONE);
					desc = 0;
				}
			}
		});

		imageshare.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.share_normal));

		ivmoveforward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (value < limit - 1) {
					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					// extras.putString("gooruOId",s);
					extras.putStringArrayList("goor", gooruOID1);
					value++;
					extras.putInt("key", value);
					intentResPlayer.putExtras(extras);
					urlcheck = 0;
					finish();
					startActivity(intentResPlayer);
				}

			}
		});

		ivmoveforward.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.arrowright));

		ivmoveback = new ImageView(ResourcePlayer.this);
		ivmoveback.setId(220);
		if (value == 0) {
			ivmoveback.setVisibility(View.GONE);
		}
		ivmoveback.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams ivmovebackIconIconParams = new RelativeLayout.LayoutParams(
				21, 38);
		ivmovebackIconIconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		ivmovebackIconIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		ivmovebackIconIconParams.setMargins(55, 0, 0, 0);

		ivmoveback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!(value <= 0)) {
					value--;
					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putStringArrayList("goor", gooruOID1);

					extras.putInt("key", value);
					intentResPlayer.putExtras(extras);
					urlcheck = 0;
					finish();
					startActivity(intentResPlayer);
				}

			}
		});

		ivmoveback.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.left));

		webViewBack = new ImageView(ResourcePlayer.this);
		webViewBack.setId(323);
		webViewBack.setScaleType(ImageView.ScaleType.FIT_XY);

		RelativeLayout.LayoutParams webViewBackIconParams = new RelativeLayout.LayoutParams(
				25, 26);

		webViewBackIconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		webViewBackIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		webViewBackIconParams.setMargins(175, 0, 0, 0);

		webViewBack.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.arrow_leftactive));
		webViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wvPlayer.canGoBack()) {

					wvPlayer.goBack();

				}

			}
		});

		webViewRefresh = new ImageView(ResourcePlayer.this);
		webViewRefresh.setId(322);
		webViewRefresh.setScaleType(ImageView.ScaleType.FIT_XY);

		RelativeLayout.LayoutParams webViewRefreshIconParams = new RelativeLayout.LayoutParams(
				30, 30);

		webViewRefreshIconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		webViewRefreshIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		webViewRefreshIconParams.setMargins(305, 0, 0, 0);

		webViewRefresh.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.refresh));
		webViewRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				wvPlayer.reload();
			}
		});

		webViewForward = new ImageView(ResourcePlayer.this);
		webViewForward.setId(321);
		webViewForward.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams webViewForwardIconParams = new RelativeLayout.LayoutParams(
				25, 26);

		webViewForwardIconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
		webViewForwardIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		webViewForwardIconParams.setMargins(245, 0, 0, 0);
		webViewForward.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.arrow_rightactive));
		webViewForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wvPlayer.canGoForward()) {

					wvPlayer.goForward();

				}
			}
		});

		ivResourceIcon = new ImageView(ResourcePlayer.this);
		ivResourceIcon.setId(30);
		ivResourceIcon.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams ivResourceIconParams = new RelativeLayout.LayoutParams(
				50, 25);
		ivResourceIconParams.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		ivResourceIconParams.addRule(RelativeLayout.LEFT_OF, 130);
		ivResourceIcon.setPadding(50, 0, 0, 0);

		ivResourceIcon.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.handouts));
		header.addView(ivResourceIcon, ivResourceIconParams);

		tvLearn = new TextView(this);
		tvLearn.setText("Learn More");
		tvLearn.setId(20);
		tvLearn.setPadding(100, 0, 0, 0);
		tvLearn.setTextSize(20);
		tvLearn.setTextColor(getResources().getColor(android.R.color.white));
		RelativeLayout.LayoutParams tvLearnParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tvLearnParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
		tvLearnParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
		tvAbout = new ImageView(ResourcePlayer.this);
		tvAbout.setId(21);
		tvAbout.setScaleType(ImageView.ScaleType.FIT_XY);
		RelativeLayout.LayoutParams webViewForwardIconParamsa = new RelativeLayout.LayoutParams(
				32, 32);

		webViewForwardIconParamsa
				.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);

		webViewForwardIconParamsa.addRule(RelativeLayout.CENTER_VERTICAL, -1);
		webViewForwardIconParamsa.setMargins(0, 0, 200, 0);

		tvAbout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.info));

		header.addView(tvAbout, webViewForwardIconParamsa);

		RelativeLayout fortvtitle = new RelativeLayout(this);
		RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tvTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		tvTitleParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
		tvTitleParams.addRule(RelativeLayout.RIGHT_OF, 322);
		tvTitleParams.addRule(RelativeLayout.LEFT_OF, 21);
		header.addView(fortvtitle, tvTitleParams);

		tvTitle = new TextView(this);
		tvTitle.setText("");
		tvTitle.setId(22);
		tvTitle.setPadding(0, 0, 0, 0);
		tvTitle.setTextSize(25);
		tvTitle.setSingleLine(true);
		tvTitle.setTextColor(getResources().getColor(android.R.color.white));
		RelativeLayout.LayoutParams tvTitleParamstv = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		tvTitleParamstv.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
		tvTitleParamstv.addRule(RelativeLayout.CENTER_VERTICAL, 1);

		fortvtitle.addView(tvTitle, tvTitleParamstv);

		tvViewsNLikes = new TextView(this);
		tvViewsNLikes.setText("");
		tvViewsNLikes.setId(23);
		tvViewsNLikes.setPadding(0, 0, 5, 5);
		tvViewsNLikes.setTextSize(18);
		tvViewsNLikes.setTextColor(getResources().getColor(
				android.R.color.white));
		RelativeLayout.LayoutParams tvViewsNLikesParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		tvViewsNLikesParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
		tvViewsNLikesParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);

		subheader = new RelativeLayout(ResourcePlayer.this);
		subheader.setId(100);
		subheader.setVisibility(View.GONE);
		subheader.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.navbar));

		RelativeLayout.LayoutParams subheaderParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT, 100);
		subheaderParams.addRule(RelativeLayout.BELOW, 1);
		subheaderParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);

		RelativeLayout.LayoutParams wvPlayerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		wvPlayerParams.addRule(RelativeLayout.BELOW, 100);
		wvPlayerParams.addRule(RelativeLayout.CENTER_IN_PARENT, 100);

		LinearLayout videoLayout = new LinearLayout(this);
		videoLayout.setVisibility(View.GONE);

		header.addView(webViewBack, webViewBackIconParams);
		header.addView(webViewRefresh, webViewRefreshIconParams);
		header.addView(webViewForward, webViewForwardIconParams);
		header.addView(ivmoveforward, ivmoveforwardIconIconParams);
		header.addView(imageshare, imageshareIconParams);
		header.addView(ivmoveback, ivmovebackIconIconParams);
		temp.addView(header, headerParams);
		temp.addView(subheader, subheaderParams);
		temp.addView(wvPlayer, wvPlayerParams);
		temp.addView(videoLayout, wvPlayerParams);

		setContentViewLayout.addView(temp, layoutParams);

		setContentView(setContentViewLayout);
		tvDescription = new TextView(ResourcePlayer.this);
		tvDescription1 = new TextView(ResourcePlayer.this);
		tvAbout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag == 0) {
					subheader.setVisibility(View.VISIBLE);
					subheader.removeAllViews();
					// tvDescriptionn.setVisibility(View.INVISIBLE);
					tvDescription1.setVisibility(View.VISIBLE);
					tvDescription.setVisibility(View.VISIBLE);

					tvDescription.setText("Description");
					tvDescription.setId(221);

					tvDescription.setTextSize(18);
					tvDescription.setTypeface(null, Typeface.BOLD);
					tvDescription.setTextColor(getResources().getColor(
							android.R.color.white));
					RelativeLayout.LayoutParams tvDescriptionParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					tvDescriptionParams.setMargins(20, 10, 0, 20);
					tvDescriptionParams.addRule(RelativeLayout.BELOW, 220);

					tvDescription1.setText(description);
					tvDescription1.setLines(3);
					tvDescription1.setId(321);

					tvDescription1.setTextSize(15);
					tvDescription1.setTextColor(getResources().getColor(
							android.R.color.white));
					RelativeLayout.LayoutParams tvDescription1Params = new RelativeLayout.LayoutParams(
							1100, 100);
					tvDescription1Params.addRule(
							RelativeLayout.CENTER_IN_PARENT, -1);
					tvDescription1.setPadding(100, 20, 100, 0);
					subheader.addView(tvDescription1, tvDescription1Params);
					desc = 0;
					flag = 1;
					flag1 = 0;

				} else {
					subheader.removeAllViews();
					subheader.setVisibility(View.GONE);

					flag = 0;
				}
			}
		});

	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			view.loadUrl(url);
			return true;

		}

		public void onPageFinished(WebView view, String url) {
			if (wvPlayer.canGoBack()) {

				webViewBack.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.arrow_leftactivee));

			}
			if (wvPlayer.canGoForward()) {

				webViewForward.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.arrow_rightactivee));

			}
		}

	}

	private boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			Log.v("", "Internet Connection Not Present");
			return false;
		}
	}

	@Override
	protected void onPause() {
		pauseBrowser();
		super.onPause();
	}

	@Override
	protected void onResume() {
		resumeBrowser();
		super.onResume();
	}

	private void pauseBrowser() {

		// pause flash and javascript etc
		callHiddenWebViewMethod(wvPlayer, "onPause");
		wvPlayer.pauseTimers();
	}

	private void resumeBrowser() {

		// resume flash and javascript etc
		callHiddenWebViewMethod(wvPlayer, "onResume");
		wvPlayer.resumeTimers();
	}

	private void callHiddenWebViewMethod(final WebView wv, final String name) {
		if (wvPlayer != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(wvPlayer);
			} catch (final Exception e) {
			}
		}
	}

	private class getResourcesInfo extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String responsedata = null;

			try {

				String url = "http://concept.goorulearning.org/gooruapi/rest/v2/resource/"
						+ gooruOID + "?sessionToken=" + token;
				url1 = url;
				Log.i("url", "" + url);

				HttpClient httpClient = new DefaultHttpClient();

				HttpGet httpGet = new HttpGet(url);

				try {
					// execute(); executes a request using the default context.
					// Then we assign the execution result to HttpResponse
					HttpResponse httpResponse = httpClient.execute(httpGet);

					// getEntity() ; obtains the message entity of this response
					// getContent() ; creates a new InputStream object of the
					// entity.
					// Now we need a readable source to read the byte stream
					// that comes as the httpResponse
					InputStream inputStream = httpResponse.getEntity()
							.getContent();

					// We have a byte stream. Next step is to convert it to a
					// Character stream
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);

					// Then we have to wraps the existing reader
					// (InputStreamReader) and buffer the input
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);

					// InputStreamReader contains a buffer of bytes read from
					// the source stream and converts these into characters as
					// needed.
					// The buffer size is 8K
					// Therefore we need a mechanism to append the separately
					// coming chunks in to one String element
					// We have to use a class that can handle modifiable
					// sequence of characters for use in creating String
					StringBuilder stringBuilder = new StringBuilder();

					String bufferedStrChunk = null;

					// There may be so many buffered chunks. We have to go
					// through each and every chunk of characters
					// and assign a each chunk to bufferedStrChunk String
					// variable
					// and append that value one by one to the stringBuilder
					while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
						stringBuilder.append(bufferedStrChunk);
					}

					// Now we have the whole response as a String value.
					// We return that value then the onPostExecute() can handle
					// the content
					// System.out.println("Returning value of doInBackground :"
					// + stringBuilder.toString());

					// If the Username and Password match, it will return
					// "working" as response
					// If the Username or Password wrong, it will return
					// "invalid" as response
					return stringBuilder.toString();

				} catch (ClientProtocolException cpe) {
					System.out
							.println("Exception generates caz of httpResponse :"
									+ cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out
							.println("Second exception generates caz of httpResponse :"
									+ ioe);
					ioe.printStackTrace();
				}

				return null;

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}
			return responsedata;

		}

		protected void onPostExecute(String result) {

			Log.i("result :", "" + result);

			if (result == null) {

			} else if (result.equals("Please try again")) {

			} else {

				try {
					JSONObject jsonnew = new JSONObject(result);
					JSONObject json = jsonnew.getJSONObject("resource");
					String strVoteUp = json.getString("views");
					String strCategory = json.getString("category");
					resourceType = strCategory;
					String strAssetUri = json.getString("assetURI");
					String strFolder = json.getString("folder");
					String strLoadUrl = json.getString("url");
					String strTitle = json.getString("title");
					if (json.has("description")) {
						description = json.getString("description");
					}
					if (description.equalsIgnoreCase("null")) {
						tvAbout.setVisibility(View.GONE);
					}

					type = strCategory;
					if (strCategory.equalsIgnoreCase("video")) {
						videoFlag = true;
						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.video));
					} else if (strCategory.equalsIgnoreCase("textbook")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.textbook));

					} else if (strCategory.equalsIgnoreCase("website")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.website));

					} else if (strCategory.equalsIgnoreCase("interactive")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.interactive));

					} else if (strCategory.equalsIgnoreCase("exam")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.exam));

					} else if (strCategory.equalsIgnoreCase("lesson")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.lesson));

					} else if (strCategory.equalsIgnoreCase("slide")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.slides));

					} else if (strCategory.equalsIgnoreCase("handouts")) {

						ivResourceIcon.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.handouts));

					}

					tvTitle.setText(strTitle);
					int length = strTitle.length();
					if (length > 41) {
						tvTitle.setTextSize(20);
					}
					tvViewsNLikes.setText("0 Likes\n" + strVoteUp + " Views");

					if (strLoadUrl.contains(".pdf")) {
						webViewBack.setVisibility(View.INVISIBLE);
						webViewForward.setVisibility(View.INVISIBLE);
						webViewRefresh.setVisibility(View.INVISIBLE);
						strLoadUrl = "https://docs.google.com/gview?embedded=true&url="
								+ strAssetUri + strFolder + strLoadUrl;
					}
					if (strLoadUrl.contains("youtube.com")) {
						webViewBack.setVisibility(View.INVISIBLE);
						webViewForward.setVisibility(View.INVISIBLE);
						webViewRefresh.setVisibility(View.INVISIBLE);
						Uri uri = Uri.parse(strLoadUrl);
						String id = uri.getQueryParameter("v");
						Log.i("ID", id);
						String myHtmlData = "<html><head></head><body margin-left = '0px'margin-top = '0px' leftmargin = '0px' topmargin = '0px' ><iframe width=\"100%\" height=\"100%           \""
								+ "src=\"http://www.youtube.com/embed/"
								+ id
								+ "?rel=0&autoplay=1\"frameborder=\"0\" allowfullscreen> </iframe></body></html>";

						Log.i("html", myHtmlData);
						wvPlayer.setBackgroundColor(Color.TRANSPARENT);
						wvPlayer.loadData(myHtmlData, "text/html", "utf-8");

					} else {
						wvPlayer.loadUrl(strLoadUrl);
					}
					Log.i("strVoteUp :", "" + strVoteUp.toString());
					Log.i("strCategory :", "" + strCategory.toString());
					Log.i("strLoadUrl :", "" + strLoadUrl.toString());
					Log.i("strTitle :", "" + strTitle.toString());

					dialog.dismiss();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onBackPressed() {
		Log.i("why not come out from the full screen",
				"why not come out from the full screen");
		if (mCustomViewContainer != null) {
			Log.i("full screen", "full screen");

			mWebChromeClient.onHideCustomView();

		}

		else {
			Log.i("full screen", "exit from the full screen");
			finish();
		}

	}

	private class MyWebChromeClient extends WebChromeClient {
		FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// if a view already exists then immediately terminate the new one
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			mContentView = new RelativeLayout(ResourcePlayer.this);
			mContentView.setVisibility(View.GONE);
			mCustomViewContainer = new FrameLayout(ResourcePlayer.this);
			mCustomViewContainer.setLayoutParams(LayoutParameters);
			mCustomViewContainer.setBackgroundResource(android.R.color.black);
			view.setLayoutParams(LayoutParameters);
			mCustomViewContainer.addView(view);
			mCustomView = view;
			mCustomViewCallback = callback;
			mCustomViewContainer.setVisibility(View.VISIBLE);
			setContentView(mCustomViewContainer);
		}

		@Override
		public void onHideCustomView() {
			if (mCustomView == null) {
				return;
			} else {
				// Hide the custom view.
				mCustomView.setVisibility(View.GONE);
				// Remove the custom view from its container.
				mCustomViewContainer.removeView(mCustomView);
				mCustomView = null;
				mCustomViewContainer = null;
				mCustomViewCallback.onCustomViewHidden();
				// Show the content view.
				setContentViewLayout.setVisibility(View.VISIBLE);
				setContentView(setContentViewLayout);
			}
		}

	}

	private class getShortUrl extends AsyncTask<Void, String, String> {

		protected void onPreExecute() {
			dialog1 = new ProgressDialog(ResourcePlayer.this);
			dialog1.setTitle("gooru");
			dialog1.setMessage("Please wait while loading...");
			dialog1.setCancelable(false);
			dialog1.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {

			String url2 = "http://concept.goorulearning.org/gooruapi/rest/url/shorten/"
					+ gooruOID
					+ "?sessionToken="
					+ token
					+ "&realUrl=http%3A%2F%2Fconcept.goorulearning.org%2Fbeta%2F%23!resource-play%26id%3D"
					+ gooruOID + "%26pn=resource";
			Log.i("url2", url2);
			try {

				JSONParser jParser = new JSONParser();

				JSONObject json = jParser.getJSONFromUrl(url2);
				String getsurl = json.getString(surl);
				Log.i("getsurl", getsurl);
				return getsurl;

			} catch (Exception e) {
				// TODO: handle exception
				// dialog1.cancel();
			}

			return null;

		}

		protected void onPostExecute(String result) {

			edittext_copyurl.setText(result);
			dialog1.cancel();

		}
	}

	public class httppoststop extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://concept.goorulearning.org/gooruapi/rest/activity/log/665db479-1a38-454c-bf77-20d80394ec94/stop");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs
						.add(new BasicNameValuePair("sessionToken", token));
				nameValuePairs.add(new BasicNameValuePair("contentGooruOid",
						gooruOID));
				nameValuePairs.add(new BasicNameValuePair("eventName",
						"resourceplayerplay"));
				nameValuePairs
						.add(new BasicNameValuePair("parentGooruId", null));
				nameValuePairs
						.add(new BasicNameValuePair("context",
								"%23!%2Fcollection%2F67085b3a-b626-413f-b947-eb3e7f4ee9dc%2Fplay"));
				nameValuePairs
						.add(new BasicNameValuePair("parentEventId", null));
				nameValuePairs.add(new BasicNameValuePair("sessionActivityId",
						null));
				nameValuePairs.add(new BasicNameValuePair(
						" resourceInstanceId", null));
				nameValuePairs
						.add(new BasicNameValuePair("imeiCode", imeicode));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			Log.i(str, str);
			Log.i("result str", "result str");
			return str;

		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something with data retrieved from server in doInBackground
		}
	}

	public class httppoststart extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://concept.goorulearning.org/gooruapi/rest/activity/log/665db479-1a38-454c-bf77-20d80394ec94/start");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs
						.add(new BasicNameValuePair("sessionToken", token));
				nameValuePairs.add(new BasicNameValuePair("contentGooruOid",
						gooruOID));
				nameValuePairs.add(new BasicNameValuePair("eventName",
						"resourceplayerplay"));
				nameValuePairs
						.add(new BasicNameValuePair("parentGooruId", null));
				nameValuePairs
						.add(new BasicNameValuePair("context",
								"%23!%2Fcollection%2F67085b3a-b626-413f-b947-eb3e7f4ee9dc%2Fplay"));
				nameValuePairs
						.add(new BasicNameValuePair("parentEventId", null));
				nameValuePairs.add(new BasicNameValuePair("sessionActivityId",
						null));
				nameValuePairs.add(new BasicNameValuePair(
						" resourceInstanceId", null));
				nameValuePairs
						.add(new BasicNameValuePair("imeiCode", imeicode));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			Log.i(str, str);
			Log.i("result str", "result str");
			return str;

		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something with data retrieved from server in doInBackground
		}
	}

}
