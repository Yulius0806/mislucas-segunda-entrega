package org.mislucas.model.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.mislucas.model.config.GameParameters;
import org.mislucas.model.game.Money;

/**
 * Deuda creada al financiar un faltante obligatorio.
 *
 * <p>El interés se calcula una sola vez al crear la deuda. En el plan de dos
 * cuotas, un peso sobrante se asigna a la segunda cuota.</p>
 */
public final class Debt {
    private final Money financedAmount;
    private final Money interest;
    private final Money totalOwed;
    private final FinancingPlan plan;
    private final List<Installment> installments;

    /**
     * Crea una deuda y su calendario de pagos.
     *
     * @param financedAmount faltante financiado, mayor que cero
     * @param currentWeek semana en que se origina la deuda, entre 1 y 8
     * @param plan opción de financiación elegida
     * @throws NullPointerException si el monto o el plan son nulos
     * @throws IllegalArgumentException si el monto es cero o la semana es inválida
     * @throws ArithmeticException si el total supera el rango de pesos admitido
     */
    public Debt(Money financedAmount, int currentWeek, FinancingPlan plan) {
        this.financedAmount = Objects.requireNonNull(financedAmount, "financedAmount");
        this.plan = Objects.requireNonNull(plan, "plan");
        if (financedAmount.pesos() == 0) {
            throw new IllegalArgumentException("No se puede financiar un faltante de cero pesos.");
        }
        if (currentWeek < 1 || currentWeek > GameParameters.WEEKS_PER_GAME) {
            throw new IllegalArgumentException("La deuda debe originarse entre las semanas 1 y 8.");
        }

        long interestPesos = plan == FinancingPlan.TWO_INSTALLMENTS
                ? (financedAmount.pesos() + 5L) / 10L
                : 0L;
        int totalPesos = Math.toIntExact(financedAmount.pesos() + interestPesos);
        this.interest = new Money(Math.toIntExact(interestPesos));
        this.totalOwed = new Money(totalPesos);
        this.installments = createInstallments(currentWeek, totalPesos, plan);
    }

    private static List<Installment> createInstallments(
            int currentWeek, int totalPesos, FinancingPlan plan) {
        List<Installment> schedule = new ArrayList<>();
        if (plan == FinancingPlan.ONE_INSTALLMENT) {
            schedule.add(new Installment(currentWeek + 1, new Money(totalPesos)));
        } else {
            int firstPayment = totalPesos / 2;
            int secondPayment = totalPesos - firstPayment;
            schedule.add(new Installment(currentWeek + 1, new Money(firstPayment)));
            schedule.add(new Installment(currentWeek + 2, new Money(secondPayment)));
        }
        return List.copyOf(schedule);
    }

    /**
     * Devuelve el faltante original que se financió.
     *
     * @return monto financiado
     */
    public Money financedAmount() {
        return financedAmount;
    }

    /**
     * Devuelve el interés total calculado al crear la deuda.
     *
     * @return interés, cero para una cuota
     */
    public Money interest() {
        return interest;
    }

    /**
     * Devuelve el total que se debe pagar según el plan elegido.
     *
     * @return total adeudado
     */
    public Money totalOwed() {
        return totalOwed;
    }

    /**
     * Devuelve el plan elegido.
     *
     * @return plan de financiación
     */
    public FinancingPlan plan() {
        return plan;
    }

    /**
     * Devuelve las cuotas y sus fechas de vencimiento.
     *
     * @return lista inmodificable de cuotas
     */
    public List<Installment> installments() {
        return installments;
    }

    /**
     * Devuelve el saldo total pendiente de esta deuda.
     *
     * @return saldo pendiente en pesos
     */
    public long outstandingBalancePesos() {
        long balance = 0;
        for (Installment installment : installments) {
            balance += installment.outstandingBalance().pesos();
        }
        return balance;
    }
}
