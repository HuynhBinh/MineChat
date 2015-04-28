package com.es.hello.chat.ui.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.es.hello.chat.services.HelloMainService;
import com.lat.hello.chat.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Activity_Register extends Activity
{

    EditText txtUsername;

    EditText txtPass;

    EditText txtConfirmPass;

    EditText txtEmail;

    Button btnRegister;

    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_register);

	setTitle("Register new account");

	initView();

    }

    private void initView()
    {

	txtUsername = (EditText) findViewById(R.id.txtUser);
	txtPass = (EditText) findViewById(R.id.txtPass);
	txtConfirmPass = (EditText) findViewById(R.id.txtConfirmPass);
	txtEmail = (EditText) findViewById(R.id.txtEmail);

	btnRegister = (Button) findViewById(R.id.btnRegister);

	txtError = (TextView) findViewById(R.id.txtError);

	btnRegister.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		txtError.setText("");
		txtError.setVisibility(View.GONE);

		String username = txtUsername.getText().toString().trim();
		String pass = txtPass.getText().toString().trim();
		String confirmPass = txtConfirmPass.getText().toString().trim();
		String email = txtEmail.getText().toString().trim();

		boolean isOk = validateUserInput(username, pass, confirmPass, email);
		if (isOk)
		{
		    isOk = validatePass(pass, confirmPass);
		    if (isOk)
		    {
			isOk = (isEmailValid(email));

			if (isOk)
			{

			    QBSettings.getInstance().fastConfigInit(HelloMainService.APP_ID, HelloMainService.AUTH_KEY, HelloMainService.AUTH_SECRET);

			    QBAuth.createSession(new QBEntityCallbackImpl<QBSession>()
			    {

				@Override
				public void onSuccess(QBSession session, Bundle params)
				{

				    runOnUiThread(new Runnable()
				    {

					@Override
					public void run()
					{

					    QBUser qbUser = new QBUser();
					    qbUser.setLogin(txtUsername.getText().toString().trim());
					    qbUser.setPassword(txtPass.getText().toString().trim());
					    qbUser.setEmail(txtEmail.getText().toString().trim());

					    QBUsers.signUp(qbUser, new QBEntityCallbackImpl<QBUser>()
					    {

						@Override
						public void onSuccess(QBUser user, Bundle args)
						{

						    try
						    {
							BaseService.getBaseService().setToken(null);
						    }
						    catch (BaseServiceException e)
						    {
							e.printStackTrace();
						    }

						    Toast.makeText(Activity_Register.this, "Signup Ok", Toast.LENGTH_LONG).show();
						    finish();

						}

						@Override
						public void onError(List<String> errors)
						{

						    txtError.setText(errors.toString());
						    txtError.setVisibility(View.VISIBLE);
						}
					    });

					}
				    });

				}

				@Override
				public void onError(List<String> errors)
				{

				    txtError.setText(errors.toString());
				    txtError.setVisibility(View.VISIBLE);
				}
			    });

			}
			else
			{

			}

		    }
		    else
		    {

		    }

		}

	    }
	});

    }

    private boolean validatePass(String pass, String confirmPass)
    {

	if (!pass.equals(confirmPass))
	{
	    return false;
	}

	return true;
    }

    private boolean validateUserInput(String username, String pass, String confirmPass, String email)
    {

	if (username.equals("") || pass.equals("") || confirmPass.equals("") || email.equals(""))
	{
	    return false;
	}

	return true;

    }

    private boolean isEmailValid(CharSequence email)
    {

	return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
