package com.game.strategy;
import com.game.core.Lane;
import com.game.model.Enemy;
import java.util.List;

public class AtaqueEmArea implements AttackStrategy {
    private List<Lane> todasLanes;

    public AtaqueEmArea(List<Lane> todasLanes) {
        this.todasLanes = todasLanes;
    }

    @Override
    public void atacar(Lane laneAtual) {
        System.out.println("-> ATAQUE EM ÁREA! Uma onda de choque atinge todas as lanes!");
        for (Lane lane : todasLanes) {
            for (Enemy enemy : lane.getInimigos()) {
                enemy.receberDano(30); // causa 30 de dano em geral
            }
            lane.limparInimigosInativos();
        }
    }
}