package com.yourname.dynamicpriceshop.command;

import com.yourname.dynamicpriceshop.DynamicPriceShop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelloCommand implements CommandExecutor {

    private final DynamicPriceShop plugin;

    public HelloCommand(DynamicPriceShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player p) {
            p.sendMessage(ChatColor.AQUA + "🌟 " + ChatColor.YELLOW + "DynamicPriceShop 정상 동작 중!");
        } else {
            sender.sendMessage("콘솔에서도 플러그인 로드 확인되었습니다.");
        }
        return true;
    }
}