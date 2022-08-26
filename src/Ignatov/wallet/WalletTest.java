package liptSoft.Ignatov.wallet;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @author Ignatov Nikolay ({@link <a href="https://github.com/nikolyanikolya?tab=repositories"> github </a>})
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WalletTest {

    private static final String SEPARATOR = File.separator;

    @Rule
    public final TestName testName = new TestName();


    public static void main(String[] args) {
        WalletImpl wallet = new WalletImpl(true);
        try {
            wallet.start(new Scanner(System.in));
        } catch (WalletException e) {
            System.err.println("Errors occurred while processing wallet operations. " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static TreeMap<Currency, Double> extractMap(Wallet wallet) throws NoSuchFieldException, IllegalAccessException {
        Field field = wallet.getClass().getDeclaredField("wallet");
        field.setAccessible(true);
        return (TreeMap<Currency, Double>) field.get(wallet);
    }

    @Test
    public void test01() {
        testPassed("test1");
    }

    @Test
    public void test02() {
        testPassed("test2");
    }

    @Test
    public void test03() {
        testPassed("test3");
    }

    @Test
    public void test04() {
        testPassed("test4");
    }

    @Test
    public void test05() {
        testPassed("test5");
    }

    @Test
    public void test06() {
        testPassed("test6");
    }

    @Test
    public void testNotEnoughMoney() {
        testFailed("testFail1");
    }

    @Test
    public void testStrangeDigit() {
        testFailed("testFail2");
    }

    @Test
    public void testNoSuchCurrency() {
        testFailed("testFail3");
    }

    @Test
    public void testUnknownCommand() {
        testFailed("testFail4");
    }

    @Test
    public void testBadConverting() {
        testFailed("testFail5");
    }

    @Test
    public void testIllegalDeposit1() {
        testFailed("testFail6");
    }

    @Test
    public void testIllegalDeposit2() {
        testFailed("testFail7");
    }

    @Test
    public void testNotEnoughRates() {
        testFailed("testFail8");
    }

    @Test
    public void testFailedWithdraw1() {
        testFailed("testFail9");
    }

    @Test
    public void testFailedShowTotal1() {
        testFailed("testFail10");
    }

    @Test
    public void testFailedWithdraw2() {
        testFailed("testFail11");
    }

    @Test
    public void testFailedShowTotal2() {
        testFailed("testFail12");
    }

    @Test
    public void testFailedDeposit() {
        testFailed("testFail13");
    }

    @Test
    public void testAddCurrency() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            Assert.assertEquals(1, allCurrencies.size());
            wallet.addCurrency("ruble");
            Assert.assertEquals(1, allCurrencies.size());
            wallet.addCurrency("dollar");
            Assert.assertEquals(2, allCurrencies.size());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
    }

    @Test
    public void testDeposit() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100);
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            Assert.assertEquals((Double) 100.0, allCurrencies.firstEntry().getValue());
            wallet.deposit(200, "ruble");
            Assert.assertEquals((Double) 300.0, allCurrencies.firstEntry().getValue());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
    }

    @Test
    public void testBadDeposit() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100, "dollar");
            Assert.fail("Expected an error, because there is no dollars in a wallet");
        } catch (WalletException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testWithdraw() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100);
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            wallet.withdraw(50);
            Assert.assertEquals((Double) 50.0, allCurrencies.firstEntry().getValue());
            wallet.withdraw(50.5, "ruble");
            Assert.fail("Expected an error, because not enough rubles in a wallet");
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        } catch (WalletException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testBadConvert() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100);
            wallet.addCurrency("dollar");
            wallet.convertTo(50, "ruble", "dollar");
            Assert.fail("Expected an error, because there is no rate from ruble to dollar");
        } catch (WalletException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testConvert() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.addCurrency("dollar");
            wallet.setRate("ruble", "dollar", 60);
            wallet.deposit(100);
            wallet.convertTo(50, "ruble", "dollar");
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            Assert.assertEquals((Double) 50., allCurrencies.firstEntry().getValue());
            var iterator = allCurrencies.entrySet().iterator();
            iterator.next();
            Assert.assertEquals((Double) (50. / 60), iterator.next().getValue());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
    }

    @Test
    public void testShow() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100);
            wallet.addCurrency("dollar");
            wallet.deposit(80, "dollar");
            wallet.setRate("ruble", "dollar", 60);
            wallet.showTotalIn();
            wallet.showBalance();
        } catch (WalletException e) {
            Assert.fail("Expected no errors. " + e.getMessage());
        }
    }

    @Test
    public void testClearWallet() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.deposit(100);
            wallet.addCurrency("dollar");
            wallet.deposit(80, "dollar");
            wallet.setRate("ruble", "dollar", 60);
            wallet.clearWallet();
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            Assert.assertEquals(0, allCurrencies.size());
        } catch (WalletException | NoSuchFieldException | IllegalAccessException e) {
            Assert.fail("Expected no errors. " + e.getMessage());
        }
    }

    @Test
    public void testCurrencyOrder() {
        printTestInfo();
        try {
            WalletImpl wallet = new WalletImpl();
            wallet.addCurrency("ruble");
            wallet.addCurrency("dollar");
            wallet.addCurrency("euro");
            TreeMap<Currency, Double> allCurrencies = extractMap(wallet);
            var iterator = allCurrencies.entrySet().iterator();
            Assert.assertEquals("ruble", iterator.next().getKey().getCurrency());
            Assert.assertEquals("dollar", iterator.next().getKey().getCurrency());
            Assert.assertEquals("euro", iterator.next().getKey().getCurrency());
        } catch (WalletException | NoSuchFieldException | IllegalAccessException e) {
            Assert.fail("Expected no errors. " + e.getMessage());
        }
    }

    private void testPassed(String fileName) {
        printTestInfo();
        WalletImpl wallet = new WalletImpl();
        try {
            try (BufferedReader bufferedReader = Files.newBufferedReader
                    (Paths.get(SEPARATOR + "shouldPass" + SEPARATOR + fileName), StandardCharsets.UTF_8)) {
                wallet.start(new Scanner(bufferedReader));
            } catch (IOException ignored) {
            }
        } catch (WalletException e) {
            Assert.fail("Errors occurred while processing wallet operations. " + e.getMessage());
        }
    }

    private void testFailed(String fileName) {
        printTestInfo();
        WalletImpl wallet = new WalletImpl();
        try {
            try (BufferedReader bufferedReader = Files.newBufferedReader
                    (Paths.get(SEPARATOR + "shouldFail" + SEPARATOR + fileName), StandardCharsets.UTF_8)) {
                wallet.start(new Scanner(bufferedReader));
                Assert.fail("Errors should be occurred");
            } catch (IOException ignored) {
            }
        } catch (WalletException e) {
            System.err.println("Errors occurred while processing wallet operations. " + e.getMessage());
        }
    }

    private void printTestInfo() {
        System.err.println("=============" + testName.getMethodName() + "============");
    }

}
