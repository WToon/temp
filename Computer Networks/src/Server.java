import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * Represents a multithreaded server.
 * @author R0596433
 */
public class Server implements Runnable {
	
	protected int 			serverPort 	  = 9000;
	protected ServerSocket 	serverSocket  = null;
	protected Boolean 		isStopped 	  = false;
	protected Thread 		runningThread = null;

	public Server(int port) {
		this.serverPort = port;
	}

	@Override
	public void run() {
		synchronized(this) {
			this.runningThread = Thread.currentThread();
		}
		openServerSocket();
		while (! isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch(IOException e) {
				if (isStopped()) {
					System.out.println("Server offline");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			new Thread(new ServerThreadRunnable(clientSocket)).start();
		}
		System.out.println("Server went offline");
	}
	
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 9000", e);
        }
    }
}
