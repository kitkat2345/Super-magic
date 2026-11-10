package com.kitkat2345.supermagic.item;

import com.kitkat2345.supermagic.SuperMagic;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SuperMagic.MODID);

    public static final RegistryObject<Item> EMBER_SEAL = ITEMS.register(
            "ember_seal",
            () -> new EmberSealItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC))
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
