package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="GROUPES")
public class Groupe {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String groupe;

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "groupe" )
    private List<Compte> comptes  = new ArrayList<>();

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "ceintureBlanc" )
    private Set<Combat>  ceinturesBlancs  = new HashSet<>();

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "ceintureRouge" )
    private Set<Combat> ceinturesRouges  = new HashSet<>();

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "ceinture" )
    private Set<Examen>  ceintures  = new HashSet<>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public Set<Combat> getCeinturesBlancs() {
        return ceinturesBlancs;
    }

    public void setCeinturesBlancs(Set<Combat> ceinturesBlancs) {
        this.ceinturesBlancs = ceinturesBlancs;
    }

    public Set<Combat> getCeinturesRouges() {
        return ceinturesRouges;
    }

    public void setCeinturesRouges(Set<Combat> ceinturesRouges) {
        this.ceinturesRouges = ceinturesRouges;
    }

    public Set<Examen> getCeintures() {
        return ceintures;
    }

    public void setCeintures(Set<Examen> ceintures) {
        this.ceintures = ceintures;
    }
    public Groupe(){}

    public Groupe(String groupe) {
        this.groupe = groupe;
    }
}
