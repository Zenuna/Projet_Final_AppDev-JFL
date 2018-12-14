package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.CompteSimple;
import cgg.informatique.jfl.webSocket.Message;
import cgg.informatique.jfl.webSocket.Reponse;
import cgg.informatique.jfl.webSocket.WebSocketApplication;
import cgg.informatique.jfl.webSocket.dao.*;
import cgg.informatique.jfl.webSocket.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReponseControleur {
    @Autowired
    private CompteDao compteDao;

    @Autowired
    private CombatDao combatDao;

    @Autowired
    private ExamenDao examenDao;

    @Autowired
    private AvatarDao avatarDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private GroupeDao groupDao;

    private List<Compte> lstComptesAilleurs = new ArrayList<>();
    private List<Compte> lstComptesSpectateur = new ArrayList<>();
    private List<Compte> lstComptesAttente = new ArrayList<>();
    private List<Compte> lstComptesArbitre = new ArrayList<>();

    static public Map<String, String> listeDesConnexions = new HashMap();


    static long id = 1;

    //Constante pour btn (USERS)
    private final String s1 = "s1@dojo";
    private final String v1 = "v1@dojo";

    // POUR TROUVER AVATAR
    @RequestMapping(value="/TrouverAvatarSupreme/{nom}", method= RequestMethod.GET)
    public String avatar(@PathVariable String nom) {
        return avatarDao.findByNom(nom).getAvatar();
    }


    @CrossOrigin()
    @MessageMapping("/messageprive")
    @SendTo("/sujet/prive")
    public Reponse prive (Message message) throws Exception{
        Boolean booValide = false;
        for(String keys : listeDesConnexions.keySet()){
            if(listeDesConnexions.get(keys).equals(message.getSessionRest())){
                booValide = true;
            }
        }
        if(booValide)
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
        else
            return new Reponse(id++, message.getDe(), "Erreur (Message privé) : Vous n'êtes pas un utilisateur authentifié!", message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/messagepublique")
    @SendTo("/sujet/publique")
    public Reponse publique (Message message) throws Exception{
        Boolean booValide = false;
        for(String keys : listeDesConnexions.keySet()){
            if(listeDesConnexions.get(keys).equals(message.getSessionRest()) && !compteDao.findById(keys).get().getRole().getRole().equals("NOUVEAU")){
                booValide = true;
            }
        }
        if(booValide){
            return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
        }
        else{
            return new Reponse(id++, message.getDe(), "Erreur (Message publique) : Vous n'avez pas l'authorité pour envoyer des messages publiques!", message.getCreation(), message.getAvatar());
        }

    }

    // POUR POSITION
    @CrossOrigin()
    @MessageMapping("/listePosition")
    @SendTo("/sujet/radiogroup")
    public Reponse position(Message message) throws Exception{
        Compte compte = compteDao.findById(message.getDe()).get();


        //On positionne/repositionne
        videListe(message.getTexte().trim().toUpperCase(),compte);

        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // POUR btnCombat
    // TRUE  = Rouge gagne
    // FALSE  = Blanc gagne
    // NULLE = Nulle
    @CrossOrigin()
    @MessageMapping("/Combat")
    @SendTo("/sujet/Combat")
    public Reponse combat(Message message) throws Exception{
        //On récupère le combattant actuel et place les autres
        Compte compteRouge = compteDao.findById(message.getDe()).get();
        Compte compteBlanc = compteDao.findById(s1).get();
        Compte compteArbitre = compteDao.findById(v1).get();
        Combat combat = new Combat();

        //Vérif
        if(compteRouge.getUsername().equals(compteArbitre.getUsername())){
            System.out.println("COMBAT : Le combattant rouge ne peut pas être aussi l'arbitre!");
            return new Reponse(id++, message.getDe(), "Le combattant rouge ne peut pas être aussi l'arbitre!", message.getCreation(), message.getAvatar());
        }
        else if(compteRouge.getUsername().equals(compteBlanc.getUsername())){
            System.out.println("COMBAT : Le combattant rouge ne peut pas être aussi le combattant blanc!");
            return new Reponse(id++, message.getDe(), "Le combattant rouge ne peut pas être aussi le combattant blanc!", message.getCreation(), message.getAvatar());
        }

        combat = resultatCombat(compteRouge, compteBlanc, compteArbitre, combat, message.getTexte().trim().toUpperCase());
        if(combat.getDate() != null) combatDao.saveAndFlush(combat);

        System.out.println("COMBAT : OK");
        return new Reponse(id++, message.getDe(), "OK", message.getCreation(), message.getAvatar());
    }

    // POUR btnArbitre
    // TRUE  = Rouge gagne
    // FALSE  = Blanc gagne
    // NULLE = Nulle
    @CrossOrigin()
    @MessageMapping("/Arbitre")
    @SendTo("/sujet/Arbitre")
    public Reponse arbitre(Message message) throws Exception{
        //On récupère l'arbitre actuel et place les autres
        Compte compteArbitre = compteDao.findById(message.getDe()).get();
        Compte compteRouge =  compteDao.findById(v1).get();
        Compte compteBlanc =compteDao.findById(s1).get();
        Combat combat = new Combat();
        //Vérif
        if(compteArbitre.getUsername().equals(compteRouge.getUsername())){
            System.out.println("ARBITRE : L'arbitre ne peut pas être aussi le combattant rouge!");
            return new Reponse(id++, message.getDe(), "L'arbitre ne peut pas être aussi le combattant rouge!", message.getCreation(), message.getAvatar());
        }
        else if(compteArbitre.getUsername().equals(compteBlanc.getUsername())){
            System.out.println("ARBITRE : L'arbitre ne peut pas être aussi le combattant blanc!");
            return new Reponse(id++, message.getDe(), "L'arbitre ne peut pas être aussi le combattant blanc!", message.getCreation(), message.getAvatar());
        }
        combat = resultatCombat(compteRouge, compteBlanc, compteArbitre, combat, message.getTexte().trim().toUpperCase());
        if(combat.getDate() != null) combatDao.saveAndFlush(combat);

        System.out.println("ARBITRE : OK");
        return new Reponse(id++, message.getDe(), "OK", message.getCreation(), message.getAvatar());
    }

    // POUR btnArbitre
    // TRUE  = Examen passé
    // FALSE  = Examen échoué
    @CrossOrigin()
    @MessageMapping("/Examen")
    @SendTo("/sujet/Examen")
    public Reponse examen(Message message) throws Exception{
        //On récupère l'utilisateur evaluer
        Compte compteEvaluer =  compteDao.findById(message.getDe()).get();
        Compte compteEvaluateur = compteDao.findById(v1).get();

        int[] tabPtsCredits = trouvePtsEtCredits(compteEvaluer.getUsername());
        String strTexte = message.getTexte().trim().toUpperCase();
        Examen examen= new Examen();

        //Vérif
        if(tabPtsCredits[0] < 100){
            System.out.println("EXAMEN : Vous n'avez pas les 100 points nécessaires");
            return new Reponse(id++, message.getDe(), "Vous n'avez pas les 100 points nécessaires", message.getCreation(), message.getAvatar());
        }
        if(tabPtsCredits[1]<10){
            System.out.println("EXAMEN : Vous n'avaz pas les 10 crédits nécessaires");
            return new Reponse(id++, message.getDe(), "Vous n'avaz pas les 10 crédits nécessaires", message.getCreation(), message.getAvatar());
        }
        if(compteEvaluer.getGroupe().getId()>=7){
            System.out.println("EXAMEN : Vous avez déjà la plus haute ceinture accessible");
            return new Reponse(id++, message.getDe(), "Vous avez déjà la plus haute ceinture accessible", message.getCreation(), message.getAvatar());
        }

        if(strTexte.equals("TRUE")){
            examen = new Examen(System.currentTimeMillis(),true,compteEvaluer.getGroupe(),compteEvaluateur,compteEvaluer);
            compteEvaluer.setGroupe(groupDao.findById(compteEvaluer.getGroupe().getId()+1).get());
            compteDao.saveAndFlush(compteEvaluer);
        }
        else if(strTexte.equals("FALSE")) {
            examen = new Examen(System.currentTimeMillis(),false,compteEvaluer.getGroupe(),compteEvaluateur,compteEvaluer);
        }

        if(examen.getDate() != null) examenDao.saveAndFlush(examen);

        System.out.println("EXAMEN : OK");
        return new Reponse(id++, message.getDe(), "OK", message.getCreation(), message.getAvatar());
    }

    // POUR btnAncien
    @CrossOrigin()
    @MessageMapping("/Ancien")
    @SendTo("/sujet/Ancien")
    public Reponse ancien(Message message) throws Exception{
        Compte compte =  compteDao.findById(message.getDe()).get();

        if(compte.getRole().getId()>=2){
            System.out.println("ANCIEN : Vous êtes déjà ancien ou plus élevé");
            return new Reponse(id++, message.getDe(), "Vous êtes déjà ancien ou plus élevé", message.getCreation(), message.getAvatar());
        }
        int[] tabPtsCredits = trouvePtsEtCredits(compte.getUsername());
        if(tabPtsCredits[1]<10){
            System.out.println("ANCIEN : Vous n'avaz pas les 10 crédits nécessaires");
            return new Reponse(id++, message.getDe(), "Vous n'avaz pas les 10 crédits nécessaires", message.getCreation(), message.getAvatar());
        }
        if(tabPtsCredits[2]<30){
            System.out.println("ANCIEN : Vous n'avaz pas arbitrer 30 combats");
            return new Reponse(id++, message.getDe(), "Vous n'avaz pas arbitrer 30 combats", message.getCreation(), message.getAvatar());
        }
        Role role = roleDao.findById(2).get();
        compte.setRole(role);
        compte.setAnciendepuis(System.currentTimeMillis());
        compteDao.saveAndFlush(compte);

        System.out.println("OK");
        return new Reponse(id++, message.getDe(), "OK", message.getCreation(), message.getAvatar());
    }

    // POUR TROUVER LISTE AILLEURS
    @RequestMapping(value="/ListeAilleur", method= RequestMethod.GET)
    public String ListeAilleur(Model model) {
        return "AIL"+lstComptesAilleurs.toString();
    }
//    // POUR TROUVER LISTE AILLEURS
//    @RequestMapping(value="/ListeAvatarAilleur", method= RequestMethod.GET)
//    public String ListeAvatarAilleur(Model model) {
//
//        String strAvatarAilleurs = "";
//        for (Compte c : lstComptesAilleurs){
//            strAvatarAilleurs += c.getAvatar().getAvatar()+"-----";
//        }
//        System.out.println(lstComptesAilleurs);
//       // strAvatarAilleurs += compteDao.findById("v1@dojo").get().getAvatar().getAvatar();
//        return strAvatarAilleurs;
//    }

    // POUR TROUVER LISTE ARBITRE
    @RequestMapping(value="/ListeArbitre", method= RequestMethod.GET)
    public String ListeArbitre(Model model) {
        return "ARB"+lstComptesArbitre.toString();
    }

    // POUR TROUVER LISTE ATTENTE
    @RequestMapping(value="/ListeAttente", method= RequestMethod.GET)
    public String ListeAttente(Model model) {
        return "ATT"+lstComptesAttente.toString();
    }
    // POUR TROUVER LISTE SPECTATEUR
    @RequestMapping(value="/ListeSpectateur", method= RequestMethod.GET)
    public String ListeSpectateur(Model model) {
        return "SPE"+lstComptesSpectateur.toString();
    }



    private void videListe(String strBonneListe, Compte compte){
        int index = -1;
        WebSocketApplication.videListeExtract(strBonneListe, compte, index, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
    }
    private int[] trouvePtsEtCredits(String username){
        int[] tabPtsCredits = new int[3];
        int points = 0 ;
        int credits = 0;
        int combatArbitrer = 0;
        long dateExamen = 0;

        if(compteDao.findById(username).get().getAnciendepuis() <= System.currentTimeMillis()){
            credits -=10;
        }else{
        }

        for(Examen lesExamens : examenDao.findAll()) {
            if (lesExamens.getEvalue().getUsername().equals(username)) {
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
            if(lesCombats.getArbitre().getUsername().equals(username)){
                combatArbitrer++;
            }
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
                else
                    credits -= 5;
            }
        }
        tabPtsCredits[0] = points;
        tabPtsCredits[1] = credits;
        tabPtsCredits[2] = combatArbitrer;
        return tabPtsCredits;
    }

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
    private Combat resultatCombat(Compte compteRouge, Compte compteBlanc, Compte compteArbitre, Combat combat, String strTexte) {
        if(strTexte.equals("TRUE")){
            combat = new Combat(1,System.currentTimeMillis(),0,10,compteArbitre,compteBlanc,compteBlanc.getGroupe(),compteRouge.getGroupe(),compteRouge);
        }
        else if(strTexte.equals("FALSE")) {
            combat = new Combat(1, System.currentTimeMillis(), 10, 0, compteArbitre, compteBlanc, compteBlanc.getGroupe(), compteRouge.getGroupe(), compteRouge);
        }
        else if(strTexte.equals("NULLE")){
            combat = new Combat(1,System.currentTimeMillis(),5,5,compteArbitre,compteBlanc,compteBlanc.getGroupe(),compteRouge.getGroupe(),compteRouge);
        }else if(strTexte.equals("FAUTE")){
            combat = new Combat(0,System.currentTimeMillis(),0,10,compteArbitre,compteBlanc,compteBlanc.getGroupe(),compteRouge.getGroupe(),compteRouge);
        }
        return combat;
    }
}

