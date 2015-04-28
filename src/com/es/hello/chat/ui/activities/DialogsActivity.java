package com.es.hello.chat.ui.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.GCMIntentService;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.core.ChatManager;
import com.es.hello.chat.services.HelloMainService;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_Message;
import com.es.hello.chat.ui.adapters.DialogsAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lat.hello.chat.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DialogsActivity extends ActionBarActivity implements HelloMainService.ServiceInterfaceCallback, ChatActivity.ChatCallback, HelloMainService.ConnectionInterfaceCallback
{

    LinearLayout btnMoveDown;

    PullToRefreshListView pullToRefreshDialogListView;

    private ListView dialogsListView;

    private RelativeLayout progressBar;

    public List<ChatManager> listChatManager;

    ArrayList<QBDialog> dialogsList = new ArrayList<QBDialog>();

    public boolean IS_LOADING_DIALOG = false;

    private String FROM = "";

    public DialogsAdapter adapter;

    public static int y = 10;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver()
    {

	@Override
	public void onReceive(Context context, Intent intent)
	{

	    if (intent.getAction().equalsIgnoreCase(GCMIntentService.ACTION_BROADCAST_RECEIVER))
	    {
		String message_body = intent.getExtras().getString("MESS_BODY");
		String dialog_id = intent.getExtras().getString("DIALOG_ID");

		if (IS_LOADING_DIALOG == false)
		{
		    updateDialogOnNewChat(dialog_id, message_body);
		}
	    }
	    else if (intent.getAction().equalsIgnoreCase(GCMIntentService.ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES))
	    {
		String dialog_id = intent.getExtras().getString("DIALOG_ID");

		loadDialogProfileInfoForGroupChat(dialog_id);
	    }
	    else if (intent.getAction().equalsIgnoreCase(GCMIntentService.ACTION_BROADCAST_RECEIVER_GROUP_New_User_Join))
	    {
		loadDialogFromDB();
	    }
	    else if (intent.getAction().equalsIgnoreCase(GCMIntentService.ACTION_BROADCAST_RECEIVER_NEW_GROUP_CREATED))
	    {
		loadDialogFromDB();
	    }
	}
    };

    public boolean isOnCreate = false;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.dialogs_activity);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "CHATS", false);

	HelloMainService.ConnectionCallback = this;

	isOnCreate = true;

	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);
	progressBar.setVisibility(View.VISIBLE);

	pullToRefreshDialogListView = (PullToRefreshListView) findViewById(R.id.roomsList);
	pullToRefreshDialogListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
	{

	    @Override
	    public void onRefresh(PullToRefreshBase<ListView> refreshView)
	    {

		loadDialogs(false);

	    }
	});

	dialogsListView = pullToRefreshDialogListView.getRefreshableView();

	registerMoveDownButton();
	//

	ChatActivity.chatCallback = this;

	Bundle extras = getIntent().getExtras();
	if (extras == null)
	{
	    FROM = "";
	}
	else
	{
	    FROM = extras.getString("FROM", "");
	}

	if (activityReceiver != null)
	{
	    IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(GCMIntentService.ACTION_BROADCAST_RECEIVER);
	    intentFilter.addAction(GCMIntentService.ACTION_BROADCAST_RECEIVER_GROUP_PROFILE_CHANGES);
	    intentFilter.addAction(GCMIntentService.ACTION_BROADCAST_RECEIVER_GROUP_New_User_Join);
	    intentFilter.addAction(GCMIntentService.ACTION_BROADCAST_RECEIVER_NEW_GROUP_CREATED);

	    registerReceiver(activityReceiver, intentFilter);
	}

	// resgister service
	HelloMainService.Callback = this;
	Intent intent = new Intent(this, HelloMainService.class);
	intent.addCategory(HelloMainService.TAG);
	startService(intent);
    }

    @Override
    protected void onResume()
    {

	super.onResume();
	HelloMainService.ConnectionCallback = this;

	if (isOnCreate == false)
	{
	    // resgister service
	    HelloMainService.Callback = this;
	    Intent intent = new Intent(this, HelloMainService.class);
	    intent.addCategory(HelloMainService.TAG);
	    startService(intent);
	}
    }

    private void scrollListViewToBottom()
    {

	if (dialogsListView != null)
	{
	    dialogsListView.post(new Runnable()
	    {

		@Override
		public void run()
		{

		    if (adapter != null)
		    {
			int size = adapter.getCount();
			int totalHeight = 0;

			for (int i = 0; i < size; i++)
			{
			    View mView = adapter.getView(i, null, dialogsListView);

			    mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

			    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			    totalHeight += mView.getMeasuredHeight();

			}

			dialogsListView.smoothScrollBy(totalHeight, totalHeight * 5);

		    }

		}
	    });

	}

    }

    private void StopScrollListView()
    {

	if (dialogsListView != null)
	{
	    dialogsListView.post(new Runnable()
	    {

		@Override
		public void run()
		{

		    dialogsListView.smoothScrollBy(0, 0);

		}
	    });
	}

    }

    // sugar intent to chat
    //
    // test
    /*Sugar_Dialog sDialog = Sugar_Dialog.findById(Sugar_Dialog.class, 3l);

    final QBDialog qbDialog = new QBDialog();
    qbDialog.setDialogId(sDialog.dialogId);
    qbDialog.setName(sDialog.dialogName);
    qbDialog.setType(QBDialogType.PRIVATE);
    qbDialog.setPhoto(sDialog.dialogPhoto);
    qbDialog.setRoomJid(sDialog.dialogXmppRoomJid);

    String abc = sDialog.dialogOccupantsIds;
    ArrayList<Integer> listIDs = new ArrayList<Integer>();

    listIDs.add(2141895);
    listIDs.add(2174474);

    qbDialog.setOccupantsIds(listIDs);
    qbDialog.setLastMessage(sDialog.dialogLastMessage);
    qbDialog.setLastMessageDateSent(sDialog.dialogLastMessageDateSent);
    qbDialog.setLastMessageUserId(sDialog.dialogLastMessageUserId);
    qbDialog.setUnreadMessageCount(sDialog.dialogUnreadMessagesCount);
    qbDialog.setUserId(sDialog.dialogCreatedUserID);

    QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
    requestBuilder.setPage(1);
    requestBuilder.setPerPage(listIDs.size());
    //

    QBUsers.getUsersByIDs(listIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>()
    {

        @Override
        public void onSuccess(ArrayList<QBUser> users, Bundle params)
        {

    	// Save users
    	//
    	ApplicationSingleton.setDialogsUsers(users);

    	Bundle bundle = new Bundle();
    	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, qbDialog);

    	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);

    	// Open chat activity
    	//
    	ChatActivity.start(DialogsActivity.this, bundle);
    	finish();

        }

        @Override
        public void onError(List<String> errors)
        {

    	progressBar.setVisibility(View.GONE);
    	AlertDialog.Builder dialog = new AlertDialog.Builder(DialogsActivity.this);
    	dialog.setMessage("get occupants errors: " + errors).create().show();
        }

    });*/

    // test
    //
    // sugar intent to chat

    private void registerMoveDownButton()
    {

	btnMoveDown = (LinearLayout) findViewById(R.id.btnMoveDown);
	btnMoveDown.setVisibility(View.GONE);
	btnMoveDown.setOnTouchListener(new OnTouchListener()
	{

	    @Override
	    public boolean onTouch(View v, MotionEvent event)
	    {

		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{

		    scrollListViewToBottom();
		    Log.e("ACTION_DOWN", "ACTION_DOWN");

		}

		// }

		if (event.getAction() == MotionEvent.ACTION_UP)
		{
		    Log.e("ACTION_UP", "ACTION_UP");
		    StopScrollListView();

		}

		return true;

	    }
	});
    }

    private void loadDialogFromServer()
    {

	final QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
	customObjectRequestBuilder.setPagesLimit(500);

	// this part is for loading only new dialogs, others taken from // now
	// not using temporaryly
	// database
	// List<String> listIDs = new ArrayList<String>();
	// List<Sugar_Dialog> listSugarDialogs =
	// Sugar_Dialog.listAll(Sugar_Dialog.class);
	// for (Sugar_Dialog sugar_Dialog : listSugarDialogs)
	// {
	// listIDs.add(sugar_Dialog.dialogId);
	// }

	// String[] exceptionDialogs = new String[listIDs.size()];
	// listIDs.toArray(exceptionDialogs);

	// customObjectRequestBuilder.nin("_id", exceptionDialogs);

	// this part is for loading only new dialogs, others taken from
	// database

	QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>()
	{

	    @Override
	    public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args)
	    {

		runOnUiThread(new Runnable()
		{

		    @Override
		    public void run()
		    {

			if (!isFinishing())
			{
			    dialogsList = dialogs;

			    // this part is for loading only new dialogs, others
			    // taken
			    // from
			    // database
			    StaticFunction.saveListDialogToDB(dialogsList);
			    ArrayList<QBDialog> listQBDialog = new ArrayList<QBDialog>();
			    List<Sugar_Dialog> listSugarDialogs = Sugar_Dialog.listAll(Sugar_Dialog.class);
			    listQBDialog = listSugarDialogToListQBDialog(listSugarDialogs);
			    dialogsList = listQBDialog;
			    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(true, DialogsActivity.this);
			    // this part is for loading only new dialogs, others
			    // taken
			    // from
			    // database

			    initForDialogList1(true);
			}
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

			if (!isFinishing())
			{
			    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(false, DialogsActivity.this);
			    progressBar.setVisibility(View.GONE);
			    IS_LOADING_DIALOG = false;
			    pullToRefreshDialogListView.onRefreshComplete();

			    showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, errors.toString());
			}

		    }
		});

	    }
	});

    }

    private void loadDialogFromDB()
    {

	progressBar.setVisibility(View.VISIBLE);

	ArrayList<QBDialog> listQBDialog = new ArrayList<QBDialog>();

	List<Sugar_Dialog> listSugarDialogs = Sugar_Dialog.listAll(Sugar_Dialog.class);

	listQBDialog = listSugarDialogToListQBDialog(listSugarDialogs);

	dialogsList = listQBDialog;

	initForDialogList1(false);
    }

    private void loadDialogs(boolean isDownloaded)
    {

	IS_LOADING_DIALOG = true;

	progressBar.setVisibility(View.VISIBLE);

	// boolean isDownload = false;

	// now it is false by default mean that it
	// will re-load the dialog each time // but
	// foe future, it should be check from
	// database using next comment
	// isDownload =
	// SharePrefsHelper.getIsDownloadedDialogListToSharePrefs(DialogsActivity.this);

	if (isDownloaded == false)
	{
	    loadDialogFromServer();
	}
	else
	// no need to load again => take from database
	{
	    loadDialogFromDB();

	    // after loading the old from DB, now we will update the new data
	    // backgroudy
	    /*Handler handle = new Handler();
	    handle.postDelayed(new Runnable()
	    {

	    @Override
	    public void run()
	    {

	        loadDialogFromServer();

	    }
	    }, 2000);*/

	}

    }

    public void initForDialogList1(final boolean isFromServer)
    {

	// collect all occupants ids
	List<Integer> usersIDs = new ArrayList<Integer>();
	for (final QBDialog dialog : dialogsList)
	{

	    usersIDs.addAll(dialog.getOccupants());

	}

	List<Integer> listExceptionUsersIDs = new ArrayList<Integer>();

	listExceptionUsersIDs = StaticFunction.findListExceptionUserId(usersIDs);

	// load user from db first, if not exist, load from server
	// and save to db for later use

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

		    updateListView(dialogsList, FROM, isFromServer);
		    Log.e("11111111111111111111", "initForDialogList 1");
		    progressBar.setVisibility(View.GONE);

		    IS_LOADING_DIALOG = false;

		}

		@Override
		public void onError(List<String> errors)
		{

		    if (!isFinishing())
		    {
			progressBar.setVisibility(View.GONE);
			IS_LOADING_DIALOG = false;
			AlertDialog.Builder dialog = new AlertDialog.Builder(DialogsActivity.this);
			dialog.setMessage("get occupants errors: " + errors).create().show();
			pullToRefreshDialogListView.onRefreshComplete();
		    }
		}

	    });

	}
	else
	{
	    // Save users
	    // ApplicationSingleton.setDialogsUsers(listQBUsers);

	    updateListView(dialogsList, FROM, isFromServer);
	    Log.e("11111111111111111111", "initForDialogList 2");

	    progressBar.setVisibility(View.GONE);

	    IS_LOADING_DIALOG = false;

	    pullToRefreshDialogListView.onRefreshComplete();

	}

	StaticFunction.ChangeEdgeEffect(DialogsActivity.this, pullToRefreshDialogListView, Color.LTGRAY);

    }

    private ArrayList<QBDialog> listSugarDialogToListQBDialog(List<Sugar_Dialog> listSugarDialogs)
    {

	ArrayList<QBDialog> listQBDialog = new ArrayList<QBDialog>();

	for (Sugar_Dialog sDialog : listSugarDialogs)
	{

	    if (sDialog.dialogStatus != 3)
	    {
		QBDialog qbDialog = new QBDialog();
		qbDialog.setDialogId(sDialog.dialogId);
		qbDialog.setName(sDialog.dialogName);
		if (sDialog.dialogType == 1)
		{
		    qbDialog.setType(QBDialogType.PRIVATE);
		}
		else if (sDialog.dialogType == 2)
		{
		    qbDialog.setType(QBDialogType.GROUP);
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

		    listQBDialog.add(qbDialog);

		}

	    }

	}

	dialogsList = listQBDialog;

	return dialogsList;
    }

    public class MyComparator implements Comparator<QBDialog>
    {

	public int compare(QBDialog dg1, QBDialog dg2)
	{

	    if (dg1.getLastMessageDateSent() < dg2.getLastMessageDateSent())
		return 1;

	    if (dg1.getLastMessageDateSent() == dg2.getLastMessageDateSent())
		return 0;
	    return -1;
	}
    }

    void updateListView(List<QBDialog> dialogs, String from, boolean isFromServer)
    {

	if (isFromServer == true)
	{
	    if (adapter != null)
	    {
		for (int i = 0; i < dialogs.size(); i++)
		{

		    long lastMessDateSent = findLastMessageTimeforDialog(dialogs.get(i).getDialogId());
		    int length = String.valueOf(lastMessDateSent).length();

		    if (length > 10)
		    {
			if (length > 13)
			{
			    lastMessDateSent = lastMessDateSent / 1000000;
			}
			else
			{
			    lastMessDateSent = lastMessDateSent / 1000;
			}
		    }

		    dialogs.get(i).setLastMessageDateSent(lastMessDateSent * 1000);
		    Collections.sort(dialogs, new MyComparator());
		}

		adapter.setDataSource(dialogs);
		adapter.notifyDataSetChanged();

		//
		if (adapter.getCount() > 0)
		{

		    DisplayMetrics displayMetrics = DialogsActivity.this.getResources().getDisplayMetrics();

		    float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

		    int oneRowHeight = 67;

		    int totalHeight = oneRowHeight * (adapter.getCount() + 1);

		    if (totalHeight > dpHeight)
		    {

			btnMoveDown.setVisibility(View.VISIBLE);
		    }
		    else
		    {
			btnMoveDown.setVisibility(View.GONE);
		    }
		}
		else
		{
		    btnMoveDown.setVisibility(View.GONE);
		}
		//
	    }
	    else
	    {
		buildListView1(dialogs, from);
	    }
	}
	else
	{
	    buildListView1(dialogs, from);
	}

    }

    void buildListView1(List<QBDialog> dialogs, String from)
    {

	adapter = new DialogsAdapter(dialogs, DialogsActivity.this, from);
	dialogsListView.setAdapter(adapter);

	// float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

	// int height = getItemHeightofListView(dialogsListView, 0);

	if (adapter.getCount() > 0)
	{

	    DisplayMetrics displayMetrics = DialogsActivity.this.getResources().getDisplayMetrics();

	    float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

	    int oneRowHeight = 67;

	    int totalHeight = oneRowHeight * (adapter.getCount() + 1);

	    if (totalHeight > dpHeight)
	    {

		btnMoveDown.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		btnMoveDown.setVisibility(View.GONE);
	    }
	}
	else
	{
	    btnMoveDown.setVisibility(View.GONE);
	}

	dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
	{

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    {

		Sugar_Dialog sdia = StaticFunction.findDialogInDBByID("550bd167535c12dcaa0010cc");

		position = position - 1;

		QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

		Bundle bundle = new Bundle();
		bundle.putSerializable(ChatActivity.EXTRA_DIALOG, (QBDialog) adapter.getItem(position));

		// group
		if (selectedDialog.getType().equals(QBDialogType.GROUP))
		{
		    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

		    // private
		}
		else
		{
		    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
		}

		// Open chat activity
		//
		ChatActivity.reIntentClass = DialogsActivity.class;
		ChatActivity.start(DialogsActivity.this, bundle);
		// finish();
	    }
	});

	dialogsListView.setOnItemLongClickListener(new OnItemLongClickListener()
	{

	    @Override
	    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	    {

		position = position - 1;
		QBDialog selectedDialog = (QBDialog) adapter.getItem(position);
		String dialogID = selectedDialog.getDialogId();

		showPopupMenu(DialogsActivity.this, R.layout.dialog_menu_favourite_view, dialogID);
		return false;
	    }
	});

	// pullToRefreshDialogListView.onRefreshComplete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Settings") || this.FROM.equalsIgnoreCase("Activity_Group_Chat_Step2"))
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_dialog_create_group_in_dialog, menu);
	}
	else
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.rooms, menu);
	}

	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Settings"))
	{
	    int id = item.getItemId();
	    if (id == R.id.action_1)
	    {

		List<QBDialog> listDialogs = adapter.getSelected();

		if (listDialogs != null)
		{
		    ApplicationSingleton.selectedDialogToCreateGroupChat = listDialogs;
		}

		Intent intent = new Intent(DialogsActivity.this, Activity_Group_Chat_Settings.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("ACTION", "BACK_FROM_SELECT_DIALOG_CREATE");
		startActivity(intent);
		finish();

	    }

	}
	else if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Step2"))
	{

	    int id = item.getItemId();
	    if (id == R.id.action_1)
	    {

		List<QBDialog> listDialogs = adapter.getSelected();

		if (listDialogs != null)
		{
		    ApplicationSingleton.selectedDialogToCreateGroupChat = listDialogs;
		}

		Intent intent = new Intent(DialogsActivity.this, Activity_Group_Chat_Step2.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("ACTION", "BACK_FROM_SELECT_DIALOG_CREATE");
		startActivity(intent);
		finish();

	    }

	}
	else
	{
	    int id = item.getItemId();
	    if (id == R.id.action_add)
	    {
		/*
		 * // go to New Dialog activity Intent intent = new
		 * Intent(DialogsActivity.this, NewDialogActivity.class);
		 * startActivity(intent); finish();
		 */
		showPopupMenuActionOthers(DialogsActivity.this, R.layout.dialog_menu_action_others);
		return true;
	    }

	    if (id == R.id.action_1)
	    {
		Intent intent = new Intent(DialogsActivity.this, Activity_Setting.class);
		startActivity(intent);
		// finish();

	    }

	    if (id == R.id.action_2)
	    {
		Intent intent = new Intent(DialogsActivity.this, Activity_Search.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		// finish();
		return true;
	    }
	}

	return super.onOptionsItemSelected(item);
    }

    private void updateDialogOnNewChat(final String dialogID, final String message)
    {

	DialogsActivity.this.runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		final String id = dialogID; // message.getProperty("dialog_id");

		// mean dialog existing in the list
		for (int i = 0; i < dialogsList.size(); i++)
		{
		    String IDD = dialogsList.get(i).getDialogId();
		    if (IDD.equalsIgnoreCase(id))
		    {
			QBDialog qbDialog = dialogsList.get(i);
			qbDialog.setLastMessage(message);
			qbDialog.setLastMessageDateSent((System.currentTimeMillis() / 1000));
			dialogsList.remove(i);
			dialogsList.add(0, qbDialog);

			ArrayList<QBDialog> listQBDialogs = new ArrayList<QBDialog>();
			listQBDialogs.add(qbDialog);
			StaticFunction.saveListDialogToDB(listQBDialogs);

			updateListView(dialogsList, FROM, false);
			Log.e("11111111111111111111", "updateDialogOnNewChat");
			return;
		    }
		}

		// use this one to load all
		// if new dialog -> reload the list from server
		// loadDialogFromServer();
		// use this one to load all

		// use this one to load only 1 new dialog
		// else this is a new dialog
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.eq("_id", id);

		QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>()
		{

		    @Override
		    public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args)
		    {

			runOnUiThread(new Runnable()
			{

			    @Override
			    public void run()
			    {

				if (dialogs != null && !dialogs.isEmpty())
				{
				    QBDialog qbDialog = dialogs.get(0);

				    long lastmesssent = qbDialog.getLastMessageDateSent();

				    qbDialog.setLastMessageDateSent(lastmesssent * 1000);

				    dialogsList.add(0, qbDialog);

				    for (int i = 0; i < dialogsList.size(); i++)
				    {
					dialogsList.get(i).setLastMessageDateSent(dialogsList.get(i).getLastMessageDateSent() / 1000);

				    }

				    // ArrayList<QBDialog> listQBDialogs = new
				    // ArrayList<QBDialog>();
				    // listQBDialogs.add(qbDialog);

				    // this part is for loading only new
				    // dialogs, others
				    // taken
				    // from
				    // database
				    StaticFunction.saveListDialogToDB(dialogsList);
				    ArrayList<QBDialog> listQBDialog = new ArrayList<QBDialog>();

				    List<Sugar_Dialog> listSugarDialogs = Sugar_Dialog.listAll(Sugar_Dialog.class);

				    listQBDialog = listSugarDialogToListQBDialog(listSugarDialogs);
				    dialogsList = listQBDialog;
				    SharePrefsHelper.saveIsDownloadedDialogListToSharePrefs(true, DialogsActivity.this);
				    // this part is for loading only new
				    // dialogs, others
				    // taken
				    // from
				    // database

				    initForDialogList1(true);
				}

			    }
			});

		    }

		    @Override
		    public void onError(final List<String> errors)
		    {

			// AlertDialog.Builder dialog = new
			// AlertDialog.Builder(DialogsActivity.this);
			// dialog.setMessage("get dialogs errors: " +
			// errors).create().show();
			runOnUiThread(new Runnable()
			{

			    @Override
			    public void run()
			    {

				showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, errors.toString());

			    }
			});
		    }
		});
		// else this is a new dialog
		// use this one to load only 1 new dialog

	    }
	});

    }

    public void showPopupMenuActionOthers(Context context, int ResourceID)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);

	// dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(ResourceID);

	// modified by Loi
	LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);

	container.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		dialog.dismiss();
	    }
	});

	LinearLayout container1 = (LinearLayout) dialog.findViewById(R.id.container1);
	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container1.getLayoutParams();
	params.topMargin = getSupportActionBar().getHeight();
	container1.setLayoutParams(params);
	// modified by Loi

	// set the custom dialog components - text, image and button
	LinearLayout btnWallpaper = (LinearLayout) dialog.findViewById(R.id.btnWallpaper);
	// if button is clicked, close the custom dialog
	btnWallpaper.setOnClickListener(new OnClickListener()
	{

	    @SuppressWarnings("static-access")
	    public void onClick(View v)
	    {

		// go to New Dialog activity
		Intent intent = new Intent(DialogsActivity.this, NewDialogActivity.class);
		startActivity(intent);

		dialog.dismiss();
	    }
	});

	LinearLayout btnCreateGroupChat = (LinearLayout) dialog.findViewById(R.id.btnCreateGroupChat);
	// if button is clicked, close the custom dialog
	btnCreateGroupChat.setOnClickListener(new OnClickListener()
	{

	    @SuppressWarnings("static-access")
	    public void onClick(View v)
	    {

		Intent intent = new Intent(DialogsActivity.this, Activity_Group_Chat_Step1.class);
		startActivity(intent);

		dialog.dismiss();

		/*Intent intent = new Intent(DialogsActivity.this, Activity_Group_Chat_Settings.class);
		intent.putExtra("ACTION", "CREATE_GROUP_CHAT");
		startActivity(intent);

		dialog.dismiss();*/
	    }
	});

	LinearLayout btnMainMenu = (LinearLayout) dialog.findViewById(R.id.btnShareFB);
	btnMainMenu.setOnClickListener(new OnClickListener()
	{

	    public void onClick(View v)
	    {

		dialog.dismiss();

	    }
	});

	dialog.show();

    }

    public void showPopupMenu(Context context, int ResourceID, final String dialogId)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);

	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(ResourceID);

	// modified by Loi
	LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);

	container.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		dialog.dismiss();
	    }
	});

	// set the custom dialog components - text, image and button
	LinearLayout btnWallpaper = (LinearLayout) dialog.findViewById(R.id.btnWallpaper);
	// if button is clicked, close the custom dialog
	btnWallpaper.setOnClickListener(new OnClickListener()
	{

	    @SuppressWarnings("static-access")
	    public void onClick(View v)
	    {

		showPopupConfirmDelete(DialogsActivity.this, R.layout.dialog_confirm_delete, dialogId);
		dialog.dismiss();

	    }
	});

	LinearLayout btnMainMenu = (LinearLayout) dialog.findViewById(R.id.btnShareFB);
	btnMainMenu.setOnClickListener(new OnClickListener()
	{

	    public void onClick(View v)
	    {

		dialog.dismiss();

	    }
	});

	dialog.show();

    }

    @Override
    public void onBackPressed()
    {

	/*if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Settings"))
	{
	    ApplicationSingleton.selectedDialogToCreateGroupChat = new ArrayList<QBDialog>();

	    Intent intent = new Intent(DialogsActivity.this, Activity_Group_Chat_Settings.class);
	    intent.putExtra("ACTION", "BACK_FROM_SELECT_DIALOG_CREATE");
	    startActivity(intent);
	    finish();

	}
	else*/
	{
	    finish();
	}
	super.onBackPressed();

    }

    @Override
    protected void onStop()
    {

	super.onStop();

	isOnCreate = false;

    }

    @Override
    protected void onDestroy()
    {

	// TODO Auto-generated method stub
	super.onDestroy();
	try
	{

	    if (activityReceiver != null)
		this.unregisterReceiver(activityReceiver);
	    activityReceiver = null;

	    // HelloMainService.Callback = null;
	}
	catch (Exception e)
	{

	}
    }

    @Override
    public void onLoginSuccess()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		progressBar.setVisibility(View.GONE);

		boolean isDownloaded = SharePrefsHelper.getIsDownloadedDialogListToSharePrefs(DialogsActivity.this);

		loadDialogs(isDownloaded);

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

		progressBar.setVisibility(View.GONE);
		showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, error);

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

		progressBar.setVisibility(View.GONE);
		showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, error);

	    }
	});

    }

    @Override
    public void onNewPrivateMessage(String dialogId, String message)
    {

	if (!isFinishing())
	{

	    // if (IS_LOADING_DIALOG == false)
	    // {
	    updateDialogOnNewChat(dialogId, message);
	    // }

	}

    }

    public void showPopupConfirmDelete(Context context, int ResourceID, final String dialogId)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);

	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(ResourceID);

	TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
	txtMessage.setText("Are you sure want to delete?");

	// modified by Loi
	LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);

	container.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		dialog.dismiss();
	    }
	});

	// set the custom dialog components - text, image and button
	LinearLayout btnWallpaper = (LinearLayout) dialog.findViewById(R.id.btnWallpaper);
	// if button is clicked, close the custom dialog
	btnWallpaper.setOnClickListener(new OnClickListener()
	{

	    @SuppressWarnings("static-access")
	    public void onClick(View v)
	    {

		// delete dialog

		for (int i = 0; i < dialogsList.size(); i++)
		{
		    QBDialog dl = dialogsList.get(i);
		    if (dl.getDialogId().equalsIgnoreCase(dialogId))
		    {
			Sugar_Dialog suDialog = StaticFunction.findDialogInDBByID(dialogId);
			if (suDialog != null)
			{
			    suDialog.delete();
			    dialogsList.remove(i);
			    adapter.notifyDataSetChanged();
			}
			else
			{
			    Toast.makeText(DialogsActivity.this, "Delete chat room fail. Please try again!", Toast.LENGTH_LONG).show();
			}
			break;
		    }
		}

		// updateListView(dialogsList, FROM, false);

		QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();

		groupChatManager.deleteDialog(dialogId, new QBEntityCallbackImpl<Void>()
		{

		    @Override
		    public void onSuccess()
		    {

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

		    }
		});
		dialog.dismiss();
	    }
	});

	dialog.show();

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

			// Intent intent = new Intent(acti,
			// Activity_FlashScreen.class);
			// acti.startActivity(intent);

			progressBar.setVisibility(View.VISIBLE);
			// resgister service
			HelloMainService.Callback = DialogsActivity.this;
			Intent intent = new Intent(DialogsActivity.this, HelloMainService.class);
			intent.addCategory(HelloMainService.TAG);
			startService(intent);

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

    @Override
    public void onConnectionClosedOnError()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, "         Connection Lost!         ");

	    }
	});

    }

    @Override
    public void onConnectedSuccess()
    {

	// TODO Auto-generated method stub

    }

    @Override
    public void onReconnecting()
    {

	// TODO Auto-generated method stub

    }

    @Override
    public void onConnectFail()
    {

	// TODO Auto-generated method stub

    }

    @Override
    public void onCreateSessionErrorNeedToResetAll()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(DialogsActivity.this, DialogsActivity.this, "Connect to server fail. Please try again!");

	    }
	});

    }

    private long findLastMessageTimeforDialog(String dialogID)
    {

	long lastMessTime = 0;

	String[] str = new String[1];
	str[0] = dialogID;
	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ?", str, "", "MESS_DATE_SENT DESC", "1");

	if (listsss.size() > 0)
	{
	    lastMessTime = listsss.get(0).messDateSent;
	}
	else
	{

	    // return 0;
	    Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialogID);
	    if (sDialog != null)
	    {

		lastMessTime = sDialog.dialogLastMessageDateSent * 1000;

	    }

	}

	return lastMessTime;

    }

    @Override
    public void onConnectionClosedDuetoUserLoginToOtherDevice()
    {

    }

    private void loadDialogProfileInfoForGroupChat(String dialogID)
    {

	loadDialogFromDB();

    }

}
