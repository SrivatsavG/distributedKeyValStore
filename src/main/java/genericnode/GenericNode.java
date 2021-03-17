/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    static String MEMBERSHIPFILENAME = "/tmp/membership.txt";

    public static void main(String[] args) throws IOException {
        String myIP = InetAddress.getLocalHost().getHostAddress();
        if (args.length > 0) {
            if (args[0].equals("tc")) {
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
            } else if (args[0].equals("ts") && args.length == 2) { //SERVER FOR STATIC FILE
                //java -jar GenericNode.jar ts <server port number>
                int port = Integer.parseInt(args[1]);

                try ( ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Connected To server");

                    //Create Key val store object
                    KeyValStore kv = new KeyValStore();

                    //-----------------------------WRITE TO FILE------------------------------
                    writeToMembershipFile(port);

                    System.out.println("Starting to listen");

                    //Listen to clients
                    TCPServer tcpServer = new TCPServer();
                    tcpServer.startThreads(port, serverSocket, kv, MEMBERSHIPFILENAME);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (args[0].equals("ts") && args.length == 4) { //SERVER FOR DYNAMIC TCP MEMBERSHIP
                //java -jar GenericNode.jar ts <server port number> <membership-server-IP> <membership-server-port> 

                int port = Integer.parseInt(args[1]);
                String membershipServerIP = args[2];
                int membershipServerPort = Integer.parseInt(args[3]);

                try ( ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Connected To server");

                    //Create Key val store object
                    KeyValStore kv = new KeyValStore();

                    //Connect to membership server and Send ip and port to membership server
                    TCPClient tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.write + " " + myIP + " " + port);
                    String membershipServerResponse = tcpClient.connect();
                    System.out.println(membershipServerResponse);

                    System.out.println("Starting to listen");

                    //Listen to clients
                    TCPServer tcpServer = new TCPServer();
                    tcpServer.startThreads(port, serverSocket, kv, membershipServerIP, membershipServerPort);

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
                    tcpServer.startThreads(serverSocket, ml);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ONLY TCP SERVER and CLIENT ALLOWED. PLEASE USE TC or TS");
            }
        } else {
            String msg = "GenericNode Usage:\n\n"
                    + "Client:\n"
                    + "tc <address> <port> put <key> <msg>  TCP CLIENT: Put an object into store\n"
                    + "tc <address> <port> get <key> TCP CLIENT: Get an object from store by key\n"
                    + "tc <address> <port> del <key> TCP CLIENT: Delete an object from store by key\n"
                    + "tc <address> <port> store TCP CLIENT: Display object store\n"
                    + "tc <address> <port> exit  TCP CLIENT: Shutdown server\n"
                    + "Server:\n"
                    + "ts <port>  UDP/TCP SERVER: tcp server on <port>.\n";
            System.out.println(msg);
        }
    }

    public static void writeToMembershipFile(int port) throws IOException {
        Path pathOfLog = Paths.get(MEMBERSHIPFILENAME);
        Charset charSetOfLog = Charset.forName("US-ASCII");
        BufferedWriter bw = Files.newBufferedWriter(pathOfLog, charSetOfLog, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        String myIP = InetAddress.getLocalHost().getHostAddress();
        String ipPort = myIP + "-" + port;
        bw.append(ipPort, 0, ipPort.length());
        bw.newLine();
        bw.close();
    }
}
