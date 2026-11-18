# Sokoban - Trabajo Integrador

Trabajo Integrador de la materia **Proceso de Desarrollo de Software** (UADE). 
Profesor: Jonathan Pepe

**Equipo:** 4 es nota

## Descripción
Versión del clásico juego de ingenio Sokoban desarrollada en Java con la librería Swing. El objetivo principal del proyecto es aplicar los patrones y principios de diseño vistos en la cursada (SOLID, GRASP, MVC, GOF).

-- Funcionalidades implementadas
* **Carga de niveles:** Los mapas se cargan desde archivos `.txt` en la carpeta `levels`.
* **Movimiento:** El jugador empuja cajas, y al ganar pasa al siguiente nivel.
* **Cajas especiales:** Cajas normales, frágiles (se rompen tras varios empujes) y llave (abren muros al colocarse en cerrojos).
* **Hielo:** Las cajas se deslizan hasta chocar o salir del hielo.
* **Deshacer:** Guarda los últimos 15 movimientos; cada uso retrocede 5, con un máximo de 3 usos por nivel. También descuenta movimientos y empujes.
* **Power-ups (Decorator):** `H` da un martillo (10 turnos, 3 usos) que rompe muros interiores con `Ctrl + flecha`. `F` da superfuerza (15 turnos) para jalar cajas con `Shift + flecha`. Se pueden tener los dos a la vez.
* **Menú:** Jugar, selector de niveles, créditos y música en bucle. Cada nivel se desbloquea al terminar el anterior.
* **Interfaz:** Nivel actual, contadores de movimientos y empujes, cajas ubicadas, usos de deshacer y resumen al terminar cada nivel.

-- En desarrollo
* Puntaje, imágenes y efectos de sonido.


-- Controles
* **Flechas direccionales:** Movimiento del jugador.
* **Ctrl + flecha:** Romper un muro interior (con martillo).
* **Shift + flecha:** Jalar una caja (con superfuerza).
* **Botones en pantalla:** Menú, Deshacer y Reiniciar.

-- Formato de los niveles
| Símbolo | Elemento |
|---|---|
| `#` | Pared |
| espacio | Piso |
| `.` | Destino |
| `@` / `+` | Jugador / jugador sobre destino |
| `$` / `*` | Caja normal / caja normal sobre destino |
| `1`-`9` | Caja frágil con esa cantidad de empujes |
| `K`-`O` | Caja llave: `K` abre el cerrojo `a`, `L` el `b`, `M` el `c`, `N` el `d` y `O` el `e` |
| `~` | Hielo |
| `H` | Power-up martillo |
| `F` | Power-up superfuerza |
| `a`-`e` | Cerrojo que abre los muros de su letra |
| `A`-`E` | Muro cerrado vinculado a un cerrojo |

-- Otros entregables
En la entrega por mail se incluyen:
* Informe técnico con justificación de patrones.
* Diagrama de clases.
* Gameplay.
