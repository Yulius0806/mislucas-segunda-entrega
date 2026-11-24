package org.mislucas.model.game;

/**
 * Cantidad de dinero expresada en pesos colombianos enteros.
 *
 * @param pesos cantidad no negativa de pesos colombianos
 */
public record Money(int pesos) {
    /**
     * Crea una cantidad monetaria expresada en pesos colombianos enteros.
     *
     * @param pesos cantidad de pesos colombianos
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    public Money {
        if (pesos < 0) {
            throw new IllegalArgumentException("Money cannot be negative.");
        }
    }
}
