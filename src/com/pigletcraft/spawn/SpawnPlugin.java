package com.pigletcraft.spawn;

import com.pigletcraft.spawn.offerings.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.block.Skull;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/**
 * Spawn plugin for Pigletcraft
 *
 * @author Ben Carvell
 * @author Geoff Wilson
 */
public class SpawnPlugin extends JavaPlugin implements Listener {

    // WorldGuard
    private WorldGuardPlugin worldGuard;
    private int fireWorkTaskID;
    private int pigletRainTaskID;
    private int pigletUndoTaskID;
    private ArrayList<FireworkLocation> fireworkSpawnerLocations;
    private int fireworkCounter = 0;
    private ConcurrentLinkedQueue<Pig> pigRain;
    private Color[] colors;
    private HashMap<Material, Offering> configuredOfferings;
    private boolean canBillyBomb = true;

    public boolean getCanBillyBomb() {
        return canBillyBomb;
    }

    public synchronized void setCanBillyBomb(boolean value) {
        this.canBillyBomb = value;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
        return (WorldGuardPlugin) plugin;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        worldGuard = getWorldGuard();

        colors = new Color[]{Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON, Color.fromRGB(0xFF7F00), Color.fromRGB(0xD63400), Color.fromRGB(0x8E0F08), Color.fromRGB(0xDEF3F8), Color.fromRGB(0x32A2A4), Color.fromRGB(0x17529E), Color.NAVY, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW, Color.fromRGB(255, 123, 0)};

        this.pigRain = new ConcurrentLinkedQueue<>();
        this.fireworkSpawnerLocations = new ArrayList<>();

        configureOfferings();

        try {
            FileInputStream fileInputStream = new FileInputStream("fireworks.obj");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            fireworkSpawnerLocations = (ArrayList<FireworkLocation>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void configureOfferings() {

        this.configuredOfferings = new HashMap<>();
        World world = Bukkit.getWorld("world");

        // Pork Chop
        configuredOfferings.put(Material.GRILLED_PORK, new PorkChopOffering(this, world));
        configuredOfferings.put(Material.PORK, new PorkChopOffering(this, world));

        // Carrot
        configuredOfferings.put(Material.CARROT_ITEM, new CarrotOffering(this, world));

        // Beef
        //configuredOfferings.put(Material.RAW_BEEF, new BeefOffering(this, world));
        //configuredOfferings.put(Material.COOKED_BEEF, new BeefOffering(this, world));

        //Gravel
        configuredOfferings.put(Material.GRAVEL, new GravelOffering(this, world));

        //Cobblestone
        configuredOfferings.put(Material.COBBLESTONE, new CobbleStoneOffering(this, world));

        //Cake
        configuredOfferings.put(Material.CAKE, new CakeOffering(this, world));

        //Golden Carrot
        configuredOfferings.put(Material.GOLDEN_CARROT, new GoldenCarrotOffering(this, world));

        //Leather Armour
        configuredOfferings.put(Material.LEATHER_HELMET, new LeatherArmourOffering(this, world));
        configuredOfferings.put(Material.LEATHER_CHESTPLATE, new LeatherArmourOffering(this, world));
        configuredOfferings.put(Material.LEATHER_LEGGINGS, new LeatherArmourOffering(this, world));
        configuredOfferings.put(Material.LEATHER_BOOTS, new LeatherArmourOffering(this, world));

        //Dirt
        configuredOfferings.put(Material.DIRT, new DirtOffering(this, world));

        //Grass
        configuredOfferings.put(Material.GRASS, new GrassOffering(this, world));

        //Stone
        configuredOfferings.put(Material.STONE, new StoneOffering(this, world));

        //Plank
        configuredOfferings.put(Material.WOOD, new PlankOffering(this, world));

        //GoldOre
        configuredOfferings.put(Material.GOLD_ORE, new GoldOreOffering(this, world));

        //Sand
        configuredOfferings.put(Material.SAND, new SandOffering(this, world));
        configuredOfferings.put(Material.SANDSTONE, new SandOffering(this, world));

        //IronOre
        configuredOfferings.put(Material.IRON_ORE, new IronOreOffering(this, world));

        //Coal
        configuredOfferings.put(Material.COAL_ORE, new CoalOreOffering(this, world));
        configuredOfferings.put(Material.COAL_BLOCK, new CoalOreOffering(this, world));
        configuredOfferings.put(Material.COAL, new CoalOreOffering(this, world));

        //Log
        configuredOfferings.put(Material.LOG, new LogOffering(this, world));
        configuredOfferings.put(Material.LOG_2, new LogOffering(this, world));

        //Glass
        configuredOfferings.put(Material.GLASS, new GlassOffering(this, world));
        configuredOfferings.put(Material.THIN_GLASS, new GlassOffering(this, world));
        configuredOfferings.put(Material.STAINED_GLASS, new GlassOffering(this, world));
        configuredOfferings.put(Material.STAINED_GLASS_PANE, new GlassOffering(this, world));

        //Lapis
        configuredOfferings.put(Material.LAPIS_BLOCK, new LapisOffering(this, world));
        configuredOfferings.put(Material.LAPIS_ORE, new LapisOffering(this, world));

        //Wool
        configuredOfferings.put(Material.WOOL, new WoolOffering(this, world));

        //Gold
        configuredOfferings.put(Material.GOLD_BLOCK, new GoldOffering(this, world));
        configuredOfferings.put(Material.GOLD_INGOT, new GoldOffering(this, world));
        configuredOfferings.put(Material.GOLD_NUGGET, new GoldOffering(this, world));

        //Iron
        configuredOfferings.put(Material.IRON_BLOCK, new IronOffering(this, world));
        configuredOfferings.put(Material.IRON_INGOT, new IronOffering(this, world));

        //Stairs
        configuredOfferings.put(Material.SANDSTONE_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.ACACIA_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.BIRCH_WOOD_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.BRICK_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.COBBLESTONE_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.DARK_OAK_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.JUNGLE_WOOD_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.NETHER_BRICK_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.QUARTZ_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.SMOOTH_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.SPRUCE_WOOD_STAIRS, new StairsOffering(this, world));
        configuredOfferings.put(Material.WOOD_STAIRS, new StairsOffering(this, world));

        //Step
        configuredOfferings.put(Material.STEP, new StepOffering(this, world));
        configuredOfferings.put(Material.WOOD_STEP, new StepOffering(this, world));

        //Brick
        configuredOfferings.put(Material.BRICK, new BrickOffering(this, world));
        configuredOfferings.put(Material.SMOOTH_BRICK, new BrickOffering(this, world));

        //Bookshelf
        configuredOfferings.put(Material.BOOKSHELF, new BookshelfOffering(this, world));
        configuredOfferings.put(Material.BOOK, new BookshelfOffering(this, world));
        configuredOfferings.put(Material.BOOK_AND_QUILL, new BookshelfOffering(this, world));
        configuredOfferings.put(Material.ENCHANTED_BOOK, new BookshelfOffering(this, world));
        configuredOfferings.put(Material.WRITTEN_BOOK, new BookshelfOffering(this, world));

        //Obsidian
        configuredOfferings.put(Material.OBSIDIAN, new ObsidianOffering(this, world));

        //Diamond
        configuredOfferings.put(Material.DIAMOND, new DiamondOffering(this, world));
        configuredOfferings.put(Material.DIAMOND_BLOCK, new DiamondOffering(this, world));

        //Ice
        configuredOfferings.put(Material.ICE, new IceOffering(this, world));
        configuredOfferings.put(Material.PACKED_ICE, new IceOffering(this, world));

        //Snow
        configuredOfferings.put(Material.SNOW, new SnowOffering(this, world));
        configuredOfferings.put(Material.SNOW_BALL, new SnowOffering(this, world));
        configuredOfferings.put(Material.SNOW_BLOCK, new SnowOffering(this, world));

        //Clay
        configuredOfferings.put(Material.CLAY, new ClayOffering(this, world));
        configuredOfferings.put(Material.CLAY_BALL, new ClayOffering(this, world));
        configuredOfferings.put(Material.CLAY_BRICK, new ClayOffering(this, world));
        configuredOfferings.put(Material.STAINED_CLAY, new ClayOffering(this, world));
        configuredOfferings.put(Material.HARD_CLAY, new ClayOffering(this, world));

        //Pumpkin
        configuredOfferings.put(Material.PUMPKIN, new PumpkinOffering(this, world));
        configuredOfferings.put(Material.JACK_O_LANTERN, new PumpkinOffering(this, world));

        //Nether
        configuredOfferings.put(Material.NETHERRACK, new NetherOffering(this, world));
        configuredOfferings.put(Material.NETHER_BRICK, new NetherOffering(this, world));
        configuredOfferings.put(Material.NETHER_BRICK_ITEM, new NetherOffering(this, world));
        configuredOfferings.put(Material.SOUL_SAND, new NetherOffering(this, world));
        configuredOfferings.put(Material.QUARTZ_ORE, new NetherOffering(this, world));

        //Glowstone
        configuredOfferings.put(Material.GLOWSTONE, new GlowstoneOffering(this, world));
        configuredOfferings.put(Material.GLOWSTONE_DUST, new GlowstoneOffering(this, world));

        //Melon
        configuredOfferings.put(Material.MELON, new MelonOffering(this, world));
        configuredOfferings.put(Material.MELON_BLOCK, new MelonOffering(this, world));

        //Endstone
        configuredOfferings.put(Material.ENDER_STONE, new EndstoneOffering(this, world));

        //Emerald
        configuredOfferings.put(Material.EMERALD, new EmeraldOffering(this, world));
        configuredOfferings.put(Material.EMERALD_BLOCK, new EmeraldOffering(this, world));
        configuredOfferings.put(Material.EMERALD_ORE, new EmeraldOffering(this, world));

        //Emerald
        configuredOfferings.put(Material.QUARTZ, new QuartzOffering(this, world));
        configuredOfferings.put(Material.QUARTZ_BLOCK, new QuartzOffering(this, world));


    }

    @Override
    public void onDisable() {

        try {
            FileOutputStream fileOutput = new FileOutputStream("fireworks.obj");
            ObjectOutputStream oos = new ObjectOutputStream(fileOutput);
            oos.writeObject(fireworkSpawnerLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void spawnPIG(Player player) {
        Location location = player.getLocation();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        int[] xLocations = {x - 5, x + 5, x - 5, x + 5};
        int[] zLocations = {z + 5, z + 5, z - 5, z - 5};

        for (int i = 0; i < 4; i++) {
            double random = Math.random();

            Zombie guard;

            Location spawnLocation = new Location(player.getWorld(), xLocations[i], y, zLocations[i]);
            guard = (Zombie) player.getWorld().spawnEntity(spawnLocation, random >= 0.50 ? EntityType.ZOMBIE : EntityType.PIG_ZOMBIE);
            EntityEquipment ee = guard.getEquipment();

            HashMap<Enchantment, Integer> enchantmentIntegerHashMap = new HashMap<>();
            enchantmentIntegerHashMap.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            enchantmentIntegerHashMap.put(Enchantment.THORNS, 3);

            ItemStack skullHead = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skullMeta = (SkullMeta) skullHead.getItemMeta();
            skullMeta.setOwner("BillyLeBoar");
            skullHead.setItemMeta(skullMeta);

            ee.setHelmet(random >= 0.5 ? skullHead : setArmorColor(Color.BLACK, new ItemStack(Material.LEATHER_HELMET)));
            ee.setChestplate(setArmorColor(Color.BLACK, new ItemStack(Material.LEATHER_CHESTPLATE)));
            ee.getChestplate().addEnchantments(enchantmentIntegerHashMap);
            ee.setLeggings(setArmorColor(Color.BLACK, new ItemStack(Material.LEATHER_LEGGINGS)));
            ee.setBoots(setArmorColor(Color.BLACK, new ItemStack(Material.LEATHER_BOOTS)));

            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
            sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
            ee.setItemInHand(sword);

            guard.setMetadata("guard.target", new FixedMetadataValue(this, player.getName()));

            if (random < 0.5) {
                ((PigZombie) guard).setAngry(true);
            }
            guard.setTarget(player);

        }
    }

    /**
     * Used to monitor the P.I.G spawned entities and remove them if they switch from their
     * original target
     *
     * @param event EntityTargetEvent to check
     * @return True if the event was OK, False if not (not really used)
     */
    @EventHandler
    public boolean onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof PigZombie) {
            PigZombie guard = (PigZombie) event.getEntity();
            if (guard.hasMetadata("guard.target")) {
                String target = guard.getMetadata("guard.target").get(0).asString();

                Entity targetEntity = event.getTarget();
                if (targetEntity instanceof Player) {
                    Player targetPlayer = (Player) targetEntity;
                    if (!targetPlayer.getName().equalsIgnoreCase(target)) {
                        guard.remove();
                    }
                }
            }
        }
        return true;
    }

    /**
     * Takes an item stack and applies color to the meat data (use for coloring Armor)
     *
     * @param color     The color to apply to the armor
     * @param itemStack The item stack containing the armor item
     * @return Colored item stack
     */
    private ItemStack setArmorColor(Color color, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        armorMeta.setColor(color);
        itemStack.setItemMeta(armorMeta);
        return itemStack;
    }

    private void spawnStarFirework(Firework fireWork) {
        double random = Math.random() * (colors.length - 1);
        int intRandom = (int) Math.round(random);

        FireworkMeta fireworkMeta = fireWork.getFireworkMeta();

        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(colors[intRandom]);
        effectBuilder.with(FireworkEffect.Type.STAR);
        fireworkMeta.addEffect(effectBuilder.build());

        fireworkMeta.setPower(3);

        fireWork.setFireworkMeta(fireworkMeta);
    }

    private void spawnBurstFirework(Firework fireWork) {
        double random = Math.random() * (colors.length - 1);
        int intRandom = (int) Math.round(random);

        FireworkMeta fireworkMeta = fireWork.getFireworkMeta();

        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(colors[intRandom]);
        effectBuilder.with(FireworkEffect.Type.BURST);

        random = Math.random();
        if (random < 0.3D) effectBuilder.trail(true);
        else if (random < 0.6D) effectBuilder.flicker(true);

        fireworkMeta.addEffect(effectBuilder.build());
        fireworkMeta.setPower(3);

        fireWork.setFireworkMeta(fireworkMeta);
    }

    private void spawnCreeperFirework(Firework fireWork) {

        FireworkMeta fireworkMeta = fireWork.getFireworkMeta();

        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(Color.GREEN);
        effectBuilder.with(FireworkEffect.Type.CREEPER);

        double random = Math.random();
        if (random < 0.3D) effectBuilder.trail(true);
        else if (random < 0.6D) effectBuilder.flicker(true);

        fireworkMeta.addEffect(effectBuilder.build());
        fireworkMeta.setPower(3);
        fireWork.setFireworkMeta(fireworkMeta);
    }

    private void spawnBallFirework(Firework fireWork) {

        double random = Math.random() * (colors.length - 1);
        int intRandom = (int) Math.round(random);

        FireworkMeta fireworkMeta = fireWork.getFireworkMeta();

        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(colors[intRandom]);
        effectBuilder.with(FireworkEffect.Type.BALL_LARGE);
        fireworkMeta.addEffect(effectBuilder.build());

        random = Math.random() * (colors.length - 1);
        intRandom = (int) Math.round(random);
        effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(colors[intRandom]);
        effectBuilder.with(FireworkEffect.Type.BALL);

        random = Math.random();
        if (random < 0.3D) effectBuilder.trail(true);
        else if (random < 0.6D) effectBuilder.flicker(true);

        fireworkMeta.addEffect(effectBuilder.build());

        fireworkMeta.setPower(3);

        fireWork.setFireworkMeta(fireworkMeta);
    }

    private void spawnFireworks(Player player) {

        World world = player.getWorld();
        Location location = player.getLocation();

        for (int i = 0; i < 5; i++) {

            location.setX(location.getBlockX() + i);
            Firework fireWork = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
            int random = (int) Math.round(Math.random() * 10);

            switch (random) {
                case 1:
                    spawnBurstFirework(fireWork);
                    break;
                case 2:
                    spawnCreeperFirework(fireWork);
                    break;
                case 3:
                    spawnStarFirework(fireWork);
                    break;
                default:
                    spawnBallFirework(fireWork);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        switch (cmd.getName()) {

            case "scanfw":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp()) {
                        RegionManager rm = worldGuard.getRegionManager(player.getWorld());
                        ProtectedRegion r = rm.getRegion("spawn");
                        for (int i = r.getMinimumPoint().getBlockX(); i < r.getMaximumPoint().getBlockX(); i++) {
                            for (int j = r.getMinimumPoint().getBlockY(); j < r.getMaximumPoint().getBlockY(); j++) {
                                for (int k = r.getMinimumPoint().getBlockZ(); k < r.getMaximumPoint().getBlockZ(); k++) {
                                    Location l = new Location(player.getWorld(), i, j, k);
                                    Block b = player.getWorld().getBlockAt(l);
                                    if (b.getType() == Material.SKULL) {
                                        if (((Skull) b.getState()).getOwner().equals("MHF_TNT2")) {
                                            fireworkSpawnerLocations.add(new FireworkLocation(l));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case "fwblock":
                if (sender instanceof BlockCommandSender) {
                    fireworkCounter++;
                    if (fireworkCounter >= 7) {
                        fireworkCounter = 0;
                        ArrayList<Location> locations = new ArrayList<>();
                        for (FireworkLocation fwl : fireworkSpawnerLocations) {
                            locations.add(new Location(Bukkit.getWorld("world"), fwl.x, fwl.y, fwl.z));
                        }
                        fireWorkTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new FireworkRunnable(Bukkit.getWorld("world"), 0, this, locations.toArray(new Location[locations.size()])), 20L, 20L);
                    }
                }

                break;

            case "fw":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp()) {
                        ArrayList<Location> locations = new ArrayList<>();
                        for (FireworkLocation fwl : fireworkSpawnerLocations) {
                            locations.add(new Location(player.getWorld(), fwl.x, fwl.y, fwl.z));
                        }
                        fireWorkTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new FireworkRunnable(Bukkit.getWorld("world"), 0, this, locations.toArray(new Location[locations.size()])), 20L, 20L);
                    }
                }
                break;

            case "rain":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (pigletRainTaskID > 0) {
                        player.sendMessage(ChatColor.RED + "It's already raining!");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.BLUE + "A pigletstorm is brewing...");
                        pigletRainTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new PigRainRunnable(this.getServer().getWorld("world"), player, this), 5L, 5L);
                    }
                }
                break;

            case "tnt":
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (player.isOp()) {
                        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                            player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                        }

                        return true;
                    }
                }
                break;

            case "ynennjgkamvpctbfrcrz":
                if (sender instanceof BlockCommandSender) {
                    BlockCommandSender block = (BlockCommandSender) sender;
                    Location l = block.getBlock().getLocation();
                    l.setZ(l.getBlockZ() + 1);
                    l.setY(l.getBlockY());
                    l.setX(l.getBlockX());
                    l.getWorld().getBlockAt(l).setType(Material.TNT);
                    return true;
                }
                break;

        }

        return true;
    }

    public boolean inRegion(Player player, String regionName) {

        Vector pt = toVector(player.getLocation()); // This also takes a location

        RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Monitors interaction events, used for the gambling machine in the Jazz club
     *
     * @param event PlayerInteractEvent to look at
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (player.getItemInHand().getType() == Material.STICK) {
            if (player.hasPermission("validation.fireworks")) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    spawnFireworks(player);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player p = event.getPlayer();

        if (p.isOp() && inRegion(p, "spawn")) {
            Block b = event.getBlock();
            if (b.getType() == Material.SKULL) {
                Skull skull = (Skull) b.getState();

                if (skull.getOwner().equals("MHF_TNT2")) {
                    if (fireworkSpawnerLocations.contains(new FireworkLocation(skull.getLocation()))) {
                        fireworkSpawnerLocations.remove(new FireworkLocation(skull.getLocation()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {

        Player p = event.getPlayer();

        if (p.isOp() && inRegion(p, "spawn")) {
            Block b = event.getBlock();
            if (b.getType() == Material.SKULL) {
                if (p.getItemInHand().getType() == Material.SKULL_ITEM) {
                    if (((SkullMeta) p.getItemInHand().getItemMeta()).getOwner().equals("MHF_TNT2")) {
                        fireworkSpawnerLocations.add(new FireworkLocation(b.getLocation()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        Entity victim = event.getEntity();
        if (victim == null) {
            return;
        }

        if (victim instanceof Pig) {
            Pig pig = (Pig) victim;
            if (pig.hasMetadata("pig_rain") || pig.getCustomName().equals("Billy, King of Pigs")) {
                event.setCancelled(true);
                return;
            }
        }

        if (victim instanceof LeashHitch) {
            LeashHitch leashHitch = (LeashHitch) victim;
            World world = leashHitch.getWorld();
            Location billyLeashLocation = new Location(world, 534, 25, -223);
            Location leashLocation = leashHitch.getLocation();
            if (billyLeashLocation.getBlockX() == leashLocation.getBlockX()) {
                if (billyLeashLocation.getBlockY() == leashLocation.getBlockY()) {
                    if (billyLeashLocation.getBlockZ() == leashLocation.getBlockZ()) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    /**
     * Monitors entities for damage and takes action in certain situations
     * 1) Punishes players with the "P.I.G" for attacking piglets
     * 2) Spawns the "P.I.G" to deal retribution for PVP in spawn and other protected zones
     *
     * @param event EntityDamageByEntityEvent to check for infractions
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity entity = event.getDamager();
        Entity victim = event.getEntity();

        if (entity == null || victim == null) {
            return;
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Piglet protection
            if (victim.getType() == EntityType.PIG) {
                Pig pig = (Pig) victim;
                if (!pig.isAdult()) {
                    //player.getWorld().strikeLightning(player.getLocation());
                    spawnPIG(player);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Attacking Piglets is a capital offence!");
                }
                return;
            }

            // P.I.G code
            if (victim.getType() == EntityType.PLAYER) {
                if (inRegion(player, "spawn")) {
                    spawnPIG(player);
                }
            }
        }
    }

    //Billy Offering Analyser
    @EventHandler
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {

        Inventory inventory = event.getInventory();                     //Get the Inventory that took the item
        Material itemType = event.getItem().getItemStack().getType();   //Get the item that was picked up
        if (inventory.getHolder() instanceof Hopper) {                  //Check inventory is a hopper
            Hopper hopper = (Hopper) inventory.getHolder();
            Location hopperLocation = hopper.getLocation();             //Get the hopper location and store xyz
            int x = hopperLocation.getBlockX();
            int y = hopperLocation.getBlockY();
            int z = hopperLocation.getBlockZ();
            if (532 < x && x < 536 && y == 18 && -214 < z && z < -210) {// Check this is a Billy hopper

                hopperLocation.getBlock().setType(Material.AIR);
                hopperLocation.getBlock().setType(Material.HOPPER);

                Item item = event.getItem();

                if (item.hasMetadata("droppedBy")) {

                    List<MetadataValue> metadata = event.getItem().getMetadata("droppedBy");
                    if (metadata.size() >= 0) {

                        String playerName = metadata.get(0).asString();
                        Player droppedBy = Bukkit.getPlayer(playerName);

                        if (droppedBy == null) return;

                        if (configuredOfferings.containsKey(itemType)) {
                            configuredOfferings.get(itemType).grantOffering(droppedBy, item);
                        } else {
                            droppedBy.sendMessage(ChatColor.LIGHT_PURPLE + "Billy seems nonplussed...");
                        }
                    }
                }
            }
        }
    }

    //
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (inRegion(player, "throneroom")) {
            event.getItemDrop().setMetadata("droppedBy", new FixedMetadataValue(this, player.getName()));
        }
    }

    private class PigRainRunnable implements Runnable {

        private World world;
        private Player player;
        private JavaPlugin parent;
        private AtomicInteger pigCounter = new AtomicInteger(0);

        public PigRainRunnable(World world, Player player, JavaPlugin parent) {
            this.world = world;
            this.player = player;
            this.parent = parent;

            world.setStorm(true);
            world.setThundering(true);
        }

        @Override
        public void run() {

            int x = pigCounter.incrementAndGet();

            int randomX = (int) (Math.random() * 15);
            int randomZ = (int) (Math.random() * 15);

            Location playerLocation = player.getLocation();

            boolean flip = Math.random() >= 0.50D;
            int newX = flip ? playerLocation.getBlockX() + randomX : playerLocation.getBlockX() - randomX;
            flip = Math.random() >= 0.50D;
            int newZ = flip ? playerLocation.getBlockZ() + randomZ : playerLocation.getBlockZ() - randomZ;

            Location spawnLocation = new Location(player.getWorld(), newX, 128, newZ);

            if (Math.random() > 0.75D) world.strikeLightning(spawnLocation.add(100, 0, 0));

            Pig piglet = (Pig) this.world.spawnEntity(spawnLocation, EntityType.PIG);
            piglet.setBaby();
            piglet.setMetadata("pig_rain", new FixedMetadataValue(parent, true));

            pigRain.add(piglet);

            if (x >= 100) {
                getServer().getScheduler().cancelTask(pigletRainTaskID);
                pigletUndoTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(parent, new PigletRemoverRunnable(), 100, 15);
                world.setStorm(false);
                world.setThundering(false);
                pigletRainTaskID = 0;
            }
        }
    }

    private class PigletRemoverRunnable implements Runnable {

        @Override
        public void run() {
            if (pigRain.size() > 0) {
                Pig pig = pigRain.poll();
                pig.remove();
            } else {
                getServer().getScheduler().cancelTask(pigletUndoTaskID);
            }
        }
    }

    private class FireworkRunnable implements Runnable {

        private Location[] spawnerLocations;
        private World world;
        private long startTime;
        private int mode;
        private JavaPlugin parent;

        public FireworkRunnable(World world, int mode, JavaPlugin parent, Location[] locations) {
            this.world = world;
            this.mode = mode;
            this.parent = parent;
            this.spawnerLocations = locations;

            startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {

            for (Location l : spawnerLocations) {
                Firework firework = (Firework) world.spawnEntity(l, EntityType.FIREWORK);
                spawnBallFirework(firework);
            }

            switch (mode) {
                case 0:
                    if (System.currentTimeMillis() - startTime > 30000) {
                        getServer().getScheduler().cancelTask(fireWorkTaskID);

                        // Schedule new task
                        fireWorkTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(parent, new FireworkRunnable(getServer().getWorld("world"), 1, parent, spawnerLocations), 20L, 10L);
                    }

                    break;
                case 1:
                    if (System.currentTimeMillis() - startTime > 10000)
                        getServer().getScheduler().cancelTask(fireWorkTaskID);
            }
        }
    }


}
