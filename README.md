# ZenNextGen

ZenNextGen 是基于 [OpenZen](https://github.com/Margele/OpenZen) 代码修改后的 Minecraft Forge 1.20.1 客户端 Mod。项目现在使用标准 Forge Mod 加载方式，并通过 Sponge Mixin 注入运行时逻辑。

构建出的 jar 像普通 Forge Mod 一样放入 `mods` 文件夹即可加载，不再需要 Java Agent、DLL、EXE 注入器、native loader 或 `-javaagent` 启动参数。

## 目标环境

- Minecraft：`1.20.1`
- Forge：`47.4.20`
- Java：`17`
- 加载方式：Forge Mod Loader
- 注入方式：Sponge Mixin
- Mixin 配置：`src/main/resources/hey.mixins.json`

## 功能概览

- 普通 Forge + Mixin 加载链路
- 模块系统、按键绑定和设置保存
- Click GUI 与 HUD 渲染
- Tick、Render、Packet、Key、Motion、Rotation 等事件钩子
- WebUI 资源与本地配置逻辑
- Scaffold、MoveFix 等移动模块

## 构建

在项目根目录运行 Gradle Wrapper：

```powershell
.\gradlew.bat compileJava
.\gradlew.bat jar
```

构建产物位置：

```text
build/libs/hey-1.0.jar
```

## 开发环境运行

```powershell
.\gradlew.bat runClient
```

开发环境和正式 jar 使用同一套 Forge + Mixin 加载方式。

## 安装

1. 运行 `.\gradlew.bat jar` 构建 jar。
2. 将 `build/libs/hey-1.0.jar` 放入 Minecraft Forge `1.20.1-47.4.20` 环境的 `mods` 文件夹。
3. 正常启动 Forge 配置文件。

无需添加额外 JVM 参数。

## 项目结构

```text
src/main/java/com/mihoyo/zen
  command/        命令系统
  config/         配置保存与加载
  event/          事件总线与事件类型
  gui/            Click GUI
  hud/            HUD 元素
  manager/        模块、配置、HUD、命令管理器
  mixin/          Sponge Mixin 注入点
  modules/        客户端模块
  network/        WebUI 请求处理
  render/         字体与渲染工具
  settings/       模块设置项
  utils/          游戏、数学、杂项、渲染、旋转工具

src/main/resources
  hey.mixins.json
  META-INF/mods.toml
  assets/zen/
  webui/
```

## 说明

- 原项目的 Patchify、自定义 Java Agent、DLL/native 和外部注入链路已经移除。
- Mixin refmap 会在 Gradle 构建时生成并打入 jar。
- 当前 Mod ID 仍为 `hey`，与现有 Forge 元数据和 Mixin 配置保持一致。

