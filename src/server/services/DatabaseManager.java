package server.services;

import common.HashDigest;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    // Fields
    private String connectionString;


    // Constructor
    /* This constructor is used to create an initial database for a new account. Upon reception of the CREATE_LOGIN
     * a new database is created with the user's chosen name.
     */
    public DatabaseManager(String accountName) {
        String fileString = System.getProperty("user.dir");
        this.connectionString = fileString + File.separator + "src" + File.separator + "server" + File.separator
                + "database" + File.separator + accountName + ".db";
        this.connectionString = "jdbc:sqlite:" + connectionString;
        
    }

    // Custom methods

    /* Database initialization method
     * This method gets called after the server receives a CREATE_LOGIN method. It throws the default database schema
     * shown in the picture on the README.
     */
    public void initializeDatabase() {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            System.out.println("[DATABASE-MANAGER] Initializing database...");

            // Create Accounts table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Accounts (" +
                    "Account_ID INTEGER PRIMARY KEY," +
                    "Name STRING NOT NULL," +
                    "Password STRING NOT NULL)");

            // Create items table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Items (" +
                    "ToDo_ID INTEGER PRIMARY KEY," +
                    "Account_ID INTEGER NOT NULL," +
                    "Title STRING NOT NULL," +
                    "Priority STRING NOT NULL," +
                    "Description STRING," +
                    "DueDate STRING," +
                    "Category STRING)");

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
        }
    }

    /* Method to store login credentials
     * Stores the login credentials on the database. Assumes that the input was already validated when this method gets
     * called! Handle with caution.
     */
    public void storeLoginCredentials(String username, String password) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Hash password
            String hashedPassword = new HashDigest(password).getDigest();

            // Write to db
            String writeString = "INSERT INTO Accounts (Name, Password) VALUES("
                    + "'" + username + "', "
                    + "'" + hashedPassword + "'"
                    + ")";
            statement.executeUpdate(writeString);

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
        }
    }

    /* Method to validate if a user already exist based on if it already has a database
     * Takes the username (an email) as an input
     */
    public static boolean doesDatabaseExist(String accountName) {
        String fileString = System.getProperty("user.dir");
        fileString = fileString + File.separator + "src" + File.separator + "server" + File.separator
                + "database" + File.separator + accountName + ".db";
        return new File(fileString).exists();
    }

    // Method to grab a users hashed password
    public String getPassword() {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Query password
            String queryString = "SELECT Password FROM Accounts WHERE Account_ID='1'";
            ResultSet resultSet = statement.executeQuery(queryString);
            String password = null;

            // Set to var & return
            while(resultSet.next()) { password = resultSet.getString("Password"); }

            return password;
        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return null;
        }
    }

    // Method to change a users password
    // ATTENTION: This method does not validate the input - it assumes the input was already validated before!
    public void changePassword(String newPassword) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Hash password
            String hashedPassword = new HashDigest(newPassword).getDigest();

            // Build queryString & execute it right away
            String queryString = "UPDATE Accounts SET Password='" + hashedPassword + "' WHERE Account_ID=1";
            statement.executeUpdate(queryString);

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
        }
    }

    // CRUD-Methods

    // CREATE Method with only title and priority
    public int createItem(String title, String priority) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Build string
            String insertString = "INSERT INTO Items(Account_ID, Title, Priority) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, priority);

            // Execute update
            preparedStatement.executeUpdate();

            // Grab item ID
            String selectStatement = "SELECT ToDo_ID FROM Items ORDER BY ToDo_ID";
            ResultSet resultSet = statement.executeQuery(selectStatement);
            int highestId = -1;
            while(resultSet.next()) { highestId = resultSet.getInt("ToDo_ID"); }

            return highestId;

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return -1;
        }
    }

    // CREATE Method with all (5) parameters
    public int createItem(String title, String priority, String description, String dueDate, String category) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Build string
            String insertString = "INSERT INTO Items(Account_ID, Title, Priority, Description, DueDate, Category) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, priority);
            preparedStatement.setString(4, description);
            preparedStatement.setString(5, dueDate);
            preparedStatement.setString(6, category);

            // Execute update
            preparedStatement.executeUpdate();

            // Grab item ID
            String selectStatement = "SELECT ToDo_ID FROM Items ORDER BY ToDo_ID";
            ResultSet resultSet = statement.executeQuery(selectStatement);
            int highestId = -1;
            while(resultSet.next()) { highestId = resultSet.getInt("ToDo_ID"); }

            return highestId;

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return -1;
        }
    }

    // CREATE Method with 4 parameters
    public int createItem(String title, String priority, String thirdParameter, String fourthParameter) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Check what type the missing parameters are
            InputValidator inputValidator = new InputValidator();
            String thirdParameterType = inputValidator.getParameterType(thirdParameter);
            String fourthParameterType = inputValidator.getParameterType(fourthParameter);

            // Description & DueDate
            if (thirdParameterType.equals("Description") && fourthParameterType.equals("DueDate")) {
                // Set values
                String description = thirdParameter;
                String dueDate = fourthParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, Description, DueDate) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, dueDate);

                // Execute update
                preparedStatement.executeUpdate();
            }

            // Description & Category
            if (thirdParameterType.equals("Description") && fourthParameterType.equals("Category")) {
                // Set values
                String description = thirdParameter;
                String category = fourthParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, Description, Category) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, category);

                // Execute update
                preparedStatement.executeUpdate();
            }

            // DueDate & Category
            if (thirdParameterType.equals("DueDate") && fourthParameterType.equals("Category")) {
                // Set values
                String dueDate = thirdParameter;
                String category = fourthParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, DueDate, Category) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, dueDate);
                preparedStatement.setString(5, category);

                // Execute update
                preparedStatement.executeUpdate();
            }


            // Grab item ID
            String selectStatement = "SELECT ToDo_ID FROM Items ORDER BY ToDo_ID";
            ResultSet resultSet = statement.executeQuery(selectStatement);
            int highestId = -1;
            while(resultSet.next()) { highestId = resultSet.getInt("ToDo_ID"); }

            return highestId;

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return -1;
        }
    }

    // CREATE Method with 3 parameters
    public int createItem(String title, String priority, String thirdParameter) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Check what type the missing parameters are
            InputValidator inputValidator = new InputValidator();
            String thirdParameterType = inputValidator.getParameterType(thirdParameter);

            // Description
            if (thirdParameterType.equals("Description")) {
                // Set values
                String description = thirdParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, Description) VALUES(?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, description);

                // Execute update
                preparedStatement.executeUpdate();
            }

            // DueDate
            if (thirdParameterType.equals("DueDate")) {
                // Set values
                String dueDate = thirdParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, DueDate) VALUES(?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, dueDate);

                // Execute update
                preparedStatement.executeUpdate();
            }

            // Category
            if (thirdParameterType.equals("Category")) {
                // Set values
                String category = thirdParameter;

                // Build string
                String insertString = "INSERT INTO Items(Account_ID, Title, Priority, Category) VALUES(?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, priority);
                preparedStatement.setString(4, category);
                // Execute update
                preparedStatement.executeUpdate();
            }


            // Grab item ID
            String selectStatement = "SELECT ToDo_ID FROM Items ORDER BY ToDo_ID";
            ResultSet resultSet = statement.executeQuery(selectStatement);
            int highestId = -1;
            while(resultSet.next()) { highestId = resultSet.getInt("ToDo_ID"); }

            return highestId;

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return -1;
        }
    }


    // READ method
    public ArrayList<String> getToDo(String idString) {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Grab item from database and parse out contents
            ArrayList<String> resultList = new ArrayList<>();
            String queryString = "SELECT ToDo_ID, Title, Priority, Description, DueDate, Category FROM main.Items WHERE ToDo_ID=" + idString;
            ResultSet resultSet = statement.executeQuery(queryString);
            while (resultSet.next()) {

                // Grab values
                String id = String.valueOf(resultSet.getInt("ToDo_ID"));
                String title = resultSet.getString("Title");
                String priority = resultSet.getString("Priority");
                String description = resultSet.getString("Description");
                String dueDate = resultSet.getString("DueDate");
                String category = resultSet.getString("Category");

                // Parse out nulls
                if(id != null && !id.equals("null")) { resultList.add(id); }
                if(title != null && !title.equals("null")) { resultList.add(title); }
                if(priority != null && !priority.equals("null")) { resultList.add(priority); }
                if(description != null && !description.equals("null")) { resultList.add(description); }
                if(dueDate != null && !dueDate.equals("null")) { resultList.add(dueDate); }
                if(category != null && !category.equals("null")) {resultList.add(category); }
            }

            // Return contents
            return resultList;

        } catch (Exception e) {
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return new ArrayList<String>();
        }

    }

    // LIST Method
    public ArrayList<String> listToDos() {
        try(Connection connection = DriverManager.getConnection(this.connectionString);
            Statement statement = connection.createStatement()) {

            // Prepare resultArray
            ArrayList<String> resultList = new ArrayList<>();

            // Prepare queryString
            String queryString = "SELECT ToDo_ID FROM Items WHERE Account_ID=1";
            ResultSet resultSet = statement.executeQuery(queryString);

            // Grab data
            while(resultSet.next()) {
                resultList.add(String.valueOf(resultSet.getInt("ToDo_ID")));
            }

            return resultList;

        } catch (Exception e) {
            // Return empty arrayList if we catch an exception
            System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void deleteItem(String ID) {
    	try(Connection connection = DriverManager.getConnection(this.connectionString)) {

                // Build string
                String insertString = "DELETE FROM Items WHERE ToDo_ID=" + ID;
                PreparedStatement preparedStatement = connection.prepareStatement(insertString);
             
                // Execute update
                preparedStatement.executeUpdate();
                System.out.println("[DATABASE] ITEM DELETED");

            } catch (Exception e) {
                System.out.println("[DATABASE-MANAGER] EXCEPTION: " + e.getMessage());
            }
    }



}
