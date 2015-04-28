package com.es.hello.chat.ui.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.ui.adapters.ImageViewFragmentAdapter;
import com.lat.hello.chat.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;
import com.viewpagerindicator.CirclePageIndicator;

public class Activity_Opponent_Profile extends ActionBarActivity
{

    TextView txtName;

    TextView txtGender;

    TextView txtCountry;

    TextView txtStatus;

    TextView txtStatus1;

    private ViewPager pager;

    private CirclePageIndicator indicator;

    private ImageViewFragmentAdapter adapter;

    private EditText Tag1, Tag2, Tag3, Tag4;

    FrameLayout frameLayout;

    Toolbar toolbar;

    LinearLayout layoutTextStatus;

    LinearLayout layoutTextStatus1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_opponent_profile);

	toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "PROFILE", true);

	/*getActionBar().setTitle("PROFILE");
	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	getActionBar().setDisplayHomeAsUpEnabled(true);
	getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));*/

	// FontTypeUtils.setFontForTittleBar(this, this);

	pager = (ViewPager) findViewById(R.id.pager);
	indicator = (CirclePageIndicator) findViewById(R.id.indicator);

	adapter = new ImageViewFragmentAdapter(getSupportFragmentManager(), this, R.drawable.sampleava2, R.drawable.sampleava3, R.drawable.sampleava4, R.drawable.sampleava5);
	pager.setAdapter(adapter);
	indicator.setViewPager(pager);
	pager.setCurrentItem(0);

	initView();

    }

    @Override
    public void onBackPressed()
    {

	// Intent inttent = new Intent(Activity_Opponent_Profile.this,
	// Activity_Search.class);
	// inttent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	// ApplicationSingleton.queueActivities.enqueue(Activity_Opponent_Profile.class);
	// startActivity(inttent);
	finish();
	super.onBackPressed();

    }

    private void initView()
    {

	txtName = (TextView) findViewById(R.id.txtProfileName);
	txtGender = (TextView) findViewById(R.id.txtProfileAgeGender);
	txtCountry = (TextView) findViewById(R.id.txtProfileCountry);
	txtStatus = (TextView) findViewById(R.id.txtProfileStatus);
	txtStatus1 = (TextView) findViewById(R.id.txtProfileStatus1);

	txtName.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(getApplicationContext()), Typeface.BOLD);
	txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);

	txtGender.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));
	txtGender.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
	txtCountry.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(getApplicationContext()));
	txtCountry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

	txtStatus.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(getApplicationContext()), Typeface.BOLD);
	txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	txtStatus.setTextColor(Color.parseColor("#666666"));

	txtStatus1.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(getApplicationContext()), Typeface.BOLD);
	txtStatus1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	txtStatus1.setTextColor(Color.parseColor("#666666"));

	frameLayout = (FrameLayout) findViewById(R.id.frame);
	layoutTextStatus = (LinearLayout) findViewById(R.id.layoutTextStatus);
	layoutTextStatus1 = (LinearLayout) findViewById(R.id.layoutTextStatus1);

	//
	Tag1 = (EditText) findViewById(R.id.txtTag1);
	Tag2 = (EditText) findViewById(R.id.txtTag2);
	Tag3 = (EditText) findViewById(R.id.txtTag3);
	Tag4 = (EditText) findViewById(R.id.txtTag4);

	if (getIntent().getExtras() != null)
	{
	    if (!getIntent().getExtras().getBoolean("IsTag1Found", false))
	    {
		Tag1.setBackgroundColor(Color.WHITE);
		Tag1.setTextColor(Color.parseColor("#dcdcdc"));
	    }
	    if (!getIntent().getExtras().getBoolean("IsTag2Found", false))
	    {
		Tag2.setBackgroundColor(Color.WHITE);
		Tag2.setTextColor(Color.parseColor("#dcdcdc"));
	    }
	    if (!getIntent().getExtras().getBoolean("IsTag3Found", false))
	    {
		Tag3.setBackgroundColor(Color.WHITE);
		Tag3.setTextColor(Color.parseColor("#dcdcdc"));
	    }
	    if (!getIntent().getExtras().getBoolean("IsTag4Found", false))
	    {
		Tag4.setBackgroundColor(Color.WHITE);
		Tag4.setTextColor(Color.parseColor("#dcdcdc"));
	    }
	}

	frameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	int fheight = frameLayout.getMeasuredHeight();

	toolbar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	int theight = toolbar.getMeasuredHeight();

	int sHeight = getStatusBarHeight();

	int bHeight = getSoftbuttonsbarHeight();

	int sumHeight = fheight + theight + sHeight;

	Display display = getWindowManager().getDefaultDisplay();
	Point size = new Point();
	display.getSize(size);
	int screenHeight = size.y;

	int diff = screenHeight - sumHeight;

	int atlestHeight = 56;

	if (diff > 0)
	{
	    if (diff > atlestHeight)
	    {
		layoutTextStatus1.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		layoutTextStatus.setVisibility(View.VISIBLE);
	    }
	}
	else
	{
	    layoutTextStatus.setVisibility(View.VISIBLE);
	}

    }

    public int getStatusBarHeight()
    {

	int result = 0;
	int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	if (resourceId > 0)
	{
	    result = getResources().getDimensionPixelSize(resourceId);
	}
	return result;
    }

    @SuppressLint("NewApi")
    private int getSoftbuttonsbarHeight()
    {

	// getRealMetrics is only available with API 17 and +
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
	{
	    DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    int usableHeight = metrics.heightPixels;
	    getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
	    int realHeight = metrics.heightPixels;
	    if (realHeight > usableHeight)
		return realHeight - usableHeight;
	    else
		return 0;
	}
	return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_opponent_profile, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	int id = item.getItemId();

	if (id == android.R.id.home)
	{
	    this.finish();
	    return true;
	}

	if (id == R.id.action_1)
	{

	    // go to Search screen
	    Intent intent = new Intent(Activity_Opponent_Profile.this, Activity_Search.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Opponent_Profile.class);
	    startActivity(intent);

	    // finish();

	}

	if (id == R.id.action_2)
	{

	    // go to Dialogs screen
	    Intent intent = new Intent(Activity_Opponent_Profile.this, DialogsActivity.class);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Opponent_Profile.class);
	    startActivity(intent);
	    // finish();

	}

	if (id == R.id.action_3)
	{
	    QBUser user = new QBUser();
	    user.setId(2174474);
	    user.setFullName("user7");
	    user.setLogin("user7");
	    user.setWebsite(null);
	    user.setLastRequestAt(new Date());

	    List<QBUser> listUsers = new ArrayList<QBUser>();
	    listUsers.add(user);

	    // ApplicationSingleton.addDialogsUsers(listUsers);

	    // Create new group dialog
	    QBDialog dialogToCreate = new QBDialog();
	    dialogToCreate.setName("user7");

	    dialogToCreate.setType(QBDialogType.PRIVATE);

	    ArrayList<Integer> listID = new ArrayList<Integer>();
	    listID.add(2174474);

	    dialogToCreate.setOccupantsIds(listID);

	    QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>()
	    {

		@Override
		public void onSuccess(QBDialog dialog, Bundle args)
		{

		    startSingleChat(dialog);

		}

		@Override
		public void onError(List<String> errors)
		{

		}
	    });

	}

	return super.onOptionsItemSelected(item);
    }

    public void startSingleChat(QBDialog dialog)
    {

	Bundle bundle = new Bundle();
	bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
	bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

	ChatActivity.reIntentClass = Activity_Opponent_Profile.class;
	ChatActivity.start(Activity_Opponent_Profile.this, bundle);
    }

}
