package edu.harding.acornmaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Layout;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.harding.acornmaze.Maze.DifficultyLevel;

public class AcornMazeActivity extends Activity implements MazeListener {
    
    //Adding our own result codes.
    public static final int RESULT_WIN = 0;
    public static final int RESULT_LOSE = 1;
    
    private LinearLayout mLayout;
    private SharedPreferences mPrefs;
    private long start;
    
    private WakeLock mWakeLock;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = new LinearLayout(this);
        MazeView mazeView = new MazeView(this);
        mPrefs = this.getSharedPreferences("maze_prefs", MODE_PRIVATE);
        long previousTime = mPrefs.getLong("previousTime", 0);
        mazeView.setPreviousTime(previousTime);
        mLayout.addView(mazeView);
        setContentView(mLayout);
        //TODO: Don't hardcode numbers. (Fix 'zero bug'.)
        Maze maze = Maze.generateMaze(DifficultyLevel.HARD, 3,
                                      5,
                                      8,
                                      this);
        mazeView.setMaze(maze);
        start = System.currentTimeMillis();
        maze.start(this, mazeView);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                AcornMazeActivity.class.getPackage().getName());
        mWakeLock.acquire();
    }

    @Override
    public void mazeFinished() {
        mWakeLock.release();
        Intent result = new Intent().putExtra("score", System.currentTimeMillis()-start);
        setResult(AcornMazeActivity.RESULT_WIN, result);
        finish();
    }
}