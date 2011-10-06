package edu.harding.acornmaze;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Hole {

    private int mXPosition;
    private int mYPosition;
    private int mRadius;
    private RectF mOval;
    private Paint mPaint;
    private boolean mToUpperLayer;
    private boolean mFinalHole;
    
    public Hole(int xPosition,
                int yPosition,
                boolean toUpperLayer,
                boolean finalHole) {
        mXPosition = xPosition;
        mYPosition = yPosition;
        mRadius = 12;
        mToUpperLayer = toUpperLayer;
        mFinalHole = finalHole;
        mPaint = new Paint();
        if (toUpperLayer){
            //Translucent yellow.
            mPaint.setARGB(128, 255, 255, 0);
        } else {
            mPaint.setARGB(255, 0, 0, 0);
        }
        mOval = new RectF(xPosition-mRadius,
                          yPosition+mRadius,
                          xPosition+mRadius,
                          yPosition-mRadius);
    }
    
    public boolean isNearAcorn(Acorn acorn) {
        float xPosition = acorn.getX();
        float yPosition = acorn.getY();
        double distance = Math.sqrt(Math.abs(Math.pow(mXPosition-xPosition, 2))+
                                    Math.abs(Math.pow(mYPosition-yPosition, 2)));
        return distance < mRadius + acorn.getRadius();
    }
    
    public boolean intersectsCircle(int xPosition, int yPosition, int radius) {
        double distance = Math.sqrt(Math.abs(Math.pow(mXPosition-xPosition, 2))+
                                    Math.abs(Math.pow(mYPosition-yPosition, 2)));
        return distance < mRadius + radius;
    }
    
    public void draw(Canvas canvas) {
        canvas.drawOval(mOval, mPaint);
    }
    
    public int getXPosition() {
        return mXPosition;
    }

    public int getYPosition() {
        return mYPosition;
    }

    public int getRadius() {
        return mRadius;
    }

    public boolean isToUpperLayer() {
        return mToUpperLayer;
    }

    public boolean isFinalHole() {
        return mFinalHole;
    }
}
