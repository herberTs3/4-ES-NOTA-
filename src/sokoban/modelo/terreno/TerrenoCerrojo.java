package sokoban.modelo.terreno;

/** Terreno bloqueado hasta que una CajaLlave con la misma clave lo abre. */
public class TerrenoCerrojo extends Terreno {

    private final int clave;
    private boolean abierto;

    public TerrenoCerrojo(int clave) {
        this.clave = clave;
    }

    public int getClave() {
        return clave;
    }

    public boolean estaAbierto() {
        return abierto;
    }

    public void abrir() {
        abierto = true;
    }

    @Override
    public boolean esTransitable() {
        return abierto;
    }

    @Override
    public void aceptar(VisitanteTerreno visitante) {
        visitante.visitarTerrenoCerrojo(clave, abierto);
    }
}
