package cgg.informatique.jfl.webSocket.controleurs;

import cgg.informatique.jfl.webSocket.dao.CompteDao;
import cgg.informatique.jfl.webSocket.entites.Combat;
import cgg.informatique.jfl.webSocket.entites.Compte;
import cgg.informatique.jfl.webSocket.entites.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class controleurMVC {

    @Autowired
    private CompteDao unCompteDao;



    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String racine(Model model) {
        List<Compte> lesComptes = unCompteDao.findAll();
        model.addAttribute("lesComptes", lesComptes);
        return "public/dojo";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Map<String, Object> model) {
        return "login";
    }

    @RequestMapping(value = "/ecole", method = RequestMethod.GET)
    public String ecole(Model model) {
        List<Compte> lesComptes = unCompteDao.findAll();
        List<Compte> lesRoles = new ArrayList<>();
        List<Compte> Shame = new ArrayList<>();
        for(Compte c: lesComptes){
            List<Examen> lstExam = c.getCptsEvaluer();
            int indexReussite = -1;
            int indexEchec = -1;
            for(int i = 0;i<lstExam.size();i++){
                if(lstExam.get(i).isaPasser() && !lstExam.get(i).isExamAncien()){
                    if(indexReussite == -1){
                        indexReussite = i;
                    }
                    else if(lstExam.get(i).getDate() > lstExam.get(indexReussite).getDate()){
                        indexReussite = i;
                    }
                }
                else if(!lstExam.get(i).isaPasser() && !lstExam.get(i).isExamAncien()){
                    if(indexEchec == -1){
                        indexEchec = i;
                    }
                    else if(lstExam.get(i).getDate() > lstExam.get(indexEchec).getDate()){
                        indexEchec = i;
                    }
                }
            }
            if(indexEchec == -1 && indexReussite == -1){
                lesRoles.add(c);
            }
            else {
                if (indexEchec == -1 && indexReussite != -1) {
                    lesRoles.add(c);
                } else if (indexReussite == -1 && indexEchec != -1) {
                    Shame.add(c);
                } else {
                    if (lstExam.get(indexEchec).getDate() > lstExam.get(indexReussite).getDate()) {
                        Shame.add(c);
                    } else {
                        lesRoles.add(c);
                    }
                }
            }
        }
        model.addAttribute("lesComptes", lesRoles);
        model.addAttribute("Shame",Shame);
        return "public/ecole";
    }

    @RequestMapping(value = "/gradation", method = RequestMethod.GET)
    public String gradation(Model model) {
        List<Compte> lesComptes = unCompteDao.findAll();
        List<Compte> lesComptesCeinture = new ArrayList<>();
        List<Compte> lesComptesVersAncien = new ArrayList<>();
        List<Compte> lesComptesVersSensei = new ArrayList<>();
        List<Compte> lesComptesHonteux = new ArrayList<>();
        for(Compte c: lesComptes){
            int nbPoints = 0;
            int nbCredits = 0;
            int nbCombatArbitre = 0;
            List<Combat> lstCombatRouge = c.getCptsRouge();
            List<Combat> lstCombatBlanc = c.getCptsBlanc();
            List<Combat> lstCombatArbitre = c.getCptsArbitre();
            List<Examen> lstExam = c.getCptsEvaluer();
            int indexExamen = -1;
            int indexExamenEchouee = -1;
            if(lstExam.size() > 0) {
                for (int i = 0; i < lstExam.size(); i++) {
                    if (lstExam.get(i).isaPasser()) {
                        nbCredits -= 10;
                        if(!lstExam.get(i).isExamAncien()){
                            if (indexExamen == -1) {
                                indexExamen = i;
                            } else if (lstExam.get(i).getDate() > lstExam.get(indexExamen).getDate()) {
                                indexExamen = i;
                            }

                        }
                    }
                    else{
                        nbCredits -= 5;
                        if(indexExamenEchouee == -1){
                            indexExamenEchouee = i;
                        }
                        else if(lstExam.get(i).getDate() > lstExam.get(indexExamenEchouee).getDate()){
                            indexExamenEchouee = i;
                        }
                    }
                }
                if(indexExamenEchouee == -1 && indexExamen == -1){
                }
                else {
                    if (indexExamenEchouee == -1 && indexExamen != -1) {
                    } else if (indexExamen == -1 && indexExamenEchouee != -1) {
                        lesComptesHonteux.add(c);
                    } else {
                        if (lstExam.get(indexExamenEchouee).getDate() > lstExam.get(indexExamen).getDate()) {
                            lesComptesHonteux.add(c);
                        }
                    }
                }
            }
            if(indexExamen != -1){
                for(Combat cRouge: lstCombatRouge){
                    if(cRouge.getDateCombat()>lstExam.get(indexExamen).getDate()){
                        nbPoints += cRouge.getPointsRouge();
                    }
                }
                for(Combat cBlanc: lstCombatBlanc){
                    if(cBlanc.getDateCombat()>lstExam.get(indexExamen).getDate()){
                        nbPoints += cBlanc.getPointsBlancs();
                    }
                }
                for(Combat cArbitre: lstCombatArbitre){
                    nbCombatArbitre++;
                    nbCredits += cArbitre.getCreditArbitre();
                }
                if(nbPoints>=100 && nbCredits >= 10 && !c.getGroupe().trim().equals("NOIR")){
                    lesComptesCeinture.add(c);
                }
                if(nbCombatArbitre >= 30 && nbCredits >= 10 && c.getRole().trim().equals("NOUVEAU")){
                    lesComptesVersAncien.add(c);
                }
                if(c.getGroupe().trim().equals("NOIR") && !c.getRole().trim().equals("VÉNÉRABLE")){
                    lesComptesVersSensei.add(c);
                }
            }
            else{
                for(Combat cRouge: lstCombatRouge){
                    nbPoints += cRouge.getPointsRouge();
                }
                for(Combat cBlanc: lstCombatBlanc){
                    nbPoints += cBlanc.getPointsBlancs();
                }
                for(Combat cArbitre: lstCombatArbitre){
                    nbCombatArbitre++;
                    nbCredits += cArbitre.getCreditArbitre();
                }
                if(nbPoints>=100 && nbCredits >= 10 && !c.getGroupe().trim().equals("NOIR")){
                    lesComptesCeinture.add(c);
                }
                if(nbCombatArbitre >= 30 && nbCredits >= 10 && c.getRole().trim().equals("NOUVEAU")){
                    lesComptesVersAncien.add(c);
                }
                if(c.getGroupe().trim().equals("NOIR")){
                    lesComptesVersSensei.add(c);
                }
            }

        }
        model.addAttribute("lesComptesCeinture", lesComptesCeinture);
        model.addAttribute("lesComptesVersAncien", lesComptesVersAncien);
        model.addAttribute("lesComptesVersSensei", lesComptesVersSensei);
        model.addAttribute("lesComptesHonteux", lesComptesHonteux);
        return "public/gradation";
    }

    @RequestMapping(value = "/kumite", method = RequestMethod.GET)
    public String kumite(Model model) {
        List<Compte> lesComptes = unCompteDao.findAll();
        model.addAttribute("lesComptes", lesComptes);
        return "public/kumite";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Map<String, Object> model) {
        return "public/index";
    }
}