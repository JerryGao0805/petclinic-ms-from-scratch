 # petclinic-ms-from-scratch (V1) RUNBOOK

  ## 0. 你现在复现到了什么程度
  你已经从零搭起来了一个最小可用的微服务系统（V1）：
  - `config-server`：配置中心（8888）
  - `discovery-server`：服务注册中心 Eureka（8761）
  - `customers-service`：业务服务（8081）
  - `vets-service`：业务服务（8082）
  - `visits-service`：业务服务（8083）
  - `api-gateway`：统一入口网关（8080），基于 Eureka 做服务发现和转发

  最终验证标准（你已跑通）：
  - `http://localhost:8080/customers/actuator/health` -> `{"status":"UP"}`
  - `http://localhost:8080/vets/actuator/health` -> `{"status":"UP"}`
  - `http://localhost:8080/visits/actuator/health` -> `{"status":"UP"}`

  ## 1. 前置条件（环境基线）
  - JDK：>= 17（你当前是 21）
  - Maven：3.9+
  - `curl`：用于验证接口
  - IDE：IntelliJ IDEA（可选，用于更方便启动多个服务）

  知识点：
  - 微服务本地联调 = 多个进程同时跑，所以你需要多个终端窗口或多个 IDEA 运行配置。

  ## 2. 端口清单（记住这个表）
  | 组件 | 模块 | 端口 | 健康检查 |
  |---|---|---:|---|
  | 配置中心 | `config-server` | 8888 | `curl -s http://localhost:8888/actuator/health` |
  | 注册中心 | `discovery-server` | 8761 | `curl -s http://localhost:8761/actuator/health` |
  | 业务服务 | `customers-service` | 8081 | `curl -s http://localhost:8081/actuator/health` |
  | 业务服务 | `vets-service` | 8082 | `curl -s http://localhost:8082/actuator/health` |
  | 业务服务 | `visits-service` | 8083 | `curl -s http://localhost:8083/actuator/health` |
  | 网关入口 | `api-gateway` | 8080 | `curl -s http://localhost:8080/actuator/health` |

  知识点：
  - 常见端口规划：网关固定 8080，业务服务从 8081 起递增，避免冲突。

  ## 3. 配置中心（Config Server）怎么组织配置
  Config Server 使用 `native + classpath:/config` 模式，配置文件放在：
  - `config-server/src/main/resources/config/`

  当前 V1 用到的文件：
  - `customers-service.yml`：下发 `server.port: 8081`
  - `vets-service.yml`：下发 `server.port: 8082`
  - `visits-service.yml`：下发 `server.port: 8083`
  - `api-gateway.yml`：下发 `server.port: 8080` 和网关路由规则

  验证 Config Server 能返回配置：
  - `curl -s http://localhost:8888/customers-service/default`
  - `curl -s http://localhost:8888/vets-service/default`
  - `curl -s http://localhost:8888/visits-service/default`
  - `curl -s http://localhost:8888/api-gateway/default`

  重要说明（非常关键）：
  - 因为是 `classpath:/config`，改了这些配置文件后：
    1. 需要重启 `config-server` 才会加载新配置
    2. 端口这类“启动期配置”还需要重启对应服务才会生效

  知识点：
  - Spring Boot 3.x 使用 `spring.config.import` 机制显式启用 Config Server，否则会启动失败。

  ## 4. 启动顺序（必须按这个来）
  启动顺序（从基础设施到业务，再到网关）：
  1. `config-server`
  2. `discovery-server`
  3. `customers-service`、`vets-service`、`visits-service`
  4. `api-gateway`

  为什么必须这样：
  - 业务服务启动时要拉配置（需要 config-server）
  - 业务服务启动后要注册（需要 discovery-server）
  - 网关转发需要从 Eureka 拿实例列表（需要业务服务已注册）

  ## 5. 终端启动方式（推荐，最清晰）
  在项目根目录 `petclinic-ms-from-scratch` 下，分别开 5 个终端窗口启动：

  ### 5.1 启动 config-server
  ```bash
  cd config-server
  mvn spring-boot:run

  验证：

  curl -s http://localhost:8888/actuator/health

  ### 5.2 启动 discovery-server

  cd discovery-server
  mvn spring-boot:run

  验证：

  curl -s http://localhost:8761/actuator/health

  打开控制台：

  - http://localhost:8761

  ### 5.3 启动三个业务服务

  cd customers-service && mvn spring-boot:run
  cd vets-service && mvn spring-boot:run
  cd visits-service && mvn spring-boot:run

  验证（直连）：

  curl -s http://localhost:8081/actuator/health
  curl -s http://localhost:8082/actuator/health
  curl -s http://localhost:8083/actuator/health

  验证（注册中心）：

  - 打开 http://localhost:8761，应能看到 CUSTOMERS-SERVICE、VETS-SERVICE、VISITS-SERVICE（可能还会看到网关）

  ### 5.4 启动 api-gateway

  cd api-gateway
  mvn spring-boot:run

  验证网关自身：

  curl -s http://localhost:8080/actuator/health

  ## 6. 端到端验证（通过网关访问后端）

  curl -s http://localhost:8080/customers/actuator/health
  curl -s http://localhost:8080/vets/actuator/health
  curl -s http://localhost:8080/visits/actuator/health

  知识点：

  - 网关路由使用 lb://SERVICE-ID，表示通过 Eureka 做服务发现 + 负载均衡转发。
  - StripPrefix=1 的效果：/customers/actuator/health 会转发成后端的 /actuator/health。

  ## 7. IntelliJ IDEA 启动方式（可选）

  方式 A（直接跑 main）：

  - 打开项目根目录（含父 pom.xml）
  - 等 Maven 导入完成
  - 分别运行每个模块的启动类（Run）：
      - ConfigServerApplication
      - DiscoveryServerApplication
      - CustomersServiceApplication
      - VetsServiceApplication
      - VisitsServiceApplication
      - ApiGatewayApplication

  方式 B（用 Maven Run 配置）：

  - Run/Debug Configurations -> Add New -> Maven
  - Working directory 指向对应模块目录
  - Command line: spring-boot:run

  ## 8. 停止所有服务

  - 在每个运行窗口按 Ctrl + C

  ## 9. 常见问题排查（最常用的 4 个）

  1. 端口被占用（Address already in use）

  - 用端口表检查你是不是重复启动了某个服务
  - 或者换端口（优先通过 Config Server 下发）

  2. 启动报 No spring.config.import property has been defined

  - 说明服务没声明 Config Server 导入
  - 检查服务的 src/main/resources/application.yml 是否包含：
      - spring.config.import: "optional:configserver:"（或 configserver:）

  3. 网关转发 503/404

  - 先看 Eureka Dashboard 是否有对应服务实例 UP
  - 再看网关路由是否匹配（Path 前缀是否写对）

  4. 改了 config 文件但不生效

  - 你是 classpath 模式：先重启 config-server
  - 再重启目标服务（尤其是端口这种启动期配置）

