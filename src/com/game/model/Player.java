package com.game.model;
import com.game.core.Lane;
import com.game.strategy.AttackStrategy;

public class Player {
    private int vida = 100;
    private int laneAtual = 1; // 1, 2 ou 3
    private int energia = 0; // De 0 a 100%
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

    // gerenciamento da energia 
    public void carregarEnergia(int quanto) {
        this.energia += quanto;
        if (this.energia > 100) {
            this.energia = 100;
        }
    }

    public void gastarEnergia() {
        this.energia = 0;
    }

    public boolean podeUsarEspecial() {
        return this.energia >= 100;
    }

    public int getVida() { 
        return vida; 
    }
    
    public int getLaneAtual() { 
        return laneAtual; 
    }

    public int getEnergia() {
        return energia;
    }
}