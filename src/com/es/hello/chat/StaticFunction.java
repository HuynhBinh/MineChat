package com.es.hello.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.services.HelloMainService;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_Message;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.activities.Activity_Login_Hello;
import com.es.hello.chat.ui.activities.Activity_Setting;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBMessage;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.server.BaseService;
import com.quickblox.messages.QBMessages;
import com.quickblox.users.model.QBUser;

public class StaticFunction
{

    public static void initImageLoader(Context ctx)
    {

	// Catche
	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).memoryCache(new WeakMemoryCache()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
	// Initialize ImageLoader with configuration.
	if (ImageLoader.getInstance().isInited() == false)
	{
	    ImageLoader.getInstance().init(config);
	}
    }

    public static TextView getActionBarTextView(Toolbar mToolBar)
    {

	TextView titleTextView = null;

	try
	{
	    Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
	    f.setAccessible(true);
	    titleTextView = (TextView) f.get(mToolBar);
	}
	catch (NoSuchFieldException e)
	{
	}
	catch (IllegalAccessException e)
	{
	}
	return titleTextView;
    }

    public static TextView getActionBarSubTextView(Toolbar mToolBar)
    {

	TextView titleTextView = null;

	try
	{
	    Field f = mToolBar.getClass().getDeclaredField("mSubTitleTextView");
	    f.setAccessible(true);
	    titleTextView = (TextView) f.get(mToolBar);
	}
	catch (NoSuchFieldException e)
	{

	}
	catch (IllegalAccessException e)
	{
	}
	return titleTextView;
    }

    public static void initActionToolBar(final ActionBarActivity activity, Toolbar toolbar, String Tittle, boolean isBackButtonVisible)
    {

	LinearLayout btnBack = (LinearLayout) toolbar.findViewById(R.id.btnBack);
	ImageView imgBack = (ImageView) toolbar.findViewById(R.id.imgBack);

	if (isBackButtonVisible == true)
	{
	    imgBack.setVisibility(View.VISIBLE);

	    btnBack.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    activity.finish();

		}
	    });

	}
	else
	{
	    imgBack.setVisibility(View.GONE);
	    btnBack.setClickable(false);
	    btnBack.setBackgroundColor(Color.TRANSPARENT);

	}

	activity.getSupportActionBar().setTitle(Tittle);

	// TextView actionBarTittle = getActionBarTextView(toolbar);

	// actionBarTittle.setVisibility(View.GONE);

	TextView txtTittle = (TextView) toolbar.findViewById(R.id.txttTittle);
	txtTittle.setVisibility(View.GONE);
	// txtTittle.setText(Tittle);
	// activity.getSupportActionBar().setTitle(Tittle);

	activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	activity.getSupportActionBar().setIcon(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));

	/*{
	    int padding_in_dp = 3;
	    final float scale = activity.getResources().getDisplayMetrics().density;
	    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

	    txtTittle.setPadding(0, padding_in_px, 0, 0);
	    txtTittle.setGravity(Gravity.CENTER_VERTICAL);
	    txtTittle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29);
	    txtTittle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(activity), Typeface.BOLD);
	    txtTittle.setTextColor(Color.parseColor("#474749"));
	}*/
    }

    public static void initActionToolBarForChatOnly(final ActionBarActivity activity, Toolbar toolbar, String Tittle)
    {

	LinearLayout btnBack = (LinearLayout) toolbar.findViewById(R.id.btnBack);
	ImageView imgBack = (ImageView) toolbar.findViewById(R.id.imgBack);

	imgBack.setVisibility(View.VISIBLE);

	btnBack.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		activity.finish();

	    }
	});

	activity.getSupportActionBar().setTitle(Tittle);
	activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	activity.getSupportActionBar().setIcon(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));

	/*{
	    int padding_in_dp = 3;
	    final float scale = activity.getResources().getDisplayMetrics().density;
	    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

	    TextView actionBarTittle = getActionBarTextView(toolbar);
	    actionBarTittle.setPadding(0, padding_in_px, 0, 0);
	    actionBarTittle.setGravity(Gravity.CENTER_VERTICAL);
	    actionBarTittle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29);
	    actionBarTittle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(activity), Typeface.BOLD);
	    actionBarTittle.setTextColor(Color.parseColor("#474749"));
	}*/
    }

    public static void showPopupWrongUser(final Activity acti, Context context, String dialogMessage)
    {

	if (!acti.isFinishing())
	{
	    // custom dialog
	    final Dialog dialog = new Dialog(context);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	    dialog.setCanceledOnTouchOutside(false);
	    dialog.setCancelable(false);

	    dialog.setContentView(R.layout.dialog_no_internet);

	    TextView txtDialogMessage = (TextView) dialog.findViewById(R.id.txtDialogMessage);
	    txtDialogMessage.setText(dialogMessage);

	    Button btnRetry = (Button) dialog.findViewById(R.id.btnRetry);
	    Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
	    btnCancel.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    // Activity_Setting.logoutHelloChat(acti, null);
		    dialog.dismiss();
		    acti.finish();

		}
	    });

	    btnRetry.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    Intent intent = new Intent(acti, Activity_Login_Hello.class);
		    acti.startActivity(intent);

		    dialog.dismiss();
		    acti.finish();

		}
	    });

	    dialog.show();
	}
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Integer> getUserIds(List<QBDialog> dialogs)
    {

	ArrayList<Integer> ids = new ArrayList<Integer>();
	for (QBDialog dialog : dialogs)
	{
	    ArrayList<Integer> list = dialog.getOccupants();
	    ids.addAll(list);
	}

	@SuppressWarnings("rawtypes")
	HashSet hs = new HashSet();
	hs.addAll(ids);
	ids.clear();
	ids.addAll(hs);

	return ids;
    }

    public static ArrayList<Integer> getUserIds1(List<QBUser> users)
    {

	ArrayList<Integer> ids = new ArrayList<Integer>();
	for (QBUser user : users)
	{

	    ids.add(user.getId());
	}

	return ids;
    }

    public static Sugar_User findUserInDBByID(String userID)
    {

	List<Sugar_User> list = Sugar_User.find(Sugar_User.class, "USER_ID = ?", userID);
	if (list != null)
	{
	    if (list.isEmpty())
	    {
		return null;
	    }
	    else
	    {
		return list.get(0);
	    }
	}
	else
	{
	    return null;
	}
    }

    public static int getBitmapInSampleSize(Activity acti, String path)
    {

	int inSampleSize = 1;

	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	bitmapOptions.inJustDecodeBounds = true;
	BitmapFactory.decodeFile(path, bitmapOptions);
	int imageWidth = bitmapOptions.outWidth;
	int imageHeight = bitmapOptions.outHeight;

	int Measuredwidth = 0;
	int Measuredheight = 0;
	Point size = new Point();
	WindowManager w = acti.getWindowManager();

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	{
	    w.getDefaultDisplay().getSize(size);
	    Measuredwidth = size.x;
	    Measuredheight = size.y;
	}
	else
	{
	    Display d = w.getDefaultDisplay();
	    Measuredwidth = d.getWidth();
	    Measuredheight = d.getHeight();
	}

	if (imageWidth <= Measuredwidth || imageHeight <= Measuredheight)
	{
	    inSampleSize = 1;
	}
	else
	{
	    int max = Math.max(imageWidth / Measuredwidth, imageHeight / Measuredheight);
	    inSampleSize = max;

	}

	return inSampleSize;
    }

    public static List<QBUser> findListUserByUserId(List<Integer> usersIDs)
    {

	List<Integer> listExceptionUsersIDs = new ArrayList<Integer>();

	final List<QBUser> listQBUsers = new ArrayList<QBUser>();
	for (Integer usersID : usersIDs)
	{
	    List<Sugar_User> listSUsers = new ArrayList<Sugar_User>();

	    listSUsers = Sugar_User.find(Sugar_User.class, "USER_ID = ?", usersID + "");

	    if (listSUsers != null)
	    {
		if (listSUsers.size() > 0)
		{
		    Sugar_User sUser = new Sugar_User();
		    sUser = listSUsers.get(0);

		    QBUser qbUser = new QBUser();
		    qbUser.setId(sUser.userId);
		    qbUser.setLogin(sUser.userLogin);
		    qbUser.setWebsite(sUser.userWebsite);
		    qbUser.setFullName(sUser.userFullName);
		    qbUser.setCustomData(sUser.userCustomData);

		    listQBUsers.add(qbUser);

		}
		else
		{
		    listExceptionUsersIDs.add(usersID);
		}

	    }
	    else
	    {
		listExceptionUsersIDs.add(usersID);
	    }

	}

	return listQBUsers;

    }

    public static void hideSoftKeyboard(Activity activity)
    {

	InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void logoutHelloChat(final Activity activity, final View progressBar)
    {

	Sugar_Dialog.deleteAll(Sugar_Dialog.class);
	Sugar_Message.deleteAll(Sugar_Message.class);
	Sugar_User.deleteAll(Sugar_User.class);

	SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, activity);

	if (HelloMainService.chatService != null)
	{

	    final int iSubId1 = SharePrefsHelper.getPushSubscriptionToSharePrefs(activity);
	    if (iSubId1 != -1)
	    {
		QBMessages.deleteSubscription(iSubId1, new QBEntityCallbackImpl<Void>()
		{

		    @Override
		    public void onSuccess()
		    {

			SharePrefsHelper.savePushSubscriptionToSharePrefs("", activity);
			if (progressBar != null)
			{
			    progressBar.setVisibility(View.GONE);
			}

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			if (progressBar != null)
			{
			    progressBar.setVisibility(View.GONE);
			}
		    }
		});

	    }

	    boolean isLoggedIn = HelloMainService.chatService.isLoggedIn();
	    if (isLoggedIn == true)
	    {
		if (progressBar != null)
		{
		    progressBar.setVisibility(View.VISIBLE);
		}

		HelloMainService.chatService.logout(new QBEntityCallbackImpl()
		{

		    @Override
		    public void onSuccess()
		    {

			// success
			try
			{
			    BaseService.getBaseService().setToken(null);
			}
			catch (BaseServiceException e)
			{
			    e.printStackTrace();
			}

			SharePrefsHelper.clearUserInfoInSharePrefs(activity);

			if (StaticFunction.isMyServiceRunning(HelloMainService.class, activity))
			{
			    Intent intent = new Intent(activity, HelloMainService.class);
			    intent.addCategory(HelloMainService.TAG);
			    activity.stopService(intent);
			}

			HelloMainService.chatService.stopAutoSendPresence();
			HelloMainService.chatService.destroy();
			HelloMainService.chatService = null;

			Intent intent = new Intent(activity, Activity_Login_Hello.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
			activity.finish();

		    }

		    @Override
		    public void onError(final List list)
		    {

			try
			{
			    BaseService.getBaseService().setToken(null);
			}
			catch (BaseServiceException e)
			{
			    e.printStackTrace();
			}

			SharePrefsHelper.clearUserInfoInSharePrefs(activity);

			if (StaticFunction.isMyServiceRunning(HelloMainService.class, activity))
			{
			    Intent intent = new Intent(activity, HelloMainService.class);
			    intent.addCategory(HelloMainService.TAG);
			    activity.stopService(intent);
			}

			HelloMainService.chatService.stopAutoSendPresence();
			HelloMainService.chatService.destroy();
			HelloMainService.chatService = null;

			Intent intent = new Intent(activity, Activity_Login_Hello.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
			activity.finish();

		    }
		});
	    }
	    else
	    {

		try
		{
		    BaseService.getBaseService().setToken(null);
		}
		catch (BaseServiceException e)
		{
		    e.printStackTrace();
		}

		SharePrefsHelper.clearUserInfoInSharePrefs(activity);

		if (StaticFunction.isMyServiceRunning(HelloMainService.class, activity))
		{
		    Intent intent = new Intent(activity, HelloMainService.class);
		    intent.addCategory(HelloMainService.TAG);
		    activity.stopService(intent);
		}

		HelloMainService.chatService.stopAutoSendPresence();
		HelloMainService.chatService.destroy();
		HelloMainService.chatService = null;

		Intent intent = new Intent(activity, Activity_Login_Hello.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
		activity.finish();

	    }

	}
	else
	{

	    final int iSubId = SharePrefsHelper.getPushSubscriptionToSharePrefs(activity);
	    if (iSubId != -1)
	    {
		QBMessages.deleteSubscription(iSubId, new QBEntityCallbackImpl<Void>()
		{

		    @Override
		    public void onSuccess()
		    {

			SharePrefsHelper.savePushSubscriptionToSharePrefs("", activity);
			if (progressBar != null)
			{
			    progressBar.setVisibility(View.GONE);
			}

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			if (progressBar != null)
			{
			    progressBar.setVisibility(View.GONE);
			}

		    }
		});

	    }

	    try
	    {
		BaseService.getBaseService().setToken(null);
	    }
	    catch (BaseServiceException e)
	    {
		e.printStackTrace();
	    }

	    SharePrefsHelper.clearUserInfoInSharePrefs(activity);

	    if (StaticFunction.isMyServiceRunning(HelloMainService.class, activity))
	    {
		Intent intent = new Intent(activity, HelloMainService.class);
		intent.addCategory(HelloMainService.TAG);
		activity.stopService(intent);
	    }

	    HelloMainService.chatService = null;

	    Intent intent = new Intent(activity, Activity_Login_Hello.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	    activity.startActivity(intent);
	    activity.finish();

	}

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Activity activity)
    {

	ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
	for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	{
	    if (serviceClass.getName().equals(service.service.getClassName()))
	    {
		return true;
	    }
	}
	return false;
    }

    public static void RotatePicture(String path, Activity activity)
    {

	int inSampleSize = 1;
	inSampleSize = StaticFunction.getBitmapInSampleSize(activity, path);
	Log.e("inSampleSize", inSampleSize + "");

	BitmapFactory.Options opts = new BitmapFactory.Options();
	opts.inSampleSize = inSampleSize;
	Bitmap bm = BitmapFactory.decodeFile(path, opts);
	ExifInterface exif = null;
	String orientString = "";
	try
	{
	    exif = new ExifInterface(path);
	    orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}

	int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

	int rotationAngle = 0;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
	    rotationAngle = 90;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
	    rotationAngle = 180;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
	    rotationAngle = 270;

	if (rotationAngle == 0)
	{
	    saveToFile(path, bm, activity);

	    if (!bm.isRecycled())
	    {
		bm.recycle();
	    }
	}
	else
	{
	    Matrix matrix = new Matrix();

	    matrix.postRotate(rotationAngle);

	    Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	    saveToFile(path, rotatedBitmap, activity);

	    if (!rotatedBitmap.isRecycled())
	    {
		rotatedBitmap.recycle();
	    }
	}

    }

    public static void saveToFile(String filename, Bitmap bmp, Activity activity)
    {

	try
	{
	    FileOutputStream out = new FileOutputStream(filename);
	    bmp.compress(CompressFormat.JPEG, 50, out);
	    out.flush();
	    out.close();

	    if (!bmp.isRecycled())
	    {
		bmp.recycle();
	    }

	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(filename);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    activity.sendBroadcast(mediaScanIntent);
	}
	catch (Exception ex)
	{

	}
    }

    public static Uri convertUriMediaPath(Uri uriFrom)
    {

	if (uriFrom.toString().startsWith("content://com.android.providers.media.documents"))
	{
	    String[] split = uriFrom.toString().split("%3A");
	    Uri uriTo = Uri.parse("content://media/external/images/media/" + split[1]);
	    return uriTo;
	}
	else
	{
	    return uriFrom;
	}
    }

    public static QBUser getUserById(int userID)
    {

	List<Sugar_User> listSugarUser = new ArrayList<Sugar_User>();
	listSugarUser = Sugar_User.find(Sugar_User.class, "USER_ID = ?", userID + "");

	if (listSugarUser != null)
	{
	    if (listSugarUser.size() > 0)
	    {
		Sugar_User sUser = listSugarUser.get(0);

		QBUser user = new QBUser();
		user.setId(sUser.userId);
		user.setLogin(sUser.userLogin);
		user.setFullName(sUser.userFullName);
		user.setWebsite(sUser.userWebsite);
		user.setCustomData(sUser.userCustomData);

		return user;

	    }
	    else
	    {
		return null;
	    }
	}
	else
	{
	    return null;
	}

    }

    @TargetApi(Build.VERSION_CODES.L)
    @SuppressLint("NewApi")
    public static void ChangeEdgeEffect(Context cxt, View list, int color)
    {

	if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
	{

	    EdgeEffect edgeEffectTop = new EdgeEffect(cxt);
	    edgeEffectTop.setColor(color);
	    EdgeEffect edgeEffectBottom = new EdgeEffect(cxt);
	    edgeEffectBottom.setColor(color);

	    try
	    {
		Field f1 = AbsListView.class.getDeclaredField("mEdgeGlowTop");
		f1.setAccessible(true);
		f1.set(list, edgeEffectTop);

		Field f2 = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
		f2.setAccessible(true);
		f2.set(list, edgeEffectBottom);
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	else
	{
	    int glowDrawableId = cxt.getResources().getIdentifier("overscroll_glow", "drawable", "android");
	    Drawable androidGlow = cxt.getResources().getDrawable(glowDrawableId);
	    androidGlow.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);

	    glowDrawableId = cxt.getResources().getIdentifier("overscroll_edge", "drawable", "android");
	    Drawable androidEdge = cxt.getResources().getDrawable(glowDrawableId);
	    androidEdge.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
	}

    }

    public static List<Integer> findListExceptionUserId(List<Integer> usersIDs)
    {

	List<Integer> listExceptionUsersIDs = new ArrayList<Integer>();

	// load user from db first, if not exist, load from server
	// and save to db for later use

	final ArrayList<QBUser> listQBUsers = new ArrayList<QBUser>();
	for (Integer usersID : usersIDs)
	{
	    List<Sugar_User> listSUsers = new ArrayList<Sugar_User>();

	    listSUsers = Sugar_User.find(Sugar_User.class, "USER_ID = ?", usersID + "");

	    if (listSUsers != null)
	    {
		if (listSUsers.size() > 0)
		{
		    Sugar_User sUser = new Sugar_User();
		    sUser = listSUsers.get(0);

		    QBUser qbUser = new QBUser();
		    qbUser.setId(sUser.userId);
		    qbUser.setLogin(sUser.userLogin);
		    qbUser.setWebsite(sUser.userWebsite);
		    qbUser.setFullName(sUser.userFullName);
		    qbUser.setCustomData(sUser.userCustomData);

		    listQBUsers.add(qbUser);

		}
		else
		{
		    listExceptionUsersIDs.add(usersID);
		}

	    }
	    else
	    {
		listExceptionUsersIDs.add(usersID);
	    }

	}

	return listExceptionUsersIDs;

    }

    public static void saveListDialogToDB(ArrayList<QBDialog> listQBDialog)
    {

	if (listQBDialog != null)
	{
	    for (QBDialog qbDialog : listQBDialog)
	    {
		if (!isDialogExstingInDB(qbDialog.getDialogId()))
		{
		    Sugar_Dialog sDialog = new Sugar_Dialog();

		    sDialog.dialogId = qbDialog.getDialogId();
		    sDialog.dialogName = qbDialog.getName();
		    if (qbDialog.getType() == QBDialogType.PRIVATE)
		    {
			sDialog.dialogType = 1;
		    }
		    else if (qbDialog.getType() == QBDialogType.GROUP)
		    {
			sDialog.dialogType = 2;
		    }

		    sDialog.dialogPhoto = qbDialog.getPhoto();

		    sDialog.dialogXmppRoomJid = qbDialog.getRoomJid();

		    if (qbDialog.getOccupants() != null)
		    {
			ArrayList<Integer> listIntIDs = qbDialog.getOccupants();
			String listStringIDs = "";

			for (Integer intID : listIntIDs)
			{
			    listStringIDs += intID + ",";
			}

			sDialog.dialogOccupantsIds = listStringIDs;
		    }

		    if (qbDialog.getLastMessage() != null)
		    {
			sDialog.dialogLastMessage = qbDialog.getLastMessage();
		    }

		    if (qbDialog.getLastMessageDateSent() > 0)
		    {
			sDialog.dialogLastMessageDateSent = qbDialog.getLastMessageDateSent();
		    }
		    else
		    {
			if (qbDialog.getCreatedAt() != null)
			{
			    sDialog.dialogLastMessageDateSent = qbDialog.getCreatedAt().getTime() / 1000;
			}
		    }

		    if (qbDialog.getLastMessageUserId() != null)
		    {
			sDialog.dialogLastMessageUserId = qbDialog.getLastMessageUserId();
		    }
		    if (qbDialog.getUnreadMessageCount() != null)
		    {
			sDialog.dialogUnreadMessagesCount = qbDialog.getUnreadMessageCount();
		    }

		    if (qbDialog.getUserId() != null)
		    {
			sDialog.dialogCreatedUserID = qbDialog.getUserId();
		    }
		    sDialog.isDownloadedMessOfDialog = false;

		    sDialog.save();
		}
		else
		{
		    Sugar_Dialog sDialog2 = findDialogInDBByID(qbDialog.getDialogId());

		    sDialog2.dialogName = qbDialog.getName();

		    sDialog2.dialogPhoto = qbDialog.getPhoto();

		    sDialog2.dialogXmppRoomJid = qbDialog.getRoomJid();

		    if (qbDialog.getOccupants() != null)
		    {
			ArrayList<Integer> listIntIDs = qbDialog.getOccupants();
			String listStringIDs = "";

			for (Integer intID : listIntIDs)
			{
			    listStringIDs += intID + ",";
			}

			sDialog2.dialogOccupantsIds = listStringIDs;
		    }

		    if (qbDialog.getLastMessage() != null)
		    {
			sDialog2.dialogLastMessage = qbDialog.getLastMessage();
		    }

		    if (qbDialog.getLastMessageDateSent() > sDialog2.dialogLastMessageDateSent)
		    {
			sDialog2.dialogLastMessageDateSent = qbDialog.getLastMessageDateSent();
		    }

		    if (qbDialog.getLastMessageUserId() != null)
		    {
			sDialog2.dialogLastMessageUserId = qbDialog.getLastMessageUserId();
		    }
		    if (qbDialog.getUnreadMessageCount() != null)
		    {
			sDialog2.dialogUnreadMessagesCount = qbDialog.getUnreadMessageCount();
		    }

		    if (qbDialog.getUserId() != null)
		    {
			sDialog2.dialogCreatedUserID = qbDialog.getUserId();
		    }
		    sDialog2.save();

		}

	    }
	}

    }

    public static boolean isDialogExstingInDB(String DialogId)
    {

	List<Sugar_Dialog> list = Sugar_Dialog.find(Sugar_Dialog.class, "DIALOG_ID = ?", DialogId);
	if (list != null)
	{
	    if (list.isEmpty())
	    {
		return false;
	    }
	    else
	    {
		return true;
	    }
	}
	else
	{
	    return false;
	}
    }

    public static Sugar_Dialog findDialogInDBByID(String DialogId)
    {

	List<Sugar_Dialog> list = Sugar_Dialog.find(Sugar_Dialog.class, "DIALOG_ID = ?", DialogId);
	if (list != null)
	{
	    if (list.isEmpty())
	    {
		return null;
	    }
	    else
	    {
		return list.get(0);
	    }
	}
	else
	{
	    return null;
	}
    }

    public static ArrayList<Integer> splitStringToArrayInteger(String listID)
    {

	ArrayList<Integer> returnList = new ArrayList<Integer>();

	if (listID != null)
	{

	    StringTokenizer st = new StringTokenizer(listID, ",");

	    while (st.hasMoreTokens())
	    {
		String id = st.nextToken().trim();
		int intID = Integer.parseInt(id);

		returnList.add(intID);

	    }

	}

	return returnList;

    }

    public static int getItemHeightofListView(ListView listView, int items)
    {

	ListAdapter mAdapter = listView.getAdapter();

	int listviewElementsheight = 0;
	// for listview total item height
	// items = mAdapter.getCount();

	for (int i = 0; i < items; i++)
	{

	    View childView = mAdapter.getView(i, null, listView);
	    childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    listviewElementsheight += childView.getMeasuredHeight();
	}

	return listviewElementsheight;

    }

    public static QBPagedRequestBuilder getQBPagedRequestBuilder(int page)
    {

	QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
	pagedRequestBuilder.setPage(page);
	pagedRequestBuilder.setPerPage(200);

	return pagedRequestBuilder;
    }

    public static String findLastMessageforDialog(String dialogID)
    {

	String[] str = new String[1];
	str[0] = dialogID;
	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ?", str, "", "MESS_DATE_SENT DESC", "1");

	if (listsss.size() > 0)
	{
	    return listsss.get(0).messMessage;
	}
	else
	{
	    Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialogID);
	    if (sDialog != null)
	    {
		if (sDialog.dialogLastMessage != null)
		{
		    return sDialog.dialogLastMessage;
		}
		else
		{
		    return "...";

		}
	    }
	    else
	    {
		return "...";
	    }
	}

    }

    public static boolean isUserOnline(QBUser user)
    {

	long currentTime = System.currentTimeMillis();
	if (user.getLastRequestAt() != null)
	{
	    long userLastRequestAtTime = user.getLastRequestAt().getTime();

	    // if user didn't do anything last 5 minutes (5*60*1000
	    // milliseconds)
	    if ((currentTime - userLastRequestAtTime) > 3 * 60 * 1000)
	    {
		// user is offline now
		return false;
	    }
	    else
	    {
		return true;
	    }

	}
	else
	{
	    return false;
	}

    }

    public static void saveSugarUserToDB(ArrayList<QBUser> newUsers)
    {

	for (QBUser qbUser : newUsers)
	{

	    if (isUserExistingInDB(qbUser.getId()) == false)
	    {

		Sugar_User sUser = new Sugar_User();
		sUser.userId = qbUser.getId();
		sUser.userLogin = qbUser.getLogin();
		sUser.userFullName = qbUser.getFullName();
		sUser.userWebsite = qbUser.getWebsite();
		sUser.userCustomData = qbUser.getCustomData();
		sUser.userPassword = qbUser.getPassword();

		sUser.save();
	    }

	}

    }

    public static boolean isUserExistingInDB(int userID)
    {

	List<Sugar_User> listSUsers = new ArrayList<Sugar_User>();

	listSUsers = Sugar_User.find(Sugar_User.class, "USER_ID = ?", userID + "");

	if (listSUsers != null)
	{
	    if (listSUsers.size() > 0)
	    {
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
	else
	{
	    return false;
	}
    }

    public static Sugar_User getUserFromDBByID(int userID)
    {

	List<Sugar_User> listSUsers = new ArrayList<Sugar_User>();

	listSUsers = Sugar_User.find(Sugar_User.class, "USER_ID = ?", userID + "");

	if (listSUsers != null)
	{
	    if (listSUsers.size() > 0)
	    {
		return listSUsers.get(0);
	    }
	    else
	    {
		return null;
	    }
	}
	else
	{
	    return null;
	}

    }

    public static QBUser SugarUserToQBUser(Sugar_User sUser)
    {

	if (sUser != null)
	{
	    QBUser qbUser = new QBUser();
	    qbUser.setId(sUser.userId);
	    qbUser.setLogin(sUser.userLogin);
	    qbUser.setWebsite(sUser.userWebsite);
	    qbUser.setFullName(sUser.userFullName);
	    qbUser.setCustomData(sUser.userCustomData);

	    return qbUser;
	}
	else
	{
	    return null;
	}

    }

    public static Sugar_Message findMessageByID(String messID)
    {

	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", messID);

	if (listsss.size() > 0)
	{
	    return listsss.get(0);
	}
	else
	{
	    return null;
	}

    }

    public static long findLastMessageTimeforDialog(String dialogID)
    {

	String[] str = new String[1];
	str[0] = dialogID;
	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ?", str, "", "MESS_DATE_SENT DESC", "1");

	if (listsss.size() > 0)
	{
	    return listsss.get(0).messDateSent;
	}
	else
	{
	    return 0;
	}

    }

    public static long findLastMessageTimeforDialog1(String dialogID)
    {

	String[] str = new String[1];
	str[0] = dialogID;
	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ?", str, "", "ID DESC", "1");

	if (listsss.size() > 0)
	{
	    return listsss.get(0).messDateSent;
	}
	else
	{
	    return 0;
	}

    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isInsertDate(String diaID)
    {

	boolean isInsert = true;
	// get previous mess to check date
	long previoustime = 0;// findLastMessageTimeforDialog(diaID);

	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ? AND IS_SHOW_DATE = ?", diaID, "true");

	for (int i = 0; i < listsss.size(); i++)
	{
	    previoustime = listsss.get(i).messDateSent;

	    if (previoustime != 0)
	    {
		Date previousDate = new Date(previoustime);

		Date dateToSet = new Date(java.lang.System.currentTimeMillis());

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

		if (fmt.format(previousDate).equals(fmt.format(dateToSet)))
		{
		    // if previous time < current time
		    // isInsert = false -> no insert
		    if (previousDate.before(dateToSet))
		    {

			isInsert = false;
			break;
		    }
		    else
		    {
			listsss.get(i).isShowDate = "false";
			listsss.get(i).save();
			isInsert = true;
			break;
		    }
		}

	    }

	}

	return isInsert;

    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isInsertDateHistory(String diaID, long historydate)
    {

	boolean isInsert = true;
	// get previous mess to check date
	long previoustime = 0;// findLastMessageTimeforDialog(diaID);

	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ? AND IS_SHOW_DATE = ?", diaID, "true");

	for (int i = 0; i < listsss.size(); i++)
	{
	    previoustime = listsss.get(i).messDateSent;

	    if (previoustime != 0)
	    {
		Date previousDate = new Date(previoustime);

		Date dateToSet = new Date(historydate);

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		if (fmt.format(previousDate).equals(fmt.format(dateToSet)))
		{
		    // if previous time < current time
		    // isInsert = false
		    if (previousDate.before(dateToSet))
		    {

			isInsert = false;
			break;
		    }
		    else
		    {
			listsss.get(i).isShowDate = "false";
			listsss.get(i).save();
			isInsert = true;
			break;
		    }
		}

	    }

	}

	return isInsert;

    }

    public static void saveMessageToDB(QBChatMessage mess)
    {

	List<Sugar_Message> listS = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", mess.getId());

	if (listS != null)
	{
	    if (listS.size() > 0)
	    {
		return;
	    }
	}

	Sugar_Message sMess = new Sugar_Message();
	sMess.messId = mess.getId();
	sMess.messSenderId = mess.getSenderId();
	sMess.messRecipientId = mess.getRecipientId();
	sMess.messChatDialogId = mess.getProperty("dialog_id");
	sMess.messMessage = mess.getBody();

	sMess.messDateSent = java.lang.System.currentTimeMillis();

	boolean isInsertDate = isInsertDate(sMess.messChatDialogId);
	if (isInsertDate == true)
	{
	    sMess.isShowDate = "true";
	}
	else
	{
	    sMess.isShowDate = "false";
	}

	if (mess.getAttachments() != null)
	{
	    if (mess.getAttachments().size() > 0)
	    {
		for (final QBAttachment att : mess.getAttachments())
		{
		    sMess.messAttachment = att.getUrl();
		}
	    }
	}

	sMess.save();

    }

    public static void saveMessageToDB(QBMessage mess, String messID, int senderID, int recipientID, String dialogID, String messBody, boolean sendFail)
    {

	List<Sugar_Message> listS = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", messID);

	if (listS != null)
	{
	    if (listS.size() > 0)
	    {
		return;
	    }
	}

	Sugar_Message sMess = new Sugar_Message();
	sMess.messId = messID;
	sMess.messSenderId = senderID;
	sMess.messRecipientId = recipientID;
	sMess.messChatDialogId = dialogID;
	sMess.messMessage = messBody;

	if (sendFail == true)
	{
	    sMess.messRead = 3;
	}

	if (mess instanceof QBChatHistoryMessage)
	{
	    sMess.messDateSent = (((QBChatHistoryMessage) mess).getDateSent() * 1000);
	    // date = new Date(((QBChatHistoryMessage) message).getDateSent() *
	    // 1000);
	}
	else
	{
	    sMess.messDateSent = java.lang.System.currentTimeMillis();
	}

	boolean isInsertDate = isInsertDateHistory(sMess.messChatDialogId, sMess.messDateSent);

	if (isInsertDate == true)
	{
	    sMess.isShowDate = "true";
	}
	else
	{
	    sMess.isShowDate = "false";
	}

	if (mess.getAttachments() != null)
	{
	    if (mess.getAttachments().size() > 0)
	    {
		for (final QBAttachment att : mess.getAttachments())
		{
		    sMess.messAttachment = att.getUrl();
		}
	    }
	}

	sMess.save();

    }

    public static void saveMessageToDBFromGCM(String messID, int senderID, int recipientID, String dialogID, String messBody, String attachURL)
    {

	List<Sugar_Message> listS = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", messID);

	if (listS != null)
	{
	    if (listS.size() > 0)
	    {
		return;
	    }
	}

	Sugar_Message sMess = new Sugar_Message();
	sMess.messId = messID;
	sMess.messSenderId = senderID;
	sMess.messRecipientId = recipientID;
	sMess.messChatDialogId = dialogID;
	sMess.messMessage = messBody;
	sMess.messDateSent = java.lang.System.currentTimeMillis();

	boolean isInsertDate = isInsertDate(sMess.messChatDialogId);
	if (isInsertDate == true)
	{
	    sMess.isShowDate = "true";
	}
	else
	{
	    sMess.isShowDate = "false";
	}

	sMess.messAttachment = attachURL;

	sMess.save();

    }

    public static Integer getOpponentIDForPrivateDialog(QBDialog dialog, Context ctx)
    {

	int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(ctx);

	Integer opponentID = -1;
	for (Integer userID : dialog.getOccupants())
	{
	    if (!userID.equals(currentLoginUserID))
	    {
		opponentID = userID;
		break;
	    }
	}
	return opponentID;
    }

}
