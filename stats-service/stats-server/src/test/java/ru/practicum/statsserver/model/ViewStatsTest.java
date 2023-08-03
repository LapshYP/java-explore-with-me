package ru.practicum.statsserver.model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.statsserver.model.ViewStats;

public class ViewStatsTest {
    private String app;
    private String uri;
    private Long hits;

    @BeforeEach
    public void setUp() {
        app = "TestApp";
        uri = "/test";
        hits = 100L;
    }

    @Test
    public void testViewStatsConstructor() {
        ViewStats viewStats = new ViewStats(app, uri, hits);

        Assertions.assertEquals(app, viewStats.getApp());
        Assertions.assertEquals(uri, viewStats.getUri());
        Assertions.assertEquals(hits, viewStats.getHits());
    }

    @Test
    public void testViewStatsSettersAndGetters() {
        ViewStats viewStats = new ViewStats();

        viewStats.setApp(app);
        viewStats.setUri(uri);
        viewStats.setHits(hits);

        Assertions.assertEquals(app, viewStats.getApp());
        Assertions.assertEquals(uri, viewStats.getUri());
        Assertions.assertEquals(hits, viewStats.getHits());
    }

    @Test
    public void testViewStatsBuilder() {
        ViewStats viewStats = ViewStats.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();

        Assertions.assertEquals(app, viewStats.getApp());
        Assertions.assertEquals(uri, viewStats.getUri());
        Assertions.assertEquals(hits, viewStats.getHits());
    }
}