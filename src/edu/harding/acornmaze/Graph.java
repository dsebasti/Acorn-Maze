package edu.harding.acornmaze;

import java.util.PriorityQueue;
import java.util.Random;

public class Graph {
	
    public int mWidth, mHeight, mDepth;
    public int mStartX, mStartY;
    public int mEndX, mEndY;
	public Node[][][] mNodes;
	
	public Graph(int width, int height, int depth) {
		this(width, height, depth, 10);
	}
	
	public Graph(int width, int height, int depth, int maxWeight) {
	    mWidth = width;
	    mHeight = height;
	    mDepth = depth;
		Random rand = new Random();
		mNodes = new Node[depth][width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < depth; k++) {
					mNodes[k][i][j] = new Node(i, j, k,
											   rand.nextInt(maxWeight),
											   rand.nextInt(maxWeight),
											   rand.nextInt(maxWeight)+10);
				}
			}
		}
	    mStartX = rand.nextInt(width);
	    mStartY = rand.nextInt(height);
        mEndX = rand.nextInt(width);
        mEndY = rand.nextInt(height);
		runPrims();
	}
	
	private void runPrims() {
	    int x = mStartX;
	    int y = mStartY;
	    boolean[][][] nodesChecked = new boolean[mDepth][mWidth][mHeight];
	    PriorityQueue<Edge> edgeQueue = new PriorityQueue<Edge>();
	    nodesChecked[0][x][y] = true;
	    Node startNode = mNodes[0][x][y];
	    for (Direction dir : Direction.values()) {
	        if (startNode.getAdjacentNode(dir) != null) {
	            edgeQueue.add(new Edge(startNode, dir, startNode.getWeight(dir)));
	        }
	    }
	    while (!edgeQueue.isEmpty()) {
	        Edge edge = edgeQueue.poll();
	        Node endNode = edge.getEndNode();
	        if (nodesChecked[endNode.mZ][endNode.mX][endNode.mY]) {
	            continue;
	        }
	        edge.getStartNode().open(edge.getDirection());
	        nodesChecked[endNode.mZ][endNode.mX][endNode.mY] = true;
	        for (Direction dir : Direction.values()) {
	            Node testNode = endNode.getAdjacentNode(dir);
	            if (testNode != null && !nodesChecked[testNode.mZ][testNode.mX][testNode.mY]) {
	                edgeQueue.add(new Edge(endNode, dir, endNode.getWeight(dir)));
	            }
	        }
	    }
	}
	
	public class Node {
		
		private int mX, mY, mZ;
		private int mEastWeight, mSouthWeight, mDownWeight;
		private boolean mEastOpen, mSouthOpen, mDownOpen;
		
		public Node(int x, int y, int z,
					int eastWeight, int southWeight, int downWeight) {
		    mX = x;
		    mY = y;
		    mZ = z;
			mEastWeight = eastWeight;
			mSouthWeight = southWeight;
			mDownWeight = downWeight;
		}
		
		public Node getAdjacentNode(Direction dir) {
            switch (dir) {
            case UP:
                if (mZ > 0) {
                    return Graph.this.mNodes[mZ-1][mX][mY];
                }
                break;
            case DOWN:
                if (mZ < Graph.this.mDepth-1) {
                    return Graph.this.mNodes[mZ+1][mX][mY];
                }
                break;
            case NORTH:
                if (mY > 0) {
                    return Graph.this.mNodes[mZ][mX][mY-1];
                }
                break;
            case SOUTH:
                if (mY < Graph.this.mHeight-1) {
                    return Graph.this.mNodes[mZ][mX][mY+1];
                }
                break;
            case WEST:
                if (mX > 0) {
                    return Graph.this.mNodes[mZ][mX-1][mY];
                }
                break;
            case EAST:
                if (mX < Graph.this.mWidth-1) {
                    return Graph.this.mNodes[mZ][mX+1][mY];
                }
                break;
            }
            return null;
		}
		
		public int getWeight(Direction dir) {
			switch (dir) {
			case UP:
				if (mZ > 0) {
					return Graph.this.mNodes[mZ-1][mX][mY].mDownWeight;
				}
				break;
			case DOWN:
				return mDownWeight;
			case NORTH:
				if (mY > 0) {
					return Graph.this.mNodes[mZ][mX][mY-1].mSouthWeight;
				}
				break;
			case SOUTH:
				return mSouthWeight;
			case WEST:
				if (mX > 0) {
					return Graph.this.mNodes[mZ][mX-1][mY].mEastWeight;
				}
				break;
			case EAST:
				return mEastWeight;
			}
			return -1;
		}
		
		public void open(Direction dir) {
            switch (dir) {
            case UP:
                if (mZ > 0) {
                    Graph.this.mNodes[mZ-1][mX][mY].open(Direction.SOUTH);
                }
                break;
            case DOWN:
                mDownOpen = true;
            case NORTH:
                if (mY > 0) {
                    Graph.this.mNodes[mZ][mX][mY-1].open(Direction.SOUTH);
                }
                break;
            case SOUTH:
                mSouthOpen = true;
            case WEST:
                if (mX > 0) {
                    Graph.this.mNodes[mZ][mX-1][mY].open(Direction.EAST);
                }
                break;
            case EAST:
                mEastOpen = true;
            }
		}
		
		public boolean isOpen(Direction dir) {
			switch (dir) {
			case UP:
				if (mZ > 0) {
					return Graph.this.mNodes[mZ-1][mX][mY].mDownOpen;
				}
				break;
			case DOWN:
				return mDownOpen;
			case NORTH:
				if (mY > 0) {
					return Graph.this.mNodes[mZ][mX][mY-1].mSouthOpen;
				}
				break;
			case SOUTH:
				return mSouthOpen;
			case WEST:
				if (mX > 0) {
					return Graph.this.mNodes[mZ][mX-1][mY].mEastOpen;
				}
				break;
			case EAST:
				return mEastOpen;
			}
			return false;
		}
		
		public boolean equals(Object obj) {
		    if (obj instanceof Node) {
		        Node node = (Node) obj;
		        return (this.mX == node.mX) &&
		               (this.mY == node.mY) &&
		               (this.mZ == node.mZ);
		    }
		    return false;
		}

        public boolean isFinishNode() {
            return (mX == mEndX && mY == mEndY && mZ == mDepth-1);
        }
	}
	
	private class Edge implements Comparable<Edge> {
	    
	    private Node mStartNode;
	    private Direction mDirection;
	    private int mWeight;
	    
	    public Edge(Node startNode, Direction direction, int weight) {
	        mStartNode = startNode;
	        mDirection = direction;
	        mWeight = weight;
	    }

        public Node getStartNode() {
	        return mStartNode;
	    }
	    
	    public Direction getDirection() {
            return mDirection;
        }
	    
	    public Node getEndNode() {
	        return mStartNode.getAdjacentNode(mDirection);
	    }
	    
	    public boolean equals(Object obj) {
	        if (obj instanceof Edge) {
	            Edge edge = (Edge) obj;
	            return (this.mStartNode.equals(edge.mStartNode)) &&
	                   (this.mDirection == edge.mDirection);
	        }
	        return false;
	    }

	    public int compareTo(Edge rValue) {
	        return mWeight - rValue.mWeight;
	    }
	}
	
	public int getStartX() {
	    return mStartX;
	}
	
	public int getStartY() {
	    return mStartY;
	}
	
	public static enum Direction {UP, DOWN, NORTH, SOUTH, WEST, EAST};
}
