package com.es.hello.chat.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.es.hello.chat.StaticFunction;
import com.lat.hello.chat.R;

public class Activity_Dialog_Logout extends Activity
{

    Button btnLogout;

    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_dialog_logout);

	progressBar = (RelativeLayout) findViewById(R.id.progressBarRelative);
	progressBar.setVisibility(View.GONE);

	btnLogout = (Button) findViewById(R.id.btnLogout);

	btnLogout.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		StaticFunction.logoutHelloChat(Activity_Dialog_Logout.this, progressBar);

	    }
	});

    }

    @Override
    public void onBackPressed()
    {

	StaticFunction.logoutHelloChat(Activity_Dialog_Logout.this, progressBar);

    }

}
