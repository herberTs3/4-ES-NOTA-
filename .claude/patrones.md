# Patrones para el Sokoban — dónde, por qué y con qué roles

Criterio de evaluación del TI: **correcta aplicación** de SOLID, GRASP, MVC y GoF. Pesa más un patrón bien
justificado que diez forzados. Para cada patrón, el equipo tiene que poder decir:
**Problema** (qué pasaría sin él) → **Solución** (roles y clases) → **Consecuencias** (ventajas/desventajas).

Patrones GoF dados en la cursada (solo usar estos, salvo que el equipo decida otra cosa):
- Creacionales: Singleton, Factory Method, Abstract Factory, Builder, Prototype.
- Estructurales: Adapter, Decorator, Bridge, Composite, Facade.
- Comportamiento: Observer, Strategy, Command, Memento, State, Mediator, Template Method.

---

## 1. Núcleo recomendado (mapeo requerimiento → patrón)

| Requerimiento | Patrón | Roles en el juego | Justificación para la defensa |
|---|---|---|---|
| Arquitectura GUI | **MVC** | Model: `Game`, `Board`…; View: `GameFrame`, `BoardPanel`, `HudPanel`; Controller: `GameController` | Separación de responsabilidades; la lógica no está en la vista (buena práctica de la Clase 6). |
| Deshacer 15 movs, de a 5 | **Memento** | Originator: `Board` (o `Game`); Memento: `BoardMemento` (inmutable); Caretaker: `MoveHistory` | Hielo, frágiles que se rompen y muros que se abren hacen que "invertir" un movimiento sea complejo; guardar el estado es simple y correcto. El Caretaker nunca mira adentro del memento → no se rompe el encapsulamiento. |
| Copiar el tablero para el memento y para reiniciar | **Prototype** | `Board.copy()` / `Box.copy()` (clonación profunda); prototipo inicial del nivel | Reiniciar = clonar el tablero inicial, sin releer el .txt. Cada `Box` sabe clonarse (frágil copia su resistencia) sin que el cliente conozca la clase concreta. |
| Acciones del usuario (flechas, undo, reiniciar) | **Command** | Command: `Command.execute()`; Concretos: `MoveCommand(dir)`, `UndoCommand`, `RestartCommand`; Invoker: `GameController`/key bindings; Receiver: `Game` | Desacopla Swing del modelo (los botones no llaman directo a la lógica). Base para la funcionalidad adicional de *replay*. **Ojo: Memento ≠ Command** (Clase 13): Command encapsula la acción, Memento el estado. No decir que el undo es Command si se hace con Memento. |
| Comportamiento de cada tipo de caja | **Template Method** (+ GRASP Polimorfismo) | Clase base abstracta `Box` con `push()` (método plantilla, `final`) y hooks `afterPush()`, `isInPlace()`; subclases `NormalBox`, `FragileBox`, `KeyBox` | Las tres cajas comparten "las mismas reglas de movimiento" (lo dice el enunciado) y varían en un paso. Evita *Duplicate code* y *Switch statements* por tipo. |
| Terrenos (piso, pared, destino, hielo, cerrojo, muro) | GRASP **Polimorfismo** / **Variaciones protegidas** | `Terrain` con `isWalkable()`, `isSlippery()`, `onBoxEntered()` | Agregar un terreno nuevo no toca `Board` ni `Player` (OCP). |
| Muros abiertos/cerrados | **State** (opcional, ver nota) | Context: `Door`; State: `DoorState`; Concretos: `OpenDoorState`, `ClosedDoorState` | Con solo dos estados un `boolean` alcanza; usar State solo si el muro tiene comportamiento distinto además de "transitable" (sprite, sonido, animación). Si no, es *Speculative generality*. |
| Flujo del juego (jugando / nivel completado / juego terminado) | **State** | Context: `Game`; State: `GameState`; Concretos: `PlayingState`, `LevelCompletedState`, `GameFinishedState` | Las flechas, el undo y el reiniciar se comportan distinto según el estado (en "nivel completado" no se mueve el jugador; Enter pasa de nivel). Evita `if (state == …)` por todos lados. |
| Cerrojo abre "sus" muros | **Observer** | Publisher: `Lock`; Subscribers: los `Door` con su misma letra | El cerrojo no necesita conocer la clase concreta de lo que abre; se pueden enganchar muros (o efectos) sin modificarlo. |
| HUD, repintado y sonidos reaccionan a lo que pasa | **Observer** | Publisher: `Game` (`addListener/removeListener/notify`); Subscribers: `BoardPanel`, `HudPanel`, `SoundManager` | El modelo no conoce la vista (MVC). Agregar sonido no modifica `Game` (OCP). |
| Carga de niveles desde .txt | **Builder** (con Director) | Builder: `LevelBuilder` (`addWall`, `addBox`, `setPlayer`, `linkLock`…, `build()` valida); Director: `LevelParser` | Construcción paso a paso de un objeto complejo con validación al final; el parser no conoce cómo se arma internamente el `Level`. Permite otro Director (p. ej. un editor de niveles) con el mismo Builder. |
| Reproducir sonido con `javax.sound.sampled` | **Adapter** | Target: `SoundPlayer` (interfaz propia `play(SoundId)`); Adaptee: `Clip`/`AudioSystem`; Adapter: `ClipSoundPlayer` | La API de Java es incómoda e incompatible con lo que el juego espera; el resto del código no depende de ella (DIP). Permite un `SilentSoundPlayer` para mutear. |
| Imágenes y sonidos cargados una sola vez | **Singleton** | `ImageRepository.getInstance()`, `SoundManager.getInstance()` | Recurso costoso y único (como la "conexión" del ejemplo de la Clase 8). No convertir controllers ni el modelo en Singleton sin necesidad. |
| Puntaje "definido por el equipo" | **Strategy** o **Decorator** | Strategy: `ScoringStrategy.calculate(LevelStats)`; o Decorator: `Score` base + `NoUndoBonus`, `ParBonus`, `UndoPenalty` | Strategy si hay criterios alternativos intercambiables (p. ej. modos de dificultad). Decorator si el puntaje se arma apilando bonus/penalidades combinables. Elegir **uno** según la funcionalidad adicional. |
| Punto de entrada simple al modelo | **Facade** (opcional) | `Game` ofrece `move(dir)`, `undo()`, `restart()`, `nextLevel()` y coordina Board, MoveHistory, LevelRepository, Scoring | El controller no conoce los subsistemas internos (Ley de Demeter). Solo nombrarlo Facade si realmente oculta varios subsistemas. |

## 2. Principios a señalar en el código (para el informe y la defensa)

- **SRP**: `LevelParser` solo lee, `LevelBuilder` solo arma, `MoveHistory` solo guarda historial, `BoardPanel` solo dibuja.
- **OCP**: nuevos tipos de caja/terreno/sonido/puntaje sin modificar `Board`/`Game`.
- **LSP**: cualquier `Box` se empuja igual desde `Player`; ninguna subclase rompe el contrato (p. ej. `KeyBox` no lanza excepciones raras en `push`).
- **ISP**: interfaces chicas (`GameListener`, `SoundPlayer`, `Command`).
- **DIP**: `Game` depende de `SoundPlayer`/`GameListener`/`ScoringStrategy` (abstracciones), no de clases concretas.
- **GRASP**: Experto (`Board` sabe si una celda es transitable), Creador (`LevelBuilder` crea las entidades porque tiene los datos de inicialización), Controlador (`GameController`), Bajo acoplamiento / Alta cohesión, Polimorfismo (cajas y terrenos), Fabricación pura (`MoveHistory`, `LevelParser`, `SoundManager`), Indirección (Observer/Adapter), Variaciones protegidas (interfaces alrededor de audio y puntaje).

## 3. Funcionalidades adicionales (hay que elegir 2) — candidatas

Cada una debe **implicar** un patrón nuevo o un uso nuevo y visible en la demo.

| Funcionalidad | Patrón | Esfuerzo | Comentario |
|---|---|---|---|
| **Temas visuales** intercambiables en el menú (clásico / pixel / nocturno) | **Abstract Factory** | Bajo-medio | `ThemeFactory` crea la familia completa de sprites (pared, piso, cajas, jugador) coherente entre sí. Muy fácil de mostrar en vivo. ⭐ |
| **Replay** del nivel resuelto | **Command** | Medio | Se guarda la lista de `MoveCommand` ejecutados y se re-ejecutan con un `javax.swing.Timer`. Reaprovecha el Command del núcleo. ⭐ |
| **Bonus/penalidades de puntaje combinables** (sin undo, bajo el par de movimientos, rapidez) | **Decorator** | Bajo | Se ve en el resumen de fin de nivel desglosado. |
| **Modos de dificultad** (fácil: undo ilimitado; difícil: sin undo / puntaje más exigente) | **Strategy** | Bajo | Cuidar que realmente cambie comportamiento, no solo un número. |
| **Packs/mundos de niveles** con puntaje acumulado | **Composite** | Medio | `LevelPack` contiene `Level` o sub-packs; `getTotalScore()` uniforme (como el ejemplo de Orden/Paquete de la Clase 11). |
| **Logros** ("sin undo", "rompiste 3 cajas frágiles") | **Observer** | Bajo | Nuevo suscriptor `AchievementTracker`, sin tocar `Game`. Puede quedar flojo si Observer ya está usado. |
| **Guardar/continuar partida** | **Memento** + serialización | Medio | Reutiliza Memento; menos "nuevo" para mostrar. |
| **Editor de niveles** | **Builder** (otro Director) | Alto | Muy vistoso pero caro para el tiempo disponible. |

Recomendación por tiempo/impacto en la demo: **Temas visuales (Abstract Factory)** + **Replay (Command)** o **Bonus de puntaje (Decorator)**.
La decisión es del equipo → registrarla en `PLAN_DE_ACCION.md`.

## 4. Errores a evitar (los marca la cátedra)

- **Patrón forzado / Speculative generality**: interfaces con una sola implementación "por si acaso", State para un boolean, Mediator sin componentes que se hablen.
- **Mal nombre**: un `switch` de char → objeto es una *simple factory*, no Factory Method (Factory Method requiere un creador con método de fábrica que las subclases redefinen).
- **Singleton por comodidad**: usarlo como variable global para todo aumenta el acoplamiento.
- **Lógica en la vista** (violación de MVC): `BoardPanel` decidiendo si una caja se puede mover.
- **Large class / God object**: `Game` con 800 líneas; delegar en `Board`, `MoveHistory`, scoring, estados.
- **Switch statements por tipo** de caja/terreno → reemplazar por polimorfismo.
- **Message chains**: `game.getBoard().getCell(p).getTerrain().isWalkable()` desde la vista o el controller.
- **Memento que expone el estado** con getters públicos: solo el Originator debe poder leerlo.
