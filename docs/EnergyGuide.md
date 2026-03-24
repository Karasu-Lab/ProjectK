# ProjectK エネルギーシステム実装ガイド

このドキュメントでは、ProjectKにおける独自エネルギーの実装方法、マルチローダー（Fabric/NeoForge）対応、およびFE（Forge Energy）との統合方法について解説します。

## 1. 独自エネルギーシステムの概要

ProjectKでは、ローダーに依存しない共通のエネルギー管理クラスを `common` モジュールに定義し、各ローダー（Fabric/NeoForge）の独自のエネルギーAPIにブリッジする手法を採用します。これにより、ロジックの大部分を共通化しつつ、他MODとの互換性を確保できます。

## 2. `common` モジュールでの実装

### 2-1. 共通インターフェース (`IKPower`) の定義

まず、エネルギー操作を行うための共通インターフェースを定義します。

```java
package com.karasu256.projectk.energy;

/**
 * ProjectK独自のエネルギー操作インターフェース
 */
public interface IKPower {
    /**
     * エネルギーを注入する
     * @param maxReceive 注入する最大量
     * @param simulate シミュレーションかどうか
     * @return 実際に注入された量
     */
    long insert(long maxReceive, boolean simulate);

    /**
     * エネルギーを引き出す
     * @param maxExtract 引き出す最大量
     * @param simulate シミュレーションかどうか
     * @return 実際に引き出された量
     */
    long extract(long maxExtract, boolean simulate);

    long getAmount();
    long getCapacity();

    default boolean canInsert() { return true; }
    default boolean canExtract() { return true; }
}
```

### 2-2. 基盤となる保持クラス (`KPowerStorage`)

次に、`IKPower` を実装した基本的な保持クラスを用意します。

```java
public class KPowerStorage implements IKPower {
    protected long energy;
    protected long capacity;

    public KPowerStorage(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long received = Math.min(capacity - energy, maxReceive);
        if (!simulate) energy += received;
        return received;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        long extracted = Math.min(energy, maxExtract);
        if (!simulate) energy -= extracted;
        return extracted;
    }

    @Override
    public long getAmount() { return energy; }
    @Override
    public long getCapacity() { return capacity; }

    // NBTへの保存・読み込み
    public void save(CompoundTag nbt) {
        nbt.putLong("Energy", energy);
    }

    public void load(CompoundTag nbt) {
        energy = nbt.getLong("Energy");
    }
}
```

## 3. `KGenerator` への統合

`KGenerator` ブロックがエネルギー源として動作するように、`BlockEntity` を実装します。

### 3-1. `BlockEntity` の実装

```java
public class KGeneratorBlockEntity extends BlockEntity {
    private final KPowerStorage energy = new KPowerStorage(10000);

    public KGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.K_GENERATOR.get(), pos, state);
    }

    public KPowerStorage getEnergyStorage() {
        return energy;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KGeneratorBlockEntity be) {
        if (level.isClientSide) return;

        // 1. エネルギーの生成（例: 毎tick 10 KPower）
        be.energy.insert(10, false);

        // 2. 隣接ブロックへの供給
        if (be.energy.getAmount() > 0) {
            be.distributeEnergy(level, pos);
        }
        
        be.setChanged();
    }

    private void distributeEnergy(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockEntity targetBe = level.getBlockEntity(targetPos);
            if (targetBe != null) {
                // プラットフォームごとのエネルギー供給ロジックを呼び出す
                // (Architectury等のExpect/Actualパターンでの実装を推奨)
            }
        }
    }
}
```

## 4. NeoForge での対応 (IEnergyStorage / FE統合)

NeoForge では、`common` の `IKPower` を `IEnergyStorage` に変換するラッパーを作成し、`Capability` として公開します。

### 4-1. `IEnergyStorage` ラッパー

```java
public class NeoKPowerWrapper implements IEnergyStorage {
    private final IKPower internal;

    public NeoKPowerWrapper(IKPower internal) {
        this.internal = internal;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) internal.insert(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) internal.extract(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() { return (int) internal.getAmount(); }
    @Override
    public int getMaxEnergyStored() { return (int) internal.getCapacity(); }
    @Override
    public boolean canExtract() { return internal.canExtract(); }
    @Override
    public boolean canReceive() { return internal.canInsert(); }
}
```
> [!IMPORTANT]
> NeoForge では `IEnergyStorage` を実装し `Capabilities.EnergyStorage.BLOCK` に登録することで、**自動的に他MODの FE (Forge Energy) と統合されます。**

### 4-2. Capability の登録 (NeoForge モジュール)

```java
@SubscribeEvent
public static void registerCaps(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.K_GENERATOR.get(), (be, side) -> {
        return new NeoKPowerWrapper(be.getEnergyStorage());
    });
}
```

## 5. Fabric での対応 (Transfer API)

Fabric では `Fabric Transfer API` を使用します。`IKPower` を `Storage<EnergyVariant>` にブリッジします。

### 5-1. `Storage<EnergyVariant>` ラッパー

```java
public class FabricKPowerWrapper implements Storage<EnergyVariant> {
    private final IKPower internal;

    public FabricKPowerWrapper(IKPower internal) {
        this.internal = internal;
    }

    @Override
    public long insert(EnergyVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlank(resource);
        if (!resource.isMain()) return 0;
        
        long inserted = internal.insert(maxAmount, true); // シミュレーション
        transaction.addCloseCallback((t, result) -> {
            if (result.wasCommitted()) internal.insert(maxAmount, false); // コミット時に適用
        });
        return inserted;
    }
    
    // ... extract も同様に実装
}
```

### 5-2. BlockApiLookup の登録 (Fabric モジュール)

```java
public static void onInitialize() {
    EnergyStorage.SIDED.registerForBlockEntities((be, direction) -> {
        if (be instanceof KGeneratorBlockEntity generator) {
            return new FabricKPowerWrapper(generator.getEnergyStorage());
        }
        return null;
    }, ModBlockEntities.K_GENERATOR.get());
}
```

## 6. まとめ

1. **Common**: 独自インターフェースと保持クラスを作り、`BlockEntity` でロジックを完結させる。
2. **NeoForge**: `IEnergyStorage` ラッパーを介して Capability に登録。これで FE 互換となる。
3. **Fabric**: `Storage<EnergyVariant>` ラッパーを介して `BlockApiLookup` に登録。

この構成により、コードの再利用性を最大化しつつ、Minecraft の主要なエネルギーエコシステム（FE, Transfer API）との完全な統合が可能になります。
