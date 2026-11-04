package sokoban.modelo.caja;

/** Visitante de cajas (double dispatch); ver {@link Caja#aceptar(VisitanteCaja)}. */
public interface VisitanteCaja {

    void visitarCajaNormal();

    void visitarCajaLlave(int clave);

    void visitarCajaFragil(int movimientosRestantes);
}
