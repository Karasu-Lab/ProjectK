# ProjectE 連携ガイド (NeoForge 向け)

> **注意:** このガイドはクローンした `modules/ProjectE` のAPIソースコードに基づいています。
> 旧バージョンのドキュメントに記載されていた `ProjectEAPI.getEMCProxy().registerCustomEMC()` は**存在しません**。
> `IEMCProxy` はEMC値の**読み取り専用**インターフェースです。

---

## 概要: EMC登録の3つの方法

| 方法 | 用途 | タイミング |
|------|------|------------|
| DataGen (`CustomConversionProvider`) | 固定EMC値・変換ルールをJSONで定義 | ビルド時（推奨） |
| `@EMCMapper` + `IEMCMapper` | レシピツリーを解析してEMCを動的計算 | サーバーリロード時 |
| `@RecipeTypeMapper` + `IRecipeTypeMapper` | 独自RecipeTypeのレシピをEMC計算エンジンに伝える | サーバーリロード時 |

---

## セクション 1: DataGen による固定EMC値の登録（最もシンプル）

特定アイテムに固定のEMC値を設定する場合、`CustomConversionProvider` を拡張したDataGenクラスを作成します。出力先は `data/<modid>/pe_custom_conversions/<name>.json` です。

### 実装例

```java
// neoforge/src/.../datagen/ProjectEConversionProvider.java
public class ProjectEConversionProvider extends CustomConversionProvider {

    public ProjectEConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, MyMod.MOD_ID);
    }

    @Override
    protected void addCustomConversions(HolderLookup.Provider registries) {
        createConversionBuilder(ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "main"))
            // 固定EMC値の設定 (before: グラフ計算前に設定)
            .before(NSSItem.createItem(MyItems.CUSTOM_ORE.get()), 256L)
            // 変換ルールの定義: copper_ingot 4個 -> my_alloy 1個
            .conversion(NSSItem.createItem(MyItems.MY_ALLOY.get()), 1)
                .ingredient(NSSItem.createItem(Items.COPPER_INGOT), 4)
            .end();
    }
}
```

### DataGenへの登録

```java
// DataGenイベントリスナー内
@SubscribeEvent
public static void onGatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    gen.addProvider(event.includeServer(), new ProjectEConversionProvider(
        gen.getPackOutput(),
        event.getLookupProvider()
    ));
}
```

---

## セクション 2: `@EMCMapper` + `IEMCMapper` による動的登録

DataGenではなく、ゲームの起動時やリロード時にプログラムでEMCを登録したい場合は `IEMCMapper` を実装し、クラスに `@EMCMapper` を付与します。ProjectEは `ModFileScanData` を使ってアノテーションスキャンを行い、自動的にこのクラスを発見・ロードします。

> **重要:** `@EMCMapper` はNeoForgeのクラスパススキャンで発見されるため、**インターフェースや抽象クラスには付与できません**。具象クラスに直接付与してください。

### 実装例

```java
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

@EMCMapper // ProjectEがこのクラスを自動検出しロードします
public class MyModEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper,
                            ReloadableServerResources serverResources,
                            RegistryAccess registryAccess,
                            ResourceManager resourceManager) {
        // MyItems.CUSTOM_ORE にEMC値 256 を設定 (グラフ計算前)
        mapper.setValueBefore(NSSItem.createItem(MyItems.CUSTOM_ORE.get()), 256L);

        // 変換ルールの登録: inputItem 4個 -> MY_ALLOY 1個 (素材のEMCから自動計算)
        Object2IntMap<NormalizedSimpleStack> ingredients = new Object2IntOpenHashMap<>();
        ingredients.put(NSSItem.createItem(Items.COPPER_INGOT), 4);
        mapper.addConversion(1, NSSItem.createItem(MyItems.MY_ALLOY.get()), ingredients);
    }

    @Override
    public String getName() { return "MyMod EMC Mapper"; }

    @Override
    public String getTranslationKey() { return "mymod.emc_mapper"; }
}
```

### シングルトンパターン（`@EMCMapper.Instance`）

インスタンスの生成をコントロールしたい場合は、静的フィールドに `@EMCMapper.Instance` を付与します。

```java
@EMCMapper
public class MyModEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

    @EMCMapper.Instance // このフィールドのインスタンスを使用する宣言
    public static final MyModEMCMapper INSTANCE = new MyModEMCMapper();

    // 以下同様に実装...
}
```

---

## セクション 3: `@RecipeTypeMapper` + `IRecipeTypeMapper` による独自レシピのEMC計算

自作機械の `RecipeType` を持つレシピをProjectEのグラフ計算エンジンに組み込むには `IRecipeTypeMapper` を実装します。これはProjectEの `CraftingMapper`（内部）が呼び出すハンドラです。

```java
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;

@RecipeTypeMapper // ProjectEが自動検出
public class MyMachineRecipeMapper implements IRecipeTypeMapper {

    @Override
    public boolean canHandle(RecipeType<?> recipeType) {
        // 自作機械のRecipeTypeだけを処理する
        return recipeType == MyRecipeTypes.MY_MACHINE.get();
    }

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper,
                                RecipeHolder<?> recipeHolder,
                                RegistryAccess registryAccess,
                                INSSFakeGroupManager fakeGroupManager) {
        if (!(recipeHolder.value() instanceof MyMachineRecipe recipe)) return false;

        // 出力アイテムをNSSに変換
        NSSItem output = NSSItem.createItem(recipe.getResultItem(registryAccess));
        if (output == null) return false;

        // 入力素材をNSSに変換してMapに積む
        Object2IntMap<NormalizedSimpleStack> ingredients = new Object2IntOpenHashMap<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack stack : ingredient.getItems()) {
                ingredients.merge(NSSItem.createItem(stack), 1, Integer::sum);
            }
        }

        // コレクターに変換を追加
        mapper.addConversion(recipe.getResultItem(registryAccess).getCount(), output, ingredients);
        return true;
    }

    @Override
    public String getName() { return "MyMod Machine Recipe Mapper"; }

    @Override
    public String getTranslationKey() { return "mymod.machine_recipe_mapper"; }
}
```

---

## セクション 4: EMC値の読み取り (`IEMCProxy`)

EMCの**登録**ではなく**読み取り**を行う場合は `IEMCProxy.INSTANCE` を使います。**ワールドがロードされた後にのみ有効な値が返ります。**

```java
import moze_intel.projecte.api.proxy.IEMCProxy;

// アイテムのEMC値を取得
long emc = IEMCProxy.INSTANCE.getValue(MyItems.CUSTOM_ORE.get());

// EMC値を持っているか確認
boolean hasEmc = IEMCProxy.INSTANCE.hasValue(stack);
```

---

## セクション 5: マルチローダー対応のブリッジ実装

ProjectEはNeoForge専用のため、`common` プロジェクトにはProjectEへの直接依存を持たせず、インターフェース/ブリッジパターンで隠蔽します。

### ① Common: インターフェース定義

```java
// common/src/.../compat/projecte/IProjectEBridge.java
public interface IProjectEBridge {
    /** EMC付与対象のアイテムをブリッジに通知する用途（実際の登録はDataGenや@EMCMapperで行う） */
    void setup();

    long getEMCValue(ItemLike item);

    class Dummy implements IProjectEBridge {
        @Override public void setup() {}
        @Override public long getEMCValue(ItemLike item) { return 0L; }
    }
}
```

### ② NeoForge: ブリッジ実装

```java
// neoforge/src/.../compat/projecte/ProjectEBridgeImpl.java
public class ProjectEBridgeImpl implements IProjectEBridge {

    @Override
    public void setup() {
        // 初期化はDataGenか @EMCMapper で行うため、ここでは空
    }

    @Override
    public long getEMCValue(ItemLike item) {
        // ワールドロード後のみ正確な値が返る
        return IEMCProxy.INSTANCE.getValue(item);
    }
}
```

### ③ NeoForge メインクラス: ソフト依存チェック

```java
public class MyModNeoForge {
    public static IProjectEBridge projectEBridge = new IProjectEBridge.Dummy();

    public MyModNeoForge(IEventBus modBus) {
        if (ModList.get().isLoaded("projecte")) {
            projectEBridge = new ProjectEBridgeImpl();
        }
        projectEBridge.setup();
    }
}
```

> **EMCの設定 (`@EMCMapper` の扱い) について:**
> `@EMCMapper`アノテーションを付与したクラスは、ProjectEが起動時にスキャンして自動ロードします。
> そのため `@EMCMapper` を付与したクラスはブリッジでラップする必要はありません。ただし、`requiredMods` を使って ProjectE がロードされている場合にのみ動作するよう制限できます。
>
> ```java
> @EMCMapper(requiredMods = "projecte") // ProjectEが存在する場合のみロード
> public class MyModEMCMapper implements IEMCMapper<...> { ... }
> ```
