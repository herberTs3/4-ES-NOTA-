# Sokoban - Trabajo Integrador

Trabajo Integrador de la materia **Proceso de Desarrollo de Software** (UADE). 
Profesor: Jonathan Pepe

**Equipo:** 4 es nota

## Descripción
Versión del clásico juego de ingenio Sokoban desarrollada en Java con la librería Swing (GUI 2D). El objetivo principal del proyecto es aplicar los patrones y principios de diseño vistos en la cursada (SOLID, GRASP, MVC, GOF).

-- Funcionalidades implementadas
* **Carga de niveles:** Los mapas se cargan desde archivos `.txt` en la carpeta `levels`.
* **Movimiento:** El jugador empuja cajas, y al ganar pasa al siguiente nivel.
* **Cajas especiales:** Cajas normales, frágiles (se rompen tras varios empujes) y llave (abren muros al colocarse en cerrojos).
* **Hielo:** Las cajas se deslizan hasta chocar o salir del hielo.
* **Deshacer:** Guarda los últimos 15 movimientos; cada uso retrocede 5, con un máximo de 3 usos consecutivos.
* **Interfaz:** Nivel actual, contadores de movimientos y empujes, cajas ubicadas, usos de deshacer y resumen al terminar cada nivel.

-- En desarrollo
* Puntaje, imágenes y sonido.

-- Cómo ejecutar
Requiere JDK 16 o superior. En VS Code, elegir la configuración **Sokoban** en Ejecutar y depurar.

Por consola, desde la raíz del proyecto. En PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out sokoban.Main
```

En Git Bash, Linux o macOS:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out sokoban.Main
```

Pruebas, en PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src,test -Filter *.java).FullName
java -cp out sokoban.level.PruebaRegistro
java -cp out sokoban.controller.PruebaControlador
java -cp out sokoban.model.PruebaJuego
```

En Git Bash, Linux o macOS, cambiar la primera línea por:

```bash
javac -encoding UTF-8 -d out $(find src test -name "*.java")
```

-- Controles
* **Flechas direccionales:** Movimiento del jugador.
* **Botones en pantalla:** Anterior, Siguiente, Deshacer y Reiniciar.

-- Formato de los niveles
| Símbolo | Elemento |
|---|---|
| `#` | Pared |
| espacio | Piso |
| `.` | Destino |
| `@` / `+` | Jugador / jugador sobre destino |
| `$` / `*` | Caja normal / caja normal sobre destino |
| `1`-`9` | Caja frágil con esa cantidad de empujes |
| `K` | Caja llave |
| `~` | Hielo |
| `a`-`e` | Cerrojo que abre los muros de su letra |
| `A`-`E` | Muro cerrado vinculado a un cerrojo |

-- Otros entregables
En la entrega por mail se incluyen:
* Informe técnico con justificación de patrones.
* Diagrama de clases.
* Gameplay.
