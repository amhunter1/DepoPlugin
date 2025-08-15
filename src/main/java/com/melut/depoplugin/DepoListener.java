package com.melut.depoplugin;

public class DepoListener {
}
<xaiArtifact contentType="text/x-java" artifact_id="depo_listener_java">
        package com.melut.depoplugin; // listeners alt paketi kullanmak iyi bir pratik olabilir

import com.melut.depoplugin.DepoPl;
import com.melut.depoplugin.PlayerDepotData;
import com.melut.depoplugin.InventoryUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent; // Oyuncu girişini dinlemek için
import org.bukkit.event.player.PlayerQuitEvent; // Oyuncu çıkışını dinlemek için
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

// Envanter ve oyuncu olaylarını dinleyen sınıf
public class DepoListener implements Listener {

    private final DepoPlugin plugin;
    // Hangi oyuncunun hangi depoyu açık tuttuğunu takip eden harita
    // Oyuncu UUID'si -> Açık depo numarası
    private final Map<Player, Integer> playersCurrentlyInDepot = new HashMap<>();

    public DepoListener(DepoPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler // Envanter tıklama olayını dinler
    public void onInventoryClick(InventoryClickEvent event) {
        // Olayın bir oyuncu tarafından yapıldığını kontrol et
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory topInventory = event.getView().getTopInventory(); // Tıklanan GUI

        // Eğer tıklanan envanter oyuncunun envanteri değilse ve custom GUI'miz ise
        if (clickedInventory != null && clickedInventory.equals(topInventory)) {

            // Depo Seçim GUI'sini kontrol et
            if (topInventory.getHolder() == null && topInventory.getSize() == 27 && topInventory.getRawTitle().equals(ChatColor.BLUE + "Depo Secimi")) {
                // Depo seçim GUI'sindeki bir yuvaya tıklandı

                event.setCancelled(true); // Tıklama olayını iptal et (item alınamaz/taşınamaz)

                // Tıklanan item boş değilse ve bir varil simgesi ise
                if (clickedItem != null && clickedItem.getType() == Material.BARREL) {
                    ItemMeta meta = clickedItem.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        String displayName = ChatColor.stripColor(meta.getDisplayName()); // Renk kodlarını kaldır

                        // DisplayName "Depo #X" formatında mı kontrol et
                        if (displayName.startsWith("Depo #")) {
                            try {
                                int depotNumber = Integer.parseInt(displayName.substring("Depo #".length())); // Depo numarasını al

                                // Depo numarası 1 ile 14 arasında mı kontrol et
                                if (depotNumber >= 1 && depotNumber <= 14) {

                                    // Oyuncunun bu depoyu açma izni var mı kontrol et
                                    if (player.hasPermission("depo." + depotNumber)) {
                                        // İzin varsa depoyu aç

                                        // Oyuncunun depo verilerini al
                                        PlayerDepotData depotData = plugin.getPlayerDepotData(player);
                                        // Belirtilen depo numarasının envanter içeriğini al
                                        ItemStack[] depotContents = depotData.getDepotInventory(depotNumber);

                                        // Depo envanteri için yeni bir GUI oluştur (54 yuvalık, sandık boyutu)
                                        // Bukkit.createInventory(sahibi, boyutu, başlığı)
                                        Inventory depotInventoryGUI = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Depo #" + depotNumber);

                                        // Depo içeriğini GUI'ye yerleştir
                                        if (depotContents != null) {
                                            depotInventoryGUI.setContents(depotContents);
                                        }

                                        // Oyuncuya depo envanteri GUI'sini aç
                                        player.openInventory(depotInventoryGUI);

                                        // Oyuncunun şu anda hangi depoda olduğunu takip et
                                        playersCurrentlyInDepot.put(player, depotNumber);

                                        // Loglama: Oyuncu belirli bir depoyu açtı
                                        plugin.getPluginLogger().log(Level.INFO, player.getName() + " Depo #" + depotNumber + " acildi.");

                                    } else {
                                        // İzin yoksa mesaj gönder
                                        player.sendMessage(ChatColor.RED + "Bu depoyu acmak icin iznin yok! (depo." + depotNumber + ")");
                                        // Loglama: Oyuncu izin olmadan depoyu açmaya çalıştı
                                        plugin.getPluginLogger().log(Level.WARNING, player.getName() + " Depo #" + depotNumber + " izinsiz acmaya calisti.");
                                    }

                                } else {
                                    // Geçersiz depo numarası
                                    plugin.getPluginLogger().log(Level.WARNING, "Gecersiz depo numarasi tiklandi: " + depotNumber);
                                }
                            } catch (NumberFormatException e) {
                                // DisplayName parse edilemedi
                                plugin.getPluginLogger().log(Level.WARNING, "Depo numarasi parse edilemedi: " + displayName, e);
                            }
                        }
                    }
                }
            } else {
                // Eğer tıklanan GUI bir depo envanteri GUI'si ise
                // Başlığı "Depo #X" formatında olan GUI'leri kontrol et
                if (topInventory.getHolder() == null && topInventory.getSize() == 54 && topInventory.getRawTitle().startsWith(ChatColor.GOLD + "Depo #")) {
                    // Oyuncu depo envanteri GUI'sinde bir yere tıkladı

                    // Tıklama olayını iptal etme (oyuncunun itemleri taşımasına izin ver)
                    // event.setCancelled(true); // Bunu yaparsak oyuncu item taşıyamaz

                    // Eğer shift-click ile oyuncu envanterinden depo envanterine item konuyorsa
                    if (event.isShiftClick() && event.getClickedInventory().equals(player.getInventory())) {
                        // Shift-click ile kendi envanterinden depo GUI'sine item koyuluyor
                        // Burada envanter doluluğu gibi ek kontroller yapılabilir
                        // Şimdilik direk item konulmasına izin veriyoruz
                        // Loglama: Oyuncu depo envanterine item koydu (shift-click)
                        if (clickedItem != null) {
                            plugin.getPluginLogger().log(Level.INFO, player.getName() + " depo envanterine (shift-click) ile item koydu: " + clickedItem.getType().name());
                        }

                    } else if (event.getClickedInventory().equals(topInventory)) {
                        // Depo GUI'sinin içinde tıklandı (item alınıyor veya yuva değiştiriliyor)
                        // Loglama: Oyuncu depo envanterinden item aldı veya yer değiştirdi
                        if (clickedItem != null) {
                            plugin.getPluginLogger().log(Level.INFO, player.getName() + " depo envanterinde islem yapti: " + clickedItem.getType().name());
                        } else {
                            plugin.getPluginLogger().log(Level.INFO, player.getName() + " depo envanterinde bos yuvaya tikladi.");
                        }

                    } else if (event.getClickedInventory().equals(player.getInventory())) {
                        // Depo GUI'si açıkken kendi envanterinde tıklandı
                        // Loglama: Oyuncu kendi envanterinde islem yapti (depo GUI'si acikken)
                        if (clickedItem != null) {
                            plugin.getPluginLogger().log(Level.INFO, player.getName() + " kendi envanterinde islem yapti (depo GUI'si acikken): " + clickedItem.getType().name());
                        } else {
                            plugin.getPluginLogger().log(Level.INFO, player.getName() + " kendi envanterinde bos yuvaya tikladi (depo GUI'si acikken).");
                        }
                    }
                }
            }
        }
        // Oyuncunun envanterine tıklanırsa ve üstteki envanter Depo GUI'si ise
        else if (clickedInventory != null && clickedInventory.equals(player.getInventory()) && topInventory.getHolder() == null && topInventory.getSize() == 54 && topInventory.getRawTitle().startsWith(ChatColor.GOLD + "Depo #")) {
            // Oyuncu kendi envanterinde bir yere tıkladı, ama Depo GUI'si açık
            // Tıklama olayını iptal etme (oyuncunun itemleri taşımasına izin ver)
            // event.setCancelled(true); // Bunu yaparsak oyuncu item taşıyamaz

            // Eğer shift-click ile depo envanterinden oyuncu envanterine item konuyorsa
            if (event.isShiftClick() && event.getClickedInventory().equals(player.getInventory())) {
                // Shift-click ile kendi envanterinden depo GUI'sine item konulması durumu yukarıda yakalandı.
                // Burası, Depo GUI'sinden kendi envanterine shift-click ile item alınması durumunu yakalar.
                if (clickedItem != null) {
                    plugin.getPluginLogger().log(Level.INFO, player.getName() + " depo envanterinden (shift-click) ile item aldi: " + clickedItem.getType().name());
                }
            }
        }
    }

    @EventHandler // Envanter kapatma olayını dinler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Olayın bir oyuncu tarafından yapıldığını kontrol et
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory(); // Kapatılan GUI

        // Eğer kapatılan GUI bir depo envanteri GUI'si ise
        // Başlığı "Depo #X" formatında olan GUI'leri kontrol et
        if (closedInventory.getHolder() == null && closedInventory.getSize() == 54 && closedInventory.getRawTitle().startsWith(ChatColor.GOLD + "Depo #")) {

            // Oyuncunun hangi depoda olduğunu takip eden haritada var mı kontrol et
            if (playersCurrentlyInDepot.containsKey(player)) {
                int depotNumber = playersCurrentlyInDepot.get(player); // Açık olan depo numarasını al

                // Oyuncunun depo verilerini al
                PlayerDepotData depotData = plugin.getPlayerDepotData(player);
                // Kapatılan envanterin içeriğini al
                ItemStack[] currentContents = InventoryUtil.getInventoryContents(closedInventory);

                // Deponun güncel içeriğini oyuncunun verisine kaydet
                depotData.setDepotInventory(depotNumber, currentContents);

                // Oyuncunun bu depoda olmadığı bilgisini kaldır
                playersCurrentlyInDepot.remove(player);

                // Verileri dosyaya kaydet (isteğe bağlı, her kapattığında kaydedebiliriz veya sadece sunucu kapanırken/oyuncu çıkarken)
                // Her kapatmada kaydetmek daha güvenli olabilir ama performans etkisi olabilir.
                // Şimdilik sadece sunucu kapanırken/oyuncu çıkarken kaydedeceğiz.
                // plugin.saveDepotData(); // Anında kaydetme örneği

                // Loglama: Oyuncu belirli bir depoyu kapattı
                plugin.getPluginLogger().log(Level.INFO, player.getName() + " Depo #" + depotNumber + " kapatildi. Veriler guncellendi.");
            }
        }
        // Depo Seçim GUI'si kapatıldığında herhangi bir işlem yapmaya gerek yok
        // else if (closedInventory.getHolder() == null && closedInventory.getSize() == 27 && closedInventory.getRawTitle().equals(ChatColor.BLUE + "Depo Secimi")) {
        //     // Depo seçim GUI'si kapatıldı, özel bir işlem yapmaya gerek yok
        // }
    }

    @EventHandler // Oyuncu sunucuya giriş yaptığında
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Oyuncu giriş yaptığında depo verilerini yüklemeye gerek yok çünkü onEnable'da hepsi yükleniyor.
        // Ancak, eğer sadece o oyuncunun verisini yüklemek isterseniz burada yapabilirsiniz.
        // Şu anki yapıda onEnable ve onDisable tüm veriyi yönetiyor.
        // getPlayerDepotData çağrıldığında oyuncu için veri yoksa otomatik oluşturulur.
        // Yani burada ekstra bir yükleme koduna gerek yok.
        plugin.getPluginLogger().log(Level.INFO, event.getPlayer().getName() + " sunucuya katildi.");
    }

    @EventHandler // Oyuncu sunucudan çıktığında
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Oyuncu sunucudan çıktığında o oyuncunun depo verilerini kaydet
        // Bu, sunucu çökmesi durumunda veri kaybını önlemeye yardımcı olur.
        Player player = event.getPlayer();

        // Eğer oyuncu şu anda bir depo GUI'sindeyse, oradaki veriyi de kaydet
        // Bu durum normalde InventoryCloseEvent tarafından zaten halledilmeli,
        // ancak ek önlem olarak burada da kontrol edilebilir.
        // Eğer oyuncu Depo GUI'sindeyken çıkarsa, InventoryCloseEvent tetiklenmeyebilir.
        // Bu nedenle, çıkışta o oyuncunun tüm verisini kaydetmek daha güvenlidir.

        // Oyuncunun tüm depo verisini (tüm 14 depo) kaydetmek en güvenlisidir.
        // Ancak saveDepotData metodu tüm oyuncuların verisini kaydeder.
        // Sadece bu oyuncunun verisini kaydetmek için PlayerDepotDataMap'ten çekip,
        // sadece o oyuncunun verisini serileştirip dosyaya yazmak gerekir.
        // Basitlik açısından, şu anki yapıda sadece saveDepotData'yı onDisable'da çağırıyoruz.
        // Eğer oyuncu çıktığında anlık kaydetme istenirse, saveDepotData metodunu
        // tek bir oyuncuyu kaydedecek şekilde refactor etmek gerekebilir.

        // Mevcut yapıda, oyuncu çıktığında açık olan depo GUI'si varsa,
        // InventoryCloseEvent tetiklenir ve o deponun verisi güncellenir.
        // onDisable çağrıldığında tüm veriler topluca kaydedilir.
        // Çökme durumunda, son kaydedilen duruma dönülür.

        // Daha sağlam bir yaklaşım: Oyuncu çıktığında sadece o oyuncunun verisini anlık kaydetmek
        // Bunun için saveDepotData metodunu UUID alacak şekilde değiştirmek veya yeni bir metot yazmak gerekir.
        // Şimdilik mevcut yapıyı koruyalım.

        // Loglama: Oyuncu sunucudan ayrıldı
        plugin.getPluginLogger().log(Level.INFO, event.getPlayer().getName() + " sunucudan ayrildi.");

        // Eğer oyuncu çıkarken hala bir depo GUI'sindeyse, bunu playersCurrentlyInDepot haritasından kaldır.
        playersCurrentlyInDepot.remove(player);
    }
}
</xaiArtifact>