package cgg.informatique.jfl.webSocket;


public class Message {

    private String de;
    private String texte;
    private Long   creation;
    private String avatar;
    private String sessionRest;

    public Message(String de, String texte, Long creation, String avatar, String sessionRest) {
        this.de = de;
        this.texte = texte;
        this.creation = creation;
        this.avatar = avatar;
        this.sessionRest = sessionRest;
    }

    public String getSessionRest() {

        return sessionRest;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setSessionRest(String sessionRest) {
        this.sessionRest = sessionRest;
    }



    public Message() {
    }

    public Message(String de, String texte, Long creation, String id_avatar) {
        this.de = de;
        this.texte = texte;
        this.creation = creation;
        this.avatar = id_avatar;
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
}

