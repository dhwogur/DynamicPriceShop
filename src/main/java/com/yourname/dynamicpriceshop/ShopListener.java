package com.yourname.dynamicpriceshop;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private final DynamicPriceShop plugin;

    public ShopListener(DynamicPriceShop plugin) {
        this.plugin = plugin;
    }

    /** 예시: 손에 금괴(GOLD_INGOT)를 들고 오른쪽 클릭 하면 상점 열기 */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        if (item.getType() == Material.GOLD_INGOT) {
            // 실제 상점 GUI를 여는 로직을 여기서 호출
            event.getPlayer().sendMessage("§a상점을 여는 중… (아직 구현되지 않음)");
            // 예시: plugin.getServer().getScheduler().runTaskLater(plugin, () -> openShop(event.getPlayer()), 1L);
        }
    }
}