package liptSoft.Ignatov.wallet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Class implementing {@link Bank}
 */
public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, Set<String>> personIds = new ConcurrentHashMap<>(); // Passport -> ids
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>(); // passport -> Person
    private final ConcurrentMap<String, Wallet> wallets = new ConcurrentHashMap<>(); // id -> wallet

    /**
     * RemoteBank constructor
     * @param port the port where the bank accepts clients
     */
    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public Wallet createWallet(String id) throws RemoteException {
        if (Objects.isNull(id)) {
            return null;
        }
        final String[] parts = id.split(":");
        final String passport = parts[0];
        final Wallet wallet = new WalletImpl();
        if (wallets.putIfAbsent(id, wallet) == null) {
            UnicastRemoteObject.exportObject(wallet, port);
            if (personIds.putIfAbsent(passport, new ConcurrentSkipListSet<>()) != null) {
                personIds.get(passport).add(id);
            }
            return wallet;
        } else {
            return wallets.get(id);
        }
    }

    @Override
    public Wallet getWallet(Person person, String id) {
        if (Objects.isNull(person) || Objects.isNull(id)) {
            return null;
        }
        return wallets.get(id);
    }

    @Override
    public boolean createIndividual(String name, String surname, String passport) throws RemoteException {
        if (Objects.isNull(passport)) {
            return false;
        }
        Person person = new RemotePerson(name, surname, passport);
        if (persons.putIfAbsent(passport, person) == null) {
            UnicastRemoteObject.exportObject(person, port);
            personIds.putIfAbsent(passport, new ConcurrentSkipListSet<>());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Person getIndividual(String passport) {
        if (Objects.isNull(passport)) {
            return null;
        }
        return persons.get(passport);
    }
    @Override
    public Set<String> getAllIds(Person person) {
        if (Objects.isNull(person)) {
            return null;
        }
        return personIds.get(((RemotePerson) person).getPassport());

    }

    @Override
    public boolean isExists(Person person) throws RemoteException {
        if (Objects.isNull(person) || Objects.isNull(person.getPassport())) {
            return false;
        }
        return persons.containsKey(person.getPassport());
    }
}