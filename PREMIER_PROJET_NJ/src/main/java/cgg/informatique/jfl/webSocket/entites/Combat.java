package cgg.informatique.jfl.webSocket.entites;

import javax.persistence.*;

@Entity
@Table(name="COMBAT")
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int creditArbitre;
    private int pointsRouge;
    private int pointsBlancs;

    private Long dateCombat;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="COMPTE_ROUGE" )
    private Compte cptRouge;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="COMPTE_BLANC" )
    private Compte cptBlanc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="COMPTE_ARBITRE" )
    private Compte cptArbitre;

    public Compte getCptRouge() {
        return cptRouge;
    }

    public void setCptRouge(Compte cptRouge) {
        this.cptRouge = cptRouge;
    }

    public Compte getCptBlanc() {
        return cptBlanc;
    }

    public void setCptBlanc(Compte cptBlanc) {
        this.cptBlanc = cptBlanc;
    }

    public Compte getCptArbitre() {
        return cptArbitre;
    }

    public void setCptArbitre(Compte cptArbitre) {
        this.cptArbitre = cptArbitre;
    }

    public Combat(){}

    public Combat(int creditArbitre, int pointsRouge, int pointsBlancs, Compte cptRouge, Compte cptBlanc, Compte cptArbitre){
        this.creditArbitre = creditArbitre;
        this.pointsRouge = pointsRouge;
        this.pointsBlancs = pointsBlancs;
        this.cptRouge = cptRouge;
        this.cptBlanc = cptBlanc;
        this.cptArbitre = cptArbitre;
        this.dateCombat = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreditArbitre() {
        return creditArbitre;
    }

    public void setCreditArbitre(int creditArbitre) {
        this.creditArbitre = creditArbitre;
    }

    public int getPointsRouge() {
        return pointsRouge;
    }

    public void setPointsRouge(int pointsRouge) {
        this.pointsRouge = pointsRouge;
    }

    public int getPointsBlancs() {
        return pointsBlancs;
    }

    public void setPointsBlancs(int pointsBlancs) {
        this.pointsBlancs = pointsBlancs;
    }

    public Long getDateCombat() {
        return dateCombat;
    }

    public void setDateCombat(Long dateCombat) {
        this.dateCombat = dateCombat;
    }

    @Override
    public String toString() {
        return "Combat{" +
                "id=" + id +
                ", creditArbitre=" + creditArbitre +
                ", pointsRouge=" + pointsRouge +
                ", pointsBlancs=" + pointsBlancs +
                '}';
    }
}
