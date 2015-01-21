package org.fenixedu.cms.domain;

import static org.fenixedu.cms.domain.Sanitization.sanitize;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SanitizationTest {

    @Test
    public void testBasicHtml() {
        checkNoChange("Hello world!");
    }

    @Test
    public void testLinks() {
        checkNoChange("<a href=\"xpto\">Hello</a>");
    }

    @Test
    public void testStyles() {
        checkNoChange("<div class=\"jumbotron\">Hello</div>");
        checkNoChange("<table class=\"jumbotron\"></table>");
    }

    @Test
    public void testEmail() {
        checkNoChange("<a href=\"mailto:hello@fenixedu.org\">hello@fenixedu.org</a>");
    }

    private void checkNoChange(String str) {
        assertEquals(str, sanitize(str));
    }

}
