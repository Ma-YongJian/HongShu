<p align="center">
  <img alt="logo" src="https://image.mayongjian.cn/2024/07/03/1de3ee08e0a34ab6bf9a163d380fb596.png" width="100"/>
</p>

<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">HongShu</h1>
<p align="center">
  <b>基于 SpringBoot + Vue 前后端分离的仿小红书项目</b>
</p>
<p align="center">
  <i>一款高仿小红书的全栈开源项目，支持内容发布、社交互动、即时通讯等核心功能，适合全栈学习与实战。</i>
</p>

<p align="center">
  <a href="https://gitee.com/Maverick_Ma/hongshu/stargazers">
    <img src="https://gitee.com/Maverick_Ma/hongshu/badge/star.svg?theme=dark">
  </a>
  <a href="https://gitee.com/Maverick_Ma/hongshu">
    <img src="https://img.shields.io/badge/HongShu-v1.0-brightgreen.svg">
  </a>
  <a href="https://gitee.com/Maverick_Ma/hongshu/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/mashape/apistatus.svg">
  </a>
</p>


> **声明**  
> 本项目基于 MIT 协议，免费开源，仅供学习交流，禁止转卖，谨防受骗。如需商用请保留版权信息，确保合法合规使用，运营风险自负，与作者无关。

---

## 🚀 项目简介

基于 **SpringBoot + Vue + TypeScript** 前后端分离的**全栈仿小红书系统**，
1:1 还原小红书页面及核心功能，微服务架构演进版本。  
涵盖内容发布、社交互动、私信聊天、搜索发现等模块，致力于打造一个功能完善、体验流畅的社交分享平台。

### 📂 项目组成

| 模块 | 技术栈 | 地址                                                                                                |
|------|--------|---------------------------------------------------------------------------------------------------|
| **[HongShu](https://gitee.com/Maverick_Ma/hongshu)** | SpringBoot | [Gitee](https://gitee.com/Maverick_Ma/hongshu) 、 [github](https://github.com/Ma-YongJian/HongShu) |
| **[HongShu-Web](https://gitee.com/Maverick_Ma/hongshu-web)** | Vue3 + Element Plus | [Gitee](https://gitee.com/Maverick_Ma/hongshu-web) 、 [github](https://github.com/Ma-YongJian/HongShu-Web) |
| **[HongShu-Admin](https://gitee.com/Maverick_Ma/hongshu-admin)**  | Vue3 + Vite | [Gitee](https://gitee.com/Maverick_Ma/hongshu-admin) 、 [github](https://github.com/Ma-YongJian/HongShu-Admin) |
| **[HongShu-App](https://gitee.com/Maverick_Ma/hongshu-app)** | Uniapp | [Gitee](https://gitee.com/Maverick_Ma/hongshu-app) 、 [github](https://github.com/Ma-YongJian/HongShu-App) |

### 🏗️ 技术选型

- 后端：SpringBoot、SpringCloud、Nacos、Redis、ElasticSearch、MySQL
- 前端：Vue3、TypeScript、Element UI、Vite、uniapp
- 消息：WebSocket、Netty、MQ
- 存储：本地、Minio、七牛云、腾讯云、阿里云
- 短信：阿里云、腾讯云、云片
- 地图：高德地图
---

## 🌟 项目特色

| 功能模块      | 技术实现                     |
|-----------|--------------------------|
| 智能推荐      | ElasticSearch + 协同过滤算法   |
| 即时通讯      | WebSocket + Netty        |
| 多端存储方案    | Minio/七牛云/阿里云/腾讯云OSS动态切换 |
| 高并发优化     | Redis + MQ 消息队列          |
| 安全认证      | 双Token无感刷新机制             |
| 数据双通道同步机制 | 实时写入机制、定时更新机制            |
| 社交与电商融入   | 打通商品和社交通道，实现种草分享，链接购买             |

---

## 📅 版本演进
> 💡 点击下方版本号可展开查看详细内容
<details>
<summary><b>v1.0（Lite版）</b></summary>

- ES 智能查询、笔记随机推荐
- 笔记支持图文类型
- 本地存储、七牛云存储
- 审核机制

</details>

<details>
<summary><b>v2.0（Pro版）</b></summary>

- 新增 uniapp 移动端
- 笔记支持视频和 live 图类型
- 新增多种 OSS 存储方式，可动态配置
- 协同过滤算法推荐

</details>

<details>
<summary><b>v3.0（Pro版）</b></summary>

- 重构为 springCloud 微服务架构，Nacos 配置中心
- 新后台管理页面（Arco-Design）
- 引入高德地图
- 闲置商城购物功能
- Netty 重构消息聊天
- MQ+Redis 优化点赞、收藏、浏览

</details>

---

## 🌐 演示站（微服务版本-持续更新）

`小龟速服务器正在奔跑中...`

| 端口   | 入口链接                                   | 二维码                                        |
|--------|--------------------------------------------|--------------------------------------------|
| Web端  | [点我进入](http://115.190.73.103)         | -                                          |
| 管理端 | [点我进入](http://115.190.73.103/admin/)  | -                                          |
| 移动端 | [点我进入](http://115.190.73.103/app/)    | <img src="doc/images/h5.png" width="120"/> |

> 🌟 Pro版源码暂未开源，如需可点击 [获取源码](https://www.yuque.com/xiaomage-ippj7/kaoqwn/rqslw69egcbgx7v7?singleDoc#)
> 或 `添加微信` 获取👇 
> 
> <img src="doc/images/wx.png" width="200"/>

> 💡 遇到问题？想获取最新动态？扫码加入临时交流群，我们一起讨论优化~
> 
> <img src="doc/images/wx.jpg" width="200"/>

---

## 📦 项目地址

- Gitee: [https://gitee.com/Maverick_Ma/hongshu](https://gitee.com/Maverick_Ma/hongshu)
- Github: [https://github.com/Ma-YongJian/HongShu](https://github.com/Ma-YongJian/HongShu)

> **欢迎 Star & Fork 支持项目！如有`项目问题`、`部署`或`定制需求`可微信联系作者**

---

## 📚 项目文档

- 文档及资料：[博客](https://www.yuque.com/xiaomage-ippj7/kaoqwn/zdgtgrco82vftbot?singleDoc#)

---

## 🎬 视频演示

- [Bilibili 演示视频](https://www.bilibili.com/video/BV1QP8dekEGq/?spm_id_from=333.999.list.card_archive.click&vd_source=ec9224821314432ac6e12dc7d500d74b)

---

## 💖 打赏支持

感谢小伙伴们打赏支持！当前服务器配置有限（2核4G），偶尔会有加载缓慢的情况。
<img src="doc/images/pay.png" width="300"/>

---

## 🛠️ 快速开始

详见[项目文档](https://www.yuque.com/xiaomage-ippj7/kaoqwn/zdgtgrco82vftbot?singleDoc# )，或参考各子项目 README 进行本地部署体验。

---

## ❓ FAQ

- Q: 如何本地运行？
  A: 详见各子项目 README 或 [项目文档](https://www.yuque.com/xiaomage-ippj7/kaoqwn/zdgtgrco82vftbot?singleDoc# )。
- Q: 依赖环境有哪些？
  A: JDK 8+、Node 16+、MySQL 8+、Redis 6+ 、Elasticsearch 7+等。
- Q: 数据库脚本在哪里？
  A: 见后端项目 `doc/sql` 目录。

---

## 📷 演示图

### Web端

<table>
  <tr>
    <td><img src="doc/images/web/web-login.png" width="500"/></td>
    <td><img src="doc/images/web/web-dashboard.png" width="500"/></td>
    <td><img src="doc/images/web/web-idle.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-search.png" width="500"/></td>
    <td><img src="doc/images/web/web-trends-note.png" width="500"/></td>
    <td><img src="doc/images/web/web-trends-idle.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-publish.png" width="500"/></td>
    <td><img src="doc/images/web/web-publish-img.png" width="500"/></td>
    <td><img src="doc/images/web/web-publish-video.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-publish-map.png" width="500"/></td>
    <td><img src="doc/images/web/web-main-img.png" width="500"/></td>
    <td><img src="doc/images/web/web-main-video.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-message-note.png" width="500"/></td>
    <td><img src="doc/images/web/web-message-idle.png" width="500"/></td>
    <td><img src="doc/images/web/web-message-comment.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-message-like.png" width="500"/></td>
    <td><img src="doc/images/web/web-message-follow.png" width="500"/></td>
    <td><img src="doc/images/web/web-user.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-user-mine.png" width="500"/></td>
    <td><img src="doc/images/web/web-user-idle.png" width="500"/></td>
  </tr>
</table>

### 管理端

<table>
  <tr>
    <td><img src="doc/images/admin/admin-login.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-data.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-data-map.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/admin/admin-note.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-idle.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-member.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/admin/admin-album.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-comment.png" width="500"/></td>
  </tr>
</table>

### App端

<table>
  <tr>
    <td><img src="doc/images/app/app-login.png" width="300"/></td>
    <td><img src="doc/images/app/app-index.png" width="300"/></td>
    <td><img src="doc/images/app/app-follow.png" width="300"/></td>
    <td><img src="doc/images/app/app-near.png" width="300"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/app/app-idle.png" width="300"/></td>
    <td><img src="doc/images/app/app-user.png" width="300"/></td>
    <td><img src="doc/images/app/app-user-mine.png" width="300"/></td>
    <td><img src="doc/images/app/app-message.png" width="300"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/app/app-search.png" width="300"/></td>
    <td><img src="doc/images/app/app-main.png" width="300"/></td>
    <td><img src="doc/images/app/app-comment.png" width="300"/></td>
    <td><img src="doc/images/app/app-like.png" width="300"/></td>
  </tr>
</table>


## 🙏 致谢

- [若依 RuoYi](https://gitee.com/y_project/RuoYi)
- [蘑菇博客](https://gitee.com/moxi159753/mogu_blog_v2)
- [xiaozhao 仿小红书前端](https://gitee.com/xzjsccz/xiaohongshu)
- <img src="./doc/images/qiniu.jpg" width="80" />感谢 [七牛云](https://portal.qiniu.com/signup?utm_source=kaiyuan&utm_media=mogu) 提供的免费云存储和CDN服务
- <img src="./doc/images/jetbrains.png" width="40" />感谢 [jetbrains](https://www.jetbrains.com/?from=mogu_blog_v2) 提供的开源License
