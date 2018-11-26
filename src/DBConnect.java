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
            String query = "SELECT * FROM dndb";
            rs = st.executeQuery(query);
            System.out.println("Records from db");
            while(rs.next()){
                String id = rs.getString("customer_id");
                String name = rs.getString("name");
                String address = rs.getString("name");
                String city = rs.getString("name");
                String postalCode = rs.getString("name");
                String totalLoans = rs.getString("name");
                String totalSavings = rs.getString("name");
                String status = rs.getString("name");
                String dateOfCreation = rs.getString("name");

                System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s", id, name, address, city, postalCode,
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
}
