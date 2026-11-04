# Estilo de código

## Idioma

- **Código** (clases, métodos, variables, paquetes, enums): **inglés**.
- **Textos visibles** (labels, botones, HUD, mensajes, diálogos): **español**.
- Documentos del repo (README, informe, `.claude/`): español.

## Nombres

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clase | PascalCase | `MoveHistory` |
| Método | camelCase | `canPushTo` |
| Variable | camelCase | `currentLevel` |
| Constante | UPPER_SNAKE_CASE | `MAX_UNDO_USES` |
| Paquete | minúsculas | `model.terrain` |
| Enum / valor | PascalCase / UPPER_SNAKE_CASE | `Direction.UP` |
| Frame / Panel / Dialog | `XxxFrame`, `XxxPanel`, `XxxDialog` | `SummaryDialog` |

**Los roles del patrón en el nombre** cuando ayude a reconocerlo en la defensa:
`BoardMemento`, `MoveCommand`, `LevelBuilder`, `ScoringStrategy`, `ClipSoundPlayer` (adapter),
`ClassicThemeFactory`, `PlayingState`, `NoUndoBonusDecorator`.

## Comentarios

- **Sin comentarios en el código.** Los nombres y la estructura tienen que explicar la intención
  (la Clase 3 trata los comentarios explicativos como *code smell*).
- La explicación de patrones va en `patrones-aplicados.md` y en el informe técnico, no en el código.

## Formato

- 4 espacios, sin tabs. Llave de apertura en la misma línea.
- Una línea en blanco entre métodos.
- Imports: `java.*`, línea en blanco, `javax.*`, línea en blanco, imports del proyecto. Sin `import x.*;` comodín.
- Máximo 120 caracteres por línea. Una clase pública por archivo.

## Diseño

- Atributos siempre `private` (o `protected` solo cuando una subclase del Template Method lo necesite).
- Constantes en vez de números mágicos (`UNDO_STEP = 5`, `MAX_HISTORY = 15`, `MAX_UNDO_USES = 3`, `TILE_SIZE = 48`).
- `Position`/`Direction` en lugar de pares `int x, int y` (evitar *Primitive obsession*).
- Métodos cortos (< ~25 líneas). Si un método crece, extraer (*Long method*).
- Preferir `for`/`for-each`; Streams solo si acortan sin complicar.
- Colecciones simples: `List`/`ArrayList`, `Map`/`HashMap`, `Deque`/`ArrayDeque` para el historial.
- Inyectar dependencias por constructor (DIP), en especial `SoundPlayer`, `ScoringStrategy`, listeners.

## Errores

- Excepciones propias para reglas de negocio/formato: `LevelFormatException`, `LevelNotFoundException`.
- Un movimiento inválido **no** es excepción: es un resultado normal (evento `BLOCKED`).
- El modelo lanza; el controller/vista atrapa y muestra con `JOptionPane` en español.
- Nunca `catch (Exception e) {}` vacío ni `e.printStackTrace()` como manejo.

## Swing

- `BorderLayout`, `BoxLayout`, `FlowLayout`, `GridBagLayout`. Sin `null` layout.
- Key Bindings para las flechas; botones con `setFocusable(false)`.
- Nada de lógica de juego en listeners de Swing: delegan al controller.
- Sin `System.out.println` de depuración en el código entregado.
