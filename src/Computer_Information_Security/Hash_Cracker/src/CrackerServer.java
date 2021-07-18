package Computer_Information_Security.Hash_Cracker.src;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created By: Assaf, On 04/01/2020
 * Description:
 */
public class CrackerServer extends Thread
{
    private int port;
    private ExecutorService threadPool;
    private volatile boolean stop = false;

    private final int POOL_SIZE = 10;
    private final int WORK_TIME_OUT = 10000;

    public CrackerServer(int port)
    {
        this.port = port;
    }

    public void setStop(boolean b) { stop = b; }

    public void work(DatagramPacket workInfo,DatagramSocket socket)
    {
        try
        {
            String hash = CrackerPacket.getHash(workInfo);
            String startDomain = CrackerPacket.getStartRange(workInfo);
            String endDomain = CrackerPacket.getEndRange(workInfo);

            if(hash != null && startDomain != null && endDomain != null)
            {
                String result = tryToDeHash(hash,startDomain,endDomain);

                if(result != null)
                {
                    // ack
                    socket.send(CrackerPacket.ack(workInfo,result));
                }
                else
                {
                    // nack
                    socket.send(CrackerPacket.nack(workInfo));
                }
            }
        }
        catch (Exception e) {
            System.out.println("Packet Not In Format!");
        }
    }

    private String tryToDeHash(String originalHash, String startDomain, String endDomain)
    {
        long startTime = System.currentTimeMillis();
        double elapsed = 0;
        BigInteger start = HelperFunctions.convertStringToInt(startDomain);
        BigInteger end = HelperFunctions.convertStringToInt(endDomain);
        int length = startDomain.length();

        for(BigInteger i = start; i.compareTo(end) <= 0 && elapsed < WORK_TIME_OUT; i = i.add(new BigInteger("1")))
        {
            String currentString = HelperFunctions.convertIntToString(i, length);
            String hash = hash(currentString);

            if(originalHash.equals(hash))
            {
                return currentString;
            }

            elapsed = (double)(System.currentTimeMillis() - startTime);
        }
        return null;
    }

    private String hash(String toHash)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(toHash.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));

            while (hashText.length() < 32){
                hashText.insert(0, "0");
            }
            return hashText.toString();
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public void offer(DatagramPacket packet,DatagramSocket socket)
    {
        DatagramPacket offer = CrackerPacket.offer(packet);

        try
        {
            socket.send(offer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            threadPool = Executors.newFixedThreadPool(POOL_SIZE);
            DatagramSocket serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(WORK_TIME_OUT);

            while (!stop)
            {
                try
                {
                    DatagramPacket packet = CrackerPacket.rawPacket();

                    serverSocket.receive(packet);

                    HelperFunctions.debugServer(CrackerPacket.toString(packet));

                    CrackerPacket.Type type = CrackerPacket.getPacketType(packet);
                    if(CrackerPacket.Type.DISCOVER.equals(type))
                    {
                        // OFFER
                        threadPool.execute(() -> offer(packet,serverSocket));
                    }
                    else if(CrackerPacket.Type.REQUEST.equals(type))
                    {
                        // WORK
                        threadPool.execute(() -> work(packet,serverSocket));
                    }
                }
                catch (SocketTimeoutException timeOut) {}
            }

            threadPool.shutdown();
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
