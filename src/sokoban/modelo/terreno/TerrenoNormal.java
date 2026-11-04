package sokoban.modelo.terreno;

public class TerrenoNormal extends Terreno {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public void aceptar(VisitanteTerreno visitante) {
        visitante.visitarTerrenoNormal();
    }
}
