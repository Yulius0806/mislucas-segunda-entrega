# Mis Lucas

Mis Lucas es un juego educativo de gestión financiera para adolescentes de 12 a 16 años. En una partida individual de ocho semanas, el jugador distribuye su mesada ficticia entre tres frascos, responde a gastos y oportunidades, y trabaja para alcanzar una meta de ahorro. Los valores se simulan en pesos colombianos.

El juego se desarrolla para la asignatura Paradigmas de Programación del Politécnico Grancolombiano. La segunda entrega comprende el modelo orientado a objetos, las reglas de negocio, las pruebas con JUnit y la documentación generada con Javadoc. La interfaz de escritorio con JavaFX corresponde a la tercera entrega.

## Guía de implementación

- [Guía de documentación](docs/README.md): alcance y orden de lectura.
- [Juego, actor y flujo](docs/game.md): acciones del jugador y comportamiento del sistema.
- [Reglas de negocio](docs/rules.md): valores, secuencia semanal, crédito y resultado.
- [Modelo propuesto](docs/model.md): responsabilidades y límites de las clases.
- [Tablero de Mis Lucas en GitHub](https://github.com/users/camarinb2096/projects/1): tareas y progreso del equipo.

Las reglas documentadas son la referencia para implementar y probar el modelo. Todo cambio en una regla debe reflejarse en `docs/rules.md` y en las pruebas relacionadas.

## Tecnologías y entrega actual

Java 21, Maven y JUnit 5. La lógica del juego debe ejecutarse y probarse sin JavaFX. El periodo académico de entrega es del **21 al 24 de noviembre de 2026**.

## Arquitectura

Mis Lucas usa un único módulo Maven. En la segunda entrega, el modelo se implementa en `org.mislucas.model`, con subpaquetes para configuración, estado del juego, eventos, reglas y resultados. En la tercera entrega se completará la estructura Modelo–Vista–Controlador (MVC) con JavaFX. Los controladores enviarán las acciones del jugador al modelo y las vistas mostrarán el estado y la retroalimentación. El modelo contiene las reglas del juego y puede probarse de forma independiente de JavaFX. Consulta el [diseño del modelo](docs/model.md) para conocer las responsabilidades de cada paquete.

## Compilación y verificación

Instala JDK 21 y Maven 3.6.3 o posterior, y verifica que `JAVA_HOME` apunte al JDK. Consulta las versiones activas con `java -version` y `mvn -version`. Maven descargará las dependencias y los complementos del proyecto durante la primera compilación.

```sh
mvn test             # Compila y ejecuta las pruebas de JUnit 5
mvn clean test       # Repite la verificación desde una compilación limpia
mvn javadoc:javadoc  # Genera la documentación en target/site/apidocs
```

El código de producción va en `src/main/java` y las pruebas en `src/test/java`.
