package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.IWhiteboard;

public class CreateWhiteboard {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java CreateWhiteboard <serverIPAddress> <serverPort> <username>");
            System.exit(1);
        }

        String serverIPAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String username = args[2];

        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);
            IWhiteboard whiteboard = (IWhiteboard) registry.lookup("Whiteboard");
            new WhiteboardClient(username, whiteboard, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
