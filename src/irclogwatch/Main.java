/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irclogwatch;

import java.io.IOException;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author jvanek
 */
public class Main {

    private static LoginWatcher lw;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, IrcException {
        lw = new LoginWatcher();
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                lw.disconnect();
            }
        });

        lw.LogLoginWatcherToJAva();

    }
}
