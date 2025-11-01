package com.lonevox.kubejsreflected.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows for Java Class objects to be sent from JS to Java. This is fixed in Rhino in <a href="https://github.com/KubeJS-Mods/Rhino/pull/66">this PR</a>.
 */
@Mixin(Context.class)
public class ContextMixin {
    @Inject(method = "wrapAsJavaObject", at = @At("TAIL"), cancellable = true)
    private void kubejsreflected$wrapAsJavaObject(Scriptable scope, Object javaObject, TypeInfo target, CallbackInfoReturnable<NativeJavaClass> cir) {
        if (javaObject instanceof Class<?> clazz) {
            cir.setReturnValue(new NativeJavaClass((Context) (Object) this, scope, clazz));
            cir.cancel();
        }
    }
}
