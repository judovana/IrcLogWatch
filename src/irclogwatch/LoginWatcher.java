/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irclogwatch;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author jvanek
 */
public class LoginWatcher extends PircBot {

    private Set<Watchers> watchers = new HashSet<Watchers>();
    Properties prop = new Properties();
    public static final String NAME = "name";
    public static final String OWENER = "owener";
    public static final String SERVER = "server";
    public static final String CHANEL = "chanel";
    public static final String WATCH = "watch";
    String owenersName = "unknown";

    public void LogLoginWatcherToJAva() throws IOException, IrcException {
        prop.load(this.getClass().getResourceAsStream("settings.txt"));
        this.setName(prop.getProperty(NAME));
        if (prop.getProperty(OWENER) != null) {
            owenersName = prop.getProperty(OWENER);
        }
        int i = 0;
        while (true) {
            i++;
            String s = prop.getProperty(SERVER + String.valueOf(i));
            if (s == null) {
                break;
            }

            System.out.println("connecting to server " + i + ": " + s);
            if (s.contains(":")) {
                String[] ss = s.split(":");
                this.connect(ss[0], new Integer(ss[1]));
            } else {
                this.connect(s.trim());
            }
        }

        i = 0;
        while (true) {
            i++;
            String s = prop.getProperty(CHANEL + String.valueOf(i));
            if (s == null) {
                break;
            }

            System.out.println("joining chanel " + i + ": " + s);


            this.joinChannel(s);

        }

        i = 0;
        while (true) {
            i++;
            String s = prop.getProperty(WATCH + String.valueOf(i));
            if (s == null) {
                break;
            }

            add(owenersName, owenersName, s.trim());


            this.joinChannel(s);
        }


        new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60 * 1000);
                        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {

                            Watchers w = it.next();


                            {
                                Set<Watcher> ww = w.getWatchers();
                                for (Iterator<Watcher> it1 = ww.iterator(); it1.hasNext();) {
                                    Watcher string = it1.next();
                                    int timeOut = Math.abs(string.getLevel());
                                    if (timeOut > 4) {
                                        if ((System.currentTimeMillis() / 1000 / 60) % timeOut == 0) {
                                            if (string.getLevel() >= 0) {
                                                sendMessage(w.getNick(), " ping");
                                                sendMessage(w.getNick(), w.getNick() + ", " + string.getName() + " need to talk with you");
                                            } else {
                                                String[] ch = getChannels();
                                                for (int j = 0; j < ch.length; j++) {
                                                    String string1 = ch[j];
                                                    sendMessage(string1, w.getNick() + " ping");
                                                    sendMessage(string1, w.getNick() + ", " + string.getName() + " need to talk with you");

                                                }

                                            }
                                        }
                                    }
                                }

                            }


                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }) {
        }.start();

    }

    public boolean proceed(String message, String sender) {
        String[] command = message.split("\\s*,*:*\\s+");
        if (command.length < 2) {
            return true;
        }
        if (command[0].equals(getName())) {
//            System.out.println("juch! command!");
//            for (int i = 0; i < command.length; i++) {
//                String string = command[i];
//                System.out.println("   "+string);
//
//            }
        } else {
            return true;
        }
        if (command[1].equalsIgnoreCase("watch")) {
            if (command.length < 3) {
                return true;
            }
            String subject = command[2];
            sendMessage(sender, "I'm watching " + subject + " for you");
            if (command.length >= 3) {
                try {
                    Integer level = new Integer(command[3]);
                    add(sender, sender, subject, level);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    add(sender, sender, subject);
                }

            } else {
                add(sender, sender, subject);
            }
        } else if (command[1].equalsIgnoreCase("unwatch")) {
            if (command.length < 3) {
                return true;
            }
            String subject = command[2];
            sendMessage(sender, "I have stopped watching " + subject + " for you");
            remove(sender, sender, subject);
        } else if (command[1].equalsIgnoreCase("watchfor")) {
            if (command.length < 4) {
                return true;
            }
            String reciever = command[2];
            String subject = command[3];
            sendMessage(sender, "I'm watching " + subject + " for " + reciever);
            sendMessage(reciever, "I'm watching " + subject + " for you from " + sender + " command");
            if (command.length >= 4) {
                try {
                    Integer level = new Integer(command[4]);
                    add(sender, reciever, subject, level);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    add(sender, reciever, subject);
                }

            } else {
                add(sender, reciever, subject);
            }
        } else if (command[1].equalsIgnoreCase("unwatchfor")) {
            if (command.length < 4) {
                return true;
            }
            String reciever = command[2];
            String subject = command[3];
            sendMessage(sender, "I have stopped watching " + subject + " for " + reciever);
            sendMessage(reciever, "I have stopped watching " + subject + " for you from " + sender + " command");
            remove(sender, reciever, subject);
        } else if (command[1].equalsIgnoreCase("help")) {
            sendMessage(sender, "watchbot. Notifiing watcher about fact taht somebody needed  have joined irc");
            sendMessage(sender, "owener: " + owenersName);
            sendMessage(sender, "--------commands-------");
            sendMessage(sender, "watch WHO");
            sendMessage(sender, "unwatch WHO");
            sendMessage(sender, "watchfor  FORWHO WHO");
            sendMessage(sender, "unwatchfor FORWHO WHO");
            sendMessage(sender, "-----------------------");
            sendMessage(sender, "watch and watchfor have optional parameter - number:");
            sendMessage(sender, "       1 - default will ping you when join chanel");
            sendMessage(sender, "       2 - will ping you when join and leave chanel");
            sendMessage(sender, "       3 - will ping you when join or leave chanel or change nick");
            sendMessage(sender, "       4, <0,-4> - reserved;)");
            sendMessage(sender, "       5 and more - same as 3 but it will ping watched person privately every  this number minutes");
            sendMessage(sender, "       -5 and less - same as 3 but it will ping watched person publicky every abs of this number minutes ");
            sendMessage(sender, "-----------------------");

            Set<Watcher> n = new HashSet<Watcher>();
            for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {
                Watchers w = it.next();
                Set<Watcher> ww = w.getWatchers();
                for (Iterator<Watcher> www = ww.iterator(); www.hasNext();) {
                    Watcher string = www.next();
                    n.add(string);

                }
            }
            for (Iterator<Watcher> it1 = n.iterator(); it1.hasNext();) {
                Watcher string = it1.next();
                {
                    infoFor(string, sender);
                }


            }


            sendMessage(sender, "-----------------------");
            infoAbout(sender);


        } else {
            sendMessage(sender, "try help with me");
        }
        return false;
    }

    @Override
    protected void onUnknown(String line) {
        System.out.println("on unknown: " + line);
    }

    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        System.out.println("on Nick Change: " + oldNick + ", " + login + ", " + hostname + ", " + newNick);
        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {

            Watchers w = it.next();
            Set<Watcher> www = w.getWatchers();
            for (Iterator<Watcher> it1 = www.iterator(); it1.hasNext();) {
                Watcher string = it1.next();

                if (string.getName().equals(oldNick)) {
                    string.setName(newNick);
                }

            }

            if (oldNick.contains(w.getNick()) || newNick.contains(w.getNick())) {
                Set<Watcher> ww = w.getWatchers();
                for (Iterator<Watcher> it1 = ww.iterator(); it1.hasNext();) {
                    Watcher string = it1.next();

                    if (string.getLevel() > 2) {
                        sendMessage(string.getName(), oldNick + " have changed nick to " + newNick);
                    }
                }

            }


        }
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        System.out.println("onAction: " + sender + ", " + login + ", " + hostname + ", " + target + ", " + action);

    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        System.out.println("onJoin: " + sender + ", " + login + ", " + hostname);
        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {

            Watchers w = it.next();

            if (sender.contains(w.getNick())) {
                Set<Watcher> ww = w.getWatchers();
                for (Iterator<Watcher> it1 = ww.iterator(); it1.hasNext();) {
                    Watcher string = it1.next();

                    sendMessage(string.getName(), sender + " have finally joined " + channel);
                }

            }


        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        System.out.println("onMessage: " + sender + ", " + login + ", " + hostname + ", " + message);
        if (proceed(message, sender)) {
            return;
        }
    }


    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (!(message.startsWith(getName()))) {
            message=getName()+" "+message;
        }
        System.out.println("onPrivateMessage: " + sender + ", " + login + ", " + hostname + ", " + message);
        if (proceed(message, sender)) {
            return;
        }



    }

    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        System.out.println("onQuite: " + sourceNick + ", " + sourceLogin + ", " + sourceHostname + ", " + reason);
        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {

            Watchers w = it.next();
            String sender = sourceNick;

            if (sender.contains(w.getNick())) {
                Set<Watcher> ww = w.getWatchers();
                for (Iterator<Watcher> it1 = ww.iterator(); it1.hasNext();) {
                    Watcher string = it1.next();

                    if (string.getLevel() > 1) {
                        sendMessage(string.getName(), sender + " have left " + sourceHostname);
                    }

                }

            }


        }
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        System.out.println("onNotice: " + sourceNick + ", " + sourceLogin + ", " + sourceHostname + ", " + target + ", " + notice);

    }

    public void LogOffLoginWatcherToJAva() {
    }

    private void infoAbout(String reciever) {


        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {
            Watchers w = it.next();
            StringBuilder sb = new StringBuilder(w.getNick().toString() + " is watched by: ");

            Set<Watcher> watched = w.getWatchers();
            for (Iterator<Watcher> it1 = watched.iterator(); it1.hasNext();) {
                Watcher string = it1.next();
                sb.append(" " + string.toString());
            }
            sendMessage(reciever, sb.toString());
        }

    }

    private void infoFor(Watcher watcher, String reciever) {

        StringBuilder sb = new StringBuilder();
        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {
            Watchers w = it.next();
            if (w.getWatchers().contains(watcher)) {
                sb.append(" " + w.getNick().toString());
            }


        }
        //this is ok. Watcher requals works fine with String
        if (watcher.equals(reciever)) {
            sendMessage(reciever, "You are currently watching: " + sb.toString());
        } else {
            sendMessage(reciever, watcher + " is currently watching: " + sb.toString());
        }
    }

    private void infoFor(Watcher watcher) {
        infoFor(watcher, watcher.getName());
    }

    private void remove(String sender, String reciever, String subject) {
        Watchers w = getWatcherByNick(subject);
        if (w == null) {
            sendMessage(reciever, "You  werent watching him at all");
            return;
        }

        boolean b = w.removeWatcher(reciever);
        if (!b) {
            sendMessage(reciever, "You  werent watching him at all");
        }
        if (w.getWatchers().size() == 0) {
            watchers.remove(w);
        }
    }

    private void add(String sender, String reciever, String subject) {
        add(sender, reciever, subject, 1);
    }

    private void add(String sender, String reciever, String subject, int level) {
        Watchers w = getWatcherByNick(subject);
        if (w == null) {
            w = new Watchers(subject);
            watchers.add(w);
        }


        boolean b = w.addWatcher(reciever, level);


        if (!b) {
            w.removeWatcher(reciever);
            w.addWatcher(sender, level);
            sendMessage(reciever, "You have been watching him already");
        }



    }

    private Watchers getWatcherByNick(String nick) {
        for (Iterator<Watchers> it = watchers.iterator(); it.hasNext();) {
            Watchers w = it.next();
            if (w.getNick().equals(nick)) {
                return w;
            }

        }
        return null;
    }
}
