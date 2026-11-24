package org.mislucas.model.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.mislucas.model.config.GameParameters;
import org.mislucas.model.game.Jar;
import org.mislucas.model.game.JarType;
import org.mislucas.model.game.Money;

/**
 * Registra deudas y cobra las cuotas vencidas usando la prioridad de frascos.
 *
 * <p>Las cuotas vencidas más antiguas se intentan cobrar primero. Un pago
 * parcial conserva el saldo de la cuota y no recalcula el interés.</p>
 */
public final class DebtBook {
    private final Jar spending;
    private final Jar cushion;
    private final Jar goal;
    private final List<Debt> debts = new ArrayList<>();
    private boolean goalJarUsedForInstallments;

    /**
     * Crea un registro de deudas para los frascos de una partida.
     *
     * @param spending frasco Para gastar
     * @param goal frasco Mi meta
     * @param cushion frasco Colchón
     * @throws NullPointerException si algún frasco es nulo
     * @throws IllegalArgumentException si los frascos no tienen el tipo indicado
     */
    public DebtBook(Jar spending, Jar goal, Jar cushion) {
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
     * Registra un faltante obligatorio y crea su calendario de cuotas.
     *
     * @param shortfall faltante que se va a financiar
     * @param currentWeek semana en que se origina la deuda
     * @param plan opción de financiación elegida
     * @return deuda creada, con monto, interés y vencimientos
     * @throws NullPointerException si un argumento es nulo
     * @throws IllegalArgumentException si el faltante es cero o la semana inválida
     */
    public Debt finance(Money shortfall, int currentWeek, FinancingPlan plan) {
        Debt debt = new Debt(shortfall, currentWeek, plan);
        debts.add(debt);
        return debt;
    }

    /**
     * Cobra las cuotas vencidas hasta la semana actual.
     *
     * <p>Los pagos salen de Para gastar, luego Colchón y finalmente Mi meta.
     * Las deudas con vencimiento posterior a la semana 8 quedan pendientes para
     * el cierre de la partida.</p>
     *
     * @param currentWeek semana actual, entre 1 y 8
     * @throws IllegalArgumentException si la semana está fuera del juego
     */
    public void collectDueInstallments(int currentWeek) {
        requireValidWeek(currentWeek);
        List<Installment> due = new ArrayList<>();
        for (Debt debt : debts) {
            for (Installment installment : debt.installments()) {
                if (!installment.isPaid() && installment.dueWeek() <= currentWeek) {
                    due.add(installment);
                }
            }
        }
        due.sort(Comparator.comparingInt(Installment::dueWeek));

        for (Installment installment : due) {
            int remaining = installment.outstandingBalance().pesos();
            remaining = collectFrom(installment, spending, remaining);
            remaining = collectFrom(installment, cushion, remaining);
            collectFrom(installment, goal, remaining);
        }
    }

    private int collectFrom(Installment installment, Jar jar, int remaining) {
        int payment = Math.min(remaining, jar.balance().pesos());
        if (payment > 0) {
            jar.withdraw(new Money(payment));
            installment.recordPayment(new Money(payment));
            if (jar.type() == JarType.GOAL) {
                goalJarUsedForInstallments = true;
            }
            remaining -= payment;
        }
        return remaining;
    }

    /**
     * Devuelve las deudas registradas.
     *
     * @return lista inmodificable de deudas
     */
    public List<Debt> debts() {
        return List.copyOf(debts);
    }

    /**
     * Devuelve el saldo agregado de todas las deudas.
     *
     * @return deuda pendiente en pesos
     */
    public long outstandingBalancePesos() {
        long balance = 0;
        for (Debt debt : debts) {
            balance = Math.addExact(balance, debt.outstandingBalancePesos());
        }
        return balance;
    }

    /**
     * Indica si alguna cuota se pagó usando Mi meta.
     *
     * @return {@code true} si Mi meta se usó para cobrar cuotas
     */
    public boolean goalJarUsedForInstallments() {
        return goalJarUsedForInstallments;
    }

    private static void requireValidWeek(int week) {
        if (week < 1 || week > GameParameters.WEEKS_PER_GAME) {
            throw new IllegalArgumentException("La semana debe estar entre 1 y 8.");
        }
    }
}
