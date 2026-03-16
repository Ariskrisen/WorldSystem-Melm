package de.cycodly.worldsystem.guicreate.pages;

import de.cycodly.worldsystem.guicreate.OrcInventory;
import de.cycodly.worldsystem.guicreate.OrcItem;
import de.cycodly.worldsystem.config.GuiConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import de.cycodly.worldsystem.WorldSystem;
import java.util.logging.Level;

/**
 * @author Butzlabben
 * @since 20.05.2018
 */
public class InventoryPage extends OrcInventory {

    InventoryPage next, before = null;
    private int i = 0;

    public InventoryPage(String title, int page, int pages) {
        super(title, GuiConfig.getRows("options.players"));

        YamlConfiguration getConf = GuiConfig.getConfig();
        String path = "options.players.currentpage";

        if (GuiConfig.isEnabled(path)) {
            OrcItem oi = new OrcItem(GuiConfig.getItemStack(getConf, path));
            oi.setItemStack(GuiConfig.getItemStack(getConf, path), GuiConfig.getDisplay(getConf, path).replaceAll("%page", "" + page), GuiConfig.getLore(getConf, path));
            addItem(GuiConfig.getSlot(path), oi);
        }

        path = "options.players.pagebefore";
        if (GuiConfig.isEnabled(path)) {
            OrcItem oi = GuiConfig.getItem(path);
            oi.setOnClick((p, inv, item) -> {
                p.closeInventory();
                p.openInventory(this.before.getInventory(p));
            });
            addItem(GuiConfig.getSlot(path), oi);
        }

        path = "options.players.nextpage";
        if (GuiConfig.isEnabled(path)) {
            OrcItem oi = GuiConfig.getItem(path);
            oi.setOnClick((p, inv, item) -> {
                p.closeInventory();
                p.openInventory(this.next.getInventory(p));
            });
            addItem(GuiConfig.getSlot(path), oi);
        }
    }

    @Override
    public Inventory getInventory(Player p) {
        return super.getInventory(p);
    }

    public void addItem(OrcItem item) {
        if (i > 36) {
            WorldSystem.logger().log(Level.SEVERE,"More items than allowed in page view");
            return;
        }
        addItem(i, item);
        i++;
    }
}
