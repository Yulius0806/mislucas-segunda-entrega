package org.mislucas.model.events;

/**
 * Las ocho tarjetas que componen el mazo Normal, una por semana.
 *
 * <p>El orden de declaración sigue la tabla de tarjetas de
 * {@code docs/rules.md}. Los valores dependen del escenario elegido.</p>
 */
public enum CardKind {
    /** Primera compra opcional. */
    WANT_1(CardType.WANT, "Deseo 1"),
    /** Segunda compra opcional. */
    WANT_2(CardType.WANT, "Deseo 2"),
    /** Tercera compra opcional. */
    WANT_3(CardType.WANT, "Deseo 3"),
    /** Necesidad obligatoria. */
    NEED(CardType.NEED, "Necesidad"),
    /** Primer imprevisto. */
    EMERGENCY_1(CardType.EMERGENCY, "Emergencia 1"),
    /** Segundo imprevisto. */
    EMERGENCY_2(CardType.EMERGENCY, "Emergencia 2"),
    /** Ingreso adicional. */
    OPPORTUNITY(CardType.OPPORTUNITY, "Oportunidad"),
    /** Semana sin efecto directo. */
    QUIET_WEEK(CardType.QUIET_WEEK, "Semana tranquila");

    private final CardType type;
    private final String displayName;

    /**
     * Crea un tipo de tarjeta.
     *
     * @param type categoría de la tarjeta
     * @param displayName nombre visible documentado
     */
    CardKind(CardType type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    /**
     * Devuelve la categoría de la tarjeta.
     *
     * @return el tipo de tarjeta, nunca {@code null}
     */
    public CardType getType() {
        return type;
    }

    /**
     * Devuelve el nombre documentado de la tarjeta.
     *
     * @return el nombre visible, nunca {@code null}
     */
    public String getDisplayName() {
        return displayName;
    }
}
