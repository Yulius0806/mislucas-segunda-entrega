package org.mislucas.model.game;

import java.util.Objects;

/**
 * Mantiene el saldo de un frasco Para gastar, Mi meta o Colchón.
 */
public final class Jar {
    private final JarType type;
    private Money balance = new Money(0);

    /**
     * Crea un frasco con un saldo de cero pesos.
     *
     * @param type tipo que identifica el frasco
     * @throws NullPointerException si el tipo es nulo
     */
    public Jar(JarType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    /**
     * Devuelve el tipo de este frasco.
     *
     * @return tipo del frasco
     */
    public JarType type() {
        return type;
    }

    /**
     * Devuelve el saldo actual.
     *
     * @return saldo en pesos colombianos enteros
     */
    public Money balance() {
        return balance;
    }

    /**
     * Suma la cantidad indicada al saldo.
     *
     * @param amount cantidad que se depositará
     * @throws NullPointerException si la cantidad es nula
     * @throws ArithmeticException si el saldo resultante supera el rango de enteros
     */
    public void deposit(Money amount) {
        balance = new Money(Math.addExact(balance.pesos(), amount.pesos()));
    }

    /**
     * Resta la cantidad indicada del saldo.
     *
     * @param amount cantidad que se retirará
     * @throws NullPointerException si la cantidad es nula
     * @throws IllegalArgumentException si la cantidad supera el saldo disponible
     */
    public void withdraw(Money amount) {
        if (amount.pesos() > balance.pesos()) {
            throw new IllegalArgumentException("Withdrawal exceeds the available balance.");
        }
        balance = new Money(balance.pesos() - amount.pesos());
    }
}
