package com.zhang.mymediaplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	Button previous, play, next, first, last;
	ListView list;
	SeekBar sb, volume;
	TextView volumeValue;
	Handler hd = new Handler();
	int curPlay;
	boolean isPlaying = false;
	MediaPlayer mp;
	List<String> fileName;
	List<String> filePath;
	List<Map<String, String>> fileList;
	int playMode = 1;
	AudioManager am;
	int currentVolume;
	int maxVolume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		curPlay = 0;// 默认播放第一首

		previous = (Button) this.findViewById(R.id.before);
		play = (Button) this.findViewById(R.id.play);
		next = (Button) this.findViewById(R.id.after);
		first = (Button) this.findViewById(R.id.first);
		last = (Button) this.findViewById(R.id.last);
		list = (ListView) findViewById(android.R.id.list);
		sb = (SeekBar) this.findViewById(R.id.seekBar);
		volume = (SeekBar) this.findViewById(R.id.volume);
		volumeValue = (TextView) this.findViewById(R.id.volumeValue);

		findSongs();

		if (fileList.size() == 0) {
			previous.setEnabled(false);
			next.setEnabled(false);
			play.setEnabled(false);
		}
		previous.setOnClickListener(new MyButtonListener());
		next.setOnClickListener(new MyButtonListener());
		play.setOnClickListener(new MyButtonListener());
		first.setOnClickListener(new MyButtonListener());
		last.setOnClickListener(new MyButtonListener());
		list.setOnItemClickListener(new MyListListener());

		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume.setMax(maxVolume);
		currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
		volume.setProgress(currentVolume);
		volumeValue.setText(currentVolume * 100 / maxVolume + " %");
		volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				am.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
				currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
				volume.setProgress(currentVolume);
				volumeValue.setText(currentVolume * 100 / maxVolume + " %");
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

		});
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

		// 意图过滤器
		IntentFilter filter = new IntentFilter();

		// 播出电话暂停音乐播放
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(new PhoneListener(), filter);

		// 创建一个电话服务
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听电话状态，接电话时停止播放
		manager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

	}

	/*
	 * 监听电话状态
	 */
	private final class MyPhoneStateListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			mp.pause();
		}
	}

	/*
	 * 收到广播时暂停
	 */
	private final class PhoneListener extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			mp.pause();
		}
	}

	/*
	 * 恢复播放
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		mp.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.oneCycle:
			playMode = 1;
			break;
		case R.id.listCycle:
			playMode = 2;
			break;
		case R.id.listSeq:
			playMode = 3;
			break;
		case R.id.random:
			playMode = 4;
			break;
		}
		return true;
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
		if (playMode == 1) {
			play();
		} else if (playMode == 2) {
			if (curPlay == fileName.size() - 1) {
				curPlay = 0;
			} else {
				curPlay += 1;
			}
			play();
		} else if (playMode == 3) {
			if (curPlay == fileName.size() - 1) {
				// curPlay = 0;
				isPlaying = false;
				changePlayButtonShow();
			} else {
				curPlay += 1;
				play();
			}
		} else if (playMode == 4) {
			curPlay = getRandomSong();
			play();
		}

	}

	private int getRandomSong() {
		// TODO Auto-generated method stub
		Random random = new Random();
//		for (int i = 0; i < fileList.size(); i++) {
			int num = Math.abs(random.nextInt()) % fileList.size();
			System.out.println(num);
//		}
		return num;
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
		System.out.println(file.toString());
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
				if (curPlay == 0)
					curPlay = fileName.size() - 1;
				else
					curPlay--;
				play();
				break;
			case R.id.play:
				if (isPlaying) {
					mp.pause();
					isPlaying = false;
				} else {
					if (mp == null)
						play();
					else {
						mp.start();
						isPlaying = true;
					}

				}
				changePlayButtonShow();
				break;
			case R.id.after:
				if (curPlay == fileName.size() - 1)
					curPlay = 0;
				else
					curPlay++;
				play();
				break;
			case R.id.first:
				curPlay = 0;
				play();
				break;
			case R.id.last:
				curPlay = fileList.size() - 1;
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
