package ma.fsbm.lias.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import ma.fsbm.lias.exception.BusinessException;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stockage physique des fichiers sur disque (dossier configure par {@code app.storage.location}).
 */
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.storage.location}") String location) {
        this.root = Paths.get(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de creer le dossier de stockage : " + root, e);
        }
    }

    /** Enregistre le fichier sous un nom unique et renvoie ce nom. */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "fichier" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        String storedName = UUID.randomUUID() + extension;
        try {
            Path target = root.resolve(storedName).normalize();
            if (!target.getParent().equals(root)) {
                throw new BusinessException("Nom de fichier invalide");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Echec de l'enregistrement du fichier", e);
        }
        return storedName;
    }

    public Resource load(String fileName) {
        try {
            Path file = root.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("Fichier introuvable : " + fileName);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Fichier introuvable : " + fileName);
        }
    }

    public void delete(String fileName) {
        try {
            Files.deleteIfExists(root.resolve(fileName).normalize());
        } catch (IOException e) {
            // On ignore : la suppression de la metadonnee reste prioritaire.
        }
    }
}
