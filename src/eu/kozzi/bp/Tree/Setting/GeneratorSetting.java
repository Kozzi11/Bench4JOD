package eu.kozzi.bp.Tree.Setting;

import eu.kozzi.bp.Tree.NodeGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.2.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public interface GeneratorSetting {

    public String getPersistenceUnitName();

    public NodeGenerator.Variant getVariant();

    public int getMinChildren();

    public int getMaxChildren();

    public int getNumberOfNodes();

    public int getHeight();

    public int getNumberOfChildren();
}
