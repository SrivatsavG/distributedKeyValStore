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
public class Value {
    String data;
    boolean locked;
    String lockedBy;
    
    public Value(String data, boolean locked, String lockedBy){
        this.data = data;
        this.locked = locked;
        this.lockedBy = lockedBy;
    }
}
