package com.es.hello.chat.ui.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_Group_Chat_Step1 extends ActionBarActivity
{

    ImageView imgGroupPhoto;

    EditText edtGroupName;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    public String FILE_PATH = "";

    public String FILE_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_group_chat_step1);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "New Group", true);

	StaticFunction.initImageLoader(Activity_Group_Chat_Step1.this);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_contact_picture).showImageOnFail(R.drawable.ic_contact_picture).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	initView();

	SharePrefsHelper.saveGroupNameToSharePrefs("", Activity_Group_Chat_Step1.this);
	SharePrefsHelper.saveGroupPhotoToSharePrefs("", Activity_Group_Chat_Step1.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_create_group_step1, menu);

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
	    String groupName = edtGroupName.getText().toString().trim();

	    if (groupName.equalsIgnoreCase(""))
	    {

		Toast.makeText(Activity_Group_Chat_Step1.this, "Please enter group name!", Toast.LENGTH_LONG).show();

	    }
	    else
	    {
		SharePrefsHelper.saveGroupNameToSharePrefs(groupName, Activity_Group_Chat_Step1.this);

		Intent intent = new Intent(Activity_Group_Chat_Step1.this, Activity_Group_Chat_Step2.class);
		intent.putExtra("FROM", "Activity_Group_Chat_Settings");
		startActivity(intent);

		finish();
	    }

	}

	return super.onOptionsItemSelected(item);
    }

    private void initView()
    {

	imgGroupPhoto = (ImageView) findViewById(R.id.imgGroupPhoto);
	imgGroupPhoto.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);

	    }
	});

	edtGroupName = (EditText) findViewById(R.id.txtGroupSubject);
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

			    FILE_PATH = filePath;
			    FILE_NAME = filename;

			    SharePrefsHelper.saveGroupPhotoToSharePrefs(FILE_PATH, Activity_Group_Chat_Step1.this);

			    RotatePicture(FILE_PATH);

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

    public void RotatePicture(String path)
    {

	int inSampleSize = 1;
	inSampleSize = StaticFunction.getBitmapInSampleSize(Activity_Group_Chat_Step1.this, path);

	BitmapFactory.Options opts = new BitmapFactory.Options();
	opts.inSampleSize = inSampleSize;
	Bitmap bm = BitmapFactory.decodeFile(path, opts);
	ExifInterface exif = null;
	String orientString = "";
	try
	{
	    exif = new ExifInterface(path);
	    orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}

	int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

	int rotationAngle = 0;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
	    rotationAngle = 90;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
	    rotationAngle = 180;
	if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
	    rotationAngle = 270;

	if (rotationAngle == 0)
	{
	    saveToFile(path, bm);

	    if (!bm.isRecycled())
	    {
		bm.recycle();
	    }
	}
	else
	{
	    Matrix matrix = new Matrix();

	    matrix.postRotate(rotationAngle);

	    Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	    saveToFile(path, rotatedBitmap);

	    if (!rotatedBitmap.isRecycled())
	    {
		rotatedBitmap.recycle();
	    }
	}

    }

    private void saveToFile(String filename, Bitmap bmp)
    {

	try
	{
	    FileOutputStream out = new FileOutputStream(filename);
	    bmp.compress(CompressFormat.JPEG, 50, out);
	    out.flush();
	    out.close();

	    if (!bmp.isRecycled())
	    {
		bmp.recycle();
	    }

	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(filename);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    sendBroadcast(mediaScanIntent);
	}
	catch (IOException e)
	{
	}

	// imageLoader.displayImage(ATTACH_FILE_PATH, imvPicture, options);
    }

}
