package com.es.hello.chat.ui.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.view.TouchImageView;
import com.es.hello.chat.view.TouchImageView.OnTouchImageViewListener;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_View_Attachment extends ActionBarActivity
{

    TouchImageView imgView;

    LinearLayout linear;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    public static String imgPath = "";

    ImageView btnEdit;

    ImageView btnOk;

    public static String FILE_PATH = "";

    public static String FILE_NAME = "";

    public static String Display_Path = "";

    // public static String DialogID = "";

    LinearLayout btnBack;

    private MyAsyntask myAsyntask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	// getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_img_view);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "GROUP PHOTO", true);

	btnBack = (LinearLayout) findViewById(R.id.btnBack);
	btnBack.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		onBackPressed();

	    }
	});

	initControl();

	imageLoader.displayImage(imgPath, imgView, options);

    }

    int optionMenuState = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_dialog_update_group_photo, menu);

	if (optionMenuState == 0)
	{
	    menu.findItem(R.id.action_1).setVisible(false);
	}
	else
	{
	    menu.findItem(R.id.action_1).setVisible(true);
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

	    Intent returnIntent = new Intent();
	    setResult(RESULT_OK, returnIntent);
	    finish();

	}

	if (id == R.id.action_2)
	{

	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("image/*");
	    startActivityForResult(intent, 0);

	}

	return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    public void initControl()
    {

	imgView = (TouchImageView) findViewById(R.id.imgView);
	linear = (LinearLayout) findViewById(R.id.linear_img_view);
	btnEdit = (ImageView) findViewById(R.id.btnEdit);
	btnOk = (ImageView) findViewById(R.id.btnOk);
	btnOk.setVisibility(View.GONE);

	// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
	// linear.getLayoutParams();
	// params.width = getScreenWidth(Activity_View_Attachment.this);
	// params.height = getScreenHeight(Activity_View_Attachment.this);
	// linear.setLayoutParams(params);

	StaticFunction.initImageLoader(Activity_View_Attachment.this);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	imgView.setOnTouchImageViewListener(new OnTouchImageViewListener()
	{

	    @Override
	    public void onMove()
	    {

		// PointF point = imgView.getScrollPosition();
		// RectF rect = imgView.getZoomedRect();
		// float currentZoom = imgView.getCurrentZoom();
		// boolean isZoomed = imgView.isZoomed();
	    }
	});

	btnEdit.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);

	    }
	});

	btnOk.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		finish();

	    }
	});

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {

	super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	switch (requestCode)
	{

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

			    Display_Path = selectedImage.toString();
			    FILE_PATH = filePath;
			    FILE_NAME = filename;
			    ApplicationSingleton.isNewGroupPhoto = true;
			    ApplicationSingleton.CURRENT_GROUP_PHOTO = selectedImage.toString();
			    // imageLoader.displayImage(selectedImage.toString(),
			    // imgView, options);
			    optionMenuState = 1;
			    invalidateOptionsMenu();
			    // btnOk.setVisibility(View.VISIBLE);

			    if (filePath.length() > 0)
			    {
				// RotatePicture(getpath);
				// imageLoader.displayImage(path, imvPicture,
				// options);
				myAsyntask = new MyAsyntask();
				myAsyntask.activity = Activity_View_Attachment.this;
				myAsyntask.execute(filePath);
			    }

			}
		    }
		    catch (Exception ex)
		    {

		    }

		}
		break;
	}
    }

    private class MyAsyntask extends AsyncTask<String, Void, String>
    {

	Activity activity;

	@Override
	protected String doInBackground(String... params)
	{

	    StaticFunction.RotatePicture(params[0], activity);
	    return null;
	}

	@Override
	protected void onPostExecute(String result)
	{

	    super.onPostExecute(result);

	    imageLoader.displayImage(Display_Path, imgView, options);

	}
    }

}
