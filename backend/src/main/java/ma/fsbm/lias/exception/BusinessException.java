package ma.fsbm.lias.exception;

/**
 * Levee lorsqu'une regle metier est violee. Traduite en HTTP 400.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
