package org.mislucas.model.rules;

import java.util.Objects;
import org.mislucas.model.config.GameParameters;
import org.mislucas.model.game.Jar;
import org.mislucas.model.game.JarType;
import org.mislucas.model.game.Money;

/**
 * Aplica pagos con las prioridades definidas para gastos y compras opcionales.
 *
 * <p>Los faltantes de gastos obligatorios se devuelven para que el sistema de
 * deuda pueda financiarlos. Las compras opcionales nunca generan deuda.</p>
 */
public final class ExpensePayment {
    private final Jar spending;
    private final Jar goal;
    private final Jar cushion;

    /**
     * Asocia la regla de pago con los tres frascos de una partida.
     *
     * @param spending frasco Para gastar
     * @param goal frasco Mi meta
     * @param cushion frasco Colchón
     * @throws NullPointerException si algún frasco es nulo
     * @throws IllegalArgumentException si los frascos no tienen el tipo indicado
     */
    public ExpensePayment(Jar spending, Jar goal, Jar cushion) {
        this.spending = Objects.requireNonNull(spending, "spending");
        this.goal = Objects.requireNonNull(goal, "goal");
        this.cushion = Objects.requireNonNull(cushion, "cushion");
        if (spending.type() != JarType.SPENDING
                || goal.type() != JarType.GOAL
                || cushion.type() != JarType.CUSHION) {
            throw new IllegalArgumentException("Se requieren Para gastar, Mi meta y Colchón en ese orden.");
        }
    }

    /**
     * Cobra el gasto fijo únicamente en las semanas 1 y 5.
     *
     * @param amount valor del gasto fijo
     * @param week semana actual, entre 1 y 8
     * @return faltante que puede recibir la regla de financiación
     * @throws NullPointerException si {@code amount} es nulo
     * @throws IllegalArgumentException si la semana está fuera del juego
     */
    public Money payFixedExpense(Money amount, int week) {
        Objects.requireNonNull(amount, "amount");
        requireValidWeek(week);
        if (week != 1 && week != 5) {
            return new Money(0);
        }
        return payInOrder(amount, spending, cushion, goal);
    }

    /**
     * Paga una necesidad usando Para gastar, Colchón y Mi meta.
     *
     * @param amount valor de la necesidad
     * @return faltante que puede recibir la regla de financiación
     * @throws NullPointerException si {@code amount} es nulo
     */
    public Money payNeed(Money amount) {
        Objects.requireNonNull(amount, "amount");
        return payInOrder(amount, spending, cushion, goal);
    }

    /**
     * Paga un imprevisto usando Colchón, Para gastar y Mi meta.
     *
     * @param amount valor del imprevisto
     * @return faltante que puede recibir la regla de financiación
     * @throws NullPointerException si {@code amount} es nulo
     */
    public Money payEmergency(Money amount) {
        Objects.requireNonNull(amount, "amount");
        return payInOrder(amount, cushion, spending, goal);
    }

    /**
     * Compra un deseo si el jugador lo acepta y Para gastar tiene saldo suficiente.
     *
     * @param amount precio del deseo
     * @return {@code true} si se realizó la compra; {@code false} si no alcanza
     *         el saldo de Para gastar
     * @throws NullPointerException si {@code amount} es nulo
     */
    public boolean buyWant(Money amount) {
        Objects.requireNonNull(amount, "amount");
        if (spending.balance().pesos() < amount.pesos()) {
            return false;
        }
        spending.withdraw(amount);
        return true;
    }

    private Money payInOrder(Money amount, Jar... priority) {
        int remaining = amount.pesos();
        for (Jar jar : priority) {
            int payment = Math.min(remaining, jar.balance().pesos());
            if (payment > 0) {
                jar.withdraw(new Money(payment));
                remaining -= payment;
            }
            if (remaining == 0) {
                break;
            }
        }
        return new Money(remaining);
    }

    private static void requireValidWeek(int week) {
        if (week < 1 || week > GameParameters.WEEKS_PER_GAME) {
            throw new IllegalArgumentException("La semana debe estar entre 1 y 8.");
        }
    }
}
