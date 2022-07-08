package edu.whimc.feedback.utils.sql.migration.schemas;

import edu.whimc.feedback.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_4 extends SchemaVersion {
    private static final String CREATE_DIALOG_SCIENCE =
            "CREATE TABLE IF NOT EXISTS `whimc_dialog_science` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `world`    VARCHAR(36)           NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `science_inquiry`    TEXT           NOT NULL," +
                    "  PRIMARY KEY    (`rowid`));";
    /**
     * Constructor to specify which migrations to do
     */
    public Schema_4() {
        super(4, null);
    }
    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_DIALOG_SCIENCE)) {
            statement.execute();
        } catch (Exception e){

        }

    }
}
