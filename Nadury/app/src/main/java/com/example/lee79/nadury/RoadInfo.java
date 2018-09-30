package com.example.lee79.nadury;

public class RoadInfo {
    public String roadName;
    public String title;
    public double roadX,currentX;
    public double roadY,currentY;
    public int position;
    public RoadInfo(String roadName,String title,Double roadX,Double roadY, Double currentX, Double currentY){
        this.roadName = roadName;
        this.title = title;
        this.roadX = roadX;
        this.roadY = roadY;
        this.currentX = currentX;
        this.currentY = currentY;
    }
}
