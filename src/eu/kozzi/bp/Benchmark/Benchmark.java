package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
public interface Benchmark {

    public void run();

    public int getTotalTime();

    public void generateTree();

    public void findRoot();

    public Node getRoot();

    public void updateRoot();

    public void computeTreeHeight();

    public void findLeafs();

    public void addLeafs();

    public void findNodesWithValueDb(final int value);

    public void findNodesWithValue(final int value);

    public void makeBinaryTree();

    public void swapRootChildren();

    public void exploreTree(String path);

    public void exploreBinaryTree(String path);

    public void deleteTree();

    public void initialize();

    public void finish();

    public void cleanup();

}
