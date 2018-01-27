package at.jku.ce.ue.service;

import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import at.jku.ce.ue.api.GetAvailableChatServices;
import at.jku.ce.ue.api.RegisterChatService;
import at.jku.ce.ue.helper.CEHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ChatService extends AbstractActor{

	public static void main(String[] args) {

		Config cfg = ConfigFactory.load("application.conf");
		ActorSystem acthors = ActorSystem.create("WarEternal");
		CEHelper ceTemp = new CEHelper(acthors, cfg);

		ActorRef refTemp = acthors.actorOf(Props.create(ServiceActor.class), "ArchEnemy");
		System.out.println(ceTemp.getActorPath(refTemp));

		ActorSelection roomClient = acthors.actorSelection(ceTemp.getChatServiceRegistry());
		roomClient.tell(new RegisterChatService(), refTemp);
	}

	private void getServices() {
		Config cfg = ConfigFactory.load("application.conf");
		ActorSystem acthors = ActorSystem.create("WarEternal");
		CEHelper ceTemp = new CEHelper(acthors, cfg);
		getSender().tell(ceTemp.getChatServiceRegistry(), this.self());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(GetAvailableChatServices.class, x -> getServices()).build();
	}
}
