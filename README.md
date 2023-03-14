# OpenTelemetry Aware Maven Plugin Example


Example to instrument your Maven Mojos with OpenTelemetry to get better visibility on Maven build executions.

OpenTelemetry instrumentation of Maven plugins must be used in conjunction with the 
[OpenTelemetry Maven Extension](https://github.com/open-telemetry/opentelemetry-java-contrib/tree/main/maven-extension)

Add the OpenTelemetry API dependency in the pom.xml of your Maven plugin.
````xml
<project>
    ...    
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-api</artifactId>
            <version>1.23.0</version>
        </dependency>
        ...
    </dependencies>
</project>        

````


```java
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TestMojo extends AbstractMojo {

    @Override
    public void execute() {
        Span mojoExecuteSpan = Span.current();
        try (Scope ignored = mojoExecuteSpan.makeCurrent()) {
            // ENRICH THE DEFAULT SPAN OF THE MOJO EXECUTION
            // (this span is created by the opentelemetry-maven-extension)
            mojoExecuteSpan.setAttribute("an-attribute", "a-value");

            // CREATE SUB SPANS TO CAPTURE FINE GRAINED DETAILS OF THE MOJO EXECUTION
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
```
