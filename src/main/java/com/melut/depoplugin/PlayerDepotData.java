<xaiArtifact contentType="text/x-java" artifact_id="player_depot_data_java">
        package com.melut.depoplugin; // data alt paketi kullanmak iyi bir pratik olabilir

import com.melut.depoplugin.InventoryUtil; // Envanter yardımcı sınıfınızı import edin

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Base64; // Base64 import edin
import java.util.List;

// Oyuncunun depo verilerini tutan sınıf
public class PlayerDepotData {

    // 14 adet depo için envanter içeriklerini tutacak liste
    // Her bir element bir depo envanterinin içeriğini temsil eder (ItemStack dizisi)
    private List<String> serializedDepots; // ItemStack[] yerine Base64 stringleri saklayacağız

    // Kurucu metot: Yeni bir oyuncu için boş depo verisi oluşturur
    public PlayerDepotData() {
        // 14 adet boş depo envanteri oluştur (null olarak temsil edilecek)
        serializedDepots = new ArrayList<>(14);
        for (int i = 0; i < 14; i++) {
            serializedDepots.add(null); // Başlangıçta depolar boş
        }
    }

    // Belirli bir depo numarasının envanter içeriğini döndüren metot
    // Depo numaraları 1'den 14'e kadar olmalıdır
    public ItemStack[] getDepotInventory(int depotNumber) {
        // Geçersiz depo numarası kontrolü
        if (depotNumber < 1 || depotNumber > 14) {
            return null; // Geçersiz numara için null döndür
        }

        // List index'i 0'dan başladığı için depo numarasından 1 çıkarılır
        String serialized = serializedDepots.get(depotNumber - 1);

        // Eğer depo boşsa null döndür
        if (serialized == null) {
            return new ItemStack[0]; // Boş envanter döndür
        }

        // Base64 stringini ItemStack dizisine dönüştür ve döndür
        try {
            return InventoryUtil.deserializeInventory(serialized);
        } catch (Exception e) {
            // Dönüştürme hatası durumunda logla ve boş envanter döndür
            // Hata oluşması durumunda loglamayı burada değil, InventoryUtil'de yapmak daha iyi olabilir.
            // Ancak temel hata yakalama burada yapılabilir.
            // Plugin ana sınıfının logger'ına erişimimiz olmadığı için burada direk loglama yapmayalım.
            e.printStackTrace(); // Hata ayıklama için geçici log
            return new ItemStack[0];
        }
    }

    // Belirli bir depo numarasının envanter içeriğini ayarlayan metot
    // Depo numaraları 1'den 14'e kadar olmalıdır
    public void setDepotInventory(int depotNumber, ItemStack[] contents) {
        // Geçersiz depo numarası kontrolü
        if (depotNumber < 1 || depotNumber > 14) {
            return; // Geçersiz numara için işlem yapma
        }

        // Envanter içeriğini Base64 stringine dönüştür
        try {
            String serialized = InventoryUtil.serializeInventory(contents);
            // List index'i 0'dan başladığı için depo numarasından 1 çıkarılır
            serializedDepots.set(depotNumber - 1, serialized);
        } catch (Exception e) {
            // Dönüştürme hatası durumunda logla
            e.printStackTrace(); // Hata ayıklama için geçici log
        }
    }
}
</xaiArtifact>