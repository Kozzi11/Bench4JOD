package eu.kozzi.bp.Tree;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */
public class NodeGeneratorOrientDb extends NodeGeneratorBase implements NodeGenerator {

    private ODatabaseObject db;
    private long currentId;

    @Override
    public Node makeTree() {

        Node node = db.newInstance(Node.class);
        node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
        this.numberOfNodesGenerated = 1;
        if (this.variant == Variant.FIX_HEIGHT) {
            this.makeSubTree(node, 1);
        }   else {
            this.makeSubTree(node);
        }
        db.save(node);

        return node;
    }

    public void setDb(ODatabaseObject db) {
        this.db = db;
    }

    private void makeSubTree(Node parent, int currentHeight) {
        if (this.variant == Variant.FIX_HEIGHT && currentHeight >= this.height) return;
        int childrenCount = this.numberOfChildren;
        List<Node> children = new ArrayList<Node>();

        for (int index = 0; index < childrenCount; ++index) {
            Node node = db.newInstance(Node.class);
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
                Node node = db.newInstance(Node.class);
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
