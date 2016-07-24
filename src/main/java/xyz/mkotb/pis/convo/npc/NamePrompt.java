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
import xyz.mkotb.pis.npc.PrisonTradeNPC;

public class NamePrompt extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Please send the name of this NPC";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        PrisonTradeNPC npc = new PrisonTradeNPC();

        conversationContext.setSessionData("npc", npc);
        npc.setName(s);

        ((Player) conversationContext.getForWhom()).sendMessage("Successfully set the name of this NPC!");
        return ((boolean) conversationContext.getSessionData("continue")) ? new EnchantmentPrompt() : null;
    }
}
