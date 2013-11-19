package com.zhang.mediaplayer.service;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhang.mediaplayer.model.FileInfo;
import com.zhang.mymediaplayer.R;

public class MyFileAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<FileInfo> fileInfos;

	public MyFileAdapter(Context context, ArrayList<FileInfo> fileInfos) {
		super();
		inflater = LayoutInflater.from(context);
		// inflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fileInfos = fileInfos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileInfos.size();
	}

	@Override
	public FileInfo getItem(int arg0) {
		// TODO Auto-generated method stub
		return fileInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.activity_file_choose_item, null);
			viewHolder = new ViewHolder(arg1);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}

		FileInfo fileInfo = this.getItem(arg0);
		viewHolder.fileName.setText(fileInfo.getFileName());
		if (fileInfo.isDir()) {
			viewHolder.filePic.setImageResource(R.drawable.folder);
		} else if (fileInfo.isMusicFile()) {
			viewHolder.filePic.setImageResource(R.drawable.mp3);
		} else
			viewHolder.filePic.setImageResource(R.drawable.file_unknown);
		return arg1;
	}

	class ViewHolder {
		TextView fileName;
		ImageView filePic;

		public ViewHolder(View view) {
			filePic = (ImageView) view.findViewById(R.id.filePic);
			fileName = (TextView) view.findViewById(R.id.fileName);
		}
	}
}
