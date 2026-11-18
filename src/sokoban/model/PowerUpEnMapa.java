package sokoban.model;

import sokoban.dto.PowerUpData;
import sokoban.dto.TipoPowerUp;

public record PowerUpEnMapa(TipoPowerUp tipo, Posicion posicion) {

    public PowerUpData toData() {
        return new PowerUpData(tipo, posicion.toData());
    }
}
