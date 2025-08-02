package server;

import client.IWhiteboardClient;
import shapes.Shape;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IWhiteboard extends Remote {
    void drawShape(Shape shape) throws RemoteException;
    List<Shape> getShapes() throws RemoteException;
    void registerClient(IWhiteboardClient client) throws RemoteException;
    void approveJoinRequest(String username, IWhiteboardClient client) throws RemoteException;
    void unregisterClient(IWhiteboardClient client) throws RemoteException;
    void sendMessage(String message) throws RemoteException;
    void kickUser(String username) throws RemoteException;
    List<String> getUserList() throws RemoteException;
    void clearWhiteboard() throws RemoteException;
    void openWhiteboard(List<Shape> shapes) throws RemoteException;
}
