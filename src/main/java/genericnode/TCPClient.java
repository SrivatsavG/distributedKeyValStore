/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author srivatsav
 */
public class TCPClient {

    String addr, input;
    int port;

    public TCPClient(String addr, int port, String input) {
        this.addr = addr;
        this.port = port;
        this.input = input;
    }

    public String connect() {
        String serverResponse = "";
        StringBuilder allServerResponse = new StringBuilder("");
        boolean store = false;
        try ( Socket socket = new Socket(addr, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(input.toString());
            out.flush();

            while (true) {
                serverResponse = br.readLine();
                if (serverResponse == null) {
                    break;
                }

                if (serverResponse.equals("STORE-START")) {
                    store = true;
                    continue;
                } else if (serverResponse.equals("STORE-END")) {
                    store = false;
                    continue;
                }
                allServerResponse.append(serverResponse);
                if (store) {
                    allServerResponse.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allServerResponse.toString();
    }
}
