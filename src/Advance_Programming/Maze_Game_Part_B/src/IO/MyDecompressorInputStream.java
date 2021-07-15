package Advance_Programming.Maze_Game_Part_B.src.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static IO.BinaryOperations.*;

/**
 * This class decorate an InputStream that decompress the information when reading - compressed using LZ78 algorithm with bitwise index
 * Created by Assaf Attias
 */
public class MyDecompressorInputStream extends InputStream
{
    InputStream in;
    ArrayList<ArrayList<Integer>> translatedBytes;
    int translatedIndex = 0, mainIndex = 0, bytesRead = 0;

    /**
     * Constructor
     * @param inputStream - streamer to decorate
     */
    public MyDecompressorInputStream(InputStream inputStream)
    {
        in = inputStream;
        translatedBytes = new ArrayList<>();
        translatedBytes.add(new ArrayList<>()); // 0 index is not used - reserved for flush delimiter
        translatedBytes.add(new ArrayList<>()); // root index (1) value is empty
    }

    @Override
    public int read() throws IOException
    {
        // check if a new information needs to be decompressed
        if(translatedIndex >= translatedBytes.get(mainIndex).size())
        {
            int currentByte = -1;

            // get next index - until MSB is not 1 (<= 127)
            ArrayList<Integer> bytesToConvert = new ArrayList<>();
            while ((currentByte = readFromIn()) > 127) bytesToConvert.add(currentByte);
            if(currentByte != -1) bytesToConvert.add(currentByte);

            if(bytesToConvert.isEmpty()) return -1; // end of stream

            // set main index (insert new array when needed)
            translatedIndex = 0; // reset
            int converted = convertBytesToIndex(bytesToConvert);

            if(converted == 0) // flush delimiter
            {
                // last prefix known and flushed stream
                bytesToConvert.clear();
                while ((currentByte = readFromIn()) > 127) bytesToConvert.add(currentByte);
                if(currentByte != -1) bytesToConvert.add(currentByte);
                mainIndex = convertBytesToIndex(bytesToConvert);
            }
            else
            {
                // inset new list base on the index-value pair
                ArrayList<Integer> newList = new ArrayList<>(translatedBytes.get(converted));
                newList.add(readFromIn()); // get value
                translatedBytes.add(newList);
                mainIndex = translatedBytes.size() - 1; // set the main list to the new one
            }
        }

        // get next byte from the translated list
        int result = translatedBytes.get(mainIndex).get(translatedIndex);
        translatedIndex++;
        return result;
    }

    /**
     * Read byte from the input stream and set the read count
     * @return - the byte that was read
     * @throws IOException
     */
    private int readFromIn() throws IOException
    {
        int result = in.read();
        if(result != -1) bytesRead++;
        return result;

    }

    /**
     * decompressed bitwise binary bytes to the Integer they represents
     * @param bytes - binary bytes to convert
     * @return - Integer
     */
    private int convertBytesToIndex(ArrayList<Integer> bytes)
    {
        ArrayList<Byte> toTranslate = new ArrayList<>();

        for (int i = 0; i < bytes.size(); i++)
        {
            ArrayList<Byte> tokens = toBytes(bytes.get(i));
            if(tokens.size() == 8) tokens.remove(tokens.size() - 1);
            toTranslate.addAll(tokens);
        }

        return transformBitwise(toTranslate);
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        // validate
        if(b == null) return 0;

        bytesRead = 0;

        int currentIndex = 0;
        int currentbyte = -1;

        // read to buffer
        while (currentIndex < b.length && (currentbyte = read()) != -1)
        {
            b[currentIndex] = (byte)currentbyte;
            currentIndex++;
        }

        // return number of bytes read
        if(currentbyte == -1) return -1;
        return currentIndex;
    }

    @Override
    public void close() throws IOException
    {
        in.close();
    }
}
