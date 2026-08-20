package exam.file.code.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Une cohorte d'étudiants entrée la même année, sur un parcours de 3 ans (ex: "Promo 2023").
@Entity
@Table(name = "promotion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  private String libelle;

  @Column(nullable = false)
  private int anneeEntree;
}
