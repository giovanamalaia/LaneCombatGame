package com.game.model;

public class InimigoComum extends Enemy {
    public InimigoComum() {
        this.vida = 100;
        this.velocidade = 1.0;
        this.dano = 10;
    }

    @Override
    public void avancar() {
        posicao -= velocidade;
    }
}