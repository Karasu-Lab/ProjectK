# REI (Roughly Enough Items) 連携ガイド

> このガイドは `modules/RoughlyEnoughItems` のAPIソースコードに基づいています。

---

## セクション 1: プラグインクラスの作成と登録

REIへの機能追加は **`REIClientPlugin`** インターフェースを実装したクラスを作ることで行います（クライアントサイドのみ）。

### プラグインクラスの例

```java
// neoforge/src/client/java/.../MyModREIPlugin.java
// または fabric/src/client/java/.../MyModREIPlugin.java

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
// ...その他のインポート

public class MyModREIPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) { /* ... */ }

    @Override
    public void registerDisplays(DisplayRegistry registry) { /* ... */ }

    @Override
    public void registerScreens(ScreenRegistry registry) { /* ... */ }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) { /* ... */ }
}
```

### 登録方法（ローダー別）

REIのプラグインは、ローダーのサービスローダー（`META-INF`）経由で登録します。**アノテーションによる自動検出ではありません。**

**Fabric** の場合、`fabric.mod.json` に記載：
```json
{
  "entrypoints": {
    "rei-client": ["com.yourmod.rei.MyModREIPlugin"]
  }
}
```

**NeoForge** の場合、`neoforge.mods.toml` に記載：
```toml
[[dependencies.yourmodid]]
[[mods.yourmodid.clientExtraModFiles]]

# または REIが提供する @REIClientPlugin アノテーションを使う場合
```
> **実際の方法:** REI-NeoForge版は `IClientModPlugin` をサービスでロードするか `@REIPluginProvider` 相当の仕組みを使います。最も確実なのは REI の NeoForge 版 README や neoforge モジュール内の `entrypoints` 設定を参照してください。

---

## セクション 2: カテゴリとディスプレイの登録

自作機械のレシピをREI上に表示するには **Category（カテゴリ）** と **Display（表示データ）** が必要です。

```java
public static final CategoryIdentifier<MyMachineDisplay> MY_CATEGORY =
    CategoryIdentifier.of(MyMod.MOD_ID, "my_machine");

@Override
public void registerCategories(CategoryRegistry registry) {
    // 自作DisplayCategoryの実装クラスを登録
    registry.add(new MyMachineCategory());

    // このカテゴリの"ワークステーション"として自作ブロックを関連付ける（REI上でアイコン表示される）
    registry.addWorkstations(MY_CATEGORY, EntryStacks.of(MyBlocks.MY_MACHINE.get()));
}

@Override
public void registerDisplays(DisplayRegistry registry) {
    // RecipeManager内の MyMachineRecipe を MyMachineDisplay に変換して登録
    registry.registerRecipeFiller(MyMachineRecipe.class, MyMachineRecipe.TYPE, MyMachineDisplay::new);
}
```

---

## セクション 3: ClickArea — GUIからレシピ画面を開く

自作 GUI の特定範囲（矢印ボタンなど）をクリックしたときに REI のレシピリストを開くには `registerContainerClickArea` または `registerClickArea` を使います。

> **旧API注意:** `registry.registerClickArea(screen -> new Rect2i(...), ScreenClass, CATEGORY)` は**削除済み**です。

### 正しいAPI (`registerContainerClickArea`)

`AbstractContainerScreen` を継承した画面クラスには、GUIの左上を基準とした相対座標で指定します。

```java
@Override
public void registerScreens(ScreenRegistry registry) {
    // AbstractContainerScreen を継承した画面の場合（推奨）
    // 座標はGUIの左上(getGuiLeft/getGuiTop)からの相対座標
    registry.registerContainerClickArea(
        new Rectangle(76, 30, 22, 15), // x=76, y=30 を基点に 22x15 の矩形
        MyMachineScreen.class,
        MY_CATEGORY
    );
}
```

### 動的な矩形参照が必要な場合 (`SimpleClickArea` ラムダ)

```java
@Override
public void registerScreens(ScreenRegistry registry) {
    registry.registerContainerClickArea(
        screen -> new Rectangle(screen.getGuiLeft() + 76, screen.getGuiTop() + 30, 22, 15),
        MyMachineScreen.class,
        MY_CATEGORY
    );
}
```

---

## セクション 4: TransferHandler — ワンクリック移動機能

REI のレシピ画面で「＋ボタン」を押したとき、プレイヤーのインベントリから GUIへアイテムを自動移動させる機能は `TransferHandler` で実装します。

### `SimpleTransferHandler.create` を使う（シンプルなケース）

`SimpleTransferHandler` は `@ApiStatus.Experimental` ですが、標準的なスロット構成の機械であれば十分実用的です。

```java
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;

@Override
public void registerTransferHandlers(TransferHandlerRegistry registry) {
    registry.register(SimpleTransferHandler.create(
        MyMachineMenu.class,
        MY_CATEGORY,
        new SimpleTransferHandler.IntRange(0, 2) // インデックス 0 から 2（exclusive）= スロット0,1が入力スロット
    ));
}
```

> **`IntRange` の型:** `record IntRange(int min, int maxExclusive)` です。第2引数は**末尾を含まない（exclusive）**インデックスです。

### カスタム `TransferHandler` の実装（複雑なケース）

スロット番号が固定でない場合や特殊なロジックが必要な場合は `TransferHandler` を直接実装します。

```java
public class MyMachineTransferHandler implements TransferHandler {
    @Override
    public ApplicabilityResult checkApplicable(Context context) {
        if (!(context.getMenu() instanceof MyMachineMenu)
            || !(context.getDisplay() instanceof MyMachineDisplay)) {
            return ApplicabilityResult.createNotApplicable();
        }
        return ApplicabilityResult.createApplicable();
    }

    @Override
    public Result handle(Context context) {
        if (!context.isActuallyCrafting()) {
            // isActuallyCrafting が false のとき は移動を実行しない（ハイライト表示のみ）
            return Result.createSuccessful();
        }
        // 実際にアイテムを移動するサーバーパケット送信処理を書く...
        return Result.createSuccessful();
    }
}
```

---

## セクション 5: マルチローダー対応のブリッジ実装

REI は `@Environment(EnvType.CLIENT)` であるため、**クライアントサイドのみ**で動作するコードです。Common モジュールには絶対に REI の import を書いてはいけません。

```java
// common/src/.../compat/rei/IREIBridge.java (Common)
public interface IREIBridge {
    void registerPlugin();

    class Dummy implements IREIBridge {
        @Override public void registerPlugin() {}
    }
}

// neoforge/src/.../compat/rei/REIBridgeImpl.java (NeoForge, Clientサイド)
public class REIBridgeImpl implements IREIBridge {
    @Override
    public void registerPlugin() {
        // 実際の登録はentrypoint経由で MyModREIPlugin が呼ばれるため
        // ここでは起動時チェックなど補助的な処理のみ行う
    }
}

// NeoForge Mainクラス (クライアント初期化時のみ実行)
public static IREIBridge reiBridge = new IREIBridge.Dummy();

public void onClientSetup() {
    if (ModList.get().isLoaded("rei")) {
        reiBridge = new REIBridgeImpl();
    }
}
```
