package sokoban.modelo.terreno;

public class Muro extends Terreno {

    @Override
    public boolean esTransitable() {
        return false;
    }

    @Override
    public boolean esMuro() {
        return true;
    }

    @Override
    public void aceptar(VisitanteTerreno visitante) {
        visitante.visitarMuro();
    }
}
