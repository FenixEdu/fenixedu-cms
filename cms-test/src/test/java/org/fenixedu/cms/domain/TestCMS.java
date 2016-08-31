package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.groups.ArgumentParser;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.BooleanParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.DateTimeParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.StringParser;
import org.fenixedu.bennu.core.groups.Group;
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
    protected static final String DATETIME_PATTERN = "dd-MM-YYY HH:mm:ss";
    protected static final int DATETIME_EPSILON = 1;

    public static void ensure() throws ClassNotFoundException {
        CustomGroupRegistry.registerCustomGroup((Class<? extends CustomGroup>) Group.anonymous().getClass());
        CustomGroupRegistry.registerCustomGroup((Class<? extends CustomGroup>) Group.anyone().getClass());
        CustomGroupRegistry.registerCustomGroup((Class<? extends CustomGroup>) Group.logged().getClass());
        CustomGroupRegistry.registerCustomGroup((Class<? extends CustomGroup>) Group.nobody().getClass());
        CustomGroupRegistry.registerCustomGroup((Class<? extends CustomGroup>) Class.forName(USER_GROUP));
        CustomGroupRegistry.registerArgumentParser((Class<? extends ArgumentParser<?>>) Class.forName(USER_PARSER));
        CustomGroupRegistry.registerArgumentParser(BooleanParser.class);
        CustomGroupRegistry.registerArgumentParser(StringParser.class);
        CustomGroupRegistry.registerArgumentParser(DateTimeParser.class);
        loadComponents();
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void initObjects() throws ClassNotFoundException {
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
