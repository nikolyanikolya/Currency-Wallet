package liptSoft.Ignatov.wallet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Interface representing methods for bank operations with currencies
 * @see WalletImpl
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public interface Wallet extends Remote {

    /**
     * Starts reading commands with {@link Scanner} and executing these commands
     * @param in Scanner which is used for input commands
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void start(final Scanner in) throws RemoteException;

    /**
     * Deposits amount money in the currency added first
     * @param amount number of money in the currency added first. If there are no added currencies,
     *               then the command fails with an error
     * @throws RemoteException when errors occurred while executing interface methods
     */
    void deposit(double amount) throws RemoteException;

    /**
     * Deposits amount money in the provided currency
     * @param amount number of money in the provided currency. If this currency is not in this wallet,
     *                       the command fails with an error
     * @param currency provided currency
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void deposit(double amount, final String currency) throws RemoteException;

    /**
     * Adds provided currency to this wallet
     * @param currency provided currency
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void addCurrency(final String currency) throws RemoteException;

    /**
     * Withdraws amount money in the currency added first from this wallet
     * @param amount number of money in the currency added first
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void withdraw(double amount) throws RemoteException;

    /**
     * Withdraws amount money in the provided currency from this wallet
     * @param amount number of money in the provided currency
     * @param currency provided currency
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void withdraw(double amount, final String currency) throws RemoteException;

    /**
     * Sets the rate from currency1 to currency2
     * @param rate the number of currency1 in currency2
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void setRate(final String currency1, final String currency2, double rate) throws RemoteException;

    /**
     * Converts amount of money in currency 1 to currency 2 according to the rate
     * @param amount amount of money to convert
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void convertTo(double amount, final String currency1, final String currency2) throws RemoteException;

    /**
     * Shows all saving in this wallet in format '<amount> <currency>'.
     * Amount is rounded to two decimal places in US format.
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void showBalance() throws RemoteException;

    /**
     * Shows all savings converted in provided currency in this wallet. If this currency is not in a wallet,
     * command fails with an error.
     * @throws RemoteException when errors occurred while executing interface methods
     */
    void showTotalIn(final String currency) throws RemoteException;

    /**
     * Shows all savings converted in currency added first to this wallet. If wallet is empty,
     * command fails with an error
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void showTotalIn() throws RemoteException;

    /**
     * Checks whether this wallet is empty
     * @return true, if this wallet is empty, otherwise false
     * @throws RemoteException when errors occurred while executing interface methods
     */

    boolean isEmpty() throws RemoteException;

    /**
     * Clears the wallet
     * @throws RemoteException when errors occurred while executing interface methods
     */

    void clearWallet() throws RemoteException;

    /**
     * Gets balance in a provided currency. Currency should be in the wallet, else method fails with an error
     * @return balance in this wallet in the provided currency
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Double getBalanceIn(final String currency) throws RemoteException;

    /**
     * Gets a wallet balance converted to a provided currency.
     * Currency should be in the wallet, else method fails with an error
     * @return balance in this wallet converted to the provided currency
     * @throws RemoteException when errors occurred while executing interface methods
     */

    Double getTotal(final String currency) throws RemoteException;

}
