package eu.kozzi.bp.Tree;

import javax.persistence.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 16.1.13
 * Time: 8:27
 * To change this template use File | Settings | File Templates.
 */
public class NodeGeneratorJPA extends NodeGeneratorBase implements NodeGenerator {

    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    public Node makeTree() {

        Node node = new Node();
        this.em.persist(node);
        node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
        this.numberOfNodesGenerated = 1;
        if (this.variant == Variant.FIX_HEIGHT) {
            this.makeSubTree(node, 1);
        }   else {
            this.makeSubTree(node);
        }

        return node;
    }

    private void makeSubTree(Node parent, int currentHeight) {
        if (this.variant == Variant.FIX_HEIGHT && currentHeight >= this.height) return;
        int childrenCount = this.numberOfChildren;
        List<Node> children = new ArrayList<Node>();

        for (int index = 0; index < childrenCount; ++index) {
            Node node = new Node();
            this.em.persist(node);
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
                this.em.persist(node);
                node.setMyValue(this.numberGenerator.nextInt(this.numberOfNodes));
                node.setParent(parent);
                children.add(node);
                queue.offer(node);
            }
            parent.setChildren(children);
        }
    }
}
