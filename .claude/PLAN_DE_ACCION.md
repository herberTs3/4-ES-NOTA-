# Plan de Acción — TI Sokoban (end-to-end)

> Creado: 2026-11-03 · **Demo + entrega: miércoles 2026-11-18**
> Leyenda: ✅ Completo · 🔶 Parcial · ❌ Pendiente · ❓ Sin auditar
> Actualizar este archivo al cerrar cada ítem.

---

## Calendario (15 días)

| Iteración | Fechas | Objetivo | Resultado demostrable |
|---|---|---|---|
| 0 | mar 3 – mié 4 | Auditoría del repo + decisiones D1–D8 + elección de las 2 funcionalidades extra | Plan y decisiones cerradas |
| 1 | mié 4 – vie 6 | Núcleo jugable: MVC, niveles .txt (Builder), movimiento, empuje de cajas normales, victoria y paso de nivel | Se juegan 2–3 niveles clásicos con sprites |
| 2 | sáb 7 – lun 9 | Cajas frágiles y llave, hielo, cerrojos y muros, undo (Memento) y reinicio (Prototype), Command | Todas las mecánicas del enunciado |
| 3 | mar 10 – jue 12 | HUD completo, resumen y puntaje, sonidos (Observer + Adapter + Singleton), assets finales, 5+ niveles | Juego completo según enunciado |
| 4 | vie 13 – dom 15 | Las 2 funcionalidades adicionales con patrón | Features extra funcionando |
| 5 | lun 16 – mar 17 | Diagrama StarUML, informe técnico, README, video gameplay, ensayo de la demo, pulido | Entregables listos |
| — | **mié 18** | **Demo en vivo + mail de entrega** | |

> Si el repo ya tiene funcionalidades hechas (el README del proyecto menciona carga de niveles, cajas especiales,
> undo, HUD y sonido), la Iteración 0 marca qué está ✅ y el calendario se adelanta: el tiempo liberado va a
> **revisar que los patrones estén bien aplicados** y a las funcionalidades extra.

---

## Iteración 0 — Auditoría y decisiones

| Tarea | Estado |
|---|---|
| Recorrer `src/` y listar clases por capa (model / view / controller / otros) | ❓ |
| Detectar violaciones de MVC (Swing en el modelo, lógica en la vista) | ❓ |
| Detectar code smells (Large class, Switch por tipo, Duplicate code, Long method) | ❓ |
| Marcar estados reales en `requirements.md` | ❓ |
| Listar patrones ya presentes y si están bien aplicados/nombrados → `patrones-aplicados.md` | ❓ |
| Cerrar decisiones D1–D8 con el equipo | ❌ |
| Elegir las 2 funcionalidades adicionales (ver `patrones.md` §3) | ❌ |
| Verificar que el repo es público y que el README tiene instrucciones de ejecución | ❓ |

**Decisiones tomadas** (completar):

| # | Decisión final | Fecha |
|---|---|---|
| D1 | Provisional, a confirmar: por letra, el cerrojo `a` abre los muros `A` | 2026-11-04 |
| D2 | Provisional, a confirmar: los muros quedan abiertos | 2026-11-04 |
| D3 | Provisional, a confirmar: el empuje que deja la frágil en 0 la mueve y después desaparece | 2026-11-04 |
| D4 | Provisional, a confirmar: todos los destinos cubiertos y cada llave sobre un cerrojo | 2026-11-04 |
| D5 | Provisional, a confirmar: solo resbalan las cajas | 2026-11-04 |
| D6 | Provisional, a confirmar: 3 usos consecutivos; un movimiento nuevo reinicia el contador | 2026-11-04 |
| D7 | | |
| D8 | Provisional, a confirmar: deshacer no descuenta movimientos ni empujes | 2026-11-04 |
| Extra #1 | | |
| Extra #2 | | |

---

## Iteración 1 — Núcleo jugable

| Tarea | Patrón / principio | Estado |
|---|---|---|
| `Position`, `Direction` | evita Primitive obsession | ❓ |
| `Terrain` + `Floor`, `Wall`, `Goal` | GRASP Polimorfismo | ❓ |
| `Box` abstracta + `NormalBox` | Template Method (esqueleto) | ❓ |
| `Board`, `Cell`, `Player` | GRASP Experto | ❓ |
| `LevelBuilder` + `LevelParser` (Director) + `LevelFormatException` | Builder | ❓ |
| `LevelRepository` (lista ordenada de niveles en `resources/levels/`) | Fabricación pura | ❓ |
| `Game` con `move(direction)`, contadores y chequeo de victoria | Facade (si aplica) | ❓ |
| `GameEvent` / `GameListener` y notificación desde `Game` | Observer | ❓ |
| `GameController` + Key Bindings de flechas | GRASP Controller | ❓ |
| `GameFrame`, `BoardPanel` (sprites), `ImageRepository` | MVC · Singleton | ❓ |
| Paso al siguiente nivel al ganar | | ❓ |
| 2–3 niveles clásicos de prueba | | ❓ |

## Iteración 2 — Mecánicas del enunciado

| Tarea | Patrón / principio | Estado |
|---|---|---|
| `Ice` + deslizamiento de cajas | Polimorfismo de Terrain | ❓ |
| `FragileBox` (resistencia, rotura) | Template Method (hook) | ❓ |
| `KeyBox`, `Lock`, `Door` (abiertos/cerrados) y vínculo por letra | Observer (Lock → Doors) · State opcional | ❓ |
| `isInPlace()` polimórfico y victoria con tipos de caja | Polimorfismo · LSP | ❓ |
| `BoardMemento` + `MoveHistory` (15 / de a 5 / 3 usos) | Memento | ❓ |
| `Board.copy()` y reinicio desde el prototipo del nivel | Prototype | ❓ |
| `Command`, `MoveCommand`, `UndoCommand`, `RestartCommand` | Command | ❓ |
| `GameState`: Playing / LevelCompleted / GameFinished | State | ❓ |
| Niveles de prueba para cada mecánica | | ❓ |

## Iteración 3 — HUD, puntaje, sonido y contenido

| Tarea | Patrón / principio | Estado |
|---|---|---|
| `HudPanel`: nivel, movimientos, empujes, undos restantes + botones Deshacer y Reiniciar | MVC · Observer | ❓ |
| Puntaje (fórmula D7) | Strategy o Decorator | ❓ |
| `SummaryDialog` de fin de nivel (movs, empujes, undos, puntaje) | | ❓ |
| `SoundPlayer` + `ClipSoundPlayer` | Adapter | ❓ |
| `SoundManager` suscripto a eventos | Observer · Singleton | ❓ |
| Assets de dominio público (imágenes + .wav) + `CREDITS.md` con fuentes/licencias | | ❓ |
| 5+ niveles finales con curva de dificultad que muestren todas las mecánicas | | ❓ |
| Pantalla de fin de juego | State | ❓ |

## Iteración 4 — Funcionalidades adicionales

| Tarea | Patrón | Estado |
|---|---|---|
| Extra #1: _(completar)_ | | ❌ |
| Extra #2: _(completar)_ | | ❌ |

## Iteración 5 — Entregables y ensayo

| Tarea | Estado |
|---|---|
| Revisión final de code smells y nombres de patrones | ❌ |
| `patrones-aplicados.md` completo | ❌ |
| Diagrama de clases en **StarUML** (exportar también a PNG/PDF) | ❌ |
| Informe técnico (estructura en `entrega-y-defensa.md`), incluye **declaración de uso de IA** | ❌ |
| README: descripción, requisitos (JDK), cómo ejecutar, controles, formato de niveles, créditos | ❌ |
| Video gameplay mostrando **todas** las funcionalidades | ❌ |
| Repo público y link verificado en ventana de incógnito | ❌ |
| Ensayo de la demo cronometrado (15–20 min), con reparto de quién explica qué | ❌ |
| Mail de entrega preparado (ver `entrega-y-defensa.md`) | ❌ |
