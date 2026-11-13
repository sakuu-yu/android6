# 实验六：多 Fragment 应用

Android 课程实验六，在实验五的基础上把应用重构为多 Fragment 结构，并使用对象序列化保存个人信息和翻牌游戏的历史数据。

> 姓名：王浩宇　学号：2024110536

## 功能

- **个人信息 Fragment**：展示头像、姓名、性别、学号、电话、邮件地址、爱好、两种游戏的最高分以及上次保存时间；点击「修改个人信息」可在同一 Fragment 内编辑并保存。
- **两张牌翻牌游戏 Fragment**：连续翻开两张牌，点数相同或花色相同即可配对。
- **三张牌翻牌游戏 Fragment**：连续翻开三张牌，点数相同或花色相同即可配对。
- **历史记录持久化**：每次翻牌、配对、重置以及离开页面时都会保存当前游戏状态；重新打开应用后，牌面、得分、最高分和最近记录都能恢复。
- **对象序列化存储**：个人信息和游戏模型均实现 `java.io.Serializable`，通过 `ObjectOutputStream` 写入文件，通过 `ObjectInputStream` 读取文件。
- **应用标题**：Toolbar 和桌面启动器标题均为「王浩宇 + 2024110536」。

## 页面结构

`MainActivity` 使用 `FrameLayout` 作为 Fragment 容器，底部 `BottomNavigationView` 在三个 Fragment 之间切换：

| 底部导航 | Fragment | 说明 |
| --- | --- | --- |
| 个人信息 | `ProfileFragment` | 展示 / 编辑个人信息 |
| 两张牌 | `TwoCardGameFragment` | `cardsPerMatch = 2` 的翻牌游戏 |
| 三张牌 | `ThreeCardGameFragment` | `cardsPerMatch = 3` 的翻牌游戏 |

两个游戏 Fragment 继承自 `GameFragment`，复用同一套 RecyclerView、计分、历史记录和序列化逻辑。

## 序列化与文件存储

| 文件 | 保存的对象 | 说明 |
| --- | --- | --- |
| `profile.dat` | `Profile` | 姓名、性别、学号、电话、邮件、爱好、保存时间 |
| `game2.dat` | `CardMatchingGame` | 两张牌游戏的牌面、得分、最高分、历史记录 |
| `game3.dat` | `CardMatchingGame` | 三张牌游戏的牌面、得分、最高分、历史记录 |

`SerializableStore` 中的关键代码：

```kotlin
// 写文件
ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)).use {
    it.writeObject(value)
}

// 读文件
ObjectInputStream(context.openFileInput(fileName)).use {
    it.readObject() as? T
}
```

`Profile`、`Card`、`CardMatchingGame`、`GameRecord` 均实现 `Serializable`。

## 项目结构

```
app/src/main/java/cn/edu/sicnu/cs/why/ex6/
├── MainActivity.kt              # 底部导航 + Fragment 容器 + 关于菜单
├── ProfileFragment.kt           # 个人信息 Fragment
├── GameFragment.kt              # 翻牌游戏公共基类
├── TwoCardGameFragment.kt       # 两张牌游戏
├── ThreeCardGameFragment.kt     # 三张牌游戏
├── CardAdapter.kt               # RecyclerView 适配器
├── CrossBoxView.kt              # 头像占位框 / 卡背
├── data/
│   ├── SerializableStore.kt     # openFileInput / openFileOutput + 对象流
│   ├── Profile.kt               # 可序列化的个人信息
│   ├── ProfileStore.kt          # 个人信息读写
│   ├── GameStore.kt             # 两种游戏读写
│   └── AvatarStore.kt           # 头像读写
└── game/
    ├── Card.kt                  # 扑克牌与配对规则
    ├── Deck.kt                  # 52 张牌堆
    ├── CardMatchingGame.kt      # 可序列化的游戏模型
    └── GameRecord.kt            # 可序列化的历史记录
```

## 构建

```bash
./gradlew assembleDebug
```

生成的 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。
