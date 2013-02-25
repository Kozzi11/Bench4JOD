package eu.kozzi.bp.Exception;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.1.13
 * Time: 3:25
 * To change this template use File | Settings | File Templates.
 */
public class ArgsParserException extends Exception {
    public ArgsParserException(String msg) {
        super(msg);
    }

    public ArgsParserException() {
        super("Wrong number of arguments");
    }
}
