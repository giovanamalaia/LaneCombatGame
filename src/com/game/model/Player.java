package com.game.model;
import com.game.core.Lane;
import com.game.strategy.AttackStrategy;

public class Player {
    private int vida = 100;
    private int laneAtual = 1; // 0, 1 ou 2 (3 lanes)
    private AttackStrategy attackStrategy;

    public Player(AttackStrategy strategyInicial) {
        this.attackStrategy = strategyInicial;
    }

    public void setAttackStrategy(AttackStrategy strategy) {
        this.attackStrategy = strategy;
    }

    public void atacar(Lane lane) {
        attackStrategy.atacar(lane);
    }

    public void moverParaLane(int novaLane) {
        this.laneAtual = novaLane;
    }

    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida < 0) {
            this.vida = 0;
        }
    }

    public int getVida() { 
        return vida; 
    }
    
    public int getLaneAtual() { 
        return laneAtual; 
    }
}