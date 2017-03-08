/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.rendering;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.portal.servlet.LazyForTokenParser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.util.*;

public class CMSExtensions extends AbstractExtension {
    public class LengthFilter implements Filter {
        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("collection");
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            if (input != null) {
                if (input.getClass().isArray()) {
                    return Array.getLength(input);
                } else if (input instanceof Collection) {
                    return ((Collection<?>) input).size();
                }
            }
            return 0;
        }
    }

    public class MapEntriesFunction implements Function {
        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("map");
        }

        @Override
        public Object execute(Map<String, Object> args) {
            if (args.get("map") != null && args.get("map") instanceof Map) {
                return ((Map<?, ?>) args.get("map")).entrySet();
            }
            return Sets.newLinkedHashSet();
        }
    }

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

    public class IterableHeadFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("iterable");
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            return input instanceof Iterable ? Iterables.getFirst((Iterable<?>) input, null) : null;
        }

    }

    public class IterableTailFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("iterable");
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            return input instanceof Iterable ? Iterables.skip((Iterable<?>) input, 1) : null;
        }

    }

    public class TitleFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            if (input != null && input instanceof String) {
                return capitalizeFully((String) input);
            } else {
                return "";
            }
        }

        private String capitalizeFully(String str) {
            char[] chars = str.toLowerCase().toCharArray();
            StringBuffer buffer = new StringBuffer(chars.length);
            boolean capitalizeNext = true;
            for (char ch : chars) {
                if (Character.isWhitespace(ch)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }
            return buffer.toString();
        }
    }

    public class ReverseFilter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            List<Object> list = new ArrayList<Object>();
            if (input != null && input instanceof List) {
                list.addAll((List<?>) input);
                Collections.reverse(list);
            }
            return list;
        }
    }

    private static class I18NFunction implements Function {
        final List<String> variableArgs = ImmutableList.of("arg0", "arg1", "arg2", "arg3", "arg4", "arg5");

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("bundle", "key", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5");
        }

        @Override
        public Object execute(Map<String, Object> args) {
            String bundle = (String) args.get("bundle");
            String key = args.get("key").toString();
            return BundleUtil.getString(bundle, key, arguments(args));
        }

        public String[] arguments(Map<String, Object> args) {
            List<String> values = new ArrayList<>();
            for (String variableArg : variableArgs) {
                if (args.containsKey(variableArg) && args.get(variableArg) instanceof String) {
                    values.add((String) args.get(variableArg));
                }
            }
            return values.toArray(new String[] {});
        }
    }

    private static class MapValueFunction implements Function {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("map", "key");
        }

        @Override
        public Object execute(Map<String, Object> args) {
            Object mapObject = args.get("map");
            Object keyObject = args.get("key");
            Preconditions.checkArgument(mapObject != null && keyObject != null, "Please specify non empty 'map' and 'key'");
            Preconditions.checkArgument(mapObject instanceof Map, "The first argument must be of type " + Map.class.getName());
            return ((Map<?, ?>) mapObject).get(keyObject);
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
        Map<String, Filter> map = new HashMap<String, Filter>();
        map.put("formatDate", new DateTimeFormaterFilter());
        map.put("title", new TitleFilter());
        map.put("head", new IterableHeadFilter());
        map.put("tail", new IterableTailFilter());
        map.put("length", new LengthFilter());
        map.put("reverse", new ReverseFilter());
        return ImmutableMap.copyOf(map);
    }

    public class InTest implements Test {

        @Override
        public List<String> getArgumentNames() {
            return ImmutableList.of("collection");
        }

        @Override
        public boolean apply(Object input, Map<String, Object> args) {
            return ((Collection<?>) args.get("collection")).contains(input);
        }

    }

    @Override
    public Map<String, Test> getTests() {
        Map<String, Test> tests = new HashMap<>();
        tests.put("in", new InTest());
        return tests;
    }

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("i18n", new I18NFunction());
        functions.put("range", new RangeFunction());
        functions.put("entries", new MapEntriesFunction());
        functions.put("getValue", new MapValueFunction());
        return functions;
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        return Collections.singletonList(new LazyForTokenParser());
    }

}
