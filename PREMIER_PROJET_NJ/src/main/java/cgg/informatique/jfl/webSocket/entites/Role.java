package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name="ROLE")
public class Role {
    private static final long serialVersionUID = 1L;

    @Id
    private int id;
    private String role;


    @OneToMany( fetch = FetchType.EAGER, mappedBy = "role" )
    private List<Compte> comptes = new ArrayList<>();

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

    public Role(int id, String role){
        this.id = id;
        this.role = role;
    }

    public Role(){
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", comptes=" + comptes +
                '}';
    }
}
