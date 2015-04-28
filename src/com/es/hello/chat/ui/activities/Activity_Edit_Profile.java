package com.es.hello.chat.ui.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.customobject.CustomObject;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Activity_Edit_Profile extends ActionBarActivity
{

    ImageView imgAvata;

    Button btnSave;

    EditText txtStatus;

    EditText txtFullname;

    EditText txtBirthday;

    CheckBox chkMale;

    CheckBox chkFemale;

    String mCurrentPhotoPath;

    public File avataFile;

    public String filePath;

    public String fileName;

    public boolean isAvata = false;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private MyAsyntask myAsyntask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_edit_profile);

	StaticFunction.initImageLoader(Activity_Edit_Profile.this);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "SETTINGS", false);

	initView();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {

	super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

	switch (requestCode)
	{

	    case 1:
		if (resultCode == RESULT_OK)
		{

		    filePath = mCurrentPhotoPath;
		    avataFile = new File(filePath);
		    fileName = avataFile.getName();

		    isAvata = true;

		    myAsyntask = new MyAsyntask();
		    myAsyntask.execute(filePath);

		}

		break;

	    case 0:
		if (resultCode == RESULT_OK)
		{
		    Uri selectedImage = imageReturnedIntent.getData();

		    selectedImage = convertUriMediaPath(selectedImage);

		    String[] filePathColumn =
		    {
			MediaStore.Images.Media.DATA
		    };
		    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

		    cursor.moveToFirst();

		    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

		    // file path of selected image
		    filePath = cursor.getString(columnIndex);
		    avataFile = new File(filePath);
		    fileName = avataFile.getName();

		    cursor.close();

		    isAvata = true;

		    myAsyntask = new MyAsyntask();
		    myAsyntask.execute(filePath);

		}
		break;
	}
    }

    private void uploadContent(File f, boolean fileIsPublic, final String filePath, final String fileName, final String fullname, final String gender, final String birthday, final String status)
    {

	QBContent.uploadFileTask(f, true, null, new QBEntityCallbackImpl<QBFile>()
	{

	    @Override
	    public void onSuccess(QBFile file, Bundle params)
	    {

		QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Edit_Profile.this);
		int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Edit_Profile.this);
		currentLoginUser.setId(currentLoginUserID);

		currentLoginUser.setWebsite(file.getPublicUrl());
		/*CustomObject cus = new CustomObject();
		cus.fullName = "full name";
		cus.status = "this is status";
		cus.gender = "male";
		currentLoginUser.setCustomDataAsObject(cus);*/
		currentLoginUser.setFullName(fullname);
		currentLoginUser.setFacebookId(gender); // gender
		currentLoginUser.setTwitterId(birthday); // birthday
		currentLoginUser.setExternalId(status); // status

		QBUsers.updateUser(currentLoginUser, new QBEntityCallbackImpl<QBUser>()
		{

		    @Override
		    public void onSuccess(QBUser users, Bundle params)
		    {

			Toast.makeText(Activity_Edit_Profile.this, "OK", Toast.LENGTH_LONG).show();

		    }

		    @Override
		    public void onError(List<String> errors)
		    {

			Toast.makeText(Activity_Edit_Profile.this, "Error", Toast.LENGTH_LONG).show();
		    }

		});

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		Toast.makeText(Activity_Edit_Profile.this, "Error", Toast.LENGTH_LONG).show();
	    }
	});
	//
    }

    private void initView()
    {

	imgAvata = (ImageView) findViewById(R.id.imgAvata);
	btnSave = (Button) findViewById(R.id.btnSave);
	txtStatus = (EditText) findViewById(R.id.txtStatus);
	txtFullname = (EditText) findViewById(R.id.txtFullname);
	txtBirthday = (EditText) findViewById(R.id.txtBirthday);
	chkMale = (CheckBox) findViewById(R.id.chkMale);
	chkFemale = (CheckBox) findViewById(R.id.chkFemale);

	onAvataClick();
	onButtonSaveClick();
    }

    private void onAvataClick()
    {

	imgAvata.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		showPopupMenu(Activity_Edit_Profile.this, R.layout.dialog_menu_settings);
	    }
	});
    }

    private void onButtonSaveClick()
    {

	btnSave.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		String fullname = txtFullname.getText().toString().trim();
		String birthday = txtBirthday.getText().toString().trim();
		String status = txtStatus.getText().toString().trim();
		String gender = "";
		if (chkMale.isChecked())
		{
		    gender = "1";
		}
		else if (chkFemale.isChecked())
		{
		    gender = "0";
		}
		else
		{
		    Toast.makeText(Activity_Edit_Profile.this, "Please select gender!", Toast.LENGTH_LONG).show();
		    return;
		}

		if (isAvata)
		{
		    updateProfileWithAvata(fullname, gender, status, birthday);
		}
		else
		{
		    updateProfileNoAvata(fullname, gender, status, birthday);
		}
	    }
	});
    }

    // create new user without avata
    private void updateProfileNoAvata(String fName, String gender, String status, String birthday)
    {

	QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Edit_Profile.this);
	int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Edit_Profile.this);
	currentLoginUser.setId(currentLoginUserID);

	currentLoginUser.setFullName(fName);
	currentLoginUser.setFacebookId(gender); // gender
	currentLoginUser.setTwitterId(birthday); // birthday
	currentLoginUser.setExternalId(status); // status

	QBUsers.updateUser(currentLoginUser, new QBEntityCallbackImpl<QBUser>()
	{

	    @Override
	    public void onSuccess(QBUser users, Bundle params)
	    {

		Toast.makeText(Activity_Edit_Profile.this, "OK", Toast.LENGTH_LONG).show();

	    }

	    @Override
	    public void onError(List<String> errors)
	    {

		Toast.makeText(Activity_Edit_Profile.this, "Error", Toast.LENGTH_LONG).show();
	    }

	});

    }

    
    // upload new user with profile and avata
    private void updateProfileWithAvata(String fName, String gender, String status, String birthday)
    {

	uploadContent(avataFile, true, filePath, fileName, fName, gender, birthday, status);
    }

    //
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

    private class MyAsyntask extends AsyncTask<String, Void, String>
    {

	@Override
	protected String doInBackground(String... params)
	{

	    StaticFunction.RotatePicture(params[0], Activity_Edit_Profile.this);
	    return null;
	}

	@Override
	protected void onPostExecute(String result)
	{

	    super.onPostExecute(result);

	    imageLoader.displayImage(filePath, imgAvata, options);

	}
    }

}
