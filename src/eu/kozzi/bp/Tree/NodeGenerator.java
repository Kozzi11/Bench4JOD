package eu.kozzi.bp.Tree;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public interface NodeGenerator {

    public enum Variant {
        FIX_HEIGHT,
        FIX_NODE_COUNT
    }

    static final long SEED = 19580427;

    public NodeGenerator init();

    public int getHeight();

    public void setHeight(int height);

    public int getNumberOfChildren();

    public void setNumberOfChildren(int numberOfChildren);

    public int getNumberOfNodes();

    public void setNumberOfNodes(int numberOfNodes);

    public int getMaxChildren();

    public void setMaxChildren(int maxChildren);

    public int getMinChildren();

    public void setMinChildren(int minChildren);

    public Variant getVariant();

    public void setVariant(Variant variant);

    public Node makeTree();
}
