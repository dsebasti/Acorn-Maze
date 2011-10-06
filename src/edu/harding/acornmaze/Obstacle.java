package edu.harding.acornmaze;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

//TODO: This class is a temporary construction for the vertical prototype.
//It should be removed later.
public class Obstacle {
    
    private float mXPosition;
    private float mYPosition;
    private float mRadius;
    private RectF mOval;
    private Paint mPaint;

    //All obstacles are square.
    public Obstacle(float xPosition, float yPosition, float radius) {
        mXPosition = xPosition;
        mYPosition = yPosition;
        mRadius = radius;
        mPaint = new Paint();
        mPaint.setARGB(255, 128, 128, 128);
        mOval = new RectF(xPosition-radius,
                          yPosition+radius,
                          xPosition+radius,
                          yPosition-radius);
    }

    public boolean intersectsObstacle(Obstacle obstacle) {
        double distance = Math.sqrt(Math.abs(Math.pow(mXPosition-obstacle.mXPosition, 2))+
                                    Math.abs(Math.pow(mYPosition-obstacle.mYPosition, 2)));
        return distance < mRadius + obstacle.mRadius;
    }
    
    public boolean intersectsCircle(float xPosition, float yPosition, float radius) {
        return intersectsObstacle(new Obstacle(xPosition, yPosition, radius));
    }
    
    public void draw(Canvas canvas) {
        canvas.drawOval(mOval, mPaint);
    }
    
    public float getXPosition() {
        return mXPosition;
    }

    public float getYPosition() {
        return mYPosition;
    }

    public float getRadius() {
        return mRadius;
    }

    public String toString() {
        return "Obstacle: x="+String.valueOf(mXPosition)+
                       ", y="+String.valueOf(mYPosition)+
                       ", r="+String.valueOf(mRadius);
    }
}
