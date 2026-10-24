package ma.fsbm.lias.exception;

/**
 * Levee lorsqu'une ressource demandee n'existe pas. Traduite en HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entite, Long id) {
        return new ResourceNotFoundException(entite + " introuvable (id=" + id + ")");
    }
}
