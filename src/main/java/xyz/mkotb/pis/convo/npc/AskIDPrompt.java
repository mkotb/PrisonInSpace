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
import org.bukkit.conversations.ValidatingPrompt;
import xyz.mkotb.pis.PrisonInSpacePlugin;

public class AskIDPrompt extends ValidatingPrompt {
    public static final AskIDPrompt INSTANCE = new AskIDPrompt();

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return PrisonInSpacePlugin.instance().data().npcs().containsKey(s) || "back".equalsIgnoreCase(s);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        if ("back".equalsIgnoreCase(s)) {
            return InitialNPCPrompt.INSTANCE;
        }

        conversationContext.setSessionData("npc", PrisonInSpacePlugin.instance().data().npcs().get(s));
        return ModifyOptionsPrompt.INSTANCE;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Please send the name of the NPC you want to modify (if you wish to go back, send 'back', to exit, send 'exit')!";
    }
}
