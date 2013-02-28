package eu.kozzi.bp.Benchmark;

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
        generateTree();
        findRoot();
        updateRoot();
        findLeafs();
        addLeafs();
        findNodesWithValue(17);
        findNodesWithValueDb(17);
        makeBinaryTree();
        swapRootChildren();
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
        System.out.print(msg + ": ");
        System.out.println(B.getTime() - A.getTime());
    }
}
