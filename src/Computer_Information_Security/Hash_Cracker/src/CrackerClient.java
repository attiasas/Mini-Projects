package Computer_Information_Security.Hash_Cracker.src;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created By: Assaf, On 04/01/2020
 * Description:
 */
public class CrackerClient
{
    private static final int SEEK_TIME_OUT = 1000;
    private static final int ANSWER_TIME_OUT = 15000;

    private volatile String answer = null;

    public CrackerClient()
    {

    }

    public void runCommunication()
    {
        answer = null;

        Scanner scanner = new Scanner(System.in);
        String hash;
        int answerLength;

        // INIT
        System.out.println("Welcome to " + CrackerPacket.TEAM_NAME + ". Please enter the hash:");
        while (true)
        {
            hash = scanner.nextLine();
            if(hash.matches("[a-f0-9]{40}")) break;
            else System.out.println("Wrong Input Format, enter a 40 length hash only from the characters [a-f],[0-9]:");
        }

        System.out.println("Please enter the input string length:");
        answerLength = scanner.nextInt();

        // BROADCAST - and gather worker servers from offers
        Set<InetAddress> workers = new HashSet<>();

        try(DatagramSocket clientSocket = new DatagramSocket())
        {
            clientSocket.setBroadcast(true);
            clientSocket.setSoTimeout(SEEK_TIME_OUT);

            ExecutorService gatherThread = Executors.newSingleThreadExecutor();

            DatagramPacket packet = CrackerPacket.discover();
            clientSocket.send(packet);

            gatherThread.execute(() -> gatherServers(clientSocket,workers));
            gatherThread.shutdown();

            gatherThread.awaitTermination(1, TimeUnit.HOURS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(workers.isEmpty())
        {
            System.out.println("Could not find available servers.");
        }
        else
        {
            // Crack
            HelperFunctions.debugClient("Found " + workers.size() + " servers, start cracking");

            long startWait = System.currentTimeMillis();

            try(DatagramSocket clientSocket = new DatagramSocket())
            {
                ExecutorService taskThreads = Executors.newSingleThreadExecutor();

                final  String finalHash = hash;
                taskThreads.execute(() -> handleDomain(workers,finalHash,answerLength,clientSocket));

                taskThreads.shutdown();

                while (System.currentTimeMillis() - startWait < ANSWER_TIME_OUT && answer == null)
                {
                    DatagramPacket serverAnswer = CrackerPacket.rawPacket();
                    clientSocket.setSoTimeout(ANSWER_TIME_OUT);

                    clientSocket.receive(serverAnswer);
                    HelperFunctions.debugClient(CrackerPacket.toString(serverAnswer));

                    if(CrackerPacket.Type.ACK.equals(CrackerPacket.getPacketType(serverAnswer)))
                    {
                        if(hash.equals(CrackerPacket.getHash(serverAnswer)))
                        {
                            answer = CrackerPacket.getStartRange(serverAnswer); // found
                        }
                    }
                }

                if(answer != null)
                {
                    // FOUND
                    System.out.println("The input string is " + answer);
                }

                try
                {
                    taskThreads.awaitTermination(1,TimeUnit.HOURS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
            catch (SocketTimeoutException e)
            {
                // NO ANSWER
            }
            catch (Exception e) {e.printStackTrace();}

            if(answer == null)
            {
                // NO ANSWER
                System.out.println("Could not find answer");
            }
        }
    }

    private void handleDomain(Set<InetAddress> workers,String hash , int answerLength, DatagramSocket socket)
    {
        String[] ranges = divideToDomains(answerLength, workers.size());
        int i = 0;
        for (InetAddress server : workers)
        {
            DatagramPacket requestPacket = CrackerPacket.request(hash, answerLength, ranges[i], ranges[i + 1], server);

            try
            {
                socket.send(requestPacket);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            i += 2;
        }
    }

    private String[] divideToDomains(int stringLength, int numOfServers)
    {
        String [] domains = new String[numOfServers * 2];

        StringBuilder first = new StringBuilder(); //aaa
        StringBuilder last = new StringBuilder(); //zzz

        for(int i = 0; i < stringLength; i++){
            first.append("a"); //aaa
            last.append("z"); //zzz
        }

        BigInteger total = HelperFunctions.convertStringToInt(last.toString());
        BigInteger perServer = total.divide(BigInteger.valueOf(numOfServers));

        domains[0] = first.toString(); //aaa
        domains[domains.length -1 ] = last.toString(); //zzz
        BigInteger summer = new BigInteger("0");

        for(int i = 1; i <= domains.length -2; i += 2){
            summer =  summer.add(perServer);
            domains[i] = HelperFunctions.convertIntToString(summer, stringLength); //end domain of server
            summer = summer.add(BigInteger.valueOf(1));//++;
            domains[i + 1] = HelperFunctions.convertIntToString(summer, stringLength); //start domain of next server
        }

        return domains;
    }

   
    private void gatherServers(DatagramSocket clientSocket,Set<InetAddress> servers)
    {
        try
        {
            clientSocket.setSoTimeout(SEEK_TIME_OUT);

            while (true)
            {
                DatagramPacket offer = CrackerPacket.rawPacket();
                clientSocket.receive(offer);

                HelperFunctions.debugClient(CrackerPacket.toString(offer));


                // add to server set
                servers.add(offer.getAddress());
            }
        }
        catch (SocketTimeoutException timeout)
        {
            // DONE WAITING
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
