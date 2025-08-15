package com.melut.depoplugin;

public class DepoCommand {
}
<xaiArtifact contentType="text/x-java" artifact_id="depo_command_java">
        package com.melut.depoplugin; // commands alt paketi kullanmak iyi bir pratik olabilir

import com.melut.depoplugin.DepoPl;
import com.melut.depoplugin.PlayerDepotData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

// "/depo" komutunu işleyen sınıf
public class DepoCommand implements CommandExecutor {

    private final DepoPlugin plugin;
    private static final int GUI_SIZE = 27; // GUI boyutu (27 yuva)
    private static final int[] BARREL_SLOTS = {
            10, 11, 12, 13, 14, 15, 16, // İlk sıra
            19, 20, 21, 22, 23, 24, 25  // İkinci sıra (toplam 14 yuva)
    };

    public DepoCommand(DepoPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Komutu çalıştıranın oyuncu olup olmadığını kontrol et
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;

        // Depo seçim GUI'sini oluştur
        Inventory selectionGui = Bukkit.createInventory(null, GUI_SIZE, ChatColor.BLUE + "Depo Secimi");

        // Her bir depo için GUI'ye varil simgesi ekle
        for (int i = 0; i < 14; i++) {
            int depotNumber = i + 1; // Depo numarası 1'den başlar
            Material barrelMaterial = Material.BARREL; // Varil materyali
            ItemStack barrelItem = new ItemStack(barrelMaterial);
            ItemMeta meta = barrelItem.getItemMeta();

            // Varil itemının adını ayarla
            meta.setDisplayName(ChatColor.GOLD + "Depo #" + depotNumber);

            // Varil itemının açıklamasını ayarla (izin bilgisi içerebilir)
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Bu depoyu acmak icin tikla.");

            // Oyuncunun bu depoyu açma izni var mı kontrol et
            if (player.hasPermission("depo." + depotNumber)) {
                lore.add(ChatColor.GREEN + "Erisim izni: Var");
            } else {
                lore.add(ChatColor.RED + "Erisim izni: Yok (depo." + depotNumber + ")");
                // İzni olmayan varilleri daha soluk veya farklı bir renkle gösterebilirsiniz
                // Örneğin, gri yün bloğu gibi
                // barrelItem.setType(Material.LIGHT_GRAY_WOOL); // Alternatif görünüm
            }

            meta.setLore(lore);
            barrelItem.setItemMeta(meta);

            // Varil simgesini belirlenen yuvaya yerleştir
            selectionGui.setItem(BARREL_SLOTS[i], barrelItem);
        }

        // Oyuncuya depo seçim GUI'sini aç
        player.openInventory(selectionGui);

        // Loglama: Depo seçim GUI'si açıldı
        plugin.getPluginLogger().log(java.util.logging.Level.INFO, player.getName() + " depo secim GUI'sini acti.");

        return true; // Komut başarıyla işlendi
    }
}
</xaiArtifact>