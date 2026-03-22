# レジストリと定数管理ガイド（安全で効率的な登録手法）

MinecraftのMod開発において、アイテム、ブロック、液体、エネルギーなどの定数を個別に手作業で登録し、さらに対応する言語ファイル（JSON）やモデル（JSON）、BlockItemの登録を別々に行うと、**「BlockItemの登録忘れ」「タグの付け忘れ」「JSONの一致ミス」「翻訳名（lang）の抜け漏れ」**といったヒューマンエラー（人間的ミス）が頻発します。

大規模なMod（AE2、Create、Mekanismなど）は、独自の実装パターンやライブラリを活用することでこれらのミスを完全に排除し、ひとつの宣言で関連データ（DataGenを含む）をすべて自動生成する構造を取っています。本ガイドでは、ソースコード調査から得られたこれら3つの主要なアプローチを解説します。

---

## 1. Create アプローチ: `Registrate` による完全自動化（Fluent Builder パターン）

Createは、独自のラッパーライブラリである **`Registrate`** (現在は単独ライブラリとしても利用可能) を使用し、ブロックの登録からデータ生成（DataGen）までをメソッドチェーンで一気に定義します。

### 特徴と利点
*   **一元管理:** `REGISTRATE.block("name", ...)` の1つの文の中に、ブロックのプロパティ、タグ、ブロックステートのモデル生成プロセス、ドロップ（LootTable）の挙動、対応するアイテムの生成処理をすべて書くことができます。
*   **DataGenとの統合:** Javaコード上で「どのように登録するか」を書くと、ゲーム起動時の実際の登録処理だけでなく、ビルド時のJSONファイル出力（言語、モデル、タグ、レシピ）も自動で行ってくれます。

### 実装例（Createのソースより抜粋・簡略化）

```java
public static final BlockEntry<ShaftBlock> SHAFT = REGISTRATE.block("shaft", ShaftBlock::new)
    .initialProperties(SharedProperties::stone)
    .properties(p -> p.mapColor(MapColor.METAL).forceSolidOff()) // ブロックの基本設定
    .transform(CStress.setNoImpact())                            // 独自の応力（エネルギー）システムの属性付与
    .transform(pickaxeOnly())                                    // 「ツルハシ有効」用タグの自動追加（DataGen）
    .blockstate(BlockStateGen.axisBlockProvider(false))          // 設置方向に応じたBlockState JSONの自動生成
    .simpleItem()                                                // このブロックに対応する BlockItem を自動で登録
    .register();                                                 // 登録実行
```

**推奨されるケース:**
JSONの手書きを極限まで減らしたい場合や、新規アイテム・ブロックを大量に追加する予定があるModに最適です。

---

## 2. AE2 アプローチ: `DefinitionBuilder` パターンによる必須項目の制約

AE2はネイティブの `DeferredRegister` をラップする独自クラス（`BlockDefinition<T>` 等）を作成し、**「登録時に必ず英語の翻訳名（English Name）を渡さなければならない」** または **「BlockとBlockItemを同時に登録する」** ような制約をコードレベルで課しています。

### 特徴と利点
*   **登録と翻訳の紐付け:** 登録用メソッド `block("英語名", id, ブロックのSupplier)` の引数に生の英語名を含めることで、内部のDataGenが `en_us.json` を生成する際にこの文字列を確実に拾い出し、翻訳ファイルの書き忘れを防ぎます。
*   **Creative Tabへの自動登録:** アイテムやブロックを定義した時点で、内部で自動的にMod専用のクリエイティブタブへ追加されるよう管理されています。

### 実装例（AE2のソースより抜粋・簡略化）

```java
// AE2ではこのような独自のヘルパーメソッドを用意している
public static final BlockDefinition<InterfaceBlock> INTERFACE = 
    block("ME Interface", AEBlockIds.INTERFACE, InterfaceBlock::new);

// 内部の実装イメージ（抜粋）
private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier) {
    var deferredBlock = DR.register(id.getPath(), blockSupplier);
    var deferredItem = AEItems.DR.register(id.getPath(), () -> new BlockItem(deferredBlock.get(), new Item.Properties()));
    
    var itemDef = new ItemDefinition<>(englishName, deferredItem);
    MainCreativeTab.add(itemDef); // タブへ自動追加
    
    // 定義オブジェクトとして保持（DataGenでenglishNameを利用する）
    return new BlockDefinition<>(englishName, deferredBlock, itemDef);
}
```

**推奨されるケース:**
Registrateほどの巨大なフレームワークを入れたくないが、「BlockItemの登録忘れ」や「langファイルの英語名抜け・ミス」をJavaのコンパイルエラーとして防ぎたい場合に有効です。

---

## 3. Mekanism アプローチ: `BlockRegistryObject` とアタッチメント（Component）の一元化

Mekanismもネイティブのレジストリをラップした `BlockDeferredRegister` を使用します。最大の特徴は、独自のエネルギー、液体タンクの容量、ガス（Chemical）タンク、専用スロットなどの設定を、**ブロック（TileEntity）とアイテム（ItemBlock）の双方に同時にアタッチ（結合）する**ビルダーパターンを採用していることです。

### 特徴と利点
*   **ItemBlockとTileEntityの整合性:** アイテム状態でもエネルギー残量やスロットの情報を保持する必要がある機械類において、「ブロックとして置かれている時」と「アイテムとしてインベントリにある時」の挙動（NBT/Component）のズレをなくします。
*   **UI（ツールチップ）の自動化:** ブロックにどのような能力（Eject機能があるか、どのモードに対応しているか）を持たせているかの情報をアイテム生成時に付与し、ツールチップで自動的に表示させます。

### 実装例（Mekanismのソースより抜粋・簡略化）

```java
public static final BlockRegistryObject<BlockFactoryMachine<...>, ItemBlockTooltip<...>> PURIFICATION_CHAMBER =
    BLOCKS.register("purification_chamber", 
        () -> new BlockFactoryMachine<>(...),
        (block, properties) -> new ItemBlockTooltip<>(block, true, properties
              .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT) // Ejector機能の付与
        )
    ).forItemHolder(holder -> holder
          // この機械が持つ化学物質（Chemical）タンクの定義（アイテム状態でも保持する）
          .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                .addBasic(MAX_GAS, MekanismRecipeType.PURIFYING, ItemChemical::containsInputB)
                .build()
          // アイテムスロット機能の定義
          ).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                .addInput(MekanismRecipeType.PURIFYING, ItemChemical::containsInputA)
                .addOutput()
                .addEnergy() // エネルギー受容スロットの自動追加
                .build()
          )
    );
```

**推奨されるケース:**
機械系Modなど、複雑なNBTデータ（エネルギー容量、液体タンク、インベントリなど）をアイテム化した際にも保持させたり、機能に応じたツールチップを確実に付与したい場合に最適です。

---

## 結論：自作Modへの適用におけるベストプラクティス

これら大手Modの設計手法を取り入れ、ヒューマンエラーを防ぐには以下の構成を構築することをお勧めします。

1.  **ネイティブな `DeferredRegister` を直接呼ばず、ラッパークラス（`ModBlockRegistry` など）を作る。**
2.  ラッパークラスの引数に「英語名」「プロパティ」「付与するタグ」を渡し、メソッド内で必ず **「Blockの登録」「BlockItemの登録」「DataGen向けのキャッシュ（英語名やタグ）の保持」** を一括で行うようにする。
3.  独自データ（エネルギー容量やGUIの有無など）は、Mekanismのように登録時のビルダーチェインで宣言し、ItemBlockとBlock(Tile)のコード内でその宣言を参照する設計にする。

これにより、新しいブロックを1つ追加するときにコードを1行（または1ブロック）書くだけで、ゲーム内のすべて（モデル、言語、タブ、機能）が自動的に紐づくようになります。
