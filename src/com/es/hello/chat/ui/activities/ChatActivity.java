package com.es.hello.chat.ui.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.GCMIntentService;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.core.ChatManager;
import com.es.hello.chat.core.GroupChatManagerImpl;
import com.es.hello.chat.core.PrivateChatManagerImpl;
import com.es.hello.chat.services.HelloMainService;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_Message;
import com.es.hello.chat.sugarobject.Sugar_Noti;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.adapters.ChatAdapter;
import com.es.hello.chat.ui.adapters.GroupChatUsersAdapter;
import com.es.hello.chat.ui.fragments.UsersFragment;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.BaseService;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class ChatActivity extends ActionBarActivity implements HelloMainService.ServiceInterfaceCallback, HelloMainService.ConnectionInterfaceCallback
{

    public interface ChatCallback
    {

	public void onNewPrivateMessage(String dialogId, String message);

    }

    public interface MyTypingCallback
    {

	public void onSenderTyping();

	public void onSenderStopTyping();
    }

    public boolean isFromPushNotification = false;

    public MyTypingCallback mTypingCallback;

    public static ChatCallback chatCallback;

    public static String WHERE = "";

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_MODE = "mode";

    public static final String EXTRA_DIALOG = "dialog";

    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private EditText messageEditText;

    private ListView messagesContainer;

    private ImageView sendButton;

    private ImageView btnCapture;

    private ImageView cancelAttachButton;

    private ImageView imgPreviewAttachment;

    private RelativeLayout progressBar;

    public static Mode mode = Mode.PRIVATE;

    private ChatManager chat;

    public ChatAdapter adapter;

    private QBDialog dialog;

    String blockedList = "";

    public TextView txtTypingStatus;

    Boolean isAttached = false;

    Boolean isFromCamera = false;

    public String ATTACH_FILE_PATH = "";

    public String ATTACH_FILE_NAME = "";

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    Boolean isAttachAnImage = false;

    File attachFile;

    LinearLayout chatBarContainer;

    ImageView imgViewActionBarAvatar;

    ArrayList<QBMessage> messInQueue = new ArrayList<QBMessage>();

    public static String DialogIdForNotification = "";

    static Uri capturedImageUri = null;

    TextView txtTitle;

    TextView txtSubTitle;

    RelativeLayout titleContainer;

    public static Class reIntentClass;

    public String dialog_id = "";

    GestureDetector gestureDetector;

    LinearLayout btnBack;

    public LinearLayout typingStatusContainer;

    public static void start(Context context, Bundle bundle)
    {

	Intent intent = new Intent(context, ChatActivity.class);
	intent.putExtras(bundle);
	context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {

	super.onNewIntent(intent);
	Log.e("onNewIntent", "onNewIntent");
    }

    @Override
    protected void onStart()
    {

	// TODO Auto-generated method stub
	super.onStart();
	SharePrefsHelper.saveIsChatActivityRunningToSharePrefs(true, ChatActivity.this);
    }

    @Override
    protected void onPause()
    {

	// TODO Auto-generated method stub
	super.onPause();
	// IS_ACTIVITY_CHAT_RUNNING = false;
	SharePrefsHelper.saveIsChatActivityRunningToSharePrefs(false, ChatActivity.this);
    }

    @Override
    protected void onStop()
    {

	// TODO Auto-generated method stub
	super.onStop();
	// IS_ACTIVITY_CHAT_RUNNING = false;
	SharePrefsHelper.saveIsChatActivityRunningToSharePrefs(false, ChatActivity.this);

    }

    @Override
    protected void onResume()
    {

	// TODO Auto-generated method stub
	super.onResume();
	// IS_ACTIVITY_CHAT_RUNNING = true;
	SharePrefsHelper.saveIsChatActivityRunningToSharePrefs(true, ChatActivity.this);

	HelloMainService.ConnectionCallback = this;

	switch (mode)
	{
	    case GROUP:

		if (dialog != null)
		{

		    if (Activity_Group_Chat_Settings.isCloseChatActivity == true)
		    {
			finish();
		    }
		    else
		    {

			Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog.getDialogId());

			if (sDialog != null)
			{
			    if (sDialog.dialogPhoto != null)
			    {
				imageLoader.displayImage(sDialog.dialogPhoto, imgViewActionBarAvatar, options);
			    }
			    else
			    {
				// getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_room));
				imgViewActionBarAvatar.setImageResource(R.drawable.ic_room);
			    }

			    // update group name
			    if (sDialog.dialogName != null)
			    {
				txtTitle.setText(sDialog.dialogName);
			    }

			}
		    }

		}

	}

    }

    private void initActionBar1()
    {

	ActionBar actionBar = getActionBar();
	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	actionBar.setDisplayHomeAsUpEnabled(true);
	actionBar.setDisplayOptions(getActionBar().getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
	getActionBar().setTitle("");

	// action bar custom view
	LayoutInflater mInflater = LayoutInflater.from(this);

	View mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_chat, null);
	titleContainer = (RelativeLayout) mCustomView.findViewById(R.id.titleContainer);
	txtTitle = (TextView) mCustomView.findViewById(R.id.txt_title);
	txtTitle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(this), Typeface.BOLD);

	txtSubTitle = (TextView) mCustomView.findViewById(R.id.txt_sub_title);
	txtSubTitle.setTextColor(Color.parseColor("#666666"));
	txtSubTitle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(this), Typeface.BOLD);

	actionBar.setCustomView(mCustomView);
	actionBar.setDisplayShowCustomEnabled(true);
	// action bar custom view

	// Calculate ActionBar height
	int actionBarHeight = 64;
	TypedValue tv = new TypedValue();
	if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
	{
	    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
	}

	imgViewActionBarAvatar = (ImageView) findViewById(R.id.toolbar_icon);
	// imgViewActionBarAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
	// FrameLayout.LayoutParams param = new
	// FrameLayout.LayoutParams(actionBarHeight - 15, actionBarHeight - 15);
	// param.leftMargin = 25;
	// param.rightMargin = 10;
	// imgViewActionBarAvatar.setLayoutParams(param);
	// Calculate ActionBar height
    }

    private void initToolBar()
    {

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBarForChatOnly(this, toolbar, "");

	android.support.v7.app.ActionBar actionBar = getSupportActionBar();
	actionBar.setDisplayOptions(getSupportActionBar().getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);

	// action bar custom view
	LayoutInflater mInflater = LayoutInflater.from(this);

	View mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_chat, null);
	titleContainer = (RelativeLayout) mCustomView.findViewById(R.id.titleContainer);
	txtTitle = (TextView) mCustomView.findViewById(R.id.txt_title);
	txtTitle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(this), Typeface.BOLD);

	txtSubTitle = (TextView) mCustomView.findViewById(R.id.txt_sub_title);
	txtSubTitle.setTextColor(Color.parseColor("#666666"));
	txtSubTitle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(this), Typeface.BOLD);

	actionBar.setCustomView(mCustomView);
	actionBar.setDisplayShowCustomEnabled(true);
	// action bar custom view

	// Calculate ActionBar height
	/*int actionBarHeight = 144;
	TypedValue tv = new TypedValue();
	if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
	{
	    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
	}*/

	imgViewActionBarAvatar = (ImageView) findViewById(R.id.toolbar_icon);
	/*imgViewActionBarAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
	FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(actionBarHeight - 15, actionBarHeight - 15);
	param.leftMargin = 25;
	param.rightMargin = 10;
	imgViewActionBarAvatar.setLayoutParams(param);*/
	// Calculate ActionBar height
    }

    LinearLayout layoutConnection;

    TextView txtConnactionStatus;

    private void initConnectionBar()
    {

	layoutConnection = (LinearLayout) findViewById(R.id.layoutTxtConnectionStatus);
	txtConnactionStatus = (TextView) findViewById(R.id.txtConnectionStatus);

    }

    private BroadcastReceiver activityReceiver = new BroadcastReceiver()
    {

	@Override
	public void onReceive(Context context, Intent intent)
	{

	    String dialog_id = intent.getExtras().getString("DIALOG_ID");

	    loadDialogProfileInfoForGroupChat(dialog_id);

	}
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_chat);

	Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");

	getWindow().setBackgroundDrawableResource(R.drawable.bgchat1);

	initConnectionBar();
	HelloMainService.ConnectionCallback = this;

	isFromPushNotification = false;

	StaticFunction.initImageLoader(ChatActivity.this);

	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	SharePrefsHelper.saveIsChatActivityRunningToSharePrefs(true, ChatActivity.this);

	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);

	initToolBar();

	//
	//

	chatBarContainer = (LinearLayout) findViewById(R.id.container1);
	chatBarContainer.setVisibility(View.GONE);
	// resgister service
	progressBar.setVisibility(View.VISIBLE);
	HelloMainService.Callback = this;
	Intent intenttt = new Intent(this, HelloMainService.class);
	intenttt.addCategory(HelloMainService.TAG);
	startService(intenttt);
	// register service

	//
	if (activityReceiver != null)
	{
	    // Create an intent filter to listen to the broadcast sent with the
	    // action "ACTION_STRING_ACTIVITY"
	    IntentFilter intentFilter = new IntentFilter(GCMIntentService.ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES);
	    // Map the intent filter to the receiver
	    registerReceiver(activityReceiver, intentFilter);
	}
	//

    }

    @Override
    protected void onDestroy()
    {

	super.onDestroy();
	try
	{

	    if (activityReceiver != null)
		this.unregisterReceiver(activityReceiver);
	    activityReceiver = null;

	}
	catch (Exception e)
	{

	}
    }

    private void loadDialogProfileInfoForGroupChat(String dialogID)
    {

	if (!isFinishing())
	{
	    if (dialog_id != null)
	    {
		if (dialog_id.equalsIgnoreCase(dialogID))
		{
		    Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialogID);
		    txtTitle.setText(sDialog.dialogName);

		    if (sDialog.dialogPhoto != null)
		    {
			try
			{
			    imageLoader.displayImage(sDialog.dialogPhoto, imgViewActionBarAvatar, options);

			}
			catch (Exception ex)
			{

			}
		    }
		    else
		    {
			imgViewActionBarAvatar.setImageResource(R.drawable.ic_room);
		    }
		}

	    }
	}
	// getDialogAndLoadChatMessage(dialogID);

	/*final QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
	customObjectRequestBuilder.eq("_id", dialogID);

	QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>()
	{

	    @Override
	    public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args)
	    {

		if (!isFinishing())
		{
		    if (dialog_id != null)
		    {
			if (dialog_id.equalsIgnoreCase(dialogs.get(0).getDialogId()))
			{

			    txtTitle.setText(dialogs.get(0).getName());

			    if (dialogs.get(0).getPhoto() != null)
			    {
				try
				{
				    // Uri myUri = Uri.parse(dialog.getPhoto());
				    // imgViewActionBarAvatar.setImageURI(myUri);
				    imageLoader.displayImage(dialogs.get(0).getPhoto(), imgViewActionBarAvatar, options);

				}
				catch (Exception ex)
				{

				}
			    }
			    else
			    {
				// getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_room));
				imgViewActionBarAvatar.setImageResource(R.drawable.ic_room);
			    }

			}
		    }
		    StaticFunction.saveListDialogToDB(dialogs);

		}

	    }

	    @Override
	    public void onError(final List<String> errors)
	    {

	    }
	});*/

    }

    private void getDialogAndLoadChatMessage(String dialog_id)
    {

	boolean isDialogExistingInDB = StaticFunction.isDialogExstingInDB(dialog_id);

	if (isDialogExistingInDB == true)
	{
	    List<Sugar_Dialog> list = Sugar_Dialog.find(Sugar_Dialog.class, "DIALOG_ID = ?", dialog_id);
	    Sugar_Dialog sDialog = list.get(0);

	    //

	    QBDialog qbDialog = new QBDialog();
	    qbDialog.setDialogId(sDialog.dialogId);
	    qbDialog.setName(sDialog.dialogName);
	    if (sDialog.dialogType == 1)
	    {
		qbDialog.setType(QBDialogType.PRIVATE);
		mode = Mode.PRIVATE;
	    }
	    else if (sDialog.dialogType == 2)
	    {
		qbDialog.setType(QBDialogType.GROUP);
		mode = Mode.GROUP;
	    }

	    qbDialog.setPhoto(sDialog.dialogPhoto);
	    qbDialog.setRoomJid(sDialog.dialogXmppRoomJid);

	    ArrayList<Integer> listIDs = new ArrayList<Integer>();
	    listIDs = StaticFunction.splitStringToArrayInteger(sDialog.dialogOccupantsIds);

	    if (!listIDs.isEmpty())
	    {
		qbDialog.setOccupantsIds(listIDs);

		qbDialog.setLastMessage(sDialog.dialogLastMessage);
		qbDialog.setLastMessageDateSent(sDialog.dialogLastMessageDateSent);
		qbDialog.setLastMessageUserId(sDialog.dialogLastMessageUserId);
		qbDialog.setUnreadMessageCount(sDialog.dialogUnreadMessagesCount);
		qbDialog.setUserId(sDialog.dialogCreatedUserID);

		//

		dialog = qbDialog;

		List<Integer> usersIDs = new ArrayList<Integer>();

		usersIDs.addAll(dialog.getOccupants());

		List<Integer> listExceptionUsersIDs = new ArrayList<Integer>();

		listExceptionUsersIDs = StaticFunction.findListExceptionUserId(usersIDs);

		if (listExceptionUsersIDs.size() > 0)
		{

		    // Get all occupants info
		    QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
		    requestBuilder.setPage(1);
		    requestBuilder.setPerPage(usersIDs.size());

		    QBUsers.getUsersByIDs(listExceptionUsersIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>()
		    {

			@Override
			public void onSuccess(ArrayList<QBUser> users, Bundle params)
			{

			    StaticFunction.saveSugarUserToDB(users);
			    progressBar.setVisibility(View.GONE);
			    initViews();

			}

			@Override
			public void onError(final List<String> errors)
			{

			    runOnUiThread(new Runnable()
			    {

				@Override
				public void run()
				{

				    progressBar.setVisibility(View.GONE);
				    showPopupNoInternet(ChatActivity.this, ChatActivity.this, errors.toString());

				}
			    });

			}

		    });

		}
		else
		{
		    initViews();
		}

	    }
	    else
	    {
		getDialogWhenItisnotInDB();
	    }

	}
	else
	{
	    getDialogWhenItisnotInDB();
	}

    }

    private void getDialogWhenItisnotInDB()
    {

	QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
	customObjectRequestBuilder.eq("_id", dialog_id);

	QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>()
	{

	    @Override
	    public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args)
	    {

		Log.e("CHAT ACTIVITY", "getChatDialogs: " + "onSuccess");

		StaticFunction.saveListDialogToDB(dialogs);

		dialog = dialogs.get(0);

		Log.e("CHAT ACTIVITY", "dialog = dialogs.get: " + dialog.getDialogId());

		if (dialog.getType().equals(QBDialogType.GROUP))
		{
		    mode = ChatActivity.Mode.GROUP;
		    Log.e("CHAT ACTIVITY", "ChatActivity.Mode.GROUP");
		}
		else
		{
		    mode = ChatActivity.Mode.PRIVATE;
		    Log.e("CHAT ACTIVITY", "ChatActivity.Mode.PRIVATE");
		}

		// collect all occupants ids
		//
		List<Integer> usersIDs = new ArrayList<Integer>();

		usersIDs.addAll(dialog.getOccupants());

		Log.e("CHAT ACTIVITY", "usersIDs: " + usersIDs.toString());

		// Get all occupants info
		//
		QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
		requestBuilder.setPage(1);
		requestBuilder.setPerPage(usersIDs.size());
		//

		QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>()
		{

		    @Override
		    public void onSuccess(ArrayList<QBUser> users, Bundle params)
		    {

			Log.e("CHAT ACTIVITY", "getUsersByIDs: " + "onSuccess");
			StaticFunction.saveSugarUserToDB(users);
			progressBar.setVisibility(View.GONE);
			initViews();

		    }

		    @Override
		    public void onError(final List<String> errors)
		    {

			runOnUiThread(new Runnable()
			{

			    @Override
			    public void run()
			    {

				showPopupNoInternet(ChatActivity.this, ChatActivity.this, errors.toString());
				Log.e("CHAT ACTIVITY", "getUsersByIDs: " + "onError");

			    }
			});

		    }

		});

	    }

	    @Override
	    public void onError(final List<String> errors)
	    {

		runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			showPopupNoInternet(ChatActivity.this, ChatActivity.this, errors.toString());
			Log.e("CHAT ACTIVITY", "getChatDialogs: " + "onError");

		    }
		});

	    }
	});

    }

    @Override
    public void onBackPressed()
    {

	try
	{
	    if (chat != null)
	    {
		chat.release();
	    }

	}
	catch (XMPPException e)
	{
	    Log.e(TAG, "failed to release chat", e);
	}

	if (isFromPushNotification == true)
	{
	    Intent intent = new Intent(ChatActivity.this, DialogsActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    startActivity(intent);

	    finish();
	}
	else
	{
	    finish();
	}

	super.onBackPressed();
    }

    @SuppressWarnings("rawtypes")
    private void initViews()
    {

	Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");

	messagesContainer = (ListView) findViewById(R.id.messagesContainer);
	messageEditText = (EditText) findViewById(R.id.messageEdit);
	sendButton = (ImageView) findViewById(R.id.chatSendButton);
	btnCapture = (ImageView) findViewById(R.id.chatCaptureButton);
	cancelAttachButton = (ImageView) findViewById(R.id.chatCancelButtonAttach);
	imgPreviewAttachment = (ImageView) findViewById(R.id.imgPreviewAttachment);
	txtTypingStatus = (TextView) findViewById(R.id.txtTypingStatus);
	typingStatusContainer = (LinearLayout) findViewById(R.id.containerStatus);
	btnBack = (LinearLayout) findViewById(R.id.btnBack);

	btnBack.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		onBackPressed();
	    }
	});

	messageEditText.setOnKeyListener(new OnKeyListener()
	{

	    @Override
	    public boolean onKey(View v, int keyCode, KeyEvent event)
	    {

		if (keyCode == KeyEvent.KEYCODE_ENTER)
		{

		}
		return false;
	    }
	});

	switch (mode)
	{
	    case GROUP:

		if (dialog.getPhoto() != null)
		{
		    imageLoader.displayImage(dialog.getPhoto(), imgViewActionBarAvatar, options);
		}
		else
		{
		    // imageLoader.displayImage(dialog.getPhoto(),
		    // imgViewActionBarAvatar, options);
		    // getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_room));
		    imgViewActionBarAvatar.setImageResource(R.drawable.ic_room);
		}

		txtTitle.setText(dialog.getName());
		txtSubTitle.setText("Group");
		// getActionBar().setTitle(dialog.getName());

		chat = new GroupChatManagerImpl(this);

		// Join group chat
		progressBar.setVisibility(View.VISIBLE);
		//
		((GroupChatManagerImpl) chat).joinGroupChat(dialog, new QBEntityCallbackImpl()
		{

		    @Override
		    public void onSuccess()
		    {

			// Load Chat history
			ChatActivity.DialogIdForNotification = dialog.getDialogId();
			loadChatHistory(Mode.GROUP, dialog.getDialogId());
			// loadChatHistory(Mode.GROUP);

		    }

		    @Override
		    public void onError(final List list)
		    {

			if (list.toString().equalsIgnoreCase("[NoResponseException]"))
			{
			    runOnUiThread(new Runnable()
			    {

				@Override
				public void run()
				{

				    showPopupNoInternet2(ChatActivity.this, ChatActivity.this, "Connect to server fail. Please try again!");

				}
			    });
			}
			else
			{
			    runOnUiThread(new Runnable()
			    {

				@Override
				public void run()
				{

				    showPopupNoInternet(ChatActivity.this, ChatActivity.this, list.toString());

				}
			    });
			}

		    }
		});

		/*class GestureDoubleTap extends GestureDetector.SimpleOnGestureListener
		{

		    @Override
		    public boolean onDown(MotionEvent e)
		    {

			return true;
		    }

		    @Override
		    public boolean onDoubleTap(MotionEvent e)
		    {

			// some logic
			Intent inttent = new Intent(ChatActivity.this, Activity_Group_Chat_Settings.class);
			inttent.putExtra("ACTION", "UPDATE_GROUP_CHAT");
			Bundle bundle = new Bundle();
			bundle.putSerializable("DIALOGFROMCHAT", dialog);
			inttent.putExtras(bundle);
			startActivity(inttent);
			return true;
		    }

		}

		GestureDoubleTap gestureDoubleTap = new GestureDoubleTap();
		gestureDetector = new GestureDetector(this context , gestureDoubleTap);*/

		/*txtTitle.setOnTouchListener(new View.OnTouchListener()
		{

		    @Override
		    public boolean onTouch(View view, MotionEvent motionEvent)
		    {

			return gestureDetector.onTouchEvent(motionEvent);
		    }

		});*/

		titleContainer.setOnClickListener(new OnClickListener()
		{

		    @Override
		    public void onClick(View v)
		    {

			Intent inttent = new Intent(ChatActivity.this, Activity_Group_Chat_Settings.class);
			inttent.putExtra("ACTION", "UPDATE_GROUP_CHAT");
			Bundle bundle = new Bundle();
			bundle.putSerializable("DIALOGFROMCHAT", dialog);
			inttent.putExtras(bundle);
			startActivity(inttent);
			// finish();

		    }
		});

		break;
	    case PRIVATE:

		// QBUser mySeft = ApplicationSingleton.getCurrentUser();

		// myBlockedList = mySeft.getCustomData();

		Integer opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, ChatActivity.this);

		QBUser opponentUser = StaticFunction.getUserById(opponentID);

		// myBlockedList = opponentUser.getCustomData();

		ChatActivity.DialogIdForNotification = dialog.getDialogId();

		String avartaPath = opponentUser.getWebsite(); // ApplicationSingleton.getDialogsUsers().get(opponentID).getWebsite();

		if (avartaPath != null)
		{
		    imageLoader.displayImage(avartaPath, imgViewActionBarAvatar, options);
		}
		else
		{
		    // getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_contact_picture));
		    imgViewActionBarAvatar.setImageResource(R.drawable.ic_contact_picture);
		}

		if (opponentUser.getCustomData() != null)
		{
		    int currentLogInUserID = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);

		    // QBUser currentLoginUser =
		    // SharePrefsHelper.getCurrentLoginUser(ChatActivity.this);

		    if (opponentUser.getCustomData().contains(currentLogInUserID + ""))
		    {
			chatBarContainer.setVisibility(View.GONE);
			optionMenuState = 1;
			invalidateOptionsMenu();

		    }
		}

		chat = new PrivateChatManagerImpl(this, opponentID, dialog.getDialogId());

		txtTitle.setText(opponentUser.getLogin());

		txtSubTitle.setText("Online");

		// Load CHat history
		loadChatHistory(Mode.PRIVATE, dialog.getDialogId());
		// loadChatHistory(Mode.PRIVATE);

		getUserByIDFromServer(opponentID);

		titleContainer.setOnClickListener(new OnClickListener()
		{

		    @Override
		    public void onClick(View v)
		    {

			Intent inttent = new Intent(ChatActivity.this, Activity_Opponent_Profile.class);
			startActivity(inttent);

		    }
		});

		break;
	}

	cancelAttachButton.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		isAttached = false;
		ATTACH_FILE_PATH = "";
		ATTACH_FILE_NAME = "";
		if (cancelAttachButton.getVisibility() == View.VISIBLE)
		{
		    cancelAttachButton.setVisibility(View.GONE);
		}
		imgPreviewAttachment.setVisibility(View.GONE);

	    }
	});

	// addListenerToEditTextChatBar();

	btnCapture.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		// dispatchTakePictureIntent();

		Calendar cal = Calendar.getInstance();
		File file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));
		if (!file.exists())
		{
		    try
		    {
			file.createNewFile();
		    }
		    catch (IOException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		else
		{
		    file.delete();
		    try
		    {
			file.createNewFile();
		    }
		    catch (IOException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		capturedImageUri = Uri.fromFile(file);
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
		startActivityForResult(i, 1);

	    }
	});

	sendButton.setOnClickListener(new View.OnClickListener()
	{

	    @SuppressLint("UseSparseArrays")
	    @Override
	    public void onClick(View v)
	    {

		String body = messageEditText.getText().toString().trim();

		if (isAttached == true)
		{
		    isAttached = false;
		    imgPreviewAttachment.setVisibility(View.GONE);
		    cancelAttachButton.setVisibility(View.GONE);
		    if (isAttachAnImage == false)
		    {
			uploadContent(attachFile, true, ATTACH_FILE_PATH, ATTACH_FILE_NAME);
		    }
		}
		else
		{
		    if (!body.equals(""))
		    {

			messageEditText.setText("");

			Integer opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, ChatActivity.this);

			QBChatMessage chatMessage = new QBChatMessage();
			chatMessage.setBody(body);
			chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
			//
			chatMessage.setProperty("dialog_id", dialog.getDialogId());
			chatMessage.setProperty("sender_id", SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this) + "");
			if (mode == Mode.PRIVATE)
			{
			    chatMessage.setProperty("recipient_id", opponentID + "");
			}
			else
			{
			    chatMessage.setProperty("recipient_id", -1 + "");
			}
			//
			chatMessage.setMarkable(true);

			try
			{
			    chat.sendMessage(chatMessage);

			    if (mode == Mode.PRIVATE)
			    {

				if (adapter != null)
				{
				    showMessage(chatMessage);
				    if (chatCallback != null)
				    {
					chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
				    }
				}
				else
				{
				    messInQueue.add(chatMessage);
				}

				StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
				userIds.add(opponentID);

				QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(ChatActivity.this);
				String senderUsername = currentLoginUser.getLogin();
				int currentUserId = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);

				sendPushNotificationToOpponentUser(v, body, userIds, senderUsername, dialog.getDialogId(), chatMessage.getId(), currentUserId, opponentID + "", "PRIVATE", "");
			    }
			    else
			    {

				if (chatCallback != null)
				{
				    chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
				}

				// collect all occupants ids
				StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();

				Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog.getDialogId());

				ArrayList<Integer> listIDs = new ArrayList<Integer>();
				listIDs = StaticFunction.splitStringToArrayInteger(sDialog.dialogOccupantsIds);

				usersIDs.addAll(listIDs);

				int currentUserId = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);

				sendPushNotificationToOpponentUser(v, body, usersIDs, dialog.getName(), dialog.getDialogId(), chatMessage.getId(), currentUserId, usersIDs.toString(), "GROUP", "");

			    }

			}
			catch (Exception e)
			{

			    String dia = chatMessage.getProperty("dialog_id");
			    String sender = chatMessage.getProperty("sender_id");
			    String messBody = chatMessage.getBody();
			    String messID = chatMessage.getId();

			    Toast.makeText(ChatActivity.this, "sending fail! try again...", Toast.LENGTH_LONG).show();
			    StaticFunction.saveMessageToDB(chatMessage, messID, Integer.parseInt(sender), -999, dia, messBody, true);

			    if (mode == Mode.PRIVATE)
			    {

				if (adapter != null)
				{
				    showMessage(chatMessage);
				    if (chatCallback != null)
				    {
					chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
				    }
				}
				else
				{
				    messInQueue.add(chatMessage);
				}
			    }
			    else
			    {
				if (chatCallback != null)
				{
				    chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
				}
			    }
			}

		    }
		}

	    }
	});

	messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{

	    @Override
	    public void onFocusChange(View v, boolean hasFocus)
	    {

		if (!hasFocus)
		{
		    hideKeyboard(v);
		}
	    }
	});

	if (mode == Mode.PRIVATE)
	{
	    messageEditText.addTextChangedListener(new TextWatcher()
	    {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{

		    if (s.length() > 0)
		    {

			mTypingCallback.onSenderTyping();

			/*try
			{
			    ((PrivateChatManagerImpl) chat).privateChat.sendIsTypingNotification();
			}
			catch (NotConnectedException e)
			{

			}
			catch (XMPPException e)
			{

			}*/
		    }
		    else
		    {

			mTypingCallback.onSenderStopTyping();

			/*try
			{
			    ((PrivateChatManagerImpl) chat).privateChat.sendStopTypingNotification();

			}
			catch (NotConnectedException e)
			{

			}
			catch (XMPPException e)
			{

			}*/
		    }

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void afterTextChanged(Editable s)
		{

		}
	    });
	}
    }

    /*
     * @SuppressWarnings("deprecation") public static Drawable
     * drawableFromUrl(String url) throws IOException {
     * 
     * StrictMode.ThreadPolicy policy = new
     * StrictMode.ThreadPolicy.Builder().permitAll().build();
     * 
     * StrictMode.setThreadPolicy(policy);
     * 
     * Bitmap x;
     * 
     * HttpURLConnection connection = (HttpURLConnection) new
     * URL(url).openConnection(); connection.connect(); InputStream input =
     * connection.getInputStream();
     * 
     * x = BitmapFactory.decodeStream(input); return new BitmapDrawable(x); }
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {

	super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	switch (requestCode)
	{

	    case 3: // after write caption
		if (resultCode == RESULT_OK)
		{

		    if (isFromCamera == true)
		    {
			ATTACH_FILE_PATH = capturedImageUri.getPath();
		    }
		    else
		    {
			// ATTACH_FILE_PATH // keep the path
		    }
		    File f = new File(ATTACH_FILE_PATH);
		    final String filename = f.getName();
		    ATTACH_FILE_NAME = filename;

		    // imageLoader.displayImage(capturedImageUri.toString(),
		    // imgPreviewAttachment, options);
		    // imgPreviewAttachment.setVisibility(View.VISIBLE);
		    // cancelAttachButton.setVisibility(View.VISIBLE);

		    {
			if (isAttached == true)
			{

			    // progressBar.setVisibility(View.VISIBLE);
			    isAttached = false;
			    // imgPreviewAttachment.setVisibility(View.GONE);
			    // cancelAttachButton.setVisibility(View.GONE);
			    if (isAttachAnImage == true)
			    {

				File file = null;
				if (isFromCamera == true)
				{
				    // file =
				    // createFileFromBitmap(ATTACH_FILE_PATH,
				    // ATTACH_FILE_NAME);
				    file = new File(ATTACH_FILE_PATH);
				}
				else
				{
				    // file =
				    // createFileFromBitmap1(ATTACH_FILE_PATH,
				    // ATTACH_FILE_NAME);
				    file = new File(ATTACH_FILE_PATH);

				}
				uploadContent(file, true, ATTACH_FILE_PATH, ATTACH_FILE_NAME);

			    }
			}
		    }

		}
		break;

	    case 1: // capture
		if (resultCode == RESULT_OK)
		{
		    try
		    {
			isAttached = true;
			ATTACH_FILE_PATH = capturedImageUri.getPath();
			File f = new File(ATTACH_FILE_PATH);
			final String filename = f.getName();
			ATTACH_FILE_NAME = filename;
			isFromCamera = true;
			isAttachAnImage = true;

			Intent intent = new Intent(ChatActivity.this, Activity_WriteCaptionPicture.class);
			intent.putExtra("path", capturedImageUri.toString());
			intent.putExtra("getpath", capturedImageUri.getPath());
			startActivityForResult(intent, 3);
			// startActivity(intent);

			// imageLoader.displayImage(capturedImageUri.toString(),
			// imgPreviewAttachment, options);
			// imgPreviewAttachment.setVisibility(View.VISIBLE);
			// cancelAttachButton.setVisibility(View.VISIBLE);

		    }
		    catch (Exception e)
		    {
			e.printStackTrace();
		    }

		}

		break;

	    case 0: // attach
		if (resultCode == RESULT_OK)
		{

		    final String[] okFileExtensions = new String[]
		    {
		    "jpg", "png", "gif", "jpeg"
		    };

		    Boolean isImage = false;

		    Uri selectedImage = imageReturnedIntent.getData();

		    selectedImage = StaticFunction.convertUriMediaPath(selectedImage);

		    String[] filePathColumn =
		    {
			MediaStore.Images.Media.DATA
		    };

		    try
		    {
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

			// file path of selected image
			final String filePath = cursor.getString(columnIndex);
			File f = new File(filePath);
			final String filename = f.getName();

			cursor.close();

			String fileType = filePath.toString().substring((filePath.toString().lastIndexOf(".") + 1), filePath.toString().length());

			for (String extension : okFileExtensions)
			{
			    if (fileType.equals(extension))
			    {
				isImage = true;
			    }
			}

			isAttached = true;

			ATTACH_FILE_PATH = filePath;
			ATTACH_FILE_NAME = filename;
			isFromCamera = false;

			if (isImage)
			{

			    isAttachAnImage = true;

			    Intent intent = new Intent(ChatActivity.this, Activity_WriteCaptionPicture.class);
			    intent.putExtra("path", selectedImage.toString());
			    intent.putExtra("getpath", ATTACH_FILE_PATH);
			    startActivityForResult(intent, 3);

			    // imageLoader.displayImage(selectedImage.toString(),
			    // imgPreviewAttachment, options);

			}
			else
			{
			    isAttachAnImage = false;
			    attachFile = f;
			    imgPreviewAttachment.setImageResource(R.drawable.ic_menu_paste_holo_light);
			    imgPreviewAttachment.setVisibility(View.VISIBLE);

			    if (cancelAttachButton.getVisibility() == View.GONE)
			    {
				cancelAttachButton.setVisibility(View.VISIBLE);
			    }
			}

		    }
		    catch (Exception ex)
		    {
			isImage = false;

			if (isImage == false)
			{
			    File f = new File(selectedImage.getPath());
			    final String filename = f.getName();

			    if (cancelAttachButton.getVisibility() == View.GONE)
			    {
				cancelAttachButton.setVisibility(View.VISIBLE);
			    }

			    isAttached = true;

			    ATTACH_FILE_PATH = selectedImage.getPath();
			    ATTACH_FILE_NAME = filename;
			    isFromCamera = false;

			    isAttachAnImage = false;
			    attachFile = f;
			    imgPreviewAttachment.setImageResource(R.drawable.ic_menu_paste_holo_light);

			    imgPreviewAttachment.setVisibility(View.VISIBLE);

			}

		    }

		}
		break;
	}
    }

    @SuppressWarnings("deprecation")
    private File createFileFromBitmap1(String filepath, String filename)
    {

	Bitmap resizedBitmap = null;

	// BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	// bmOptions.inSampleSize = 4;
	Bitmap bmp = BitmapFactory.decodeFile(filepath);
	int width = bmp.getWidth();
	int height = bmp.getHeight();

	int Measuredwidth = 0;
	int Measuredheight = 0;
	Point size = new Point();
	WindowManager w = getWindowManager();

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

	if (width <= Measuredwidth || height <= Measuredheight)
	{
	    resizedBitmap = bmp;
	}
	else
	{
	    float scaleWidth = ((float) Measuredwidth) / width;
	    float scaleHeight = ((float) Measuredheight) / height;

	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);
	}

	File f1 = new File(ChatActivity.this.getCacheDir(), filename);

	try
	{

	    f1.createNewFile();

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    resizedBitmap.compress(CompressFormat.JPEG, 50 /* ignored for PNG */, bos);
	    byte[] bitmapdata = bos.toByteArray();

	    // write the bytes in file
	    FileOutputStream fos = new FileOutputStream(f1);
	    fos.write(bitmapdata);
	    fos.close();
	    fos.flush();

	}
	catch (Exception e)
	{
	    progressBar.setVisibility(View.GONE);

	}

	return f1;
    }

    private File createFileFromBitmap(String filepath, String filename)
    {

	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	bmOptions.inSampleSize = 1;
	Bitmap bmp = BitmapFactory.decodeFile(filepath, bmOptions);

	File f1 = new File(ChatActivity.this.getCacheDir(), filename);

	try
	{

	    f1.createNewFile();

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    bmp.compress(CompressFormat.JPEG, 100 /* ignored for PNG */, bos);
	    byte[] bitmapdata = bos.toByteArray();

	    // write the bytes in file
	    FileOutputStream fos = new FileOutputStream(f1);
	    fos.write(bitmapdata);
	    fos.close();
	    fos.flush();

	}
	catch (Exception e)
	{

	}

	return f1;
    }

    String mCurrentPhotoPath;

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException
    {

	// Create an image file name
	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	String imageFileName = "JPEG_" + timeStamp + "_";
	File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

	File image = File.createTempFile(imageFileName, ".jpg", storageDir);

	// Save a file: path for use with ACTION_VIEW intents
	mCurrentPhotoPath = image.getAbsolutePath();
	return image;
    }

    /*private void dispatchTakePictureIntent()
    {

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null)
    {
        // Create the File where the photo should go
        File photoFile = null;
        try
        {
    	photoFile = createImageFile();
        }
        catch (IOException ex)
        {
    	// Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null)
        {
    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
    	startActivityForResult(takePictureIntent, 1);
        }
    }
    }*/

    private void uploadContent(File f, boolean fileIsPublic, final String filePath, final String fileName)
    {

	QBContent.uploadFileTask(f, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>()
	{

	    @Override
	    public void onSuccess(QBFile file, Bundle params)
	    {

		messageEditText.setText("");

		progressBar.setVisibility(View.GONE);

		// Send chat message
		//
		QBChatMessage chatMessage = new QBChatMessage();
		chatMessage.setBody(Activity_WriteCaptionPicture.CAPTION);
		chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
		//
		Integer opponentID = -1;
		if (mode == Mode.PRIVATE)
		{

		    opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, ChatActivity.this);

		}
		chatMessage.setProperty("dialog_id", dialog.getDialogId());
		chatMessage.setProperty("sender_id", SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this) + "");
		if (mode == Mode.PRIVATE)
		{
		    chatMessage.setProperty("recipient_id", opponentID + "");
		}
		else
		{
		    chatMessage.setProperty("recipient_id", -1 + "");
		}
		//
		chatMessage.setMarkable(true);

		if (isAttachAnImage)
		{
		    // attach a photo
		    QBAttachment attachment = new QBAttachment("photo");
		    attachment.setId(file.getId().toString());
		    attachment.setUrl(file.getPublicUrl());

		    chatMessage.addAttachment(attachment);
		}
		else
		{
		    // attach a photo
		    QBAttachment attachment = new QBAttachment("other");
		    attachment.setId(file.getId().toString());
		    attachment.setUrl(file.getPublicUrl());

		    chatMessage.addAttachment(attachment);
		}

		try
		{
		    chat.sendMessage(chatMessage);

		    if (mode == Mode.PRIVATE)
		    {

			showMessage(chatMessage);
			if (chatCallback != null)
			{
			    chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
			}

			StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
			userIds.add(opponentID);

			QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(ChatActivity.this);
			String senderUsername = currentLoginUser.getLogin();
			int currentUserId = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);

			sendPushNotificationToOpponentUser(progressBar, Activity_WriteCaptionPicture.CAPTION, userIds, senderUsername, dialog.getDialogId(), chatMessage.getId(), currentUserId, opponentID + "", "PRIVATE", file.getPublicUrl());
		    }
		    else
		    {

			StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();

			usersIDs.addAll(dialog.getOccupants());

			int currentUserId = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);

			sendPushNotificationToOpponentUser(progressBar, Activity_WriteCaptionPicture.CAPTION, usersIDs, dialog.getName(), dialog.getDialogId(), chatMessage.getId(), currentUserId, usersIDs.toString(), "GROUP", file.getPublicUrl());

		    }
		}
		catch (Exception e)
		{

		    String dia = chatMessage.getProperty("dialog_id");
		    String sender = chatMessage.getProperty("sender_id");
		    String messBody = chatMessage.getBody();
		    String messID = chatMessage.getId();

		    Toast.makeText(ChatActivity.this, "sending fail! try again...", Toast.LENGTH_LONG).show();
		    StaticFunction.saveMessageToDB(chatMessage, messID, Integer.parseInt(sender), -999, dia, messBody, true);

		    if (mode == Mode.PRIVATE)
		    {

			if (adapter != null)
			{
			    showMessage(chatMessage);
			    if (chatCallback != null)
			    {
				chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
			    }
			}
			else
			{
			    messInQueue.add(chatMessage);
			}
		    }
		    else
		    {
			if (chatCallback != null)
			{
			    chatCallback.onNewPrivateMessage(dialog.getDialogId(), chatMessage.getBody());
			}
		    }

		}

		//

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		// error
	    }
	});
	//
    }

    private boolean isDownloaded(String dialogID)
    {

	boolean isDownloaded = false;
	Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialogID);
	if (sDialog != null)
	{
	    isDownloaded = sDialog.isDownloadedMessOfDialog;
	}
	return isDownloaded;
    }

    private void loadChatHistory(final Mode mode1, final String dialogID)
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		if (!isFinishing())
		{

		    Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");

		    if (isDownloaded(dialogID) == false)
		    {
			loadChatHistoryFromServer(mode1);
		    }
		    else
		    {
			loadChatHistoryFromDB(mode1);
		    }

		}

	    }
	});

    }

    private void loadChatHistoryFromDB(Mode mode1)
    {

	Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");
	List<Sugar_Message> listMessages = new ArrayList<Sugar_Message>();
	String dialogID = dialog.getDialogId();

	try
	{
	    listMessages = Select.from(Sugar_Message.class).where(Condition.prop("MESS_CHAT_DIALOG_ID").eq(dialogID)).orderBy("MESS_DATE_SENT").list();// Sugar_Message.find(Sugar_Message.class,
																		       // "MESS_CHAT_DIALOG_ID = ?",
																		       // //
																		       // dialogID);
	}
	catch (Exception ex)
	{
	    Log.e("", "");
	}

	ArrayList<QBMessage> listQBmess = convertSugarMessToQBMess(listMessages);

	adapter = new ChatAdapter(ChatActivity.this, listQBmess, mode1);
	messagesContainer.setAdapter(adapter);

	progressBar.setVisibility(View.GONE);

	invalidateOptionMenuAndChatBarWhenServiceInitFinish();

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		adapter.notifyDataSetChanged();
		scrollDown();
	    }
	});

    }

    private void loadChatHistoryFromDB1(Mode mode1, String messID)
    {

	if (adapter != null)
	{

	    List<Sugar_Message> listMessages = new ArrayList<Sugar_Message>();
	    try
	    {
		listMessages = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", messID);
	    }
	    catch (Exception ex)
	    {
		Log.e("", "");
	    }

	    ArrayList<QBMessage> listQBmess = convertSugarMessToQBMess(listMessages);

	    adapter.add(listQBmess);

	    runOnUiThread(new Runnable()
	    {

		@Override
		public void run()
		{

		    adapter.notifyDataSetChanged();
		    scrollDown();
		}
	    });

	}
	else
	{
	    loadChatHistoryFromDB(mode1);
	}

    }

    private void invalidateOptionMenuAndChatBarWhenServiceInitIsLoading()
    {

	isFinishInitService = false;
	invalidateOptionsMenu();
	chatBarContainer.setVisibility(View.GONE);
    }

    private void invalidateOptionMenuAndChatBarWhenServiceInitFinish()
    {

	isFinishInitService = true;
	invalidateOptionsMenu();
	chatBarContainer.setVisibility(View.VISIBLE);
    }

    /*private void setDateForChat(ArrayList<QBMessage> listQBmess)
    {
    Date dateToSet = new Date();
    
    for(int i = 0 ; i < listQBmess.size(); i++)
    {
        QBMessage qbMess = listQBmess.get(i);
        String strTime = qbMess.getProperty("sent_time");
        long longTime = Long.parseLong(strTime);

        Date date = new Date(longTime);
        
        if (date != null)
        {
    	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    	if (fmt.format(date).equals(fmt.format(dateToSet)))
    	{
    	    
    	    
    	}
    	else
    	{
    	    listQBmess.get(i).

    	   
    	}

    	
        }
        
    }
    }*/

    private ArrayList<QBMessage> convertSugarMessToQBMess(List<Sugar_Message> listMessages)
    {

	ArrayList<QBMessage> listQBMess = new ArrayList<QBMessage>();

	for (int i = 0; i < listMessages.size(); i++)
	{

	    final Sugar_Message sMess = listMessages.get(i);
	    QBMessage m = new QBMessage()
	    {

		@Override
		public Integer getSenderId()
		{

		    return sMess.messSenderId;
		}

		@Override
		public Integer getRecipientId()
		{

		    return sMess.messRecipientId;
		}

		@Override
		public Collection<String> getPropertyNames()
		{

		    return null;
		}

		@Override
		public String getProperty(String arg0)
		{

		    if (arg0.equals("sent_time"))
		    {
			return sMess.messDateSent + "";
		    }
		    else if (arg0.equals("is_set_time_bar"))
		    {
			return sMess.isShowDate;
		    }
		    else if (arg0.equals("mess_status"))
		    {
			if (sMess.messRead == 1)
			{
			    return "sent";
			}
			else if (sMess.messRead == 2)
			{
			    return "seen";
			}
			else if (sMess.messRead == 3)
			{
			    return "fail";
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

		@Override
		public String getId()
		{

		    return sMess.messId;
		}

		@Override
		public String getDialogId()
		{

		    return sMess.messChatDialogId;
		}

		@Override
		public String getBody()
		{

		    return sMess.messMessage;
		}

		@Override
		public Collection<QBAttachment> getAttachments()
		{

		    if (sMess.messAttachment == null || sMess.messAttachment.equals(""))
		    {
			return null;
		    }
		    else
		    {

			Collection<QBAttachment> attachList = new ArrayList<QBAttachment>();
			QBAttachment attach = new QBAttachment("photo");
			attach.setUrl(sMess.messAttachment);

			attachList.add(attach);
			return attachList;
		    }
		}
	    };

	    listQBMess.add(m);
	}

	return listQBMess;

    }

    private void loadChatHistoryFromServer(final Mode mode)
    {

	QBChatService.getDialogMessagesCount(dialog.getDialogId(), new QBEntityCallback<Integer>()
	{

	    @Override
	    public void onSuccess(final Integer messCount, Bundle arg1)
	    {

		runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
			customObjectRequestBuilder.setPagesLimit(messCount);

			QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatHistoryMessage>>()
			{

			    @Override
			    public void onSuccess(final ArrayList<QBChatHistoryMessage> messages, Bundle args)
			    {

				runOnUiThread(new Runnable()
				{

				    @Override
				    public void run()
				    {

					for (QBMessage msg : messages)
					{

					    String messID = msg.getId();
					    int sendID = msg.getSenderId();
					    int receptID = -1;
					    if (msg.getRecipientId() != null)
					    {
						receptID = msg.getRecipientId();
					    }

					    String dialogID = msg.getDialogId();
					    String body = msg.getBody();

					    StaticFunction.saveMessageToDB(msg, messID, sendID, receptID, dialogID, body, false);
					}

					progressBar.setVisibility(View.GONE);

					// add current message
					for (QBMessage msg1 : messInQueue)
					{
					    if (!messages.contains(msg1))
					    {

						String messID = msg1.getId();
						int senderID = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);// msg1.getSenderId();
						int repID = msg1.getRecipientId();
						String dialogId = dialog.getDialogId();// msg1.getDialogId();
						String messBody = msg1.getBody();

						StaticFunction.saveMessageToDB(msg1, messID, senderID, repID, dialogId, messBody, false);
					    }
					}

					Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog.getDialogId());
					if (sDialog != null)
					{
					    sDialog.isDownloadedMessOfDialog = true;
					    sDialog.save();
					}
					// after save to db load data from sb
					loadChatHistoryFromDB(mode);

				    }
				});

			    }

			    @Override
			    public void onError(final List<String> errors)
			    {

				runOnUiThread(new Runnable()
				{

				    @Override
				    public void run()
				    {

					showPopupNoInternet(ChatActivity.this, ChatActivity.this, errors.toString());

				    }
				});

			    }
			});

		    }
		});

	    }

	    @Override
	    public void onError(final List<String> arg0)
	    {

		runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			showPopupNoInternet(ChatActivity.this, ChatActivity.this, arg0.toString());
		    }
		});

	    }

	    @Override
	    public void onSuccess()
	    {

	    }
	});

    }

    private Sugar_Message findMessageByID(String messID)
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

    private QBMessage findLastMessageOfDialogByID(String messID)
    {

	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_ID = ?", messID);

	if (listsss.size() > 0)
	{
	    ArrayList<QBMessage> listQBmess = convertSugarMessToQBMess(listsss);
	    QBMessage qbMess = listQBmess.get(0);
	    return qbMess;
	}
	else
	{
	    return null;

	}
    }

    public void showMessageOnReceiveOnly(final QBChatMessage mess)
    {

	final String messID = mess.getId();

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		// adapter.add(mess);
		// adapter.notifyDataSetChanged();
		// scrollDown();
		loadChatHistoryFromDB1(mode, messID);
	    }
	});
    }

    public void showMessage(final QBMessage message)
    {

	// QBMessage qbMess = findLastMessageOfDialogByID(message.getId());

	Sugar_Message sMess = findMessageByID(message.getId());

	if (sMess != null)
	{

	    if (sMess.messRecipientId == -999)
	    {
		((QBChatMessage) message).setProperty("mess_status", "fail");
	    }

	    if (sMess.isShowDate.equalsIgnoreCase("true"))
	    {
		((QBChatMessage) message).setProperty("is_set_time_bar", "true");
	    }
	    else
	    {
		((QBChatMessage) message).setProperty("is_set_time_bar", "false");
	    }

	    runOnUiThread(new Runnable()
	    {

		@Override
		public void run()
		{

		    adapter.add(message);
		    adapter.notifyDataSetChanged();
		    scrollDown();
		}
	    });
	}
    }

    public void scrollDown()
    {

	messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    public static enum Mode
    {
	PRIVATE, GROUP
    }

    int optionMenuState = 0;

    boolean isFinishInitService = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	if (isFinishInitService == true)
	{

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_chat, menu);

	    if (optionMenuState != 0)
	    {
		menu.findItem(R.id.action_block).setVisible(false);
		menu.findItem(R.id.action_attach).setVisible(false);
	    }

	    if (mode == Mode.GROUP)
	    {
		menu.findItem(R.id.action_block).setVisible(false);
	    }
	}

	//
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	int id = item.getItemId();

	if (id == android.R.id.home)
	{
	    try
	    {
		if (chat != null)
		{
		    chat.release();
		}

	    }
	    catch (XMPPException e)
	    {
		Log.e(TAG, "failed to release chat", e);
	    }

	    if (isFromPushNotification == true)
	    {
		Intent intent = new Intent(ChatActivity.this, DialogsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);

		finish();
	    }
	    else
	    {
		finish();
	    }

	    return true;
	}

	if (id == R.id.action_block)
	{

	    showPopupMenuActionOthers(ChatActivity.this, R.layout.dialog_menu_action_others_chat);

	    return true;
	}

	if (id == R.id.action_attach)
	{

	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("image/*");
	    startActivityForResult(intent, 0);
	    return true;
	}

	return super.onOptionsItemSelected(item);
    }

    public void sendPushNotificationToOpponentUser(View view, String message, StringifyArrayList<Integer> userids, String senderUsername, String dialogID, String messID, int senderID, String recipientID, String type, String attachURL)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	Log.e("GENNNNNNNNNNNNNNN", message + "---" + senderUsername + "---" + dialogID);

	//
	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "new_message");
	    json.put("content_mess", message);
	    json.put("user_id", senderUsername);
	    json.put("dialog_id", dialogID);
	    json.put("mess_id", messID);
	    json.put("sender_id", senderID);
	    json.put("recipient_id", recipientID);
	    json.put("dialog_type", type);
	    json.put("attach_URL", attachURL);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	String abc = json.toString();

	Log.e("GENNNNNNNNNNNNNNN", abc);

	qbEvent.setMessage(abc);
	//

	// qbEvent.setMessage(message);

	StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
	int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);
	for (int i = 0; i < userids.size(); i++)
	{

	    if (userids.get(i) != currentLoginUserID)
	    {
		userIds.add(userids.get(i));
	    }
	}
	// userIds.addAll(userids); // .add(2111993)
	qbEvent.setUserIds(userIds);

	QBMessages.createEvent(qbEvent, new QBEntityCallbackImpl<QBEvent>()
	{

	    @Override
	    public void onSuccess(QBEvent qbEvent, Bundle bundle)
	    {

		progressBar.setVisibility(View.GONE);

		// hide keyboard
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(messageOutEditText.getWindowToken(),
		// 0);
	    }

	    @Override
	    public void onError(List<String> strings)
	    {

		// errors
		// DialogUtils.showLong(MessagesActivity.this,
		// strings.toString());

		progressBar.setVisibility(View.GONE);

		// hide keyboard
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(messageOutEditText.getWindowToken(),
		// 0);
	    }
	});

	// progressBar.setVisibility(View.VISIBLE);
    }

    public void sendPushNotificationBlockUser(View view, StringifyArrayList<Integer> userids, int senderID)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "blockuser");
	    json.put("block_user_id", senderID);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	String abc = json.toString();
	qbEvent.setMessage(abc);

	StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
	userIds.addAll(userids);
	qbEvent.setUserIds(userIds);

	QBMessages.createEvent(qbEvent, new QBEntityCallbackImpl<QBEvent>()
	{

	    @Override
	    public void onSuccess(QBEvent qbEvent, Bundle bundle)
	    {

		progressBar.setVisibility(View.GONE);
	    }

	    @Override
	    public void onError(List<String> strings)
	    {

		progressBar.setVisibility(View.GONE);

	    }
	});

    }

    public void showPopupMenuActionOthers(Context context, int ResourceID)
    {

	// custom dialog
	final Dialog dialogView = new Dialog(context);

	dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialogView.setCanceledOnTouchOutside(true);
	dialogView.setContentView(ResourceID);

	// modified by Loi
	LinearLayout container = (LinearLayout) dialogView.findViewById(R.id.container);

	container.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		dialogView.dismiss();
	    }
	});

	LinearLayout container1 = (LinearLayout) dialogView.findViewById(R.id.container1);
	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container1.getLayoutParams();
	params.topMargin = getSupportActionBar().getHeight();
	container1.setLayoutParams(params);
	// modified by Loi

	int currentLoginID = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);
	Sugar_User sUser = StaticFunction.getUserFromDBByID(currentLoginID);
	String customData = sUser.userCustomData;

	Integer opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, ChatActivity.this);
	if (customData != null)
	{
	    TextView txtBlockUser = (TextView) dialogView.findViewById(R.id.txtBlockUser);
	    if (customData.contains(opponentID + ""))
	    {
		txtBlockUser.setText("un-block user");
	    }
	    else
	    {
		txtBlockUser.setText("block user");
	    }
	}

	// set the custom dialog components - text, image and button
	LinearLayout btnWallpaper = (LinearLayout) dialogView.findViewById(R.id.btnWallpaper);
	// if button is clicked, close the custom dialog
	btnWallpaper.setOnClickListener(new OnClickListener()
	{

	    @SuppressWarnings("static-access")
	    public void onClick(View v)
	    {

		QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(ChatActivity.this);
		final int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(ChatActivity.this);
		currentLoginUser.setId(currentLoginUserID);

		Sugar_User sUser = StaticFunction.getUserFromDBByID(currentLoginUserID);
		QBUser currentUser = StaticFunction.SugarUserToQBUser(sUser);

		// QBUser currentUser = qbUSer;

		final Integer opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, ChatActivity.this);
		QBUser opponentUser = StaticFunction.getUserById(opponentID);
		// String opponentLogin = opponentUser.getLogin();

		String previousCustomData = currentUser.getCustomData();

		if (previousCustomData != null)
		{

		    if (previousCustomData.contains(opponentID + ""))
		    {
			previousCustomData = previousCustomData.replace(opponentID + "", "");
			currentUser.setCustomData(previousCustomData);
		    }
		    else
		    {
			currentUser.setCustomData(previousCustomData + "," + opponentID);
		    }
		}
		else
		{
		    currentUser.setCustomData(opponentID + ",");
		}

		QBUsers.updateUser(currentUser, new QBEntityCallbackImpl<QBUser>()
		{

		    @Override
		    public void onSuccess(QBUser users, Bundle params)
		    {

			Sugar_User sUpdateUser = StaticFunction.getUserFromDBByID(currentLoginUserID);
			sUpdateUser.userCustomData = users.getCustomData();
			sUpdateUser.save();

			StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
			userIds.add(opponentID);

			sendPushNotificationBlockUser(progressBar, userIds, currentLoginUserID);

			Toast.makeText(ChatActivity.this, "this user is blocked!", Toast.LENGTH_SHORT).show();

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			Toast.makeText(ChatActivity.this, "ERROR: Please try again!", Toast.LENGTH_LONG).show();
		    }

		});
		dialogView.dismiss();
	    }
	});

	LinearLayout btnMainMenu = (LinearLayout) dialogView.findViewById(R.id.btnShareFB);
	btnMainMenu.setOnClickListener(new OnClickListener()
	{

	    public void onClick(View v)
	    {

		dialogView.dismiss();

	    }
	});

	dialogView.show();

    }

    public void hideKeyboard(View view)
    {

	InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onLoginSuccess()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");

		Intent intent = getIntent();

		dialog = (QBDialog) intent.getSerializableExtra(EXTRA_DIALOG);

		if (dialog == null)
		{

		    isFromPushNotification = true;

		    dialog_id = intent.getStringExtra("DIALOG_ID");

		    //
		    // SharedPreferences preferences =
		    // getSharedPreferences(dialog_id.hashCode() + "",
		    // MODE_PRIVATE);
		    // Editor editor = preferences.edit();
		    // editor.clear();
		    // editor.commit();
		    //
		    Sugar_Noti.deleteAll(Sugar_Noti.class, "hashcode_Id = ?", dialog_id.hashCode() + "");
		    List<Sugar_Noti> list = Sugar_Noti.listAll(Sugar_Noti.class);
		    if (list.size() == 0)
		    {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
		    }
		    //
		    //

		    if (dialog_id != null && !dialog_id.equals(""))
		    {
			getDialogAndLoadChatMessage(dialog_id);

		    }
		}
		else
		{
		    Log.e("CHAT ACTIVITY", "dialog == HAVE.......: " + "YESSSSSSSSSSSSS");
		    mode = (Mode) intent.getSerializableExtra(EXTRA_MODE);

		    String dialog_ID = dialog.getDialogId();

		    dialog_id = dialog_ID;

		    try
		    {
			// clear the notification icon on the status bar if same
			// id
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(dialog_ID.hashCode());
		    }
		    catch (Exception ex)
		    {

		    }

		    //
		    // SharedPreferences preferences =
		    // getSharedPreferences(dialog_ID.hashCode() + "",
		    // MODE_PRIVATE);
		    // Editor editor = preferences.edit();
		    // editor.clear();
		    // editor.commit();
		    Sugar_Noti.deleteAll(Sugar_Noti.class, "hashcode_Id = ?", dialog_id.hashCode() + "");
		    List<Sugar_Noti> list = Sugar_Noti.listAll(Sugar_Noti.class);
		    if (list.size() == 0)
		    {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);
		    }
		    //

		    initViews();
		}

	    }
	});

    }

    @Override
    public void onCreateSessionError(final String error)
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(ChatActivity.this, ChatActivity.this, error);

	    }
	});

    }

    @Override
    public void onLoginError(final String error)
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(ChatActivity.this, ChatActivity.this, error);

	    }
	});

    }

    private void getUserByIDFromServer(int userID)
    {

	List<Integer> listUsersIDs = new ArrayList<Integer>();
	listUsersIDs.add(userID);

	QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
	requestBuilder.setPage(1);
	requestBuilder.setPerPage(listUsersIDs.size());

	QBUsers.getUsersByIDs(listUsersIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>()
	{

	    @Override
	    public void onSuccess(final ArrayList<QBUser> users, Bundle params)
	    {

		if (users != null && !users.isEmpty())
		{
		    runOnUiThread(new Runnable()
		    {

			public void run()
			{

			    boolean isUserOnline = StaticFunction.isUserOnline(users.get(0));
			    if (isUserOnline == true)
			    {
				txtSubTitle.setText("Online");
			    }
			    else
			    {
				txtSubTitle.setText("Offline");

			    }
			}
		    });
		}

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		Log.e("", "");
		// progressBar.setVisibility(View.GONE);
		// Activity_FlashScreen.showPopupNoInternet(ChatActivity.this,
		// ChatActivity.this, errors.toString());
	    }

	});

    }

    @Override
    public void onConnectionClosedOnError()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(ChatActivity.this, ChatActivity.this, "         Connection Lost!         ");

	    }
	});

    }

    @Override
    public void onConnectedSuccess()
    {/*

     runOnUiThread(new Runnable()
     {

         @Override
         public void run()
         {

     	layoutConnection.setVisibility(View.VISIBLE);
     	txtConnactionStatus.setVisibility(View.VISIBLE);
     	txtConnactionStatus.setText("Connected!");

     	chatBarContainer.setVisibility(View.VISIBLE);
     	optionMenuState = 0;
     	invalidateOptionsMenu();

     	Handler hand = new Handler();
     	hand.postDelayed(new Runnable()
     	{

     	    @Override
     	    public void run()
     	    {

     		layoutConnection.setVisibility(View.GONE);
     		txtConnactionStatus.setVisibility(View.GONE);

     	    }
     	}, 2000);
         }
     });

     */

    }

    @Override
    public void onReconnecting()
    {/*

     runOnUiThread(new Runnable()
     {

         @Override
         public void run()
         {

     	layoutConnection.setVisibility(View.VISIBLE);
     	txtConnactionStatus.setVisibility(View.VISIBLE);
     	txtConnactionStatus.setText("Connection lost! re-connecting...");

         }
     });

     */

    }

    @Override
    public void onConnectFail()
    {

    }

    public void showPopupNoInternet(final Activity acti, Context context, String dialogMessage)
    {

	if (!isFinishing())
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

		    try
		    {

			invalidateOptionMenuAndChatBarWhenServiceInitIsLoading();

			if (chat != null)
			{
			    chat.release();
			}

			// resgister service
			progressBar.setVisibility(View.VISIBLE);
			HelloMainService.Callback = ChatActivity.this;
			Intent intenttt = new Intent(ChatActivity.this, HelloMainService.class);
			intenttt.addCategory(HelloMainService.TAG);
			startService(intenttt);

			// Intent intent = new Intent(acti,
			// Activity_FlashScreen.class);
			// acti.startActivity(intent);

			dialog.dismiss();
			// acti.finish();
		    }
		    catch (Exception ex)
		    {
			Log.e("", "");
		    }

		}
	    });

	    dialog.show();
	}

    }

    public void showPopupNoInternet2(final Activity acti, Context context, String dialogMessage)
    {

	if (!isFinishing())
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

		    try
		    {
			HelloMainService.chatService = null;
			BaseService.getBaseService().setToken(null);

			Intent intent = new Intent(acti, Activity_FlashScreen.class);
			acti.startActivity(intent);

			dialog.dismiss();
			acti.finish();
		    }
		    catch (Exception ex)
		    {
			Log.e("", "");
		    }

		}
	    });

	    dialog.show();
	}

    }

    /*public void showPopupNoInternet3(final Activity acti, Context context, String dialogMessage)
    {

    if (!isFinishing())
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
        btnRetry.setVisibility(View.GONE);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Logout");
        btnCancel.setOnClickListener(new OnClickListener()
        {

    	@Override
    	public void onClick(View v)
    	{

    	    Activity_Setting.logoutHelloChat(acti, null);
    	    dialog.dismiss();
    	    acti.finish();

    	}
        });

        dialog.show();
    }

    }*/

    @Override
    public void onCreateSessionErrorNeedToResetAll()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet2(ChatActivity.this, ChatActivity.this, "Connect to server fail. Please try again!");

	    }
	});

    }

    @Override
    public void onConnectionClosedDuetoUserLoginToOtherDevice()
    {/*

     runOnUiThread(new Runnable()
     {

         @Override
         public void run()
         {

     	showPopupNoInternet3(ChatActivity.this, ChatActivity.this, "You have login to another device. Logout now!");

         }
     });

     */

    }

}
