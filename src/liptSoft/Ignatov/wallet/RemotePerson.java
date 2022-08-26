package liptSoft.Ignatov.wallet;


/**
 * Class represents individuals for remote operations with {@link Wallet}
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class RemotePerson implements Person{
    private final String name;
    private final String surname;
    private final String passport;

    /**
     * RemotePerson constructor
     *
     * @param name     individual name
     * @param surname  individual surname
     * @param passport individual passport. All individual wallets are linked
     *                 to passport
     */
    public RemotePerson(String name, String surname, String passport) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurName() {
        return surname;
    }

    @Override
    public String getPassport() {
        return passport;
    }
}

