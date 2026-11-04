# Arquitectura — MVC + patrones GoF

> Propuesta base. Si el repo ya tiene otra estructura, se respeta la existente y se actualiza este archivo.

## Capas (MVC, como lo da la cátedra en la Clase 6)

```
┌──────────────────────────────────────────────┐
│ VIEW (Swing: JFrame, JPanel, botones, HUD)   │  muestra datos, captura eventos
└──────────────┬───────────────▲───────────────┘
   acción del  │               │ notificación (Observer) con datos/DTO
   usuario     ▼               │
┌──────────────────────────────┴───────────────┐
│ CONTROLLER (GameController)                  │  traduce eventos a comandos sobre el modelo
└──────────────┬───────────────────────────────┘
               ▼
┌──────────────────────────────────────────────┐
│ MODEL (Game, Board, Player, Box, Terrain…)   │  TODAS las reglas del juego
└──────────────────────────────────────────────┘
```

### Reglas por capa

**Model**
- Contiene toda la lógica: movimiento, empuje, deslizamiento, resistencia, cerrojos, victoria, puntaje, historial.
- **Prohibido** importar `javax.swing.*` o `java.awt.*` (el hook lo controla). Puede usar `java.io`/`java.nio` solo en la carga de niveles.
- Notifica cambios a través de una interfaz propia de Observer (`GameListener`/`GameEvent`), no conoce a la vista.
- Tell, don't ask: las entidades tienen comportamiento (evitar *Data class*).

**Controller**
- Recibe eventos de la vista (flechas, botones) y los convierte en llamadas/comandos al modelo (GRASP Controller).
- No dibuja nada y no contiene reglas del juego.

**View**
- Solo Swing: dibujar el tablero, HUD, botones, diálogo de resumen.
- No decide nada del juego. Pide/recibe datos (idealmente un DTO de solo lectura tipo `BoardSnapshot`/`HudData`).
- Textos visibles en español.

## Estructura de paquetes propuesta

```
src/
├── Main.java                         arranca Swing en el EDT (SwingUtilities.invokeLater)
├── model/
│   ├── Game.java                     punto de entrada del modelo (fachada del dominio)
│   ├── board/                        Board, Cell, Position, Direction
│   ├── entity/                       Player, Box (abstracta), NormalBox, FragileBox, KeyBox
│   ├── terrain/                      Terrain (abstracta/interfaz), Floor, Wall, Goal, Ice, Lock, Door (+ estados)
│   ├── level/                        Level, LevelBuilder, LevelParser (Director), LevelRepository
│   ├── history/                      BoardMemento, MoveHistory (Caretaker)
│   ├── score/                        ScoringStrategy / decoradores de puntaje, LevelSummary
│   ├── state/                        GameState, PlayingState, LevelCompletedState, GameFinishedState
│   └── event/                        GameEvent, GameEventType, GameListener
├── controller/
│   ├── GameController.java
│   └── command/                      Command, MoveCommand, UndoCommand, RestartCommand
├── view/
│   ├── GameFrame.java
│   ├── BoardPanel.java               pinta el tablero con sprites
│   ├── HudPanel.java                 nivel, movimientos, empujes, botones
│   ├── SummaryDialog.java            resumen de fin de nivel
│   └── assets/                       ImageRepository (Singleton), tema visual
├── audio/                            SoundPlayer (interfaz), ClipSoundPlayer (Adapter), SoundManager (Observer)
└── exception/                        LevelFormatException, …
resources/
├── levels/                           level01.txt, level02.txt, …
├── images/
└── sounds/                           .wav
```

Recursos: cargar con `getClass().getResourceAsStream("/levels/level01.txt")` (marcar `resources/` como
*Resources Root* en IntelliJ). Así el juego corre igual desde el IDE y desde un `.jar`.

## Modelo de dominio (guía)

| Concepto | Responsabilidad (GRASP) |
|---|---|
| `Position` (record x,y) y `Direction` (enum con dx,dy) | Evitan *Primitive obsession* / *Data clumps* (`int x, int y` por todos lados). `position.next(direction)`. |
| `Board` | **Experto** en la grilla: qué hay en cada celda, si una posición es transitable, mover entidades. Sabe copiarse (snapshot). |
| `Cell` | Terreno + ocupante (caja o nada). |
| `Terrain` (polimorfismo) | `isWalkable()`, `isSlippery()`, `onBoxEntered(box)`. `Wall` nunca transitable; `Ice` resbala; `Lock` avisa a sus muros; `Door` depende de su estado abierto/cerrado. |
| `Box` (abstracta, **Template Method**) | `push(direction, board)` define el algoritmo fijo: validar destino → mover → deslizar si el terreno es resbaladizo → `afterPush()` (hook) → notificar. `FragileBox` reduce resistencia en el hook; `KeyBox` activa el cerrojo. `isInPlace(cell)` polimórfico para la victoria. |
| `Player` | Posición y orientación (para el sprite). Intenta moverse; si hay caja, le pide a la caja que se empuje. |
| `Game` | Orquesta un nivel: contadores, historial, estado, eventos. Es lo único que el controller conoce del modelo. |
| `MoveHistory` | **Caretaker**: guarda hasta 15 mementos; `undo()` retrocede 5; cuenta usos consecutivos. |
| `LevelParser` + `LevelBuilder` | **Director + Builder**: lee el .txt y construye el `Level` paso a paso, validando al final. |

### Reglas de movimiento (algoritmo)

1. `target = player.position.next(dir)`.
2. Si `target` no es transitable (pared / muro cerrado) → evento `BLOCKED`, no cuenta movimiento.
3. Si en `target` hay una caja: `boxTarget = target.next(dir)`; si `boxTarget` no es transitable **o** tiene otra caja → bloqueado (nunca dos cajas a la vez).
4. Antes de cambiar nada: guardar memento en el historial.
5. Empujar la caja (Template Method): mover; mientras esté en hielo y la celda siguiente esté libre, seguir deslizando.
6. Mover al jugador; `moves++`, y si hubo caja `pushes++`.
7. Hook de la caja (frágil/llave), chequear victoria, emitir eventos (`MOVE`, `PUSH`, `BOX_ON_GOAL`, `BOX_BROKEN`, `DOOR_OPENED`, `LEVEL_COMPLETED`).

## Formato de los niveles (.txt)

Base estándar de Sokoban + extensiones del TP. Un archivo por nivel. Las líneas pueden tener distinto largo
(se rellena con vacío).

| Char | Elemento |
|---|---|
| `#` | Pared |
| ` ` (espacio) | Piso |
| `.` | Destino |
| `@` / `+` | Jugador / jugador sobre destino |
| `$` / `*` | Caja normal / caja normal sobre destino |
| `F` | Caja frágil (resistencia por defecto: constante `FragileBox.DEFAULT_RESISTANCE`) |
| `K` | Caja llave |
| `~` | Hielo (terreno resbaladizo) |
| `a`–`e` | Cerrojo (abre los muros con la misma letra en mayúscula) — decisión D1 |
| `A`–`E` | Muro cerrado vinculado al cerrojo de su letra |

Validaciones del Builder (`LevelFormatException` con mensaje claro): exactamente un jugador, al menos una
caja que vaya a destino, `#cajas a destino ≥ #destinos`, caracteres desconocidos, cerrojos sin muro o muros sin cerrojo.

## Swing: cosas que evitan bugs clásicos

- Arrancar en el EDT: `SwingUtilities.invokeLater(() -> new GameFrame(...))`.
- **Flechas con Key Bindings** (`InputMap`/`ActionMap` con `WHEN_IN_FOCUSED_WINDOW`), no `KeyListener`:
  con `KeyListener`, al clickear "Deshacer" el botón se queda el foco y las flechas dejan de andar.
  Además `button.setFocusable(false)`.
- Dibujar en `paintComponent(Graphics g)` de un `JPanel` (llamar a `super.paintComponent`), y pedir `repaint()`
  cuando llega un evento del modelo. El juego es por turnos: no hace falta game loop ni hilos.
- Imágenes cargadas **una sola vez** (cache en `ImageRepository`) — nunca leer archivos dentro de `paintComponent`.
- Sonidos: `Clip` precargados; reproducir con `clip.setFramePosition(0); clip.start();`.
