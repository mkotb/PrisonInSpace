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

public class InitialNPCPrompt extends ValidatingPrompt {
    public static final InitialNPCPrompt INSTANCE = new InitialNPCPrompt();

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return "modify".equalsIgnoreCase(s) || "add".equalsIgnoreCase(s);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        switch (s.toLowerCase()) {
            case "modify":
                conversationContext.setSessionData("continue", false);
                return IDPrompt.INSTANCE;
            default:
                conversationContext.setSessionData("continue", true);
                return NPCModificationType.NAME.prompt();
        }
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return null;
    }
}
