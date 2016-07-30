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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import xyz.mkotb.configapi.internal.InternalsHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PrisonTradeNPC {
    private transient static final String NMS_PREFIX = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage()
            .getName().replace(".", ",").split(",")[3] + ".";
    private String id;
    private String name;
    private Location location;
    private transient Villager entity;

    public int spawn() {
        Villager villager = location.getWorld().spawn(location, Villager.class);

        villager.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        villager.setCustomNameVisible(true);

        this.entity = villager;

        try {
            Object handle = villager.getClass().getMethod("getHandle").invoke(villager);
            Class<?> methodProfilerCls = Class.forName(NMS_PREFIX + "MethodProfiler");
            Constructor goalSelectorConstructor = Class.forName(NMS_PREFIX + "PathfinderGoalSelector")
                    .getConstructor(methodProfilerCls);
            Field goalSelectorField = handle.getClass().getField("goalSelector");
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            modifiersField.setAccessible(true);
            InternalsHelper.setField("modifiers", goalSelectorField, goalSelectorField.getModifiers()
                    & ~Modifier.FINAL); // remove final field
            InternalsHelper.setField(goalSelectorField, handle, goalSelectorConstructor.newInstance(InternalsHelper.newInstance(methodProfilerCls)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return villager.getEntityId();
    }

    public void kill() {
        if (entity != null) {
            entity.remove(); // bye lol
        }
    }

    public void updateEntity() {
        if (entity != null) {
            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
            entity.teleport(location);
        }
    }

    public Villager entity() {
        return entity;
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location location() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
