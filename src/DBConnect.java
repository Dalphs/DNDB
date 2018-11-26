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

    public void getData(){
        try{
            String query = "SELECT * FROM user";
            rs = st.executeQuery(query);
            System.out.printf("%-4s%-20s%-15s%-12s%-18s%-15s%-7s%-7s%-15s", "id", "name", "address", "city",
                    "postal Code", "total loans", "total savings", "status", "timestamp");
            System.out.println("\n-------------------------------------------------------------------------------" +
                    "----------------------------------");
            while(rs.next()){
                String id = rs.getString("customer_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String postalCode = rs.getString("postal_code");
                String totalLoans = rs.getString("total_loans");
                String totalSavings = rs.getString("total_savings");
                String status = rs.getString("status");
                String dateOfCreation = rs.getString("date_of_creation");

                System.out.printf("%-4s%-20s%-15s%-15s%-15s%-15s%-7s%-7s%-15s", id, name, address, city, postalCode,
                totalLoans, totalSavings, status, dateOfCreation);
                System.out.println();

            }
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void insert(String name){
        try{
            String query = "INSERT INTO datetest (navn) VALUES ('" + name + "');";
            st.executeUpdate(query);

        }catch (Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void delete(String name){
        try{
            String query = "DELETE FROM datetest WHERE navn = '" + name + "';";
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }

    public void update (){
        try{
            String query = "update mathtest SET dectal = tal * dectal";
            st.executeUpdate(query);
        }catch(Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    public void createUser(int status){
        try {
            int rand = (int) (Math.random() * 4);
            String query = "INSERT INTO user (name, address, city, postal_code, total_loans, total_savings, status) " +
                    "VALUES ('" + Generator.generateName() + "', '" + Generator.generateAdress() + "', '" +
                    Generator.generateCity(rand) + "', '" + Generator.generatePostalCode(rand) + "', " + 0 + ", " + 0 +
                    ", " + status + ");";
            ;
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

    public void transfer(double amount, int senderUserID, int senderAccountID, int receiverUserID, int receiverAccountID){
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
                updateTotalSavings(senderUserID);
                updateTotalSavings(receiverUserID);

            }else{
                System.out.println("Insufficient funds");
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex);
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

}
