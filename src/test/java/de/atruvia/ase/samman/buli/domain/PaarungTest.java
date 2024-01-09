package de.atruvia.ase.samman.buli.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PaarungTest {

	@Test
	void verifyAllFieldsGetSwapped()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Paarung paarung = PaarungMother.all();
		Paarung swappedPaarung = paarung.swap();

		Class<?> clazz = paarung.getClass();
		List<Field> fields = new ArrayList<>(asList(clazz.getDeclaredFields()));
		List<Field> fieldsToIgnore = asList(getByName(clazz, "gespielt"));
		fields.removeAll(fieldsToIgnore);

		verifyFieldsNotEqual(paarung, swappedPaarung, fields);
	}

	@Test
	void swapTwoTimesIsEqualToOrigin()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Paarung origin = PaarungMother.all();
		Paarung twoTimesSwapped = origin.swap().swap();

		assertThat(twoTimesSwapped).isEqualTo(origin);

	}

	private static void verifyFieldsNotEqual(Paarung paarung, Paarung swapped, List<Field> fields)
			throws IllegalAccessException {
		for (Field field : fields) {
			field.setAccessible(true);
			verifyFieldNotEqual(paarung, swapped, field);
		}
	}

	private static void verifyFieldNotEqual(Paarung paarung, Paarung swapped, Field field)
			throws IllegalAccessException {
		Object expectedValue = field.get(paarung);
		Object actualValue = field.get(swapped);
		assertThat(expectedValue).overridingErrorMessage("Field '%s' should be different, but both are equal (%s).",
				field.getName(), actualValue).isNotEqualTo(actualValue);
	}

	private Field getByName(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		return clazz.getDeclaredField(name);
	}

}
