package edu.harding.acornmaze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MazeView extends View implements OnTouchListener {

    private Maze mMaze;
    private long mPreviousTime;
    
    public MazeView(Context context) {
        super(context);
    }
    
    public MazeView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }
    
    public MazeView(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
    }
    
    public void setMaze(Maze maze) {
        mMaze = maze;
        setOnTouchListener(this);
    }
    
    public void setPreviousTime(long previousTime) {
        mPreviousTime = previousTime;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mMaze.draw(canvas);
        canvas.drawText("Previous time: "+String.valueOf(mPreviousTime)+" secs", 10, 20, new Paint());
    }

    @Override
    public boolean onTouch(View itsMe, MotionEvent event) {
        mMaze.jumpAcorn();
        return false;
    }
}
