package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import client.IWhiteboardClient;
import shapes.Shape;

public class WhiteboardImpl extends UnicastRemoteObject implements IWhiteboard {
    private List<Shape> shapes = new ArrayList<>();
    private List<IWhiteboardClient> clients = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private IWhiteboardClient manager;

    protected WhiteboardImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void drawShape(Shape shape) throws RemoteException {
        shapes.add(shape);
        for (IWhiteboardClient client : clients) {
            client.updateWhiteboard(shape);
        }
    }

    @Override
    public synchronized List<Shape> getShapes() throws RemoteException {
        return new ArrayList<>(shapes);
    }

    @Override
    public synchronized void registerClient(IWhiteboardClient client) throws RemoteException {
        if (userList.contains(client.getUsername())) {
            throw new RemoteException("Username already exists.");
        }
        if (clients.isEmpty()) {
            manager = client;
        }
        clients.add(client);
        userList.add(client.getUsername());
        updateAllClients();
    }

    @Override
    public synchronized void approveJoinRequest(String username, IWhiteboardClient client) throws RemoteException {
        userList.add(username);
        clients.add(client);
        updateAllClients();
    }

    @Override
    public synchronized void unregisterClient(IWhiteboardClient client) throws RemoteException {
        clients.remove(client);
        userList.remove(client.getUsername());
        updateAllClients();
        if (client.equals(manager)) {
            for (IWhiteboardClient c : clients) {
                c.notifyManagerExit();
            }
            System.exit(0);
        }
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        for (IWhiteboardClient client : clients) {
            client.receiveMessage(message);
        }
    }

    @Override
    public void kickUser(String username) throws RemoteException {
        IWhiteboardClient clientToRemove = null;
        for (IWhiteboardClient client : clients) {
            if (client.getUsername().equals(username)) {
                clientToRemove = client;
                break;
            }
        }
        if (clientToRemove != null) {
            clientToRemove.notifyKick();
            unregisterClient(clientToRemove);
        }
    }

    @Override
    public List<String> getUserList() throws RemoteException {
        return new ArrayList<>(userList);
    }

    @Override
    public synchronized void clearWhiteboard() throws RemoteException {
        shapes.clear();
        for (IWhiteboardClient client : clients) {
            client.clearWhiteboard();
        }
    }

    @Override
    public synchronized void openWhiteboard(List<Shape> shapes) throws RemoteException {
        this.shapes = new ArrayList<>(shapes);
        for (IWhiteboardClient client : clients) {
            client.openWhiteboard(shapes);
        }
    }

    private void updateAllClients() throws RemoteException {
        for (IWhiteboardClient client : clients) {
            client.updateUserList(userList);
        }
    }
}
