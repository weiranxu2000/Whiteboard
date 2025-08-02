package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WhiteboardServer {
    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        try {
            LocateRegistry.createRegistry(serverPort);
            WhiteboardImpl whiteboard = new WhiteboardImpl();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Whiteboard", whiteboard);
            System.out.println("Whiteboard Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
