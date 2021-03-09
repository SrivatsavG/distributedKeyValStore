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
public class InputTCP {

    CommandTCP cmd;
    String key, val;

    public InputTCP (String cmd, String key, String val) {
        this.cmd = CommandTCP.valueOf(cmd);
        this.key = key;
        this.val = val;
    }

    public InputTCP (String input) {
        String[] inputArray = input.split(" ");
        this.cmd = CommandTCP.valueOf(inputArray[0]);
        this.key =  (inputArray.length > 1) ? inputArray[1] : "";
        this.val = (inputArray.length > 2) ? inputArray[2] : "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        return sb.append(cmd + " " + key + " " + val).toString();
    }
}

