package cgg.informatique.jfl.webSocket;


import cgg.informatique.jfl.webSocket.configurations.MonStompSessionHandler;
import cgg.informatique.jfl.webSocket.dao.*;
import cgg.informatique.jfl.webSocket.entites.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class WebSocketApplication implements CommandLineRunner {
	@Autowired
	private CompteDao compteDao;

	@Autowired
	private GroupeDao groupeDao;
	@Autowired
	private CombatDao combatDao;


    private boolean blnCombat = false;


	private List<Compte> lstComptesAilleurs = new ArrayList<>();
	private List<Compte> lstComptesSpectateur = new ArrayList<>();
	private List<Compte> lstComptesAttente = new ArrayList<>();
	private List<Compte> lstComptesArbitre = new ArrayList<>();

	private Compte combattantRouge = null;
	private Compte combattantBlanc = null;
	private Compte arbitre = null;

	public static void main(String[] args) {
		SpringApplication.run(WebSocketApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
		List<Transport> transports = new ArrayList<>();


		transports.add(new WebSocketTransport(simpleWebSocketClient));
		SockJsClient sockJsClient = new SockJsClient(transports);

		//Créer un client Stomp qui gère les messages.
		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
		MappingJackson2MessageConverter conversionJson = new MappingJackson2MessageConverter();
		stompClient.setMessageConverter(conversionJson);
		stompClient.setInboundMessageSizeLimit(32 * 1024);

		//Se connecter à un websocket
		String url = "ws://localhost:8093/webSocket";
		StompSessionHandler sessionHandler = new MonStompSessionHandler();
		StompSession session = stompClient.connect(url, sessionHandler).get();
		Long creation = System.currentTimeMillis();

		// Réception du message du changement de groupe
		session.subscribe("/sujet/radiogroup", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Reponse.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				try{
					ObjectMapper objMapper = new ObjectMapper();
					Reponse rep = objMapper.readValue(payload.toString(),Reponse.class);
					Compte compte = new Compte();
					compte = compteDao.findById(rep.getDe()).get();
                    videListeExtract(rep.getTexte().trim().toUpperCase(), compte, -1, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
				}
				catch(Exception e){
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		});

		// Réception du message de la fin d'un combat
		session.subscribe("/sujet/finCombat", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Reponse.class;
			}
			@Override
			public void handleFrame(StompHeaders headers,Object payload) {
				combattantRouge = null;
				combattantBlanc = null;
				arbitre = null;
				blnCombat = false;
			}
		});

		// Boucle continue pour les combats
		while (true) {
			Thread.sleep(4000);                                                                                                 //Attente 4s
			if(!blnCombat && lstComptesAttente.size()>=2 && lstComptesArbitre.size()>=1){
				Random rand = new Random();
				combattantRouge = lstComptesAttente.get(rand.nextInt(lstComptesAttente.size()));
				TrouverAdversaire();
				TrouverArbitre();
				if(combattantRouge != null && combattantBlanc != null && arbitre != null){
					blnCombat = true;
				    // Message pour le début d'un combat
                    String debutCombat = combattantRouge.fixString()+"-A-"+combattantBlanc.fixString()+"-A-"+arbitre.fixString();
                    Message unMessage = new Message("DEBUTCOMBAT" ,debutCombat,  creation, "" ,"POSITION");
                    session.send("/app/debutCombat", unMessage);

                    // Message pour indiquer les résultats
                    rand = new Random();
                    int randomBlanc = rand.nextInt(3)+1;
                    int randomRouge = rand.nextInt(3)+1;
                    String chiffreRecu = randomBlanc + "-A-" + randomRouge;

                    Thread.sleep(2000);                                                                                         //Attente 2s

                    unMessage = new Message("ENVOYERCHIFFRE" ,debutCombat,  creation, chiffreRecu,"POSITION");
                    session.send("/app/envoyerChiffre", unMessage);
                    // Entrer le résultat du combat à la base de donnée
                    entrerUnCombat(randomBlanc,randomRouge);

                    Thread.sleep(4000);                                                                                          //Attente 4s

                    // Message pour indiquer la fin du combat
                    unMessage = new Message("FINCOMBAT" ,"",  creation, "" ,"");
                    session.send("/app/finCombat", unMessage);
                }
			}
		}
	}

	// Fonction pour trouver l'adversaire lors d'un combat
    public void TrouverAdversaire(){
        List<Groupe> lstGroupe = groupeDao.findAll();
        Integer idGroupRouge = -1;
        for(Groupe unGroupe: lstGroupe){
            if(unGroupe.getGroupe().equals(combattantRouge.getGroupe().getGroupe())) idGroupRouge = unGroupe.getId();
        }
        boolean blnTrouver = false;
        int idBlanc = -1;
        int trouverPlusProche = Integer.MAX_VALUE;
        for( int i = 0; i < lstComptesAttente.size() && !blnTrouver;i++){
            Compte veutCombattre = lstComptesAttente.get(i);
            if(veutCombattre != combattantRouge && veutCombattre.getGroupe().equals(combattantRouge.getGroupe())){
                combattantBlanc = veutCombattre;
                blnTrouver = true;
            }
            else if(veutCombattre != combattantRouge){
                Integer idGroupeCombattantActuelle = 0;
                for(Groupe unGroupe : lstGroupe){
                    if(unGroupe.getGroupe().equals(veutCombattre.getGroupe().getGroupe()))idGroupeCombattantActuelle = unGroupe.getId();
                }
                if(idGroupeCombattantActuelle-idGroupRouge < trouverPlusProche){
                    idBlanc = i;
                    trouverPlusProche = idGroupeCombattantActuelle-idGroupRouge;
                }
            }
        }
        combattantBlanc = lstComptesAttente.get(idBlanc);
    }

    // Fonction pour trouver l'arbitre lors d'un combat
    public void TrouverArbitre(){
        List<Compte> lstCompteArbtireVerifier = new ArrayList<>();
        for(Compte c : lstComptesArbitre){
            if(!c.getUsername().equals(combattantBlanc.getUsername()) && !c.getUsername().equals(combattantRouge.getUsername())) lstCompteArbtireVerifier.add(c);
        }
        Random rand = new Random();
        if(lstCompteArbtireVerifier.size()>0) arbitre = lstComptesArbitre.get(rand.nextInt(lstCompteArbtireVerifier.size()));
        else arbitre = null;
    }

    // Trouver qui gagne le combat
	public Integer trouverVainqueur(int chiffreBlanc, int chiffreRouge){
		if(chiffreBlanc == chiffreRouge){
			return 0;
		}
		else{
			switch(chiffreBlanc){
				case 1:
					if(chiffreRouge == 2) {
						return -1;
					}
					else  {
						return 1;
					}
				case 2:
					if(chiffreRouge == 3){
						return -1;
					}
					else {
						return 1;
					}
				case 3:
					if(chiffreRouge == 1) {
						return -1;
					}
					else{
						return 1;
					}
			}
		}
		return 0;
	}

	// Entre un combat dans la base de donnée
	public void entrerUnCombat(int chiffreBlanc, int chiffreRouge) {
		int intVainqueur = trouverVainqueur(chiffreBlanc, chiffreRouge);
		Combat unCombat = new Combat();
		switch (intVainqueur) {
			//Victoire Rouge
			case -1:
				unCombat = new Combat(1,System.currentTimeMillis(),0,10,arbitre,combattantBlanc,combattantBlanc.getGroupe(),combattantRouge.getGroupe(),combattantRouge);
				break;
			//Match Nul
			case 0:
				unCombat = new Combat(1,System.currentTimeMillis(),5,5,arbitre,combattantBlanc,combattantBlanc.getGroupe(),combattantRouge.getGroupe(),combattantRouge);
				break;
			// Victoire blanc
			case 1:
				unCombat = new Combat(1,System.currentTimeMillis(),10,0,arbitre,combattantBlanc,combattantBlanc.getGroupe(),combattantRouge.getGroupe(),combattantRouge);
				break;
		}
		combatDao.save(unCombat);
	}
	public static void videListeExtract(String strBonneListe, Compte compte, int index, List<Compte> lstAttente, List<Compte> lstSpectateur, List<Compte> lstAilleur, List<Compte> lstArbitre) {
		List<Compte> lstComptesAttenteRecu = lstAttente;
		List<Compte> lstComptesSpectateurRecu = lstSpectateur;
		List<Compte> lstComptesAilleurRecu = lstAilleur;
		List<Compte> lstComptesArbitreRecu = lstArbitre;
		switch(strBonneListe){
			case "AILLEURS" :
				for(Compte c : lstComptesAttenteRecu){
					if(c.getUsername().equals(compte.getUsername())){
						index = lstComptesAttenteRecu.indexOf(c);
					}

				}
				if(index != -1) lstAttente.remove(index);
				index = -1;
				for(Compte c : lstComptesSpectateurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesSpectateurRecu.indexOf(c);
					}
				}
				if(index != -1) lstSpectateur.remove(index);
				lstAilleur.add(compte);
				break;
			case "ATTENTE":
				for(Compte c : lstComptesAilleurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesAilleurRecu.indexOf(c);
					}
				}
				if(index != -1) lstAilleur.remove(index);
				index = -1;
				for(Compte c : lstComptesSpectateurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesSpectateurRecu.indexOf(c);
					}
				}
				if(index != -1) lstSpectateur.remove(index);
				lstAttente.add(compte);
				break;
			case "SPECTATEUR":
				for(Compte c : lstComptesAilleurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesAilleurRecu.indexOf(c);
					}
				}
				if(index != -1) lstAilleur.remove(index);
				index = -1;
				for(Compte c : lstComptesAttenteRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesAttenteRecu.indexOf(c);
					}
				}
				if(index != -1) lstAttente.remove(index);
				lstSpectateur.add(compte);
				break;
			case "PEACE":
				for(Compte c : lstComptesAilleurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesAilleurRecu.indexOf(c);
					}
				}
				if(index != -1) lstAilleur.remove(index);
				index = -1;
				for(Compte c : lstComptesArbitreRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesArbitreRecu.indexOf(c);
					}
				}
				if(index != -1) lstArbitre.remove(index);
				index = -1;
				for(Compte c : lstComptesAttenteRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesAttenteRecu.indexOf(c);
					}
				}
				if(index != -1) lstAttente.remove(index);
				index = -1;
				for(Compte c : lstComptesSpectateurRecu){
					if(c.getUsername().equals(compte.getUsername())) {
						index = lstComptesSpectateurRecu.indexOf(c);
					}
				}
				if(index != -1) lstSpectateur.remove(index);
				break;
			default:
				if(strBonneListe.equals("ARBITRE")){
					for(Compte c : lstComptesArbitreRecu){
						if(c.getUsername().equals(compte.getUsername())) {
							index = lstComptesArbitreRecu.indexOf(c);
						}
					}
					if(index != -1) lstArbitre.remove(index);
					lstArbitre.add(compte);
				}
				else{
					for(Compte c : lstComptesArbitreRecu){
						if(c.getUsername().equals(compte.getUsername())) {
							index = lstComptesArbitreRecu.indexOf(c);
						}
					}
					if(index != -1) lstArbitre.remove(index);
				}
				break;
		}
	}
}