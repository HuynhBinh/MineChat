package com.es.hello.chat.ui.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.es.hello.chat.StaticFunction;
import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.consts.SharePrefsHelper;
import com.es.hello.chat.ui.activities.Activity_View_Attachment_OtherType;
import com.es.hello.chat.ui.activities.Activity_View_Attachment_With_Slide;
import com.es.hello.chat.ui.activities.ChatActivity.Mode;
import com.lat.hello.chat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBMessage;
import com.quickblox.users.model.QBUser;

public class ChatAdapter extends BaseAdapter
{

    // private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private static final String DATE_FORMAT1 = "HH:mm";

    private static final String DATE_FORMAT2 = "dd/MM/yyyy";

    public List<QBMessage> chatMessages;

    private Activity context;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private Mode mode;

    List<String> listUserNameForGroup = new ArrayList<String>();

    List<String> listColorString = new ArrayList<String>();

    public int count = 0;

    List<QBMessage> chatMessagesListToShowDate = new ArrayList<QBMessage>();

    @SuppressWarnings("deprecation")
    public ChatAdapter(Activity context, List<QBMessage> chatMessages, Mode mode1)
    {

	this.context = context;
	this.chatMessages = chatMessages;
	this.mode = mode1;

	listColorString.add("#3333FF");
	listColorString.add("#FF3333");
	listColorString.add("#FFFF00");
	listColorString.add("#9900CC");
	listColorString.add("#00DD00");
	listColorString.add("#000000");
	listColorString.add("#EE9A00");
	listColorString.add("#FFCCFF");
	listColorString.add("#BBBBBB");
	listColorString.add("#2F4F4F");
	listColorString.add("#F4A460");
	listColorString.add("#6B8E23");
	listColorString.add("#A0522D");
	listColorString.add("#8470FF");
	listColorString.add("#8FBC8F");
	listColorString.add("#FFEC8B");
	listColorString.add("#8B658B");
	listColorString.add("#D2691E");
	listColorString.add("#FF1493");
	listColorString.add("#008080");

	StaticFunction.initImageLoader(this.context);
	options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).build();
    }

    @Override
    public int getCount()
    {

	if (chatMessages != null)
	{
	    return chatMessages.size();
	}
	else
	{
	    return 0;
	}
    }

    @Override
    public QBMessage getItem(int position)
    {

	if (chatMessages != null)
	{
	    return chatMessages.get(position);
	}
	else
	{
	    return null;
	}
    }

    @Override
    public long getItemId(int position)
    {

	return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

	final ViewHolder holder;
	final QBMessage chatMessage = getItem(position);
	LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	if (convertView == null)
	{
	    convertView = vi.inflate(R.layout.list_item_message, null);
	    holder = createViewHolder(convertView);

	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	// QBUser currentUser = ApplicationSingleton.getCurrentUser();

	int currentLoginUserID = SharePrefsHelper.getCurrentLoginUserID(this.context);

	boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId() == currentLoginUserID;
	setAlignment(holder, isOutgoing);

	String senderUsername = "";
	String messBody = "";

	String isSetTimeBar = chatMessage.getProperty("is_set_time_bar");

	if (isSetTimeBar != null)
	{
	    if (isSetTimeBar.equals("true"))
	    {
		holder.txtTimeForGroup.setVisibility(View.VISIBLE);
		holder.containerTxtTimeForGroup.setVisibility(View.VISIBLE);
		holder.txtTimeForGroup.setText(getFULLTimeTextForGroup(chatMessage));

	    }
	    else
	    {
		holder.txtTimeForGroup.setVisibility(View.GONE);
		holder.containerTxtTimeForGroup.setVisibility(View.GONE);

	    }
	}
	else
	{
	    holder.txtTimeForGroup.setVisibility(View.GONE);
	    holder.containerTxtTimeForGroup.setVisibility(View.GONE);
	}

	if (this.mode == Mode.GROUP)
	{

	    if (chatMessage.getSenderId() != null)
	    {

		if (chatMessage.getSenderId() != currentLoginUserID)
		{
		    QBUser opponentUser = StaticFunction.getUserById(chatMessage.getSenderId());
		    senderUsername = opponentUser.getLogin();
		}

	    }

	    if (!senderUsername.equals(""))
	    {

		if (!listUserNameForGroup.contains(senderUsername))
		{
		    listUserNameForGroup.add(senderUsername);
		}
		else
		{

		}

		int pos = listUserNameForGroup.indexOf(senderUsername);

		String colorStr = "#C71585";

		if (pos < listColorString.size())
		{
		    colorStr = listColorString.get(pos);
		}

		holder.txtUserNameForGroup.setText(senderUsername + ":");
		holder.txtUserNameForGroup.setTextColor(Color.parseColor(colorStr));
		holder.txtUserNameForGroup.setVisibility(View.VISIBLE);
		// messBody += senderUsername + ": ";
	    }
	    else
	    {
		holder.txtUserNameForGroup.setText("");
		holder.txtUserNameForGroup.setVisibility(View.GONE);
	    }
	}
	else
	{

	    holder.txtUserNameForGroup.setVisibility(View.GONE);
	}

	messBody += chatMessage.getBody();

	holder.txtMessage.setText(messBody);
	count++;

	if (chatMessage.getAttachments() != null)
	{
	    if (chatMessage.getAttachments().size() > 0)
	    {

		holder.txtTimeForPix.setVisibility(View.VISIBLE);
		holder.txtInfo.setVisibility(View.GONE);

		// holder.txtMessage.setVisibility(View.GONE);
		holder.imgAttach.setVisibility(View.VISIBLE);

		String bodys = chatMessage.getBody();

		if (bodys != null && !bodys.equals(""))
		{
		    holder.txtMessage.setText(chatMessage.getBody());
		    holder.txtMessage.setVisibility(View.VISIBLE);
		}
		else
		{
		    holder.txtMessage.setText("");
		    holder.txtMessage.setVisibility(View.GONE);
		}

		for (final QBAttachment att : chatMessage.getAttachments())
		{
		    if (att.getType().equals("photo"))
		    {

			imageLoader.displayImage(att.getUrl(), holder.imgAttach, options);

			holder.imgAttach.setOnClickListener(new OnClickListener()
			{

			    @Override
			    public void onClick(View v)
			    {

				Intent intent = new Intent(context, Activity_View_Attachment_With_Slide.class);
				Activity_View_Attachment_With_Slide.imgPath = att.getUrl();
				Activity_View_Attachment_With_Slide.DialogID = chatMessage.getDialogId();

				if (Activity_View_Attachment_With_Slide.DialogID == null)
				{
				    Activity_View_Attachment_With_Slide.DialogID = chatMessage.getProperty("dialog_id");
				}

				context.startActivity(intent);

			    }
			});
		    }
		    else
		    {

			holder.imgAttach.setImageResource(R.drawable.file_ic);

			LinearLayout.LayoutParams param = new LayoutParams(128, 128);
			holder.imgAttach.setLayoutParams(param);

			// holder.imgAttach.setPadding(50, 50, 50, 50);

			holder.imgAttach.setOnClickListener(new OnClickListener()
			{

			    @Override
			    public void onClick(View v)
			    {

				// view other file type here
				Intent intent = new Intent(context, Activity_View_Attachment_OtherType.class);
				Activity_View_Attachment_OtherType.urlFilePath = att.getUrl();
				context.startActivity(intent);

			    }
			});
		    }

		}

	    }
	    else
	    {
		holder.txtMessage.setVisibility(View.VISIBLE);
		holder.imgAttach.setVisibility(View.GONE);
		holder.txtTimeForPix.setVisibility(View.GONE);
		holder.txtInfo.setVisibility(View.VISIBLE);
	    }

	}
	else
	{
	    holder.txtMessage.setVisibility(View.VISIBLE);
	    holder.imgAttach.setVisibility(View.GONE);
	    holder.txtTimeForPix.setVisibility(View.GONE);
	    holder.txtInfo.setVisibility(View.VISIBLE);
	}

	if (chatMessage.getSenderId() != null)
	{
	    // chatMessage.getSenderId() + ": " +
	    holder.txtInfo.setText(getTimeText(chatMessage));
	    holder.txtTimeForPix.setText(getTimeText(chatMessage));
	}
	else
	{
	    holder.txtInfo.setText(getTimeText(chatMessage));
	    holder.txtTimeForPix.setText(getTimeText(chatMessage));
	}

	if (isOutgoing == true)
	{
	    String mess_status = chatMessage.getProperty("mess_status");
	    if (mess_status != null)
	    {
		if (mess_status.equalsIgnoreCase("fail"))
		{
		    holder.txtMessStatus.setTextColor(Color.RED);
		}
		else
		{
		    holder.txtMessStatus.setTextColor(Color.parseColor("#4c4c4c"));
		}
		holder.txtMessStatus.setVisibility(View.VISIBLE);
		holder.txtMessStatus.setText(mess_status);
	    }
	    else
	    {
		holder.txtMessStatus.setVisibility(View.GONE);
	    }
	}
	else
	{
	    holder.txtMessStatus.setVisibility(View.GONE);
	}

	return convertView;
    }

    public void add(QBMessage message)
    {

	chatMessages.add(message);
    }

    public void add(List<QBMessage> messages)
    {

	chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing)
    {

	if (isOutgoing)
	{
	    holder.contentWithBG.setBackgroundResource(R.drawable.bubble2);

	    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
	    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
	    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
	    layoutParams.gravity = Gravity.CENTER_VERTICAL;
	    holder.contentWithBG.setLayoutParams(layoutParams);

	    //
	    int padding_in_dp = 10;
	    final float scale = context.getResources().getDisplayMetrics().density;
	    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
	    //

	    //
	    int padding_in_dp1 = 5;
	    int padding_in_px1 = (int) (padding_in_dp1 * scale + 0.5f);
	    //

	    holder.contentWithBG.setPadding(padding_in_px1, 0, padding_in_px, 0);

	    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
	    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
	    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    holder.content.setLayoutParams(lp);

	    layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
	    layoutParams.gravity = Gravity.LEFT;
	    //
	    int padding_in_dp2 = 0;
	    int padding_in_px2 = 0;// (int) (padding_in_dp2 * scale + 0.5f);
	    //
	    layoutParams.setMargins(padding_in_px2, 0, 0, 0);
	    holder.txtMessage.setLayoutParams(layoutParams);

	    layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
	    layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
	    holder.txtInfo.setLayoutParams(layoutParams);

	}
	else
	{
	    holder.contentWithBG.setBackgroundResource(R.drawable.bubble1);

	    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
	    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
	    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
	    layoutParams.gravity = Gravity.CENTER_VERTICAL;
	    holder.contentWithBG.setLayoutParams(layoutParams);

	    //
	    int padding_in_dp = 15;
	    final float scale = context.getResources().getDisplayMetrics().density;
	    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
	    //

	    //
	    int padding_in_dp1 = 5;
	    int padding_in_px1 = (int) (padding_in_dp1 * scale + 0.5f);
	    //

	    holder.contentWithBG.setPadding(padding_in_px, 0, padding_in_px1, 0);

	    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
	    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
	    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    holder.content.setLayoutParams(lp);

	    layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
	    layoutParams.gravity = Gravity.LEFT;
	    //
	    int padding_in_dp2 = 0;
	    int padding_in_px2 = 0;// (int) (padding_in_dp2 * scale + 0.5f);
	    //
	    layoutParams.setMargins(padding_in_px2, 0, 0, 0);
	    holder.txtMessage.setLayoutParams(layoutParams);

	    layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
	    layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
	    holder.txtInfo.setLayoutParams(layoutParams);

	}
    }

    private ViewHolder createViewHolder(View v)
    {

	ViewHolder holder = new ViewHolder();
	holder.txtUserNameForGroup = (TextView) v.findViewById(R.id.txtUserNameForGroup);
	holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
	holder.txtMessage.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(context), Typeface.NORMAL);
	holder.txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.2f);
	holder.txtTimeForPix = (TextView) v.findViewById(R.id.txtInfoForPix);

	holder.content = (LinearLayout) v.findViewById(R.id.content);
	holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
	holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
	holder.imgAttach = (ImageView) v.findViewById(R.id.imageAttachment);
	holder.txtMessStatus = (TextView) v.findViewById(R.id.txtMessStatus);
	holder.txtTimeForGroup = (TextView) v.findViewById(R.id.txtTimeForGroup);
	holder.containerTxtTimeForGroup = (LinearLayout) v.findViewById(R.id.ContainertxtTimeForGroup);

	return holder;
    }

    private String getTimeText(QBMessage message)
    {

	Date date;

	try
	{

	    String strTime = message.getProperty("sent_time");
	    long longTime = Long.parseLong(strTime);

	    date = new Date(longTime);
	}
	catch (Exception ex)
	{
	    date = new Date();

	}

	/*if (message instanceof QBChatHistoryMessage)
	{
	    date = new Date(((QBChatHistoryMessage) message).getDateSent() * 1000);
	}
	else
	{
	    date = new Date();
	}*/
	return DateFormat.format(DATE_FORMAT1, date.getTime()).toString();
    }

    @SuppressWarnings("unused")
    private String getDateTextForGroup(QBMessage message)
    {

	Date date;
	if (message instanceof QBChatHistoryMessage)
	{
	    date = new Date(((QBChatHistoryMessage) message).getDateSent() * 1000);
	}
	else
	{
	    date = new Date();
	}
	return DateFormat.format(DATE_FORMAT2, date.getTime()).toString();
    }

    private String getFULLTimeTextForGroup(QBMessage message)
    {

	Date date;

	try
	{

	    String strTime = message.getProperty("sent_time");
	    long longTime = Long.parseLong(strTime);

	    date = new Date(longTime);
	}
	catch (Exception ex)
	{
	    date = new Date();

	}

	return DateFormat.format(DATE_FORMAT2, date.getTime()).toString();
    }

    private static class ViewHolder
    {

	public TextView txtUserNameForGroup;

	public TextView txtMessage;

	public TextView txtInfo;

	public LinearLayout content;

	public LinearLayout contentWithBG;

	public ImageView imgAttach;

	public TextView txtMessStatus;

	public TextView txtTimeForGroup;

	public LinearLayout containerTxtTimeForGroup;

	public TextView txtTimeForPix;

    }

    // return list position to show date
    /*private void getDate(List<QBMessage> chatMessagesList)
    {

    for (int i = 0; i < chatMessagesList.size(); i++)
    {

        String strDateTime = getFULLTimeTextForGroup(chatMessagesList.get(i));
        StringTokenizer stn = new StringTokenizer(strDateTime, ":");

        String strDate1 = stn.nextToken();
        String strTime1 = stn.nextToken();
        
        if (listDateForGroup.contains(strDate1))
        {
    	compareDateReturnMinDate(date1, date2)
        }
        else
        {
    	chatMessagesListToShowDate.add(chatMessagesList.get(i));
    	listDateForGroup.add(strDate1);
        }

    }

    }*/

    @SuppressWarnings("unused")
    private Date compareDateReturnMinDate(Date date1, Date date2)
    {

	if (date1.before(date2))
	{
	    return date1;
	}
	else
	{
	    return date2;

	}
    }

    class GroupTime
    {

	public String strDate;

	public String strTime;
    }
}
