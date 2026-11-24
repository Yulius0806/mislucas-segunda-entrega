package org.mislucas.model.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mislucas.model.game.Jar;
import org.mislucas.model.game.JarType;
import org.mislucas.model.game.Money;

class ExpensePaymentTest {
    private Jar spending;
    private Jar goal;
    private Jar cushion;
    private ExpensePayment payments;

    @BeforeEach
    void createPaymentRule() {
        spending = new Jar(JarType.SPENDING);
        goal = new Jar(JarType.GOAL);
        cushion = new Jar(JarType.CUSHION);
        payments = new ExpensePayment(spending, goal, cushion);
    }

    @Test
    void chargesFixedExpenseOnlyInWeeksOneAndFive() {
        seed(2_000, 1_000, 3_000);

        assertEquals(new Money(0), payments.payFixedExpense(new Money(6_000), 2));
        assertBalances(2_000, 1_000, 3_000);
        assertEquals(new Money(0), payments.payFixedExpense(new Money(6_000), 1));
        assertBalances(0, 0, 0);

        seed(2_000, 1_000, 3_000);
        assertEquals(new Money(0), payments.payFixedExpense(new Money(6_000), 5));
        assertBalances(0, 0, 0);
    }

    @Test
    void paysNeedsFromSpendingThenCushionThenGoalAndReturnsShortfall() {
        seed(3_000, 1_000, 2_000);

        Money shortfall = payments.payNeed(new Money(8_000));

        assertEquals(new Money(2_000), shortfall);
        assertBalances(0, 0, 0);
    }

    @Test
    void paysEmergenciesFromCushionThenSpendingThenGoal() {
        seed(3_000, 4_000, 2_000);

        Money shortfall = payments.payEmergency(new Money(6_000));

        assertEquals(new Money(0), shortfall);
        assertBalances(0, 3_000, 0);
    }

    @Test
    void refusesAnUnaffordableWantWithoutUsingOtherJarsOrCreatingDebt() {
        seed(1_000, 5_000, 3_000);

        assertFalse(payments.buyWant(new Money(2_000)));
        assertBalances(1_000, 5_000, 3_000);
    }

    @Test
    void buysAnAcceptedWantUsingOnlyTheSpendingJar() {
        seed(3_000, 5_000, 4_000);

        assertTrue(payments.buyWant(new Money(2_000)));
        assertBalances(1_000, 5_000, 4_000);
    }

    @Test
    void rejectsFixedExpenseWeeksOutsideTheGame() {
        assertThrows(IllegalArgumentException.class,
                () -> payments.payFixedExpense(new Money(1_000), 0));
        assertThrows(IllegalArgumentException.class,
                () -> payments.payFixedExpense(new Money(1_000), 9));
    }

    private void seed(int spendingPesos, int goalPesos, int cushionPesos) {
        spending.deposit(new Money(spendingPesos));
        goal.deposit(new Money(goalPesos));
        cushion.deposit(new Money(cushionPesos));
    }

    private void assertBalances(int spendingPesos, int goalPesos, int cushionPesos) {
        assertEquals(new Money(spendingPesos), spending.balance());
        assertEquals(new Money(goalPesos), goal.balance());
        assertEquals(new Money(cushionPesos), cushion.balance());
    }
}
