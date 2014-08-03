package client;

/**
 * Essa classe inicia os trabalhos com o Slick2d.
 * Ela não é a principal classe do jogo, mas configura e chama a principal,
 * que é a classe Game.java.
 * 
 */

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Main extends StateBasedGame {

    //------------------------------------------------------------------- Config
    public static final String gamename = "Socket War 1.0";
    public static int width = 850; // largura do jogo
    public static int height = 450; // altura do jogo
    public static int fps = 60; // variável usada para limitar os frames por segundo
    //------------------------------------------------------------------- States
    public static final int game = 0;

    public Main(String gamename) {
        super(gamename);
        this.addState(new Game(game)); // adiciona o estado game, o único do jogo. Preparando uso da classe Game, a principal do jogo.      
        
    }

    /**
     * Esse método inicia a lista de estados, como existe apenas 1, o game, ele prepara
     * o jogo para rodar esse estado.
     * @param gc
     * @throws SlickException 
     */
    
    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        this.getState(game).init(gc, this);
        this.enterState(game);
    }

    /**
     * O método main desta classe faz algumas configurações importantes para o jogo.
     * @param args 
     */
    public static void main(String[] args) {
        AppGameContainer appgc;
        try {
            appgc = new AppGameContainer(new Main(gamename));
            appgc.setDisplayMode(width, height, false); // configura o tamanho da tela e desabilita full screen.
            appgc.setTargetFrameRate(fps); // limita quantidade de FPS
            appgc.setShowFPS(false); // configura para que o fps não seja mostrado na tela do jogo
            appgc.setForceExit(false); // diz que a aplicação não vai ser abortada após o jogador fechar a tela do jogo.
            appgc.start(); // inicia o app game container
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
}
