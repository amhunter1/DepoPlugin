<xaiArtifact contentType="text/x-java" artifact_id="depo_plugin_java">
        package com.melut.depoplugin; // Kendi paket adınızla değiştirin

import com.melut.depoplugin.commands.DepoCommand; // Komut sınıfınızı import edin
import com.melut.depoplugin.listeners.DepoListener; // Olay dinleyici sınıfınızı import edin
import com.melut.depoplugin.data.PlayerDepotData; // Oyuncu veri sınıfınızı import edin
import com.melut.depoplugin.util.InventoryUtil; // Envanter yardımcı sınıfınızı import edin

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken; // TypeToken'ı import edin

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
        import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level; // Logging seviyeleri için

public final class DepoPl extends JavaPlugin {

    // Oyuncu UUID'sine karşılık gelen depo verilerini tutan harita
    private Map<UUID, PlayerDepotData> playerDepotDataMap;
    // JSON işlemleri için Gson nesnesi
    private Gson gson;
    // Depo veri dosyasının yolu
    private File dataFile;

    @Override
    public void onEnable() {
        // Plugin aktifleştiğinde yapılacaklar

        // Veri dosyasını oluştur
        dataFile = new File(getDataFolder(), "depot_data.json");
        // Eğer plugin veri klasörü yoksa oluştur
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Gson nesnesini oluştur (güzel formatlama ile)
        gson = new GsonBuilder().setPrettyPrinting().create();

        // Verileri dosyadan yükle
        loadDepotData();

        // Komutları kaydet
        // "/depo" komutunu DepoCommand sınıfı işleyecek
        getCommand("depo").setExecutor(new DepoCommand(this));

        // Olay dinleyicilerini kaydet
        // Envanter olaylarını DepoListener sınıfı işleyecek
        getServer().getPluginManager().registerEvents(new DepoListener(this), this);

        // Pluginin başarıyla yüklendiğini logla
        getLogger().log(Level.INFO, "DepoPlugin basariyla yuklendi!");
    }

    @Override
    public void onDisable() {
        // Plugin devre dışı bırakıldığında yapılacaklar

        // Verileri dosyaya kaydet
        saveDepotData();

        // Pluginin başarıyla devre dışı bırakıldığını logla
        getLogger().log(Level.INFO, "DepoPlugin devre disi birakildi!");
    }

    // Depo verilerini dosyadan yükleme metodu
    private void loadDepotData() {
        // Eğer veri dosyası yoksa, boş bir harita ile başla
        if (!dataFile.exists()) {
            playerDepotDataMap = new HashMap<>();
            getLogger().log(Level.INFO, "Depo veri dosyasi bulunamadi, yeni dosya olusturulacak.");
            return;
        }

        // Veri dosyasını oku
        try (Reader reader = new FileReader(dataFile)) {
            // Gson ile JSON verisini PlayerDepotData haritasına dönüştür
            Type type = new TypeToken<Map<UUID, PlayerDepotData>>(){}.getType();
            playerDepotDataMap = gson.fromJson(reader, type);

            // Eğer dosya boşsa veya hatalı formatlıysa boş bir harita ata
            if (playerDepotDataMap == null) {
                playerDepotDataMap = new HashMap<>();
                getLogger().log(Level.WARNING, "Depo veri dosyasi bos veya hatali, bos harita olusturuldu.");
            } else {
                getLogger().log(Level.INFO, "Depo verileri basariyla yuklendi.");
            }

        } catch (IOException e) {
            // Dosya okuma hatası
            getLogger().log(Level.SEVERE, "Depo verileri yuklenirken bir hata olustu!", e);
            // Hata durumunda boş bir harita ile devam et
            playerDepotDataMap = new HashMap<>();
        }
    }

    // Depo verilerini dosyaya kaydetme metodu
    public void saveDepotData() {
        // Eğer playerDepotDataMap null ise kaydetme
        if (playerDepotDataMap == null) {
            getLogger().log(Level.WARNING, "Kaydedilecek depo verisi bulunmuyor (playerDepotDataMap null).");
            return;
        }

        // Verileri JSON formatına dönüştür ve dosyaya yaz
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(playerDepotDataMap, writer);
            getLogger().log(Level.INFO, "Depo verileri basariyla kaydedildi.");
        } catch (IOException e) {
            // Dosya yazma hatası
            getLogger().log(Level.SEVERE, "Depo verileri kaydedilirken bir hata olustu!", e);
        }
    }

    // Belirli bir oyuncunun depo verisini döndüren metot
    public PlayerDepotData getPlayerDepotData(Player player) {
        // Eğer oyuncu için veri yoksa, yeni bir PlayerDepotData nesnesi oluştur ve haritaya ekle
        playerDepotDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerDepotData());
        // Oyuncunun verisini döndür
        return playerDepotDataMap.get(player.getUniqueId());
    }

    // Pluginin logger nesnesini döndüren metot
    public java.util.logging.Logger getPluginLogger() {
        return getLogger();
    }
}
</xaiArtifact>