package ru.stepup.bank;

public class Start {

    public static void main(String[] args) {
        Account account = new Account("Client1");
        account.addCurrencyVal(Currency.RUR, 100);
        account.addCurrencyVal(Currency.USD, 10);
        System.out.println(account.getClientName() + "; " + account.getCurrencyMap().get(Currency.RUR));
    }
}
