package edu.harding.acornmaze;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

class Acorn {
    
    private float mMaxX;
    private float mMaxY;
    private float mX;
    private float mY;
    private float mXSpeed = 0;
    private float mYSpeed = 0;
    private float mRadius = 8;
    private Paint mPaint;
    
    public Acorn(float xPosition, float yPosition, float maxX, float maxY) {
        mMaxX = maxX;
        mMaxY = maxY;
        mX = xPosition;
        mY = yPosition;
        mPaint = new Paint();
        mPaint.setARGB(255, 255, 64, 64);
    }
    
    public void draw(Canvas canvas) {
        RectF oval = new RectF(mX-mRadius, mY-mRadius, mX+mRadius, mY+mRadius);
        canvas.drawOval(oval, mPaint);
    }
    
    public void setPosition(float newX, float newY) {
        mX = newX; 
        mY = newY;
        mX = Math.max(0, Math.min(mMaxX, mX));
        mY = Math.max(0, Math.min(mMaxY, mY));
    }
    
    public void setSpeed(float newXSpeed, float newYSpeed) {
        mXSpeed = newXSpeed;
        mYSpeed = newYSpeed;
        mXSpeed = Math.max(-10, Math.min(10, mXSpeed));
        mYSpeed = Math.max(-10, Math.min(10, mYSpeed));
    }
    
    public void changePosition(float deltaX, float deltaY) {
        mX += deltaX; 
        mY += deltaY;
        mX = Math.max(0, Math.min(mMaxX, mX));
        mY = Math.max(0, Math.min(mMaxY, mY));
        if (mX == mMaxX) {
            mXSpeed = 0;
        }
        if (mY == mMaxY) {
            mYSpeed = 0;
        }
    }
    
    public void changeSpeed(float deltaXSpeed, float deltaYSpeed) {
        mXSpeed += deltaXSpeed;
        mYSpeed += deltaYSpeed;
        mXSpeed = Math.max(-10, Math.min(10, mXSpeed));
        mYSpeed = Math.max(-10, Math.min(10, mYSpeed));
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getXSpeed() {
        return mXSpeed;
    }

    public float getYSpeed() {
        return mYSpeed;
    }

    public float getRadius() {
        return mRadius;
    }

    public void reflect(Obstacle obstacle) {
        float reflectX = -(mX-obstacle.getXPosition());
        float reflectY = mY-obstacle.getYPosition();
        float speedProjLength = (mXSpeed*reflectX + mYSpeed*reflectY)/
                                (reflectX*reflectX + reflectY*reflectY);
        float newXSpeed = 2*speedProjLength*reflectX - mXSpeed;
        float newYSpeed = 2*speedProjLength*reflectY - mYSpeed;
        setSpeed(0.75f*newXSpeed, 0.75f*newYSpeed);
    }

    public void flee(Obstacle obstacle) {
        float xOffset = mX-obstacle.getXPosition();
        float yOffset = mY-obstacle.getYPosition();
        float length = (float) Math.sqrt(xOffset*xOffset + yOffset*yOffset);
        float unitX = xOffset/length;
        float unitY = yOffset/length;
        float newX = obstacle.getXPosition()+unitX*(mRadius+obstacle.getRadius());
        float newY = obstacle.getYPosition()+unitY*(mRadius+obstacle.getRadius());
        setPosition(newX, newY);
    }
}