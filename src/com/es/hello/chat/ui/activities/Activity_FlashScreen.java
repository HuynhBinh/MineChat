package com.es.hello.chat.ui.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.services.HelloMainService;
import com.lat.hello.chat.R;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.model.QBUser;
import com.splunk.mint.Mint;

public class Activity_FlashScreen extends Activity implements HelloMainService.ServiceInterfaceCallback
{

    LinearLayout lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);

	Mint.initAndStartSession(Activity_FlashScreen.this, "ea849c36");

	setContentView(R.layout.activity_flashscreen);

	Log.e("Activity_FlashScreen", "Activity_FlashScreen");

	lLayout = (LinearLayout) findViewById(R.id.idFlashScreen);

	StaticFunction.initImageLoader(Activity_FlashScreen.this);

	String FROM_ACTIVITY = "";

	Bundle extras = getIntent().getExtras();
	if (extras == null)
	{
	    FROM_ACTIVITY = "";

	}
	else
	{
	    FROM_ACTIVITY = extras.getString("FROM", "");
	}

	QBUser user = null;

	if (FROM_ACTIVITY.equals("Activity_Login_Hello"))
	{

	    String userLogin = extras.getString("CURRENT_USER_LOGIN_NAME", "");
	    String userPass = extras.getString("CURRENT_USER_LOGIN_PASSWORD", "");

	    user = new QBUser();
	    user.setLogin(userLogin);
	    user.setPassword(userPass);

	    SharePrefsHelper.saveUserToSharePrefs(user, Activity_FlashScreen.this);

	}

	user = SharePrefsHelper.getCurrentLoginUser(Activity_FlashScreen.this);

	if (user == null)
	{
	    startProgress(lLayout);
	}
	else
	{

	    HelloMainService.Callback = this;
	    Intent intent = new Intent(this, HelloMainService.class);
	    intent.addCategory(HelloMainService.TAG);
	    startService(intent);

	}

    }

    @Override
    public void onLoginSuccess()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		int numOfApp = SharePrefsHelper.getNumOfAppUsedForInstructionToSharePrefs(Activity_FlashScreen.this);
		/*if (numOfApp < 1)
		{
		    Intent intent = new Intent(Activity_FlashScreen.this, Activity_Edit_Profile.class);
		    startActivity(intent);
		    finish();
		}
		else
		{*/
		    Intent intent = new Intent(Activity_FlashScreen.this, Activity_Search.class);
		    startActivity(intent);
		    finish();
		//}

	    }
	});

    }

    @SuppressWarnings("unused")
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {

	ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	{
	    if (serviceClass.getName().equals(service.service.getClassName()))
	    {
		return true;
	    }
	}
	return false;
    }

    public void startProgress(View view)
    {

	Runnable runnable = new Runnable()
	{

	    @Override
	    public void run()
	    {

		doFakeWork();
		lLayout.post(new Runnable()
		{

		    @Override
		    public void run()
		    {

			Intent intent = new Intent(Activity_FlashScreen.this, Activity_Login_Hello.class);
			startActivity(intent);
			finish();
		    }
		});

	    }
	};
	new Thread(runnable).start();
    }

    private void doFakeWork()
    {

	try
	{
	    Thread.sleep(1000 * 1);
	}
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void onCreateSessionError(final String error)
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		if (error.equalsIgnoreCase("[Unauthorized]"))
		{
		    StaticFunction.showPopupWrongUser(Activity_FlashScreen.this, Activity_FlashScreen.this, "wrong user name or password!");
		}
		else
		{
		    showPopupNoInternet(Activity_FlashScreen.this, Activity_FlashScreen.this, error);

		}

	    }
	});

    }

    @Override
    public void onLoginError(final String error)
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		if (error.equalsIgnoreCase("[Unauthorized]"))
		{
		    StaticFunction.showPopupWrongUser(Activity_FlashScreen.this, Activity_FlashScreen.this, "wrong user name or password!");
		}
		else
		{
		    showPopupNoInternet(Activity_FlashScreen.this, Activity_FlashScreen.this, error);
		}

	    }
	});

    }

    @Override
    protected void onDestroy()
    {

	super.onDestroy();

	// HelloMainService.Callback = null;
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
    public void onCreateSessionErrorNeedToResetAll()
    {

	runOnUiThread(new Runnable()
	{

	    @Override
	    public void run()
	    {

		showPopupNoInternet2(Activity_FlashScreen.this, Activity_FlashScreen.this, "Connect to server fail. Please try again!");

	    }
	});

    }

    public void showPopupNoInternet2(final Activity acti, Context context, String dialogMessage)
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

			HelloMainService.chatService = null;
			BaseService.getBaseService().setToken(null);

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

}
