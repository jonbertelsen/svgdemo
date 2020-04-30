package DBAccess;

import FunctionLayer.LoginSampleException;
import FunctionLayer.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.hamcrest.Matchers.*;

public class UserMapperTest {

    private static Connection testConnection;
    private static String USER = "root";
    private static String USERPW = "root";
    private static String DBNAME = "useradmin_test?serverTimezone=CET&useSSL=false";
    private static String HOST = "localhost";

    @BeforeClass
    public static void  setUp() {
        try {
            // avoid making a new connection for each test
            if ( testConnection == null ) {
                String url = String.format( "jdbc:mysql://%s:3306/%s", HOST, DBNAME );
                Class.forName( "com.mysql.cj.jdbc.Driver" );

                testConnection = DriverManager.getConnection( url, USER, USERPW );
                // Make mappers use test 
                Connector.setConnection( testConnection );
            } }
         catch ( ClassNotFoundException | SQLException ex ) {
             testConnection = null;
             System.out.println("Could not open connection to database: " + ex.getMessage());
         }
    }

    @Before
    public void beforeEachTest(){
        try ( Statement stmt = testConnection.createStatement()) {
            stmt.execute( "drop table if exists users" );
            stmt.execute( "CREATE TABLE `users` LIKE useradmin.users;" );
            stmt.execute("INSERT INTO users VALUES " +
                    "(1,'jens@somewhere.com','jensen','customer')," +
                    "(2,'ken@somewhere.com','kensen','customer')," +
                    "(3,'robin@somewhere.com','batman','employee')," +
                    "(4,'someone@nowhere.com','sesam','customer');");
        } catch ( SQLException ex ) {
            System.out.println( "Could not open connection to database: " + ex.getMessage() );
        }
    }

    @Test
    public void testSetUpOK() {
        // Just check that we have a connection.
        assertNotNull( testConnection );
    }

    @Test
    public void testLogin01() throws LoginSampleException {
        // Can we log in
        User user = UserMapper.login( "jens@somewhere.com", "jensen" );
        assertTrue( user != null );
    }

    @Test( expected = LoginSampleException.class )
    public void testLogin02() throws LoginSampleException {
        // We should get an exception if we use the wrong password
        User user = UserMapper.login( "jens@somewhere.com", "larsen" );
    }

    @Test
    public void testLogin03() throws LoginSampleException {
        // Jens is supposed to be a customer
        User user = UserMapper.login( "jens@somewhere.com", "jensen" );
        assertEquals( "customer", user.getRole() );
    }

    @Test
    public void testLogin04() throws LoginSampleException {
        // Jens is supposed to be a customer
        User user = UserMapper.login( "jens@somewhere.com", "jensen" );
        assertNotEquals( "employee", user.getRole() );
    }

    @Test
    public void testGetAllUsers() throws  LoginSampleException {
        List<User> userList = UserMapper.getAllUsers();
        // Check that the user table has 4 rows
        assertThat(userList, hasSize(4));
        // Check that one of the users' email address is "jens@somewhere.com"
        assertThat(userList, hasItem(hasProperty("email", equalTo("jens@somewhere.com"))));
    }

    @Test
    public void testCreateUser01() throws LoginSampleException {
        // Can we create a new user - Notice, if login fails, this will fail
        // but so would login01, so this is OK
        User original = new User( "king@kong.com", "uhahvorhemmeligt", "konge" );
        UserMapper.createUser( original );
        User retrieved = UserMapper.login( "king@kong.com", "uhahvorhemmeligt" );
        assertEquals( "konge", retrieved.getRole() );
        List<User> userList = UserMapper.getAllUsers();
        assertThat(userList, hasSize(5));
    }
}
