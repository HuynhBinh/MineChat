package com.es.hello.chat.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lat.hello.chat.R;

public class Activity_Login_Hello extends Activity
{

    EditText txtUser;

    EditText txtPass;

    Button btnLogin;

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login_hello);

	initControl();

    }

    private void initControl()
    {

	txtUser = (EditText) findViewById(R.id.txtUser);
	txtPass = (EditText) findViewById(R.id.txtPass);
	btnLogin = (Button) findViewById(R.id.btnLogin);
	btnRegister = (Button) findViewById(R.id.btnRegisterNewAccount);

	txtPass.setOnKeyListener(new OnKeyListener()
	{

	    @Override
	    public boolean onKey(View v, int keyCode, KeyEvent event)
	    {

		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
		{
		    String username = txtUser.getText().toString().trim();
		    String pass = txtPass.getText().toString().trim();

		    if (!username.equalsIgnoreCase("") && !pass.equalsIgnoreCase(""))
		    {

			Intent i = new Intent(Activity_Login_Hello.this, Activity_FlashScreen.class);
			i.putExtra("FROM", "Activity_Login_Hello");
			i.putExtra("CURRENT_USER_LOGIN_NAME", txtUser.getText().toString().trim());
			i.putExtra("CURRENT_USER_LOGIN_PASSWORD", txtPass.getText().toString().trim());

			startActivity(i);
			finish();
		    }
		    else
		    {
			Toast.makeText(Activity_Login_Hello.this, "Please enter username and password properly!", Toast.LENGTH_LONG).show();
		    }

		    return false;
		}
		else
		{
		    return false;
		}

	    }
	});

	btnLogin.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		String username = txtUser.getText().toString().trim();
		String pass = txtPass.getText().toString().trim();

		if (!username.equalsIgnoreCase("") && !pass.equalsIgnoreCase(""))
		{

		    Intent i = new Intent(Activity_Login_Hello.this, Activity_FlashScreen.class);
		    i.putExtra("FROM", "Activity_Login_Hello");
		    i.putExtra("CURRENT_USER_LOGIN_NAME", txtUser.getText().toString().trim());
		    i.putExtra("CURRENT_USER_LOGIN_PASSWORD", txtPass.getText().toString().trim());

		    startActivity(i);
		    finish();
		}
		else
		{
		    Toast.makeText(Activity_Login_Hello.this, "Please enter username and password properly!", Toast.LENGTH_LONG).show();
		}

	    }
	});

	btnRegister.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		Intent intent = new Intent(Activity_Login_Hello.this, Activity_Register.class);
		startActivity(intent);

	    }
	});

    }

}
