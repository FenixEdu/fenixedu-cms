package org.fenixedu.bennu.cms.rendering;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class MenuTreeNode extends AbstractRenderableNode {
    
    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
        writer.write("List<String> bits = new ArrayList<String>();");
        writer.write("context.pushScope();");
    }

    @Override
    public void accept(NodeVisitor visitor) {
        // TODO Auto-generated method stub

    }
    

}
