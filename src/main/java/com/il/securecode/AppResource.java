package com.il.securecode;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.joor.Reflect;

@Path("/")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public final class AppResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public IndexView index() {
        return new IndexView();
    }

    @Path("/vul2")
    @POST
    public String vul2(final String code) {
        final long shouldBeOkayForNow = System.currentTimeMillis();

        final Object s = Reflect.compile(
            "com.il.securecode.NewCompileTest" + shouldBeOkayForNow,
            "package com.il.securecode;\n" +

            "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
            "import org.junit.jupiter.api.Test;\n" +

            "class NewCompileTest" + shouldBeOkayForNow +"{\n" +

            code +

            "  public static boolean userExists(final String username) {\n" +
            "    return username == \"existing_user\";\n" +
            "  }\n" +

            "  public static boolean passwordMatches(final String username, final String password) {\n" +
            "    return password == \"correct_password\";\n" +
            "  }\n" +

            "  @Test\n" +
            "  void checkSuccessfulLogin() {\n" +
            "    assertEquals(\"login successful\", login(\"existing_user\", \"correct_password\"));\n" +
            "  }\n" +

            "  @Test\n" +
            "  void checkUserDoesNotExist() {\n" +
            "    assertEquals(\"invalid username or password\", login(\"not_existing_user\", \"doesnt_matter\"));\n" +
            "  }\n" +

            "  @Test\n" +
            "  void checkPasswordNotMatched() {\n" +
            "    assertEquals(\"invalid username or password\", login(\"existing_user\", \"wrong_password\"));\n" +
            "  }\n" +
            "}\n"
        ).create().get();

        final LauncherDiscoveryRequest request = 
            LauncherDiscoveryRequestBuilder.request().selectors(selectClass(s.getClass())).build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary().getTestsSucceededCount() == 3 ? "OK" : "NO";
    }

    @Path("/vul3")
    @POST
    public String vul3(final String code) {
        final long shouldBeOkayForNow = System.currentTimeMillis();

        final Object s = Reflect.compile(
            "com.il.securecode.NewCompileTest" + shouldBeOkayForNow,
            "package com.il.securecode;\n" +

            "import static org.junit.jupiter.api.Assertions.assertTrue;\n" +
            "import org.junit.jupiter.api.Test;\n" +

            "class NewCompileTest" + shouldBeOkayForNow +"{\n" +

            "  static class XMLReader {\n" +
            "    boolean fSet;\n" +
            "    void setFeature(String feature, boolean flag) {\n" +
            "      if (feature == \"https://xml.org/sax/features/external-general-entities\" && flag == false) {\n" +
            "        fSet = true;\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +

            "  static class SAXParser { XMLReader getXMLReader() { return new XMLReader(); } }\n" +

            "  static class SAXParserFactory {\n" +
            "    boolean fSet;\n" +
            "    static SAXParserFactory newInstance() { return new SAXParserFactory(); }\n" +
            "    static SAXParser newSAXParser() { return new SAXParser(); }\n" +
            "    void setFeature(String feature, boolean flag) {\n " +
            "      if (feature == \"https://xml.org/sax/features/external-general-entities\" && flag == false) {\n " +
            "        fSet = true;\n" +
            "      }\n" +
            "    }\n " +
            "  }\n" +
            
            "  boolean fn(String xmlString) {\n" +
            code +
            "    return factory.fSet && reader.fSet;\n" +
            "  }\n" +

            // this, unfortunately, doesn't ensure that the factory instance's feature is set BEFORE it is used to instantiate the parser
            "  @Test\n" +
            "  void checkFeatureFlagsSet() {\n" +
            "    assertTrue(fn(\"<somexml></somexml>\"));\n" +
            "  }\n" +
            "}\n"
        ).create().get();

        final LauncherDiscoveryRequest request = 
            LauncherDiscoveryRequestBuilder.request().selectors(selectClass(s.getClass())).build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary().getTestsSucceededCount() == 1 ? "OK" : "NO";
    }

    @Path("/vul4")
    @POST
    public String vul4(final String code) {
        final long shouldBeOkayForNow = System.currentTimeMillis();

        final Object s = Reflect.compile(
            "com.il.securecode.NewCompileTest" + shouldBeOkayForNow,
            "package com.il.securecode;\n" +

            "import org.owasp.encoder.Encode;\n" +
            "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
            "import org.junit.jupiter.api.Test;\n" +

            "class NewCompileTest" + shouldBeOkayForNow +"{\n" +

            code +

            "  @Test\n" +
            "  void checkEncoding() {\n" +
            "    assertEquals(\n" +
            "      \"<div>Here are the product's features: feature1, feature2, &lt;script&gt;some-script&lt;/script&gt;</div>\",\n" +
            "      getHtmlDiv(\"feature1, feature2, <script>some-script</script>\")\n" +
            "    );\n" +
            "  }\n" +
            "}\n"
        ).create().get();

        final LauncherDiscoveryRequest request = 
            LauncherDiscoveryRequestBuilder.request().selectors(selectClass(s.getClass())).build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary().getTestsSucceededCount() == 1 ? "OK" : "NO";
    }

    @Path("/vul5")
    @POST
    public String vul5(final String code) {
        final long shouldBeOkayForNow = System.currentTimeMillis();

        final Object s = Reflect.compile(
            "com.il.securecode.NewCompileTest" + shouldBeOkayForNow,
            "package com.il.securecode;\n" +

            "import static org.junit.jupiter.api.Assertions.assertEquals;\n" +
            "import org.junit.jupiter.api.Test;\n" +

            "class NewCompileTest" + shouldBeOkayForNow +"{\n" +

            code +

            "  @Test\n" +
            "  void checkGetCountryCookie() {\n" +
            "    final NewCompileTest" + shouldBeOkayForNow + " x = new NewCompileTest" + shouldBeOkayForNow + "();\n" +
            "    x.country=\"IN\";\n" +
            "    assertEquals(\"country=IN\", x.getCountryCookie());\n" +
            "  }\n" +
            "}\n"
        ).create().get();

        final LauncherDiscoveryRequest request = 
            LauncherDiscoveryRequestBuilder.request().selectors(selectClass(s.getClass())).build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary().getTestsSucceededCount() == 1 ? "OK" : "NO";
    }

    @Path("/vul6")
    @POST
    public String vul6(final String code) {
        final long shouldBeOkayForNow = System.currentTimeMillis();

        final Object s = Reflect.compile(
            "com.il.securecode.NewCompileTest" + shouldBeOkayForNow,
            "package com.il.securecode;\n" +

            "import static org.junit.jupiter.api.Assertions.assertTrue;\n" +
            "import static org.junit.jupiter.api.Assertions.assertFalse;\n" +
            "import org.junit.jupiter.api.Test;\n" +

            "final class NewCompileTest" + shouldBeOkayForNow +"{\n" +

            "  private boolean authenticate(final String u, final String p) {\n" +
            "    return u == \"valid_user\" && p == \"valid_password\";\n" +
            "  }\n" +

            "  private void incrementLoginAttempts(final String x) {}\n" +
            "  private int loginAttempts(final String x) { return 1; }\n" +

            "  static class Logger {\n" +
            "    static boolean called;\n" +
            "    static void log(final String x) { called = true; }\n" +
            "  }\n" +

            code +

            "  @Test\n" +
            "  void check() {\n" +
            "    final NewCompileTest" + shouldBeOkayForNow + " x = new NewCompileTest" + shouldBeOkayForNow + "();\n" +
            "    assertTrue(x.login(\"valid_user\", \"valid_password\"));\n" +
            "    assertFalse(Logger.called);\n" +
            "    assertFalse(x.login(\"invalid_user\", \"doesnt_matter\"));\n" +
            "    assertTrue(Logger.called);\n" +
            "  }\n" +
            "}\n"
        ).create().get();

        final LauncherDiscoveryRequest request = 
            LauncherDiscoveryRequestBuilder.request().selectors(selectClass(s.getClass())).build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary().getTestsSucceededCount() == 1 ? "OK" : "NO";
    }
}
