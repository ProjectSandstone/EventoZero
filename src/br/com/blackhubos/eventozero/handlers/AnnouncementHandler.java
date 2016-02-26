/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.blackhubos.eventozero.handlers;

import br.com.blackhubos.eventozero.EventoZero;
import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.factory.EventHandler;
import br.com.blackhubos.eventozero.factory.EventState;
import br.com.blackhubos.eventozero.util.ThreadUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hugo
 */
public class AnnouncementHandler implements Runnable {

    private EventHandler handler;

    private boolean destroy;

    public AnnouncementHandler() {
        ThreadUtils.createNewThread(this, this);
        this.handler = EventoZero.getEventHandler();
    }

    @Override
    public void run() {
        while (!destroy) {
            for(Event e : handler.getEvents() ) {
                if(e.getState() == EventState.OPENED) {
                    e.getAnnouncement().tryAnnouncement();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(AnnouncementHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public AnnouncementHandler destroy() {
        this.destroy = true;
        ThreadUtils.stopAllThreads(this);
        return this;
    }

}
