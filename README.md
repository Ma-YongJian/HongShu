<p align="center">
  <img alt="logo" src="https://image.mayongjian.cn/2025/11/11/d6ab67fa0291468ea68afdfb18217ae4.png" width="100"/>
</p>

<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">HongShu</h1>
<p align="center">
  <b>基于 SpringBoot + Vue + Uniapp + AI大模型 的仿小红书全栈项目</b>
</p>
<p align="center">
  <i>1:1 高仿小红书，具备内容发布、商品交易、社交互动、算法推荐、即时通讯、AI对话等核心功能，适合全栈学习与项目实战。</i>
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


> **🌟声明**  
> 本项目基于 [MIT](https://opensource.org/licenses/MIT) 协议开源，免费用于学习交流，禁止转售，请提高警惕，谨防受骗。
> 项目名称“小蓝书”为开发代号与“小红书”无任何商业关联，仅为个人学习研究目的使用，演示环境所用图片/视频素材均来源于网络，如有侵权请联系删除。
> 如需商用请保留版权信息，确保合法合规使用，自行承担运营风险，与项目作者无关。

---
## 🚀 项目简介

### 🎓 适合人群

|                             👨‍💻 **全栈开发者**                              |         **求职者**          |         **创业团队**          |         **学生党**         |
|:------------------------------------------------------------------------:|:--------------------------:|:---------------------------:|:-------------------------:|
| 学习 SpringBoot + Vue + Uniapp 全栈开发<br>了解微服务架构设计<br>了解高并发业务场景<br>掌握前后端分离开发 | 简历项目经验<br>面试技术亮点<br>实战能力证明 | 快速搭建社交平台<br>二次开发定制<br>商业化落地 | 毕业设计项目<br>技术栈学习<br>实战经验积累 |

### 📂 项目组成

| 模块                                                               | 模块说明             | 技术栈                                            | 项目地址                                                                                                          |
|------------------------------------------------------------------|------------------|------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **[HongShu](https://gitee.com/Maverick_Ma/hongshu)**             | 后端服务代码（Java）     | SpringBoot、SpringCloud                         | [gitee](https://gitee.com/Maverick_Ma/hongshu) 、 [github](https://github.com/Ma-YongJian/HongShu)             |
| **[HongShu-Ui](https://gitee.com/Maverick_Ma/hongshu-ui)**      | 前端代码（Vue、Uniapp） | Vue3、Element Plus、TypeScript、Uniapp | [gitee](https://gitee.com/Maverick_Ma/hongshu-ui) 、 [github](https://github.com/Ma-YongJian/HongShu-Ui)     |

### 🏗️ 技术选型

<table>
<td width="33%">

#### 后端技术栈
- **框架**：SpringBoot 3.x、SpringCloud
- **注册中心**：Nacos
- **数据库**：MySQL 8.x、MyBatis-Plus
- **缓存**：Redis 7.x
- **搜索**：ElasticSearch 8.x
- **消息队列**：RocketMQ
- **即时通讯**：WebSocket、Netty
- **AI集成**：OpenAI SDK、多模型适配

</td>
<td width="33%">

#### 前端技术栈
- **框架**：Vue3 + TypeScript
- **构建工具**：Vite 4.x
- **UI组件**：Element Plus、Arco Design
- **移动端**：Uniapp（支持H5、APP、微信小程序）
- **状态管理**：Pinia
- **网络请求**：Axios
- **富文本**：WangEditor
- **地图**：高德地图

</td>
<td width="33%">

#### 其他技术
- **容器化**：Docker、Docker Compose
- **反向代理**：Nginx
- **对象存储**：Minio、七牛云、阿里云、腾讯云
- **短信服务**：阿里云、腾讯云、云片
- **支付**：支付宝沙箱
- **监控**：SpringBoot Admin
- **文档**：Swagger

</td>
</table>

---

## 🌟 项目特色

| 功能模块 | 技术实现                                                            | 创新点                             |
|------|-----------------------------------------------------------------|---------------------------------|
| 智能推荐 | 轻量级推荐 + 协同过滤 + ES检索 动态切换                                       | ✅ 三套算法、新用户优化、性能<100ms         |
| 即时通讯 | WebSocket + Netty + RocketMQ                                    | ✅ 离线消息、多端同步、高并发支持             |
| 社交电商 | 笔记种草 + 商品转化 + 在线支付                                             | ✅ 价格区间推荐、同城优先、闭环转化           |
| 高并发优化 | Redis + RocketMQ 双通道 + 批量落库                                    | ✅ 性能提升20倍、数据库压力降低90%          |
| AI大模型 | ChatGPT + DeepSeek + 多模型适配                                     | ✅ 8+模型支持、智能创作、对话系统            |
| 安全认证 | 双Token无感刷新 + JWT                                               | ✅ 用户无感知、安全可靠                  |
| 多云存储 | 本地 + Minio + 七牛云 + 阿里云 + 腾讯云                                   | ✅ 动态切换、后台配置                    |
| 智能搜索 | ElasticSearch + MySQL 双引擎                                      | ✅ 多字段权重、模糊匹配                  |
| LBS定位 | 高德地图 + IP定位                                                    | ✅ 同城推荐、附近内容                    |
| 在线支付 | 支付宝沙箱支付                                                         | ✅ 完整支付流程                       |
| 多端适配 | Web + App + 小程序                                                | ✅ 代码复用、统一API                   |
| 数据统计 | 用户增长、内容统计、交易分析                                                  | ✅ 可视化图表、实时监控                  |

---

## 📅 版本演进
> `💡 点击下方版本号可展开查看版本详情`
>  [👉 点击获取 Pro 版源码](https://www.yuque.com/xiaomage-ippj7/kaoqwn/rqslw69egcbgx7v7?singleDoc#)
<details>
<summary><b>v1.0（Lite版）</b></summary>

- ✅ ES 智能查询
- ✅ 笔记随机推荐
- ✅ 图文笔记发布
- ✅ 本地存储、七牛云存储
- ✅ 基础审核机制

</details>

<details>
<summary><b>v2.0（Pro版）</b></summary>

- ✅ 新增 Uniapp 移动端
- ✅ 支持视频笔记
- ✅ 支持Live Photo（动态图片）
- ✅ OSS多端存储方案（动态配置）
- ✅ 协同过滤推荐

</details>

<details>
<summary><b>v3.0（Pro版）</b></summary>

- ✅ 新增 SpringCloud 微服务架构
- ✅ Nacos 配置中心
- ✅ WebSocket 即时通讯系统
- ✅ 闲置商城（社交电商融合）
- ✅ 高德地图 LBS 定位
- ✅ Redis + RocketMQ 高并发优化

</details>

<details>
<summary><b>v4.0（Pro版）</b></summary>

- ✅ 支持扫码登录
- ✅ WebSocket 离线消息推送
- ✅ 8+ 主流AI大模型集成
- ✅ 智能对话系统
- ✅ AI辅助创作
- ✅ 微信小程序上线
- ✅ 新增 Arco Design 新版管理后台
- ✅ **轻量级推荐算法**（自研）
    - 四维度混合召回
    - 性能<100ms

</details>

<details>
<summary><b>v5.0（Pro版）</b></summary>

- ✅ 移动端支持大模型对话
- ✅ 微信一键登录
- ✅ 微信支付
- ✅ 支持微信分享（对话、朋友圈）

</details>

<details>
<summary><b>v6.0（Pro版）</b></summary>

-  使用ruoyi-plus重构单体版/微服务版

</details>

---

## 🌐 演示站（微服务版本 - 演进更新）

> 💡 **温馨提示**: 因服务器配置有限，如遇访问缓慢请耐心等待
> `小龟速服务器正在奔跑中...`

| 端类型           | 访问方式                                                                                | 功能说明           | 
|---------------|-------------------------------------------------------------------------------------|----------------|
| **Web端（响应式）** | [👉 点击查看（电脑）](https://hongshu.website/)                                             | 完整功能，PC体验最佳    |
| **Arco管理端**   | [👉 点击查看（电脑）](https://hongshu.website/arco-admin/)                                  | 现代化管理界面，PC体验最佳 |
| **移动端(H5)**   | [👉 点击查看](https://hongshu.website/app/) 或 扫码体验 <img src="doc/images/h5.png" width="80"/> | 移动端体验最佳        |
| **微信小程序**     | 体验版 <img src="doc/images/wxamp.png" width="80"/>                                    | 小程序生态          |

---

## 📞 联系与支持

<table>
<tr>
<td align="center" width="25%">

#### 🤝 项目定制 & 部署
<img src="doc/images/wx.png" width="150"/>

- 项目部署指导
- 功能定制开发
- 技术咨询支持
- Bug修复服务

</td>
<td align="center" width="25%">

#### 📂 项目资料
<img src="doc/images/gongzhonghao.jpg" width="150"/>

- 搭建部署文档
- 核心功能详解
- 架构图、时序图
- 开题报告、毕业论文

</td>
<td align="center" width="25%">

#### 💖 打赏支持
<img src="doc/images/pay.png" width="200"/>

（ ps: 小伙伴赞赏的时候可以备注一下下~）
- 升级服务器配置
- 持续功能开发
- 提升访问速度
- 更好的开源体验

</td>
</tr>
</table>

---

## 📦 项目地址

- **Gitee**: [https://gitee.com/Maverick_Ma/hongshu](https://gitee.com/Maverick_Ma/hongshu) （⭐ Star支持）
- **Github**: [https://github.com/Ma-YongJian/HongShu](https://github.com/Ma-YongJian/HongShu) （⭐ Star支持）

> **❤️ 如果这个项目对您有帮助，欢迎 Star & Fork 支持一下！** **您的 Star 是对我最大的鼓励！** 


## 📚 项目文档
> 都放在公众号里了，自取即可<br>
> 
<img src="doc/images/gongzhonghao-search.png" width="300"/>

## 🎬 视频演示

- [演示视频](https://www.bilibili.com/video/BV1dr7Gz9E53/?vd_source=ec9224821314432ac6e12dc7d500d74b)

---

## 📷 演示图

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
    <td><img src="doc/images/app/app-chat.png" width="300"/></td>
    <td><img src="doc/images/app/app-note.png" width="300"/></td>
    <td><img src="doc/images/app/app-product.png" width="300"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/app/app-mine.png" width="300"/></td>
    <td><img src="doc/images/app/app-message.png" width="300"/></td>
    <td><img src="doc/images/app/app-message-chat.png" width="300"/></td>
    <td><img src="doc/images/app/app-message-notice.png" width="300"/></td>
  </tr>
</table>

### Web端

<table>
  <tr>
    <td><img src="doc/images/web/web-login.png" width="500"/></td>
    <td><img src="doc/images/web/web-dashboard.png" width="500"/></td>
    <td><img src="doc/images/web/web-idle.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-trends-note.png" width="500"/></td>
    <td><img src="doc/images/web/web-message.png" width="500"/></td>
    <td><img src="doc/images/web/web-user.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-publish.png" width="500"/></td>
    <td><img src="doc/images/web/web-publish-img.png" width="500"/></td>
    <td><img src="doc/images/web/web-publish-video.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/web/web-message-comment.png" width="500"/></td>  
    <td><img src="doc/images/web/web-message-notice.png" width="500"/></td>
    <td><img src="doc/images/web/web-message-chat.png" width="500"/></td>
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
    <td><img src="doc/images/admin/admin-navbar.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-note.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-idle.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/admin/admin-member.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-album.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-config.png" width="500"/></td>
  </tr>
  <tr>
    <td><img src="doc/images/admin/admin-gpt.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-gpt-login.png" width="500"/></td>
    <td><img src="doc/images/admin/admin-gpt-chat.png" width="500"/></td>
  </tr>
</table>


## 🙏 致谢
**小蓝书** 项目参考了很多开源项目的解决方案，开源不易，感谢分享

- 感谢 **若依** 提供的RuoYi项目：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 、 [RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud)
- 感谢 **dromara** 提供的RuoYi-plus项目：[RuoYi-Vue-Plus](https://gitee.com/dromara/RuoYi-Vue-Plus) 、 [RuoYi-Cloud-Plus](https://gitee.com/dromara/RuoYi-Cloud-Plus)
- 感谢 **陌溪** 提供的博客项目：[蘑菇博客](https://gitee.com/moxi159753/mogu_blog_v2)
- 感谢 **xiaozhao** 提供的：[仿小红书前端](https://gitee.com/xzjsccz/xiaohongshu) 、 [yanhuo后端](https://gitee.com/xzjsccz/yanhuo-springboot-vue)
- 感谢 **panday** 提供的：[Chat MASTER](https://gitee.com/panday94/chat-master)
- 感谢 **belief-team** 提供的：[AJ-Captcha](https://gitee.com/belief-team/captcha)
- <img src="./doc/images/qiniu.jpg" width="80" />感谢 [七牛云](https://portal.qiniu.com/signup?utm_source=kaiyuan&utm_media=mogu) 提供的免费云存储和CDN服务
- <img src="./doc/images/jetbrains.png" width="40" />感谢 [jetbrains](https://www.jetbrains.com/?from=mogu_blog_v2) 提供的开源License
