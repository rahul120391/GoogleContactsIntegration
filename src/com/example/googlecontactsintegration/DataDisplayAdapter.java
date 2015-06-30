package com.example.googlecontactsintegration;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataDisplayAdapter extends BaseAdapter {

	private ArrayList<DataModel> list;
	LayoutInflater inf;
	TextView tv_email, tv_name;
	CircularImageView  iv_image;

	public DataDisplayAdapter(ArrayList<DataModel> list, Context context) {
		this.list = list;
		inf = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int pos) {
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		if (view == null) {
			view = inf.inflate(R.layout.listview_cell, null);
		}
		tv_email = (TextView) view.findViewById(R.id.tv_email);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		iv_image = (CircularImageView) view.findViewById(R.id.iv_image);
		System.out.println("emails"+list.get(position).getEmail());
		tv_email.setText(list.get(position).getEmail());
		tv_name.setText(list.get(position).getName());
		iv_image.setImageResource(R.drawable.ic_launcher);
		if (list.get(position).getBm() != null) {
			iv_image.setImageBitmap(list.get(position).getBm());
		}
		else{
			iv_image.setImageResource(R.drawable.ic_launcher);
		}

		return view;
	}

}
