package com.example.googlecontactsintegration;

public class Config {
   
	public static final String CONSUMER_KEY="842537427973-a381vrch0t5cgrgvtr02lik77a5bc8o7.apps.googleusercontent.com";
	public static final String CONSUMER_SECRET="EEOeMFHQpLtaHDU4Rr8k-l3N";
	public static final String REDIRECT_URL="http://localhost";
	public static final String	APP_NAME   = "GoogleContacts";
	public static final String	OAUTH_CALLBACK_SCHEME	= "GoogleContacts";
	public static final String	OAUTH_CALLBACK_HOST		= "rahul";
	public static final String	OAUTH_CALLBACK_URL		=  OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	public static final String ENCODING= "UTF-8";
	public static final String SCOPE 			= "https://www.google.com/m8/feeds/";
	public static final String REQUEST_URL 		= "https://www.google.com/accounts/OAuthGetRequestToken";
	public static final String ACCESS_URL 		= "https://www.google.com/accounts/OAuthGetAccessToken";  
	public static final String AUTHORIZE_URL 	= "https://www.google.com/accounts/OAuthAuthorizeToken";
	public static final String GET_CONTACTS_FROM_GOOGLE_REQUEST= "https://www.google.com/m8/feeds/contacts/default/full?alt=json";
}
