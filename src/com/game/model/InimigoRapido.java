package com.game.model;

public class InimigoRapido extends Enemy {
    public InimigoRapido() {
        this.vida = 50;
        this.velocidade = 2.0; // anda duas vezes mais rapido
        this.dano = 5;
    }

    @Override
    public void avancar() {
        posicao -= velocidade;
    }
}