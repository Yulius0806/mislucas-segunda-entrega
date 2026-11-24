package org.mislucas.model.game;

/**
 * Identifica cada uno de los tres frascos de una partida.
 */
public enum JarType {
    /**
     * Guarda dinero para gastar.
     */
    SPENDING,

    /**
     * Guarda el ahorro para la meta del jugador.
     */
    GOAL,

    /**
     * Guarda la reserva para emergencias del jugador.
     */
    CUSHION
}
