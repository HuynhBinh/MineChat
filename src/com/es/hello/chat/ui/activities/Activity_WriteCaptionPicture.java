package com.es.hello.chat.ui.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.es.hello.chat.StaticFunction;
import com.google.android.gms.drive.internal.ay;
import com.google.android.gms.internal.pa;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.lat.hello.chat.R;

public class Activity_WriteCaptionPicture extends Activity implements OnClickListener
{

    private ImageView imvPicture;

    private ImageView imvOk;

    private EditText edtCaption;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private String ATTACH_FILE_PATH;

    public static String CAPTION = "";

    private MyAsyntask myAsyntask;

    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_write_caption_picture);

	StaticFunction.initImageLoader(Activity_WriteCaptionPicture.this);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	imvPicture = (ImageView) findViewById(R.id.imvPicture);
	imvOk = (ImageView) findViewById(R.id.imvOk);
	edtCaption = (EditText) findViewById(R.id.edtCaption);
	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);

	imvPicture.setVisibility(View.INVISIBLE);

	imvOk.setOnClickListener(this);

	if (getIntent().getExtras() != null)
	{
	    String path = getIntent().getExtras().getString("path", "");
	    String getpath = getIntent().getExtras().getString("getpath", "");
	    ATTACH_FILE_PATH = path;
	    if (path.length() > 0)
	    {
		// RotatePicture(getpath);
		// imageLoader.displayImage(path, imvPicture, options);
		myAsyntask = new MyAsyntask();
		myAsyntask.execute(getpath);
	    }
	}
    }

    @Override
    public void onClick(View v)
    {

	switch (v.getId())
	{
	    case R.id.imvOk:

		Intent returnIntent = new Intent();
		String strCaption = edtCaption.getText().toString().trim();
		if (strCaption == null || strCaption.equals(""))
		{
		    strCaption = "";
		}

		Activity_WriteCaptionPicture.CAPTION = strCaption;
		setResult(RESULT_OK, returnIntent);
		finish();
		break;

	    default:
		break;
	}
    }

    @Override
    protected void onDestroy()
    {

	super.onDestroy();
	if (myAsyntask != null && (myAsyntask.getStatus() == AsyncTask.Status.RUNNING))
	{
	    myAsyntask.cancel(true);
	}
    }

    private class MyAsyntask extends AsyncTask<String, Void, String>
    {

	@Override
	protected String doInBackground(String... params)
	{

	    StaticFunction.RotatePicture(params[0], Activity_WriteCaptionPicture.this);
	    return null;
	}

	@Override
	protected void onPostExecute(String result)
	{

	    super.onPostExecute(result);

	    imageLoader.displayImage(ATTACH_FILE_PATH, imvPicture, options);
	    progressBar.setVisibility(View.INVISIBLE);
	    imvPicture.setVisibility(View.VISIBLE);
	}
    }

}
