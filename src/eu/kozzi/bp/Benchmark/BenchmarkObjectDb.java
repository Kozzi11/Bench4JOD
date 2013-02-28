package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Node;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;

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
    BenchmarkObjectDb(GeneratorSetting generatorSetting) {
        super(generatorSetting);
    }

    @Override
    public void findLeafs() {
        initialize();
        startTest();
        List<Node> list1 = entityManager.createQuery(
                "SELECT DISTINCT n.parent FROM Node n", Node.class).getResultList();
        List<Node> leafs = entityManager.createQuery(
                "SELECT n FROM Node n WHERE n NOT IN :list", Node.class)
                .setParameter("list", list1).getResultList();
        System.out.print("Lefs count: ");
        System.out.println(leafs.size());
        stopTest("Find tree lefs");
        clear();
    }

    @Override
    public void addLeafs() {

       initialize();
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
            exception.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            stopTest("Generate leafs");
            clear();
        }
    }

}
