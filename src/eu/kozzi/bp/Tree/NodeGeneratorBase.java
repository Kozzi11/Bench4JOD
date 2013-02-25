package eu.kozzi.bp.Tree;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class NodeGeneratorBase implements NodeGenerator {

    protected int height;

    protected int numberOfChildren;

    protected int minChildren;

    protected int maxChildren;

    protected int numberOfNodes;

    protected int numberOfNodesGenerated;

    protected Random numberGenerator;

    protected Random numberOfNodesGenerator;

    protected NodeGenerator.Variant variant;

    public NodeGenerator init() {
        if (this.variant == NodeGenerator.Variant.FIX_HEIGHT) {
            for (int index = 0; index < height; ++index) {
                this.numberOfNodes += numberOfChildren ^ index;
            }
        }

        this.numberGenerator = new Random(SEED);
        this.numberOfNodesGenerator = new Random(SEED);

        return this;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
    }

    public int getMinChildren() {
        return minChildren;
    }

    public void setMinChildren(int minChildren) {
        this.minChildren = minChildren;
    }

    public NodeGenerator.Variant getVariant() {
        return variant;
    }

    public void setVariant(NodeGenerator.Variant variant) {
        this.variant = variant;
    }
}
