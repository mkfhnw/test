package common;

public enum MessageType {

    // The following values are valid for client-to-server messages
    LOGIN("Login"),
    LOGOUT("Logout"),
    CREATELOGIN("CreateLogin"),
    CREATETODO("CreateToDo"),
    CHANGEPASSWORD("ChangePassword"),
    GETTODO("GetToDo"),
    DELETETODO("DeleteToDo"),
    LISTTODOS("ListToDos"),
    PING("Ping"),

    // The following value is valid for server-to-client messages
    RESULT("Result");

    // Fields of the enum values
    private final String name;

    // Constructor to pass the names
    MessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
