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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DrowningTask extends BukkitRunnable {
    private UUID playerId;

    public DrowningTask(UUID playerId) {
        this.playerId = playerId;
    }

    public DrowningTask() { // config serialization
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerId);

        if (player == null) {
            cancel();
            return; // don't ask why this would happen
        }

        player.damage(2.0); // mmm....
    }

    public DrowningTask execute() {
        long interval = PrisonInSpacePlugin.instance().config().damageInterval() * 20;
        runTaskTimer(PrisonInSpacePlugin.instance(), interval, interval);
        return this;
    }

    public UUID playerId() {
        return playerId;
    }
}
