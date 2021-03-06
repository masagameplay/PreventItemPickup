package dev.masa.preventitempickup.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import dev.masa.preventitempickup.PreventItemPickup;
import dev.masa.preventitempickup.model.PreventedItem;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@CommandAlias("pip|preventitempickup|preventitem")
public class PreventItemPickupCommand extends BaseCommand {

    private PreventItemPickup plugin;

    @CommandAlias("add")
    @Description("Add an item to prevented list.")
    @CommandPermission("preventitempickup.item.add")
    public void addItem(Player player, @Single String material) {
        if (material.equalsIgnoreCase("hand")) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                sendMessage(player, plugin.getConfig().getString("messages.hand-is-empty"));
                return;
            }
            material = player.getInventory().getItemInMainHand().getType().name();
        }
        if (Material.matchMaterial(material) != null) {
            if (plugin.getPreventedItemService().getPreventedItems(player.getUniqueId()).contains(Material.matchMaterial(material))) {
                sendMessage(player, plugin.getConfig().getString("messages.item-already-added"));
                return;
            }
            PreventedItem item = new PreventedItem(material, player.getUniqueId());
            plugin.getPreventedItemService().addPreventedItem(item);
            sendMessage(player, plugin.getConfig().getString("messages.item-added"));
            return;
        }
        sendMessage(player, plugin.getConfig().getString("messages.item-not-found"));
    }

    @CommandAlias("remove")
    @Description("Remove an item from the prevented list.")
    @CommandPermission("preventitempickup.item.remove")
    public void removeItem(Player player, @Single String material) {
        if (Material.matchMaterial(material) != null) {
            if (!plugin.getPreventedItemService().getPreventedItems(player.getUniqueId()).contains(Material.matchMaterial(material))) {
                sendMessage(player, plugin.getConfig().getString("messages.item-not-found"));
                return;
            }
            plugin.getPreventedItemService().removePreventedItem(player.getUniqueId(), Material.matchMaterial(material));
            sendMessage(player, plugin.getConfig().getString("messages.item-removed"));
            return;
        }
        sendMessage(player, plugin.getConfig().getString("messages.item-not-found"));
    }


    @CommandAlias("list")
    @Description("List all prevented items.")
    @CommandPermission("preventitempickup.item.list")
    public void listItems(Player player) {
        List<Material> materials = plugin.getPreventedItemService().getPreventedItems(player.getUniqueId());

        String prefix = colorize(plugin.getConfig().getString("messages.item-list-prefix"));
        String item = colorize(plugin.getConfig().getString("messages.item-list-item"));
        String separator = colorize(plugin.getConfig().getString("messages.item-list-item-separator"));

        BaseComponent component = new TextComponent(prefix);
        int i = 0;
        for (Material material : materials) {
            component.addExtra(item.replace("%item%", material.name()));
            if (i < materials.size() - 1) {
                component.addExtra(separator);
            }
            i++;
        }
        player.sendMessage(component.toLegacyText());
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
