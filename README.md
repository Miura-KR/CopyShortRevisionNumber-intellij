# CopyShortRevisionNumber

An IntelliJ Platform plugin that adds a **Copy Short Revision Number** action to the Git Log context menu. It copies the abbreviated hash of the selected commit to the clipboard, with configurable abbreviation style.

---

## English

### Features

- Adds **Copy Short Revision Number** to the Git Log view context menu (next to the built-in *Copy Revision Number*).
- Two abbreviation modes:
  - **Unique shortest** — uses `git rev-parse --short` to produce the shortest unambiguous hash (default).
  - **Fixed length** — truncates the hash to a user-specified length (1–40 characters).
- Runs Git in the background; handles multiple-commit selections by joining results with newlines.

### Requirements

- IntelliJ Platform-based IDE with the bundled **Git** plugin (`Git4Idea`) enabled.

### Usage

1. Open the **Git** tool window and switch to the **Log** tab.
2. Right-click a commit and choose **Copy Short Revision Number**.
3. The abbreviated hash is placed on the clipboard.

### Configuration

Open **Settings → Tools → Copy Short Revision Number** and choose:

- **Hash mode**: *Unique shortest* or *Fixed length*.
- **Fixed length**: the number of characters to keep when *Fixed length* mode is selected.

---

## 日本語

### 機能

- Git ログビューのコンテキストメニューに **Copy Short Revision Number** を追加します（標準の *Copy Revision Number* の隣に配置）。
- 2 種類の省略モード:
  - **Unique shortest** — `git rev-parse --short` を使用して一意に識別できる最短のハッシュを生成（デフォルト）。
  - **Fixed length** — ユーザー指定の長さ（1〜40 文字）にハッシュを切り詰めます。
- Git 実行はバックグラウンドで行われ、複数コミットを選択した場合は改行区切りで連結してコピーします。

### 動作要件

- バンドル済み **Git** プラグイン（`Git4Idea`）が有効な IntelliJ プラットフォーム系 IDE。

### 使い方

1. **Git** ツールウィンドウを開き、**Log** タブに切り替えます。
2. コミットを右クリックし、**Copy Short Revision Number** を選択します。
3. 省略されたハッシュがクリップボードにコピーされます。

### 設定

**Settings → Tools → Copy Short Revision Number** を開き、以下を選択します:

- **Hash mode**: *Unique shortest* または *Fixed length*。
- **Fixed length**: *Fixed length* モード選択時に残す文字数。
