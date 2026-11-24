# Tipo de juego, actor y flujo

## Tipo y propósito

Mis Lucas es una **simulación educativa por turnos para un solo jugador**. Cada turno representa una semana y la partida dura ocho semanas. El jugador practica cómo elaborar un presupuesto, distinguir entre necesidades y deseos, ahorrar para una meta, crear una reserva para emergencias y comparar el costo de financiar un gasto. El dinero y los rendimientos son ficticios; el juego no representa una cuenta bancaria ni una tarjeta de crédito real.

El público al que se dirige son adolescentes de 12 a 16 años en Colombia. La tercera entrega añadirá una interfaz de escritorio con JavaFX. En la segunda entrega, el modelo Java debe funcionar sin una interfaz gráfica.

## Actor y comportamiento del sistema

| Rol | Acciones o responsabilidades |
|---|---|
| Jugador | Elige un escenario de recursos y una meta de ahorro; distribuye cada ingreso; decide si compra artículos opcionales; elige una o dos cuotas cuando los gastos obligatorios superan los fondos disponibles; consulta la retroalimentación y el resultado final. |
| Sistema del juego | Entrega las mesadas, mezcla y revela las tarjetas, valida las decisiones, cobra los gastos y las cuotas, calcula los rendimientos, lleva el registro de las deudas y determina el resultado y las estrellas. |

El **jugador es el único actor externo** del juego. El profesor y el equipo de desarrollo evalúan o construyen el proyecto, pero no participan en una partida. No hay cuentas de usuario, modo multijugador ni conexiones con servicios financieros.

## Flujo del juego

1. **Preparación:** elige un escenario de recursos y una meta de ahorro. La dificultad Normal usa un mazo de ocho tarjetas.
2. **Semanas 1–8:** recibe una mesada y la distribuye entre los frascos Para gastar (*Spending*), Mi meta (*Goal*) y Colchón (*Cushion*); paga las cuotas y los gastos; resuelve una tarjeta y ve una explicación. Los ingresos por oportunidades se distribuyen de inmediato. Los rendimientos se abonan en las semanas 4 y 8.
3. **Resultado:** salda la deuda pendiente con el frasco Mi meta, comprueba si alcanzó la meta de ahorro y muestra los saldos de los frascos, la deuda restante, el porcentaje de ahorro y las estrellas.

La futura interfaz tendrá tres pantallas previstas: preparación, partida semanal y resultado. El modelo contiene las reglas; los controles visuales solo recogen las acciones y muestran los resultados.

## Alcance de la segunda entrega

Incluye tres escenarios, tres metas, tres frascos, el mazo Normal, pagos, crédito educativo, rendimientos, resultado final, pruebas con JUnit y Javadoc. Las dificultades Fácil y Difícil, más perfiles de jugador, las partidas guardadas y los efectos visuales quedan fuera del alcance mínimo de esta entrega.
