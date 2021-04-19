package com.laputa.xrouter.annotations.compiler;

import com.google.auto.service.AutoService;
import com.laputa.xrouter.annotations.Constant;
import com.laputa.xrouter.annotations.RouterBean;
import com.laputa.xrouter.annotations.XRoute;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Author by xpl, Date on 2021/4/17.
 */
@AutoService(Processor.class)
//@SupportedAnnotationTypes({"com.laputa.xrouter.annotations.XRoute"})//注解
//@SupportedOptions({MODULE_NAME_FOR_APT, PACKAGE_NAME_FOR_APT, "test"}) // gradle传参
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class XRouteCompiler extends AbstractProcessor {
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    private String moduleName;
    private String packageNameForAPT;
    /* group to paths */
    private Map<String, List<RouterBean>> pathMap = new HashMap<>();
    /* group to ClassName */
    private Map<String, String> groupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        println("<<<<<<<<<<<<<<<<<<<<<<< XRouteCompiler::init >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        //displayOptions();
        moduleName = processingEnv.getOptions().get(Constant.MODULE_NAME_FOR_APT);
        packageNameForAPT = processingEnv.getOptions().get(Constant.PACKAGE_NAME_FOR_APT);
        println("-> moduleName = " + moduleName + " , " + "packageNameForAPT = " + packageNameForAPT);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        println("<<<<<<<<<<<<<<<<<<<<<<< XRouteCompiler::process >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        line("scan annotations ...");
        if (set.isEmpty()) {
            line("annotations is empty");
            return false;
        }
        TypeMirror activityTypeMirror = elementUtils.getTypeElement(Constant.ANDROID_ACTIVITY).asType();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XRoute.class);
        // 扫描所有注解信息，然后加入pathMap，等待生成对应的RouterPath
        for (Element element : elements) {
            XRoute annotation = element.getAnnotation(XRoute.class);
            // 构建路由基本信息RouterBean
            RouterBean routerBean;
            // 判断被标注的类是否是支持，目前只支持Activity子类
            TypeMirror typeMirror = element.asType();
            TypeName typeName = TypeName.get(element.asType());
            line(typeName.toString());
            if (typeUtils.isSubtype(typeMirror, activityTypeMirror)) {
                routerBean = new RouterBean();
                routerBean.setPath(annotation.path());
                routerBean.setGroup(annotation.group());
                routerBean.setElement(element);
                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else {
                throw new RuntimeException("XRoute注解target类型不支持");
            }
            // 校验注解信息，并解析出group，
            if (validatePath(routerBean)) {
                // 将path放入pathMap，形成group与path一对多的关系，等待javaPoet生成代码。
                List<RouterBean> routerBeans = pathMap.get(routerBean.getGroup());
                if (routerBeans == null) {
                    routerBeans = new ArrayList<>();
                    routerBeans.add(routerBean);
                    pathMap.put(routerBean.getGroup(), routerBeans);
                } else {
                    routerBeans.add(routerBean);
                }
            }
        }
        line("scan annotations end. pathMap.size = " + pathMap.size());
        // RouterPath类
        TypeElement routerPathTypeElement = elementUtils.getTypeElement(Constant.INTERFACE_ROUTER_PATH_PACKAGE_NAME);
        line("create path...");
        try {
            createPath(routerPathTypeElement);
        } catch (IOException e) {
            error(e.getMessage());
        }
        line("create path end.");
        line("create group...");
        // RouterGroup类
        TypeElement routerGroupTypeElement = elementUtils.getTypeElement(Constant.INTERFACE_ROUTER_GROUP_PACKAGE_NAME);
        try {
            createGroup(routerGroupTypeElement, routerPathTypeElement);
        } catch (IOException e) {
            error(e.getMessage());
        }
        line("create group end.");
        return true;
    }

    // 生成RouterPath接口实现类
    private void createPath(TypeElement routerPathTypeElement) throws IOException {
        if (pathMap.isEmpty()) {
            line("  pathMap is empty.");
            return;
        }

        // 1.构建方法
        line("  build method...");
        // 构建类型Map<String,RouterBean>
        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterBean.class));
        final String pathMapVar = "pathMap";
        for (Map.Entry<String, List<RouterBean>> entry : pathMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.INTERFACE_METHOD_ROUTER_PATH)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType)
                    //  Map<String,RouterBean> pathMap = new HashMap<>();
                    .addStatement(
                            "$T<$T,$T> $N = new $T<>()",
                            ClassName.get(Map.class),  // Map
                            ClassName.get(String.class), // String
                            ClassName.get(RouterBean.class),// RouterBean
                            pathMapVar,
                            ClassName.get(HashMap.class)
                    );
            // 生成代码将RouterBean添加到pathMap中
            List<RouterBean> value = entry.getValue();
            int index = 0;
            for (RouterBean routerBean : value) {
                String beanVar = "bean" + index;
                // RouterBean bean = new RouterBean();
                methodBuilder.addStatement("$T $N = new $T()",
                        ClassName.get(RouterBean.class),
                        beanVar,
                        ClassName.get(RouterBean.class)
                );
                // bean.setPath(path);
                methodBuilder.addStatement("$N.setPath($S)", beanVar, routerBean.getPath());
                // bean.setGroup(group);
                methodBuilder.addStatement("$N.setGroup($S)", beanVar, routerBean.getGroup());
                // bean.setType(Type.ACTIVITY);
                methodBuilder.addStatement("$N.setType($T.$L)", beanVar, RouterBean.Type.class, routerBean.getType());
                // bean.setAnnotationClass(cls);
                methodBuilder.addStatement("$N.setAnnotationClass($T.class)", beanVar, ClassName.get((TypeElement) routerBean.getElement()));
                // pathMap.put("/app/MainActivity",routerBean)
                methodBuilder.addStatement("$N.put($S,$N)", pathMapVar, routerBean.getPath(), beanVar);
                index++;
            }
            // return pathMap
            methodBuilder.addStatement("return $N", pathMapVar);
            line("  build class...");
            // 生成的类名XRouter$$Path$$GroupName，比如login模块就生成XRouter$$Path$$login.class implement RouterPath
            String finalClassName = Constant.PREFIX_ROUTER_PATH + entry.getKey();
            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addSuperinterface(ClassName.get(routerPathTypeElement))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            line("  build java file...");
            JavaFile.builder(packageNameForAPT, typeSpec).build().writeTo(filer);
            // 记得将生成的类添加到groupMap
            groupMap.put(entry.getKey(), finalClassName);
        }
        line( "  build java file total size = " + groupMap.size());
    }

    //生成RouterGroup接口实现类
    private void createGroup(TypeElement routerGroupTypeElement, TypeElement routerPathTypeElement) throws IOException {
        if (groupMap.isEmpty()) {
            line("  group is empty.");
            return;
        }
        line("  build method...");
        //  Map<String,Class<? extends RouterPath>>
        ParameterizedTypeName returnType = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // String
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(routerPathTypeElement)) // ? extends RouterPath
                ) // Class<?  extends RouterPath
        );

        final String groupMapVar = "groupMap";
        // 一个group对应一个RouterGroup.class
        //map.put(key,value);
        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            // method
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(Constant.INTERFACE_METHOD_ROUTER_GROUP)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType);
            // Map<String,Class<? extends RouterPath> map = new HashMap<>();
            methodSpecBuilder.addStatement(
                    "$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(routerPathTypeElement))),
                    groupMapVar,
                    ClassName.get(HashMap.class)
            );
            methodSpecBuilder.addStatement(
                    "$N.put($S,$T.class)",
                    groupMapVar,
                    entry.getKey(),
                    ClassName.get(packageNameForAPT, entry.getValue())//包名+类名确定一个类
            );
            // return map
            methodSpecBuilder.addStatement("return $N", groupMapVar);
            line("  build class");
            // 生成的类名XRouter$$Group$$GroupName，比如login模块就生成XRouter$$Group$$login.class implement RouterGroup
            String finalClassName = Constant.PREFIX_ROUTER_GROUP + entry.getKey();
            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addSuperinterface(ClassName.get(routerGroupTypeElement))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpecBuilder.build())
                    .build();
            line("  build java file");
            JavaFile.builder(packageNameForAPT, typeSpec).build().writeTo(filer);
        }

    }

    private boolean validatePath(RouterBean routerBean) {
        String path = routerBean.getPath();
        try {
            // todo 校验 暂时未处理
            String group = path.substring(1, path.indexOf("/", 1));
            routerBean.setGroup(group);
            return true;
        } catch (Exception e) {
            error(e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        String canonicalName = XRoute.class.getName();
        Set<String> set = new HashSet<>();
        set.add(canonicalName);
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> set = new HashSet<>();
        set.add(Constant.MODULE_NAME_FOR_APT);
        set.add(Constant.PACKAGE_NAME_FOR_APT);
        return set;
    }

    private void displayOptions() {
        Map<String, String> options = processingEnv.getOptions();
        for (Map.Entry<String, String> next : options.entrySet()) {
            println(next.getKey() + "-->" + next.getValue());
        }
    }

    private  void line(String msg) {
        println("-> " + msg);
    }

    private void println(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, "\n" + msg + "\n");
    }

    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, "\n" + msg + "\n");
    }
}
