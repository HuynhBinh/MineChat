package com.es.hello.chat.ui.adapters;

import com.es.hello.chat.ui.fragments.MyFragment;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ImageViewFragmentAdapter extends FragmentPagerAdapter
{

    Context context;

    private int drawable1;
    private int drawable2;
    private int drawable3;
    private int drawable4;

    public ImageViewFragmentAdapter(FragmentManager fragmentManager, Context context, int drawable1, int drawable2, int drawable3, int drawable4)
    {

	super(fragmentManager);
	this.context = context;
	this.drawable1 = drawable1;
	this.drawable2 = drawable2;
	this.drawable3 = drawable3;
	this.drawable4 = drawable4;
    }

    @Override
    public Fragment getItem(int i)
    {

	switch (i)
	{
	    case 0:
		return new MyFragment(drawable1);
	    case 1:
		return new MyFragment(drawable2);
	    case 2:
		return new MyFragment(drawable3);
	    default:
		return new MyFragment(drawable4);
	}
    }

    @Override
    public int getCount()
    {

	return 4;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {

	switch (position)
	{
	    case 0:
		return "";
	    case 1:
		return "";
	    case 2:
		return "";
	    default:
		return "";
	}
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {

	super.destroyItem(container, position, object);
    }

    @Override
    public void finishUpdate(ViewGroup container)
    {

	super.finishUpdate(container);
    }

    @Override
    public long getItemId(int position)
    {

	return super.getItemId(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {

	return super.instantiateItem(container, position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {

	return super.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {

	super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState()
    {

	return super.saveState();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {

	super.setPrimaryItem(container, position, object);
    }

    @Override
    public void startUpdate(ViewGroup container)
    {

	super.startUpdate(container);
    }

}
