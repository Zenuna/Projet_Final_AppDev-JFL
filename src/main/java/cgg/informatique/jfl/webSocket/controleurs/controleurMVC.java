package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.dao.CombatDao;
import cgg.informatique.jfl.webSocket.dao.CompteDao;
import cgg.informatique.jfl.webSocket.entites.Combat;
import cgg.informatique.jfl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class controleurMVC {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String accueil(Map<String, Object> model) {
        return "public/kumite";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Map<String, Object> model) {
        return "login";
    }

    @RequestMapping(value = "/gradation", method = RequestMethod.GET)
    public String gradation(Map<String, Object> model) {
        return "public/gradation";
    }

}