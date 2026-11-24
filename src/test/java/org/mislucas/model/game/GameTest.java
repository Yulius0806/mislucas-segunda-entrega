package org.mislucas.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mislucas.model.config.Goal;
import org.mislucas.model.config.Scenario;
import org.mislucas.model.events.Card;
import org.mislucas.model.events.CardKind;
import org.mislucas.model.events.NormalDeck;
import org.mislucas.model.rules.Debt;
import org.mislucas.model.rules.FinancingPlan;

class GameTest {
    private final Scenario scenario = Scenario.MEDIUM;
    private final List<Card> fixedOrder = NormalDeck.cardsFor(scenario);

    private Game newGame() {
        return new Game(scenario, Goal.SNEAKERS, fixedOrder);
    }

    @Test
    void startsInWeekOneWithEmptyJarsAndAllocationPhase() {
        Game game = newGame();

        assertEquals(1, game.currentWeek());
        assertEquals(GamePhase.ALLOCATION, game.phase());
        assertEquals(Goal.SNEAKERS, game.goal());
        assertEquals(new Money(0), game.spendingJar().balance());
        assertEquals(new Money(0), game.goalJar().balance());
        assertEquals(new Money(0), game.cushionJar().balance());
    }

    @Test
    void rejectsActionsOutsideTheirPhaseWithoutChangingState() {
        Game game = newGame();

        assertThrows(IllegalStateException.class, game::completeInstallmentCollection);
        assertThrows(IllegalStateException.class, game::advanceWeek);

        assertEquals(1, game.currentWeek());
        assertEquals(GamePhase.ALLOCATION, game.phase());
        assertEquals(new Money(0), game.spendingJar().balance());
    }

    @Test
    void rejectsInvalidAllocationWithoutChangingJarsOrPhase() {
        Game game = newGame();

        assertThrows(IllegalArgumentException.class,
                () -> game.allocateWeeklyAllowance(new Money(8_000), new Money(7_000), new Money(4_000)));

        assertEquals(GamePhase.ALLOCATION, game.phase());
        assertEquals(new Money(0), game.spendingJar().balance());
        assertEquals(new Money(0), game.goalJar().balance());
        assertEquals(new Money(0), game.cushionJar().balance());
    }

    @Test
    void rejectsRepeatedAllocationAndRepeatedCardDraw() {
        Game game = newGame();
        game.allocateWeeklyAllowance(new Money(8_000), new Money(7_000), new Money(5_000));
        assertThrows(IllegalStateException.class,
                () -> game.allocateWeeklyAllowance(new Money(8_000), new Money(7_000), new Money(5_000)));

        game.completeInstallmentCollection();
        game.completeFixedExpense();
        Card firstCard = game.drawCard();
        assertThrows(IllegalStateException.class, game::drawCard);
        assertEquals(firstCard, fixedOrder.get(0));
        assertEquals(1, game.currentWeek());
    }

    @Test
    void followsAControlledCardOrderAndClosesAfterEightWeeks() {
        Game game = newGame();

        for (int week = 1; week <= 8; week++) {
            assertEquals(week, game.currentWeek());
            game.allocateWeeklyAllowance(new Money(8_000), new Money(7_000), new Money(5_000));
            game.completeInstallmentCollection();
            game.completeFixedExpense();
            assertEquals(fixedOrder.get(week - 1), game.drawCard());
            game.completeCardResolution();
            game.completeReturnCredit();
            game.advanceWeek();
        }

        assertEquals(8, game.currentWeek());
        assertEquals(GamePhase.CLOSED, game.phase());
        assertEquals(new Money(44_000), game.spendingJar().balance());
        assertEquals(new Money(56_000), game.goalJar().balance());
        assertEquals(new Money(36_000), game.cushionJar().balance());
        assertThrows(IllegalStateException.class, game::advanceWeek);
        assertThrows(IllegalStateException.class,
                () -> game.allocateWeeklyAllowance(new Money(8_000), new Money(7_000), new Money(5_000)));
    }

    @Test
    void financesFixedExpenseAndNeedShortfallsBeforeContinuing() {
        List<Card> cards = new ArrayList<>(NormalDeck.cardsFor(Scenario.TIGHT));
        int needIndex = 0;
        for (int index = 0; index < cards.size(); index++) {
            if (cards.get(index).getKind() == CardKind.NEED) {
                needIndex = index;
                break;
            }
        }
        Collections.swap(cards, 0, needIndex);
        Game game = new Game(Scenario.TIGHT, Goal.HEADPHONES, cards);
        game.allocateWeeklyAllowance(new Money(10_000), new Money(0), new Money(0));
        game.completeInstallmentCollection();
        game.spendingJar().withdraw(new Money(7_000));

        assertEquals(new Money(3_000), game.completeFixedExpense());
        assertEquals(GamePhase.FINANCING, game.phase());
        Debt fixedExpenseDebt = game.financePendingShortfall(FinancingPlan.ONE_INSTALLMENT);
        assertEquals(new Money(3_000), fixedExpenseDebt.totalOwed());
        assertEquals(GamePhase.CARD_RESOLUTION, game.phase());

        assertEquals(CardKind.NEED, game.drawCard().getKind());
        assertEquals(new Money(8_000), game.payCurrentNeed());
        assertEquals(GamePhase.FINANCING, game.phase());
        Debt needDebt = game.financePendingShortfall(FinancingPlan.TWO_INSTALLMENTS);
        assertEquals(new Money(800), needDebt.interest());
        assertEquals(new Money(8_800), needDebt.totalOwed());
        assertEquals(GamePhase.CARD_RESOLUTION, game.phase());
        assertEquals(2, game.debtBook().debts().size());
    }

    @Test
    void rejectsADeckWithDuplicateKinds() {
        List<Card> duplicateOrder = new ArrayList<>(fixedOrder);
        duplicateOrder.set(1, duplicateOrder.get(0));

        assertThrows(IllegalArgumentException.class,
                () -> new Game(scenario, Goal.SNEAKERS, duplicateOrder));
    }
}
