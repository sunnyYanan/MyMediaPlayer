package com.zhang.mymediaplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;

public class MainActivity extends ListActivity {
	Button previous, play, next;
	ListView list;
	SeekBar sb;
	Handler hd = new Handler();
	int curPlay;
	boolean isPlaying = false;
	MediaPlayer mp;
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
		sb = (SeekBar) this.findViewById(R.id.seekBar);
		findSongs();
		previous.setOnClickListener(new MyButtonListener());
		next.setOnClickListener(new MyButtonListener());
		play.setOnClickListener(new MyButtonListener());
		list.setOnItemClickListener(new MyListListener());
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				mp.seekTo(arg0.getProgress());
			}

		});
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
	}

	private void play() {
		// TODO Auto-generated method stub
		// Intent intent = new Intent("player");
		// 如果当前正在播放，就释放
		isPlaying = true;
		changePlayButtonShow();
		if (mp != null) {
			mp.release();
			mp = null;
		}
		mp = new MediaPlayer();
		// 设置进度条位置
		/*
		 * Thread t = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * sb.setProgress(mp.getCurrentPosition()); }
		 * 
		 * }); t.start();
		 */
		try {
			mp.setDataSource(filePath.get(curPlay));
			mp.prepareAsync();
			mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();// 异步准备数据的方法，service是可以在用户与其他应用交互时仍运行，此时需要wake
								// lock
					mp.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							next();
						}
					});
					setSeekBar();
				}
			});
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

	// 只有mp准备好以后，才能调用mp的方法
	private void setSeekBar() {
		// TODO Auto-generated method stub

		sb.setMax(mp.getDuration());
		// 发送一个Runnable, handler收到之后就会执行run()方法
		hd.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sb.setProgress(mp.getCurrentPosition());
				hd.postDelayed(this, 1000);
			}

		});
	}

	private void next() {
		// TODO Auto-generated method stub
		if (curPlay == fileName.size() - 1) {
			curPlay = 0;
		} else {
			curPlay += 1;
		}
		play();
	}

	private void changePlayButtonShow() {
		// TODO Auto-generated method stub
		if (isPlaying) {
			play.setText("stop");
		} else {
			play.setText("play");
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
				R.layout.song_item, new String[] { "name", "path" }, new int[] {
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
				if(curPlay==0)
					curPlay=fileName.size()-1;
				else
					curPlay--;
				play();
				break;
			case R.id.play:
				if(isPlaying) {
					mp.pause();
				}else {
					if(mp== null)
						play();
					else
						mp.start();
				}
				isPlaying = !isPlaying;
				changePlayButtonShow();
				break;
			case R.id.after:
				if(curPlay==fileName.size()-1)
					curPlay= 0;
				else
					curPlay++;
				play();
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
