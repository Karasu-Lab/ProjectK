# ProjectK エネルギーシステム実装ガイド

このドキュメントでは、ProjectKにおける独自エネルギー (`AbyssEnergy`) の実装、および他MOD（Mekanism等）との高精度な統合管理について解説します。

## 1. 独自エネルギーシステムの概要

ProjectKでは、**AbyssEnergy (AE)** を全エネルギー計算の基準単位（リファレンスユニット）とします。

- **基準レートの原則**:
    - **1 AE = 10 FE** (Forge Energy)
    - **1 AE = 25 Joules** (Mekanism)
    - ※ 1 FE = 2.5 Joules (Mekanism標準) に準拠

## 2. モジュール式統合管理 (`ModIntegration`)

異なるMODごとのエネルギー変換レートを共通の仕組みで管理するため、`IEnergyIntegration` インターフェースを使用します。

### 2-1. `IEnergyIntegration` インターフェース

```java
public interface IEnergyIntegration extends IModIntegration {
    /** AE 1単位に対する該当MODのエネルギー量 (例: 10.0) */
    double getFromAbyssRate();
    
    /** 該当MODのエネルギー1単位に対する AE 量 (例: 0.1) */
    double getToAbyssRate();
}
```

### 2-2. 統合インスタンスの登録と取得

`ModIntegrationBootstrapper` によってロードされた統合クラスは、`ModIntegrationRegistry` に自動登録されます。

```java
// 使用例
Optional<IEnergyIntegration> mekanism = ModIntegrationRegistry.getEnergy("mekanism");
if (mekanism.isPresent()) {
    double rate = mekanism.get().getFromAbyssRate(); // 25.0
}
```

## 3. Mekanism との高精度統合例

`IEnergyIntegration` を実装した `MekanismIntegration` を作成することで、Joule 単位での変換をカプセル化します。

```java
public class MekanismIntegration extends AbstractNeoForgeModIntegration implements IEnergyIntegration {
    public static final String MOD_ID = "mekanism";

    @Override
    public double getToAbyssRate() { return 0.04; } // 1/25

    @Override
    public double getFromAbyssRate() { return 25.0; }
}
```

## 4. 便利な変換ユーティリティ (`ProjectKEnergy`)

コード内で AE と他MODエネルギーを相互変換する際は、`ProjectKEnergy` ユーティリティを使用することを推奨します。

```java
// FE から AE へ変換
long ae = ProjectKEnergy.toAbyssEnergy("forge", 100); // 10 AE

// AE から Mekanism Joule へ変換
long joules = ProjectKEnergy.fromAbyssEnergy("mekanism", 10); // 250 J
```

## 5. まとめ

1. **AbyssEnergy (AE) を絶対基準**とし、他MODとのレート差は統合クラスに押し込める。
2. **`IEnergyIntegration`** を実装することで、MOD固有の変換ロジックを分離・カプセル化する。
3. **`ModIntegrationRegistry`** を介して、実行時に安全に変換レートを取得する。

このアーキテクチャにより、ProjectK は将来的にどのMODのエネルギーシステムとも、依存関係をクリーンに保ったまま正確に統合することが可能です。
