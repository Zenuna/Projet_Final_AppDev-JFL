package cgg.informatique.jfl.webSocket.configurations;

import cgg.informatique.jfl.webSocket.entites.Compte;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MonUserPrincipal implements UserDetails {
    private Compte compte;

    public MonUserPrincipal(Compte compte) {
        if (compte != null)
            this.compte = compte;
        else
            this.compte = new Compte("","","",0,0,0,    null,null,null,null);
    }

    public String getUserName() { return this.compte.getUsername();}

    public String getRole() { return this.compte.getRole().getRole();}

    public String getGroupe() { return this.compte.getGroupe().getGroupe();}

    public String getAvatar() { return this.compte.getAvatar().getAvatar(); }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return compte.getAuthorities();
    }
    @Override
    public String getPassword() { return compte.getPassword();}
    @Override
    public String getUsername() { return compte.getUsername();}
    @Override
    public boolean isAccountNonExpired() { return compte.isAccountNonExpired();}
    @Override
    public boolean isAccountNonLocked() { return compte.isAccountNonLocked();}
    @Override
    public boolean isCredentialsNonExpired() { return
            compte.isCredentialsNonExpired();
    }
    @Override
    public boolean isEnabled() { return compte.isEnabled(); }

   /* @Override
    public String toString() {
        return "Compte{" +
                "courriel='" + compte.getCourriel() + '\'' +
                ", MdeP='" + compte.getMdeP() + '\'' +
                ", alias='" + compte.getAlias() + '\'' +
                ", avatar=" + compte.getAvatar() +
                ", role=" + compte.getRole() +
                ", groupe=" + compte.getGroupe() +
                '}';
    }*/
}
