package BundleResource;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.InsurancePlan;
import org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent;
import org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent;
import org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

import ResourcePropulator.ResourcePopulator;

import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

public class InsurancePlanBundle {

	// populate CoverageEligibilityRequest

	static FhirContext ctx = FhirContext.forR4();
	static FhirValidator validator;
	static FhirInstanceValidator fhirInstanceValidator;

	public static void main(String[] arg) throws Exception {

		// Initialize validation support and loads all required profiles
		init();

		// Populate ClaimBundle resource
		Bundle insurancePlanBundle = populateInsurancePlanBundle();

		
		
		
		if (validator(insurancePlanBundle)) {
			System.out.println("\nInsurancePlanBundle is Succesfully Validated");
		}

	}

	public static InsurancePlan populateInsurancePlan() {

		InsurancePlan insurancePlan = new InsurancePlan();

		// set id
		insurancePlan.setId("InsurancePlan-01");

		// set meta
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/InsurancePlan");
		insurancePlan.setMeta(meta);

		// set identifer
		Identifier identifier = new Identifier();
		identifier.setSystem("https://irdai.gov.in/InsurancePlan");
		identifier.setValue("761234556546");
		insurancePlan.addIdentifier(identifier);
//
		// set status
		insurancePlan.setStatus(PublicationStatus.ACTIVE);

		// set type
		insurancePlan.addType(
				new CodeableConcept(new Coding("https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-insuranceplan-type", "01",
						"Hospitalisation Indemnity Policy")));

		// set name
		insurancePlan.setName("");

		// set period
		insurancePlan.setPeriod(new Period().setStart(new Date()));

		// set ownerdBy
		insurancePlan.setOwnedBy(new Reference().setReference("Organization/Organization-02"));

		// set adminstraterBy
		insurancePlan.setAdministeredBy(new Reference().setReference("Organization/Organization-02"));

		// set coverage
		InsurancePlanCoverageComponent coverageComponent = new InsurancePlanCoverageComponent();

		coverageComponent.setType(new CodeableConcept(
				new Coding("http://snomed.info/sct", "737481003", "Inpatient care management (procedure)")));

		CoverageBenefitComponent BenefitComponent1 = new CoverageBenefitComponent();

		// set benefit
		BenefitComponent1.setType(new CodeableConcept(
				new Coding("http://snomed.info/sct", "737481003", "Inpatient care management (procedure)")));

		// set benefitLimit

		CoverageBenefitLimitComponent benefitLimit = new CoverageBenefitLimitComponent();

		benefitLimit.setValue(new Quantity().setValue(50000).setComparator(QuantityComparator.LESS_OR_EQUAL));

		BenefitComponent1.addLimit(benefitLimit);
		coverageComponent.addBenefit(BenefitComponent1);
		insurancePlan.addCoverage(coverageComponent);

		// set plan of the insurance

		InsurancePlanPlanComponent planComponent = new InsurancePlanPlanComponent();

		planComponent.addIdentifier().setValue("Activ assure Dimaond").setUse(IdentifierUse.OFFICIAL);

		planComponent.setType(new CodeableConcept(
				new Coding("https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-plan-type", "01", "Individual")));

		planComponent.addGeneralCost().setCost(new Money().setCurrency("INR").setValue(200000));

		insurancePlan.addPlan(planComponent);

		return insurancePlan;

	}

	// populating InsurancePlanBundle Resource
	public static Bundle populateInsurancePlanBundle() {

		Bundle insurancePlanBundle = new Bundle();

		// set Id - Logical id of this artifact
		insurancePlanBundle.setId("InsuarncePlanBundle-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/InsurancePlanBundle");
		meta.addSecurity(
				new Coding("http://terminology.hl7.org/CodeSystem/v3-Confidentiality", "V", "very restricted"));
		insurancePlanBundle.setMeta(meta);

		// set Type - collection
		insurancePlanBundle.setType(BundleType.COLLECTION);

		// set Timestamp - When the bundle was assembled
		insurancePlanBundle.setTimestamp(new Date());

		// set Entry - Entry in the bundle - will have a resource or information
		List<Bundle.BundleEntryComponent> list = insurancePlanBundle.getEntry();

		BundleEntryComponent bundleEntry0 = new BundleEntryComponent();
		bundleEntry0.setFullUrl("InsurancePlan/InsurancePlan-01");
		bundleEntry0.setResource(populateInsurancePlan());

		BundleEntryComponent bundleEntry1 = new BundleEntryComponent();
		bundleEntry1.setFullUrl("Oragnization/Organization-02");
		bundleEntry1.setResource(ResourcePopulator.populateSecondOrganizationResource());

		
		list.add(bundleEntry0);
		list.add(bundleEntry1);
		insurancePlanBundle.setEntry(list);

		return insurancePlanBundle;

	}

	/**
	 * This method initiates loading of FHIR default profiles and NDHM profiles for
	 * validation
	 */
	public static void init() throws IOException {

		/*
		 * Load NPM Package containing ABDM FHIR Profiles Copy NPM Package.tgz
		 * (<package_name>.tgz) at "src/main/resource"
		 */

		NpmPackageValidationSupport npmValidationSupport = new NpmPackageValidationSupport(ctx);
		npmValidationSupport.loadPackageFromClasspath("classpath:package.tgz");

		// Create a chain that will hold our modules
		ValidationSupportChain validationsupportchain = new ValidationSupportChain(

				npmValidationSupport, new DefaultProfileValidationSupport(ctx),
				new InMemoryTerminologyServerValidationSupport(ctx), new CommonCodeSystemsTerminologyService(ctx),
				new SnapshotGeneratingValidationSupport(ctx));

		CachingValidationSupport validationSupport = new CachingValidationSupport(validationsupportchain);

		validator = ctx.newValidator();
		fhirInstanceValidator = new FhirInstanceValidator(validationSupport);
		validator.registerValidatorModule(fhirInstanceValidator);

	}

	// Validation method will validate claimBundle against ABDM ClaimBundle FHIR
	// Profile and return true or false boolean value

	public static boolean validator(IBaseResource resource) throws Exception {

		ValidationResult outcome = validator.validateWithResult(resource);
		System.out.println(outcome);

		for (SingleValidationMessage next : outcome.getMessages()) {

			System.out.println(next.getSeverity() + " - " + next.getLocationString() + " - " + next.getMessage());
		}

		return outcome.isSuccessful();

	}

}
