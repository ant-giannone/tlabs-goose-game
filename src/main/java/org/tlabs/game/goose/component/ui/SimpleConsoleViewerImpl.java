package org.tlabs.game.goose.component.ui;

public class SimpleConsoleViewerImpl implements SimpleViewerComponent {

    @Override
    public void view(String message) {
        System.out.println(message);
    }

    @Override
    public void view(final String message, final Object... parameters) {

        String m = message;

        for (Object parameter : parameters) {
            m = m.replaceFirst("\\{\\}", String.valueOf(parameter));
        }

        System.out.println(m);
    }
}
