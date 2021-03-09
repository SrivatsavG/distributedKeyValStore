/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author srivatsav
 */
class KeyValStore {

    ConcurrentMap<String, Value> map = new ConcurrentHashMap<>();
    final int MAXSIZE = 65000;

    public String operation(InputTCP input, String[] members, int port) {
        StringBuilder sb = new StringBuilder("");
        try {

            CommandTCP cmd = input.cmd;
            String key = input.key;
            String val = input.val;
            Value v;
            boolean locked;

            switch (cmd) {
                case put:
                    //LOCK LOCALLY IF OLD KEY,NO NEED TO LOCK NEW KEY
                    v = map.get(key);
                    if (v != null) {
                        map.put(key, new Value(v.data, true));

                        //CHECK ALL MEMBERS TO SEE IF KEY IS LOCKED, 
                        locked = operationUtil(port, members, CommandTCP.dput1.toString(), key, val);
                        if (locked) { // SEND dputabort to all members if locked
                            operationUtil(port, members, CommandTCP.dputabort.toString(), key, val);
                            break;
                        }
                    }

                    //CALL DPUT2 ON ALL MEMBERS FOR OLD AND NEW KEY IF ALL RETURN ACK
                    operationUtil(port, members, CommandTCP.dput2.toString(), key, val);

                    //ADD NEW VAL TO MAP AND UNLOCK
                    map.put(key, new Value(val, false));
                    sb.append("server response: put key= " + key);
                    break;
                case dput1:
                    v = map.get(key);
                    if (v == null) { //IF NOT IN MAP, CAN BE WRITTEN, SEND ACK
                        sb.append(CommandAck.ack);
                        break;
                    }
                    //CHECK IF KEY IS LOCKED, IF LOCKED, RETURN "abort". IF NOT, LOCK IT AND RETURN "ACK"
                    locked = v.locked;
                    if (locked) { //Send abort if locked already
                        sb.append(CommandAck.abort);
                        break;
                    }
                    //Lock and send ack
                    map.put(key, new Value(v.data, true));
                    sb.append(CommandAck.ack);//SEND ACK IF NOT LOCKED
                    break;
                case dput2:
                    map.put(key, new Value(val, false));
                    sb.append("server response: put key= " + key);
                    break;
                case dputabort:
                    //UNLOCK THE KEY
                    v = map.get(key);
                    if (v == null) {
                        break;
                    }

                    map.put(key, new Value(v.data, false));
                    break;
                case get:
                    v = map.get(key);
                    if (v == null) {
                        sb.append("server response: invalid key");
                        break;
                    }

                    sb.append("server response: get key= " + key + " Get val: " + v.data);
                    break;
                case del:
                    //LOCK LOCALLY
                    v = map.get(key);
                    if (v == null) {
                        break; //NOT THERE
                    }
                    map.put(key, new Value(v.data, true));

                    //CHECK ALL MEMBERS TO SEE IF KEY IS LOCKED
                    locked = operationUtil(port, members, CommandTCP.ddel1.toString(), key, val);
                    if (locked) { // SEND ddelabort to all members if locked
                        operationUtil(port, members, CommandTCP.ddelabort.toString(), key, val);
                        break;
                    }

                    //Call ddel2 on all other members
                    operationUtil(port, members, CommandTCP.ddel2.toString(), key, val);

                    //Del from map and unlock
                    map.remove(key);
                    sb.append("server response: delete key= " + key);
                    break;
                case ddel1:
                    v = map.get(key);
                    if (v == null) {
                        break; //NOT THERE
                    }

                    //CHECK IF KEY IS LOCKED, IF LOCKED, RETURN "abort". IF NOT, LOCK IT AND RETURN "ACK"                    
                    locked = v.locked;
                    if (locked) { //Send abort if locked already
                        sb.append(CommandAck.abort);
                        break;
                    }
                    //Lock and send ack if not locked
                    map.put(key, new Value(v.data, true));
                    sb.append(CommandAck.ack);
                    break;
                case ddel2:
                    v = map.get(key);
                    if (v == null) {
                        break; //NOT THERE
                    }
                    map.remove(key);
                    sb.append("server response: delete key= " + key);
                    break;
                case ddelabort:
                    //UNLOCK THE KEY
                    v = map.get(key);
                    if (v == null) {
                        break; //NOT THERE
                    }
                    map.put(key, new Value(v.data, false));
                    break;
                case store:
                    int entryCount = 0;
                    sb.append("STORE-START\n");
                    sb.append("server response:\n");
                    for (ConcurrentMap.Entry<String, Value> entry : map.entrySet()) {

                        if (sb.length() >= MAXSIZE) {
                            sb.insert(0, "TRIMMED:\n");
                            break;
                        }

                        sb.append("key:" + entry.getKey() + ":" + "value:" + entry.getValue().data);
                        
                        
                        if(entryCount == map.size() - 2){
                            sb.append("\nSTORE-END");
                        }
                        
                        entryCount++;

                        if (entryCount < map.size()) {
                            sb.append("\n");
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();

    }

    private boolean operationUtil(int port, String[] members, String command, String key, String val) {
        for (String m : members) {
            Member member = new Member(m);
            InputTCP input = new InputTCP(command, key, val);
            if (Integer.parseInt(member.port) == port) {
                continue; //SKIP IF SAME NODE
            }
            TCPClient tcpClient = new TCPClient(member.ip, Integer.parseInt(member.port), input.toString());
            String serverResponse = tcpClient.connect();
            if (serverResponse.equals(CommandAck.abort.toString())) {
                return true;
            }
        }
        return false;
    }
}
