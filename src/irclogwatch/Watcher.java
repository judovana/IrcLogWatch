/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package irclogwatch;

/**
 *
 * @author jvanek
 */
class Watcher {
private String name;
    private int level;

    public Watcher(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public Watcher(String name) {
        this.name = name;
        this.level = 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (obj instanceof String ){ return ((String)obj).equals(name);
        }
        if (obj instanceof Watcher ){ return ((Watcher)obj).getName().equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }




    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return getName()+" ("+getLevel()+")";
    }




}
