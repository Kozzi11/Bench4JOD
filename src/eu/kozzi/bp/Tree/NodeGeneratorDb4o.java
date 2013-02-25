package eu.kozzi.bp.Tree;

import com.db4o.ObjectContainer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 11.2.13
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class NodeGeneratorDb4o extends NodeGeneratorBase implements NodeGenerator {

    private ObjectContainer db;
    private long currentId = 0;

    @Override
    public Node makeTree() {

        Node node = new Node();
        node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
        this.numberOfNodesGenerated = 1;
        if (this.variant == Variant.FIX_HEIGHT) {
            this.makeSubTree(node, 1);
        }   else {
            this.makeSubTree(node);
        }
        db.store(node);

        return node;
    }

    public void setDb(ObjectContainer db) {
        this.db = db;
    }

    private void makeSubTree(Node parent, int currentHeight) {
        if (this.variant == Variant.FIX_HEIGHT && currentHeight >= this.height) return;
        int childrenCount = this.numberOfChildren;
        List<Node> children = new ArrayList<Node>();

        for (int index = 0; index < childrenCount; ++index) {
            Node node = new Node();
            node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
            node.setParent(parent);
            children.add(node);
            this.makeSubTree(node, currentHeight + 1);
        }
        parent.setChildren(children);
    }

    private void makeSubTree(Node parent) {
        Queue<Node> queue = new LinkedList<Node>();
        queue.offer(parent);
        while (queue.size() >= 0) {
            parent = queue.remove();
            int childrenCount = numberOfNodesGenerator.nextInt(this.maxChildren - this.minChildren) + this.minChildren;
            List<Node> children = new ArrayList<Node>();

            for (int index = 0; index < childrenCount; ++index) {
                if (this.numberOfNodesGenerated >= this.numberOfNodes) return;
                Node node = new Node();
                this.numberOfNodesGenerated++;
                node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
                node.setParent(parent);
                children.add(node);
                queue.offer(node);
            }
            parent.setChildren(children);
        }
    }
}
