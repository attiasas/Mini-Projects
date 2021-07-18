package Computer_Information_Security.AES3.src;

/**
 * Created By: Assaf Attias
 * Description: An algorithm for braking the simplify AES3 Algorithm, implementing PlainText Attack.
 */
public class AES3Breaker
{
    private static AES3 aes3 = new AES3();

    /**
     * Bitwise Xor operation on a given bytes arrays (same size)
     * @param a - bytes to xor
     * @param b - bytes to xor
     * @return a.len size byte that contains the bitwise xor result
     */
    private byte[] xor(byte[] a, byte[] b)
    {
        byte[] res = new byte[a.length];
        for(int i = 0; i < res.length; i++)
        {
            res[i] = (byte) (a[i] ^ b[i]);
        }
        return res;
    }

    /**
     * Combine (Concatenate) 3 keys into a single byte key
     * @param k1 - block size encryption byte key
     * @param k2 - block size encryption byte key
     * @param k3 - block size encryption byte key
     * @return 3 block size encryption byte key after concatenating the 3 keys (k1 is first, than k2 and last is k3)
     */
    private byte[] combine(byte[] k1, byte[] k2, byte[] k3)
    {
        byte[] res = new byte[k1.length + k2.length + k3.length];
        for(int i = 0; i < res.length; i++)
        {
            int currentKey = i / AES.BLOCK_SIZE;
            byte[] k = currentKey == 0 ? k1 : currentKey == 1 ? k2 : k3;
            res[i] = k[i % AES.BLOCK_SIZE];
        }
        return res;
    }

    /**
     * Given a <msg,cipher> pair this algorithm will find the key (3 concatenating keys) that can be use
     * in AES3 Algorithm to generate the given cipher from the given msg.
     * @param msg - msg that was used to generate the cipher with an unknown key
     * @param cipher - cipher that was generated from the msg with an unknown key
     * @return - a potential key that matches (can generate) the pair
     */
    public byte[] breakEncryption(byte[] msg, byte[] cipher)
    {
        byte[] k1 = new byte[AES.BLOCK_SIZE];
        byte[] k2 = new byte[AES.BLOCK_SIZE];
        byte[] k3 = new byte[AES.BLOCK_SIZE];
        byte[] cLSB = new byte[AES.BLOCK_SIZE];

        // generate two keys k1,k2 that: shiftColumn(k1) xor k2 = 0
        for(int i = 0; i < k1.length; i++)
        {
            k1[i] = (byte)i;
            k2[i] = (byte)i;

            k3[i] = msg[i];
            cLSB[i] = cipher[i];
        }
        k1 = aes3.inverseShiftColumns(k1);

        // generate key k3 that: k3 = inverseShiftColumn(m[0-k3.len]) xor c
        k3 = xor(aes3.inverseShiftColumns(k3),cLSB);

        return combine(k1,k2,k3);
    }
}
