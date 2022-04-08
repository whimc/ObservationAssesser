package edu.whimc.feedback.utils.sql.migration.schemas;

import edu.whimc.feedback.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Schema to create skills table in db
 */
public class Schema_1 extends SchemaVersion {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_skills` (" +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `analogy`           DOUBLE                NOT NULL," +
                    "  `comparative`           DOUBLE                NOT NULL," +
                    "  `descriptive`           DOUBLE                NOT NULL," +
                    "  `inference`           DOUBLE                NOT NULL," +
                    "  PRIMARY KEY    (`uuid`));";

    /**
     * Constructor to specify which migrations to do
     */
    public Schema_1() {
        super(1, new Schema_2());
    }

    /**
     * Method to migrate SQL
     * @param connection SQL connection
     */
    @Override
    protected void migrateRoutine(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        } catch (Exception e){

        }
    }


}
