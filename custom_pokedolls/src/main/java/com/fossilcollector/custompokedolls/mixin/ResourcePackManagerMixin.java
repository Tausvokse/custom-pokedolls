package com.fossilcollector.custompokedolls.mixin;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {
    @Shadow @Final @Mutable
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(ResourcePackProvider[] providers, CallbackInfo ci) {
        System.out.println("[CustomPokedolls DEBUG] ResourcePackManager.<init> executed! providers count = " + (this.providers != null ? this.providers.size() : "null"));
        ensureVirtualProviderAdded("init");
    }

    @Inject(method = "scanPacks", at = @At("HEAD"))
    private void onScanPacks(CallbackInfo ci) {
        System.out.println("[CustomPokedolls DEBUG] ResourcePackManager.scanPacks executed! providers count = " + (this.providers != null ? this.providers.size() : "null"));
        ensureVirtualProviderAdded("scanPacks");
    }

    private void ensureVirtualProviderAdded(String source) {
        if (this.providers == null) return;
        boolean found = false;
        for (ResourcePackProvider p : this.providers) {
            if (p.getClass().getName().contains("custompokedolls") || p.getClass().getName().contains("Virtual") || p.getClass().getName().contains("DirectoryVirtual")) {
                found = true;
                break;
            }
        }
        if (!found) {
            Set<ResourcePackProvider> newProviders = new HashSet<>(this.providers);
            newProviders.add(com.fossilcollector.custompokedolls.registry.DirectoryVirtualResourcePack::registerProfile);
            this.providers = com.google.common.collect.ImmutableSet.copyOf(newProviders);
            System.out.println("[CustomPokedolls DEBUG] Successfully added virtual resource pack provider via " + source + "!");
        } else {
            System.out.println("[CustomPokedolls DEBUG] Virtual resource pack provider ALREADY PRESENT in " + source + "!");
        }
    }
}
