package com.es.hello.chat.ui.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.es.hello.chat.consts.FontTypeUtils;
import com.es.hello.chat.customobject.ObjectSearch4;
import com.lat.hello.chat.R;

public class Search4Adapter extends BaseAdapter
{

    // public List<String> dataSource;

    public List<ObjectSearch4> dataSource;

    private LayoutInflater inflater;

    private Activity ctx;

    // private Activity activity;

    public Search4Adapter(List<ObjectSearch4> dataSource, Activity ctx)
    {

	this.dataSource = dataSource;
	this.ctx = ctx;
	this.inflater = LayoutInflater.from(ctx);
	// this.activity = ()ctx;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

	final ViewHolder holder;
	if (convertView == null)
	{
	    convertView = inflater.inflate(R.layout.list_item_search_tag, null);
	    holder = new ViewHolder();
	    holder.container = (LinearLayout) convertView.findViewById(R.id.containerTxtSearchTagHash);
	    holder.txtSearchTag = (EditText) convertView.findViewById(R.id.txtSearchTag);

	    holder.txtSearchTag.setTypeface(FontTypeUtils.getFont_Myriad_Pro_Regular(ctx), Typeface.BOLD);
	    holder.txtSearchTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	if (position == 0)
	{
	    holder.container.setBackgroundResource(R.drawable.searchbar1);
	    // holder.txtSearchTag.setBackgroundResource(R.drawable.searchbar1);
	}
	else if (position == 1)
	{
	    holder.container.setBackgroundResource(R.drawable.searchbar2);
	    // holder.txtSearchTag.setBackgroundResource(R.drawable.searchbar2);
	}
	else if (position == 2)
	{
	    holder.container.setBackgroundResource(R.drawable.searchbar3);
	    // holder.txtSearchTag.setBackgroundResource(R.drawable.searchbar3);
	}
	else if (position == 3)
	{
	    holder.container.setBackgroundResource(R.drawable.searchbar4);
	    // holder.txtSearchTag.setBackgroundResource(R.drawable.searchbar4);
	}

	String searchTag = dataSource.get(position).strTag;
	if (searchTag != null)
	{
	    holder.txtSearchTag.setText(searchTag);
	}
	else
	{
	    // holder.txtSearchTag.setText("# Tap here to type");
	}

	holder.txtSearchTag.addTextChangedListener(new TextWatcher()
	{

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count)
	    {

		s = s.toString().trim();

		if (s.length() > 0)
		{
		    holder.txtSearchTag.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		    // String sasas = dataSource.get(position);
		    dataSource.get(position).strTag = s.toString().trim();

		}
		else
		{
		    holder.txtSearchTag.setGravity(Gravity.CENTER);
		}

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after)
	    {

	    }

	    @Override
	    public void afterTextChanged(Editable s)
	    {

		String result = s.toString().replaceAll(" ", "");
		if (!s.toString().equals(result))
		{
		    holder.txtSearchTag.setText(result);
		    holder.txtSearchTag.setSelection(result.length());
		    // alert the user
		}
	    }
	});

	holder.txtSearchTag.setOnEditorActionListener(new OnEditorActionListener()
	{

	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	    {

		new Handler().postDelayed(new Runnable()
		{

		    @Override
		    public void run()
		    {

			ctx.getWindow().getDecorView().clearFocus();
		    }
		}, 500);

		return false;
	    }
	});

	return convertView;
    }

    public void hideKeyboard(View view)
    {

	InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static class ViewHolder
    {

	LinearLayout container;

	EditText txtSearchTag;

    }

}
