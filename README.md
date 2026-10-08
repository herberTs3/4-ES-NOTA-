# Sokoban - Trabajo Integrador

Trabajo Integrador de la materia **Proceso de Desarrollo de Software** (UADE). 
Profesor: Jonathan Pepe

**Equipo:** 4 es nota

## Descripción
Versión del clásico juego de ingenio Sokoban desarrollada en Java con la librería Swing (GUI 2D). El objetivo principal del proyecto es aplicar los patrones y principios de diseño vistos en la cursada (SOLID, GRASP, MVC, GOF).

-- Funcionalidades implementadas
* **Carga de niveles:** Los mapas se cargan automáticamente desde archivos `.txt`.
* **Cajas especiales:** Cajas normales, frágiles (se rompen tras varios empujes) y llave (abren muros al colocarse en cerrojos).
* **Sistema Undo:** Permite retroceder hasta 15 movimientos (5 por cada uso del botón, máximo 3 usos).
* **Interfaz y Sonido:** HUD con contador de nivel, movimientos, empujes y puntaje final. Efectos de sonido y assets de dominio público.


-- Controles
* **Flechas direccionales:** Movimiento del jugador.
* **Botones en pantalla:** Reiniciar nivel y Deshacer movimiento.

-- Otros entregables
En la entrega por mail se incluyen:
* Informe técnico con justificación de patrones.
* Diagrama de clases.
* Gameplay.
