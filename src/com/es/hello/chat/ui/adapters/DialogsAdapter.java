package com.es.hello.chat.ui.adapters;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.es.hello.chat.ApplicationSingleton;
import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.sugarobject.Sugar_Dialog;
import com.es.hello.chat.sugarobject.Sugar_Message;
import com.es.hello.chat.sugarobject.Sugar_Noti;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

public class DialogsAdapter extends BaseAdapter
{

    private static final String DATE_FORMAT1 = "HH:mm";

    private static final String DATE_FORMAT2 = "dd/MM/yyyy";

    private List<QBDialog> dataSource;

    private LayoutInflater inflater;

    private Activity ctx;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private String FROM = "";

    public List<QBDialog> selected = new ArrayList<QBDialog>();

    public class MyComparator implements Comparator<QBDialog>
    {

	public int compare(QBDialog dg1, QBDialog dg2)
	{

	    if (dg1.getLastMessageDateSent() < dg2.getLastMessageDateSent())
		return 1;

	    if (dg1.getLastMessageDateSent() == dg2.getLastMessageDateSent())
		return 0;
	    return -1;
	}
    }

    @SuppressWarnings("deprecation")
    public DialogsAdapter(List<QBDialog> dataSource, Activity ctx, String from)
    {

	this.dataSource = dataSource;

	for (int i = 0; i < this.dataSource.size(); i++)
	{
	    this.dataSource.get(i).setLastMessageDateSent(findLastMessageTimeforDialog(this.dataSource.get(i).getDialogId()));
	}

	Collections.sort(this.dataSource, new MyComparator());

	this.inflater = LayoutInflater.from(ctx);
	this.ctx = ctx;
	this.FROM = from;

	if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Settings"))
	{
	    // this.selected =
	    // ApplicationSingleton.selectedDialogToCreateGroupChat;
	}
	else if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Step2"))
	{
	    this.selected = ApplicationSingleton.selectedDialogToCreateGroupChat;
	}

	StaticFunction.initImageLoader(this.ctx);

	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();
    }

    public List<QBDialog> getSelected()
    {

	return selected;
    }

    public List<QBDialog> getDataSource()
    {

	return dataSource;
    }

    public void setDataSource(List<QBDialog> DataSource)
    {

	this.dataSource = DataSource;
    }

    @Override
    public long getItemId(int position)
    {

	return position;
    }

    @Override
    public Object getItem(int position)
    {

	return dataSource.get(position);
    }

    @Override
    public int getCount()
    {

	return dataSource.size();
    }

    public String getMonth(int month)
    {

	return new DateFormatSymbols().getShortMonths()[month];
    }

    private long findLastMessageTimeforDialog(String dialogID)
    {

	long lastMessTime = 0;

	String[] str = new String[1];
	str[0] = dialogID;
	List<Sugar_Message> listsss = Sugar_Message.find(Sugar_Message.class, "MESS_CHAT_DIALOG_ID = ?", str, "", "MESS_DATE_SENT DESC", "1");

	if (listsss.size() > 0)
	{
	    lastMessTime = listsss.get(0).messDateSent;
	}
	else
	{

	    // return 0;
	    Sugar_Dialog sDialog = StaticFunction.findDialogInDBByID(dialogID);
	    if (sDialog != null)
	    {

		lastMessTime = sDialog.dialogLastMessageDateSent * 1000;

	    }

	}

	return lastMessTime;

    }

    @SuppressLint(
    {
    "InflateParams", "SimpleDateFormat"
    })
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

	ViewHolder holder;

	// init view
	if (convertView == null)
	{

	    convertView = inflater.inflate(R.layout.item_dialog, null);
	    holder = new ViewHolder();

	    holder.dialogContainer = (LinearLayout) convertView.findViewById(R.id.dialogContainer);
	    holder.viewDivider = (View) convertView.findViewById(R.id.viewDivider);

	    holder.name = (TextView) convertView.findViewById(R.id.roomName);

	    holder.name.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(ctx), Typeface.BOLD);
	    holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.25f);

	    holder.lastMessage = (TextView) convertView.findViewById(R.id.lastMessage);
	    holder.lastMessage.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(ctx), Typeface.BOLD);
	    holder.lastMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
	    holder.lastMessage.setTextColor(Color.parseColor("#666666"));

	    holder.groupType = (TextView) convertView.findViewById(R.id.textViewGroupType);
	    holder.imgDialog = (ImageView) convertView.findViewById(R.id.roomImage);

	    holder.txtTime = (TextView) convertView.findViewById(R.id.textTime);
	    holder.txtTime.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(ctx), Typeface.BOLD);
	    holder.txtTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

	    holder.chkAddToGroup = (CheckBox) convertView.findViewById(R.id.checkBoxAddToGroup);

	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	final QBDialog dialog = dataSource.get(position);

	if (this.FROM.equalsIgnoreCase("Activity_Group_Chat_Settings") || this.FROM.equalsIgnoreCase("Activity_Group_Chat_Step2"))
	{
	    if (dialog.getType().equals(QBDialogType.GROUP))
	    {

		holder.imgDialog.setVisibility(View.GONE);
		holder.lastMessage.setVisibility(View.GONE);
		holder.name.setVisibility(View.GONE);
		holder.txtTime.setVisibility(View.GONE);
		holder.chkAddToGroup.setVisibility(View.GONE);
		holder.dialogContainer.setVisibility(View.GONE);
		holder.viewDivider.setVisibility(View.GONE);
	    }
	    else
	    {
		// if(dialog.getOccupants().c)
		boolean isUserBelongToDialog = isUserBelongToDialog(dialog);
		if (isUserBelongToDialog == true)
		{
		    holder.imgDialog.setVisibility(View.GONE);
		    holder.lastMessage.setVisibility(View.GONE);
		    holder.name.setVisibility(View.GONE);
		    holder.txtTime.setVisibility(View.GONE);
		    holder.chkAddToGroup.setVisibility(View.GONE);
		    holder.dialogContainer.setVisibility(View.GONE);
		    holder.viewDivider.setVisibility(View.GONE);
		}
		else
		{
		    holder.imgDialog.setVisibility(View.VISIBLE);
		    holder.lastMessage.setVisibility(View.VISIBLE);
		    holder.name.setVisibility(View.VISIBLE);
		    holder.txtTime.setVisibility(View.GONE);
		    holder.chkAddToGroup.setVisibility(View.VISIBLE);
		    holder.dialogContainer.setVisibility(View.VISIBLE);
		    holder.viewDivider.setVisibility(View.VISIBLE);
		}

	    }

	    holder.chkAddToGroup.setOnClickListener(new View.OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{

		    if ((((CheckBox) v).isChecked()))
		    {
			selected.add(dialog);
		    }
		    else
		    {
			removeDialog(dialog);
		    }
		}
	    });

	    holder.chkAddToGroup.setChecked(isExist(dialog));
	    holder.groupType.setVisibility(View.GONE);

	}
	else
	{
	    holder.chkAddToGroup.setVisibility(View.GONE);
	    holder.groupType.setVisibility(View.VISIBLE);

	    int unreadCount = 0;
	    unreadCount = findUnReadMessage(dialog.getDialogId());
	    if (unreadCount != 0)
	    {
		holder.groupType.setText(unreadCount + "");
		holder.groupType.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		holder.groupType.setText("");
		holder.groupType.setVisibility(View.GONE);

	    }
	}

	if (dialog.getType().equals(QBDialogType.GROUP))
	{
	    holder.name.setText(dialog.getName());

	    String dialogPhoto = null;
	    dialogPhoto = dialog.getPhoto();

	    if (dialogPhoto != null)
	    {
		imageLoader.displayImage(dialogPhoto, holder.imgDialog, options);
	    }
	    else
	    {
		holder.imgDialog.setImageResource(R.drawable.ic_room);
	    }

	}
	else
	{

	    // get opponent name for private dialog
	    Integer opponentID = StaticFunction.getOpponentIDForPrivateDialog(dialog, this.ctx);

	    QBUser user = StaticFunction.getUserById(opponentID);// ApplicationSingleton.getDialogsUsers().get(opponentID);

	    if (user != null)
	    {
		holder.name.setText(user.getLogin() == null ? user.getFullName() : user.getLogin());

		if (user.getWebsite() != null)
		{
		    imageLoader.displayImage(user.getWebsite(), holder.imgDialog, options);
		}
		else
		{
		    holder.imgDialog.setImageResource(R.drawable.ic_contact_picture);
		}
	    }

	}

	// set date time for last mess
	Date date = null;
	try
	{

	    long lastmesstime = 0;
	    lastmesstime = dialog.getLastMessageDateSent();

	    if (lastmesstime > 1111111111111111l)
	    {
		Log.e("", "");
	    }

	    if (lastmesstime != 0)
	    {

		Timestamp stamp = new Timestamp(lastmesstime);

		date = new Date(stamp.getTime());
	    }
	    else
	    {
		date = null;
	    }

	}
	catch (Exception ex)
	{
	    date = new Date();
	}

	Date today = new Date();

	if (date != null)
	{

	    String displayTime = "";

	    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
	    if (fmt.format(date).equals(fmt.format(today)))
	    {
		displayTime += DateFormat.format(DATE_FORMAT1, date.getTime()).toString();
	    }
	    else
	    {

		displayTime += DateFormat.format(DATE_FORMAT2, date.getTime()).toString();

	    }

	    holder.txtTime.setText(displayTime);
	}
	else
	{
	    holder.txtTime.setText("");

	}
	// set date time for last mess

	String strLastMess = StaticFunction.findLastMessageforDialog(dialog.getDialogId());
	holder.lastMessage.setText(strLastMess);

	return convertView;
    }

    @SuppressLint("SimpleDateFormat")
    public String convertTime(long time)
    {

	Date date = new Date(time);
	Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
	return format.format(date);
    }

    private static class ViewHolder
    {

	LinearLayout dialogContainer;

	TextView name;

	TextView lastMessage;

	TextView groupType;

	ImageView imgDialog;

	TextView txtTime;

	CheckBox chkAddToGroup;

	View viewDivider;
    }

    private boolean isExist(QBDialog dialog)
    {

	for (QBDialog dg : selected)
	{
	    if (dg.getDialogId().equalsIgnoreCase(dialog.getDialogId()))
	    {
		return true;
	    }

	}
	return false;
    }

    private void removeDialog(QBDialog dialog)
    {

	for (int i = 0; i < selected.size(); i++)
	{
	    QBDialog dg = selected.get(i);

	    if (dg.getDialogId().equalsIgnoreCase(dialog.getDialogId()))
	    {
		selected.remove(i);
	    }
	}

    }

    private boolean isUserBelongToDialog(QBDialog dialog)
    {

	Integer currentUserID = SharePrefsHelper.getCurrentLoginUserID(ctx);
	for (int i = 0; i < ApplicationSingleton.currentSelectedUsersInGroup.size(); i++)
	{
	    Integer userID = ApplicationSingleton.currentSelectedUsersInGroup.get(i).getId();
	    if (dialog.getOccupants().contains(userID))
	    {

		if (userID.equals(currentUserID))
		{
		    //return false;
		}
		else
		{
		    return true;
		}

	    }
	}

	return false;
    }

    private int findUnReadMessage(String dialogId)
    {

	int hashcodeId = 0;
	hashcodeId = dialogId.hashCode();

	List<Sugar_Noti> listSNoti = Sugar_Noti.find(Sugar_Noti.class, "hashcode_Id = ?", hashcodeId + "");

	if (listSNoti.size() > 0)
	{
	    return listSNoti.get(0).message;
	}
	else
	{
	    return 0;
	}

    }
}
