package com.es.hello.chat.ui.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.ui.adapters.GroupChatUsersAdapter;
import com.lat.hello.chat.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.users.model.QBUser;

public class Activity_Group_Chat_Step2 extends ActionBarActivity
{

    ListView listViewParticipants;

    public String ACTION = "";

    List<QBDialog> listDialogs = new ArrayList<QBDialog>();

    ArrayList<Integer> listUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_group_chat_step2);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "Add participants", true);
	// FontTypeUtils.setFontForTittleBar(this, this);

	/*getActionBar().setTitle("Add participants");
	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	getActionBar().setHomeButtonEnabled(true);
	getActionBar().setDisplayHomeAsUpEnabled(true);*/

	initView();

	// Intent intent = getIntent();
	Bundle extras = getIntent().getExtras();
	if (extras == null)
	{
	    ACTION = "";
	}
	else
	{
	    ACTION = extras.getString("ACTION", "");
	}

	if (ACTION.equals("BACK_FROM_SELECT_DIALOG_CREATE"))
	{

	    listDialogs = ApplicationSingleton.selectedDialogToCreateGroupChat;
	    listUserIds = StaticFunction.getUserIds(listDialogs);

	    List<QBUser> users = StaticFunction.findListUserByUserId(listUserIds);

	    int iCurrentUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Group_Chat_Step2.this);
	    String strCurrentUserID = iCurrentUserID + "";
	    GroupChatUsersAdapter adapter = new GroupChatUsersAdapter(users, Activity_Group_Chat_Step2.this, strCurrentUserID);
	    listViewParticipants.setAdapter(adapter);
	    // edtGroupMemberCount.setText(users.size() + " peoples");

	    // taskLoadUserInfoBackground(listUserIds);

	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_create_group_step2, menu);

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

	    // check if no user or one user, do not create
	    if (listUserIds != null)
	    {
		if (listUserIds.isEmpty())
		{
		    Toast.makeText(Activity_Group_Chat_Step2.this, "Please select friends to chat", Toast.LENGTH_LONG).show();

		}
		else
		{
		    if (listUserIds.size() < 3)
		    {
			Toast.makeText(Activity_Group_Chat_Step2.this, "Need at least 2 friends to create group chat!", Toast.LENGTH_LONG).show();
		    }
		    else
		    {

			String file_path = SharePrefsHelper.getGroupPhoto(Activity_Group_Chat_Step2.this);
			if (!file_path.equalsIgnoreCase(""))
			{

			    File f = new File(file_path);
			    final String filename = f.getName();

			    File file = null;
			    file = createFileFromBitmap1(file_path, filename);

			    item.setEnabled(false);
			    uploadContentAndCreateNewGroup(file, true, file_path, filename);

			}
			else
			{
			    item.setEnabled(false);
			    createGroupChatWithoutPhoto();

			}

		    }

		}
	    }
	    else
	    {

		Toast.makeText(Activity_Group_Chat_Step2.this, "Please select friends to chat", Toast.LENGTH_LONG).show();
	    }

	}

	if (id == R.id.action_2)
	{

	    Intent intent = new Intent(Activity_Group_Chat_Step2.this, DialogsActivity.class);
	    intent.putExtra("FROM", "Activity_Group_Chat_Step2");
	    startActivity(intent);
	    finish();

	}

	return super.onOptionsItemSelected(item);
    }

    private void initView()
    {

	listViewParticipants = (ListView) findViewById(R.id.listViewParticipants);
    }

    private void startGroupChat(QBDialog dialog)
    {

	// send push to orther user
	StringifyArrayList<Integer> usersIDs = new StringifyArrayList<Integer>();
	usersIDs.addAll(dialog.getOccupants());

	sendPushNotificationCreateNewGroupToAllMember(listViewParticipants, usersIDs, dialog.getDialogId());

	//

	Bundle bundle = new Bundle();
	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);
	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

	ChatActivity.start(Activity_Group_Chat_Step2.this, bundle);
	finish();

    }

    private void createGroupChatWithoutPhoto()
    {

	String groupName = SharePrefsHelper.getGroupName(Activity_Group_Chat_Step2.this);

	QBDialog dialogToCreate = new QBDialog();
	dialogToCreate.setName(groupName);
	dialogToCreate.setType(QBDialogType.GROUP);
	dialogToCreate.setOccupantsIds(listUserIds);
	dialogToCreate.setLastMessageDateSent(System.currentTimeMillis() / 1000);

	QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
	{

	    @Override
	    public void onSuccess(QBDialog dialog, Bundle args)
	    {

		SharePrefsHelper.saveGroupNameToSharePrefs("", Activity_Group_Chat_Step2.this);

		dialog.setLastMessageDateSent(System.currentTimeMillis() / 1000);
		ArrayList<QBDialog> listDialogs = new ArrayList<QBDialog>();
		listDialogs.add(dialog);

		StaticFunction.saveListDialogToDB(listDialogs);
		// progressBar.setVisibility(View.GONE);
		startGroupChat(dialog);

		ApplicationSingleton.selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		// progressBar.setVisibility(View.GONE);

	    }
	});

    }

    private File createFileFromBitmap1(String filepath, String filename)
    {

	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	int screenSampleSize = StaticFunction.getBitmapInSampleSize(Activity_Group_Chat_Step2.this, filepath);
	bmOptions.inSampleSize = screenSampleSize / 2;
	Bitmap bmp = BitmapFactory.decodeFile(filepath, bmOptions);
	int width = bmp.getWidth();
	int height = bmp.getHeight();

	File f1 = new File(Activity_Group_Chat_Step2.this.getCacheDir(), filename);

	try
	{

	    f1.createNewFile();

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    bmp.compress(CompressFormat.JPEG, 50 /* ignored for PNG */, bos);
	    byte[] bitmapdata = bos.toByteArray();

	    // write the bytes in file
	    FileOutputStream fos = new FileOutputStream(f1);
	    fos.write(bitmapdata);
	    fos.close();
	    fos.flush();

	    bmp.recycle();

	}
	catch (Exception e)
	{
	    bmp.recycle();
	}

	return f1;
    }

    private void uploadContentAndCreateNewGroup(File f, boolean fileIsPublic, final String filePath, final String fileName)
    {

	QBContent.uploadFileTask(f, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>()
	{

	    @Override
	    public void onSuccess(QBFile file, Bundle params)
	    {

		String groupName = SharePrefsHelper.getGroupName(Activity_Group_Chat_Step2.this);

		QBDialog dialogToCreate = new QBDialog();
		dialogToCreate.setName(groupName);
		dialogToCreate.setType(QBDialogType.GROUP);
		dialogToCreate.setOccupantsIds(listUserIds);
		dialogToCreate.setPhoto(file.getPublicUrl());
		dialogToCreate.setLastMessageDateSent(System.currentTimeMillis() / 1000);

		// reset data here
		SharePrefsHelper.saveGroupPhotoToSharePrefs("", Activity_Group_Chat_Step2.this);
		SharePrefsHelper.saveGroupNameToSharePrefs("", Activity_Group_Chat_Step2.this);

		QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
		{

		    @Override
		    public void onSuccess(QBDialog dialog, Bundle args)
		    {

			dialog.setLastMessageDateSent(System.currentTimeMillis() / 1000);
			ArrayList<QBDialog> listDialogs = new ArrayList<QBDialog>();
			listDialogs.add(dialog);

			StaticFunction.saveListDialogToDB(listDialogs);
			// progressBar.setVisibility(View.GONE);
			startGroupChat(dialog);

			ApplicationSingleton.selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			// progressBar.setVisibility(View.GONE);
		    }
		});

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		// progressBar.setVisibility(View.GONE);
	    }
	});
	//
    }

    public void sendPushNotificationCreateNewGroupToAllMember(View view, StringifyArrayList<Integer> userids, String dialogId)
    {

	// Send Push: create QuickBlox Push Notification Event
	QBEvent qbEvent = new QBEvent();
	qbEvent.setNotificationType(QBNotificationType.PUSH);
	qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
	qbEvent.setPushType(QBPushType.GCM);

	JSONObject json = new JSONObject();
	try
	{
	    json.put("type", "createnewgroup");
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

		// progressBar.setVisibility(View.GONE);
	    }

	    @Override
	    public void onError(List<String> strings)
	    {

		// progressBar.setVisibility(View.GONE);

	    }
	});

    }

}
