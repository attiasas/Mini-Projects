package Computer_Information_Security.AES3.src;

import java.util.Arrays;

/**
 * Created By: Assaf Attias
 * Description: Simple AES Algorithm that encrypt a msg with the simple AES algorithm 3 times (Rounds).
 *              Requires a byte key that is 3 times bigger than normal, the key consist of 3 concatenating normal AES keys.
 */
public class AES3 extends AES
{
    public static final int ROUNDS = 3;
    public static final int KEY_SIZE = BLOCK_SIZE * ROUNDS;

    @Override
    public byte[] encrypt(byte[] msg, byte[] key)
    {
        if(msg == null || key == null) throw new IllegalArgumentException("Null not allowed");
        if(key.length != KEY_SIZE) throw new IllegalArgumentException("Key must be " + KEY_SIZE + " byte size");

        byte[] result = msg;

        // Divide key and encrypt in ROUNDS
        for(int round = 0; round < ROUNDS; round++)
        {
            result = super.encrypt(result,Arrays.copyOfRange(key,round * BLOCK_SIZE,(round + 1) * BLOCK_SIZE));
        }

        return result;
    }

    @Override
    public byte[] decrypt(byte[] cipher, byte[] key)
    {
        if(cipher == null || key == null) throw new IllegalArgumentException("Null not allowed");
        if(key.length != KEY_SIZE) throw new IllegalArgumentException("Key must be " + KEY_SIZE + " byte size");

        byte[] result = cipher;

        // Divide key and decrypt in ROUNDS
        for(int round = ROUNDS - 1; round >= 0; round--)
        {
            result = super.decrypt(result,Arrays.copyOfRange(key,round * BLOCK_SIZE,(round + 1) * BLOCK_SIZE));
        }

        return result;
    }
}
