package org.testshift.testcube.amplify;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class AmplifyTestMarkerContributorTest extends LightJavaCodeInsightFixtureTestCase {

    public void testMarker() {
        myFixture.addClass("package junit.framework; public class TestCase {}");
        myFixture.configureByText("MainTest.java", "public class MainTest extends junit.framework.TestCase {\n" +
                "    @Test public void testFoo<caret>() {\n" +
                "    }\n" +
                "}\n");

        List<GutterMark> marks = myFixture.findGuttersAtCaret();
        assertEquals(1, marks.size());
        GutterIconRenderer mark = (GutterIconRenderer)marks.get(0);
        ActionGroup group = mark.getPopupMenuActions();
        assertNotNull(group);
        TestActionEvent event = new TestActionEvent();
        List<AnAction> list = ContainerUtil.findAll(group.getChildren(event), action -> {
            TestActionEvent actionEvent = new TestActionEvent();
            action.update(actionEvent);
            String text = actionEvent.getPresentation().getText();
            return text != null && text.startsWith("Amplify '") && text.endsWith("'");
        });
        assertEquals(list.toString(), 1, list.size());
        list.get(0).update(event);
        assertEquals("Amplify 'foo()'", event.getPresentation().getText());
    }

    public void testTooltipWithUnderscores() {
        myFixture.configureByText("Main_class_test.java", "public class Main_class_test {\n" +
                "    public static void m<caret>ain(String[] args) {\n" +
                "      someCode();\n" +
                "    }\n" +
                "}");
        List<GutterMark> marks = myFixture.findGuttersAtCaret();
        assertEquals(1, marks.size());
        GutterIconRenderer mark = (GutterIconRenderer)marks.get(0);
        String text = mark.getTooltipText();
        assertTrue(text.startsWith("Run 'Main_class_test.main()'\n" +
                "Debug 'Main_class_test.main()'\n" +
                "Run 'Main_class_test.main()' with Coverage"));
    }

}
