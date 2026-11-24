package org.mislucas.model.config;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.mislucas.model.events.CardKind;

/**
 * Escenarios de recursos del juego con los valores documentados en
 * {@code docs/rules.md}.
 *
 * <p>Cada escenario define la mesada semanal, el gasto fijo, el precio de cada
 * {@link Goal} y el valor de cada {@link CardKind} del mazo Normal. Todos los
 * valores se expresan en pesos colombianos enteros.</p>
 */
public enum Scenario {
    /** Ajustado. */
    TIGHT("Tight", "Ajustado", 10_000, 6_000,
            new int[] {40_000, 60_000, 80_000},
            new int[] {5_000, 6_000, 10_000, 8_000, 8_000, 10_000, 10_000, 0}),
    /** Medio. */
    MEDIUM("Medium", "Medio", 20_000, 12_000,
            new int[] {80_000, 120_000, 160_000},
            new int[] {10_000, 12_000, 20_000, 16_000, 16_000, 20_000, 20_000, 0}),
    /** Holgado. */
    COMFORTABLE("Comfortable", "Holgado", 40_000, 24_000,
            new int[] {160_000, 240_000, 320_000},
            new int[] {20_000, 24_000, 40_000, 32_000, 32_000, 40_000, 40_000, 0});

    private final String displayName;
    private final String spanishName;
    private final int weeklyAllowance;
    private final int fixedExpense;
    private final Map<Goal, Integer> goalPrices;
    private final Map<CardKind, Integer> cardAmounts;

    /**
     * Crea un escenario.
     *
     * @param displayName nombre en inglés
     * @param spanishName nombre en español
     * @param weeklyAllowance mesada entregada cada semana, en COP
     * @param fixedExpense gasto fijo cobrado en las semanas 1 y 5, en COP
     * @param goalPrices precios en el orden de declaración de {@link Goal}
     * @param cardAmounts valores en el orden de declaración de {@link CardKind}
     */
    Scenario(String displayName, String spanishName, int weeklyAllowance,
            int fixedExpense, int[] goalPrices, int[] cardAmounts) {
        this.displayName = displayName;
        this.spanishName = spanishName;
        this.weeklyAllowance = weeklyAllowance;
        this.fixedExpense = fixedExpense;
        this.goalPrices = toMap(Goal.values(), goalPrices);
        this.cardAmounts = toMap(CardKind.values(), cardAmounts);
    }

    private static <K extends Enum<K>> Map<K, Integer> toMap(K[] keys, int[] values) {
        if (keys.length != values.length) {
            throw new IllegalStateException("Scenario data does not match " + keys.length + " entries");
        }
        Map<K, Integer> map = new EnumMap<>(keys[0].getDeclaringClass());
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Devuelve el nombre en inglés de este escenario.
     *
     * @return el nombre en inglés
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Devuelve el nombre en español de este escenario.
     *
     * @return el nombre en español
     */
    public String getSpanishName() {
        return spanishName;
    }

    /**
     * Devuelve la mesada semanal.
     *
     * @return la mesada en COP
     */
    public int getWeeklyAllowance() {
        return weeklyAllowance;
    }

    /**
     * Devuelve el gasto fijo que se cobra en las semanas 1 y 5.
     *
     * @return el gasto fijo en COP
     */
    public int getFixedExpense() {
        return fixedExpense;
    }

    /**
     * Devuelve el precio de una meta en este escenario.
     *
     * @param goal la meta; no puede ser {@code null}
     * @return el precio en COP
     * @throws NullPointerException si {@code goal} es {@code null}
     */
    public int getGoalPrice(Goal goal) {
        if (goal == null) {
            throw new NullPointerException("goal must not be null");
        }
        return goalPrices.get(goal);
    }

    /**
     * Devuelve el valor de una tarjeta del mazo Normal en este escenario.
     *
     * @param kind tipo de tarjeta; no puede ser {@code null}
     * @return el valor en COP, nunca negativo
     * @throws NullPointerException si {@code kind} es {@code null}
     */
    public int getCardAmount(CardKind kind) {
        if (kind == null) {
            throw new NullPointerException("kind must not be null");
        }
        return cardAmounts.get(kind);
    }
}
