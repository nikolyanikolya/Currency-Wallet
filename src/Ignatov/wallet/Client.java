package liptSoft.Ignatov.wallet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class Client {
    private static final int PORT = 8888;
    private static final String OBJECT = "bank";
    private static final String URL = "//localhost:" + PORT + "/" + OBJECT;

    /**
     * Utility class.
     */
    private Client() {
    }

    private static String getId(String passport, String subId) {
        return passport + ":" + subId;
    }

    private static void print(final String arg1, Double arg2) {
        System.out.println(new Formatter(Locale.US).format("Amount of %s is: %.2f\n",
                arg1, arg2));
    }

    public static void main(final String... args) {

        final Bank bank;
        if (args == null || args.length != 6 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Illegal arguments. Usage: Client <name> " +
                    "<surname> <passport> <subId> <currency> <amount>");
            return;
        }
        try {
            try {
                bank = (Bank) Naming.lookup(URL);
            } catch (final NotBoundException e) {
                System.err.println("Bank is not bound. " + e.getMessage());
                return;
            } catch (final MalformedURLException e) {
                System.err.println("Bank URL is invalid. " + e.getMessage());
                return;
            }
            final String walletId = getId(args[2], args[3]);
            Person person = bank.getIndividual(args[2]);
            if (person == null) {
                bank.createIndividual(args[0], args[1], args[2]);
                person = bank.getIndividual(args[2]);
            } else {
                if (!person.getName().equals(args[0]) || !person.getSurName().equals(args[1])) {
                    System.err.println("Incorrect person name or surname. ");
                    return;
                }
            }
            Wallet wallet = bank.getWallet(person, walletId);
            if (wallet == null) {
                System.out.println("Creating wallet");
                wallet = bank.createWallet(walletId);
            } else {
                System.out.println("Wallet already exists");
            }
            wallet.addCurrency(args[4]);

            print(args[4], wallet.getBalanceIn(args[4]));
            System.out.printf("Adding %s %s...\n", args[5], args[4]);
            wallet.deposit(Double.parseDouble(args[5]));
            print(args[4], wallet.getBalanceIn(args[4]));
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
    }
}
