package com.yourname.dynamicpriceshop.price;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플러그인 전체에서 가격 데이터를 보관하고 자동 감쇠(scheduled decay)를 수행한다.
 */
public class PriceManager {

    private final Plugin plugin;
    private final Map<Material, ItemPrice> priceMap = new ConcurrentHashMap<>();
    private final File dataFile;

    /** 감쇠 주기 – 5분 (밀리초) */
    private static final long DECAY_INTERVAL = 5 * 60 * 1000L;
    /** 감쇠 비율 – 5% */
    private static final double DECAY_FACTOR = 0.05;

    public PriceManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "prices.yml");
        loadAll();            // 서버 시작 시 기존 파일 복원
        startDecayTask();     // 자동 감쇠 스케줄러 시작
    }

    /** 가격 객체를 반환하거나 없으면 기본값으로 새로 만든다. */
    public ItemPrice getOrCreate(Material material, double defaultBase) {
        return priceMap.computeIfAbsent(material, m -> new ItemPrice(m, defaultBase));
    }

    /** 구매가 발생했을 때 호출 – 가격 상승 로직을 위임 */
    public void onPurchase(Material material, double defaultBase) {
        ItemPrice ip = getOrCreate(material, defaultBase);
        ip.onPurchase();
        plugin.getLogger().info("[Shop] " + material.name()
                + " purchased, new price: " + ip.getCurrentPrice());
    }

    /* -------------------------------------------------
     *  자동 감쇠 (Decay) 스케줄러
     * ------------------------------------------------- */
    private void startDecayTask() {
        // 비동기 스케줄러 – 1분마다 모든 아이템에 decay 적용
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (ItemPrice ip : priceMap.values()) {
                ip.decayIfNeeded(DECAY_INTERVAL, DECAY_FACTOR);
            }
        }, 20L * 60, 20L * 60); // delay 1분, period 1분
    }

    /* -------------------------------------------------
     *  파일 저장 / 로드
     * ------------------------------------------------- */

    /** 현재 가격 데이터를 plugins/DynamicPriceShop/prices.yml 로 저장 */
    public void saveAll() {
        YamlConfiguration yaml = new YamlConfiguration();
        ConfigurationSection root = yaml.createSection("prices");

        for (ItemPrice ip : priceMap.values()) {
            // 각 아이템에 대한 서브 섹션 생성 후, ItemPrice.save 로 기록
            ConfigurationSection sec = root.createSection(ip.getMaterial().name());
            ip.save(sec);
        }

        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("[PriceManager] 파일 저장 오류: " + e);
        }
    }

    /** 서버 시작 시 기존 파일을 읽어 priceMap 에 복원 */
    public void loadAll() {
        if (!dataFile.exists()) {
            plugin.getLogger().info("[PriceManager] 기존 가격 파일이 없으므로 새로 시작합니다.");
            return;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection root = yaml.getConfigurationSection("prices");
        if (root == null) {
            plugin.getLogger().warning("[PriceManager] prices 섹션이 없어요.");
            return;
        }

        for (String key : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(key);
            if (sec == null) continue;

            try {
                ItemPrice ip = ItemPrice.load(sec);   // static 메서드가 모든 필드를 채워줌
                priceMap.put(ip.getMaterial(), ip);
            } catch (Exception e) {
                plugin.getLogger().warning("[PriceManager] " + key + " 로드 실패: " + e);
            }
        }

        plugin.getLogger().info("[PriceManager] " + priceMap.size()
                + " 개 아이템 가격을 로드했습니다.");
    }

    /** 플러그인 비활성화 시 자동 저장 */
    public void shutdown() {
        saveAll();
    }
}