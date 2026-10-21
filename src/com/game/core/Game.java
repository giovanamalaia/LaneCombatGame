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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static final int INIMIGOS_PARA_VENCER = 15;

    private Player player;
    private List<Lane> lanes;
    private int rodadaAtual;
    private boolean emAndamento;
    private GameObserver observer;
    private Scanner scanner;
    private Random random;
    private String ultimoTipoGerado = "";
    public Game() {
        this(System.in, new Random());
    }

    public Game(InputStream entrada, Random random) {
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
        this.scanner = new Scanner(entrada);
        this.random = random;
    }

    public Player getPlayer() { return player; }

    public List<Lane> getLanes() { return lanes; }

    public boolean isEmAndamento() { return emAndamento; }

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
        avancarInimigos();

        if (rodadaAtual % 2 == 0) {
            gerarInimigosAleatorios();
        }

        System.out.println("\n========================================================");
        System.out.println(" Rodada: " + rodadaAtual + " | Vida do Jogador: " + player.getVida() + " | Energia: " + player.getEnergia() + "% | Abates: " + getInimigosAbatidos() + "/" + INIMIGOS_PARA_VENCER);
        System.out.println("========================================================");
        System.out.print(renderizarTabuleiro());

        // a cada rodada o jogador acumula 25% de energia
        player.carregarEnergia(25);
    }

    public void avancarInimigos() {
        for (Lane lane : lanes) {
            for (Enemy enemy : lane.getInimigos()) {
                enemy.avancar();

                // se chegou na posição 0 causa o dano do proprio inimigo ao jogador
                if (enemy.getPosicao() <= 0.0 && !enemy.estaMorto()) {
                    player.receberDano(enemy.getDano());
                    observer.atualizar("Alerta! Um inimigo na Lane " + lane.getId() + " alcançou o jogador!");
                }
            }
            lane.limparInimigosInativos();
        }
    }

    public String renderizarTabuleiro() {
        StringBuilder sb = new StringBuilder();
        sb.append("      | Lane 1     | Lane 2     | Lane 3     |\n");
        sb.append("--------------------------------------------------------\n");

        for (int pos = 10; pos >= 0; pos--) {
            StringBuilder linhaVisual = new StringBuilder();

            for (int idLane = 0; idLane < 3; idLane++) {
                Lane lane = lanes.get(idLane);
                String simbolo = null;
                int quantidade = 0;

                for (Enemy enemy : lane.getInimigos()) {
                    if ((int) Math.round(enemy.getPosicao()) == pos) {
                        quantidade++;
                        if (simbolo == null) {
                            if (enemy instanceof InimigoRapido) {
                                simbolo = "[ >> ]";
                            } else if (enemy instanceof InimigoTanque) {
                                simbolo = "[ ** ]";
                            } else {
                                simbolo = "[ * ]";
                            }
                        }
                    }
                }

                String celula = ".";
                if (simbolo != null) {
                    celula = quantidade > 1 ? simbolo + "x" + quantidade : simbolo;
                }
                linhaVisual.append(String.format(" %-10s |", celula));
            }
            sb.append(String.format("%2d    |%s%n", pos, linhaVisual));
        }

        sb.append("--------------------------------------------------------\n");

        StringBuilder linhaJogador = new StringBuilder();
        for (int idLane = 0; idLane < 3; idLane++) {
            String celulaJogador = (idLane + 1 == player.getLaneAtual()) ? "\\0/" : "";
            linhaJogador.append(String.format(" %-10s |", celulaJogador));
        }
        sb.append(String.format(" J    |%s%n", linhaJogador));
        sb.append("========================================================\n");
        return sb.toString();
    }

    public void gerarInimigosAleatorios() {
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

        // evita dois tanques seguidos
        if (tipoStr.equals("tanque") && ultimoTipoGerado.equals("tanque")) {
            tipoStr = "comum";
        }
        ultimoTipoGerado = tipoStr;
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

        if (!scanner.hasNext()) {
            emAndamento = false;
            return;
        }

        if (!scanner.hasNextInt()) {
            scanner.next();
            System.out.println("Opção inválida! Perdeu a vez.");
            return;
        }

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

    public void verificarCondicoesVitoriaDerrota() {
        if (player.getVida() <= 0) {
            observer.atualizar("FIM DE JOGO: O jogador foi derrotado pelos inimigos!");
            emAndamento = false;
        } else if (getInimigosAbatidos() >= INIMIGOS_PARA_VENCER) {
            observer.atualizar("VITORIA! Voce derrotou " + INIMIGOS_PARA_VENCER + " inimigos!");
            emAndamento = false;
        }
    }

    public int getInimigosAbatidos() {
        int total = 0;
        for (Lane lane : lanes) {
            total += lane.getAbatidos();
        }
        return total;
    }
}