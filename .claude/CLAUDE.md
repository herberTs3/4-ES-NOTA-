# Sokoban — Trabajo Integrador · Proceso de Desarrollo de Software (UADE 3.4.210)

Profesor: Jonathan Pepe (jpepe@uade.edu.ar) · Equipo: **"4 es nota"**
Enunciado completo: `.claude/PDS - TI - Prof Pepe.pdf` (transcripto en `requirements.md`).

## Qué es este proyecto

Versión del Sokoban en **Java + Swing (2D)**. El objetivo académico NO es solo que el juego funcione:
se evalúa la **correcta aplicación de SOLID, GRASP, MVC y patrones GoF vistos en la cursada**, además de
creatividad, calidad de código, documentación y diseño general. Cada integrante defiende el proyecto
oralmente: **todo lo que generes tiene que poder ser explicado por el equipo**.

## Fechas clave

- **Miércoles 18/11/2026** — DEMO en vivo (15–20 min) explicando funcionalidades y **patrones aplicados**
  + entrega formal por mail. Ver `entrega-y-defensa.md`.

## Reglas que siempre aplican (resumen — el detalle está en los archivos importados)

1. **MVC estricto**: el modelo no importa `javax.swing` ni `java.awt`. La vista no tiene lógica de juego.
2. **Cada patrón tiene que resolver un problema real del juego.** Nada de patrones "de adorno":
   la cátedra enseña *Speculative generality* y *YAGNI* como code smells. Si un patrón no se puede
   justificar con el formato **Problema → Solución → Consecuencias**, no va.
3. **Nombrar bien los patrones**: no llamar "Factory Method" a un `switch` que crea objetos (eso es una
   simple factory, no GoF). En la defensa el profe pregunta por los roles (Originator, Caretaker, Invoker…).
4. **Workflow por ítems con aprobación** (`workflow.md`): plan → aprobación → un ítem por vez → commit sugerido.
5. Después de implementar un patrón, **registrarlo en `patrones-aplicados.md`** (alimenta el informe técnico y la demo).
6. Código en inglés, textos de UI en español, sin comentarios en el código (`code-style.md`).
7. No hacer `git push`. Sugerir commits; el equipo decide cuándo commitear.
8. Si el repo ya tiene una estructura/decisión distinta a la que proponen estos documentos,
   **el repo manda**: avisar la diferencia y proponer actualizar el documento.
9. El uso de IA está permitido **siempre que se declare en el informe técnico**.

## Documentos (cargados siempre)

@requirements.md
@architecture.md
@patrones.md
@code-style.md
@workflow.md

## Documentos de consulta (leerlos cuando haga falta)

- `PLAN_DE_ACCION.md` — iteraciones, estado de cada tarea y orden de implementación. **Leerlo al empezar cada sesión.**
- `patrones-aplicados.md` — registro vivo de patrones implementados (actualizar al cerrar cada ítem con patrón).
- `teoria-catedra.md` — resumen de TODAS las clases: POO, UML, code smells, SOLID, GRASP, MVC y los 17 patrones GoF
  dados, con el vocabulario y los roles tal como los presenta la cátedra. Usarlo para justificar decisiones
  y para preparar la defensa.
- `entrega-y-defensa.md` — entregables, mail, guion de la demo y preguntas probables de defensa.

## Comandos útiles

```bash
# Compilar (sin Maven; ajustar si el repo usa Maven/IntelliJ con otra salida)
javac -d out $(find src -name "*.java") && cp -r resources/* out/ 2>/dev/null
# Ejecutar
java -cp out Main
```
