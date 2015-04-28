package com.es.hello.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class API {
	
	String HOST = "http://www.freeappsforyou.info/chat/";

	private HttpClient client = new DefaultHttpClient();
	private HttpPost post;
	HttpParams httpParams = client.getParams();	
	
	private int TIME_OUT = 20000;
    
	public String addTag(String Tag) {
		post = new HttpPost(HOST + "addtag.php");
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
		String content = "";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("tag", Tag));
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(entity);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			content = client.execute(post, handler);
		} catch (Exception e) {
			
		}

		return content;
	}
	
	public String getTop() {
		post = new HttpPost(HOST + "gettop.php");
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
		String content = "";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(entity);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			content = client.execute(post, handler);
		} catch (Exception e) {
			
		}

		return content;
	}
	
}
