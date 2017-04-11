package com.github.keough99;

/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.block.Sign;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.FileConfigurationOptions;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
/*     */ import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockRedstoneEvent;
/*     */ import org.bukkit.event.block.SignChangeEvent;
/*     */ import org.bukkit.event.entity.ProjectileHitEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class PirateCannon extends JavaPlugin implements Listener
/*     */ {
/*     */   public static BukkitTask pid;
/*  45 */   public static FileConfiguration idConfig = null;
/*  46 */   public static File idConfigFile = null;
/*  47 */   @SuppressWarnings({ "unchecked", "rawtypes" })
HashMap<String, Long> playerlastshot = new HashMap();
/*  48 */   @SuppressWarnings({ "unchecked", "rawtypes" })
ArrayList<Entity> projectiles = new ArrayList();
/*  49 */   @SuppressWarnings({ "unchecked", "rawtypes" })
ArrayList<Integer> unusedids = new ArrayList();
/*  50 */   @SuppressWarnings({ "unchecked", "rawtypes" })
HashMap<Integer, Long> cannonlastshot = new HashMap();
/*     */   
/*     */   public void onEnable()
/*     */   {
/*  54 */     Cannon.init(this);
/*  55 */     getLogger().info("PirateCannons Enabled!");
/*  56 */     Bukkit.getPluginManager().registerEvents(this, this);
/*  57 */     getConfig().options().copyDefaults(true);
/*  58 */     saveDefaultConfig();
/*  59 */     idConfigFile = new File("plugins/PirateCannon/idConfig.yml");
/*  60 */     idConfig = YamlConfiguration.loadConfiguration(idConfigFile);
/*  61 */     idConfig.options().header("This is a list of unused cannon id's, please do not edit this!");
/*     */     
/*  63 */     this.unusedids = stringToList(idConfig.getString("unusedids"));
/*  64 */     if (this.unusedids.size() < 1) {
/*  65 */       this.unusedids.add(Integer.valueOf(1));
/*  66 */       this.unusedids.add(Integer.valueOf(2));
/*  67 */       idConfig.set("unusedids", listToString(this.unusedids));
/*  68 */       saveIDConfig();
/*  69 */       this.unusedids = stringToList(idConfig.getString("unusedids"));
/*     */     }
/*  71 */     Cannon.checkConfig();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  77 */     idConfig.set("unusedids", listToString(this.unusedids));
/*  78 */     saveIDConfig();
/*  79 */     saveDefaultConfig();
/*  80 */     getLogger().info("PirateCannons Disabled!");
/*     */   }
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
/*     */   {
/*  85 */     if (cmd.getName().equalsIgnoreCase("cannon")) {
/*  86 */       sender.sendMessage(ChatColor.AQUA + "PirateCannons are enabled on this Server.");
/*  87 */       return true;
/*     */     }
/*  89 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressWarnings("deprecation")
@EventHandler
/*     */   public void PlayerInteract(PlayerInteractEvent event)
/*     */   {
/*  96 */     if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType() == Material.WALL_SIGN)) {
/*  97 */       Sign sign = (Sign)event.getClickedBlock().getState();
/*  98 */       if (sign.getLine(0).contains("[Cannon]"))
/*     */       {
/*     */ 
/* 101 */         int cannonid = Cannon.checkCannonId(event.getClickedBlock().getLocation(), this.unusedids);
/*     */         
/*     */ 
/* 104 */         boolean playerpermitted = Cannon.checkPermission(event.getPlayer().getName());
/*     */         
/*     */ 
/* 107 */         Date date = new Date();
/* 108 */         boolean playerdelay = false;
/* 109 */         boolean cannondelay = false;
/*     */         
/* 111 */         playerdelay = Cannon.getPlayerCooldown(event.getPlayer().getName(), (Long)this.playerlastshot.get(event.getPlayer().getName()));
/*     */         
/*     */ 
/* 114 */         cannondelay = Cannon.getCannonCooldown(cannonid, (Long)this.cannonlastshot.get(Integer.valueOf(cannonid)), event.getPlayer().getName());
/*     */         
/*     */ 
/* 117 */         boolean playerhasammo = Cannon.checkAmmo(event.getPlayer().getName());
/*     */         
/*     */ 
/* 120 */         boolean playerhastool = Cannon.checkTool(event.getPlayer());
/*     */         
/*     */ 
/* 123 */         if ((sign.getLine(0).contains("[Cannon]")) && (playerdelay) && (cannondelay) && (playerhasammo) && (playerhastool) && (playerpermitted))
/*     */         {
/* 125 */           Location loc = sign.getLocation();
/* 126 */           Vector direction = Cannon.getDirection(loc);
/* 127 */           Vector correction = Cannon.getCorrection(loc);
/* 128 */           int smokedirection = Cannon.getSmokeDirection(loc);
/* 129 */           int length = Cannon.getLength(loc, direction);
/* 130 */           Vector launch = Cannon.getLaunchVector(direction, length);
/*     */           
/*     */ 
/* 133 */           if ((getConfig().getInt("mincannonsize") <= length) && (length <= getConfig().getInt("maxcannonsize"))) {
/* 134 */             this.projectiles.add(Cannon.shoot(loc, launch, correction, smokedirection));
/*     */             
/* 136 */             Cannon.removeAmmo(event.getPlayer().getName());
/* 137 */             Cannon.removeDurability(event.getPlayer());
/* 138 */             this.playerlastshot.put(event.getPlayer().getName(), Long.valueOf(date.getTime()));
/* 139 */             this.cannonlastshot.put(Integer.valueOf(cannonid), Long.valueOf(date.getTime()));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 146 */         event.setCancelled(true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 152 */     if (((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) && 
/* 153 */       (event.getClickedBlock().getType() == Material.getMaterial(getConfig().getInt("rotationblock"))) && (getConfig().getBoolean("allowrotation")) && (
/* 154 */       (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (event.getAction().equals(Action.LEFT_CLICK_BLOCK)))) {
/* 155 */       for (int i = 0; i < 4; i++)
/*     */       {
/*     */ 
/* 158 */         Location loc = event.getClickedBlock().getLocation();
/* 159 */         switch (i) {
/*     */         case 0: 
/* 161 */           loc.setX(loc.getX() - 1.0D);
/* 162 */           loc.setY(loc.getY() - 1.0D);
/* 163 */           break;
/*     */         case 1: 
/* 165 */           loc.setX(loc.getX() + 1.0D);
/* 166 */           loc.setY(loc.getY() - 1.0D);
/* 167 */           break;
/*     */         case 2: 
/* 169 */           loc.setZ(loc.getZ() - 1.0D);
/* 170 */           loc.setY(loc.getY() - 1.0D);
/* 171 */           break;
/*     */         case 3: 
/* 173 */           loc.setZ(loc.getZ() + 1.0D);
/* 174 */           loc.setY(loc.getY() - 1.0D);
/*     */         }
/*     */         
/* 177 */         if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {
/* 178 */           Sign sign = (Sign)loc.getBlock().getState();
/* 179 */           if (sign.getLine(0).contains("[Cannon]"))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 184 */             Vector direction = Cannon.getDirection(loc);
/* 185 */             int length = Cannon.getLength(loc, direction);
/* 186 */             String turn = "";
/* 187 */             if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
/* 188 */               turn = "right";
/* 189 */             } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
/* 190 */               turn = "left";
/*     */             }
/* 192 */             Vector targetdirection = Cannon.getTurnTo(direction, turn);
/* 193 */             byte signdirection = Cannon.getTurnedSignDirection(targetdirection);
/*     */             
/*     */ 
/*     */ 
/* 197 */             Block signfrom = loc.getBlock();
/* 198 */             Vector dir = new Vector(direction.getX(), direction.getY(), direction.getZ());
/* 199 */             Vector targetdir = new Vector(targetdirection.getX(), targetdirection.getY(), targetdirection.getZ());
/* 200 */             Block signto = loc.toVector().add(dir.add(targetdir.multiply(-1))).toLocation(loc.getWorld()).getBlock();
/* 201 */             @SuppressWarnings({ "unchecked", "rawtypes" })
ArrayList<Block> turnfrom = new ArrayList();
/* 202 */             @SuppressWarnings({ "unchecked", "rawtypes" })
ArrayList<Block> turnto = new ArrayList();
/* 203 */             loc = event.getClickedBlock().getLocation();
/* 204 */             @SuppressWarnings("unused")
Location saveloc = loc;
/* 205 */             loc.setY(loc.getY() - 1.0D);
/*     */             
/*     */ 
/* 208 */             for (int j = 1; j <= length; j++) {
/* 209 */               dir = new Vector(direction.getX(), direction.getY(), direction.getZ());
/* 210 */               saveloc = loc;
/* 211 */               Block from = loc.toVector().add(dir.multiply(j)).toLocation(loc.getWorld()).getBlock();
/* 212 */               saveloc = loc;
/* 213 */               targetdir = new Vector(targetdirection.getX(), targetdirection.getY(), targetdirection.getZ());
/* 214 */               Block to = loc.toVector().add(targetdir.multiply(j)).toLocation(loc.getWorld()).getBlock();
/* 215 */               turnfrom.add(from);
/* 216 */               turnto.add(to);
/*     */             }
/*     */             
/* 219 */             boolean enoughspace = true;
/* 220 */             for (Block block : turnto) {
/* 221 */               if (block.getType() != Material.AIR) {
/* 222 */                 enoughspace = false;
/*     */               }
/*     */             }
/* 225 */             if (signto.getType() != Material.AIR) {
/* 226 */               enoughspace = false;
/*     */             }
/*     */             
/* 229 */             if (enoughspace) {
/* 230 */               for (int j = 0; j < turnfrom.size(); j++) {
/* 231 */                 ((Block)turnto.get(j)).setType(((Block)turnfrom.get(j)).getType());
/* 232 */                 ((Block)turnfrom.get(j)).setType(Material.AIR);
/*     */               }
/* 234 */               String[] lines = ((Sign)signfrom.getState()).getLines();
/* 235 */               signfrom.setType(Material.AIR);
/* 236 */               signto.setType(Material.WALL_SIGN);
/* 237 */               signto.setData(signdirection);
/* 238 */               Sign turnsign = (Sign)signto.getState();
/* 239 */               turnsign.setLine(0, lines[0]);
/* 240 */               turnsign.setLine(1, lines[1]);
/* 241 */               turnsign.setLine(2, lines[2]);
/* 242 */               turnsign.setLine(3, lines[3]);
/* 243 */               turnsign.update(true);
/*     */             }
/*     */           }
/* 246 */           i = 10;
/* 247 */           event.setCancelled(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @EventHandler
/*     */   public void Sign(SignChangeEvent event)
/*     */   {
/* 260 */     if (event.getPlayer().hasPermission("cannon.create")) {
/* 261 */       String line0 = event.getLine(0);
/* 262 */       if ((line0.equalsIgnoreCase("c")) && (getConfig().getBoolean("useplayercooldown"))) {
/* 263 */         event.getPlayer().sendMessage(ChatColor.AQUA + "Cannon created successfully.");
/* 264 */         event.setLine(0, "[Cannon]");
/* 265 */         event.setLine(1, "kaboom");
/* 266 */         event.setLine(2, ":D");
/* 267 */         event.setLine(3, "");
/*     */       }
/* 269 */       if ((line0.equalsIgnoreCase("[Cannon]")) && (getConfig().getBoolean("useplayercooldown"))) {
/* 270 */         event.getPlayer().sendMessage(ChatColor.AQUA + "Cannon created successfully.");
/* 271 */         event.setLine(0, "[Cannon]");
/* 272 */         event.setLine(1, "kaboom");
/* 273 */         event.setLine(2, ":D");
/* 274 */         event.setLine(3, "");
/*     */       }
/* 276 */       if ((line0.equalsIgnoreCase("c")) && (getConfig().getBoolean("usecannoncooldown"))) {
/* 277 */         boolean changedsth = true;
/* 278 */         int i; for (; changedsth; 
/*     */             
/* 280 */             i < this.unusedids.size() - 1)
/*     */         {
/* 279 */           changedsth = false;
/* 280 */           i = 0; continue;
/* 281 */           int swap = 0;
/* 282 */           if (((Integer)this.unusedids.get(i)).intValue() > ((Integer)this.unusedids.get(i + 1)).intValue()) {
/* 283 */             swap = ((Integer)this.unusedids.get(i)).intValue();
/* 284 */             this.unusedids.set(i, (Integer)this.unusedids.get(i + 1));
/* 285 */             this.unusedids.set(i + 1, Integer.valueOf(swap));
/* 286 */             changedsth = true;
/*     */           }
/* 280 */           i++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 290 */         event.getPlayer().sendMessage(ChatColor.AQUA + "Cannon created successfully.");
/* 291 */         event.setLine(0, "[Cannon]");
/* 292 */         event.setLine(1, this.unusedids.get(0));
/* 293 */         event.setLine(2, "");
/* 294 */         event.setLine(3, "");
/*     */         
/* 296 */         if (this.unusedids.size() <= 1) {
/* 297 */           this.unusedids.add(Integer.valueOf(((Integer)this.unusedids.get(0)).intValue() + 1));
/*     */         }
/* 299 */         this.unusedids.remove(0);
/*     */       }
/* 301 */       if ((line0.equalsIgnoreCase("[Cannon]")) && (getConfig().getBoolean("usecannoncooldown"))) {
/* 302 */         boolean changedsth = true;
/* 303 */         int i; for (; changedsth; 
/*     */             
/* 305 */             i < this.unusedids.size() - 1)
/*     */         {
/* 304 */           changedsth = false;
/* 305 */           i = 0; continue;
/* 306 */           int swap = 0;
/* 307 */           if (((Integer)this.unusedids.get(i)).intValue() > ((Integer)this.unusedids.get(i + 1)).intValue()) {
/* 308 */             swap = ((Integer)this.unusedids.get(i)).intValue();
/* 309 */             this.unusedids.set(i, (Integer)this.unusedids.get(i + 1));
/* 310 */             this.unusedids.set(i + 1, Integer.valueOf(swap));
/* 311 */             changedsth = true;
/*     */           }
/* 305 */           i++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 315 */         event.getPlayer().sendMessage(ChatColor.AQUA + "Cannon created successfully.");
/* 316 */         event.setLine(0, "[Cannon]");
/* 317 */         event.setLine(1, this.unusedids.get(0));
/* 318 */         event.setLine(2, "");
/* 319 */         event.setLine(3, "");
/*     */         
/* 321 */         if (this.unusedids.size() <= 1) {
/* 322 */           this.unusedids.add(Integer.valueOf(((Integer)this.unusedids.get(0)).intValue() + 1));
/*     */         }
/* 324 */         this.unusedids.remove(0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onProjectileHit(ProjectileHitEvent event)
/*     */   {
/* 332 */     if (this.projectiles.contains(event.getEntity())) {
/* 333 */       Location loc = event.getEntity().getLocation();
/* 334 */       event.getEntity().remove();
/* 335 */       int power = getConfig().getInt("explosion.power");
/* 336 */       boolean setfire = getConfig().getBoolean("explosion.setfire");
/* 337 */       boolean blockdamage = getConfig().getBoolean("explosion.blockdamage");
/* 338 */       loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setfire, blockdamage);
/*     */       
/* 340 */       if (getConfig().getDouble("explosion.playerdamage") > 0.0D) {
/* 341 */         List<Entity> nearby = event.getEntity().getNearbyEntities(getConfig().getDouble("explosion.playerdamagerange"), getConfig().getDouble("explosion.playerdamagerange") + 1.0D, getConfig().getDouble("explosion.playerdamagerange"));
/* 342 */         for (int i = 0; i < nearby.size(); i++) {
/* 343 */           if (((nearby.get(i) instanceof Player)) && (((Player)nearby.get(i)).getGameMode() != org.bukkit.GameMode.CREATIVE) && (((CraftPlayer)nearby.get(i)).getHealth() > 0.0D)) {
/* 344 */             if (((CraftPlayer)nearby.get(i)).getHealth() - getConfig().getDouble("explosion.playerdamage") <= 0.0D) {
/* 345 */               ((CraftPlayer)nearby.get(i)).setHealth(0.0D);
/*     */             }
/*     */             else {
/* 348 */               ((CraftPlayer)nearby.get(i)).setHealth(((CraftPlayer)nearby.get(i)).getHealth() - getConfig().getDouble("explosion.playerdamage"));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 354 */       if (getConfig().getDouble("explosion.entitydamage") > 0.0D) {
/* 355 */         List<Entity> nearby = event.getEntity().getNearbyEntities(getConfig().getDouble("explosion.entitydamagerange"), getConfig().getDouble("explosion.entitydamagerange") + 1.0D, getConfig().getDouble("explosion.entitydamagerange"));
/* 356 */         for (int i = 0; i < nearby.size(); i++) {
/* 357 */           if (((nearby.get(i) instanceof LivingEntity)) && (!(nearby.get(i) instanceof Player))) {
/* 358 */             if (((CraftLivingEntity)nearby.get(i)).getHealth() - getConfig().getDouble("explosion.entitydamage") <= 0.0D) {
/* 359 */               ((CraftLivingEntity)nearby.get(i)).setHealth(0.0D);
/*     */             }
/*     */             else {
/* 362 */               ((CraftLivingEntity)nearby.get(i)).setHealth(((CraftLivingEntity)nearby.get(i)).getHealth() - getConfig().getDouble("explosion.entitydamage"));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 368 */       this.projectiles.remove(event.getEntity());
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onBlockBreak(BlockBreakEvent event)
/*     */   {
/* 375 */     Block block = event.getBlock();
/* 376 */     if (block.getType().equals(Material.WALL_SIGN)) {
/* 377 */       Sign sign = (Sign)block.getState();
/* 378 */       if ((sign.getLine(0).contains("[Cannon]")) && (getConfig().getBoolean("usecannoncooldown"))) {
/*     */         try {
/* 380 */           this.unusedids.add(Integer.valueOf(Integer.parseInt(sign.getLine(1))));
/*     */         } catch (Exception localException) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String listToString(ArrayList<Integer> list) {
/* 387 */     String string = "";
/* 388 */     for (Integer i : list) {
/* 389 */       if (string.equals("")) {
/* 390 */         string = i;
/*     */       } else {
/* 392 */         string = string + "," + i;
/*     */       }
/*     */     }
/* 395 */     return string;
/*     */   }
/*     */   
/*     */   @SuppressWarnings({ "rawtypes", "unchecked" })
public ArrayList<Integer> stringToList(String string) {
/*     */     try {
/* 400 */       @SuppressWarnings({ "rawtypes", "unchecked" })
ArrayList<Integer> list = new ArrayList();
/* 401 */       String[] stringlist = string.split(",");
/* 402 */       String[] arrayOfString1; int j = (arrayOfString1 = stringlist).length; for (int i = 0; i < j; i++) { String s = arrayOfString1[i];
/* 403 */         list.add(Integer.valueOf(Integer.parseInt(s)));
/*     */       }
/* 405 */       return list;
/*     */     } catch (Exception e) {
/* 407 */       getLogger().info("Error in config.yml: Unused Cannon id's could not be loaded!");
/* 408 */       return new ArrayList();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void saveIDConfig()
/*     */   {
/* 415 */     if ((idConfig == null) || (idConfigFile == null)) {
/* 416 */       return;
/*     */     }
/*     */     try {
/* 419 */       idConfig.save(idConfigFile);
/*     */     } catch (IOException ex) {
/* 421 */       Bukkit.getLogger().log(Level.SEVERE, "Could not save idconfig to " + idConfigFile, ex);
/*     */     }
/*     */   }
/*     */   
/*     */   @SuppressWarnings("deprecation")
@EventHandler
/*     */   public void onRedstoneChange(BlockRedstoneEvent event)
/*     */   {
/* 428 */     if (getConfig().getList("cannontypes").contains(Integer.valueOf(event.getBlock().getRelative(BlockFace.UP).getTypeId()))) {
/* 429 */       Sign sign = null;
/* 430 */       for (int i = 0; i <= 3; i++) {
/* 431 */         switch (i) {
/*     */         case 0: 
/* 433 */           if (event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
/* 434 */             sign = (Sign)event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getState();
/*     */           }
/* 436 */           break;
/*     */         case 1: 
/* 438 */           if (event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN) {
/* 439 */             sign = (Sign)event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getState();
/*     */           }
/* 441 */           break;
/*     */         case 2: 
/* 443 */           if (event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN) {
/* 444 */             sign = (Sign)event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getState();
/*     */           }
/* 446 */           break;
/*     */         case 3: 
/* 448 */           if (event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN) {
/* 449 */             sign = (Sign)event.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getState();
/*     */           }
/*     */           break;
/*     */         }
/*     */       }
/* 454 */       if ((sign != null) && (sign.getLine(0).equalsIgnoreCase("[Cannon]"))) {
/* 455 */         int cannonid = Cannon.checkCannonId(sign.getLocation(), this.unusedids);
/*     */         
/* 457 */         Date date = new Date();
/* 458 */         boolean cannondelay = Cannon.getCannonCooldown(cannonid, (Long)this.cannonlastshot.get(Integer.valueOf(cannonid)), null);
/* 459 */         if (cannondelay) {
/* 460 */           this.cannonlastshot.put(Integer.valueOf(cannonid), Long.valueOf(date.getTime()));
/*     */         }
/*     */         
/*     */ 
/* 464 */         if ((getConfig().getBoolean("useredstonetriggers")) && (sign.getLine(0).contains("[Cannon]")) && (cannondelay))
/*     */         {
/* 466 */           Location loc = sign.getLocation();
/* 467 */           Vector direction = Cannon.getDirection(loc);
/* 468 */           Vector correction = Cannon.getCorrection(loc);
/* 469 */           int smokedirection = Cannon.getSmokeDirection(loc);
/* 470 */           int length = Cannon.getLength(loc, direction);
/* 471 */           Vector launch = Cannon.getLaunchVector(direction, length);
/*     */           
/*     */ 
/* 474 */           if ((getConfig().getInt("mincannonsize") <= length) && (length <= getConfig().getInt("maxcannonsize"))) {
/* 475 */             this.projectiles.add(Cannon.shoot(loc, launch, correction, smokedirection));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }
