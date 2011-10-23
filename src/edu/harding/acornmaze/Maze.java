package edu.harding.acornmaze;

import java.util.LinkedList;
import java.util.Random;

import edu.harding.acornmaze.Graph.Direction;
import edu.harding.acornmaze.Graph.Node;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
        paint.setARGB(255, 0, 128, 0);
        canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
        Node[][] slice = mMazeGraph.mNodes[mAvatar.mZ];
        if (firstDraw) {
            drawWalls(canvas, slice);
        } else {
            int x = (int) mAvatar.mX;
            int y = (int) mAvatar.mY;
            float cellWidth = canvas.getWidth()/mWidth;
            float cellHeight = canvas.getHeight()/mHeight;
            Node node = mMazeGraph.mNodes[mAvatar.mZ][x][y];
            if (!node.isOpen(Direction.NORTH)) {
                canvas.drawLine(cellWidth*x, cellHeight*y, cellWidth*(x+1), cellHeight*y, paint);
            }
            if (!node.isOpen(Direction.SOUTH)) {
                canvas.drawLine(cellWidth*x, cellHeight*(y+1), cellWidth*(x+1), cellHeight*(y+1), paint);
            }
            if (!node.isOpen(Direction.WEST)) {
                canvas.drawLine(cellWidth*x, cellHeight*y, cellWidth*x, cellHeight*(y+1), paint);
            }
            if (!node.isOpen(Direction.EAST)) {
                canvas.drawLine(cellWidth*(x+1), cellHeight*y, cellWidth*(x+1), cellHeight*(y+1), paint);
            }
        }
        drawHoles(canvas, slice, false);
        mAvatar.draw(canvas);
        drawHoles(canvas, slice, true);
    }

    private void drawWalls(Canvas canvas, Node[][] slice) {
        Paint paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        float cellWidth = canvas.getWidth()/mWidth;
        float cellHeight = canvas.getHeight()/mHeight;
        for (int i = 0; i < mWidth; i++) {
            for (int j = 0; j < mHeight; j++) {
                Node node = mMazeGraph.mNodes[mAvatar.mZ][i][j];
                if (!node.isOpen(Direction.EAST)) {
                    canvas.drawLine(cellWidth*(i+1), cellHeight*j, cellWidth*(i+1), cellHeight*(j+1), paint);
                }
                if (!node.isOpen(Direction.SOUTH)) {
                    canvas.drawLine(cellWidth*i, cellHeight*(j+1), cellWidth*(i+1), cellHeight*(j+1), paint);
                }
            }
        }
    }
    
    private void drawHoles(Canvas canvas, Node[][] slice, boolean drawUpHoles) {
        Paint paint = new Paint();
        if (drawUpHoles) {
            paint.setARGB(128, 200, 200, 0);
        } else {
            paint.setARGB(200, 0, 0, 0);
        }
        float cellWidth = canvas.getWidth()/mWidth;
        float cellHeight = canvas.getHeight()/mHeight;
        float radius = 0.45f * Math.min(cellWidth, cellHeight);

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

    public void moveAvatar(float accelX, float accelY) {
        mAvatar.move(accelX, accelY);
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
            if (!location.isOpen(Direction.NORTH) && mY - Math.floor(mY) < mRadius) {
                rebound(mX, (float) Math.floor(mY));
            }
            if (!location.isOpen(Direction.SOUTH) && Math.ceil(mY) - mY < mRadius) {
                rebound(mX, (float) Math.ceil(mY));
            }
            if (!location.isOpen(Direction.WEST) && mX - Math.floor(mX) < mRadius) {
                rebound((float) Math.floor(mX), mY);
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
            float unitX = xOffset/length;
            float unitY = yOffset/length;
            mX = contactX+unitX*mRadius;
            mY = contactY+unitY*mRadius;
        }

        public Node getLocation() {
            return Maze.this.mMazeGraph.mNodes[mZ][(int) mX][(int) mY];
        }
    }
}
