package org.tlabs.game.goose.component.ui;

public class SimpleUiViewerFactoryImpl implements SimpleUiViewerFactory<SimpleViewerComponent> {

    private SimpleUiViewerFactoryImpl() {

    }

    private static class SingletonHelper {
        private static final SimpleUiViewerFactoryImpl INSTANCE = new SimpleUiViewerFactoryImpl();
    }

    public static SimpleUiViewerFactoryImpl getInstance() {
        return SimpleUiViewerFactoryImpl.SingletonHelper.INSTANCE;
    }

    @Override
    public SimpleViewerComponent create(SimpleViewerType viewerType) {

        String type = viewerType.toString();

        if (SimpleViewerType.CONSOLE.toString().equals(type)) {
            return new SimpleConsoleViewerImpl();
        } else if (SimpleViewerType.LOGS.toString().equals(type)) {
            return new SimpleLogViewerImpl();
        }

        throw new RuntimeException(String.format("Unknown SimpleViewerType for '%s'", type));
    }
}
