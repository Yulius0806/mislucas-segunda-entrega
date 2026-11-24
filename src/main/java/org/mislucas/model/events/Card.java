package org.mislucas.model.events;

import java.util.Objects;

/**
 * Datos inmutables de una tarjeta: su tipo y valor en COP.
 *
 * <p>Esta clase solo almacena los datos de la tarjeta; aplicar su efecto
 * corresponde a las reglas del juego.</p>
 */
public final class Card {
    private final CardKind kind;
    private final int amount;

    /**
     * Crea una tarjeta.
     *
     * @param kind tipo de tarjeta; no puede ser {@code null}
     * @param amount valor en COP; solo puede ser cero en una semana tranquila
     * @throws NullPointerException si {@code kind} es {@code null}
     * @throws IllegalArgumentException si el valor no es válido para el tipo
     */
    public Card(CardKind kind, int amount) {
        this.kind = Objects.requireNonNull(kind, "kind must not be null");
        if (kind.getType() == CardType.QUIET_WEEK) {
            if (amount != 0) {
                throw new IllegalArgumentException("A quiet week has no amount");
            }
        } else if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive for " + kind.getDisplayName());
        }
        this.amount = amount;
    }

    /**
     * Devuelve el tipo de esta tarjeta.
     *
     * @return el tipo, nunca {@code null}
     */
    public CardKind getKind() {
        return kind;
    }

    /**
     * Devuelve la categoría de esta tarjeta.
     *
     * @return la categoría, nunca {@code null}
     */
    public CardType getType() {
        return kind.getType();
    }

    /**
     * Devuelve el valor de esta tarjeta.
     *
     * @return el valor en COP
     */
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        Card card = (Card) other;
        return kind == card.kind && amount == card.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, amount);
    }

    @Override
    public String toString() {
        return kind.getDisplayName() + " (COP " + amount + ")";
    }
}
