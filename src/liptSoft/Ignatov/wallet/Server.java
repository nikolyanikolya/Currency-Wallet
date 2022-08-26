package liptSoft.Ignatov.wallet;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
/**
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class Server {
    private static final int DEFAULT_PORT = 8888;
    private static final String URL = "//localhost:" + DEFAULT_PORT + "/bank";
    /**
     Utility class
     */
    private Server() {

    }
    public static void main(final String... args) {
        final Bank bank = new RemoteBank(DEFAULT_PORT);
        try {
            LocateRegistry.createRegistry(DEFAULT_PORT);
            UnicastRemoteObject.exportObject(bank, DEFAULT_PORT);
            Naming.rebind(URL, bank);
            System.out.println("Server started");
        } catch (final RemoteException e) {
            System.err.println("Cannot export object: " + e.getMessage());
        } catch (final MalformedURLException e) {
            System.err.println("Malformed URL. " + e.getMessage());
        }
    }
}
