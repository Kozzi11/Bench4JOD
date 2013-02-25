package eu.kozzi.bp.Tree;

import javax.persistence.EntityManager;

public class NodeGeneratorBuilder {
    private int minChildren;
    private int maxChildren;
    private int numberOfNodes;
    private int height;
    private int numberOfChildren;
    private NodeGenerator nodeGenerator;

    NodeGenerator.Variant variant;

    public void setNodeGenerator(NodeGenerator nodeGenerator) {
        this.nodeGenerator = nodeGenerator;
    }

    public NodeGeneratorBuilder(NodeGenerator nodeGenerator, NodeGenerator.Variant variant) {
        this.nodeGenerator = nodeGenerator;
        this.variant = variant;
    }

    public NodeGeneratorBuilder setMinChildren(int minChildren) {
        this.minChildren = minChildren;
        return this;
    }

    public NodeGeneratorBuilder setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
        return this;
    }

    public NodeGeneratorBuilder setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        return this;
    }

    public NodeGeneratorBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public NodeGeneratorBuilder setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        return this;
    }

    public NodeGenerator createNodeGenerator() {
        nodeGenerator.setVariant(variant);
        nodeGenerator.setMinChildren(minChildren);
        nodeGenerator.setMaxChildren(maxChildren);
        nodeGenerator.setNumberOfNodes(numberOfNodes);
        nodeGenerator.setNumberOfChildren(numberOfChildren);
        nodeGenerator.setHeight(height);
        return nodeGenerator.init();
    }
}