package at.jku.ce.ue.service;

import akka.actor.AbstractActor;
import at.jku.ce.ue.api.*;
import at.jku.ce.ue.data.Room;

import java.util.*;

public class SampleActor extends AbstractActor {

	private HashMap<String, Room> roomMap = new HashMap<String,Room>();
	private Set<Room> roomSet = new HashSet<>();
	private ArrayList<AbstractActor> actorList = new ArrayList<>();
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

	private void getRooms() {
		roomSet.add(new Room("Zhaitan"));
		roomSet.add(new Room("Grenth"));
		roomSet.add(new Room("Balthazar"));
		rooms = new AvailableRooms("DeathAndWar", roomSet);
		getSender().tell(rooms,this.self());
	}

	private void getStatus() {
		System.out.print("7");
		this.sender().tell(new Heartbeat(), this.self());
		System.out.println("a");
	}

	private void getServices() {


	}

	private void roomJoin(JoinRoom x) {
		roomMap.put(x.getName(),x.getRoom());
		getSender().tell(new RoomJoined(),this.self());
	}

	private void roomLeave(LeaveRoom x) {
		for(Map.Entry<String, Room> entry : roomMap.entrySet()) {
			String key = entry.getKey();
			Room room = entry.getValue();

			if(room.getName().equals(x.getRoom().getName()) && key.equals(getSender().toString())) roomMap.remove(entry);

		}
		getSender().tell(new RoomLeft(x.getRoom()) ,this.self());
	}

    private void sendMsg(SendMessage x) {
	    for(Map.Entry<String, Room> entry : roomMap.entrySet()) {
            Room temp = entry.getValue();
            if(temp.getName().equals(x.getRoom().getName())) {
                //TODO
                getSender().tell(new NewMessageAvailable(x.getRoom(), getSender().toString(), x.getMessage() ), this.self());
            }
        }
    }

	private void regService(RegisterChatService rcs){
	    System.out.println(rcs.toString());
	}
}
