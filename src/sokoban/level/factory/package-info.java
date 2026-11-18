/**
 * <h2>Fábricas de terrenos y cajas (patrón Factory Method)</h2>
 *
 * <p>Este paquete responde a una sola pregunta: <b>"dado un símbolo del archivo de nivel,
 * ¿qué objeto del modelo hay que construir?"</b>. Antes la respuesta vivía en dos
 * {@code switch} dentro del cargador; ahora vive acá, repartida en clases chicas.</p>
 *
 * <h3>Correspondencia con el patrón Factory Method</h3>
 * <pre>
 *  Rol del patrón        Terrenos                         Cajas
 *  --------------------  -------------------------------  ------------------------------
 *  Producto (abstracto)  Terreno                          Caja
 *  Producto concreto     Muro, TerrenoNormal,             CajaNormal, CajaLlave,
 *                        TerrenoDestino,                  CajaFragil
 *                        TerrenoResbaladizo,
 *                        Lock, Door
 *  Creador (interfaz)    {@link CreadorTerreno}           {@link CreadorCaja}
 *  Creador concreto      CreadorMuro, CreadorTerreno...   CreadorCajaNormal, ...
 *  Cliente               CargarNivelPorTxt (a través del {@link RegistroDeCreadores})
 * </pre>
 *
 * <h3>¿Por qué hay un registro?</h3>
 * <p>Factory Method resuelve "cómo se construye un producto", pero alguien tiene que elegir
 * <i>qué creador</i> usar para cada símbolo. Esa elección la hace el
 * {@link RegistroDeCreadores}: guarda la lista de creadores disponibles y le pregunta a cada
 * uno si reconoce el símbolo. Gracias a eso el cargador no conoce ninguna clase concreta y
 * se pueden agregar tipos nuevos sin modificarlo (principio abierto/cerrado).</p>
 *
 * <h3>Dos familias separadas</h3>
 * <p>Una celda del archivo puede describir <b>dos cosas a la vez</b>: el terreno y la caja
 * que está encima (por ejemplo {@code *} es "caja sobre destino"). Por eso hay una familia de
 * creadores para terrenos y otra para cajas, y un mismo símbolo puede ser reconocido por un
 * creador de cada familia.</p>
 */
package sokoban.level.factory;
