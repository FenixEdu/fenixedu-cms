package org.fenixedu.cms.domain.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentContextProvider.EmptyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Describes a {@link Component}, containing all the necessary information to
 * dynamically instantiate them.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class ComponentDescriptor {

    private static final Logger logger = LoggerFactory.getLogger(ComponentDescriptor.class);

    private final Class<?> type;
    private final String name;
    private final boolean stateless;
    private final Map<String, ComponentParameterDescriptor> parameters = new HashMap<>();
    private final Constructor<?> ctor;
    private final Method filter;

    ComponentDescriptor(Class<?> type) {
        this.type = type;
        ComponentType ann = type.getAnnotation(ComponentType.class);
        this.name = ann.name();
        this.stateless = CMSComponent.class.isAssignableFrom(type);
        this.filter = ClassUtils.getMethodIfAvailable(type, "supportsSite", Site.class);
        if (!this.stateless) {
            this.ctor = getCustomCtor(type);
            for (Parameter param : ctor.getParameters()) {
                parameters.put(param.getName(), new ComponentParameterDescriptor(param));
            }
        } else {
            this.ctor = null;
        }
    }

    private Constructor<?> getCustomCtor(Class<?> type) {
        for (Constructor<?> ctor : type.getDeclaredConstructors()) {
            if (ctor.isAnnotationPresent(DynamicComponent.class)) {
                return ctor;
            }
        }
        return ClassUtils.getConstructorIfAvailable(type);
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isStateless() {
        return stateless;
    }

    public boolean isForSite(Site site) {
        try {
            return filter == null ? true : (boolean) filter.invoke(null, site);
        } catch (Exception e) {
            logger.warn("Exception when running component site filter, returning true!", e);
            return true;
        }
    }

    private class ComponentParameterDescriptor {

        private final ComponentParameter ann;
        private final ParameterType type;
        private final Parameter parameter;
        private final ComponentContextProvider<Object> provider;

        public ComponentParameterDescriptor(Parameter parameter) {
            this.parameter = parameter;
            this.ann = parameter.getAnnotation(ComponentParameter.class);
            this.type = getType(parameter.getType());
            this.provider = findProviderMethod();
        }

        @SuppressWarnings("unchecked")
        private ComponentContextProvider<Object> findProviderMethod() {
            try {
                return (ComponentContextProvider<Object>) ann.provider().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Exception while creating provider", e);
            }
        }

        private ParameterType getType(Class<?> parameterClass) {
            if (String.class == parameterClass) {
                return ParameterType.STRING;
            }
            if (Number.class.isAssignableFrom(parameterClass)) {
                return ParameterType.NUMBER;
            }
            if (Enum.class.isAssignableFrom(parameterClass)) {
                return ParameterType.ENUM;
            }
            if (DomainObject.class.isAssignableFrom(parameterClass)) {
                return ParameterType.DOMAIN_OBJECT;
            }
            if (Boolean.class.isAssignableFrom(parameterClass)) {
                return ParameterType.BOOLEAN;
            }
            throw new IllegalArgumentException("ComponentParameter parameter is not supported!");
        }

        public JsonObject toJson(Page page) {
            JsonObject json = new JsonObject();
            json.addProperty("key", parameter.getName());
            json.addProperty("title", ann.value());
            json.addProperty("type", type.name());
            json.addProperty("required", ann.required());
            JsonArray values = new JsonArray();

            Iterable<?> possibleValues = getPossibleValues(page);

            for (Object value : possibleValues) {
                JsonObject obj = new JsonObject();
                obj.addProperty("value", type.stringify(value));
                obj.addProperty("label", provider.present(value));
                values.add(obj);
            }

            json.add("values", values);
            return json;
        }

        private Iterable<?> getPossibleValues(Page page) {
            if (parameter.getType().isEnum() && provider instanceof EmptyProvider) {
                return Arrays.asList(parameter.getType().getEnumConstants());
            } else {
                return provider.provide(page);
            }
        }

        public boolean isRequired() {
            return ann.required();
        }
    }

    private static enum ParameterType {
        STRING,

        NUMBER {
            @Override
            public Object coerce(Class<?> type, String value) throws Exception {
                return ClassUtils.getMethodIfAvailable(type, "valueOf", String.class).invoke(null, value);
            }
        },

        BOOLEAN {
            @Override
            public Object coerce(Class<?> type, String value) {
                return Boolean.valueOf(value);
            }
        },

        ENUM {
            @Override
            public String stringify(Object object) {
                return ((Enum<?>) object).name();
            }

            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Object coerce(Class<?> type, String value) {
                return Enum.valueOf((Class) type, value);
            }
        },

        DOMAIN_OBJECT {
            @Override
            public String stringify(Object object) {
                return ((DomainObject) object).getExternalId();
            }

            @Override
            public Object coerce(Class<?> type, String value) {
                return FenixFramework.getDomainObject(value);
            }
        };

        public String stringify(Object object) {
            return String.valueOf(object);
        };

        public Object coerce(Class<?> type, String value) throws Exception {
            return value;
        }

    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.getName());
        json.addProperty("name", name);
        json.addProperty("stateless", stateless);
        return json;
    }

    public JsonArray getParameterDescription(Page page) {
        JsonArray array = new JsonArray();
        parameters.values().stream().forEach(param -> array.add(param.toJson(page)));
        return array;
    }

    public Component instantiate(JsonObject params) throws Exception {
        Object[] arguments = new Object[ctor.getParameterCount()];
        for (int i = 0; i < ctor.getParameterCount(); i++) {
            Parameter parameter = ctor.getParameters()[i];
            ComponentParameterDescriptor descriptor = parameters.get(parameter.getName());
            String value = params.has(parameter.getName()) ? params.get(parameter.getName()).getAsString() : null;

            Class<?> type = parameter.getType();
            Object coercedValue = descriptor.type.coerce(type, value);

            if (coercedValue == null && descriptor.isRequired()) {
                throw new IllegalArgumentException("Required parameter " + parameter.getName() + " was not found!");
            }

            arguments[i] = coercedValue;
        }
        return (Component) ctor.newInstance(arguments);
    }

}
