package shapes;

import java.awt.*;

public class Rectangle extends Shape {

    public Rectangle(Point start, Point end, Color color, int brushSize) {
        super(start, end, color, brushSize);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(brushSize));
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        g2.drawRect(x, y, width, height);
    }
}
