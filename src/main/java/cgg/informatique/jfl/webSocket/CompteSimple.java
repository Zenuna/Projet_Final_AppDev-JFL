package cgg.informatique.jfl.webSocket;

public class CompteSimple {

    private String username;
    private String fullname;
    private String avatar ;
    private int points;
    private int credits;
    private String groupe;
    private String role;
    private long anciendepuis;


    public CompteSimple(String username,String fullname, String avatar,int points,int credits,String groupe,String role,long anciendepuis) {
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.points = points;
        this.credits = credits;
        this.groupe = groupe;
        this.role = role;
        this.anciendepuis = anciendepuis;
    }

    public CompteSimple(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }

    public CompteSimple(){

    }
   /* public String toStringExtraSimple() {
        return "{ \"username\" : \"" + username +
                "\", \"avatar\" : \"" + avatar + "\"  }";
    }*/
    @Override
    public String toString() {
        return "{ \"username\" : \"" + username +
                "\", \"fullname\" : \"" + fullname +
                "\", \"points\" : \"" + points +
                "\", \"credits\" : \"" + credits +
                "\", \"groupe\" : \"" + groupe +
                "\", \"role\" : \"" + role +
                "\", \"anciendepuis\" : \"" + anciendepuis +
                "\", \"avatar\" : \"" + avatar + "\"  }";
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }
}
