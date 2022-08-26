package liptSoft.Ignatov.wallet;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Class implementing {@link Wallet} interface
 *
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class WalletImpl implements Wallet {

    private final TreeMap<Currency, Double> wallet = new TreeMap<>(Currency::compareTo); // Map from Currency to amount

    private final HashMap<String, Currency> currencies = new HashMap<>();

    private int currencyNumber = 0;

    private final boolean logging;

    /**
     * WalletImpl constructor
     *
     * @param logging whether print balance after each command
     */

    public WalletImpl(boolean logging) {
        this.logging = logging;
    }

    /**
     * Default constructor
     */

    public WalletImpl() {
        this.logging = false;
    }

    @Override
    public void start(final Scanner in) {
        if (!isEmpty()) {
            return;
        }
        while (in.hasNextLine()) {
            final String command = in.nextLine();
            final String[] commandWords = command.split(" ");
            final int wordsNumber = commandWords.length;
            switch (commandWords[0]) {
                case "add" -> {
                    if (wordsNumber == 3) {
                        addCurrency(commandWords[2]);
                    } else {
                        throwWalletException("add", "add currency <currency>");
                    }
                }
                case "deposit" -> {
                    switch (wordsNumber) {
                        case 3 -> deposit(toDouble(commandWords[1]), commandWords[2]);
                        case 2 -> deposit(toDouble(commandWords[1]));
                        default -> throwWalletException("deposit", "deposit <amount> [currency]");
                    }
                }
                case "withdraw" -> {
                    switch (wordsNumber) {
                        case 3 -> withdraw(toDouble(commandWords[1]), commandWords[2]);
                        case 2 -> withdraw(toDouble(commandWords[1]));
                        default -> throwWalletException("withdraw", "withdraw <amount> [currency]");
                    }
                }
                case "set" -> {
                    if (wordsNumber == 5) {
                        final String[] rateParts = commandWords[4].split(":");
                        if (rateParts.length == 2) {
                            double rate = toDouble(rateParts[0])
                                    / toDouble(rateParts[1]);
                            setRate(commandWords[2], commandWords[3], rate);
                        } else {
                            throw new WalletException("illegal rate. Format: <double>:<double>");
                        }
                    } else {
                        throwWalletException("set", "set rate <currency 1> <currency 2> <rate>");
                    }
                }
                case "convert" -> {
                    if (wordsNumber == 5) {
                        convertTo(toDouble(commandWords[1]), commandWords[2], commandWords[4]);
                    } else {
                        throwWalletException("convert", "convert <amount> <currency 1> to <currency 2>");
                    }
                }
                case "show" -> {
                    if (wordsNumber == 1) {
                        throwWalletException("show",
                                "Have a look at usages of 'show balance' and 'show total' commands");
                    }
                    switch (commandWords[1]) {
                        case "balance" -> {
                            if (wordsNumber == 2) {
                                showBalance();
                            } else {
                                throwWalletException("show balance", "show balance");
                            }
                        }
                        case "total" -> {
                            switch (wordsNumber) {
                                case 2 -> showTotalIn();
                                case 4 -> showTotalIn(commandWords[3]);
                                default -> throwWalletException("show total", "show total in [currency]");
                            }
                        }
                    }
                }
                default -> throw new WalletException("Unknown command '" + commandWords[0] + "'");
            }
        }
    }


    @Override
    public void deposit(double amount) {
        checkNonEmpty();
        deposit(amount, wallet.firstKey().getCurrency());
    }

    @Override
    public void deposit(double amount, final String currency) {
        changeAmount(amount, currency, Double::sum, false);
    }

    @Override
    public void addCurrency(final String currency) {
        if (currencies.containsKey(currency)) {
            return;
        }
        Currency newCurrency = new Currency(currency, ++currencyNumber);
        wallet.put(newCurrency, 0.);
        currencies.put(currency, newCurrency);
        logBalance();
    }

    @Override
    public void withdraw(double amount) {
        checkNonEmpty();
        withdraw(amount, wallet.firstKey().getCurrency());
    }

    @Override
    public void withdraw(double amount, final String currency) {
        changeAmount(amount, currency, (a, b) -> a - b, true);
    }

    @Override
    public void setRate(final String currency1, final String currency2, double rate) {
        getCurrencyRequireNonNull(currency1).addRate(currency2, rate);
        getCurrencyRequireNonNull(currency2).addRate(currency1, 1. / rate);
        logBalance();
    }

    @Override
    public void convertTo(double amount, final String currency1, final String currency2) {
        withdraw(amount, currency1);
        getCurrencyRequireNonNull(currency1);
        var walletCurrency2 = getCurrencyRequireNonNull(currency2);
        var rate = walletCurrency2.getRate(currency1);
        deposit(getRequireNonNullRate(rate, currency1, currency2) * amount, currency2);
    }

    @Override
    public void showBalance() {
        wallet.forEach((key, value) -> printBalance(value, key.getCurrency()));
    }

    @Override
    public void showTotalIn(final String currency) {
        printBalance(getTotal(currency), currency);
    }

    @Override
    public void showTotalIn() {
        checkNonEmpty();
        showTotalIn(wallet.firstKey().getCurrency());
    }

    @Override
    public boolean isEmpty() {
        return currencyNumber == 0;
    }

    @Override
    public void clearWallet() {
        wallet.clear();
        currencies.clear();
        currencyNumber = 0;
        logBalance();
    }

    @Override
    public Double getBalanceIn(final String currency) {
        var walletCurrency = getCurrencyRequireNonNull(currency);
        return wallet.get(walletCurrency);
    }

    @Override
    public Double getTotal(final String currency) {
        double sum = 0;
        getCurrencyRequireNonNull(currency);
        for (var entry : wallet.entrySet()) {
            var rate = entry.getKey().getRate(currency);
            sum += entry.getValue() / getRequireNonNullRate(rate, currency, entry.getKey().getCurrency());
        }
        return sum;
    }

    private void changeAmount(double amount, final String currency,
                              BiFunction<Double, Double, Double> function, boolean requiredGreater) {
        final var walletCurrency = getCurrencyRequireNonNull(currency);
        double oldAmount = wallet.get(walletCurrency);
        if (requiredGreater && oldAmount < amount) {
            throw new WalletException("Not enough money in this currency to do an operation");
        }
        wallet.put(walletCurrency, function.apply(oldAmount, amount));
        logBalance();
    }

    private void printBalance(double sum, final String currency) {
        System.out.println(new Formatter(Locale.US).format("%.2f %s", sum, currency));
    }

    private void throwWalletException(final String command, final String usage) {
        throw new WalletException("illegal command " + command + ". Usage: " + usage);
    }

    private Currency getCurrencyRequireNonNull(final String currency) {
        var walletCurrency = currencies.get(currency);
        if (walletCurrency == null) {
            throw new WalletException(String.format("Expected that currency %s is in a wallet", currency));
        }
        return walletCurrency;
    }

    private void logBalance() {
        if (logging) {
            System.out.println("----------------Balance--------------");
            showBalance();
            System.out.println("--------------------------------------");
        }
    }

    private Double toDouble(final String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new WalletException("'" + value + "' is not a double number. " + e.getMessage());
        }
    }

    private Double getRequireNonNullRate(Double rate, final String currency1, final String currency2) {
        if (rate == null) {
            throw new WalletException(String.format("Expected defined rate from %s to %s",
                    currency1, currency2));
        }
        return rate;
    }

    private void checkNonEmpty() {
        if (isEmpty()) {
            throw new WalletException("Expected that there are at least one currency in a wallet");
        }
    }
}
