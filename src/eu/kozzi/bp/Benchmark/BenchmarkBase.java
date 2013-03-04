package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.Bench4JODProperties;
import eu.kozzi.bp.Tree.Node;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 17.2.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public abstract class BenchmarkBase implements Benchmark {

    protected Node root;
    private int totalTime;
    private Timestamp A;
    private Timestamp B;

    @Override
    public void run() {

        Bench4JODProperties properties = Bench4JODProperties.getInstance();

        generateTree();
        findRoot();
        updateRoot();
        findLeafs();
        addLeafs();
        findNodesWithValue(Integer.valueOf(properties.getProperty(Bench4JODProperties.Benchmark.FIND_NODE_VALUE)));
        findNodesWithValueDb(Integer.valueOf(properties.getProperty(Bench4JODProperties.Benchmark.FIND_NODE_DB_VALUE)));
        exploreTree(properties.getProperty(Bench4JODProperties.Benchmark.EXPLORE_PATH));
        makeBinaryTree();
        swapRootChildren();
        exploreBinaryTree(properties.getProperty(Bench4JODProperties.Benchmark.EXPLORE_BINARY_PATH));
        computeTreeHeight();
        deleteTree();
        cleanup();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void findRoot() {
        initialize();
        startTest();
        root = getRoot();
        stopTest("Find tree root");
        finish();
    }

    @Override
    public void computeTreeHeight() {
        initialize();
        root = getRoot();
        startTest();
        int height = root.getHeight();
        System.out.print("Tree height is: ");
        System.out.println(height);
        stopTest("Compute tree height");
        finish();
    }

    @Override
    public void findNodesWithValue(final int value) {
        initialize();
        root = getRoot();
        startTest();
        List<Node> nodes = root.findByValue(value);
        System.out.print("Find nodes: ");
        System.out.println(nodes.size());
        stopTest("Find nodes by value");
        finish();
    }

    @Override
    public void exploreTree(String pathString) {

        String[] path = pathString.split(",");
        initialize();
        root = getRoot();
        startTest();
        try {
            Node node = root.findByPath(path);
            System.out.print("Find node explore tree: ");
            System.out.println(node.getMyValue());
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        stopTest("Find node explore tree duration");
        finish();
    }

    @Override
    public void exploreBinaryTree(String pathString) {

        String[] path = pathString.split(",");
        initialize();
        root = getRoot();
        startTest();
        try {
            Node node = root.findByPath(path);
            System.out.print("Find node explore binary tree: ");
            System.out.println(node.getMyValue());
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        stopTest("Find node explore binary tree duration");
        finish();
    }

    @Override
    public int getTotalTime() {
        return totalTime;
    }


    protected void startTest() {
        Date date = new java.util.Date();
        A = new Timestamp(date.getTime());
    }

    protected void stopTest(String msg) {
        Date date = new java.util.Date();
        B = new Timestamp(date.getTime());
        totalTime += B.getTime() - A.getTime();
        System.err.print(msg + ": ");
        System.err.println(B.getTime() - A.getTime());
    }
}
