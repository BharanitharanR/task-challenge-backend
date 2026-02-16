package com.banyan.platform.ast.builder;

import com.banyan.platform.ast.builder.AstBuilder;
import com.banyan.platform.ast.node.ExecutableNode;
import com.banyan.platform.runtime.EvidenceContext;
import com.banyan.platform.runtime.context.DarRuntimeContext;
import com.banyan.platform.runtime.darLoader.ZipDarLoader;
import com.banyan.platform.runtime.exception.InvalidEvidenceTypeException;
import com.banyan.platform.runtime.exception.MissingEvidenceException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AstBuilderTest {


    @Test
    public  void endToendTest() {

        try {
            // 1. Load DAR
            DarRuntimeContext context =
                    ZipDarLoader.load("/Users/bharani/Documents/task-challenge-backend/project-banyan-runtime/src/main/resources/compilation_package.dar");
            // 2. Pick the only ruleset
            DarRuntimeContext.RulesetKey key = null;

            for (var entry : context.rulesets()) {
                key = entry.getKey();
                break;
            }

            // 3. Build AST
            AstBuilder builder = new AstBuilder(context);
            ExecutableNode root = builder.build(key);

            // 4. Create evidence
            Map<String, Object> evidence = Map.of(
                    "faileAttempts", 4,
                    "score", 75,
                    "userType", 2,
                    "country", "IN",
                    "businessHours", true
            );

            EvidenceContext evidenceContext =
                    new EvidenceContext(evidence);

            // 5. Evaluate
            boolean result = root.evaluate(evidenceContext);

            System.out.println("Evaluation result = " + result);
        } catch (Exception e) {
            System.out.println("Exception result = " + e.getMessage());
        }
    }
///Users/bharani/Documents/task-challenge-backend/target
@Test
void goodDriver() throws Exception {

    DarRuntimeContext context =
            ZipDarLoader.load("/Users/bharani/Documents/task-challenge-backend/target/compilation_package.dar");

    var key = context.rulesets().iterator().next().getKey();

    ExecutableNode root =
            new AstBuilder(context).build(key);

    EvidenceContext evidence = new EvidenceContext(Map.of(
            "speedOverLimitSeconds",  500000,
            "laneDepartureCount", 9,// triggers first OR branch true
            "country","IN"
            // intentionally omit some fields from second branch
    ));

    boolean result = root.evaluate(evidence);

    assertTrue(result);
}
    @Test
    void shouldShortCircuitOr() throws Exception {

        DarRuntimeContext context =
                ZipDarLoader.load("src/main/resources/compilation_package.dar");

        var key = context.rulesets().iterator().next().getKey();

        ExecutableNode root =
                new AstBuilder(context).build(key);

        EvidenceContext evidence = new EvidenceContext(Map.of(
                "failedAttempts", 9,  // triggers first OR branch true
                "country","IN"
                // intentionally omit some fields from second branch
        ));

        boolean result = root.evaluate(evidence);

        assertTrue(result);
    }

}