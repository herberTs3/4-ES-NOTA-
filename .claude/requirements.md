# Requerimientos — TI Sokoban

> Leyenda: ✅ Completo · 🔶 Parcial · ❌ Pendiente · ❓ Sin auditar
> Los estados arrancan en ❓: en la primera sesión, auditar el repo y actualizarlos (ver `PLAN_DE_ACCION.md`, Iteración 0).

---

## 1. Tablero de juego

| # | Requerimiento (enunciado) | Estado | Notas |
|---|---|---|---|
| 1.1 | Paredes y espacios vacíos | ❓ | |
| 1.2 | Cajas **normales**, **frágiles** y **llave** | ❓ | ver §4 |
| 1.3 | Casillas de **destino** | ❓ | |
| 1.4 | Terrenos **resbaladizos** | ❓ | |
| 1.5 | Casilleros **cerrojo** | ❓ | |
| 1.6 | **Muros abiertos/cerrados** | ❓ | se abren con caja llave en cerrojo |
| 1.7 | **Jugador** (sokoban) | ❓ | |

## 2. Niveles

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 2.1 | Niveles cargados desde **distintos archivos .txt** con la disposición inicial en caracteres | ❓ | formato en `architecture.md` |
| 2.2 | Varios niveles encadenados (al ganar se avanza al siguiente) | ❓ | |

## 3. Movimiento del jugador

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 3.1 | Movimiento con **flechas direccionales** | ❓ | usar Key Bindings, no KeyListener (ver `architecture.md`) |
| 3.2 | Solo **empuja** cajas, nunca tira de ellas | ❓ | |
| 3.3 | No puede empujar **más de una caja a la vez** | ❓ | |
| 3.4 | No atraviesa cajas, paredes ni **muros cerrados** | ❓ | |

## 4. Tipos de caja

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 4.1 | **Normal**: se empuja en la dirección del jugador | ❓ | |
| 4.2 | Caja sobre **terreno resbaladizo** se desliza hasta chocar con otro objeto o llegar a un espacio no resbaladizo | ❓ | |
| 4.3 | Las cajas no atraviesan otras cajas, paredes ni muros cerrados | ❓ | |
| 4.4 | **Frágil**: mismas reglas que la normal, pero con **resistencia** limitada; cada empuje la reduce; en 0 se **rompe** | ❓ | |
| 4.5 | **Llave**: mismas reglas que la normal; al quedar sobre un **cerrojo** abre los **muros correspondientes** | ❓ | |

## 5. Deshacer (undo)

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 5.1 | Se pueden deshacer los **últimos 15 movimientos** | ❓ | historial acotado a 15 |
| 5.2 | **Botón undo**: cada presión restaura el nivel **5 movimientos atrás** | ❓ | |
| 5.3 | Máximo **3 usos consecutivos** | ❓ | ver decisión D6 |

## 6. Victoria, puntaje e interfaz

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 6.1 | Victoria cuando **todas las cajas están en sus destinos** → avanza al siguiente nivel | ❓ | ver decisión D4 |
| 6.2 | Al terminar cada nivel, **resumen**: movimientos, empujes, usos de undo y **puntaje final** | ❓ | |
| 6.3 | Criterio de puntaje **definido por el equipo** (documentarlo en el informe) | ❓ | ver decisión D7 |
| 6.4 | **HUD**: movimientos, empujes y nivel actual | ❓ | |
| 6.5 | **Botones visibles**: deshacer y reiniciar nivel | ❓ | |

## 7. Gráficos y sonido

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 7.1 | **Todos** los elementos del tablero representados con **imágenes** | ❓ | |
| 7.2 | Acciones acompañadas de **efectos de sonido** apropiados | ❓ | `javax.sound.sampled` solo lee WAV/AIFF/AU → usar **.wav** |
| 7.3 | Todos los assets de **dominio público** (registrar fuente y licencia en `CREDITS.md`) | ❓ | p. ej. Kenney (CC0), OpenGameArt CC0 |

## 8. Funcionalidades adicionales (obligatorio: 2)

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 8.1 | Funcionalidad adicional #1 **que implique aplicar un patrón de diseño** | ❓ | candidatas en `patrones.md` §3 |
| 8.2 | Funcionalidad adicional #2 **que implique aplicar un patrón de diseño** | ❓ | |

## 9. Requisitos técnicos y entregables

| # | Requerimiento | Estado | Notas |
|---|---|---|---|
| 9.1 | **Java + Swing** (2D) | ❓ | sin librerías externas |
| 9.2 | Repositorio **público** en GitHub | ❓ | |
| 9.3 | **README** con instrucciones para ejecutar | ❓ | |
| 9.4 | **Video gameplay** con todas las funcionalidades | ❓ | |
| 9.5 | **Diagrama de clases en StarUML** | ❓ | |
| 9.6 | **Informe técnico** con decisiones de diseño y justificación (incluye declaración de uso de IA) | ❓ | |

---

## Decisiones de diseño a confirmar con el equipo

El enunciado deja huecos. Cada decisión tomada va al informe técnico. Recomendación entre corchetes.

| # | Pregunta | Recomendación |
|---|---|---|
| D1 | ¿Cómo se vincula un cerrojo con "sus" muros? | [Por letra: cerrojo `a`–`e` abre los muros `A`–`E`. Cumple "muros correspondientes" y el .txt sigue siendo un mapa de caracteres.] |
| D2 | Si la caja llave sale del cerrojo, ¿los muros se vuelven a cerrar? | [No, quedan abiertos: evita encerrar al jugador o a una caja dentro de un muro.] |
| D3 | Frágil: ¿cuándo se rompe? ¿qué pasa con ella? | [Resistencia inicial 3; el empuje que la deja en 0 la mueve y luego se rompe (desaparece del tablero, sonido + animación simple).] |
| D4 | ¿Las cajas llave y las frágiles cuentan para la victoria? | [Polimorfismo `box.isInPlace(cell)`: normal/frágil → sobre destino; llave → sobre cerrojo. Si se rompe una frágil necesaria, el HUD avisa "nivel sin solución: deshacé o reiniciá".] |
| D5 | ¿El jugador también resbala en el hielo? | [No, solo las cajas (así lo dice el enunciado).] |
| D6 | Undo: "máximo 3 usos **consecutivos**" vs "3 usos por nivel" (el README actual dice "máximo 3 usos") | [Literal del enunciado: consecutivos; el contador se reinicia cuando el jugador hace un movimiento nuevo. Con historial de 15, 3 usos × 5 = 15. Confirmar con el profe si hay duda.] |
| D7 | Fórmula de puntaje | [p. ej. `max(0, 1000 − 2·movs − 5·empujes − 100·usosUndo)` + bonus si no se usó undo. Encapsularla (Strategy o Decorator) para poder cambiarla.] |
| D8 | ¿El undo restaura los contadores del HUD? | [Restaura el tablero pero los movimientos/empujes realizados siguen contando (si no, el undo sería gratis para el puntaje).] |
