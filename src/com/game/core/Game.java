package com.game.core;

import com.game.factory.EnemyFactory;
import com.game.model.Enemy;
import com.game.model.InimigoRapido;
import com.game.model.InimigoTanque;
import com.game.model.Player;
import com.game.observer.GameObserver;
import com.game.observer.TerminalView;
import com.game.strategy.AtaqueDireto;
import com.game.strategy.AtaqueEmArea;

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
        // jogador comeca na lane do meio
        this.player = new Player(new AtaqueDireto());
        this.player.moverParaLane(1);

        this.lanes = new ArrayList<>();
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
        System.out.println(" Rodada: " + rodadaAtual + " | Vida do Jogador: " + player.getVida() + " | Energia: " + player.getEnergia() + "%");
        System.out.println("========================================================");
        System.out.println("      | Lane 1     | Lane 2     | Lane 3     |");
        System.out.println("--------------------------------------------------------");

        for (Lane lane : lanes) {
            for (Enemy enemy : lane.getInimigos()) {
                enemy.avancar();

                // se chegou na posição 0 causa dano ao jogador
                if (enemy.getPosicao() <= 0.0) {
                    player.receberDano(10);
                    observer.atualizar("Alerta! Um inimigo na Lane " + lane.getId() + " alcançou o jogador!");
                }
            }
            lane.limparInimigosInativos();
        }

        // renderiza o tabuleiro
        for (int pos = 10; pos >= 0; pos--) {
            StringBuilder linhaVisual = new StringBuilder();

            for (int idLane = 0; idLane < 3; idLane++) {
                Lane lane = lanes.get(idLane);
                String celula = "    .    ";

                for (Enemy enemy : lane.getInimigos()) {
                    int posInimigo = (int) Math.round(enemy.getPosicao());
                    if (posInimigo == pos) {
                        if (enemy instanceof InimigoRapido) {
                            celula = "  [ >> ] ";
                        } else if (enemy instanceof InimigoTanque) {
                            celula = "  [ ** ] ";
                        } else {
                            celula = "  [ * ]  ";
                        }
                    }
                }
                linhaVisual.append(String.format(" %-10s |", celula));
            }
            System.out.println(String.format("%2d    |%s", pos, linhaVisual.toString()));
        }

        System.out.println("--------------------------------------------------------");

        StringBuilder linhaJogador = new StringBuilder();
        for (int idLane = 0; idLane < 3; idLane++) {
            int laneVisual = idLane + 1;
            String celulaJogador = (laneVisual == player.getLaneAtual()) ? " [ \\0/ ]   " : "           ";
            linhaJogador.append(String.format(" %-10s|", celulaJogador));
        }
        System.out.println(String.format(" J    |%s", linhaJogador.toString()));
        System.out.println("========================================================");

        // a cada rodad o jogador acumula 25% de energia 
        player.carregarEnergia(25);

        // chance de adicionar novos inimigos nas rodadas
        if (rodadaAtual % 2 == 0) {
            gerarInimigosAleatorios();
        }
    }

    private void gerarInimigosAleatorios() {
        int laneSorteada = random.nextInt(3) + 1; 

        // sorteia tipo de inimigo
        int tipoChance = random.nextInt(100);
        String tipoStr = "comum";

        if (tipoChance < 55) {
            tipoStr = "comum";
        } else if (tipoChance < 80) {
            tipoStr = "rapido";
        } else {
            tipoStr = "tanque";
        }

        Enemy novoInimigo = EnemyFactory.criarInimigo(tipoStr);
        lanes.get(laneSorteada - 1).adicionarInimigo(novoInimigo);
        observer.atualizar("Um inimigo (" + tipoStr + ") apareceu na Lane " + laneSorteada + "!");
    }

    private void aguardarAcaoJogador() {
        System.out.println("\n--- CONTROLES DO JOGO ---");
        System.out.println(" [1] Mover para Lane 1");
        System.out.println(" [2] Mover para Lane 2");
        System.out.println(" [3] Mover para Lane 3");
        System.out.println(" [0] ATACAR Direto na Lane atual (" + player.getLaneAtual() + ")");

        boolean especialPronto = player.podeUsarEspecial();
        if (especialPronto) {
            System.out.println(" [9] ⭐ USAR ATAQUE EM ÁREA (ESPECIAL)");
        } else {
            System.out.println(" [9] 🔒 Ataque em Área (Carregando: " + player.getEnergia() + "%)");
        }

        System.out.print("Digite sua escolha: ");

        if (scanner.hasNextInt()) {
            int opcao = scanner.nextInt();

            if (opcao >= 1 && opcao <= 3) {
                player.moverParaLane(opcao); 
                observer.atualizar("Jogador moveu-se para a Lane " + opcao);
            } else if (opcao == 0) {
                player.setAttackStrategy(new AtaqueDireto());
                int indiceLista = player.getLaneAtual() - 1;
                Lane laneAtualObj = lanes.get(indiceLista);
                player.atacar(laneAtualObj);
                laneAtualObj.limparInimigosInativos();
            } else if (opcao == 9) {
                if (especialPronto) {
                    // ataque em área passando todas as lanes
                    player.setAttackStrategy(new AtaqueEmArea(lanes));
                    int indiceLista = player.getLaneAtual() - 1;
                    Lane laneAtualObj = lanes.get(indiceLista);
                    player.atacar(laneAtualObj);
                    
                    // zera a energia após o uso
                    player.gastarEnergia();
                    observer.atualizar("O jogador descarregou o Ataque em Área em todas as lanes!");
                } else {
                    System.out.println("\n⚠️ Energia insuficiente! O especial ainda não está pronto.");
                    aguardarAcaoJogador(); // permite jogar novamente neste turno
                }
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