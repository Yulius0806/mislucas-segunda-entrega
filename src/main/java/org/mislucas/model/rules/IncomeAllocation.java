package org.mislucas.model.rules;

import org.mislucas.model.game.Jar;
import org.mislucas.model.game.JarType;
import org.mislucas.model.game.Money;

/**
 * Distribuye una mesada o un ingreso por oportunidad entre los tres frascos de la partida.
 */
public final class IncomeAllocation {
    /**
     * Incremento requerido para cada distribución, en pesos colombianos.
     */
    private static final int ALLOCATION_INCREMENT = 1_000;

    private final Jar spending;
    private final Jar goal;
    private final Jar cushion;

    /**
     * Asocia la regla de distribución con los frascos que recibirán el ingreso.
     *
     * @param spending frasco Para gastar
     * @param goal frasco Mi meta
     * @param cushion frasco Colchón
     * @throws NullPointerException si algún frasco es nulo
     * @throws IllegalArgumentException si algún frasco no tiene el tipo requerido
     */
    public IncomeAllocation(Jar spending, Jar goal, Jar cushion) {
        if (spending.type() != JarType.SPENDING
                || goal.type() != JarType.GOAL
                || cushion.type() != JarType.CUSHION) {
            throw new IllegalArgumentException("Use the spending, goal, and cushion jars in that order.");
        }

        this.spending = spending;
        this.goal = goal;
        this.cushion = cushion;
    }

    /**
     * Abona la distribución completa después de validar las cantidades y los saldos resultantes.
     * Si la validación falla, cada frasco conserva su saldo.
     *
     * @param income ingreso total que se va a distribuir
     * @param spendingAmount cantidad que se abonará al frasco Para gastar
     * @param goalAmount cantidad que se abonará al frasco Mi meta
     * @param cushionAmount cantidad que se abonará al frasco Colchón
     * @throws NullPointerException si alguna cantidad es nula
     * @throws IllegalArgumentException si una cantidad no respeta el incremento requerido
     *     o si las cantidades no suman el total del ingreso
     * @throws ArithmeticException si un saldo resultante supera el rango de enteros
     */
    public void apply(Money income, Money spendingAmount, Money goalAmount, Money cushionAmount) {
        int spendingPesos = spendingAmount.pesos();
        int goalPesos = goalAmount.pesos();
        int cushionPesos = cushionAmount.pesos();

        if (spendingPesos % ALLOCATION_INCREMENT != 0
                || goalPesos % ALLOCATION_INCREMENT != 0
                || cushionPesos % ALLOCATION_INCREMENT != 0) {
            throw new IllegalArgumentException("Allocate income in increments of COP 1,000.");
        }

        long allocatedPesos = (long) spendingPesos + goalPesos + cushionPesos;
        if (allocatedPesos != income.pesos()) {
            throw new IllegalArgumentException("Allocate the full income among the three jars.");
        }

        // Verifica cada saldo resultante antes de abonar dinero al primer frasco.
        Math.addExact(spending.balance().pesos(), spendingPesos);
        Math.addExact(goal.balance().pesos(), goalPesos);
        Math.addExact(cushion.balance().pesos(), cushionPesos);

        spending.deposit(spendingAmount);
        goal.deposit(goalAmount);
        cushion.deposit(cushionAmount);
    }
}
