package edu.whimc.ObservationAssesser.assessments;

import edu.whimc.ObservationAssesser.ObservationAssesser;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExplorationAssessment extends ProgressAssessment{
    private ObservationAssesser plugin;
    public ExplorationAssessment(Player player, Long sessionStart, Object resultSet, ObservationAssesser plugin) {
        super(player, sessionStart, resultSet);
        this.plugin = plugin;
    }

    @Override
    public double metric() {
        HashMap<String, ArrayList<Point>> positions = (HashMap<String, ArrayList<Point>>) this.getResultSet();
        double[] scores = new double[positions.keySet().size()];
        int ctr = 0;
        for(Map.Entry<String, ArrayList<Point>> entry : positions.entrySet()) {
            int score = 0;
            String world = entry.getKey();
            ArrayList<Point> points = entry.getValue();
            int pixelRatio = plugin.getConfig().getInt("worlds."+world+".pixel_to_block_ratio");
            int min_x = plugin.getConfig().getInt("worlds."+world+".top_left_coordinate_x");
            int min_z = plugin.getConfig().getInt("worlds."+world+".top_left_coordinate_z");
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(System.getProperty("user.dir")+"/plugins/maps/"+world+".png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int max_x = min_x + img.getWidth() / pixelRatio;
            int max_z = min_z + img.getHeight() / pixelRatio;
            for(int k = 0; k < points.size(); k++){
                double x = points.get(k).getX();
                double z = points.get(k).getX();
                if(!is_inside_view(min_x,min_z,max_x,max_z,x,z)){
                    continue;
                }
                int row = (int) scale(x, new int[]{min_x, max_x}, new int[]{0, 10});
                int col = (int) scale(z, new int[]{min_z, max_z}, new int[]{0, 10});
                score += row + col;
            }
            scores[ctr] = score;
            ctr++;
        }
        int total = 0;
        for(int k = 0; k < scores.length; k++){
            total += scores[k];
        }
        return total/scores.length;
    }

    /**
     * Determines if this coordinate is inside of the World's view
     * @param min_x
     * @param min_z
     * @param max_x
     * @param max_z
     * @param x
     * @param z
     * @return
     */
    public boolean is_inside_view(int min_x, int min_z, int max_x, int max_z, double x, double z){
        return (min_x < x && x < max_x) && (min_z < z && z < max_z);
    }

    /**
     * Scale the given value from the scale of src to the scale of dst
     * @param val
     * @param src
     * @param dst
     * @return
     */
    public double scale(double val, int[]src, int[]dst){
        return ((val - src[0]) / (src[1]-src[0])) * (dst[1]-dst[0]) + dst[0];
    }

    @Override
    public String getName() {
        return "Exploration Assessment";
    }
}
