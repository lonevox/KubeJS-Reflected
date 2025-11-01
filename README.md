
KubeJS Reflected
=======

Returns the ability to use reflection in [KubeJS](https://kubejs.com/) scripts, which became impossible in 1.21.

## Usage
> [!IMPORTANT]
> Calling Java Class object methods is unsupported, because Class objects in JS (such as `$ListTag.__javaObject__`) is seen by Rhino as a ListTag when finding its methods, not the ListTag Class object, so Rhino can't find any Class methods like `getDeclaredField`.

The following code throws an `EvaulatorException` when trying to call `getDeclaredField`.

```js
let $ListTag = Java.loadClass("net.minecraft.nbt.ListTag")

// NOTE: __javaObject__ only exists on objects loaded with Java.loadClass
const field_SELF_SIZE_IN_BYTES = $ListTag.__javaObject__.getDeclaredField("SELF_SIZE_IN_BYTES");
```

Instead, use the `Reflection` object. The following script works in `startup_scripts`, `server_scripts`, or `client_scripts`.

```js
let $ListTag = Java.loadClass("net.minecraft.nbt.ListTag")
let $Direction = Java.loadClass("net.minecraft.core.Direction")
let $LevelType = Java.loadClass("com.mojang.realmsclient.util.LevelType")
let $RecipeCategory = Java.loadClass("net.minecraft.data.recipes.RecipeCategory")

// Getting Classes:
console.log($ListTag.__javaObject__); // From Java.loadClass object
console.log(Reflection.getClass("net.minecraft.nbt.ListTag")); // From class name
console.log(Reflection.getClass(new $ListTag())); // From object
// NOTE: If you want a quick way to get several Classes from objects at once, you can use Reflection.objectsToClasses:
console.log(Reflection.objectsToClasses(Direction.DOWN, $LevelType.FLAT, $RecipeCategory.BUILDING_BLOCKS));

// Getting Fields:
console.log(Reflection.getField($Direction.__javaObject__, "name")); // From class
console.log(Reflection.getField("net.minecraft.core.Direction", "name")); // From class name
console.log(Reflection.getField(Direction.SOUTH, "name")); // From object

// Setting and getting field values (this changes the CPU text in the F3 info):
Reflection.setFieldValue("com.mojang.blaze3d.platform.GLX", "cpuInfo", "The best CPU");
console.log(Reflection.getFieldValue("com.mojang.blaze3d.platform.GLX", "cpuInfo"));
// NOTE: Since both functions above deal with the same field, it is better to cache the Field object and then use it like so:
const cpuInfoField = Reflection.getField("com.mojang.blaze3d.platform.GLX", "cpuInfo");
Reflection.setFieldValue(cpuInfoField, "The best CPU");
console.log(Reflection.getFieldValue(cpuInfoField));

// Getting and invoking private methods:
// NOTE: All private/protected Methods obtained with Reflection.getMethod are made accessible so that you can call them.
const getClockWiseXMethod = Reflection.getMethod(Direction.SOUTH, "getClockWiseX");
console.log(getClockWiseXMethod.invoke(Direction.SOUTH));
// NOTE: Methods can also be directly invoked on objects:
console.log(Reflection.invokeMethod(Direction.SOUTH, "getClockWiseX"));

// Getting and invoking private methods with parameters:
const directionClass = Reflection.getClass($Direction.__javaObject__);
const makeDirectionArrayMethod = Reflection.getMethod(directionClass, "makeDirectionArray", directionClass, directionClass, directionClass);
console.log(makeDirectionArrayMethod.invoke(null, Direction.NORTH, Direction.EAST, Direction.DOWN));
// NOTE: Reflection.invokeMethod can determine the method from the arguments for you, so this is equivalent to the above code:
console.log(Reflection.invokeMethod(directionClass, "makeDirectionArray", Direction.NORTH, Direction.EAST, Direction.DOWN));
```
