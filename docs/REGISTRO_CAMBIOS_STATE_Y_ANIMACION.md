# Sokoban — Registro de cambios: State y animación de hielo

**Fecha del registro:** 16 de noviembre de 2026  
**Estado:** Implementado en Java y verificado mediante pruebas automatizadas  
**Base de comparación:** `sokoban.zip` (última versión del equipo recibida)  
**Versión documentada:** `Sokoban_Animacion_Hielo_Proyecto_Completo.zip` (incluye State y la animación visual de hielo)

> **Alcance.** Este registro describe las modificaciones efectivamente realizadas sobre la última versión compartida del código. Los power-ups con Decorator, su lectura mediante Factory Method y las mecánicas existentes de Sokoban ya estaban presentes en `sokoban.zip`: no se presentan aquí como funcionalidades desarrolladas en esta intervención.

## 1. Resumen ejecutivo

Se incorporó el patrón de comportamiento **State** a las **cajas frágiles** para representar su deterioro y cambiar las operaciones permitidas conforme disminuye su resistencia. Se agregó también información del estado visual para que la vista Swing dibuje una caja intacta o agrietada y muestre brevemente los restos cuando se rompe. Posteriormente se añadió una **animación de deslizamiento sobre terrenos resbaladizos** que interpola visualmente las posiciones sin modificar los movimientos calculados por el modelo.

**Se mantuvieron las reglas originales** de eliminación de cajas rotas, evaluación del estado del nivel, `undo()` con Memento, carga de cajas por caracteres TXT y comportamiento de los demás tipos de cajas.

### Alcance de archivos

| Entrega | Archivos Java nuevos | Archivos Java modificados |
|---|---:|---:|
| Incorporación de State | 5 | 5 |
| Animación de hielo (posterior a State) | 0 | 2 |
| **Total respecto de `sokoban.zip`** | **5** | **6 archivos únicos** |

**Nota:** `VistaTablero.java` fue modificado en ambas entregas; por eso el total de archivos modificados únicos es seis, no siete. También se agregaron dos archivos de pruebas bajo `tests/`.

---

## 2. Cambio principal: patrón State en `CajaFragil`

### 2.1. Motivo

Antes, `CajaFragil` controlaba el desgaste principalmente mediante `movimientosRestantes`. Se necesitaba distinguir comportamientos a medida que la caja se deterioraba: una caja suficientemente dañada **sigue pudiendo empujarse, pero ya no debe poder jalarse con el power-up de superfuerza**. Además, la interfaz gráfica debía reflejar ese deterioro.

El patrón **State** permite encapsular las reglas de cada situación en objetos distintos. `CajaFragil` actúa como **Context** y delega sus reacciones a una implementación de `EstadoCajaFragil`. No se sustituyó la jerarquía existente `Caja` → `CajaFragil`.

### 2.2. Estados y transiciones

| Estado concreto | Condición sobre resistencia | Empujar | Jalar con superfuerza | Representación |
|---|---|---|---|---|
| `EstadoIntacta` | Consumió menos del 50 % | Permitido | Permitido | Caja sin grietas |
| `EstadoAgrietada` | Consumió al menos el 50 % y aún resta resistencia | Permitido | **No permitido** | Caja con grietas |
| `EstadoRota` | Resistencia restante = 0 | Ya no se mueve | No permitido | La caja es retirada del tablero; restos visuales temporales |

La condición para mostrar **Agrietada** es `2 × movimientosRestantes <= resistenciaInicial`, siempre que `movimientosRestantes > 0`. Se conserva el valor inicial de la resistencia para calcular correctamente este umbral.

**Ejemplo: una caja creada con el carácter `4` en un TXT**

| Después de... | Resistencia | Estado |
|---|---:|---|
| Aparecer en el nivel | 4/4 | Intacta |
| 1 empuje o jalón válido | 3/4 | Intacta |
| 2 movimientos de caja válidos | 2/4 | Agrietada |
| 3 movimientos de caja válidos | 1/4 | Agrietada |
| 4 movimientos de caja válidos | 0/4 | Rota (retirada por el tablero) |

- Una operación válida de **empujar o jalar** consume una unidad de resistencia, no una por cada celda recorrida sobre hielo.
- El jalón de una caja agrietada se rechaza **antes** de cambiar su posición y **no consume un turno**.
- Para resistencias impares, el umbral se aplica al cruzar o alcanzar el 50 % consumido. Los caracteres `1` a `9` de la carga TXT continúan admitiéndose sin modificaciones.

### 2.3. Participantes del patrón y responsabilidades

| Rol GoF | Clase | Responsabilidad |
|---|---|---|
| **Context** | `CajaFragil` | Conserva resistencia inicial y restante; selecciona el estado actual y delega las reacciones |
| **State** | `EstadoCajaFragil` | Define `alSerEmpujada`, `alSerJalada`, `permiteJalar`, `estaRota` y `apariencia` |
| **ConcreteState** | `EstadoIntacta` | Descuenta resistencia al empujar o jalar; permite ambas acciones |
| **ConcreteState** | `EstadoAgrietada` | Descuenta resistencia al empujar, pero impide jalar |
| **ConcreteState** | `EstadoRota` | Representa el estado terminal y prohíbe el jalón |

Los estados no son tipos de caja. `CajaNormal`, `CajaLlave` y `CajaFragil` siguen siendo tipos distintos; los estados se aplican solamente a `CajaFragil`.

### 2.4. Estructuras nuevas

| Ruta | Uso |
|---|---|
| `sokoban/model/box/EstadoCajaFragil.java` | Interfaz del patrón |
| `sokoban/model/box/EstadoIntacta.java` | Estado concreto intacto |
| `sokoban/model/box/EstadoAgrietada.java` | Estado concreto agrietado |
| `sokoban/model/box/EstadoRota.java` | Estado concreto roto |
| `sokoban/dto/EstadoVisualCajaFragil.java` | Enum `INTACTA`, `AGRIETADA`, `ROTA` para informar a la vista |

### 2.5. Archivos existentes modificados para State

| Ruta | Modificación concreta |
|---|---|
| `sokoban/model/box/Caja.java` | Añade los puntos de extensión `puedeSerJalada()` y `despuesDeJalar()`; `jalarA()` consulta la posibilidad de jalar; el comportamiento por defecto de otras cajas se conserva |
| `sokoban/model/box/CajaFragil.java` | Almacena `resistenciaInicial`, `movimientosRestantes` y `estado`; determina transiciones; delega empuje y jalón; informa apariencia; conserva ambos valores al copiar |
| `sokoban/model/Tablero.java` | Verifica `caja.puedeSerJalada()` antes de intentar un jalón, evitando desplazar piezas en acciones rechazadas |
| `sokoban/dto/BoxData.java` | Añade el campo `estadoFragil` y mantiene constructor compatible de cinco argumentos para cajas no frágiles |
| `sokoban/view/VistaTablero.java` | Diferencia intacta/agrietada al pintar la caja y muestra un efecto temporal de restos tras la rotura |

### 2.6. Integración con la vista

`CajaFragil.toData()` envía su estado a `BoxData.estadoFragil()`; `VistaTablero` usa ese dato para dibujar la caja con o sin grietas. La vista **no decide** cuándo se produce la transición: esa responsabilidad sigue en el modelo. El sprite o dibujo de la caja destruida se expresa mediante un efecto gráfico transitorio de unos **850 ms**, ya que el tablero elimina el objeto roto tal como lo hacía antes.

### 2.7. Compatibilidad con Memento, carga TXT y finalización

- **Memento:** `CajaFragil.copiar()` conserva la resistencia **inicial y restante**. Al restaurarse una caja, se reconstruye su estado según estos valores; no hubo que cambiar `BoardMemento` ni `MoveHistory`.
- **Factory Method / niveles TXT:** `CreadorCajaFragil` continúa creando cajas a partir de los caracteres `1` a `9`; no fue modificado.
- **Rotura:** el `Tablero` conserva la eliminación de la caja al romperse.
- **Fin del nivel:** **`Game` no fue modificado**. Conserva la evaluación existente de `GameStatus.UNSOLVABLE` cuando no quedan cajas suficientes para cubrir los destinos. Esto significa que una rotura no implica necesariamente `UNSOLVABLE` si el nivel dispone de cajas sobrantes.
- **Decorator:** la superfuerza existente utiliza la nueva verificación de posibilidad de jalón; no fue necesario modificar las clases de los power-ups.

---

## 3. Cambio posterior: animación de deslizamiento sobre hielo

La lógica que determina el movimiento sobre hielo **ya existía** en `Caja.deslizar()`; esta entrega añadió solamente la transición visual entre la posición inicial y la final.

| Archivo modificado | Cambio |
|---|---|
| `sokoban/view/VistaTablero.java` | Compara instantáneas anteriores y posteriores (`BoardSnapshot`), identifica el recorrido de la caja, interpola posiciones usando `javax.swing.Timer` y añade una estela visual. Si la caja frágil se rompe tras deslizarse, dibuja los restos al final del trayecto |
| `sokoban/view/VentanaPrincipal.java` | Determina si hubo un movimiento válido para indicar cuándo animar; evita animaciones de deslizamiento al deshacer, reiniciar o cambiar de nivel |

**Parámetros actuales** (en `VistaTablero.java`):

| Parámetro | Valor |
|---|---|
| Duración nominal por celda | 115 ms |
| Duración mínima de animación | 150 ms |
| Duración máxima de animación | 850 ms |
| Intervalo del temporizador visual | 16 ms (~60 fps nominales) |

La animación del jugador acompaña el empuje inicial para evitar superposiciones. Si llega una nueva acción, puede cancelarse la animación anterior y mostrarse el estado real; la lógica del modelo no espera a la animación.

**Limitación conocida:** si un deslizamiento completa el nivel, el diálogo modal de victoria puede aparecer antes de que termine la animación. No se modificó ese flujo.

---

## 4. Validación efectuada

Se compiló la versión conjunta con **JDK 21** y se ejecutaron nuevamente las dos pruebas incluidas en la entrega:

| Archivo de prueba | Resultado | Cobertura principal |
|---|---:|---|
| `tests/PruebaStateCajaFragil.java` | **131 comprobaciones superadas** | Resistencias 1–9, umbral, empuje, jalón, superfuerza, `UNSOLVABLE`, Memento y renderizado básico |
| `tests/PruebaAnimacionHielo.java` | **14 comprobaciones superadas** | Deslizamiento sobre hielo, bloqueo por muros, cancelación por deshacer, transición visual y rotura de caja frágil |

**Total: 145 comprobaciones automatizadas superadas.** No se realizó una partida gráfica interactiva completa; es recomendable verificar visualmente los efectos y la apertura de diálogos en el entorno del equipo.

Desde la raíz de la versión completa entregada, con JDK 21 instalado:

```bash
javac -encoding UTF-8 -d build $(find sokoban -name '*.java') tests/PruebaStateCajaFragil.java tests/PruebaAnimacionHielo.java
java -Djava.awt.headless=true -cp build PruebaStateCajaFragil
java -Djava.awt.headless=true -cp build PruebaAnimacionHielo
```

> El ejemplo de compilación usa `find` (Linux/macOS). En Windows se pueden ejecutar las clases de prueba desde el IDE o usar otro método para enumerar los `.java`.

## 5. Recomendaciones para el equipo

1. Usar como referencia la **última entrega integrada**; no volver a copiar encima archivos de la versión previa de State, porque podrían perderse los cambios posteriores de la vista.
2. Si `Caja.java`, `CajaFragil.java`, `Tablero.java`, `BoxData.java`, `VistaTablero.java` o `VentanaPrincipal.java` fueron editados por otras personas, **comparar y fusionar** antes de reemplazar.
3. Probar manualmente un nivel con caja frágil, otro con superfuerza y otro con hielo, incluyendo `undo()` y reinicio.
4. Mantener este registro en la carpeta `docs/` del repositorio y actualizarlo ante futuras modificaciones.

## 6. Estado de entrega y referencias

- **Código verificado:** `Sokoban_Animacion_Hielo_Proyecto_Completo.zip`.
- **Archivos específicos de State:** `Sokoban_State_Adaptado_Solo_Cambios.zip`.
- **Archivos específicos de animación:** `Sokoban_Animacion_Hielo_Solo_Cambios.zip`.
- **Documentación previa:** `Guia_State_CajaFragil_Proyecto_Actualizado.md` y `Guia_Animacion_Hielo_Sokoban.md`.

**Este documento registra cambios efectivamente incorporados al código de las entregas anteriores. No introduce nuevas modificaciones al comportamiento de Sokoban.**
