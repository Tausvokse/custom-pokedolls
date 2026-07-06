package com.fossilcollector.custompokedolls.mixin;

import com.fossilcollector.custompokedolls.registry.ModBlocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(SimpleRegistry.class)
public class SimpleRegistryMixin<T> {
    @Shadow
    private Map<T, RegistryEntry.Reference<T>> intrusiveValueToEntry;

    @Inject(method = "assertNotFrozen()V", at = @At("HEAD"), cancellable = true, require = 0)
    private void onAssertNotFrozen(CallbackInfo ci) {
        if (ModBlocks.ALLOW_RUNTIME_REGISTRATION) {
            ci.cancel();
        }
    }

    @Inject(method = "assertNotFrozen(Lnet/minecraft/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void onAssertNotFrozenKey(RegistryKey<?> key, CallbackInfo ci) {
        if (ModBlocks.ALLOW_RUNTIME_REGISTRATION) {
            ci.cancel();
        }
    }

    @Inject(method = "createEntry", at = @At("HEAD"), require = 0)
    private void onCreateEntry(T value, CallbackInfoReturnable<RegistryEntry.Reference<T>> cir) {
        if (ModBlocks.ALLOW_RUNTIME_REGISTRATION && this.intrusiveValueToEntry == null) {
            this.intrusiveValueToEntry = new IdentityHashMap<>();
        }
    }

    @Inject(method = "add", at = @At("TAIL"), require = 0)
    private void onAddTail(CallbackInfoReturnable<RegistryEntry.Reference<T>> cir) {
        if (ModBlocks.ALLOW_RUNTIME_REGISTRATION && this.intrusiveValueToEntry != null && this.intrusiveValueToEntry.isEmpty()) {
            this.intrusiveValueToEntry = null;
        }
    }
}
