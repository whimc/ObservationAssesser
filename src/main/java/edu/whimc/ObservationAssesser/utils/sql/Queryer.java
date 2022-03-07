package edu.whimc.ObservationAssesser.utils.sql;

import edu.whimc.ObservationAssesser.ObservationAssesser;

import edu.whimc.ObservationAssesser.bkt.Skills;
import edu.whimc.ObservationAssesser.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles storing position data
 *
 * @author Jack Henhapl
 */
public class Queryer {


    //Query for inserting skills into the database.
    private static final String QUERY_SAVE_SKILLS =
            "INSERT INTO whimc_skills " +
                    "(uuid, username, analogy, comparative, descriptive, inference) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    //Query for updating skills in the database.
    private static final String QUERY_UPDATE_SKILLS =
            "UPDATE whimc_skills " +
                    "SET analogy = ?, " + "comparative = ?, " + "descriptive = ?, " + "inference = ? " +
                    "WHERE uuid=?";

    //Query for getting skills from the database.
    private static final String QUERY_GET_PLAYER_SKILLS =
            "SELECT * FROM whimc_skills "+
            "WHERE uuid=?;";



    private final ObservationAssesser plugin;
    private final MySQLConnection sqlConnection;

    /**
     * Constructor to instantiate instance variables and connect to SQL
     * @param plugin ObservationAssesser plugin instance
     * @param callback callback to signal that process completed
     */
    public Queryer(ObservationAssesser plugin, Consumer<Queryer> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean success = sqlConnection.initialize();
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(success ? this : null));
        });
    }

    /**
     * Generated a PreparedStatement for saving a new set of skills for a player.
     * @param connection MySQL Connection
     * @param player     Player to save skills
     * @param skills     SKills to give to the player
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertNewSkills(Connection connection, Player player, List<Double> skills) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_SKILLS);
        statement.setString(1, player.getUniqueId().toString());
        statement.setString(2, player.getName());
        statement.setDouble(3, skills.get(0));
        statement.setDouble(4, skills.get(1));
        statement.setDouble(5, skills.get(2));
        statement.setDouble(6, skills.get(3));
        return statement;
    }

    /**
     * Generated a PreparedStatement for updating a set of skills for a player.
     * @param connection MySQL Connection
     * @param player     Player to update skills
     * @param skills     SKills to give to the player
     * @return PreparedStatement
     * @throws SQLException
     */
    public PreparedStatement updatePlayerSkills(Connection connection, Player player, List<Double> skills) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE_SKILLS);
        statement.setDouble(1, skills.get(0));
        statement.setDouble(2, skills.get(1));
        statement.setDouble(3, skills.get(2));
        statement.setDouble(4, skills.get(3));
        statement.setString(5, player.getUniqueId().toString());
        return statement;
    }

    /**
     * Updates the player's skill based on correctness of observation
     * @param player     Player to update skills
     * @param type     Skill type
     * @param correct     Assessment of the skill application
     * @param callback    Callback to signify process completion
     * @throws SQLException
     */
    public void updateSkills(Player player, String type, int correct, Consumer callback){
        getSkills(player, previousSkills -> {
            async(() -> {

                List<Double> skills = (List<Double>) previousSkills;

                try (Connection connection = this.sqlConnection.getConnection()) {
                    if (skills.size() == 0) {
                        for(int k = 0; k < 5; k++) {
                            skills.add(0.0);
                        }
                        try (PreparedStatement insertStatement = insertNewSkills(connection, player, skills)) {
                            String query = insertStatement.toString().substring(insertStatement.toString().indexOf(" ") + 1);
                            Utils.debug("  " + query);
                            insertStatement.executeUpdate();
                        }
                    }

                    if( correct != -1) {
                        skills = Skills.updateSkills(skills, type, correct);
                    }
                    try (PreparedStatement statement = updatePlayerSkills(connection, player, skills)) {
                        String query = statement.toString().substring(statement.toString().indexOf(" ") + 1);
                        Utils.debug("  " + query);
                        statement.executeUpdate();
                    }
                    sync(callback,skills);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Method to get skills for a player
     * @param player Player to get the skills for
     * @param callback callback to signify process completion
     */
    public void getSkills(Player player, Consumer callback){
        List<Double> skills = new ArrayList<>();
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_GET_PLAYER_SKILLS)) {
                    statement.setString(1, player.getUniqueId().toString());
                    ResultSet results = statement.executeQuery();
                    while (results.next()) {
                        skills.add(results.getDouble("analogy"));
                        skills.add(results.getDouble("comparative"));
                        skills.add(results.getDouble("descriptive"));
                        skills.add(results.getDouble("inference"));
                    }
                    sync(callback,skills);
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    private <T> void sync(Consumer<T> cons, T val) {
        Bukkit.getScheduler().runTask(this.plugin, () -> cons.accept(val));
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }


}
