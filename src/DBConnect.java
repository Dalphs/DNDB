import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class DBConnect {
    //Class variables, instances of the Connection, Statement and ResulSet class
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DBConnect (){
        //Every time we want to connect to the database, we need to use try-catch to catch any exception
        try{

            //Assigning the con and st variable. The con variable connects to the dndb database
            con = DriverManager.getConnection("jdbc:mysql://localhost/dndb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
            st = con.createStatement();

        }catch(Exception ex){
            //Printing the Exception to the console
            System.out.println("Error: " + ex);
        }
    }

    //Deposit method takes two parametres, amount and accountID
    public void deposit(double amount, int accountID){
        try{
            //Creating three sql queries and executing them. The first statement changes the balance on the account.
            //The second creates a transaction and the third creates a deposit
            String query = "UPDATE accounts SET balance = balance + " + amount + " WHERE account_id = " + accountID + ";";
            st.executeUpdate(query);
            query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
            st.executeUpdate(query);
            query = "INSERT INTO deposits (transaction_id, account) VALUES (" + getTransactionID() + ", " + accountID + ");";
            st.executeUpdate(query);

            //Updating the column total-savings in user table for the owner of the account
            updateTotalSavings(getUserID(accountID));
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //Withdraw method takes two parametres, amount and accountID
    public void withdraw(double amount, int accountID){
        try{
            //Only executes the code if there is enough money in the account
            if(sufficientFunds(amount, accountID)){

                //Creates three sql queries. First statement updates the balance on the account.
                //Second creates a transaction and third creates a withdraw
                String query = "UPDATE accounts SET balance = balance - " + amount + " WHERE account_id = " + accountID + ";";
                st.executeUpdate(query);
                query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
                st.executeUpdate(query);
                query = "INSERT INTO withdrawals (transaction_id, account) VALUES (" + getTransactionID() + ", " + accountID + ");";
                st.executeUpdate(query);

                //Updates the total_saving column for the owner of the account in the user table
                updateTotalSavings(getUserID(accountID));
            }else {
                //In case there is not enough mooney in the account, Insufficient funds is printed in the console
                System.out.println("Insufficient funds");
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //Tansfer method takes three parametres, amount, senderID and ReceiverID
    public void transfer(double amount, int senderAccountID, int receiverAccountID){
        try{
            //The transfer is only excecuted if there is enough money in the senders account
            if(sufficientFunds(amount, senderAccountID)) {

                //Creating 4 sql queries, first substracts the money from the senderAccount, second adds the amount to the receiverAccount
                //Third creates a transsaction, and fourth creates a transfer
                String query = "UPDATE accounts SET balance = balance - " + amount + " WHERE account_id = " + senderAccountID + ";";
                st.executeUpdate(query);
                query = "UPDATE accounts SET balance = balance + " + amount + " WHERE account_id = " + receiverAccountID + ";";
                st.executeUpdate(query);
                query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
                st.executeUpdate(query);
                query = "INSERT INTO transfers (transaction_id, sender, receiver) VALUES (" + getTransactionID() + ", " + senderAccountID + ", " + receiverAccountID + ");";
                st.executeUpdate(query);

                //Updates the total_savings column for the owners of the two accounts
                updateTotalSavings(getUserID(senderAccountID));
                updateTotalSavings(getUserID(receiverAccountID));

            }else{
                System.out.println("Insufficient funds");
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //addInterests method takes two parametres, accountID and period
    public void addInterests(int accountID, char period){
        //creating two method variables
        double rate = getInterestRate(accountID);
        String query = "";

        //This if-else block takes the period parameter and calculates the rate for the account
        if(period == 'y')
            rate = 1 + rate;
        else if (period == 'm')
            rate = Math.pow(1 + rate, 1/12.0);
        else if (period == 'd')
            rate = Math.pow(1 + rate, 1/365.0);
        try {
            //Assigning the query woth a sql query, that changes the balance in the account according with the rate and period
            query = "UPDATE accounts SET balance = balance * " + rate + " WHERE account_id = " + accountID + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        //Updates total_savings and total_loans column for the owner of the account
        updateTotalSavings(getUserID(accountID));
        updateTotalLoans(getUserID(accountID));
    }

    //createUser method takes on parameter, status. 0 = customer, 1 = employee
    public void createUser(int status){
        try {
            //Creating a random number from 0-3, to get city and postal code
            int rand = (int) (Math.random() * 4);
            //The query creats a new user, where the name, adress, city and postal_code,
            // is generated by the Generator classs' static methods, as well as using the received status
            String query = "INSERT INTO user (name, address, city, postal_code, total_loans, total_savings, status) " +
                    "VALUES ('" + Generator.generateName() + "', '" + Generator.generateAdress() + "', '" +
                    Generator.generateCity(rand) + "', '" + Generator.generatePostalCode(rand) + "', " + 0 + ", " + 0 +
                    ", " + status + ");";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //editUser method takes two parametres, customerID and newName
    public void editUser(int customerID, String newName){
        try{
            //Creating a sql query that finds the customer identified by the customerID and changes the name
            String query = "UPDATE user SET name = '" + newName + "' WHERE customer_id = '" + customerID + "';";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //deleteUser method takes one parameter, customerID
    public void deleteUser(int customerID){
        try{
            //Creating sql query that deletes a user from the user table identified by its customer-id
            String query = "DELETE FROM user WHERE customer_id = " + customerID + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //getUser method takes one parameter, orderBy, which wil determine how the output is ordered
    public void getUsers(char orderBy){
        try{
            //Creating a string to store out sql query
            String query = "";

            //If orderBy is a it wil be alphabetical, d wil be chronological, s will be by total_savings
            //and l will be total_loans
            switch(orderBy){
                case 'a':
                    query = "SELECT * FROM user ORDER BY name ASC"; break;
                case 'd':
                    query = "SELECT * FROM user ORDER BY date_of_creation ASC"; break;
                case 's':
                    query = "SELECT * FROM user ORDER BY total_savings DESC"; break;
                case 'l':
                    query = "SELECT * FROM user ORDER BY total_loans DESC"; break;
            }

            rs = st.executeQuery(query);
            //Printing the header for the table using formatted text, to ensure a uniform look
            System.out.printf("%-4s%-20s%-25s%-15s%-18s%-15s%-15s%-10s%-10s", "id", "name", "address", "city",
                    "postal Code", "total loans", "total savings", "status", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "-----------------------------------------------------------");
            //The wile loop prints all the users from the resultSet using formatted text
            while(rs.next()){
                String id = rs.getString("customer_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String postalCode = rs.getString("postal_code");
                double totalLoans = rs.getDouble("total_loans");
                double totalSavings = rs.getDouble("total_savings");
                String status = rs.getString("status");
                String dateOfCreation = rs.getString("date_of_creation");

                System.out.printf("%-4s%-20s%-25s%-20s%-15s%-15.2f%-15.2f%-8s%-15s", id, name, address, city, postalCode,
                        totalLoans, totalSavings, status, dateOfCreation);
                System.out.println();

            }
            con.close();
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void getAccounts(char orderBy){
        try{
            //Two String variables, one to store the name and the other to store a sql query
            String name = "";
            String query = "";

            //Selects in what order the accounts will be printed.
            switch(orderBy){
                case 'a':
                    //For printing alphabetically I had to make a left join between accounts and users,
                    // since the accounts table doesnt store names of the users
                    query = "SELECT user.name, accounts.account_id, accounts.customer_id, accounts.balance, " +
                            "accounts.yearly_rate, accounts.type, accounts.date_of_creation FROM accounts " +
                            "LEFT JOIN user ON user.customer_id = accounts.customer_id ORDER BY user.name ASC\n"; break;
                case 'd':
                    query = "SELECT * FROM accounts ORDER BY date_of_creation ASC"; break;
                case 's':
                    query = "SELECT * FROM accounts ORDER BY balance DESC"; break;
                case 'l':
                    query = "SELECT * FROM accounts ORDER BY balance ASC"; break;
            }

            rs = st.executeQuery(query);

            //Printing a header for the table using formatted text
            if(orderBy == 'a')
                System.out.printf("%-25s", "name");
            System.out.printf("%-15s%-20s%-25s%-15s%-18s%-15s", "account id", "customer id", "balance", "yearly rate",
                    "type", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "----------------------------------");

            //Printing all results to the console using formatted text
            while(rs.next()){
                if(orderBy == 'a')
                    name = rs.getString("name");
                String accountID = rs.getString("account_id");
                String customerID = rs.getString("customer_id");
                double balance = rs.getDouble("balance");
                String yearlyRate = rs.getString("yearly_rate");
                String type = rs.getString("type");
                String dateOfCreation = rs.getString("date_of_creation");
                if(orderBy == 'a')
                    System.out.printf("%-25s", name);
                System.out.printf("%-15s%-20s%-25.2f%-15s%-18s%-15s", accountID, customerID, balance, yearlyRate, type, dateOfCreation);
                System.out.println();

            }
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //rollBackTransfer takes one parameter, transactionID
    public void rollBackTransfer(int transactionID){
        try {
            //The first sql query retrieves the amount from the specific transaction and saves it in the variable amount
            String query = "SELECT amount FROM transactions WHERE transaction_id = " + transactionID + ";";
            rs = st.executeQuery(query);
            rs.next();
            double amount = rs.getDouble("amount");

            //This query retrieves the senderAcconutID and receiverAccountID and seaves it in two variables
            query = "SELECT sender, receiver FROM transfers where transaction_id = " + transactionID + ";";
            rs = st.executeQuery(query);
            rs.next();
            int senderAccountID = rs.getInt("sender");
            int receiverAccountID = rs.getInt("receiver");

            //Then the transfer method is called to create a new transfer,
            //only the sender and receiver is reversed, the amount is the same as the initial transfer
            transfer(amount, receiverAccountID, senderAccountID);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //searchCities method takes a variable number of strings as parametres
    public void searchCities(String... cities){
        try{
            //This creates an sql query with all the cities represented
            String query = "SELECT * FROM user WHERE city = '" + cities[0];
            for (int i = 1; i < cities.length; i++) {
                query = query + "' OR city = '" + cities[i];
            }
            query = query + "';";

            rs = st.executeQuery(query);

            //This prints the header for the data using formatted text
            System.out.printf("%-4s%-20s%-25s%-15s%-18s%-15s%-15s%-10s%-10s", "id", "name", "address", "city",
                    "postal Code", "total loans", "total savings", "status", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "-----------------------------------------------------------");

            //This prints all the retrived data
            while(rs.next()){
                String id = rs.getString("customer_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String postalCode = rs.getString("postal_code");
                double totalLoans = rs.getDouble("total_loans");
                double totalSavings = rs.getDouble("total_savings");
                String status = rs.getString("status");
                String dateOfCreation = rs.getString("date_of_creation");

                System.out.printf("%-4s%-20s%-25s%-20s%-15s%-15.2f%-15.2f%-8s%-15s", id, name, address, city, postalCode,
                        totalLoans, totalSavings, status, dateOfCreation);
                System.out.println();

            }
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //convertToEuro takes one parameter, accountID
    public void convertToEuro(int accountID){
        try{
            //This query retrieves the specified account
            String query = "SELECT * FROM accounts WHERE account_id = " + accountID + ";";
            rs = st.executeQuery(query);

            //This is the header for the account
            System.out.printf("%-15s%-20s%-25s%-15s%-18s%-15s", "account id", "customer id", "balance", "yearly rate",
                    "type", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "----------------------------------");

            //This prints the retrived data
            while(rs.next()){
                String accountID1 = rs.getString("account_id");
                String customerID = rs.getString("customer_id");
                double balance = rs.getDouble("balance") / 7.46;
                String yearlyRate = rs.getString("yearly_rate");
                String type = rs.getString("type");
                String dateOfCreation = rs.getString("date_of_creation");
                System.out.printf("%-15s%-20s%-25.2f%-15s%-18s%-15s", accountID1, customerID, balance, yearlyRate, type, dateOfCreation);
                System.out.println();

            }
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //findRussianOligarch does not take any parametres
    public void findRussianOligarch(){
        try{
            //This sql query finds all users that have Frederikssund listed, but does not have the correct postal_code
            String query = "SELECT * FROM `user` WHERE city = 'Frederikssund' AND postal_code != 3600";
            rs = st.executeQuery(query);
            rs.next();

            //This is the header for the output
            System.out.printf("%-4s%-20s%-25s%-15s%-18s%-15s%-15s%-10s%-10s", "id", "name", "address", "city",
                    "postal Code", "total loans", "total savings", "status", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "-----------------------------------------------------------");

            //This prints the data retrieved
            System.out.printf("%-4s%-20s%-25s%-20s%-15s%-15.2f%-15.2f%-8s%-15s", rs.getString("customer_id"),
                    rs.getString("name"), rs.getString("address"), rs.getString("city"),
                    rs.getString("postal_code"), rs.getDouble("total_loans"), rs.getDouble("total_savings")
                    , rs.getString("status"), rs.getString("date_of_creation"));

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    //Creates a user with mismatching postalcode and city
    public void createRussianOligarch(){
        try {
            int rand = (int) (Math.random() * 4);
            String query = "INSERT INTO user (name, address, city, postal_code, total_loans, total_savings, status) " +
                    "VALUES ('" + Generator.generateName() + "', '" + Generator.generateAdress() + "', 'Frederikssund', '4800', "
                    + 0 + ", " + 0 + ", " + 0 + ");";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void createSavingsAccount(int customerID, double initialBalance, double yearlyRate){
        try{
            String query = "INSERT INTO accounts (customer_id, balance, yearly_rate, type) VALUES (" + customerID + ", " +
                    initialBalance + ", " + yearlyRate + ", 0);";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        updateTotalSavings(customerID);
    }

    //Method that creates a savingsaccount from customerID, initialBalacne and yearly rate
    public void createCheckInAccount(int customerID, double initialBalance, double yearlyRate){
        try{
            String query = "INSERT INTO accounts (customer_id, balance, yearly_rate, type) VALUES (" + customerID + ", " +
                    initialBalance + ", " + yearlyRate + ", 2);";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);

        }
        updateTotalSavings(customerID);
    }

    //Method that creates a loan account from customerID, initialBalance and yearlyRate
    public void createLoanAccount(int customerID, double initialBalance, double yearlyRate){
        try{
            String query = "INSERT INTO accounts (customer_id, balance, yearly_rate, type) VALUES (" + customerID + ", " +
                    -initialBalance + ", " + yearlyRate + ", 1);";
            st.executeUpdate(query);
            query = "INSERT INTO loans (account_id, original_amount) VALUES (" + getAccountID() + ", " + initialBalance + ");";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        updateTotalLoans(customerID);
    }

    //Method that returns the last generated accountID
    public int getAccountID(){
        int temp = 0;
        try{
            String query = "SELECT account_id FROM accounts ORDER BY account_id DESC";
            rs = st.executeQuery(query);
            rs.next();
            temp = rs.getInt("account_id");
        }catch (Exception ex){
            System.out.println(ex);
        }
        return temp;
    }

    //Method that updates the column total_loans for a user identified by customerID
    public void updateTotalLoans(int customerId){
        double total = 0;
        try{
            String query = "SELECT balance FROM accounts WHERE customer_id = " + customerId + " AND type = 1;";
            rs = st.executeQuery(query);
            while(rs.next()){
                total += rs.getDouble("balance");
            }
            query = "UPDATE user SET total_loans =  " + -total + " WHERE customer_id = " + customerId + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println(ex);
        }
    }

    //Method that updates total savings column for a user identified by a customerID
    public void updateTotalSavings(int customerId){
        double total = 0;
        try{
            String query = "SELECT balance FROM accounts WHERE customer_id = " + customerId + " AND (type = 0 OR type = 2);";
            rs = st.executeQuery(query);
            while(rs.next()){
                total += rs.getDouble("balance");
            }
            query = "UPDATE user SET total_savings =  " + total + " WHERE customer_id = " + customerId + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println(ex);
        }
    }

    //Method that returns wether there is more money than a specific amount in a specific account
    public boolean sufficientFunds(double amount, int accountID){
        boolean temp = false;
        try{
            String query = "SELECT balance FROM accounts WHERE account_id = " + accountID + ";";
            rs = st.executeQuery(query);
            rs.next();
            if (rs.getDouble("balance") >= amount)
                temp = true;

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        return temp;
    }

    //Method that retrieves the latest generated tansactionID
    public int getTransactionID(){
        int temp = 0;
        try{
            String query = "SELECT transaction_id FROM transactions ORDER BY transaction_id DESC";
            rs = st.executeQuery(query);
            rs.next();
            temp = rs.getInt("transaction_id");
        }catch (Exception ex){
            System.out.println(ex);
        }
        return temp;
    }

    //Method that retrices the interestRate form a specific account
    public double getInterestRate(int accountID){
        double rate = 0;
        try{
            String query = "SELECT yearly_rate FROM accounts WHERE account_id = " + accountID + ";";
            rs = st.executeQuery(query);
            rs.next();
            rate = rs.getDouble("yearly_rate");
        }catch (Exception ex){
            System.out.println(ex);
        }
        return rate;
    }

    //Method that returns the userID for a specific account
    public int getUserID(int accountID){
        int userID = 0;
        try{
            String query = "SELECT customer_id FROM accounts WHERE account_id = " + accountID + ";";
            rs = st.executeQuery(query);
            rs.next();
            userID = rs.getInt("customer_id");
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        return userID;
    }

    //This is the method i used to create 99 % of my users
    public void CreateMultipleUsersAndAccounts(){
        for (int i = 20; i < 1000; i++) {
            double[] checkInRates = {0.00125, 0.0025, 0.005, 0.0075, 0.01, 0.0125, 0.015, 0.01625, 0.0175, 0.02};
            int status = 0;
            int rand = (int) (Math.random() * 10);
            if( rand == 1)
                status = 1;
            createUser(status);
            createCheckInAccount(i, randomBalance(), checkInRates[rand]);
            if(rand < 5) {
                rand = (int) (Math.random() * 10);
                double[] loanRates = {0.025, 0.03, 0.035, 0.0375, 0.04, 0.045, 0.055, 0.065, 0.075, 0.1};
                createLoanAccount(i, randomBalance(), loanRates[rand]);
            }
            if (rand > 7){
                rand = (int) (Math.random() * 10);
                double[] savingsRates = {0.0125, 0.015, 0.01625, 0.0175, 0.02, 0.0225, 0.025, 0.02625, 0.0275, 0.3};
                createSavingsAccount(i, randomBalance(), savingsRates[rand]);

            }
        }
    }

    //This is the method that created most of the 1800 transfers
    public void createTransfers(){
        try{
            ArrayList<Integer> accountIDs = new ArrayList<>();
            String query = "SELECT account_id FROM accounts WHERE type = 0 OR type = 2";
            int[] cash = {5, 10, 15, 20, 25, 30, 40,  50 , 75, 100, 200};
            rs = st.executeQuery(query);
            while(rs.next()){
                accountIDs.add(rs.getInt("account_id"));
            }
            Collections.shuffle(accountIDs);
            for (int i = 1; i < accountIDs.size(); i++) {
                double amount = ((int) (Math.random() * 25000)) / 100.0;
                transfer(amount, accountIDs.get(i), accountIDs.get(i - 1));
                if(i % 2 == 0){
                    withdraw(cash[i % 11], accountIDs.get(i));
                } else{
                    deposit(cash[i % 11], accountIDs.get(i));
                }
            }
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public static double randomBalance(){
        double rand = 0;
        int generator = (int) (Math.random() * 3);
        if(generator == 0) {
            rand = ((int) (Math.random() * 1000000000)) / 100.0;
            if (rand > 1000000)
                rand = ((int) rand) / 100.0;
        } else if(generator == 1){
            rand = ((int) (Math.random() * 10000000)) / 100.0;
            if (rand > 10000)
                rand = ((int) rand) / 100.0;
        }else if(generator == 2){
            rand = ((int) (Math.random() * 1000000)) / 100.0;
            if (rand > 1000)
                rand = ((int) rand) / 100.0;
        }

        return rand;
    }

}
