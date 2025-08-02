package shapes;

import java.awt.*;

public class Eraser extends Shape {

    public Eraser(Point start, Point end, int brushSize) {
        super(start, end, Color.WHITE, brushSize);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(brushSize));
        g2.drawLine(start.x, start.y, end.x, end.y);
    }
}
