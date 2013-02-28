package eu.kozzi.bp.Exception;

/**
 * Created with IntelliJ IDEA.
 * User: kozzi
 * Date: 28.2.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class NoArgsException extends Exception {
    public NoArgsException(String msg) {
        super(msg);
    }

    public NoArgsException() {
        super("No arguments");
    }
}
