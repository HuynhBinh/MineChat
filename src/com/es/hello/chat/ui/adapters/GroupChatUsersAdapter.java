package com.es.hello.chat.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.es.hello.chat.StaticFunction;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.users.model.QBUser;

public class GroupChatUsersAdapter extends BaseAdapter
{

    public List<QBUser> dataSource;

    private LayoutInflater inflater;

    public List<QBUser> selected = new ArrayList<QBUser>();

    public Context mCtx;

    Activity mActivity;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    String Admin = "";

    public GroupChatUsersAdapter(List<QBUser> dataSource, Context ctx, String Admin)
    {

	this.dataSource = dataSource;
	this.inflater = LayoutInflater.from(ctx);
	mCtx = ctx;
	mActivity = (Activity) ctx;
	this.Admin = Admin;

	StaticFunction.initImageLoader(mCtx);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_contact_picture).showImageOnFail(R.drawable.ic_contact_picture).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();

    }
    
    public void setListItems(List<QBUser> listUsers)
    {
	this.dataSource = listUsers;
    }
    
    public void setAdmin(String admin)
    {
	this.Admin = admin;
    }

    public void addListItems(List<QBUser> listUsers)
    {

	dataSource.addAll(listUsers);
    }

    public List<QBUser> getSelected()
    {

	return selected;
    }

    @Override
    public int getCount()
    {

	return dataSource.size();
    }

    @Override
    public Object getItem(int position)
    {

	return dataSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {

	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

	ViewHolder holder;
	if (convertView == null)
	{
	    convertView = inflater.inflate(R.layout.list_item_user_adapter, null);
	    holder = new ViewHolder();
	    holder.login = (TextView) convertView.findViewById(R.id.txtName);
	    holder.status = (TextView) convertView.findViewById(R.id.txtStatus);

	    holder.imgAvatar = (ImageView) convertView.findViewById(R.id.img_avarta);
	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	final QBUser user = dataSource.get(position);

	if (user != null)
	{

	    boolean isUserOnline = StaticFunction.isUserOnline(user);

	    if (isUserOnline == true)
	    {
		holder.status.setText("Online");
	    }
	    else
	    {
		holder.status.setText("Offline");

	    }

	    String strLogin = user.getLogin();

	    String createdByID = this.Admin;

	    if (createdByID.equalsIgnoreCase(user.getId() + ""))
	    {

		holder.login.setText(strLogin + " (Admin)");

	    }
	    else
	    {
		holder.login.setText(strLogin);

	    }

	    imageLoader.displayImage(user.getWebsite(), holder.imgAvatar, options);

	}
	return convertView;
    }

    private static class ViewHolder
    {

	ImageView imgAvatar;

	TextView login;

	TextView status;

	// CheckBox add;
    }

}
