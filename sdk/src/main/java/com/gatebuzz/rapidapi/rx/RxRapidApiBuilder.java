package com.gatebuzz.rapidapi.rx;

import com.gatebuzz.rapidapi.rx.internal.CallConfiguration;
import com.gatebuzz.rapidapi.rx.internal.CallConfigurationFactory;
import com.gatebuzz.rapidapi.rx.internal.CallHandler;
import com.gatebuzz.rapidapi.rx.internal.CallHandlerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RxRapidApiBuilder {

    private final CallHandlerFactory callHandlerFactory;
    private final Map<String, String> classLevelDefaults = new HashMap<>();
    private final Map<String, Map<String, String>> methodLevelDefaults = new HashMap<>();
    private Class<?> interfaceClass;
    private String project;
    private String key;
    private String apiPackage;

    public RxRapidApiBuilder() {
        this(new CallHandlerFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T newInstance(Class<?> interfaceClass, Map<String, CallConfiguration> configurationMap) {
                return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                        new Class[]{interfaceClass}, new CallHandler(configurationMap));
            }
        });
    }

    protected RxRapidApiBuilder(CallHandlerFactory callHandlerFactory) {
        this.callHandlerFactory = callHandlerFactory;
    }

    @SuppressWarnings("unchecked")
    public static <I> I from(Class<I> interfaceClass) {
        return new RxRapidApiBuilder().endpoint(interfaceClass).build();
    }

    public RxRapidApiBuilder endpoint(Class<?> endpoint) {
        this.interfaceClass = endpoint;
        return this;
    }

    public RxRapidApiBuilder application(String project, String key) {
        this.project = project;
        this.key = key;
        return this;
    }

    public RxRapidApiBuilder apiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
        return this;
    }

    public RxRapidApiBuilder defaultValue(String key, String value) {
        classLevelDefaults.put(key, value);
        return this;
    }

    public RxRapidApiBuilder defaultValue(String method, String key, String value) {
        if (!methodLevelDefaults.containsKey(method)) {
            methodLevelDefaults.put(method, new HashMap<>());
        }
        Map<String, String> defaults = methodLevelDefaults.get(method);
        defaults.put(key, value);
        return this;
    }

    public RxRapidApiBuilder defaultValues(HashMap<String, String> defaultValues) {
        classLevelDefaults.putAll(defaultValues);
        return this;
    }

    public RxRapidApiBuilder defaultValues(String method, HashMap<String, String> defaultValues) {
        if (!methodLevelDefaults.containsKey(method)) {
            methodLevelDefaults.put(method, new HashMap<>());
        }
        Map<String, String> defaults = methodLevelDefaults.get(method);
        defaults.putAll(defaultValues);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T build() {
        Application applicationAnnotation = interfaceClass.getAnnotation(Application.class);
        ApiPackage apiPackageAnnotation = interfaceClass.getAnnotation(ApiPackage.class);
        DefaultParameters defaultParametersAnnotation = interfaceClass.getAnnotation(DefaultParameters.class);

        Map<String, CallConfiguration> callConfigurationMap = new HashMap<>();
        for (Method method : interfaceClass.getMethods()) {
            Application methodAppAnnotation = method.getAnnotation(Application.class);
            ApiPackage methodApiPackageAnnotation = method.getAnnotation(ApiPackage.class);
            DefaultParameters methodDefaultParametersAnnotation = method.getAnnotation(DefaultParameters.class);

            CallConfiguration configuration = CallConfigurationFactory.newInstance(
                    applicationAnnotation, methodAppAnnotation,
                    apiPackageAnnotation, methodApiPackageAnnotation,
                    method, project, key, apiPackage, classLevelDefaults,
                    methodLevelDefaults.get(method.getName()),
                    defaultParametersAnnotation, methodDefaultParametersAnnotation);
            callConfigurationMap.put(method.getName(), configuration);
        }

        return (T) callHandlerFactory.newInstance(interfaceClass, callConfigurationMap);
    }
}
