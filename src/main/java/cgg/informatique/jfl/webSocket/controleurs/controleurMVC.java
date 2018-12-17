package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.dao.CombatDao;
import cgg.informatique.jfl.webSocket.dao.CompteDao;
import cgg.informatique.jfl.webSocket.dao.ExamenDao;
import cgg.informatique.jfl.webSocket.entites.Combat;
import cgg.informatique.jfl.webSocket.entites.Compte;
import cgg.informatique.jfl.webSocket.entites.Examen;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private CompteControleurRest compteControleurRest;

    @Autowired
    private CompteDao compteDao;

    @Autowired
    private CombatDao combatDao;

    @Autowired
    private ExamenDao examenDao;

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
        List<Compte> lesComptes = compteDao.findAll();
        List<Compte> lesComptesCeinture = new ArrayList<>();
        List<Compte> lesComptesHonteux = new ArrayList<>();
        for (Compte c : lesComptes) {
            int points = 0;
            int credits = 0;
            long dateExamen = 0;
            Boolean booReussi = true;

            if (compteDao.findById(c.getUsername()).get().getAnciendepuis() < System.currentTimeMillis()) {
                credits -= 10;
            }

            for (Examen lesExamens : examenDao.findAll()) {
                if (lesExamens.getEvalue().getUsername().equals(c.getUsername())) {
                    if (dateExamen < lesExamens.getDate()){
                        if(lesExamens.getaReussi()){
                            dateExamen = lesExamens.getDate();
                        }
                        booReussi = lesExamens.getaReussi();
                    }
                    if (lesExamens.getaReussi() == false && !lesExamens.getCeinture().getGroupe().equals("BLANCHE")) {
                        credits -= 5;
                    } else {
                        if (!lesExamens.getCeinture().getGroupe().equals("BLANCHE")) {
                            credits -= 10;
                        }
                    }
                }
            }
            for (Combat lesCombats : combatDao.findAll()) {
                int diffCeinture = lesCombats.getCeintureRouge().getId() - lesCombats.getCeintureBlanc().getId();
                int[] tabPts = pointsParDifference(Math.abs(lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId()));

                if (lesCombats.getDate() >= dateExamen) {

                    if (lesCombats.getBlanc().getUsername().equals(c.getUsername())) {

                        if (lesCombats.getPointsBlanc() == 10) {
                            if (diffCeinture > 0) {
                                points += tabPts[0];
                            } else if (diffCeinture == 0) {
                                points += tabPts[0];
                            } else {
                                points += tabPts[1];
                            }
                        } else if (lesCombats.getPointsBlanc() == 5) {
                            double pts0 = tabPts[0];
                            double pts1 = tabPts[1];
                            if (diffCeinture > 0) {
                                points += Math.round(pts0 / 2);
                            } else if (diffCeinture == 0) {
                                points += Math.round(pts0 / 2);
                            } else {
                                points += Math.round(pts1 / 2);
                            }
                        }
                    } else if (lesCombats.getRouge().getUsername().equals(c.getUsername())) {
                        if (lesCombats.getPointsRouge() == 10) {
                            diffCeinture = lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId();

                            if (diffCeinture > 0) {
                                points += tabPts[0];
                            } else if (diffCeinture == 0) {
                                points += tabPts[0];
                            } else {
                                points += tabPts[1];
                            }

                        } else if (lesCombats.getPointsRouge() == 5) {
                            diffCeinture = lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId();
                            double pts0 = tabPts[0];
                            double pts1 = tabPts[1];
                            if (diffCeinture > 0) {
                                points += Math.round(pts0 / 2);
                            } else if (diffCeinture == 0) {
                                points += Math.round(pts0 / 2);
                            } else {
                                points += Math.round(pts1 / 2);
                            }
                        }
                    }
                }
                if (lesCombats.getArbitre().getUsername().equals(c.getUsername())) {
                    if (lesCombats.getCreditsArbitre() == 1)
                        credits += lesCombats.getCreditsArbitre();
                    else {
                        credits -= 5;
                    }
                }

            }
            if (!booReussi) {
                lesComptesHonteux.add(c);
            }
            if (points >= 100 && credits >= 10 && !c.getGroupe().getGroupe().equals("NOIRE")&& !c.getGroupe().getGroupe().equals("PATATE")) {
                lesComptesCeinture.add(c);
            }



        }
        model.put("lesComptesCeinture", lesComptesCeinture);
        model.put("lesComptesHonteux", lesComptesHonteux);
        return "public/gradation";
    }


    public int[] pointsParDifference(int diffCeinture) {
        int[] tabPts = new int[2];
        switch (diffCeinture) {
            case 0:
                tabPts[0] = 10;
                tabPts[1] = 10;
                break;
            case 1:
                tabPts[0] = 12;
                tabPts[1] = 9;
                break;
            case 2:
                tabPts[0] = 15;
                tabPts[1] = 7;
                break;
            case 3:
                tabPts[0] = 20;
                tabPts[1] = 5;
                break;
            case 4:
                tabPts[0] = 25;
                tabPts[1] = 3;
                break;
            case 5:
                tabPts[0] = 30;
                tabPts[1] = 2;
                break;
            case 6:
                tabPts[0] = 50;
                tabPts[1] = 1;
                break;
            case 7:
                tabPts[0] = 75;
                tabPts[1] = 0;
                break;
        }
        return tabPts;
    }
}