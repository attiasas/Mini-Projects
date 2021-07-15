package Advance_Programming.Maze_Game_Part_B.src.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

/**
 * This class represent a server that can communicate with multiple clients and execute a given strategy
 * Created by Assaf Attias
 */
public class Server
{
    private int port;
    private int listeningInterval;
    private IServerStrategy serverStrategy;
    private volatile boolean stop;
    private ExecutorService threadPoolExecutor;

    /**
     * Constructor
     * @param port - port to listen for clients
     * @param listeningInterval - interval before TimeOut
     * @param serverStrategy - strategy to execute
     */
    public Server(int port, int listeningInterval, IServerStrategy serverStrategy)
    {
        this.port = port;
        this.listeningInterval = listeningInterval;
        this.serverStrategy = serverStrategy;
    }

    /**
     * Boot server in a different thread and start to listen for clients
     */
    public void start()
    {
        new Thread(() -> {
            runServer();
        }).start();
    }

    /**
     * main server loop
     */
    private void runServer()
    {
        try
        {
            int poolSize = Integer.parseInt(Configurations.getProperty("poolSize"));
            threadPoolExecutor = Executors.newFixedThreadPool(poolSize);

            // init
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(listeningInterval);

            while (!stop)
            {
                try
                {
                    Socket clientSocket = serverSocket.accept(); // blocking call

                    threadPoolExecutor.execute(() -> handleClient(clientSocket));

                } catch (SocketTimeoutException e)
                {
                    //System.out.println("Socket Timeout - No clients pending!");
                }
            }

            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
            serverSocket.close();

        }
        catch (IOException e)
        {
            //System.out.println("IOException: " + e);
        }
        catch (InterruptedException e)
        {
            //System.out.println("InterruptedException: " + e);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    /**
     * Execute strategy
     * @param clientSocket - client socket
     */
    private void handleClient(Socket clientSocket)
    {
        try
        {
            serverStrategy.serverStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();

        } catch (IOException e)
        {
            //System.out.println("IOException: " + e);
        }
    }

    /**
     * Stop the server form running and shutdown
     */
    public void stop()
    {
        //System.out.println("Stopping server..");
        stop = true;
    }
}

