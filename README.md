  [![CI](https://github.com/JerryGao1991/petclinic-ms-from-scratch/actions/workflows/ci.yml/badge.svg)](https://github.com/JerryGao1991/petclinic-ms-from-scratch/actions/workflows/ci.yml)
  # petclinic-ms-from-scratch

  一个从零复现的 Spring PetClinic Microservices（精简版）学习项目：
  - Spring Cloud Config Server（配置中心）
  - Eureka Discovery Server（注册中心）
  - Spring Cloud Gateway（API 网关）
  - 3 个业务服务：customers / vets / visits
  - Docker Compose 一键启动
  - GitHub Actions CI：跑测试 + 打包 + Docker build

  ---

  ## 1. 环境要求

  - JDK 21（你本机示例：Temurin 21）
  - Docker Desktop（含 `docker compose`）
  - Git
  - Maven Wrapper（本项目已内置：`./mvnw`）

  检查命令：

  ```bash
  java --version
  docker --version
  docker compose version
  ./mvnw -v

  ———

  ## 2. 项目结构

  petclinic-ms-from-scratch/
    config-server/
    discovery-server/
    api-gateway/
    customers-service/
    vets-service/
    visits-service/
    docker-compose.yml
    pom.xml

  ———

  ## 3. 本地构建（不依赖 Docker）

  ### 3.1 跑全量测试

  ./mvnw -ntp -B test

  ### 3.2 打包所有模块（生成 jar）

  ./mvnw -ntp -B -DskipTests package

  ———

  ## 4. 构建 Docker 镜像

  前提：先 package 生成 target/*.jar（Dockerfile 会 COPY 这个 jar）。

  ./mvnw -ntp -B -DskipTests package

  docker build -t petclinic/config-server:dev ./config-server
  docker build -t petclinic/discovery-server:dev ./discovery-server
  docker build -t petclinic/customers-service:dev ./customers-service
  docker build -t petclinic/vets-service:dev ./vets-service
  docker build -t petclinic/visits-service:dev ./visits-service
  docker build -t petclinic/api-gateway:dev ./api-gateway

  ———

  ## 5. Docker Compose 一键启动

  ### 5.1 启动

  docker compose up -d
  docker compose ps

  ### 5.2 查看日志

  docker compose logs -f --tail=200

  ### 5.3 停止与清理

  docker compose down

  ———

  ## 6. 验证服务是否正常

  ### 6.1 基础健康检查

  curl -s http://localhost:8888/actuator/health
  curl -s http://localhost:8761/actuator/health
  curl -s http://localhost:8080/actuator/health
  curl -s http://localhost:8081/actuator/health
  curl -s http://localhost:8082/actuator/health
  curl -s http://localhost:8083/actuator/health

  预期都返回：

  {"status":"UP"}

  ### 6.2 Config Server 配置读取

  curl -s http://localhost:8888/application/default
  curl -s http://localhost:8888/customers-service/default
  curl -s http://localhost:8888/api-gateway/default

  ### 6.3 通过网关访问三个服务（路由 + Eureka + LB）

  curl -s http://localhost:8080/customers/api/hello
  curl -s http://localhost:8080/vets/api/hello
  curl -s http://localhost:8080/visits/api/hello

  预期返回 JSON，且包含：

  - "message":"hello-from-config-server"
  - "service":"customers-service" / "vets-service" / "visits-service"

  ———

  ## 7. Eureka Dashboard

  浏览器打开：

  - http://localhost:8761

  你应该能看到 CUSTOMERS-SERVICE / VETS-SERVICE / VISITS-SERVICE / API-GATEWAY 等实例注册信息。

  ———

  ## 8. CI（GitHub Actions）

  工作流文件：

  - .github/workflows/ci.yml

  CI 会在 push / PR 时执行：

  1. ./mvnw test
  2. ./mvnw -DskipTests package
  3. docker build 6 个服务镜像

  ———

  ## 9. 常见问题（Troubleshooting）

  ### 9.1 测试启动时报 Unable to load config data from 'configserver:'

  说明某个服务 spring.config.import=configserver: 是“必选导入”但测试环境没有 Config Server。

  解决思路：

  - 测试里覆盖：spring.config.import=optional:configserver:
  - 或测试里关闭 Config Client：
      - spring.cloud.config.enabled=false
      - spring.cloud.config.import-check.enabled=false

  （本项目的 api-gateway 测试已使用该策略。）

  ### 9.2 网关访问返回 503（Service Unavailable）

  一般是服务发现（Eureka）未就绪或实例未注册成功。

  排查：

  - 看 Eureka 是否 UP：curl -s http://localhost:8761/actuator/health
  - 看 Compose 容器日志：docker compose logs --tail=200 <service>
  - 看 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE 是否指向 http://discovery-server:8761/eureka/


  ---

  ## 这一步的笔记知识点（你用来记笔记）
  - Runbook/README 是“可交付”的一部分：别人只看文档就能跑起来。
  - 文档应包含：环境要求、构建、启动、验证、常见问题。
  - “验证”要写成可复制粘贴的命令（curl），不要只写文字。

  ---

  下一步你只需要做两件事：
  1) 新建 `README.md` 并粘贴上面内容
  2) `git add README.md && git commit -m "docs: add README runbook" && git push`

  你做完后告诉我。之后我们可以进入更进阶的学习阶段（比如：把网关路由测试做成真正的集成测试、加数据库、加 OpenAPI、加分布式 tracing 等）。


› 你先给我一份 readme吧，我直接粘贴


• # petclinic-ms-from-scratch

  从零复现的 Spring PetClinic Microservices（精简学习版）项目：你可以用它学习“配置中心 + 注册中心 + 网关 + 多服务 + Docker Compose + CI”的标准工程化流程。

  ## 功能组件
  - **Config Server**：集中管理配置（Spring Cloud Config Server）
  - **Discovery Server**：服务注册与发现（Eureka Server）
  - **API Gateway**：统一入口与路由转发（Spring Cloud Gateway）
  - **业务服务**：
    - `customers-service`
    - `vets-service`
    - `visits-service`
  - **Docker Compose**：一键启动全套服务
  - **CI（GitHub Actions）**：自动跑测试、打包、Docker build

  ---

  ## 1. 环境要求（Mac/Windows/Linux）
  - **JDK 21**
  - **Docker Desktop**（含 `docker compose`）
  - **Git**
  - **Maven Wrapper**：本项目已内置（用 `./mvnw`）

  检查命令：

  ```bash
  java --version
  docker --version
  docker compose version
  ./mvnw -v

  ———

  ## 2. 项目结构

  petclinic-ms-from-scratch/
    config-server/
    discovery-server/
    api-gateway/
    customers-service/
    vets-service/
    visits-service/
    docker-compose.yml
    pom.xml

  ———

  ## 3. 本地构建（不依赖 Docker）

  ### 3.1 跑全量测试（推荐先做）

  ./mvnw -ntp -B test

  ### 3.2 打包所有模块（生成各模块 jar）

  ./mvnw -ntp -B -DskipTests package

  ———

  ## 4. 构建 Docker 镜像

  > 注意：每个服务的 Dockerfile 都会 COPY target/*.jar，所以一定要先 package。

  ./mvnw -ntp -B -DskipTests package

  docker build -t petclinic/config-server:dev ./config-server
  docker build -t petclinic/discovery-server:dev ./discovery-server
  docker build -t petclinic/customers-service:dev ./customers-service
  docker build -t petclinic/vets-service:dev ./vets-service
  docker build -t petclinic/visits-service:dev ./visits-service
  docker build -t petclinic/api-gateway:dev ./api-gateway

  ———

  ## 5. Docker Compose 一键启动

  ### 5.1 启动

  docker compose up -d
  docker compose ps

  ### 5.2 查看日志

  docker compose logs -f --tail=200

  ### 5.3 停止与清理

  docker compose down

  ———

  ## 6. 验证服务是否正常

  ### 6.1 Health Check（基础）

  curl -s http://localhost:8888/actuator/health
  curl -s http://localhost:8761/actuator/health
  curl -s http://localhost:8080/actuator/health
  curl -s http://localhost:8081/actuator/health
  curl -s http://localhost:8082/actuator/health
  curl -s http://localhost:8083/actuator/health

  预期都返回：

  {"status":"UP"}

  ### 6.2 Config Server 配置读取

  curl -s http://localhost:8888/application/default
  curl -s http://localhost:8888/customers-service/default
  curl -s http://localhost:8888/api-gateway/default

  ### 6.3 通过网关访问三个服务（路由 + Eureka + LB）

  curl -s http://localhost:8080/customers/api/hello
  curl -s http://localhost:8080/vets/api/hello
  curl -s http://localhost:8080/visits/api/hello

  预期返回 JSON，且包含：

  - "message":"hello-from-config-server"
  - "service":"customers-service" / "vets-service" / "visits-service"

  ———

  ## 7. Eureka Dashboard

  浏览器打开：

  - http://localhost:8761

  你应该能看到服务实例注册信息（例如 CUSTOMERS-SERVICE / VETS-SERVICE / VISITS-SERVICE / API-GATEWAY）。

  ———

  ## 8. CI（GitHub Actions）

  工作流文件：

  - .github/workflows/ci.yml

  CI 在 push / pull_request 时执行：

  1. ./mvnw test
  2. ./mvnw -DskipTests package
  3. docker build 6 个服务镜像

  ———

  ## 9. Troubleshooting（常见问题）

  ### 9.1 测试启动时报：Unable to load config data from 'configserver:'

  原因：某服务使用了必选导入 spring.config.import=configserver:，但测试环境没起 Config Server（或你在测试里关闭了 Config Client）。

  解决思路：

  - 在测试里覆盖：spring.config.import=optional:configserver:
  - 并/或在测试里关闭 Config Client：
      - spring.cloud.config.enabled=false
      - spring.cloud.config.import-check.enabled=false

  ### 9.2 网关访问返回 503（Service Unavailable）

  通常是服务发现未就绪或实例未注册：

  - 确认 Eureka UP：curl -s http://localhost:8761/actuator/health
  - 看容器日志：docker compose logs --tail=200 <service>
  - 确认环境变量 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE 指向：
      - http://discovery-server:8761/eureka/


