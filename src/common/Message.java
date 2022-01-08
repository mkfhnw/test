package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Message {

    // Fields
    private final Addressor sender;
    private final Addressor recipient;
    private String token;
    private final ArrayList<String> messageParts;
    private final ArrayList<String> dataParts;
    private final MessageType messageType;
    private final String messageString;

    // Constructor used by the client to send a message
    public Message(String sender, String recipient, String token, String command, ArrayList<String> data) {
        this.sender = Addressor.valueOf(sender.toUpperCase());
        this.recipient = Addressor.valueOf(recipient.toUpperCase());
        this.token = token;
        this.messageType = MessageType.valueOf(command.toUpperCase());
        this.messageString = this.buildMessageString(this.messageType, this.token, data);
        this.messageParts = new ArrayList<>(Arrays.asList(this.messageString.split("\\|")));
        this.dataParts = this.parseDataParts(data);
    }
    
    // Constructor used by the client to send login, create login and ping message
    public Message(String sender, String recipient, String command, ArrayList<String> data) {
        this.sender = Addressor.valueOf(sender.toUpperCase());
        this.recipient = Addressor.valueOf(recipient.toUpperCase());
        this.messageType = MessageType.valueOf(command.toUpperCase());
        this.token = null;
        this.messageString = this.buildMessageString(this.messageType, this.token, data);
        this.messageParts = new ArrayList<>(Arrays.asList(this.messageString.split("\\|")));
        this.dataParts = this.parseDataParts(data);
    }

    // Constructor used to parse message from a messageString
    public Message(String messageString) {

        // Check if message contains the | sign and split parts by it if so
        String[] stringParts;
        if(messageString.contains("|")) { stringParts = messageString.split("\\|"); }
        else { stringParts = new String[1]; stringParts[0] = messageString; }

        this.messageString = messageString;
        this.messageType = MessageType.valueOf(stringParts[0].toUpperCase());
        this.messageParts = new ArrayList<>(Arrays.asList(stringParts));

        // Parse token
        this.parseToken(stringParts);

        // Parse data parts
        if(this.token != null) {
            this.dataParts = new ArrayList<>(this.messageParts.subList(2, (this.messageParts.size())));
        } else {
            if(this.messageParts.size() > 1) {
                this.dataParts = new ArrayList<>(this.messageParts.subList(1, (this.messageParts.size())));
            } else {
                this.dataParts = new ArrayList<>();
            }

        }

        // Parse sender & Recipient
        if (this.messageType == MessageType.RESULT) {
            this.sender = Addressor.SERVER;
            this.recipient = Addressor.CLIENT;
        } else {
            this.sender = Addressor.CLIENT;
            this.recipient = Addressor.SERVER;
        }
    }

    // Constructor only used by the server to create a message
    public Message(ArrayList<String> dataParts) {
        this.sender = Addressor.SERVER;
        this.recipient = Addressor.CLIENT;
        this.token = null;
        this.messageType = MessageType.RESULT;
        this.messageString = this.buildMessageString(this.messageType, this.token, dataParts);
        String[] stringParts = this.messageString.split("\\|");
        this.messageParts = new ArrayList<>(Arrays.asList(stringParts));
        this.dataParts = this.parseDataParts(dataParts);

    }

    // DEPRECATED
    // Constructor used by the server to receive a message
    public Message(String sender, String recipient, String token, String messageString) {
        this.sender = Addressor.valueOf(sender.toUpperCase());
        this.recipient = Addressor.valueOf(recipient.toUpperCase());
        this.token = token;
        this.messageString = messageString;
        this.messageParts = new ArrayList<>(Arrays.asList(this.messageString.split("\\|")));

        // Parse data parts - but only if message has more than 2 parts (the first 2 parts are always MessageType & Token - at least for now)
        if(messageParts.size() > 2) {
            this.dataParts = new ArrayList<>(this.messageParts.subList(2, (this.messageParts.size())));
        } else {
            this.dataParts = new ArrayList<>();
        }

        // Enforce server message type, since only server can send messages of type RESULT
        if(this.sender == Addressor.SERVER) { this.messageType = MessageType.RESULT; }
        else { this.messageType = MessageType.valueOf(this.messageParts.get(0)); }

    }

    // Custom methods
    private String buildMessageString(MessageType messageType, String token, ArrayList<String> data) {

        // Return MessageType & Token if data is empty
        if(data.size() == 0) { return messageType.toString() + "|" + token; }


        // If data is not empty, build messagestring
        StringBuilder stringBuilder = new StringBuilder();
        for(String dataString : data) {
            if(dataString.equals("")) {
                stringBuilder.append("|");
            }
            stringBuilder.append(dataString).append("|");
        }

        // Delete last |-char
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        // If message type LOGIN or CREATE_LOGIN
        if(messageType == MessageType.LOGIN || messageType == MessageType.CREATELOGIN) {
            return messageType + "|" + stringBuilder;
        }

        // Check if message has a token
        if(token == null) {
            return messageType.toString() + "|true|" + stringBuilder;
        } else {
            return messageType.toString() + "|" + token + "|" + stringBuilder;
        }

    }

    private ArrayList<String> parseDataParts(ArrayList<String> data) {

        // If data is empty, return empty arrayList
        if(data.size() == 0) { return new ArrayList<>(); }

        // Otherwise, return the list as is
        return data;

    }

    private void parseToken(String[] stringParts) {

        // Parses token for messages with MessageType only
        if(this.messageParts.size() == 1) { this.token = null; return; }

        // Parses token for PING command
        if(this.messageType == MessageType.PING && this.messageParts.size() == 2) { this.token = stringParts[1]; return; }

        // Parses token for CREATE_LOGIN and LOGIN Message Types
        if(this.messageType == MessageType.CREATELOGIN || this.messageType == MessageType.LOGIN) {
            this.token = null;
        } else {this.token = stringParts[1];}

        // Parses token for RESULT Message Types
        if(this.messageType == MessageType.RESULT && this.messageParts.size() == 3) {
            this.token = messageParts.get(2);
        }

        // Parses token for the LOGOUT Message Type
        if(this.messageType == MessageType.LOGOUT && this.messageParts.size() == 2) {
            this.token = messageParts.get(1);
        }
    }

    // Getters
    public Addressor getSender() { return this.sender; }
    public Addressor getRecipient() { return this.recipient; }
    public ArrayList<String> getMessageParts() { return this.messageParts; }
    public MessageType getMessageType() { return this.messageType; }
    public ArrayList<String> getDataParts() { return this.dataParts; }
    public String getMessageString() { return messageString + "\n"; }
    public String getToken() {
        // Returns 0 if the token is null
        return Objects.requireNonNullElse(this.token, "0");
    }
}
