package ma.fsbm.lias.dto.recherche;

public record SearchResultItem(
    String type,
    Long id,
    String titre,
    String description,
    String url
) {}
