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

	@Autowired
	private CompteDao compteDao;

	private List<Compte> lstComptesAilleurs = new ArrayList<>();
	private List<Compte> lstComptesSpectateur = new ArrayList<>();
	private List<Compte> lstComptesAttente = new ArrayList<>();
	private List<Compte> lstComptesArbitre = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(WebSocketApplication.class, args);
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

		while (true) {
			//À toutes les 5 secondes, un message est transmis par le serveur

			Thread.sleep(2000);
			Long creation = System.currentTimeMillis();
/*
			System.out.println(lstComptesAilleurs.toString());
			System.out.println(lstComptesArbitre.toString());
			System.out.println(lstComptesAttente.toString());
			System.out.println(lstComptesSpectateur.toString());*/
			/*Message		unMessage = new Message("s1@dojo", "PEACE", creation, "", "POSITION");
			session.send("/app/listePosition", unMessage);/*
			*//* unMessage = new Message("v1@dojo", "SPECTATEUR", creation, "", "POSITION");
			session.send("/app/listePosition", unMessage);/*
			Thread.sleep(2000);
			unMessage = new Message("s1@dojo", "ATTENTE", creation, "", "POSITION");
			session.send("/app/listePosition", unMessage);/*
			  unMessage = new Message("v1@dojo", "PEACE", creation, "", "POSITION");
			session.send("/app/listePosition", unMessage);

			unMessage = new Message("ARB", lstComptesArbitre.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);

			unMessage = new Message("ATT", lstComptesAttente.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);

			unMessage = new Message("SPE", lstComptesSpectateur.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);*/

		}
	}
	public void videListe(String strBonneListe, Compte compte){
		int index = -1;
		videListeExtract(strBonneListe, compte, index, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
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