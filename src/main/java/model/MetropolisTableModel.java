package model;

import dto.Metropolis;

import javax.swing.table.AbstractTableModel;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A custom table model that manages the data for the Metropolis application.
 * It acts as both the Swing Model for the JTable and the Data Access Object (DAO)
 * connecting to the underlying MySQL database.
 */
public class MetropolisTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Metropolis", "Continent", "Population"};
    private final List<Metropolis> data;

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    /**
     * Constructs a new MetropolisTableModel.
     * Initializes the empty data list and loads database credentials from properties.
     * Initializes with an empty table.
     */
    public MetropolisTableModel() {
        data = new ArrayList<>();
        loadProperties();
    }

    /**
     * Loads the database URL, username, and password from the db.properties file.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.err.println("Unable to find db.properties");
                return;
            }
            prop.load(input);
            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.user");
            dbPassword = prop.getProperty("db.password");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Helper method to establish a connection to the MySQL database.
     * @return A live Connection object to the database.
     * @throws SQLException if a database access error occurs or the url is null.
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Adds a new metropolis entry to the database and updates the table to display
     * only the newly added entry.
     * @param metropolis The name of the city.
     * @param continent  The continent where the city is located.
     * @param population The population of the city as a String.
     */
    public void add(String metropolis, String continent, String population) {
        String sql = "INSERT INTO metropolises (metropolis, continent, population) VALUES (?, ?, ?)";
        long popValue = 0;

        if (population != null && !population.trim().isEmpty()) {
            try {
                popValue = Long.parseLong(population.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid population format for Add");
                return;
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, metropolis);
            statement.setString(2, continent);
            statement.setLong(3, popValue);
            statement.executeUpdate();
            
            data.clear();
            data.add(new Metropolis(metropolis, continent, popValue));
            fireTableDataChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches the database based on the provided criteria and updates the table.
     * If all fields are empty, it retrieves the entire database.
     * @param metropolis    The name or partial name of the metropolis to search for.
     * @param continent     The name or partial name of the continent.
     * @param population    The population threshold as a String.
     * @param exactMatch    True if searching for an exact text match, false for partial match.
     * @param largerThan    True if searching for population greater than the value, false for smaller.
     */
    public void search(String metropolis, String continent, String population, boolean exactMatch, boolean largerThan) {
        List<Object> parameters = new ArrayList<>();
        String sql = buildSearchQuery(metropolis, continent, population, exactMatch, largerThan, parameters);
        executeSearchAndRefresh(sql, parameters);
    }

    private String buildSearchQuery(String metropolis, String continent, String population, boolean exactMatch, boolean largerThan, List<Object> parameters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM metropolises WHERE 1=1");

        appendStringCondition(sql, parameters, "metropolis", metropolis, exactMatch);
        appendStringCondition(sql, parameters, "continent", continent, exactMatch);
        appendPopulationCondition(sql, parameters, population, largerThan);

        return sql.toString();
    }

    private void appendStringCondition(StringBuilder sql, List<Object> parameters, String columnName, String value, boolean exactMatch) {
        if (value != null && !value.isEmpty()) {
            sql.append(" AND ").append(columnName).append(" LIKE ?");
            if (exactMatch) {
                parameters.add(value);
            } else {
                parameters.add("%" + value + "%");
            }
        }
    }

    private void appendPopulationCondition(StringBuilder sql, List<Object> parameters, String populationStr, boolean largerThan) {
        if (populationStr != null && !populationStr.isEmpty()) {
            try {
                long popValue = Long.parseLong(populationStr);
                if (largerThan) {
                    sql.append(" AND population > ?");
                } else {
                    sql.append(" AND population <= ?");
                }
                parameters.add(popValue);
            } catch (NumberFormatException e) {
                System.err.println("Invalid population format for Search");
            }
        }
    }

    private void executeSearchAndRefresh(String sql, List<Object> parameters) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            List<Metropolis> newData = new ArrayList<>();
            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    String m = res.getString("metropolis");
                    String c = res.getString("continent");
                    long p = res.getLong("population");

                    newData.add(new Metropolis(m, c, p));
                }
            }

            data.clear();
            data.addAll(newData);
            fireTableDataChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of rows in the model. A JTable uses this method to determine
     * how many rows it should display.
     * @return the number of rows in the model
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Returns the number of columns in the model. A JTable uses this method to determine
     * how many columns it should create and display by default.
     * @return the number of columns in the model
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the column at columnIndex. This is used to initialize
     * the table's column header name.
     * @param columnIndex the index of the column
     * @return the name of the column
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Returns the value for the cell at columnIndex and rowIndex.
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= data.size()) return null;
        Metropolis city = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return city.getMetropolis();
            case 1: return city.getContinent();
            case 2: return city.getPopulation();
            default: return null;
        }
    }
}