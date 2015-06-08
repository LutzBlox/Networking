package com.lutz.networking.databases;

import java.io.IOException;
import java.net.UnknownHostException;

import com.lutz.networking.Client;
import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class DatabaseClient {

	private Client client;

	private String databaseName = "";

	private boolean updated = false;

	private Object recentValue;

	private long respWait = 100;

	public DatabaseClient(String ip, int port) {

		client = new Client(ip, port);

		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				if (packet.hasData(Response.RESPONSE_KEY)) {

					recentValue = packet.getData(Response.RESPONSE_KEY);
					updated = true;
				}
			}

			@Override
			public void onConnect(Packet packet) {

				databaseName = (String) packet
						.getData(DatabaseServer.DATABASE_NAME_KEY);
			}

			@Override
			public void onTimeout(Connection connection) {
			}
		});
	}

	public DatabaseClient(String ip, int port, long responseLoopWaitTime) {

		this(ip, port);

		this.respWait = responseLoopWaitTime;
	}

	public String getDatabaseName() {

		return databaseName;
	}

	public String getIp() {

		return client.getIp();
	}

	public int getPort() {

		return client.getPort();
	}

	public Object requestValue(String key) {

		updated = false;

		Packet p = new Packet();
		p.putData(Request.REQUEST_KEY, key);

		client.sendPacket(p, true);

		while (!updated) {

			try {

				Thread.sleep(respWait);

			} catch (Exception e) {
			}
		}

		return recentValue;
	}

	public Object requestValue(Request request) {

		updated = false;

		Packet p = new Packet();
		p.putData(Request.REQUEST_KEY, request.getDataKey());

		client.sendPacket(p, true);

		while (!updated) {

			try {

				Thread.sleep(100);

			} catch (Exception e) {
			}
		}

		return recentValue;
	}

	public void putValue(String key, Object value) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, key);
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, value);

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void putValue(PutRequest putRequest) {

		Packet p = new Packet();
		p.putData(PutRequest.PUT_REQUEST_KEY_KEY, putRequest.getDataKey());
		p.putData(PutRequest.PUT_REQUEST_VALUE_KEY, putRequest.getValue());

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void sendCommand(String command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command);

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void sendCommand(Command command) {

		Packet p = new Packet();
		p.putData(Command.COMMAND_KEY, command.getCommand());

		client.sendPacket(p, false);

		client.setToSend();
	}

	public void connect() throws UnknownHostException, IOException {

		client.connect();
	}

	public void close() throws IOException {

		client.close();
	}
}
