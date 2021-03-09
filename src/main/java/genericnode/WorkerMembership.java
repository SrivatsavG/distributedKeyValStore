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
class WorkerMembership implements Runnable {

    private final Socket clientSocket;
    private final ServerSocket ss;
    private MembershipList ml;
    private ExecutorService threadPool;

    public WorkerMembership(Socket socket, ServerSocket ss, MembershipList ml, ExecutorService threadPool) {
        this.clientSocket = socket;
        this.ss = ss;
        this.ml = ml;
        this.threadPool = threadPool;
    }

    public void run() {
        BufferedReader br = null;
        PrintWriter out = null;
        String inputString = null;

        try {
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            inputString = br.readLine();

            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Client sent " + inputString);
            InputMembership input = new InputMembership(inputString);

            if (input.cmd == CommandMembership.exit) {
                threadPool.shutdown();
                clientSocket.close();
                ss.close();
                return;
            }

            //If not exit, do the operation
            String output = ml.operation(input);
            out.println(output);
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(WorkerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
