package com.example.googlecontactsintegration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gdata.client.Query;
import com.google.gdata.client.Service;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import dmax.dialog.SpotsDialogg;

public class MainActivity extends Activity {

	private static String CLIENT_ID = "320788304569-p8kdatqvkma1rmkceiuapmf18dun8cn4.apps.googleusercontent.com";
	// Use your own client id
	private static String CLIENT_SECRET = "5_uWMIWqs9T2B6G6JG9CTaLk";
	// Use your own client secret
	private static String REDIRECT_URI = "http://localhost";
	private static String GRANT_TYPE = "authorization_code";
	private static String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	private static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	// https://www.googleapis.com/auth/urlshortener
	private static String OAUTH_SCOPE = "https://www.google.com/m8/feeds";
	// Change the Scope as you need
	WebView web;
	ArrayList<DataModel> list = new ArrayList<DataModel>();
	String tok;
	ContactsService service;
	ProgressDialog progressdialog;
	Button auth;
	SharedPreferences pref;
	TextView Access;
	static int index = 1;
	ListView lv_list;
	boolean flag = false;
	int totalcount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv_list = (ListView) findViewById(R.id.lv_values);
		lv_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					if (lv_list.getLastVisiblePosition() == lv_list
							.getAdapter().getCount() - 1
							&& lv_list.getChildAt(lv_list.getChildCount() - 1)
									.getBottom() <= lv_list.getHeight()) {
						System.out.println("listview count"
								+ lv_list.getCount());
						if (totalcount > lv_list.getCount()) {
							new GetData().executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, "");
						} else {
							CommonClass.ShowCroutan(MainActivity.this,
									"No more contacts to fetch");
						}

					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		pref = getSharedPreferences("AppPref", MODE_PRIVATE);
		auth = (Button) findViewById(R.id.btn_login);
		auth.setOnClickListener(new View.OnClickListener() {
			Dialog auth_dialog;

			@SuppressLint("SetJavaScriptEnabled")
			@Override
			public void onClick(View arg0) {
				if (CommonClass.checkInternetConnection(MainActivity.this)) {
					auth_dialog = new Dialog(MainActivity.this);
					auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					auth_dialog.setContentView(R.layout.auth_dialog);
					web = (WebView) auth_dialog.findViewById(R.id.webv);
					web.getSettings().setJavaScriptEnabled(true);
					web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI
							+ "&response_type=code&client_id=" + CLIENT_ID
							+ "&scope=" + OAUTH_SCOPE);
					web.setWebViewClient(new WebViewClient() {

						boolean authComplete = false;
						Intent resultIntent = new Intent();

						@Override
						public void onPageStarted(WebView view, String url,
								Bitmap favicon) {
							super.onPageStarted(view, url, favicon);
							SpotsDialogg.ShowDialog(MainActivity.this);
						}

						String authCode;

						@Override
						public void onPageFinished(WebView view, String url) {
							super.onPageFinished(view, url);
							SpotsDialogg.DissmissDialog();
							if (url.contains("?code=") && authComplete != true) {
								Uri uri = Uri.parse(url);
								authCode = uri.getQueryParameter("code");
								Log.i("", "CODE : " + authCode);
								authComplete = true;
								resultIntent.putExtra("code", authCode);
								MainActivity.this.setResult(Activity.RESULT_OK,
										resultIntent);
								setResult(Activity.RESULT_CANCELED,
										resultIntent);

								SharedPreferences.Editor edit = pref.edit();
								edit.putString("Code", authCode);
								edit.commit();
								auth_dialog.dismiss();
								if (CommonClass
										.checkInternetConnection(MainActivity.this)) {
									new TokenGet().execute();
								} else {
									CommonClass.ShowCroutan(MainActivity.this,
											"No internet connection!");
								}

								Toast.makeText(getApplicationContext(),
										"Authorization Code is: " + authCode,
										Toast.LENGTH_SHORT).show();
							} else if (url.contains("error=access_denied")) {
								Log.i("", "ACCESS_DENIED_HERE");
								resultIntent.putExtra("code", authCode);
								authComplete = true;
								setResult(Activity.RESULT_CANCELED,
										resultIntent);
								CommonClass.ShowCroutan(MainActivity.this,
										"Error Occured!");

								auth_dialog.dismiss();
							}
						}
					});
					auth_dialog.show();
					auth_dialog.setCancelable(true);
				} else {
					CommonClass.ShowCroutan(MainActivity.this,
							"No internet connection!");
				}

			}
		});

	}

	private class TokenGet extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
		String Code;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			SpotsDialogg.ShowDialog(MainActivity.this);
			Code = pref.getString("Code", "");
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			GetAccessToken jParser = new GetAccessToken();
			JSONObject json = jParser.gettoken(TOKEN_URL, Code, CLIENT_ID,
					CLIENT_SECRET, REDIRECT_URI, GRANT_TYPE);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			SpotsDialogg.DissmissDialog();
			if (json != null) {

				try {

					tok = json.getString("access_token");
					String expire = json.getString("expires_in");
					String refresh = json.getString("refresh_token");

					Log.d("Token Access", tok);
					Log.d("Expire", expire);
					Log.d("Refresh", refresh);
					if (CommonClass.checkInternetConnection(MainActivity.this)) {
						new GetData().execute("");
					} else {
						CommonClass.ShowCroutan(MainActivity.this,
								"No internet connection!");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				CommonClass.ShowCroutan(MainActivity.this, "Network Error!");
				SpotsDialogg.DissmissDialog();
			}
		}
	}

	class GetData extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;
		String data;
		String namee = null;
		Bitmap bitmapp;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			SpotsDialogg.DissmissDialog();
			DataDisplayAdapter adapter = new DataDisplayAdapter(list,
					MainActivity.this);
			lv_list.setAdapter(adapter);
			System.out.println("size of listview" + lv_list.getCount());
			lv_list.setSelection(index - 1);
			index = lv_list.getCount() + 1;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			SpotsDialogg.ShowDialog(MainActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			service = new ContactsService(tok);// add here your access token
			service.setAuthSubToken(tok);// add here your access token
			String photoLinkHref = null;
			String emailaddress = "";
			try {
				Query query = new Query(
						new URL(
								"https://www.google.com/m8/feeds/contacts/default/full"));
				query.setMaxResults(50);
				System.out.println("index value" + index);
				query.setStartIndex(index);
				query.setStringCustomParameter("showdeleted", "false");
				query.setStringCustomParameter("requirealldeleted", "false");
				ContactFeed resultFeed = service.getFeed(query,
						ContactFeed.class);
				if (flag == false) {
					totalcount = resultFeed.getEntries().size();
					System.out.println("size" + resultFeed.getEntries().size());
					flag = true;
				}

				for (int i = 0; i < resultFeed.getEntries().size(); i++) {
					ContactEntry entry = resultFeed.getEntries().get(i);
					DataModel model = new DataModel();
					if (entry.hasName()) {
						Name name = entry.getName();

						if (name.hasFullName()) {
							String fullNameToDisplay = name.getFullName()
									.getValue();
							/*
							 * if (name.getFullName().hasYomi()) {
							 * fullNameToDisplay += "(" +
							 * name.getFullName().getYomi() + ")"; }
							 */
							System.out.println("fullname" + fullNameToDisplay);
							namee = fullNameToDisplay;
							model.setName(fullNameToDisplay);
						}
						Link photoLink = entry.getContactPhotoLink();
						photoLinkHref = photoLink.getHref();
						System.out
								.println("Photo Link:" + photoLinkHref + "\n");
						if (photoLink.getEtag() != null) {
							Service.GDataRequest request = service
									.createLinkQueryRequest(photoLink);
							request.execute();
							InputStream in = request.getResponseStream();
							Bitmap bitmap = BitmapFactory.decodeStream(in);
							if (bitmap != null) {
								bitmapp = bitmap;
								System.out.println("bitmap" + bitmap);
								model.setBm(bitmap);

							}
						}
						if (entry.getEmailAddresses() != null
								&& entry.getEmailAddresses().size() > 0) {
							List<Email> emails = entry.getEmailAddresses();
							for (int x = 0; x < emails.size(); x++) {
								if (x == 0) {
									model.setEmail(entry.getEmailAddresses()
											.get(0).getAddress());
									list.add(model);
								} else {
									DataModel modell = new DataModel();
									modell.setBm(bitmapp);
									modell.setName(namee);
									modell.setEmail(emails.get(x).getAddress());
									list.add(modell);
								}
							}
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}
}
