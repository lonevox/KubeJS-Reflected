package com.lonevox.kubejsreflected.mixin;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.KubeJSContextFactory;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KubeJSContext.class)
public class KubeJSContextMixin extends Context {
	public KubeJSContextMixin(KubeJSContextFactory factory) {
		super(factory);
	}

	@Inject(method = "wrapAsJavaObject", at = @At("HEAD"), cancellable = true)
	private void kubejsreflected$wrapAsJavaObject(Scriptable scope, Object javaObject, TypeInfo target, CallbackInfoReturnable<Scriptable> cir) {
		 var result = super.wrapAsJavaObject(scope, javaObject, target);
		 cir.setReturnValue(result);
		 cir.cancel();
	}
}
