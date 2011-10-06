package edu.harding.acornmaze;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;

import edu.harding.acornmaze.Maze.DifficultyLevel;

/**
 * This class represents a 2-dimensional slice of a maze. It
 * contains links to other maze slices in the form of holes.
 * 
 * @author Daniel
 */
public class MazeSlice {

    private List<Obstacle> mObstacles;
    private List<Hole> mHoles;
    
    
    //TODO: Trees are circular. This MazeSlice class is not.
    //TODO: The maze will be defined by openings in successive rings, not by
    //obstacles.
    public MazeSlice(List<Obstacle> obstacles, List<Hole> holes) {
        mObstacles = obstacles;
        mHoles = holes;
    }

    public void draw(Canvas canvas) {
        for (Obstacle obstacle : mObstacles) {
            obstacle.draw(canvas);
        }
        for (Hole hole: mHoles) {
            hole.draw(canvas);
        }
    }

    public List<Obstacle> checkForHits(int xPosition, int yPosition, int objectRadius) {
        LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
        for (Obstacle checkObstacle : obstacles) {
            if (checkObstacle.intersectsCircle(xPosition, yPosition, objectRadius)) {
                obstacles.add(checkObstacle);
            }
        }
        return obstacles;
    }
    
    public void moveAcorn(Acorn acorn) {
        acorn.changePosition(acorn.getXSpeed(), acorn.getYSpeed());
        for (Obstacle obstacle : mObstacles) {
            if (obstacle.intersectsCircle(acorn.getX(), acorn.getY(), acorn.getRadius())) {
                acorn.reflect(obstacle);
                acorn.flee(obstacle);
            }
        }
    }

    public boolean jumpAcorn(Acorn acorn) {
        for (Hole hole : mHoles) {
            if (hole.isToUpperLayer() && hole.isNearAcorn(acorn)) {
                return true;
            }
        }
        return false;
    }

    public boolean acornFalls(Acorn acorn) {
        for (Hole hole : mHoles) {
            if (!hole.isToUpperLayer() && hole.isNearAcorn(acorn)) {
                return true;
            }
        }
        return false;
    }
    
    public static MazeSlice generateSlice(Random random,
                                          DifficultyLevel difficultyLevel,
                                          MazeSlice previousSlice,
                                          int mazeWidth,
                                          int mazeHeight,
                                          boolean isFirstSlice,
                                          boolean isLastSlice) {
        int numObstacles = 0;
        switch (difficultyLevel) {
        case HARD:
            numObstacles += 4;
        case MEDIUM:
            numObstacles += 4;
        case EASY:
            numObstacles += 4;
        }
        LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
        LinkedList<Hole> holes = new LinkedList<Hole>();
        if (!isFirstSlice) {
            for (Hole hole : previousSlice.mHoles) {
                if (!hole.isToUpperLayer()) {
                    holes.add(new Hole(hole.getXPosition(),
                                       hole.getYPosition(),
                                       true,
                                       false));
                }
            }
        }
        for (int i = 0; i < numObstacles; i++) {
            do {
                //TODO: Don't hardcode size. (Moot, as obstacles wont exist soon.)
                Obstacle obstacle = new Obstacle(random.nextInt(mazeWidth),
                                                 random.nextInt(mazeHeight),
                                                 12);
                boolean insertable = true;
                for (Obstacle checkObstacle : obstacles) {
                    if (checkObstacle.intersectsObstacle(obstacle)) {
                        insertable = false;
                        break;
                    }
                }
                for (Hole hole : holes) {
                    //TODO: Don't hardcode hole radius.
                    if (obstacle.intersectsCircle(hole.getXPosition(), hole.getYPosition(), 24)) {
                        insertable = false;
                        break;
                    }
                }
                if (insertable) {
                    obstacles.add(obstacle);
                }
            } while (obstacles.size() == i);
        }
        //TODO: Allow more than one hole.
        boolean insertable;
        do {
            insertable = true;
            Hole hole = new Hole(random.nextInt(mazeWidth), random.nextInt(mazeHeight), false, isLastSlice);
            for (Obstacle obstacle : obstacles) {
                if (obstacle.intersectsCircle(hole.getXPosition(), hole.getYPosition(), hole.getRadius())) {
                    insertable = false;
                    break;
                }
            }
            for (Hole testHole : holes) {
                if (hole.intersectsCircle(testHole.getXPosition(),
                                              testHole.getYPosition(),
                                              testHole.getRadius())) {
                    insertable = false;
                    break;
                }
            }
            if (insertable) {
                holes.add(hole);
            }
        } while (!insertable);
        
        return new MazeSlice(obstacles, holes);
    }
}
