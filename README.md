# modelProxy

一个用于对接大模型（如阿里云通义千问 DashScope/Qwen）的模型代理服务，提供统一的 HTTP 接口能力（例如内容审核）。

## 环境要求

- Java 17+
- Maven 3.8+

## 配置（通义千问 / DashScope）

本项目通过环境变量读取 DashScope API Key。

- macOS / Linux（zsh/bash）：

```bash
export DASHSCOPE_API_KEY="你的真实Key"
```

- Windows PowerShell：

```powershell
setx DASHSCOPE_API_KEY "你的真实Key"
```

安全建议：不要把 Key 写入代码或提交到 Git；使用环境变量最安全。

## 本地运行

```bash
mvn spring-boot:run
```

## 接口说明

- **POST** `/api/v1/model-proxy/execute`
  - 用途：执行内容审核等能力（具体字段以项目内 `AuditRequest`/`AuditResponse` 为准）

## 测试

### 单元测试

```bash
mvn test
```

### 真实调用通义千问（连通性集成测试）

该测试会真实请求 DashScope。

```bash
DASHSCOPE_API_KEY="你的真实Key" mvn -Dtest=QwenConnectivityIT test
```

测试类：`org.audit.modelproxy.llm.QwenConnectivityIT`

### 真实调用通义千问（内容审核接口集成测试）

该测试会启动 Spring Boot 容器，并通过 HTTP 调用接口，最终由服务端真实请求 DashScope。

```bash
DASHSCOPE_API_KEY="你的真实Key" mvn -Dtest=ModelAuditControllerIT test
```

测试类：`org.audit.modelproxy.controller.ModelAuditControllerIT`

## 常见问题

### 1) 测试显示 Skipped/Assumption failed

通常表示运行测试的 JVM 没拿到 `DASHSCOPE_API_KEY`。

- 命令行运行时请使用：

```bash
DASHSCOPE_API_KEY="你的真实Key" mvn -Dtest=ModelAuditControllerIT test
```

- IntelliJ IDEA 运行时：在对应 Run/Debug Configuration 中增加环境变量 `DASHSCOPE_API_KEY=你的真实Key`。
