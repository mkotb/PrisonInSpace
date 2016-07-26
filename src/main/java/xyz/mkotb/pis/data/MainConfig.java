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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import xyz.mkotb.configapi.Coloured;
import xyz.mkotb.configapi.comment.Comment;
import xyz.mkotb.configapi.comment.Comments;

public class MainConfig {
    @Comments({"How often damage is applied to the player",
            "when they are without armour (in seconds)"})
    private int damageInterval = 1;
    @Coloured
    @Comments({"The added lore message to enchanted books in the menu",
            "%s is the enchantment name, %d is the next level"})
    private String enchantmentLore = "&3%s: &6+%d";
    @Coloured
    @Comments({"The display name of the enchantment, applied to the enchanted book",
            "%s is the enchantment name"})
    private String enchantmentName  = "&6%s";
    @Coloured
    @Comment("The title of the enchantment menu")
    private String enchantmentMenuTitle = "&3Enchantments";
    @Coloured
    @Comment("The message sent when a player does not have enough money for any enchantments")
    private String insufficientMoneyEnchant = "&4You do not have enough money to " +
            "purchase any enchantments!";
    @Coloured
    @Comments({"The message sent when a player purchases an enchant",
            "The %s is the enchantment name, the first %d is the level, the second is the price"})
    private String purchaseMessage = "&3You have purchased %s %d for %d";
    @Comment("The price per level of an enchantment")
    private int pricePerLevel = 5;

    public int damageInterval() {
        return damageInterval;
    }

    public String enchantmentLore() {
        return enchantmentLore;
    }

    public int pricePerLevel() {
        return pricePerLevel;
    }

    public String enchantmentMenuTitle() {
        return enchantmentMenuTitle;
    }

    public String enchantmentName() {
        return enchantmentName;
    }

    public String purchaseMessage() {
        return purchaseMessage;
    }

    public String insufficientMoneyEnchant() {
        return insufficientMoneyEnchant;
    }

    public ItemStack generateBook(Enchantment enchantment, int level, int addLevel) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        String enchantmentDisplay = enchantmentDisplay(enchantment);

        meta.addStoredEnchant(enchantment, level, true);
        meta.setLore(Lists.newArrayList(String.format(enchantmentLore, enchantmentDisplay, addLevel)));
        meta.setDisplayName(String.format(enchantmentName, enchantmentDisplay));

        stack.setItemMeta(meta);
        return stack;
    }

    public String enchantmentDisplay(Enchantment enchantment) {
        return StringUtils.capitalize(enchantment.getName().replace("_", " ").toLowerCase());
    }
}
