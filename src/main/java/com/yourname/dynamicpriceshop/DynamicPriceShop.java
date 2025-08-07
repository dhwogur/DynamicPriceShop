package com.yourname.dynamicpriceshop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DynamicPriceShop extends JavaPlugin {

    private Economy economy;   // Vault에서 제공받는 Economy 객체

    @Override
    public void onEnable() {
        // 1️⃣ Vault 연결 확인
        if (!setupVault()) {
            getLogger().severe("Vault가 감지되지 않았습니다. 플러그인을 비활성화합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // 2️⃣ 커맨드와 리스너 등록
        getCommand("shop").setExecutor(new ShopCommand(this));
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);

        getLogger().info("DynamicPriceShop 활성화 완료!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DynamicPriceShop 비활성화 중...");
    }

    /** Vault와 연결하고 Economy 객체를 가져옵니다. */
    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;   // Vault 플러그인 자체가 없을 때
        }

        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;   // Vault는 있지만 Economy 서비스가 등록되지 않은 경우
        }

        economy = rsp.getProvider();   // 실제 Economy 구현체 (EssentialsX, iConomy 등)
        return economy != null;
    }

    /** 다른 클래스에서 Economy에 접근할 때 쓰는 getter */
    public Economy getEconomy() {
        return economy;
    }
}