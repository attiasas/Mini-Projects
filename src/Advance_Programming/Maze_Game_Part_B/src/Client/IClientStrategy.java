package Advance_Programming.Maze_Game_Part_B.src.Client;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents an interface for client strategy when communicating with a server
 * Created by Assaf Attias
 */
public interface IClientStrategy
{
    /**
     * Strategy to execute when communicating with a server
     * @param inFromServer - InputStream of a server socket
     * @param outToServer - OutputStream of a server socket
     */
    void clientStrategy(InputStream inFromServer, OutputStream outToServer);
}
