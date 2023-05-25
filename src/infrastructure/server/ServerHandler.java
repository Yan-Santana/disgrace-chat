package src.infrastructure.server;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import src.interfaces.InputEventHandler;
import src.interfaces.PointHandler;

public class ServerHandler implements PointHandler<ConnectedClient> {
    private final ChatServer server;

    public ServerHandler(ChatServer server) {
        this.server = server;
    }

    public InputEventHandler<ConnectedClient> handleEventOnReceive() {
        return new InputEventHandler<ConnectedClient>() {
            @Override
            public void execute(ConnectedClient connectedClient, String event, JSONObject data) throws IOException {
                switch (event) {
                    case "join":
                        joinEvent(connectedClient, data);
                        break;

                    case "send_message":
                        sendMessageEvent(connectedClient, data);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void broadcastEvent(String event, JSONObject data, ConnectedClient excludedClient) throws IOException {
        for (ConnectedClient clientItem: this.server.getConnectedClients()) {
            if (clientItem != excludedClient && clientItem.isIdentified()) {
                clientItem.emitEvent(event, data);
            }
        }
    }

    private void joinEvent(ConnectedClient client, JSONObject data) throws IOException {
        String username = data.getString("name");
        int avatarId = data.getInt("avatarId");

        client.setName(username);
        client.setAvatarId(avatarId);
        client.setAsIdentified();

        JSONObject eventDataObject = new JSONObject();
        JSONObject serverInfoObject = new JSONObject();

        Vector<ConnectedClient> connectedClients = this.server.getConnectedClients();
        JSONArray clientsArray = new JSONArray();

        for (ConnectedClient clientItem: connectedClients) {
            if (clientItem != client && clientItem.isIdentified()) {
                JSONObject clientObject = new JSONObject();
                clientObject.put("name", clientItem.getName());
                clientObject.put("avatarId", clientItem.getAvatarId());
                clientObject.put("channelId", clientItem.getChannelId());

                clientsArray.put(clientObject);
            }
        }
        
        serverInfoObject.put("connectedClients", clientsArray);
        serverInfoObject.put("name", this.server.getName());
        
        eventDataObject.put("channelId", client.getChannelId());
        eventDataObject.put("server", serverInfoObject);

        client.emitEvent("joined", eventDataObject);
        this.emitClientJoinedEvent(client);
    }

    private void sendMessageEvent(ConnectedClient client, JSONObject data) throws IOException {
        String message = data.getString("message");
        int channelId = data.getInt("channelId");

        JSONObject dataObject = new JSONObject();

        dataObject.put("channelId", channelId);
        dataObject.put("message", message);

        this.broadcastEvent("received_message", dataObject, client);
    }

    private void emitClientJoinedEvent(ConnectedClient client) throws IOException {
        JSONObject dataObject = new JSONObject();
        dataObject.put("name", client.getName());
        dataObject.put("avatarId", client.getAvatarId());
        dataObject.put("channelId", client.getChannelId());

        this.broadcastEvent("client_joined", dataObject, client);
    }
}
