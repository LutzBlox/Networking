package com.github.lutzblox.ping;


import com.github.lutzblox.Client;
import com.github.lutzblox.listeners.NetworkListener;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

import java.util.ArrayList;
import java.util.List;


public class ConstantPingClient extends Client {

    private List<Packet> waiting = new ArrayList<Packet>();

    public ConstantPingClient(String ip, int port) {

        super(ip, port);
    }

    /**
     * Sends a {@code Packet} across the connection to the server on the receiving end
     *
     * @param p              The {@code Packet} to send
     * @param expectResponse {@code ConstantPingClient} disregards this parameter, because it always expects a response
     */
    @Override
    public void sendPacket(Packet p, boolean expectResponse) {

        waiting.add(p);
    }

    /**
     * This method has no effect in a {@code ConstantPingClient}
     */
    @Override
    public void setToReceive() {

    }

    /**
     * This method has no effect in a {@code ConstantPingClient}
     */
    @Override
    public void setToSend() {

    }

    /**
     * This method has no effect in {@code ConstantPingClient} because the default state is locked on {@code RECEIVING}
     *
     * @param state The default {@code State}
     */
    @Override
    public void setDefaultConnectionState(State state) {

    }

    @Override
    public void fireListenerOnReceive(Connection connection, Packet packet) {

        if (!packet.isEmpty()) {

            for (NetworkListener l : getNetworkListeners()) {

                l.onReceive(connection, packet);
            }
        }

        Packet toSend = new Packet();

        if (waiting.size() > 0) {

            for (Packet p : waiting) {

                if (p.isVital())
                    toSend.setVital(true);

                toSend.putData(p.getData());
            }

            waiting.clear();
        }

        connection.sendPacket(toSend, true);
    }
}