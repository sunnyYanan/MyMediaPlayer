package com.zhang.mymediaplayer;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.mediaplayer.model.FileInfo;
import com.zhang.mediaplayer.service.MyFileAdapter;

public class FileChoose extends Activity {
	TextView nowPath;
	GridView filesView;
	// Button ok;
	Button back;

	String rootPath = "/";
	String lastFilePath;

	ArrayList<FileInfo> fileInfos;
	MyFileAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_choose);
		// Show the Up button in the action bar.
		setupActionBar();

		fileInfos = new ArrayList<FileInfo>();

		nowPath = (TextView) this.findViewById(R.id.nowPath);
		filesView = (GridView) this.findViewById(R.id.allFiles);
		// ok = (Button) this.findViewById(R.id.ok);
		back = (Button) this.findViewById(R.id.back);
		back.setOnClickListener(new MyClickListener());

		filesView.setEmptyView(findViewById(android.R.id.empty));
		// filesView.setNumColumns(GridView.AUTO_FIT);
		filesView.setOnItemClickListener(new MyOnItemClickListener());
		filesView.setNumColumns(4);

		setGridViewAdapter(rootPath);
//		updateFileView(rootPath);
	}

	private void setGridViewAdapter(String rootPath) {
		// TODO Auto-generated method stub
		updateFileView(rootPath);
		adapter = new MyFileAdapter(this, fileInfos);
		filesView.setAdapter(adapter);
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			FileInfo fileInfo = (FileInfo) (((MyFileAdapter) arg0.getAdapter())
					.getItem(arg2));
			if (fileInfo.isDir()) {
				updateFileView(fileInfo.getFilePath());
			} else if (fileInfo.isMusicFile()) {
				Intent intent = new Intent();
				intent.putExtra(MainActivity.MUSICFILEPATH,
						fileInfo.getFilePath());
				setResult(RESULT_OK, intent);
				finish();
			} else {
				toast("文件类型错误");
			}
		}

	}

	private void toast(CharSequence hint) {
		Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
	}

	private class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(lastFilePath.equals(rootPath))
				return;
			File file = new File(lastFilePath);
			File parent = file.getParentFile();
			updateFileView(parent.getAbsolutePath());
		}

	}

	private void updateFileView(String path) {
		// TODO Auto-generated method stub
		nowPath.setText(path);
		lastFilePath = path;
		File file = new File(path);
		File[] files = file.listFiles();
		if (!fileInfos.isEmpty())
			fileInfos.clear();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
//			if (files[i].isHidden())
//				continue;
			boolean isDir = files[i].isDirectory();
			FileInfo fileInfo = new FileInfo(files[i].getName(),
					files[i].getAbsolutePath(), isDir);
			fileInfos.add(fileInfo);
		}
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_choose, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
