package org.fenixedu.cms.rendering;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.AbstractNode;

public class MenuTreeNode extends AbstractNode {
    
    private void compileNode(){
        
               
    }
    
    @Override
    public void compile(Compiler compiler) {
        compiler.newline().write("List<String> bits = new ArrayList<String>();");        
        compiler.write("context.pushScope();").newline();
    }
    

}
