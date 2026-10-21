package com.game.factory;
import com.game.model.*;

public class EnemyFactory {
    public static Enemy criarInimigo(String tipo) {
        if (tipo.equalsIgnoreCase("comum")) {
            return new InimigoComum();
        } else if (tipo.equalsIgnoreCase("rapido")) {
            // Você pode criar InimigoRapido depois
            return new InimigoComum(); 
        }
        throw new IllegalArgumentException("Tipo de inimigo desconhecido: " + tipo);
    }
}