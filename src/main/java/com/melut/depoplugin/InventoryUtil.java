<xaiArtifact contentType="text/x-java" artifact_id="inventory_util_java">
        package com.melut.depoplugin; // util alt paketi kullanmak iyi bir pratik olabilir

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64; // Base64 import edin

// Envanter içeriklerini Base64 stringlerine dönüştürmek için yardımcı sınıf
public class InventoryUtil {

    // ItemStack dizisini Base64 stringine dönüştürür
    public static String serializeInventory(ItemStack[] items) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(os)) {

            // Envanterdeki item sayısını yaz
            dataOutput.writeInt(items.length);

            // Her itemı serileştir ve yaz
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Output stream'i kapat (try-with-resources ile otomatik yapılır)
            // dataOutput.close(); // Gerek yok
            // os.close(); // Gerek yok

            // Byte dizisini Base64 stringine dönüştür ve döndür
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) {
            // Serileştirme hatası durumunda hatayı tekrar fırlat
            throw new IOException("Envanter serilestirilirken bir hata olustu.", e);
        }
    }

    // Base64 stringini ItemStack dizisine dönüştürür
    public static ItemStack[] deserializeInventory(String data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(is)) {

            // Item sayısını oku
            int itemLength = dataInput.readInt();
            ItemStack[] items = new ItemStack[itemLength];

            // Her itemı oku ve diziye ekle
            for (int i = 0; i < itemLength; i++) {
                // Hata yönetimi: NullPointerException veya ClassCastException olabilir
                try {
                    Object obj = dataInput.readObject();
                    if (obj instanceof ItemStack) {
                        items[i] = (ItemStack) obj;
                    } else {
                        // Beklenen türde değilse hata logla veya atla
                        // Burada şimdilik null olarak bırakalım
                        items[i] = null;
                        System.err.println("Beklenmeyen nesne turu okundu: " + (obj != null ? obj.getClass().getName() : "null"));
                    }
                } catch (IOException | ClassNotFoundException | ClassCastException e) {
                    // Item okuma hatası
                    System.err.println("Item okunurken bir hata olustu: " + e.getMessage());
                    items[i] = null; // Hatalı itemı null yap
                }
            }

            // Input stream'leri kapat (try-with-resources ile otomatik yapılır)
            // dataInput.close(); // Gerek yok
            // is.close(); // Gerek yok

            return items;
        } catch (IOException | ClassNotFoundException e) {
            // Deserileştirme hatası durumunda hatayı tekrar fırlat
            throw new IOException("Envanter deserilestirilirken bir hata olustu.", e);
        }
    }

    // Bir Inventory nesnesinin içeriğini ItemStack dizisine kopyalar
    public static ItemStack[] getInventoryContents(Inventory inventory) {
        if (inventory == null) {
            return new ItemStack[0]; // Null envanter için boş dizi döndür
        }
        // Envanter içeriğini kopyala
        return inventory.getContents();
    }
}
</xaiArtifact>
