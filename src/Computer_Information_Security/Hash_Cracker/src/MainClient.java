package Computer_Information_Security.Hash_Cracker.src;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MainClient {

    public static void main(String[] args) {

        try
        {
            CrackerClient client = new CrackerClient();

            client.runCommunication();
        }
        catch (Exception e) { e.printStackTrace();}
    }
}
