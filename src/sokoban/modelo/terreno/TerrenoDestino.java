package sokoban.modelo.terreno;

public class TerrenoDestino extends Terreno {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean esDestino() {
        return true;
    }

    @Override
    public void aceptar(VisitanteTerreno visitante) {
        visitante.visitarTerrenoDestino();
    }
}
