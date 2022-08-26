package liptSoft.Ignatov.wallet;

import java.util.HashMap;

/**
 * Class representing operations with currencies in a {@link Wallet}
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class Currency implements Comparable<Currency> {

    private final String currency;
    private final int orderingNumber;
    private final HashMap<String, Double> rates;

    /**
     * Currency constructor
     * @param currency string representation of currency
     * @param orderingNumber individual number for sorting currencies
     */
    public Currency(String currency, int orderingNumber) {
        this.currency = currency;
        this.orderingNumber = orderingNumber;
        rates = new HashMap<>();
        rates.put(currency, 1.);
    }

    /**
     * Adds rate from this currency and provided currency
     * @param currency currency for adding rate
     * @param rate the number of provided currency in this currency
     */
    public void addRate(String currency, Double rate){
        rates.put(currency, rate);
    }

    /**
     * Gets the rate from this currency to provided currency
     * @param currency a string representation of provided currency
     * @return rate from this currency to provided currency
     */
    public Double getRate(String currency){
        return rates.get(currency);
    }

    /**
     * returns a string representation of this currency
     */
    public String getCurrency() {
        return currency;
    }

    @Override
    public int compareTo(Currency other) {
        return this.orderingNumber - other.orderingNumber;
    }
}
