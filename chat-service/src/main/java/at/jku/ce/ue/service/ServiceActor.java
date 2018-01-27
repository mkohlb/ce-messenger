package at.jku.ce.ue.service;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import at.jku.ce.ue.api.*;
import at.jku.ce.ue.data.Room;
import at.jku.ce.ue.helper.CEHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;

/**
 * Class representation of the service actor.
 */
public class ServiceActor extends AbstractActor {

	private HashMap<Room, String> roomMap = new HashMap<>();
	private Set<Room> roomSet = new HashSet<>();
	private AvailableRooms rooms;


	@Override
	public Receive createReceive() {

		return receiveBuilder()
				.match(GetStatus.class, event -> getStatus())
				.match(RegisterChatService.class, rcs -> regService(rcs))
				.match(GetAvailableChatServices.class, x -> getServices())
				.match(GetAvailableRooms.class, x -> getRooms())
				.match(JoinRoom.class,  x -> roomJoin(x))
				.match(LeaveRoom.class,  x -> roomLeave(x))
				.match(SendMessage.class, x -> sendMsg(x))
				.build();

	}

	/**
	 * Tells the registry all available rooms.
	 */
	private void getRooms() {
		roomSet.add(new Room("Zhaitan"));
		roomSet.add(new Room("Grenth"));
		roomSet.add(new Room("Balthazar"));
		rooms = new AvailableRooms("DeathAndWar", roomSet);
		getSender().tell(rooms,this.self());
	}

	/**
	 * Sends the heartbeat message to a client to see if it is still available.
	 */
	private void getStatus() {
		this.sender().tell(new Heartbeat(), this.self());
	}

	/**
	 * Tells the registry all available services.
	 */
	private void getServices() {
		Config cfg = ConfigFactory.load("application.conf");
		ActorSystem acthors = ActorSystem.create("WarEternal");
		CEHelper ceTemp = new CEHelper(acthors, cfg);
		getSender().tell(ceTemp.getChatServiceRegistry(), this.self());
	}

	/**
	 * Method for a client to join a specific room.
	 * @param x The Room ro join.
	 */
	private void roomJoin(JoinRoom x) {
		roomMap.put(x.getRoom(), x.getName());
		getSender().tell(new RoomJoined(),this.self());
	}

	/**
	 * The method to leave a room.
	 * @param x The room to leave.
	 */
	private void roomLeave(LeaveRoom x) {
		for(Map.Entry<Room, String> temp : roomMap.entrySet()) {
			String key= temp.getValue();
			Room room = temp.getKey();

			if(room.getName().equals(x.getRoom().getName()) && key.equals(getSender().toString())) roomMap.remove(temp);
		}
		getSender().tell(new RoomLeft(x.getRoom()) ,this.self());
	}

	/**
	 * The method to send a message to all users in a room.
	 * @param x The message to be sent.
	 */
    private void sendMsg(SendMessage x) {
	    for(Map.Entry<Room, String> entry : roomMap.entrySet()) {
            String temp = entry.getValue();
            if(temp.equals(x.getRoom().getName())) {
                //TODO
                getSender().tell(new NewMessageAvailable(x.getRoom(), getSender().toString(), x.getMessage() ), this.self());
            }
        }
    }

	/**
	 * Method to register the chat service.
	 * @param rcs The service to be registered.
	 */
	private void regService(RegisterChatService rcs){
	    System.out.println(rcs.toString());
	}
}
