package org.mislucas.model.config;

/**
 * Metas de ahorro que el jugador puede elegir al iniciar la partida.
 *
 * <p>El precio depende del {@link Scenario} elegido; se obtiene mediante
 * {@link #getPrice(Scenario)} y se expresa en pesos colombianos enteros.</p>
 */
public enum Goal {
    /** Meta de los audífonos. */
    HEADPHONES("Audifonos"),
    /** Meta de los tenis. */
    SNEAKERS("Tenis"),
    /** Meta del viaje de curso. */
    CLASS_TRIP("Viaje de curso");

    private final String displayName;

    /**
     * Crea una meta.
     *
     * @param displayName nombre tal como aparece en la documentación
     */
    Goal(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Devuelve el nombre documentado de esta meta.
     *
     * @return el nombre visible, nunca {@code null}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Devuelve el precio de esta meta en el escenario indicado.
     *
     * @param scenario escenario elegido; no puede ser {@code null}
     * @return el precio en COP
     * @throws NullPointerException si {@code scenario} es {@code null}
     */
    public int getPrice(Scenario scenario) {
        if (scenario == null) {
            throw new NullPointerException("scenario must not be null");
        }
        return scenario.getGoalPrice(this);
    }
}
