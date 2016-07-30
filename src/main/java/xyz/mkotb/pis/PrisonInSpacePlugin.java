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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mkotb.configapi.ConfigFactory;
import xyz.mkotb.pis.convo.npc.InitialNPCPrompt;
import xyz.mkotb.pis.data.MainConfig;
import xyz.mkotb.pis.data.DataConfig;
import xyz.mkotb.pis.npc.NPCListener;

public class PrisonInSpacePlugin extends JavaPlugin {
    public static final Enchantment[] ENCHANTMENTS = {Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DIG_SPEED, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_INFINITE, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.DAMAGE_ALL};
    private static PrisonInSpacePlugin instance;
    private Economy economy;
    private ConversationFactory npcFactory;
    private ConfigFactory configFactory = ConfigFactory.newFactory(this);
    private NPCListener npcListener;
    private PrisonPlayerListener playerListener;
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
                .withFirstPrompt(InitialNPCPrompt.INSTANCE)
                .withEscapeSequence("exit");
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
        npcListener = new NPCListener().init();
        playerListener = new PrisonPlayerListener().load();

        Bukkit.getPluginManager().registerEvents(npcListener, this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
    }

    @Override
    public void onDisable() {
        npcListener.despawn();
        playerListener.save();
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

    public NPCListener npcListener() {
        return npcListener;
    }

    public PrisonPlayerListener playerListener() {
        return playerListener;
    }

    public boolean isInSpawn(Location location) {
        return WGBukkit.getRegionManager(location.getWorld())
                .getApplicableRegionsIDs(new Vector(location.getX(), location.getY(), location.getZ()))
                .contains("spawn");
    }

    public double balanceFor(Player player) {
        return economy.getBalance(player);
    }

    public int affordLevel(double balanceD, Enchantment enchantment) {
        int balance = (int) balanceD;

        if (balance < config().pricePerLevel()) {
            return -1;
        }

        return Math.min((int) Math.floor(balance / config().pricePerLevel()), enchantment.getMaxLevel());
    }

    public Economy economy() {
        return economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("pisnpc".equalsIgnoreCase(command.getName())) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("no");
                return false;
            }

            if (!sender.hasPermission("pis.npc")) {
                sender.sendMessage("You do not have enough permissions to use this command!");
                return false;
            }

            npcFactory.buildConversation((Player) sender).begin();
            return true;
        }
        return false;
    }
}
