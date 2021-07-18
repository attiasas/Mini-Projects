package Computer_Information_Security.Hash_Cracker.src;

/**
 * Created By: Assaf, On 08/01/2020
 * Description:
 */
public class MainServer {

    public static void main(String[] args) {
        CrackerServer server = new CrackerServer(CrackerPacket.SERVER_PORT);
        server.start();
    }
}
