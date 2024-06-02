package ru.stepup.bank;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;

public class Account implements Cloneable {
    private String clientName;  // имя клиента
    private EnumMap<Currency, Integer> currencyMap;   // поле, в котором хранятся пары валюта-количество
    private ArrayList<Account> accountHistory; // история состояний

    public Account(String clientName) {
        if((clientName == null) || (clientName.trim().isEmpty()))
        {
            throw new IllegalArgumentException("Некорректное имя клиента");
        }
        this.clientName = clientName;
        currencyMap = new EnumMap<>(Currency.class);

        // ТЗ: Валюта должна быть представлена таким образом, чтобы указать можно было только значение из некоторого фиксированного
        // списка значений (конкретный перечень допустимо указать произвольно в коде)
        currencyMap.put(Currency.RUR, 0);
        currencyMap.put(Currency.USD, 0);
        currencyMap.put(Currency.EUR, 0);
        // служебное поле для хранения историю коммитов
        accountHistory = new ArrayList<>();
    }

    @Override
    public Account clone() throws CloneNotSupportedException {
        Account acc = (Account) super.clone();
        // клонируем тип хранящий ссылки
        acc.currencyMap = new EnumMap<>(this.currencyMap);
        return acc;        // возвращаем deep copy
    }

    public void setClientName(String clientName) {
        if((clientName == null) || (clientName.trim().isEmpty()))
        {
            throw new IllegalArgumentException("Некорректное имя клиента");
        }
        // сохраняем тек. состояние Account в истории
        Account acc = null;
        try {
            acc = this.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        accountHistory.add(acc);

        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public EnumMap<Currency, Integer> getCurrencyMap() {
        return currencyMap;
    }

    public boolean addCurrencyVal(Currency curCode, Integer saldo) {
        if (saldo < 0) {
            throw new IllegalArgumentException("Ошибочный остаток для валюты " + curCode);
        }

        // сохраняем тек. состояние Account в истории
        Account acc = null;
        try {
            acc = this.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        accountHistory.add(acc);

        // добавим/обновим остаток валюты
        this.currencyMap.put(curCode, saldo);
        return true;
    }

    // ТЗ: метод undo, который будет отменять одно последнее изменение объекта класса Account. Метод должен поддерживать следующие требования:
    public boolean undo() {
        int idx = accountHistory.size() - 1;
        // ТЗ: Откатывать изменения можно до тех пор, пока объект не вернется к состоянию, в котором он был на момент создания
        if (idx < 0)
            // ТЗ: Попытка отменить изменения, если их не было — это ошибка.
            return false;

        Account tmpAccount = this.accountHistory.get(idx);

        // ТЗ: когда в класс будут добавлены новые поля, их можно было учитывать в отмене, однако ранее реализованный код не требовал бы изменения
        Field[] fields = this.getClass().getDeclaredFields();
        // восстанавливаем все поля, кроме: служебного "accountHistory", где хранится история изменений счета и Map хранящегосписок валют
        for (Field field:fields) {
            if ((field.getName() != "currencyMap") && (field.getName() != "accountHistory")){
                field.setAccessible(true);
                try {
                    field.set(this, field.get(tmpAccount));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.currencyMap = tmpAccount.getCurrencyMap(); // откатываем список валют

        accountHistory.remove(idx);
        return true;
    }

    // ТЗ: Необходимо предоставить метод проверки возможности отмены.
    public boolean checkUndo() {
        if (0 < accountHistory.size())
            return true;
        else
            return false;
    }

    // ТЗ: Метод сохранения возвращает объект, который хранит состояние Account на момент запроса сохранения (idx)
    public Account getAccountState(int idx) {
        idx--;
        Account acc = accountHistory.get(idx);
        Account tmpAcc = null;
        try {
            tmpAcc = acc.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return tmpAcc;
    }
}
