package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name="GROUPE")
public class Groupe {

    @Id
    private int id;

    @Column(name = "groupe")
    private String groupe;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "groupe" )
    private List<Compte> comptes = new ArrayList<>();

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


    public Groupe(int id, String groupe){
        this.id = id;
        this.groupe = groupe;
    }

    public Groupe(){}

    @Override
    public String toString() {
        return "Groupe{" +
                "id=" + id +
                ", groupe='" + groupe + '\'' +
                ", comptes=" + comptes +
                '}';
    }
}
