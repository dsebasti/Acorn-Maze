package edu.harding.acornmaze;

import java.util.LinkedList;
import java.util.Random;

import edu.harding.acornmaze.Graph.Direction;
import edu.harding.acornmaze.Graph.Node;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class Maze {

    public enum DifficultyLevel {EASY, MEDIUM, HARD};
    
    //TODO: Width and height should be 'normal' values, scaled by the MazeView.
    private Graph mMazeGraph;
    private int mWidth;
    private int mHeight;
    private int mDepth;
    private Avatar mAvatar;
    private DifficultyLevel mDifficultyLevel;
    private MazeListener mListener;
    private MazeRunnable mRunnable;
    private boolean firstDraw = true;
    
    private Maze(Graph mazeGraph,
                 int width,
                 int height,
                 int depth,
                 int startX,
                 int startY,
                 DifficultyLevel difficultyLevel,
                 MazeListener listener) {
        mMazeGraph = mazeGraph;
        mWidth = width;
        mHeight = height;
        mDepth = depth;
        mAvatar = new Avatar(startX+0.5f, startY+0.5f, 0);
        
        //Record keeping purposes only.
        mDifficultyLevel = difficultyLevel;
        
        mListener = listener;
    }
    
    public void start(Context context, MazeView mazeView) {
        mRunnable = new MazeRunnable(context, this, mazeView);
        new Thread(mRunnable).start();
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        Node[][] slice = mMazeGraph.mNodes[mAvatar.mZ];
        if (firstDraw) {
            paint.setARGB(255, 200, 150, 100);
            canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
            drawWalls(canvas, slice);
        } else {
            paint.setARGB(255, 100, 50, 0);
            float cellWidth = canvas.getWidth()/mWidth;
            float cellHeight = canvas.getHeight()/mHeight;
            int x = (int) mAvatar.mX;
            int y = (int) mAvatar.mY;
            Node node = mMazeGraph.mNodes[mAvatar.mZ][x][y];
            if (!node.isOpen(Direction.NORTH)) {
                canvas.drawRect(cellWidth*x-2,
                                cellHeight*y-2,
                                cellWidth*(x+1)+2,
                                cellHeight*y+2,
                                paint);
            }
            if (!node.isOpen(Direction.SOUTH)) {
                canvas.drawRect(cellWidth*x-2,
                                cellHeight*(y+1)-2,
                                cellWidth*(x+1)+2,
                                cellHeight*(y+1)+2,
                                paint);
            }
            if (!node.isOpen(Direction.WEST)) {
                canvas.drawRect(cellWidth*x-2,
                                cellHeight*y-2,
                                cellWidth*x+2,
                                cellHeight*(y+1)+2,
                                paint);
            }
            if (!node.isOpen(Direction.EAST)) {
                canvas.drawRect(cellWidth*(x+1)-2,
                                cellHeight*y-2,
                                cellWidth*(x+1)+2,
                                cellHeight*(y+1)+2,
                                paint);
            }
        }
        drawHoles(canvas, slice, false);
        if (mAvatar.mZ == mDepth-1) {
            drawFlag(canvas);
        }
        mAvatar.draw(canvas);
        drawHoles(canvas, slice, true);
    }

    private void drawWalls(Canvas canvas, Node[][] slice) {
        Paint paint = new Paint();
        paint.setARGB(255, 100, 50, 0);
        paint.setStyle(Paint.Style.FILL);
        float cellWidth = canvas.getWidth()/mWidth;
        float cellHeight = canvas.getHeight()/mHeight;
        for (int i = 0; i < mWidth; i++) {
            for (int j = 0; j < mHeight; j++) {
                Node node = mMazeGraph.mNodes[mAvatar.mZ][i][j];
                if (!node.isOpen(Direction.EAST)) {
                    canvas.drawRect(cellWidth*(i+1)-2,
                                    cellHeight*j-2,
                                    cellWidth*(i+1)+2,
                                    cellHeight*(j+1)+2,
                                    paint);
                }
                if (!node.isOpen(Direction.SOUTH)) {
                    canvas.drawRect(cellWidth*i-2,
                                    cellHeight*(j+1)-2,
                                    cellWidth*(i+1)+2,
                                    cellHeight*(j+1)+2,
                                    paint);
                }
            }
        }
    }
    
    private void drawHoles(Canvas canvas, Node[][] slice, boolean drawUpHoles) {
        float cellWidth = canvas.getWidth()/mWidth;
        float cellHeight = canvas.getHeight()/mHeight;
        Paint paint = new Paint();
        float radius;
        if (drawUpHoles) {
            paint.setARGB(128, 200, 200, 0);
            radius = 0.45f * Math.min(cellWidth, cellHeight);
        } else {
            paint.setARGB(200, 0, 0, 0);
            radius = 0.35f * Math.min(cellWidth, cellHeight);
        }
        
        for (int i = 0; i < mWidth; i++) {
            for (int j = 0; j < mHeight; j++) {
                if ((slice[i][j].isOpen(Direction.UP) && drawUpHoles) ||
                    (slice[i][j].isOpen(Direction.DOWN) && !drawUpHoles)) {
                    canvas.drawOval(new RectF((i+0.5f)*cellWidth - radius,
                                              (j+0.5f)*cellHeight - radius,
                                              (i+0.5f)*cellWidth + radius,
                                              (j+0.5f)*cellHeight + radius), paint);
                }
            }
        }
    }
    
    public void drawFlag(Canvas canvas) {
        float cellWidth = canvas.getWidth()/mWidth;
        float cellHeight = canvas.getHeight()/mHeight;
        Paint paint = new Paint();
        float midX = mMazeGraph.mEndX+0.5f;
        float midY = mMazeGraph.mEndY+0.5f;
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(255, 255, 255, 255);
        Path flag = new Path();
        flag.moveTo(midX*cellWidth, (midY-0.5f)*cellHeight+4);
        flag.lineTo((midX+0.5f)*cellWidth-4, (midY-0.25f)*cellHeight);
        flag.lineTo(midX*cellWidth, midY*cellHeight-4);
        canvas.drawPath(flag, paint);
        paint.setARGB(255, 0, 0, 0);
        canvas.drawRect(new RectF(midX*cellWidth-2,
                                  (midY-0.5f)*cellHeight+4,
                                  midX*cellWidth+2,
                                  (midY+0.5f)*cellHeight-4), paint);
    }

    public void moveAvatar(float accelX, float accelY) {
        mAvatar.move(accelX, accelY);
        Node node = mAvatar.getLocation();
        if (node.isFinishNode()) {
            mRunnable.stop();
            mListener.mazeFinished();
        }
    }

    public void jumpAvatar() {
        Node location = mAvatar.getLocation();
        if (location.isOpen(Direction.DOWN)) {
            mAvatar.mZ++;
            firstDraw = true;
        } else if (location.isOpen(Direction.UP)) {
            mAvatar.mZ--;
            firstDraw = true;
        }
    }

    public static Maze generateMaze(DifficultyLevel difficultyLevel,
                                    int numLevels,
                                    int width,
                                    int height,
                                    MazeListener listener) {
            Graph graph = new Graph(width, height, 3);
        return new Maze(graph,
                        width,
                        height,
                        numLevels,
                        graph.getStartX(),
                        graph.getStartY(),
                        difficultyLevel,
                        listener);
    }

    private class Avatar {

        public float mX, mY;
        public int mZ;
        public float mRadius;
        
        public float mSpeedX = 0, mSpeedY = 0;
        
        public Avatar(float startX, float startY, int startZ) {
            mX = startX;
            mY = startY;
            mZ = startZ;
            mRadius = 0.25f;
        }
        
        public void draw(Canvas canvas) {
            float cellWidth = canvas.getWidth()/mWidth;
            float cellHeight = canvas.getHeight()/mHeight;
            Paint paint = new Paint();
            paint.setARGB(255, 150, 75, 0);
            canvas.drawOval(new RectF(cellWidth*(mX  - mRadius),
                                      cellHeight*(mY - mRadius),
                                      cellWidth*(mX  + mRadius),
                                      cellHeight*(mY + mRadius)), paint);
        }
        
        public void move(float accelX, float accelY) {
            mSpeedX = Math.min(0.05f, Math.max(-0.05f, mSpeedX+accelX));
            mSpeedY = Math.min(0.05f, Math.max(-0.05f, mSpeedY+accelY));
            Node location = getLocation();
            mX = Math.min(Maze.this.mWidth, Math.max(0, mX+mSpeedX));
            mY = Math.min(Maze.this.mHeight, Math.max(0, mY+mSpeedY));
            Node up = location.getAdjacentNode(Direction.NORTH);
            if (!location.isOpen(Direction.NORTH) && mY - ((int)mY) < mRadius) {
                rebound(mX, (int)mY);
            }
            if (!location.isOpen(Direction.SOUTH) && Math.ceil(mY) - mY < mRadius) {
                rebound(mX, (float) Math.ceil(mY));
            }
            if (!location.isOpen(Direction.WEST) && mX - ((int)mX) < mRadius) {
                rebound((int)mX, mY);
            }
            if (!location.isOpen(Direction.EAST) && Math.ceil(mX) - mX < mRadius) {
                rebound((float) Math.ceil(mX), mY);
            }
        }
        
        public void rebound(float contactX, float contactY) {
            float reflectX = -(mX-contactX);
            float reflectY =   mY-contactY;
            float speedProjLength = (mSpeedX*reflectX + mSpeedY*reflectY)/
                                    (reflectX*reflectX + reflectY*reflectY);
            mSpeedX = 0.5f*(2*speedProjLength*reflectX - mSpeedX);
            mSpeedY = 0.5f*(2*speedProjLength*reflectY - mSpeedY);
            float xOffset = mX-contactX;
            float yOffset = mY-contactY;
            float length = (float) Math.sqrt(xOffset*xOffset + yOffset*yOffset);
            if (length != 0) {
                float unitX = xOffset/length;
                float unitY = yOffset/length;
                mX = contactX+unitX*mRadius;
                mY = contactY+unitY*mRadius;
            }
        }

        public Node getLocation() {
            return Maze.this.mMazeGraph.mNodes[mZ][(int) mX][(int) mY];
        }
    }
}
