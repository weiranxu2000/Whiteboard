package shapes;

import java.awt.*;

public class Circle extends Shape {

    public Circle(Point start, Point end, Color color, int brushSize) {
        super(start, end, color, brushSize);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(brushSize));
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int diameter = Math.min(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
        g2.drawOval(x, y, diameter, diameter);
    }
}
