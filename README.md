# fcitx5-android

[Fcitx5](https://github.com/fcitx/fcitx5) input method framework and engines ported to Android.

## 智能回车（本 fork）

这是带有智能回车（Smart Enter）的 fcitx5-android（小企鹅输入法），是上游 [fcitx5-android](https://github.com/fcitx5-android/fcitx5-android) 的个人 fork。在聊天里，回车键会变成发送，接近 iOS 键盘，例如 X、Instagram、LINE、KakaoTalk。可以只对列表中的应用开启，也可以对所有应用开启；默认列表就是这四个。某个应用能不能发出去，要看它自己的界面，并不保证每一个都能用。

已发布的安装包仅适用于 **arm64-v8a** 手机（几乎所有现代 Android 手机）。

### 安装

请先卸载官方版 Fcitx5 for Android，再安装本仓库 [Releases](https://github.com/ion76-design/fcitx5-android-smart-enter/releases) 里的包。签名不同，不能覆盖安装。

如果已经装了本 fork 的 v2，可以直接覆盖安装 v2.1，用的是同一把签名密钥。

要用 Rime，请安装本 fork 发布的插件，它必须和主程序同一把密钥。[v2 发布页](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2) 上的 Rime 插件可以继续和 v2.1 一起用。官方构建的插件不能和这个版本一起用。

### v2.1

最新版是 [smart-enter-v2.1](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2.1)。它修了官方 X Android 应用里回复发不出去的问题：按下发送时，应当点击回复框上的「回复」或「发布」按钮，而不是帖子的「更多」菜单。versionName `smart-enter-v2.1`，arm64 的 versionCode 是 132，由提交 `ecbaa3c61cf789c00ef60e284d5d4bbd6eef7d1a` 构建。

这项修正已经打进已发布的 v2.1 安装包。对应源码在尚未合并的 [PR #2](https://github.com/ion76-design/fcitx5-android-smart-enter/pull/2)，当前 master 还没有这段改动。上一版 [smart-enter-v2](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2) 仍然可以下载（versionCode 122，合并提交 `45d79a5`，基于上游 `eb5b502`）。

### 启用

设置 → 虚拟键盘 → 智能回车。

如果某个应用的输入框不响应发送动作，请在 Android 设置 → 无障碍 中开启「Fcitx5 智能回车」。Android 13 及以上可能需要先在应用信息 → ⋮ → 允许受限设置。长按回车键输入换行。

诊断模式默认关闭。打开后，找不到发送按钮时会把按钮标签写到 `Download/fcitx5-smart-enter-dump-*.txt`，不记录你输入的内容。

### 隐私

应用没有 INTERNET 权限。无障碍辅助只在你按下回车时工作（找到并点击发送按钮），不保存任何数据。诊断记录只含按钮标签，且默认关闭。

### 下载与校验

- [fcitx5-android-smart-enter-v2.1-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2.1/fcitx5-android-smart-enter-v2.1-arm64-v8a.apk)（54550491 字节）  
  SHA-256 `9457de41826074f9df83f1a56b8478d2e86a6ac81567b56986eee8875a30c0c3`
- [fcitx5-android-smart-enter-v2-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2/fcitx5-android-smart-enter-v2-arm64-v8a.apk)  
  SHA-256 `e677a7634f21cae9bb09d521af3ecfb61b2c0e4dfd753660c821be2645a68d3b`
- Rime 插件 [fcitx5-android-plugin-rime-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2/fcitx5-android-plugin-rime-arm64-v8a.apk)（附在 v2 发布页，与 v2.1 同一签名密钥）  
  SHA-256 `e95804a6be18945c7e078fc19da4f2f3c1c5c2558e021a652e89da6b617b633a`

本 fork 的签名证书 SHA-256：`25cf25e50967cdc452a01230c000209991fc3c291e578eaf04fe2c59cbcd3ccd`。

非官方构建，许可证 LGPL-2.1，基于上游作者的 [fcitx5-android](https://github.com/fcitx5-android/fcitx5-android)。官方 GitHub Release、F-Droid、Google Play 和 Jenkins 上的包没有智能回车，签名也不同，不能当作本 fork 的安装包。

## Smart Enter (this fork)

fcitx5-android (小企鹅输入法) with Smart Enter, a personal fork of [fcitx5-android](https://github.com/fcitx5-android/fcitx5-android). In chat, the return key turns into Send, similar to the iOS keyboard, for example in X, Instagram, LINE, and KakaoTalk. It can apply to a list of apps or to all apps; those four are the default list. Whether a given app actually sends depends on that app. This fork does not guarantee every app.

Published APKs are **arm64-v8a only** (almost all modern Android phones).

### Install

Uninstall the official Fcitx5 for Android first, then install a package from this repository's [Releases](https://github.com/ion76-design/fcitx5-android-smart-enter/releases). The signature is different, so this fork cannot be installed over the official app.

If you already have this fork's v2, v2.1 installs over it with the same signing key.

For Rime, install the plugin published by this fork; it has to share the main app's key. The plugin on the [v2 release](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2) still works with v2.1. Plugins from the official build will not.

### v2.1

The latest build is [smart-enter-v2.1](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2.1). It fixes sending a reply in the official X Android app: pressing send should click the composer Reply / Post button (回复 / 发布), not the post overflow menu (更多). versionName `smart-enter-v2.1`, arm64 versionCode 132, built from commit `ecbaa3c61cf789c00ef60e284d5d4bbd6eef7d1a`.

That fix is already in the published v2.1 APK. The source is the still-open [PR #2](https://github.com/ion76-design/fcitx5-android-smart-enter/pull/2) and is not on master yet. The previous [smart-enter-v2](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/tag/smart-enter-v2) build is still available (versionCode 122, merge commit `45d79a5`, based on upstream `eb5b502`).

### Enable

设置 → 虚拟键盘 → 智能回车.

For apps whose input box ignores the send action, enable 「Fcitx5 智能回车」 in Android Settings → Accessibility. On Android 13+ you may first need App info → ⋮ → Allow restricted settings. Long-press Enter for a newline.

Diagnostic mode is off by default. When it is on and no send button is found, button labels are written to `Download/fcitx5-smart-enter-dump-*.txt`. It does not record what you type.

### Privacy

The app has no INTERNET permission. The accessibility helper only acts when you press Enter (it finds and taps the send button) and stores nothing. The diagnostic dump records button labels only and is off by default.

### Download and checksums

- [fcitx5-android-smart-enter-v2.1-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2.1/fcitx5-android-smart-enter-v2.1-arm64-v8a.apk) (54550491 bytes)  
  SHA-256 `9457de41826074f9df83f1a56b8478d2e86a6ac81567b56986eee8875a30c0c3`
- [fcitx5-android-smart-enter-v2-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2/fcitx5-android-smart-enter-v2-arm64-v8a.apk)  
  SHA-256 `e677a7634f21cae9bb09d521af3ecfb61b2c0e4dfd753660c821be2645a68d3b`
- Rime plugin [fcitx5-android-plugin-rime-arm64-v8a.apk](https://github.com/ion76-design/fcitx5-android-smart-enter/releases/download/smart-enter-v2/fcitx5-android-plugin-rime-arm64-v8a.apk) (attached to the v2 release, same signing key as v2.1)  
  SHA-256 `e95804a6be18945c7e078fc19da4f2f3c1c5c2558e021a652e89da6b617b633a`

This fork's signing certificate SHA-256: `25cf25e50967cdc452a01230c000209991fc3c291e578eaf04fe2c59cbcd3ccd`.

Unofficial build, LGPL-2.1, based on [fcitx5-android](https://github.com/fcitx5-android/fcitx5-android) by the upstream authors. Official GitHub Release, F-Droid, Google Play, and Jenkins packages do not include Smart Enter and use a different signature, so they are not the download for this fork.

In case you want Fcitx5 on other platforms: [macOS](https://github.com/fcitx-contrib/fcitx5-macos), [iOS](https://github.com/fcitx-contrib/fcitx5-ios), [HarmonyOS](https://github.com/fcitx-contrib/fcitx5-harmony), [ChromeOS](https://github.com/fcitx-contrib/fcitx5-chrome), [Windows](https://github.com/fcitx-contrib/fcitx5-windows); or [try Fcitx5 in the browser](https://fcitx-contrib.github.io/online/index.html)

## Project status

### Supported Languages

- English (with spell check)
- Chinese
  - Pinyin, Shuangpin, Wubi, Cangjie and custom tables (built-in, powered by [fcitx5-chinese-addons](https://github.com/fcitx/fcitx5-chinese-addons))
  - Zhuyin/Bopomofo (via [Chewing Plugin](./plugin/chewing))
  - Jyutping (via [Jyutping Plugin](./plugin/jyutping/), powered by [libime-jyutping](https://github.com/fcitx/libime-jyutping))
- Vietnamese (via [UniKey Plugin](./plugin/unikey), supports Telex, VNI and VIQR)
- Japanese (via [Anthy Plugin](./plugin/anthy))
- Korean (via [Hangul Plugin](./plugin/hangul))
- Sinhala (via [Sayura Plugin](./plugin/sayura))
- Thai (via [Thai Plugin](./plugin/thai))
- Generic (via [RIME Plugin](./plugin/rime), supports importing custom schemas)

### Implemented Features

- Virtual Keyboard (layout not customizable yet)
- Expandable candidate view
- Clipboard management (plain text only)
- Theming (custom color scheme, background image and dynamic color aka monet color after Android 12)
- Popup preview on key press
- Long press popup keyboard for convenient symbol input
- Symbol and Emoji picker
- Plugin System for loading addons from other installed apk
- Floating candidates panel when using physical keyboard

### Planned Features

- Customizable keyboard layout
- More input methods (via plugin)

## Screenshots

|拼音, Material Light theme, key border enabled|自然码双拼, Pixel Dark theme, key border disabled|
|:-:|:-:|
|<img src="https://github.com/fcitx5-android/fcitx5-android/assets/13914967/bd429247-62d9-4c78-bab8-70ef3ce47588" width="360px">|<img src="https://github.com/fcitx5-android/fcitx5-android/assets/13914967/3ae969c1-7ed0-4f92-a5df-19dc8c90a8c3" width="360px">|

|Emoji picker, Pixel Light theme, key border enabled|Symbol picker, Material Dark theme, key border disabled|
|:-:|:-:|
|<img src="https://user-images.githubusercontent.com/13914967/202181845-6a5f6bb2-a877-468c-851a-fd7e66e64ed4.png" width="360px">|<img src="https://user-images.githubusercontent.com/13914967/202181861-dd253439-1d5e-4f5f-9535-934f28796a6b.png" width="360px">|

## Get involved

Trello kanban: https://trello.com/b/gftk6ZdV/kanban

Matrix Room: https://matrix.to/#/#fcitx5-android:mozilla.org

Discuss on Telegram: [@fcitx5_android_group](https://t.me/fcitx5_android_group) ([@fcitx5_android](https://t.me/fcitx5_android) originally)

## Build

### Dependencies

- Android SDK Platform & Build-Tools 35.
- Android NDK (Side by side) 25 & CMake 3.22.1, they can be installed using SDK Manager in Android Studio or `sdkmanager` command line.
- [KDE/extra-cmake-modules](https://github.com/KDE/extra-cmake-modules)
- GNU Gettext >= 0.20 (for `msgfmt` binary; or install `appstream` if you really have to use gettext <= 0.19.)

### How to set up development environment

<details>
<summary>Prerequisites for Windows</summary>

- Enable [Developer Mode](https://learn.microsoft.com/en-us/windows/apps/get-started/enable-your-device-for-development) so that symlinks can be created without administrator privilege.

- Enable symlink support for `git`:

    ```shell
    git config --global core.symlinks true
    ```

</details>

First, clone this repository and fetch all submodules:

```shell
git clone git@github.com:fcitx5-android/fcitx5-android.git
git submodule update --init --recursive
```

Install `extra-cmake-modules` and `gettext` with your system package manager:

```shell
# For Arch Linux (Arch has gettext in it's base meta package)
sudo pacman -S extra-cmake-modules

# For Debian/Ubuntu
sudo apt install extra-cmake-modules gettext

# For macOS
brew install extra-cmake-modules gettext

# For Windows, install MSYS2 and execute in its shell (UCRT64)
pacman -S mingw-w64-ucrt-x86_64-extra-cmake-modules mingw-w64-ucrt-x86_64-gettext
# then add C:\msys64\ucrt64\bin to PATH
```

Install Android SDK Platform, Android SDK Build-Tools, Android NDK and cmake via SDK Manager in Android Studio:

<details>
<summary>Detailed steps (screenshots)</summary>

**Note:** These screenshots are for references and the versions in them may be out of date.
The current recommended versions are recorded in [Versions.kt](build-logic/convention/src/main/kotlin/Versions.kt) file.

![Open SDK Manager](https://user-images.githubusercontent.com/13914967/202184493-3ee1546b-0a83-4cc9-9e41-d20b0904a0cf.png)

![Install SDK Platform](https://user-images.githubusercontent.com/13914967/202184534-340a9e7c-7c42-49bd-9cf5-1ec9dcafcf32.png)

![Install SDK Build-Tools](https://user-images.githubusercontent.com/13914967/202185945-0c7a9f39-1fcc-4018-9c81-b3d2bf1c2d3f.png)

![Install NDK](https://user-images.githubusercontent.com/13914967/202185601-0cf877ea-e148-4b88-bd2f-70533189b3d4.png)

![Install CMake](https://user-images.githubusercontent.com/13914967/202184655-3c1ab47c-432f-4bd7-a508-92096482de50.png)

</details>

### Trouble-shooting

- Android Studio indexing takes forever to complete and cosumes a lot of memory.

    Switch to "Project" view in the "Project" tool window (namely the file tree side bar), right click `lib/fcitx5/src/main/cpp/prebuilt` directory, then select "Mark Directory as > Excluded". You may also need to restart the IDE to interrupt ongoing indexing process.

- Gradle error: "No variants found for ':app'. Check build files to ensure at least one variant exists." or "[CXX1210] <whatever>/CMakeLists.txt debug|arm64-v8a : No compatible library found"

    Examine if there are environment variables set such as `_JAVA_OPTIONS` or `JAVA_TOOL_OPTIONS`. You might want to clear them (maybe in the startup script `studio.sh` of Android Studio), as some gradle plugin treats anything in stderr as errors and aborts.

## Nix

Appropriate Android SDK with NDK is available in the development shell.  The `gradlew` should work out-of-the-box, so you can install the app to your phone with `./gradlew installDebug` after applying the patch mentioned above. For development, you may want to install the unstable version of Android Studio, and point the project SDK path to `$ANDROID_SDK_ROOT` defined in the shell. Notice that Android Studio may generate wrong `local.properties` which sets the SDK location to `~/Android/SDK` (installed by SDK Manager). In such case, you need specify `sdk.dir` as the project SDK in that file manually, in case Android Studio sticks to the wrong global SDK.
