package com.es.hello.chat.ui.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.ui.adapters.ImageViewFragmentAdapter;
import com.lat.hello.chat.R;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Activity_SignUp extends ActionBarActivity
{

    ImageView imgAvarta;

    private ProgressBar progressBar;

    // showPopupMenu(DialogsActivity.this, R.layout.dialog_menu_favourite_view,
    // dialogID);

    ImageView btnEditAvatar;

    private ViewPager pager;

    private ImageViewFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_setting);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "SIGNUP", false);

	// getActionBar().setTitle("SIGNUP");
	// getActionBar().setBackgroundDrawable(new
	// ColorDrawable(Color.parseColor("#fef8ec")));
	// getActionBar().setIcon(new
	// ColorDrawable(getResources().getColor(android.R.color.transparent)));
	// FontTypeUtils.setFontForTittleBar(this, this);

	// imgAvarta = (ImageView) findViewById(R.id.img_ava);

	btnEditAvatar = (ImageView) findViewById(R.id.img_btn_edit_ava);

	progressBar = (ProgressBar) findViewById(R.id.progressBar);

	// progressBar.setVisibility(View.GONE);

	pager = (ViewPager) findViewById(R.id.pager);

	adapter = new ImageViewFragmentAdapter(getSupportFragmentManager(), this, R.drawable.sampleava2, R.drawable.sampleava3, R.drawable.sampleava4, R.drawable.sampleava5);
	pager.setAdapter(adapter);
	pager.setCurrentItem(0);

	btnEditAvatar.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		// showPopupMenu(Activity_SignUp.this,
		// R.layout.dialog_menu_settings);

	    }
	});

    }

    @Override
    public void onBackPressed()
    {

	//Intent inttent = new Intent(Activity_SignUp.this, Activity_Pre_Login.class);
	//startActivity(inttent);
	finish();
	super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_signup, menu);
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

	    Intent i = new Intent(Activity_SignUp.this, Activity_FlashScreen.class);
	    i.putExtra("FROM", "Activity_Login_Hello");
	    i.putExtra("CURRENT_USER_LOGIN_NAME", "admin2");
	    i.putExtra("CURRENT_USER_LOGIN_PASSWORD", "12345678");

	    startActivity(i);
	    finish();

	}

	return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {

	super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	switch (requestCode)
	{

	    case 1:
		if (resultCode == RESULT_OK)
		{
		    // Bundle extras = imageReturnedIntent.getExtras();
		    // Bitmap imageBitmap = (Bitmap) extras.get("data");
		    // mImageView.setImageBitmap(imageBitmap);
		    final String filePath = mCurrentPhotoPath;
		    File f = new File(filePath);
		    final String filename = f.getName();

		    progressBar.setVisibility(View.VISIBLE);

		    Boolean fileIsPublic = false;

		    uploadContent(f, fileIsPublic, filePath, filename);

		}

		break;

	    case 0:
		if (resultCode == RESULT_OK)
		{
		    Uri selectedImage = imageReturnedIntent.getData();

		    selectedImage = convertUriMediaPath(selectedImage);

		    // Bitmap bitmap = imageUtils.getBitmap(selectedImage);
		    // new
		    // ReceiveFileFromBitmapTask(ChatActivity.this).execute(imageUtils,
		    // bitmap, true);

		    String[] filePathColumn =
		    {
			MediaStore.Images.Media.DATA
		    };
		    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

		    cursor.moveToFirst();

		    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

		    // file path of selected image
		    final String filePath = cursor.getString(columnIndex);
		    File f = new File(filePath);
		    final String filename = f.getName();

		    cursor.close();

		    progressBar.setVisibility(View.VISIBLE);

		    Boolean fileIsPublic = false;

		    uploadContent(f, fileIsPublic, filePath, filename);

		}
		break;
	}
    }

    public void showPopupMenu(Context context, int ResourceID)
    {

	// custom dialog
	final Dialog dialog = new Dialog(context);

	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(ResourceID);

	// set the custom dialog components - text, image and button
	LinearLayout btnWallpaper = (LinearLayout) dialog.findViewById(R.id.btnTakePhoto);
	// if button is clicked, close the custom dialog
	btnWallpaper.setOnClickListener(new OnClickListener()
	{

	    public void onClick(View v)
	    {

		dispatchTakePictureIntent();
		dialog.dismiss();
	    }
	});

	LinearLayout btnMainMenu = (LinearLayout) dialog.findViewById(R.id.btnGallery);
	btnMainMenu.setOnClickListener(new OnClickListener()
	{

	    public void onClick(View v)
	    {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);
		dialog.dismiss();

	    }
	});

	dialog.show();

    }

    String mCurrentPhotoPath;

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException
    {

	// Create an image file name
	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	String imageFileName = "JPEG_" + timeStamp + "_";
	File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	File image = File.createTempFile(imageFileName, /* prefix */
		".jpg", /* suffix */
		storageDir /* directory */
	);

	// Save a file: path for use with ACTION_VIEW intents
	mCurrentPhotoPath = image.getAbsolutePath();
	return image;
    }

    public Uri convertUriMediaPath(Uri uriFrom)
    {

	if (uriFrom.toString().startsWith("content://com.android.providers.media.documents"))
	{
	    String[] split = uriFrom.toString().split("%3A");
	    Uri uriTo = Uri.parse("content://media/external/images/media/" + split[1]);
	    return uriTo;
	}
	else
	{
	    return uriFrom;
	}
    }

    private void dispatchTakePictureIntent()
    {

	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// Ensure that there's a camera activity to handle the intent
	if (takePictureIntent.resolveActivity(getPackageManager()) != null)
	{
	    // Create the File where the photo should go
	    File photoFile = null;
	    try
	    {
		photoFile = createImageFile();
	    }
	    catch (IOException ex)
	    {
		// Error occurred while creating the File

	    }
	    // Continue only if the File was successfully created
	    if (photoFile != null)
	    {
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(takePictureIntent, 1);
	    }
	}
    }

    private void uploadContent(File f, boolean fileIsPublic, final String filePath, final String fileName)
    {

	QBContent.uploadFileTask(f, true, null, new QBEntityCallbackImpl<QBFile>()
	{

	    @Override
	    public void onSuccess(QBFile file, Bundle params)
	    {

		Bitmap bmp = BitmapFactory.decodeFile(filePath);

		imgAvarta.setImageBitmap(bmp);

		QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_SignUp.this);
		int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_SignUp.this);
		currentLoginUser.setId(currentLoginUserID);

		// QBUser user = ApplicationSingleton.getCurrentUser();

		currentLoginUser.setWebsite(file.getPublicUrl());

		QBUsers.updateUser(currentLoginUser, new QBEntityCallbackImpl<QBUser>()
		{

		    @Override
		    public void onSuccess(QBUser users, Bundle params)
		    {

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

		    }

		});

		progressBar.setVisibility(View.GONE);

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		// error
	    }
	});
	//
    }

}
