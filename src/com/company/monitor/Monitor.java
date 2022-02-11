package com.company.monitor;

import java.util.ArrayList;
import java.util.Collections;

public class Monitor extends  Thread{
    private final ArrayList<String> msgList;

    public Monitor() {
        Collections.synchronizedList(msgList = new ArrayList<String>());
    }

    public synchronized void put(String message) {
        //mete todos los mensajes que lleguen a la lista
        notifyAll();
        msgList.add(message);
    }

    public synchronized String get(){
        //devuelve los mensajes
        notifyAll();
        String msg = "";
        for (int i = 0; i < msgList.size(); i++) {
            msg += msgList.get(i) + "\n";
        }
        return msg;
    }

}
