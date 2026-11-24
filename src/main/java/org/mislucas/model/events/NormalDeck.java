package org.mislucas.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.mislucas.model.config.Scenario;

/**
 * Fábrica de los datos del mazo Normal: una tarjeta de cada {@link CardKind}.
 *
 * <p>La lista devuelta sigue el orden documentado y no se mezcla. La partida
 * o un componente posterior de reglas puede elegir y conservar otro orden.</p>
 */
public final class NormalDeck {
    /** Número de tarjetas del mazo Normal (una por semana). */
    public static final int SIZE = CardKind.values().length;

    private NormalDeck() {
    }

    /**
     * Construye el mazo Normal para un escenario.
     *
     * @param scenario escenario elegido; no puede ser {@code null}
     * @return lista inmodificable con {@link #SIZE} tarjetas, una por tipo
     * @throws NullPointerException si {@code scenario} es {@code null}
     */
    public static List<Card> cardsFor(Scenario scenario) {
        Objects.requireNonNull(scenario, "scenario must not be null");
        List<Card> cards = new ArrayList<>(SIZE);
        for (CardKind kind : CardKind.values()) {
            cards.add(new Card(kind, scenario.getCardAmount(kind)));
        }
        return List.copyOf(cards);
    }
}
