package com.carmanconsulting.akka.voting.actor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import akka.actor.Status;
import akka.testkit.TestActorRef;
import com.carmanconsulting.akka.AkkaTestCase;
import com.carmanconsulting.akka.voting.msg.GetVoterRegistration;
import com.carmanconsulting.akka.voting.msg.VoterRegistration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoterRegistryTest extends AkkaTestCase {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    public static final String URL = "jdbc:hsqldb:mem:test";
    public static final String USER = "sa";
    public static final String PASS = "";
    public static final String INSERT_SQL = "INSERT INTO voter_registration (voter_id, name) values ('%s', '%s')";
    public static final String CREATE_SQL = "CREATE TABLE voter_registration (voter_id VARCHAR(255), name VARCHAR(255), PRIMARY KEY (voter_id))";
    public static final String VOTER_NAME = "Test Voter";
    public static final String VOTER_ID = "12345";
    private static Connection connection;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
    }

    @BeforeClass
    public static void initDatabase() throws Exception {
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(URL, USER, PASS);
        Statement statement = connection.createStatement();
        statement.execute(CREATE_SQL);
        statement.execute(String.format(INSERT_SQL, VOTER_ID, VOTER_NAME));
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testGetVoterRegistration() {
        TestActorRef<VoterRegistry> ref = TestActorRef.create(system(), VoterRegistry.props(DRIVER, URL, USER, PASS));

        ref.tell(new GetVoterRegistration(VOTER_ID), testActor());

        VoterRegistration reg = expectMsgClass(VoterRegistration.class);
        assertEquals(VOTER_ID, reg.getVoterId());
        assertEquals(VOTER_NAME, reg.getName());
    }

    @Test
    public void testGetVoterRegistrationWhenNotFound() {
        TestActorRef<VoterRegistry> ref = TestActorRef.create(system(), VoterRegistry.props(DRIVER, URL, USER, PASS));

        ref.tell(new GetVoterRegistration("bogus"), testActor());

        Status.Failure failure = expectMsgClass(Status.Failure.class);
    }
}