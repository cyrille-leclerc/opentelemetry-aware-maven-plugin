package com.example.maven;


import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.Scope;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Optional;

/**
 * Produce debug info
 */
@Mojo(name = "debug", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DebugMojo extends AbstractMojo {

    @Override
    public void execute() {
        Span mojoExecuteSpan = Span.current();
        try (Scope ignored = mojoExecuteSpan.makeCurrent()) {
            mojoExecuteSpan.setAttribute("test", "attribute-added-by-debug-mojo");
            System.out.println("Context: " + Context.current());
            ContextStorage contextStorage = ContextStorage.get();
            Class<? extends ContextStorage> contextStorageClass = contextStorage.getClass();
            System.out.println("Span: " + mojoExecuteSpan);
            System.out.println("ContextStorage: " + contextStorage + " - " + contextStorageClass + "@" + System.identityHashCode(contextStorage));
            System.out.println("\tclass " + System.identityHashCode(contextStorageClass) +
                    " loaded " +
                    " by: " + contextStorageClass.getClassLoader() +
                    " from: " + Optional.of(contextStorageClass.getProtectionDomain().getCodeSource()).map(source -> source.getLocation().toString()).orElse("#unknown#"));
        }
    }
}
