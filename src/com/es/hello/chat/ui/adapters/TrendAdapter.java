package com.es.hello.chat.ui.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.es.hello.chat.consts.FontTypeUtils;
import com.lat.hello.chat.R;

public class TrendAdapter extends BaseAdapter
{

    public List<String> dataSource;

    private LayoutInflater inflater;

    private Context ctx;

    public TrendAdapter(List<String> dataSource, Context ctx)
    {

	this.dataSource = dataSource;
	this.ctx = ctx;
	this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {

	// TODO Auto-generated method stub
	return dataSource.size();
    }

    @Override
    public Object getItem(int position)
    {

	// TODO Auto-generated method stub
	return dataSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {

	// TODO Auto-generated method stub
	return position;
    }

    private static class ViewHolder
    {

	EditText txtTrendTag;

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

	ViewHolder holder;
	if (convertView == null)
	{
	    convertView = inflater.inflate(R.layout.list_item_trend, null);
	    holder = new ViewHolder();
	    holder.txtTrendTag = (EditText) convertView.findViewById(R.id.txtTrendTag);
	    holder.txtTrendTag.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(ctx));
	    holder.txtTrendTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
	    holder.txtTrendTag.setTextColor(Color.parseColor("#585745"));

	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	String tTag = dataSource.get(position);
	if (tTag != null && !tTag.equals(""))
	{
	    holder.txtTrendTag.setText(dataSource.get(position));
	}
	else
	{
	    holder.txtTrendTag.setText("# Tap here to type");
	}

	return convertView;
    }

}
