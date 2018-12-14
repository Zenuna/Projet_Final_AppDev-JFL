package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;

@Entity
@Table(name="EXAMEN")
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean aPasser;
    private long date;
    private boolean examAncien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="COMPTE_EVALUER" )
    private Compte cptEvaluer = new Compte();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="COMPTE_EVALUATEUR" )
    private Compte cptEvaluateur = new Compte();

    public Examen() {
    }
    public Examen(boolean aPasser, Compte cptEvaluer, Compte cptEvaluateur,boolean examAncien){
        this.aPasser = aPasser;
        this.date = System.currentTimeMillis();
        this.cptEvaluer = cptEvaluer;
        this.cptEvaluateur = cptEvaluateur;
        this.examAncien = examAncien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isaPasser() {
        return aPasser;
    }

    public void setaPasser(boolean aPasser) {
        this.aPasser = aPasser;
    }

    public Long getDate() {
        return date;
    }


    public Compte getCptEvaluer() {
        return cptEvaluer;
    }

    public void setCptEvaluer(Compte cptEvaluer) {
        this.cptEvaluer = cptEvaluer;
    }

    public Compte getCptEvaluateur() {
        return cptEvaluateur;
    }

    public void setCptEvaluateur(Compte cptEvaluateur) {
        this.cptEvaluateur = cptEvaluateur;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isExamAncien() {
        return examAncien;
    }

    public void setExamAncien(boolean examAncien) {
        this.examAncien = examAncien;
    }

    @Override
    public String toString() {
        return "Examen{" +
                "id=" + id +
                ", aPasser=" + aPasser +
                ", date=" + date +
                '}';
    }
}
