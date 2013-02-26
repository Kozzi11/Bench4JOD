package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorJPA;
import org.datanucleus.enhancer.DataNucleusEnhancer;

import javax.persistence.Persistence;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 17.2.13
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkDataNucleus extends BenchmarkJPA {
    BenchmarkDataNucleus(ArgsParser argsParser) {
        String persistenceUnitName = argsParser.getPersistenceUnitName();
        DataNucleusEnhancer enhancer = new DataNucleusEnhancer("JPA", null);
        enhancer.addPersistenceUnit(persistenceUnitName);
        enhancer.enhance();

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

    public void addLeafs() {
        String query = "SELECT n FROM Node n WHERE n.children IS EMPTY";
        tx = entityManager.getTransaction();
        try {
            tx.begin();
            List<Node> leafs = entityManager.createQuery(query, Node.class).getResultList();
            startTest();
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

    public void makeBinaryTree() {

        try {
            tx = entityManager.getTransaction();
            tx.begin();

            root = getRoot();

            String query = "SELECT n FROM Node n ORDER BY n.myValue DESC";
            List<Node> nodes = entityManager.createQuery(query, Node.class).getResultList();

            startTest();
            Iterator<Node> nodeIterator = nodes.iterator();
            Queue<Node> queue2 = new LinkedList<Node>();

            Node firstNode = nodeIterator.next();
            Node newParent = new Node();
            entityManager.persist(newParent);
            newParent.setMyValue(firstNode.getMyValue());
            newParent.setParent(null);
            queue2.offer(newParent);

            while (queue2.isEmpty() == false) {
                Node parent = queue2.remove();
                List<Node> children = new ArrayList<Node>();

                for (int index = 0; index < 2; ++index) {
                    if (nodeIterator.hasNext() == false) break;
                    Node node = nodeIterator.next();
                    Node newNode = new Node();
                    entityManager.persist(newNode);
                    newNode.setMyValue(node.getMyValue());
                    newNode.setParent(parent);
                    children.add(newNode);
                    queue2.offer(newNode);
                }

                parent.setChildren(children);
            }

            entityManager.remove(root);

            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Generate binary tree");

    }

    @Override
    public void swapRootChildren() {

        startTest();
        stopTest("Swap root children");
    }

    protected void generateLeafChildren(Node leaf) {
        List<Node> children = leaf.getChildren();
        for (int index = 0; index < nodeGenerator.getNumberOfChildren(); ++index) {
            Node node = new Node();
            entityManager.persist(node);
            node.setMyValue(leaf.getMyValue());
            node.setParent(leaf);
            children.add(node);
        }
        leaf.setChildren(children);
        //entityManager.persist(leaf);
    }

    protected void generateBinaryTree(List<Node> nodes) {


    }
}
