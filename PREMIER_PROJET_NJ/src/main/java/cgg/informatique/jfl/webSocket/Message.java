package cgg.informatique.jfl.webSocket;


import java.util.Date;

public class Message {

    private String de;
    private String texte;
    private Long   creation;
    private String avatar  = new String();
    private String type;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Message() {
    }

    public Message(String de, String texte, Long creation, String avatar, String type) {
        this.de = de;
        this.texte = texte;
        this.creation = creation;
        this.avatar = avatar;
        this.type = type;
    }

    public Long getCreation() {
        return creation;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public Message(String de, String texte) {
        this.de = de;
        this.texte = texte;
    }

    public Message(  String texte) {
        this.de = "anonyme";
        this.texte = texte;
    }


    @Override
    public String toString() {
        return "Reponse{" +
                "de='" + de + '\'' +
                ", texte='" + texte + '\'' +
                ", creation=" + creation +
                ", avatar=" + avatar +
                '}';
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String text) {
        this.texte = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

