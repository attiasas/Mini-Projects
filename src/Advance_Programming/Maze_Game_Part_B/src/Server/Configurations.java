package Advance_Programming.Maze_Game_Part_B.src.Server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * This static class represents the configurations of the project, can store and load properties and has a main function to load default values
 * Created by Assaf Attias
 */
public final class Configurations
{
    private static Properties properties = null;
    private static Object lock = new Object();

    /**
     * Get a property value from the stored configurations
     * @param label - ths key that was used to store the property
     * @return string property value that was stored
     */
    public static String getProperty(String label)
    {
        loadProperties();

        return properties.getProperty(label);
    }

    // Init properties if needed and load from file
    private static void loadProperties()
    {
        synchronized (lock)
        {
            if(properties == null)
            {
                properties = new Properties();

                try(InputStream input = new FileInputStream("resources/config.properties"))
                {
                    properties.load(input);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set a property to the configuration files that can be found later by a given label (key)
     * @param label - key that define tha property
     * @param value - configuration value to store
     */
    public static void setProperties(String label, String value)
    {
        loadProperties();
        properties.setProperty(label,value);

        synchronized (lock)
        {
            try(OutputStream out = new FileOutputStream("resources/config.properties"))
            {
                properties.store(out,null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * Set default configurations property
     * @param args
     */
    public static void main(String[] args)
    {
        setProperties("poolSize","4");
        setProperties("solvingAlgorithm","Best First Search"); // Best First Search / Breadth First Search / Depth First Search
        setProperties("generatorType","myMaze"); // myMaze / empty / simple
    }
}
