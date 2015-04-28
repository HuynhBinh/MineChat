package com.es.hello.chat.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lat.hello.chat.R;

public class Activity_View_Attachment_OtherType extends Activity
{

    WebView webView;

    private ProgressBar progressBar;

    public static String urlFilePath = "";

    public static String drivePath = "https://drive.google.com/viewerng/viewer?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_view_attachment_other_type);

	initControl();
	progressBar.setVisibility(View.GONE);

    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initControl()
    {

	progressBar = (ProgressBar) findViewById(R.id.progressBar);
	webView = (WebView) findViewById(R.id.webView);

	WebSettings settings = webView.getSettings();
	settings.setJavaScriptEnabled(true);

	webView.setWebViewClient(new WebViewClient()
	{

	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap facIcon)
	    {

		progressBar.setVisibility(View.VISIBLE);

	    }

	    @Override
	    public void onPageFinished(WebView view, String url)
	    {

		progressBar.setVisibility(View.GONE);

	    }

	    @Override
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	    {

		progressBar.setVisibility(View.GONE);
		super.onReceivedError(view, errorCode, description, failingUrl);

	    }
	});

	String finalURL = drivePath + urlFilePath;// "https://qbprod.s3.amazonaws.com/ab12933d439b4a958daca182c67ea0f800";
	webView.loadUrl(finalURL);

    }

}
