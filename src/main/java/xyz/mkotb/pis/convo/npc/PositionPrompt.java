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
package xyz.mkotb.pis.convo.npc;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import xyz.mkotb.pis.PrisonInSpacePlugin;
import xyz.mkotb.pis.npc.PrisonTradeNPC;

public class PositionPrompt extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Please stand in the position in-which you want your NPC, and then send \"yes\" to set it";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        if (!(conversationContext.getForWhom() instanceof Player)) {
            throw new UnsupportedOperationException("Cannot use a position prompt on a non-player!");
        }

        PrisonTradeNPC npc = (PrisonTradeNPC) conversationContext.getSessionData("npc");
        npc.setLocation(((Player) conversationContext.getForWhom()).getLocation());

        ((Player) conversationContext.getForWhom()).sendMessage("Successfully set the npc location!");

        if (((boolean) conversationContext.getSessionData("continue"))) {
            PrisonInSpacePlugin.instance().npcListener().add(npc);
            PrisonInSpacePlugin.instance().data().npcs().put(npc.id(), npc);
        } else {
            npc.updateEntity();
        }

        return null;
    }
}
