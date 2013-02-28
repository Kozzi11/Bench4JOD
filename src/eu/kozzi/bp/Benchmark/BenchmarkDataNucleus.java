package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorJPA;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;
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
    BenchmarkDataNucleus(GeneratorSetting generatorSetting) {
        String persistenceUnitName = generatorSetting.getPersistenceUnitName();
        DataNucleusEnhancer enhancer = new DataNucleusEnhancer("JPA", null);
        enhancer.addPersistenceUnit(persistenceUnitName);
        enhancer.enhance();

        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        entityManager = entityManagerFactory.createEntityManager();
        NodeGeneratorBuilder nodeGeneratorBuilder = new NodeGeneratorBuilder(new NodeGeneratorJPA(), generatorSetting.getVariant());
        nodeGenerator = (NodeGeneratorJPA) nodeGeneratorBuilder.setMinChildren(generatorSetting.getMinChildren())
                .setMaxChildren(generatorSetting.getMaxChildren())
                .setNumberOfChildren(generatorSetting.getNumberOfChildren())
                .setNumberOfNodes(generatorSetting.getNumberOfNodes())
                .setHeight(generatorSetting.getHeight())
                .createNodeGenerator();
        nodeGenerator.setEntityManager(entityManager);
    }

    public void addLeafs() {

        initialize();
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
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Generate leafs");
            clear();
        }
    }

    @Override
    public void swapRootChildren() {
        initialize();
        tx = entityManager.getTransaction();
        startTest();
        stopTest("Swap root children");
        clear();

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
    }


    protected void generateBinaryTree(List<Node> nodes) {
        Iterator<Node> nodeIterator = nodes.iterator();
        Queue<Node> queue2 = new LinkedList<Node>();

        Node firstNode = nodeIterator.next();
        firstNode.setParent(null);
        queue2.offer(firstNode);

        while (queue2.isEmpty() == false) {
            Node parent = queue2.remove();
            //List<Node> children = new ArrayList<Node>();

            for (int index = 0; index < 2; ++index) {
                if (nodeIterator.hasNext() == false) break;
                Node node = nodeIterator.next();
                node.setParent(parent);
                //children.add(node);
                queue2.offer(node);
            }
            //parent.setChildren(children);
        }
    }
}
