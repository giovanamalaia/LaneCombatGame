package com.game.strategy;
import com.game.core.Lane;
import com.game.model.Enemy;

public class AtaqueDireto implements AttackStrategy {
    @Override
    public void atacar(Lane lane) {
        if (!lane.getInimigos().isEmpty()) {
            Enemy alvo = lane.getInimigos().get(0); 
            alvo.receberDano(50);
            System.out.println("-> Ataque Direto causou 50 de dano no inimigo!");
        } else {
            System.out.println("-> Nenhum inimigo nesta lane para atacar.");
        }
    }
}