/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author srivatsav
 */
public class MembershipList {
    List<String> list = new CopyOnWriteArrayList<>();
    
    public String operation(InputMembership input) {  
        
        StringBuilder sb = new StringBuilder("");

        CommandMembership cmd = input.cmd;
        String ip = input.ip;
        String port = input.port;
         
        try{
            switch(cmd){
                case write: 
                    list.add(ip + "-" + port);
                    sb.append("Added " + ip +"-"+ port + " to membership list");
                    break;                    
                case read:  
                    int i = 0;
                    for(String s : list){
                        sb.append(s);
                        if(i != list.size() - 1) sb.append(',');
                        i++;
                    }
                    break;
                case del:
                    list.remove(ip + "-" + port);
                    sb.append("Deleted from membership list");
            }
            return sb.toString();            
        } catch(Exception e) {
             e.printStackTrace();
             return "Error adding node to membership list";
        }
    }    
}
