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

	private List<CompteSimple> lstComptesAilleurs = new ArrayList<>();
	private List<CompteSimple> lstComptesSpectateur = new ArrayList<>();
	private List<CompteSimple> lstComptesAttente = new ArrayList<>();
	private List<CompteSimple> lstComptesArbitre = new ArrayList<>();

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
					CompteSimple compteSimple = null;
					compte = compteDao.findById(rep.getDe()).get();
					compteSimple = new CompteSimple(compte.getUsername(),compte.getAvatar().getAvatar().replaceAll("data:image/jpeg;base64,",""));
					videListe(rep.getTexte().trim().toUpperCase(),compteSimple);

				}
				catch(Exception e){
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		});

		while (true) {
			//À toutes les 5 secondes, un message est transmis par le serveur

			/*Thread.sleep(5000);
			Long creation = System.currentTimeMillis();

			System.out.println(lstComptesAilleurs.toString());
			System.out.println(lstComptesArbitre.toString());
			System.out.println(lstComptesAttente.toString());
			System.out.println(lstComptesSpectateur.toString());
			Message unMessage = new Message("AIL", lstComptesAilleurs.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);

			unMessage = new Message("ARB", lstComptesArbitre.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);

			unMessage = new Message("ATT", lstComptesAttente.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);

			unMessage = new Message("SPE", lstComptesSpectateur.toString(), creation, "", "POSITION");
			session.send("/app/message/listePosition", unMessage);*/

		}
	}
	public void videListe(String strBonneListe, CompteSimple compteSimple){
		int index = -1;
		videListeExtract(strBonneListe, compteSimple, index, lstComptesAttente, lstComptesSpectateur, lstComptesAilleurs, lstComptesArbitre);
	}

	public static void videListeExtract(String strBonneListe, CompteSimple compteSimple, int index, List<CompteSimple> lstComptesAttente, List<CompteSimple> lstComptesSpectateur, List<CompteSimple> lstComptesAilleurs, List<CompteSimple> lstComptesArbitre) {
		switch(strBonneListe){
			case "AILLEURS" :
				for(CompteSimple c : lstComptesAttente){
					if(c.getUsername().equals(compteSimple.getUsername())){
						index = lstComptesAttente.indexOf(c);
					}

				}
				if(index != -1) lstComptesAttente.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesSpectateur){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesSpectateur.indexOf(c);
					}
				}
				if(index != -1) lstComptesSpectateur.remove(index);
				lstComptesAilleurs.add(compteSimple);
				break;
			case "ATTENTE":
				for(CompteSimple c : lstComptesAilleurs){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesAilleurs.indexOf(c);
					}
				}
				if(index != -1) lstComptesAilleurs.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesSpectateur){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesSpectateur.indexOf(c);
					}
				}
				if(index != -1) lstComptesSpectateur.remove(index);
				lstComptesAttente.add(compteSimple);
				break;
			case "SPECTATEUR":
				for(CompteSimple c : lstComptesAilleurs){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesAilleurs.indexOf(c);
					}
				}
				if(index != -1) lstComptesAilleurs.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesAttente){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesAttente.indexOf(c);
					}
				}
				if(index != -1) lstComptesAttente.remove(index);
				lstComptesSpectateur.add(compteSimple);
				break;
			case "PEACE":
				for(CompteSimple c : lstComptesAilleurs){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesAilleurs.indexOf(c);
					}
				}
				if(index != -1) lstComptesAilleurs.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesArbitre){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesArbitre.indexOf(c);
					}
				}
				if(index != -1) lstComptesArbitre.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesAttente){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesAttente.indexOf(c);
					}
				}
				if(index != -1) lstComptesAttente.remove(index);
				index = -1;
				for(CompteSimple c : lstComptesSpectateur){
					if(c.getUsername().equals(compteSimple.getUsername())) {
						index = lstComptesSpectateur.indexOf(c);
					}
				}
				if(index != -1) lstComptesSpectateur.remove(index);
				break;
			default:
				if(strBonneListe.equals("ARBITRE")){
					for(CompteSimple c : lstComptesArbitre){
						if(c.getUsername().equals(compteSimple.getUsername())) {
							index = lstComptesArbitre.indexOf(c);
						}
					}
					if(index != -1) lstComptesArbitre.remove(index);
					lstComptesArbitre.add(compteSimple);
				}
				else{
					for(CompteSimple c : lstComptesArbitre){
						if(c.getUsername().equals(compteSimple.getUsername())) {
							index = lstComptesArbitre.indexOf(c);
						}
					}
					if(index != -1) lstComptesArbitre.remove(index);
				}
				break;
		}
	}
}