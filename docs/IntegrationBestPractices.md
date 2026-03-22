# 大規模MODの実装設計と他MODとの安全な連携（ブリッジ）手法

大規模なMOD（Create、AE2、Mekanismなど）がどのようにして膨大なブロック・メカニズムを効率的に登録・生成しているのか、そして依存MODの有無にかかわらずクラッシュを回避する「安全なソフト依存（Soft Dependency）のブリッジ実装」について解説します。

---

## 1. 効率的なブロック・モデル・レシピの生成・登録設計

大規模MODでは、手作業でJSONファイルを書いたり、1つ1つのブロックに対して個別に登録処理を書くことはほぼありません。以下の仕組みを用いて効率化を図っています。

### ① 登録の自動化・一元化 (Registrate 等の利用)
NeoForge標準の `DeferredRegister` をさらにラップし、**「ブロックの登録」「アイテム化」「モデルの生成設定」「ルートテーブルの発行」などを流れるように（Fluent APIで）一括設定するシステム**を独自に構築しているケースが多いです。

**Create の例 (Registrateライブラリの利用):**
Createは `Registrate` (tterrag氏が作成したライブラリ) を用いており、以下のように1つの文で全てを定義します。
```java
public static final BlockEntry<MechanicalPressBlock> MECHANICAL_PRESS = REGISTRATE
    .block("mechanical_press", MechanicalPressBlock::new)
    .initialProperties(SharedProperties::kineticBlocks)
    .item(KineticBlockItem::new).build()
    .blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(p.modLoc("block/press"))))
    .register();
```
このように記述することで、ゲームへの登録と同時に、DataGen（データジェネレータ）が走る際に必要なJSONファイル（BlockstateやItem Model）も自動生成される設計になっています。

### ② Data Generation (データジェネ) の徹底
レシピやモデル、タグのJSONは手書きせず、`DataGen` (`GatherDataEvent`) を通じてJavaコードから出力します。
*   **レシピ:** `ProcessingRecipeBuilder` のような独自のビルダーを作り、ループ処理などで全鉱石に対する粉砕バリエーションを一気に生成。
*   **モデル生成:** AE2のケーブル（パイプ）のように、接続状態によって無数に見た目が変わるものはJSONではなく、Javaの `IBakedModel` (カスタムモデルローダー) を使ってランタイムで動的にメッシュ（ポリゴン）を結合して描画しています。

---

## 2. MOD導入有無に依存しない安全なブリッジ実装（ソフト依存）

特定のMOD（例：REIやAE2など）が導入されている時だけそのAPIを使い、未導入なら何もしない（あるいはバニラの機能でフォールバックする）という実装を行う際、単純にAPIクラスをインポートして呼び出すと **`NoClassDefFoundError`** でクラッシュします。
これを防ぐための強力な設計が **「インターフェース・ブリッジパターンのダミー実行」** です。

### 設計と実装の手順

#### ① [コア側] ブリッジ用インターフェースの定義 (外部API非依存)
自分のMOD内に、外部MODのクラスを**一切インポートしない**インターフェースを作成します。

```java
// MyMod/api/compat/ICompatBridge.java
public interface ICompatBridge {
    void registerIntegration();     // 連携初期化用
    void doSomeAction(Object data); // プラグイン特有の処理
    
    // 未導入時用のダミー実装 (何も実行しない無の世界)
    class Dummy implements ICompatBridge {
        @Override
        public void registerIntegration() { /* 何もしない */ }
        @Override
        public void doSomeAction(Object data) { /* 何もしない */ }
    }
}
```

#### ② [互換モジュール側] 実際の処理を行う実装クラス (外部API依存)
このクラスの中でのみ、他MODのAPIをガッツリとインポートして処理を書きます。
※このクラスは、対象MODがロードされていない限り絶対にJVMに読み込まれないように隔離しておく必要があります。

```java
// MyMod/compat/rei/REICompatImpl.java
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
// ... REIのAPIを多数インポート ...

public class REICompatImpl implements ICompatBridge {
    @Override
    public void registerIntegration() {
        // REI特有の複雑な登録処理を実行
    }

    @Override
    public void doSomeAction(Object data) {
        // REIのGUIに関連する操作など
    }
}
```

#### ③ [コア側] ロード状態での動的インスタンス化
MODのメインクラスや初期化イベントで、`ModList`を使って他MODが導入されているかを確認します。導入されている場合のみ、実装クラスをインスタンス化します。

```java
// MyMod/MyModMain.java
import net.neoforged.fml.ModList;

public class MyModMain {
    public static ICompatBridge reiBridge;

    public MyModMain() {
        if (ModList.get().isLoaded("roughlyenoughitems")) {
            // クラスローディングのタイミングを遅延させるため、
            // Supplierやリフレクションを使ってインスタンスを生成する
            reiBridge = createREIBridge();
        } else {
            // 未導入時はダミーを割り当てる
            reiBridge = new ICompatBridge.Dummy();
        }
        
        // 開発者はクラッシュを一切気にせずメソッドを実行できる！
        reiBridge.registerIntegration();
    }
    
    // 【極めて重要】このメソッド内だけでREICompatImplに触れる。
    // メソッド自体が呼ばれなければREICompatImplはロードされないため安全。
    private ICompatBridge createREIBridge() {
        return new REICompatImpl();
    }
}
```

### なぜこの設計でエラーを回避できるのか？
Javaのクラスローダーは、**「そのクラス（やメソッド）が実際に実行・評価される瞬間」** まで、内部で参照している別クラスの参照解決（リンク）を行いません。
未導入（`ModList...isLoaded == false`）の時は `createREIBridge()` メソッドが呼ばれないため、JVMは `REICompatImpl` クラスの存在を確かめることなくスルーします。
また、どこからでも `reiBridge.doSomeAction()` のように呼び出せますが、未導入時は単なる `Dummy` クラスによる「空っぽの実行」となるため、処理分けの `if` 文をあちこちに散らかす必要がなく、非常にきれいで保守性の高いコードになります。
