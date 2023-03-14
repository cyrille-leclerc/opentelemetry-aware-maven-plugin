package com.example.maven;


import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.Scope;
import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Optional;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TestMojo extends AbstractMojo {

    public void execute() {
        Span mojoExecuteSpan = Span.current();
        try (Scope ignored = mojoExecuteSpan.makeCurrent()) {
            mojoExecuteSpan.setAttribute("hello", "world");
            System.out.println("Context: " + Context.current());
            ContextStorage contextStorage = ContextStorage.get();
            Class<? extends ContextStorage> contextStorageClass = contextStorage.getClass();
            System.out.println("Span: " + mojoExecuteSpan);
            System.out.println("ContextStorage: " + contextStorage + " - " + contextStorageClass + "@" + System.identityHashCode(contextStorage));
            System.out.println("\tclass " + System.identityHashCode(contextStorageClass) +
                    " loaded " +
                    " by: " + contextStorageClass.getClassLoader() +
                    " from: " + Optional.of(contextStorageClass.getProtectionDomain().getCodeSource()).map(source -> source.getLocation().toString()).orElse("#unknown#"));

            Tracer tracer = GlobalOpenTelemetry.get().getTracer("com.example.maven");
            Span childSpan = tracer.spanBuilder("otel-aware-goal-subspan").setAttribute("an-attribute", "a-value").startSpan();
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
