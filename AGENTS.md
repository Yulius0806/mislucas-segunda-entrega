# Guía para contribuir al repositorio

## Estructura del proyecto y módulos

La descripción general del proyecto está en `README.md` y la especificación del juego está en `docs/`. Antes de cambiar el comportamiento, lee `docs/game.md`, `docs/rules.md` y `docs/model.md`. El único módulo Maven contiene el código Java de producción en `src/main/java` y las pruebas con JUnit en `src/test/java`. Ubica el estado del juego y las reglas financieras en `org.mislucas.model`; usa paquetes equivalentes para las pruebas del dominio. La tercera entrega añadirá las vistas y los controladores JavaFX, con recursos en `src/main/resources`.

## Comandos de compilación, pruebas y desarrollo

Ejecuta estos comandos desde la carpeta raíz del módulo Maven.

- `mvn test` — compila el modelo y ejecuta las pruebas con JUnit 5.
- `mvn clean test` — verifica el proyecto desde una compilación limpia.
- `mvn javadoc:javadoc` — genera los archivos Javadoc requeridos para la entrega.

Usa JDK 21. No agregues un comando para ejecutar JavaFX hasta que exista un punto de entrada de la aplicación.

## Estilo de código y convenciones de nombres

Usa cuatro espacios para la indentación de Java y un tipo público de nivel superior por archivo. Nombra las clases con `PascalCase`, los métodos y campos con `camelCase`, y las constantes con `UPPER_SNAKE_CASE`. Representa el dinero como pesos colombianos enteros; no uses `double` para saldos, intereses ni rendimientos. Mantén los frascos en valores no negativos y lleva las deudas por separado. Agrega comentarios Javadoc de varias líneas a las clases y miembros del modelo, incluidos parámetros y resultados cuando corresponda. Aún no hay un formateador ni un analizador estático configurados; conserva un formato uniforme dentro de cada paquete.

## Guía para las pruebas

Usa JUnit 5. Nombra las clases de prueba como `*Test.java` y los métodos según la regla y el resultado esperado, por ejemplo, `rejectsAllocationAboveAllowance`. Cubre el comportamiento normal, los límites, las decisiones inválidas, las cuotas de deuda, el redondeo y una partida completa de ocho semanas con un orden fijo de tarjetas. No se ha definido un umbral numérico de cobertura; las pruebas deben ejecutarse sin JavaFX.

## Commits y solicitudes de cambios

El historial de GitHub contiene `Initial commit` y commits de documentación con el prefijo `docs:`; todavía no define una convención para el código. Usa asuntos breves en modo imperativo y con un prefijo del área, por ejemplo, `model: Validate jar withdrawals`. Nombra las ramas con prefijos de estilo Gitflow y el ID de la tarea, por ejemplo, `feature/T04-jar-balances`, `bugfix/T08-installments` o `release/second-submission`. Abre una solicitud de cambios por cada tarea del tablero de GitHub Project, escribe su título y descripción en inglés, enlaza la incidencia, describe la regla modificada e incluye los comandos y resultados de verificación. Pide la revisión de otro integrante del equipo; solo se necesitan capturas para futuros cambios de interfaz.
