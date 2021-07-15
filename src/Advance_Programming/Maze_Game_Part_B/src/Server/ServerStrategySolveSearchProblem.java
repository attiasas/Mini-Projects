package Advance_Programming.Maze_Game_Part_B.src.Server;

import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import java.io.*;

/**
 * This strategy creates a maze via client request (int[2] = {numOfRows,numOfColumns})
 * Created by Assaf Attias
 */
public class ServerStrategySolveSearchProblem implements IServerStrategy
{
    @Override
    public void serverStrategy(InputStream inFromClient, OutputStream outToClient)
    {
        try
        {
            // init
            ObjectInputStream in = new ObjectInputStream(inFromClient);
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
            out.flush();

            String solvedDirPath = System.getProperty("java.io.tmpdir") + "\\308214899_205381684";
            File solvedFolder = new File(solvedDirPath);
            solvedFolder.mkdir();

            Solution solution = null;

            // get maze from client
            Maze mazeToSolve = (Maze) in.readObject();

            // check if solved
            synchronized (this)
            {
                // get files
                String[] optionalSolutions = solvedFolder.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.startsWith("" + (mazeToSolve.hashCode()))) return true;
                        return false;
                    }
                });

                // search for known solution from files
                for (int i = 0; i < optionalSolutions.length && solution == null; i++)
                {
                    ObjectInputStream fileReader = new ObjectInputStream(new FileInputStream(solvedDirPath + "\\" + optionalSolutions[i]));
                    Maze savedMaze = (Maze)fileReader.readObject();
                    if(mazeToSolve.equals(savedMaze)) solution = (Solution)fileReader.readObject();
                    fileReader.close();
                }

                if(solution == null)
                {
                    // solve new Maze
                    SearchableMaze searchableMaze = new SearchableMaze(mazeToSolve);
                    ISearchingAlgorithm searchingAlgorithm = getAlgorithm();
                    solution = searchingAlgorithm.solve(searchableMaze);

                    // write solution to a file in temp dir
                    ObjectOutputStream solutionWriter = new ObjectOutputStream(new FileOutputStream(solvedDirPath + "\\" + mazeToSolve.hashCode() + "_" + optionalSolutions.length));
                    solutionWriter.writeObject(mazeToSolve);
                    solutionWriter.writeObject(solution);
                    solutionWriter.flush();
                    solutionWriter.close();
                }
            }
            // write to client
            out.writeObject(solution);
            out.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Factory to get a solving algorithm base on configurations
     * @return ISearchingAlgorithm
     */
    private ISearchingAlgorithm getAlgorithm()
    {
        String algorithmName = Configurations.getProperty("solvingAlgorithm");
        if(algorithmName == "Breadth First Search")
        {
            return new BreadthFirstSearch();
        }
        else if(algorithmName == "Depth First Search")
        {
            return new DepthFirstSearch();
        }
        else
        {
            return new BestFirstSearch();
        }
    }
}
