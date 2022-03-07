package edu.whimc.ObservationAssesser.utils.sql.migration;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import edu.whimc.ObservationAssesser.utils.sql.migration.schemas.Schema_1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;

/**
 * Class to handle schema migrations
 */
public class SchemaManager {

    public static final String VERSION_FILE_NAME = ".schema_version";

    private static final SchemaVersion BASE_SCHEMA = new Schema_1();

    private final ObservationAssesser plugin;
    private final Connection connection;

    /**
     * COnstructor to instantiate instance variables
     * @param plugin ObservationAssesser plugin
     * @param connection SQL connection
     */
    public SchemaManager(ObservationAssesser plugin, Connection connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    /**
     * Gets SQL connection
     * @return SQL connection
     */
    protected Connection getConnection() {
        return this.connection;
    }

    /**
     * Gets File of schema version (currently) not used
     * @return File with current schema version
     */
    protected File getVersionFile() {
        return new File(this.plugin.getDataFolder(), VERSION_FILE_NAME);
    }

    /**
     * Gets schema version (currently) not used
     * @return current schema version
     */
    private int getCurrentVersion() {
        try {
            return Integer.parseInt(new String(Files.readAllBytes(getVersionFile().toPath())));
        } catch (NumberFormatException | IOException exc) {
            return 0;
        }
    }

    /**
     * Method to handle running migrations
     * @return boolean for migration status
     */
    public boolean initialize() {
        int curVersion = getCurrentVersion();

        SchemaVersion schema = BASE_SCHEMA;
        while (schema != null) {
            if (schema.getVersion() > curVersion) {
                this.plugin.getLogger().info("Migrating to schema " + schema.getVersion() + "...");
                if (!schema.migrate(this)) {
                    return false;
                }
            }
            schema = schema.getNextSchema();
        }

        return true;
    }

}
