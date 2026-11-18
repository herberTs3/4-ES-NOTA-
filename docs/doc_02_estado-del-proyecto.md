# Estado del proyecto Sokoban

Equipo "4 es nota" · Proceso de Desarrollo de Software · Prof. Pepe
Revisión del código en la rama `main`, comparado con `docs/doc_01_linea-de-tiempo-equipo.pdf`.

## Resumen

El juego se puede jugar de punta a punta: menú, créditos, selector y 5 niveles, con cajas normales, frágiles y llave, hielo, cerrojos, muros y
deshacer. Falta lo obligatorio que sigue: **puntaje, imágenes, sonido y las 2 funcionalidades adicionales**.
La demo y la entrega son el miércoles 18/11/2026.

## Funcionalidades adicionales

**Hoy hay 0 de las 2 obligatorias.** Ninguna está implementada ni elegida.

Lo que ya funciona (movimiento, cajas, hielo, cerrojos, HUD) lo pide el enunciado, así que no cuenta como adicional.
El contador de cajas ubicadas del HUD es un detalle visual y tampoco alcanza: cada adicional tiene que implicar un
patrón de diseño.

Opciones recomendadas en `.claude/patrones.md`:

| Funcionalidad | Patrón | Roles | Esfuerzo |
|---|---|---|---|
| Temas visuales intercambiables | Abstract Factory | `ThemeFactory` crea la familia completa de imágenes de cada tema | Bajo a medio |
| Replay del nivel resuelto | Command | Se guardan los `MoveCommand` y se vuelven a ejecutar con un `javax.swing.Timer` | Medio |
| Bonus y penalidades de puntaje | Decorator | Un puntaje base envuelto por `NoUndoBonus`, `ParBonus`, etc. | Bajo |

Recomendación: **temas visuales** más **replay**. Replay reutiliza el Command que igual hace falta para las acciones
del jugador, y los temas se muestran fácil en vivo.

## Patrones aplicados hoy

| Patrón | Dónde | Qué resuelve |
|---|---|---|
| MVC | `model`, `view`, `controller`, con DTOs en `dto` | La vista dibuja datos de solo lectura y no toca las reglas |
| Observer | `Game` → `GameListener` → `VentanaPrincipal` | El modelo avisa cambios sin conocer la vista |
| Observer | `Lock` → `LockListener` → `Door` | Un cerrojo abre todos sus muros sin saber cuántos son |
| Template Method | `Caja.empujar()`, hook `despuesDeEmpujar()` | Las cajas comparten el empuje y varían en un paso |
| Factory Method | `CreadorTerreno`, `CreadorCaja` y sus concretos | El cargador no conoce las clases concretas |
| Memento | `Tablero`, `BoardMemento`, `MoveHistory` | Deshacer restaura todo el estado sin exponerlo |
| Prototype (parcial) | `Caja.copiar()` | El memento copia cajas sin conocer sus clases concretas |
| Adapter | `MusicPlayer`, `ClipMusicPlayer` | El controlador no depende de Java Sound |
| Decorator | `AccionesJugador`, `PowerUpDecorador` y sus concretos | Poderes temporales y combinables sin tocar `Jugador` |

El detalle con problema, roles y consecuencias está en `.claude/patrones-aplicados.md`.

**Para la defensa:** el comentario de `CargadorDeNivel` lo llama "Estrategia", pero tiene una sola implementación.
No conviene presentarlo como Strategy.

## Patrones que faltan implementar

| Patrón | Para qué | Estado |
|---|---|---|
| Command | Acciones del jugador; base del replay | No existe: el controlador llama directo a `Game` |
| State | Flujo del juego | No existe: hoy es el enum `GameStatus`, que no es el patrón |
| Prototype en el reinicio | Reinicio sin releer el archivo | Falta: Reiniciar funciona, pero relee el .txt |
| Singleton | Imágenes y sonidos cargados una vez | No existe |
| Strategy o Decorator | Puntaje | No existe |

## Qué falta, etapa por etapa

### Etapa 0: alinear al equipo (mié 4/11)

- [x] Carpetas en inglés, `.gitignore` y configuración de VS Code.
- [ ] Confirmar las decisiones D1 a D5, los símbolos de los .txt y Factory Method como carga de niveles.
- [ ] Confirmar D6 con el profe: el juego usa 3 usos por nivel y el enunciado dice "consecutivos".
- [x] D8: deshacer descuenta movimientos y empujes.
- [ ] Definir D7 (fórmula de puntaje).
- [ ] Elegir las 2 funcionalidades adicionales.
- [ ] Asignar un responsable por etapa.
- [ ] Hacer público el repo: **sin iniciar sesión sigue dando 404**, y el enunciado lo exige.
- [ ] Pasar `Desarollo` a `main` cuando el equipo lo revise.

### Etapa 1: limpieza (jue 5 a vie 6/11)

- [x] Los niveles se encuentran desde cualquier carpeta.
- [ ] Pasar al inglés las clases y métodos que siguen en español (`Tablero`, `Caja`, `Terreno`, `Controlador`...).
- [ ] Sacar los comentarios: quedan 312 líneas. Antes, pasar a `patrones-aplicados.md` lo que sirva.
- [ ] Mover los niveles a recursos del classpath si se va a entregar un `.jar`.

### Etapa 2: deshacer y patrones (sáb 7 a lun 9/11)

- [x] Undo con Memento: últimos 15 movimientos, retrocede 5, máximo 3 usos por nivel.
- [x] Botón Deshacer visible.
- [ ] Reinicio con Prototype.
- [ ] Command para las acciones y State para el flujo del juego.
- [ ] Un nivel corto por mecánica para la demo.

### Etapa 3: puntaje, sonido y contenido (mar 10 a jue 12/11)

Obligatorio según el enunciado:

- [x] HUD con nivel, movimientos y empujes.
- [x] Usos de deshacer en el resumen de fin de nivel.
- [ ] Puntaje final en el resumen, con criterio definido por el equipo.
- [ ] **Imágenes:** se piden para todos los elementos, y hoy el tablero se dibuja con figuras.
- [x] Música del menú en bucle.
- [ ] Efectos de sonido `.wav`.
- [ ] Assets de dominio público registrados en `CREDITS.md`. La música del menú (*Hellwalker*) tiene copyright: el enunciado pide dominio público, y `menu-theme.wav` queda como alternativa.

Opcional, propuesto por el plan y no por el enunciado:

- [x] Llegar a 5 niveles.
- [ ] Permitir cajas frágiles y llave sobre destinos, y cualquier caja sobre hielo, al inicio del nivel.

### Etapa 4: funcionalidades adicionales (vie 13 a dom 15/11)

- [x] Funcionalidad adicional 1: power-ups con Decorator (martillo y superfuerza, nivel 6). Diseño en doc_06 y doc_07.
- [ ] Funcionalidad adicional 2.

### Etapa 5: entregables y ensayo (lun 16 a mar 17/11)

- [ ] Diagrama de clases en StarUML (`.mdj` y PNG o PDF).
- [ ] Informe técnico, con decisiones de diseño, fórmula de puntaje y declaración de uso de IA.
- [ ] README final y video gameplay con todas las funcionalidades.
- [ ] Completar `patrones-aplicados.md` y revisar code smells.
- [ ] Probar el link del repo en incógnito.
- [ ] Ensayo cronometrado de 15 a 20 minutos, con al menos 2 patrones por integrante.
- [ ] Preparar el mail de entrega.

## Prioridad si falta tiempo

1. Imágenes, sonido y puntaje: son obligatorios, en su versión más simple.
2. Las 2 funcionalidades adicionales: son obligatorias, pero se pueden hacer en una versión simple.
3. Diagrama, informe y video: no se recortan, porque son parte de la entrega.
4. Más niveles y Command, State o Prototype en el reinicio: solo si queda tiempo, salvo que un extra los necesite.

## Verificación

Compila desde PowerShell y pasan las 181 pruebas. Una prueba automática de la ventana real, incluido el botón
Deshacer, no arrojó excepciones.
