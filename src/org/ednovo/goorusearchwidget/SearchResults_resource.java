package org.ednovo.goorusearchwidget;

/*
 * SearchResults_resource.java 
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



import java.io.IOException;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ednovo.R;
import org.ednovo.shutterbug.FetchableImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class SearchResults_resource extends Activity

{
	ImageView imgViewSections;
	ImageView imgViewGooruSearch;
	ImageView imgViewSettings;
	ImageView imageViewClose;
	ImageView imageViewCategory;
	ImageView imageViewSearch;
	Switch switchResColl;
	RelativeLayout headerSearch;
	EditText editTextSearchResults;
	ProgressDialog dialog;
	InputMethodManager imm;
	String imeicode;
	String searchKeyword;
	View resourcelayout;
	List<String> resUrls = new ArrayList<String>();
	List<String> resTitles = new ArrayList<String>();
	List<String> resCategory = new ArrayList<String>();
	List<String> resDescription = new ArrayList<String>();
	List<String> resGooruOid = new ArrayList<String>();
	List<String> resCat = new ArrayList<String>();
	ArrayList<String> globalresGooruOid = new ArrayList<String>();
	ImageView videoRight, interactiveRight, websiteRight, textbookRight,
			examRight, handoutRight, slideRight, lessonRight;
	HorizontalScrollView videoScroll, interactiveScroll, websiteScroll,
			textbookScroll, examScroll, handoutScroll, slideScroll,
			lessonScroll;
	ArrayList<String> videoresGooruOid = new ArrayList<String>();
	ArrayList<String> interactiveresGooruOid = new ArrayList<String>();
	ArrayList<String> websiteresGooruOid = new ArrayList<String>();
	ArrayList<String> textbookresGooruOid = new ArrayList<String>();
	ArrayList<String> examresGooruOid = new ArrayList<String>();
	ArrayList<String> handoutresGooruOid = new ArrayList<String>();
	ArrayList<String> slideresGooruOid = new ArrayList<String>();
	ArrayList<String> lessonresGooruOid = new ArrayList<String>();
	int videoCount = 1, interactiveCount = 1, websiteCount = 1,
			textbookCount = 1, examCount = 1, handoutCount = 1, slideCount = 1,
			lessonCount = 1;
	Dialog dialog1;
	private SharedPreferences prefsPrivate;
	public static final String PREFS_PRIVATE = "PREFS_PRIVATE";
	String token = "";

	// Flurry Variables
	String resourceType = "";
	String resourceGooruId = "";
	Boolean flag_isPlayerTransition = false;

	@Override
	protected void onStart() {
		super.onStart();

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		imeicode = tm.getDeviceId();

		new httppoststart().execute();
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
	}

	@Override
	protected void onDestroy() {

		new httppoststop().execute();
		super.onDestroy();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_resource);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Bundle extra = getIntent().getExtras();

		if (extra != null) {
			searchKeyword = extra.getString("keyWord").trim();
		}
		prefsPrivate = getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

		token = prefsPrivate.getString("token", "");

		imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
		imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
		editTextSearchResults = (EditText) findViewById(R.id.textViewSearch);
		switchResColl = (Switch) findViewById(R.id.switchResColl);
		dialog1 = new Dialog(this);
		editTextSearchResults.setText(searchKeyword);
		imm = (InputMethodManager) this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		dialog = new ProgressDialog(this);
		dialog.setTitle("gooru");
		dialog.setMessage("Please wait while loading...");
		dialog.setCancelable(false);
		dialog.show();

		if (checkInternetConnection()) {
			new getResources().execute();
		} else {
			showDialog("Please Check Internet connection");
			new getResources().execute();
		}

		// scroll views

		videoScroll = (HorizontalScrollView) findViewById(R.id.videoScroll);
		interactiveScroll = (HorizontalScrollView) findViewById(R.id.interactiveScroll);
		websiteScroll = (HorizontalScrollView) findViewById(R.id.websiteScroll);
		textbookScroll = (HorizontalScrollView) findViewById(R.id.textbookScroll);
		examScroll = (HorizontalScrollView) findViewById(R.id.examScroll);
		handoutScroll = (HorizontalScrollView) findViewById(R.id.handoutScroll);
		slideScroll = (HorizontalScrollView) findViewById(R.id.slideScroll);
		lessonScroll = (HorizontalScrollView) findViewById(R.id.lessonScroll);
		// category image load more resources
		videoRight = (ImageView) findViewById(R.id.videoRight);
		interactiveRight = (ImageView) findViewById(R.id.interactiveRight);
		websiteRight = (ImageView) findViewById(R.id.websiteRight);
		textbookRight = (ImageView) findViewById(R.id.textbookRight);
		examRight = (ImageView) findViewById(R.id.examRight);
		handoutRight = (ImageView) findViewById(R.id.handoutRight);
		slideRight = (ImageView) findViewById(R.id.slideRight);
		lessonRight = (ImageView) findViewById(R.id.lessonRight);

		videoRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					videoCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5Videos().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		interactiveRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					interactiveCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5interactive().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});

		imageViewSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String searchKeyword = editTextSearchResults.getText()
						.toString().trim();

				if (searchKeyword.length() > 0) {

					Intent intentResResults = new Intent(getBaseContext(),
							SearchResults_resource.class);
					searchKeyword = editTextSearchResults.getText().toString()
							.trim();
					Log.i("Search :", searchKeyword);
					Bundle extras = new Bundle();
					extras.putString("keyWord", searchKeyword);
					intentResResults.putExtras(extras);
					startActivity(intentResResults);
					finish();

				} else {
					dialog1.setTitle("Please enter a Search keyword");
					dialog1.show();
				}
			}
		});

		websiteRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					websiteCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5website().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		textbookRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					textbookCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5textbook().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		examRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					examCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5exam().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		handoutRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					handoutCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5handout().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		slideRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					slideCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5slide().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});
		lessonRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					lessonCount++;
					dialog.setTitle("gooru");
					dialog.setMessage("Please wait while loading...");
					dialog.setCancelable(false);
					dialog.show();
					new getNext5lesson().execute();
				} else {
					showDialog("Please Check Internet connection");
				}

			}
		});

		editTextSearchResults.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						String searchKeyword = editTextSearchResults.getText()
								.toString().trim();

						if (searchKeyword.length() > 0) {

							Log.i("Resources", searchKeyword);
							Intent intentResResults = new Intent(
									getBaseContext(),
									SearchResults_resource.class);

							Bundle extras = new Bundle();
							extras.putString("keyWord", searchKeyword);

							intentResResults.putExtras(extras);
							startActivity(intentResResults);
							finish();
						} else {
							dialog1.setTitle("Please enter a Search keyword");
							dialog1.show();
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

	/**
	 * @function name :checkInternetConnection
	 * 
	 *           This function is used to check internet connection is available
	 *           or not
	 * 
	 * @param ----
	 * 
	 * @return true or false
	 * 
	 * 
	 */

	private boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if ((cm.getActiveNetworkInfo() != null)
				&& (cm.getActiveNetworkInfo().isAvailable())
				&& (cm.getActiveNetworkInfo().isConnected())) {
			return true;
		} else {
			Log.i("", "Internet Connection Not Present");
			return false;
		}
	}

	/**
	 * @function name : showDialog
	 * 
	 *           This function is used to show alert dialog
	 * 
	 * @param String
	 *            message to show
	 * 
	 * @return void
	 * 
	 * 
	 */

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

	/**
	 * @function name : getResources
	 * 
	 *           This function is used to get all related resource of
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */

	private class getResources extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);
			try {

				Log.i("URl",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum=1&pageSize=5&queryType=multiCategory");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum=1&pageSize=5&queryType=multiCategory");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			try {

			} catch (Exception e) {
			}
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);

					if (json.has("Website")) {
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						JSONArray Website = json.getJSONArray("Website");
						if (Website.length() > 0) {

							int size = Website.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Website.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");
								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								websiteresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);

							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {

								createwebsiteLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutWebsite);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							websiteRight.setVisibility(View.GONE);

						}

						JSONArray Video = json.getJSONArray("Video");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Video.length() > 0) {
							int size = Video.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Video.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								videoresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");

								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								String resNativeUrl = obj.getString("url");
								// Check if youtube

								String thumbnailUrl = "";
								if (resNativeUrl.contains("youtube")) {

									int k = resNativeUrl.indexOf("v=");
									if (k > 0) {
										String youtubeId = resNativeUrl
												.substring(k);
										if (resNativeUrl.contains("&")) {
											int h = youtubeId.indexOf("&");

											if (h > 0) {
												youtubeId = youtubeId
														.substring(0, h);
											}
										}
										Log.i("Youtube id :", youtubeId);
										String tmpId[] = youtubeId.split("=");
										thumbnailUrl = "http://img.youtube.com/vi/"
												+ tmpId[1] + "/1.jpg";
									}
									url = thumbnailUrl;
								}

								resUrls.add(url);
								String title = obj.getString("title");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {
								createVideoLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);

							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutVideo);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));

							videoRight.setVisibility(View.GONE);

						}

						JSONArray Textbook = json.getJSONArray("Textbook");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Textbook.length() > 0) {
							int size = Textbook.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Textbook.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								textbookresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {

								createtextbookLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutTextbook);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));

							textbookRight.setVisibility(View.GONE);
						}

				
						JSONArray Slide = json.getJSONArray("Slide");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Slide.length() > 0) {
							int size = Slide.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Slide.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								slideresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {

								createslideLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutSlide);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							slideRight.setVisibility(View.GONE);

						}

					
						JSONArray Interactive = json
								.getJSONArray("Interactive");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Interactive.length() > 0) {
							int size = Interactive.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Interactive.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								interactiveresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {
								createinteractiveLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutInteractive);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							interactiveRight.setVisibility(View.GONE);

						}

					
						JSONArray Exam = json.getJSONArray("Exam");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Exam.length() > 0) {
							int size = Exam.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Exam.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								examresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {

								createexamLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutExam);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							examRight.setVisibility(View.GONE);

						}

				

						JSONArray Handout = json.getJSONArray("Handout");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Handout.length() > 0) {
							int size = Handout.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Handout.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");
								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								handoutresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {
								createhandoutLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutHandout);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							handoutRight.setVisibility(View.GONE);

						}

				

						JSONArray Lesson = json.getJSONArray("Lesson");
						resGooruOid.clear();
						resUrls.clear();
						resTitles.clear();
						resDescription.clear();
						resCategory.clear();
						if (Lesson.length() > 0) {
							int size = Lesson.length();
							if (size == 6) {
								size = size - 1;
							}
							for (int i = 0; i < size; i++) {
								JSONObject obj = Lesson.getJSONObject(i);
								String auri = nullCheck(
										obj.getString("assetURI"), "None Added");

								String des = nullCheck(
										obj.getString("description"),
										"None Added");

								resDescription.add(des);
								String fol = nullCheck(obj.getString("folder"),
										"None Added");
								String gid = obj.getString("gooruOid");
								resGooruOid.add(gid);
								lessonresGooruOid.add(gid);
								JSONObject obj1 = obj
										.getJSONObject("resourceType");
								String name = obj1.getString("name");
								JSONObject obja = obj
										.getJSONObject("resourceSource");
								String nameDom = obja.getString("sourceName");
								nameDom = nullCheck(nameDom, "none added");
								resCategory.add(nameDom);
								JSONObject obj2 = obj
										.getJSONObject("thumbnails");
								String url = obj2.getString("url");

								resUrls.add(url);
								String title = obj.getString("title");
								title = nullCheck(title, "none added");
								resTitles.add(title);
							}
							if (!resUrls.isEmpty() || resUrls.size() != 0) {
								createlessonLayout(resUrls, resTitles,
										resCategory, resDescription,
										resGooruOid);
							}
						} else {
							LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutLesson);
							tempLayout
									.addView(
											createResourceEmptyView(),
											new LinearLayout.LayoutParams(
													LinearLayout.LayoutParams.WRAP_CONTENT,
													LinearLayout.LayoutParams.WRAP_CONTENT));
							lessonRight.setVisibility(View.GONE);

						}

				
					} else {

						dialog.dismiss();
						createAllEmpty();
					}
					dialog.dismiss();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Log.i("result :", "" + result);

		}
	}

	public LinearLayout createResourceEmptyView() {

		LinearLayout scrollChild = new LinearLayout(this);
		scrollChild.setPadding(10, 0, 10, 0);
		resourcelayout = LayoutInflater.from(this).inflate(
				R.layout.resource_view, null);
		TextView title = (TextView) resourcelayout
				.findViewById(R.id.textViewTitle);
		title.setText("Sorry! No Results Found");
		title.setTextColor(getResources().getColor(R.color.Grey));
		title.setPadding(50, 50, 0, 10);
		title.setTextSize(25);

		scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		scrollChild.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		return scrollChild;
	}

	/**
	 * @function name :nullCheck
	 * 
	 *           This function is used to null check.
	 * 
	 * @param String
	 *            ,String
	 * 
	 * @return String
	 * 
	 * 
	 */

	public String nullCheck(String incoming, String toReplace) {

		String returnString;
		if (incoming.isEmpty() || incoming.length() == 0
				|| incoming.equalsIgnoreCase("null")) {

			returnString = toReplace;
		} else {
			returnString = incoming;
		}

		return returnString;

	}

	public void createAllEmpty() {
		videoRight.setVisibility(View.GONE);
		interactiveRight.setVisibility(View.GONE);
		websiteRight.setVisibility(View.GONE);
		textbookRight.setVisibility(View.GONE);
		examRight.setVisibility(View.GONE);
		handoutRight.setVisibility(View.GONE);
		slideRight.setVisibility(View.GONE);
		lessonRight.setVisibility(View.GONE);
		LinearLayout tempLayout = (LinearLayout) findViewById(R.id.layoutHandout);
		tempLayout.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout1 = (LinearLayout) findViewById(R.id.layoutExam);
		tempLayout1.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout2 = (LinearLayout) findViewById(R.id.layoutInteractive);
		tempLayout2.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout3 = (LinearLayout) findViewById(R.id.layoutSlide);
		tempLayout3.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout4 = (LinearLayout) findViewById(R.id.layoutTextbook);
		tempLayout4.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout5 = (LinearLayout) findViewById(R.id.layoutVideo);
		tempLayout5.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout6 = (LinearLayout) findViewById(R.id.layoutWebsite);
		tempLayout6.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout tempLayout7 = (LinearLayout) findViewById(R.id.layoutLesson);
		tempLayout7.addView(createResourceEmptyView(),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * @function name : createVideoLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */

	public void createVideoLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutVideo);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		int intial = 0;
		if (videoresGooruOid.size() > 4) {
			intial = videoresGooruOid.size() - 5;
		} else {
			videoRight.setVisibility(View.GONE);
		}

		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.video_ico));
			intial++;
			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + videoresGooruOid.size());
					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Video";
					resourceGooruId = videoresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", videoresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createinteractiveLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// interactive
	public void createinteractiveLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutInteractive);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (interactiveresGooruOid.size() > 4) {
			intial = interactiveresGooruOid.size() - 5;
		} else {
			interactiveRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.interactive_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :", ""
							+ interactiveresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Interactive";
					resourceGooruId = interactiveresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", interactiveresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createwebsiteLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// website
	public void createwebsiteLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutWebsite);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (websiteresGooruOid.size() > 4) {
			intial = websiteresGooruOid.size() - 5;
		} else {
			websiteRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.website_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + websiteresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Website";
					resourceGooruId = websiteresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", websiteresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createtextbookLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// textbook
	public void createtextbookLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutTextbook);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;

		int intial = 0;
		if (textbookresGooruOid.size() > 4) {
			intial = textbookresGooruOid.size() - 5;
		} else {
			textbookRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.textbook_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + textbookresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Textbook";
					resourceGooruId = textbookresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", textbookresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createexamLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */

	// exam
	public void createexamLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutExam);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (examresGooruOid.size() > 4) {
			intial = examresGooruOid.size() - 5;
		} else {
			examRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.exam_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + examresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Exam";
					resourceGooruId = examresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", examresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createhandoutLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// handout
	public void createhandoutLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutHandout);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (handoutresGooruOid.size() > 4) {
			intial = handoutresGooruOid.size() - 5;
		} else {
			handoutRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.handouts_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + handoutresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Handout";
					resourceGooruId = handoutresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", handoutresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	/**
	 * @function name :createslideLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// slide
	public void createslideLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutSlide);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (slideresGooruOid.size() > 4) {
			intial = slideresGooruOid.size() - 5;
		} else {
			slideRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.slides_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + slideresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Slide";
					resourceGooruId = slideresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", slideresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);
				}
			});
		}

	}

	/**
	 * @function name : createlessonLayout
	 * 
	 *           This function is used to add 5 more resources in horizontallist
	 *           view.
	 * 
	 * @param 5 list(resUrls, resTitles, resCategory, resDescription,
	 *        resGooruOid);
	 * 
	 * @return void
	 * 
	 * 
	 */
	// lesson
	public void createlessonLayout(List<String> resincUrls,
			List<String> resincTitle, List<String> resincCategory,
			List<String> resincDescription, List<String> resincGooruid) {

		LinearLayout scrollChild = (LinearLayout) findViewById(R.id.layoutLesson);
		int size = resincUrls.size();
		Log.i("Size to check :", "" + size);
		List<String> resTempincGooruid = new ArrayList<String>();
		resTempincGooruid = resincGooruid;
		int intial = 0;
		if (lessonresGooruOid.size() > 4) {
			intial = lessonresGooruOid.size() - 5;
		} else {
			lessonRight.setVisibility(View.GONE);
		}
		Log.i("intial", "" + intial);
		for (int i = 0; i < size; i++) {
			resourcelayout = LayoutInflater.from(this).inflate(
					R.layout.resource_view, null);
			TextView title = (TextView) resourcelayout
					.findViewById(R.id.textViewTitle);
			title.setText(nullCheck(resincTitle.get(i), "None Added"));

			TextView category = (TextView) resourcelayout
					.findViewById(R.id.textViewSource);
			category.setText(nullCheck(resincCategory.get(i), "None Added"));

			TextView descr = (TextView) resourcelayout
					.findViewById(R.id.textViewDescription);
			descr.setText(nullCheck(resincDescription.get(i), "None Added"));

			FetchableImageView image = (FetchableImageView) resourcelayout
					.findViewById(R.id.imgViewRes);
			image.setImage(resUrls.get(i), R.drawable.resourcedefault);

			resourcelayout.setTag(intial);
			imageViewCategory = (ImageView) resourcelayout
					.findViewById(R.id.imageViewCategory);
			imageViewCategory.setImageDrawable(getResources().getDrawable(
					R.drawable.lesson_ico));
			intial++;

			scrollChild.addView(resourcelayout, new LinearLayout.LayoutParams(
					250, 180));
			resourcelayout.setPadding(10, 0, 10, 0);
			resourcelayout.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int s = (Integer) v.getTag();
					Log.i("Size  completeGooruOid :",
							"" + lessonresGooruOid.size());

					// Flurry Log
					flag_isPlayerTransition = true;
					resourceType = "Lesson";
					resourceGooruId = lessonresGooruOid.get(s);

					Intent intentResPlayer = new Intent(getBaseContext(),
							ResourcePlayer.class);
					Bundle extras = new Bundle();
					extras.putInt("key", s);
					extras.putString("token", token);
					extras.putString("searchkey", searchKeyword);
					extras.putStringArrayList("goor", lessonresGooruOid);
					intentResPlayer.putExtras(extras);
					startActivity(intentResPlayer);

				}
			});
		}

	}

	// -------end-------------

	// Indiviual api calls for diff category types

	/**
	 * @function name :getNext5Video
	 * 
	 *           This function is used to get 5 next Video resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */
	// Video
	private class getNext5Videos extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ videoCount
								+ "&pageSize=5&queryType=Video");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ videoCount
								+ "&pageSize=5&queryType=single&category=Video");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							videoresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createVideoLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);

						new Handler().postDelayed(new Runnable() {
							public void run() {
								videoScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

							}
						}, 100L);

					} else {
						videoRight.setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5interactive
	 * 
	 *           This function is used to get 5 next interactive resource
	 *           related to searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */

	// for interactive
	private class getNext5interactive extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5 interactive",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ interactiveCount
								+ "&pageSize=5&queryType=single&category=interactive");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ interactiveCount
								+ "&pageSize=5&queryType=single&category=Interactive");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							interactiveresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createinteractiveLayout(resUrls, resTitles,
								resCategory, resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {
								interactiveScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					} else {

						interactiveRight.setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5website
	 * 
	 *           This function is used to get 5 next website resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */
	// for website
	private class getNext5website extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ websiteCount
								+ "&pageSize=5&queryType=website");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ websiteCount
								+ "&pageSize=5&queryType=single&category=Website");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							websiteresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createwebsiteLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {
								websiteScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					} else {
						websiteRight.setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5textbook
	 * 
	 *           This function is used to get 5 next textbook resource related
	 *           to searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */
	// for textbook
	private class getNext5textbook extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ textbookCount
								+ "&pageSize=5&queryType=textbook");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ textbookCount
								+ "&pageSize=5&queryType=single&category=Textbook");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							textbookresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createtextbookLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {
								textbookScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5exam
	 * 
	 *           This function is used to get 5 next exam resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */

	// for exam
	private class getNext5exam extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ examCount
								+ "&pageSize=5&queryType=single&category=exam");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ examCount
								+ "&pageSize=5&queryType=single&category=Exam");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							examresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createexamLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {
								examScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5handout
	 * 
	 *           This function is used to get 5 next handout resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */
	// for handout
	private class getNext5handout extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ handoutCount
								+ "&pageSize=5&queryType=single&category=handout");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ handoutCount
								+ "&pageSize=5&queryType=single&category=Handout");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							handoutresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createhandoutLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {

								handoutScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5slide
	 * 
	 *           This function is used to get 5 next slide resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */

	// for slide
	private class getNext5slide extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ slideCount
								+ "&pageSize=5&queryType=single&category=slide");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ slideCount
								+ "&pageSize=5&queryType=single&category=Slide");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {
				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							slideresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createslideLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);
						new Handler().postDelayed(new Runnable() {
							public void run() {

								slideScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 100L);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :getNext5lesson
	 * 
	 *           This function is used to get 5 next lesson resource related to
	 *           searchkeyword from API
	 * 
	 * @param SessionToken
	 *            , searchKeyword
	 * 
	 * @return void
	 * 
	 * 
	 */

	// for lesson
	private class getNext5lesson extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responsedata = null;
			searchKeyword = searchKeyword.replace(" ", "%20");
			searchKeyword = searchKeyword.replaceAll("[^a-zA-Z0-9]+%", "");
			Log.i("searchKeyword", searchKeyword);

			try {
				Log.i("Next 5",
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ lessonCount
								+ "&pageSize=5&queryType=single&category=lesson");
				WebService webdata = new WebService(
						"http://concept.goorulearning.org/gooruapi/rest/search/resource?sessionToken="
								+ token
								+ "&query="
								+ searchKeyword
								+ "&pageNum="
								+ lessonCount
								+ "&pageSize=5&queryType=single&category=lesson");

				responsedata = webdata.webInvoke(null, "", null);

				Log.i("response", "" + responsedata);

			} catch (Exception e) {
				e.printStackTrace();
				responsedata = "Please try again";
			}

			return responsedata;
		}

		protected void onPostExecute(String result) {
			Log.i("LOGGER", "...Done");
			imm.hideSoftInputFromWindow(editTextSearchResults.getWindowToken(),
					0);
			if (result == null) {

				createAllEmpty();
				dialog.dismiss();
			} else if (result.equals("Please try again")) {
				createAllEmpty();
				dialog.dismiss();
			} else {

				try {

					JSONObject json = new JSONObject(result);
					resGooruOid.clear();
					resUrls.clear();
					resTitles.clear();
					resDescription.clear();
					resCategory.clear();
					JSONArray Video = json.getJSONArray("searchResults");
					if (Video.length() > 0) {
						int size = Video.length();
						for (int i = 0; i < size; i++) {
							JSONObject obj = Video.getJSONObject(i);
							String auri = nullCheck(obj.getString("assetURI"),
									"None Added");

							String des = nullCheck(
									obj.getString("description"), "None Added");
							resDescription.add(des);
							String fol = nullCheck(obj.getString("folder"),
									"None Added");
							String gid = obj.getString("gooruOid");
							resGooruOid.add(gid);
							lessonresGooruOid.add(gid);

							JSONObject obj1 = obj.getJSONObject("resourceType");
							String name = obj1.getString("name");
							JSONObject obja = obj
									.getJSONObject("resourceSource");
							String nameDom = obja.getString("sourceName");

							resCategory.add(nameDom);
							JSONObject obj2 = obj.getJSONObject("thumbnails");
							String url = obj2.getString("url");

							String resNativeUrl = obj.getString("url");
							// Check if youtube

							String thumbnailUrl = "";
							if (resNativeUrl.contains("youtube")) {

								int k = resNativeUrl.indexOf("v=");
								if (k > 0) {
									String youtubeId = resNativeUrl
											.substring(k);
									if (resNativeUrl.contains("&")) {
										int h = youtubeId.indexOf("&");

										if (h > 0) {
											youtubeId = youtubeId.substring(0,
													h);
										}
									}
									Log.i("Youtube id :", youtubeId);
									String tmpId[] = youtubeId.split("=");
									thumbnailUrl = "http://img.youtube.com/vi/"
											+ tmpId[1] + "/1.jpg";
								}
								url = thumbnailUrl;
							}
							resUrls.add(url);
							String title = obj.getString("title");
							resTitles.add(title);
						}

						createlessonLayout(resUrls, resTitles, resCategory,
								resDescription, resGooruOid);

						new Handler().postDelayed(new Runnable() {
							public void run() {

								lessonScroll
										.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
							}
						}, 200L);

					} else {

						lessonRight.setVisibility(View.GONE);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			Log.i("result :", "" + result);

		}
	}

	/**
	 * @function name :onBackPressed
	 * 
	 *           This function is used to control back button of the device.
	 * 
	 * @param
	 * 
	 * @return void
	 * 
	 * 
	 */

	@Override
	public void onBackPressed() {

		finish();

	}

	public class httppoststart extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://www.goorulearning.org/gooruapi/rest/activity/log/665db479-1a38-454c-bf77-20d80394ec94/start");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs
						.add(new BasicNameValuePair("sessionToken", token));
				nameValuePairs.add(new BasicNameValuePair("contentGooruOid",
						null));
				nameValuePairs.add(new BasicNameValuePair("eventName",
						"Search Key Word" + searchKeyword));
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

	public class httppoststop extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://www.goorulearning.org/gooruapi/rest/activity/log/665db479-1a38-454c-bf77-20d80394ec94/stop");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs
						.add(new BasicNameValuePair("sessionToken", token));
				nameValuePairs.add(new BasicNameValuePair("contentGooruOid",
						null));
				nameValuePairs.add(new BasicNameValuePair("eventName",
						"Search Key Word was" + searchKeyword));
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
