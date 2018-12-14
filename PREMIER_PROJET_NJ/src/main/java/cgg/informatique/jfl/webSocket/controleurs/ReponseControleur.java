package cgg.informatique.jfl.webSocket.controleurs;


import cgg.informatique.jfl.webSocket.Message;
import cgg.informatique.jfl.webSocket.Reponse;
import cgg.informatique.jfl.webSocket.dao.AvatarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReponseControleur {

    @Autowired
    private AvatarDao avatarDao;

    static long id = 1;


    // POUR MESSAGES
    @CrossOrigin()
    @MessageMapping("/message/publique")
    @SendTo("/position/reponse")
    public Reponse reponse(Message message) throws Exception{
            System.err.println(message.toString());
            Reponse rep = new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
            return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/message/prive")
    @SendTo("/position/reponseprive")
    public Reponse reponsePrive(Message message) throws Exception{
        System.err.println(message.toString());
           return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    // POUR DEMANDER QUI EST OÙ
    @CrossOrigin()
    @MessageMapping("/position_client/spectateur")
    @SendTo("/position/quiestspectateur")
    public Reponse positionSpec(Message message) throws Exception {
        //System.err.println(message.toString());
        Reponse rep = new Reponse(id++,message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());

    }

    @CrossOrigin()
    @MessageMapping("/position_client/combattant")
    @SendTo("/position/quiestcombattant")
    public Reponse positionComb(Message message) throws Exception {
        //System.err.println(message.toString());
        Reponse rep = new Reponse(id++,message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());

    }

    @CrossOrigin()
    @MessageMapping("/position_client/arbitre")
    @SendTo("/position/quiestarbitre")
    public Reponse positionArbitre(Message message) throws Exception {
        //System.err.println(message.toString());
        Reponse rep = new Reponse(id++,message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());

    }

    // POUR ENVOYER QUI EST OÙ
    @CrossOrigin()
    @MessageMapping("/spectateur_client")
    @SendTo("/position/spectateur")
    public Reponse spectateur(Message message) throws Exception {
        //System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/combattant_client")
    @SendTo("/position/combattant")
    public Reponse combatant(Message message) throws Exception {
        //System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/debutCombat")
    @SendTo("/position/debutCombat")
    public Reponse debutCombat(Message message) throws Exception {
        //System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/finCombat")
    @SendTo("/position/finCombat")
    public Reponse finCombat(Message message) throws Exception {
        //System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/gradation/ceinture")
    @SendTo("/position/ceinture")
    public Reponse gradationCeinture(Message message) throws Exception {
        System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }

    @CrossOrigin()
    @MessageMapping("/gradation/ancien")
    @SendTo("/position/ancien")
    public Reponse gradationAncien(Message message) throws Exception {
        System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }
    @CrossOrigin()
    @MessageMapping("/gradation/sensei")
    @SendTo("/position/sensei")
    public Reponse gradationSensei(Message message) throws Exception {
        System.err.println(message.toString());
        return new Reponse(id++, message.getDe(), message.getTexte(), message.getCreation(), message.getAvatar());
    }
    // POUR TROUVER AVATAR
    @RequestMapping(value="/TrouverAvatar/{nom}", method= RequestMethod.GET)
    public String avatar(@PathVariable String nom) {
        return avatarDao.findById(nom).get().getAvatar();
    }
}


