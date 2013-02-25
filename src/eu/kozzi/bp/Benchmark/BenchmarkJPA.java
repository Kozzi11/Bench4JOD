package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorJPA;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 9:40
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkJPA extends BenchmarkBase implements Benchmark {

    protected NodeGeneratorJPA nodeGenerator;
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected EntityTransaction tx;

    BenchmarkJPA() {}

    BenchmarkJPA(ArgsParser argsParser) {
        String persistenceUnitName = argsParser.getPersistenceUnitName();

        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        entityManager = entityManagerFactory.createEntityManager();
        NodeGeneratorBuilder nodeGeneratorBuilder = new NodeGeneratorBuilder(new NodeGeneratorJPA(), argsParser.getVariant());
        nodeGenerator = (NodeGeneratorJPA) nodeGeneratorBuilder.setMinChildren(argsParser.getMinChildren())
                .setMaxChildren(argsParser.getMaxChildren())
                .setNumberOfChildren(argsParser.getNumberOfChildren())
                .setNumberOfNodes(argsParser.getNumberOfNodes())
                .setHeight(argsParser.getHeight())
                .createNodeGenerator();
        nodeGenerator.setEntityManager(entityManager);
    }

    @Override
    public void cleanup() {
        entityManager.close();
        entityManagerFactory.close();
    }

    public void generateTree() {
        startTest();
        tx = entityManager.getTransaction();

        try {
            tx.begin();
            Node root = nodeGenerator.makeTree();
            tx.commit();
        } catch (Exception exception) {
            tx.rollback();
        }
        stopTest("Generate tree");
    }

    public Node getRoot() {
        return entityManager.createQuery("SELECT n FROM Node n WHERE n.parent IS NULL", Node.class).getSingleResult();
    }

    public void updateRoot() {
        root = getRoot();
        startTest();
        tx = entityManager.getTransaction();
        try {
            tx.begin();
            root.setMyValue(root.getMyValue() + 10);
            tx.commit();
        } catch (Exception exception) {
            tx.rollback();
        }
        stopTest("Update root");
    }

    public void findLeafs() {
        startTest();
        String query = "SELECT n FROM Node n WHERE n.children IS EMPTY";

        List<Node> leafs = entityManager.createQuery(query, Node.class).getResultList();
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
    }

    public void addLeafs() {
        String query = "SELECT n FROM Node n WHERE n.children IS EMPTY";
        List<Node> leafs = entityManager.createQuery(query, Node.class).getResultList();
        startTest();
        tx = entityManager.getTransaction();
        try {
            tx.begin();
            for(Node leaf: leafs) {
                generateLeafChildren(leaf);
            }
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Generate leafs");
    }

    @Override
    public void findNodesWithValueDb(final int value) {
        String query = "SELECT n FROM Node n WHERE n.myValue = :searchValue";
        startTest();

        List<Node> nodes = entityManager.createQuery(query, Node.class).setParameter("searchValue", value).getResultList();
        System.out.print("Find nodes db: ");
        System.out.println(nodes.size());
        stopTest("Find nodes by value in db");
    }

    @Override
    public void makeBinaryTree() {
        tx = entityManager.getTransaction();
        String query = "SELECT n FROM Node n ORDER BY n.myValue DESC";
        List<Node> nodes = entityManager.createQuery(query, Node.class).getResultList();

        startTest();

        try {
            tx.begin();
            generateBinaryTree(nodes);
            tx.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            tx.rollback();
        }
        stopTest("Generate binary tree");

    }

    @Override
    public void swapRootChildren() {
        tx = entityManager.getTransaction();
        root = getRoot();
        startTest();

        try {
            tx.begin();
            Node rootLeftChild = root.getChildren().get(0);
            Node rootRightChild = root.getChildren().get(1);
            List<Node> leftChildChildren = new ArrayList<Node>(rootLeftChild.getChildren());
            List<Node> rightChildChildren = new ArrayList<Node>(rootRightChild.getChildren());
            rootLeftChild.setChildren(rightChildChildren);
            rootRightChild.setChildren(leftChildChildren);
            entityManager.persist(rootLeftChild);
            entityManager.persist(rootRightChild);
            root.getChildren().clear();
            root.getChildren().add(rootRightChild);
            root.getChildren().add(rootLeftChild);
            entityManager.persist(root);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Swap root children");

    }

    public void deleteTree() {
        entityManager.clear();
        root = getRoot();
        tx = entityManager.getTransaction();
        startTest();
        try {
            tx.begin();
            entityManager.remove(root);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Delete tree");
    }

    protected void generateLeafChildren(Node leaf) {
        List<Node> children = new ArrayList<Node>();
        for (int index = 0; index < nodeGenerator.getNumberOfChildren(); ++index) {
            Node node = new Node();
            node.setMyValue(leaf.getMyValue());
            node.setParent(leaf);
            entityManager.persist(node);
            children.add(node);
        }
        leaf.setChildren(children);
        entityManager.persist(leaf);
    }

    protected void generateBinaryTree(List<Node> nodes) {
        Queue<Node> queue = new LinkedList<Node>(nodes);
        Queue<Node> queue2 = new LinkedList<Node>();

        Node firstNode = queue.remove();
        firstNode.setParent(null);
        queue2.offer(firstNode);

        while (queue2.isEmpty() == false) {
            Node parent = queue2.remove();
            List<Node> children = new ArrayList<Node>();

            for (int index = 0; index < 2; ++index) {
                if (queue.isEmpty()) break;
                Node node = queue.remove();
                node.setParent(parent);
                entityManager.persist(node);
                children.add(node);
                queue2.offer(node);
            }
            parent.setChildren(children);
            entityManager.persist(parent);
        }
    }
}
