# ARouter

## 一、相关类

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



## 二、过程

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



## 三、知识点

### APT

注解处理器annotation processor tool

1.降低耦合2.从运行期（反射）转到编译器，提高性能。

#### 实现complier：

1.创建一个java lib 写入注解

```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface XRoute {
    String path();

    String group() default "";
}
```

2.再次创建一个java lib

build配置

```groovy
plugins {
    id 'java-library'
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(":lib-xrouter-annotations")
    implementation 'com.google.auto.service:auto-service:1.0-rc4'
    annotationProcessor 'com.google.auto.service:auto-service:1.0-rc4'
    implementation 'com.squareup:javapoet:1.13.0'
}

tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
```

> 1.AutoService会自动在build/classes输入目录下生成文件META-INF/services/javax.annotation.processing.Processor
>
> 2.SPI , 全称Service Provider Interface，是一种服务发现机制。通过在classPath路径下的MTA-INF/services文件夹查找文件，自动加载文件里所定义的类。
>
> 3.这里还使用到了javapoet

3.实现AbstractProcessor

```java
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.laputa.xrouter.annotations.XField"})
@SupportedOptions({Constant.MODULE_NAME_FOR_APT, Constant.PACKAGE_NAME_FOR_APT})
public class XFieldCompiler extends AbstractProcessor {
	// todo
}
```

4.app使用

```
plugins {
    id 'kotlin-kapt'
}

implementation project(":lib-xrouter-annotations")
kapt project(":lib-xrouter-annotations-compiler")
```

> 没有使用kotlin，用annotationProcessor加载注解处理器

### Javapoet

生成Java源码的工具

> Github：https://github.com/square/javapoet
>
> 模块：https://juejin.im/entry/58fefebf8d6d810058a610de

不要使用kotlin编写，$会产生冲突

也有专门的Kotlinpoet

### Gradle Plugin

#### 实现一个插件步骤：

##### <1> 建一个android lib

##### <2> 创建resources/META-INFO/gradle-plugins/[插件名称].properties文件 

```
implementation-class = com.laputa.plugin.helloworld.HelloWorldPlugin
```

> 这个目录一定要一级级创建，不然找不到插件resources/META-INFO/gradle-plugins/[插件名称].properties。

##### <3> 修改build

```groovy
plugins {
    id 'groovy'
    id 'maven'
}
sourceSets {
    main {
        groovy {
            srcDir 'src/main/groovy'
        }
        java {
            srcDir 'src/main/java'
        }

        resources {
            srcDir 'src/main/resources'
        }
    }
}

sourceCompatibility = "1.8"
targetCompatibility = "1.8"

dependencies {
//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version" // 不然没有asm
    implementation "com.android.tools.build:gradle:$android_tools_build_gradle"
    implementation gradleApi()
    implementation localGroovy()
    implementation "org.ow2.asm:asm:7.0"
    implementation "org.ow2.asm:asm-commons:7.0"
//    implementation thrid_dependencies.asm // todo 为什么无法获取
//    implementation thrid_dependencies.asm_commons
}

repositories {
    mavenCentral()
}

// 发布到本地仓库
group = "com.laputa.plugin.helloworld"
version = "0.0.6"

uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: uri('../repo'))
//            repository(url:uri('C:/Users/xpl/.m2/repository'))
        }
    }
}
```

> 这里使用到了Gradle Transform Api 和 ASM api

##### <4> 继承Plugin

groovy包

```groovy
package com.laputa.plugin.helloworld

import org.gradle.api.Plugin
import org.gradle.api.Project

class HelloWorldPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("************ HelloWorldTransform ************ ")
//        target.task("a_hello_world_test_01"){
//            doFirst {
//                println(" ************ hello world ************ ")
//                println("-> version" + target.version)
//            }
//        }
        target.gradle.addListener(new TimeListener())

    }
}

```

kotlin包

```kotlin
package com.laputa.plugin.logger

import com.android.build.gradle.AppExtension
import com.laputa.plugin.logger.transform.LoggerTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        line(" LoggerPlugin")
        line1("version = ${target.version}")
        target.extensions.apply {
                this.create("laputa_logger",LoggerExtension::class.java)
                getByType(AppExtension::class.java)
                .registerTransform(LoggerTransform(target))
        }

        line1("....end")
    }
}
```

> 这里注册了Gradle Transform Api，对生成的class文件进行处理（添加了方法耗时监控）。

##### <5> Gradle会生成upload/uploadArchives的Task，点击生成插件仓库。

```groovy
classpath 'com.laputa.plugin.timetask:plugin-task-time:0.0.4'
classpath 'com.laputa.plugin:plugin-logger:0.0.2'
```

##### <6> 在工程build里引入classpath，app里引入插件

```groovy
apply plugin: 'laputa.timetask'
apply plugin: 'laputa.logger'
```

##### <7> sync后，会运行插件。插件添加的task也会在app/tasks/other里面出现，点击即可运行

### Gradle Transform

我们编译Android项目时，如果我们想拿到编译时产生的Class文件，并在生成Dex之前做一些处理，我们可以通过编写一个`Transform`来接收这些输入(编译产生的Class文件),并向已经产生的输入中添加一些东西。

1.我们需要对编译class文件做自定义的处理。

2.我们需要读取编译产生的class文件，做一些其他事情，但是不需要修改它

>  Transform:http://google.github.io/android-gradle-dsl/javadoc/2.1/com/android/build/api/transform/Transform.html

> 参考：https://github.com/SusionSuc/AdvancedAndroid/tree/master/gradle

#### Ttansform实例

```kotlin
package com.laputa.plugin.logger.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.laputa.plugin.logger.LoggerExtension
import com.laputa.plugin.logger.line
import com.laputa.plugin.logger.line1
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.FileOutputStream

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerTransform(private val project: Project) : Transform() {
    private var type = 1

    override fun getName(): String = "com.laputa.plugin.logger.transform.LoggerTransform"

    //transform要处理的输入类型,有class,resource,dex
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    /**
     * 输入文件的范围
     * PROJECT 当前工程
     * SUB_PROJECTS 子工程
     * EXTERNAL_LIBRARIES lib
     * LOCAL_DEPS jar
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        line("LoggerTransform a")

        // val type = project.laputa_logger.type
        type = try {
            project.extensions.getByType(LoggerExtension::class.java).type.toInt()
        } catch (e: Throwable) {
            0
        }
        line1(" type = $type")

        val inputs = transformInvocation?.inputs
        val outputProvider = transformInvocation?.outputProvider

        if (!isIncremental) {
            outputProvider?.deleteAll()
        }

        inputs?.forEach { input ->
            line1("input${input}")
            input.directoryInputs.forEach {
                line1("   directoryInput=${it.name}")
                if (it.file.isDirectory) {
                    FileUtils.getAllFiles(it.file).forEach { file ->
                        line1("       file=${file.name}")
                        val name = file.name
                        //过滤出需要的class,将一些基本用不到的class去掉
                        if (name.endsWith(".class") && name != "R.class"
                            && !name.startsWith("R\$") && name != ("BuildConfig.class")
                        ) {
                            val classPath = file.absoluteFile
                            line1("       file#classPath=${classPath}")
                            val cr = ClassReader(file.readBytes())
                            val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            //需要处理的类使用自定义的visitor来处理
                            val visitor = LoggerClassVisitor(type == 0, cw) // [1]
                            cr.accept(visitor, ClassReader.EXPAND_FRAMES)
                            val bytes = cw.toByteArray()
                            val fos = FileOutputStream(classPath)
                            fos.write(bytes)
                            fos.close()
                        }
                    }
                }

                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                FileUtils.copyDirectoryToDirectory(it.file, dest)
            }

            //将jar也加进来,androidx需要这个
            input.jarInputs.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                FileUtils.copyFile(it.file, dest)
            }
        }
        line("LoggerTransform z")
    }
}
```

> 标注[1]使用到了ASM，在方法前后加上自定义字节码。

### ASM

ASM 是一个 Java 字节码操控框架。它能被用来动态生成类或者增强既有类的功能。ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为

> 使用指南：https://github.com/SusionSuc/AdvancedAndroid/blob/master/gradle/ASM4%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97.pdf

#### ASM示例

##### ClassVisitor

```kotlin
package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
//import jdk.internal.org.objectweb.asm.ClassVisitor
//import jdk.internal.org.objectweb.asm.MethodVisitor
//import jdk.internal.org.objectweb.asm.Opcodes
// todo 注意包
import org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor


/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerClassVisitor(private val type: Boolean = true, classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM5, classVisitor) {
    private var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        line1("           -> LoggerClassVisitor::visit")
        line1("           -> className = $className")
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        line1("           -> LoggerClassVisitor::visitMethod name = $name,descriptor=$descriptor type = $type ")
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        // 方式一
        if (type) {
            return LoggerAdviceAdapter(visitMethod, access, name, descriptor)
        }

        // 方式二
        // 过滤方法 ：className 要是MainActivity 并且方法是onCreate或者testAsm
        if (className?.endsWith("MainActivity") == true
            && (name == "onCreate" || name == "testAsm")
        ) {//过滤需要操作的类名和方法名
            return LoggerMethodVisitor(visitMethod)
        }

        return visitMethod
    }
}
```

> 两种实现方式返回MethodVisitor： 1.MethodVisitor 2.AdviceAdapter

##### MethodVisitor

```kotlin
package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerMethodVisitor(methodVisitor: MethodVisitor) :
    MethodVisitor(Opcodes.ASM5, methodVisitor) {

    companion object {
        private const val TAG = "laputa_logger"
    }

    override fun visitCode() {
        super.visitCode()
        line1("               -> LoggerMethodVisitor::visitMethod")
        mv.visitLdcInsn(TAG)
        mv.visitLdcInsn("hello ASM !!! ");
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "i",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        );
        mv.visitInsn(org.objectweb.asm.Opcodes.POP)

    }

    //指令操作,这里可以判断拦截return,并在方法尾部插入字节码
    override fun visitInsn(opcode: Int) {
        println("               -> LoggerMethodVisitor::visitInsn : opcode = $opcode")
        if (opcode == Opcodes.ARETURN || opcode == Opcodes.RETURN) {
            // android.util.Log.i("MainActivity", "ttt run3")
            mv.visitLdcInsn(TAG);
            mv.visitLdcInsn("hello asm after");
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/util/Log",
                "i",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false
            );
            mv.visitInsn(Opcodes.POP);
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun visitEnd() {
        super.visitEnd()
        line1("               -> LoggerMethodVisitor::visitEnd :)")
    }

}

```

##### AdviceAdapter

```kotlin
package com.laputa.plugin.logger.transform

import com.laputa.plugin.logger.line1
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Author by xpl, Date on 2021/4/21.
 */
class LoggerAdviceAdapter(
    methodVisitor: MethodVisitor, access: Int, name: String?,
    descriptor: String?
) : AdviceAdapter(
    Opcodes.ASM5, methodVisitor,
    access,
    name, descriptor
) {

    companion object {
        private const val TAG = "laputa_logger"

        // 注解descriptor
        private const val DESCRIPTOR_ANNOTATION = "Lcom/laputa/plan/logger/Logger;"
        private const val OWNER_LOGGER = "com/laputa/plan/logger/TimeCache"
    }

    private var inject: Boolean = false

    override fun visitCode() {
        super.visitCode()
        line1("               -> LoggerAdviceAdapter::visitCode")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        line1("               -> LoggerAdviceAdapter::visitAnnotation ${descriptor}")
        if (DESCRIPTOR_ANNOTATION == descriptor
        // todo 怎么直接用注解类型？
        // || Type.getDescriptor(com.laputa.annotations.Dog::class.java) == descriptor
        // todo 只能用这种方式么？如何获取注解上的信息？
        // || "Lcom/laputa/annotations/Dog;" == descriptor
        // || Type.getDescriptor(Cat::class.java) == descriptor
        ) {
            inject = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        line1("               -> LoggerAdviceAdapter::onMethodEnter ")
        if (inject) {

            mv.visitFieldInsn(
                GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;"
            )
            mv.visitLdcInsn("========start=========$name==>des:")
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            );

            mv.visitLdcInsn(name);
            mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
            )
            mv.visitMethodInsn(
                INVOKESTATIC,
                OWNER_LOGGER,
                "setStartTime",
                "(Ljava/lang/String;J)V",
                false
            );
        }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        line1("               -> LoggerAdviceAdapter::onMethodExit")
        if (inject) {
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitMethodInsn(
                INVOKESTATIC, OWNER_LOGGER, "setEndTime",
                "(Ljava/lang/String;J)V", false
            );

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(name)
            mv.visitMethodInsn(
                INVOKESTATIC, OWNER_LOGGER, "getCostTime",
                "(Ljava/lang/String;)Ljava/lang/String;", false
            );
            mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false
            );

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("========end=========");
            mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false
            );
        }
    }

    override fun visitEnd() {
        super.visitEnd()

    }
}
```

## 四、说明

* 为什么使用GradlePlugin+ASM

  在编译时，扫描所有类，将符合条件的类收集起来，并通过修改字节码生成注册代码到指定的管理类中，从而实现编译时自动注册的功能，不用再关心项目中有哪些组件类了。
  优点：不会增加新的class，不需要反射，运行时直接调用组件的构造方法；ASM自定义扫描，过滤不必要的类；
  缺点：增加编译时耗时，可忽略

  

* 组件通信 

  CC 、WMRouter https://github.com/luckybilly/AndroidComponentizeLibs

## 五、参考

> ARouter：https://github.com/alibaba/ARouter/blob/master/README_CN.md
>
> 阿里云曦 https://developer.aliyun.com/article/71687
>
> 路由对比：https://github.com/luckybilly/AndroidComponentizeLibs
>
> 自动注解插件：https://github.com/luckybilly/AutoRegister
>
> 几个问题：https://zhuanlan.zhihu.com/p/361025253
>
> 解释器编译器：https://blog.csdn.net/qq_45453185/article/details/103556633
>