package cgg.informatique.jfl.webSocket;


import cgg.informatique.jfl.webSocket.configurations.MonStompSessionHandler;
import cgg.informatique.jfl.webSocket.configurations.WebSocketConfig;
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
	boolean blnCombat = false;
	@Autowired
	private CompteDao compteDao;

	@Autowired
	private GroupeDao groupeDao;
	@Autowired
	private CombatDao combatDao;

	String chiffreRecu = "";

	private List<Compte> lstComptesAilleurs = new ArrayList<>();
	private List<Compte> lstComptesSpectateur = new ArrayList<>();
	private List<Compte> lstComptesAttente = new ArrayList<>();
	private List<Compte> lstComptesArbitre = new ArrayList<>();

	private Compte combattantRouge = new Compte();
	private Compte combattantBlanc = new Compte();
	private Compte arbitre = new Compte();

	public static void main(String[] args) {
		SpringApplication.run(WebSocketApplication.class, args);
	}

	public void TrouverAdversaire(){
		List<Groupe> lstGroupe = groupeDao.findAll();
		Integer idGroupRouge = -1;
		for(Groupe unGroupe: lstGroupe){
			if(unGroupe.getGroupe().equals(combattantRouge.getGroupe())) idGroupRouge = unGroupe.getId();
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
					if(unGroupe.getGroupe().equals(veutCombattre.getGroupe()))idGroupeCombattantActuelle = unGroupe.getId();
				}
				if(idGroupeCombattantActuelle-idGroupRouge < trouverPlusProche){
					idBlanc = i;
					trouverPlusProche = idGroupeCombattantActuelle-idGroupRouge;
				}
			}
		}
		combattantBlanc = lstComptesAttente.get(idBlanc);
	}
	@Override
	public void run(String... args) throws Exception {
		// Ce laboratoire est basé sur les exemples suivants:
		// https://spring.io/guides/gs/messaging-stomp-websocket/
		// https://www.sitepoint.com/implementing-spring-websocket-server-and-client/
		// https://www.baeldung.com/websockets-api-java-spring-client
		// https://www.baeldung.com/spring-security-websockets
		// https://www.html5rocks.com/en/tutorials/getusermedia/intro/
		// https://davidwalsh.name/demo/camera.php
		// https://spring.io/blog/2015/06/08/cors-support-in-spring-framework
		// https://developer.mozilla.org/fr/docs/Web/API/MediaDevices/getUserMedia
		//
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
		Message		unMessage = new Message("s1@dojo", "ATTENTE", creation, "", "POSITION");
		session.send("/app/listePosition",

				unMessage);unMessage = new Message("v1@dojo", "ATTENTE", creation, "", "POSITION");
		session.send("/app/listePosition", unMessage);

		unMessage = new Message("s2@dojo", "ARBITRE", creation, "", "POSITION");
		session.send("/app/listePosition", unMessage);
		// MESSAGES
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
					videListe(rep.getTexte().trim().toUpperCase(),compte);

				}
				catch(Exception e){
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		});

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
		while (true) {
			//À toutes les 5 secondes, un message est transmis par le serveur

			Thread.sleep(4000);
			if(!blnCombat && lstComptesAttente.size()>=2 && lstComptesArbitre.size()>=1){
				blnCombat = true;
				Random rand = new Random();
				combattantRouge = lstComptesAttente.get(rand.nextInt(lstComptesAttente.size()));
				arbitre = lstComptesArbitre.get(rand.nextInt(lstComptesArbitre.size()));
				TrouverAdversaire();
				String debutCombat = combattantRouge.fixString()+"-A-"+combattantBlanc.fixString()+"-A-"+arbitre.fixString();
				System.out.println(debutCombat);

				unMessage = new Message("DEBUTCOMBAT" ,debutCombat,  creation, "" ,"POSITION");
				session.send("/app/debutCombat", unMessage);
				rand = new Random();
				int randomBlanc = rand.nextInt(3)+1;
				int randomRouge = rand.nextInt(3)+1;
				chiffreRecu = randomBlanc + "|" + randomRouge;
				Thread.sleep(2000);
				unMessage = new Message("ENVOYERCHIFFRE" ,debutCombat,  creation, chiffreRecu,"POSITION");
				session.send("/app/envoyerChiffre", unMessage);
				entrerUnCombat(randomBlanc,randomRouge,combattantBlanc,arbitre,combattantRouge);
				Thread.sleep(4000);
				unMessage = new Message("FINCOMBAT" ,"",  creation, "" ,"");
				session.send("/app/finCombat", unMessage);
			}


		}
	}
	public void videListe(String strBonneListe, Compte compte){
		int index = -1;
		videListeExtract(strBonneListe, compte, index, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
	}
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

	public void entrerUnCombat(int chiffreBlanc, int chiffreRouge, Compte cptBlanc, Compte cptArbitre, Compte cptRouge) {
		int ceintureRouge = -1;
		int ceintureBlanc = -1;
		int diffCeinture = 0;
		List<Groupe> lstGroupe = groupeDao.findAll();
		for (Groupe g : lstGroupe) {
			if (g.getGroupe().equals(cptRouge.getGroupe())) ceintureRouge = g.getId();
			else if (g.getGroupe().equals(cptBlanc.getGroupe())) ceintureBlanc = g.getId();
		}
		diffCeinture = ceintureBlanc - ceintureRouge;
		int[] tabPts = pointsParDifference(Math.abs(diffCeinture));
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