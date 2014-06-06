package org.fenixedu.bennu.cms.rendering;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public class CMSExtensions implements Extension {
    public class DateTimeFormaterFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("format");
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            if (input instanceof DateTime) {
                String pattern = (String) args.get("format");
                if (pattern == null) {
                    pattern = "MMMM d, Y, H:m";
                }
                DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
                return fmt.print((DateTime) input);
            }
            return "";
        }

    }

    public static class RecursiveTreeToken extends AbstractTokenParser {

        @Override
        public String getTag() {
            return "recursetree";
        }

        @Override
        public RenderableNode parse(Token token) throws ParserException {

            return null;
        }

    }

    public static class RangeFunction implements Function {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("from", "to");
        }

        @Override
        public Object execute(Map<String, Object> args) {
            Range<Integer> range = Range.closedOpen((Integer) args.get("from"), (Integer) args.get("to"));
            return ContiguousSet.create(range, DiscreteDomain.integers());
        }

    }

    @Override
    public Map<String, Filter> getFilters() {
        return ImmutableMap.of("formatDate", (Filter) new DateTimeFormaterFilter());
    }

    @Override
    public Map<String, Test> getTests() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Function> getFunctions() {
        return ImmutableMap.of("range", (Function) new RangeFunction());
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BinaryOperator> getBinaryOperators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UnaryOperator> getUnaryOperators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NodeVisitor> getNodeVisitors() {
        // TODO Auto-generated method stub
        return null;
    }

}
