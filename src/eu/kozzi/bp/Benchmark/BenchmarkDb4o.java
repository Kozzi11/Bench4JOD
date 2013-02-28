package eu.kozzi.bp.Benchmark;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import com.db4o.query.Predicate;
import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Bench4JODProperties;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorDb4o;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;


import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 11.2.13
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkDb4o extends BenchmarkBase implements Benchmark {

    private ObjectContainer db;
    private ObjectContainer objectContainer;
    private NodeGeneratorDb4o nodeGenerator;

    BenchmarkDb4o(GeneratorSetting generatorSetting) {

        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(Node.class).cascadeOnDelete(true);

        objectContainer = Db4oEmbedded.openFile(config, "db4odb");

        NodeGeneratorBuilder nodeGeneratorBuilder = new NodeGeneratorBuilder(new NodeGeneratorDb4o(), generatorSetting.getVariant());
        nodeGenerator = (NodeGeneratorDb4o) nodeGeneratorBuilder.setMinChildren(generatorSetting.getMinChildren())
                .setMaxChildren(generatorSetting.getMaxChildren())
                .setNumberOfChildren(generatorSetting.getNumberOfChildren())
                .setNumberOfNodes(generatorSetting.getNumberOfNodes())
                .setHeight(generatorSetting.getHeight())
                .createNodeGenerator();
        nodeGenerator.setDb(objectContainer);
    }

    @Override
    public void cleanup() {
        objectContainer.close();
    }

    @Override
    public void generateTree() {
        initialize();
        startTest();
        try {
            Node root = nodeGenerator.makeTree();
            db.commit();
        } catch (Exception exception) {
            db.rollback();
        } finally {
            stopTest("Generate tree");
            clear();
        }

    }

    @Override
    public Node getRoot() {
        List<Node> nodes = db.query(new Predicate<Node>() {
            public boolean match(Node node) {
                return node.getParent() == null;
            }
        });
        return nodes.get(0);
    }

    @Override
    public void updateRoot() {
        initialize();
        root = getRoot();
        startTest();
        try {
            root.setMyValue(root.getMyValue() + 10);
            db.store(root);
            db.commit();
        } catch (Exception exception) {
            db.rollback();
        } finally {
            stopTest("Update root");
            clear();
        }
    }

    @Override
    public void findLeafs() {
        initialize();
        startTest();
        List<Node> leafs = db.query(new Predicate<Node>() {
            public boolean match(Node node) {
                return node.getChildren().isEmpty();
            }
        });
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
        clear();
    }

    @Override
    public void addLeafs() {
        initialize();
        List<Node> leafs = db.query(new Predicate<Node>() {
            public boolean match(Node node) {
                return node.getChildren().isEmpty();
            }
        });
        startTest();
        try {
            for(Node leaf: leafs) {
                generateLeafChildren(leaf);
                db.store(leaf);
            }
            db.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            db.rollback();
        } finally {
            stopTest("Generate leafs");
            clear();
        }
    }

    @Override
    public void findNodesWithValueDb(final int value) {
        initialize();
        startTest();
        List<Node> nodes = db.query(new Predicate<Node>() {
            public boolean match(Node node) {
                return node.getMyValue() == value;
            }
        });
        System.out.print("Find nodes db: ");
        System.out.println(nodes.size());
        stopTest("Find nodes by value in db");
        clear();
    }

    @Override
    public void makeBinaryTree() {

        initialize();

        Comparator<Node> comparator = new Comparator<Node>() {
            @Override
            public int compare(Node node, Node node2) {
                return node.getMyValue().compareTo(node2.getMyValue());
            }
        };

        Predicate<Node> predicate = new Predicate<Node>() {
            public boolean match(Node node) {
                return true;
            }
        };
        List<Node> nodes = db.query(predicate, comparator);

        startTest();

        try {
            generateBinaryTree(nodes);
            db.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            db.rollback();
        } finally {
            stopTest("Generate binary tree");
            clear();
        }
    }

    @Override
    public void swapRootChildren() {

        initialize();

        root = getRoot();
        startTest();

        try {

            List<Node> children = new ArrayList<Node>();

            Node leftChild = root.getChildren().get(0);
            Node rightChild = root.getChildren().get(1);

            Node newLeftChild = new Node();
            Node newRightChild = new Node();

            db.store(newLeftChild);
            db.store(newRightChild);

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

            db.delete(rightChild);
            db.delete(leftChild);

            db.store(root);
            db.commit();

        } catch (Exception exception) {
            exception.printStackTrace();
            db.rollback();
        } finally {
            stopTest("Swap root children");
            clear();
        }

    }

    @Override
    public void deleteTree() {

        initialize();

        root = getRoot();
        startTest();
        try {
            db.delete(root);
            db.commit();
        } catch (Exception exception) {
            db.rollback();
        } finally {
            stopTest("Delete tree");
            clear();
        }
    }

    @Override
    public void initialize() {
        Bench4JODProperties properties = Bench4JODProperties.getInstance();
        String cleanCache = properties.getProperty(Bench4JODProperties.Benchmark.CLEAN_CACHE, "false");
        if (Boolean.valueOf(cleanCache)) {
            db = objectContainer.ext().openSession();
        } else {
            db = objectContainer;
        }
    }

    @Override
    public void clear() {
        Bench4JODProperties properties = Bench4JODProperties.getInstance();
        String cleanCache = properties.getProperty(Bench4JODProperties.Benchmark.CLEAN_CACHE, "false");
        if (Boolean.valueOf(cleanCache)) {
            db.close();
        }
    }

    private void generateLeafChildren(Node leaf) {
        List<Node> children = new ArrayList<Node>();
        for (int index = 0; index < nodeGenerator.getNumberOfChildren(); ++index) {
            Node node = new Node();
            node.setMyValue(leaf.getMyValue());
            node.setParent(leaf);
            children.add(node);
        }
        leaf.setChildren(children);
    }

    private void generateBinaryTree(List<Node> nodes) {
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
                children.add(node);
                db.store(node);
                queue2.offer(node);
            }
            parent.setChildren(children);
            db.store(parent);
        }
    }
}
