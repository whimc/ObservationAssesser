package edu.whimc.ObservationAssesser.utils.sql.migration;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class for schema version for SQL table (currently unused)
 */
public abstract class SchemaVersion {

    private final int version;
    private final SchemaVersion nextSchema;

    /**
     * Constructor to instantiate instance variables
     * @param version schema version
     * @param next the next shcema to migrate
     */
    protected SchemaVersion(int version, SchemaVersion next) {
        this.version = version;
        this.nextSchema = next;
    }

    /**
     * Gets schema version
     * @return current schema veersion
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * Gets next schema to migrate
     * @return next schema to migrate
     */
    public SchemaVersion getNextSchema() {
        return this.nextSchema;
    }

    /**
     * Abstract method to migrate schema to be implemented in schemas
     * @param connection SQL connection
     * @throws SQLException
     */
    protected abstract void migrateRoutine(Connection connection) throws SQLException;

    /**
     * Migrates schema
     * @param manager the schemamanager instance being used
     * @return boolean for migration status
     */
    public final boolean migrate(SchemaManager manager) {
        // Migrate the database
        try {
            migrateRoutine(manager.getConnection());
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        }


        return true;
    }

}
