package com.es.hello.chat.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lat.hello.chat.R;
import com.quickblox.users.model.QBUser;

public class UsersAdapter extends BaseAdapter
{

    public List<QBUser> dataSource;

    private LayoutInflater inflater;

    public List<QBUser> selected = new ArrayList<QBUser>();

    public Context mCtx;

    Activity mActivity;

    public UsersAdapter(List<QBUser> dataSource, Context ctx)
    {

	this.dataSource = dataSource;
	this.inflater = LayoutInflater.from(ctx);
	mCtx = ctx;
	mActivity = (Activity) ctx;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

	ViewHolder holder;
	if (convertView == null)
	{
	    convertView = inflater.inflate(R.layout.list_item_user, null);
	    holder = new ViewHolder();
	    holder.login = (TextView) convertView.findViewById(R.id.userLogin);
	    //holder.add = (CheckBox) convertView.findViewById(R.id.addCheckBox);
	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	final QBUser user = dataSource.get(position);

	// QBUser currentLoginUser =
	// SharePrefsHelper.getCurrentLoginUser(this.mCtx);

	if (user != null)
	{
	    holder.login.setText(user.getLogin());
	}

	/*if (currentLoginUser.getLogin().equalsIgnoreCase(dataSource.get(position).getLogin()))
	{
	    holder.add.setVisibility(View.INVISIBLE);
	}
	else
	{
	    holder.add.setVisibility(View.VISIBLE);

	}*/

	/*if (user != null)
	{
	    holder.login.setText(user.getLogin());
	    holder.add.setOnClickListener(new View.OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    if ((((CheckBox) v).isChecked()))
		    {
			selected.add(user);
		    }
		    else
		    {
			selected.remove(user);
		    }
		}
	    });
	    holder.add.setChecked(selected.contains(user));

	}*/
	return convertView;
    }

    private static class ViewHolder
    {

	TextView login;

	//CheckBox add;
    }
}
