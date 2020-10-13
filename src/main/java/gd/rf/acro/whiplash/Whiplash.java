package gd.rf.acro.whiplash;

import gd.rf.acro.whiplash.blocks.BossSpawnerBlock;
import gd.rf.acro.whiplash.blocks.DeactivatedSpawnerBlock;
import gd.rf.acro.whiplash.blocks.DungeonBlock;
import gd.rf.acro.whiplash.blocks.SolidVoidBlock;
import gd.rf.acro.whiplash.items.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Material;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class Whiplash implements ModInitializer {
	private static final List<Identifier> tables = Arrays.asList(LootTables.BURIED_TREASURE_CHEST, LootTables.STRONGHOLD_LIBRARY_CHEST,LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY,LootTables.DESERT_PYRAMID_CHEST,LootTables.JUNGLE_TEMPLE_CHEST);
	public static final ItemGroup TAB = FabricItemGroupBuilder.build(
			new Identifier("whiplash", "tab"),
			() -> new ItemStack(Whiplash.DUNGEON_BRICK_EARTH));

	public static final Identifier SEND_SPELL_DATA = new Identifier("whiplash","spell_data");
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerBlocks();
		registerItems();
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (tables.contains(id))
			{
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
						.withEntry(ItemEntry.builder(Whiplash.FIRE_CRYSTAL).weight(1).build())
						.withEntry(ItemEntry.builder(Whiplash.EARTH_CRYSTAL).weight(1).build())
						.withEntry(ItemEntry.builder(Whiplash.WATER_CRYSTAL).weight(1).build())
						.withEntry(ItemEntry.builder(Whiplash.DYNAMIC_SPELL_BOOK_ITEM).weight(1).build())
						.withEntry(ItemEntry.builder(Items.AIR).weight(10).build());

				supplier.withPool(poolBuilder.build());
			}
		});

		ServerSidePacketRegistry.INSTANCE.register(SEND_SPELL_DATA, (packetContext, passedData) -> {
			// Get the BlockPos we put earlier in the IO thread
			ItemStack stack = passedData.readItemStack();
			CompoundTag tag = new CompoundTag();
			tag.putByteArray("tex",passedData.readByteArray());
			tag.putString("element",passedData.readString(32767));
			tag.putString("style",passedData.readString(32767));
			tag.putInt("power",passedData.readInt());
			tag.putInt("drain",passedData.readInt());
			tag.putInt("effect",passedData.readInt());
			packetContext.getTaskQueue().execute(() -> {
				stack.setTag(tag);

			});
		});



		System.out.println("Ready for some whiplash?");
	}
	public static final DungeonBlock DUNGEON_BRICK_EARTH = new DungeonBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).build(),"earth");
	public static final DungeonBlock DUNGEON_BRICK_FIRE = new DungeonBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).build(),"fire");
	public static final DungeonBlock DUNGEON_BRICK_WATER = new DungeonBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).build(),"water");
	public static final SolidVoidBlock SOLID_VOID_BLOCK = new SolidVoidBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).lightLevel(10).build());
	public static final DeactivatedSpawnerBlock DEACTIVATED_SPAWNER_BLOCK = new DeactivatedSpawnerBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).build());
	public static final BossSpawnerBlock BOSS_SPAWNER_BLOCK = new BossSpawnerBlock(FabricBlockSettings.of(Material.STONE).strength(-1,3600000.0F).ticksRandomly().build());

	private void registerBlocks()
	{
		Registry.register(Registry.BLOCK,new Identifier("whiplash","dungeon_brick_earth"),DUNGEON_BRICK_EARTH);
		Registry.register(Registry.BLOCK,new Identifier("whiplash","dungeon_brick_fire"),DUNGEON_BRICK_FIRE);
		Registry.register(Registry.BLOCK,new Identifier("whiplash","dungeon_brick_water"),DUNGEON_BRICK_WATER);
		Registry.register(Registry.BLOCK,new Identifier("whiplash","solid_void"),SOLID_VOID_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("whiplash","deactivated_spawner"),DEACTIVATED_SPAWNER_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("whiplash","boss_spawner"),BOSS_SPAWNER_BLOCK);
	}
	public static final DungeonMapItem DEBUG_ITEM = new DungeonMapItem(new Item.Settings().group(Whiplash.TAB));
	public static final CrystalItem EARTH_CRYSTAL = new CrystalItem(new Item.Settings().group(Whiplash.TAB).maxCount(1));
	public static final CrystalItem WATER_CRYSTAL = new CrystalItem(new Item.Settings().group(Whiplash.TAB).maxCount(1));
	public static final CrystalItem FIRE_CRYSTAL = new CrystalItem(new Item.Settings().group(Whiplash.TAB).maxCount(1));
	public static final DynamicSpellBookItem DYNAMIC_SPELL_BOOK_ITEM = new DynamicSpellBookItem(new Item.Settings().group(Whiplash.TAB).maxCount(1));
	public static final ElementalCoreItem ELEMENTAL_CORE_ITEM = new ElementalCoreItem(new Item.Settings().group(Whiplash.TAB).maxCount(1));
	public static final ItemEscapeRope ITEM_ESCAPE_ROPE = new ItemEscapeRope(new Item.Settings().group(Whiplash.TAB));
	private void registerItems()
	{
		Registry.register(Registry.ITEM, new Identifier("whiplash", "dungeon_brick_earth"), new BlockItem(DUNGEON_BRICK_EARTH, new Item.Settings().group(Whiplash.TAB)));
		Registry.register(Registry.ITEM, new Identifier("whiplash", "dungeon_brick_fire"), new BlockItem(DUNGEON_BRICK_FIRE, new Item.Settings().group(Whiplash.TAB)));
		Registry.register(Registry.ITEM, new Identifier("whiplash", "dungeon_brick_water"), new BlockItem(DUNGEON_BRICK_WATER, new Item.Settings().group(Whiplash.TAB)));
		Registry.register(Registry.ITEM,new Identifier("whiplash","dungeon_map"),DEBUG_ITEM);
		Registry.register(Registry.ITEM,new Identifier("whiplash","earth_crystal"),EARTH_CRYSTAL);
		Registry.register(Registry.ITEM,new Identifier("whiplash","water_crystal"),WATER_CRYSTAL);
		Registry.register(Registry.ITEM,new Identifier("whiplash","fire_crystal"),FIRE_CRYSTAL);
		Registry.register(Registry.ITEM,new Identifier("whiplash","spell_book"),DYNAMIC_SPELL_BOOK_ITEM);
		Registry.register(Registry.ITEM,new Identifier("whiplash","elemental_core"),ELEMENTAL_CORE_ITEM);
		Registry.register(Registry.ITEM,new Identifier("whiplash","escape_rope"),ITEM_ESCAPE_ROPE);

	}
}
