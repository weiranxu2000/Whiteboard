package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import server.IWhiteboard;
import shapes.Shape;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class WhiteboardClient extends JFrame implements IWhiteboardClient {
    private IWhiteboard whiteboard;
    private String username;
    private boolean isManager;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private WhiteboardPanel whiteboardPanel;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;

    public WhiteboardClient(String username, IWhiteboard whiteboard, boolean isManager) throws RemoteException {
        this.username = username;
        this.whiteboard = whiteboard;
        this.isManager = isManager;

        // Initialize GUI components
        userListModel = new DefaultListModel<>();
        chatArea = new JTextArea();
        chatInput = new JTextField();
        userList = new JList<>(userListModel);
        whiteboardPanel = new WhiteboardPanel(this);

        // GUI setup
        setupGUI();

        // RMI setup
        IWhiteboardClient stub = (IWhiteboardClient) UnicastRemoteObject.exportObject(this, 0);
        whiteboard.registerClient(stub);

        // Load existing shapes
        List<Shape> shapes = whiteboard.getShapes();
        for (Shape shape : shapes) {
            whiteboardPanel.addShape(shape);
        }

        // Load existing users
        List<String> users = whiteboard.getUserList();
        for (String user : users) {
            if (!userListModel.contains(user)) {
                userListModel.addElement(user);
            }
        }

        setVisible(true);
    }

    private void setupGUI() {
        setTitle("Shared Whiteboard - " + username);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    whiteboard.unregisterClient((IWhiteboardClient) UnicastRemoteObject.toStub(WhiteboardClient.this));
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(whiteboardPanel, BorderLayout.CENTER);

        chatArea.setEditable(false);
        chatInput.addActionListener(e -> {
            try {
                whiteboard.sendMessage(username + ": " + chatInput.getText());
                chatInput.setText("");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });

        // Create chat and user panel
        JPanel chatUserPanel = new JPanel();
        chatUserPanel.setLayout(new BorderLayout());

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(250, 400));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(new JLabel("Online Users"), BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        // Add kick button for manager
        if (isManager) {
            JButton kickButton = new JButton("Kick User");
            kickButton.addActionListener(e -> {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null && !selectedUser.equals(username)) {
                    try {
                        whiteboard.kickUser(selectedUser);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            userPanel.add(kickButton, BorderLayout.SOUTH);
        }

        userPanel.setPreferredSize(new Dimension(250, 200));

        chatUserPanel.add(chatPanel, BorderLayout.CENTER);
        chatUserPanel.add(userPanel, BorderLayout.SOUTH);

        mainPanel.add(chatUserPanel, BorderLayout.EAST);
        setContentPane(mainPanel);

        // Add toolbars and menus
        createToolBar();
        createMenuBar();
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton pencilButton = new JButton("Free Draw");
        pencilButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Free Draw"));
        toolBar.add(pencilButton);

        JButton eraserButton = new JButton("Eraser");
        eraserButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Eraser"));
        toolBar.add(eraserButton);

        JButton rectButton = new JButton("Rectangle");
        rectButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Rectangle"));
        toolBar.add(rectButton);

        JButton ovalButton = new JButton("Oval");
        ovalButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Oval"));
        toolBar.add(ovalButton);

        JButton circleButton = new JButton("Circle");
        circleButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Circle"));
        toolBar.add(circleButton);

        JButton lineButton = new JButton("Line");
        lineButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Line"));
        toolBar.add(lineButton);

        JButton textButton = new JButton("Text");
        textButton.addActionListener(e -> whiteboardPanel.setCurrentTool("Text"));
        toolBar.add(textButton);

        // Color chooser
        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(this, "Choose a color", currentColor);
            if (chosenColor != null) {
                currentColor = chosenColor;
                whiteboardPanel.setCurrentColor(currentColor);
            }
        });
        toolBar.add(colorButton);

        // Brush size slider
        JSlider brushSizeSlider = new JSlider(1, 20, brushSize);
        brushSizeSlider.setMajorTickSpacing(5);
        brushSizeSlider.setMinorTickSpacing(1);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);
        brushSizeSlider.addChangeListener(e -> whiteboardPanel.setBrushSize(brushSizeSlider.getValue()));
        toolBar.add(new JLabel("Brush Size: "));
        toolBar.add(brushSizeSlider);

        add(toolBar, BorderLayout.NORTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> {
            if (isManager) {
                whiteboardPanel.clear();
                try {
                    whiteboard.clearWhiteboard();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Only the manager can create a new whiteboard.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        fileMenu.add(newItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            if (isManager) {
                whiteboardPanel.save();
            } else {
                JOptionPane.showMessageDialog(this, "Only the manager can save the whiteboard.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(e -> {
            if (isManager) {
                whiteboardPanel.saveAs();
            } else {
                JOptionPane.showMessageDialog(this, "Only the manager can save the whiteboard.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        fileMenu.add(saveAsItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> {
            if (isManager) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                        List<Shape> shapes = (List<Shape>) in.readObject();
                        whiteboardPanel.setShapes(shapes);
                        whiteboard.openWhiteboard(shapes);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Only the manager can open a whiteboard.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        fileMenu.add(openItem);

        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(e -> {
            try {
                whiteboard.unregisterClient((IWhiteboardClient) UnicastRemoteObject.toStub(WhiteboardClient.this));
                System.exit(0);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
        fileMenu.add(closeItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    @Override
    public void updateWhiteboard(Shape shape) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            whiteboardPanel.addShape(shape);
        });
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    @Override
    public void updateUserList(List<String> users) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void notifyManagerExit() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Manager has exited. The application will close.", "Manager Exit", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    @Override
    public void notifyKick() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "You have been kicked by the manager. The application will close.", "Kicked Out", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    @Override
    public void clearWhiteboard() throws RemoteException {
        SwingUtilities.invokeLater(() -> whiteboardPanel.clear());
    }

    @Override
    public void openWhiteboard(List<Shape> shapes) throws RemoteException {
        SwingUtilities.invokeLater(() -> whiteboardPanel.setShapes(shapes));
    }

    public IWhiteboard getWhiteboard() {
        return whiteboard;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java WhiteboardClient <username> <serverIPAddress> <serverPort>");
            System.exit(1);
        }

        String username = args[0];
        String serverIPAddress = args[1];
        int serverPort = Integer.parseInt(args[2]);

        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);
            IWhiteboard whiteboard = (IWhiteboard) registry.lookup("Whiteboard");
            new WhiteboardClient(username, whiteboard, false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
