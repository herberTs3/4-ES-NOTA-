# Entrega, demo y defensa

## Presentación — miércoles 18/11/2026

Comunicado del profesor:
- Cada grupo hace una **DEMO en vivo**: mostrar las funcionalidades implementadas y **explicar en detalle los patrones de diseño aplicados**.
- Duración estimada: **15 a 20 minutos**.
- **No hace falta presentar diagramas ni mostrar código** en la exposición (pero el diagrama y el código se entregan igual).
- Ese mismo día se **entrega formalmente por mail**.
- Además, según el enunciado, **cada integrante defiende el proyecto** (oral y/o escrito) y eso define la nota. La falta de comprensión baja mucho la calificación.

## Mail de entrega

- Para: **jpepe@uade.edu.ar** (desde el correo **institucional**).
- Lo envía **un solo integrante**, con **copia (CC) al resto** del grupo.
- Indicar **nombre del equipo** y **apellido y nombre** de cada desarrollador/a.
- Adjuntar **todos los entregables** + **link al repositorio público**.

Plantilla:

```
Asunto: PDS – Trabajo Integrador Sokoban – Equipo "4 es nota"

Profesor Pepe:

Le enviamos la entrega del Trabajo Integrador (Sokoban) del equipo "4 es nota".

Integrantes:
- Apellido, Nombre
- Apellido, Nombre
- Apellido, Nombre

Repositorio público: https://github.com/<usuario>/<repo>
Video gameplay: <link o adjunto>

Adjuntos:
- Informe técnico (PDF)
- Diagrama de clases (StarUML .mdj + exportación PNG/PDF)

Saludos,
<Nombre>
```

## Checklist de entregables

- [ ] Repo **público** en GitHub (probar el link en incógnito).
- [ ] **README**: descripción, requisitos (versión de JDK), cómo ejecutar (IDE y línea de comandos), controles, formato de niveles, créditos de assets.
- [ ] **Video gameplay** que muestre **todas** las funcionalidades (ver guion abajo).
- [ ] **Diagrama de clases en StarUML** (`.mdj` + PNG/PDF legible). Los patrones tienen que reconocerse en el diagrama.
      Tip: StarUML tiene extensión de Java para ingeniería inversa del código (verificar que esté instalada); después ordenar a mano y quitar ruido.
- [ ] **Informe técnico** con decisiones de diseño y su justificación.
- [ ] `CREDITS.md` con fuente y licencia de cada asset (dominio público / CC0).

## Estructura sugerida del informe técnico

1. Portada: materia, profesor, equipo, integrantes, fecha.
2. Introducción y alcance (qué se implementó, funcionalidades adicionales).
3. Arquitectura: MVC, paquetes, flujo de un movimiento (vista → controller → modelo → notificación).
4. **Patrones aplicados** (uno por sección, desde `patrones-aplicados.md`): problema → solución con roles → consecuencias → alternativa descartada.
5. Principios SOLID y GRASP con ejemplos concretos del código.
6. Decisiones de diseño sobre ambigüedades del enunciado (D1–D8) y criterio de **puntaje**.
7. Formato de los niveles .txt.
8. Funcionalidades adicionales y su patrón.
9. Limitaciones y mejoras futuras (deuda técnica consciente, si la hay).
10. **Declaración de uso de herramientas de IA**: qué herramienta, para qué tareas (p. ej. propuesta de estructura, generación asistida de código, revisión), y cómo el equipo validó y adaptó lo generado.
11. Créditos de assets.

## Guion de la demo (15–20 min)

Repartir: **cada integrante explica al menos dos patrones** (todos tienen que poder explicar cualquiera).

| Min | Qué se muestra | Patrones que se explican |
|---|---|---|
| 0–2 | Presentación del equipo, arquitectura en una frase | MVC |
| 2–5 | Nivel 1: movimiento, empuje, bloqueo con dos cajas, HUD, victoria y resumen | Command (flechas), Observer (HUD/sonido), State (nivel completado) |
| 5–8 | Nivel con hielo y caja frágil hasta romperla | Template Method (cajas), Polimorfismo (terrenos) |
| 8–10 | Nivel con caja llave, cerrojo y muro | Observer (cerrojo → muros) |
| 10–12 | Deshacer (incluye volver a tener la caja frágil sana) y reiniciar | Memento (Originator/Memento/Caretaker), Prototype |
| 12–15 | Funcionalidad adicional #1 y #2 | (los elegidos) |
| 15–17 | Carga de niveles desde .txt (abrir un .txt y mostrar el nivel) y sonidos | Builder + Director, Adapter, Singleton |
| 17–20 | Preguntas | |

Preparar **niveles cortos específicos para la demo** (uno por mecánica) para no perder tiempo resolviendo puzzles en vivo.
Tener el `.jar`/proyecto compilado y probado en la máquina que se va a usar; plan B: el video.

## Preguntas probables en la defensa

- ¿Por qué Memento y no Command con `undo()` para deshacer? (hielo/frágiles/muros hacen difícil invertir; estado completo es simple y seguro).
- ¿Quién es el Originator, el Memento y el Caretaker? ¿Por qué el Caretaker no puede leer el memento?
- ¿Qué pasa si agregamos un nuevo tipo de caja/terreno? ¿Qué clases se tocan? (OCP).
- ¿Dónde está la lógica de "no se pueden empujar dos cajas"? ¿Por qué ahí? (Experto).
- ¿Por qué el modelo no conoce a la vista? ¿Cómo se entera la vista de los cambios? (MVC + Observer).
- ¿Qué diferencia hay entre Strategy y State? (Strategy: el cliente elige el algoritmo; State: el objeto cambia de comportamiento según su estado interno y las transiciones las definen los estados).
- ¿Qué diferencia hay entre Builder y Factory Method / Abstract Factory?
- ¿Por qué tal clase es Singleton? ¿Qué desventajas tiene? (acceso global, acoplamiento, testeo).
- ¿Qué code smells encontraron y cómo los refactorizaron?
- ¿Cómo se calcula el puntaje y por qué?
- ¿Qué parte se hizo con IA y cómo la validaron?
