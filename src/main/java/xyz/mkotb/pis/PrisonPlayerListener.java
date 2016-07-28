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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrisonPlayerListener implements Listener {
    private Map<UUID, DrowningTask> drowningTasks = new HashMap<>();

    public PrisonPlayerListener load() {
        PrisonInSpacePlugin.instance().data().savedTasks().forEach((task) ->
                drowningTasks.put(task.playerId(), task));
        return this;
    }

    public void save() {
        PrisonInSpacePlugin.instance().data().setSavedTasks(new ArrayList<>(drowningTasks.values()));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        UUID id = event.getWhoClicked().getUniqueId();

        if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
            return;
        }

        Bukkit.getScheduler().runTask(PrisonInSpacePlugin.instance(), () -> {
            if (checkSlots(event.getWhoClicked().getInventory())) { // all armor is back, stop damaging
                if (drowningTasks.containsKey(id)) {
                    drowningTasks.get(id).cancel();
                    drowningTasks.remove(id);
                }
            } else {
                drowningTasks.put(id, new DrowningTask(id).execute());
            }
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (drowningTasks.containsKey(event.getPlayer().getUniqueId()))
            drowningTasks.get(event.getPlayer().getUniqueId()).cancel(); // stop execution of task
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();

        if (drowningTasks.containsKey(id))
            drowningTasks.get(id).execute(); // resume
        else if (!checkSlots(event.getPlayer().getInventory()))
            drowningTasks.put(id, new DrowningTask(id).execute());
    }

    public boolean checkSlots(PlayerInventory inventory) {
        return !checkSlot(inventory.getBoots()) && !checkSlot(inventory.getLeggings()) &&
                !checkSlot(inventory.getChestplate()) && !checkSlot(inventory.getHelmet());
    }

    private boolean checkSlot(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    public void removePlayer(UUID id) {
        drowningTasks.remove(id);
    }
}
