/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

/**
 *
 * @author srivatsav
 */
public class InputMembership {
    CommandMembership cmd;
    String ip, port;

    public InputMembership (String cmd, String ip, String port) {
        this.cmd = CommandMembership.valueOf(cmd);
        this.ip = ip;
        this.port = port;
    }

    public InputMembership (String input) {
        String[] inputArray = input.split(" ");
        this.cmd = CommandMembership.valueOf(inputArray[0]);
        this.ip =  (inputArray.length > 1) ? inputArray[1] : "";
        this.port = (inputArray.length > 2) ? inputArray[2] : "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        return sb.append(cmd + " " + ip + " " + port).toString();
    }
}
