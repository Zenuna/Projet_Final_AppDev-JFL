package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ROLES")
public class Role {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String role;

    @OneToMany(  mappedBy = "role" )
    private List<Compte> comptes  = new ArrayList<>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }

    public Role(){}

    public Role(String role) {
        this.role = role;
    }
}
