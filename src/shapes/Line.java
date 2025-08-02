package shapes;

import java.awt.*;

public class Line extends Shape {

    public Line(Point start, Point end, Color color, int brushSize) {
        super(start, end, color, brushSize);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(brushSize));
        g2.drawLine(start.x, start.y, end.x, end.y);
    }
}
