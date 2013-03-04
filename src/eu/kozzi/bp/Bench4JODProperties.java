package eu.kozzi.bp;

import eu.kozzi.bp.Tree.NodeGenerator;
import eu.kozzi.bp.Tree.Setting.GeneratorSetting;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.2.13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class Bench4JODProperties extends Properties {

    private static Bench4JODProperties ourInstance = new Bench4JODProperties();

    private GeneratorProperties generatorProperties = null;

    abstract public class Benchmark {
        public static final String CLEAN_CACHE = "bench4jod.benchmark.cleanCache";
        public static final String FIND_NODE_VALUE ="bench4jod.benchmark.findNodeValue";
        public static final String FIND_NODE_DB_VALUE ="bench4jod.benchmark.findNodeDbValue";
        public static final String EXPLORE_PATH ="bench4jod.benchmark.explorePath";
        public static final String EXPLORE_BINARY_PATH ="bench4jod.benchmark.exploreBinaryPath";
    }

    abstract public class Generator {

        public static final String PERSISTENCE_UNIT_NAME = "bench4jod.generator.persistenceUnitName";
        public static final String VARIANT = "bench4jod.generator.variant";
        public static final String NODE_COUNT ="bench4jod.generator.nodeCount";
        public static final String MIN_CHILDREN ="bench4jod.generator.minChildren";
        public static final String MAX_CHILDREN ="bench4jod.generator.maxChildren";
        public static final String NUMBER_OF_NODES ="bench4jod.generator.numberOfNodes";
        public static final String NUMBER_OF_CHILDREN ="bench4jod.generator.numberOfChildren";
        public static final String TREE_HEIGHT ="bench4jod.generator.treeHeight";
    }

    public static Bench4JODProperties getInstance() {
        return ourInstance;
    }

    private Bench4JODProperties() {
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            InputStream file = loader.getResourceAsStream("META-INF/benchmark.properties");
            this.load(file);
            generatorProperties = new GeneratorProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GeneratorProperties getGeneratorProperties() {
        return generatorProperties;
    }

    public class GeneratorProperties implements GeneratorSetting {

        private String persistenceUnitName;
        private NodeGenerator.Variant variant;
        private int minChildren;
        private int maxChildren;
        private int numberOfNodes;
        private int height;
        private int numberOfChildren;

        GeneratorProperties() {

            persistenceUnitName = getProperty(Generator.PERSISTENCE_UNIT_NAME);

            String variant = getProperty(Generator.VARIANT);

            if (variant.equals(NodeGenerator.Variant.FIX_HEIGHT.toString())) {
                this.variant = NodeGenerator.Variant.FIX_HEIGHT;
            } else if(variant.equals(NodeGenerator.Variant.FIX_NODE_COUNT.toString())) {
                this.variant = NodeGenerator.Variant.FIX_NODE_COUNT;
            } else {
                this.variant = NodeGenerator.Variant.ERROR;
            }

            minChildren = Integer.valueOf(getProperty(Generator.MIN_CHILDREN));
            maxChildren = Integer.valueOf(getProperty(Generator.MAX_CHILDREN));
            numberOfNodes = Integer.valueOf(getProperty(Generator.NUMBER_OF_NODES));
            numberOfChildren = Integer.valueOf(getProperty(Generator.NUMBER_OF_CHILDREN));
            height = Integer.valueOf(getProperty(Generator.TREE_HEIGHT));

        }


        @Override
        public String getPersistenceUnitName() {
            return persistenceUnitName;
        }

        @Override
        public NodeGenerator.Variant getVariant() {
            return variant;
        }

        @Override
        public int getMinChildren() {
            return minChildren;
        }

        @Override
        public int getMaxChildren() {
            return maxChildren;
        }

        @Override
        public int getNumberOfNodes() {
            return numberOfNodes;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getNumberOfChildren() {
            return numberOfChildren;
        }
    }
}
