package com.game.factory;
import com.game.model.*;

public class EnemyFactory {
    public static Enemy criarInimigo(String tipo) {
        if (tipo.equalsIgnoreCase("comum")) {
            return new InimigoComum();
        } else if (tipo.equalsIgnoreCase("rapido")) {
            return new InimigoRapido();
        } else if (tipo.equalsIgnoreCase("tanque")) {
            return new InimigoTanque();
        }
        throw new IllegalArgumentException("Tipo de inimigo desconhecido: " + tipo);
    }
}