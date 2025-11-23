package fr.heneria.core.command;

import fr.heneria.core.manager.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinsCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public CoinsCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("heneria.admin.coins")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /coins <give|set|see> <joueur> [montant]");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable ou hors ligne.");
            return true;
        }

        if (subCommand.equals("see")) {
            int coins = economyManager.getCoins(target);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " a " + ChatColor.GOLD + coins + " coins.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Veuillez spécifier un montant.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Montant invalide.");
            return true;
        }

        switch (subCommand) {
            case "give":
                economyManager.addCoins(target, amount);
                sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + amount + " coins à " + target.getName() + ".");
                break;
            case "set":
                economyManager.setCoins(target, amount);
                sender.sendMessage(ChatColor.GREEN + "Vous avez défini les coins de " + target.getName() + " à " + amount + ".");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Action inconnue. Utilisez: give, set, see.");
                break;
        }

        return true;
    }
}
