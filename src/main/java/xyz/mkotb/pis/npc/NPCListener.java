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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import xyz.mkotb.pis.PrisonInSpacePlugin;

import java.util.HashMap;
import java.util.Map;

public class NPCListener implements Listener {
    private final Map<Integer, PrisonTradeNPC> npcStore = new HashMap<>();

    public void init() {
        PrisonInSpacePlugin.instance().data().npcs().forEach((e) -> npcStore.put(e.spawn(), e));
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        PrisonTradeNPC npc = npcStore.get(event.getRightClicked().getEntityId());

        if (npc == null) {
            return;
        }

        // todo open menu
        event.setCancelled(true);
    }
}
