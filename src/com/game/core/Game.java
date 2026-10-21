package com.game.core;

import com.game.factory.EnemyFactory;
import com.game.model.Enemy;
import com.game.model.Player;
import com.game.observer.GameObserver;
import com.game.observer.TerminalView;
import com.game.strategy.AtaqueDireto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player player;
    private List<Lane> lanes;
    private int rodadaAtual;
    private boolean emAndamento;
    private GameObserver observer;
    private Scanner scanner;
    private Random random;

    public Game() {
        this.observer = new TerminalView();
        // Jogador começa na Lane 1
        this.player = new Player(new AtaqueDireto());
        this.player.moverParaLane(1); 

        this.lanes = new ArrayList<>();
        // Cria as lanes com IDs 1, 2 e 3 (índices 0, 1 e 2 na lista)
        for (int i = 1; i <= 3; i++) {
            lanes.add(new Lane(i));
        }

        this.rodadaAtual = 0;
        this.emAndamento = true;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void iniciarJogo() {
        observer.atualizar("=== BEM-VINDO AO LANE-BASED COMBAT GAME ===");

        // Spawn inicial
        gerarInimigosAleatorios();

        while (emAndamento) {
            rodadaAtual++;
            atualizarLoop();
            verificarCondicoesVitoriaDerrota();

            if (emAndamento) {
                aguardarAcaoJogador();
            }
        }

        scanner.close();
    }

    public void atualizarLoop() {
        System.out.println("\n========================================================");
        System.out.println(" Rodada: " + rodadaAtual + " | Vida do Jogador: " + player.getVida());
        System.out.println("========================================================");
        System.out.println("      | Lane 1     | Lane 2     | Lane 3     |");
        System.out.println("--------------------------------------------------------");

        // Atualiza a movimentação e limpa os inativos em cada lane
        for (Lane lane : lanes) {
            for (Enemy enemy : lane.getInimigos()) {
                enemy.avancar();

                // Se chegou na posição 0, causa dano ao jogador
                if (enemy.getPosicao() <= 0.0) {
                    player.receberDano(10);
                    observer.atualizar("Alerta! Um inimigo na Lane " + lane.getId() + " alcançou o jogador!");
                }
            }
            lane.limparInimigosInativos();
        }

        // Renderiza o tabuleiro
        for (int pos = 10; pos >= 0; pos--) {
            StringBuilder linhaVisual = new StringBuilder();

            for (int idLane = 0; idLane < 3; idLane++) {
                Lane lane = lanes.get(idLane);
                String celula = "    .    ";

                for (Enemy enemy : lane.getInimigos()) {
                    int posInimigo = (int) Math.round(enemy.getPosicao());
                    if (posInimigo == pos) {
                        celula = "  [ * ]  ";
                    }
                }
                linhaVisual.append(String.format(" %-10s |", celula));
            }
            System.out.println(String.format("%2d    |%s", pos, linhaVisual.toString()));
        }

        System.out.println("--------------------------------------------------------");

        StringBuilder linhaJogador = new StringBuilder();
        for (int idLane = 0; idLane < 3; idLane++) {
            // idLane 0 corresponde à Lane 1 visual
            int laneVisual = idLane + 1;
            String celulaJogador = (laneVisual == player.getLaneAtual()) ? " [ \\0/ ]   " : "           ";
            linhaJogador.append(String.format(" %-10s|", celulaJogador));
        }
        System.out.println(String.format(" J    |%s", linhaJogador.toString()));
        System.out.println("========================================================");

        // Chance de nascer novos inimigos nas rodadas (Ondas contínuas)
        if (rodadaAtual % 2 == 0) {
            gerarInimigosAleatorios();
        }
    }

    private void gerarInimigosAleatorios() {
        int laneSorteada = random.nextInt(3) + 1; // Sorteia entre 1, 2 e 3
        Enemy novoInimigo = EnemyFactory.criarInimigo("comum");
        
        // Insere na lista correta (Lane 1 vai para o índice 0, etc.)
        lanes.get(laneSorteada - 1).adicionarInimigo(novoInimigo);
        observer.atualizar("Um novo inimigo comum apareceu na Lane " + laneSorteada + "!");
    }

    private void aguardarAcaoJogador() {
        System.out.println("\n--- CONTROLES DO JOGO ---");
        System.out.println(" [1] Mover para Lane 1");
        System.out.println(" [2] Mover para Lane 2");
        System.out.println(" [3] Mover para Lane 3");
        System.out.println(" [0] ATACAR na Lane atual (" + player.getLaneAtual() + ")");
        System.out.print("Digite sua escolha: ");

        if (scanner.hasNextInt()) {
            int opcao = scanner.nextInt();

            if (opcao >= 1 && opcao <= 3) {
                player.moverParaLane(opcao); // Atualiza para a lane 1, 2 ou 3
                observer.atualizar("Jogador moveu-se para a Lane " + opcao);
            } else if (opcao == 0) {
                // Pega a lane correspondente na lista interna (Lane 1 -> índice 0)
                int indiceLista = player.getLaneAtual() - 1;
                Lane laneAtualObj = lanes.get(indiceLista);
                player.atacar(laneAtualObj);
                laneAtualObj.limparInimigosInativos();
            } else {
                System.out.println("Opção inválida! Perdeu a vez.");
            }
        }
    }

    public void verificarCondicoesVitoriaDerrota() {
        if (player.getVida() <= 0) {
            observer.atualizar("FIM DE JOGO: O jogador foi derrotado pelos inimigos!");
            emAndamento = false;
        }
    }
}