package com.game.model;

public abstract class Enemy {
    protected double posicao = 10.0; 
    protected int vida;
    protected double velocidade;
    protected int dano;

    public abstract void avancar();

    public void receberDano(int quantidade) {
        this.vida -= quantidade;
    }

    public boolean estaMorto() {
        return this.vida <= 0;
    }

    public double getPosicao() { return posicao; }
}