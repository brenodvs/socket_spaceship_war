package client;

/**
 * Essa classe é a principal do jogo. Tudo que acontece no jogo é controlado por
 * essa classe.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends BasicGameState {

    //----------------------------------------------------- NET
    private Socket socket = References.socket; // socket cliente
    private DataInputStream in; // canal de entrada
    private DataOutputStream out; // canal de saida
    //variaveis para movimentação do adversário(net)
    private boolean net_CIMA = false;
    private boolean net_BAIXO = false;
    private boolean net_TIRO = false;
    //---------------------------------------------------------
    private boolean initialized = false; // flag usada para contornar um erro do slick, que chama o método initialize 2 vezes.
    private Image bg; // esse objeto representa a imagem de fundo que será usada no jogo.
    private Image gameOver; // esse objeto representa a imagem que sera mostrada quando o jogo acabar. Popup.
    private String gameOver_message; // string que recebe a mensagem que aparecerá junto com o popup gameOver.
    private Player player0; // instância da classe Player, representando 1 dos 2 jogadores.
    private Player player1; // instância da classe Player, representando 1 dos 2 jogadores.
    // O Player é também a nave.
    private int playersSpeed = 5; // int para configurar a velocidade do jogo facilmente mudando o valor dessa variável.
    private TrueTypeFont font2; // essa classe representa a fonte que será usada para exibir o texto de String gameOver_message
    private boolean flag_gameOver = false; // flag para dizer quando o jogo acabou
    private ArrayList<Tiro> tiros = new ArrayList<Tiro>(); // array que receberá todos os tiros adicionados por ambos os jogadores no cenário

    public Game(int state) {
    }

    /**
     * Método init, representa o load do game. Tudo que é necessário sera
     * inicializado aqui.
     *
     * @param gc
     * @param sbg
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (!initialized) {

            initRedes(); // chama metodo que configura as propriedades de rede

            // bg
            bg = new Image("assets/bg.png"); // configura o bg para receber a imagem.

            // game over window
            gameOver = new Image("assets/gameOver.png"); // configura o gameOver para receber a imagem do popup

            player0 = new Player(0, 0, 180); // instancia o player 0, passando o id, o x, e o y.
            player1 = new Player(1, 730, 180); // instancia o player 1, passando o id, o x, e o y.

            initialized = true; // usando a flag que contorna o erro do init do Slick2d

            font2 = new TrueTypeFont(new java.awt.Font(java.awt.Font.SERIF, java.awt.Font.BOLD, 28), false); // inicializa a fonte
        }
    }

    /**
     * Esse metodo inicia os canais de saida e entrada, e cria uma Thread da Classe Escuta Servidor
     */
    public void initRedes() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            new Thread(new EscutaServidor()).start();
        } catch (Exception e) {
        }
    }


    /*
     * Update, o coração do jogo.
     * Esse método será chamado 60 vezes por segundo, e nele deve conter todas as regras do jogo.
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        if (!flag_gameOver) { // só executa o "coração do jogo" quando o jogo não tiver terminado
            inputManager(gc.getInput()); // chama o método inputManager, que recebe os inputs dos jogadores
            netInputManager(gc.getInput()); // chama o método de input do adversário, que faz a movimentação dele baseado em flags setadas pelos sockets

            // corrigir posições
            /*
             * esses códigos limitam os movimentos do player na tela, fazendo ele trancar na altura,y.
             * assim, a nave irá se mexer apenas até os limites da tela.
             */
            if (player0.y < - 30) {
                player0.y = - 30;
            }
            if (player0.y > 400) {
                player0.y = 400;
            }

            if (player1.y < - 30) {
                player1.y = - 30;
            }
            if (player1.y > 400) {
                player1.y = 400;
            }
            //-------------------------------------------
            player0.update(gc, sbg, delta); // chama o update do player0
            player1.update(gc, sbg, delta); // chama o update do player1

            // tiros movimentação e limpeza---------------------
            /*
             * Chama o update de todos os tiros que estão no array de tiros, para
             * que eles se movimentem corretamente na tela.
             */
            for (int i = 0; i < tiros.size(); i++) {
                tiros.get(i).update(gc, sbg, delta);
            }

            /*
             * Verifica os tiros que ultrapassaram os limites da tela e não são mais úteis,
             * e então os remove.
             */
            for (int i = 0; i < tiros.size(); i++) {
                if (tiros.get(i).x >= -50 && tiros.get(i).x <= 900) {
                } else {
                    tiros.remove(i);
                    break;
                }
            }

            // colisões -------------------------------------
            /*
             * Testa a colisão de todos os tiros com cada player.
             * A colisão é testada com uma propriedade body, tanto do tiro quanto do player.
             * Essa propriedade body é um retângulo invisível, que representa a área onde o objeto
             * pode colidir-se com outro.
             * É o corpo físico do objeto.
             */
            for (int i = 0; i < tiros.size(); i++) {
                if (tiros.get(i).body.intersects(player0.body)) { // testa colisão com player 0, se tiver colisão: \/
                    player0.hp--; // decrementa a vida do player
                    tiros.remove(i); // remove o tiro do array tiros
                    break;
                }

                if (tiros.get(i).body.intersects(player1.body)) {// testa colisão com player 1, se tiver colisão: \/
                    player1.hp--;// decrementa a vida do player
                    tiros.remove(i); // remove o tiro do array tiros
                    break;
                }
            }

            // testar fim do jogo-------------------------------
            if (player0.hp < 0) { // se o player 0 tem vida menor que 0, ele perdeu
                flag_gameOver = true; // seta a flag de fim de jogo para verdadeiro
                gameOver_message = "Você perdeu!"; // configura a mensagem dizendo que o outro jogador venceu
            } else if (player1.hp < 0) { // se o player 1 tem vida menor que 0, ele perdeu
                flag_gameOver = true; // seta a flag de fim de jogo para verdadeiro
                gameOver_message = "Você venceu!"; // configura a mensagem dizendo que o outro jogador venceu
            }

        } else { // se a flag que o jogo acabou estiver habilitada
            if (gc.getInput().isMousePressed(0)) {
                if (gc.getInput().getMouseX() > 540
                        && gc.getInput().getMouseX() < 660
                        && gc.getInput().getMouseY() > 290
                        && gc.getInput().getMouseY() < 322) { // testa clique em uma área específica, que representa o botão
                    // se tiver este clique
                    System.gc(); // fecha o jogo
                    gc.exit();

                }
            }
        }




    }

    //------------------------------ diminuir numero de mensages
    private boolean tecla_cima_flag = false;
    private boolean tecla_baixo_flag = false;
    //----------------------------------------------------------
    public void inputManager(Input input) {
        // ====================== player0 (Na verdade o Player 1)
        if (input.isKeyDown(Input.KEY_DOWN)) { // se esta tecla tiver apertada
            player0.y += playersSpeed; // faz o player0 mover para baixo
            
            if(!tecla_baixo_flag){
                tecla_baixo_flag = true;
                try {
                    out.writeUTF("baixo"); // envia mensagem
                } catch (Exception ex) {
                }
            }

        } else if (input.isKeyDown(Input.KEY_UP)) { // se esta tecla tiver apertada
            player0.y -= playersSpeed; // faz o player0 mover para a cima

            if(!tecla_cima_flag){
                tecla_cima_flag = true;
                try {
                    out.writeUTF("cima"); // envia mensagem
                } catch (Exception ex) {
                }
            }
            
        }

        if (input.isKeyPressed(Input.KEY_SPACE)) { // se esta tecla tiver apertada
            if (player0.canShoot) { // ve se o jogador ja tem disponibilidade para atirar
                player0.canShoot = false; // diz que o jogador acabou de atirar
                tiros.add(new Tiro(player0.id, player0.x + 50, player0.y + 32)); // adiciona um objeto tiro ao array tiros
                // configura esse objeto passando o id do jogador que atirou, e uma posição referente ao jogador

                try {
                    out.writeUTF("tiro"); // envia mensagem
                } catch (Exception ex) {
                }

            }
        }
        //
        if (!input.isKeyDown(Input.KEY_DOWN)) { // se esta tecla tiver apertada
            if(tecla_baixo_flag){
                tecla_baixo_flag = false;
                try {
                    out.writeUTF("dbaixo"); // envia mensagem (d = drop, essa mensagem diz que o jogador cliente soltou determiada tecla)
                } catch (Exception ex) {
                }
            }

        }
        if (!input.isKeyDown(Input.KEY_UP)) { // se esta tecla tiver apertada
            if(tecla_cima_flag){
                tecla_cima_flag = false;
                try {
                    out.writeUTF("dcima");// envia mensagem (d = drop, essa mensagem diz que o jogador cliente soltou determiada tecla)
                } catch (Exception ex) {
                }
            }    
        }



    }

    /**
     * Este método controla os inputs do adversário. Dados são recebidos via
     * sockets, e setam as flags. Essas flags são interpretadas nesse método.
     *
     * @param input
     */
    public void netInputManager(Input input) {
        // ======================= player1 (Na verdade o Player 2, adversário)
        if (net_BAIXO) { // movimento para baixo se a flag for verdadeira
            player1.y += playersSpeed;
        } else if (net_CIMA) { // movimento para cima se a flag for verdadeira
            player1.y -= playersSpeed;
        }

        if (net_TIRO) { // atirar se a flag for verdadeira
            if (player1.canShoot) {
                player1.canShoot = false;
                tiros.add(new Tiro(player1.id, player1.x - 30, player1.y + 32));
            }
        }
        
        net_TIRO = false;

    }

    /*
     * Método que pinta tudo que tem na tela, é chamado muitas vezes por segundo, acompanhando o update.
     * Ou seja, o que o update atualiza, o render ja pinta para dar a impressão de movimento.
     * 
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
        bg.draw(0, 0); // desenha o fundo do jogo
        player0.render(gc, sbg, grphcs); // chama o metodo render do player 0
        player1.render(gc, sbg, grphcs); // chama o metodo render do player 1

        for (int i = 0; i < tiros.size(); i++) { // chama o metodo render de todos os tiros
            tiros.get(i).render(gc, sbg, grphcs);
        }

        if (flag_gameOver) { // se a flagGameover estiver verdadeira é pq o jogo acabou, então\/

            gameOver.draw(0, 0); // desenha o popup de game over
            font2.drawString(420, 175, gameOver_message); // desenha a mensagem encima do popup

        }

    }

    @Override
    public int getID() {
        return 0;
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    private class EscutaServidor implements Runnable {

        /*
         * Sobrescreve metodo run
         */
        @Override
        public void run() {

            String s;

            try {
                while (true) {
                    s = in.readUTF();

                    /**
                     * Identifica a mensagem que veio, para setar as flags que controlam o adversário corretamente
                     */
                    if (s.equals("baixo")) {
                        net_CIMA = false;
                        net_BAIXO = true;
                    } else if (s.equals("cima")) {
                        net_CIMA = true;
                        net_BAIXO = false;
                    } else if (s.equals("tiro")) {
                        net_TIRO = true;
                    } else if(s.equals("dcima")){
                        net_CIMA = false;
                    } else if(s.equals("dbaixo")){
                        net_BAIXO = false;
                    }


                }
            } catch (Exception ex) {
            }
        }
    }
}
