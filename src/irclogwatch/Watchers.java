/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package irclogwatch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jvanek
 */
class Watchers {

    private String nick;
    private Set<Watcher> watchers=new HashSet();

    public Watchers() {
    }

    public Watchers(String nick) {
        this.nick = nick;

    }
  

    
    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (obj instanceof Watchers) {

            return (this.nick.equals(((Watchers)obj).nick));
        }
        return false;
    }

    @Override
    public int hashCode() {
  return getNick().hashCode();
    }



    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

 

    

    /**
     * @return the watchers
     */
    public Set<Watcher> getWatchers() {
        return Collections.unmodifiableSet(watchers);
    }

    /**
     * @param watchers the watchers to set
     */
    public boolean addWatcher(String watcher) {
        return this.watchers.add(new Watcher(watcher));
    }
    public boolean addWatcher(Watcher watcher) {
        return this.watchers.add(watcher);
    }
    public boolean addWatcher(String watcher,int level) {
        return this.watchers.add(new Watcher(watcher,level));
    }

    public boolean removeWatcher(String watcher) {
        return this.watchers.remove(new Watcher(watcher));
    }

    

}
