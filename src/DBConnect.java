import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DBConnect (){
        try{

            con = DriverManager.getConnection("jdbc:mysql://localhost/dndb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
            st = con.createStatement();

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void deposit(double amount, int accountID, int customerID){
        try{
            String query = "UPDATE accounts SET balance = balance + " + amount + " WHERE account_id = " + accountID + ";";
            st.executeUpdate(query);
            query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
            st.executeUpdate(query);
            query = "INSERT INTO deposits (transaction_id, account) VALUES (" + getTransactionID() + ", " + accountID + ");";
            st.executeUpdate(query);
            updateTotalSavings(customerID);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void withdraw(double amount, int accountID, int customerID){
        try{
            if(sufficientFunds(amount, accountID)){
                String query = "UPDATE accounts SET balance = balance - " + amount + " WHERE account_id = " + accountID + ";";
                st.executeUpdate(query);
                query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
                st.executeUpdate(query);
                query = "INSERT INTO withdrawals (transaction_id, account) VALUES (" + getTransactionID() + ", " + accountID + ");";
                st.executeUpdate(query);
                updateTotalSavings(customerID);
            }else {
                System.out.println("Insufficient funds");
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void transfer(double amount, int senderAccountID, int receiverAccountID){
        try{
            if(sufficientFunds(amount, senderAccountID)) {
                String query = "UPDATE accounts SET balance = balance - " + amount + " WHERE account_id = " + senderAccountID + ";";
                st.executeUpdate(query);
                query = "UPDATE accounts SET balance = balance + " + amount + " WHERE account_id = " + receiverAccountID + ";";
                st.executeUpdate(query);
                query = "INSERT INTO transactions (amount) VALUES (" + amount + ");";
                st.executeUpdate(query);
                query = "INSERT INTO transfers (transaction_id, sender, receiver) VALUES (" + getTransactionID() + ", " + senderAccountID + ", " + receiverAccountID + ");";
                st.executeUpdate(query);
                updateTotalSavings(getUserID(senderAccountID));
                updateTotalSavings(getUserID(receiverAccountID));

            }else{
                System.out.println("Insufficient funds");
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void addInterests(int accountID, int customerID, char period){

        double rate = getInterestRate(accountID);
        String query = "";
        if(period == 'y')
            rate = 1 + rate;
        else if (period == 'c')
            rate = Math.pow(1 + rate, 1/12.0);
        else if (period == 'd')
            rate = Math.pow(1 + rate, 1/365.0);
        try {
            query = "UPDATE accounts SET balance = balance * " + rate + " WHERE account_id = " + accountID + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
        updateTotalSavings(customerID);
        updateTotalLoans(customerID);
    }

    public void createUser(int status){
        try {
            int rand = (int) (Math.random() * 4);
            String query = "INSERT INTO user (name, address, city, postal_code, total_loans, total_savings, status) " +
                    "VALUES ('" + Generator.generateName() + "', '" + Generator.generateAdress() + "', '" +
                    Generator.generateCity(rand) + "', '" + Generator.generatePostalCode(rand) + "', " + 0 + ", " + 0 +
                    ", " + status + ");";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void editUser(int customerID, String newName){
        try{
            String query = "UPDATE user SET name = '" + newName + "' WHERE customer_id = '" + customerID + "';";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void deleteUser(int customerID){
        try{
            String query = "DELETE FROM user WHERE customer_id = " + customerID + ";";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void getUsers(char orderBy){
        try{
            String query = "";

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
            System.out.printf("%-4s%-20s%-25s%-15s%-18s%-15s%-15s%-10s%-10s", "id", "name", "address", "city",
                    "postal Code", "total loans", "total savings", "status", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "-----------------------------------------------------------");
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

    public void getAccounts(char orderBy){
        try{
            String name = "";
            String query = "";

            switch(orderBy){
                case 'a':
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
            if(orderBy == 'a')
                System.out.printf("%-25s", "name");
            System.out.printf("%-15s%-20s%-25s%-15s%-18s%-15s", "account id", "customer id", "balance", "yearly rate",
                    "type", "date of creation");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "----------------------------------");
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

    public void rollBackTransfer(int transactionID){
        try {
            String query = "SELECT amount FROM transactions WHERE transaction_id = " + transactionID + ";";
            rs = st.executeQuery(query);
            rs.next();
            double amount = rs.getDouble("amount");
            query = "SELECT sender, receiver FROM transfers where transaction_id = " + transactionID + ";";
            rs = st.executeQuery(query);
            rs.next();
            int senderAccountID = rs.getInt("sender");
            int receiverAccountID = rs.getInt("receiver");
            transfer(amount, receiverAccountID, senderAccountID);
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

}
