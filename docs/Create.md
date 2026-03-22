# Create 連携ガイド（機械へのレシピ追加・DataGen・マルチローダー対応）

> このガイドは `modules/Create/src/main/java` のソースコードに基づいています。

---

## セクション 1: Create 機械のレシピタイプ一覧

| 機械 | `AllRecipeTypes` 定数 | レシピタイプ ID |
|------|----------------------|---------------|
| 粉砕ホイール | `CRUSHING` | `create:crushing` |
| 石臼 | `MILLING` | `create:milling` |
| 加圧機 | `PRESSING` | `create:pressing` |
| ミキサー | `MIXING` | `create:mixing` |
| 圧縮器 | `COMPACTING` | `create:compacting` |
| デプロイヤー | `DEPLOYING` | `create:deploying` |
| ノコギリ | `CUTTING` | `create:cutting` |
| 充填機 | `FILLING` | `create:filling` |
| 排出機 | `EMPTYING` | `create:emptying` |

---

## セクション 2: DataGen（推奨） — RecipeGen クラスを継承する

> **重要:** Create が公開している API は `ProcessingRecipeBuilder` を直接 `new` する方法ではなく、**`CrushingRecipeGen` などの `Gen` クラスを継承する**パターンです。

### 粉砕レシピ（Crushing）の DataGen クラス

```java
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class MyModCrushingRecipes extends CrushingRecipeGen {

    public MyModCrushingRecipes(PackOutput output,
                                 CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MyMod.MOD_ID); // defaultNamespace を MOD_ID に
    }

    // GeneratedRecipe フィールドとして定義する（フィールドを宣言するだけで自動登録される）
    GeneratedRecipe CUSTOM_ORE = create(
        MyItems.CUSTOM_ORE::get,           // 入力アイテム（IDの自動生成に使われる）
        b -> b.duration(250)
              .output(MyItems.CUSTOM_DUST.get(), 2)
              .output(0.5f, MyItems.BONUS_DUST.get(), 1) // 50%でボーナスアイテム
    );

    GeneratedRecipe RAW_CUSTOM_ORE = create(
        MyItems.RAW_CUSTOM_ORE::get,
        b -> b.duration(200)
              .output(MyItems.CUSTOM_DUST.get(), 1)
    );
}
```

> `create(Supplier<ItemLike>, UnaryOperator<Builder>)` は入力アイテムの Registry Key からレシピ ID を自動生成します。
> カスタム ID が必要な場合は `create(ResourceLocation, UnaryOperator<Builder>)` を使ってください。

### その他の機械（Mixing 等）

```java
import com.simibubi.create.api.data.recipe.MixingRecipeGen;

public class MyModMixingRecipes extends MixingRecipeGen {

    public MyModMixingRecipes(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MyMod.MOD_ID);
    }

    GeneratedRecipe ALLOY = create(
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "my_alloy"),
        b -> b.duration(100)
              .require(MyItems.CUSTOM_DUST.get())
              .require(Items.COPPER_INGOT)
              .output(MyItems.MY_ALLOY.get())
    );
}
```

### GatherDataEvent への登録

```java
@SubscribeEvent
public static void onGatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

    gen.addProvider(event.includeServer(),
        new MyModCrushingRecipes(gen.getPackOutput(), lookup));
    gen.addProvider(event.includeServer(),
        new MyModMixingRecipes(gen.getPackOutput(), lookup));
}
```

---

## セクション 3: JSON による手書きレシピ（DataGen を使わない場合）

```json
// data/mymod/recipe/crushing_custom_ore.json
{
  "type": "create:crushing",
  "ingredients": [{ "item": "mymod:custom_ore" }],
  "results": [
    { "item": "mymod:custom_dust", "count": 2 },
    { "item": "mymod:bonus_dust", "chance": 0.5 }
  ],
  "processingTime": 250
}
```

---

## セクション 4: マルチローダー対応のブリッジ実装

Create は NeoForge 専用です。`common` モジュールには Create クラスを import してはいけません。

### ① Common: インターフェース定義

```java
// common/src/.../compat/create/ICreateBridge.java
public interface ICreateBridge {
    void addDataProviders(DataGenerator gen, CompletableFuture<HolderLookup.Provider> lookup);

    class Dummy implements ICreateBridge {
        @Override
        public void addDataProviders(DataGenerator gen, CompletableFuture<HolderLookup.Provider> lookup) {}
    }
}
```

### ② NeoForge: ブリッジ実装

```java
// neoforge/src/.../compat/create/CreateBridgeImpl.java
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;

public class CreateBridgeImpl implements ICreateBridge {

    @Override
    public void addDataProviders(DataGenerator gen, CompletableFuture<HolderLookup.Provider> lookup) {
        gen.addProvider(true, new MyModCrushingRecipes(gen.getPackOutput(), lookup));
        gen.addProvider(true, new MyModMixingRecipes(gen.getPackOutput(), lookup));
    }
}
```

### ③ NeoForge GatherDataEvent 内で呼び出し

```java
public static ICreateBridge createBridge = new ICreateBridge.Dummy();

public MyModNeoForge(IEventBus modBus) {
    if (ModList.get().isLoaded("create")) {
        createBridge = new CreateBridgeImpl();
    }
    modBus.addListener(this::onGatherData);
}

private void onGatherData(GatherDataEvent event) {
    createBridge.addDataProviders(event.getGenerator(), event.getLookupProvider());
}
```
