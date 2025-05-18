<p align="center">
 <img alt="logo" src="https://image.mayongjian.cn/2024/07/03/1de3ee08e0a34ab6bf9a163d380fb596.png" style="width: 100px">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">HongShu</h1>
<h4 align="center">基于SpringBoot+Vue前后端分离的仿小红书项目</h4>
<p align="center">
	<a href="https://gitee.com/Maverick_Ma/hongshu/stargazers">
    <img src="https://gitee.com/Maverick_Ma/hongshu/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/Maverick_Ma/hongshu">
    <img src="https://img.shields.io/badge/HongShu-v1.0-brightgreen.svg"></a>
	<a href="https://gitee.com/Maverick_Ma/hongshu/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 项目简介
基于 SpringBoot + Vue 前后端分离的仿小红书系统，1:1 还原小红书页面及功能。本项目采用当下最主流的前后端分离架构，前端使用 Vue3 + TypeScript 开发，后端采用 SpringBoot 框架。项目完整还原了小红书的核心功能，包括内容发布、社交互动、私信聊天、搜索发现等模块，致力于打造一个功能完善、体验流畅的社交分享平台。
* [HongShu](https://gitee.com/Maverick_Ma/hongshu) 本项目为单应用版本 
* [HongShu-Web](https://gitee.com/Maverick_Ma/hongshu-web) 用户端采用Vue3、ts、Element UI。
* [HongShu-Admin](https://gitee.com/Maverick_Ma/hongshu-admin) 管理端采用Vue3、Vite、Element UI。
* 后端采用Spring Boot、Mybatis-plus、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。

## 项目背景
小红书作为当下最受欢迎的生活方式分享社区，拥有海量的用户群体和丰富的内容生态。本项目旨在通过技术手段还原小红书的核心功能，不仅可以学习到完整的全栈开发流程，还能深入理解大型社交平台的架构设计和业务实现。

## 项目特色
### 用户端内置功能
1. 瀑布流展示笔记、懒加载笔记图片
2. 使用 ElasticSearch 做关键词搜索查询 
3. 支持私信聊天，关注、评论、点赞和收藏等功能
4. 使用 WebSocket 做消息通知，实现私信聊天消息实时通知，消息页面实时显示用户未读消息数量
5. 双Token登陆机制，使用 Redis 做对象缓存
6. 笔记发布和修改，支持本地存储和Minio存储

### 管理端内置功能
1. 用户管理：该功能主要完成系统用户配置。
2. 角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
3. 菜单管理：配置系统菜单，操作权限，按钮权限标识等。
4. 日志管理：包括登录日志和操作日志，系统登录日志记录查询包含登录异常，系统异常信息日志记录和查询。
5. 在线用户：当前系统中活跃用户状态监控。
6. 定时任务：在线（添加、修改、删除）任务调度包含执行结果日志。
7. 系统接口：根据业务代码自动生成相关的api接口文档。
8. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
9. 缓存监控：对系统的缓存信息查询，命令统计等。
10. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 2.0版本功能
1. 新增uniapp移动端
2. 笔记支持视频和live图类型
3. 新增七牛云、阿里云、腾讯云等多种OSS对象存储方式，可动态配置
4. 加入协同过滤算法实现推荐功能

## 3.0版本功能
1. 重构实现 SpringCloud 微服务架构版本，采用 Nacos 作为服务发现和配置中心，轻松完成项目的配置的维护
2. 引入Arco-Design字节提供的前端UI实现新的后台管理页面
3. 引入高德地图
4. 加入商城购物功能
5. 使用netty重构消息聊天
6. 使用 MQ+Redis 优化点赞、收藏、浏览功能

## 说明:
1. 后续只会更新微服务版（HongShu-Cloud），演示站会同步更新
2. 由于微信小程序每次发布审核过于严苛和繁琐，暂时不再提供体验环境，但uniapp端依然会继续兼容小程序
3. 演示站部署的是微服务版本,与开源版本功能存在一定差异，具体请参考:


- 目前，微服务版源码暂未开源，详情可添加微信了解：coder_xiaomage
- 如有项目问题、部署需求、项目定制也可微信联系

  <img src="doc/images/wx.png" style="width: 100px; height: 100px"/>

## 演示站（微服务版本-持续更新）
- 【web端】 ➡️ [点我进入](http://115.190.73.103)
- 【管理端】 ➡️ [点我进入](http://115.190.73.103/admin/)
- 【移动端】 ➡️ [点我进入](http://115.190.73.103/app/) 或扫码体验: 

  <img src="doc/images/h5.png" style="width: 100px; height: 100px"/>

* 也感谢小伙伴们打赏支持更新升级服务器❤️

  <img src="doc/images/pay.png" style="width: 200px; height: 100px"/>

## 技术选型
* 系统架构图


## 项目地址
目前项目托管在 Gitee 和 Github 平台上中，欢迎大家 Star 和 Fork 支持~
- Gitee地址：https://gitee.com/Maverick_Ma/hongshu
- Github地址：https://github.com/Ma-YongJian/HongShu

## 项目文档
* 文档及资料会暂时放到我的个人博客：[点我进入](https://mayongjian.cn)

## 视频演示
- [点击查看](https://www.bilibili.com/video/BV1QP8dekEGq/?spm_id_from=333.999.list.card_archive.click&vd_source=ec9224821314432ac6e12dc7d500d74b)

## 致谢
红薯项目参考了很多开源项目的解决方案，开源不易，感谢分享
- 感谢 [若依](https://gitee.com/y_project/RuoYi) 提供的 RuoYi 项目：https://gitee.com/y_project/RuoYi
- 感谢 [xiaozhao](https://gitee.com/xzjsccz/xiaohongshu) 提供的前端仿小红书源码：https://gitee.com/xzjsccz/xiaohongshu
- 感谢 [陌溪](https://gitee.com/moxi159753/mogu_blog_v2) 提供的 蘑菇博客 项目：https://gitee.com/moxi159753/mogu_blog_v2
- <img src="./doc/images/qiniu.jpg" width="80" />感谢 [七牛云](https://portal.qiniu.com/signup?utm_source=kaiyuan&utm_media=mogu) 提供的免费云存储和CDN服务
- <img src="./doc/images/jetbrains.png" width="40" />感谢 [jetbrains](https://www.jetbrains.com/?from=mogu_blog_v2) 提供的开源License

## 演示图
* web端：
<table>
    <tr>
        <td><img src="doc/images/web/web-login.png"/></td>
        <td><img src="doc/images/web/web-dashboard.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/web/web-search.png"/></td>
        <td><img src="doc/images/web/web-trends.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/web/web-message.png"/></td>
        <td><img src="doc/images/web/web-follow.png"/></td>
    </tr>
	<tr>
        <td><img src="doc/images/web/web-publish.png"/></td>
        <td><img src="doc/images/web/web-publish-map.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/web/web-publish-img.png"/></td>
        <td><img src="doc/images/web/web-publish-video.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/web/web-main-img.png"/></td>
        <td><img src="doc/images/web/web-user.png"/></td>
    </tr>
</table>

* 管理端：
<table>	 
    <tr>
        <td><img src="doc/images/admin/admin-login.png"/></td>
        <td><img src="doc/images/admin/admin-data.png"/></td>
    </tr>
	<tr>
        <td><img src="doc/images/admin/admin-category.png"/></td>
        <td><img src="doc/images/admin/admin-member.png"/></td>
    </tr>
	<tr>
        <td><img src="doc/images/admin/admin-note.png"/></td>
        <td><img src="doc/images/admin/admin-album.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/admin/admin-comment.png"/></td>
        <td><img src="doc/images/admin/admin-log.png"/></td>
    </tr>
</table>

* app端：
<table>	 
    <tr>
        <td><img src="doc/images/app/app-login.png"/></td>
        <td><img src="doc/images/app/app-index.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/app/app-trend.png"/></td>
        <td><img src="doc/images/app/app-message.png"/></td>
    </tr>
	<tr>
        <td><img src="doc/images/app/app-user.png"/></td>
        <td><img src="doc/images/app/app-user-edit.png"/></td>
    </tr>
	<tr>
        <td><img src="doc/images/app/app-follow.png"/></td>
        <td><img src="doc/images/app/app-search.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/app/app-main.png"/></td>
        <td><img src="doc/images/app/app-push.png"/></td>
    </tr>
    <tr>
        <td><img src="doc/images/app/app-comment.png"/></td>
        <td><img src="doc/images/app/app-like.png"/></td>
    </tr>
</table>
