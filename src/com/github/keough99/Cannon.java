package com.github.keough99;

/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.Sign;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Egg;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.TNTPrimed;
/*     */ import org.bukkit.entity.ThrownExpBottle;
/*     */ import org.bukkit.entity.ThrownPotion;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class Cannon
/*     */ {
/*     */   private static PirateCannon plugin;
/*     */   
/*     */   public static void init(PirateCannon piratecannon)
/*     */   {
/*  35 */     plugin = piratecannon;
/*     */   }
/*     */   
/*     */   public static void checkConfig()
/*     */   {
/*  40 */     if (plugin.getConfig().getInt("mincannonsize") < 1) {
/*  41 */       plugin.getConfig().set("mincannonsize", Integer.valueOf(1));
/*  42 */       plugin.getLogger().info("Error in plugins/PirateCannon/config.yml: mincannonsize cant be smaller than 1!");
/*     */     }
/*  44 */     if (plugin.getConfig().getInt("mincannonsize") >= plugin.getConfig().getInt("maxcannonsize")) {
/*  45 */       plugin.getConfig().set("maxcannonsize", Integer.valueOf(plugin.getConfig().getInt("mincannonsize") + 1));
/*  46 */       plugin.getLogger().info("Error in plugins/PirateCannon/config.yml: maxcannonsize cant be smaller or the same as mincannonsize!");
/*     */     }
/*     */   }
/*     */   
/*     */   @SuppressWarnings("deprecation")
public static boolean checkPermission(String playername)
/*     */   {
/*  52 */     boolean playerpermitted = false;
/*  53 */     if ((Bukkit.getPlayer(playername).isOp()) || (Bukkit.getPlayer(playername).hasPermission("cannon.use")) || (plugin.getConfig().getString("permissions").equalsIgnoreCase("all"))) {
/*  54 */       playerpermitted = true;
/*     */     }
/*     */     else {
/*  57 */       Bukkit.getPlayer(playername).sendMessage(ChatColor.RED + "You dont have permission.");
/*     */     }
/*  59 */     return playerpermitted;
/*     */   }
/*     */   
/*     */   public static int checkCannonId(Location loc, ArrayList<Integer> unusedids)
/*     */   {
/*  64 */     int cannonid = 0;
/*  65 */     Sign sign = (Sign)loc.getWorld().getBlockAt(loc).getState();
/*  66 */     if (plugin.getConfig().getBoolean("usecannoncooldown")) {
/*     */       try {
/*  68 */         cannonid = Integer.parseInt(sign.getLine(1));
/*     */       } catch (Exception e) {
/*  70 */         boolean changedsth = true;
/*  71 */         int i; for (; changedsth; 
/*     */             
/*  73 */             i < unusedids.size() - 1)
/*     */         {
/*  72 */           changedsth = false;
/*  73 */           i = 0; continue;
/*  74 */           int swap = 0;
/*  75 */           if (((Integer)unusedids.get(i)).intValue() > ((Integer)unusedids.get(i + 1)).intValue()) {
/*  76 */             swap = ((Integer)unusedids.get(i)).intValue();
/*  77 */             unusedids.set(i, (Integer)unusedids.get(i + 1));
/*  78 */             unusedids.set(i + 1, Integer.valueOf(swap));
/*  79 */             changedsth = true;
/*     */           }
/*  73 */           i++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */         sign.setLine(0, "[Cannon]");
/*  84 */         sign.setLine(1, unusedids.get(0));
/*  85 */         sign.setLine(2, "");
/*  86 */         sign.setLine(3, "");
/*  87 */         sign.update(true);
/*     */         
/*  89 */         cannonid = ((Integer)unusedids.get(0)).intValue();
/*  90 */         if (unusedids.size() <= 1) {
/*  91 */           unusedids.add(Integer.valueOf(((Integer)unusedids.get(0)).intValue() + 1));
/*     */         }
/*  93 */         unusedids.remove(0);
/*     */       }
/*     */     }
/*  96 */     return cannonid;
/*     */   }
/*     */   
/*     */   public static boolean getPlayerCooldown(String playername, Long playerdelay)
/*     */   {
/* 101 */     Date date = new Date();
/* 102 */     boolean cooleddown = false;
/* 103 */     if (plugin.getConfig().getBoolean("useplayercooldown")) {
/* 104 */       long playerdelaytime = plugin.getConfig().getInt("playercooldown") + 1;
/*     */       try {
/* 106 */         playerdelaytime = date.getTime() - playerdelay.longValue();
/*     */       } catch (Exception localException) {}
/* 108 */       if (playerdelaytime >= plugin.getConfig().getInt("playercooldown")) {
/* 109 */         cooleddown = true;
/*     */       }
/*     */     }
/*     */     else {
/* 113 */       cooleddown = true;
/*     */     }
/* 115 */     return cooleddown;
/*     */   }
/*     */   
/*     */   @SuppressWarnings("deprecation")
public static boolean getCannonCooldown(int cannonid, Long cannondelay, String playername)
/*     */   {
/* 120 */     Date date = new Date();
/* 121 */     boolean cooleddown = false;
/* 122 */     if (plugin.getConfig().getBoolean("usecannoncooldown")) {
/* 123 */       long cannondelaytime = plugin.getConfig().getInt("cannoncooldown") + 1;
/*     */       try {
/* 125 */         cannondelaytime = date.getTime() - cannondelay.longValue();
/*     */       } catch (Exception localException) {}
/* 127 */       if (cannondelaytime >= plugin.getConfig().getInt("cannoncooldown")) {
/* 128 */         cooleddown = true;
/* 129 */       } else if (playername != null) {
/* 130 */         if (plugin.getConfig().getString("cannoncooldownoutput").equalsIgnoreCase("message+double")) {
/* 131 */           Bukkit.getPlayer(playername).sendMessage(ChatColor.DARK_AQUA + "This cannon is still cooling down. Remaining time: " + ChatColor.AQUA + (plugin.getConfig().getInt("cannoncooldown") - cannondelaytime) / 1000.0D + "s");
/* 132 */         } else if (plugin.getConfig().getString("cannoncooldownoutput").equalsIgnoreCase("message+int")) {
/* 133 */           Bukkit.getPlayer(playername).sendMessage(ChatColor.DARK_AQUA + "This cannon is still cooling down. Remaining time: " + ChatColor.AQUA + (plugin.getConfig().getInt("cannoncooldown") - cannondelaytime) / 1000L + "s");
/* 134 */         } else if (plugin.getConfig().getString("cannoncooldownoutput").equalsIgnoreCase("double")) {
/* 135 */           Bukkit.getPlayer(playername).sendMessage(ChatColor.AQUA + (plugin.getConfig().getInt("cannoncooldown") - cannondelaytime) / 1000.0D + "s");
/* 136 */         } else if (plugin.getConfig().getString("cannoncooldownoutput").equalsIgnoreCase("int")) {
/* 137 */           Bukkit.getPlayer(playername).sendMessage(ChatColor.AQUA + (plugin.getConfig().getInt("cannoncooldown") - cannondelaytime) / 1000L + "s");
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 142 */       cooleddown = true;
/*     */     }
/* 144 */     return cooleddown;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
public static boolean checkAmmo(String playername)
/*     */   {
/* 150 */     boolean playerhasammo = true;
/*     */     
/* 152 */     if (plugin.getConfig().getBoolean("ammo.use")) {
/* 153 */       Inventory inv = Bukkit.getPlayer(playername).getInventory();
/* 154 */       List<Integer> ammo = plugin.getConfig().getIntegerList("ammo.types");
/* 155 */       List<Integer> countofammo = plugin.getConfig().getIntegerList("ammo.number");
/* 156 */       for (int i = 0; i < ammo.size(); i++) {
/* 157 */         if (!inv.containsAtLeast(new ItemStack(Material.getMaterial(((Integer)ammo.get(i)).intValue())), ((Integer)countofammo.get(i)).intValue())) {
/* 158 */           playerhasammo = false;
/*     */         }
/*     */       }
/* 161 */       if (!playerhasammo) {
/* 162 */         Bukkit.getPlayer(playername).sendMessage(ChatColor.DARK_AQUA + "You need some munition to fire this Cannon!");
/* 163 */         Bukkit.getPlayer(playername).sendMessage(ChatColor.DARK_AQUA + "Munition type ids: ");
/* 164 */         for (int i = 0; i < ammo.size(); i++) {
/* 165 */           Bukkit.getPlayer(playername).sendMessage(ChatColor.AQUA + countofammo.get(i) + ChatColor.DARK_AQUA + " x " + ChatColor.AQUA + Material.getMaterial(((Integer)ammo.get(i)).intValue()).name() + ChatColor.DARK_AQUA + " (id: " + ChatColor.AQUA + ammo.get(i) + ChatColor.DARK_AQUA + ")");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 170 */     if (!plugin.getConfig().getBoolean("ammo.use")) {
/* 171 */       playerhasammo = true;
/*     */     }
/* 173 */     return playerhasammo;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
public static boolean removeAmmo(String playername)
/*     */   {
/* 179 */     if (plugin.getConfig().getBoolean("ammo.use")) {
/* 180 */       List<Integer> ammo = plugin.getConfig().getIntegerList("ammo.types");
/* 181 */       List<Integer> countofammo = plugin.getConfig().getIntegerList("ammo.number");
/* 182 */       for (int i = 0; i < ammo.size(); i++) {
/* 183 */         @SuppressWarnings("deprecation")
ItemStack ammoitem = new ItemStack(((Integer)ammo.get(i)).intValue(), ((Integer)countofammo.get(i)).intValue());
/* 184 */         Bukkit.getPlayer(playername).getInventory().removeItem(new ItemStack[] { ammoitem });
/* 185 */         Bukkit.getPlayer(playername).updateInventory();
/*     */       }
/*     */     }
/* 188 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
public static boolean checkTool(Player player)
/*     */   {
/* 194 */     boolean playerhastool = true;
/* 195 */     if (plugin.getConfig().getBoolean("tool.use")) {
/* 196 */       if (player.getItemInHand().getTypeId() == plugin.getConfig().getInt("tool.type")) {
/* 197 */         ItemStack inhand = player.getItemInHand();
/* 198 */         if (inhand.getType().getMaxDurability() - inhand.getDurability() <= plugin.getConfig().getInt("tool.damage")) {
/* 199 */           playerhastool = false;
/*     */         } else {
/* 201 */           playerhastool = true;
/*     */         }
/*     */       } else {
/* 204 */         player.sendMessage(ChatColor.DARK_AQUA + "You need a tool to fire this cannon: " + ChatColor.AQUA + Material.getMaterial(plugin.getConfig().getInt("tool.type")).name());
/* 205 */         player.sendMessage(ChatColor.DARK_AQUA + "One shot will damage it " + ChatColor.AQUA + plugin.getConfig().getInt("tool.damage") + ChatColor.DARK_AQUA + " uses.");
/* 206 */         playerhastool = false;
/*     */       }
/*     */     }
/* 209 */     return playerhastool;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
public static void removeDurability(Player player)
/*     */   {
/* 215 */     if ((plugin.getConfig().getBoolean("tool.use")) && 
/* 216 */       (player.getItemInHand().getTypeId() == plugin.getConfig().getInt("tool.type"))) {
/* 217 */       ItemStack inhand = player.getItemInHand();
/* 218 */       if (inhand.getType().getMaxDurability() - inhand.getDurability() <= plugin.getConfig().getInt("tool.damage")) {
/* 219 */         inhand = new ItemStack(0, 0);
/* 220 */         player.setItemInHand(inhand);
/*     */       } else {
/* 222 */         int damage = inhand.getDurability() + plugin.getConfig().getInt("tool.damage");
/* 223 */         inhand.setDurability((short)damage);
/* 224 */         player.setItemInHand(inhand);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Entity shoot(Location loc, Vector launch, Vector correction, int smokedir)
/*     */   {
/* 233 */     World world = loc.getWorld();
/* 234 */     Location barrelend = loc.toVector().add(launch).toLocation(world);
/* 235 */     barrelend = barrelend.toVector().add(correction).toLocation(world);
/*     */     
/* 237 */     for (int i = 0; i < plugin.getConfig().getInt("smokeeffect"); i++) {
/* 238 */       world.playEffect(barrelend, org.bukkit.Effect.SMOKE, smokedir);
/*     */     }
/* 240 */     world.playSound(barrelend, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 1.0F);
/* 241 */     world.playSound(barrelend, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.0F);
/* 242 */     Entity projectile = null;
/*     */     
/*     */ 
/*     */ 
/* 246 */     if ((plugin.getConfig().getString("projectile").equalsIgnoreCase("snowball")) || (plugin.getConfig().getString("projectile").equalsIgnoreCase("snow"))) {
/* 247 */       projectile = world.spawn(barrelend, org.bukkit.entity.Snowball.class);
/* 248 */       projectile.setVelocity(launch.multiply(1));
/* 249 */       return projectile;
/*     */     }
/*     */     
/* 252 */     if (plugin.getConfig().getString("projectile").equalsIgnoreCase("arrow")) {
/* 253 */       projectile = world.spawn(barrelend, org.bukkit.entity.Arrow.class);
/* 254 */       projectile.setVelocity(launch.multiply(1));
/* 255 */       return projectile;
/*     */     }
/*     */     
/* 258 */     if (plugin.getConfig().getString("projectile").equalsIgnoreCase("egg")) {
/* 259 */       projectile = world.spawn(barrelend, Egg.class);
/* 260 */       projectile.setVelocity(launch.multiply(1));
/* 261 */       return projectile;
/*     */     }
/*     */     
/* 264 */     if ((plugin.getConfig().getString("projectile").equalsIgnoreCase("thrownexpbottle")) || (plugin.getConfig().getString("projectile").equalsIgnoreCase("expbottle"))) {
/* 265 */       projectile = world.spawn(barrelend, ThrownExpBottle.class);
/* 266 */       projectile.setVelocity(launch.multiply(1));
/* 267 */       return projectile;
/*     */     }
/*     */     
/* 270 */     if ((plugin.getConfig().getString("projectile").equalsIgnoreCase("thrownpotion")) || (plugin.getConfig().getString("projectile").equalsIgnoreCase("potion"))) {
/* 271 */       projectile = world.spawn(barrelend, ThrownPotion.class);
/* 272 */       projectile.setVelocity(launch.multiply(1));
/* 273 */       return projectile;
/*     */     }
/*     */     
/* 276 */     if (plugin.getConfig().getString("projectile").equalsIgnoreCase("tnt")) {
/* 277 */       projectile = world.spawn(barrelend, TNTPrimed.class);
/* 278 */       projectile.setVelocity(launch.multiply(1));
/* 279 */       return projectile;
/*     */     }
/*     */     
/*     */ 
/* 283 */     if (plugin.getConfig().getString("projectile").equalsIgnoreCase("tnt")) {
/* 284 */       @SuppressWarnings("unused")
TNTPrimed tnt = (TNTPrimed)projectile;
/* 285 */       PirateCannon.pid = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 291 */           if ((Cannon.this.getVelocity().getX() * Cannon.this.getVelocity().getX() < 0.25D) && (Cannon.this.getVelocity().getZ() * Cannon.this.getVelocity().getZ() < 0.25D)) {
/* 292 */             Location loc = Cannon.this.getLocation();
/* 293 */             Cannon.this.remove();
/* 294 */             int power = Cannon.plugin.getConfig().getInt("explosion.power");
/* 295 */             boolean setfire = Cannon.plugin.getConfig().getBoolean("explosion.setfire");
/* 296 */             boolean blockdamage = Cannon.plugin.getConfig().getBoolean("explosion.blockdamage");
/* 297 */             loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setfire, blockdamage);
/*     */             
/* 299 */             if (Cannon.plugin.getConfig().getDouble("explosion.playerdamage") > 0.0D) {
/* 300 */               List<Entity> nearby = Cannon.this.getNearbyEntities(Cannon.plugin.getConfig().getDouble("explosion.playerdamagerange"), Cannon.plugin.getConfig().getDouble("explosion.playerdamagerange") + 1.0D, Cannon.plugin.getConfig().getDouble("explosion.playerdamagerange"));
/* 301 */               for (int i = 0; i < nearby.size(); i++) {
/* 302 */                 if (((nearby.get(i) instanceof Player)) && (((Player)nearby.get(i)).getGameMode() != org.bukkit.GameMode.CREATIVE)) {
/* 303 */                   if (((CraftPlayer)nearby.get(i)).getHealth() - Cannon.plugin.getConfig().getDouble("explosion.playerdamage") <= 0.0D) {
/* 304 */                     ((CraftPlayer)nearby.get(i)).setHealth(0.0D);
/*     */                   }
/*     */                   else {
/* 307 */                     ((CraftPlayer)nearby.get(i)).setHealth(((CraftPlayer)nearby.get(i)).getHealth() - Cannon.plugin.getConfig().getDouble("explosion.playerdamage"));
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 313 */             Bukkit.getScheduler().cancelTask(PirateCannon.pid.getTaskId());
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 318 */       }, 0L, 10L);
/*     */     }
/*     */     
/* 321 */     return null;
/*     */   }
protected List<Entity> getNearbyEntities(double double1, double d, double double2) {
	// TODO Auto-generated method stub
	return null;
}
protected Location getLocation() {
	// TODO Auto-generated method stub
	return null;
}
protected void remove() {
	// TODO Auto-generated method stub
	
}
protected Location getVelocity() {
	// TODO Auto-generated method stub
	return null;
}
/*     */   
/*     */ 
/*     */   public static Vector getDirection(Location loc)
/*     */   {
/* 327 */     Vector direction = new Vector(0, 0, 0);
/* 328 */     @SuppressWarnings("deprecation")
byte damage = loc.getBlock().getData();
/* 329 */     switch (damage)
/*     */     {
/*     */     case 2: 
/* 332 */       direction = new Vector(0, 0, 1);
/* 333 */       break;
/*     */     
/*     */     case 3: 
/* 336 */       direction = new Vector(0, 0, -1);
/* 337 */       break;
/*     */     
/*     */     case 4: 
/* 340 */       direction = new Vector(1, 0, 0);
/* 341 */       break;
/*     */     
/*     */     case 5: 
/* 344 */       direction = new Vector(-1, 0, 0);
/*     */     }
/*     */     
/* 347 */     return direction;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
public static Vector getCorrection(Location loc)
/*     */   {
/* 353 */     Vector correction = new Vector(0.0D, 0.5D, 0.0D);
/* 354 */     byte damage = loc.getBlock().getData();
/* 355 */     switch (damage)
/*     */     {
/*     */     case 2: 
/* 358 */       correction.setX(0.5D);
/* 359 */       correction.setZ(1.5D);
/* 360 */       break;
/*     */     
/*     */     case 3: 
/* 363 */       correction.setX(0.5D);
/* 364 */       correction.setZ(-0.5D);
/* 365 */       break;
/*     */     
/*     */     case 4: 
/* 368 */       correction.setX(1.5D);
/* 369 */       correction.setZ(0.5D);
/* 370 */       break;
/*     */     
/*     */     case 5: 
/* 373 */       correction.setX(-0.5D);
/* 374 */       correction.setZ(0.5D);
/*     */     }
/*     */     
/* 377 */     return correction;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getSmokeDirection(Location loc)
/*     */   {
/* 383 */     int smokedirection = 0;
/* 384 */     @SuppressWarnings("deprecation")
byte damage = loc.getBlock().getData();
/* 385 */     switch (damage)
/*     */     {
/*     */     case 2: 
/* 388 */       smokedirection = 7;
/* 389 */       break;
/*     */     
/*     */     case 3: 
/* 392 */       smokedirection = 1;
/* 393 */       break;
/*     */     
/*     */     case 4: 
/* 396 */       smokedirection = 5;
/* 397 */       break;
/*     */     
/*     */     case 5: 
/* 400 */       smokedirection = 3;
/*     */     }
/*     */     
/* 403 */     return smokedirection;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings({ "unused", "deprecation" })
public static int getLength(Location loc, Vector direction)
/*     */   {
/* 409 */     Vector savedir = direction;
/* 410 */     @SuppressWarnings("deprecation")
byte damage = loc.getBlock().getData();
/* 411 */     int length = 0;
/* 412 */     Location testloc = loc;
/* 413 */     for (int i = 1; i <= plugin.getConfig().getInt("maxcannonsize") + 1; i++) {
/* 414 */       direction = savedir;
/* 415 */       testloc = testloc.toVector().add(direction).toLocation(testloc.getWorld());
/* 416 */       if (!plugin.getConfig().getList("cannontypes").contains(Integer.valueOf(testloc.getBlock().getTypeId()))) {
/* 417 */         length = i - 1;
/* 418 */         i = plugin.getConfig().getInt("maxcannonsize") + 1;
/*     */       }
/*     */     }
/* 421 */     return length;
/*     */   }
/*     */   
/*     */   public static Vector getLaunchVector(Vector direction, int length)
/*     */   {
/* 426 */     Vector launchvector = direction.multiply(length);
/* 427 */     launchvector.setY(0.25D);
/* 428 */     return launchvector;
/*     */   }
/*     */   
/*     */   public static Vector getTurnTo(Vector direction, String turn)
/*     */   {
/* 433 */     Vector turnto = new Vector(0, 0, 0);
/* 434 */     if (direction.normalize().equals(new Vector(1, 0, 0))) {
/* 435 */       turnto = new Vector(0, 0, 1);
/*     */     }
/* 437 */     else if (direction.normalize().equals(new Vector(-1, 0, 0))) {
/* 438 */       turnto = new Vector(0, 0, -1);
/*     */     }
/* 440 */     else if (direction.normalize().equals(new Vector(0, 0, 1))) {
/* 441 */       turnto = new Vector(-1, 0, 0);
/*     */     }
/* 443 */     else if (direction.normalize().equals(new Vector(0, 0, -1))) {
/* 444 */       turnto = new Vector(1, 0, 0);
/*     */     }
/* 446 */     if (turn.equals("left")) {
/* 447 */       turnto = turnto.multiply(-1);
/*     */     }
/* 449 */     return turnto;
/*     */   }
/*     */   
/*     */   public static byte getTurnedSignDirection(Vector turnto)
/*     */   {
/* 454 */     byte signdirection = 0;
/* 455 */     if (turnto.normalize().equals(new Vector(1, 0, 0))) {
/* 456 */       signdirection = 4;
/*     */     }
/* 458 */     else if (turnto.normalize().equals(new Vector(-1, 0, 0))) {
/* 459 */       signdirection = 5;
/*     */     }
/* 461 */     else if (turnto.normalize().equals(new Vector(0, 0, 1))) {
/* 462 */       signdirection = 2;
/*     */     }
/* 464 */     else if (turnto.normalize().equals(new Vector(0, 0, -1))) {
/* 465 */       signdirection = 3;
/*     */     }
/* 467 */     return signdirection;
/*     */   }
/*     */ }
