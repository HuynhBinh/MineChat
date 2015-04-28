package com.es.hello.chat.ui.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.es.hello.chat.PlayServicesHelper;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.services.HelloMainService;
import com.es.hello.chat.ui.adapters.ImageViewFragmentAdapter;
import com.lat.hello.chat.R;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.viewpagerindicator.CirclePageIndicator;

public class Activity_Setting extends ActionBarActivity implements HelloMainService.ConnectionInterfaceCallback
{

    private ProgressBar progressBar;

    ImageView btnEditAvatar;

    public PlayServicesHelper playServicesHelper;

    Typeface myriadPro_semibold_italic;

    Typeface myriadPro_semibold;

    EditText txtUserID;

    TextView txtUserIDtxt;

    EditText txtEmail;

    TextView txtEmailtxt;

    EditText txtBirthday;

    TextView txtBirthdaytxt;

    EditText txtGender;

    TextView txtGendertxt;

    EditText txtStatus;

    EditText txtChangePass;

    private ViewPager pager;

    private CirclePageIndicator indicator;

    private ImageViewFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	// setTheme(android.R.style.Theme_Holo_Light);
	setContentView(R.layout.activity_setting);

	Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	setSupportActionBar(toolbar);

	StaticFunction.initActionToolBar(this, toolbar, "SETTINGS", false);

	{
	    myriadPro_semibold_italic = Typeface.createFromAsset(this.getAssets(), "MyriadPro-Semibold_Italic.ttf");
	    myriadPro_semibold = Typeface.createFromAsset(this.getAssets(), "MyriadPro-Semibold.ttf");
	    txtUserID = (EditText) findViewById(R.id.txtUserID);
	    txtUserID.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtUserID.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtUserID.setTextColor(Color.parseColor("#666666"));

	    txtUserIDtxt = (TextView) findViewById(R.id.txtUserIDtxt);
	    txtUserIDtxt.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(Activity_Setting.this), Typeface.BOLD);
	    txtUserIDtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtUserIDtxt.setTextColor(Color.parseColor("#666666"));

	    txtEmail = (EditText) findViewById(R.id.txtEmail);
	    txtEmailtxt = (TextView) findViewById(R.id.txtEmailtxt);

	    txtEmail.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtEmail.setTextColor(Color.parseColor("#666666"));
	    txtEmailtxt.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(Activity_Setting.this), Typeface.BOLD);
	    txtEmailtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtEmailtxt.setTextColor(Color.parseColor("#666666"));

	    txtBirthday = (EditText) findViewById(R.id.txtBirthDay);
	    txtBirthdaytxt = (TextView) findViewById(R.id.txtBirthDaytxt);

	    txtBirthday.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtBirthday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtBirthday.setTextColor(Color.parseColor("#666666"));
	    txtBirthdaytxt.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(Activity_Setting.this), Typeface.BOLD);
	    txtBirthdaytxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtBirthdaytxt.setTextColor(Color.parseColor("#666666"));

	    txtGender = (EditText) findViewById(R.id.txtGender);
	    txtGendertxt = (TextView) findViewById(R.id.txtGendertxt);

	    txtGender.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtGender.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtGender.setTextColor(Color.parseColor("#666666"));
	    txtGendertxt.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(Activity_Setting.this), Typeface.BOLD);
	    txtGendertxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
	    txtGendertxt.setTextColor(Color.parseColor("#666666"));

	    txtStatus = (EditText) findViewById(R.id.txtStatus);
	    txtChangePass = (EditText) findViewById(R.id.txtChangePass);

	    txtStatus.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
	    txtStatus.setTextColor(Color.parseColor("#666666"));

	    txtChangePass.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(Activity_Setting.this), Typeface.BOLD);
	    txtChangePass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
	    txtChangePass.setTextColor(Color.parseColor("#666666"));

	}

	pager = (ViewPager) findViewById(R.id.pager);
	indicator = (CirclePageIndicator) findViewById(R.id.indicator);

	adapter = new ImageViewFragmentAdapter(getSupportFragmentManager(), this, R.drawable.sampleava2, R.drawable.sampleava3, R.drawable.sampleava4, R.drawable.sampleava5);
	pager.setAdapter(adapter);
	indicator.setViewPager(pager);
	pager.setCurrentItem(0);

	QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Setting.this);

	playServicesHelper = new PlayServicesHelper(this, currentLoginUser.getLogin());

	// imgAvarta = (ImageView) findViewById(R.id.img_ava);

	btnEditAvatar = (ImageView) findViewById(R.id.img_btn_edit_ava);

	progressBar = (ProgressBar) findViewById(R.id.progressBar);

	// progressBar.setVisibility(View.GONE);

	btnEditAvatar.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		showPopupMenu(Activity_Setting.this, R.layout.dialog_menu_settings);

	    }
	});

    }

    @Override
    public void onBackPressed()
    {

	// Intent inttent = new Intent(Activity_Setting.this,
	// Activity_Search.class);
	// inttent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	// startActivity(inttent);
	// ApplicationSingleton.queueActivities.enqueue(Activity_Setting.class);
	finish();
	super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_settings, menu);
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

	    Intent intent = new Intent(Activity_Setting.this, Activity_Search.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    startActivity(intent);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Setting.class);
	    // finish();

	}

	if (id == R.id.action_2)
	{

	    Intent intent = new Intent(Activity_Setting.this, DialogsActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    startActivity(intent);
	    // ApplicationSingleton.queueActivities.enqueue(Activity_Setting.class);
	    // finish();

	}

	if (id == R.id.action_3)
	{
	    StaticFunction.logoutHelloChat(Activity_Setting.this, progressBar);
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

		QBUser currentLoginUser = SharePrefsHelper.getCurrentLoginUser(Activity_Setting.this);
		int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(Activity_Setting.this);
		currentLoginUser.setId(currentLoginUserID);

		// QBUser user = ApplicationSingleton.getCurrentUser();

		currentLoginUser.setWebsite(file.getPublicUrl());

		QBUsers.updateUser(currentLoginUser, new QBEntityCallbackImpl<QBUser>()
		{

		    @Override
		    public void onSuccess(QBUser users, Bundle params)
		    {

			// Toast locked
			// Toast.makeText(getApplicationContext(),
			// "Upload successfuly!", Toast.LENGTH_LONG).show();

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

    @Override
    public void onConnectionClosedOnError()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet(Activity_Setting.this, Activity_Setting.this, "         Connection Lost!         ");

	    }
	});

    }

    @Override
    public void onConnectedSuccess()
    {

    }

    @Override
    public void onReconnecting()
    {

	// TODO Auto-generated method stub

    }

    @Override
    public void onConnectFail()
    {

	// TODO Auto-generated method stub

    }

    public void showPopupNoInternet(final Activity acti, Context context, String dialogMessage)
    {

	if (!isFinishing())
	{
	    // custom dialog
	    final Dialog dialog = new Dialog(context);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	    dialog.setCanceledOnTouchOutside(false);
	    dialog.setCancelable(false);

	    dialog.setContentView(R.layout.dialog_no_internet);

	    TextView txtDialogMessage = (TextView) dialog.findViewById(R.id.txtDialogMessage);
	    txtDialogMessage.setText(dialogMessage);

	    Button btnRetry = (Button) dialog.findViewById(R.id.btnRetry);
	    Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
	    btnCancel.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    StaticFunction.logoutHelloChat(acti, null);
		    dialog.dismiss();
		    acti.finish();

		}
	    });

	    btnRetry.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    try
		    {

			Intent intent = new Intent(acti, Activity_FlashScreen.class);
			acti.startActivity(intent);

			dialog.dismiss();
			acti.finish();
		    }
		    catch (Exception ex)
		    {
			Log.e("", "");
		    }

		}
	    });

	    dialog.show();
	}

    }

    @Override
    public void onConnectionClosedDuetoUserLoginToOtherDevice()
    {

	// TODO Auto-generated method stub

    }

}
