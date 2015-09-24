import graph.CyclicGraphException;
import graph.DependencyGraph;
import graph.DependencyGraphFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by gharizanov on 23.9.2015 �..
 */
public class GraphTest {

    public static final String CLASSPATH = new File("").getAbsolutePath() + "\\test";

    @Test
    public void testDependencyAdding(){
        try{
            DependencyGraph graph = new DependencyGraph();
            graph.addDependency("A", "B", "C");

            assertTrue(graph.hasDependency("A", "B"));
            assertTrue(!graph.hasDependency("B", "C"));
            assertTrue(graph.hasDependency("A", "C"));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDependencyRemoving(){
        try{
            DependencyGraph graph = new DependencyGraph();
            graph.addDependency("A", "B", "C");

            graph.removeDependency("A", "B");
            assertTrue(!graph.hasDependency("A", "B"));
            assertTrue(graph.hasDependency("A", "C"));

            graph.removeDependency("A", "A");

            assertTrue(graph.hasDependency("A", "C"));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void testInverseDependencyAdding(){
        try{
            DependencyGraph graph = new DependencyGraph();
            graph.addDependency("A", "B", "C");

            assertTrue(graph.hasInverseDependency("B", "A"));
            assertTrue(!graph.hasInverseDependency("B", "C"));
            assertTrue(graph.hasInverseDependency("C", "A"));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDuplicates() throws Exception{
        DependencyGraph graph = new DependencyGraph();

        graph.addDependency("A", "B", "C");
        graph.addDependency("A", "C", "D");

        List<String> dependencySet = graph.buildFullDependencySet();

        for (String dependency : dependencySet) {
            assertTrue(dependency.indexOf("C") == dependency.lastIndexOf("C"));
        }

        graph.removeDependency("A", "C");

        assertTrue(!graph.hasDependency("A", "C"));
    }

    @Test
    public void testInverseDependencyRemoving(){
        try{
            DependencyGraph graph = new DependencyGraph();
            graph.addDependency("A", "B", "C");

            graph.removeDependency("A", "B");

            assertTrue(!graph.hasInverseDependency("B", "A"));
            assertTrue(graph.hasInverseDependency("C", "A"));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void buildFromFile(){
        final String FILENAME = "simpleGraph.txt";

        System.out.printf("Testing graph from %s..", FILENAME);

        final String expectedResult = "A B\n";

        try{
            DependencyGraph graph = DependencyGraphFactory.createDependencyGraph(new FileInputStream(CLASSPATH + "\\data\\" + FILENAME));
            List<String> result = graph.buildFullDependencySet();

            String resultString = buildStringFromDependencyList(result);

            System.out.println(resultString);

            assertTrue(graph.hasDependency("A", "B"));
            assertEquals(expectedResult, resultString);

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }
    @Test
    public void testBuildFromEmptyFile(){
        final String FILENAME = "emptyGraph.txt";

        System.out.printf("Testing graph from %s..", FILENAME);

        try{
            DependencyGraph graph = DependencyGraphFactory.createDependencyGraph(new FileInputStream(CLASSPATH + "\\data\\" + FILENAME));
            List<String> result = graph.buildFullDependencySet();

            String resultString = buildStringFromDependencyList(result);
            assertTrue("".equals(resultString));

            System.out.println(resultString);

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void runExample(){
        final String FILENAME = "example.txt";

        System.out.printf("Testing graph from %s..", FILENAME);

        final String expectedResult = "" +
                "A B C E F G H\n" +
                "B C E F G H\n" +
                "C G\n" +
                "D A B C E F G H\n" +
                "E F H\n" +
                "F H\n";

        try{
            DependencyGraph graph = DependencyGraphFactory.createDependencyGraph(new FileInputStream(CLASSPATH + "\\data\\" + FILENAME));
            List<String> result = graph.buildFullDependencySet();

            String actualResult = buildStringFromDependencyList(result);

            System.out.println(actualResult);

            assertEquals(expectedResult.length(), actualResult.length());
            assertTrue(dependenciesEqual(expectedResult, actualResult));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void runInverseExample(){
        System.out.println("Testing inverse graph from example.txt..");

        final String expectedResult = "" +
                "A D\n" +
                "B A D\n" +
                "C A B D\n" +
                "E B A D\n" +
                "F D E B A\n" +
                "G C A B D\n" +
                "H F D E B A\n";

        try{
            DependencyGraph graph = DependencyGraphFactory.createDependencyGraph(new FileInputStream(CLASSPATH + "\\data\\example.txt"));
            List<String> result = graph.buildFullInverseDependencySet();

            String actualResult = buildStringFromDependencyList(result);

            System.out.println(actualResult);

            assertEquals(expectedResult.length(), actualResult.length());
            assertTrue(dependenciesEqual(expectedResult, actualResult));

        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    private String buildStringFromDependencyList(List<String> dependencies){
        StringBuilder sb = new StringBuilder();

        for (String dependency : dependencies) {
            sb.append(String.format("%s\n", dependency));
        }

        return sb.toString();
    }

    private boolean dependenciesEqual(String expectedResult, String actualResult){

        String[] expectedResultLines = expectedResult.split("\n");
        String[] actualResultLines = actualResult.split("\n");

        for (int i = 0; i < expectedResultLines.length; i++) {
            assertEquals(expectedResultLines[i].length(), actualResultLines[i].length());

            String[] expectedResultLineDependencies = expectedResultLines[i].split(" ");
            String[] actualResultLineDependencies = actualResultLines[i].split(" ");

            Arrays.sort(expectedResultLineDependencies);
            Arrays.sort(actualResultLineDependencies);

            if(!Arrays.equals(expectedResultLineDependencies, actualResultLineDependencies)){
                return false;
            }
        }
        return true;
    }

    @Test(expected = CyclicGraphException.class)
    public void testCyclicGraph() throws Exception{
        DependencyGraph graph = new DependencyGraph();
        graph.addDependency("A","B");
        graph.addDependency("B","A");
    }
}
