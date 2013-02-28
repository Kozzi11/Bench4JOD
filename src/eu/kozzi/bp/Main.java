package eu.kozzi.bp;

import eu.kozzi.bp.Benchmark.Benchmark;
import eu.kozzi.bp.Benchmark.BenchmarkFactory;
import eu.kozzi.bp.Benchmark.BenchmarkOrientDb;
import eu.kozzi.bp.Exception.ArgsParserException;
import eu.kozzi.bp.Exception.NoArgsException;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 11.1.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */


public class Main {

    public static void main(String[] args) {

        GeneratorSetting generatorSetting = null;

        try {
            ArgsParser argsParser = new ArgsParser(args);
            generatorSetting = argsParser;

        } catch (ArgsParserException e) {
            System.err.println(e.getMessage());
            printUsage();
        } catch (NoArgsException e) {
            generatorSetting = Bench4JODProperties.getInstance().getGeneratorProperties();
        }
        try {
            BenchmarkFactory benchmarkFactory = BenchmarkFactory.createInstance(generatorSetting);
            Benchmark benchmark = benchmarkFactory.getBenchmark();
            benchmark.run();
            System.out.print("\nTotal: ");
            System.out.println(benchmark.getTotalTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printUsage() {
        System.err.println("\nUsage:");
        System.err.println("\tprogram");
        System.err.println("\tprogram <persistenceUnitName> NODE_COUNT <minChildren> <maxChildren> <numberOfNodes>");
        System.err.println("\tprogram <persistenceUnitName> TREE_HEIGHT <height> <numberOfChildren>");
    }
}
