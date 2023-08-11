package BundleResource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.CoverageEligibilityResponse;
import org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponsePurpose;
import org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponseStatus;
import org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent;
import org.hl7.fhir.r4.model.Enumerations.RemittanceOutcome;

import ResourcePropulator.ResourcePopulator;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

public class CoverageEligibilityResponseBundle {

	// Initialize the FHIR context for r4
	static FhirContext ctx = FhirContext.forR4();
	static FhirValidator validator;
	static FhirInstanceValidator fhirInstanceValidator;

	public static void main(String[] arg) throws Exception {

		// Initialize validation support and loads all required profiles
		init();

		// Populate CoverageEligibilityResponseBundle resource
		Bundle coverageEligibilityResponseBundle = populateCoverageEligibilityResponseBundle();

		// Instantiate a new parser
		IParser parser = ctx.newJsonParser().setPrettyPrint(true);
		
		String str = parser.encodeResourceToString(coverageEligibilityResponseBundle);
		
		System.out.println(str);
		
		
		if (validator(coverageEligibilityResponseBundle)) {
			System.out.println("\nCoverageEligibilityResponseBundle is Succesfully Validated");

		}

	}

	// populate coverageEligibilityResponse

	public static CoverageEligibilityResponse populateCoverageEligiblityResponse() {

		CoverageEligibilityResponse coverageEligiblityResponse = new CoverageEligibilityResponse();

		// set id
		coverageEligiblityResponse.setId("coverageEligiblityResponse-01");

		// set meta
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/CoverageEligibilityResponse");
		coverageEligiblityResponse.setMeta(meta);

		// set identifier
		Identifier identifier = new Identifier();
		identifier.setSystem("http://happypharma.com/claim");
		identifier.setValue("123456");
		coverageEligiblityResponse.addIdentifier(identifier);

		// set status
		coverageEligiblityResponse.setStatus(EligibilityResponseStatus.ACTIVE);

		// set purpose
		coverageEligiblityResponse.addPurpose(EligibilityResponsePurpose.AUTHREQUIREMENTS);

		// set patient
		coverageEligiblityResponse.setPatient(new Reference().setReference("Patient/Patient-01"));

		// set created date
		coverageEligiblityResponse.setCreated(new Date());

		// set requestor
		coverageEligiblityResponse.setRequestor(new Reference().setReference("Organization/Organization-01"));

		// set request
		coverageEligiblityResponse
				.setRequest(new Reference().setReference("CoverageEligibilityRequest/CoverageEligibilityRequest-01"));

		// set outcome
		coverageEligiblityResponse.setOutcome(RemittanceOutcome.COMPLETE);

		// set disposition
		coverageEligiblityResponse.setDisposition("Policy is currently in-force.");

		// set insurer
		coverageEligiblityResponse.setInsurer(new Reference().setReference("Organization/Organization-02"));

		// set insurance
		InsuranceComponent insurance = new InsuranceComponent();
		insurance.setCoverage(new Reference("Coverage/Coverage-01"));
		insurance.setInforce(true);
		insurance.setBenefitPeriod(new Period().setStart(new Date(2022 - 02 - 27)).setEnd(new Date()));
		insurance.addItem().setProductOrService(
				new CodeableConcept(new Coding("http://snomed.info/sct", "10849003", "Removal of foreign body")));

		coverageEligiblityResponse.addInsurance(insurance);

		return coverageEligiblityResponse;

	}

	// populating CoverageEligibilityResponseBundle Resource
	public static Bundle populateCoverageEligibilityResponseBundle() {

		Bundle coverageEligibilityResponseBundle = new Bundle();

		// set Id - Logical id of this artifact
		coverageEligibilityResponseBundle.setId("CoverageEligibilityResponseBundle-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/CoverageEligibilityResponseBundle");
		meta.addSecurity(
				new Coding("http://terminology.hl7.org/CodeSystem/v3-Confidentiality", "V", "very restricted"));
		coverageEligibilityResponseBundle.setMeta(meta);

		// set Type - collection
		coverageEligibilityResponseBundle.setType(BundleType.COLLECTION);

		// set Timestamp - When the bundle was assembled
		coverageEligibilityResponseBundle.setTimestamp(new Date());

		// set Entry - Entry in the bundle - will have a resource or information
		List<Bundle.BundleEntryComponent> list = coverageEligibilityResponseBundle.getEntry();

		BundleEntryComponent bundleEntry1 = new BundleEntryComponent();
		bundleEntry1.setFullUrl("CoverageEligibilityResponse/CoverageEligibilityResponse-01");
		bundleEntry1.setResource(populateCoverageEligiblityResponse());

		BundleEntryComponent bundleEntry2 = new BundleEntryComponent();
		bundleEntry2.setFullUrl("Patient/Patient-01");
		bundleEntry2.setResource(ResourcePopulator.populatePatientResource());

		BundleEntryComponent bundleEntry3 = new BundleEntryComponent();
		bundleEntry3.setFullUrl("Organization/Organization-01");
		bundleEntry3.setResource(ResourcePopulator.populateOrganizationResource());

		BundleEntryComponent bundleEntry4 = new BundleEntryComponent();
		bundleEntry4.setFullUrl("Organization/Organization-02");
		bundleEntry4.setResource(ResourcePopulator.populateSecondOrganizationResource());

		BundleEntryComponent bundleEntry5 = new BundleEntryComponent();
		bundleEntry5.setFullUrl("Coverage/Coverage-01");
		bundleEntry5.setResource(ResourcePopulator.populateCoverageResource());

		BundleEntryComponent bundleEntry6 = new BundleEntryComponent();
		bundleEntry5.setFullUrl("Procedure/Procedure-01");
		bundleEntry5.setResource(ResourcePopulator.populateProcedureResource());

		BundleEntryComponent bundleEntry7 = new BundleEntryComponent();
		bundleEntry7.setFullUrl("DocumentReference/DocumentReference-01");
		bundleEntry7.setResource(ResourcePopulator.populateDocumentReferenceResource());

		list.add(bundleEntry1);
		list.add(bundleEntry2);
		list.add(bundleEntry3);
		list.add(bundleEntry4);
		list.add(bundleEntry5);
		list.add(bundleEntry6);
//		list.add(bundleEntry7);
		coverageEligibilityResponseBundle.setEntry(list);

		return coverageEligibilityResponseBundle;

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

	// Validation method will validate CoverageEligibilityResponseBundle against ABDM CoverageEligibilityResponseBundle FHIR
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
