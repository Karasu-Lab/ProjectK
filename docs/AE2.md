# AE2 (Applied Energistics 2) 連携ガイド

> このガイドは `modules/Applied-Energistics-2/src/main/java/appeng/api` のソースコードに基づいています。

---

## セクション 1: 標準リソース（アイテム・液体・FE）の転送

AE2 は NeoForge の標準 Capability を持つ BlockEntity を自動認識します。自作機械で以下の Capability を提供するだけで、ME インターフェース・ストレージバス・エクスポートバスが自動的に対応します。

| リソース | 必要な Capability |
|---------|-----------------|
| アイテム | `IItemHandler` (NeoForge) |
| 液体 | `IFluidHandler` (NeoForge) |
| エネルギー (FE) | `IEnergyStorage` (NeoForge) |

---

## セクション 2: 独自エネルギー（カスタムキータイプ）の ME ネットワーク対応

自作 MOD の独自エネルギーを ME ドライブに格納したい場合は、`AEKeyType` と `AEKey` を実装し `AEKeyTypes.register()` で登録します。

### ① `AEKey` サブクラスの作成（AE2 依存クラス内）

```java
// neoforge/src/.../compat/ae2/MyEnergyAEKey.java
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;

public class MyEnergyAEKey extends AEKey {

    public static final MyEnergyAEKey INSTANCE = new MyEnergyAEKey();

    private MyEnergyAEKey() {}

    @Override
    public AEKeyType getType() {
        return MyEnergyKeyType.INSTANCE; // 後述の AEKeyType 実装を参照
    }

    @Override
    public AEKey copy() { return this; }

    @Override
    public Object getPrimaryKey() { return "my_energy"; }

    @Override
    public String getModID() { return MyMod.MOD_ID; }
}
```

### ② `AEKeyType` サブクラスの作成

```java
// neoforge/src/.../compat/ae2/MyEnergyKeyType.java
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MyEnergyKeyType extends AEKeyType {
    public static final MyEnergyKeyType INSTANCE = new MyEnergyKeyType();

    private MyEnergyKeyType() {
        super(
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "my_energy"),
            MyEnergyAEKey.class,
            Component.translatable("ae_keytype.mymod.my_energy")
        );
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return MapCodec.unit(MyEnergyAEKey.INSTANCE);
    }

    @Override
    public AEKey readFromPacket(RegistryFriendlyByteBuf input) {
        return MyEnergyAEKey.INSTANCE;
    }

    // ストレージ効率の調整（1バイトあたりの保存量）
    @Override
    public int getAmountPerByte() { return 8000; }

    // 1操作あたりの転送量
    @Override
    public int getAmountPerOperation() { return 1000; }
}
```

### ③ 初期化時の登録（`AEKeyTypes.register()`）

> **重要:** 旧ドキュメントにあった `Registry.register(AEKeyType.REGISTRY_KEY, ...)` は不正です。
> 正しくは `AEKeyTypes.register(keyType)` を使います。

```java
// neoforge/src/.../compat/ae2/AE2BridgeImpl.java
import appeng.api.stacks.AEKeyTypes;

public class AE2BridgeImpl implements IAE2Bridge {

    @Override
    public void registerCustomKeyType() {
        AEKeyTypes.register(MyEnergyKeyType.INSTANCE);
    }
}
```

---

## セクション 3: マルチローダー対応のブリッジ実装

```java
// common/src/.../compat/ae2/IAE2Bridge.java（Common）
public interface IAE2Bridge {
    void registerCustomKeyType();

    class Dummy implements IAE2Bridge {
        @Override public void registerCustomKeyType() {}
    }
}

// NeoForge メインクラス
public static IAE2Bridge ae2Bridge = new IAE2Bridge.Dummy();

public MyModNeoForge(IEventBus modBus) {
    if (ModList.get().isLoaded("ae2")) {
        ae2Bridge = new AE2BridgeImpl();
        ae2Bridge.registerCustomKeyType(); // MOD 初期化時に登録
    }
}
```

---

## セクション 4: 独自パーツ（インポート/エクスポートバス）の追加

パーツ（ケーブルに取り付けるバス等）を追加する場合は `IPart` インターフェースを実装し、モデルを AE2 のレジストリに登録します。

### ① モデルの事前登録（クライアント初期化時）

```java
import appeng.client.render.model.ModelsCache;
import appeng.client.render.part.PartModels;

// FMLClientSetupEvent または ClientModEvents.ON_REGISTER_GEOMETRY_LOADERS 内
@SubscribeEvent
public static void onClientSetup(FMLClientSetupEvent event) {
    PartModels.registerModels(
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_base"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_on"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_off"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_has_channel")
    );
}
```

### ② IPart クラスでのモデル返却

```java
import appeng.api.parts.IPartModel;
import appeng.client.render.parts.PartModel;

public class MyExportBusPart extends ExportBusPart {

    private static final IPartModel MODEL_OFF = new PartModel(
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_base"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_off")
    );
    private static final IPartModel MODEL_ON = new PartModel(
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_base"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_on")
    );
    private static final IPartModel MODEL_HAS_CHANNEL = new PartModel(
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_base"),
        ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "part/my_export_bus_has_channel")
    );

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) return MODEL_HAS_CHANNEL;
        else if (this.isPowered()) return MODEL_ON;
        else return MODEL_OFF;
    }
}
```

### ③ DataGen: モデル JSON の出力

AE2 パーツのモデルは通常の `block/` モデルと同じ形式で `assets/<modid>/models/part/` 以下に配置します。DataGen では `ItemModelProvider` の仕組みで出力可能です。

```java
// モデルファイルの出力先例：
// assets/mymod/models/part/my_export_bus_base.json
// assets/mymod/models/part/my_export_bus_on.json
// assets/mymod/models/part/my_export_bus_off.json
// assets/mymod/models/part/my_export_bus_has_channel.json
```
