package Advance_Programming.Maze_Game_Part_B.src.Server;

import IO.MyCompressorOutputStream;
import algorithms.mazeGenerators.*;

import java.io.*;

/**
 * This strategy creates a maze via client request (int[2] = {numOfRows,numOfColumns}) and sends it to the client
 * Created by Assaf Attias
 */
public class ServerStrategyGenerateMaze implements IServerStrategy
{
    @Override
    public void serverStrategy(InputStream inFromClient, OutputStream outToClient)
    {
        try
        {
            // init
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(inFromClient);

            // read dimensions from client
            int[] parameters = (int[])in.readObject();

            if(parameters != null && parameters.length >= 2)
            {
                // create maze
                IMazeGenerator generator = getGenerator();
                Maze maze = generator.generate(parameters[0],parameters[1]);
                byte[] byteMaze = maze.toByteArray();
                // deflate (compress)
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                MyCompressorOutputStream deflater = new MyCompressorOutputStream(byteStream);
                deflater.write(byteMaze);
                deflater.flush();
                deflater.close();
                // write to client
                out.writeObject(byteStream.toByteArray());
            }
            else
            {
                // wrong arguments
                out.writeObject(null);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Factory to get a Maze generator base on configurations
     * @return - IMazeGenerator
     */
    private IMazeGenerator getGenerator()
    {
        String type = Configurations.getProperty("generatorType");
        if(type == "empty")
        {
            return new EmptyMazeGenerator();
        }
        else if(type == "simple")
        {
            return new SimpleMazeGenerator();
        }
        else
        {
            return new MyMazeGenerator();
        }
    }
}
