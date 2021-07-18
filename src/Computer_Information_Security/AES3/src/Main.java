package Computer_Information_Security.AES3.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created By: Assaf Attias
 */
public class Main
{
    private static final int TYPE = 0;

    private static AES3 aes3 = new AES3();
    private static AES3Breaker breaker = new AES3Breaker();

    public static void main(String[] args) throws IOException
    {
        program(args);
    }

    public static void program(String[] args) throws IOException
    {
        if(args.length == 0) return;

        File outFile = null;

        if(args[TYPE].equals("-e") || args[TYPE].equals("-d"))
        {
            // encrypt / decrypt
            byte[] keyData = null;
            byte[] inputData = null;

            for(int i = 1; i < args.length; i += 2)
            {
                switch (args[i])
                {
                    case "-k": keyData = Files.readAllBytes(Paths.get(args[i+1])); break;
                    case "-i": inputData = Files.readAllBytes(Paths.get(args[i+1])); break;
                    case "-o": outFile = new File(args[i+1]); break;
                }
            }

            // apply encryption / decryption and write to output file
            try(FileOutputStream writer = new FileOutputStream(outFile))
            {
                if(args[TYPE].equals("-e")) writer.write(aes3.encrypt(inputData,keyData));
                else writer.write(aes3.decrypt(inputData,keyData));
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        else if(args[TYPE].equals("-b"))
        {
            // break
            byte[] msgData = null;
            byte[] cipherData = null;

            for(int i = 1; i < args.length; i += 2)
            {
                switch (args[i])
                {
                    case "-m": msgData = Files.readAllBytes(Paths.get(args[i+1])); break;
                    case "-c": cipherData = Files.readAllBytes(Paths.get(args[i+1])); break;
                    case "-o": outFile = new File(args[i+1]); break;
                }
            }
            // find keys and write to file
            try(FileOutputStream writer = new FileOutputStream(outFile))
            {
                writer.write(breaker.breakEncryption(msgData,cipherData));
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}
