package cgg.informatique.jfl.webSocket;

public class Reponse {
    private Long id;
    private String de;
    private String texte;
    private Long creation;

    private String avatar = new String();

    public String getAvatar() {
        return avatar;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reponse() {
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Reponse(Long id, String de, String texte, Long creation, String avatar) {
        this.id = id;
        this.de = de;
        this.texte = texte;
        this.creation = creation;
        this.avatar = avatar;
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

    public void setTexte(String texte) {
        this.texte = texte;
    }


    public Long getCreation() {
        return creation;
    }

    public void setTime(Long creation) {
        this.creation = creation;
    }

    @Override
    public String toString() {
        return "{ \"id\" : "+id+", \"de\" : \""+de+"\",   \"texte\" :  \""+texte+"\", \"creation\" : "+creation+" , \"avatar\" : \""+avatar+"\"  }";
    }

}


