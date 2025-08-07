package com.yourname.dynamicpriceshop.price;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

/**
 * 단일 아이템(예: DIAMOND)의 가격·구매 이력 데이터를 보관합니다.
 * - 기본 가격(basePrice) : 최초 설정값
 * - 현재 가격(currentPrice) : 거래·감쇠 로직에 의해 변동
 * - purchases : 현재 가격 계산에 사용되는 구매 횟수
 * - lastPurchaseTime : 마지막 구매 시각 (밀리초)
 */
public final class ItemPrice {
    private final Material material;
    private double basePrice;
    private double currentPrice;
    private int purchases;               // 현재 세션(또는 전체)에서 몇 번 팔렸는가
    private long lastPurchaseTime;       // System.currentTimeMillis() 값

    public ItemPrice(Material material, double basePrice) {
        this.material = Objects.requireNonNull(material);
        this.basePrice = basePrice;
        this.currentPrice = basePrice;
        this.purchases = 0;
        this.lastPurchaseTime = 0L;
    }

    /* ---------- Getter / Setter ---------- */
    public Material getMaterial() { return material; }
    public double getBasePrice() { return basePrice; }
    public double getCurrentPrice() { return currentPrice; }
    public int getPurchases() { return purchases; }
    public long getLastPurchaseTime() { return lastPurchaseTime; }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        this.currentPrice = basePrice;
    }

    /* ---------- 가격 로직 ---------- */

    /**
     * 아이템 한 개가 구매됐을 때 호출.
     * 현재 가격을 상승시키고 구매 카운트를 증가시킨다.
     *
     * 예시 알고리즘: 구매당 기본가격의 5% 상승 (단순 예시)
     */
    public void onPurchase() {
        purchases++;
        lastPurchaseTime = System.currentTimeMillis();

        // 5%씩 상승 (거래당)
        this.currentPrice = Math.round(basePrice * (1 + 0.05 * purchases) * 100.0) / 100.0;
    }

    /**
     * 일정 시간(예: 5분) 동안 구매가 없으면 가격을 낮춘다.
     * decayMillis : 감쇠를 체크할 최소 간격 (ms)
     * decayFactor : 감쇠 비율 (0.05 = 5% 감소)
     */
    public void decayIfNeeded(long decayMillis, double decayFactor) {
        if (purchases == 0) return; // 아직 한 번도 안 팔림

        long now = System.currentTimeMillis();
        if (now - lastPurchaseTime >= decayMillis) {
            // 현재 가격을 기본 가격에 점진적으로 수렴
            this.currentPrice = Math.max(basePrice,
                    Math.round(currentPrice * (1 - decayFactor) * 100.0) / 100.0);

            // 현재 가격이 기본 가격에 거의 도달했으면 카운트를 초기화
            if (Math.abs(currentPrice - basePrice) < 0.01) {
                purchases = 0;
            }
        }
    }

    /* ---------- 파일 입출력 (YAML) ---------- */

    /** config 섹션에 데이터를 저장한다. */
    public void save(ConfigurationSection section) {
        section.set("material", material.name());
        section.set("base-price", basePrice);
        section.set("current-price", currentPrice);
        section.set("purchases", purchases);
        section.set("last-purchase", lastPurchaseTime);
    }

    /** config 섹션으로부터 객체를 복구한다. */
    public static ItemPrice load(ConfigurationSection section) {
        Material mat = Material.valueOf(section.getString("material"));
        double base = section.getDouble("base-price");
        ItemPrice ip = new ItemPrice(mat, base);
        ip.currentPrice = section.getDouble("current-price", base);
        ip.purchases = section.getInt("purchases", 0);
        ip.lastPurchaseTime = section.getLong("last-purchase", 0L);
        return ip;
    }

    /** GUI에 띄울 ItemStack 생성 (플러그인 UI용) */
    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§e" + material.name());
        meta.setLore(List.of(
                "§7기본 가격: §b" + basePrice,
                "§7현재 가격: §a" + currentPrice,
                "§7구매 횟수: §f" + purchases
        ));
        stack.setItemMeta(meta);
        return stack;
    }
}