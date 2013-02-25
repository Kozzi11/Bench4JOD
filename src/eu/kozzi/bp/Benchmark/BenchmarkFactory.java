package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;


/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.1.13
 * Time: 3:38
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkFactory {


    private static BenchmarkFactory instance = null;
    private Benchmark benchmark;


    public static BenchmarkFactory createInstance(ArgsParser argsParser) {
        instance = new BenchmarkFactory(argsParser);
        return instance;
    }

    public static BenchmarkFactory getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("You must call BenchmarkFactory.createInstance(ArgsParser argsParser), before BenchmarkFactory.getInstance()");
        }
        return instance;
    }

    public Benchmark getBenchmark() {
        return this.benchmark;
    }


    private BenchmarkFactory(ArgsParser argsParser) {
        String persistenceUnitName = argsParser.getPersistenceUnitName();
        if (persistenceUnitName.equals("orientdb")) {
            this.benchmark = new BenchmarkOrientDb(argsParser);
        } else if (persistenceUnitName.equals("db4o")) {
            this.benchmark = new BenchmarkDb4o(argsParser);
        } else if (persistenceUnitName.equals("objectdb")) {
            this.benchmark = new BenchmarkObjectDb(argsParser);
        } else if (persistenceUnitName.equals("dn-mysql")) {
            this.benchmark = new BenchmarkDataNucleus(argsParser);
        } else if (persistenceUnitName.equals("batoo-mysql")) {
            this.benchmark = new BenchmarkBatoo(argsParser);
        } else {
            this.benchmark = new BenchmarkJPA(argsParser);
        }
    }




}
