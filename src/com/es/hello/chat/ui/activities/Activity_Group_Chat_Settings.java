package com.es.hello.chat.ui.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.adapters.GroupChatUsersAdapter;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Activity_Group_Chat_Settings extends ActionBarActivity
{

    ImageView btnChooseParticipant;

    ImageView imgGroupPhoto;

    EditText edtGroupName;

    EditText edtGroupMemberCount;

    ListView listViewParticipants;

    List<QBUser> listUsers = new ArrayList<QBUser>();

    List<QBUser> listNewUsers = new ArrayList<QBUser>();

    List<QBDialog> listDialogs = new ArrayList<QBDialog>();

    ArrayList<Integer> listUserIds;

    ArrayList<Integer> listNewUserIds;

    public String ACTION = "";

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    public static String FILE_PATH = "";

    public static String FILE_NAME = "";

    QBDialog updateDialog;

    public String currentGroupName = "";

    private RelativeLayout progressBar;

    public LinearLayout groupInfoCointainer;

    public Activity activity;

    public EditText edtCreatedBy;

    GroupChatUsersAdapter adapter;

    Button btnLeaveGroup;

    LinearLayout btnBack;

    public static boolean isCloseChatActivity = false;

    @Override
    public void onBackPressed()
    {

	super.onBackPressed();
	ApplicationSingleton.currentSelectedUsersInGroup = new ArrayList<QBUser>();
	// Intent intent = new Intent(Activity_Group_Chat_Settings.this,
	// DialogsActivity.class);
	// startActivity(intent);
	finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_group_chat_settings);

	isCloseChatActivity = false;

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "GROUP SETTINGS", true);

	btnBack = (LinearLayout) findViewById(R.id.btnBack);
	btnBack.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		onBackPressed();

	    }
	});

	// FontTypeUtils.setFontForTittleBar(this, this);
	groupInfoCointainer = (LinearLayout) findViewById(R.id.groupInfoContainer);

	activity = this;

	StaticFunction.initImageLoader(Activity_Group_Chat_Settings.this);

	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_contact_picture).showImageOnFail(R.drawable.ic_contact_picture).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	initView();

	Intent intent = getIntent();
	Bundle extras = getIntent().getExtras();
	if (extras == null)
	{
	    ACTION = "";
	}
	else
	{
	    ACTION = extras.getString("ACTION", "");
	}

	if (ACTION.equals("CREATE_GROUP_CHAT"))
	{

	}
	else if (ACTION.equals("UPDATE_GROUP_CHAT"))
	{

	    updateDialog = (QBDialog) intent.getSerializableExtra("DIALOGFROMCHAT");
	    if (updateDialog != null)
	    {

		Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(updateDialog.getDialogId());

		updateDialog.setName(sDialog.dialogName);
		updateDialog.setPhoto(sDialog.dialogPhoto);

		ArrayList<Integer> listIDs = new ArrayList<Integer>();
		listIDs = StaticFunction.splitStringToArrayInteger(sDialog.dialogOccupantsIds);

		if (!listIDs.isEmpty())
		{
		    updateDialog.setOccupantsIds(listIDs);
		}

		String createdByID = updateDialog.getUserId() + "";

		Sugar_User sUser = StaticFunction.findUserInDBByID(createdByID);
		if (sUser != null)
		{
		    edtCreatedBy.setText("Created by: " + sUser.userLogin);

		}

		edtGroupName.setText(updateDialog.getName());

		currentGroupName = edtGroupName.getText().toString();

		imageLoader.displayImage(updateDialog.getPhoto(), imgGroupPhoto, options);

		listDialogs.add(updateDialog);
		listUserIds = StaticFunction.getUserIds(listDialogs);

		listUsers = StaticFunction.findListUserByUserId(listUserIds);

		String admin = findAdminofChatRoom(updateDialog);

		adapter = new GroupChatUsersAdapter(listUsers, Activity_Group_Chat_Settings.this, admin);
		listViewParticipants.setAdapter(adapter);
		edtGroupMemberCount.setText(listUsers.size() + " peoples");

		taskLoadUserInfoBackground(listUserIds);
	    }

	}
	else if (ACTION.equals("BACK_FROM_SELECT_DIALOG_CREATE"))
	{
	    // no use anymore
	    /*
	    if (!ApplicationSingleton.CURRENT_GROUP_NAME.equals(""))
	    {
	    edtGroupName.setText(ApplicationSingleton.CURRENT_GROUP_NAME);
	    }

	    if (!ApplicationSingleton.CURRENT_GROUP_PHOTO.equals(""))
	    {
	    imageLoader.displayImage(ApplicationSingleton.CURRENT_GROUP_PHOTO, imgGroupPhoto, options);
	    }

	    listDialogs = ApplicationSingleton.selectedDialogToCreateGroupChat;
	    listUserIds = getUserIds(listDialogs);

	    listUsers = findListUserByUserId(listUserIds);
	    String admin = findAdminofChatRoom(updateDialog);

	    GroupChatUsersAdapter adapter = new GroupChatUsersAdapter(listUsers, Activity_Group_Chat_Settings.this, admin);
	    listViewParticipants.setAdapter(adapter);
	    edtGroupMemberCount.setText(listUsers.size() + " peoples");

	    taskLoadUserInfoBackground(listUserIds);

	    */
	}

    }

    @Override
    protected void onNewIntent(Intent intent)
    {

	super.onNewIntent(intent);
	Bundle extras = intent.getExtras();

	if (extras == null)
	{
	    ACTION = "";
	}
	else
	{
	    ACTION = extras.getString("ACTION", "");
	}
    }

    @Override
    protected void onResume()
    {

	super.onResume();

	listDialogs = ApplicationSingleton.selectedDialogToCreateGroupChat;
	listNewUserIds = StaticFunction.getUserIds(listDialogs);

	int currentUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Group_Chat_Settings.this);
	for (int i = 0; i < listNewUserIds.size(); i++)
	{
	    if (listNewUserIds.get(i).intValue() == currentUserID)
	    {
		listNewUserIds.remove(i);
		break;
	    }
	}

	listNewUsers = StaticFunction.findListUserByUserId(listNewUserIds);

	listUsers.addAll(listNewUsers);

	listUserIds.addAll(listNewUserIds);

	String admin = findAdminofChatRoom(updateDialog);

	if (adapter != null)
	{
	    adapter.setListItems(listUsers);
	    adapter.setAdmin(admin);
	    adapter.notifyDataSetChanged();
	}
	else
	{
	    adapter = new GroupChatUsersAdapter(listUsers, Activity_Group_Chat_Settings.this, admin);
	    listViewParticipants.setAdapter(adapter);
	}

	edtGroupMemberCount.setText(listUsers.size() + " peoples");

	ApplicationSingleton.selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();

	/*if (ACTION.equals("BACK_FROM_SELECT_DIALOG_CREATE"))
	{

	    listDialogs = ApplicationSingleton.selectedDialogToCreateGroupChat;
	    listNewUserIds = getUserIds(listDialogs);

	    List<QBUser> users = findListUserByUserId(listNewUserIds);

	    for (int j = 0; j < users.size(); j++)
	    {
		QBUser user = users.get(j);
		if (!listUsers.contains(user))
		{
		    listUsers.add(user);
		    if (!listNewUsers.contains(user))
		    {
			listNewUsers.add(user);
		    }
		}
	    }

	    // if (adapter != null)
	    // {
	    String admin = findAdminofChatRoom(updateDialog);

	    adapter = new GroupChatUsersAdapter(listUsers, Activity_Group_Chat_Settings.this, admin);
	    listViewParticipants.setAdapter(adapter);
	    // adapter.addListItems(listNewUsers);
	    // adapter.notifyDataSetChanged();
	    // }

	    edtGroupMemberCount.setText(listUsers.size() + " peoples");

	    // taskLoadUserInfoBackground(listUserIds);

	}*/

    }

    private void taskLoadUserInfoBackground(ArrayList<Integer> listUserId)
    {

	QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
	requestBuilder.setPage(1);
	requestBuilder.setPerPage(listUserId.size());
	QBUsers.getUsersByIDs(listUserId, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>()
	{

	    @Override
	    public void onSuccess(final ArrayList<QBUser> users, Bundle params)
	    {

		// ApplicationSingleton.addDialogsUsers(users);
		Activity_Group_Chat_Settings.this.runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			String admin = findAdminofChatRoom(updateDialog);

			ArrayList<QBUser> listUserOnline = new ArrayList<QBUser>();
			ArrayList<QBUser> listUserOffline = new ArrayList<QBUser>();

			for (QBUser user : users)
			{
			    if (StaticFunction.isUserOnline(user))
			    {
				listUserOnline.add(user);
			    }
			    else
			    {
				listUserOffline.add(user);

			    }
			}
			
			ArrayList<QBUser> listFullUsersSorted = new ArrayList<QBUser>();
			listFullUsersSorted.addAll(listUserOnline);
			listFullUsersSorted.addAll(listUserOffline);

			if (adapter != null)
			{
			    adapter.setListItems(listFullUsersSorted);
			    adapter.setAdmin(admin);
			    adapter.notifyDataSetChanged();
			}
			else
			{
			    GroupChatUsersAdapter adapter = new GroupChatUsersAdapter(listFullUsersSorted, Activity_Group_Chat_Settings.this, admin);
			    listViewParticipants.setAdapter(adapter);
			    edtGroupMemberCount.setText(users.size() + " peoples");
			}

		    }
		});

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

	    }

	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	if (ACTION.equals("UPDATE_GROUP_CHAT"))
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_dialog_update_group, menu);

	    String createdByID = findAdminofChatRoom(updateDialog);
	    int iCurrentUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Group_Chat_Settings.this);
	    String strCurrentUserID = iCurrentUserID + "";

	    MenuItem item = menu.findItem(R.id.action_2);
	    if (createdByID.equalsIgnoreCase(strCurrentUserID))
	    {

		item.setVisible(true);

	    }
	    else
	    {
		item.setVisible(false);

	    }

	    MenuItem item1 = menu.findItem(R.id.action_3);
	    item1.setVisible(false);

	}
	else
	{

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_dialog_create_group, menu);
	}

	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	int id = item.getItemId();

	if (id == android.R.id.home)
	{
	    onBackPressed();
	}

	if (id == R.id.action_1)
	{
	    progressBar.setVisibility(View.VISIBLE);

	    if (ACTION.equals("BACK_FROM_SELECT_DIALOG_CREATE"))
	    {
		// no use anymore
		String groupName = edtGroupName.getText().toString().trim();
		if (groupName.equalsIgnoreCase(""))
		{
		    progressBar.setVisibility(View.GONE);
		    Toast.makeText(Activity_Group_Chat_Settings.this, "Please enter group name!", Toast.LENGTH_LONG).show();

		}
		else
		{
		    // check if no user or one user, do not create
		    if (listUserIds != null)
		    {
			if (listUserIds.isEmpty())
			{
			    progressBar.setVisibility(View.GONE);
			    Toast.makeText(Activity_Group_Chat_Settings.this, "Please select friends to chat", Toast.LENGTH_LONG).show();

			}
			else
			{
			    if (listUserIds.size() < 3)
			    {
				progressBar.setVisibility(View.GONE);
				Toast.makeText(Activity_Group_Chat_Settings.this, "Need at least 2 friends to create group chat!", Toast.LENGTH_LONG).show();
			    }
			    else
			    {
				addMoreUsersToChatRoom(listNewUsers);
			    }

			}
		    }
		    else
		    {
			progressBar.setVisibility(View.GONE);
			Toast.makeText(Activity_Group_Chat_Settings.this, "Please select friends to chat", Toast.LENGTH_LONG).show();
		    }

		}

	    }
	    else if (ACTION.equals("UPDATE_GROUP_CHAT"))
	    {

		String newGroupName = edtGroupName.getText().toString().trim();

		if (newGroupName.equalsIgnoreCase(""))
		{
		    progressBar.setVisibility(View.GONE);
		    Toast.makeText(Activity_Group_Chat_Settings.this, "Please enter group name!", Toast.LENGTH_LONG).show();
		}
		else
		{
		    if (ApplicationSingleton.isNewGroupPhoto == true)
		    {
			File file = null;
			file = createFileFromBitmap1(FILE_PATH, FILE_NAME);

			uploadContentAndUpdateGroup(file, true, FILE_PATH, FILE_NAME);
		    }
		    else if (!currentGroupName.equals(newGroupName))
		    {

			updateGroupName();

		    }
		    else
		    {
			progressBar.setVisibility(View.GONE);
			// Intent intent = new
			// Intent(Activity_Group_Chat_Settings.this,
			// DialogsActivity.class);
			// intent.putExtra("FROM",
			// "Activity_Group_Chat_Settings");
			// startActivity(intent);
		    }
		}

	    }
	    else
	    {
		Toast.makeText(Activity_Group_Chat_Settings.this, "Please select friends to chat", Toast.LENGTH_LONG).show();
		progressBar.setVisibility(View.GONE);

	    }

	}

	if (id == R.id.action_2)
	{

	    String currentGroupName = "";
	    currentGroupName = edtGroupName.getText().toString().trim();

	    ApplicationSingleton.CURRENT_GROUP_NAME = currentGroupName;
	    ApplicationSingleton.currentSelectedUsersInGroup = listUsers;

	    Intent intent = new Intent(Activity_Group_Chat_Settings.this, DialogsActivity.class);
	    intent.putExtra("FROM", "Activity_Group_Chat_Settings");
	    startActivity(intent);
	    // finish();

	}

	if (id == R.id.action_3)
	{
	    progressBar.setVisibility(View.VISIBLE);
	    leaveChatRoom();
	}

	return super.onOptionsItemSelected(item);
    }

    private void initView()
    {

	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);
	progressBar.setVisibility(View.GONE);

	btnLeaveGroup = (Button) findViewById(R.id.btnExitGroup);

	edtGroupMemberCount = (EditText) findViewById(R.id.txtMemberCount);

	listViewParticipants = (ListView) findViewById(R.id.listViewParticipants);
	imgGroupPhoto = (ImageView) findViewById(R.id.imgGroupPhoto);
	imgGroupPhoto.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setType("image/*");
		// startActivityForResult(intent, 0);

		// Intent inttent = new
		// Intent(Activity_Group_Chat_Settings.this,
		// Activity_View_Attachment.class);
		// startActivity(inttent);

		Intent intent = new Intent(Activity_Group_Chat_Settings.this, Activity_View_Attachment.class);
		// intent.putExtra("path", capturedImageUri.toString());
		// intent.putExtra("getpath", capturedImageUri.getPath());
		Activity_View_Attachment.imgPath = updateDialog.getPhoto();
		startActivityForResult(intent, 3);

	    }
	});

	adapter = new GroupChatUsersAdapter(listUsers, Activity_Group_Chat_Settings.this, "");
	listViewParticipants.setAdapter(adapter);
	edtGroupMemberCount.setText(listUsers.size() + " peoples");

	edtGroupName = (EditText) findViewById(R.id.txtGroupName);
	edtGroupName.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		edtGroupName.setEnabled(true);

	    }
	});

	btnLeaveGroup.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		progressBar.setVisibility(View.VISIBLE);
		leaveChatRoom();

	    }
	});

	edtCreatedBy = (EditText) findViewById(R.id.txtCreatedBy);

    }

    private void addMoreUsersToChatRoom(final List<QBUser> listJoinUsers)
    {

	if (updateDialog != null)
	{
	    String groupName = edtGroupName.getText().toString();
	    updateDialog.setName(groupName);

	    QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();

	    ArrayList<Integer> listNewUserIDs1 = StaticFunction.getUserIds1(listJoinUsers);

	    String[] strabc = new String[listNewUserIDs1.size()];

	    for (int i = 0; i < listNewUserIDs1.size(); i++)
	    {
		strabc[i] = listNewUserIDs1.get(i) + "";
	    }

	    requestBuilder.push("occupants_ids", strabc);

	    QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
	    groupChatManager.updateDialog(updateDialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>()
	    {

		@Override
		public void onSuccess(QBDialog dialog, Bundle args)
		{

		    progressBar.setVisibility(View.GONE);

		    // Sugar_Dialog sDialog =
		    // DialogsActivity.findDialogInDBByID(dialog.getDialogId());
		    // sDialog.delete();

		    String joinUser = "";
		    StringifyArrayList<Integer> joinUserIds = new StringifyArrayList<Integer>();

		    for (int i = 0; i < listJoinUsers.size(); i++)
		    {
			joinUser += listJoinUsers.get(i).getLogin() + " ";
			joinUserIds.add(listJoinUsers.get(i).getId());
		    }

		    StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();
		    usersIDs.addAll(updateDialog.getOccupants());

		    sendPushNotificationJoinGroup(progressBar, usersIDs, updateDialog.getDialogId(), joinUser, joinUserIds);
		    sendPushNotificationInviteUsers(progressBar, joinUserIds, updateDialog.getDialogId());

		    ApplicationSingleton.selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();
		    ApplicationSingleton.currentSelectedUsersInGroup = new ArrayList<QBUser>();

		    Intent intent = new Intent(Activity_Group_Chat_Settings.this, DialogsActivity.class);
		    startActivity(intent);
		    finish();
		}

		@Override
		public void onError(List<String> errors)
		{

		    progressBar.setVisibility(View.GONE);

		    Toast.makeText(Activity_Group_Chat_Settings.this, "add participants fail, please try again!", Toast.LENGTH_LONG).show();
		    // Log.e("leaveChatRoom", "onError");
		}
	    });
	}

    }

    private void leaveChatRoom()
    {

	// ArrayList<Integer> listOcc1 = updateDialog.getOccupants();

	if (updateDialog != null)
	{
	    String groupName = edtGroupName.getText().toString();
	    updateDialog.setName(groupName);

	    QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
	    // requestBuilder.push("occupants_ids", 378); // add another
	    // users

	    QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Group_Chat_Settings.this);
	    int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Group_Chat_Settings.this);
	    currentLoginUser.setId(currentLoginUserID);

	    requestBuilder.pullAll("occupants_ids", currentLoginUserID); // Remove
									 // yourself
									 // (user
	    // with ID 22)

	    // assign admin role to other user
	    //
	    // check if current user is admin

	    //

	    QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
	    groupChatManager.updateDialog(updateDialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>()
	    {

		@Override
		public void onSuccess(QBDialog dialog, Bundle args)
		{

		    progressBar.setVisibility(View.GONE);
		    Log.e("leaveChatRoom", "onSuccess");

		    // leave group, not delete set status = 3
		    Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialog.getDialogId());
		    sDialog.dialogStatus = 3;
		    sDialog.save();

		    // Intent intent = new
		    // Intent(Activity_Group_Chat_Settings.this,
		    // DialogsActivity.class);
		    // startActivity(intent);

		    isCloseChatActivity = true;

		    //
		    // collect all occupants ids
		    StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();

		    usersIDs.addAll(dialog.getOccupants());

		    int currentUserId = SharePrefsHelper.getCurrentLoginUserID(Activity_Group_Chat_Settings.this);
		    QBUser user = SharePrefsHelper.getCurrentLoginUser(Activity_Group_Chat_Settings.this);

		    sendPushNotificationLeaveGroup(progressBar, usersIDs, dialog.getDialogId(), currentUserId, user.getLogin());
		    //

		    finish();
		}

		@Override
		public void onError(List<String> errors)
		{

		    progressBar.setVisibility(View.GONE);

		    Toast.makeText(Activity_Group_Chat_Settings.this, "leave group fail, please try again!", Toast.LENGTH_LONG).show();
		    // Log.e("leaveChatRoom", "onError");
		}
	    });
	}

    }

    public void sendPushNotificationLeaveGroup(View view, StringifyArrayList<Integer> userids, String dialogId, int senderId, String senderName)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "leavegroup");
	    json.put("dialog_id", dialogId);
	    json.put("sender_id", senderId);
	    json.put("sender_name", senderName);
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

    public void sendPushNotificationJoinGroup(View view, StringifyArrayList<Integer> userids, String dialogId, String joinUsers, StringifyArrayList<Integer> joinUserids)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "joingroup");
	    json.put("dialog_id", dialogId);
	    json.put("join_users", joinUsers);

	    String strJoinUserIDs = "";
	    for (Integer integer : joinUserids)
	    {
		strJoinUserIDs += integer + ",";
	    }

	    json.put("join_user_ids", strJoinUserIDs);
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

    public void sendPushNotificationInviteUsers(View view, StringifyArrayList<Integer> userids, String dialogId)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "youareinvited");
	    json.put("dialog_id", dialogId);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {

	super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	switch (requestCode)
	{

	    case 3:
		if (resultCode == RESULT_OK)
		{
		    FILE_PATH = Activity_View_Attachment.FILE_PATH;
		    FILE_NAME = Activity_View_Attachment.FILE_NAME;
		    ApplicationSingleton.isNewGroupPhoto = true;
		    imageLoader.displayImage(ApplicationSingleton.CURRENT_GROUP_PHOTO, imgGroupPhoto, options);

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

			if (isImage)
			{

			    FILE_PATH = filePath;
			    FILE_NAME = filename;
			    ApplicationSingleton.isNewGroupPhoto = true;
			    ApplicationSingleton.CURRENT_GROUP_PHOTO = selectedImage.toString();
			    imageLoader.displayImage(selectedImage.toString(), imgGroupPhoto, options);

			}
		    }
		    catch (Exception ex)
		    {

		    }

		}
		break;
	}
    }

    private void uploadContentAndUpdateGroup(File f, boolean fileIsPublic, final String filePath, final String fileName)
    {

	QBContent.uploadFileTask(f, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>()
	{

	    @Override
	    public void onSuccess(QBFile file, Bundle params)
	    {

		String groupName = edtGroupName.getText().toString();

		updateDialog.setName(groupName);
		updateDialog.setPhoto(file.getPublicUrl());

		FILE_PATH = "";
		FILE_NAME = "";
		ApplicationSingleton.CURRENT_GROUP_PHOTO = "";
		ApplicationSingleton.isNewGroupPhoto = false;

		QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
		// requestBuilder.push("occupants_ids", 378); // add another
		// users
		// requestBuilder.pullAll("occupants_ids", 22); // Remove
		// yourself (user with ID 22)

		QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
		groupChatManager.updateDialog(updateDialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>()
		{

		    @Override
		    public void onSuccess(QBDialog dialog, Bundle args)
		    {

			ArrayList<QBDialog> listDialogs = new ArrayList<QBDialog>();
			listDialogs.add(dialog);

			StaticFunction.saveListDialogToDB(listDialogs);

			progressBar.setVisibility(View.GONE);

			//
			StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();
			usersIDs.addAll(dialog.getOccupants());
			sendPushNotificationToOpponentUserNotifyChangeGroupProfile(progressBar, usersIDs, dialog.getDialogId());
			//

			finish();
		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			progressBar.setVisibility(View.GONE);
		    }
		});

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		progressBar.setVisibility(View.GONE);
	    }
	});
	//
    }

    public void sendPushNotificationToOpponentUserNotifyChangeGroupProfile(View view, StringifyArrayList<Integer> userids, String dialogID)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	//
	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "update_group_profile");
	    json.put("dialog_id", dialogID);
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
	userIds.addAll(userids); // .add(2111993)
	qbEvent.setUserIds(userIds);

	QBMessages.createEvent(qbEvent, new QBEntityCallbackImpl<QBEvent>()
	{

	    @Override
	    public void onSuccess(QBEvent qbEvent, Bundle bundle)
	    {

	    }

	    @Override
	    public void onError(List<String> strings)
	    {

	    }
	});

    }

    private void updateGroupName()
    {

	String groupName = edtGroupName.getText().toString();

	updateDialog.setName(groupName);

	QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
	// requestBuilder.push("occupants_ids", 378); // add another
	// users
	// requestBuilder.pullAll("occupants_ids", 22); // Remove
	// yourself (user with ID 22)

	QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
	groupChatManager.updateDialog(updateDialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>()
	{

	    @Override
	    public void onSuccess(QBDialog dialog, Bundle args)
	    {

		ArrayList<QBDialog> listQBDialog = new ArrayList<QBDialog>();
		listQBDialog.add(dialog);

		StaticFunction.saveListDialogToDB(listQBDialog);

		progressBar.setVisibility(View.GONE);
		finish();

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		progressBar.setVisibility(View.GONE);
	    }
	});

    }

    private File createFileFromBitmap1(String filepath, String filename)
    {

	Bitmap resizedBitmap = null;

	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	int screenSampleSize = StaticFunction.getBitmapInSampleSize(Activity_Group_Chat_Settings.this, filepath);
	bmOptions.inSampleSize = screenSampleSize;
	Bitmap bmp = BitmapFactory.decodeFile(filepath, bmOptions);
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

	File f1 = new File(Activity_Group_Chat_Settings.this.getCacheDir(), filename);

	try
	{

	    f1.createNewFile();

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    resizedBitmap.compress(CompressFormat.JPEG, 100 /* ignored for PNG */, bos);
	    byte[] bitmapdata = bos.toByteArray();

	    // write the bytes in file
	    FileOutputStream fos = new FileOutputStream(f1);
	    fos.write(bitmapdata);
	    fos.close();
	    fos.flush();

	    resizedBitmap.recycle();

	}
	catch (Exception e)
	{
	    resizedBitmap.recycle();
	    // progressBar.setVisibility(View.GONE);

	}

	return f1;
    }

    private void startGroupChat(QBDialog dialog)
    {

	Bundle bundle = new Bundle();
	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);
	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

	ChatActivity.start(Activity_Group_Chat_Settings.this, bundle);

	finish();

    }

    public String findAdminofChatRoom(QBDialog dialog)
    {

	int adminID = 0;

	try
	{
	    adminID = dialog.getUserId();
	}
	catch (Exception ex)
	{
	    adminID = 0;
	}

	ArrayList<Integer> listOccupants = new ArrayList<Integer>();

	if (adminID != 0)
	{
	    listOccupants = dialog.getOccupants();

	    for (int i = 0; i < listOccupants.size(); i++)
	    {
		if (listOccupants.get(i) == adminID)
		{
		    return adminID + "";
		}
	    }

	    return listOccupants.get(0) + "";

	}
	else
	{
	    return "";
	}

    }

}
