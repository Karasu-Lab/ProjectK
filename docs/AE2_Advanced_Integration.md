# AE2における独自エネルギーと液体の統合ガイド

本ドキュメントでは、Applied Energistics 2 (AE2)に対して、自作MODの「独自エネルギー」や「独自の液体システム」を統合するためのアドバンスドな方法を解説します。
この手順を実装することで、AE2標準の**インポートバス**、**エクスポートバス**、**MEインターフェース**から独自エネルギーを出力させたり、**MEドライブ**（ストレージ）に独自の液体を保存することができるようになります。

また、AE2未導入時にクラス読み込みエラー（`NoClassDefFoundError`）を防ぐため、**ブリッジとインターフェースによるソフトデペンデンシー（Soft Dependency）パターン**を利用します。

---

## 1. ソフトデペンデンシー・ブリッジの構築

AE2専用のクラスを直接インポートしない「ブリッジ用のインターフェース」をコア（ مستقل）モジュールに定義します。

```java
package mymod.api.compat;

public interface IAE2Bridge {
    void register();

    class Dummy implements IAE2Bridge {
        @Override
        public void register() {
            // Do nothing when AE2 is not loaded
        }
    }
}
```

メインクラスの初期化時に、AE2が導入されているかを判定して実装クラスを遅延ロードします。

```java
package mymod;

import net.neoforged.fml.ModList;
import mymod.api.compat.IAE2Bridge;

public class MyMod {
    public static IAE2Bridge ae2Bridge = new IAE2Bridge.Dummy();

    public MyMod() {
        if (ModList.get().isLoaded("ae2")) {
            // Load the actual implementation safely
            ae2Bridge = new mymod.compat.ae2.AE2BridgeImpl();
        }
        ae2Bridge.register();
    }
}
```

---

## 2. 独自リソースタイプ (`AEKeyType`) の定義

AE2のAPIをインポートする実装クラス内で、独自エネルギーやカスタム液体を表す `AEKeyType` と固有の `AEKey` 実装を作成します。

```java
package mymod.compat.ae2;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKey;
import mymod.api.compat.IAE2Bridge;

public class AE2BridgeImpl implements IAE2Bridge {

    // Define the custom energy or fluid type for AE2
    public static final AEKeyType MY_ENERGY_TYPE = new AEKeyType(...) {
        @Override
        public int getAmountPerByte() {
            return 8000;
        }
    };

    @Override
    public void register() {
        // Register MY_ENERGY_TYPE to AEKeyType registry here
        registerBehaviors();
    }
}
```

---

## 3. インポートバス・エクスポートバス・インターフェースの対応

AE2標準のインポートバス、エクスポートバス、MEインターフェース等のパーツに対し、独自エネルギーを搬入出するよう教えるには、対象となる `AEKeyType` 向けにAE2の動作戦略（Grid Behaviors）を登録します。

これを登録するだけで、自作ブロックのCapabilityとAE2ネットワークのバスパーツが自動的に連携し、リソースのやり取りが可能になります。

```java
import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.ExternalStorageStrategy;

public void registerBehaviors() {
    // Register strategy for AE2 Import Bus
    StackImportStrategy.register(MY_ENERGY_TYPE, (level, pos, side) -> {
        return new MyEnergyImportStrategy(level, pos, side);
    });

    // Register strategy for AE2 Export Bus
    StackExportStrategy.register(MY_ENERGY_TYPE, (level, pos, side) -> {
        return new MyEnergyExportStrategy(level, pos, side);
    });

    // Register strategy for AE2 Storage Bus and ME Interface
    ExternalStorageStrategy.register(MY_ENERGY_TYPE, (level, pos, side) -> {
        return new MyEnergyExternalStorage(level, pos, side);
    });
}
```

*   **`StackImportStrategy`**: インポートバスが隣接ブロックからネットワーク内へ抜き取る挙動を定義します。
*   **`StackExportStrategy`**: エクスポートバスがネットワーク内から隣接ブロックへ送り込む挙動を定義します。
*   **`ExternalStorageStrategy`**: ストレージバスやMEインターフェースが隣接ブロックを外部ストレージ（`MEStorage`ラッパー）として認識するための挙動を定義します。

---

## 4. 独自の液体システムをMEドライブに保存させる方法

標準のNeoForge流体(`FluidStack`)とは異なる独自の液体やガスシステムを利用している場合、既存の「ME流体セル」には格納することができません。
独自の液体をそのままMEドライブに保存できるようにするためには、`appeng.api.storage.cells.IBasicCellItem` を実装した「専用のストレージセルアイテム」を作成する必要があります。

```java
package mymod.compat.ae2;

import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.stacks.AEKeyType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Custom Cell Item for ME Drive
public class MyLiquidCellItem extends Item implements IBasicCellItem {
    
    public MyLiquidCellItem(Properties properties) {
        super(properties);
    }

    @Override
    public AEKeyType getKeyType() {
        // Return your custom liquid key type
        return AE2BridgeImpl.MY_ENERGY_TYPE; 
    }

    @Override
    public int getBytes(ItemStack cellItem) {
        // Return max storage bytes for this cell
        return 4096;
    }

    @Override
    public double getIdleDrain() {
        return 0.5;
    }
}
```

この独自のセルアイテムをMinecraftの標準アイテムとして登録することで、プレイヤーはこれをAE2の標準的なMEドライブブロックに直接挿入できるようになります。
MEネットワークは、先ほど登録したStrategyとこのセルを紐づけ、独自の液体やエネルギーを自動でセルに保存・転送します。

---

## 5. DataGenによるバスモデルの自動生成と流用（テクスチャ配置のみで完結させる設計）

AE2のインポートバスやエクスポートバスのモデル（形状）は非常に複雑ですが、**DataGenを利用してAE2の標準モデルを親(Parent)として指定する**設計にすることで、自作MOD側は「決まった名前のテクスチャを配置するだけ」で独自のバスパーツを量産・流用できるようになります。

以下のコードは、NeoForgeの `BlockStateProvider` や `BlockModelProvider` を用いて、独自のバスパーツ向けモデル定義（ベース筐体と発光部分）を自動生成する例です。

```java
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MyModPartModelProvider extends BlockModelProvider {

    public MyModPartModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, "mymod", helper);
    }

    @Override
    protected void registerModels() {
        // Generate an export bus reusing AE2's shape
        generateBusModels("custom_export_bus");
        
        // Generate an import bus reusing AE2's shape
        generateBusModels("custom_import_bus");
    }

    private void generateBusModels(String busName) {
        // 1. Base Model: Reuses AE2's generic base shape
        withExistingParent("part/" + busName + "_base", "ae2:block/part/base")
            .texture("particle", "mymod:block/part/" + busName + "_base")
            .texture("texture", "mymod:block/part/" + busName + "_base");

        // 2. Front Model (Off state)
        withExistingParent("part/" + busName + "_off", "ae2:block/part/export_bus_front")
            .texture("particle", "mymod:block/part/" + busName + "_off")
            .texture("texture", "mymod:block/part/" + busName + "_off");

        // 3. Front Model (On state)
        withExistingParent("part/" + busName + "_on", "ae2:block/part/export_bus_front")
            .texture("particle", "mymod:block/part/" + busName + "_on")
            .texture("texture", "mymod:block/part/" + busName + "_on");
    }
}
```

このメソッド設計にしておけば、新しく `custom_fluid_export_bus` のようなパーツを追加したい場合でも、`registerModels()` 内で `generateBusModels("custom_fluid_export_bus")` を1行追記し、以下の3つのテクスチャ画像を `src/main/resources/assets/mymod/textures/block/part/` フォルダに配置してDataGenを実行するだけで、完璧な形状のバスモデルJSONが出力されます。

*   `custom_fluid_export_bus_base.png` (筐体全体)
*   `custom_fluid_export_bus_off.png` (未通電時の画面/ケーブル接続部)
*   `custom_fluid_export_bus_on.png` (通電時の画面/ケーブル接続部)

AE2側でこの出力された2つのモデル（ベースとフロント）を結合して描画するため、自作MOD側で複雑な面設定や回転を行う必要は一切なくなります。
