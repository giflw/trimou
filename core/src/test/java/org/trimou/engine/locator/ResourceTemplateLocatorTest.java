package org.trimou.engine.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;

/**
 *
 * @author Martin Kouba
 */
public class ResourceTemplateLocatorTest extends PathTemplateLocatorTest {

    @Test
    public void testLocator() throws IOException {

        TemplateLocator locator = new ResourceTemplateLocator(1,
                "locator/file", "foo");

        // Just to init the locator
        MustacheEngineBuilder.newBuilder().addTemplateLocator(locator).build();

        Set<String> ids = locator.getAllIdentifiers();
        assertEquals(5, ids.size());
        assertTrue(ids.contains("index"));
        assertTrue(ids.contains("home"));
        assertTrue(ids.contains("foo"));
        assertTrue(ids.contains("sub/bar"));
        assertTrue(ids.contains("sub/subsub/qux"));

        assertEquals("{{foo}}", read(locator.locate("index")));
        assertEquals("bar", read(locator.locate("home")));
        assertEquals("foo", read(locator.locate("foo")));
        assertEquals("{{foo}}", read(locator.locate("sub/bar")));
        assertEquals("{{bar}}", read(locator.locate("sub/subsub/qux")));
    }

    @Test
    public void testLocatorNoSuffix() throws IOException {

        TemplateLocator locator = new ResourceTemplateLocator(1,
                "locator/file");

        // Just to init the locator
        MustacheEngineBuilder.newBuilder().addTemplateLocator(locator).build();

        Set<String> ids = locator.getAllIdentifiers();
        assertEquals(7, ids.size());
        assertTrue(ids.contains("index.foo"));
        assertTrue(ids.contains("home.foo"));
        assertTrue(ids.contains("foo.foo"));
        assertTrue(ids.contains("detail.html"));
        assertTrue(ids.contains("encoding.html"));
        assertTrue(ids.contains("sub/bar.foo"));
        assertTrue(ids.contains("sub/subsub/qux.foo"));

        assertEquals("{{foo}}", read(locator.locate("index.foo")));
        assertEquals("bar", read(locator.locate("home.foo")));
        assertEquals("foo", read(locator.locate("foo.foo")));
        assertEquals("<html/>", read(locator.locate("detail.html")));
        assertEquals("{{foo}}", read(locator.locate("sub/bar.foo")));
        assertEquals("{{bar}}", read(locator.locate("sub/subsub/qux.foo")));
    }

    @Test
    public void testEncoding() throws IOException {
        TemplateLocator locator = new ResourceTemplateLocator(1,
                "locator/file", "html");
        // Just to init the locator
        MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING,
                        "windows-1250").addTemplateLocator(locator).build();
        assertEquals("Hurá ěščřřžžýá!", read(locator.locate("encoding")));
    }

    @Test
    public void testLocatorResourceNoRootPath() throws IOException {

        TemplateLocator locator = ResourceTemplateLocator.builder(1)
                .setSuffix("foo").build();

        // Just to init the locator
        MustacheEngineBuilder.newBuilder().addTemplateLocator(locator).build();

        Set<String> ids = locator.getAllIdentifiers();
        // No templates available
        assertEquals(0, ids.size());

        assertEquals("{{foo}}", read(locator.locate("locator/file/index")));
        assertEquals("bar", read(locator.locate("locator/file/home")));
        assertEquals("foo", read(locator.locate("locator/file/foo")));
        assertEquals("{{foo}}", read(locator.locate("locator/file/sub/bar")));
        assertEquals("{{bar}}", read(locator.locate("locator/file/sub/subsub/qux")));
        assertEquals("root", read(locator.locate("/oof")));
    }

}
