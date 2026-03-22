# 鉱石生成（Ore Generation）の実装とマルチローダー共有ガイド

Minecraft 1.18 以降の鉱石生成はデータドリブンです。`ConfiguredFeature`（何をどう置くか）と `PlacedFeature`（どこに配置するか）を `BootstrapContext` で定義し、DataGen で JSON として出力します。

このドキュメントでは、**Common ソースで定義を一括管理**し、各ローダーの DataGen で同じ JSON を生成する方法を解説します。

---

## セクション 1: 3つの登録ステップ

| ステップ | 内容 | 管理場所 |
|---------|------|---------|
| `ConfiguredFeature` | 何を・どのブロックに・どのサイズで | **Common** |
| `PlacedFeature` | 何回・どのY座標に・どう分布させるか | **Common** |
| バイオームへの追加 | どのバイオームに追加するか | 各ローダー固有 |

---

## セクション 2: Common での一括定義

```java
// common/src/.../worldgen/MyOreFeatures.java
public class MyOreFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> CUSTOM_ORE_CONFIGURED =
        ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "custom_ore"));

    public static final ResourceKey<PlacedFeature> CUSTOM_ORE_PLACED =
        ResourceKey.create(Registries.PLACED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "custom_ore"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        List<OreConfiguration.TargetBlockState> targets = List.of(
            OreConfiguration.target(
                new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                MyBlocks.CUSTOM_ORE.get().defaultBlockState()),
            OreConfiguration.target(
                new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                MyBlocks.DEEPSLATE_CUSTOM_ORE.get().defaultBlockState())
        );
        ctx.register(CUSTOM_ORE_CONFIGURED,
            new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, 9)));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> cfGetter = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder.Reference<ConfiguredFeature<?, ?>> configured = cfGetter.getOrThrow(CUSTOM_ORE_CONFIGURED);
        ctx.register(CUSTOM_ORE_PLACED, new PlacedFeature(configured, List.of(
            CountPlacement.of(10),
            InSquarePlacement.spread(),
            HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)),
            BiomeFilter.biome()
        )));
    }
}
```

---

## セクション 3: 各ローダーでの DataGen 登録

両ローダーとも `RegistrySetBuilder` を使います。

### NeoForge (`DatapackBuiltinEntriesProvider`)

```java
// neoforge/src/.../datagen/NeoForgeWorldGenProvider.java
public class NeoForgeWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.CONFIGURED_FEATURE, MyOreFeatures::bootstrapConfigured)
        .add(Registries.PLACED_FEATURE, MyOreFeatures::bootstrapPlaced)
        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MyBiomeModifiers::bootstrap);

    public NeoForgeWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MyMod.MOD_ID));
    }
}

// GatherDataEvent リスナー内
@SubscribeEvent
public static void onGatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    gen.addProvider(event.includeServer(),
        new NeoForgeWorldGenProvider(gen.getPackOutput(), event.getLookupProvider()));
}
```

### Fabric (`FabricDynamicRegistryProvider`)

> **注意:** Fabric では `RegistrySetBuilder` をデータ生成の Entrypoint で組み合わせて使います。`FabricDynamicRegistryProvider.configure` の `addAll` だけでは、**bootstrap されていないエントリは出力されません**。

```java
// fabric/src/.../datagen/FabricWorldGenProvider.java
public class FabricWorldGenProvider extends FabricDynamicRegistryProvider {

    public FabricWorldGenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        // ConfiguredFeature と PlacedFeature をすべて出力する
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
    }

    @Override
    public String getName() { return "MyMod WorldGen"; }
}

// DataGeneratorEntrypoint 実装クラス内
// RegistrySetBuilder で bootstrap 済みの HolderLookup.Provider を渡す必要がある
@Override
public void buildRegistry(RegistrySetBuilder registryBuilder) {
    registryBuilder.add(Registries.CONFIGURED_FEATURE, MyOreFeatures::bootstrapConfigured);
    registryBuilder.add(Registries.PLACED_FEATURE, MyOreFeatures::bootstrapPlaced);
}
```

Fabric の `DataGeneratorEntrypoint` 実装クラスに `buildRegistry` をオーバーライドすることで、DataGen 実行時に `FabricDynamicRegistryProvider.configure` へ渡される `HolderLookup.Provider` に自分の定義が含まれるようになります。

---

## セクション 4: バイオームへの追加

定義した `PlacedFeature` をワールドに配置させるには、バイオームへ追加する処理が必要です。

### NeoForge (BiomeModifier DataGen)

```java
// common/src/.../worldgen/MyBiomeModifiers.java
public class MyBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_CUSTOM_ORE =
        ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "add_custom_ore"));

    public static void bootstrap(BootstrapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> features = ctx.lookup(Registries.PLACED_FEATURE);
        ctx.register(ADD_CUSTOM_ORE, new AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(features.getOrThrow(MyOreFeatures.CUSTOM_ORE_PLACED)),
            GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }
}
```

### Fabric (BiomeModifications コード呼び出し)

```java
// fabric/src/.../FabricMod.java (ModInitializer)
@Override
public void onInitialize() {
    BiomeModifications.addFeature(
        BiomeSelectors.foundInOverworld(),
        GenerationStep.Decoration.UNDERGROUND_ORES,
        MyOreFeatures.CUSTOM_ORE_PLACED
    );
}
```
