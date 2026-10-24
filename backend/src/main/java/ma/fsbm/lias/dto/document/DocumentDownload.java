package ma.fsbm.lias.dto.document;

import org.springframework.core.io.Resource;

/**
 * Conteneur pour le telechargement d'un document (ressource fichier + metadonnees d'en-tete).
 */
public record DocumentDownload(Resource resource, String filename, String contentType) {
}
