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
public class Member {
    String ip;
    String port;
    
   
    public Member(String s){
        String[] sSplit = s.split("-");
        this.ip = sSplit[0];
        this.port = sSplit[1];
    }
}
