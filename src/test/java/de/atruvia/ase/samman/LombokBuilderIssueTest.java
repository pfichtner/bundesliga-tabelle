package de.atruvia.ase.samman;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lombok.Builder;

public class LombokBuilderIssueTest {
	
	@Builder(toBuilder = true)
	static class MyPojo {
		
	}
	
	@Test
	void testName() {
		
	}

}
