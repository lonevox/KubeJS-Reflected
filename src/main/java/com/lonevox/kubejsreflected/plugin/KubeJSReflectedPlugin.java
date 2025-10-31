package com.lonevox.kubejsreflected.plugin;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class KubeJSReflectedPlugin implements KubeJSPlugin {
	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("Reflection", ReflectionWrapper.class);
	}
}
