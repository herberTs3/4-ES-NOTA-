# Registro de patrones aplicados

> Registro vivo. Se completa al cerrar cada ítem que aplica un patrón o principio.
> Es la base de la sección de patrones del **informe técnico** y del guion de la **demo**.
> Una entrada por patrón (si un patrón se usa en dos lugares, dos filas en "Dónde").

## Resumen

| Patrón | Categoría | Clases | Funcionalidad que lo muestra en la demo | Estado |
|---|---|---|---|---|
| MVC | Arquitectura | `Game`, `Tablero` · `Controlador` · `VentanaPrincipal`, `VistaTablero` · DTOs de `sokoban.dto` | Mover al jugador: la vista se actualiza sola | ✅ |
| Observer | Comportamiento | `Game` → `GameListener` → `VentanaPrincipal`; `Lock` → `LockListener` → `Door` | Contadores y tablero al moverse; muros que se abren | ✅ |
| Template Method | Comportamiento | `Caja.empujar()`, `CajaFragil.despuesDeEmpujar()` | Empujar una caja frágil hasta romperla | ✅ |
| Factory Method | Creacional | `CreadorTerreno`, `CreadorCaja` y sus creadores concretos | Abrir un .txt y mostrar el nivel cargado | ✅ |

## MVC con DTOs — Arquitectura

- **Dónde**: `sokoban.model`, `sokoban.controller`, `sokoban.view`, `sokoban.dto`.
- **Problema**: la vista recibía el `Tablero` real, así que tenía acceso a objetos mutables del dominio y dependía de sus clases.
- **Solución / roles**:
  - Modelo → `Game`, `Tablero`, cajas y terrenos: tienen todas las reglas.
  - Controlador → `Controlador`: traduce las flechas y los botones en acciones del modelo.
  - Vista → `VentanaPrincipal`, `VistaTablero`: dibujan una foto de solo lectura.
  - DTO → `GameSnapshot`, `BoardSnapshot`, `TileData`, `BoxData`.
- **Consecuencias**:
  - Ventajas: la vista no puede cambiar el juego por accidente y se puede probar el modelo sin Swing.
  - Desventajas / costo: se arma una foto nueva en cada cambio; en un tablero chico el costo no se nota.
- **Alternativa descartada**: Visitor para que la vista distinga terrenos y cajas. Obligaba a la vista a conocer el modelo y no es uno de los patrones de la cursada.
- **Principios relacionados**: SRP, DIP, Bajo acoplamiento.
- **Cómo mostrarlo en la demo**: mover al jugador y explicar que la vista solo importa DTOs, el Controlador y `GameListener`.

## Observer — Comportamiento

- **Dónde**: `Game`/`GameListener` y `Lock`/`LockListener`/`Door`.
- **Problema**: el modelo tiene que avisar cambios sin conocer la vista, y un cerrojo tiene que abrir sus muros sin depender de cuántos son.
- **Solución / roles**:
  - Publisher → `Game`; Subscriber → `VentanaPrincipal`.
  - Publisher → `Lock`; Subscriber → `Door`.
- **Consecuencias**:
  - Ventajas: se pueden sumar suscriptores, como el sonido, sin modificar `Game`.
  - Desventajas / costo: el flujo es menos directo de seguir.
- **Alternativa descartada**: que el controlador le pase el tablero a la vista después de cada acción.
- **Principios relacionados**: OCP, DIP, Indirección.
- **Cómo mostrarlo en la demo**: en el nivel 3, llevar la caja llave al cerrojo y ver abrirse el muro.

## Template Method — Comportamiento

- **Dónde**: `Caja`, `CajaFragil`.
- **Problema**: las tres cajas comparten las mismas reglas de movimiento y varían en un paso; sin el patrón habría código duplicado.
- **Solución / roles**:
  - Clase abstracta → `Caja`, con el método plantilla `empujar()` (final).
  - Hook → `despuesDeEmpujar()`, que `CajaFragil` redefine.
- **Consecuencias**:
  - Ventajas: el orden mover, deslizar y avisar al terreno queda en un solo lugar.
  - Desventajas / costo: depende de herencia.
- **Alternativa descartada**: un `switch` por tipo de caja en `Tablero`.
- **Principios relacionados**: LSP, OCP, Polimorfismo.
- **Cómo mostrarlo en la demo**: empujar una caja frágil tres veces y ver cómo baja el número hasta romperse.

## Factory Method — Creacional

- **Dónde**: `sokoban.level.factory`.
- **Problema**: el cargador tendría que conocer todas las clases concretas de terrenos y cajas.
- **Solución / roles**:
  - Creator → `CreadorTerreno`, `CreadorCaja`.
  - ConcreteCreator → `CreadorMuro`, `LockCreator`, `DoorCreator`, `CreadorCajaFragil`, etc.
  - Product → `Terreno`, `Caja`.
  - Cliente → `CargarNivelPorTxt`, a través de `RegistroDeCreadores`.
- **Consecuencias**:
  - Ventajas: agregar un tipo no toca el cargador. Una caja nueva tampoco toca los creadores de terreno.
  - Desventajas / costo: una clase chica por tipo.
- **Alternativa descartada**: Builder con Director; ya existía esta solución probada y sumar otra duplicaba responsabilidades.
- **Principios relacionados**: OCP, Creador.
- **Cómo mostrarlo en la demo**: abrir `levels/nivel3.txt` y señalar qué crea cada símbolo.

---

## Plantilla de entrada (copiar por cada patrón)

### <Nombre del patrón> — <Creacional | Estructural | Comportamiento | GRASP | Arquitectura>

- **Dónde**: `paquete.Clase`, `paquete.OtraClase`
- **Problema**: qué pasaba (o pasaría) sin el patrón, en términos del juego. Mencionar el smell/principio violado
  (p. ej. *Switch statements*, OCP).
- **Solución / roles**:
  - <Rol del patrón> → `Clase`
  - <Rol del patrón> → `Clase`
- **Consecuencias**:
  - Ventajas: …
  - Desventajas / costo: …
- **Alternativa descartada**: qué otra opción se consideró y por qué no (p. ej. Command con `undo()` vs Memento).
- **Principios relacionados**: SOLID / GRASP que refuerza.
- **Cómo mostrarlo en la demo**: acción concreta en el juego (p. ej. "apretar Deshacer tras romper una caja frágil: vuelve intacta").
