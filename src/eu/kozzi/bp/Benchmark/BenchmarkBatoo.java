package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;

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
}
