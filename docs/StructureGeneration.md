# 構造物（木・隕石など）の生成とマルチローダー共有ガイド

このドキュメントでは、木や隕石のような構造物を JSON を手書きせずに Common ソースで一括管理し、各ローダーの DataGen で生成する方法を解説します。

---

## セクション 1: 構造物生成の2種類のアプローチ

| 種類 | 規模 | 仕組み |
|------|------|-------|
| **Feature ベース** | 小〜中規模（木、ジオード） | `ConfiguredFeature` + `PlacedFeature` |
| **Structure ベース** | 中〜大規模（隕石、廃坑、村） | `Structure` + `StructureSet` + NBT/Jigsaw |

どちらも Common で `ResourceKey` と `BootstrapContext` メソッドを定義し、各ローダーの DataGen から `RegistrySetBuilder` で登録します。

---

## セクション 2: Feature ベース（木の例）

木のような Feature も鉱石生成（`OreGeneration.md`）と同じパターンで一元管理できます。

### Common: 定義クラス

```java
// common/src/.../worldgen/MyTreeFeatures.java
public class MyTreeFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> CUSTOM_TREE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "custom_tree"));

    public static final ResourceKey<PlacedFeature> CUSTOM_TREE_PLACED =
        ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "custom_tree"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        ctx.register(CUSTOM_TREE, new ConfiguredFeature<>(Feature.TREE,
            new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(MyBlocks.CUSTOM_LOG.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(MyBlocks.CUSTOM_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
            ).build()));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> cf = ctx.lookup(Registries.CONFIGURED_FEATURE);
        ctx.register(CUSTOM_TREE_PLACED, new PlacedFeature(
            cf.getOrThrow(CUSTOM_TREE),
            List.of(
                PlacementUtils.filteredByBlockSurvival(MyBlocks.CUSTOM_SAPLING.get()),
                RarityFilter.onAverageOnceEvery(5), // 5チャンクに1回
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
            )
        ));
    }
}
```

### NeoForge DataGen への追加

```java
public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
    .add(Registries.CONFIGURED_FEATURE, ctx -> {
        MyOreFeatures.bootstrapConfigured(ctx);
        MyTreeFeatures.bootstrapConfigured(ctx); // 追加
    })
    .add(Registries.PLACED_FEATURE, ctx -> {
        MyOreFeatures.bootstrapPlaced(ctx);
        MyTreeFeatures.bootstrapPlaced(ctx); // 追加
    })
    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MyBiomeModifiers::bootstrap);
```

### Fabric DataGeneratorEntrypoint への追加

```java
@Override
public void buildRegistry(RegistrySetBuilder registryBuilder) {
    registryBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> {
        MyOreFeatures.bootstrapConfigured(ctx);
        MyTreeFeatures.bootstrapConfigured(ctx);
    });
    registryBuilder.add(Registries.PLACED_FEATURE, ctx -> {
        MyOreFeatures.bootstrapPlaced(ctx);
        MyTreeFeatures.bootstrapPlaced(ctx);
    });
}
```

---

## セクション 3: Structure ベース（隕石・独自構造物の例）

大規模構造物は `Structure` + `StructureSet` の DataGen が必要です。さらに独自ロジックがある場合は `StructureType` へのレジストリ登録も必要となります。

### ステップ ①: `Structure` アブストラクトクラスの実装（Common）

```java
// common/src/.../worldgen/structures/CustomMeteoriteStructure.java
public class CustomMeteoriteStructure extends Structure {

    public static final MapCodec<CustomMeteoriteStructure> CODEC =
        simpleCodec(CustomMeteoriteStructure::new);

    public CustomMeteoriteStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        // 生成位置と配置ロジックを記述
        return onTopOfChunkCenter(ctx, Heightmap.Types.WORLD_SURFACE_WG,
            builder -> builder.addPiece(new CustomMeteoritePiece(/* ... */)));
    }

    @Override
    public StructureType<?> type() {
        return MyStructureTypes.METEORITE.get(); // StructureType レジストリへの参照
    }
}
```

### ステップ ②: `StructureType` のレジストリ登録（各ローダー）

`StructureType` は通常のレジストリ（`Registries.STRUCTURE_TYPE`）に登録します。

```java
// common/src/.../registry/MyStructureTypes.java (例: DeferredRegister)
public class MyStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
        DeferredRegister.create(Registries.STRUCTURE_TYPE, MyMod.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<CustomMeteoriteStructure>> METEORITE =
        STRUCTURE_TYPES.register("meteorite", () -> () -> CustomMeteoriteStructure.CODEC);
}
```

### ステップ ③: Structure と StructureSet の DataGen 定義（Common）

```java
// common/src/.../worldgen/MyStructures.java
public class MyStructures {

    public static final ResourceKey<Structure> METEORITE =
        ResourceKey.create(Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "meteorite"));

    public static final ResourceKey<StructureSet> METEORITE_SET =
        ResourceKey.create(Registries.STRUCTURE_SET,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "meteorite"));

    public static void bootstrapStructure(BootstrapContext<Structure> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        ctx.register(METEORITE, new CustomMeteoriteStructure(
            new Structure.StructureSettings(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD), // オーバーワールド全バイオーム
                Map.of(), // 生成可能なBiome内構造制限なし
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                TerrainAdjustment.NONE
            )
        ));
    }

    public static void bootstrapStructureSet(BootstrapContext<StructureSet> ctx) {
        HolderGetter<Structure> structures = ctx.lookup(Registries.STRUCTURE);
        ctx.register(METEORITE_SET, new StructureSet(
            structures.getOrThrow(METEORITE),
            new RandomSpreadStructurePlacement(
                32,  // spacing: グリッドのチャンクサイズ
                8,   // separation: グリッド内の最小余白
                RandomSpreadType.LINEAR,
                87654321 // MOD固有のシード値（他のStructureと被らないように）
            )
        ));
    }
}
```

### ステップ ④: DataGen への登録（両ローダー共通パターン）

```java
// NeoForge: RegistrySetBuilder
public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
    .add(Registries.CONFIGURED_FEATURE, MyOreFeatures::bootstrapConfigured)
    .add(Registries.PLACED_FEATURE, MyOreFeatures::bootstrapPlaced)
    .add(Registries.STRUCTURE, MyStructures::bootstrapStructure)           // 追加
    .add(Registries.STRUCTURE_SET, MyStructures::bootstrapStructureSet)    // 追加
    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MyBiomeModifiers::bootstrap);

// Fabric: DataGeneratorEntrypoint.buildRegistry
@Override
public void buildRegistry(RegistrySetBuilder registryBuilder) {
    registryBuilder.add(Registries.CONFIGURED_FEATURE, MyOreFeatures::bootstrapConfigured);
    registryBuilder.add(Registries.PLACED_FEATURE, MyOreFeatures::bootstrapPlaced);
    registryBuilder.add(Registries.STRUCTURE, MyStructures::bootstrapStructure);
    registryBuilder.add(Registries.STRUCTURE_SET, MyStructures::bootstrapStructureSet);
}
```

---

## セクション 4: Common 集約のメリットと注意点

| メリット | 説明 |
|---------|------|
| JSON 手書き不要 | DataGen が自動でJSON出力。タイポによる「生成されない」事故を防ぐ |
| パラメータの一元管理 | 生成頻度・Y座標範囲・サイズを1ファイルで管理 |
| 複数ローダー対応 | NeoForge/Fabric どちらも同じ Common の `bootstrap` を呼ぶだけ |

> **注意:** Fabric では `FabricDynamicRegistryProvider.configure` の `entries.addAll()` は、`buildRegistry` でブートストラップ済みのデータのみ出力します。`buildRegistry` のオーバーライドを**必ず**実装してください。
