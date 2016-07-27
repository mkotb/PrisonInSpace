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
package xyz.mkotb.pis.data;

import xyz.mkotb.configapi.comment.HeaderComments;
import xyz.mkotb.pis.DrowningTask;
import xyz.mkotb.pis.npc.PrisonTradeNPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HeaderComments({"This is a data file",
        "Unexpected behaviour may be encountered if modified manually"})
public class DataConfig {
    private Map<String, PrisonTradeNPC> npcs = new HashMap<>();
    private List<DrowningTask> savedTasks = new ArrayList<>();

    public List<DrowningTask> savedTasks() {
        return savedTasks;
    }

    public Map<String, PrisonTradeNPC> npcs() {
        return npcs;
    }

    public void setSavedTasks(List<DrowningTask> savedTasks) {
        this.savedTasks = savedTasks;
    }
}
