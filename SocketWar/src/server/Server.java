/**
 * Classe que representa o servidor de aplicação do jogo SocketWar.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static int counter = 0; // contador de conexoes
    private ArrayList<NetPlayer> netPlayers = new ArrayList<NetPlayer>(); // array de objetos classe NetPlayer, que possui as propriedades de um cliente
    
    public Server() {
        ServerSocket server;
        DataInputStream in; 

        System.out.println("ServerSocket inicializado na porta 5000.");
        
        try {
            server = new ServerSocket(5000); 
            while (true) { // loop infinito
                if(counter < 2){ // limita quantidade de conexoes
                    Socket socket = server.accept(); // aceita conexao, apartir daqui o codigo continua se a conexao foi efetuada
                    // \/ \/ \/ \/
                    NetPlayer aux = new NetPlayer(counter, socket); // cria objeto da classe NetPlayer pro cliente que se conectou
                    System.out.println(" *** Player " + counter + " se conectou.");
                    counter++; // incrementa contador de clientes
                    netPlayers.add(aux); // adiciona ao ArrayList
                    new Thread(new Server.EscutaCliente(aux)).start(); // cria uma thread da classe EscutaCliente e starta
                    
                    if(counter == 2){ // se o numero de jogadores atual for 2
                        System.out.println("Servidor cheio. Nenhum cliente a mais poderá se conectar;\n----------------------");
                        encaminharParaTodos("start"); // manda ambos startarem o jogo
                    }
                }
            }
        } catch (Exception ex) {
        }
        
    }                                    
    
    /**
     * Método que encaminha para todos os clientes determinada mensagem
     * @param texto 
     */
    private void encaminharParaTodos(String texto){
        try {
            for(NetPlayer obj : netPlayers){
                obj.out.writeUTF(texto);
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * Classe EscutaCliente. Cada cliente tera uma thread executando aqui.
     * Essa classe ouve a mensagem dos clientes, e envia apenas para o outro cliente(adversario).
     */
    private class EscutaCliente implements Runnable {

        NetPlayer np;

        public EscutaCliente(NetPlayer np) {
            this.np = np;
        }

        /**
         * Sobrescrevendo metodo run
         */
        @Override
        public void run() {
            String s = "";
            try {
                while (true) {
                    s = np.in.readUTF(); // le a mensagem
                    if(np.id == 0){ // se o cliente desta thread for o 0
                        netPlayers.get(1).out.writeUTF(s); // envia a mensagem para o 1
                    } else { // se nao, o cliente é o 1
                        netPlayers.get(0).out.writeUTF(s); // envia mensagem para o 0
                    }
                    // DEBUG  System.out.println("id " + np.id + " :" + s);
                }
            } catch (Exception ex) {
            }
        }
    }
    
    
    public static void main(String args[]) {
        new Server(); // metodo main chama o construtor da classe Server
    }
           
}
