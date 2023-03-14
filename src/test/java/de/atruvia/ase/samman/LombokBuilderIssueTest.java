package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Value;

public class LombokBuilderIssueTest {

	@Value
	@Builder(toBuilder = true)
	static class MyPojo {
		int intValue;
		String stringValue;
		boolean booleanValue;

		static class MyPojoBuilder {
			MyPojoBuilder activate() {
				booleanValue = true;
				return this;
			}
		}

		private MyPojo copy() {
			return toBuilder().build();
		}
	}

	@Test
	void thisWorks() {
		MyPojo myPojo1 = MyPojo.builder().stringValue("string").intValue(42).booleanValue(true).build();
		MyPojo myPojo2 = myPojo1.toBuilder().build();
		assertThat(myPojo1).isEqualTo(myPojo2);
	}

	@Test
	void thisWorksToo() {
		MyPojo myPojo1 = MyPojo.builder().stringValue("string").intValue(42).activate().build();
		MyPojo myPojo2 = myPojo1.toBuilder().build();
		assertThat(myPojo1).isEqualTo(myPojo2);
	}

}
