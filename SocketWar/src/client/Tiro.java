package client;

/**
 * A classe Tiro representa um objeto tiro.
 * São criados objetos dessa classe infinitamente.
 * Ambos os jogadores dão tiros.
 * Um array na classe Game armazena todos os tiros, ou seja, vários objetos desta classe.
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

public class Tiro {
    
    public int playerId; // o id do jogador que deu o tiro
    public int x; // x
    public int y; // y
    public Shape body; // corpo fisico do tiro, usado para colisões
    
    private int speed = 5; // velocidade do tiro
    private Image grafico; // imagem gráfica do tiro
    private int bodyWidth = 27; // tamanho do body
    private int bodyHeight = 14;
    
    public Tiro(int playerId, int x, int y){
        this.playerId = playerId; // recebe o jogador que deu o tiro. A classe Game sempre sabe quem deu o tiro, por isso quando cria o tiro, passa esse parametro corretamente.
        this.x = x;
        this.y = y;
        try {
            init();
        } catch (Exception e) {
        }
    }
    
    /**
     * Método init do tiro, chamado pelo construtor
     * @throws SlickException 
     */
    public void init() throws SlickException{
        grafico = new Image("assets/tiro.png"); // configura a imagem do tiro
        
        body = new Rectangle(x, y, bodyWidth, bodyHeight); // configura o corpo fisico
        
    }
    
    
    /**
     * Método update do tiro. É chamado pela classe Game, ou seja executa 60 vezes por segundo.
     * Todos os tiros são atualizados pela classe Game.
     * 
     * @param gc
     * @param sbg
     * @param delta 
     */
    public void update(GameContainer gc, StateBasedGame sbg, int delta){
        if(playerId == 0) // se o tiro pertencer ao jogador 0
            this.x += speed; // faz o tiro se mover constantemente para a direita =>
        else // senão, ou seja pertence ao jogador 1
            this.x -= speed; // faz o tiro se mover constantemente para a esquerda <=
        
        // faz o corpo se mover com o x e o y
        body.setX(x);
        body.setY(y);
    }
    
    /**
     * Método de pintura do tiro, chamado pela classe Game
     * 
     * @param gc
     * @param sbg
     * @param g 
     */
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g){
        g.setColor(Color.red);
        grafico.draw(x, y);
        //g.draw(body);
    }
    
}
