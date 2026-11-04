# Teoría de la cátedra — Proceso de Desarrollo de Software (Prof. Pepe)

Resumen de las clases 1–13, el apunte de UML y el programa 3.4.210, con el vocabulario de la cátedra.
Usarlo para justificar decisiones y preparar la defensa. Bibliografía: GoF (Gamma et al.), Larman
(*UML y patrones*), Booch/Rumbaugh/Jacobson, Wirfs-Brock; ejemplos de patrones basados en Refactoring Guru / Source Making.

---

## 1. Programa (unidades)

1. Objetos y metodologías (análisis y diseño iterativo).
2. Uso de patrones para mejorar soluciones: deuda técnica, **SOLID**, patrones, antipatrones, **bad smells**.
3. Asignación de responsabilidades: **GRASP**.
4. Patrones de diseño: creacionales, estructurales, de comportamiento.
5. Patrones de acceso a datos.

Herramientas: **Java**, **IntelliJ IDEA**, **StarUML**, Git/GitHub.

## 2. POO (Clase 1)

- Cuatro pilares: **abstracción** (simplificar conceptos), **encapsulamiento** (proteger datos; métodos públicos sobre atributos privados), **herencia** (reutilización y especialización, relación "es un"), **polimorfismo** (tratar igual objetos distintos, comportamiento resuelto en ejecución).
- `static`: pertenece a la clase. `final`: método no sobrescribible. `abstract`: obliga a implementar. `protected`: clase y derivadas.
- **Clase abstracta** → "es un" (Arquero es una Tropa). **Interfaz** → contrato "puede hacer" (Pato puede volar y nadar); desde Java 8 admite `default`; permite herencia múltiple de tipo.
- Sobrescritura (override, misma firma) ≠ sobrecarga (overloading).

## 3. Proceso de desarrollo (Clases 1–2)

- Ingeniería de software (IEEE): enfoque sistemático, disciplinado y cuantificable.
- **Cascada** (Royce 1970): etapas secuenciales; los errores de interpretación aparecen tarde y caros.
- **Iterativo e incremental**: mini-cascadas de 2–4 semanas, subsistema ejecutable temprano, "**fallar rápido y barato**".
- **Ágil**: manifiesto (4 valores, 12 principios). **Scrum**: artefactos (product backlog, sprint backlog, incremento), eventos (planning, daily, review, retro, refinamiento), roles (Product Owner, Scrum Master, equipo).
- Calidad = grado en que el software cumple su propósito (resuelve los *pain points*); 8 dimensiones de Garvin.
- Metodología = lenguaje de modelado (UML) + proceso (XP, Scrum, RUP) + herramientas.

## 4. UML (Clase 2 + apunte "Introducción al UML")

- Lenguaje para visualizar, especificar, construir y documentar; independiente del lenguaje y del proceso. Versión 2.5.1. 13 diagramas en tres grupos: estructural (estático), comportamiento (funcional), interacción (dinámico).
- **Clase**: nombre / atributos `visibilidad nombre: tipo` / operaciones `visibilidad nombre(params): retorno`. Visibilidad: `+` público, `-` privado, `#` protegido, `~` paquete. Estáticos subrayados, abstractos en cursiva.
- **Relaciones**:
  - **Asociación**: línea continua; roles, multiplicidad (`1`, `0..1`, `*`, `1..*`, `n..m`) y navegabilidad (flecha abierta). En Java se implementa como atributo (o colección para "a muchos").
  - **Agregación**: rombo vacío (todo-parte, la parte puede existir sola).
  - **Composición**: rombo lleno (la parte no existe sin el todo; el todo la crea y destruye). Ej.: `Board` ◆— `Cell`.
  - **Dependencia / uso**: flecha punteada (una clase usa a otra temporalmente, p. ej. como parámetro).
  - **Generalización**: flecha con triángulo vacío, línea continua (herencia).
  - **Realización**: triángulo vacío, línea punteada (implementa interfaz).
- En el diagrama de clases del TI se deben poder **ver los patrones**: p. ej. Singleton (`-instance: static`, constructor privado, `+getInstance(): static`), Observer (interfaz Listener + lista en el sujeto), Memento (Originator–Memento–Caretaker).

## 5. Refactoring, deuda técnica y code smells (Clase 3)

- **Refactorizar**: cambiar la estructura interna sin cambiar el comportamiento externo. Lleva a **clean code**: legible, KISS, sin duplicación (DRY), simple y mínimo, escalable.
- **Deuda técnica** (Ward Cunningham): costo futuro de elegir la solución rápida. Deliberada o inadvertida; idealmente consciente y con plan de pago.
- **Code smells** (taxonomía de 5 grupos):

| Grupo | Smells | Principio afectado |
|---|---|---|
| **Bloaters** | Long method, Large class, Primitive obsession, Long parameter list (→ DTO), Data clumps | SRP, cohesión |
| **OO abusers** | Switch statements, Temporary field, Refused bequest, Alternative classes with different interfaces | OCP, LSP |
| **Change preventers** | Divergent change, Shotgun surgery, Parallel inheritance hierarchies | SRP, acoplamiento |
| **Dispensables** | Comments, Duplicate code (DRY), Lazy class, Data class (tell, don't ask), Dead code, Speculative generality (YAGNI) | KISS, YAGNI |
| **Couplers** | Feature envy, Inappropriate intimacy, Message chains (Ley de Demeter), Middle man | Bajo acoplamiento |

## 6. SOLID (Clase 4, Robert C. Martin)

| Principio | Enunciado | Foco |
|---|---|---|
| **SRP** | Nunca debería haber más de una razón para cambiar una clase. | Alta cohesión (qué hace / a quién conoce). |
| **OCP** | Abiertas a la extensión, cerradas a la modificación. | Bajo acoplamiento; extender sin tocar código existente. |
| **LSP** | Quien usa la clase base debe poder usar derivadas sin saberlo. "Si heredás, comportate como tu padre". | Extensión del OCP; evita Refused bequest. |
| **ISP** | Los clientes no deben depender de interfaces que no usan. | Interfaces chicas y cohesivas; previene violar LSP. |
| **DIP** | Alto nivel y bajo nivel dependen de abstracciones; los detalles dependen de las abstracciones. | Desacoplar; depender de interfaces (práctica: `NotificationService` + `NotificationSender` inyectado). |

## 7. GRASP (Clase 5, Larman) — 9 patrones de asignación de responsabilidades

Responsabilidades: **de hacer** (hacer algo, iniciar acciones, coordinar) y **de conocer** (datos propios, objetos relacionados, cosas derivables). Una responsabilidad no es un método.

| Patrón | Pregunta | Respuesta |
|---|---|---|
| **Experto en información** | ¿Principio básico para asignar responsabilidades? | A la clase que tiene la información necesaria. |
| **Creador** | ¿Quién crea A? | B si B contiene, agrega, registra, usa directamente o tiene los datos de inicialización de A. |
| **Controlador** | ¿Quién gestiona un evento del sistema? | Un objeto que representa el sistema (fachada) o el caso de uso (sesión). Separa UI de negocio. |
| **Bajo acoplamiento** | ¿Cómo reducir dependencias? | Asignar para que el acoplamiento innecesario sea bajo. SOLID ayuda. |
| **Alta cohesión** | ¿Cómo mantener manejable la complejidad? | Responsabilidades fuertemente relacionadas en cada clase. |
| **Polimorfismo** | ¿Comportamiento que varía según el tipo? | Operaciones polimórficas en los tipos que varían (interfaces/abstractas). Muy ligado a Strategy. |
| **Fabricación pura** | ¿Y si ninguna clase del dominio encaja sin romper cohesión/acoplamiento? | Clase artificial (servicio de dominio) con responsabilidades cohesivas. |
| **Indirección** | ¿Cómo evitar acoplamiento directo? | Un intermediario (interfaz, Facade, Adapter, Proxy, Observer). |
| **Variaciones protegidas** | ¿Cómo aislar puntos inestables? | Interfaz estable alrededor de lo que se prevé que cambie. |

SOLID = diseño de bajo nivel y estructura del código; GRASP = capa conceptual de a quién asignar responsabilidades. Se complementan.

## 8. MVC y Swing (Clase 6)

- Patrón **arquitectónico** para GUIs. **Modelo**: datos y reglas de negocio. **Controlador**: responde a eventos del usuario e invoca al modelo; intermediario. **Vista**: presenta datos; la comunicación Vista–Controlador idealmente con **DTOs**.
- Beneficios: separación de responsabilidades, mantenibilidad, desarrollo en equipo, testing, escalabilidad.
- Buenas prácticas: la lógica no está en la vista; controladores como intermediarios; vista simple y reactiva.
- **Swing**: incluido en Java SE, evolución de AWT, basado en componentes y **orientado a eventos (listeners = observadores)**, gráficos 2D, multiplataforma.

## 9. Patrones GoF (Clases 8–13)

**Patrón vs principio**: un principio es una guía abstracta (SRP, OCP, Tell don't ask); un patrón es una solución probada con estructura definida (nombre, problema, solución, consecuencias). Un patrón no es un algoritmo ni código para copiar. GoF = 23 patrones (1994): creacionales (instanciación), estructurales (cómo se componen clases/objetos), de comportamiento (comunicación y responsabilidades).

### Creacionales

| Patrón | Analogía de clase | Problema | Solución / roles |
|---|---|---|---|
| **Singleton** | La CIA: solo puede haber una | Múltiples instancias innecesarias de algo costoso o que debe coordinarse globalmente (configuración, conexión) | Constructor privado + `private static instance` + `public static getInstance()` con lazy initialization. |
| **Factory Method** | Logística: Truck → luego Ship | `new` de clases concretas acopla y viola OCP al agregar tipos | **Creator** con método de fábrica abstracto; **ConcreteCreators** (`TruckCreator`, `ShipCreator`) lo sobrescriben; el cliente usa el Creator/Product abstractos. |
| **Abstract Factory** | Muebles moderno/victoriano | Crear **familias** de objetos relacionados que deben combinar entre sí | `FurnitureFactory` con `createChair()`, `createSofa()`; fábricas concretas por estilo; el cliente solo conoce la interfaz. |
| **Builder** | Construir una casa por etapas | Constructores telescópicos, `null` en parámetros, inmutabilidad perdida | **Product**, **Builder** (pasos), **ConcreteBuilder**, **Director** opcional que define el orden. |
| **Prototype** | Clonar un monstruo complejo | Creación costosa o muchas copias casi idénticas, sin acoplarse a clases concretas | Interfaz con `clone()`; cada clase concreta sabe copiarse; registro de prototipos opcional. |

### Estructurales

| Patrón | Analogía | Problema | Solución / roles |
|---|---|---|---|
| **Adapter** | Adaptador de enchufe | Interfaces incompatibles (`SistemaViejo.obtenerListado()` vs `traerData()`), sin poder modificar ninguna | **Target** (interfaz del cliente), **Adaptee** (clase incompatible), **Adapter** implementa Target, contiene al Adaptee y traduce. |
| **Decorator** | Café + leche + canela | Agregar funcionalidades sin explosión de subclases (`VehiculoConGPSTurbo`…); extensión dinámica | Componente común; `DecoradorBase` con referencia al envuelto; decoradores concretos delegan y agregan su lógica antes/después. Capas en tiempo de ejecución. |
| **Bridge** | Control remoto y dispositivos | Explosión de clases por varias dimensiones (Forma × Color) | Separar **Abstracción** (Forma) de **Implementación** (IColor); el puente es la referencia. 2+2 clases en vez de 2×2. |
| **Composite** | Soldado y brigada responden a `ejecutarOrden()` | Tratar distinto hojas y grupos lleva a `instanceof` y viola OCP | **Component** (interfaz común, `obtenerCosto()`), **Leaf**, **Composite** (lista de Components, delega y suma). Estructura de árbol. |
| **Facade** | Recepcionista de hotel | Cliente acoplado a muchos subsistemas, viola Demeter | Clase **Fachada** con una operación simple que orquesta los **subsistemas** (`InicializadorDeJuego.iniciarJuego()`). |

### Comportamiento

| Patrón | Analogía | Problema | Solución / roles |
|---|---|---|---|
| **Observer** | Suscripción a canal de YouTube (1 a N) | El sujeto conoce clases concretas de observadores y cambia con cada nuevo tipo (viola OCP) | **Publisher** con `attach()`, `detach()`, `notify()` y lista de **Subscriber**; Subscriber define `update()`. |
| **Strategy** | GPS: auto, a pie, transporte público | `if/switch` gigantes para elegir algoritmo; viola OCP; algoritmos mezclados | **Strategy** (interfaz `calcularRuta`), **ConcreteStrategies**, **Context** con referencia a la Strategy, intercambiable en ejecución. |
| **Command** | Comanda de restaurante | El invocador conoce al receptor; no hay historial ni undo; no se puede encolar | **Command** (`execute()`), **ConcreteCommand** (referencia al Receiver), **Invoker** (no conoce al Receiver), **Receiver** (hace el trabajo). Facilita historial, colas, deshacer/rehacer. |
| **Memento** | Guardar/cargar partida | Volver a un estado anterior sin violar encapsulamiento ni acoplar el historial a los campos internos | **Originator** (crea y consume el memento), **Memento** (cápsula **inmutable**, solo visible para el Originator), **Caretaker** (pila/lista de mementos, nunca mira adentro). |
| **State** | Reproductor: detenido / reproduciendo / pausado | `if/switch` por estado en todos los métodos, transiciones duplicadas, viola OCP | **Context** (referencia al estado actual), **State** (interfaz), **ConcreteStates** con comportamiento y reglas de transición. Implementación OO de una máquina de estados finitos. |
| **Mediator** | Torre de control aéreo | Red caótica N a N entre componentes | **Mediator** (interfaz), **ConcreteMediator** (orquesta), **Components** que solo conocen al mediador. Estrella 1 a N. |
| **Template Method** | Receta: preparar, cocinar, emplatar | Algoritmos con pasos idénticos y variables → duplicación; difícil asegurar el orden | **Clase base abstracta** con el método plantilla (orden fijo) y métodos primitivos abstractos; **subclases** implementan los pasos. Base del DIP. |

**Memento ≠ Command** (Clase 13): Command encapsula **acciones** (qué ejecutar y con qué parámetros); Memento captura y restaura **estados**. Se complementan pero no son lo mismo.

## 10. Git y GitHub (Clase 9)

Git: control de versiones distribuido (Linus Torvalds, 2005). GitHub: hosting + colaboración (issues, pull requests, README). Flujo: clonar → rama por desarrollador → commits → push → pull request y revisión.

## 11. Ejercicios de práctica de la cursada (referencia de estilo)

- **Práctica DIP**: `NotificationService` depende de `NotificationSender` (Email/WhatsApp/SMS) inyectado → intercambiable sin modificar el servicio.
- **Biblioteca (GRASP)**: ¿quién calcula demora, multa, disponibilidad, quién crea el préstamo? → Experto/Creador/Polimorfismo para políticas por tipo de usuario.
- **Casino con dado (MVC + Swing)**: modelo con saldo/reglas, vista con tabla de historial, controlador intermediario.
- **Resort de lujo (integrador)**: reservas corporativas jerárquicas → **Composite**; precio por temporada intercambiable → **Strategy**; paquetes paso a paso con ofertas estándar → **Builder + Director**; estados de habitación sin condicionales → **State**; termostato heredado incompatible → **Adapter**; check-out express que coordina subsistemas → **Facade**; monitores únicos que reflejan cambios de estado → **Observer + Singleton**.
  Este es el tipo de razonamiento "requerimiento → patrón" que se espera en la defensa del TI.
