package com.es.hello.chat.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lat.hello.chat.R;

public class TestActivity extends Activity
{

    Button btn1;

    boolean isIn = true;

    LinearLayout layoutSearch;
    
    Animation ani;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.testsearch);

	layoutSearch = (LinearLayout) findViewById(R.id.searchLayout);

	btn1 = (Button) findViewById(R.id.button1);
	btn1.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		//layoutSearch.setc
		
		    //down(layoutSearch);
		layoutSearch.setVisibility(View.VISIBLE);
		ani = AnimationUtils.loadAnimation(TestActivity.this, R.anim.bouncedown);
		layoutSearch.startAnimation(ani);
		   
	    }
	});

    }

    private void down(View view)
    {

	//view.setVisibility(View.VISIBLE);
	//view.setAlpha(0.0f);

	// Start the animation
	//view.animate().y(-200).translationY(10).setDuration(2000);//.alpha(1.0f);
	
    }

    private void up(final View view)
    {

	view.animate().translationY(0).setDuration(1000).setListener(new AnimatorListenerAdapter()
	{

	    @Override
	    public void onAnimationEnd(Animator animation)
	    {

		super.onAnimationEnd(animation);
		view.setVisibility(View.GONE);
	    }
	});
    }
}
