package com.yourname.dynamicpriceshop;

import org.bukkit.plugin.java.JavaPlugin;

public class DynamicPriceShop extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("DynamicPriceShop 플러그인이 활성화되었습니다!");
        // 간단 테스트 명령어 등록
        getCommand("helloworld").setExecutor(new command.HelloCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("DynamicPriceShop 플러그인이 비활성화되었습니다.");
    }
}