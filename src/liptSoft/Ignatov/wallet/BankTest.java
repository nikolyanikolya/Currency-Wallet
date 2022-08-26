package liptSoft.Ignatov.wallet;

import org.junit.*;
import org.junit.rules.TestName;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */
public class BankTest {

    private static Bank bank;
    private static final int PORT = 8888;
    private static final String OBJECT = "bank";
    private static final String URL = "//localhost:" + PORT + "/" + OBJECT;
    private static final int MAX_INDIVIDUALS = 1000;
    private static final int MAX_WALLETS = 1000;
    private static String subId = "239";
    private static String name = "defaultName";
    private static String surname = "defaultSurname";
    private static String passport = "defaultPassport";
    private static final int TIMEOUT = 50;
    private static final int THREADS = 10;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void BeforeAllTests() throws RemoteException, MalformedURLException {
        LocateRegistry.createRegistry(PORT);
        bank = new RemoteBank(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        Naming.rebind(URL, bank);
    }

    @Test
    public void test01() throws RemoteException {
        setPersonData(testName.getMethodName());
        Person person = bank.getIndividual(passport);
        Assert.assertNull("expected null person", person);
        Assert.assertTrue("expected successful adding of new person",
                bank.createIndividual(name, surname, passport));
        person = bank.getIndividual(passport);
        Assert.assertNotNull("expected not null person", person);
        Wallet wallet = bank.getWallet(person, getId(passport, subId));
        Assert.assertNull("expected null wallet", wallet);
        wallet = bank.createWallet(getId(passport, subId));
        Assert.assertNotNull("expected not null wallet", wallet);
    }

    @Test
    public void test02() throws RemoteException {
        for (int i = 0; i < MAX_INDIVIDUALS; i++) {
            String name = "Ivan" + i;
            String surname = "Ivanov" + i;
            String passport = "passport" + i;
            Assert.assertTrue("expected successful adding of new person",
                    bank.createIndividual(name, surname, passport));
            var person = bank.getIndividual(passport);
            Assert.assertNotNull("expected not null person", person);
            Assert.assertEquals("invalid name of person", name, person.getName());
            Assert.assertEquals("invalid surname of person", surname, person.getSurName());
            Assert.assertEquals("invalid passport of person", passport, person.getPassport());
        }
    }

    @Test
    public void test03() throws RemoteException {
        Assert.assertNull("expected null wallet", bank.getWallet(null, "239"));
        Assert.assertFalse("expected failure while adding of new person",
                bank.createIndividual("Ivan", null, null));
        Assert.assertFalse("expected failure while adding of new person",
                bank.createIndividual(null, "Ivanov", null));
        Assert.assertTrue("expected successful adding of new person",
                bank.createIndividual(null, null, "123"));
        Assert.assertFalse("expected failure while adding of new person",
                bank.createIndividual(null, null, null));
    }

    @Test
    public void test04() throws RemoteException {
        setPersonData(testName.getMethodName());
        Assert.assertTrue("expected successful adding of new person",
                bank.createIndividual(name, surname, passport));
        Wallet wallet1 = bank.createWallet(getId(passport, subId));
        Wallet wallet2 = bank.createWallet(getId(passport, subId));
        Assert.assertSame("expected that each wallet would be created only once",
                wallet1, wallet2);
    }

    @Test
    public void test05() throws RemoteException {
        setPersonData(testName.getMethodName());
        Assert.assertTrue("expected successful adding of new person",
                bank.createIndividual(name, surname, passport));
        Assert.assertNull("expected null wallet by null id and person",
                bank.getWallet(null, null));
        Assert.assertNull("expected null wallet by null id",
                bank.createWallet(null));
        var person = bank.getIndividual(passport);
        Assert.assertNotNull("expected not null person", person);
        Assert.assertNull("expected null wallet by null id",
                bank.getWallet(person, null));
        for (int i = 0; i < MAX_WALLETS; i++) {
            String subId = "subId" + i;
            Assert.assertNull("expected that such wallet of person does not exist",
                    bank.getWallet(person, getId(passport, subId)));
            Wallet wallet = bank.createWallet(getId(passport, subId));
            Assert.assertNotNull("expected not null wallet", wallet);
            Assert.assertNotNull("expected not null wallet of person",
                    bank.getWallet(person, getId(passport, subId)));
        }
    }

    @Test
    public void test06() {
        final ExecutorService executors = Executors.newFixedThreadPool(THREADS);
        setPersonData(testName.getMethodName());
        Collection<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                long threadId = Thread.currentThread().getId();
                String s = "";
                try {
                    String name = BankTest.name + threadId;
                    String surname = BankTest.surname + threadId;
                    String passport = BankTest.passport + threadId;
                    String subId = BankTest.subId + threadId;
                    if (!bank.createIndividual(name, surname, passport)) {
                        s += "Expected successful adding of a new person";
                    }
                    var person = bank.getIndividual(passport);

                    s = checkCond(checkCond(s,
                                    Objects.isNull(person),
                                    "expected not null remotePerson\n"),
                            !bank.isExists(person),
                            "remotePerson must be presented at the bank\n");

                    Wallet wallet = bank.getWallet(person, getId(passport, subId));
                    s = checkCond(s, wallet != null, "expected null account\n");

                    wallet = bank.createWallet(getId(passport, subId));
                    if (wallet == null) {
                        s += "expected not null account\n";
                        return s;
                    }
                    wallet.addCurrency("ruble");
                    return checkCond(checkCond(s, wallet.getBalanceIn("ruble") != 0,
                                    "expected zero account balance\n"),
                            bank.getAllIds(person).size() != 1,
                            "Wrong number of accounts for remotePerson\n");

                } catch (RemoteException e) {
                    return "Some errors occurred while executing methods of a remoted interface. " + e.getMessage();
                }
            });
        }
        try {
            List<String> errors = executors.invokeAll(tasks).stream().map(s -> {
                try {
                    return s.get();
                } catch (InterruptedException | ExecutionException e) {
                    return "Error while executing the task. " + e.getMessage();
                }
            }).filter(s -> !s.isEmpty()).toList();
            if (!errors.isEmpty()) {
                Assert.fail("Bank operations are not thread-safe\n" + String.join("\n", errors));
            }
        } catch (InterruptedException e) {
            Assert.fail("Some of threads were interrupted. " + e.getMessage());
        }

        executors.shutdown();

        try {
            if (!executors.awaitTermination(
                    (long) THREADS * TIMEOUT, TimeUnit.SECONDS)) {
                Assert.fail("Pool did not terminate...");
            }
        } catch (InterruptedException e) {
            Assert.fail("Some threads were interrupted. " + e.getMessage());
        }
    }

    @AfterClass
    public static void afterAllMethods() throws RemoteException, MalformedURLException, NotBoundException {
        Naming.unbind(URL);
    }

    private static String getId(String passport, String subId) {
        return passport + ":" + subId;
    }

    private static void setPersonData(String methodName) {
        name = methodName + "Name";
        surname = methodName + "Surname";
        passport = methodName + "Passport";
        subId = methodName + "subId";
    }

    private static String checkCond(String s, boolean condition, String message) {
        if (condition) {
            s += message;
        }
        return s;
    }

}
