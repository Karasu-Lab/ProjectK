# Botaniaパーティクルシステムの技術仕様

このドキュメントでは、Botaniaのマナ・アーキテクチャを支えるパーティクルシステムの登録、データ同期、およびレンダリングパイプラインの詳細について解説します。

## 1. 登録アーキテクチャ (Decoupled Registration)

Botaniaは、複数のModローダー（NeoForge/Fabric）に対応するため、パーティクルの登録を抽象化しています。

- **`BotaniaParticles`**: パーティクル型の集中管理クラス。
    - `ParticleType<T>` の定義 (`WISP`, `SPARKLE`)。
    - `registerParticles(BiConsumer<ParticleType<?>, ResourceLocation>)`: ローダー固有のレジストリに依存せず、関数型インターフェースを介して名前と型を紐付けます。

```java
// BotaniaParticles.java
public static void registerParticles(BiConsumer<ParticleType<?>, ResourceLocation> r) {
    r.accept(WISP, prefix("wisp"));
    r.accept(SPARKLE, prefix("sparkle"));
}
```

- **`FactoryHandler`**: クライアント側での `ParticleProvider` (Factory) の登録。
    - `SpriteSet` を受け取り、各パーティクルのコンストラクタに渡すファクトリを生成します。

```java
// ParticleProvider (Factory) の実装例
public static class Factory implements ParticleProvider<WispParticleData> {
    private final SpriteSet sprite;
    public Factory(SpriteSet sprite) { this.sprite = sprite; }

    @Override
    public Particle createParticle(WispParticleData data, ClientLevel world, 
        double x, double y, double z, double mx, double my, double mz) {
        FXWisp ret = new FXWisp(world, x, y, z, mx, my, mz, data.size, data.r, data.g, data.b, 
            data.depthTest, data.maxAgeMul, data.noClip, data.gravity);
        ret.pickSprite(sprite);
        return ret;
    }
}
```

## 2. データ構造とネットワーク同期 (`ParticleOptions`)

各パーティクルは、専用の `ParticleOptions` 実装クラスを持ち、属性の保持とシリアライズを制御します。

- **`WispParticleData` / `SparkleParticleData`**:
    - **シリアライズ**: `Codec` を使用してデータ駆動型の定義（NBT/JSON）に対応。
    - **ネットワーク**: `Deserializer` を介して、サーバーからクライアントへ属性（色、サイズ、重力、特殊フラグ等）をバイナリ同期します。
    - **属性例**: `size`, `r/g/b`, `maxAgeMul`, `depthTest`, `noClip`, `corrupt`, `fake`。

## 3. レンダリング・パイプライン (Custom Rendering)

パーティクルの視覚的な独自性は、カスタム `ParticleRenderType` によって定義されます。

### 3-1. ブレンドモードと頂点フォーマット
- **Additive Blending**: `RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)` を使用。これにより、色が重なるほど明るくなる「発光」表現を実現。
- **Vertex Format**: `DefaultVertexFormat.PARTICLE`（位置、テクスチャ座標、色、ライトマップ）を使用。

```java
// FXWisp.java / FXSparkle.java
private static void beginRenderCommon(BufferBuilder bufferBuilder, TextureManager textureManager) {
    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
    RenderSystem.depthMask(false);
    RenderSystem.enableBlend();
    // 加算合成 (Additive Blending) の設定
    RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
}
```

### 3-2. テクスチャ制御
- **Linear Filtering**: `setFilterSave(tex, true, false)` により、パーティクルテクスチャに線形フィルタリングを適用し、ピクセル感を抑えた滑らかな描画を行います。
- **Depth Mask**: `RenderSystem.depthMask(false)` で深度書き込みを無効化し、透過物体の重なりを正しく表現。
- **DIW (Depth Ignoring Wisp)**: 深度テストを無効化 (`RenderSystem.disableDepthTest()`) し、地形を突き抜けて視認できる効果。

## 4. シェーダー統合 (`CoreShaders`)

Botaniaはバニラのパーティクルシェーダーに加え、特定の条件下でカスタムシェーダーを適用します。

- **`filmGrainParticle`**: 
    - 「Corrupt（崩壊した）」状態のSPARKLEパーティクルに使用。
    - フィルム粒子のようなノイズエフェクトをシェーダーレベルで適用。
- **動的フォールバック**:
    - `BotaniaConfig.client().useShaders()` の設定に応じて、カスタムシェーダーと `GameRenderer.getParticleShader()` を動的に切り替えます。

```java
// CoreShaders.java
public static void init(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> registrations) {
    // フィルムグレインシェーダーの登録
    registrations.accept(
            prefix("film_grain_particle"),
            DefaultVertexFormat.PARTICLE,
            inst -> filmGrainParticle = inst
    );
}

public static ShaderInstance filmGrainParticle() {
    return BotaniaConfig.client().useShaders() ? filmGrainParticle : GameRenderer.getParticleShader();
}
```

## 5. ライフサイクル計算

- **Wisp**: `agescale` 変数により、寿命の前半で拡大し、後半で収束（消滅）するアニメーションをティックごとに計算します。
- **Sparkle**: `lifetime` に基づく縮小、および衝突時の `wiggleAround`（物理的な揺らぎ）ロジックが組み込まれています。
