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
import org.hl7.fhir.r4.model.CoverageEligibilityRequest;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestPurpose;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestStatus;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Reference;

import ResourcePropulator.ResourcePopulator;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

public class CoverageEligibilityRequestBundle {

	// populate CoverageEligibilityRequest
	

	static FhirContext ctx = FhirContext.forR4();
	static FhirValidator validator;
	static FhirInstanceValidator fhirInstanceValidator;

	public static void main(String[] arg) throws Exception {

		// Initialize validation support and loads all required profiles
		init();

		// Populate CoverageEligibilityRequestBundle resource
		Bundle coverageEligibilityRequestBundle = populateCoverageEligibilityRequestBundleResource();

		// Instantiate a new parser
		IParser parser = ctx.newJsonParser().setPrettyPrint(true);

		String str = parser.encodeResourceToString(coverageEligibilityRequestBundle);
		System.out.println(str);
		if (validator(coverageEligibilityRequestBundle)) {
			System.out.println("\nCoverageEligibilityRequestBundle is Succesfully Validated");
		}

	}
	
	public static CoverageEligibilityRequest populateCoverageEligibilityRequest() {

		CoverageEligibilityRequest coverageEligibilityRequest = new CoverageEligibilityRequest();

		// set Id - Logical id of this artifact
		coverageEligibilityRequest.setId("CoverageEligibilityRequest-01");

		// set meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/CoverageEligibilityRequest");
		coverageEligibilityRequest.setMeta(meta);

		// Set identifier - Business Identifier for coverage eligiblity request
		Identifier identifier = new Identifier();
		identifier.setSystem("http://happypharma.com/claim");
		identifier.setValue("123456");
		coverageEligibilityRequest.addIdentifier(identifier);

		// set status - active | cancelled | draft | entered-in-error
		coverageEligibilityRequest.setStatus(EligibilityRequestStatus.ACTIVE);

		// set priority - Desired processing priority
		coverageEligibilityRequest.setPriority(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/processpriority", "normal", "Normal")));

		// set purpose - auth-requirements | benefits | discovery | validation
		coverageEligibilityRequest.addPurpose(EligibilityRequestPurpose.AUTHREQUIREMENTS);

		// set patient - Intended recipient of products and services
		coverageEligibilityRequest.setPatient(new Reference().setReference("Patient/Patient-01"));

		// set created - Creation date
		coverageEligibilityRequest.setCreated(new Date());

		// set enterer - Author
		coverageEligibilityRequest.setEnterer(new Reference().setReference("Practitioner/Practitioner-01"));

		// set provider - Party responsible for the request
		coverageEligibilityRequest.setProvider(new Reference().setReference("Organization/Organization-02"));

		// set insurer - Coverage issuer
		coverageEligibilityRequest.setInsurer(new Reference().setReference("Organization/Organization-01"));

		// set facility - Servicing facility
		coverageEligibilityRequest.setFacility(new Reference().setReference("Location/Location-01"));

		// set supportinginfo - Supporting information
		coverageEligibilityRequest.addSupportingInfo().setSequence(1)
				.setInformation(new Reference().setDisplay("DocumentReference/DocumentReference-01"))
				.setAppliesToAll(true);

		// set insurance - Patient insurance information
		coverageEligibilityRequest.addInsurance().setFocal(true)
				.setCoverage(new Reference().setReference("Coverage/Coverage-01"));

		// set Item - Item to be evaluated for eligibiity
		coverageEligibilityRequest.addItem().addSupportingInfoSequence(1).setCategory(new CodeableConcept(new Coding("http://snomed.info/sct", "225362009"	, "Dental care"))).setProductOrService(
				new CodeableConcept(new Coding("http://snomed.info/sct", "10849003", "Removal of foreign body")));

		return coverageEligibilityRequest;

	}

	// populating CoverageEligibilityRequestBundle Resource
	public static Bundle populateCoverageEligibilityRequestBundleResource() {

		Bundle coverageEligibilityRequestBundle = new Bundle();

		// set Id - Logical id of this artifact
		coverageEligibilityRequestBundle.setId("CoverageEligibilityRequestBundle-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/CoverageEligibilityRequestBundle");
		meta.addSecurity(
				new Coding("http://terminology.hl7.org/CodeSystem/v3-Confidentiality", "V", "very restricted"));
		coverageEligibilityRequestBundle.setMeta(meta);

		// set Type - collection
		coverageEligibilityRequestBundle.setType(BundleType.COLLECTION);

		// set Timestamp - When the bundle was assembled
		coverageEligibilityRequestBundle.setTimestamp(new Date());

		// set Entry - Entry in the bundle - will have a resource or information
		List<Bundle.BundleEntryComponent> list = coverageEligibilityRequestBundle.getEntry();

		BundleEntryComponent bundleEntry0 = new BundleEntryComponent();
		bundleEntry0.setFullUrl("CoverageEligbilityRequest/CoverageEligibilityRequest-01");
		bundleEntry0.setResource(populateCoverageEligibilityRequest());

		BundleEntryComponent bundleEntry1 = new BundleEntryComponent();
		bundleEntry1.setFullUrl("Patient/Patient-01");
		bundleEntry1.setResource(ResourcePopulator.populatePatientResource());

		BundleEntryComponent bundleEntry2 = new BundleEntryComponent();
		bundleEntry2.setFullUrl("Practitioner/Practitioner-01");
		bundleEntry2.setResource(ResourcePopulator.populatePractitionerResource());

		BundleEntryComponent bundleEntry3 = new BundleEntryComponent();
		bundleEntry3.setFullUrl("Organization/Organization-01");
		bundleEntry3.setResource(ResourcePopulator.populateOrganizationResource());

		
		BundleEntryComponent bundleEntry4 = new BundleEntryComponent();
		bundleEntry4.setFullUrl("Organization/Organization-02");
		bundleEntry4.setResource(ResourcePopulator.populateSecondOrganizationResource());

		
		BundleEntryComponent bundleEntry5 = new BundleEntryComponent();
		bundleEntry5.setFullUrl("Location/Location-01");
		bundleEntry5.setResource(ResourcePopulator.populateLocationResource());

		
		
		BundleEntryComponent bundleEntry6 = new BundleEntryComponent();
		bundleEntry6.setFullUrl("Coverage/Coverage-01");
		bundleEntry6.setResource(ResourcePopulator.populateCoverageResource());

		BundleEntryComponent bundleEntry7 = new BundleEntryComponent();
		bundleEntry7.setFullUrl("DocumentReference/DocumentReference-01");
		bundleEntry7.setResource(ResourcePopulator.populateDocumentReferenceResource());

		list.add(bundleEntry0);
		list.add(bundleEntry1);
		list.add(bundleEntry2);
		list.add(bundleEntry3);
		list.add(bundleEntry4);
		list.add(bundleEntry5);
		list.add(bundleEntry6);
		coverageEligibilityRequestBundle.setEntry(list);

		return coverageEligibilityRequestBundle;

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

	// Validation method will validate CoverageEligibilityRequestBundle against ABDM CoverageEligibilityRequestBundle FHIR
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
