package com.es.hello.chat.ui.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.es.hello.chat.ui.fragments.UsersFragment;
import com.lat.hello.chat.R;

public class NewDialogActivity extends FragmentActivity
{

    private SectionsPagerAdapter sectionsPagerAdapter;

    private ViewPager viewPager;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	this.setTheme(android.R.style.Theme_Holo_Light);
	final android.app.ActionBar actionBar = getActionBar();
	// actionBar.setTitle(Html.fromHtml("<font color=\"black\">" + "Users" +
	// "</font>"));
	actionBar.setTitle("Users");
	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef8ec")));
	actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

	setContentView(R.layout.activity_main);

	// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	List<Fragment> tabs = new ArrayList<Fragment>();
	tabs.add(UsersFragment.getInstance());

	sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabs);

	viewPager = (ViewPager) findViewById(R.id.pager);
	viewPager.setAdapter(sectionsPagerAdapter);

	viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
	{

	    @Override
	    public void onPageSelected(int position)
	    {

		actionBar.setSelectedNavigationItem(position);
	    }
	});
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter
    {

	private List<Fragment> fragments;

	public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments)
	{

	    super(fm);
	    this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position)
	{

	    return fragments.get(position);
	}

	@Override
	public int getCount()
	{

	    return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position)
	{

	    return "Users";
	}
    }

    @Override
    public void onBackPressed()
    {

	Intent intent = new Intent(NewDialogActivity.this, DialogsActivity.class);
	startActivity(intent);
	finish();
	super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_new_dialog, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

	int id = item.getItemId();
	if (id == R.id.action_create_chat)
	{
	    return false;
	}
	return super.onOptionsItemSelected(item);
    }
}
