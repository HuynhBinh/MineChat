package com.es.hello.chat.ui.activities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.es.hello.chat.API;
import com.es.hello.chat.PlayServicesHelper;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.customobject.ObjectSearch4;
import com.es.hello.chat.customobject.Object_SearchExpandChild;
import com.es.hello.chat.customobject.Object_SearchExpandGroup;
import com.es.hello.chat.sugarobject.Sugar_User;
import com.es.hello.chat.ui.adapters.ExpandableListAdapter;
import com.es.hello.chat.ui.adapters.TrendAdapter;
import com.es.hello.chat.ui.fragments.UsersFragment;
import com.lat.hello.chat.R;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Activity_Search extends ActionBarActivity
{

    public int BackPressCount = 0;

    private DrawerLayout mDrawerLayout;

    // private LinearLayout mDrawerList;

    List<ObjectSearch4> listDataSearch = new ArrayList<ObjectSearch4>();

    List<String> listTrendTags = new ArrayList<String>();

    ExpandableListAdapter listAdapter;

    ExpandableListView expListView;

    List<Object_SearchExpandGroup> listObjectGroup;

    // private int listViewIndex;

    // private int listViewTop;

    ListView listivewTrendTags;

    Button btn1;

    Button btn2;

    Button btn3;

    Button btn4;

    private RelativeLayout progressBar;

    // private RelativeLayout progressBarRelative;

    TextView txtTrendingTittle;

    public PlayServicesHelper playServicesHelper;

    RelativeLayout leftLayout;

    LinearLayout contentLayout;

    RelativeLayout listBelowLayout;

    TrendAdapter adapter;

    LinearLayout btnMoveDown;

    private AsyncGetTop asyncGetTop;

    private AsyncAddTag asyncAddTag;

    private EditText edtTag1;

    private EditText edtTag2;

    private EditText edtTag3;

    private EditText edtTag4;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_search);

	setupUI(findViewById(R.id.drawer_layout));

	QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Search.this);

	playServicesHelper = new PlayServicesHelper(this, currentLoginUser.getLogin());

	listivewTrendTags = (ListView) findViewById(R.id.listviewBelowTrend);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "SEARCH", false);

	// getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	txtTrendingTittle = (TextView) findViewById(R.id.txtTittleTrending);
	txtTrendingTittle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 29);
	txtTrendingTittle.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(Activity_Search.this), Typeface.BOLD);

	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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

	leftLayout = (RelativeLayout) findViewById(R.id.left_drawer);
	leftLayout.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{

	    @Override
	    public void onFocusChange(View v, boolean hasFocus)
	    {

		if (hasFocus)
		{
		    hideKeyboard(v);
		}
	    }
	});

	listBelowLayout = (RelativeLayout) findViewById(R.id.expanListBelowContainer);
	listBelowLayout.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{

	    @Override
	    public void onFocusChange(View v, boolean hasFocus)
	    {

		if (hasFocus)
		{
		    hideKeyboard(v);
		}
	    }
	});

	contentLayout = (LinearLayout) findViewById(R.id.content_frame);
	contentLayout.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{

	    @Override
	    public void onFocusChange(View v, boolean hasFocus)
	    {

		if (hasFocus)
		{
		    hideKeyboard(v);
		}
	    }
	});

	expListView = (ExpandableListView) findViewById(R.id.expanListBelow);

	expListView.setOnFocusChangeListener(new View.OnFocusChangeListener()
	{

	    @Override
	    public void onFocusChange(View v, boolean hasFocus)
	    {

		if (hasFocus)
		{
		    hideKeyboard(v);
		}
	    }
	});

	Field mDragger;
	try
	{
	    mDragger = mDrawerLayout.getClass().getDeclaredField("mRightDragger"); // "mLeftDragger"
	    // for
	    // left
	    // ;
	    // "mRightDragger"
	    // for
	    // right
	    // drawer
	    // navigation

	    mDragger.setAccessible(true);
	    ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(mDrawerLayout);

	    Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
	    mEdgeSize.setAccessible(true);
	    int edge = mEdgeSize.getInt(draggerObj);

	    mEdgeSize.setInt(draggerObj, edge * 6);
	    //

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);
	// progressBarRelative = (RelativeLayout)
	// findViewById(R.id.progressBarRelative);
	progressBar.setVisibility(View.GONE);
	// progressBar.setVisibility(View.GONE);

	int numOfApp = SharePrefsHelper.getNumOfAppUsedForInstructionToSharePrefs(Activity_Search.this);

	if (numOfApp < 3)
	{

	    contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
	    {

		public void onGlobalLayout()
		{

		    contentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

		    int[] locations = new int[2];
		    listBelowLayout.getLocationOnScreen(locations);
		    int x = locations[0];
		    int y = locations[1];

		    showPopupInstruction(Activity_Search.this, R.layout.dialog_menu_instruction, y + 35);
		}
	    });

	}

	initTrendList(0);

	btn1 = (Button) findViewById(R.id.btnHello);
	btn1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	btn1.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));

	btn1.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		initTrendList(0);

	    }
	});

	btn2 = (Button) findViewById(R.id.btnTweeter);
	btn2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	btn2.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));

	btn2.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		initTrendList(1);

	    }
	});

	btn3 = (Button) findViewById(R.id.btnInstagram);
	btn3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	btn3.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));

	btn3.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		initTrendList(2);

	    }
	});

	btn4 = (Button) findViewById(R.id.btnMyFavou);
	btn4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	btn4.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));

	btn4.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		initTrendList(3);

	    }
	});

	edtTag1 = (EditText) findViewById(R.id.txtSearchTag1);
	edtTag2 = (EditText) findViewById(R.id.txtSearchTag2);
	edtTag3 = (EditText) findViewById(R.id.txtSearchTag3);
	edtTag4 = (EditText) findViewById(R.id.txtSearchTag4);

    }

    @Override
    public void onBackPressed()
    {

	BackPressCount++;
	if (BackPressCount == 2)
	{
	    BackPressCount = 0;
	    // java.lang.System.exit(0);
	    super.onBackPressed();
	}
	else
	{
	    Toast.makeText(Activity_Search.this, "press BACK 1 more time to close app!", Toast.LENGTH_LONG).show();
	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_search, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	int id = item.getItemId();

	if (id == android.R.id.home)
	{
	    // this.finish();
	    return true;
	}

	if (id == R.id.action_1)
	{

	    Intent intent = new Intent(Activity_Search.this, Activity_Setting.class);
	    startActivity(intent);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Search.class);
	    // finish();

	}

	if (id == R.id.action_2)
	{

	    Intent intent = new Intent(Activity_Search.this, DialogsActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    startActivity(intent);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Search.class);
	    // finish();

	}

	if (id == R.id.action_3)
	{

	    // progressBar.setVisibility(View.VISIBLE);

	    expListView.setEnabled(false);
	    expListView.setAlpha(0.3f);

	    String tag1 = edtTag1.getText().toString().trim();
	    String tag2 = edtTag2.getText().toString().trim();
	    String tag3 = edtTag3.getText().toString().trim();
	    String tag4 = edtTag4.getText().toString().trim();
	    if (tag1.length() > 0 || tag2.length() > 0 || tag3.length() > 0 || tag4.length() > 0)
	    {
		progressBar.setVisibility(View.VISIBLE);
		startProgress(progressBar);
	    }
	    else
	    {
		Toast.makeText(Activity_Search.this, "Enter at least one tag, please.", Toast.LENGTH_SHORT).show();
	    }

	}
	return super.onOptionsItemSelected(item);
    }

    private boolean isExistedUserInExpandGroup(List<Object_SearchExpandGroup> objGroups, QBUser qbUser)
    {

	int id = SharePrefsHelper.getCurrentLoginUserID(Activity_Search.this);
	Sugar_User sUser = StaticFunction.getUserFromDBByID(id);
	QBUser user = StaticFunction.SugarUserToQBUser(sUser);
	if (user.getId().equals(qbUser.getId()))
	{
	    return true;
	}

	for (Object_SearchExpandGroup object_SearchExpandGroup : objGroups)
	{
	    for (Object_SearchExpandChild objChild : object_SearchExpandGroup.ListChilds)
	    {
		if (objChild.LoginId.equals(qbUser.getLogin()))
		{
		    return true;
		}
	    }
	}

	return false;
    }

    private boolean isExistedUserHasTags(List<Object_SearchExpandGroup> objGroups, QBUser qbUser, boolean tag1, boolean tag2, boolean tag3, boolean tag4)
    {

	for (Object_SearchExpandGroup object_SearchExpandGroup : objGroups)
	{
	    if (object_SearchExpandGroup.IsTag1Found == tag1 && object_SearchExpandGroup.IsTag2Found == tag2 && object_SearchExpandGroup.IsTag3Found == tag3 && object_SearchExpandGroup.IsTag4Found == tag4)
	    {
		addUserToGroup(object_SearchExpandGroup, qbUser);
		return true;
	    }
	}

	addUserToNewGroup(objGroups, qbUser, tag1, tag2, tag3, tag4);
	return false;
    }

    private void addUserToNewGroup(List<Object_SearchExpandGroup> objGroups, QBUser qbUser, boolean tag1, boolean tag2, boolean tag3, boolean tag4)
    {

	Object_SearchExpandGroup objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = tag1;
	objGroup.IsTag2Found = tag2;
	objGroup.IsTag3Found = tag3;
	objGroup.IsTag4Found = tag4;
	objGroup.NumOfUserFound = 1;

	Object_SearchExpandChild objChild = new Object_SearchExpandChild();
	objChild.Name = qbUser.getFullName();
	objChild.Status = qbUser.getLogin();
	objChild.HowFar = 10;
	objChild.LoginId = qbUser.getLogin();
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
    }

    private void addUserToGroup(Object_SearchExpandGroup object_SearchExpandGroup, QBUser qbUser)
    {

	object_SearchExpandGroup.NumOfUserFound = object_SearchExpandGroup.NumOfUserFound + 1;

	Object_SearchExpandChild objChild = new Object_SearchExpandChild();
	objChild.Name = qbUser.getFullName();
	objChild.Status = qbUser.getLogin();
	objChild.HowFar = 10;
	objChild.LoginId = qbUser.getLogin();
	object_SearchExpandGroup.ListChilds.add(objChild);
    }

    public void startProgress(View view)
    {

	if (listObjectGroup != null)
	{
	    listObjectGroup.clear();
	}
	else
	{
	    listObjectGroup = new ArrayList<Object_SearchExpandGroup>();
	}

	final String tag1 = edtTag1.getText().toString().trim().toLowerCase();
	final String tag2 = edtTag2.getText().toString().trim().toLowerCase();
	final String tag3 = edtTag3.getText().toString().trim().toLowerCase();
	final String tag4 = edtTag4.getText().toString().trim().toLowerCase();

	StringifyArrayList<String> listTag1 = new StringifyArrayList<String>();
	listTag1.add(tag1);
	StringifyArrayList<String> listTag2 = new StringifyArrayList<String>();
	listTag2.add(tag2);
	StringifyArrayList<String> listTag3 = new StringifyArrayList<String>();
	listTag3.add(tag3);
	StringifyArrayList<String> listTag4 = new StringifyArrayList<String>();
	listTag4.add(tag4);

	if (tag1.length() > 0)
	{
	    QBUsers.getUsersByTags(listTag1, null, new QBEntityCallback<ArrayList<QBUser>>()
	    {

		@Override
		public void onSuccess(ArrayList<QBUser> arg0, Bundle arg1)
		{

		    for (QBUser qbUser : arg0)
		    {
			if (!isExistedUserInExpandGroup(listObjectGroup, qbUser))
			{
			    boolean btag1 = false;
			    boolean btag2 = false;
			    boolean btag3 = false;
			    boolean btag4 = false;

			    for (String string : qbUser.getTags())
			    {
				string = string.toLowerCase();
				if (tag1.equals(string))
				{
				    btag1 = true;
				}
				if (tag2.equals(string))
				{
				    btag2 = true;
				}
				if (tag3.equals(string))
				{
				    btag3 = true;
				}
				if (tag4.equals(string))
				{
				    btag4 = true;
				}
			    }

			    isExistedUserHasTags(listObjectGroup, qbUser, btag1, btag2, btag3, btag4);
			}
		    }

		    progressBar.setVisibility(View.GONE);

		    expListView.setEnabled(true);
		    expListView.setAlpha(1f);
		    initExpandList();
		}

		@Override
		public void onSuccess()
		{

		}

		@Override
		public void onError(List<String> arg0)
		{

		}
	    });
	}

	if (tag2.length() > 0)
	{
	    QBUsers.getUsersByTags(listTag2, null, new QBEntityCallback<ArrayList<QBUser>>()
	    {

		@Override
		public void onSuccess(ArrayList<QBUser> arg0, Bundle arg1)
		{

		    for (QBUser qbUser : arg0)
		    {
			if (!isExistedUserInExpandGroup(listObjectGroup, qbUser))
			{
			    boolean btag1 = false;
			    boolean btag2 = false;
			    boolean btag3 = false;
			    boolean btag4 = false;

			    for (String string : qbUser.getTags())
			    {
				string = string.toLowerCase();
				if (tag1.equals(string))
				{
				    btag1 = true;
				}
				if (tag2.equals(string))
				{
				    btag2 = true;
				}
				if (tag3.equals(string))
				{
				    btag3 = true;
				}
				if (tag4.equals(string))
				{
				    btag4 = true;
				}
			    }

			    isExistedUserHasTags(listObjectGroup, qbUser, btag1, btag2, btag3, btag4);
			}
		    }
		    progressBar.setVisibility(View.GONE);

		    expListView.setEnabled(true);
		    expListView.setAlpha(1f);
		    initExpandList();
		}

		@Override
		public void onSuccess()
		{

		}

		@Override
		public void onError(List<String> arg0)
		{

		}
	    });
	}

	if (tag3.length() > 0)
	{
	    QBUsers.getUsersByTags(listTag3, null, new QBEntityCallback<ArrayList<QBUser>>()
	    {

		@Override
		public void onSuccess(ArrayList<QBUser> arg0, Bundle arg1)
		{

		    for (QBUser qbUser : arg0)
		    {
			if (!isExistedUserInExpandGroup(listObjectGroup, qbUser))
			{
			    boolean btag1 = false;
			    boolean btag2 = false;
			    boolean btag3 = false;
			    boolean btag4 = false;

			    for (String string : qbUser.getTags())
			    {
				string = string.toLowerCase();
				if (tag1.equals(string))
				{
				    btag1 = true;
				}
				if (tag2.equals(string))
				{
				    btag2 = true;
				}
				if (tag3.equals(string))
				{
				    btag3 = true;
				}
				if (tag4.equals(string))
				{
				    btag4 = true;
				}
			    }

			    isExistedUserHasTags(listObjectGroup, qbUser, btag1, btag2, btag3, btag4);
			}
		    }
		    progressBar.setVisibility(View.GONE);

		    expListView.setEnabled(true);
		    expListView.setAlpha(1f);
		    initExpandList();
		}

		@Override
		public void onSuccess()
		{

		}

		@Override
		public void onError(List<String> arg0)
		{

		}
	    });
	}

	if (tag4.length() > 0)
	{
	    QBUsers.getUsersByTags(listTag4, null, new QBEntityCallback<ArrayList<QBUser>>()
	    {

		@Override
		public void onSuccess(ArrayList<QBUser> arg0, Bundle arg1)
		{

		    for (QBUser qbUser : arg0)
		    {
			if (!isExistedUserInExpandGroup(listObjectGroup, qbUser))
			{
			    boolean btag1 = false;
			    boolean btag2 = false;
			    boolean btag3 = false;
			    boolean btag4 = false;

			    for (String string : qbUser.getTags())
			    {
				string = string.toLowerCase();
				if (tag1.equals(string))
				{
				    btag1 = true;
				}
				if (tag2.equals(string))
				{
				    btag2 = true;
				}
				if (tag3.equals(string))
				{
				    btag3 = true;
				}
				if (tag4.equals(string))
				{
				    btag4 = true;
				}
			    }

			    isExistedUserHasTags(listObjectGroup, qbUser, btag1, btag2, btag3, btag4);
			}
		    }
		    progressBar.setVisibility(View.GONE);

		    expListView.setEnabled(true);
		    expListView.setAlpha(1f);
		    initExpandList();
		}

		@Override
		public void onSuccess()
		{

		}

		@Override
		public void onError(List<String> arg0)
		{

		}
	    });
	}

	int id = SharePrefsHelper.getCurrentLoginUserID(Activity_Search.this);
	Sugar_User sUser = StaticFunction.getUserFromDBByID(id);
	QBUser user = StaticFunction.SugarUserToQBUser(sUser);

	// QBUser user = SharePrefsHelper
	// .getCurrentLoginUser(Activity_Search.this);
	// user.setId(id);
	StringifyArrayList<String> listTags = new StringifyArrayList<String>();
	if (tag1.length() > 0)
	{
	    listTags.add(tag1);
	}
	if (tag2.length() > 0)
	{
	    listTags.add(tag2);
	}
	if (tag3.length() > 0)
	{
	    listTags.add(tag3);
	}
	if (tag4.length() > 0)
	{
	    listTags.add(tag4);
	}

	user.setTags(listTags);

	QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
	{

	    @Override
	    public void onSuccess(QBUser arg0, Bundle arg1)
	    {

		Log.e("update tag", "onSuccess 1");

	    }

	    @Override
	    public void onSuccess()
	    {

		Log.e("update tag", "onSuccess 2");

	    }

	    @Override
	    public void onError(List<String> arg0)
	    {

		Log.e("update tag", "onError");

	    }
	});

	// update tag in host
	String tag = "";
	if (tag1.length() > 0)
	{
	    tag = tag + tag1 + "///";
	}
	if (tag2.length() > 0)
	{
	    tag = tag + tag2 + "///";
	}
	if (tag3.length() > 0)
	{
	    tag = tag + tag3 + "///";
	}
	if (tag4.length() > 0)
	{
	    tag = tag + tag4 + "///";
	}

	if (tag.endsWith("///"))
	{
	    tag = tag.substring(0, tag.length() - 3);
	    if (tag.length() > 0)
	    {
		asyncAddTag = new AsyncAddTag();
		asyncAddTag.execute(tag);
	    }
	}
    }

    private void doFakeWork()
    {

	try
	{
	    Thread.sleep(5000);

	}
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    private List<Object_SearchExpandGroup> getDummyData()
    {

	List<Object_SearchExpandGroup> objGroups = new ArrayList<Object_SearchExpandGroup>();

	Object_SearchExpandGroup objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = true;
	objGroup.NumOfUserFound = 5;

	Object_SearchExpandChild objChild = new Object_SearchExpandChild();
	objChild.Name = "Allice88";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "HippieDude12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Catagaya123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Sinbadtwo2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Lazadacocacola";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 7;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Allice88";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "HippieDude12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Catagaya123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Sinbadtwo2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Lazadacocacola";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "pepsi123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "cocojambo00";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 8;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Loi18";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Dakao12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Catagaya123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Sinbadtwo2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Madacata";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "pepsi123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "cocojambo00";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Hehehe00";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 4;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Loi18";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Dakao12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Catagaya123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Sinbadtwo2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 5;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Shark1234";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Spiderman12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Hulk123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Ironman2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Captain2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 3;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Hulk123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Ironman2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Captain2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 5;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Loki4";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Superman12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Hulk123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Ironman2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Captain2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 5;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Shark1234";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Dilolphin12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Acacdfd123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Suleman2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Captain2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================
	objGroup = new Object_SearchExpandGroup();
	objGroup.IsTag1Found = true;
	objGroup.IsTag2Found = true;
	objGroup.IsTag3Found = true;
	objGroup.IsTag4Found = false;
	objGroup.NumOfUserFound = 5;

	objChild = new Object_SearchExpandChild();
	objChild.Name = "Chelsea1234";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Spiderman12";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Manchester123";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Ironman2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);
	objChild = new Object_SearchExpandChild();
	objChild.Name = "Suno2";
	objChild.Status = "Looking for festival buddies!!";
	objChild.HowFar = 10;
	objGroup.ListChilds.add(objChild);

	objGroups.add(objGroup);
	// ===============================================

	return objGroups;

    }

    private void initTrendList(int type)
    {

	asyncGetTop = new AsyncGetTop();
	asyncGetTop.execute();

	// listTrendTags = new ArrayList<String>();
	//
	// String typee = "";
	//
	// if (type == 0) {
	// typee = "#ello trending";
	// } else if (type == 1) {
	// typee = "twitter trending";
	// } else if (type == 2) {
	// typee = "instagram trending";
	// } else if (type == 3) {
	// typee = "favourite trending";
	// }
	//
	// listivewTrendTags = (ListView) findViewById(R.id.listviewBelowTrend);
	//
	// listTrendTags.add("1. " + typee);
	// listTrendTags.add("2. " + typee);
	// listTrendTags.add("3. " + typee);
	// listTrendTags.add("4. " + typee);
	// listTrendTags.add("5. " + typee);
	// listTrendTags.add("6. " + typee);
	// listTrendTags.add("7. " + typee);
	// listTrendTags.add("8. " + typee);
	// listTrendTags.add("9. " + typee);
	// listTrendTags.add("10. " + typee);
	// listTrendTags.add("11. " + typee);
	// listTrendTags.add("12. " + typee);
	// listTrendTags.add("13. " + typee);
	// listTrendTags.add("14. " + typee);
	// listTrendTags.add("15. " + typee);
	// listTrendTags.add("16. " + typee);
	//
	// adapter = new TrendAdapter(listTrendTags, Activity_Search.this);
	//
	// listivewTrendTags.setAdapter(adapter);
	//
	// if (adapter.getCount() > 0) {
	// btnMoveDown.setVisibility(View.VISIBLE);
	// }

    }

    private class AsyncGetTop extends AsyncTask<String, Void, String>
    {

	private String result;

	@Override
	protected String doInBackground(String... params)
	{

	    API api = new API();
	    result = api.getTop();

	    return result;
	}

	@Override
	protected void onPostExecute(String json)
	{

	    super.onPostExecute(json);

	    if (json.equals("nointernet"))
	    {

	    }
	    else
	    {
		if (listTrendTags == null)
		{
		    listTrendTags = new ArrayList<String>();
		}
		else
		{
		    listTrendTags.clear();
		}

		try
		{
		    JSONArray arrayData = new JSONArray(json);
		    for (int i = 0; i < arrayData.length(); i++)
		    {
			JSONObject objData = arrayData.getJSONObject(i);
			String tag = objData.optString("Tag");

			listTrendTags.add(tag);
		    }
		}
		catch (JSONException e)
		{

		}

		adapter = new TrendAdapter(listTrendTags, Activity_Search.this);

		listivewTrendTags.setAdapter(adapter);

		if (adapter.getCount() > 0)
		{
		    btnMoveDown.setVisibility(View.VISIBLE);
		}
	    }
	}
    }

    private class AsyncAddTag extends AsyncTask<String, Void, String>
    {

	private String result;

	@Override
	protected String doInBackground(String... params)
	{

	    API api = new API();
	    result = api.addTag(params[0]);

	    return result;
	}

	@Override
	protected void onPostExecute(String json)
	{

	    super.onPostExecute(json);

	    if (json.equals("nointernet"))
	    {

	    }
	    else
	    {
		if (listTrendTags == null)
		{
		    listTrendTags = new ArrayList<String>();
		}
		else
		{
		    listTrendTags.clear();
		}

		try
		{
		    JSONArray arrayData = new JSONArray(json);
		    for (int i = 0; i < arrayData.length(); i++)
		    {
			JSONObject objData = arrayData.getJSONObject(i);
			String tag = objData.optString("Tag");

			listTrendTags.add(tag);
		    }
		}
		catch (JSONException e)
		{

		}

		adapter = new TrendAdapter(listTrendTags, Activity_Search.this);

		listivewTrendTags.setAdapter(adapter);

		if (adapter.getCount() > 0)
		{
		    btnMoveDown.setVisibility(View.VISIBLE);
		}
	    }
	}
    }

    private void initExpandList()
    {

	// sort list
	for (int i = 0; i < listObjectGroup.size(); i++)
	{
	    for (int j = i + 1; j < listObjectGroup.size(); j++)
	    {
		Object_SearchExpandGroup group1 = listObjectGroup.get(i);
		Object_SearchExpandGroup group2 = listObjectGroup.get(j);
		int n1 = 0;
		int n2 = 0;

		if (group1.IsTag1Found)
		    n1++;
		if (group1.IsTag2Found)
		    n1++;
		if (group1.IsTag3Found)
		    n1++;
		if (group1.IsTag4Found)
		    n1++;

		if (group2.IsTag1Found)
		    n2++;
		if (group2.IsTag2Found)
		    n2++;
		if (group2.IsTag3Found)
		    n2++;
		if (group2.IsTag4Found)
		    n2++;
		if (n1 < n2)
		{
		    // swap
		    Collections.swap(listObjectGroup, i, j);
		}
	    }
	}

	// listObjectGroup = getDummyData();
	// listObjectGroup = list;
	listAdapter = new ExpandableListAdapter(this, listObjectGroup);
	expListView.setAdapter(listAdapter);
	getSupportActionBar().setTitle("MATCHES");
	expListView.setOnChildClickListener(new OnChildClickListener()
	{

	    @Override
	    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	    {

		Intent intent = new Intent(Activity_Search.this, Activity_Opponent_Profile.class);
		intent.putExtra("IsTag1Found", listObjectGroup.get(groupPosition).IsTag1Found);
		intent.putExtra("IsTag2Found", listObjectGroup.get(groupPosition).IsTag2Found);
		intent.putExtra("IsTag3Found", listObjectGroup.get(groupPosition).IsTag3Found);
		intent.putExtra("IsTag4Found", listObjectGroup.get(groupPosition).IsTag4Found);
		startActivity(intent);
		// ApplicationSingleton.queueActivities.enqueue(Activity_Search.class);
		return false;
	    }
	});

	// listSearch4.onRefreshComplete();
    }

    public void showPopupInstruction(Context context, int ResourceID, int y)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);

	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialog.setCanceledOnTouchOutside(false);
	dialog.setContentView(ResourceID);

	final ImageView imgView = (ImageView) dialog.findViewById(R.id.imgInstruction);

	imgView.setOnTouchListener(new OnTouchListener()
	{

	    @Override
	    public boolean onTouch(View v, MotionEvent event)
	    {

		int width = imgView.getWidth();
		// int height = imgView.getHeight();

		float x = event.getX();
		float y = event.getY();

		if (x >= (width - 96) && y <= (96))
		{
		    dialog.dismiss();

		    int numOfApp = SharePrefsHelper.getNumOfAppUsedForInstructionToSharePrefs(Activity_Search.this);

		    SharePrefsHelper.saveNumOfAppUsedForInstructionToSharePrefs(numOfApp + 1, Activity_Search.this);
		}

		return false;
	    }
	});

	WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
	wmlp.gravity = Gravity.TOP;
	wmlp.y = y;

	dialog.show();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {

	super.onWindowFocusChanged(hasFocus);
	if (hasFocus)
	{
	    int top = listBelowLayout.getTop();
	}
    }

    public void hideKeyboard(View view)
    {

	InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // setup hide keyboard when touch outside
    public void setupUI(View view)
    {

	// Set up touch listener for non-text box views to hide keyboard.
	if (!(view instanceof EditText))
	{

	    view.setOnTouchListener(new OnTouchListener()
	    {

		public boolean onTouch(View v, MotionEvent event)
		{

		    hideSoftKeyboard(Activity_Search.this);
		    getWindow().getDecorView().clearFocus();
		    return false;
		}

	    });
	}

	// If a layout container, iterate over children and seed recursion.
	if (view instanceof ViewGroup)
	{

	    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	    {

		View innerView = ((ViewGroup) view).getChildAt(i);

		setupUI(innerView);
	    }
	}
    }

    public static void hideSoftKeyboard(Activity activity)
    {

	InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void scrollListViewToBottom()
    {

	if (listivewTrendTags != null)
	{
	    listivewTrendTags.post(new Runnable()
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
			    View mView = adapter.getView(i, null, listivewTrendTags);

			    mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

			    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			    totalHeight += mView.getMeasuredHeight();

			}

			listivewTrendTags.smoothScrollBy(totalHeight, totalHeight * 10);

		    }

		}
	    });

	}

    }

    private void StopScrollListView()
    {

	if (listivewTrendTags != null)
	{
	    listivewTrendTags.post(new Runnable()
	    {

		@Override
		public void run()
		{

		    listivewTrendTags.smoothScrollBy(0, 0);

		}
	    });
	}

    }

}
