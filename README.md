# DepoPlugin

## 🇹🇷 Türkçe Açıklama

### Proje Tanımı
DepoPlugin, Minecraft sunucuları için oyunculara kişisel depo (chest) alanları sunan, GUI tabanlı bir Bukkit/Spigot eklentisidir. Yöneticiler diğer oyuncuların depolarını görüntüleyebilir, düzenleyebilir ve silebilir. Eklenti, detaylı izin ve loglama sistemine sahiptir.

### Kurulum
1. [Releases](https://modrinth.com/plugin/depoplugin) bölümünden jar dosyasını indirin veya projeyi derleyin.
2. `DepoPlugin-vX.X.X.jar` dosyasını sunucunuzun `plugins` klasörüne atın.
3. Sunucunuzu yeniden başlatın veya `/reload` komutunu kullanın.

### Kullanım
- `/depo` : Kendi depo GUI'nizi açar.
- `/depo <depo_numarası>` : Belirli bir deponuzu açar.
- `/depobak <oyuncu> <depo_numarası>` : Başka bir oyuncunun deposunu görüntüler (Yönetici).
- `/depo-sil <oyuncu> <depo_numarası>` : Belirli bir oyuncunun deposunu siler (Yönetici).
- `/depopl` : Eklenti hakkında bilgi verir.

### Komutlar ve İzinler
- `depo.1` ... `depo.16` : Oyuncular için depo açma izinleri.
- `depo.*` : Tüm depoları açma izni.
- `depo.admin` : Yönetici yetkileri (başka oyuncuların depolarını görme, düzenleme, silme).
- `depo.bak` : `/depobak` komutunu kullanma izni.
- `depo.sil` : `/depo-sil` komutunu kullanma izni.

### Yapılandırma
- `config.yml` dosyası ile mesajlar, GUI başlıkları, sesler ve loglama ayarları özelleştirilebilir.
- `plugin.yml` dosyasında komutlar ve izinler tanımlıdır.

### Katkı ve Destek
- Geliştirici: Melüt
- Destek ve katkı için: [Discord](https://discord.com/users/871721944268038175)
- GitHub: [amhunter1](https://github.com/amhunter1)

### Lisans
Tüm hakları saklıdır. Eklentinin izinsiz kopyalanması, satılması veya lisans dışı kullanımı yasaktır.

---

## 🇬🇧 English Description

### Project Description
DepoPlugin is a Bukkit/Spigot plugin for Minecraft servers that provides players with personal storage (chest) spaces via a GUI. Admins can view, edit, and delete other players' depots. The plugin features a detailed permission and logging system.

### Installation
1. Download the jar file from the [Releases]([https://github.com/amhunter1](https://modrinth.com/plugin/depoplugin)) section or build the project.
2. Place the `DepoPlugin-vX.X.X.jar` file into your server's `plugins` directory.
3. Restart your server or use the `/reload` command.

### Usage
- `/depo` : Opens your personal depot GUI.
- `/depo <depot_number>` : Opens a specific depot.
- `/depobak <player> <depot_number>` : View another player's depot (Admin).
- `/depo-sil <player> <depot_number>` : Delete a specific depot of a player (Admin).
- `/depopl` : Shows plugin information.

### Commands and Permissions
- `depo.1` ... `depo.16` : Permissions for players to open depots.
- `depo.*` : Permission to open all depots.
- `depo.admin` : Admin privileges (view, edit, delete other players' depots).
- `depo.bak` : Permission to use `/depobak` command.
- `depo.sil` : Permission to use `/depo-sil` command.

### Configuration
- Customize messages, GUI titles, sounds, and logging via `config.yml`.
- Commands and permissions are defined in `plugin.yml`.

### Contribution and Support
- Developer: Melüt
- For support and contributions: [Discord](https://discord.com/users/871721944268038175)
- GitHub: [amhunter1](https://github.com/amhunter1)

### License
All rights reserved. Unauthorized copying, selling, or use of the plugin is strictly prohibited.
