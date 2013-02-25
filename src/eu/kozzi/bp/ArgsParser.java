package eu.kozzi.bp;

import eu.kozzi.bp.Exception.ArgsParserException;
import eu.kozzi.bp.Tree.NodeGenerator;
import eu.kozzi.bp.Tree.NodeGeneratorJPA;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.1.13
 * Time: 2:54
 * To change this template use File | Settings | File Templates.
 */
public class ArgsParser {
    private String[] args;
    private String persistenceUnitName;
    private NodeGeneratorJPA.Variant variant;
    private int minChildren;
    private int maxChildren;
    private int numberOfNodes;
    private int height;
    private int numberOfChildren;

    public ArgsParser(String args[]) throws ArgsParserException {
        this.args = args;
        if (args.length < 4) {
            throw new ArgsParserException();
        }

        int index = 0;
        for (String arg: this.args) {
            if (index == 0) {
                this.persistenceUnitName = arg;
            } else if (index == 1) {
                if (arg.equals("NODE_COUNT")) {
                    if (this.args.length < 5) {
                        throw new ArgsParserException();
                    }
                    this.variant = NodeGeneratorJPA.Variant.FIX_NODE_COUNT;
                } else if (arg.equals("TREE_HEIGHT")) {
                    this.variant = NodeGeneratorJPA.Variant.FIX_HEIGHT;
                } else {
                    throw new ArgsParserException("Wrong tree variant");
                }
            } else if (index == 2) {
                if (this.variant.equals(NodeGeneratorJPA.Variant.FIX_HEIGHT)) {
                    this.height = Integer.parseInt(arg);
                } else {
                    this.minChildren = Integer.parseInt(arg);
                }
            } else if (index == 3) {
                if (this.variant.equals(NodeGeneratorJPA.Variant.FIX_HEIGHT)) {
                    this.numberOfChildren = Integer.parseInt(arg);
                } else {
                    this.maxChildren = Integer.parseInt(arg);
                }
            } else if (index == 4) {
                if (this.variant.equals(NodeGeneratorJPA.Variant.FIX_HEIGHT)) {
                    throw new ArgsParserException();
                } else {
                    this.numberOfNodes = Integer.parseInt(arg);
                }
            } else {
                throw new ArgsParserException();
            }
            ++index;
        }
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public NodeGenerator.Variant getVariant() {
        return variant;
    }

    public int getMinChildren() {
        return minChildren;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getHeight() {
        return height;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }
}
