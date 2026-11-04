# Registro de patrones aplicados

> Registro vivo. Se completa al cerrar cada ítem que aplica un patrón o principio.
> Es la base de la sección de patrones del **informe técnico** y del guion de la **demo**.
> Una entrada por patrón (si un patrón se usa en dos lugares, dos filas en "Dónde").

## Resumen

| Patrón | Categoría | Clases | Funcionalidad que lo muestra en la demo | Estado |
|---|---|---|---|---|
| MVC | Arquitectura | | | ❌ |

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
