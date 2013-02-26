package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 17.2.13
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkBatoo extends BenchmarkJPA {
    BenchmarkBatoo(ArgsParser argsParser) {
        super(argsParser);
    }

    @Override
    public void findLeafs() {
        startTest();
        String query = "SELECT n FROM Node n WHERE n.children IS NOT EMPTY";

        List<Node> leafs = entityManager.createQuery(query, Node.class).getResultList();
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
    }

    @Override
    public void addLeafs() {
        String query = "SELECT n FROM Node n WHERE n.children IS NOT EMPTY";
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
            System.err.println(exception.getMessage());
            tx.rollback();
        }
        stopTest("Generate leafs");
    }

    @Override
    public void swapRootChildren() {
        tx = entityManager.getTransaction();


        try {
            tx.begin();
            root = getRoot();

            startTest();

            List<Node> children = new ArrayList<Node>();

            System.err.println(root.getId());
            System.err.println(root.getChildren().size());

            Node leftChild = root.getChildren().get(0);
            Node rightChild = root.getChildren().get(1);

            Node newLeftChild = new Node();
            Node newRightChild = new Node();

            entityManager.persist(newLeftChild);
            entityManager.persist(newRightChild);

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

            tx.commit();

            entityManager.getTransaction().begin();
            entityManager.createQuery("DELETE FROM Node n WHERE n.id = " + leftChild.getId(), Node.class).executeUpdate();
            entityManager.createQuery("DELETE FROM Node n WHERE n.id = " + rightChild.getId(), Node.class).executeUpdate();
            entityManager.getTransaction().commit();

        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Swap root children");

    }
}
