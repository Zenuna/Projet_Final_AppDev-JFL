package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.dao.CombatDao;
import cgg.informatique.jfl.webSocket.dao.CompteDao;
import cgg.informatique.jfl.webSocket.dao.ExamenDao;
import cgg.informatique.jfl.webSocket.entites.Combat;
import cgg.informatique.jfl.webSocket.entites.Compte;
import cgg.informatique.jfl.webSocket.CompteSimple;
import cgg.informatique.jfl.webSocket.entites.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class CompteControleurRest {

    @Autowired
    private CompteDao compteDao ;

    @Autowired
    private CombatDao combatDao ;

    @Autowired
    private ExamenDao examenDao ;


    public int[] pointsParDifference(int diffCeinture){
        int[] tabPts = new int[2];
        switch(diffCeinture){
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
        }
        return tabPts;
    }

    @RequestMapping(value="/Comptes", method= RequestMethod.GET)
    public String trouverCompte(Map<String, Object> model){
        List<CompteSimple> listComptesSimples = new ArrayList<>();
        CompteSimple nouveauCompteSimple = null;
        int points = 0 ;
        int credits = 0;
        long dateExamen = 0;




        for(Compte lesComtpes : compteDao.findAll()) {
            points = 0;
            credits = 0;
            dateExamen = 0;

            if(lesComtpes.getAnciendepuis() < System.currentTimeMillis()){
                credits -=10;
                System.err.println("ANCIEN VALIDE");
            }else{
                System.err.println("ANCIEN NONVALIDE");
            }

            for(Examen lesExamens : examenDao.findAll()) {
                if (lesExamens.getEvalue().getUsername().equals(lesComtpes.getUsername())) {
                    if (dateExamen < lesExamens.getDate() && lesExamens.getaReussi())
                        dateExamen = lesExamens.getDate();
                    if(!lesExamens.getaReussi() && !lesExamens.getCeinture().getGroupe().equals("BLANCHE")){
                        credits -= 5;
                    }else{
                        if(!lesExamens.getCeinture().getGroupe().equals("BLANCHE"))
                            credits -= 10;
                    }

                }
            }
            for (Combat lesCombats : combatDao.findAll()) {
                int diffCeinture =lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId();
                int[] tabPts = pointsParDifference(Math.abs(lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId()));

                if(lesCombats.getDate() >= dateExamen){
                    if (lesCombats.getBlanc().getUsername().equals(lesComtpes.getUsername())) {
                        if(lesCombats.getPointsBlanc() == 10){
                            if(diffCeinture > 0){
                                points += tabPts[0];
                            }
                            else if(diffCeinture == 0){
                                points += tabPts[0];
                            }
                            else{
                                points += tabPts[1];
                            }
                        }else if(lesCombats.getPointsBlanc() == 5){
                            int pts0 = Math.round(tabPts[0]/2);
                            int pts1 = Math.round(tabPts[1]/2);
                            if(diffCeinture > 0){
                                points += pts0;
                            }
                            else if(diffCeinture == 0){
                                points += pts0;
                            }
                            else{
                                points += pts1;
                            }
                        }
                    } else if (lesCombats.getRouge().getUsername().equals(lesComtpes.getUsername())) {
                        if(lesCombats.getPointsRouge() == 10){
                            if(diffCeinture > 0){
                                points += tabPts[0];
                            }
                            else if(diffCeinture == 0){
                                points += tabPts[0];
                            }
                            else{
                                points += tabPts[1];
                            }
                        }else if(lesCombats.getPointsRouge() == 5){
                            int pts0 = Math.round(tabPts[0]/2);
                            int pts1 = Math.round(tabPts[1]/2);
                            if(diffCeinture > 0){
                                points += pts0;
                            }
                            else if(diffCeinture == 0){
                                points += pts0;
                            }
                            else{
                                points += pts1;
                            }
                        }
                    }
                }
                if (lesCombats.getArbitre().getUsername().equals(lesComtpes.getUsername())) {
                    if(lesCombats.getCreditsArbitre() == 1)
                        credits += lesCombats.getCreditsArbitre();
                    else
                        credits -= 5;
                }
            }

            nouveauCompteSimple = new CompteSimple(lesComtpes.getUsername(),
                    lesComtpes.getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,",""),points, credits,
                    lesComtpes.getGroupe().getGroupe(), lesComtpes.getRole().getRole());
            listComptesSimples.add(nouveauCompteSimple);
        }
        return listComptesSimples.toString();
    }
    @RequestMapping(value="/CompteSelect/{username}", method= RequestMethod.GET)
    public String trouverCompteSelect(@PathVariable String username, Map<String, Object> model){
        List<CompteSimple> listComptesSimples = new ArrayList<>();
        CompteSimple nouveauCompteSimple = null;
        int points = 0 ;
        int credits = 0;
        long dateExamen = 0;

        if(compteDao.findById(username).get().getAnciendepuis() < System.currentTimeMillis()){
            credits -=10;
            System.err.println("ANCIEN VALIDE");
        }else{
            System.err.println("ANCIEN NONVALIDE");
        }

        for(Examen lesExamens : examenDao.findAll()) {
            if (lesExamens.getEvalue().getUsername().equals(username)) {
                if (dateExamen < lesExamens.getDate())
                    dateExamen = lesExamens.getDate();
                if(lesExamens.getaReussi() == false && !lesExamens.getCeinture().getGroupe().equals("BLANCHE")){
                    credits -= 5;
                }else{
                    if(!lesExamens.getCeinture().getGroupe().equals("BLANCHE"))
                        credits -= 10;
                }

            }
        }
        for (Combat lesCombats : combatDao.findAll()) {
            int diffCeinture =lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId();
            int[] tabPts = pointsParDifference(Math.abs(lesCombats.getBlanc().getGroupe().getId() - lesCombats.getRouge().getGroupe().getId()));

            if(lesCombats.getDate() >= dateExamen){
                if (lesCombats.getBlanc().getUsername().equals(username)) {
                    if(lesCombats.getPointsBlanc() == 10){
                        if(diffCeinture > 0){
                            points += tabPts[0];
                        }
                        else if(diffCeinture == 0){
                            points += tabPts[0];
                        }
                        else{
                            points += tabPts[1];
                        }
                    }else if(lesCombats.getPointsBlanc() == 5){
                        int pts0 = Math.round(tabPts[0]/2);
                        int pts1 = Math.round(tabPts[1]/2);
                        if(diffCeinture > 0){
                            points += pts0;
                        }
                        else if(diffCeinture == 0){
                            points += pts0;
                        }
                        else{
                            points += pts1;
                        }
                    }
                } else if (lesCombats.getRouge().getUsername().equals(username)) {
                    if(lesCombats.getPointsRouge() == 10){
                        if(diffCeinture > 0){
                            points += tabPts[0];
                        }
                        else if(diffCeinture == 0){
                            points += tabPts[0];
                        }
                        else{
                            points += tabPts[1];
                        }
                    }else if(lesCombats.getPointsRouge() == 5){
                        int pts0 = Math.round(tabPts[0]/2);
                        int pts1 = Math.round(tabPts[1]/2);
                        if(diffCeinture > 0){
                            points += pts0;
                        }
                        else if(diffCeinture == 0){
                            points += pts0;
                        }
                        else{
                            points += pts1;
                        }
                    }
                }
            }
            if (lesCombats.getArbitre().getUsername().equals(username)) {
                if(lesCombats.getCreditsArbitre() == 1)
                    credits += lesCombats.getCreditsArbitre();
                else{
                    credits -= 5;
                }

            }
        }


            nouveauCompteSimple = new CompteSimple(username,
                    compteDao.findById(username).get().getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,",""),points, credits,
                    compteDao.findById(username).get().getGroupe().getGroupe(),  compteDao.findById(username).get().getRole().getRole());


        return nouveauCompteSimple.toString();
    }

    @RequestMapping(value="/Compte/{username}", method= RequestMethod.GET)
    public String trouverCompteUserName(@PathVariable String username, Map<String, Object> model){

        Compte compteTrouver = null;
        if(compteDao.findById(username).get() != null)
            compteTrouver = compteDao.findById(username).get();
        return compteTrouver.getUsername();
    }


    @RequestMapping(value="/Avatar/{avatar}", method= RequestMethod.GET)
    public String trouverAvatar(@PathVariable String avatar, Map<String, Object> model){

        Compte compteTrouver = null;
        if(compteDao.findById(avatar).get() != null)
            compteTrouver = compteDao.findById(avatar).get();
        return compteTrouver.getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,","");
    }



}
