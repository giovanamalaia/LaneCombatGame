package com.game.model;

public class InimigoTanque extends Enemy {
    public InimigoTanque() {
        this.vida = 250; // mais resistente
        this.velocidade = 0.5; // mais lento
        this.dano = 20;
    }

    @Override
    public void avancar() {
        posicao -= velocidade;
    }
}