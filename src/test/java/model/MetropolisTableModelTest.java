package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class MetropolisTableModelTest {

    private MetropolisTableModel model;

    @BeforeEach
    public void setUp() throws Exception {
        String url = "jdbc:h2:mem:metropolis_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS metropolises");
            statement.execute("CREATE TABLE metropolises (" +
                    "metropolis VARCHAR(64), " +
                    "continent VARCHAR(64), " +
                    "population BIGINT)");

            statement.execute("INSERT INTO metropolises VALUES ('Mumbai', 'Asia', 20400000)");
            statement.execute("INSERT INTO metropolises VALUES ('New York', 'North America', 8400000)");
            statement.execute("INSERT INTO metropolises VALUES ('London', 'Europe', 8900000)");
        }

        model = new MetropolisTableModel();
    }

    @Test
    public void testInitialState() {
        assertEquals(0, model.getRowCount(), "Table should start empty.");
        assertEquals(3, model.getColumnCount());
        assertEquals("Metropolis", model.getColumnName(0));
    }

    @Test
    public void testAdd() {
        model.add("Tbilisi", "Europe", "1100000");

        assertEquals(1, model.getRowCount(), "Table should show only the newly added row.");
        assertEquals("Tbilisi", model.getValueAt(0, 0));
        assertEquals("Europe", model.getValueAt(0, 1));
        assertEquals(1100000L, model.getValueAt(0, 2));
    }

    @Test
    public void testAddInvalidPopulation() {
        model.add("GhostCity", "Nowhere", "not-a-number");
        assertEquals(0, model.getRowCount(), "Should not add row with invalid population.");
    }

    @Test
    public void testSearchAll() {
        model.search("", "", "", true, true);
        assertEquals(3, model.getRowCount());
    }

    @Test
    public void testSearchExactMatch() {
        model.search("Mumbai", "", "", true, true);
        assertEquals(1, model.getRowCount());
        assertEquals("Mumbai", model.getValueAt(0, 0));
    }

    @Test
    public void testSearchPartialMatch() {
        model.search("", "America", "", false, true);
        assertEquals(1, model.getRowCount());
        assertEquals("New York", model.getValueAt(0, 0));
    }

    @Test
    public void testSearchPopulationLarger() {
        model.search("", "", "10000000", true, true);
        assertEquals(1, model.getRowCount());
        assertEquals("Mumbai", model.getValueAt(0, 0));
    }

    @Test
    public void testSearchPopulationSmaller() {
        model.search("", "", "10000000", true, false);
        assertEquals(2, model.getRowCount());
    }

    @Test
    public void testSearchInvalidPopulation() {
        model.search("", "", "invalid", true, true);
        assertEquals(3, model.getRowCount());
    }

    @Test
    public void testGetValueAtBounds() {
        model.search("", "", "", true, true);
        assertNull(model.getValueAt(-1, 0), "Should return null for negative row.");
        assertNull(model.getValueAt(10, 0), "Should return null for out of bounds row.");
        assertNull(model.getValueAt(0, 5), "Should return null for out of bounds column.");
    }

    @Test
    public void testSearchWithNulls() {
        model.search(null, null, null, true, true);
        assertEquals(3, model.getRowCount());
    }

    @Test
    public void testAddSQLException() throws Exception {
        String url = "jdbc:h2:mem:metropolis_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE metropolises");
        }

        model.add("Atlantis", "Oceania", "5000");

        assertEquals(0, model.getRowCount(), "Should not add row if SQL fails.");
    }

    @Test
    public void testSearchSQLException() throws Exception {
        String url = "jdbc:h2:mem:metropolis_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE metropolises");
        }
        model.search("", "", "", true, true);
        assertEquals(0, model.getRowCount(), "Data should remain unchanged after failed search.");
    }
}