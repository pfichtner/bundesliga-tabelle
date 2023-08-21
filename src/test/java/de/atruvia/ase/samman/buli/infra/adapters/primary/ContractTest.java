package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.junit.jupiter.api.Tag;

@Tag("cdct")
@Tag("pact")
@Tag("contracttest")
@Retention(RUNTIME)
public @interface ContractTest {

}
