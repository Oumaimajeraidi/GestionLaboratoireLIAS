package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partenaires")
@Getter
@Setter
@NoArgsConstructor
public class Partenaire extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    private String pays;

    private String typeInstitution;

    private String contactNom;

    private String contactEmail;
}
