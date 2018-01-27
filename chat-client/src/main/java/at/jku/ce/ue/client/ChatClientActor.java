package at.jku.ce.ue.client;

import akka.actor.AbstractActor;
import at.jku.ce.ue.api.*;
import at.jku.ce.ue.data.Room;

import java.util.Set;

/**
 * The class representation of the client actor.
 */
public class ChatClientActor extends AbstractActor {


    private static Set<Room> staticRoomSet = null;
    private static Room curRoom;

    private Set<String> chat = null;
    private Set<Room> roomSet = null;

    @Override
    public AbstractActor.Receive createReceive()
    {
        return receiveBuilder()
                .match(AvailableChatServices.class, x -> getChats(x))
                .match(AvailableRooms.class, x -> getRooms(x))
                .match(ErrorOccurred.class, x -> errorShow(x))
                .match(SendMessage.class, x -> sendMsg(x))
                .match(RoomJoined.class, x -> roomJoin())
                .match(NewMessageAvailable.class, x -> gotNewMsg(x))
                .match(RoomLeft.class, x-> roomLeave())
                .build();
    }

    public Set<Room> getRoomSet() {return roomSet;}
    public static Set<Room> getStaticRoomSet() {return staticRoomSet;}
    public static Room getCurRoom() {return curRoom;}

    /**
     * Gets alls available rooms from the server.
     * @param rooms
     */
    private void getRooms(AvailableRooms rooms) {
        roomSet = rooms.getRooms();
        staticRoomSet = rooms.getRooms();
        for(Room x: roomSet)
            System.out.println(x.getName());
    }

    /**
     * Tells the server to leave a specific room.
     */
    private void roomLeave() {
        System.out.println("You want to be on your own. We can appreciate it. Come back soon.");
    }

    /**
     * Tells the server to join a specific room.
     */
    private void roomJoin() {
        System.out.println("You are no longer alone. Room joined successfully!");
    }

    /**
     * Prints a new message recieved from the server.
     * @param newMsg The message to be printed.
     */
    private void gotNewMsg(NewMessageAvailable newMsg) {
        System.out.println(newMsg.getName() + ": " + newMsg.getMessage());
    }

    /**
     * Sends a message to the server.
     * @param msg The message to be sent.
     */
    private void sendMsg(SendMessage msg) {
        new SendMessage(msg.getRoom(), msg.getMessage());
    }

    /**
     * Gets all available chat services from the server.
     * @param chats
     */
    private void getChats(AvailableChatServices chats) {
        this.chat = chats.getChatServices();
        for(String s: this.chat)
            System.out.println(s);
        }

    /**
     * Shows if an error occurred.
     * @param x
     */
    private void errorShow(ErrorOccurred x) {
        System.out.println(x.toString());
    }

}



