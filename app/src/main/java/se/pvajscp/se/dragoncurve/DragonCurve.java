package se.pvajscp.se.dragoncurve;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ani on 2016-02-25.
 */
public class DragonCurve {
    public static final Point DIRECTION_NORTH = new Point(0,1);
    public static final Point DIRECTION_EAST = new Point(1,0);
    public static final Point DIRECTION_SOUTH = new Point(0,-1);
    public static final Point DIRECTION_WEST = new Point(-1,0);
    public static final int RIGHT = 0;
    public static final int LEFT = 1;

    ArrayList<Point> segments = new ArrayList<>();
    ArrayList<Point> absolutePosition = new ArrayList<>();
    ArrayList<Integer> turns = new ArrayList<>();

    public DragonCurve(){
        segments.add(DIRECTION_EAST);
        absolutePosition.add(DIRECTION_EAST);
        turns.add(LEFT);
        int[] initialTurns = new int[]{LEFT,LEFT,RIGHT};
        for(int initialTurn : initialTurns){
            Point nextDirection = calculateNextDirection(segments.get(segments.size() - 1), initialTurn);
            segments.add(nextDirection);
            turns.add(initialTurn);
            absolutePosition.add(calculateNextAbsolutePosition(absolutePosition.get(absolutePosition.size() - 1), nextDirection));
        }
    }

    private Point calculateNextAbsolutePosition(Point startPos, Point direction) {
        return new Point(startPos.x+direction.x,startPos.y + direction.y);
    }

    private void calculateNextPart() {
        int currentSize = segments.size();
        int positionOfInvert = currentSize/2;
        for(int i = 0; i < currentSize; i++){
            int turn = turns.get(i);
            if(i == positionOfInvert){
                turn = 0;
            }
            Point direction =calculateNextDirection(segments.get(segments.size() - 1), turn);
            turns.add(turn);
            segments.add(direction);
            absolutePosition.add(calculateNextAbsolutePosition(absolutePosition.get(absolutePosition.size() - 1), direction));
        }
    }

    private Point calculateNextDirection(Point direction, int turn){
        int remap = (turn == 0)?1:-1;
        remap = (direction.x != 0)?-remap:remap;
        return new Point(direction.y*remap,direction.x*remap);// turn 90degrees in correct direction
    }

    public int getSegmentAt(int position) {
        while(segments.size() <= position){
            calculateNextPart();
        }
        return turns.get(position);
    }


    public Point getAbsolutePositionAt(int position) {
        while(segments.size() <= position){
            calculateNextPart();
        }
        return absolutePosition.get(position);
    }

    public Point getDirectionAt(int position) {
        while(segments.size() <= position){
            calculateNextPart();
        }
        return segments.get(position);
    }

    public int getTurnAt(int position) {
        while(segments.size() <= position){
            calculateNextPart();
        }
        return turns.get(position);
    }

}
