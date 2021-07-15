package Advance_Programming.Maze_Game_Part_B.src.Server;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents an interface for server strategy when communicating with a client
 * Created by Assaf Attias
 */
public interface IServerStrategy
{
    /**
     * Strategy to execute when communicating with a client
     * @param inFromClient - InputStream of a socket
     * @param outToClient - OutputStream of a socket
     */
    void serverStrategy(InputStream inFromClient, OutputStream outToClient);
}
