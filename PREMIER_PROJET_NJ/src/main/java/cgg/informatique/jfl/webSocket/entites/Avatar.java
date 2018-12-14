package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="AVATAR")
public class Avatar {

    @Id
    private String nom;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "avatar" )
    private List<Compte> lstCompte = new ArrayList<>();

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    @Override
    public String toString() {
        return "Avatar{" +
                "nom='" + nom + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
