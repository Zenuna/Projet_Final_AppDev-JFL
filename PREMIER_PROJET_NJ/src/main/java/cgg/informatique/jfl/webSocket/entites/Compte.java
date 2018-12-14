package cgg.informatique.jfl.webSocket.entites;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Table (name="COMPTE")
public class Compte implements UserDetails {
    @Id
    private String courriel;

    private String MdeP;
    private String alias;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name="AVATAR")
    private Avatar avatar;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST )
    @JoinColumn(name="ROLE" )
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST )
    @JoinColumn(name="GROUPE" )
    private Groupe groupe;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "cptBlanc" )
    private List<Combat> cptsBlanc = new ArrayList<>();

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "cptRouge" )
    private List<Combat> cptsRouge = new ArrayList<>();

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "cptArbitre" )
    private List<Combat> cptsArbitre = new ArrayList<>();

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "cptEvaluer" )
    private List<Examen> cptsEvaluer = new ArrayList<>();

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "cptEvaluateur" )
    private List<Examen> cptEvaluateur = new ArrayList<>();

    public Compte(){

    }

    public Compte( String courriel, String MdeP, String alias, Avatar avatar,Role role, Groupe groupe) {
        this.courriel = courriel;
        this.MdeP = MdeP;
        this.alias = alias;
        this.avatar = avatar;
        this.role = role;
        this.groupe = groupe;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(this.role.getRole()));
    }

    @Override
    public String getPassword() {
        return MdeP;
    }

    @Override
    public String getUsername() {
        return this.courriel;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public String getMdeP() {
        return MdeP;
    }

    public void setMdeP(String mdeP) {
        MdeP = mdeP;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar() {
        return avatar.getAvatar();
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role.getRole();
    }

    public Role getUnRole(){
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getGroupe() {
        return groupe.getGroupe();
    }

    public Groupe getUnGroupe(){
        return groupe;
    }
    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public List<Combat> getCptsBlanc() {
        return cptsBlanc;
    }

    public void setCptsBlanc(List<Combat> cptsBlanc) {
        this.cptsBlanc = cptsBlanc;
    }

    public List<Combat> getCptsRouge() {
        return cptsRouge;
    }

    public void setCptsRouge(List<Combat> cptsRouge) {
        this.cptsRouge = cptsRouge;
    }

    public List<Combat> getCptsArbitre() {
        return cptsArbitre;
    }

    public void setCptsArbitre(List<Combat> cptsArbitre) {
        this.cptsArbitre = cptsArbitre;
    }

    public List<Examen> getCptsEvaluer() {
        return cptsEvaluer;
    }

    public void setCptsEvaluer(List<Examen> cptsEvaluer) {
        this.cptsEvaluer = cptsEvaluer;
    }

    public List<Examen> getCptEvaluateur() {
        return cptEvaluateur;
    }

    public void setCptEvaluateur(List<Examen> cptEvaluateur) {
        this.cptEvaluateur = cptEvaluateur;
    }

    @Override
    public String toString() {
        return "{" +
                "\"courriel\":\"" + courriel + "\"" +
                ",\"MdeP\":\"" + MdeP + "\"" +
                ",\"alias\":\"" + alias + "\"" +
                ",\"avatar\":\"" + avatar.getNom() + "\"" +
                ",\"role\":\"" + role.getRole() + "\"" +
                ",\"groupe\":\"" + groupe.getGroupe() + "\"" +
                "}";
    }

}
