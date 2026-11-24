package org.mislucas.model.game;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import org.mislucas.model.config.GameParameters;
import org.mislucas.model.config.Goal;
import org.mislucas.model.config.Scenario;
import org.mislucas.model.events.Card;
import org.mislucas.model.events.CardKind;
import org.mislucas.model.events.NormalDeck;
import org.mislucas.model.rules.Debt;
import org.mislucas.model.rules.DebtBook;
import org.mislucas.model.rules.ExpensePayment;
import org.mislucas.model.rules.FinancingPlan;
import org.mislucas.model.rules.IncomeAllocation;

/**
 * Mantiene el estado y las fases semanales ordenadas de una partida de ocho semanas.
 *
 * <p>Los efectos de las tarjetas, el pago de gastos, las deudas y los
 * rendimientos se implementan en sus reglas respectivas. Esta clase controla
 * cuándo puede ocurrir cada operación y recibe el orden de las tarjetas para
 * permitir pruebas reproducibles.</p>
 */
public final class Game {
    private final Scenario scenario;
    private final Goal goal;
    private final List<Card> cardOrder;
    private final Jar spendingJar = new Jar(JarType.SPENDING);
    private final Jar goalJar = new Jar(JarType.GOAL);
    private final Jar cushionJar = new Jar(JarType.CUSHION);
    private final ExpensePayment expensePayment =
            new ExpensePayment(spendingJar, goalJar, cushionJar);
    private final DebtBook debtBook = new DebtBook(spendingJar, goalJar, cushionJar);
    private final IncomeAllocation incomeAllocation =
            new IncomeAllocation(spendingJar, goalJar, cushionJar);

    private int currentWeek = 1;
    private int nextCardIndex;
    private Card pendingCard;
    private Money pendingShortfall = new Money(0);
    private GamePhase phaseAfterFinancing;
    private GamePhase phase = GamePhase.ALLOCATION;

    /**
     * Inicia una partida con un orden fijo de ocho tarjetas.
     *
     * @param scenario escenario de recursos elegido
     * @param goal meta de ahorro elegida
     * @param cardOrder las ocho tarjetas en el orden en que se revelarán
     * @throws NullPointerException si algún argumento o tarjeta es nulo
     * @throws IllegalArgumentException si el orden no contiene una tarjeta de
     *         cada tipo con valores que correspondan al escenario
     */
    public Game(Scenario scenario, Goal goal, List<Card> cardOrder) {
        this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
        this.goal = Objects.requireNonNull(goal, "goal must not be null");
        this.cardOrder = validateCardOrder(scenario, cardOrder);
    }

    private static List<Card> validateCardOrder(Scenario scenario, List<Card> cards) {
        Objects.requireNonNull(cards, "cardOrder must not be null");
        if (cards.size() != NormalDeck.SIZE) {
            throw new IllegalArgumentException("A game requires exactly " + NormalDeck.SIZE + " cards.");
        }

        EnumSet<CardKind> kinds = EnumSet.noneOf(CardKind.class);
        for (Card card : cards) {
            Objects.requireNonNull(card, "cardOrder must not contain null");
            if (!kinds.add(card.getKind())) {
                throw new IllegalArgumentException("A card kind cannot appear more than once.");
            }
            if (card.getAmount() != scenario.getCardAmount(card.getKind())) {
                throw new IllegalArgumentException("Card amount does not match the selected scenario.");
            }
        }
        if (kinds.size() != NormalDeck.SIZE) {
            throw new IllegalArgumentException("The card order must contain every Normal card kind.");
        }
        return List.copyOf(cards);
    }

    /**
     * Devuelve el escenario elegido.
     *
     * @return el escenario, nunca {@code null}
     */
    public Scenario scenario() {
        return scenario;
    }

    /**
     * Devuelve la meta de ahorro elegida.
     *
     * @return la meta, nunca {@code null}
     */
    public Goal goal() {
        return goal;
    }

    /**
     * Devuelve la semana actual, entre 1 y 8.
     *
     * @return la semana actual
     */
    public int currentWeek() {
        return currentWeek;
    }

    /**
     * Devuelve la fase semanal actual.
     *
     * @return la fase actual, nunca {@code null}
     */
    public GamePhase phase() {
        return phase;
    }

    /**
     * Devuelve el frasco Para gastar.
     *
     * @return el frasco Para gastar
     */
    public Jar spendingJar() {
        return spendingJar;
    }

    /**
     * Devuelve el frasco Mi meta.
     *
     * @return el frasco Mi meta
     */
    public Jar goalJar() {
        return goalJar;
    }

    /**
     * Devuelve el frasco Colchón.
     *
     * @return el frasco Colchón
     */
    public Jar cushionJar() {
        return cushionJar;
    }

    /**
     * Devuelve el registro de deudas y cuotas de la partida.
     *
     * @return registro de deudas
     */
    public DebtBook debtBook() {
        return debtBook;
    }

    /**
     * Distribuye toda la mesada semanal entre los tres frascos.
     *
     * @param spendingAmount valor asignado al frasco Para gastar
     * @param goalAmount valor asignado al frasco Mi meta
     * @param cushionAmount valor asignado al frasco Colchón
     * @throws IllegalStateException si la partida no está en la fase de reparto
     * @throws IllegalArgumentException si el reparto no es válido
     */
    public void allocateWeeklyAllowance(
            Money spendingAmount, Money goalAmount, Money cushionAmount) {
        requirePhase(GamePhase.ALLOCATION);
        incomeAllocation.apply(new Money(scenario.getWeeklyAllowance()),
                spendingAmount, goalAmount, cushionAmount);
        phase = GamePhase.INSTALLMENT_COLLECTION;
    }

    /**
     * Completa el cobro de cuotas de esta semana.
     *
     * <p>Las reglas de cobro corresponden a la tarea de deuda; esta transición
     * conserva el orden semanal hasta que se agregue ese comportamiento.</p>
     *
     * @throws IllegalStateException si la partida no está cobrando cuotas
     */
    public void completeInstallmentCollection() {
        requirePhase(GamePhase.INSTALLMENT_COLLECTION);
        debtBook.collectDueInstallments(currentWeek);
        phase = GamePhase.FIXED_EXPENSE;
    }

    /**
     * Cobra el gasto fijo si corresponde y completa esta fase.
     *
     * @throws IllegalStateException si la partida no está en la fase del gasto fijo
     * @return faltante que debe financiarse; cero si los frascos cubrieron el gasto
     */
    public Money completeFixedExpense() {
        requirePhase(GamePhase.FIXED_EXPENSE);
        Money shortfall = expensePayment.payFixedExpense(
                new Money(scenario.getFixedExpense()), currentWeek);
        continueAfterPayment(shortfall, GamePhase.CARD_RESOLUTION);
        return shortfall;
    }

    /**
     * Revela la tarjeta de esta semana y la conserva hasta completar su resolución.
     *
     * @return la tarjeta actual
     * @throws IllegalStateException si la fase de tarjeta no está activa o ya
     *         se reveló la tarjeta de esta semana
     */
    public Card drawCard() {
        requirePhase(GamePhase.CARD_RESOLUTION);
        if (pendingCard != null) {
            throw new IllegalStateException("This week's card has already been revealed.");
        }
        pendingCard = cardOrder.get(nextCardIndex);
        return pendingCard;
    }

    /**
     * Paga la necesidad de la tarjeta actual y devuelve cualquier faltante.
     *
     * @return faltante para financiar; cero si los frascos cubrieron el gasto
     * @throws IllegalStateException si no hay una tarjeta de necesidad en resolución
     */
    public Money payCurrentNeed() {
        requireCurrentCardType(org.mislucas.model.events.CardType.NEED);
        Money shortfall = expensePayment.payNeed(new Money(pendingCard.getAmount()));
        continueAfterPayment(shortfall, GamePhase.CARD_RESOLUTION);
        return shortfall;
    }

    /**
     * Paga el imprevisto de la tarjeta actual y devuelve cualquier faltante.
     *
     * @return faltante para financiar; cero si los frascos cubrieron el gasto
     * @throws IllegalStateException si no hay una tarjeta de imprevisto en resolución
     */
    public Money payCurrentEmergency() {
        requireCurrentCardType(org.mislucas.model.events.CardType.EMERGENCY);
        Money shortfall = expensePayment.payEmergency(new Money(pendingCard.getAmount()));
        continueAfterPayment(shortfall, GamePhase.CARD_RESOLUTION);
        return shortfall;
    }

    /**
     * Intenta comprar el deseo de la tarjeta actual si el jugador lo acepta.
     *
     * @param accepted indica si el jugador acepta la compra
     * @return {@code true} si se compró; {@code false} si se rechazó o no alcanza
     *         el saldo de Para gastar
     * @throws IllegalStateException si no hay una tarjeta de deseo en resolución
     */
    public boolean buyCurrentWant(boolean accepted) {
        requireCurrentCardType(org.mislucas.model.events.CardType.WANT);
        return accepted && expensePayment.buyWant(new Money(pendingCard.getAmount()));
    }

    /**
     * Registra la financiación elegida para el faltante obligatorio pendiente.
     *
     * @param plan opción de una o dos cuotas
     * @return deuda creada con sus montos y semanas de vencimiento
     * @throws IllegalStateException si no hay un faltante pendiente de financiar
     */
    public Debt financePendingShortfall(FinancingPlan plan) {
        requirePhase(GamePhase.FINANCING);
        Debt debt = debtBook.finance(pendingShortfall, currentWeek, plan);
        pendingShortfall = new Money(0);
        phase = phaseAfterFinancing;
        phaseAfterFinancing = null;
        return debt;
    }

    /**
     * Completa la tarjeta actual y avanza a la fase de rendimientos.
     *
     * @throws IllegalStateException si no se reveló una tarjeta o la fase de
     *         tarjeta no está activa
     */
    public void completeCardResolution() {
        requirePhase(GamePhase.CARD_RESOLUTION);
        if (pendingCard == null) {
            throw new IllegalStateException("Reveal this week's card before completing its resolution.");
        }
        nextCardIndex++;
        pendingCard = null;
        phase = GamePhase.RETURN_CREDIT;
    }

    /**
     * Completa la fase de abono de rendimientos de esta semana.
     *
     * @throws IllegalStateException si la partida no está en la fase de rendimientos
     */
    public void completeReturnCredit() {
        requirePhase(GamePhase.RETURN_CREDIT);
        phase = GamePhase.WEEK_COMPLETE;
    }

    /**
     * Avanza a la semana siguiente o cierra la partida después de la semana 8.
     *
     * @throws IllegalStateException si la secuencia de la semana actual está incompleta
     */
    public void advanceWeek() {
        requirePhase(GamePhase.WEEK_COMPLETE);
        if (currentWeek == GameParameters.WEEKS_PER_GAME) {
            phase = GamePhase.CLOSED;
            return;
        }
        currentWeek++;
        phase = GamePhase.ALLOCATION;
    }

    private void requirePhase(GamePhase expected) {
        if (phase != expected) {
            throw new IllegalStateException(
                    "Expected phase " + expected + " but the game is in " + phase + ".");
        }
    }

    private void requireCurrentCardType(org.mislucas.model.events.CardType expectedType) {
        requirePhase(GamePhase.CARD_RESOLUTION);
        if (pendingCard == null || pendingCard.getType() != expectedType) {
            throw new IllegalStateException("La tarjeta actual no corresponde a " + expectedType + ".");
        }
    }

    private void continueAfterPayment(Money shortfall, GamePhase nextPhase) {
        if (shortfall.pesos() > 0) {
            pendingShortfall = shortfall;
            phaseAfterFinancing = nextPhase;
            phase = GamePhase.FINANCING;
        } else {
            phase = nextPhase;
        }
    }
}
