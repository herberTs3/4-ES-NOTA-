package sokoban.dto;

public enum TipoPowerUp {
    MARTILLO(10, 3), SUPERFUERZA(15, 0);

    private final int turnos;
    private final int usos;

    TipoPowerUp(int turnos, int usos) {
        this.turnos = turnos;
        this.usos = usos;
    }

    public EstadoPowerUp estadoInicial() {
        return new EstadoPowerUp(this, turnos, usos);
    }
}
