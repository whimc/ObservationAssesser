package edu.whimc.feedback.dialoguetemplate.gui;

import edu.whimc.feedback.StudentFeedback;

import edu.whimc.feedback.assessments.ExplorationAssessment;
import edu.whimc.feedback.assessments.ObservationAssessment;
import edu.whimc.feedback.assessments.QuestAssessment;
import edu.whimc.feedback.assessments.ScienceToolsAssessment;
import edu.whimc.feedback.dialoguetemplate.CenteredText;
import edu.whimc.feedback.dialoguetemplate.SpigotCallback;
import edu.whimc.feedback.dialoguetemplate.models.DialoguePrompt;
import edu.whimc.feedback.dialoguetemplate.models.DialogueTemplate;
import edu.whimc.feedback.dialoguetemplate.models.DialogueType;
import edu.whimc.feedback.utils.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Class to handle flow of dialogue
 */
public class TemplateSelection implements Listener {

    /* Unicode for check character */
    private static final String CHECK = "\u2714";
    /* Unicode for cross (X) character */
    private static final String CROSS = "\u274C";
    /* Unicode for bullet character */
    private static final String BULLET = "\u2022";

    /* Selections that are currently happening */
    private static final Map<UUID, TemplateSelection> ongoingSelections = new HashMap<>();

    /* Instance of main class */
    private final StudentFeedback plugin;
    /* Used to create clickable messages with callbacks */
    private final SpigotCallback spigotCallback;
    /* The selected template to fill out */
    private final DialogueTemplate template;
    /* The UUID of the player making the selection */
    private final UUID uuid;
    /* Default science prompt if student input doesn't match */
    private static final String DEFAULT_SCIENCE = "default";
    /* The selected prompt from the template */
    private DialoguePrompt prompt = null;
    /* The stage of the selection */
    private TemplateSelectionStage stage;
    private String feedback;
    private final long interactionStart;
    private int ovr_obs;
    private int quests;
    private int session_obs;
    private int sci_tools;
    private int exp_metric;
    private int sci_topics;

    public TemplateSelection(StudentFeedback plugin, SpigotCallback spigotCallback, Player player, DialogueTemplate template) {
        UUID uuid = player.getUniqueId();
        this.plugin = plugin;
        if (ongoingSelections.containsKey(uuid)) {
            this.plugin.getQueryer().storeNewInteraction(ongoingSelections.get(uuid), id -> {
                ongoingSelections.get(uuid).destroySelection();
                Utils.msg(player, "Lets start a new conversation!");
            });
        }


        this.spigotCallback = spigotCallback;
        this.template = template;
        this.uuid = uuid;
        this.interactionStart = System.currentTimeMillis();
        this.ovr_obs = 0;
        this.quests = 0;
        this.session_obs = 0;
        this.sci_tools = 0;
        this.exp_metric = 0;
        this.sci_topics = 0;

        // Register this class as a listener to cancel clickables if they change worlds
        Bukkit.getPluginManager().registerEvents(this, plugin);

        ongoingSelections.put(uuid, this);
        doStage(TemplateSelectionStage.SELECT_PROMPT);
    }

    private void doStage(TemplateSelectionStage nextStage) {
        // Clear all other callbacks before entering the next stage
        this.spigotCallback.clearCallbacks(getPlayer());
        this.stage = nextStage;

        switch (nextStage) {
            case SELECT_PROMPT:
                doSelectPrompt();
                return;
            case SELECT_RESPONSE:
                doSelectResponse();
                return;
            case CONFIRM:
                doConfirm();
        }
    }

    private void doSelectPrompt() {
        Player player = getPlayer();

        sendHeader();

        if (this.template.getType().equals(DialogueType.SCIENCE)) {
            Utils.msgNoPrefix(player, "&lWhat unique science topic on this planet do you want to discuss?", "");
            // generative knowledge custom response
            String signHeader = this.plugin.getConfig().getString("template-gui.text.custom-response-sign-header", "&f&nYour response");
            String customResponse = this.plugin.getConfig().getString("template-gui.text.write-your-own-response", "Write your own response");

            sendComponent(
                    player,
                    "&8" + BULLET + template.getColor() + " " + customResponse,
                    "&aClick here to write your own response!",
                    p -> this.plugin.getSignMenuFactory()
                            .newMenu(Collections.singletonList(Utils.color(signHeader)))
                            .reopenIfFail(true)
                            .response((signPlayer, strings) -> {
                                String response = StringUtils.join(Arrays.copyOfRange(strings, 0, strings.length), ' ').trim();
                                response = response.toLowerCase();
                                //To ensure default is properly displayed if chat again
                                this.prompt = null;
                                if (response.isEmpty()) {
                                    return false;
                                }
                                for(int k = 0; k < this.template.getPrompts().size(); k++){
                                    DialoguePrompt currPrompt = this.template.getPrompts().get(k);
                                    if(currPrompt.getResponses(player.getWorld(),"feedback") != null) {
                                        if (currPrompt.getPrompt().contains(",")) {
                                            String[] validPrompts = currPrompt.getPrompt().split(", ");
                                            for (int j = 0; j < validPrompts.length; j++) {
                                                if (response.contains(validPrompts[j])) {
                                                    this.prompt = currPrompt;
                                                    break;
                                                }
                                            }
                                        } else {
                                            if (response.contains(currPrompt.getPrompt())) {
                                                this.prompt = currPrompt;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if(this.prompt == null){
                                    for(int k = 0; k < this.template.getPrompts().size(); k++){
                                        DialoguePrompt currPrompt = this.template.getPrompts().get(k);
                                        if(DEFAULT_SCIENCE.equalsIgnoreCase(currPrompt.getPrompt())){
                                            this.prompt = currPrompt;
                                            break;
                                        }
                                    }
                                }
                                doStage(TemplateSelectionStage.SELECT_RESPONSE);
                                return true;
                            })
                            .open(p)
            );

        } else {
            Utils.msgNoPrefix(player, "&lWhat statistic do you want to see?", "");
            for (DialoguePrompt curPrompt : this.template.getPrompts()) {
                sendComponent(
                        player,
                        "&8" + BULLET + " &r" + curPrompt.getPrompt(),
                        "&aClick here to select \"&r" + curPrompt.getPrompt() + "&a\"",
                        p -> {
                            this.prompt = curPrompt;
                            doStage(TemplateSelectionStage.SELECT_RESPONSE);
                        });
            }
        }

            sendFooter(false, p -> {
                this.plugin.getQueryer().storeNewInteraction(this, id -> {
                    destroySelection();
                    this.plugin.getTemplateManager().getGui().openTemplateInventory(player);
                });
            });
    }

    private void doSelectResponse() {
        Player player = getPlayer();
        this.feedback = this.prompt.getResponses(player.getWorld(), "feedback");
        HashMap<Player,Long> sessions = plugin.getPlayerSessions();
        Long sessionStart = sessions.get(player);

        if(this.template.getType().equals(DialogueType.OVERALL)){
            if(this.prompt.getPrompt().equalsIgnoreCase("Observation Skills")){
                ovr_obs++;
                plugin.getQueryer().getSkills(player, currentSkills -> {
                    feedback = Utils.getOpenLearnerModel(player, (List<Double>) currentSkills);
                    doStage(TemplateSelectionStage.CONFIRM);
                });
            } else if (this.prompt.getPrompt().equalsIgnoreCase("Quests")){
                quests++;
                plugin.getQueryer().getQuestsCompleted(player, quests -> {
                QuestAssessment quest = new QuestAssessment(player, sessionStart, quests);
                Double metric = new Double(quest.metric());
                feedback = replaceFirst(this.feedback, DialoguePrompt.FILLIN, metric.toString());
                doStage(TemplateSelectionStage.CONFIRM);
                });
            }
        } else if (this.template.getType().equals(DialogueType.SESSION)){
            if(this.prompt.getPrompt().equalsIgnoreCase("Observations")){
                session_obs++;
                plugin.getQueryer().getSessionObservations(player, sessionStart, observations -> {
                    ObservationAssessment obs = new ObservationAssessment(player, sessionStart, observations);
                    Double metric = new Double(obs.metric());
                    feedback = replaceFirst(this.feedback, DialoguePrompt.FILLIN, metric.toString());
                    doStage(TemplateSelectionStage.CONFIRM);
                });
            } else if (this.prompt.getPrompt().equalsIgnoreCase("Science Tools")) {
                sci_tools++;
                plugin.getQueryer().getSessionScienceTools(player, sessionStart, tools -> {
                    ScienceToolsAssessment sci = new ScienceToolsAssessment(player, sessionStart, tools);
                    Double metric = new Double(sci.metric());
                    feedback = replaceFirst(this.feedback, DialoguePrompt.FILLIN, metric.toString());
                    doStage(TemplateSelectionStage.CONFIRM);
                });
            } else if (this.prompt.getPrompt().equalsIgnoreCase("Exploration")) {
                exp_metric++;
                plugin.getQueryer().getSessionPositions(player, sessionStart, pos -> {
                    ExplorationAssessment exp = new ExplorationAssessment(player, sessionStart, pos, plugin);
                    Double metric = new Double(exp.metric());
                    feedback = replaceFirst(this.feedback, DialoguePrompt.FILLIN, metric.toString());
                    doStage(TemplateSelectionStage.CONFIRM);
                });
            }
        } else if (this.template.getType().equals(DialogueType.SCIENCE)){
            sci_topics++;
            doStage(TemplateSelectionStage.CONFIRM);
        }

    }

    private void doConfirm() {
        Player player = getPlayer();

        sendHeader();

        Utils.msgNoPrefix(player, feedback,
                "&f&lAre you done chatting?");

        sendFooter(true, p -> {
            doStage(TemplateSelectionStage.SELECT_PROMPT);
        });
    }


    private String replaceFirst(String str, String pattern, String replacement) {
        return str.replaceFirst(Pattern.quote(pattern), replacement);
    }

    private void addCallback(TextComponent component, UUID playerUUID, Consumer<Player> onClick) {
        this.spigotCallback.createCommand(playerUUID, component, onClick);
    }

    private TextComponent createComponent(String text, String hoverText, Consumer<Player> onClick) {
        TextComponent message = new TextComponent(Utils.color(text));
        message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(Utils.color(hoverText)).create()));
        addCallback(message, this.uuid, onClick);
        return message;
    }

    private void sendComponent(Player player, String text, String hoverText, Consumer<Player> onClick) {
        player.spigot().sendMessage(createComponent(text, hoverText, onClick));
    }

    private void sendHeader() {
        Player player = getPlayer();
        String header = "&r " + this.template.getGuiItemName() + " ";
        CenteredText.sendCenteredMessage(player, header, "&7&m &r");
    }

    private void sendFooter(boolean withConfirm, Consumer<Player> goBackCallback) {
        Player player = getPlayer();
        ComponentBuilder builder = new ComponentBuilder("");

        if (withConfirm) {
            Consumer<Player> confirmCallback = p -> {
                Utils.msgNoPrefix(player,
                        "&f&lThanks for talking, go explore!");
                this.plugin.getQueryer().storeNewInteraction(this, id -> {
                    destroySelection();
            });
            };

            builder.append(createComponent(
                            "&a&l" + CHECK + " Done",
                            "&aClick to finish our dialogue!",
                            confirmCallback))
                    .append("  ");
        }

        Consumer<Player> cancelCallback = p -> {
            Utils.msgNoPrefix(player,
                    "&f&lDialogue cancelled, have fun exploring!");
            this.plugin.getQueryer().storeNewInteraction(this, id -> {
                destroySelection();
            });
        };

        builder.append(createComponent(
                "&c&l" + CROSS + " Cancel",
                "&cClick to cancel our dialogue",
                cancelCallback));

        if (goBackCallback != null) {
            builder.append("  ").append(createComponent(
                    "&e&l< Change Topic",
                    "&eClick to go back to the previous panel",
                    goBackCallback));
        }

        Utils.msgNoPrefix(player, "");
        player.spigot().sendMessage(builder.create());
        CenteredText.sendCenteredMessage(player, " &7Click to make a selection ", "&7&m &r");
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
    public long getInteractionStart(){return interactionStart;}
    public int[] getInteraction(){
        return new int[]{ovr_obs, quests, session_obs, sci_tools, exp_metric, sci_topics};
    }
    private void destroySelection() {
        // Clear callbacks
        this.spigotCallback.clearCallbacks(getPlayer());

        // Remove this as an ongoing selection
        ongoingSelections.remove(this.uuid);

        // Unregister events
        PlayerChangedWorldEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!this.uuid.equals(player.getUniqueId())) {
            return;
        }

        Utils.msg(player, "Our dialogue has been canceled because you changed worlds!");
        destroySelection();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (this.uuid.equals(event.getPlayer().getUniqueId())) {
            destroySelection();
        }
    }

}
