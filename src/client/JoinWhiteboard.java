package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.IWhiteboard;

public class JoinWhiteboard {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java JoinWhiteboard <serverIPAddress> <serverPort> <username>");
            System.exit(1);
        }

        String serverIPAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String username = args[2];

        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);
            IWhiteboard whiteboard = (IWhiteboard) registry.lookup("Whiteboard");

            // Check if the username is unique
            if (whiteboard.getUserList().contains(username)) {
                System.out.println("Username already exists. Please choose a different username.");
                System.exit(1);
            }

            new WhiteboardClient(username, whiteboard, false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
