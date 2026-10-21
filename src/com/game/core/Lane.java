package com.game.core;
import com.game.model.Enemy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lane {
    private int id;
    private List<Enemy> inimigos;
    private int abatidos;
    public Lane(int id) {
        this.id = id;
        this.inimigos = new ArrayList<>();
    }

    public void adicionarInimigo(Enemy enemy) {
        inimigos.add(enemy);
    }

    public List<Enemy> getInimigos() { 
        return inimigos; 
    }

    public int getId() { 
        return id; 
    }

    public int getAbatidos() {
        return abatidos;
    }

    public void limparInimigosInativos() {
        Iterator<Enemy> iterator = inimigos.iterator();
        while (iterator.hasNext()) {
            Enemy e = iterator.next();
            if (e.estaMorto() || e.getPosicao() <= 0.0) {
                if (e.estaMorto()) {
                    abatidos++;
                }
                iterator.remove();
            }
        }
    }
}