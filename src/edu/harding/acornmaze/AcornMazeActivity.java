package edu.harding.acornmaze;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.harding.acornmaze.Maze.DifficultyLevel;

public class AcornMazeActivity extends Activity implements MazeListener {
    
    private LinearLayout mLayout;
    private SharedPreferences mPrefs;
    private long start;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //MazeView mazeView = (MazeView)findViewById(R.id.maze);
        mLayout = new LinearLayout(this);
        MazeView mazeView = new MazeView(this);
        mPrefs = this.getSharedPreferences("maze_prefs", MODE_PRIVATE);
        long previousTime = mPrefs.getLong("previousTime", 0);
        mazeView.setPreviousTime(previousTime);
        mLayout.addView(mazeView);
        setContentView(mLayout);
        //TODO: Don't hardcode numbers. (Fix 'zero bug'.)
        Maze maze = Maze.generateMaze(DifficultyLevel.HARD, 2,
                                      300,
                                      400,
                                      this);
        mazeView.setMaze(maze);
        start = System.currentTimeMillis();
        maze.start(this, mazeView);
    }

    @Override
    public void mazeFinished() {
        long timeMillis = System.currentTimeMillis() - start;
        long timeSecs = timeMillis/1000;
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putLong("previousTime", timeSecs);
        ed.commit();
        setContentView(R.layout.main);
        TextView recordView = (TextView)findViewById(R.id.recordView);
        recordView.setText("Competed in: "+String.valueOf(timeSecs)+" seconds!");
    }
}