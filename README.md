# TdBotsLauncher

Td Bots 的启动器

## 安装

#### 依赖 (Linux)

```shell script
apt install -y openssl git zlib1g libc++-dev default-jdk
```

注： 仅支持 `amd64, i386, arm64`, 否则需自行编译 [LibTDJni](https://github.com/TdBotProject/LibTDJni) 放置在 libs 文件夹下.  

如遇到找不到 `LIBC` 库, 请更新系统或编译安装.

### 依赖 (Windows)

需要安装 [Git for Windows](https://gitforwindows.org/) 与 [VC++ 2015](https://github.com/abbodi1406/vcredist/releases) 与 [OpenJDK 11](https://github.com/ojdkbuild/ojdkbuild)

您也可使用 `vcpkg` 编译安装 `openssl` 与 `zlib`

## 配置

复制 `_bots.yml` 到 `bots.yml`.

配置文件格式:

```yaml
BOT_LANG: zh_CN
LOG_LEVEL: INFO
DATA_DIR: data
CACHE_DIR: cache
BOTS:
  - ID: main
    TYPE: pm
    BOT_TOKEN:
    B0T_OWNER:
    PM_MODE: private
    PM_WHITE_LIST:
  - ...
```

根配置项除 BOTS 外均应用到每个机器人, 但以下除外:

* 机器人默认数据目录为根项 DATA_DIR 的 规范后的 ID 子目录, 但可以覆盖.
* 仅根项 LOG_LEVEL 生效

### 特定机器人子配置项
```yaml
ID: 机器人 ID, 唯一.
TYPE: 机器人类型.
```

### 子机器人类型

`pm` - [TdPmBot](https://github.com/TdBotProject/TdPmBot)


## 管理

```shell script
echo "alias bots='bash $PWD/bot.sh'" >> $HOME/.bashrc
source $HOME/.bashrc

# 注册 ./bot.sh 的命令别名 ( bots )
```

```shell script
bots run # 编译安装并进入交互式认证  
bots init # 注册 systemd 服务  
bots <start/stop/restart> # 启动停止  
bots <enable/disable> # 启用禁用 (开机启动)  
bots rebuild # 重新编译  
bots update # 更新  
bots force-update # 强制重新更新
bots upgrade # 更新并重启服务  
bots log # 实时日志  
bots logs # 所有日志
```

`注: 重新编译前请停止服务以避免运行时 jar 文件覆盖导致的错误, 但不同版本之间不需要.`

## 其他

如需更改, 复制 `_bot.conf` 到 `bot.conf`.

```
SERVICE_NAME: systemd 服务名称, 默认 `td-bots`, 修改如果您需要多个实例.
MVN_ARGS: Maven 编译参数.
JAVA_ARGS: JVM 启动参数.
ARGS: 启动参数.
```

## 命令行命令

`bots run [all|type:[机器人类型]|机器人ID] [在目标实例列表执行的命令...]`

```
all - 所有机器人
type:[类型] - 所有指定类型的机器人
[机器人 ID] - 指定机器人
```

命令请转至对应实例之文档.

## Docker

由于环境变量限制, 待创建一管理脚本后推送镜像.