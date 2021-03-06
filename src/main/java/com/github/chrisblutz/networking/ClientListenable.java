package com.github.chrisblutz.networking;

import com.github.chrisblutz.networking.listeners.ClientListener;
import com.github.chrisblutz.networking.listeners.NetworkListener;
import com.github.chrisblutz.networking.packets.Packet;

import java.util.ArrayList;
import java.util.List;


/**
 * This class extends the {@code Listenable} class to make it specifically for
 * the {@code Client} side of a connection
 *
 * @author Christopher Lutz
 */
public class ClientListenable extends Listenable {

    /**
     * Gets all of the {@code ClientListener} objects attached to the
     * {@code Client}
     *
     * @return A {@code ClientListener[]} containing all listeners attached to
     * the {@code Client}
     */
    public ClientListener[] getClientListeners() {

        List<ClientListener> l = new ArrayList<ClientListener>();

        for (NetworkListener n : lists) {

            if (n instanceof ClientListener) {

                l.add((ClientListener) n);
            }
        }

        return l.toArray(new ClientListener[]{});
    }

    /**
     * Fires the {@code onConnect()} method in all of the {@code ClientListener}
     * objects attached to the {@code Client}
     *
     * @param packet The {@code Packet} to pass to the listener
     */
    public void fireListenerOnConnect(Packet packet) {

        for (ClientListener l : getClientListeners()) {

            l.onConnect(packet);
        }
    }
}
