# Modelo orientado a objetos

La segunda entrega implementa el dominio del juego en **un módulo Maven con Java 21** y pruebas con JUnit 5. Las clases del dominio no importan JavaFX. La interfaz de la tercera entrega invocará las operaciones del modelo y mostrará sus resultados.

## Estructura de paquetes

El dominio está en `org.mislucas.model`. Las pruebas del dominio usan los paquetes correspondientes bajo `src/test/java`.

| Subpaquete | Responsabilidad |
|---|---|
| `config` | Parámetros del juego, escenarios de recursos y metas de ahorro. |
| `game` | Estado del juego, avance de turnos, frascos y dinero. |
| `events` | Tarjetas y sus efectos. |
| `rules` | Coordinación de reglas financieras como distribución, pagos y rendimientos. |
| `result` | Resultado e indicadores de la partida finalizada. |

La tercera entrega aplicará el patrón Modelo–Vista–Controlador (MVC) a la interfaz JavaFX. Las clases de `org.mislucas.controller` traducirán las acciones del jugador en llamadas al modelo. Las pantallas de `org.mislucas.view` mostrarán el estado y la retroalimentación. El modelo contiene las reglas del juego y puede ejecutarse independientemente de JavaFX.

## Responsabilidades del dominio

| Elemento | Responsabilidad |
|---|---|
| `Scenario` y `Goal` | Proporcionan la mesada, el gasto fijo y el precio de la meta elegida. |
| `Jar` | Encapsula un saldo no negativo y valida los depósitos y retiros. Cada partida tiene frascos Para gastar, Mi meta y Colchón. |
| `Game` | Mantiene la configuración, la semana y fase actuales, los frascos, el mazo, los ingresos recibidos, el uso del frasco Mi meta y el estado final. Rechaza acciones fuera de orden. |
| `Card` y sus tipos | Representan y resuelven eventos de deseo, necesidad, emergencia, oportunidad y semana tranquila. Sus efectos distintos justifican el polimorfismo. |
| `Debt` y `Installment` | Llevan el registro de faltantes financiados, la opción de pago, el interés total, las semanas de vencimiento y el saldo pendiente. |
| `RulesEngine` | Coordina la distribución, las prioridades de pago, el cobro de cuotas, los rendimientos y el cierre sin depender de la interfaz. |
| `GameResult` | Expone los saldos, la deuda pendiente, el saldo neto, la victoria, las estrellas y el porcentaje de ahorro. |

El jugador es un actor, así que no se necesita una clase de cuenta ni de autenticación.

## Invariantes y aspectos que se pueden probar

- Representa el dinero con pesos colombianos enteros; no uses `double` para saldos, intereses ni rendimientos.
- Los frascos no pueden tener saldos negativos. Las deudas se llevan por separado.
- Rechaza una decisión inválida antes de cambiar el estado; una distribución rechazada no modifica ningún frasco.
- Permite fijar el orden de las tarjetas en las pruebas. Una partida normal mezcla el mazo una sola vez.
- Se debe poder probar una partida completa de ocho semanas, incluido el crédito y el resultado, sin JavaFX.
- Las clases y los miembros del modelo necesitan comentarios de documentación de varias líneas; también se deben generar los archivos Javadoc requeridos.
