package com.carmanconsulting.akka.voting.actor;

import java.sql.*;

import akka.actor.Props;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import com.carmanconsulting.akka.LifecycleLogger;
import com.carmanconsulting.akka.voting.exception.VoterNotFoundException;
import com.carmanconsulting.akka.voting.msg.Connect;
import com.carmanconsulting.akka.voting.msg.GetVoterRegistration;
import com.carmanconsulting.akka.voting.msg.VoterRegistration;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class VoterRegistry extends LifecycleLogger {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String SQL = "SELECT name FROM voter_registration WHERE voter_id = ?";
    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;

    private Connection connection;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Props props(String driverClassName, String url, String username, String password) {
        return Props.create(VoterRegistry.class, driverClassName, url, username, password);
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public VoterRegistry(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void preStart() throws Exception {
        super.preStart();
        self().tell(new Connect(), self());
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return connecting();
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    private void connect() throws SQLException, ClassNotFoundException {
        log.info("Connecting to \"{}\"...", url);
        Class.forName(driverClassName);
        this.connection = DriverManager.getConnection(url, username, password);
        log.info("Connected successfully, \"becoming\" connected...");
        getContext().become(connected());
        unstashAll();
    }

    public PartialFunction<Object, BoxedUnit> connected() {
        return ReceiveBuilder.match(GetVoterRegistration.class, this::getVoterRegistration).build();
    }

    public PartialFunction<Object, BoxedUnit> connecting() {
        return ReceiveBuilder
                .match(Connect.class, msg -> connect())
                .match(GetVoterRegistration.class, msg -> stash())
                .build();
    }

    private void getVoterRegistration(GetVoterRegistration msg) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SQL);
        stmt.setString(1, msg.getVoterId());
        ResultSet rs = stmt.executeQuery();
        log.info("Looking up voter \"{}\"...", msg.getVoterId());
        if (rs.next()) {
            VoterRegistration reg = new VoterRegistration(msg.getVoterId(), rs.getString(1));
            log.info("Found voter \"{}\" (\"{}\").", reg.getName(), reg.getVoterId());
            sender().tell(reg, self());
        } else {
            log.warning("Voter \"{}\" not found!", msg.getVoterId());
            sender().tell(new Status.Failure(new VoterNotFoundException(msg.getVoterId())), self());
        }
    }
}
