package ru.stepup.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("Иван Иванов");
    }

    @Test
    void setClientNameTest() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> account.setClientName(""));
        Assertions.assertEquals("Некорректное имя клиента", thrown.getMessage());
    }

    @Test
    void addCurrencyValTest() {
        account.addCurrencyVal(Currency.RUR, 200); // изменение существующей валюты
        account.addCurrencyVal(Currency.JPY, 500); // новая валюта

        Assertions.assertEquals(0, account.getCurrencyMap().get(Currency.USD));
        Assertions.assertEquals(200, account.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals(500, account.getCurrencyMap().get(Currency.JPY));
        Assertions.assertThrows(IllegalArgumentException.class, () -> account.addCurrencyVal(Currency.RUR, -100));
    }

    @Test
    void undoTest() {
        // делаем 3 изменения -> 3 commit-а в истории
        account.addCurrencyVal(Currency.USD, 100);
        account.addCurrencyVal(Currency.RUR, 200);
        account.setClientName("Василий Иванов");

        Assertions.assertTrue(account.checkUndo());
        Assertions.assertEquals(200, account.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals(100, account.getCurrencyMap().get(Currency.USD));
        Assertions.assertEquals("Василий Иванов", account.getClientName());

        int idxCommit = 0;    // счетчик commit-ов в истории

        // откатываем в цикле все изменения из истории
        while (account.undo()) {
            idxCommit++;  // считаем количество commitов в истоиии
        }

        Assertions.assertFalse(account.checkUndo());
        Assertions.assertEquals(3, idxCommit);
        Assertions.assertEquals(0, account.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals(0, account.getCurrencyMap().get(Currency.USD));
        Assertions.assertEquals("Иван Иванов", account.getClientName());
    }

    @Test
    void getAccountStateTest() {
        // вносим три изменения в Account (commit)
        account.addCurrencyVal(Currency.RUR, 300); // 1-ый commit предыдущего состояния в историю
        account.setClientName("Василий Иванов");    // 2-ой commit предыдущего состояния в историю
        account.addCurrencyVal(Currency.USD, 400); // 3-ый commit предыдущего состояния в историю

        // берем данные истории при первом commit
        int idxCommit = 1;
        Account accHisory1 = account.getAccountState(idxCommit);
        Assertions.assertEquals(0,accHisory1.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals("Иван Иванов",accHisory1.getClientName());

        // берем данные истории при втором commit
        idxCommit = 2;
        Account accHisory2 = account.getAccountState(idxCommit);
        Assertions.assertEquals(300,accHisory2.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals("Иван Иванов",accHisory2.getClientName());

        // берем данные истории при третьем commit
        idxCommit = 3;
        Account accHisory3 = account.getAccountState(idxCommit);
        Assertions.assertEquals(300,accHisory3.getCurrencyMap().get(Currency.RUR));
        Assertions.assertEquals("Василий Иванов",accHisory3.getClientName());
    }
}
