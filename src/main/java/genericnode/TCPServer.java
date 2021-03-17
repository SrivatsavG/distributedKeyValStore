/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import static genericnode.GenericNode.threadPool;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author srivatsav
 */
//SERVES DUAL PURPOSE: Key-val store server and membership server
public class TCPServer {

    //KEY-VAL STORE SERVER
    public void startThreads(int port, ServerSocket serverSocket, KeyValStore kv, String membershipServerIP, int membershipServerPort) {
        try {
            while (!threadPool.isShutdown()) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                    threadPool.execute(new WorkerTCP(port, clientSocket, serverSocket, kv, threadPool, membershipServerIP, membershipServerPort, CommandMembershipMethod.tcp, null));
                } catch (IOException e) {
                    if (threadPool.isShutdown()) {
                        System.out.println("Server Stopped.");
                        break;
                    }
                    throw new RuntimeException(
                            "Error accepting client connection", e);
                }
            }
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MEMBERSHIP TCP SERVER
    public void startThreads(ServerSocket serverSocket, MembershipList ml) {
        try {
            while (!threadPool.isShutdown()) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                    threadPool.execute(new WorkerMembership(clientSocket, serverSocket, ml, threadPool));
                } catch (IOException e) {
                    if (threadPool.isShutdown()) {
                        System.out.println("Server Stopped.");
                        break;
                    }
                    throw new RuntimeException(
                            "Error accepting client connection", e);
                }
            }
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThreads(int port, ServerSocket serverSocket, KeyValStore kv, String membershipFilename) {
        try {
            while (!threadPool.isShutdown()) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                    threadPool.execute(new WorkerTCP(port, clientSocket, serverSocket, kv, threadPool, null, -1, CommandMembershipMethod.file, membershipFilename));
                } catch (IOException e) {
                    if (threadPool.isShutdown()) {
                        System.out.println("Membership Server Stopped.");
                        break;
                    }
                    throw new RuntimeException(
                            "Error accepting client connection", e);
                }
            }
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
