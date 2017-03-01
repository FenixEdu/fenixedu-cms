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
    private static final String USER_GROUP = "org.fenixedu.bennu.core.groups.UserGroup";
    private static final String USER_PARSER = "org.fenixedu.bennu.core.groups.UserGroup$UserArgumentParser";
    private static Boolean run= false;
    protected static final String DATETIME_PATTERN = "dd-MM-YYY HH:mm:ss";
    protected static final int DATETIME_EPSILON = 1;

    public static void ensure() throws ClassNotFoundException {
        ManualGroupRegister.ensure();
        loadComponents();
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() throws ClassNotFoundException {
        if(!run){
            ensure();
        }
        run = true;
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
