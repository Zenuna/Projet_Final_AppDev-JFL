package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;

@Entity
@Table(name="EXAMENS")
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    Long    date;
    Boolean aReussi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ceinture_id" )
    private Groupe   ceinture = new Groupe();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="evaluateur_id" )
    private Compte   evaluateur = new Compte();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="evalue_id" )
    private Compte   evalue = new Compte();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getaReussi() {
        return aReussi;
    }

    public void setaReussi(Boolean aReussi) {
        this.aReussi = aReussi;
    }

    public Groupe getCeinture() {
        return ceinture;
    }

    public void setCeinture(Groupe ceinture) {
        this.ceinture = ceinture;
    }

    public Compte getEvaluateur() {
        return evaluateur;
    }

    public void setEvaluateur(Compte evaluateur) {
        this.evaluateur = evaluateur;
    }

    public Compte getEvalue() {
        return evalue;
    }

    public void setEvalue(Compte evalue) {
        this.evalue = evalue;
    }

    public Examen(){}

    @Override
    public String toString() {

        return "Examen{" +
                "id=" + id +
                ", date=" + date+
                ", aReussi=" + aReussi +
                ", ceinture=" + ceinture.getGroupe() +
                ", evaluateur=" + evaluateur.getUsername() +
                ", evalue=" + evalue.getUsername() +
                '}';
    }

    public Examen(Long date, Boolean aReussi, Groupe ceinture, Compte evaluateur, Compte evalue) {
        this.date = date;
        this.aReussi = aReussi;
        this.ceinture = ceinture;
        this.evaluateur = evaluateur;
        this.evalue = evalue;
    }
}
