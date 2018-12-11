package cgg.informatique.jfl.webSocket.configurations;


import cgg.informatique.jfl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MonUserDetailsService implements UserDetailsService {
    @Autowired
    private cgg.informatique.jfl.webSocket.dao.CompteDao CompteDao;

    @Override
    public UserDetails loadUserByUsername(String nom) {
        Optional<Compte> compte = CompteDao.findById(nom);
        Compte c;
        if (compte.isPresent()){
            c = compte.get();
        }
        else
        {
            c = new Compte("b@cgodin.qc.ca", "$2a$10$fC4LOea.5JcmFdcgLJvPueSKidqSf2r0sMmTcbOf5clNZj9Qnx7Zu",
                    "XXXXX",0,0,0,null,null,null,null);
        }
        return new MonUserPrincipal(c);
    }

}

