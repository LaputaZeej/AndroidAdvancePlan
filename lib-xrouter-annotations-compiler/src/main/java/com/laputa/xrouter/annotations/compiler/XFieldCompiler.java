package com.laputa.xrouter.annotations.compiler;

import com.google.auto.service.AutoService;
import com.laputa.xrouter.annotations.XField;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.laputa.xrouter.annotations.compiler.Constant.MODULE_NAME_FOR_APT;

/**
 * Author by xpl, Date on 2021/4/17.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.laputa.xrouter.annotations.XField"})
@SupportedOptions({Constant.MODULE_NAME_FOR_APT, Constant.PACKAGE_NAME_FOR_APT})
public class XFieldCompiler extends AbstractProcessor {
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    private String moduleName;
    private String packageNameForAPT;

    // Activity to List<Field> 除了Activity还可以支持Fragment、接口等
    private final Map<TypeElement, List<Element>> fieldMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        println("<<<<<<<<<<<<<<<<<<<<<<< XFieldCompiler::init >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        //displayOptions();
        moduleName = processingEnv.getOptions().get(MODULE_NAME_FOR_APT);
        packageNameForAPT = processingEnv.getOptions().get(Constant.PACKAGE_NAME_FOR_APT);
        println("-> moduleName = " + moduleName + " , " + "packageNameForAPT = " + packageNameForAPT);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        println("<<<<<<<<<<<<<<<<<<<<<<< XFieldCompiler::process >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        line("scan annotations ...");
        if (set.isEmpty()) {
            line("annotations is empty");
            return false;
        }
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XField.class);
        if (elements.isEmpty()) {
            line("elements is empty");
            return false;
        }
        TypeElement activityTypeElement = elementUtils.getTypeElement(Constant.ANDROID_ACTIVITY);
        // 把属性都放进去，key为注解的类.class(即Activity)值为属性Element List
        for (Element element : elements) {
            // 当前节点的父节点-> Activity
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            if (!typeUtils.isSubtype(enclosingElement.asType(), activityTypeElement.asType())) {
                throw new RuntimeException("XField当前只能用在Activity中");
            }
            // 把属性都添加进去
            if (fieldMap.containsKey(enclosingElement)) {
                fieldMap.get(enclosingElement).add(element);
            } else {
                List<Element> fields = new ArrayList<>();
                fields.add(element);
                fieldMap.put(enclosingElement, fields);
            }
        }//end
        if (fieldMap.isEmpty()) {
            return false;
        }
        line("scan annotations end. size = " + fieldMap.size());
        // 生成RouterField接口实现类
        line("create field ...");
        line("  build method");
        // 参数Object target
        final String targetVar = "target";
        final String activityVar = "activity";
        final String bundleVar = "bundle";
        ParameterSpec target = ParameterSpec.builder(TypeName.OBJECT, targetVar).build();
        for (Map.Entry<TypeElement, List<Element>> entry : fieldMap.entrySet()) {
            TypeElement key = entry.getKey(); // 即Activity.class
            ClassName className = ClassName.get(key); // 即Activity.class
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.INTERFACE_METHOD_ROUTER_FIELD)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(target);
            // MainActivity activity = (MainActivity)target
            methodBuilder.addStatement("$T $N = ($T)$N", className, activityVar, className, targetVar);
            // 将属性一一从bundle里取出
            // activity.getIntent().getStringExtra("name")..
            List<Element> fields = entry.getValue();
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            // Bundle bundle = activity.getIntent().getExtras();
            methodBuilder.addStatement("$T $N = $N.getIntent().getExtras()",
                    elementUtils.getTypeElement(Constant.ANDROID_BUNDLE),
                    bundleVar,
                    activityVar
            );
            // if(bundle!=null)
            methodBuilder.beginControlFlow("if($N!=null)", bundleVar);
            // activity.name = activity.getIntent().getExtras().getString("name1");
            // todo 可以使用反射？防止获取不到属性？
            for (Element field : fields) {
                TypeMirror typeMirror = field.asType();
                TypeKind kind = typeMirror.getKind();
                String fieldName = field.getSimpleName().toString();
                String annotationValue = field.getAnnotation(XField.class).name();
                String finalString = "$N.$N = $N.";

                annotationValue = (annotationValue.trim().isEmpty()) ? fieldName : annotationValue;
                switch (kind) {
                    case INT:
                        finalString += "getInt($S,0)";
                        break;
                    case BOOLEAN:
                        finalString += "getBoolean($S,false)";
                        break;
                    case LONG:
                        finalString += "getLong($S,0)";
                        break;
                    case FLOAT:
                        finalString += "getFloat($S,0F)";
                        break;
                    default:
                        // todo 更多情况 比如fragment 接口等 做到自动注入功能
                        if (typeMirror.toString().equalsIgnoreCase("java.lang.String")) {
                            finalString += "getString($S)";
                        }
                        break;
                }
                methodBuilder.addStatement(finalString, activityVar, fieldName, bundleVar, annotationValue);
            }//end
            methodBuilder.endControlFlow();
            line("  build class...");
            TypeElement fieldTypeElement = elementUtils.getTypeElement(Constant.INTERFACE_ROUTER_FIELD_PACKAGE_NAME);
            String finalClassName = key.getSimpleName() + Constant.PREFIX_ROUTER_FILED;
            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ClassName.get(fieldTypeElement))
                    .addMethod(methodBuilder.build())
                    .build();
            line("  build java file...");
            try {
                JavaFile.builder(packageNameForAPT, typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        line("build create field end.");
        return true;
    }

    private void line(String msg) {
        println("-> " + msg);
    }

    private void println(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, "\n" + msg + "\n");
    }

    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, "\n" + msg + "\n");
    }
}
