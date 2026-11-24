package org.mislucas.model.game;

/**
 * Fases ordenadas de un turno semanal y estado final de la partida.
 */
public enum GamePhase {
    /** El jugador distribuye la mesada semanal. */
    ALLOCATION,
    /** El sistema cobra las cuotas que vencen esta semana. */
    INSTALLMENT_COLLECTION,
    /** El sistema cobra el gasto fijo cuando corresponde. */
    FIXED_EXPENSE,
    /** El jugador decide cómo financiar un faltante obligatorio. */
    FINANCING,
    /** El sistema revela y resuelve la única tarjeta de esta semana. */
    CARD_RESOLUTION,
    /** El sistema abona los rendimientos cuando corresponde. */
    RETURN_CREDIT,
    /** El resumen semanal está listo y se puede avanzar de semana. */
    WEEK_COMPLETE,
    /** Terminó la octava semana y la partida no puede continuar. */
    CLOSED
}
