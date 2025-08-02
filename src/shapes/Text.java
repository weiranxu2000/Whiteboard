package shapes;

import java.awt.*;

public class Text extends Shape {
    private String text;

    public Text(Point start, Point end, Color color, String text, int brushSize) {
        super(start, end, color, brushSize);
        this.text = text;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.PLAIN, brushSize * 2));
        g.drawString(text, start.x, start.y);
    }
}
