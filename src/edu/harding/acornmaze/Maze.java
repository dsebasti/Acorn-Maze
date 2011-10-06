package edu.harding.acornmaze;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Maze {

    public enum DifficultyLevel {EASY, MEDIUM, HARD};
    
    //TODO: Width and height should be 'normal' values, scaled by the MazeView.
    private int mWidth;
    private int mHeight;
    private int mCurrentLevel;
    private LinkedList<MazeSlice> mLevels;
    private Acorn mAcorn;
    private DifficultyLevel mDifficultyLevel;
    private MazeListener mListener;
    private MazeRunnable mRunnable;
    
    private Maze(int startingLevel,
                 LinkedList<MazeSlice> levels,
                 DifficultyLevel difficultyLevel,
                 int width,
                 int height,
                 MazeListener listener) {
        mCurrentLevel = startingLevel;
        mLevels = levels;
        mAcorn = new Acorn(width/2, height/2, width, height);
        //Record keeping purposes only.
        mDifficultyLevel = difficultyLevel;
        mWidth = width;
        mHeight = height;
        mListener = listener;
    }
    
    public void moveAcorn(float accelX, float accelY) {
        mAcorn.changeSpeed(accelX, accelY);
        MazeSlice currentLevel = mLevels.get(mCurrentLevel);
        currentLevel.moveAcorn(mAcorn);
        if (currentLevel.acornFalls(mAcorn)) {
            mCurrentLevel++;
            if (mCurrentLevel == mLevels.size()) {
                mRunnable.stop();
                mListener.mazeFinished();
            }
        }
    }
    
    public void jumpAcorn() {
        if (mLevels.get(mCurrentLevel).jumpAcorn(mAcorn)) {
            mAcorn.changePosition(8*mAcorn.getXSpeed(), 8*mAcorn.getYSpeed());
            mCurrentLevel--;
        }
    }
    
    public void start(Context context, MazeView mazeView) {
        mRunnable = new MazeRunnable(context, this, mazeView);
        new Thread(mRunnable).start();
    }

    public void draw(Canvas canvas) {
        canvas.drawRGB(150, 75, 0);
        mAcorn.draw(canvas);
        mLevels.get(mCurrentLevel).draw(canvas);
    }
    
    public static Maze generateMaze(DifficultyLevel difficultyLevel,
                                    int numLevels,
                                    int width,
                                    int height,
                                    MazeListener listener) {
        Random random = new Random();
        LinkedList<MazeSlice> levels = new LinkedList<MazeSlice>();
        for (int i = 0; i < numLevels; i++) {
            MazeSlice level = null;
            if (i > 0) {
                level = levels.get(i-1);
            }
            levels.add(MazeSlice.generateSlice(random,
                                               difficultyLevel,
                                               level,
                                               width,
                                               height,
                                               i == 0,
                                               i == numLevels-1));
            
        }
        return new Maze(0, levels, difficultyLevel, width, height, listener);
    }
}
