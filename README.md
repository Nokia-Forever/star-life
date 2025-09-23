该项目正处于开发中,项目名称:星选生活\
这是基于springboot实现的点评项目,实现用户端+管理端的相结合\
初步实现:\
1.用户端的SpringSecurity框架的集成\
2.数据库存储采用Mysql,缓冲采用redis\
3.采用了定时任务,redis初始化,关闭监听器\

所用技术:
mysql,redis,lombok,mybatis-plus,jjwt,hutool-captcha,jackson........
spring:springboot,springSecurity,spring-cache,spring-validator,web,spring-task
模块区分:\
1.star-life-app 启动模块\
2.star-life-common 通用模块\
3.star-life-core 客户端核心模块\
4.star-life-admin 管理模块\
5.star-life-pojo 数据模块\ 

功能实现:\
客户端:\
1.用户:登录,注册,查询当前用户,更新当前用户,修改密码\
2.关注:关注和取关,判断是否关注,获取粉丝列表,获取关注列表,共同关注\
3.商铺类型:查询商铺类型\
4.商铺:申请店铺(未完善),定时任务判断店铺营业状态,获取店铺详细信息,手动开启或关闭商铺,清除手动处理营业状态\
5.店员:获取当前用户角色下权重比自己小,当前用户获取管理的店铺的信息,添加店员
