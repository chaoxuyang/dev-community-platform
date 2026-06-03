# 开发者技术社区平台

一个面向开发者内容创作与交流场景的技术社区系统，支持文章发布、评论互动、点赞收藏、消息通知、全文搜索、用户认证、活跃度排行和后台管理等功能。

项目基于 Spring Boot 多模块架构实现，重点体现高并发读写场景下的缓存设计、搜索索引同步、异步消息处理和内容安全治理，适合作为 Java 后端项目经历展示。

## 项目亮点

- 使用 JWT + Redis 实现用户登录态校验，兼顾无状态 Token 与服务端会话可控性。
- 使用 Caffeine + Redis 构建多级缓存，优化首页侧边栏、热门文章、活跃排行等高频查询。
- 使用 Elasticsearch 实现文章全文检索，并通过 Canal 监听 MySQL binlog 同步文章索引。
- 使用 Redis ZSet 维护用户活跃度排行榜。
- 使用 RabbitMQ 异步处理评论、点赞、收藏产生的通知、积分和活跃度更新。
- 采用“先更新 MySQL，再删除 Redis”的策略降低缓存脏读风险。
- 结合 MyBatis 拦截器和 DFA 敏感词过滤，对文章、评论等用户输入做内容安全处理。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 2.7.1、Java 8 |
| 数据访问 | MyBatis-Plus、MySQL、Liquibase |
| 缓存 | Redis、Caffeine |
| 搜索 | Elasticsearch、Canal |
| 消息队列 | RabbitMQ |
| 前端页面 | Thymeleaf、JavaScript、CSS |
| 接口文档 | Knife4j |
| 工具组件 | Lombok、MapStruct、Guava、Hutool |

## 模块结构

```text
dev-community-platform
├── dev-community-api       # 实体、DTO、VO、枚举、事件模型
├── dev-community-core      # 通用工具、缓存、搜索、敏感词、基础组件
├── dev-community-service   # 业务逻辑、数据访问、RabbitMQ、Canal 同步
├── dev-community-ui        # Thymeleaf 模板、JS、CSS、静态资源
└── dev-community-web       # Web 接口、拦截器、全局异常、应用启动入口
```

模块依赖关系：

```text
dev-community-web
├── dev-community-ui
└── dev-community-service
    ├── dev-community-core
    └── dev-community-api
```

启动类：

```text
dev-community-web/src/main/java/com/devcommunity/platform/web/DevCommunityApplication.java
```

## 核心功能

### 用户认证

系统通过 JWT 标识用户身份，并结合 Redis 保存服务端登录状态。请求进入业务接口前由拦截器解析用户上下文，避免每个 Controller 重复处理认证逻辑。

相关位置：

```text
dev-community-web/src/main/java/com/devcommunity/platform/web/front/login
dev-community-web/src/main/java/com/devcommunity/platform/web/hook/interceptor
dev-community-core/src/main/java/com/devcommunity/platform/core/util/SessionUtil.java
```

### 多级缓存

系统将热点数据拆分为本地缓存和分布式缓存两层。Caffeine 用于缓存单机热点数据，Redis 用于跨实例共享缓存，适合首页聚合数据、侧边栏、热门文章、排行榜等读多写少场景。

相关位置：

```text
dev-community-core/src/main/java/com/devcommunity/platform/core/cache
dev-community-service/src/main/java/com/devcommunity/platform/service/sidebar
dev-community-service/src/main/java/com/devcommunity/platform/service/article
```

### 全文搜索与索引同步

文章搜索基于 Elasticsearch 实现。文章数据发生变化后，可通过 Canal 监听 MySQL binlog，将 INSERT、UPDATE、DELETE 事件同步到 ES 索引，保证搜索数据及时更新。

相关位置：

```text
dev-community-core/src/main/java/com/devcommunity/platform/core/search
dev-community-service/src/main/java/com/devcommunity/platform/service/sync/canal
```

Canal 默认关闭，需要时可在配置中开启：

```yaml
canal:
  sync:
    enabled: true
    host: 127.0.0.1
    port: 11111
    destination: example
    batchSize: 100
```

### 活跃度排行与异步消息

用户评论、点赞、收藏等行为会影响活跃度、积分和通知。系统使用 Redis ZSet 维护活跃度分值，并将通知、积分更新等衍生操作投递到 RabbitMQ 异步消费，减少主链路耗时。

相关位置：

```text
dev-community-service/src/main/java/com/devcommunity/platform/service/rank
dev-community-service/src/main/java/com/devcommunity/platform/service/notify
dev-community-service/src/main/java/com/devcommunity/platform/service/rabbitmq
```

### 内容安全

系统使用敏感词组件对文章、评论等用户输入进行过滤，并结合 MyBatis 拦截能力在数据写入链路前置校验，降低违规内容进入数据库和搜索索引的风险。

相关位置：

```text
dev-community-core/src/main/java/com/devcommunity/platform/core/senstive
dev-community-service/src/main/java/com/devcommunity/platform/service/article
dev-community-web/src/main/java/com/devcommunity/platform/web/hook
```

## 环境要求

- JDK 8
- Maven 3.6+
- MySQL 5.7+
- Redis
- RabbitMQ
- Elasticsearch
- Canal Server

说明：当前项目按 Java 8 编译目标维护。如果本地使用 Java 21，建议不要执行 `mvn clean`，避免 Lombok 或注解处理器出现兼容性问题。

## 本地启动

1. 创建数据库：

```sql
CREATE DATABASE dev_community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改本地数据库配置：

```text
dev-community-web/src/main/resources-env/dev/application-dal.yml
```

3. 编译项目：

```bash
mvn -pl dev-community-web -am -DskipTests package
```

4. 启动项目：

```bash
mvn -pl dev-community-web -am spring-boot:run
```

5. 访问地址：

```text
首页：http://127.0.0.1:8080
接口文档：http://127.0.0.1:8080/doc.html
```

## 测试

运行 Canal 同步相关单元测试：

```bash
mvn -pl dev-community-web -am -Dtest=CanalArticleEntryHandlerTest -DfailIfNoTests=false test
```