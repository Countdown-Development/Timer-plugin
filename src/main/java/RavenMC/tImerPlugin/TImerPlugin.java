package RavenMC.tImerPlugin;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TImerPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private BossBar timerBar;
    private double timeRemainingInDays;
    private double MAX_DAYS;
    private int maxAddDays;
    private int maxRemoveDays;
    private double minDaysToRemove;
    private boolean timerVisible;
    private final long DAY_IN_SECONDS = 86400; // Seconds in a day
    private final Map<UUID, UUID> recentKillers = new HashMap<>();
    private String GUI_TITLE;

    // Special items cooldown trackers
    private final Map<UUID, Long> timeMaceCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> remainingDashes = new ConcurrentHashMap<>();

    // Config message strings
    private String msgKillerReceivedItem;
    private String msgTimerZero;
    private String msgPlayerAddedTime;
    private String msgPlayerRemovedTime;
    private String msgTimerAtMax;
    private String msgTimerAtMin;
    private String msgNotRecentKiller;
    private String itemName;
    private List<String> itemLore;

    // Configuration fields for Time Mace
    private int maxDashes;
    private int maceCooldown;
    private double dashStrength;
    private boolean explosionEnabled;
    private int explosionRadius;
    private int explosionDuration;
    private int explosionSlownessLevel;

    // Configuration fields for effects
    private String addTimeSound;
    private float addTimeSoundVolume;
    private float addTimeSoundPitch;
    private int addTimeEffectDuration;
    private String removeTimeSound;
    private float removeTimeSoundVolume;
    private float removeTimeSoundPitch;
    private int removeTimeEffectDuration;

    // Additional message fields
    private String timeMaceDashMsg;
    private String timeMaceCooldownMsg;
    private String timeMaceRechargedMsg;

    @Override
    public void onEnable() {
        try {
            // Log plugin startup attempt
            System.out.println("[TimerPlugin] Starting plugin initialization...");

            // Create data folder if it doesn't exist
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
                System.out.println("[TimerPlugin] Created plugin data folder");
            }

            // Register events
            getServer().getPluginManager().registerEvents(this, this);
            System.out.println("[TimerPlugin] Registered events");

            // Register commands
            getCommand("timerplugin").setExecutor(this);
            System.out.println("[TimerPlugin] Registered commands");

            // Create default config
            saveDefaultConfig();
            System.out.println("[TimerPlugin] Created default config");

            // Load configuration
            loadConfigValues();
            System.out.println("[TimerPlugin] Loaded configuration values");

            // Create boss bar for timer display (only if visible)
            if (timerVisible) {
                timerBar = Bukkit.createBossBar(
                        formatTimeDisplay(timeRemainingInDays),
                        getBarColor(),
                        getBarStyle()
                );
                System.out.println("[TimerPlugin] Created boss bar");
            }

            // Register custom recipes
            registerCustomRecipes();
            System.out.println("[TimerPlugin] Registered custom recipes");

            // Start timer countdown
            startTimerCountdown();
            System.out.println("[TimerPlugin] Started timer countdown");

            // Log that the plugin has been enabled
            getLogger().info("TimerPlugin has been enabled!");
            System.out.println("[TimerPlugin] Plugin has been fully enabled!");
        } catch (Exception e) {
            getLogger().severe("Error enabling TimerPlugin: " + e.getMessage());
            System.out.println("[TimerPlugin] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerCustomRecipes() {
        try {
            // Time Mace Recipe
            ItemStack timeMace = createTimeMace();
            NamespacedKey timeMaceKey = new NamespacedKey(this, "time_mace");
            ShapedRecipe timeMaceRecipe = new ShapedRecipe(timeMaceKey, timeMace);

            timeMaceRecipe.shape(" B ", "BAB", " B ");
            timeMaceRecipe.setIngredient('B', Material.HEART_OF_THE_SEA);
            timeMaceRecipe.setIngredient('A', Material.MACE);

            Bukkit.addRecipe(timeMaceRecipe);

            System.out.println("[TimerPlugin] Registered Time Mace recipe successfully");
        } catch (Exception e) {
            getLogger().severe("Error registering recipes: " + e.getMessage());
            System.out.println("[TimerPlugin] Error registering recipes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ItemStack createTimeMace() {
        ItemStack timeMace = new ItemStack(Material.MACE, 1);
        ItemMeta meta = timeMace.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_PURPLE + "Time Mace");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "A powerful weapon forged from time itself");
        lore.add(ChatColor.YELLOW + "Right-click to dash forward (" + maxDashes + " charges)");
        lore.add(ChatColor.YELLOW + "Shift + Right-click to create a time explosion");
        lore.add(ChatColor.RED + "Cooldown: " + (maceCooldown / 1000 / 60) + " minutes");
        lore.add(ChatColor.RED + "Warning: You can take fall damage!");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(1002); // Custom model data for resource packs

        // Add enchantment glow effect for 1.21.1+
        try {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
        } catch (Exception e) {
            getLogger().warning("Could not apply glow effect to Time Mace: " + e.getMessage());
        }

        timeMace.setItemMeta(meta);
        return timeMace;
    }

    private void loadConfigValues() {
        try {
            FileConfiguration config = getConfig();

            // Load timer settings
            timeRemainingInDays = config.getDouble("timer.initial-days", 30.0);
            MAX_DAYS = config.getDouble("timer.max-days", 30.0);
            maxAddDays = config.getInt("timer.modification.max-add", 2);
            maxRemoveDays = config.getInt("timer.modification.max-remove", 2);
            minDaysToRemove = config.getDouble("timer.modification.min-days-to-remove", 2.0);
            timerVisible = config.getBoolean("timer.visible", true);

            // Time Mace settings
            maxDashes = config.getInt("time-mace.max-dashes", 3);
            maceCooldown = config.getInt("time-mace.cooldown", 120) * 1000; // Convert to milliseconds
            dashStrength = config.getDouble("time-mace.dash-strength", 2.5);
            explosionEnabled = config.getBoolean("time-mace.explosion.enabled", true);
            explosionRadius = config.getInt("time-mace.explosion.radius", 8);
            explosionDuration = config.getInt("time-mace.explosion.duration", 5);
            explosionSlownessLevel = config.getInt("time-mace.explosion.slowness-level", 3);

            // Load effects settings
            addTimeSound = config.getString("effects.add-time.sound", "BLOCK_BEACON_AMBIENT");
            addTimeSoundVolume = (float) config.getDouble("effects.add-time.sound-volume", 1.0);
            addTimeSoundPitch = (float) config.getDouble("effects.add-time.sound-pitch", 1.0);
            addTimeEffectDuration = config.getInt("effects.add-time.duration", 5);

            removeTimeSound = config.getString("effects.remove-time.sound", "ENTITY_ENDER_DRAGON_GROWL");
            removeTimeSoundVolume = (float) config.getDouble("effects.remove-time.sound-volume", 1.0);
            removeTimeSoundPitch = (float) config.getDouble("effects.remove-time.sound-pitch", 1.0);
            removeTimeEffectDuration = config.getInt("effects.remove-time.duration", 5);

            // Load messages
            msgKillerReceivedItem = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.killer-received-item", "&aYou've killed %victim%! Use the Heart of the Sea to modify the timer."));

            msgTimerZero = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.timer-zero", "&cThe timer has reached zero!"));

            msgPlayerAddedTime = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.player-added-time", "&a%player% added %days% day(s) to the timer!"));

            msgPlayerRemovedTime = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.player-removed-time", "&c%player% removed %days% day(s) from the timer!"));

            msgTimerAtMax = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.timer-at-max", "&cThe timer cannot exceed %max% days!"));

            msgTimerAtMin = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.timer-at-min", "&cThe timer cannot go below 0 days!"));

            msgNotRecentKiller = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.not-recent-killer", "&cYou can only use this item after killing a player."));

            // Load special item messages
            timeMaceDashMsg = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.time-mace.dash", "&eYou dash forward! %dashes% dashes remaining."));

            timeMaceCooldownMsg = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.time-mace.cooldown", "&cTime Mace is on cooldown. %time% seconds remaining."));

            timeMaceRechargedMsg = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.time-mace.recharged", "&aTime Mace has been recharged! You have %dashes% dashes available."));

            // Load item settings
            itemName = ChatColor.translateAlternateColorCodes('&',
                    config.getString("time-modifier-item.name", "&bTime Modifier"));

            itemLore = new ArrayList<>();
            for (String loreLine : config.getStringList("time-modifier-item.lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
            }

            // Set GUI title
            GUI_TITLE = ChatColor.DARK_AQUA + "Timer Modification";

            System.out.println("[TimerPlugin] Loaded all configuration values successfully");
        } catch (Exception e) {
            getLogger().severe("Error loading config values: " + e.getMessage());
            System.out.println("[TimerPlugin] Error loading config values: " + e.getMessage());
            e.printStackTrace();

            // Set default values
            timeRemainingInDays = 30.0;
            MAX_DAYS = 30.0;
            maxAddDays = 2;
            maxRemoveDays = 2;
            minDaysToRemove = 2.0;
            timerVisible = true;
            maxDashes = 3;
            maceCooldown = 120000; // 2 minutes in milliseconds
            dashStrength = 2.5;
            explosionEnabled = true;
            explosionRadius = 8;
            explosionDuration = 5;
            explosionSlownessLevel = 3;
            // ... other default values
            GUI_TITLE = ChatColor.DARK_AQUA + "Timer Modification";

            System.out.println("[TimerPlugin] Using default values due to config error");
        }
    }

    private BarColor getBarColor() {
        String barColorStr = getConfig().getString("display.bar-color", "BLUE");
        try {
            return BarColor.valueOf(barColorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid bar color in config: " + barColorStr + ". Using BLUE instead.");
            return BarColor.BLUE;
        }
    }

    private BarStyle getBarStyle() {
        String barStyleStr = getConfig().getString("display.bar-style", "SOLID");
        try {
            return BarStyle.valueOf(barStyleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid bar style in config: " + barStyleStr + ". Using SOLID instead.");
            return BarStyle.SOLID;
        }
    }

    @Override
    public void onDisable() {
        // Save timer state
        getConfig().set("timer.initial-days", timeRemainingInDays);
        saveConfig();

        // Remove boss bar
        if (timerBar != null) {
            timerBar.removeAll();
        }

        // Log that the plugin has been disabled
        getLogger().info("TimerPlugin has been disabled!");
    }

    private void startTimerCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Decrease time by 1 second
                timeRemainingInDays -= 1.0 / DAY_IN_SECONDS;

                // Update the bar
                updateTimerBar();

                // Check if timer has reached zero
                if (timeRemainingInDays <= 0) {
                    timeRemainingInDays = 0;
                    Bukkit.broadcastMessage(msgTimerZero);
                    // Here you could implement what happens when the timer reaches zero
                }
            }
        }.runTaskTimer(this, 20L, 20L); // Run every second (20 ticks)
    }

    private void updateTimerBar() {
        // Only update if timer is visible
        if (!timerVisible || timerBar == null) return;

        // Update boss bar progress and title
        double progress = Math.max(0, Math.min(1, timeRemainingInDays / MAX_DAYS));
        timerBar.setProgress(progress);
        timerBar.setTitle(formatTimeDisplay(timeRemainingInDays));

        // Show the boss bar to all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            timerBar.addPlayer(player);
        }
    }

    private String formatTimeDisplay(double daysRemaining) {
        int days = (int) daysRemaining;
        int hours = (int) ((daysRemaining - days) * 24);
        int minutes = (int) ((daysRemaining - days - hours / 24.0) * 24 * 60);

        String format = getConfig().getString("display.bar-title",
                "&6Time Remaining: &f%d days, %h hours, %m minutes");

        return ChatColor.translateAlternateColorCodes('&', format
                .replace("%d", String.valueOf(days))
                .replace("%h", String.valueOf(hours))
                .replace("%m", String.valueOf(minutes)));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add player to boss bar (only if visible)
        if (timerVisible && timerBar != null) {
            timerBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Check if the player was killed by another player
        if (killer != null) {
            // Store the killer-victim pair
            recentKillers.put(killer.getUniqueId(), victim.getUniqueId());

            // Give the killer the time modifier item
            giveTimeModifierItem(killer);

            // Notify the killer
            killer.sendMessage(msgKillerReceivedItem.replace("%victim%", victim.getName()));
        }
    }

    private void giveTimeModifierItem(Player player) {
        // Create the Heart of the Sea item
        ItemStack heartOfTheSea = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = heartOfTheSea.getItemMeta();

        // Set display name and lore
        meta.setDisplayName(itemName);
        meta.setLore(itemLore);

        heartOfTheSea.setItemMeta(meta);

        // Give the item to the player
        player.getInventory().addItem(heartOfTheSea);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        // Check if the player is using the Time Modifier item
        if (item.getType() == Material.HEART_OF_THE_SEA &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(itemName)) {

            // Check if the player is a recent killer
            if (!recentKillers.containsKey(player.getUniqueId())) {
                player.sendMessage(msgNotRecentKiller);
                return;
            }

            // Cancel the event to prevent normal interactions
            event.setCancelled(true);

            // Open the GUI for the player
            openTimerModificationGUI(player);
        }
        // Check if the player is using the Time Mace
        else if (item.getType() == Material.MACE &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Time Mace")) {

            // Check cooldown
            if (timeMaceCooldowns.containsKey(player.getUniqueId())) {
                long timeLeft = (timeMaceCooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                if (timeLeft > 0) {
                    player.sendMessage(timeMaceCooldownMsg.replace("%time%", String.valueOf(timeLeft)));
                    event.setCancelled(true);
                    return;
                }
            }

            // Check if player has remaining dashes
            int dashes = remainingDashes.getOrDefault(player.getUniqueId(), maxDashes);
            if (dashes <= 0) {
                player.sendMessage(ChatColor.RED + "No more dashes remaining. Wait for cooldown to reset.");
                event.setCancelled(true);
                return;
            }

            // Cancel the event to prevent normal interactions
            event.setCancelled(true);

            // Check if player is sneaking (Shift + Right-click)
            if (player.isSneaking()) {
                // Execute time explosion
                executeTimeExplosion(player);

                // Use all remaining dashes for this powerful ability
                remainingDashes.put(player.getUniqueId(), 0);

                // Set cooldown
                timeMaceCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + maceCooldown);

                // Schedule a task to reset dashes after cooldown
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        remainingDashes.put(player.getUniqueId(), maxDashes);
                        player.sendMessage(timeMaceRechargedMsg.replace("%dashes%", String.valueOf(maxDashes)));
                    }
                }.runTaskLater(this, maceCooldown / 50); // Convert milliseconds to ticks

                // Notify player
                player.sendMessage(ChatColor.GOLD + "TIME EXPLOSION! " + ChatColor.RED + "All charges consumed!");
            } else {
                // Execute normal dash
                executeDash(player);

                // Decrease remaining dashes
                remainingDashes.put(player.getUniqueId(), dashes - 1);

                // If no more dashes, set cooldown
                if (dashes - 1 <= 0) {
                    timeMaceCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + maceCooldown);

                    // Schedule a task to reset dashes after cooldown
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            remainingDashes.put(player.getUniqueId(), maxDashes);
                            player.sendMessage(timeMaceRechargedMsg.replace("%dashes%", String.valueOf(maxDashes)));
                        }
                    }.runTaskLater(this, maceCooldown / 50); // Convert milliseconds to ticks
                }

                // Notify player of remaining dashes
                player.sendMessage(timeMaceDashMsg.replace("%dashes%", String.valueOf(dashes - 1)));
            }
        }
    }

    private void executeDash(Player player) {
        // Get the direction the player is looking
        Vector direction = player.getLocation().getDirection();

        // Multiply by dash strength from config
        direction.multiply(dashStrength);

        // Set a maximum y velocity to prevent too much vertical movement
        if (direction.getY() > 0.5) {
            direction.setY(0.5);
        }

        // Apply the velocity to the player
        player.setVelocity(direction);

        // Visual effects
        Location loc = player.getLocation();
        World world = player.getWorld();

        // Particle effects
        try {
            world.spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME, loc, 20, 0.2, 0.2, 0.2, 0.05);
            world.spawnParticle(org.bukkit.Particle.DRAGON_BREATH, loc, 15, 0.1, 0.1, 0.1, 0.03);
            world.playSound(loc, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.4f);
        } catch (Exception e) {
            // Fallback to older effect system
            try {
                player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.ENDER_SIGNAL, 0);
            } catch (Exception ex) {
                getLogger().warning("Could not create dash particle effects: " + ex.getMessage());
            }
        }
    }

    private void executeTimeExplosion(Player player) {
        // Check if explosions are enabled in the config
        if (!explosionEnabled) {
            player.sendMessage(ChatColor.RED + "Time explosions are disabled in the config!");
            return;
        }

        Location location = player.getLocation();
        World world = location.getWorld();

        if (world == null) return;

        try {
            // Visual effects
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1, 0, 0, 0, 0);
            world.spawnParticle(org.bukkit.Particle.PORTAL, location, 100, 3, 3, 3, 1);
            world.spawnParticle(org.bukkit.Particle.END_ROD, location, 50, 3, 3, 3, 0.5);

            // Sound effects
            world.playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
            world.playSound(location, org.bukkit.Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 1.2f);

            // Affect nearby entities (freeze and push away)
            for (Entity entity : player.getNearbyEntities(explosionRadius, explosionRadius, explosionRadius)) {
                if (entity instanceof LivingEntity && entity != player) {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    // Calculate push direction (away from explosion)
                    Vector pushDir = entity.getLocation().toVector().subtract(location.toVector()).normalize().multiply(2.0);
                    pushDir.setY(Math.min(1.0, pushDir.getY() + 0.5)); // Add some upward motion

                    // Apply velocity
                    entity.setVelocity(pushDir);

                    // Apply effects
                    if (entity instanceof Player) {
                        ((Player) entity).sendMessage(ChatColor.DARK_PURPLE + player.getName() +
                                " has created a time explosion!");
                    }

                    // Freeze effect (slowness + jump prevention)
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, explosionSlownessLevel));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 128));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 140, 1));
                }
            }

            // Create a temporary time distortion zone
            new BukkitRunnable() {
                int ticksRun = 0;
                final int maxTicks = explosionDuration * 20;

                @Override
                public void run() {
                    ticksRun++;

                    // Create ring of particles
                    double radius = 5.0 + Math.sin(ticksRun * 0.1) * 1.0;
                    for (int i = 0; i < 16; i++) {
                        double angle = i * Math.PI / 8;
                        double x = location.getX() + radius * Math.cos(angle);
                        double z = location.getZ() + radius * Math.sin(angle);
                        Location particleLoc = new Location(world, x, location.getY() + 0.1, z);

                        world.spawnParticle(org.bukkit.Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0.01);
                    }

                    // Slow entities that enter the zone
                    for (Entity entity : world.getNearbyEntities(location, radius, 5, radius)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            ((LivingEntity) entity).addPotionEffect(
                                    new PotionEffect(PotionEffectType.SLOWNESS, 20, explosionSlownessLevel));
                        }
                    }

                    if (ticksRun >= maxTicks) {
                        // Final explosion effect when zone disappears
                        world.spawnParticle(org.bukkit.Particle.FLASH, location, 10, 5, 5, 5, 0);
                        world.playSound(location, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                        this.cancel();
                    }
                }
            }.runTaskTimer(this, 0L, 1L);

        } catch (Exception e) {
            getLogger().warning("Error executing time explosion: " + e.getMessage());
        }
    }

    private void openTimerModificationGUI(Player player) {
        // Create inventory for GUI (3 rows = 27 slots)
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        // Calculate positions for dynamic centering
        int totalOptions = maxAddDays + maxRemoveDays;
        int startSlot = Math.max(9, 13 - (totalOptions / 2));

        // Add day options
        int currentSlot = startSlot;
        for (int i = 1; i <= maxAddDays; i++) {
            ItemStack addDays = createGuiItem(
                    i == 1 ? Material.LIME_WOOL : Material.GREEN_WOOL,
                    ChatColor.GREEN + "Add " + i + " Day" + (i > 1 ? "s" : ""),
                    ChatColor.GRAY + "Click to add " + i + " day" + (i > 1 ? "s" : "") + " to the timer"
            );
            gui.setItem(currentSlot++, addDays);
        }

        // Skip a slot between add and remove options
        currentSlot++;

        // Remove day options
        for (int i = 1; i <= maxRemoveDays; i++) {
            ItemStack removeDays = createGuiItem(
                    i == 1 ? Material.PINK_WOOL : Material.RED_WOOL,
                    ChatColor.RED + "Remove " + i + " Day" + (i > 1 ? "s" : ""),
                    ChatColor.GRAY + "Click to remove " + i + " day" + (i > 1 ? "s" : "") + " from the timer"
            );
            gui.setItem(currentSlot++, removeDays);
        }

        // Add info panel at the bottom center
        ItemStack info = createGuiItem(Material.PAPER, ChatColor.GOLD + "Timer Information",
                ChatColor.GRAY + "Current time: " + formatTimeDisplay(timeRemainingInDays),
                ChatColor.GRAY + "Maximum time: " + MAX_DAYS + " days",
                ChatColor.GRAY + "Minimum to remove: " + minDaysToRemove + " days");
        gui.setItem(22, info);

        // Fill empty slots with glass panes
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        // Open the GUI for the player
        player.openInventory(gui);
    }

    private void openMaceConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_PURPLE + "Time Mace Configuration");

        // Max Dashes setting
        gui.setItem(10, createGuiItem(Material.FEATHER, ChatColor.YELLOW + "Max Dashes: " + maxDashes,
                ChatColor.GRAY + "Click to change the maximum dashes"));

        // Cooldown setting
        gui.setItem(11, createGuiItem(Material.CLOCK, ChatColor.YELLOW + "Cooldown: " + (maceCooldown / 1000) + "s",
                ChatColor.GRAY + "Click to change the cooldown"));

        // Dash Strength setting
        gui.setItem(12, createGuiItem(Material.ARROW, ChatColor.YELLOW + "Dash Strength: " + dashStrength,
                ChatColor.GRAY + "Click to change the dash strength"));

        // Explosion enabled toggle
        gui.setItem(13, createGuiItem(explosionEnabled ? Material.TNT : Material.BARRIER,
                ChatColor.YELLOW + "Explosions: " + (explosionEnabled ? "Enabled" : "Disabled"),
                ChatColor.GRAY + "Click to toggle time explosions"));

        // Explosion radius
        gui.setItem(14, createGuiItem(Material.BLAZE_POWDER, ChatColor.YELLOW + "Explosion Radius: " + explosionRadius,
                ChatColor.GRAY + "Click to change explosion radius"));

        // Back button
        gui.setItem(31, createGuiItem(Material.ARROW, ChatColor.RED + "Back", ChatColor.GRAY + "Return to main menu"));

        // Fill empty slots
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 36; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    private void openTimerConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Timer Configuration");

        // Timer visibility toggle
        gui.setItem(10, createGuiItem(timerVisible ? Material.ENDER_EYE : Material.ENDER_PEARL,
                ChatColor.YELLOW + "Timer Visibility: " + (timerVisible ? "Visible" : "Hidden"),
                ChatColor.GRAY + "Click to toggle timer visibility"));

        // Max Days setting
        gui.setItem(11, createGuiItem(Material.BOOK, ChatColor.YELLOW + "Max Days: " + (int)MAX_DAYS,
                ChatColor.GRAY + "Click to change maximum days"));

        // Current time setting
        gui.setItem(12, createGuiItem(Material.CLOCK, ChatColor.YELLOW + "Current Time: " + String.format("%.1f", timeRemainingInDays) + " days",
                ChatColor.GRAY + "Click to change current time"));

        // Max Add Days
        gui.setItem(13, createGuiItem(Material.GREEN_WOOL, ChatColor.YELLOW + "Max Add Days: " + maxAddDays,
                ChatColor.GRAY + "Click to change max days that can be added"));

        // Max Remove Days
        gui.setItem(14, createGuiItem(Material.RED_WOOL, ChatColor.YELLOW + "Max Remove Days: " + maxRemoveDays,
                ChatColor.GRAY + "Click to change max days that can be removed"));

        // Back button
        gui.setItem(31, createGuiItem(Material.ARROW, ChatColor.RED + "Back", ChatColor.GRAY + "Return to main menu"));

        // Fill empty slots
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 36; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    private void openMainConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "Timer Plugin Config");

        // Mace Configuration
        gui.setItem(11, createGuiItem(Material.MACE, ChatColor.DARK_PURPLE + "Time Mace Config",
                ChatColor.GRAY + "Configure Time Mace settings"));

        // Timer Configuration
        gui.setItem(13, createGuiItem(Material.CLOCK, ChatColor.GOLD + "Timer Config",
                ChatColor.GRAY + "Configure timer settings"));

        // Reload Config
        gui.setItem(15, createGuiItem(Material.NETHER_STAR, ChatColor.GREEN + "Reload Config",
                ChatColor.GRAY + "Reload configuration from file"));

        // Fill empty slots
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(line);
        }

        meta.setLore(loreList);
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        // Check if this is any of our GUIs
        if (title.equals(GUI_TITLE) || title.equals(ChatColor.DARK_BLUE + "Timer Plugin Config") ||
                title.equals(ChatColor.DARK_PURPLE + "Time Mace Configuration") ||
                title.equals(ChatColor.GOLD + "Timer Configuration")) {

            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                return;
            }

            if (title.equals(GUI_TITLE)) {
                handleTimerModificationGUI(player, clickedItem);
            } else if (title.equals(ChatColor.DARK_BLUE + "Timer Plugin Config")) {
                handleMainConfigGUI(player, clickedItem);
            } else if (title.equals(ChatColor.DARK_PURPLE + "Time Mace Configuration")) {
                handleMaceConfigGUI(player, clickedItem);
            } else if (title.equals(ChatColor.GOLD + "Timer Configuration")) {
                handleTimerConfigGUI(player, clickedItem);
            }
        }
    }

    private void handleTimerModificationGUI(Player player, ItemStack clickedItem) {
        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();

            // Check if adding days
            for (int i = 1; i <= maxAddDays; i++) {
                if (displayName.equals(ChatColor.GREEN + "Add " + i + " Day" + (i > 1 ? "s" : ""))) {
                    modifyTimer(player, i, false);
                    player.closeInventory();
                    return;
                }
            }

            // Check if removing days
            for (int i = 1; i <= maxRemoveDays; i++) {
                if (displayName.equals(ChatColor.RED + "Remove " + i + " Day" + (i > 1 ? "s" : ""))) {
                    modifyTimer(player, -i, false);
                    player.closeInventory();
                    return;
                }
            }
        }
    }

    private void handleMainConfigGUI(Player player, ItemStack clickedItem) {
        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName.equals(ChatColor.DARK_PURPLE + "Time Mace Config")) {
            openMaceConfigGUI(player);
        } else if (displayName.equals(ChatColor.GOLD + "Timer Config")) {
            openTimerConfigGUI(player);
        } else if (displayName.equals(ChatColor.GREEN + "Reload Config")) {
            reloadConfig();
            loadConfigValues();
            updateTimerBar();
            player.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
            player.closeInventory();
        }
    }

    private void handleMaceConfigGUI(Player player, ItemStack clickedItem) {
        // Handle mace config clicks - this would need more complex input handling
        // For now, just show a message
        player.sendMessage(ChatColor.YELLOW + "Mace configuration - use commands for now: /timerplugin setmaceconfig <setting> <value>");
        player.closeInventory();
    }

    private void handleTimerConfigGUI(Player player, ItemStack clickedItem) {
        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName.contains("Timer Visibility")) {
            // Toggle timer visibility
            timerVisible = !timerVisible;
            getConfig().set("timer.visible", timerVisible);
            saveConfig();

            // Update boss bar
            if (timerVisible && timerBar == null) {
                timerBar = Bukkit.createBossBar(
                        formatTimeDisplay(timeRemainingInDays),
                        getBarColor(),
                        getBarStyle()
                );
            } else if (!timerVisible && timerBar != null) {
                timerBar.removeAll();
                timerBar = null;
            }

            player.sendMessage(ChatColor.GREEN + "Timer visibility set to: " + (timerVisible ? "Visible" : "Hidden"));
            openTimerConfigGUI(player); // Refresh GUI
        } else if (displayName.equals(ChatColor.RED + "Back")) {
            openMainConfigGUI(player);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Use commands for detailed configuration: /timerplugin settime <days>");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onCraftItem(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;

        // Check if crafting Time Mace that needs custom Heart of the Sea
        if (result.getType() == Material.MACE && result.hasItemMeta() &&
                result.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Time Mace")) {

            boolean hasCustomHeartOfSea = false;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.HEART_OF_THE_SEA) {
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                            item.getItemMeta().getDisplayName().equals(itemName)) {
                        hasCustomHeartOfSea = true;
                        break;
                    }
                }
            }

            if (!hasCustomHeartOfSea) {
                event.getInventory().setResult(null);
                if (event.getView().getPlayer() instanceof Player) {
                    Player player = (Player) event.getView().getPlayer();
                    player.sendMessage(ChatColor.RED + "This recipe requires the Heart of the Sea obtained from killing players!");
                }
            }
        }
    }

    private void modifyTimer(Player player, int days, boolean gainLife) {
        double newTime = timeRemainingInDays + days;

        // Check constraints
        if (days > 0) {
            if (newTime > MAX_DAYS) {
                player.sendMessage(msgTimerAtMax.replace("%max%", String.valueOf((int)MAX_DAYS)));
                return;
            }
        } else {
            if (timeRemainingInDays < minDaysToRemove && days < 0) {
                player.sendMessage(ChatColor.RED + "Cannot remove days when timer is below " + minDaysToRemove + " days!");
                return;
            }

            if (newTime < 0) {
                player.sendMessage(msgTimerAtMin);
                return;
            }
        }

        // Update the timer
        timeRemainingInDays = newTime;
        updateTimerBar();

        // Notify the player and the server
        String message;
        if (days > 0) {
            message = msgPlayerAddedTime
                    .replace("%player%", player.getName())
                    .replace("%days%", String.valueOf(days));
            playAddDayEffects(player.getLocation());
        } else {
            message = msgPlayerRemovedTime
                    .replace("%player%", player.getName())
                    .replace("%days%", String.valueOf(Math.abs(days)));
            playRemoveDayEffects(player.getLocation());
        }

        player.sendMessage(message);
        Bukkit.broadcastMessage(message);

        // Remove the time modifier item
        removeTimeModifierItem(player);

        // Remove player from recent killers
        recentKillers.remove(player.getUniqueId());
    }

    private void playAddDayEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        try {
            Sound sound = Sound.valueOf(addTimeSound);
            world.playSound(location, sound, addTimeSoundVolume, addTimeSoundPitch);

            for (int i = 1; i < addTimeEffectDuration; i++) {
                final int index = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        world.playSound(location, sound, addTimeSoundVolume, addTimeSoundPitch);
                    }
                }.runTaskLater(this, i * 20L);
            }
        } catch (Exception e) {
            world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 1.0f, 1.0f);
        }

        // Spawn particles
        new BukkitRunnable() {
            int ticksRun = 0;
            final int maxTicks = addTimeEffectDuration * 20;

            @Override
            public void run() {
                ticksRun++;

                for (int i = 0; i < 8; i++) {
                    double angle = (ticksRun * 0.5) + (i * Math.PI / 4);
                    double x = location.getX() + 1.5 * Math.cos(angle);
                    double z = location.getZ() + 1.5 * Math.sin(angle);
                    Location particleLoc = new Location(world, x, location.getY() + 1 + (ticksRun % 10) * 0.1, z);

                    world.spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 5, 0.2, 0.2, 0.2, 0.0);
                    world.spawnParticle(org.bukkit.Particle.COMPOSTER, particleLoc, 2, 0.2, 0.2, 0.2, 0.0);
                }

                if (ticksRun >= maxTicks) {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    private void playRemoveDayEffects(Location location) {
        org.bukkit.World world = location.getWorld();
        if (world == null) return;

        try {
            Sound sound = Sound.valueOf(removeTimeSound);
            world.playSound(location, sound, removeTimeSoundVolume, removeTimeSoundPitch);

            for (int i = 1; i < removeTimeEffectDuration; i++) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        world.playSound(location, sound, removeTimeSoundVolume, removeTimeSoundPitch);
                    }
                }.runTaskLater(this, i * 20L);
            }
        } catch (Exception e) {
            world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        }

        // Spawn particles
        new BukkitRunnable() {
            int ticksRun = 0;
            final int maxTicks = removeTimeEffectDuration * 20;

            @Override
            public void run() {
                ticksRun++;

                for (int i = 0; i < 8; i++) {
                    double angle = (ticksRun * 0.5) + (i * Math.PI / 4);
                    double x = location.getX() + 2 * Math.cos(angle);
                    double z = location.getZ() + 2 * Math.sin(angle);
                    Location particleLoc = new Location(world, x, location.getY() + 1 + (ticksRun % 20) * 0.1, z);

                    try {
                        org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.0f);
                        world.spawnParticle(Particle.DUST, particleLoc, 10, 0.3, 0.3, 0.3, 0.0, dustOptions);
                    } catch (Exception e) {
                        world.spawnParticle(org.bukkit.Particle.FLAME, particleLoc, 5, 0.2, 0.2, 0.2, 0.01);
                        world.spawnParticle(org.bukkit.Particle.LAVA, particleLoc, 1, 0.1, 0.1, 0.1, 0.01);
                    }
                }

                if (ticksRun >= maxTicks) {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    private void removeTimeModifierItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.HEART_OF_THE_SEA &&
                    item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                    item.getItemMeta().getDisplayName().equals(itemName)) {

                item.setAmount(item.getAmount() - 1);
                player.updateInventory();
                break;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("timerplugin")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("settime") && sender.hasPermission("timerplugin.admin")) {
                    if (args.length > 1) {
                        try {
                            double newTime = Double.parseDouble(args[1]);
                            if (newTime >= 0 && newTime <= MAX_DAYS) {
                                timeRemainingInDays = newTime;
                                updateTimerBar();
                                sender.sendMessage(ChatColor.GREEN + "Timer set to " + newTime + " days!");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Time must be between 0 and " + MAX_DAYS + " days!");
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Invalid number format!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /timerplugin settime <days>");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("timerplugin.admin")) {
                    reloadConfig();
                    loadConfigValues();
                    updateTimerBar();
                    sender.sendMessage(ChatColor.GREEN + "Timer Plugin configuration reloaded!");
                    return true;
                } else if (args[0].equalsIgnoreCase("configgui") && sender.hasPermission("timerplugin.admin")) {
                    if (sender instanceof Player) {
                        openMainConfigGUI((Player) sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use the config GUI!");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("givespecial") && sender.hasPermission("timerplugin.admin")) {
                    if (args.length > 2) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "Player not found!");
                            return true;
                        }

                        if (args[2].equalsIgnoreCase("mace")) {
                            target.getInventory().addItem(createTimeMace());
                            sender.sendMessage(ChatColor.GREEN + "Gave Time Mace to " + target.getName());
                            target.sendMessage(ChatColor.GREEN + "You received a Time Mace!");
                        } else if (args[2].equalsIgnoreCase("heartofthesea")) {
                            giveTimeModifierItem(target);
                            sender.sendMessage(ChatColor.GREEN + "Gave custom Heart of the Sea to " + target.getName());
                            target.sendMessage(ChatColor.GREEN + "You received a custom Heart of the Sea!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Unknown special item! Use 'mace' or 'heartofthesea'");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /timerplugin givespecial <player> <mace|heartofthesea>");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("toggletimer") && sender.hasPermission("timerplugin.admin")) {
                    timerVisible = !timerVisible;
                    getConfig().set("timer.visible", timerVisible);
                    saveConfig();

                    if (timerVisible && timerBar == null) {
                        timerBar = Bukkit.createBossBar(
                                formatTimeDisplay(timeRemainingInDays),
                                getBarColor(),
                                getBarStyle()
                        );
                    } else if (!timerVisible && timerBar != null) {
                        timerBar.removeAll();
                        timerBar = null;
                    }

                    sender.sendMessage(ChatColor.GREEN + "Timer visibility set to: " + (timerVisible ? "Visible" : "Hidden"));
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(ChatColor.GOLD + "Timer Plugin Information:");
                    sender.sendMessage(ChatColor.YELLOW + "Current time: " + formatTimeDisplay(timeRemainingInDays));
                    sender.sendMessage(ChatColor.YELLOW + "Timer visible: " + (timerVisible ? "Yes" : "No"));
                    sender.sendMessage(ChatColor.YELLOW + "Maximum days: " + MAX_DAYS);
                    sender.sendMessage(ChatColor.YELLOW + "Min days to remove: " + minDaysToRemove);
                    sender.sendMessage(ChatColor.YELLOW + "Max days to add: " + maxAddDays);
                    sender.sendMessage(ChatColor.YELLOW + "Max days to remove: " + maxRemoveDays);
                    return true;
                }
            }
            // Show help if no args or unknown command
            sender.sendMessage(ChatColor.GOLD + "Timer Plugin Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/timerplugin info - Show plugin information");

            if (sender.hasPermission("timerplugin.admin")) {
                sender.sendMessage(ChatColor.YELLOW + "/timerplugin configgui - Open configuration GUI");
                sender.sendMessage(ChatColor.YELLOW + "/timerplugin settime <days> - Set the timer value");
                sender.sendMessage(ChatColor.YELLOW + "/timerplugin toggletimer - Toggle timer visibility");
                sender.sendMessage(ChatColor.YELLOW + "/timerplugin reload - Reload the configuration");
                sender.sendMessage(ChatColor.YELLOW + "/timerplugin givespecial <player> <mace|heartofthesea> - Give special items");
            }
            return true;
        }
        return false;
    }
}