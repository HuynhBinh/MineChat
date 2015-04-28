package com.es.hello.chat;

import java.util.ArrayList;
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
import android.widget.Toast;

import com.es.hello.chat.consts.Consts;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.core.PrivateChatManagerImpl;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_Noti;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.activities.ChatActivity;
import com.es.hello.chat.ui.activities.DialogsActivity;
import com.es.hello.chat.ui.fragments.UsersFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.internal.is;
import com.lat.hello.chat.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

public class GCMIntentService extends IntentService
{

    public static final int NOTIFICATION_ID = 1;

    private static final String TAG = GCMIntentService.class.getSimpleName();

    private NotificationManager notificationManager;

    public static final String ACTION_BROADCAST_RECEIVER = "com.hellochat.service.broad.receive";

    public static final String ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES = "com.hellochat.service.broad.receive.groupprofilechanges";

    public static final String ACTION_BROADCAST_RECEIVER_GROUP_New_User_Join = "com.hellochat.service.broad.receive.group_new_user_join";

    public static final String ACTION_BROADCAST_RECEIVER_NEW_GROUP_CREATED = "com.hellochat.service.broad.receive.newgroupcreated";

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

    public GCMIntentService()
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

		// check if dialog status = 3, no notify
		Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog_ID);
		if (sDialog != null)
		{
		    if (sDialog.dialogStatus == 3)
		    {
			return;
		    }
		}

	    }
	    else if (pushtype.equalsIgnoreCase("blockuser"))
	    {
		blockUserID = obj.getString("block_user_id");

		// insert block user into db
		Sugar_User sUser = StaticFunction.getUserFromDBByID(SharePrefsHelper.getCurrentLoginUserID(GCMIntentService.this));
		String cusData = sUser.userCustomData;
		cusData += blockUserID + ",";
		sUser.userCustomData = cusData;
		sUser.save();

		return;
	    }
	    else if (pushtype.equalsIgnoreCase("update_group_profile"))
	    {
		notify_group_profile_change_dialog_id = obj.getString("dialog_id");

		loadDialogProfileInfoForGroupChat(notify_group_profile_change_dialog_id);

		return;
	    }
	    else if (pushtype.equalsIgnoreCase("leavegroup"))
	    {
		// notify user left room

		// update database remove occupants id from group dialog id
		dialog_ID = obj.getString("dialog_id");
		sender_ID = obj.getString("sender_id");
		String senderName = obj.getString("sender_name");
		Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog_ID);

		if (sDialog != null)
		{
		    ArrayList<Integer> listIDs = new ArrayList<Integer>();
		    listIDs = StaticFunction.splitStringToArrayInteger(sDialog.dialogOccupantsIds);

		    Integer intSenderID = Integer.parseInt(sender_ID);

		    listIDs.remove(intSenderID);

		    String listStringIDs = "";

		    for (Integer intID : listIDs)
		    {
			listStringIDs += intID + ",";
		    }

		    sDialog.dialogOccupantsIds = listStringIDs;

		    sDialog.save();

		    sendBroadCastToActivityNotifyNewUserJoin(dialog_ID);

		    notificationLeaveJoinGroup(1234, senderName + " has left the group " + sDialog.dialogName);
		}

		return;

	    }
	    else if (pushtype.equalsIgnoreCase("joingroup"))
	    {

		// Toast.makeText(getApplicationContext(), "aaaaaaaaaaaaa",
		// 1).show();
		dialog_ID = obj.getString("dialog_id");
		String joinUsers = obj.getString("join_users");
		String joinUserIDs = obj.getString("join_user_ids");

		// Sugar_Dialog sDialog =
		// StaticFunction.findDialogInDBByID(dialog_ID);

		List<Sugar_Dialog> list = Sugar_Dialog.find(Sugar_Dialog.class, "DIALOG_ID = ?", dialog_ID);
		Sugar_Dialog sDialog = list.get(0);

		if (sDialog != null)
		{

		    String newOccupantsIDs = sDialog.dialogOccupantsIds += joinUserIDs;

		    // sDialog.dialogName = "NEW NAME";
		    sDialog.dialogOccupantsIds = newOccupantsIDs;

		    sDialog.save();

		    sendBroadCastToActivityNotifyNewUserJoin(dialog_ID);

		    notificationLeaveJoinGroup(1234, joinUsers + "has joined the group " + sDialog.dialogName);
		}

		return;
	    }
	    else if (pushtype.equalsIgnoreCase("youareinvited"))
	    {
		dialog_ID = obj.getString("dialog_id");
		Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog_ID);
		if (sDialog != null)
		{
		    sDialog.dialogStatus = 0;
		    sDialog.save();
		}
		else
		{
		    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, GCMIntentService.this);
		}

		sendBroadCastToActivity("", dialog_ID, "");

		return;

	    }
	    else if (pushtype.equalsIgnoreCase("createnewgroup"))
	    {
		dialog_ID = obj.getString("dialog_id");
		Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog_ID);
		if (sDialog != null)
		{

		}
		else
		{		 
		    loadDialogNewGroupChat(dialog_ID);		  
		}

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
	    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, GCMIntentService.this);
	}

	if (isActivityRunning())
	{

	    // if push from opponent, no notify
	    if (ChatActivity.DialogIdForNotification.equalsIgnoreCase(dialog_ID))
	    {

		sendBroadCastToActivity(user, dialog_ID, mess);

		boolean isChatRunning = SharePrefsHelper.getIsChatActivityRunningToSharePrefs(GCMIntentService.this);

		if (isChatRunning == true)
		{

		}
		else
		{
		    // new
		    // notification
		    try
		    {
			Intent intentChat = new Intent(GCMIntentService.this, ChatActivity.class);
			Intent intentDialog = new Intent(GCMIntentService.this, DialogsActivity.class);

			int hashcodeDialogID = 0;
			hashcodeDialogID = dialog_ID.hashCode();

			List<Sugar_Noti> listAll = Sugar_Noti.listAll(Sugar_Noti.class);
			if (listAll.size() == 0)
			{
			    Sugar_Noti noti = new Sugar_Noti();
			    noti.hashcodeId = hashcodeDialogID;
			    noti.message = 1;
			    noti.save();
			    String message = "1 message from 1 conversation";
			    Notification(intentChat, hashcodeDialogID, message);
			}
			else if (listAll.size() == 1)
			{
			    List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			    if (listNoti.size() == 0)
			    {
				Sugar_Noti noti = new Sugar_Noti();
				noti.hashcodeId = hashcodeDialogID;
				noti.message = 1;
				noti.save();

				String message = (listAll.get(0).message + 1) + " messages from 2 conversations";
				Notification(intentDialog, 0, message);
				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(listAll.get(0).hashcodeId);
			    }
			    else
			    {
				Sugar_Noti noti = listNoti.get(0);
				noti.message = noti.message + 1;
				noti.save();

				String message = noti.message + " messages from 1 conversation";
				Notification(intentChat, hashcodeDialogID, message);
			    }
			}
			else
			{
			    List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			    if (listNoti.size() == 0)
			    {
				Sugar_Noti noti = new Sugar_Noti();
				noti.hashcodeId = hashcodeDialogID;
				noti.message = 1;
				noti.save();
			    }
			    else
			    {
				Sugar_Noti noti = listNoti.get(0);
				noti.message = noti.message + 1;
				noti.save();
			    }
			    listAll = Sugar_Noti.listAll(Sugar_Noti.class);
			    int msg = 0;
			    for (Sugar_Noti sugar_Noti : listAll)
			    {
				msg += sugar_Noti.message;
			    }
			    String message = msg + " messages from " + listAll.size() + " conversations";
			    Notification(intentDialog, 0, message);
			}

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
		    Intent intentChat = new Intent(GCMIntentService.this, ChatActivity.class);
		    Intent intentDialog = new Intent(GCMIntentService.this, DialogsActivity.class);

		    int hashcodeDialogID = 0;
		    hashcodeDialogID = dialog_ID.hashCode();

		    List<Sugar_Noti> listAll = Sugar_Noti.listAll(Sugar_Noti.class);
		    if (listAll.size() == 0)
		    {
			Sugar_Noti noti = new Sugar_Noti();
			noti.hashcodeId = hashcodeDialogID;
			noti.message = 1;
			noti.save();
			String message = "1 message from 1 conversation";
			Notification(intentChat, hashcodeDialogID, message);
		    }
		    else if (listAll.size() == 1)
		    {
			List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			if (listNoti.size() == 0)
			{
			    Sugar_Noti noti = new Sugar_Noti();
			    noti.hashcodeId = hashcodeDialogID;
			    noti.message = 1;
			    noti.save();

			    String message = (listAll.get(0).message + 1) + " messages from 2 conversations";
			    Notification(intentDialog, 0, message);
			    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			    notificationManager.cancel(listAll.get(0).hashcodeId);
			}
			else
			{
			    Sugar_Noti noti = listNoti.get(0);
			    noti.message = noti.message + 1;
			    noti.save();

			    String message = noti.message + " messages from 1 conversation";
			    Notification(intentChat, hashcodeDialogID, message);
			}
		    }
		    else
		    {
			List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			if (listNoti.size() == 0)
			{
			    Sugar_Noti noti = new Sugar_Noti();
			    noti.hashcodeId = hashcodeDialogID;
			    noti.message = 1;
			    noti.save();
			}
			else
			{
			    Sugar_Noti noti = listNoti.get(0);
			    noti.message = noti.message + 1;
			    noti.save();
			}
			listAll = Sugar_Noti.listAll(Sugar_Noti.class);
			int msg = 0;
			for (Sugar_Noti sugar_Noti : listAll)
			{
			    msg += sugar_Noti.message;
			}
			String message = msg + " messages from " + listAll.size() + " conversations";
			Notification(intentDialog, 0, message);
		    }

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

		int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(GCMIntentService.this);

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

		    Intent intentChat = new Intent(GCMIntentService.this, ChatActivity.class);
		    Intent intentDialog = new Intent(GCMIntentService.this, DialogsActivity.class);

		    int hashcodeDialogID = 0;
		    hashcodeDialogID = dialog_ID.hashCode();

		    List<Sugar_Noti> listAll = Sugar_Noti.listAll(Sugar_Noti.class);
		    if (listAll.size() == 0)
		    {
			Sugar_Noti noti = new Sugar_Noti();
			noti.hashcodeId = hashcodeDialogID;
			noti.message = 1;
			noti.save();
			String message = "1 message from 1 conversation";
			Notification(intentChat, hashcodeDialogID, message);
		    }
		    else if (listAll.size() == 1)
		    {
			List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			if (listNoti.size() == 0)
			{
			    Sugar_Noti noti = new Sugar_Noti();
			    noti.hashcodeId = hashcodeDialogID;
			    noti.message = 1;
			    noti.save();

			    String message = (listAll.get(0).message + 1) + " messages from 2 conversations";
			    Notification(intentDialog, 0, message);
			    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			    notificationManager.cancel(listAll.get(0).hashcodeId);
			}
			else
			{
			    Sugar_Noti noti = listNoti.get(0);
			    noti.message = noti.message + 1;
			    noti.save();

			    String message = noti.message + " messages from 1 conversation";
			    Notification(intentChat, hashcodeDialogID, message);
			    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			    notificationManager.cancel(0);
			}
		    }
		    else
		    {
			List<Sugar_Noti> listNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeDialogID + "");
			if (listNoti.size() == 0)
			{
			    Sugar_Noti noti = new Sugar_Noti();
			    noti.hashcodeId = hashcodeDialogID;
			    noti.message = 1;
			    noti.save();
			}
			else
			{
			    Sugar_Noti noti = listNoti.get(0);
			    noti.message = noti.message + 1;
			    noti.save();
			}
			listAll = Sugar_Noti.listAll(Sugar_Noti.class);
			int msg = 0;
			for (Sugar_Noti sugar_Noti : listAll)
			{
			    msg += sugar_Noti.message;
			}
			String message = msg + " messages from " + listAll.size() + " conversations";
			Notification(intentDialog, 0, message);
		    }

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

    private void sendBroadCastToActivityNotifyNewGroupCreated(String dialogid)
    {

	Intent i = new Intent();
	i.setAction(ACTION_BROADCAST_RECEIVER_NEW_GROUP_CREATED);
	i.putExtra("DIALOG_ID", dialogid);
	sendBroadcast(i);
    }

    private void sendBroadCastToActivityNotifyNewUserJoin(String dialogid)
    {

	Intent i = new Intent();
	i.setAction(ACTION_BROADCAST_RECEIVER_GROUP_New_User_Join);
	i.putExtra("DIALOG_ID", dialogid);
	sendBroadcast(i);
    }

    private void sendPushNotification(String mess, String user, String dialog_ID)
    {

	int hashcodeDialogID = 0;
	hashcodeDialogID = dialog_ID.hashCode();

	notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	// notification
	Intent intent = new Intent(GCMIntentService.this, ChatActivity.class);
	intent.putExtra("DIALOG_ID", dialog_ID);

	// new
	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService.this, hashcodeDialogID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService.this).setSmallIcon(R.drawable.hellobutton).setContentTitle(user).setStyle(new NotificationCompat.BigTextStyle().bigText(mess)).setContentText(mess);

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
	    if (services.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.lat.hello.chat/com.es.hello.chat.ui.activities.ChatActivity}"))
	    {
		isServiceFound = true;
		break;
	    }
	}
	return isServiceFound;
    }

    // Loi
    private void Notification(Intent intent, int hashcodeDialogID, String message)
    {

	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(hashcodeDialogID + "");
	intent.putExtra("DIALOG_ID", dialog_ID);

	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService.this, hashcodeDialogID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService.this).setSmallIcon(R.drawable.hellobutton).setContentTitle("#ello Chat").setContentText(message);
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

    private void notificationLeaveJoinGroup(int hashcodeDialogID, String message)
    {

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService.this).setSmallIcon(R.drawable.hellobutton).setContentTitle("#ello Chat").setContentText(message);
	mBuilder.setAutoCancel(true);

	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	mBuilder.setSound(alarmSound);
	mBuilder.setVibrate(new long[]
	{
	1000, 1000
	});

	notificationManager.notify(hashcodeDialogID, mBuilder.build());
    }

    private void NotificationUnder16(String user, JSONArray array, int count, int hashcodeDialogID)
    {

	Intent intent = new Intent(GCMIntentService.this, ChatActivity.class);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(hashcodeDialogID + "");
	intent.putExtra("DIALOG_ID", dialog_ID);

	PendingIntent contentIntent = PendingIntent.getActivity(GCMIntentService.this, hashcodeDialogID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

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

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GCMIntentService.this).setContent(remoteViews).setSmallIcon(R.drawable.hellobutton).setContentTitle(user).setContentText(mess).setNumber(count + 1);
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
    
    private boolean loadDialog(final String dialogID)
    {
	boolean isSuccess = false;
	final QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
	customObjectRequestBuilder.eq("_id", dialogID);

	try
	{
	    ArrayList<QBDialog> dialogs = QBChatService.getChatDialogs(null, customObjectRequestBuilder, new Bundle());
	    StaticFunction.saveListDialogToDB(dialogs);
	    isSuccess = true;	  
	}
	catch (Exception ex)
	{
	    isSuccess = false;
	}
	return isSuccess;
    }

    private void loadDialogProfileInfoForGroupChat(final String dialogID)
    {
	
	boolean isSuccess = loadDialog(dialogID);
	if(isSuccess)
	{
	    sendBroadCastToActivityNotifyGroupProfileChange(dialogID);
	}
    }
    
    
    private void loadDialogNewGroupChat(final String dialogID)
    {
	
	boolean isSuccess = loadDialog(dialogID);
	if(isSuccess)
	{
	    sendBroadCastToActivityNotifyNewGroupCreated(dialogID);
	}
    }

}
