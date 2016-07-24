/*
 * Copyright (c) 2016, Mazen Kotb, mazenkotb@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package xyz.mkotb.pis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mkotb.configapi.ConfigFactory;
import xyz.mkotb.pis.convo.npc.NPCModificationType;
import xyz.mkotb.pis.data.MainConfig;
import xyz.mkotb.pis.data.DataConfig;

import java.util.HashMap;

public class PrisonInSpacePlugin extends JavaPlugin {
    private static PrisonInSpacePlugin instance;
    private ConversationFactory npcFactory;
    private ConfigFactory configFactory = ConfigFactory.newFactory(this);
    private MainConfig config;
    private DataConfig data;


    public static PrisonInSpacePlugin instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = configFactory.fromFile("config", MainConfig.class);
        data = configFactory.fromFile("data", DataConfig.class);
        npcFactory = new ConversationFactory(this)
                .withFirstPrompt(NPCModificationType.NAME.prompt())
                .withInitialSessionData(new HashMap<Object, Object>() {{put("continue", true);}});
    }

    @Override
    public void onDisable() {
        instance = null;
        npcFactory = null;
        configFactory.save("config", config);
        configFactory.save("data", data);
        configFactory = null;
    }

    public MainConfig config() {
        return config;
    }

    public DataConfig data() {
        return data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("npc".equalsIgnoreCase(command.getName())) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("no");
                return false;
            }

            if (!sender.hasPermission("pis.npc")) {
                sender.sendMessage("You do not have enough permissions to use this command!");
                return false;
            }

            if (args.length == 0) {
                npcFactory.buildConversation((Player) sender).begin();
                return true;
            }

            NPCModificationType mod;

            try {
                mod = NPCModificationType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(args[0] + " is not a valid modification type; use one of the following: name, position, enchantment");
                return false;
            }

            new ConversationFactory(this)
                    .withInitialSessionData(new HashMap<Object, Object>() {{put("continue", false);}})
                    .withFirstPrompt(mod.prompt())
                    .buildConversation((Player) sender)
                    .begin();
        }
        return false;
    }
}
