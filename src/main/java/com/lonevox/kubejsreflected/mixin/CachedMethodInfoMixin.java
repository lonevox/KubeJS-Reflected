package com.lonevox.kubejsreflected.mixin;

import dev.latvian.mods.rhino.CachedMethodInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

@Mixin(CachedMethodInfo.class)
public class CachedMethodInfoMixin {
	@Final
	@Shadow
	Method method;

	@Inject(method = "invoke", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/rhino/ContextFactory;getMethodHandlesLookup()Ljava/lang/invoke/MethodHandles$Lookup;", shift = At.Shift.BEFORE), cancellable = true)
	private void kubejsreflected$invoke(Context cx, Scriptable scope, Object instance, Object[] args, CallbackInfoReturnable<Object> cir) throws InvocationTargetException, IllegalAccessException {
		Object result;
		var typeParameters = method.getParameterTypes();
		// Invoke with context as first argument if it is required
		if (typeParameters.length > 0 && typeParameters[0] != Object.class && typeParameters[0].isInstance(cx)) {
			var argsWithCx = new ArrayList<>(Arrays.asList(args));
			argsWithCx.addFirst(cx);
			result = method.invoke(instance, argsWithCx.toArray());
		} else {
			result = method.invoke(instance, args);
		}
		cir.setReturnValue(result);
		cir.cancel();
	}
}
