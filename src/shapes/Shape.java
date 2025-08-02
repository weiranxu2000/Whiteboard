package shapes;

import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements Serializable {
    protected Point start;
    protected Point end;
    protected Color color;
    protected int brushSize;

    public Shape(Point start, Point end, Color color, int brushSize) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.brushSize = brushSize;
    }

    public abstract void draw(Graphics g);
}
