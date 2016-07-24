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

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrisonPlayerListener implements Listener {
    private Map<UUID, DrowningTask> drowningTasks = new HashMap<>();

    public void load() {
        PrisonInSpacePlugin.instance().data().savedTasks().forEach((task) ->
                drowningTasks.put(task.playerId(), task));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        UUID id = event.getWhoClicked().getUniqueId();

        if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
            return;
        }

        if (checkSlot(event.getInventory(), event.getSlot())) {
            if (drowningTasks.containsKey(id)) {
                if (checkSlots(event.getInventory())) { // all armor is back, stop damaging
                    drowningTasks.get(id).cancel();
                    drowningTasks.remove(id);
                }
            }

            return;
        }

        drowningTasks.put(id, new DrowningTask(id).execute());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (drowningTasks.containsKey(event.getPlayer().getUniqueId()))
            drowningTasks.get(event.getPlayer().getUniqueId()).cancel(); // stop execution of task
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (drowningTasks.containsKey(event.getPlayer().getUniqueId()))
            drowningTasks.get(event.getPlayer().getUniqueId()).execute(); // resume
    }

    public boolean checkSlots(Inventory inventory) {
        return checkSlot(inventory, 100) && checkSlot(inventory, 101) &&
                checkSlot(inventory, 102) && checkSlot(inventory, 103);
    }

    private boolean checkSlot(Inventory inventory, int slot) {
        ItemStack stack = inventory.getItem(slot);
        return stack != null && stack.getType() != Material.AIR;
    }
}
