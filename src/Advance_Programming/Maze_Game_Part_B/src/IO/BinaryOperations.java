package Advance_Programming.Maze_Game_Part_B.src.IO;

import java.util.ArrayList;

/**
 * a class that holds static function for binary bytes operations in the process of compressing using bitwise dynamic representation of an integer
 * Created by Assaf Attias
 */
public class BinaryOperations
{

    /**
     * Convert an integer to a binary bytes
     * @param i - integer to convert
     * @return - array list of binary bytes
     */
    public static ArrayList<Byte> toBytes(int i)
    {
        ArrayList<Byte> result = new ArrayList<>();

        while (i > 0)
        {
            result.add((byte)(i % 2));
            i /= 2;
        }

        return result;
    }

    /**
     * Translate an array of binary bytes representing a bitwise compressed integer to an integer in the range of byte
     * @param bytes - byte array to convert
     * @return a byte representation of the array
     */
    public static byte transformToBitwise(ArrayList<Byte> bytes)
    {
        byte result = 0;

        for(int i = 0; i < bytes.size(); i++)
        {
            byte b = bytes.get(i);
            if(b != 1 && b != 0) return -1;
            result += (b* Math.pow(2,i));
        }

        return result;
    }

    /**
     * Translate an array of binary bytes representing a bitwise compressed integer to an integer in the range of byte
     * @param bytes - byte array to convert
     * @return a byte representation of the array
     */
    public static int transformBitwise(ArrayList<Byte> bytes)
    {
        int result = 0;

        for(int i = 0; i < bytes.size(); i++)
        {
            int b = bytes.get(i);
            if(b != 1 && b != 0) return -1;
            result += (b* Math.pow(2,i));
        }

        return result;
    }
}
