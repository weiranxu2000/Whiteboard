# **多人共享画板**

## **系统架构**

该共享白板的代码由三个部分组成，分别是` server`、`client` 和 `shape` 三个包。服务端和客户端之间的通信通过 Java RMI 完成。

- `server` 包含服务器的接口与实现。
- `client` 包含客户端的接口与实现，同时还实现了白板的用户界面（UI）。
- `shape` 包含在白板中出现的各种图形（如直线、椭圆等）。其中定义了多个公共方法，可供服务器和客户端调用。

用户通过客户端连接至服务器，由服务器负责管理用户连接并同步白板状态。

## **通信协议**

服务端与客户端之间的通信是通过 Java RMI（远程方法调用）系统建立的。

- 服务端注册自身到 RMI 注册表中，并开放服务端口供客户端查找。
- 客户端则从注册表中获取远程服务信息，并调用远程对象的方法进行通信。

## 如何运行

打开/jar文件夹，并且运行如下指令

```shell
java -jar WhiteboardServer.jar 1099

java -jar CreateWhiteboard.jar localhost 1099 admin

java -jar JoinWhiteboard.jar localhost 1099 user1
```

## **实现细节**

### **`server` **

该包包含三个类：

1. **IWhiteboard**：接口，定义了远程方法，客户端可通过此接口与服务器交互。

2. **WhiteboardImpl**：实现类，实现了 IWhiteboard 接口中定义的方法。主要功能包括：

   - 客户端注册与注销

   - 绘图操作

   - 发送消息

   - 踢出用户

   - 清空白板

     在此类中，服务器所需的数据结构如图形列表、客户端列表、用户列表也被创建。用户列表用于客户端显示当前在线用户。为实现同步，绘图与用户刷新等方法使用 synchronized 关键字修饰。

3. **WhiteboardServer**：服务器的主函数类，接收一个参数（端口号），使用 RMI 创建注册表并开放服务。

### **`client` **

该包包含五个类：

1. **IWhiteboardClient**：接口，定义远程方法，供服务器调用以与客户端交互。
2. **WhiteboardClient**：客户端主类，继承 JFrame 并实现 IWhiteboardClient 接口。
   - 创建 WhiteboardClient 对象时，初始化 GUI 组件并设置布局
   - 通过 RMI 调用服务端方法
   - 加载已有图形与用户信息
   - 提供注册、注销、消息发送、绘图等方法
3. **WhiteboardPanel**：继承 JPanel，用于实现白板界面，使用 Java Swing 与 AWT 实现。
   - 实现了自由绘画、橡皮擦、矩形、椭圆、圆形、直线、文本等鼠标绘图事件
   - 提供保存、另存为、打开等操作方法
4. **CreateWhiteboard**：供管理员使用的客户端主函数，接收三个参数：服务器 IP 地址、端口号、用户名。
   - 首先检查参数数量
   - 获取服务器注册表
   - 查找 Whiteboard 服务并创建 WhiteboardClient 对象
   - 设置 isManager = true
5. **JoinWhiteboard**：供普通用户使用的客户端主函数，参数同上。
   - 检查用户名是否唯一
   - 创建 WhiteboardClient 对象并设置 isManager = false

### **`shape` **

该包包含八个类：

- Shape：抽象类，定义所有图形的共有属性和方法。属性包括起点、终点、颜色和笔刷大小，定义了 draw 方法用于绘制图形。
- Circle、Eraser、FreeDraw、Line、Oval、Rectangle、Text：这七个类继承自 Shape 类，实现了各自的 draw 方法。每个类负责绘制特定的图形或执行特定操作。