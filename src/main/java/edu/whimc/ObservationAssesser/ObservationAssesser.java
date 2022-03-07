package edu.whimc.ObservationAssesser;

import edu.whimc.ObservationAssesser.commands.AssessmentCommand;
import edu.whimc.ObservationAssesser.utils.Utils;
import edu.whimc.ObservationAssesser.utils.sql.Queryer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;



/**
 * Class to create plugin and enable it in MC
 * @author sam
 */
public class ObservationAssesser extends JavaPlugin implements Listener {
    private static ObservationAssesser instance;
    private Queryer queryer;
    /**
     * Method to return instance of plugin
     * @return instance of ObservationAssesser plugin
     */
    public static ObservationAssesser getInstance() {
        return instance;
    }

    /**
     * Method to enable plugin
     */
    @Override
    public void onEnable() {
        ObservationAssesser.instance = this;
        this.queryer = new Queryer(this, q -> {
            // If we couldn't connect to the database disable the plugin
            if (q == null) {
                this.getLogger().severe("Could not establish MySQL connection! Disabling plugin...");
                getCommand("observationassesser").setExecutor(this);
                return;
            }
            });
        getCommand("observationassesser").setExecutor(new AssessmentCommand(instance));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

    }
    public Queryer getQueryer() {
        return this.queryer;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Utils.msg(sender, "&cThis plugin is disabled because it was unable to connect to the configured database. " +
                "Please modify the config to ensure the credentials are correct then restart the server.");
        return true;
    }
}
