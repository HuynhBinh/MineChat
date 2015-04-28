package com.es.hello.chat.ui.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.sugarobject.Sugar_Message;
import com.es.hello.chat.view.TouchImageView;
import com.es.hello.chat.view.TouchImageView.OnTouchImageViewListener;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orm.query.Condition;
import com.orm.query.Select;

public class Activity_View_Attachment_With_Slide extends Activity
{

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    public ArrayList<String> listAttachImagePath = new ArrayList<String>();

    public static String DialogID = "";

    public static String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_view_attachment_with_slide);

	StaticFunction.initImageLoader(Activity_View_Attachment_With_Slide.this);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	// listAttachImagePath.add("https://qbprod.s3.amazonaws.com/c5f5b6e6ea4242c7a06be3183a250eb100");
	// listAttachImagePath.add("https://qbprod.s3.amazonaws.com/c5c76a0aad864236be41aafdb1bd552f00");
	// listAttachImagePath.add("https://qbprod.s3.amazonaws.com/31a0d6d14c3d47dc92458183e3c0b6e700");
	// listAttachImagePath.add("https://qbprod.s3.amazonaws.com/711b3d3162b14c96bca34e534238cc3900");

	listAttachImagePath = getAllAttachImageByDialogId();

	int pos = listAttachImagePath.indexOf(imgPath);

	ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
	ImagePagerAdapter adapter = new ImagePagerAdapter();
	viewPager.setAdapter(adapter);
	viewPager.setCurrentItem(pos);

    }

    private ArrayList<String> getAllAttachImageByDialogId()
    {

	ArrayList<String> listAttachImages = new ArrayList<String>();

	List<Sugar_Message> listMessages = new ArrayList<Sugar_Message>();
	String dialogID = DialogID;

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

	for (int i = 0; i < listMessages.size(); i++)
	{
	    if (listMessages.get(i).messAttachment != null)
	    {
		if (!listMessages.get(i).messAttachment.equalsIgnoreCase(""))
		{
		    listAttachImages.add(listMessages.get(i).messAttachment);
		}
	    }

	}

	return listAttachImages;

    }

    private class ImagePagerAdapter extends PagerAdapter
    {

	/*private int[] mImages = new int[]
	{
	R.drawable.sampleava1, R.drawable.sampleava2, R.drawable.sampleava3, R.drawable.sampleava4
	};*/

	@Override
	public int getCount()
	{

	    return listAttachImagePath.size();
	    // return mImages.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{

	    return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{

	    Context context = Activity_View_Attachment_With_Slide.this;
	    final TouchImageView imageView = new TouchImageView(context);
	    // int padding =
	    // context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
	    // imageView.setPadding(padding, padding, padding, padding);
	    // imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

	    imageLoader.displayImage(listAttachImagePath.get(position), imageView, options);

	    imageView.setOnTouchImageViewListener(new OnTouchImageViewListener()
	    {

		@Override
		public void onMove()
		{

		    // PointF point = imageView.getScrollPosition();
		    // RectF rect = imageView.getZoomedRect();
		    // float currentZoom = imageView.getCurrentZoom();
		    // boolean isZoomed = imageView.isZoomed();
		}
	    });

	    // imageView.setImageResource(mImages[position]);
	    ((ViewPager) container).addView(imageView, 0);
	    return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{

	    ((ViewPager) container).removeView((ImageView) object);
	}
    }
}
