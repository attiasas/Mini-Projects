package Computer_Information_Security.AES3.src;

import java.util.Arrays;

/**
 * Created By: Assaf Attias
 * Description: Simple AES algorithm for encryption 128 bit (16 byte) blocks with 128 bit key
 */
public class AES
{
    private static final int COLUMN_SIZE = 4;
    protected static int BLOCK_SIZE = COLUMN_SIZE * COLUMN_SIZE;

    /**
     * Encrypt a msg with an encryption key base on a simplify AES block encryption
     * @param msg - a msg represented as bytes to encrypt
     * @param key - a Block Size byte encryption key (16 bytes)
     * @return - an encrypted byte cipher of the msg with the key the same size as the given msg
     */
    public byte[] encrypt(byte[] msg, byte[] key)
    {
        if(msg == null || key == null) throw new IllegalArgumentException("Null not allowed");
        if(key.length != BLOCK_SIZE) throw new IllegalArgumentException("key must be " + BLOCK_SIZE + " bytes size only.");

        byte[] result = new byte[msg.length];
        int i = 0;

        while (i < msg.length)
        {
            // divide msg to BLOCK_SIZE bytes blocks
            byte[] block = Arrays.copyOfRange(msg,i,i + BLOCK_SIZE);

            // Encrypt block
            block = shiftColumns(block);
            addKey(block,key);

            // Concatenate encrypted block to result
            for(int j = 0; j < BLOCK_SIZE; j++)
            {
                result[i+j] = block[j];
            }

            i += BLOCK_SIZE;
        }

        return result;
    }

    /**
     * Decrypt a cipher with an encryption key base on a simplify AES block encryption
     * @param cipher - byte cipher that was generated with this algorithm
     * @param key - byte encryption key that was used to generate the given cipher
     * @return - original byte msg (plain text) of the cipher
     */
    public byte[] decrypt(byte[] cipher, byte[] key)
    {
        if(cipher == null || key == null) throw new IllegalArgumentException("Null not allowed");
        if(key.length != BLOCK_SIZE) throw new IllegalArgumentException("key must be " + BLOCK_SIZE + " bytes size only.");

        byte[] result = new byte[cipher.length];
        int i = 0;

        while (i < cipher.length)
        {
            // divide cipher to BLOCK_SIZE bytes blocks
            byte[] block = Arrays.copyOfRange(cipher,i,i + BLOCK_SIZE);

            // Decrypt block
            addKey(block,key);
            block = inverseShiftColumns(block);

            // Concatenate decrypted block to result
            for(int j = 0; j < BLOCK_SIZE; j++)
            {
                result[i+j] = block[j];
            }

            i += BLOCK_SIZE;
        }

        return result;
    }

    /**
     * Get the shifted index of a given index for swapping elements
     * @param i - index
     * @return shifted index
     */
    private int getShiftedIndex(int i)
    {
        // calc current column data
        int columnNum = i / COLUMN_SIZE;
        int columnEnd = COLUMN_SIZE * (columnNum + 1);
        int columnStart = columnNum * COLUMN_SIZE;
        // calc source index for shifting data in column
        int shiftedNotBound = (i + columnNum) % columnEnd;
        // bound index to current column
        return shiftedNotBound + (i + columnNum >= columnEnd ? columnStart: 0);
    }

    /**
     * Shift a given byte block base on column separation.
     * The number of shifts in a column is base on its number
     * @param inputBlock - given byte block to shift
     * @return - the byte block after shifting elements
     */
    public byte[] shiftColumns(byte[] inputBlock)
    {
        byte[] result = new byte[inputBlock.length];

        for(int i = 0; i < result.length; i++)
        {
            result[i] = inputBlock[getShiftedIndex(i)];
        }

        return result;
    }

    /**
     * Reverse the shift function, This method will shift elements in the opposite direction in a column
     * @param inputBlock - given byte block to shift
     * @return - the byte block after shifting elements
     */
    public byte[] inverseShiftColumns(byte[] inputBlock)
    {
        byte[] result = new byte[inputBlock.length];

        for(int i = 0; i < result.length; i++)
        {
            result[getShiftedIndex(i)] = inputBlock[i];
        }
        return result;
    }

    /**
     * Add (bitwise Xor) Key with a given byte block, this method will change the elements in the given block)
     * @param inputBlock - not null byte block
     * @param key - not null byte encryption key
     */
    private void addKey(byte[] inputBlock, byte[] key)
    {
        for(int i = 0; i < inputBlock.length; i++)
        {
            inputBlock[i] = (byte) (inputBlock[i] ^ key[i]);
        }
    }
}
