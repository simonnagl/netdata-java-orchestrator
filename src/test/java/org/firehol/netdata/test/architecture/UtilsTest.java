package org.firehol.netdata.test.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

public class UtilsTest {
	@Test
	public void testUtils() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.firehol");

		final ArchRule rule = classes().that()
				.haveSimpleNameEndingWith("Utils")
				.should()
				.haveModifier(JavaModifier.FINAL)
				.andShould()
				.haveOnlyPrivateConstructors();

		rule.check(importedClasses);
	}

}
