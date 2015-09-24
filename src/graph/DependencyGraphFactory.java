package graph;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by gharizanov on 23.9.2015 ã..
 */
public class DependencyGraphFactory {

    public static DependencyGraph createDependencyGraph(InputStream is) throws Exception{
        Scanner scanner = new Scanner(is);
        DependencyGraph result = new DependencyGraph();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] dependencies = line.split(" ");
            result.addDependency(dependencies[0], Arrays.copyOfRange(dependencies, 1, dependencies.length));
        }

        return result;
    }

}
