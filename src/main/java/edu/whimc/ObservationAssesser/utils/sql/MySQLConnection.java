package edu.whimc.ObservationAssesser.utils.sql;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.utils.Utils;
import edu.whimc.ObservationAssesser.utils.sql.migration.SchemaManager;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for SQL connection
 */
public class MySQLConnection {

    public static final String URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String url;
    private final int port;

    private final ObservationAssesser plugin;

    /**
     * Constructor to set SQL info from config
     * @param plugin ObservationAssesser plugin instance
     */
    public MySQLConnection(ObservationAssesser plugin) {

        this.host = plugin.getConfig().getString("mysql.host", "localhost");
        this.port = plugin.getConfig().getInt("mysql.port", 3306);
        this.database = plugin.getConfig().getString("mysql.database", "minecraft");
        this.username = plugin.getConfig().getString("mysql.username", "user");
        this.password = plugin.getConfig().getString("mysql.password", "pass");


        this.url = String.format(URL_TEMPLATE, this.host, this.port, this.database);
        this.plugin = plugin;
    }

    /**
     * Initializes SQLConnection with schemamanager
     * @return boolean for sqlconnection status
     */
    public boolean initialize() {
        if (getConnection() == null) {
            return false;
        }

        SchemaManager manager = new SchemaManager(this.plugin, this.connection);
        return manager.initialize();
    }

    /**
     * Get SQL connection
     * @return SQL connection
     */
    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
            return null;
        }

        return this.connection;
    }

}
