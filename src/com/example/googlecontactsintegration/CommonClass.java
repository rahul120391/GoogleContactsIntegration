package com.example.googlecontactsintegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CommonClass extends Activity {

	static SharedPreferences prefs,sharedprefs;
	public static AlertDialog alertDialog;
	public static final String APP_NAME="DLR";
    public static final String APP_URL = "http://DLR:solarpower@sun.promaticstechnologies.com/";
    public static final String PROFILE_IMG="http://DLR:solarpower@sun.promaticstechnologies.com/img/profile_imgs/";
    public static final String Image_Path="http://DLR:solarpower@sun.promaticstechnologies.com/img/logos/original/";
	//public static final String APP_URL = "http://192.168.0.101/DLR/";
	public static String networkError = "No network connection";

	public static void showDialog(Context context, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		alertDialogBuilder.setTitle(R.string.app_name);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.cancel();
					}
				});

		alertDialog = alertDialogBuilder.show();
		TextView messageText = (TextView) alertDialog
				.findViewById(android.R.id.message);
		messageText.setText(message);
		messageText.setGravity(Gravity.CENTER);
		alertDialog.show();
	}

	public static boolean checkInternetConnection(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	public static String getJSON(String rurl) {
		StringBuilder builder = new StringBuilder();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpClient client = new DefaultHttpClient(httpParameters);

		try {
			String url = removeSpacesFromUrl(rurl);
			System.out.println(url);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setParams(httpParameters);
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			System.out.println("status code"+statusCode);
			if (statusCode == HttpURLConnection.HTTP_OK) {
		
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				is.close();
				System.out.println(builder.toString());
				return builder.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;

	}
  
	public static String getJSONFROMURL(String rurl) {
		StringBuilder builder = new StringBuilder();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpClient client = new DefaultHttpClient(httpParameters);

		try {
			String url = removeSpacesFromUrl(rurl);
			System.out.println(url);
			HttpPost httpGet = new HttpPost(url);
			httpGet.setParams(httpParameters);
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			System.out.println("status code"+statusCode);
			if (statusCode == HttpURLConnection.HTTP_OK) {
		
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				is.close();
				System.out.println(builder.toString());
				return builder.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;

	}
	public static String removeSpacesFromUrl(String url) {

		url = url.replaceAll(" ", "%20");
		return url;
	}

	public static String getJSONFromUrl(String url,
			List<BasicNameValuePair> data) {
        UrlEncodedFormEntity entity;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
		HttpConnectionParams.setSoTimeout(httpParameters, 20000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		String urls = APP_URL + url;
		urls = removeSpacesFromUrl(urls);
		android.util.Log.v("URL", urls);
		try {
			HttpPost httpPost = new HttpPost(urls);
			httpPost.setParams(httpParameters);
			httpPost.setHeader("Authorization",getB64Auth("DLR","solarpower"));
			entity=new UrlEncodedFormEntity(data);
			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				System.out.println("inside this");
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
                System.out.println(sb.toString());
				is.close();
				return sb.toString();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getB64Auth (String login, String pass) 
	{
		   String source=login+":"+pass;
		   String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
		   return ret;
		 }
	
	public static void ShowCroutan(Context context,String message){
		Crouton.makeText((Activity) context,message ,Style.ALERT).show();
	}
}
