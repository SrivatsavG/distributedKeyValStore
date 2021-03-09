/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wlloyd
 */
public class GenericNode {

    /**
     * @param args the command line arguments
     */

    static boolean stopped = false;
    static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {

        if (args.length > 0) {
            if (args[0].equals("tc")) {
                //READ INPUT
                //System.out.println("TCP CLIENT");
                String addr = args[1];
                int port = Integer.parseInt(args[2]);
                String cmd = args[3];
                String key = (args.length > 4) ? args[4] : "";
                String val = (args.length > 5) ? args[5] : "";
                InputTCP input = new InputTCP(cmd, key, val);
                try {
                    TCPClient tcpClient = new TCPClient(addr, port, input.toString());
                    String serverResponse = tcpClient.connect();
                    System.out.println(serverResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("ts")) {
                //java -jar GenericNode.jar ts <server port number> <membership-server-IP> <membership-server-port> 
                int port = Integer.parseInt(args[1]);
                String membershipServerIP = args[2];
                int membershipServerPort = Integer.parseInt(args[3]);

                try ( ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Connected To server");
                    
                    //Create Key val store object
                    KeyValStore kv = new KeyValStore();

                    //Connect to membership server and Send ip and port to membership server
                    TCPClient tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.write + " " + "localhost" + " " + port);
                    String membershipServerResponse = tcpClient.connect();
                    System.out.println(membershipServerResponse);
                    
                    System.out.println("Starting to listen");
                    
                    //Listen to clients
                    TCPServer tcpServer = new TCPServer();
                    tcpServer.startThreads(port,serverSocket,kv,membershipServerIP, membershipServerPort);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("ms")) {

                int port = Integer.parseInt(args[1]);

                try ( ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Connected To Membership server");

                    //Create MembershipList object
                    MembershipList ml = new MembershipList();
                    
                    //Listen to clients
                    TCPServer tcpServer = new TCPServer();
                    tcpServer.startThreads(serverSocket,ml);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ONLY TCP SERVER and CLIENT ALLOWED. PLEASE USE TC or TS");
            }
        } else {
            String msg = "GenericNode Usage:\n\n"
                    + "Client:\n"
                    + "uc/tc <address> <port> put <key> <msg>  UDP/TCP CLIENT: Put an object into store\n"
                    + "uc/tc <address> <port> get <key>  UDP/TCP CLIENT: Get an object from store by key\n"
                    + "uc/tc <address> <port> del <key>  UDP/TCP CLIENT: Delete an object from store by key\n"
                    + "uc/tc <address> <port> store  UDP/TCP CLIENT: Display object store\n"
                    + "uc/tc <address> <port> exit  UDP/TCP CLIENT: Shutdown server\n"
                    + "rmic <address> put <key> <msg>  RMI CLIENT: Put an object into store\n"
                    + "rmic <address> get <key>  RMI CLIENT: Get an object from store by key\n"
                    + "rmic <address> del <key>  RMI CLIENT: Delete an object from store by key\n"
                    + "rmic <address> store  RMI CLIENT: Display object store\n"
                    + "rmic <address> exit  RMI CLIENT: Shutdown server\n\n"
                    + "Server:\n"
                    + "us/ts <port>  UDP/TCP SERVER: run udp or tcp server on <port>.\n"
                    + "rmis  run RMI Server.\n";
            System.out.println(msg);
        }
    }
}
