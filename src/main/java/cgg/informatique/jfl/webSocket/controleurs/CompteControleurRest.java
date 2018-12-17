package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.dao.CombatDao;
import cgg.informatique.jfl.webSocket.dao.CompteDao;
import cgg.informatique.jfl.webSocket.dao.ExamenDao;
import cgg.informatique.jfl.webSocket.dao.GroupeDao;
import cgg.informatique.jfl.webSocket.entites.Combat;
import cgg.informatique.jfl.webSocket.entites.Compte;
import cgg.informatique.jfl.webSocket.CompteSimple;
import cgg.informatique.jfl.webSocket.entites.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class CompteControleurRest {

    @Autowired
    private CompteDao compteDao;

    @Autowired
    private CombatDao combatDao;

    @Autowired
    private ExamenDao examenDao;


    @Autowired
    private GroupeDao groupeDao;


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

    @RequestMapping(value = "/Comptes", method = RequestMethod.GET)
    public String trouverCompte(Map<String, Object> model) {
        List<CompteSimple> listComptesSimples = new ArrayList<>();
        CompteSimple nouveauCompteSimple = null;
        int points = 0;
        int credits = 0;
        long dateExamen = 0;
        long anciendepuis = 0;

        for (Compte lesComtpes : compteDao.findAll()) {
            points = 0;
            credits = 0;
            dateExamen = 0;

            if (lesComtpes.getAnciendepuis() < System.currentTimeMillis()) {
                credits -= 10;
                anciendepuis = compteDao.findById(lesComtpes.getUsername()).get().getAnciendepuis();
            }

            for (Examen lesExamens : examenDao.findAll()) {
                if (lesExamens.getEvalue().getUsername().equals(lesComtpes.getUsername())) {
                    if (dateExamen < lesExamens.getDate() && lesExamens.getaReussi())
                        dateExamen = lesExamens.getDate();
                    if (!lesExamens.getaReussi() && !lesExamens.getCeinture().getGroupe().equals("BLANCHE")) {
                        credits -= 5;
                    } else {
                        if (!lesExamens.getCeinture().getGroupe().equals("BLANCHE"))
                            credits -= 10;
                    }

                }
            }
            for (Combat lesCombats : combatDao.findAll()) {
                int diffCeinture = lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId();
                int[] tabPts = pointsParDifference(Math.abs(lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId()));

                if (lesCombats.getDate() >= dateExamen) {
                    if (lesCombats.getBlanc().getUsername().equals(lesComtpes.getUsername())) {
                        if (lesCombats.getPointsBlanc() == 10) {
                            if (diffCeinture > 0) {
                                points += tabPts[0];
                            } else if (diffCeinture == 0) {
                                points += tabPts[0];
                            } else {
                                points += tabPts[1];
                            }
                        } else if (lesCombats.getPointsBlanc() == 5) {
                            int pts0 = Math.round(tabPts[0] / 2);
                            int pts1 = Math.round(tabPts[1] / 2);
                            if (diffCeinture > 0) {
                                points += pts0;
                            } else if (diffCeinture == 0) {
                                points += pts0;
                            } else {
                                points += pts1;
                            }
                        }
                    } else if (lesCombats.getRouge().getUsername().equals(lesComtpes.getUsername())) {
                        if (lesCombats.getPointsRouge() == 10) {
                            if (diffCeinture > 0) {
                                points += tabPts[0];
                            } else if (diffCeinture == 0) {
                                points += tabPts[0];
                            } else {
                                points += tabPts[1];
                            }
                        } else if (lesCombats.getPointsRouge() == 5) {
                            int pts0 = Math.round(tabPts[0] / 2);
                            int pts1 = Math.round(tabPts[1] / 2);
                            if (diffCeinture > 0) {
                                points += pts0;
                            } else if (diffCeinture == 0) {
                                points += pts0;
                            } else {
                                points += pts1;
                            }
                        }
                    }
                }
                if (lesCombats.getArbitre().getUsername().equals(lesComtpes.getUsername())) {
                    if (lesCombats.getCreditsArbitre() == 1)
                        credits += lesCombats.getCreditsArbitre();
                    else
                        credits -= 5;
                }
            }

            nouveauCompteSimple = new CompteSimple(lesComtpes.getUsername(), lesComtpes.getFullname(),
                    lesComtpes.getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,", ""), points, credits,
                    lesComtpes.getGroupe().getGroupe(), lesComtpes.getRole().getRole(),anciendepuis);
            listComptesSimples.add(nouveauCompteSimple);
        }
        return listComptesSimples.toString();
    }

    @RequestMapping(value = "/CompteSelect/{username}", method = RequestMethod.GET)
    public String trouverCompteSelect(@PathVariable String username, Map<String, Object> model) {
        List<CompteSimple> listComptesSimples = new ArrayList<>();
        CompteSimple nouveauCompteSimple = null;
        int points = 0;
        int credits = 0;
        long dateExamen = 0;
        long anciendepuis = 0;
        if (compteDao.findById(username).get().getAnciendepuis() < System.currentTimeMillis()) {
            credits -= 10;
            anciendepuis = compteDao.findById(username).get().getAnciendepuis();
        }

        for (Examen lesExamens : examenDao.findAll()) {
            if (lesExamens.getEvalue().getUsername().equals(username)) {
                if (dateExamen < lesExamens.getDate()) {
                    if (lesExamens.getaReussi()) {
                        dateExamen = lesExamens.getDate();
                    }
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

                if (lesCombats.getBlanc().getUsername().equals(username)) {

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
                } else if (lesCombats.getRouge().getUsername().equals(username)) {
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
            if (lesCombats.getArbitre().getUsername().equals(username)) {
                if (lesCombats.getCreditsArbitre() == 1)
                    credits += lesCombats.getCreditsArbitre();
                else {
                    credits -= 5;
                }
            }
        }


        nouveauCompteSimple = new CompteSimple(username, compteDao.findById(username).get().getFullname(),
                compteDao.findById(username).get().getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,", ""), points, credits,
                compteDao.findById(username).get().getGroupe().getGroupe(), compteDao.findById(username).get().getRole().getRole(),anciendepuis);


        return nouveauCompteSimple.toString();
    }

    @RequestMapping(value = "/Compte/{username}", method = RequestMethod.GET)
    public String trouverCompteUserName(@PathVariable String username, Map<String, Object> model) {

        Compte compteTrouver = null;
        if (compteDao.findById(username).get() != null)
            compteTrouver = compteDao.findById(username).get();
        return compteTrouver.getUsername();
    }


    @RequestMapping(value = "/Avatar/{avatar}", method = RequestMethod.GET)
    public String trouverAvatar(@PathVariable String avatar, Map<String, Object> model) {

        Compte compteTrouver = null;
        if (compteDao.findById(avatar).get() != null)
            compteTrouver = compteDao.findById(avatar).get();
        return compteTrouver.getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,", "");
    }

    @RequestMapping(value = "/Historique/{compte}", method = RequestMethod.GET)
    public String genereHistorique(@PathVariable String compte, Map<String, Object> model) {

        Compte compteTrouver = null;
        boolean booAncien = true;
        if (compteDao.findById(compte).get() != null)
            compteTrouver = compteDao.findById(compte).get();
        String strFluxHistorique = "";
        strFluxHistorique += "<div style='font-family: Courier New, monospace;white-space: pre;'>Historique du membre : " + compteTrouver.getUsername();
        if (compteTrouver.getAnciendepuis() < System.currentTimeMillis()) {
            Date date = new Date(compteTrouver.getAnciendepuis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA_FRENCH);
            String laDate = dateFormat.format(date);
            strFluxHistorique += " Ancien depuis : " + laDate + "\n\n<br>";
        }
        int points = 0;
        int credits = 0;
        Long derniereDate = 0l;
        for (Examen lesExamens : examenDao.findAll()) {
            if (lesExamens.getEvalue().equals(compteTrouver)) {
                if (!lesExamens.getCeinture().getGroupe().equals("BLANCHE"))
                    strFluxHistorique += "Combats\n<br>|Date                         |Arbitre   |Crédits|Rouge     |Ceinture|Points |Blanc     |Ceinture|Points |\n<br>";

                for (Combat lesCombats : combatDao.findAll()) {
                    int pointsRouges = 0;
                    int pointsBlancs = 0;
                    int diffCeinture = lesCombats.getCeintureRouge().getId() - lesCombats.getCeintureBlanc().getId();
                    int[] tabPts = pointsParDifference(Math.abs(lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId()));
                    if (lesCombats.getDate() < lesExamens.getDate() && derniereDate != 0 && lesCombats.getDate() >= derniereDate) {
                        Date date = new Date(lesCombats.getDate());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA_FRENCH);
                        String laDate = dateFormat.format(date);


                        if (lesCombats.getPointsBlanc() == 10) {
                            if (diffCeinture > 0) {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += tabPts[0];
                                pointsBlancs = tabPts[0];
                            } else if (diffCeinture == 0) {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += tabPts[0];
                                pointsBlancs = tabPts[0];
                            } else {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += tabPts[1];
                                pointsBlancs = tabPts[1];
                            }
                        } else if (lesCombats.getPointsBlanc() == 5) {
                            double pts0 = tabPts[0];
                            double pts1 = tabPts[1];
                            if (diffCeinture > 0) {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += Math.round(pts0 / 2);
                                pointsBlancs = (int) Math.round(pts0 / 2);
                            } else if (diffCeinture == 0) {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += Math.round(pts0 / 2);
                                pointsBlancs = (int) Math.round(pts0 / 2);
                            } else {
                                if (lesCombats.getBlanc().equals(compteTrouver)) points += Math.round(pts1 / 2);
                                pointsBlancs = (int) Math.round(pts1 / 2);
                            }
                        }

                        if (lesCombats.getPointsRouge() == 10) {
                            diffCeinture = lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId();
                            if (diffCeinture > 0) {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += tabPts[0];
                                pointsRouges = tabPts[0];
                            } else if (diffCeinture == 0) {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += tabPts[0];
                                pointsRouges = tabPts[0];
                            } else {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += tabPts[1];
                                pointsRouges = tabPts[1];
                            }
                        } else if (lesCombats.getPointsRouge() == 5) {
                            diffCeinture = lesCombats.getCeintureBlanc().getId() - lesCombats.getCeintureRouge().getId();
                            double pts0 = tabPts[0];
                            double pts1 = tabPts[1];
                            if (diffCeinture > 0) {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += Math.round(pts0 / 2);
                                pointsRouges = (int) Math.round(pts0 / 2);
                            } else if (diffCeinture == 0) {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += Math.round(pts0 / 2);
                                pointsRouges = (int) Math.round(pts0 / 2);
                            } else {
                                if (lesCombats.getRouge().equals(compteTrouver)) points += Math.round(pts1 / 2);
                                pointsRouges = (int) Math.round(pts1 / 2);
                            }
                        }

                        if (lesCombats.getBlanc().equals(compteTrouver)) {

                            //points += lesCombats.getPointsBlanc();
                            strFluxHistorique += String.format("%-30s", "|" + laDate) +
                                    String.format("%-11s", "|" + lesCombats.getArbitre().getUsername())
                                    + String.format("%-8s", "|" + lesCombats.getCreditsArbitre())
                                    + String.format("%-11s", "|" + lesCombats.getRouge().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureRouge().getGroupe())
                                    + String.format("%-8s", "|" + pointsRouges)
                                    + String.format("%-11s", "|" + lesCombats.getBlanc().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureBlanc().getGroupe())
                                    + String.format("%-8s", "|" + pointsBlancs) + "|"
                                    + "<br>";
                        } else if (lesCombats.getRouge().equals(compteTrouver)) {
                            // points += lesCombats.getPointsRouge();
                            strFluxHistorique += String.format("%-30s", "|" + laDate) +
                                    String.format("%-11s", "|" + lesCombats.getArbitre().getUsername())
                                    + String.format("%-8s", "|" + lesCombats.getCreditsArbitre())
                                    + String.format("%-11s", "|" + lesCombats.getRouge().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureRouge().getGroupe())
                                    + String.format("%-8s", "|" + pointsRouges)
                                    + String.format("%-11s", "|" + lesCombats.getBlanc().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureBlanc().getGroupe())
                                    + String.format("%-8s", "|" + pointsBlancs) + "|"
                                    + "<br>";
                        } else if (lesCombats.getArbitre().equals(compteTrouver)) {
                            credits += lesCombats.getCreditsArbitre();
                            strFluxHistorique += String.format("%-30s", "|" + laDate) +
                                    String.format("%-11s", "|" + lesCombats.getArbitre().getUsername())
                                    + String.format("%-8s", "|" + lesCombats.getCreditsArbitre())
                                    + String.format("%-11s", "|" + lesCombats.getRouge().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureRouge().getGroupe())
                                    + String.format("%-8s", "|" + pointsRouges)
                                    + String.format("%-11s", "|" + lesCombats.getBlanc().getUsername())
                                    + String.format("%-9s", "|" + lesCombats.getCeintureBlanc().getGroupe())
                                    + String.format("%-8s", "|" + pointsBlancs) + "|"
                                    + "<br>";
                        }

                    }

                }


                Date date = new Date(lesExamens.getDate());
                if (lesExamens.getDate() >= compteTrouver.getAnciendepuis() && booAncien) {
                    credits -= 10;
                    booAncien = false;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA_FRENCH);
                String laDate = dateFormat.format(date);
                strFluxHistorique += "\n<br>Examen : " + laDate + ", Points " + points + ", Crédits " + credits + ", Ceinture " + lesExamens.getCeinture().getGroupe() + " Réussi : " + lesExamens.getaReussi() + "<br><br>\n\n";
                derniereDate = lesExamens.getDate();
                if (lesExamens.getaReussi() && !lesExamens.getCeinture().getGroupe().equals("BLANCHE")) {
                    points = 0;
                    credits = credits - 10;
                } else if (!lesExamens.getCeinture().getGroupe().equals("BLANCHE")) {
                    credits = credits - 5;
                }
            }
        }
        strFluxHistorique += "</div>";
        return strFluxHistorique;
    }





}

