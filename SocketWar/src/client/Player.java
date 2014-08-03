package client;

/**
 * Essa classe representa um objeto Jogador.
 * Ela é chamada pela classe Game, para criar os 2 jogadores.
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

public class Player {
    
    public int id; // id do Player
    public int x; // x 
    public int y; // y
    public int hp = 10; // vida do jogador
    public boolean canShoot = true; // variável que serve para limitar uma quantidade de tiros muito grande, dando um certo atraso. O jogador pode atirar?
    private int shootTime = 20; // tempo de atraso para atirar em sequencia. Trabalha junto com a flag canShoot.
    public Shape body; // representa o corpo físico do jogador. Usado para testar colisões.
    
    private Image grafico;// representa a imagem que será mostrada pelo objeto jogador (a nave)
    private int bodyWidth = 50; // tamanho do body
    private int bodyHeight = 80;
    
    public Player(int id, int x, int y){
        this.id = id;
        this.x = x + 32;
        this.y = y + 8;
        try {
            init(); // chama o método init
        } catch (Exception e) {
        }
    }
    
    /**
     * Método de inicialização, chamado pelo construtor.
     * Configura o objeto player.
     * @throws SlickException 
     */
    public void init() throws SlickException{
        if(id == 0){ // se o id for 0, ou seja é o player0
            grafico = new Image("assets/nave0.png"); // configura a imagem com a nave virada para a direita >
        } else { // se não, ou seja, é o player1
            grafico = new Image("assets/nave1.png");// configura a imagem com a nave virada para a esquerda <
        }
        
        body = new Rectangle(x, y, bodyWidth, bodyHeight); // inicializa o body
        
    }
    
    int counter = 0; // contador para o atraso de tiro em sequências. trabalha junto com a flag canShoot e o shootTime
    /**
     * Método update da classe Player. Ele é chamado pelo update de Game.java, ou seja
     * esse método também é executado 60 vezes por segundo.
     */
    public void update(GameContainer gc, StateBasedGame sbg, int delta){
        // controle do atraso de tiros
        if(!canShoot){  // se a flag estiver falsa, ou seja, o jogador não pode atirar no momento
            counter++; // então incrementa o contador
            
            if(counter > shootTime){ // quando o contador passar do tempo necessário, então habilitar o jogador a atirar de novo
                canShoot = true; // habilitar tiros
                counter = 0; // setar o contador para 0
            }
            
        }
        //---------------------------
        // fazer o body acompanhar o x e o y
        body.setX(x);
        body.setY(y);
    }
    
    /**
     * Método de pintura do player, chamado pelo Game.java.
     */
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g){
        g.setColor(Color.red);
        grafico.draw(x-32, y - 8);
        //g.draw(body);
    }
    
}
