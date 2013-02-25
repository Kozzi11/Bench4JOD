package eu.kozzi.bp;

import eu.kozzi.bp.Benchmark.Benchmark;
import eu.kozzi.bp.Benchmark.BenchmarkFactory;
import eu.kozzi.bp.Benchmark.BenchmarkOrientDb;
import eu.kozzi.bp.Exception.ArgsParserException;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 11.1.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */


public class Main {

    public static void main(String[] args) {
        try {
            ArgsParser argsParser = new ArgsParser(args);

            String persistenceUnitName = argsParser.getPersistenceUnitName();
            if (persistenceUnitName.contains("dn-")) {
                ClassPathHacker.addFile("lib/asm-4.0.jar");
            } else {
                ClassPathHacker.addFile("lib/asm-3.3.1.jar");
            }

            BenchmarkFactory benchmarkFactory = BenchmarkFactory.createInstance(argsParser);
            Benchmark benchmark = benchmarkFactory.getBenchmark();
            benchmark.run();
            System.out.print("\nTotal: ");
            System.out.println(benchmark.getTotalTime());

        } catch (ArgsParserException exception) {
            System.err.println(exception.getMessage());
            printUsage();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public static void printUsage() {
        System.err.println("\nUsage:");
        System.err.println("\tprogram <persistenceUnitName> NODE_COUNT <minChildren> <maxChildren> <numberOfNodes>");
        System.err.println("\tprogram <persistenceUnitName> TREE_HEIGHT <height> <numberOfChildren>");
    }
}
