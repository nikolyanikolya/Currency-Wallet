package liptSoft.Ignatov.wallet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Interface representing bank operations with {@link Wallet} and {@link Person}.
 * Objects implementing this interface are transmitted via remote links
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */

public interface Bank extends Remote {

    /**
     * Creates a new account with specified identifier if it is not already exists.
     *
     * @param id account id
     * @return created or existing account.
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Wallet createWallet(String id) throws RemoteException;

    /**
     * gets {@link Wallet} of the provided person with specified account id
     *
     * @param person remote person (individual)
     * @param id     wallet id
     * @return Wallet of the provided person
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Wallet getWallet(Person person, String id) throws RemoteException;

    /**
     * adds person with provided passport to the bank
     *
     * @param name     individual name
     * @param surname  individual surname
     * @param passport individual passport which serves for creating new accounts
     * @return true if person with such passport have not met before, false if passport is null
     * or such passport already exists
     * @throws RemoteException when errors occurred while executing interface methods
     */

    boolean createIndividual(String name, String surname, String passport) throws RemoteException;

    /**
     * gets {@link Person} at the bank by a provided passport
     *
     * @param passport individual passport
     * @return Person with provided passport at the bank or null if there are no
     * such passport at the bank or passport is null
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Person getIndividual(String passport) throws RemoteException;

    /**
     * gets all wallet ids of provided person
     *
     * @param person remote person (individual)
     * @return wallet ids or empty set if no such person at the bank
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Set<String> getAllIds(Person person) throws RemoteException;

    /**
     * Checks whether person at the bank or not
     *
     * @param person remote person (individual)
     * @return true if bank has such person passport, otherwise false
     * @throws RemoteException when errors occurred while executing interface methods
     */
    boolean isExists(Person person) throws RemoteException;

}
