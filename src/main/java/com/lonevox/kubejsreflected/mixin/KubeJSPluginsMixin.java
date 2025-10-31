package com.lonevox.kubejsreflected.mixin;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import net.neoforged.neoforgespi.locating.IModFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(KubeJSPlugins.class)
public class KubeJSPluginsMixin {
	@Final
	@Shadow
	private static List<String> GLOBAL_CLASS_FILTER;

	@Inject(method = "loadMod", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER))
	private static void kubejsreflected$loadMod(String modId, IModFile mod, boolean loadClientPlugins, CallbackInfo ci) {
		if (modId.equals(KubeJS.MOD_ID)) {
			GLOBAL_CLASS_FILTER.remove("- java.lang");
		}
	}
}
