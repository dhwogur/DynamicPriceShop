package com.yourname.dynamicpriceshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final DynamicPriceShop plugin;

    public ShopCommand(DynamicPriceShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 콘솔에서 실행하면 무시
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령은 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        // 여기서 실제 GUI(인벤토리)를 열거나 가격 계산 로직을 호출하면 됩니다.
        // 예시: 간단히 현재 잔액을 보여줍니다.
        double balance = plugin.getEconomy().getBalance(player);
        player.sendMessage("§a현재 잔액: §e" + balance + "§a원");

        // TODO: 실제 상점 GUI 구현
        return true;
    }
}