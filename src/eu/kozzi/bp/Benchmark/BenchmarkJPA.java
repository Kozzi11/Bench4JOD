package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.Bench4JODProperties;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorJPA;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;

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
    protected EntityManager em;
    protected EntityTransaction tx;

    BenchmarkJPA() {}

    BenchmarkJPA(GeneratorSetting generatorSetting) {
        String persistenceUnitName = generatorSetting.getPersistenceUnitName();

        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        em = entityManagerFactory.createEntityManager();
        NodeGeneratorBuilder nodeGeneratorBuilder = new NodeGeneratorBuilder(new NodeGeneratorJPA(), generatorSetting.getVariant());
        nodeGenerator = (NodeGeneratorJPA) nodeGeneratorBuilder.setMinChildren(generatorSetting.getMinChildren())
                .setMaxChildren(generatorSetting.getMaxChildren())
                .setNumberOfChildren(generatorSetting.getNumberOfChildren())
                .setNumberOfNodes(generatorSetting.getNumberOfNodes())
                .setHeight(generatorSetting.getHeight())
                .createNodeGenerator();
        nodeGenerator.setEntityManager(em);
    }

    @Override
    public void cleanup() {
        entityManagerFactory.close();
    }

    public void generateTree() {
        initialize();
        startTest();
        nodeGenerator.setEntityManager(em);
        tx = em.getTransaction();

        try {
            tx.begin();
            Node root = nodeGenerator.makeTree();
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Generate tree");
            finish();
        }
    }

    public Node getRoot() {
        return em.createQuery("SELECT n FROM Node n WHERE n.parent IS NULL", Node.class).getSingleResult();
    }

    public void updateRoot() {
        initialize();
        root = getRoot();
        startTest();
        tx = em.getTransaction();
        try {
            tx.begin();
            root.setMyValue(root.getMyValue() + 10);
            tx.commit();
        } catch (Exception exception) {
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Update root");
            finish();
        }
    }

    public void findLeafs() {
        initialize();
        startTest();
        String query = "SELECT n FROM Node n WHERE n.children IS EMPTY";

        List<Node> leafs = em.createQuery(query, Node.class).getResultList();
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
        finish();
    }

    public void addLeafs() {
        initialize();
        String query = "SELECT n FROM Node n WHERE n.children IS EMPTY";
        List<Node> leafs = em.createQuery(query, Node.class).getResultList();
        startTest();
        tx = em.getTransaction();
        try {
            tx.begin();
            for(Node leaf: leafs) {
                generateLeafChildren(leaf);
            }
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Generate leafs");
            finish();
        }
    }

    @Override
    public void findNodesWithValueDb(final int value) {
        initialize();
        String query = "SELECT n FROM Node n WHERE n.myValue = :searchValue";
        startTest();

        List<Node> nodes = em.createQuery(query, Node.class).setParameter("searchValue", value).getResultList();
        System.out.print("Find nodes db: ");
        System.out.println(nodes.size());
        stopTest("Find nodes by value in db");
        finish();
    }

    @Override
    public void makeBinaryTree() {
        initialize();
        String query = "SELECT n FROM Node n ORDER BY n.myValue DESC";
        List<Node> nodes = em.createQuery(query, Node.class).getResultList();
        tx = em.getTransaction();

        startTest();

        try {
            tx.begin();
            generateBinaryTree(nodes);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Generate binary tree");
            finish();
        }

    }

    @Override
    public void swapRootChildren() {
        initialize();
        tx = em.getTransaction();


        try {
            tx.begin();
            root = getRoot();

            startTest();

            List<Node> children = new ArrayList<Node>();

            Node leftChild = root.getChildren().get(0);
            Node rightChild = root.getChildren().get(1);

            Node newLeftChild = new Node();
            Node newRightChild = new Node();

            em.persist(newLeftChild);
            em.persist(newRightChild);

            newLeftChild.setChildren(rightChild.getChildren());
            newLeftChild.setMyValue(rightChild.getMyValue());
            newLeftChild.setParent(rightChild.getParent());

            newRightChild.setChildren(leftChild.getChildren());
            newRightChild.setMyValue(leftChild.getMyValue());
            newRightChild.setParent(leftChild.getParent());

            children.add(newLeftChild);
            children.add(newRightChild);

            for (Node child: rightChild.getChildren()) {
                child.setParent(newLeftChild);
            }

            for (Node child: leftChild.getChildren()) {
                child.setParent(newRightChild);
            }

            root.setChildren(children);

            rightChild.setChildren(new ArrayList<Node>());
            rightChild.setParent(null);

            leftChild.setChildren(new ArrayList<Node>());
            leftChild.setParent(null);

            em.remove(rightChild);
            em.remove(leftChild);

            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Swap root children");
            finish();
        }

    }

    public void deleteTree() {
        initialize();
        root = getRoot();
        tx = em.getTransaction();
        startTest();
        try {
            tx.begin();
            em.remove(root);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        } finally {
            stopTest("Delete tree");
            finish();
        }
    }


    @Override
    public void initialize() {
        Bench4JODProperties properties = Bench4JODProperties.getInstance();
        String cleanCache = properties.getProperty(Bench4JODProperties.Benchmark.CLEAN_CACHE, "false");
        if (Boolean.valueOf(cleanCache)) {
            entityManagerFactory.getCache().evictAll();
        }
        em = entityManagerFactory.createEntityManager();
    }

    @Override
    public void finish() {
        em.close();
    }

    protected void generateLeafChildren(Node leaf) {
        List<Node> children = new ArrayList<Node>();
        for (int index = 0; index < nodeGenerator.getNumberOfChildren(); ++index) {
            Node node = new Node();
            node.setMyValue(leaf.getMyValue());
            node.setParent(leaf);
            em.persist(node);
            children.add(node);
        }
        leaf.setChildren(children);
        em.persist(leaf);
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
                em.persist(node);
                children.add(node);
                queue2.offer(node);
            }
            parent.setChildren(children);
            em.persist(parent);
        }
    }
}
