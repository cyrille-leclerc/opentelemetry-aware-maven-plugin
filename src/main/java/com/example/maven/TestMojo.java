package com.example.maven;


import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TestMojo extends AbstractMojo {

    @Override
    public void execute() {
        Span mojoExecuteSpan = Span.current();
        try (Scope ignored = mojoExecuteSpan.makeCurrent()) {
            mojoExecuteSpan.setAttribute("an-attribute", "a-value");

            Tracer tracer = GlobalOpenTelemetry.get().getTracer("com.example.maven.otel_aware_plugin");
            Span childSpan = tracer.spanBuilder("otel-aware-goal-sub-span").setAttribute("another-attribute", "another-value").startSpan();
            try (Scope ignored2 = childSpan.makeCurrent()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                childSpan.end();
            }
        }
    }
}
