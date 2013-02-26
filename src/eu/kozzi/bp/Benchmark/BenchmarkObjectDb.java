package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 11.2.13
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkObjectDb extends BenchmarkJPA {
    BenchmarkObjectDb(ArgsParser argsParser) {
        super(argsParser);
    }

    @Override
    public void findLeafs() {
        startTest();
        List<Node> list1 = entityManager.createQuery(
                "SELECT DISTINCT n.parent FROM Node n", Node.class).getResultList();
        List<Node> leafs = entityManager.createQuery(
                "SELECT n FROM Node n WHERE n NOT IN :list", Node.class)
                .setParameter("list", list1).getResultList();
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
    }

    @Override
    public void addLeafs() {
        List<Node> list1 = entityManager.createQuery(
                "SELECT DISTINCT n.parent FROM Node n", Node.class).getResultList();

        List<Node> leafs = entityManager.createQuery(
                "SELECT n FROM Node n WHERE n NOT IN :list", Node.class)
                .setParameter("list", list1).getResultList();
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

    /*@Override
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
            root.getChildren().add(rootRightChild);
            root.getChildren().add(rootLeftChild);
            entityManager.persist(root);
            tx.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            tx.rollback();
        }
        stopTest("Swap root children");

    }  */
}
