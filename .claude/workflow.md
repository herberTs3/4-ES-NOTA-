# Workflow de desarrollo

Cada funcionalidad sigue este ciclo. No saltear pasos.

## Paso 0 — Al empezar cada sesión

1. Leer `PLAN_DE_ACCION.md` para saber en qué iteración/ítem estamos.
2. `git status` y `git log --oneline -10` para ver el estado real.
3. Resumir en 3–4 líneas dónde quedamos y qué sigue. Esperar indicación.

## Paso 1 — Recibir el requerimiento

Ejemplo: "Implementá las cajas frágiles".

## Paso 2 — Plan

Presentar un plan numerado de ítems chicos, cada uno implementable y verificable solo.
Si el ítem involucra un patrón, indicar **cuál y con qué roles** desde el plan.

```
PLAN: Cajas frágiles

[ ] 1. FragileBox extends Box con resistencia (constante DEFAULT_RESISTANCE = 3)
[ ] 2. Hook afterPush() que reduce resistencia y emite BOX_BROKEN en 0 (Template Method: paso variable)
[ ] 3. Board.removeBox() y efecto en la condición de victoria (D3/D4)
[ ] 4. Carácter 'F' en LevelParser/LevelBuilder
[ ] 5. Sprite de caja frágil (y agrietada) en BoardPanel
[ ] 6. Sonido de rotura en SoundManager
[ ] 7. Nivel de prueba resources/levels/test_fragile.txt + prueba manual
```

**Parar acá** y esperar aprobación.

## Paso 3 — Ítem por ítem

Para cada ítem aprobado:

1. Implementar **solo ese ítem**.
2. Compilar (`javac`) y, si aplica, correr para probar.
3. Mostrar los archivos cambiados y explicar en 1–3 líneas qué se hizo.
4. Si el ítem aplica un patrón o principio: explicar en 2–3 líneas **qué problema resuelve y qué rol cumple
   cada clase**, en lenguaje que el equipo pueda repetir en la defensa.
5. **Parar** y esperar feedback. Con "ok" / "dale" / "siguiente": marcar `[x]` y pasar al próximo.

No agrupar varios ítems en una respuesta salvo pedido explícito.

## Paso 4 — Commits

Al aprobarse cada ítem, sugerir un mensaje en formato convencional:

```
feat(boxes): add FragileBox with resistance hook
feat(undo): implement BoardMemento and MoveHistory caretaker
fix(movement): prevent pushing two boxes in a row
refactor(board): extract slide logic to Ice terrain
docs(readme): add run instructions
```

Tipos: `feat`, `fix`, `refactor`, `style`, `docs`, `chore`, `test`. Scope: módulo en minúscula
(`board`, `boxes`, `terrain`, `levels`, `undo`, `hud`, `sound`, `score`, `ui`, `theme`, …).

El equipo decide cuándo commitear. **Nunca `git push`.**

## Paso 5 — Cierre de la funcionalidad

1. Resumen: archivos creados/modificados, patrones/principios aplicados, pendientes.
2. Actualizar estados en `requirements.md` y `PLAN_DE_ACCION.md`.
3. Si hubo patrón: agregar/actualizar la entrada en `patrones-aplicados.md`.
4. Esperar el próximo requerimiento.

## Reglas duras

1. Nunca generar código sin plan aprobado.
2. Nunca saltear ítems del plan.
3. Nunca agregar librerías externas ni frameworks (solo JDK: Swing, `javax.sound.sampled`, `javax.imageio`).
4. Nunca romper las reglas de `architecture.md` y `code-style.md`. Si un requerimiento las contradice, avisar y proponer alternativa.
5. Nunca forzar un patrón para "sumar puntos": si no hay problema real que resolver, decirlo.
6. Si una regla del enunciado es ambigua, revisar las decisiones D1–D8 de `requirements.md`; si no está cubierta, preguntar.
