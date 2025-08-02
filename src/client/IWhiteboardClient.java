package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import shapes.Shape;

public interface IWhiteboardClient extends Remote {
    void updateWhiteboard(Shape shape) throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
    void updateUserList(List<String> users) throws RemoteException;
    String getUsername() throws RemoteException;
    void notifyManagerExit() throws RemoteException;
    void notifyKick() throws RemoteException;
    void clearWhiteboard() throws RemoteException;
    void openWhiteboard(List<Shape> shapes) throws RemoteException;
}
