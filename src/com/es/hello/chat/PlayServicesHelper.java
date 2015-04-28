package com.es.hello.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.es.hello.chat.consts.Consts;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;

public class PlayServicesHelper
{

    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static final String PROPERTY_REG_ID = "registration_id";

    private static final String CURRENT_LOGIN_USER_NAME = "current_login_user_name";

    private static final String TAG = "PlayServicesHelper";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleCloudMessaging googleCloudMessaging;

    private Activity activity;

    private String regId;

    private String currentLoginUserName = "";

    public PlayServicesHelper(Activity activity, String CurrentLoginUserName)
    {

	this.activity = activity;
	this.currentLoginUserName = CurrentLoginUserName;
	checkPlayService();
    }

    private void checkPlayService()
    {

	// Check device for Play Services APK. If check succeeds, proceed with
	// GCM registration.
	if (checkPlayServices())
	{
	    googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
	    regId = getRegistrationId();

	    int gcmRegisterIntID = SharePrefsHelper.getPushSubscriptionToSharePrefs(activity);

	    if (gcmRegisterIntID == -1)
	    {

		registerInBackground();
	    }
	    else
	    {
		String userName = getCurrentLoginUserName();
		if (!userName.equalsIgnoreCase(currentLoginUserName))
		{
		    registerInBackground();
		}
	    }
	}
	else
	{
	    Log.i(TAG, "No valid Google Play Services APK found.");
	}
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices()
    {

	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
	if (resultCode != ConnectionResult.SUCCESS)
	{
	    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
	    {
		GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
	    }
	    else
	    {
		Log.i(TAG, "This device is not supported.");
		activity.finish();
	    }
	    return false;
	}
	return true;
    }

    public int getAppVersion()
    {

	try
	{
	    PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
	    return packageInfo.versionCode;
	}
	catch (PackageManager.NameNotFoundException e)
	{
	    // should never happen
	    throw new RuntimeException("Could not get package name: " + e);
	}
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     * 
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId()
    {

	final SharedPreferences prefs = getGCMPreferences();
	String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	if (registrationId.isEmpty())
	{
	    Log.i(TAG, "Registration not found.");
	    return "";
	}
	// Check if app was updated; if so, it must clear the registration ID
	// since the existing regID is not guaranteed to work with the new
	// app version.
	int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	int currentVersion = getAppVersion();
	if (registeredVersion != currentVersion)
	{
	    Log.i(TAG, "App version changed.");
	    return "";
	}
	return registrationId;
    }

    private String getCurrentLoginUserName()
    {

	final SharedPreferences prefs = getGCMPreferences();
	String username = prefs.getString(CURRENT_LOGIN_USER_NAME, "");
	if (username.isEmpty())
	{
	    Log.i(TAG, "Registration not found.");
	    return "";
	}

	return username;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground()
    {

	new AsyncTask<Void, Void, String>()
	{

	    @Override
	    protected String doInBackground(Void... params)
	    {

		String msg = "";
		try
		{
		    if (googleCloudMessaging == null)
		    {
			googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
		    }
		    regId = googleCloudMessaging.register(Consts.PROJECT_NUMBER);
		    msg = "Device registered, registration ID=" + regId;

		    // You should send the registration ID to your server over
		    // HTTP, so it
		    // can use GCM/HTTP or CCS to send messages to your app.
		    Handler h = new Handler(activity.getMainLooper());
		    h.post(new Runnable()
		    {

			@Override
			public void run()
			{

			    subscribeToPushNotifications(regId);
			}
		    });

		    // For this demo: we don't need to send it because the
		    // device will send
		    // upstream messages to a server that echo back the message
		    // using the
		    // 'from' address in the message.

		    // Persist the regID - no need to register again.
		    storeRegistrationId(regId);
		}
		catch (IOException ex)
		{
		    msg = "Error :" + ex.getMessage();
		    // If there is an error, don't just keep trying to register.
		    // Require the user to click a button again, or perform
		    // exponential back-off.
		}
		return msg;
	    }

	    @Override
	    protected void onPostExecute(String msg)
	    {

		Log.i(TAG, msg + "\n");
		Log.i(TAG, msg + "Register GCM success");
		// Toast.makeText(activity, "Register GCM success",
		// Toast.LENGTH_LONG).show();
	    }
	}.execute(null, null, null);
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences()
    {

	// This sample app persists the registration ID in shared preferences,
	// but
	// how you store the regID in your app is up to you.
	return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Subscribe to Push Notifications
     * 
     * @param regId
     *            registration ID
     */
    private void subscribeToPushNotifications(String regId)
    {

	// Create push token with Registration Id for Android
	//
	Log.d(TAG, "subscribing...");

	String deviceId;

	final TelephonyManager mTelephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
	if (mTelephony.getDeviceId() != null)
	{
	    deviceId = mTelephony.getDeviceId(); // *** use for mobiles
	}
	else
	{
	    deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID); // ***
													     // use
													     // for
													     // tablets
	}

	QBMessages.subscribeToPushNotificationsTask(regId, deviceId, QBEnvironment.DEVELOPMENT, new QBEntityCallbackImpl<ArrayList<QBSubscription>>()
	{

	    @Override
	    public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle)
	    {

		Log.d(TAG, "subscribed");

		if (qbSubscriptions != null && !qbSubscriptions.isEmpty())
		    SharePrefsHelper.savePushSubscriptionToSharePrefs(qbSubscriptions.get(0).getId() + "", activity);

	    }

	    @Override
	    public void onError(List<String> strings)
	    {

		Log.d(TAG, "Error subscribed");

	    }
	});
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     * 
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(String regId)
    {

	final SharedPreferences prefs = getGCMPreferences();
	int appVersion = getAppVersion();
	Log.i(TAG, "Saving regId on app version " + appVersion);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(PROPERTY_REG_ID, regId);
	editor.putString(CURRENT_LOGIN_USER_NAME, currentLoginUserName);
	editor.putInt(PROPERTY_APP_VERSION, appVersion);
	editor.commit();
    }
}
