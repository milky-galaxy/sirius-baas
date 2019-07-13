# Sirius-BaaS

## 环境配置

```bash
npm install -g @alicloud/fun 
fun config
```

## 本地运行

```bash
mvn package && fun local invoke sirius/baas --event /path/to/event-data-file
```

## 本地调试

```bash
fun local invoke sirius/baas --event /path/to/event-data-file --debug-port 5005
```

## 部署

```bash
mvn clean package && fun deploy
```

## 执行

```bash
fcli function invoke -s sirius -f baas
```