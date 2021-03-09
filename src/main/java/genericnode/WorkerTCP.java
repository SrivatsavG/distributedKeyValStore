/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author srivatsav
 */
class WorkerTCP implements Runnable {

    private final int port;
    private final Socket clientSocket;
    private final ServerSocket ss;
    private KeyValStore kv;
    private ExecutorService threadPool;
    private final String membershipServerIP;
    private final int membershipServerPort;
    private String[] members;

    public WorkerTCP(int port, Socket socket, ServerSocket ss, KeyValStore kv, ExecutorService threadPool, String membershipServerIP, int membershipServerPort) {
        this.port = port;
        this.clientSocket = socket;
        this.ss = ss;
        this.kv = kv;
        this.threadPool = threadPool;
        this.membershipServerIP = membershipServerIP;
        this.membershipServerPort = membershipServerPort;
    }

    public void run() {
        BufferedReader br = null;
        PrintWriter out = null;
        String inputString = null;

        try {
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            inputString = br.readLine();
            System.out.println("Client sent " + inputString);
            InputTCP input = new InputTCP(inputString);

            TCPClient tcpClient;
            String membershipServerResponse;

            //IF EXIT -> REMOVE MEMBER FROM MEMBERSHIP LIST
            if (input.cmd == CommandTCP.exit) {
                threadPool.shutdown();
                clientSocket.close();
                ss.close();

                tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.del.toString() + " " + "localhost" + " " + port);
                membershipServerResponse = tcpClient.connect();

                return;
            }

            //FIND ALL MEMBERS            
            tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.read.toString());
            membershipServerResponse = tcpClient.connect();
            String[] membershipServerResponseSplit = membershipServerResponse.split(",");
            members = new String[membershipServerResponseSplit.length];
            int i = 0;
            for (String m : membershipServerResponse.split(",")) {
                members[i++] = m;
            }

            //If not exit, do the operation
            String output = kv.operation(input, members, port);
            out.println(output);
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(WorkerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
