package com.es.hello.chat.consts;

import android.content.Context;
import android.content.SharedPreferences;

import com.quickblox.users.model.QBUser;

public class SharePrefsHelper
{

    public static String Share_Prefs_Name = "HelloMainServiceSharePrefs";

    public static String LOGIN_USER_NAME = "Login_user_name";

    public static String LOGIN_USER_PASS = "Login_user_password";

    public static String LOGIN_USER_TOKEN_ID = "Login_user_token_id";

    public static String SUBSCRIPT_ID = "subscription_id";

    public static String IS_OPEN_INSTRUCTION = "is_open_instruction";

    public static String IS_CHAT_ACTIVITY_RUNNING = "is_chat_activity_running";

    public static String IS_DOWNLOAD_DIALOG_LIST = "is_download_dialog_list";

    public static String CREATE_GROUP_NAME = "CREATE_GROUP_NAME";

    public static String CREATE_GROUP_PHOTO = "CREATE_GROUP_PHOTO";

    public static SharedPreferences getSharePreferences(Context ctx)
    {

	return ctx.getSharedPreferences(Share_Prefs_Name, Context.MODE_PRIVATE);
    }

    // this user just only contain username and pass -> not engough info to
    // login
    public static QBUser getCurrentLoginUser(Context ctx)
    {

	QBUser user = new QBUser();

	final SharedPreferences prefs = SharePrefsHelper.getSharePreferences(ctx);
	String username = prefs.getString(LOGIN_USER_NAME, "");
	String password = prefs.getString(LOGIN_USER_PASS, "");
	int tokenId = prefs.getInt(LOGIN_USER_TOKEN_ID, -1);
	// String tokenid = prefs.getString(LOGIN_USER_TOKEN_ID, "");

	if (username.isEmpty() || password.isEmpty())
	{
	    // Log.i("HELLOCHAT", "Registration not found.");
	    return null;
	}
	else
	{
	    user.setLogin(username);
	    user.setPassword(password);
	    user.setId(tokenId);
	}

	return user;
    }

    public static void clearUserInfoInSharePrefs(Context ctx)
    {

	QBUser user = new QBUser();
	user.setLogin("");
	user.setPassword("");
	SharePrefsHelper.saveUserToSharePrefs(user, ctx);
	SharePrefsHelper.saveUserIDToSharePrefs(-1, ctx);
    }

    public static void saveUserToSharePrefs(QBUser user, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(LOGIN_USER_NAME, user.getLogin());
	editor.putString(LOGIN_USER_PASS, user.getPassword());

	editor.commit();

    }

    public static void saveUserIDToSharePrefs(int userID, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putInt(LOGIN_USER_TOKEN_ID, userID);
	editor.commit();

    }

    public static int getCurrentLoginUserID(Context ctx)
    {

	final SharedPreferences prefs = SharePrefsHelper.getSharePreferences(ctx);

	int tokenId = prefs.getInt(LOGIN_USER_TOKEN_ID, -1);

	return tokenId;
    }

    public static void savePushSubscriptionToSharePrefs(String subscriptID, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(SUBSCRIPT_ID, subscriptID);

	editor.commit();

    }

    public static void saveGroupNameToSharePrefs(String groupName, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(CREATE_GROUP_NAME, groupName);

	editor.commit();

    }

    public static String getGroupName(Context ctx)
    {

	final SharedPreferences prefs = SharePrefsHelper.getSharePreferences(ctx);

	String gn = prefs.getString(CREATE_GROUP_NAME, "");

	return gn;
    }

    public static void saveGroupPhotoToSharePrefs(String groupPhoto, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(CREATE_GROUP_PHOTO, groupPhoto);

	editor.commit();

    }

    public static String getGroupPhoto(Context ctx)
    {

	final SharedPreferences prefs = SharePrefsHelper.getSharePreferences(ctx);

	String gp = prefs.getString(CREATE_GROUP_PHOTO, "");

	return gp;
    }

    public static void saveNumOfAppUsedForInstructionToSharePrefs(int numOfApp, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putInt(IS_OPEN_INSTRUCTION, numOfApp);

	editor.commit();

    }

    public static void saveIsChatActivityRunningToSharePrefs(boolean isRunning, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putBoolean(IS_CHAT_ACTIVITY_RUNNING, isRunning);

	editor.commit();

    }

    public static boolean getIsChatActivityRunningToSharePrefs(Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	boolean isRunning = prefs.getBoolean(IS_CHAT_ACTIVITY_RUNNING, false);

	return isRunning;
    }

    public static void saveIsDownloadedDialogListToSharePrefs(boolean isDownload, Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	SharedPreferences.Editor editor = prefs.edit();
	editor.putBoolean(IS_DOWNLOAD_DIALOG_LIST, isDownload);

	editor.commit();

    }

    public static boolean getIsDownloadedDialogListToSharePrefs(Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	boolean isRunning = prefs.getBoolean(IS_DOWNLOAD_DIALOG_LIST, false);

	return isRunning;
    }

    public static int getNumOfAppUsedForInstructionToSharePrefs(Context ctx)
    {

	final SharedPreferences prefs = getSharePreferences(ctx);

	int numOfApp = prefs.getInt(IS_OPEN_INSTRUCTION, 0);

	return numOfApp;
    }

    public static int getPushSubscriptionToSharePrefs(Context ctx)
    {

	final SharedPreferences prefs = SharePrefsHelper.getSharePreferences(ctx);
	String subid = prefs.getString(SUBSCRIPT_ID, "");

	int iSubId = -1;

	if (!subid.equals(""))
	{
	    iSubId = Integer.parseInt(subid);
	}

	return iSubId;

    }

}
