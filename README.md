# CopyShortRevisionNumber

An IntelliJ Platform plugin that adds short-revision and GitHub link copy actions to context menus. The hash used in every action follows the same configurable abbreviation rule.

---

## English

### Features

- **Copy Short Revision Number** — added to the Git Log view context menu (next to the built-in *Copy Revision Number*). Copies the abbreviated hash of the selected commit(s).
- **Copy URL(Short Revision Number)** — added to the Git Log view context menu. Copies the GitHub commit URL using the short revision hash (`https://github.com/<owner>/<repo>/commit/<short>`).
- **GitHub Repository URL(Short Revision)** — added to the *Copy Path/Reference…* submenu (project view, tabs, etc.) and the editor's *Copy / Paste Special* submenu. Copies the GitHub URL of the selected file or directory at the current commit (`https://github.com/<owner>/<repo>/blob/<short>/<path>`). When invoked from the editor, the URL includes `#L<n>` (caret line) or `#L<n>-L<m>` (selection).
- Two abbreviation modes shared by all actions:
  - **Unique shortest** — uses `git rev-parse --short` to produce the shortest unambiguous hash (default).
  - **Fixed length** — truncates the hash to a user-specified length (1–40 characters).
- Runs Git in the background; multi-commit / multi-file selections are joined with newlines.
- GitHub link actions are hidden automatically when the repository has no `github.com` remote.

### Requirements

- IntelliJ Platform-based IDE with the bundled **Git** plugin (`Git4Idea`) enabled.
- For GitHub link actions: a remote pointing at `github.com` (GitHub Enterprise Server / Cloud with data residency are not supported).

### Usage

**Copy commit hash / URL**

1. Open the **Git** tool window and switch to the **Log** tab.
2. Right-click a commit and choose **Copy Short Revision Number** or **Copy URL(Short Revision Number)**.

**Copy file URL**

1. Right-click a file/directory in the project view (or right-click in the editor).
2. Open **Copy Path/Reference…** (project view) or **Copy / Paste Special** (editor).
3. Choose **GitHub Repository URL(Short Revision)**.

### Configuration

Open **Settings → Tools → Copy Short Revision Number** and choose:

- **Hash mode**: *Unique shortest* or *Fixed length*.
- **Fixed length**: the number of characters to keep when *Fixed length* mode is selected.

---

## 日本語

### 機能

- **Copy Short Revision Number** — Git ログビューのコンテキストメニュー（標準の *Copy Revision Number* の隣）。選択したコミットの短縮ハッシュをコピー。
- **Copy URL(Short Revision Number)** — Git ログビューのコンテキストメニュー。短縮ハッシュを使った GitHub コミット URL をコピー（`https://github.com/<owner>/<repo>/commit/<short>`）。
- **GitHub Repository URL(Short Revision)** — *パス/参照のコピー…* サブメニュー（プロジェクトビュー、タブなど）と、エディタの *Copy / Paste Special* サブメニュー。選択したファイル/ディレクトリの現在コミットでの GitHub URL をコピー（`https://github.com/<owner>/<repo>/blob/<short>/<path>`）。エディタから呼んだ場合は `#L<n>`（キャレット行）または `#L<n>-L<m>`（選択範囲）を付与。
- 全アクション共通の短縮モード:
  - **Unique shortest** — `git rev-parse --short` を使用して一意に識別できる最短のハッシュを生成（デフォルト）。
  - **Fixed length** — ユーザー指定の長さ（1〜40 文字）にハッシュを切り詰めます。
- Git 実行はバックグラウンドで行われ、複数コミット/複数ファイル選択時は改行区切りで連結してコピーします。
- GitHub リンク系アクションは `github.com` の remote が無いリポジトリでは自動的に非表示になります。

### 動作要件

- バンドル済み **Git** プラグイン（`Git4Idea`）が有効な IntelliJ プラットフォーム系 IDE。
- GitHub リンク系アクションを使う場合: `github.com` を指す remote が必要（GitHub Enterprise Server / データ所在地オプション付き GitHub Enterprise Cloud は非対応）。

### 使い方

**コミットのハッシュ / URL をコピー**

1. **Git** ツールウィンドウを開き、**Log** タブに切り替えます。
2. コミットを右クリックし、**Copy Short Revision Number** または **Copy URL(Short Revision Number)** を選択。

**ファイルの URL をコピー**

1. プロジェクトビューでファイル/ディレクトリを右クリック（またはエディタ上で右クリック）。
2. **パス/参照のコピー…**（プロジェクトビュー）または **Copy / Paste Special**（エディタ）を開きます。
3. **GitHub Repository URL(Short Revision)** を選択。

### 設定

**Settings → Tools → Copy Short Revision Number** を開き、以下を選択します:

- **Hash mode**: *Unique shortest* または *Fixed length*。
- **Fixed length**: *Fixed length* モード選択時に残す文字数。
