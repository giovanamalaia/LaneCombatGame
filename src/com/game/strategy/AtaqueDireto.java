package com.game.strategy;
import com.game.core.Lane;
import com.game.model.Enemy;

public class AtaqueDireto implements AttackStrategy {
    @Override
    public void atacar(Lane laneAtual) {
        if (!laneAtual.getInimigos().isEmpty()) {
            // alvo e o inimigo mais proximo do jogador (rapidos podem ultrapassar os outros)
            Enemy alvo = laneAtual.getInimigos().get(0);
            for (Enemy e : laneAtual.getInimigos()) {
                if (e.getPosicao() < alvo.getPosicao()) {
                    alvo = e;
                }
            }
            alvo.receberDano(50);
            System.out.println("-> Ataque Direto causou 50 de dano no inimigo!");
        } else {
            System.out.println("-> Nenhum inimigo na lane para atacar!");
        }
    }
}