package com.dev.pushkar.consochrome;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Random;

import static com.dev.pushkar.consochrome.Data.KEY_TODAY;
import static com.dev.pushkar.consochrome.Data.KEY_TT_BEST_SCORE;
import static com.dev.pushkar.consochrome.Data.KEY_TT_DAILY_BEST_SCORE;
import static com.dev.pushkar.consochrome.Data.N_COLORS;
import static com.dev.pushkar.consochrome.Data.calender;
import static com.dev.pushkar.consochrome.Data.localData;
import static com.dev.pushkar.consochrome.Data.tapTileActivity;

public class TapTilesActivity extends Activity {

	/**
	 * UI componenets
	 */

	private TextView scoreView;

	private ImageView[] tapTiles;
	private ImageView[] fillTiles;

	private int[] tapTilesImageIDs;
	private int[] fillTilesImageIDs;
	private int emptyTileImageID;

	private MediaPlayer bgAudioPlayer;
	private MediaPlayer tilePressPlayer;

	/**
	 * Game Control elements
	 */

	private static final int INITIAL_DELAY = 750;

	private ArrayList<Integer> unfilledTiles;
	private ArrayList<Integer> unusedColors;
	private LinkedList<Integer> colorQueue;
	private LinkedList<Integer> coloredTiles;

	private Random randomGen;

	private boolean gameOver;
	private boolean gameOverHandled;
	private boolean forceQuit;
	
	private long delay;
	private int score;
	private int disappearedTileIndex = 0;
	
	private ControlThread fillTilesThread;
	private OnClickListener tapListener;
	
	private Animation appear;
	private Animation disappear;

    DatabaseReference highScoreRef,allTimeRef;
    String username;
	private boolean confetti=false;

	private void initialize() {

		scoreView =  findViewById(R.id.tap_tile_textview);
		scoreView.setTypeface(Data.appTypeface, Typeface.BOLD);
		
		tapTiles = new ImageView[N_COLORS];
		tapTiles[0] =  findViewById(R.id.tap_tile_0);
		tapTiles[1] =  findViewById(R.id.tap_tile_1);
		tapTiles[2] =  findViewById(R.id.tap_tile_2);
		tapTiles[3] =  findViewById(R.id.tap_tile_3);
		tapTiles[4] =  findViewById(R.id.tap_tile_4);
		tapTiles[5] =  findViewById(R.id.tap_tile_5);
		tapTiles[6] =  findViewById(R.id.tap_tile_6);
		tapTiles[7] =  findViewById(R.id.tap_tile_7);

		for (int i = 0; i < N_COLORS; i++) {
			tapTiles[i].setTag(i);
		}

		fillTiles = new ImageView[N_COLORS];
		fillTiles[0] =  findViewById(R.id.fill_tile_0);
		fillTiles[1] =  findViewById(R.id.fill_tile_1);
		fillTiles[2] =  findViewById(R.id.fill_tile_2);
		fillTiles[3] =  findViewById(R.id.fill_tile_3);
		fillTiles[4] =  findViewById(R.id.fill_tile_4);
		fillTiles[5] = findViewById(R.id.fill_tile_5);
		fillTiles[6] = findViewById(R.id.fill_tile_6);
		fillTiles[7] = findViewById(R.id.fill_tile_7);

		tapTilesImageIDs = new int[N_COLORS];
		tapTilesImageIDs[0] = R.drawable.ic_tap_tile_red;
		tapTilesImageIDs[1] = R.drawable.ic_tap_tile_green;
		tapTilesImageIDs[2] = R.drawable.ic_tap_tile_blue;
		tapTilesImageIDs[3] = R.drawable.ic_tap_tile_yellow;
		tapTilesImageIDs[4] = R.drawable.ic_tap_tile_magenta;
		tapTilesImageIDs[5] = R.drawable.ic_tap_tile_indigo;
		tapTilesImageIDs[6] = R.drawable.ic_tap_tile_brown;
		tapTilesImageIDs[7] = R.drawable.ic_tap_tile_gray;

		fillTilesImageIDs = new int[N_COLORS];
		fillTilesImageIDs[0] = R.drawable.ic_fill_tile_red;
		fillTilesImageIDs[1] = R.drawable.ic_fill_tile_green;
		fillTilesImageIDs[2] = R.drawable.ic_fill_tile_blue;
		fillTilesImageIDs[3] = R.drawable.ic_fill_tile_yellow;
		fillTilesImageIDs[4] = R.drawable.ic_fill_tile_magenta;
		fillTilesImageIDs[5] = R.drawable.ic_fill_tile_indigo;
		fillTilesImageIDs[6] = R.drawable.ic_fill_tile_brown;
		fillTilesImageIDs[7] = R.drawable.ic_fill_tile_gray;

		emptyTileImageID = R.drawable.ic_fill_tile_empty;

		randomGen = new Random();

		bgAudioPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.audio_tap_tiles);
		bgAudioPlayer.setLooping(true);
		tilePressPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.tap_tile_press);
		
		appear = new AlphaAnimation(0, 1);
		appear.setDuration(250);
		appear.setInterpolator(new LinearInterpolator());
		
		disappear = new AlphaAnimation(1, 0);
		disappear.setDuration(250);
		disappear.setInterpolator(new LinearInterpolator());
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (tapTileActivity != null) {
			tapTileActivity.finish();
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			try {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} catch (Exception ex) {
				Toast.makeText(this, "Incompatible device", Toast.LENGTH_LONG).show();
			}
		}

		tapTileActivity = this;
		setContentView(R.layout.activity_tap_tiles);
		initialize();
		startGame();

        macAddress = getDeviceMacAddress();
        String uid= FirebaseAuth.getInstance().getUid();
        if(uid==null){
            uid=macAddress;
        }
        username=getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("username",uid);
        Log.d("xx",uid);
        Calendar cal = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(cal.getTime());

        highScoreRef=database.getReference("Highscore"+"/"+date+"/"+uid);
        allTimeRef=database.getReference("Alltime"+"/"+uid);

    }


    int scorex=0;
    int scoreAll=0;
    public void setScore(){

    /*    highScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    scorex = (-1) * dataSnapshot.getValue(Integer.class);
                }
                catch (Exception ex){
                    scorex=0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      */


    }



    public void startGame() {
		initGameValues();
		enableTapTiles(true);
		fillTilesThread.start();
	}

	private void handleUserTapAction(ImageView tile) {

		tilePressPlayer.start();
		if (colorQueue.isEmpty() || coloredTiles.isEmpty()) {
			gameOver = true;
		} else {
			int tappedIndex = (Integer) tile.getTag();
			int colorIndex = colorQueue.remove();
			int fillTileIndex = coloredTiles.remove();
			if (tappedIndex == colorIndex) {
				ImageView fillTile = fillTiles[fillTileIndex];
				fillTile.setImageResource(emptyTileImageID);
				unfilledTiles.add(fillTileIndex);
				unusedColors.add(colorIndex);
				score++;
				scoreView.setText(String.valueOf("Score: " + score));
				updateDelay();
				shuffleTiles();
				if ((score > 100 && score <= 150 && score % 4 == 1)
						|| score > 200) {

					tapTiles[disappearedTileIndex].startAnimation(appear);
					disappearedTileIndex = randomGen.nextInt(N_COLORS);
					tapTiles[disappearedTileIndex].startAnimation(disappear);

				}
			} else {
				gameOver = true;
			}
		}

		if (gameOver) {
			handleGameOverAction();
		}
	}
	
	private void shuffleTiles() {
		if(score < 70)
			return;
		int magic_number = 20;
		if(score > 120) 
			magic_number = 9;
		else if(score > 180)
			magic_number = 5;
		else if(score > 250)
			magic_number = 3;
		int num = randomGen.nextInt(magic_number);
		if (num != 0)
			return;
		int[] array = new int[N_COLORS];
		for (int i = 0; i < N_COLORS; i++) {
			array[i] = i;
		}
		int[] shuffledCodes = shuffle(array);
		for (int i = 0; i < N_COLORS; i++) {
			tapTiles[i].setImageResource(tapTilesImageIDs[shuffledCodes[i]]);
			tapTiles[i].setTag(shuffledCodes[i]);
		}
	}
	
	private void updateDelay() {
		if(score < 30)
			return;
		if(score <= 80) {
			if(score % 5 == 0)
				delay -= 10;
		}
		else if(score < 120) {
			if(score % 5 == 0)
				delay -= 3;
		}
		else if(score < 200) {
			if(score % 10 == 0)
				delay -= 8;
		}
		else if(score < 250) {
			if(score % 10 == 0)
				delay -= 5;
		}
		else if(score < 300) {
			if(score % 10 == 0) {
				delay -= 20;
			}
		}
	}
	
	private void handleGameOverAction() {
		if (!gameOverHandled) {
			gameOverHandled = true;
			if (fillTilesThread.isAlive()) {
				fillTilesThread.interrupt();
			}
			enableTapTiles(false);
			bgAudioPlayer.pause();
			scoreView.setText(String.valueOf("Score: " + score));
			setScore();
			
			int today = calender.get(Calendar.DAY_OF_MONTH);
			int lastday = localData.getInt(KEY_TODAY, 0);
			int bestScore = localData.getInt(KEY_TT_BEST_SCORE, 0);
			int dailyBestScore = localData.getInt(KEY_TT_DAILY_BEST_SCORE, 0);
			SharedPreferences.Editor editor = localData.edit();
			confetti=false;
			if(bestScore < score) {
				editor.putInt(KEY_TT_BEST_SCORE, score);
				Highscore latest= new Highscore(-1*score,username);
				allTimeRef.setValue(latest);
				confetti=true;
			}
			else{
				Highscore latest= new Highscore(-1*bestScore,username);
				allTimeRef.setValue(latest);

			}
			if(lastday < today) {
				editor.putInt(KEY_TODAY, today);
				editor.putInt(KEY_TT_DAILY_BEST_SCORE, score);
                Highscore latest1= new Highscore(-1*score,username);
                highScoreRef.setValue(latest1);
			}
			else if(dailyBestScore < score){

				editor.putInt(KEY_TT_DAILY_BEST_SCORE, score);
                Highscore latest1= new Highscore(-1*score,username);
                highScoreRef.setValue(latest1);
            }
            else{
                Highscore latest1= new Highscore(-1*dailyBestScore,username);
                highScoreRef.setValue(latest1);
            }

			editor.apply();
			
			Intent intent = new Intent(this, TapTileGameOverActivity.class);
			intent.putExtra("confetti",confetti);
			startActivity(intent);
			//MAKE SURE THIS ACTIVITY GETS DESTTOYED
		}
	}
	String macAddress;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String getDeviceMacAddress() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        boolean state = adapter.isEnabled();
        String addr = adapter.getAddress();
        if(addr.equals("") && state == false) {
            adapter.enable();
            while(addr.equals("")) {
                addr = adapter.getAddress();
            }
            adapter.disable();
        }
        return addr;
    }

    private void initGameValues() {
		gameOver = false;
		score = 0;
		gameOverHandled = false;
		forceQuit = false;
		delay = INITIAL_DELAY;
		for (int i = 0; i < N_COLORS; i++) {
			fillTiles[i].setImageResource(emptyTileImageID);
		}
		unfilledTiles = new ArrayList<Integer>(N_COLORS);
		unusedColors = new ArrayList<Integer>(N_COLORS);
		colorQueue = new LinkedList<Integer>();
		coloredTiles = new LinkedList<Integer>();

		for (int i = 0; i < N_COLORS; i++) {
			unfilledTiles.add(i);
			unusedColors.add(i);
		}

		tapListener = new OnClickListener() {
			public void onClick(View v) {
				handleUserTapAction((ImageView) v);
			}
		};

		fillTilesThread = new ControlThread() {
			private Object lock = new Object();
			private boolean pauseRequest = false;

            public void onPause() {
				pauseRequest = true;
			}

			@Override
			public void onResume() {
				try {
				Thread.sleep(300);
				} catch (InterruptedException e) {}
				pauseRequest = false;
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			public void run() {
				int randomIndex;
				runOnUiThread(new Runnable() {
					public void run() {
						bgAudioPlayer.start();
						scoreView.setText("Score: " + score);
					}
				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
				}
				while (!gameOver) {
					if (interrupted())
						return;
					if(pauseRequest) {
						try {
							synchronized (lock) {
								lock.wait();
							}
						} catch (InterruptedException e) {
						}
					}

					if (unfilledTiles.isEmpty() || unfilledTiles.isEmpty()) {
						gameOver = true;
					}

					else {
						randomIndex = randomGen.nextInt(unfilledTiles.size());
						final int tileIndex = unfilledTiles.get(randomIndex);
						unfilledTiles.remove(randomIndex);

						randomIndex = randomGen.nextInt(unusedColors.size());
						final int colorIndex = unusedColors.get(randomIndex);
						unusedColors.remove(randomIndex);

						colorQueue.add(colorIndex);
						coloredTiles.add(tileIndex);

						runOnUiThread(new Runnable() {
							public void run() {
								fillTiles[tileIndex]
										.setImageResource(fillTilesImageIDs[colorIndex]);
							}
						});
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
						}
                    }
				}
				if (!forceQuit) {
					runOnUiThread(new Runnable() {
						public void run() {
							handleGameOverAction();
						}
					});
				}
			}
		};
	}

	private int[] shuffle(int[] array) {
		int[] arr = new int[array.length];

		Random rgen = new Random();
		ArrayList<Integer> numbers = new ArrayList<Integer>(arr.length);

		for (int i = 0; i < arr.length; i++) {
			arr[i] = array[i];
			numbers.add(arr[i]);
		}
		for (int i = 0; i < arr.length; i++) {
			int index = rgen.nextInt(numbers.size());
			arr[i] = numbers.get(index);
			numbers.remove(index);
		}
		return arr;
	}

	

	private void enableTapTiles(boolean enable) {
		if (enable) {
			for (int i = 0; i < N_COLORS; i++) {
				tapTiles[i].setOnClickListener(tapListener);
			}
		} else {
			for (int i = 0; i < N_COLORS; i++) {
				tapTiles[i].setOnClickListener(null);
			}
		}
	}

	protected void onPause() {
		super.onPause();
		if (bgAudioPlayer != null && bgAudioPlayer.isPlaying())
			bgAudioPlayer.pause();
		fillTilesThread.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bgAudioPlayer != null && !gameOver) {
			int length = bgAudioPlayer.getCurrentPosition();
			bgAudioPlayer.seekTo(length);
			bgAudioPlayer.start();
		}
		fillTilesThread.onResume();
		if (gameOver) {
			onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		forceQuit = true;
		fillTilesThread.interrupt();
	}
}
