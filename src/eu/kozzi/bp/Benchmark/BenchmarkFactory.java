package eu.kozzi.bp.Benchmark;

import eu.kozzi.bp.ArgsParser;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;


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


    public static BenchmarkFactory createInstance(GeneratorSetting generatorSetting) {
        instance = new BenchmarkFactory(generatorSetting);
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


    private BenchmarkFactory(GeneratorSetting generatorSetting) {
        String persistenceUnitName = generatorSetting.getPersistenceUnitName();
        if (persistenceUnitName.equals("orientdb")) {
            this.benchmark = new BenchmarkOrientDb(generatorSetting);
        } else if (persistenceUnitName.equals("db4o")) {
            this.benchmark = new BenchmarkDb4o(generatorSetting);
        } else if (persistenceUnitName.equals("objectdb")) {
            this.benchmark = new BenchmarkObjectDb(generatorSetting);
        } else if (persistenceUnitName.contains("dn-")) {
            this.benchmark = new BenchmarkDataNucleus(generatorSetting);
        } else {
            this.benchmark = new BenchmarkJPA(generatorSetting);
        }
    }




}
