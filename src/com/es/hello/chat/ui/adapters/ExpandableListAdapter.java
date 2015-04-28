package com.es.hello.chat.ui.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.customobject.Object_SearchExpandChild;
import com.es.hello.chat.customobject.Object_SearchExpandGroup;
import com.lat.hello.chat.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

    private Context _context;

    List<Object_SearchExpandGroup> ListData;

    // private List<String> _listDataHeader; // header titles

    // child data in format of header title, child title
    // private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<Object_SearchExpandGroup> ListData)
    {

	this._context = context;
	this.ListData = ListData;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {

	return this.ListData.get(groupPosition).ListChilds.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {

	return childPosition;
    }

    private static class ViewHolderChild
    {

	ImageView imgAvarta;

	TextView txtName;

	TextView txtStatus;

	TextView txtKM;

    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

	ViewHolderChild viewHolderChild;

	final Object_SearchExpandChild objChild = (Object_SearchExpandChild) getChild(groupPosition, childPosition);

	if (convertView == null)
	{
	    LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.list_item, null);

	    viewHolderChild = new ViewHolderChild();
	    viewHolderChild.imgAvarta = (ImageView) convertView.findViewById(R.id.img_avarta);
	    viewHolderChild.txtName = (TextView) convertView.findViewById(R.id.txtName);
	    viewHolderChild.txtName.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(_context), Typeface.BOLD);
	    viewHolderChild.txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.25f);

	    viewHolderChild.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
	    viewHolderChild.txtStatus.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden_Italic(_context), Typeface.BOLD);
	    viewHolderChild.txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
	    viewHolderChild.txtStatus.setTextColor(Color.parseColor("#666666"));

	    viewHolderChild.txtKM = (TextView) convertView.findViewById(R.id.txtKM);
	    //viewHolderChild.txtKM.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(_context), Typeface.BOLD);
	    //viewHolderChild.txtKM.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
	    //viewHolderChild.txtKM.setTextColor(Color.parseColor("#000000"));

	    convertView.setTag(viewHolderChild);

	    // yourTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
	    // yourTextView.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(ctx),
	    // Typeface.BOLD);
	}
	else
	{
	    viewHolderChild = (ViewHolderChild) convertView.getTag();
	}

	viewHolderChild.imgAvarta.setImageResource(R.drawable.sampleava1);
	viewHolderChild.txtName.setText(objChild.Name);
	viewHolderChild.txtStatus.setText(objChild.Status);
	viewHolderChild.txtKM.setText(objChild.HowFar + "km");

	return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {

	return this.ListData.get(groupPosition).ListChilds.size();

	// return
	// this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {

	return this.ListData.get(groupPosition);

	// return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {

	return this.ListData.size();

	// return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {

	return groupPosition;
    }

    private static class ViewHolderGroup
    {

	// TextView txtFakeGroupIndicator;

	ImageView imgTag1;

	ImageView imgTag2;

	ImageView imgTag3;

	ImageView imgTag4;

	TextView txtNumofUserFound;

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

	ViewHolderGroup viewHolderGroup;

	Object_SearchExpandGroup objGroup = (Object_SearchExpandGroup) getGroup(groupPosition);
	if (convertView == null)
	{
	    LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.list_group, null);

	    viewHolderGroup = new ViewHolderGroup();
	    // viewHolderGroup.txtFakeGroupIndicator =
	    // (TextView)convertView.findViewById(R.id.txtFakeGroupIndicator);
	    viewHolderGroup.imgTag1 = (ImageView) convertView.findViewById(R.id.img_tag_1);
	    viewHolderGroup.imgTag2 = (ImageView) convertView.findViewById(R.id.img_tag_2);
	    viewHolderGroup.imgTag3 = (ImageView) convertView.findViewById(R.id.img_tag_3);
	    viewHolderGroup.imgTag4 = (ImageView) convertView.findViewById(R.id.img_tag_4);

	    viewHolderGroup.txtNumofUserFound = (TextView) convertView.findViewById(R.id.txtNumOfUserFound);
	    viewHolderGroup.txtNumofUserFound.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Conden(_context), Typeface.BOLD);
	    viewHolderGroup.txtNumofUserFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
	    viewHolderGroup.txtNumofUserFound.setTextColor(Color.parseColor("#000000"));

	    convertView.setTag(viewHolderGroup);
	}
	else
	{
	    viewHolderGroup = (ViewHolderGroup) convertView.getTag();
	}

	if (objGroup.IsTag1Found)
	{
	    viewHolderGroup.imgTag1.setVisibility(View.VISIBLE);
	}
	else
	{
	    viewHolderGroup.imgTag1.setVisibility(View.GONE);
	}

	if (objGroup.IsTag2Found)
	{
	    viewHolderGroup.imgTag2.setVisibility(View.VISIBLE);
	}
	else
	{
	    viewHolderGroup.imgTag2.setVisibility(View.GONE);
	}

	if (objGroup.IsTag3Found)
	{
	    viewHolderGroup.imgTag3.setVisibility(View.VISIBLE);
	}
	else
	{
	    viewHolderGroup.imgTag3.setVisibility(View.GONE);
	}

	if (objGroup.IsTag4Found)
	{
	    viewHolderGroup.imgTag4.setVisibility(View.VISIBLE);
	}
	else
	{
	    viewHolderGroup.imgTag4.setVisibility(View.GONE);
	}

	viewHolderGroup.txtNumofUserFound.setText(objGroup.NumOfUserFound + "");

	return convertView;
    }

    @Override
    public boolean hasStableIds()
    {

	return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {

	return true;
    }

}
