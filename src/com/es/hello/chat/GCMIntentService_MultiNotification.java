package com.es.hello.chat;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.es.hello.chat.consts.Consts;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.core.PrivateChatManagerImpl;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.activities.ChatActivity;
import com.es.hello.chat.ui.activities.DialogsActivity;
import com.es.hello.chat.ui.fragments.UsersFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lat.hello.chat.R;
import com.quickblox.users.model.QBUser;

public class GCMIntentService_MultiNotification extends IntentService
{

    public static final int NOTIFICATION_ID = 1;

    private static final String TAG = GCMIntentService.class.getSimpleName();

    private NotificationManager notificationManager;

    public static final String ACTION_BROADCAST_RECEIVER = "com.hellochat.service.broad.receive";

    public static final String ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES = "com.hellochat.service.broad.receive.groupprofilechanges";

    // QBDialog dialog;

    String mess = "";

    String user = "";

    String dialog_ID = "";

    String mess_ID = "";

    String sender_ID = "";

    String recipient_ID = "";

    String dialog_Type = "";

    String attach_URL = "";

    String blockUserID = "";

    String notify_group_profile_change_dialog_id = "";

    public GCMIntentService_MultiNotification()
    {

	super(Consts.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

	Log.i(TAG, "new push");

	Bundle extras = intent.getExtras();
	GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
	// The getMessageType() intent parameter must be the intent you received
	// in your BroadcastReceiver.
	String messageType = googleCloudMessaging.getMessageType(intent);

	if (!extras.isEmpty())
	{ // has effect of unparcelling Bundle
	    /*
	     * Filter messages based on message type. Since it is likely that
	     * GCM will be extended in the future with new message types, just
	     * ignore any message types you're not interested in, or that you
	     * don't recognize.
	     */
	    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
	    {
		processNotification(Consts.GCM_SEND_ERROR, extras);
	    }
	    else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
	    {
		processNotification(Consts.GCM_DELETED_MESSAGE, extras);
		// If it's a regular GCM message, do some work.
	    }
	    else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
	    {
		// Post notification of received message.
		processNotification(Consts.GCM_RECEIVED, extras);
		Log.i(TAG, "Received: " + extras.toString());
	    }
	}
	// Release the wake lock provided by the WakefulBroadcastReceiver.
	GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void processNotification(String type, Bundle extras)
    {

	final String messageValue = extras.getString("message");

	notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	try
	{
	    JSONObject obj = new JSONObject(messageValue);
	    String pushtype = obj.getString("type");
	    if (pushtype.equalsIgnoreCase("new_message"))
	    {

		dialog_ID = obj.getString("dialog_id");
		mess = obj.getString("content_mess");
		user = obj.getString("user_id");
		mess_ID = obj.getString("mess_id");
		sender_ID = obj.getString("sender_id");
		recipient_ID = obj.getString("recipient_id");
		dialog_Type = obj.getString("dialog_type");
		attach_URL = obj.getString("attach_URL");
	    }
	    else if (pushtype.equalsIgnoreCase("blockuser"))
	    {
		blockUserID = obj.getString("block_user_id");

		// insert block user into db
		Sugar_User sUser = StaticFunction.getUserFromDBByID(SharePrefsHelper.getCurrentLoginUserID(GCMIntentService_MultiNotification.this));
		String cusData = sUser.userCustomData;
		cusData += blockUserID + ",";
		sUser.userCustomData = cusData;
		sUser.save();

		return;
	    }
	    else if (pushtype.equalsIgnoreCase("update_group_profile"))
	    {
		notify_group_profile_change_dialog_id = obj.getString("dialog_id");

		sendBroadCastToActivityNotifyGroupProfileChange(notify_group_profile_change_dialog_id);

		return;
	    }

	}
	catch (Exception e)
	{
	    Log.i(TAG, "Received: " + "Exception " + e.getMessage());
	    return;
	}

	//
	if (dialog_Type.equals("PRIVATE"))
	{
	    StaticFunction.saveMessageToDBFromGCM(mess_ID, Integer.parseInt(sender_ID), Integer.parseInt(recipient_ID), dialog_ID, mess, attach_URL);

	}
	else
	{
	    StaticFunction.saveMessageToDBFromGCM(mess_ID, Integer.parseInt(sender_ID), -1, dialog_ID, mess, attach_URL);

	}

	if (StaticFunction.isDialogExstingInDB(dialog_ID) == false)
	{
	    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, GCMIntentService_MultiNotification.this);
	}

	if (isActivityRunning())
	{

	    // if push from opponent, no notify
	    if (ChatActivity.DialogIdForNotification.equalsIgnoreCase(dialog_ID))
	    {

		sendBroadCastToActivity(user, dialog_ID, mess);

		boolean isChatRunning = SharePrefsHelper.getIsChatActivityRunningToSharePrefs(GCMIntentService_MultiNotification.this);

		if (isChatRunning == true)
		{

		}
		else
		{
		    // new
		    // notification
		    try
		    {
			// Intent intent = new
			// Intent(GCMIntentService_MultiNotification.this,
			// ChatActivity.class);
			// intent.putExtra("DIALOG_ID", dialog_ID);

			int hashcodeDialogID = 0;
			hashcodeDialogID = dialog_ID.hashCode();

			// store message
			SharedPreferences preferences = getSharedPreferences(hashcodeDialogID + "", MODE_PRIVATE);
			String message = preferences.getString("message", "");
			int count = preferences.getInt("count", 0);
			JSONArray arrayShow = new JSONArray();
			if (count > 0)
			{
			    JSONArray array = new JSONArray(message);
			    if (count > 3)
			    {
				for (int i = array.length() - 3; i < array.length(); i++)
				{
				    arrayShow.put(array.getJSONObject(i));
				}
			    }
			    else
			    {
				for (int i = 0; i < array.length(); i++)
				{
				    arrayShow.put(array.getJSONObject(i));
				}
			    }

			    Editor editor = preferences.edit();
			    JSONObject object = new JSONObject();
			    object.putOpt("msg", mess);
			    array.put(object);
			    editor.putString("message", array.toString());
			    editor.putInt("count", count + 1);
			    editor.commit();
			    arrayShow.put(object);
			}
			else
			{
			    Editor editor = preferences.edit();
			    JSONArray array = new JSONArray();
			    JSONObject object = new JSONObject();
			    object.putOpt("msg", mess);
			    array.put(object);
			    editor.putString("message", array.toString());
			    editor.putInt("count", 1);
			    editor.commit();
			    arrayShow.put(object);
			}

			NotificationUnder16(user, arrayShow, count, hashcodeDialogID);

		    }
		    catch (Exception ex)
		    {

		    }
		    // new

		}

	    }
	    else
	    // else notify
	    {

		sendBroadCastToActivity(user, dialog_ID, mess);
		// sendPushNotification(mess, user, dialog_ID); <--- this is old
		// push, now we use a new one

		// new
		// notification
		try
		{
		    // Intent intent = new
		    // Intent(GCMIntentService_MultiNotification.this,
		    // ChatActivity.class);
		    // intent.putExtra("DIALOG_ID", dialog_ID);

		    int hashcodeDialogID = 0;
		    hashcodeDialogID = dialog_ID.hashCode();

		    // store message
		    SharedPreferences preferences = getSharedPreferences(hashcodeDialogID + "", MODE_PRIVATE);
		    String message = preferences.getString("message", "");
		    int count = preferences.getInt("count", 0);
		    JSONArray arrayShow = new JSONArray();
		    if (count > 0)
		    {
			JSONArray array = new JSONArray(message);
			if (count > 3)
			{
			    for (int i = array.length() - 3; i < array.length(); i++)
			    {
				arrayShow.put(array.getJSONObject(i));
			    }
			}
			else
			{
			    for (int i = 0; i < array.length(); i++)
			    {
				arrayShow.put(array.getJSONObject(i));
			    }
			}

			Editor editor = preferences.edit();
			JSONObject object = new JSONObject();
			object.putOpt("msg", mess);
			array.put(object);
			editor.putString("message", array.toString());
			editor.putInt("count", count + 1);
			editor.commit();
			arrayShow.put(object);
		    }
		    else
		    {
			Editor editor = preferences.edit();
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			object.putOpt("msg", mess);
			array.put(object);
			editor.putString("message", array.toString());
			editor.putInt("count", 1);
			editor.commit();
			arrayShow.put(object);
		    }

		    NotificationUnder16(user, arrayShow, count, hashcodeDialogID);

		}
		catch (Exception ex)
		{

		}
		// new

	    }
	}
	else
	{
	    try
	    {

		int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(GCMIntentService_MultiNotification.this);

		String strCurrentLoginUserID = "" + currentLoginUserID;

		if (sender_ID.equalsIgnoreCase(strCurrentLoginUserID))
		{
		    sendBroadCastToActivity(user, dialog_ID, mess);
		    Log.i(TAG, "Received: " + "user.equalsIgnoreCase(qbUser.getLogin()");
		    return;
		}
		else
		{

		    sendBroadCastToActivity(user, dialog_ID, mess);

		    // notification
		    Intent intent = new Intent(GCMIntentService_MultiNotification.this, ChatActivity.class);
		    intent.putExtra("DIALOG_ID", dialog_ID);

		    int hashcodeDialogID = 0;
		    hashcodeDialogID = dialog_ID.hashCode();

		    // store message
		    SharedPreferences preferences = getSharedPreferences(hashcodeDialogID + "", MODE_PRIVATE);
		    String message = preferences.getString("message", "");
		    int count = preferences.getInt("count", 0);
		    JSONArray arrayShow = new JSONArray();
		    if (count > 0)
		    {
			JSONArray array = new JSONArray(message);
			if (count > 3)
			{
			    for (int i = array.length() - 3; i < array.length(); i++)
			    {
				arrayShow.put(array.getJSONObject(i));
			    }
			}
			else
			{
			    for (int i = 0; i < array.length(); i++)
			    {
				arrayShow.put(array.getJSONObject(i));
			    }
			}

			Editor editor = preferences.edit();
			JSONObject object = new JSONObject();
			object.putOpt("msg", mess);
			array.put(object);
			editor.putString("message", array.toString());
			editor.putInt("count", count + 1);
			editor.commit();
			arrayShow.put(object);
		    }
		    else
		    {
			Editor editor = preferences.edit();
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			object.putOpt("msg", mess);
			array.put(object);
			editor.putString("message", array.toString());
			editor.putInt("count", 1);
			editor.commit();
			arrayShow.put(object);
		    }

		    NotificationUnder16(user, arrayShow, count, hashcodeDialogID);

		}
	    }
	    catch (Exception e)
	    {
		Log.i(TAG, "Received: " + "Exception " + e.getMessage());
		return;
	    }
	}

    }

    private void sendBroadCastToActivity(String user, String dialogid, String message)
    {

	Intent i = new Intent();
	i.setAction(ACTION_BROADCAST_RECEIVER);
	i.putExtra("MESS_SENDERID", user);
	i.putExtra("MESS_BODY", message);
	i.putExtra("DIALOG_ID", dialogid);
	sendBroadcast(i);
    }

    private void sendBroadCastToActivityNotifyGroupProfileChange(String dialogid)
    {

	Intent i = new Intent();
	i.setAction(ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES);
	i.putExtra("DIALOG_ID", dialogid);
	sendBroadcast(i);
    }

    private void sendPushNotification(String mess, String user, String dialog_ID)
    {

	int hashcodeDialogID = 0;
	hashcodeDialogID = dialog_ID.hashCode();

	notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	// notification
	Intent intent = new Intent(GCMIntentService_MultiNotification.this, ChatActivity.class);
	intent.putExtra("DIALOG_ID", dialog_ID);

	// new
	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService_MultiNotification.this, hashcodeDialogID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService_MultiNotification.this).setSmallIcon(R.drawable.hellobutton).setContentTitle(user).setStyle(new NotificationCompat.BigTextStyle().bigText(mess)).setContentText(mess);

	mBuilder.setAutoCancel(true);

	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	mBuilder.setSound(alarmSound);

	mBuilder.setVibrate(new long[]
	{
	1000, 1000
	});

	mBuilder.setContentIntent(contentIntent);
	notificationManager.notify(hashcodeDialogID, mBuilder.build());

    }

    public boolean isActivityRunning()
    {

	ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	@SuppressWarnings("deprecation")
	List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
	boolean isServiceFound = false;
	for (int i = 0; i < services.size(); i++)
	{
	    if (services.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.lat.hello.chat/com.quickblox.sample.chat.ui.activities.ChatActivity}"))
	    {
		isServiceFound = true;
		break;
	    }
	}
	return isServiceFound;
    }

    // Loi
    private void NotificationAbove16(String user, JSONArray array, int count, int hashcodeDialogID)
    {

	Intent intent = new Intent(GCMIntentService_MultiNotification.this, ChatActivity.class);
	intent.putExtra("DIALOG_ID", dialog_ID);

	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService_MultiNotification.this, hashcodeDialogID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
	inboxStyle.setBigContentTitle(user);
	inboxStyle.setSummaryText((count + 1) + " message");

	for (int i = 0; i < array.length(); i++)
	{
	    try
	    {
		JSONObject object = array.getJSONObject(i);
		String msg = object.getString("msg");
		inboxStyle.addLine(msg);
	    }
	    catch (JSONException e)
	    {
		e.printStackTrace();
	    }
	}

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService_MultiNotification.this).setSmallIcon(R.drawable.hellobutton).setStyle(inboxStyle);
	mBuilder.setAutoCancel(true);

	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	mBuilder.setSound(alarmSound);
	mBuilder.setVibrate(new long[]
	{
	1000, 1000
	});

	mBuilder.setContentIntent(contentIntent);
	notificationManager.notify(hashcodeDialogID, mBuilder.build());
    }

    private void NotificationUnder16(String user, JSONArray array, int count, int hashcodeDialogID)
    {

	Intent intent = new Intent(GCMIntentService_MultiNotification.this, ChatActivity.class);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(hashcodeDialogID + "");
	intent.putExtra("DIALOG_ID", dialog_ID);

	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService_MultiNotification.this, hashcodeDialogID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

	Log.e("PendingIntent", dialog_ID + "");
	Log.e("PendingIntent", hashcodeDialogID + "");
	Log.e("PendingIntent", contentIntent.toString() + "");

	RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remote_notification);
	remoteViews.setTextViewText(R.id.txtUser, user);
	remoteViews.setTextViewText(R.id.txtCount, (count + 1) + " message");

	String msg[] = new String[8];
	switch (count)
	{
	    case 0:
		for (int i = 0; i < array.length(); i++)
		{
		    try
		    {
			JSONObject object = array.getJSONObject(i);
			msg[i] = object.getString("msg");
		    }
		    catch (JSONException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		remoteViews.setTextViewText(R.id.textView1, msg[0]);
		break;
	    case 1:
		for (int i = 0; i < array.length(); i++)
		{
		    try
		    {
			JSONObject object = array.getJSONObject(i);
			msg[i] = object.getString("msg");
		    }
		    catch (JSONException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		remoteViews.setTextViewText(R.id.textView1, msg[0]);
		remoteViews.setTextViewText(R.id.textView2, msg[1]);
		break;
	    case 2:
		for (int i = 0; i < array.length(); i++)
		{
		    try
		    {
			JSONObject object = array.getJSONObject(i);
			msg[i] = object.getString("msg");
		    }
		    catch (JSONException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		remoteViews.setTextViewText(R.id.textView1, msg[0]);
		remoteViews.setTextViewText(R.id.textView2, msg[1]);
		remoteViews.setTextViewText(R.id.textView3, msg[2]);
		break;
	    default:
		for (int i = 0; i < array.length(); i++)
		{
		    try
		    {
			JSONObject object = array.getJSONObject(i);
			msg[i] = object.getString("msg");
		    }
		    catch (JSONException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		remoteViews.setTextViewText(R.id.textView1, msg[0]);
		remoteViews.setTextViewText(R.id.textView2, msg[1]);
		remoteViews.setTextViewText(R.id.textView3, msg[2]);
		remoteViews.setTextViewText(R.id.textView4, msg[3]);
		break;
	}

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService_MultiNotification.this).setContent(remoteViews).setSmallIcon(R.drawable.hellobutton).setContentTitle(user).setContentText(mess).setNumber(count + 1);
	mBuilder.setAutoCancel(true);

	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	mBuilder.setSound(alarmSound);

	mBuilder.setVibrate(new long[]
	{
	1000, 1000
	});

	mBuilder.setContentIntent(contentIntent);
	notificationManager.notify(hashcodeDialogID, mBuilder.build());
    }
}
