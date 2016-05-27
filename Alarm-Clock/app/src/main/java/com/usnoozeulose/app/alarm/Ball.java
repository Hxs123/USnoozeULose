package com.usnoozeulose.app.alarm;

/**
 * Created by lkjjkl97 on 5/26/2016.
 */
public class Ball {
    private int x;
    private int y;
    private int radius;
    private double speed;
    private boolean inHole;
    //sqrt(radius) * 2

    public Ball(int a, int b){
        x = (int)(Math.random()*1100 + 10);
        y = (int)(Math.random()*2100 + 1);
        this.radius = (int)(Math.random()*a + b);
        speed = (200/(radius));
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getRadius(){
        return radius;
    }
    public double getSpeed(){
        return speed;
    }
    public void setInHole(int holeX, int holeY, int holeS){
        if(x > holeX && x + radius * 2 < holeX + holeS && y + 100 > holeY && y + radius * 2 +
                100 <  holeY + holeS)
            inHole = true;
        else
            inHole = false;
    }
    public boolean getInHole(){
        return inHole;
    }
    public void setX(int a){
        x = a;
    }
    public void setY(int a){
        y = a;
    }
}
