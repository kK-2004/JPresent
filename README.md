# JPresent - 幻灯片制作与播放软件

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange)![Swing](https://img.shields.io/badge/GUI-Swing-blue)![Maven](https://img.shields.io/badge/Build-Maven-red)![License](https://img.shields.io/badge/License-MIT-green)

一个基于 Java Swing 的简易幻灯片制作与播放软件，模仿 PowerPoint 的基本编辑与播放流程。

[主界面预览](#界面展示) · [快速开始](#快速开始) · [功能介绍](#主要功能) · [项目结构](#项目结构)

---

## 项目简介

JPresent 是一个功能完整的幻灯片制作与播放软件，采用经典的 MVC 分层架构设计。

---

## 界面展示

### 导航页

<img src=".\docs\images\Navi.png" alt="Navi" style="zoom:80%;" />


### 编辑功能
#### 文本编辑
<img src=".\docs\images\text.png" alt="text" style="zoom: 40%;" />

### 幻灯片放映
<img src=".\docs\images\play.png" alt="play" style="zoom: 33%;" />

---

## 主要功能

### 文稿管理
- 新建演示文稿
- 打开已保存的 JSON 格式文稿
- 保存文稿（JSON 格式）
- 多页幻灯片管理与切换

### 编辑功能
- **文本编辑**：添加文本框，支持设置字体、大小、颜色等样式
- **图形绘制**：支持矩形、圆形、椭圆、直线等基本图形
- **图片插入**：支持插入本地图片，可拖动调整大小
- **对象操作**：移动、删除幻灯片中的对象
- **撤销/重做**：支持历史状态恢复

### 视图与导航
- 缩略图预览栏，实时显示各页内容
- 点击缩略图快速切换页面
- 自适应窗口大小的显示比例

### 幻灯片放映
- 从首张开始放映
- 从当前页开始放映
- 全屏播放模式
- 键盘快捷键支持（F5 开始放映）

### 导出功能
- 导出当前页为图片（PNG/JPG）
- 导出当前页为 PDF
- 导出整个文稿为多页 PDF

---

## 项目结构

```
JPresent/
├── pom.xml                          # Maven 项目配置
├── src/main/java/com/JPresent/
│   ├── Main.java                    # 程序入口
│   ├── ui/                          # 界面层
│   │   ├── MainWindow.java          # 主窗口
│   │   ├── NavigationWindow.java    # 导航窗口
│   │   ├── SlidePanel.java          # 幻灯片编辑面板
│   │   ├── ThumbnailPanel.java      # 缩略图面板
│   │   ├── SlideShowWindow.java     # 放映窗口
│   │   ├── AppMenuBar.java          # 菜单栏
│   │   ├── AppToolBar.java          # 工具栏
│   │   └── ModernTheme.java         # 主题样式
│   ├── model/                       # 模型层
│   │   ├── Presentation.java        # 演示文稿
│   │   ├── Slide.java               # 单张幻灯片
│   │   ├── SlideObject.java         # 对象基类
│   │   ├── TextBox.java             # 文本框
│   │   ├── ImageObject.java         # 图片对象
│   │   └── ShapeObject.java         # 形状基类及子类
│   ├── controller/                  # 控制层
│   │   ├── FileController.java      # 文件控制器
│   │   ├── DrawingController.java   # 绘图控制器
│   │   └── SelectionController.java # 选中控制器
│   └── service/                     # 服务层
│       ├── FileService.java         # 文件读写服务
│       ├── ExportService.java       # 导出服务
│       └── UndoRedoService.java     # 撤销重做服务
└── target/                          # 编译输出
    └── JPresent-1.0.jar             # 可执行 JAR 包
```

---

## 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6+（如需自行构建）
- **操作系统**: Windows / Linux / macOS

### 运行方式

#### 方式一：直接运行 JAR 包

```bash
java -jar JPresent-1.0.jar
```

#### 方式二：使用 Maven 运行

```bash
# 克隆项目
git clone https://github.com/your-username/JPresent.git
cd JPresent

# 编译并运行
mvn package
mvn exec:java -Dexec.mainClass="com.JPresent.Main"
```

#### 方式三：IDE 运行

在 IntelliJ IDEA 或 Eclipse 中导入 Maven 项目，运行 `com.JPresent.Main` 类。

---

## 使用说明

### 基本操作流程

1. **启动程序**：运行主程序，进入导航页面
2. **新建文稿**：点击「文件」→「新建」创建空白文稿
3. **添加内容**：
   - 点击工具栏选择绘图工具（矩形、圆形、文本等）
   - 在编辑区域绘制或插入对象
   - 双击文本对象可编辑内容和样式
4. **保存文稿**：点击「文件」→「保存」保存为 JSON 文件
5. **播放演示**：按 F5 或点击「幻灯片放映」开始播放

### 快捷键

| 快捷键 | 功能 |
|:---|:---|
| `F5` | 从第一张开始放映 |
| `Delete` | 删除选中对象 |
| `Ctrl+Z` | 撤销 |
| `Ctrl+Y` | 重做 |

### JSON 文件格式

文稿以 JSON 格式存储，包含幻灯片列表、对象属性、样式信息等。示例：

```json
{
  "slides": [
    {
      "objects": [
        {
          "type": "text",
          "x": 100,
          "y": 100,
          "text": "标题文本",
          "fontName": "微软雅黑",
          "fontSize": 48,
          "color": -16777216
        },
        {
          "type": "rectangle",
          "x": 200,
          "y": 200,
          "width": 300,
          "height": 200,
          "fillColor": -13108,
          "strokeColor": -39322
        }
      ],
      "backgroundColor": -1
    }
  ],
  "currentIndex": 0
}
```

---

## 技术架构

本项目采用经典的 **MVC 分层架构**：

- **Model（模型层）**：定义数据结构，包括文稿、幻灯片、图形对象等
- **View（视图层）**：负责界面展示，使用 Java Swing 组件
- **Controller（控制层）**：处理用户交互，协调模型与视图
- **Service（服务层）**：提供通用服务，如文件读写、导出、撤销重做等

### 核心技术

| 技术 | 用途 |
|:---|:---|
| Java Swing | GUI 界面开发 |
| Jackson | JSON 序列化/反序列化 |
| Apache PDFBox | PDF 导出功能 |
| Maven | 项目构建与依赖管理 |

