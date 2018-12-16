package cgg.informatique.jfl.webSocket.controleurs;

import cgg.informatique.jfl.webSocket.Message;
import cgg.informatique.jfl.webSocket.Reponse;
import cgg.informatique.jfl.webSocket.dao.*;
import cgg.informatique.jfl.webSocket.entites.*;

import org.json.JSONObject;
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
    private AvatarDao avatarDao;

    private static List<Compte> lstComptesAilleurs = new ArrayList<>();
    private static List<Compte> lstComptesSpectateur = new ArrayList<>();
    private static List<Compte> lstComptesAttente = new ArrayList<>();
    private static List<Compte> lstComptesArbitre = new ArrayList<>();

    private Compte combattantRouge = null;
    private Compte combattantBlanc = null;
    private Compte arbitre = null;

    static public Map<String, String> listeDesConnexions = new HashMap();


    private static long id = 1;

    private boolean blnRetirerCombattantBlanc = false;
    private boolean blnRetirerCombattantRouge = false;
    private boolean blnRetirerArbitre = false;
    private boolean blnArbitreEtaitAttente = false;
    private boolean blnArbitreEtaitSpectateur = false;
    private boolean blnCombattantRougeEtaitArbitre = false;
    private boolean blnCombattantBlancEtaitArbitre = false;
    // POUR TROUVER AVATAR
    @RequestMapping(value="/TrouverAvatarSupreme/{nom}", method= RequestMethod.GET)
    public String avatar(@PathVariable String nom) {
        return avatarDao.findByNom(nom).getAvatar();
    }

    // Lancer par le client pour indiquer sa position dans l'école
    @CrossOrigin()
    @MessageMapping("/listePosition")
    @SendTo("/sujet/radiogroup")
    public Reponse position(Message message) throws Exception{
        Compte compte = compteDao.findById(message.getDe()).get();
        //On positionne/repositionne
        videListeExtract(message.getTexte().trim().toUpperCase(), compte, -1, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // Lancer par le serveur lorsqu'un combat est prêt
    @CrossOrigin()
    @MessageMapping("/debutCombat")
    @SendTo("/sujet/debutCombat")
    public Reponse debutCombat(Message message) throws Exception {
        System.out.println("DEBUT COMBAT");
        //System.err.println(message.toString());

        String[] strCombat = message.getTexte().split("-A-");
        for(int i = 0;i<strCombat.length;i++){
            JSONObject json = new JSONObject(strCombat[i]);
            if(strCombat.length-1 != i){
                for(Compte c:lstComptesAttente){
                    if(c.getUsername().equals(json.getString("courriel"))){
                        if(i == 0){

                            combattantRouge = c;
                        }
                        else {
                            combattantBlanc = c;
                        }

                    }
                }
                for(Compte c:lstComptesArbitre){
                    if(c.getUsername().equals(json.getString("courriel"))){
                        if(i == 0) blnCombattantRougeEtaitArbitre = true;
                        else blnCombattantBlancEtaitArbitre = true;
                    }
                }
            }
            else{
                for(Compte c:lstComptesArbitre){
                    if(c.getUsername().equals(json.getString("courriel"))){
                        arbitre = c;
                    }
                }
                for(Compte c:lstComptesAttente){
                    if(c.getUsername().equals(json.getString("courriel"))){
                        blnArbitreEtaitAttente = true;
                    }
                }
                for(Compte c:lstComptesSpectateur){
                    if(c.getUsername().equals(json.getString("courriel"))){
                        blnArbitreEtaitSpectateur = true;
                    }
                }
            }
        }

        if (blnArbitreEtaitSpectateur) lstComptesSpectateur.remove(arbitre);
        if( blnArbitreEtaitAttente) lstComptesAttente.remove(arbitre);
        if( blnCombattantBlancEtaitArbitre) lstComptesArbitre.remove(combattantBlanc);
        if( blnCombattantRougeEtaitArbitre) lstComptesArbitre.remove(combattantRouge);
        lstComptesAttente.remove(combattantBlanc);
        lstComptesAttente.remove(combattantRouge);
        lstComptesArbitre.remove(arbitre);
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // Lancer par le serveur au milieu du combat pour envoyer le résultat du combat
    @CrossOrigin()
    @MessageMapping("/envoyerChiffre")
    @SendTo("/sujet/envoyerChiffre")
    public Reponse envoyerChiffre(Message message) throws Exception {
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // Lancer par le serveur à la fin du combat pour rétablir l'ordre
    @CrossOrigin()
    @MessageMapping("/finCombat")
    @SendTo("/sujet/finCombat")
    public Reponse finCombat(Message message) throws Exception {
        if(!blnRetirerCombattantRouge) {
            lstComptesAttente.add(combattantRouge);
            if( blnCombattantRougeEtaitArbitre) lstComptesArbitre.add(combattantRouge);
        }
        if(!blnRetirerCombattantBlanc) {
            lstComptesAttente.add(combattantBlanc);
            if( blnCombattantBlancEtaitArbitre) lstComptesArbitre.add(combattantBlanc);
        }
        if(!blnRetirerArbitre) {
            lstComptesArbitre.add(arbitre);
            if (blnArbitreEtaitSpectateur) lstComptesSpectateur.add(arbitre);
            if( blnArbitreEtaitAttente) lstComptesAttente.add(arbitre);
        }
        blnRetirerArbitre = false;
        blnRetirerCombattantBlanc = false;
        blnRetirerCombattantRouge = false;
        blnArbitreEtaitSpectateur = false;
        blnArbitreEtaitAttente = false;
        blnCombattantBlancEtaitArbitre = false;
        blnCombattantRougeEtaitArbitre = false;
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // POUR TROUVER LISTE AILLEURS
    @RequestMapping(value="/ListeAilleur", method= RequestMethod.GET)
    public String ListeAilleur(Model model) {
        return "AIL"+lstComptesAilleurs.toString();
    }

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

    public void videListeExtract(String strBonneListe, Compte compte, int index, List<Compte> lstComptesAttenteVar, List<Compte> lstComptesSpectateurVar, List<Compte> lstComptesAilleurVar, List<Compte> lstComptesArbitreVar) {
        List<Compte> lstComptesAilleurRecu = lstComptesAilleurVar;
        List<Compte> lstComptesSpectateurRecu = lstComptesSpectateurVar;
        List<Compte> lstComptesAttenteRecu = lstComptesAttenteVar;
        List<Compte> lstComptesArbitreRecu = lstComptesArbitreVar;
        switch(strBonneListe){
            case "AILLEURS" :
                if(arbitre != null){
                    if(arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitSpectateur = false;
                    if(arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitAttente = false;
                    if(arbitre.getUsername().equals(compte.getUsername())) blnRetirerArbitre = true;
                }
                if(combattantBlanc != null){
                    if(combattantBlanc.getUsername().equals(compte.getUsername())) blnCombattantBlancEtaitArbitre = false;
                    if(combattantBlanc.getUsername().equals(compte.getUsername())) blnRetirerCombattantBlanc = true;
                }
                if (combattantRouge != null){
                    if(combattantRouge.getUsername().equals(compte.getUsername())) blnCombattantRougeEtaitArbitre = false;
                    if(combattantRouge.getUsername().equals(compte.getUsername())) blnRetirerCombattantRouge = true;
                }

                for(Compte c : lstComptesAttenteRecu){
                    if(c.getUsername().equals(compte.getUsername())){
                        index = lstComptesAttenteRecu.indexOf(c);
                    }

                }
                if(index != -1) lstComptesAttenteVar.remove(index);
                index = -1;
                for(Compte c : lstComptesSpectateurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesSpectateurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesSpectateurVar.remove(index);
                lstComptesAilleurVar.add(compte);
                break;
            case "ATTENTE":
                if(arbitre != null && arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitSpectateur = false;

                for(Compte c : lstComptesAilleurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesAilleurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesAilleurVar.remove(index);
                index = -1;
                for(Compte c : lstComptesSpectateurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesSpectateurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesSpectateurVar.remove(index);
                lstComptesAttenteVar.add(compte);
                break;
            case "SPECTATEUR":
                if(combattantBlanc != null && combattantBlanc.getUsername().equals(compte.getUsername())) blnRetirerCombattantBlanc = true;
                if(combattantRouge != null && combattantRouge.getUsername().equals(compte.getUsername())) blnRetirerCombattantRouge = true;
                if(arbitre != null && arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitAttente = false;
                for(Compte c : lstComptesAilleurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesAilleurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesAilleurVar.remove(index);
                index = -1;
                for(Compte c : lstComptesAttenteRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesAttenteRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesAttenteVar.remove(index);
                lstComptesSpectateurVar.add(compte);
                break;
            case "PEACE":
                if(combattantBlanc != null){

                    if(combattantBlanc.getUsername().equals(compte.getUsername())) blnRetirerCombattantBlanc = true;
                    if(combattantBlanc.getUsername().equals(compte.getUsername())) blnCombattantBlancEtaitArbitre = false;
                }
                if(combattantRouge != null){

                    if(combattantRouge.getUsername().equals(compte.getUsername())) blnRetirerCombattantRouge = true;
                    if(combattantRouge.getUsername().equals(compte.getUsername())) blnCombattantRougeEtaitArbitre = false;
                }
                if(arbitre != null){
                    if(arbitre.getUsername().equals(compte.getUsername())) blnRetirerArbitre = true;
                    if(arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitSpectateur = false;
                    if(arbitre.getUsername().equals(compte.getUsername())) blnArbitreEtaitAttente = false;
                }
                for(Compte c : lstComptesAilleurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesAilleurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesAilleurVar.remove(index);
                index = -1;
                for(Compte c : lstComptesArbitreRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesArbitreRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesArbitreVar.remove(index);
                index = -1;
                for(Compte c : lstComptesAttenteRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesAttenteRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesAttenteVar.remove(index);
                index = -1;
                for(Compte c : lstComptesSpectateurRecu){
                    if(c.getUsername().equals(compte.getUsername())) {
                        index = lstComptesSpectateurRecu.indexOf(c);
                    }
                }
                if(index != -1) lstComptesSpectateurVar.remove(index);
                break;
            default:
                if(strBonneListe.equals("ARBITRE")){
                    for(Compte c : lstComptesArbitreRecu){
                        if(c.getUsername().equals(compte.getUsername())) {
                            index = lstComptesArbitreRecu.indexOf(c);
                        }
                    }
                    if(index == -1) lstComptesArbitreVar.add(compte);

                }
                else{
                    for(Compte c : lstComptesArbitreRecu){
                        if(c.getUsername().equals(compte.getUsername())) {
                            index = lstComptesArbitreRecu.indexOf(c);
                        }
                    }
                    if(index != -1) lstComptesArbitreVar.remove(index);
                }
                break;
        }
    }
}