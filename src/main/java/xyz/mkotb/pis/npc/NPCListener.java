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
package xyz.mkotb.pis.npc;

import io.mazenmc.menuapi.MenuFactory;
import io.mazenmc.menuapi.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mkotb.pis.PrisonInSpacePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCListener implements Listener {
    private final Map<Integer, PrisonTradeNPC> npcStore = new HashMap<>();
    private PrisonInSpacePlugin plugin;

    public NPCListener() {
        plugin = PrisonInSpacePlugin.instance();
    }

    public NPCListener init() {
        plugin.data().npcs().values().forEach(this::add);
        return this;
    }

    public void add(PrisonTradeNPC npc) {
        npcStore.put(npc.spawn(), npc);
    }

    public void remove(PrisonTradeNPC npc) {
        npcStore.remove(npc.entity().getEntityId());
        npc.kill();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (npcStore.containsKey(event.getEntity().getEntityId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            event.setCancelled(true); // no villager inventories
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        PrisonTradeNPC npc = npcStore.get(event.getRightClicked().getEntityId());

        if (npc == null) {
            return;
        }

        event.setCancelled(true);
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            event.getPlayer().sendMessage(plugin.config().cannotEnchantMessage());
            return;
        }

        Map<Enchantment, Integer> applicable = new HashMap<>();
        double balance = plugin.balanceFor(event.getPlayer()); // saved in-case of DB queries for balance
        boolean canEnchantItem = false;

        for (Enchantment enchantment : PrisonInSpacePlugin.ENCHANTMENTS) {
            if (enchantment.canEnchantItem(item)) {
                int affordLevel = plugin.affordLevel(balance, enchantment);

                if (affordLevel != -1) {
                    applicable.put(enchantment, affordLevel);
                }

                canEnchantItem = true;
            }
        }

        if (!canEnchantItem) {
            event.getPlayer().sendMessage(plugin.config().cannotEnchantMessage());
            return;
        }

        if (applicable.isEmpty()) {
            event.getPlayer().sendMessage(plugin.config().insufficientMoneyEnchant());
            return;
        }

        Map<Enchantment, Integer> newApplicable = new HashMap<>();

        applicable.entrySet().stream().filter((e) -> (e.getValue() - item.getEnchantmentLevel(e.getKey())) != 0)
                .forEach((e) -> newApplicable.put(e.getKey(), e.getValue()));
        applicable = newApplicable;

        if (applicable.isEmpty()) {
            event.getPlayer().sendMessage(plugin.config().fullEnchantMessage());
            return;
        }

        int index = -1;
        int menuRows = (int) Math.ceil(applicable.size() / 9D) - 1;
        int lastRowLow = (int) Math.floor((9 - (applicable.size() - (menuRows * 9))) / 2);
        Menu menu = MenuFactory.createMenu(plugin.config().enchantmentMenuTitle(), (menuRows + 1) * 9);
        List<Map.Entry<Enchantment, Integer>> entryList = new ArrayList<>(applicable.entrySet());

        for (int y = 0; y < menuRows; y++) {
            for (int x = 0; x < 9; x++) {
                addItem(x, y, entryList.get(++index), menu, item);
            }
        }

        int sub = index;

        if (sub == -1) {
            sub = 0;
        }

        int lastRowSize = (entryList.size() - sub) + lastRowLow;

        if ((lastRowLow) != (9 - lastRowSize)) {
            lastRowLow = 0;
            lastRowSize = (entryList.size() - sub);
        }

        for (int x = lastRowLow; x < lastRowSize; x++) {
            addItem(x, menuRows, entryList.get(++index), menu, item);
        }

        Bukkit.getScheduler().runTask(PrisonInSpacePlugin.instance(), () -> menu.showTo(event.getPlayer()));
    }

    public void despawn() {
        npcStore.values().forEach(PrisonTradeNPC::kill);
    }

    private void addItem(int x, int y, Map.Entry<Enchantment, Integer> entry, Menu menu, ItemStack stack) {
        Enchantment enchantment = entry.getKey();
        int level = entry.getValue();
        int addLevel = level - stack.getEnchantmentLevel(enchantment);
        ItemStack book = plugin.config().generateBook(enchantment, level, addLevel);

        menu.setItem(x, y, MenuFactory.createItem(book, (player, clickType) ->
                processItem(player, enchantment, level, addLevel)));
    }

    private void processItem(Player player, Enchantment enchant, int level, int addLevel) {
        // charge
        int price = addLevel * plugin.config().pricePerLevel();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        plugin.economy().withdrawPlayer(player, price);

        meta.removeEnchant(enchant);
        meta.addEnchant(enchant, level, true);
        item.setItemMeta(meta);

        player.getInventory().setItemInMainHand(item);
        player.closeInventory();

        player.sendMessage(String.format(plugin.config().purchaseMessage(), plugin.config().enchantmentDisplay(enchant),
                level, price));
    }
}
