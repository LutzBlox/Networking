package com.lutz.networking;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.lutz.networking.listeners.ClientListener;
import com.lutz.networking.listeners.ServerListener;
import com.lutz.networking.packets.Packet;
import com.lutz.networking.sockets.Connection;

public class SendRecieveTest extends TestCase {

	private boolean finished = false, errored = false;
	private String errorMessage = "";
	private int timesSent = 1;

	public SendRecieveTest(String name) {

		super(name);
	}

	public static TestSuite suite() {

		return new TestSuite(SendRecieveTest.class);
	}

	public void testSendReceive() {

		final Server server = new Server(12349, "SendRecieveTest");
		server.addNetworkListener(new ServerListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				System.out.println("Server: Successfully received packet!");

				if (timesSent < 10) {

					timesSent++;

					server.sendPacket(new Packet(), true);
				}
			}

			@Override
			public Packet onConnect(Connection c, Packet data) {

				System.out.println("Server: Connection received from IP "
						+ c.getIp());

				data.putData("server-name", server.getServerName());

				return data;
			}

			@Override
			public void onTimeout(Connection connection) {
				
				errored = true;
				errorMessage = "Connection timed out!";
				
				finished = true;
			}
		});

		final Client client = new Client("localhost", 12349);
		client.addNetworkListener(new ClientListener() {

			@Override
			public void onReceive(Connection connection, Packet packet) {

				System.out.println("Client: Successfully received packet!");

				if (timesSent < 10) {

					timesSent++;

					client.sendPacket(new Packet(), true);
				}
			}

			@Override
			public void onConnect(Packet packet) {

				System.out.println("Client: Successfully received packet!");

				if (timesSent < 10) {

					timesSent++;

					client.sendPacket(new Packet(), true);
				}
			}

			@Override
			public void onTimeout(Connection connection) {
				
				errored = true;
				errorMessage = "Connection timed out!";
				
				finished = true;
			}
		});

		try {

			System.out.println("Starting server...");

			server.start();

			System.out.println("Starting client...");

			client.connect();

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();

			finished = true;
		}

		while (true) {

			try {

				Thread.sleep(100);

			} catch (InterruptedException e) {
			}

			if (finished || timesSent >= 10) {

				break;
			}
		}

		try {

			client.close();
			server.close();

		} catch (Exception e) {

			e.printStackTrace();

			errored = true;
			errorMessage = e.getClass().getName();
		}

		if (errored) {

			System.out.println("Errored - " + errorMessage);

		} else {

			System.out.println("Success!");
		}
	}
}
