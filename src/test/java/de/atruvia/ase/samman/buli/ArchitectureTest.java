package de.atruvia.ase.samman.buli;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "de.atruvia.ase.samman.buli", importOptions = DoNotIncludeTests.class)
class ArchitectureTest {

	@ArchTest
	ArchRule portsAndAdapters = onionArchitecture() //
			.withOptionalLayers(true) //
			.domainModels("..domain..") //
			.adapter("primary-adapters", "..adapters.primary..") //
			.adapter("secondary-adapters", "..adapters.secondary..") //
	;

	@ArchTest
	ArchRule noDomainToAdapterDependencies = noClasses().that().resideInAPackage("..domain..") //
			.should().dependOnClassesThat().resideInAPackage("..adapters..") //
	;

	@ArchTest
	ArchRule noPortToAdapterDependencies = noClasses().that().resideInAPackage("..ports..") //
			.should().dependOnClassesThat().resideInAPackage("..adapters..") //
	;

}
