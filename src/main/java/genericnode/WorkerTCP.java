/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    private final CommandMembershipMethod membershipMethod;
    private String membershipFilename;

    public WorkerTCP(int port, Socket socket, ServerSocket ss, KeyValStore kv, ExecutorService threadPool, String membershipServerIP, int membershipServerPort, CommandMembershipMethod membershipMethod, String membershipFilename) {
        this.port = port;
        this.clientSocket = socket;
        this.ss = ss;
        this.kv = kv;
        this.threadPool = threadPool;
        this.membershipServerIP = membershipServerIP;
        this.membershipServerPort = membershipServerPort;
        this.membershipMethod = membershipMethod;
        this.membershipFilename = membershipFilename;
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
            
            String myIP = InetAddress.getLocalHost().getHostAddress();

            //IF EXIT, CLOSE THREAD and SOCKETS and remove from membershiplist
            if (input.cmd == CommandTCP.exit) {
                threadPool.shutdown();
                clientSocket.close();
                ss.close();
                switch (membershipMethod) {
                    case tcp:
                        tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.del.toString() + " " + myIP + " " + port);
                        membershipServerResponse = tcpClient.connect();
                        break;
                    case file:
                        remove(myIP+"-"+port);
                }
                return;
            }

            //FIND ALL MEMBERS    
            switch (membershipMethod) {
                case tcp:
                    tcpClient = new TCPClient(membershipServerIP, membershipServerPort, CommandMembership.read.toString());
                    membershipServerResponse = tcpClient.connect();
                    String[] membershipServerResponseSplit = membershipServerResponse.split(",");
                    members = new String[membershipServerResponseSplit.length];
                    int i = 0;
                    for (String m : membershipServerResponse.split(",")) {
                        members[i++] = m;
                    }
                    break;
                case file:
                    members = findMembers();
                    break;

            }
            //If not exit, do the operation
            String output = kv.operation(input, members, port);
            out.println(output);
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(WorkerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void remove(String removeString) throws IOException {
        List<String> newLines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(membershipFilename), StandardCharsets.UTF_8)) {
            if (line.contains(removeString)) {
                newLines.add(line.replace(removeString, ""));
            } else {
                newLines.add(line);
            }
        }
        Files.write(Paths.get(membershipFilename), newLines, StandardCharsets.UTF_8);
    }

    public String[] findMembers() throws IOException {
        List<String> memberList = new ArrayList<>();
        try {
            File myObj = new File(membershipFilename);
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.equals("")) {
                    continue;
                }
                memberList.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String[] members = new String[memberList.size()];
        int i = 0;
        for (String s : memberList) {
            members[i++] = s;
        }
        return members;
    }
}
