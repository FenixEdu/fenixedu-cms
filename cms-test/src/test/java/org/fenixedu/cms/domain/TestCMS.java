package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.*;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.BooleanParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.DateTimeParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.StringParser;
import org.fenixedu.cms.domain.component.*;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.BeforeClass;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.stream.Stream;

public class TestCMS {

    protected static final String DATETIME_PATTERN = "dd-MM-YYY HH:mm:ss";
    protected static final int DATETIME_EPSILON = 1;

    public static void ensure() {
        CustomGroupRegistry.registerCustomGroup(AnonymousGroup.class);
        CustomGroupRegistry.registerCustomGroup(AnyoneGroup.class);
        CustomGroupRegistry.registerCustomGroup(LoggedGroup.class);
        CustomGroupRegistry.registerCustomGroup(NobodyGroup.class);
        CustomGroupRegistry.registerCustomGroup(UserGroup.class);
        CustomGroupRegistry.registerArgumentParser(UserGroup.UserArgumentParser.class);
        CustomGroupRegistry.registerArgumentParser(BooleanParser.class);
        CustomGroupRegistry.registerArgumentParser(StringParser.class);
        CustomGroupRegistry.registerArgumentParser(DateTimeParser.class);
        loadComponents();
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() {
        ensure();
    }

    protected boolean equalDates(DateTime expected, DateTime result) {
        return equalDates(expected, result, DATETIME_EPSILON);
    }

    protected boolean equalDates(DateTime expected, DateTime result, int eps) {
        if (expected == null && result == null) {
            return true;
        }
        int diff = Seconds.secondsBetween(expected, result).getSeconds();
        return Math.abs(diff) <= eps;
    }

    private static void loadComponents() {
        Stream.of(StaticPost.class, ListCategoryPosts.class, ViewPost.class).forEach(type -> {
            if (type.isAnnotationPresent(ComponentType.class)) {
                Component.register(type);
            }
        });
    }
}
