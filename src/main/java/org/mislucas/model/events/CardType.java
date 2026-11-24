package org.mislucas.model.events;

/**
 * Categoría de una tarjeta del mazo Normal.
 *
 * <p>La categoría determina qué regla se aplica al resolverla. Este enum solo
 * clasifica las tarjetas; su resolución corresponde a reglas posteriores.</p>
 */
public enum CardType {
    /** Compra opcional; solo se realiza si el jugador la acepta. */
    WANT,
    /** Gasto cotidiano obligatorio. */
    NEED,
    /** Gasto imprevisto obligatorio. */
    EMERGENCY,
    /** Ingreso adicional que recibe el jugador. */
    OPPORTUNITY,
    /** Semana sin efecto directo. */
    QUIET_WEEK
}
