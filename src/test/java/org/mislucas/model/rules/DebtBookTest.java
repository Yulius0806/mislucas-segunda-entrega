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

class DebtBookTest {
    private Jar spending;
    private Jar goal;
    private Jar cushion;
    private DebtBook debtBook;

    @BeforeEach
    void createDebtBook() {
        spending = new Jar(JarType.SPENDING);
        goal = new Jar(JarType.GOAL);
        cushion = new Jar(JarType.CUSHION);
        debtBook = new DebtBook(spending, goal, cushion);
    }

    @Test
    void oneInstallmentHasNoInterestAndIsDueNextWeek() {
        Debt debt = debtBook.finance(new Money(8_000), 1, FinancingPlan.ONE_INSTALLMENT);

        assertEquals(new Money(8_000), debt.financedAmount());
        assertEquals(new Money(0), debt.interest());
        assertEquals(new Money(8_000), debt.totalOwed());
        assertEquals(1, debt.installments().size());
        assertEquals(2, debt.installments().get(0).dueWeek());
        assertEquals(new Money(8_000), debt.installments().get(0).scheduledAmount());
    }

    @Test
    void twoInstallmentsAddTenPercentInterestAndSplitEvenTotal() {
        Debt debt = debtBook.finance(new Money(8_000), 1, FinancingPlan.TWO_INSTALLMENTS);

        assertEquals(new Money(800), debt.interest());
        assertEquals(new Money(8_800), debt.totalOwed());
        assertEquals(2, debt.installments().size());
        assertEquals(2, debt.installments().get(0).dueWeek());
        assertEquals(3, debt.installments().get(1).dueWeek());
        assertEquals(new Money(4_400), debt.installments().get(0).scheduledAmount());
        assertEquals(new Money(4_400), debt.installments().get(1).scheduledAmount());
    }

    @Test
    void roundsInterestToWholePesosAndAssignsOddPesoToSecondInstallment() {
        Debt debt = debtBook.finance(new Money(8_001), 1, FinancingPlan.TWO_INSTALLMENTS);

        assertEquals(new Money(800), debt.interest());
        assertEquals(new Money(8_801), debt.totalOwed());
        assertEquals(new Money(4_400), debt.installments().get(0).scheduledAmount());
        assertEquals(new Money(4_401), debt.installments().get(1).scheduledAmount());
    }

    @Test
    void collectsFromSpendingThenCushionThenGoal() {
        Debt debt = debtBook.finance(new Money(6_000), 1, FinancingPlan.ONE_INSTALLMENT);
        spending.deposit(new Money(2_000));
        cushion.deposit(new Money(1_500));
        goal.deposit(new Money(3_000));

        debtBook.collectDueInstallments(2);

        assertEquals(new Money(0), spending.balance());
        assertEquals(new Money(0), cushion.balance());
        assertEquals(new Money(500), goal.balance());
        assertTrue(debtBook.goalJarUsedForInstallments());
        assertEquals(0, debtBook.outstandingBalancePesos());
    }

    @Test
    void partialPaymentRemainsDueWithoutAdditionalInterest() {
        Debt debt = debtBook.finance(new Money(8_000), 1, FinancingPlan.ONE_INSTALLMENT);
        spending.deposit(new Money(3_000));

        debtBook.collectDueInstallments(2);

        assertEquals(new Money(5_000), debt.installments().get(0).outstandingBalance());
        assertEquals(new Money(0), debt.interest());
        cushion.deposit(new Money(5_000));
        debtBook.collectDueInstallments(3);

        assertTrue(debt.installments().get(0).isPaid());
        assertEquals(0, debtBook.outstandingBalancePesos());
        assertEquals(new Money(0), cushion.balance());
    }

    @Test
    void collectsOverdueInstallmentsBeforeNewerOnes() {
        Debt overdue = debtBook.finance(new Money(1_000), 1, FinancingPlan.ONE_INSTALLMENT);
        Debt newer = debtBook.finance(new Money(1_000), 2, FinancingPlan.ONE_INSTALLMENT);
        spending.deposit(new Money(1_500));

        debtBook.collectDueInstallments(3);

        assertTrue(overdue.installments().get(0).isPaid());
        assertEquals(new Money(500), newer.installments().get(0).outstandingBalance());
        assertEquals(500, debtBook.outstandingBalancePesos());
    }

    @Test
    void leavesInstallmentsDueAfterWeekEightForTheClosingPhase() {
        Debt debt = debtBook.finance(new Money(8_000), 8, FinancingPlan.TWO_INSTALLMENTS);

        debtBook.collectDueInstallments(8);

        assertEquals(8_800L, debtBook.outstandingBalancePesos());
        assertEquals(9, debt.installments().get(0).dueWeek());
        assertEquals(10, debt.installments().get(1).dueWeek());
        assertFalse(debt.installments().get(0).isPaid());
    }

    @Test
    void rejectsZeroFinancingAndInvalidWeeks() {
        assertThrows(IllegalArgumentException.class,
                () -> debtBook.finance(new Money(0), 1, FinancingPlan.ONE_INSTALLMENT));
        assertThrows(IllegalArgumentException.class,
                () -> debtBook.finance(new Money(1_000), 9, FinancingPlan.ONE_INSTALLMENT));
        assertThrows(IllegalArgumentException.class, () -> debtBook.collectDueInstallments(9));
    }
}
