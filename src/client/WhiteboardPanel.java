package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import shapes.Shape;

public class WhiteboardPanel extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private String currentTool = "Free Draw";
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private Point startPoint;

    public WhiteboardPanel(WhiteboardClient client) {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Shape shape = null;
                Point endPoint = e.getPoint();

                switch (currentTool) {
                    case "Free Draw":
                        shape = new shapes.FreeDraw(startPoint, endPoint, currentColor, brushSize);
                        break;
                    case "Eraser":
                        shape = new shapes.Eraser(startPoint, endPoint, brushSize);
                        break;
                    case "Rectangle":
                        shape = new shapes.Rectangle(startPoint, endPoint, currentColor, brushSize);
                        break;
                    case "Oval":
                        shape = new shapes.Oval(startPoint, endPoint, currentColor, brushSize);
                        break;
                    case "Circle":
                        shape = new shapes.Circle(startPoint, endPoint, currentColor, brushSize);
                        break;
                    case "Line":
                        shape = new shapes.Line(startPoint, endPoint, currentColor, brushSize);
                        break;
                    case "Text":
                        String text = JOptionPane.showInputDialog("Enter text:");
                        if (text != null) {
                            shape = new shapes.Text(startPoint, endPoint, currentColor, text, brushSize);
                        }
                        break;
                }

                if (shape != null) {
                    shapes.add(shape);
                    repaint();
                    try {
                        client.getWhiteboard().drawShape(shape);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool.equals("Free Draw") || currentTool.equals("Eraser")) {
                    Shape shape = null;
                    Point endPoint = e.getPoint();

                    switch (currentTool) {
                        case "Free Draw":
                            shape = new shapes.FreeDraw(startPoint, endPoint, currentColor, brushSize);
                            break;
                        case "Eraser":
                            shape = new shapes.Eraser(startPoint, endPoint, brushSize);
                            break;
                    }

                    if (shape != null) {
                        shapes.add(shape);
                        startPoint = endPoint;
                        repaint();
                        try {
                            client.getWhiteboard().drawShape(shape);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void setCurrentTool(String tool) {
        currentTool = tool;
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
    }

    public void setBrushSize(int size) {
        brushSize = size;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    public void clear() {
        shapes.clear();
        repaint();
    }

    public void save() {
        saveAs();
    }

    public void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(shapes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void open() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                shapes = (List<Shape>) in.readObject();
                repaint();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }
}
