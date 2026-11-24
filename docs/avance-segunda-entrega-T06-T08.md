# Avance de la segunda entrega: T06–T08

## Propósito de la entrega

La segunda entrega se enfoca en el modelo del juego, las reglas financieras, las pruebas JUnit y la generación de Javadoc. La interfaz JavaFX está prevista para la tercera entrega. El modelo debe funcionar sin JavaFX y usar Java 21.

## Contexto del repositorio

En la última revisión del [tablero](https://github.com/users/camarinb2096/projects/1), T02, T03, T04 y T15 estaban en Hecho; T05 aparecía En revisión, aunque su [PR #24](https://github.com/camarinb2096/mislucas/pull/24) ya estaba fusionado a `develop`; T06–T14 estaban en Por hacer. El [PR #23 de T03](https://github.com/camarinb2096/mislucas/pull/23) se fusionó a `main`, no a `develop`, de modo que las ramas no contenían el mismo modelo. Los estados remotos no se modificaron durante este trabajo.

## Qué se preparó

### Dependencia T03

La rama `develop` ya tenía el proyecto Maven, los frascos y el reparto de ingresos, pero no incluía escenarios, metas ni tarjetas. Esos modelos estaban en `main` bajo otro paquete. Se adaptaron a los paquetes de `develop` (`org.mislucas.model.config` y `org.mislucas.model.events`) para que T06 pudiera utilizarlos sin depender de clases fuera de su rama.

### T06: estado y ciclo de la partida

`Game` guarda el escenario, la meta, los tres frascos, la semana, el orden fijo de tarjetas y la fase actual. `GamePhase` representa el reparto, el cobro de cuotas, el gasto fijo, la financiación, la tarjeta, los rendimientos, el cierre semanal y el estado final.

Las operaciones fuera de fase se rechazan. El juego permite recorrer ocho semanas con un orden controlado de tarjetas y queda cerrado después de la semana 8.

### T07: pagos por prioridad

`ExpensePayment` aplica estas prioridades:

- Gasto fijo y necesidad: Para gastar → Colchón → Mi meta.
- Imprevisto: Colchón → Para gastar → Mi meta.
- Gasto fijo: se cobra solo en las semanas 1 y 5.
- Deseo: usa únicamente Para gastar. Si el jugador lo rechaza o no alcanza el saldo, no se compra ni se crea deuda.

Los pagos obligatorios devuelven el faltante para que T08 lo financie.

### T08: financiación y cuotas

`Debt`, `Installment` y `DebtBook` registran el monto financiado, el interés, los vencimientos y el saldo pendiente.

- Una cuota vence la semana siguiente y no genera interés.
- Dos cuotas vencen en las dos semanas siguientes y agregan un 10 % de interés total.
- Si el total queda impar, la primera cuota recibe la mitad redondeada hacia abajo y la segunda recibe el peso restante.
- Las cuotas más antiguas o vencidas se intentan cobrar primero, desde Para gastar → Colchón → Mi meta.
- Un pago parcial reduce el saldo pendiente; los retrasos no vuelven a generar interés.
- Las cuotas con vencimiento después de la semana 8 permanecen como deuda para el cierre.

Como el modelo almacena pesos enteros y las reglas no especificaban cómo redondear un interés fraccionario, se dejó explícito en `docs/rules.md`: 10 % redondeado al peso más cercano, con empate hacia arriba.

## Pruebas y verificación

Se añadieron pruebas JUnit para el ciclo de ocho semanas, las prioridades de pago, la financiación, los pagos parciales y las cuotas vencidas. La suite JUnit no se pudo ejecutar porque el entorno no tiene Maven ni los archivos de JUnit disponibles.

Sí se compiló el código de producción con `javac --release 21` y pasaron comprobaciones manuales de las prioridades de gastos, los planes de cuotas, los pagos parciales y vencidos, los faltantes que llevan a financiación y el cierre del flujo de ocho semanas.

## Ubicación y estado

La copia publicada está en el repositorio personal [Yulius0806/mislucas-segunda-entrega](https://github.com/Yulius0806/mislucas-segunda-entrega), en la rama `main`. Es privado, así que para que tus compañeros lo vean tendrás que agregarlos como colaboradores. No se abrió PR al repositorio original ni se modificó su tablero. La rama local que sirvió de base se llama `feature/T06-game-state`.

La copia reúne la adaptación necesaria de T03 y las tareas T06, T07 y T08. Si desean integrar el avance en el repositorio del curso, conviene revisarlo y separarlo en PRs por tarea, como recomienda la guía del repositorio. También hace falta ejecutar `mvn test` en un entorno que tenga Maven y JUnit disponibles.

Los comentarios y Javadocs nuevos están en español; se conservaron los nombres de clases y métodos en inglés para seguir el estilo del código.

## Qué sigue

Después de integrar y revisar estos cambios, quedan T09 (efectos de las tarjetas), T10 (rendimientos), T11 (cierre y resultado), T12 (partidas completas de prueba), T13 (Javadoc) y T14 (paquete final). JavaFX no hace parte de esta entrega.
