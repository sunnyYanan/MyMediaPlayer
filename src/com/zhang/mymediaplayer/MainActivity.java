package com.zhang.mymediaplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {
	Button previous, play, next;
	ListView list;
	int curPlay;
	boolean isPlaying = false;
	List<String> fileName;
	List<String> filePath;
	List<Map<String, String>> fileList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		curPlay = 0;// 默认播放第一首

		previous = (Button) this.findViewById(R.id.before);
		play = (Button) this.findViewById(R.id.play);
		next = (Button) this.findViewById(R.id.after);
		list = (ListView) findViewById(android.R.id.list);
		findSongs();
		previous.setOnClickListener(new MyButtonListener());
		next.setOnClickListener(new MyButtonListener());
		play.setOnClickListener(new MyButtonListener());
		list.setOnItemClickListener(new MyListListener());
		// System.out.println(this.getExternalCacheDir());// null
		// System.out.println(this.getFilesDir());
	}

	public class MyListListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			curPlay = arg2;
			play();
		}

		private void play() {
			// TODO Auto-generated method stub
			// Intent intent = new Intent("player");
			MediaPlayer mp = new MediaPlayer();
			try {
				mp.setDataSource(filePath.get(curPlay));
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void findSongs() {
		// TODO Auto-generated method stub
		File file = this.getFilesDir();
		// System.out.println(path);
		fileName = new ArrayList<String>();
		filePath = new ArrayList<String>();
		fileList = new ArrayList<Map<String, String>>();

		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			fileName.add(files[i].getName());
			filePath.add(files[i].getPath());
		}
		Map<String, String> data;
		for (int i = 0; i < files.length; i++) {
			data = new HashMap<String, String>();
			data.put("name", fileName.get(i));
			data.put("path", filePath.get(i));
			fileList.add(data);
			System.out.println(fileList.get(i).get("name") + " "
					+ fileList.get(i).get("path"));
		}
		SimpleAdapter adapter = new SimpleAdapter(this, fileList,
				R.layout.song, new String[] { "name", "path" }, new int[] {
						R.id.fileName, R.id.filePath });
		this.setListAdapter(adapter);
	}

	public class MyButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// Log.i("haha", "meme");
			switch (arg0.getId()) {
			case R.id.before:
				System.out.println("1");
				break;
			case R.id.play:
				System.out.println("2");
				break;
			case R.id.after:
				System.out.println("3");
				break;
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
