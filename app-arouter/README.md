## # ARouter

# 相关类

* ARouter 

* _ARouter

  > 功能实现类、过滤服务、拦截服务、主动注入服务、降级服务

* LogisticsCenter

  > 初始化Warehouse、组装路由信息，分发路由。

* Postcard

  > 路由信息

* RouteMeta

  > 注解信息

* Warehouse 路由信息表

  > 老版本是运行期ClassUtils扫描app加载，新版本通过GradlePlugin+ASM插桩加载



## 过程

### 1.编译期

arouter-compiler

apt获取所有注解信息，javapoet生成.java源码，然后编译成.class

### 2.transform时期

arouter-gradle-plugin

修改或生成.class文件，再生成dex

> arouter-gradle-plugin是一个插件，被ARouter用来加快应用安装后第一次进入时的速度。如果使用插件的话，那么会ASM直接插入字节码，省去了运行时需要扫描指定包名下面的所有className所造成的耗时。网上说ARouter加入apk后第一次加载会耗时，这是指的是没有使用arouter插件的时候，在第一次进入apk时，主线程必须等待子线程去扫描指定包名下面的所有className，如果class比较多，会比较耗时。

### 3.运行时期

arouter-api

运行时使用ARouter.init() build() navigation()方法。



## 知识点

### APT

注解处理器annotation processor tool

1.降低耦合2.从运行期（反射）转到编译器，提高性能。

### Javapoet

生成Java源码的工具

> Github：https://github.com/square/javapoet
>
> 模块：https://juejin.im/entry/58fefebf8d6d810058a610de

不要使用kotlin编写，$会产生冲突

也有专门的Kotlinpoet

### GradlePlugin

> 参考：https://github.com/SusionSuc/AdvancedAndroid/tree/master/gradle

### ASM

ASM 是一个 Java 字节码操控框架。它能被用来动态生成类或者增强既有类的功能。ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为

> 使用指南：https://github.com/SusionSuc/AdvancedAndroid/blob/master/gradle/ASM4%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97.pdf

### 说明

* 为什么使用GradlePlugin+ASM

  在编译时，扫描所有类，将符合条件的类收集起来，并通过修改字节码生成注册代码到指定的管理类中，从而实现编译时自动注册的功能，不用再关心项目中有哪些组件类了。
  优点：不会增加新的class，不需要反射，运行时直接调用组件的构造方法；ASM自定义扫描，过滤不必要的类；
  缺点：增加编译时耗时，可忽略

## 参考

> ARouter：https://github.com/alibaba/ARouter/blob/master/README_CN.md
>
> 阿里云曦 https://developer.aliyun.com/article/71687
>
> 路由对比：https://github.com/luckybilly/AndroidComponentizeLibs
>
> 自动注解插件：https://github.com/luckybilly/AutoRegister
>
> 几个问题：https://zhuanlan.zhihu.com/p/361025253