package com.es.hello.chat.ui.fragments;

import com.lat.hello.chat.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyFragment extends Fragment
{

    private int drawable;

    public MyFragment(int drawable)
    {

	this.drawable = drawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

	View view = inflater.inflate(R.layout.fragment_image, container, false);
	ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
	imageView.setImageDrawable(getActivity().getResources().getDrawable(drawable));

	return view;
    }
}
