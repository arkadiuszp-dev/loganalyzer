package ap.cs.db;

import ap.cs.domain.LogEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogEventDbClientTest {


    @Test
    public void checkIfTableIsCreatedAtStartup() throws SQLException {
        //Given
        Connection connection = mock (Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement(any())).thenReturn(ps);
        //When
        LogEventDbClient dbClient = new LogEventDbClient(connection);
        //Then
        verify(connection).prepareStatement(contains("create table alert"));
    }

    @Test
    public void checkIfRecordIsInserted()  throws SQLException {
        //Given
        Connection connection = mock (Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(connection.prepareStatement(any())).thenReturn(ps);
        LogEvent event = new LogEvent("id1", "A", "localhost", 16, true);
        //When
        LogEventDbClient dbClient = new LogEventDbClient(connection);
        dbClient.accept(event);
        //Then
        verify(ps).setString(anyInt(), eq("id1"));//argument.capture());
        verify(ps).setString(anyInt(), eq("A"));//argument.capture());
        verify(ps).setString(anyInt(), eq("localhost"));//argument.capture());
        verify(ps).setLong(anyInt(), eq(16L));//argument.capture());
        verify(ps).setBoolean(anyInt(), eq(true));//argument.capture());
    }

    @Test
    public void checkIfCountIsExecuted() throws SQLException{
        //Given
        Connection connection = mock (Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement(any())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        //When
        LogEventDbClient dbClient = new LogEventDbClient(connection);
        dbClient.quantityCheck();
        //Then
        verify(connection).prepareStatement(contains("select count(*) from alert"));
    }
}
