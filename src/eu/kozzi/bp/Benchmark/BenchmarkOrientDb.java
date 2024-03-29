package eu.kozzi.bp.Benchmark;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import eu.kozzi.bp.Bench4JODProperties;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.NodeGeneratorBuilder;
import eu.kozzi.bp.Tree.NodeGeneratorOrientDb;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 5.2.13
 * Time: 9:54
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkOrientDb extends BenchmarkBase implements Benchmark {

    private NodeGeneratorOrientDb nodeGenerator;
    private ODatabaseObject db;
    private OTransaction tx;

    BenchmarkOrientDb(GeneratorSetting generatorSetting) {

        db = new OObjectDatabaseTx("local:orientdbtest");
        if (db.exists() == false) {
            db.create();
        } else {
            db.open("admin","admin");
            db.drop();
            db.create();
        }
        db.getEntityManager().registerEntityClass(Node.class);

        NodeGeneratorBuilder nodeGeneratorBuilder = new NodeGeneratorBuilder(new NodeGeneratorOrientDb(), generatorSetting.getVariant());
        nodeGenerator = (NodeGeneratorOrientDb) nodeGeneratorBuilder.setMinChildren(generatorSetting.getMinChildren())
                .setMaxChildren(generatorSetting.getMaxChildren())
                .setNumberOfChildren(generatorSetting.getNumberOfChildren())
                .setNumberOfNodes(generatorSetting.getNumberOfNodes())
                .setHeight(generatorSetting.getHeight())
                .createNodeGenerator();
        nodeGenerator.setDb(db);
    }

    @Override
    public void generateTree() {
        initialize();
        startTest();
        tx = db.getTransaction();

        try {
            tx.begin();
            Node root = nodeGenerator.makeTree();
            tx.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            tx.rollback();
        } finally {
            stopTest("Generate tree");
            finish();
        }
    }

    @Override
    public Node getRoot() {
        List<Node> result = db.query(new OSQLSynchQuery<Node>("SELECT * FROM Node WHERE parent IS NULL"));
        return result.get(0);
    }

    @Override
    public void updateRoot() {
        initialize();
        root = getRoot();
        startTest();
        tx = db.getTransaction();
        try {
            tx.begin();
            root.setMyValue(root.getMyValue() + 10);
            db.save(root);
            tx.commit();
        } catch (Exception exception) {
            tx.rollback();
        } finally {
            stopTest("Update root");
            finish();
        }
    }

    @Override
    public void findLeafs() {
        initialize();
        startTest();
        String query = "SELECT * FROM Node WHERE children.size() = 0 OR children IS NULL";
        List<Node> leafs = db.query(new OSQLSynchQuery<Node>(query));
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
        finish();
    }

    @Override
    public void addLeafs() {
        initialize();
        String query = "SELECT * FROM Node WHERE children.size() = 0 OR children IS NULL";
        List<Node> leafs = db.query(new OSQLSynchQuery<Node>(query));
        startTest();
        tx = db.getTransaction();
        try {
            tx.begin();
            for(Node leaf: leafs) {
                generateLeafChildren(leaf);
                db.save(leaf);
            }
            tx.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            tx.rollback();
        } finally {
            stopTest("Generate leafs");
            finish();
        }
    }

    @Override
    public void findNodesWithValueDb(final int value) {
        initialize();
        String query = "SELECT * FROM Node WHERE myValue = " + Integer.toString(value);
        startTest();

        List<Node> nodes = db.query(new OSQLSynchQuery<Node>(query));
        System.out.print("Find nodes db: ");
        System.out.println(nodes.size());
        stopTest("Find nodes by value in db");
        finish();
    }

    @Override
    public void makeBinaryTree() {

        initialize();
        db.getLevel1Cache().invalidate();
        tx = db.getTransaction();

        String query = "SELECT * FROM Node ORDER BY myValue DESC";
        List<Node> nodes = db.query(new OSQLSynchQuery<Node>(query));

        startTest();

        try {
            tx.begin();
            generateBinaryTree(nodes);
            tx.commit();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            exception.printStackTrace(System.out);
            tx.rollback();
        } finally {
            stopTest("Generate binary tree");
            finish();
        }

    }

    @Override
    public void swapRootChildren() {
        initialize();
        root = getRoot();
        tx = db.getTransaction();
        startTest();

        try {
            tx.begin();
            List<Node> children = new ArrayList<Node>();

            Node leftChild = root.getChildren().get(0);
            Node rightChild = root.getChildren().get(1);

            Node newLeftChild = new Node();
            Node newRightChild = new Node();

            db.save(newLeftChild);
            db.save(newRightChild);

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

            db.save(root);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        } finally {
            stopTest("Swap root children");
            finish();
        }

    }

    @Override
    public void deleteTree() {
        initialize();
        root = getRoot();
        startTest();
        tx = db.getTransaction();
        try {
            tx.begin();
            db.delete(root);
            tx.commit();
        } catch (Exception exception) {
            tx.rollback();
        } finally {
            stopTest("Delete tree");
            finish();
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void finish() {
        Bench4JODProperties properties = Bench4JODProperties.getInstance();
        String cleanCache = properties.getProperty(Bench4JODProperties.Benchmark.CLEAN_CACHE, "false");
        if (Boolean.valueOf(cleanCache)) {
            db.getLevel1Cache().invalidate();
            db.getLevel2Cache().clear();
        }
    }

    private void generateLeafChildren(Node leaf) {
        List<Node> children = new ArrayList<Node>();
        for (int index = 0; index < nodeGenerator.getNumberOfChildren(); ++index) {
            Node node = db.newInstance(Node.class);
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
                db.save(node);
                queue2.offer(node);
            }
            parent.setChildren(children);
            db.save(parent);
        }
    }
}
