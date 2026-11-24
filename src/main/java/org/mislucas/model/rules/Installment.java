package org.mislucas.model.rules;

import java.util.Objects;
import org.mislucas.model.game.Money;

/**
 * Cuota de una deuda con semana de vencimiento y saldo pendiente.
 */
public final class Installment {
    private final int dueWeek;
    private final Money scheduledAmount;
    private int outstandingPesos;

    Installment(int dueWeek, Money amount) {
        if (dueWeek < 1) {
            throw new IllegalArgumentException("La semana de vencimiento debe ser positiva.");
        }
        this.dueWeek = dueWeek;
        this.scheduledAmount = Objects.requireNonNull(amount, "amount");
        this.outstandingPesos = amount.pesos();
    }

    /**
     * Devuelve la semana en la que venció o vencerá la cuota.
     *
     * @return semana de vencimiento
     */
    public int dueWeek() {
        return dueWeek;
    }

    /**
     * Devuelve el valor inicial de la cuota.
     *
     * @return valor programado
     */
    public Money scheduledAmount() {
        return scheduledAmount;
    }

    /**
     * Devuelve el saldo que aún falta pagar.
     *
     * @return saldo pendiente
     */
    public Money outstandingBalance() {
        return new Money(outstandingPesos);
    }

    /**
     * Indica si la cuota ya fue pagada por completo.
     *
     * @return {@code true} si no queda saldo pendiente
     */
    public boolean isPaid() {
        return outstandingPesos == 0;
    }

    void recordPayment(Money payment) {
        Objects.requireNonNull(payment, "payment");
        if (payment.pesos() > outstandingPesos) {
            throw new IllegalArgumentException("El pago supera el saldo pendiente de la cuota.");
        }
        outstandingPesos -= payment.pesos();
    }
}
