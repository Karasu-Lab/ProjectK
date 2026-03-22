# Mekanism 連携ガイド（レシピ生成・機械追加・マルチローダー対応）

> このガイドは `modules/Mekanism/src/api` のソースコードに基づいています。

---

## セクション 1: Mekanismへのカスタムレシピ追加（DataGen）

Mekanism は専用の `RecipeBuilder` クラス群を API として提供しており、DataGen 経由でレシピ JSON を出力できます。

### 主な RecipeBuilder クラスと対応機械

| ビルダークラス | 対応機械 |
|--------------|---------|
| `ItemStackToItemStackRecipeBuilder.enriching(...)` | 濃縮室 (Enrichment Chamber) |
| `ItemStackToItemStackRecipeBuilder.crushing(...)` | クラッシャー (Crusher) |
| `ItemStackToItemStackRecipeBuilder.smelting(...)` | 製錬炉 (Energized Smelter) |
| `ItemStackChemicalToItemStackRecipeBuilder.injecting(...)` | 化学注入室 |
| `ChemicalCrystallizerRecipeBuilder.crystallizing(...)` | 化学結晶化装置 |

### 実装例（濃縮室レシピ）

```java
import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

// RecipeProvider.buildRecipes(RecipeOutput output) 内
ItemStackToItemStackRecipeBuilder.enriching(
    IngredientCreatorAccess.item().from(MyItems.CUSTOM_ORE.get()), // 入力: 自作鉱石
    new ItemStack(MyItems.CUSTOM_DUST.get(), 2)                    // 出力: 自作ダスト x2
).build(output); // Holder<Item> ベースでIDを自動決定
```

> **注意:** `build(output)` は出力アイテムの `Holder<Item>` から ID を自動生成します。
> カスタムID を指定したい場合は `build(output, ResourceLocation.fromNamespaceAndPath(MOD_ID, "my_recipe"))` を使ってください。

### IngredientCreatorAccess の主なメソッド

```java
IngredientCreatorAccess.item()        // ItemStackIngredient を作成
IngredientCreatorAccess.fluid()       // FluidStackIngredient を作成
IngredientCreatorAccess.chemicalStack() // ChemicalStackIngredient を作成（10.7以降）
IngredientCreatorAccess.chemical()    // ChemicalIngredient を作成（10.7以降）
```

---

## セクション 2: 自作機械への Mekanism リソース対応

### `IChemicalHandler`（旧 `IGasHandler`）

> **重要:** 旧バージョンドキュメントに記載されていた `IGasHandler` は現在 **`IChemicalHandler`** に統合されています。ガス・スラリー・色素・注入材はすべて「Chemical」として統一管理されています。

自作 BlockEntity に Mekanism の Chemical（ガス等）を扱わせる場合、NeoForge の Capability として `IChemicalHandler` を公開します。

```java
// neoforge/src/.../MyMachineBlockEntity.java
import mekanism.api.chemical.IChemicalHandler;
// ...

public class MyMachineBlockEntity extends BlockEntity {

    private final ChemicalTank chemicalTank = /* Mekanism API で初期化 */;

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == MekanismCapabilities.CHEMICAL) {
            return LazyOptional.of(() -> chemicalTank).cast();
        }
        return super.getCapability(cap, side);
    }
}
```

---

## セクション 3: マルチローダー環境でのブリッジ実装

Mekanism は NeoForge 専用のため、`common` モジュールに Mekanism クラスを import してはいけません。

### ① Common: インターフェース定義

```java
// common/src/.../compat/mekanism/IMekanismBridge.java
public interface IMekanismBridge {
    void generateRecipes(RecipeOutput output);

    class Dummy implements IMekanismBridge {
        @Override
        public void generateRecipes(RecipeOutput output) {}
    }
}
```

### ② NeoForge: ブリッジ実装

```java
// neoforge/src/.../compat/mekanism/MekanismBridgeImpl.java
import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

public class MekanismBridgeImpl implements IMekanismBridge {

    @Override
    public void generateRecipes(RecipeOutput output) {
        ItemStackToItemStackRecipeBuilder.enriching(
            IngredientCreatorAccess.item().from(MyItems.CUSTOM_ORE.get()),
            new ItemStack(MyItems.CUSTOM_DUST.get(), 2)
        ).build(output, ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "enriching_custom_ore"));

        ItemStackToItemStackRecipeBuilder.crushing(
            IngredientCreatorAccess.item().from(MyItems.CUSTOM_ORE.get()),
            new ItemStack(MyItems.CUSTOM_DUST.get(), 1)
        ).build(output, ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "crushing_custom_ore"));
    }
}
```

### ③ NeoForge DataGen エントリー

```java
// NeoForge Mod初期化クラス
public static IMekanismBridge mekanismBridge = new IMekanismBridge.Dummy();

public MyModNeoForge(IEventBus modBus) {
    if (ModList.get().isLoaded("mekanism")) {
        mekanismBridge = new MekanismBridgeImpl();
    }
}

// RecipeProvider.buildRecipes() 内
@Override
protected void buildRecipes(RecipeOutput output) {
    MyModNeoForge.mekanismBridge.generateRecipes(output);
}
```

---

## セクション 4: DataGen での登録先パスと出力例

Mekanism のレシピ Builder は `data/<modid>/recipe/` 以下にJSONを出力します。

```
data/mymod/recipe/enriching_custom_ore.json   <- enriching() で生成
data/mymod/recipe/crushing_custom_ore.json    <- crushing() で生成
```

これらは Mekanism がゲーム起動時に自動的に読み込みます。手書きのJSONは不要です。
