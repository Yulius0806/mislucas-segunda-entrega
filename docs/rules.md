# Reglas de negocio

Estas reglas son la referencia para el modelo y sus pruebas. Los valores son **supuestos de simulación** y se pueden ajustar cuando funcione una partida completa.

## Preparación y dinero

Todos los frascos comienzan en COP 0. Guarda los saldos como pesos enteros; ningún frasco puede tener saldo negativo. Los ingresos se deben distribuir en incrementos de COP 1.000. Los rendimientos y las cuotas pueden dejar saldos que no sean múltiplos de COP 1.000.

Los depósitos y retiros de los frascos aceptan cantidades enteras de pesos no negativas. Se puede retirar todo el saldo disponible. Si se intenta retirar más del saldo disponible, se rechaza la operación y el saldo no cambia.

| Escenario | Mesada semanal | Gasto fijo en semanas 1 y 5 | Audífonos | Tenis | Viaje de curso |
|---|---:|---:|---:|---:|---:|
| Ajustado (*Tight*) | COP 10.000 | COP 6.000 | COP 40.000 | COP 60.000 | COP 80.000 |
| Medio (*Medium*) | COP 20.000 | COP 12.000 | COP 80.000 | COP 120.000 | COP 160.000 |
| Holgado (*Comfortable*) | COP 40.000 | COP 24.000 | COP 160.000 | COP 240.000 | COP 320.000 |

El mazo Normal contiene una tarjeta por semana. Se mezcla una sola vez al inicio de la partida y no se repiten tarjetas. Las pruebas pueden usar un orden fijo.

| Tarjeta | Ajustado | Medio | Holgado |
|---|---:|---:|---:|
| Deseo 1 | COP 5.000 | COP 10.000 | COP 20.000 |
| Deseo 2 | COP 6.000 | COP 12.000 | COP 24.000 |
| Deseo 3 | COP 10.000 | COP 20.000 | COP 40.000 |
| Necesidad | COP 8.000 | COP 16.000 | COP 32.000 |
| Emergencia 1 | COP 8.000 | COP 16.000 | COP 32.000 |
| Emergencia 2 | COP 10.000 | COP 20.000 | COP 40.000 |
| Oportunidad | +COP 10.000 | +COP 20.000 | +COP 40.000 |
| Semana tranquila | COP 0 | COP 0 | COP 0 |

## Secuencia semanal

1. Entrega la mesada semanal y distribúyela **por completo** entre los frascos Para gastar (*Spending*), Mi meta (*Goal*) y Colchón (*Cushion*). Valida las tres cantidades antes de abonar dinero a cualquier frasco. Rechaza cantidades negativas, una suma incorrecta o valores que no sean múltiplos de COP 1.000. Si se rechaza la distribución, ningún frasco cambia su saldo anterior. Aplica estas mismas reglas a los ingresos por oportunidades.
2. Después de la distribución y antes de los demás gastos, cobra las cuotas que vencen esa semana, usando este orden: Para gastar → Colchón → Mi meta. Cobra primero las cuotas vencidas. Toda parte que no se pague sigue pendiente y no genera intereses adicionales.
3. Cobra el gasto fijo únicamente en las semanas 1 y 5. Financia cualquier faltante obligatorio.
4. Revela y resuelve una tarjeta. El deseo solo se compra si el jugador lo acepta y Para gastar tiene dinero suficiente; nunca genera deuda. Una necesidad o un gasto fijo se pagan en este orden: Para gastar → Colchón → Mi meta. Una emergencia se paga en este orden: Colchón → Para gastar → Mi meta. Financia los faltantes obligatorios. Distribuye de inmediato cualquier ingreso por oportunidad como un ingreso nuevo. La tarjeta de semana tranquila no tiene efecto directo.
5. En las semanas 4 y 8, abona a Mi meta y Colchón un rendimiento del 2 % del saldo de cada frasco, redondeado **por separado para cada frasco** a los COP 100 más cercanos; en caso de empate, redondea hacia arriba. Para gastar no genera rendimiento.
6. Muestra los saldos, la deuda y una explicación de los eventos de la semana. La partida termina después de la semana 8; no se permiten más turnos.

## Crédito educativo

Cuando un gasto fijo, una necesidad o una emergencia genera un faltante, el jugador elige:

| Opción | Total adeudado | Fecha de vencimiento |
|---|---|---|
| Una cuota | El faltante, sin intereses | Semana siguiente |
| Dos cuotas | El faltante más un 10 % de interés total, calculado una sola vez | La mitad en cada una de las dos semanas siguientes |

Como los saldos usan pesos enteros, redondea el interés del 10 % al peso más cercano; en caso de empate de medio peso, redondea hacia arriba.

Si el total en pesos es impar, redondea hacia abajo la primera cuota y asigna el peso restante a la segunda. Un pago parcial reduce la deuda; intenta cobrar la parte pendiente la semana siguiente, antes de las cuotas nuevas y sin agregar intereses. Las cuotas con vencimiento posterior a la semana 8 siguen contando como deuda al cierre. Explica al jugador el monto financiado, los intereses y las semanas de pago. **Ejemplo:** financiar COP 8.000 cuesta COP 8.000 en una cuota o COP 8.800 en dos cuotas de COP 4.400.

## Cierre y resultado

Después del rendimiento de la semana 8, paga la deuda pendiente con el frasco Mi meta hasta agotarlo. Muestra cualquier saldo restante como un **monto de deuda negativo separado**; los frascos se mantienen en cero o más.

- **Saldo neto final** = Para gastar + Mi meta + Colchón − deuda pendiente. Un valor negativo significa derrota.
- **Victoria:** Mi meta debe cubrir el precio elegido después de saldar la deuda y el saldo neto final debe ser no negativo.
- **Estrellas:** 0 si no hay victoria; 1 por una victoria; 2 si Mi meta nunca se usó para gastos obligatorios ni cuotas; 3 si, además, Colchón termina con al menos una mesada semanal del escenario elegido. Usar Mi meta para saldar la deuda al final también impide obtener la segunda estrella.
- **Porcentaje de ahorro** = (saldo final de Mi meta + saldo final de Colchón) ÷ (mesadas + oportunidades + rendimientos recibidos) × 100. Por decisión del equipo, el denominador incluye los rendimientos.

Ejemplos verificables: el 2 % de COP 7.500 es COP 150, que se redondea a un abono de COP 200. Si los tres frascos terminan en COP 0 y quedan COP 2.000 de deuda, el saldo neto final es −COP 2.000 y el jugador pierde.
