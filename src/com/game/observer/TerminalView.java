package com.game.observer;

public class TerminalView implements GameObserver {
    @Override
    public void atualizar(String mensagem) {
        System.out.println("[CONSOLE LOG] " + mensagem);
    }
}