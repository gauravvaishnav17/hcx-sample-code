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
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Claim.ClaimStatus;
import org.hl7.fhir.r4.model.Claim.DiagnosisComponent;
import org.hl7.fhir.r4.model.Claim.ItemComponent;
import org.hl7.fhir.r4.model.Claim.ProcedureComponent;
import org.hl7.fhir.r4.model.Claim.Use;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Reference;

import ResourcePropulator.ResourcePopulator;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

/**
 * The ClaimBundleResource class populates, validates, parse and serializes
 * Artifact - Claim
 */

public class ClaimBundleResource {

	static FhirContext ctx = FhirContext.forR4();
	static FhirValidator validator;
	static FhirInstanceValidator fhirInstanceValidator;

	public static void main(String[] arg) throws Exception {

		// Initialize validation support and loads all required profiles
		init();

		// Populate ClaimBundle resource
		Bundle ClaimResourceBundle = populateClaimBundleResource();

		if (validator(ClaimResourceBundle)) {
			System.out.println("\nClaimBundle is Succesfully Validated");
			
		}
	}

	//populate the claim resource
	public static Claim populateClaimResource(Use claimUse) {

		Claim claim = new Claim();

		// set Id - Logical id of this artifact
		claim.setId("Claim-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/Claim");
		claim.setMeta(meta);

		// set Identifier - Business Identifier for claim
		Identifier identifier = new Identifier();
		identifier.setSystem("http://happypharma.com/claim");
		identifier.setValue("7612345");
		claim.addIdentifier(identifier);

		// set Status - active | cancelled | draft | entered-in-error
		claim.setStatus(ClaimStatus.ACTIVE);

		// set Type - Category or discipline
		claim.setType(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/claim-type", "professional", "Professional")));

		// set Use - claim | preauthorization | predetermination
		switch (claimUse) {
		case CLAIM:
			claim.setUse(claimUse);
			break;

		case PREAUTHORIZATION:
			claim.setUse(claimUse);
			break;

		case PREDETERMINATION:
			claim.setUse(claimUse);
			break;
		default:
			break;
		}

		// set Patient - The recipient of the products and services
		claim.setPatient(new Reference("Patient/Patient-01"));

		// set Created - Resource creation date
		claim.setCreated(new Date());

		// set Insurer - Target
		claim.setInsurer(new Reference("Organization/Organization-02"));

		// set Provider - Party responsible for the claim
		claim.setProvider(new Reference("Organization/Organization-01"));

		// set prority - Desired processing ugency
		claim.setPriority(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/processpriority", "normal", "Normal")));

		// set supportingInfo - Supporting information
		claim.addSupportingInfo().setSequence(1)
				.setCategory(new CodeableConcept(
						new Coding("https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-supportinginfo-category", "POI",
								"proof of identity")))
				.setCode(new CodeableConcept(new Coding(
						"https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-identifier-type-code", "ADN", "Adhaar number")))
				.getValueReference().setReference("DocumentReference/DocumentReference-01");

		// set Procedure - Clinical procedures performed

		ProcedureComponent procedure = new ProcedureComponent();
		procedure.setSequence(1);
		procedure.setProcedure(new Reference("Procedure/Procedure-01"));
		claim.addProcedure(procedure);

		// set diagnosis - Pertinent diagnosis information

		DiagnosisComponent diagnosis = new DiagnosisComponent();
		diagnosis.setSequence(1);
		diagnosis.getDiagnosisCodeableConcept()
				.addCoding(new Coding("http://hl7.org/fhir/sid/icd-10", "I25.1", "Atherosclerotic heart disease"));
		diagnosis.addType(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/ex-diagnosistype",
				"clinical", "Clinical Diagnosis")));
		claim.addDiagnosis(diagnosis);

		// set Insurance - Patient insurance information
		claim.addInsurance().setSequence(1).setFocal(true).setCoverage(new Reference("Coverage/example-01"))
				.addPreAuthRef("1234");

		// set Item - Product or service provided
		ItemComponent item = new ItemComponent();
		item.setSequence(1);
		item.setCategory(
				new CodeableConcept(new Coding("http://snomed.info/sct", "225362009", "Cardiac surgery procedure")));
		item.setProductOrService(
				new CodeableConcept(new Coding("http://snomed.info/sct", "360046005", "Arterial stent")));
		item.addProgramCode(new CodeableConcept(new Coding("https://nrces.in/ndhm/fhir/r4/CodeSystem/ndhm-program-code",
				"AB-PMJAY", "Ayushman Bharat Pradhan Mantri Jan Arogya Yojana (AB-PMJAY)")));
		item.setUnitPrice(new Money().setCurrency("INR").setValue(250000));
		claim.addItem(item);

		return claim;

	}
	// populating ClaimBundle Resource
	public static Bundle populateClaimBundleResource() {

		Bundle claimBundle = new Bundle();

		// set Id - Logical id of this artifact
		claimBundle.setId("ClaimBundle-preauth-example-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/ClaimBundle");
		meta.addSecurity(
				new Coding("http://terminology.hl7.org/CodeSystem/v3-Confidentiality", "V", "very restricted"));
		claimBundle.setMeta(meta);

		// set Type - collection
		claimBundle.setType(BundleType.COLLECTION);

		// set Timestamp - When the bundle was assembled
		claimBundle.setTimestamp(new Date());

		// set Entry - Entry in the bundle - will have a resource or information
		List<Bundle.BundleEntryComponent> list = claimBundle.getEntry();
		Claim.Use claimUse = null;

		System.out.println(
				"Please Choose the use for the ClaimResponse\nEnter 1 for CLAIM\nEnter 2 for PREAUTHRIZATION\nEnter 3 for PREDETERMINATION");
		Scanner sc = new Scanner(System.in);
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			claimUse = Claim.Use.CLAIM;

			break;
		case 2:
			claimUse = Claim.Use.PREAUTHORIZATION;

			break;

		case 3:
			claimUse = Claim.Use.PREDETERMINATION;

			break;
		default:
			System.out.println("Wrong input");
			break;
		}

		BundleEntryComponent bundleEntry1 = new BundleEntryComponent();
		bundleEntry1.setFullUrl("Claim/Claim-01");
		bundleEntry1.setResource(populateClaimResource(claimUse));

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
		list.add(bundleEntry7);
		claimBundle.setEntry(list);

		sc.close();

		return claimBundle;

	}

	
	/**
	 * This method initiates loading of FHIR default profiles and NDHM profiles for
	 * validation
	 */
	public static void init() throws IOException {

		/*
		 * Load NPM Package containing ABDM FHIR Profiles Copy NPM Package.tgz
		 * (<package_name>.tgz) at "src/main/resource"
		 * Download Package : https://nrces.in/ndhm/fhir/r4/package.tgz
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
