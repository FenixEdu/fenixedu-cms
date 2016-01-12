package org.fenixedu.cms.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.cms.domain.component.Component;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(Component.class)
public class ComponentAdapter implements JsonAdapter<Component> {

    @Override
    public Component create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public Component update(JsonElement arg0, Component arg1, JsonBuilder arg2) {
        return null;
    }

    @Override
    public JsonElement view(Component component, JsonBuilder ctx) {
        return Component.forType(component.getClass().getName()).toJson();
    }
}
