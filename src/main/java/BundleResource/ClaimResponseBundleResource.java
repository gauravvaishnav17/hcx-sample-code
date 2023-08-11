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
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.ClaimResponse.ClaimResponseStatus;
import org.hl7.fhir.r4.model.ClaimResponse.ItemComponent;
import org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent;
import org.hl7.fhir.r4.model.ClaimResponse.RemittanceOutcome;
import org.hl7.fhir.r4.model.ClaimResponse.TotalComponent;
import org.hl7.fhir.r4.model.ClaimResponse.Use;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
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

public class ClaimResponseBundleResource {

	static FhirContext ctx = FhirContext.forR4();
	static FhirValidator validator;
	static FhirInstanceValidator fhirInstanceValidator;

	public static void main(String[] arg) throws Exception {

		// Initialize validation support and loads all required profiles
		init();

		// Populate ClaimResponseBundle resource
		Bundle ClaimResponseResourceBundle = populateClaimResponseBundleResource();

		

		if (validator(ClaimResponseResourceBundle)) {
			System.out.println("\nClaimReponseBundle is Succesfully Validated");
		}

	}

	// populate CliamResponse
	public static ClaimResponse populateClaimResponse(ClaimResponse.Use claimResponseUse) {

		ClaimResponse claimResponse = new ClaimResponse();

		// Set Id
		claimResponse.setId("ClaimResponseBundle-01");

		// set Meta
		claimResponse.getMeta().addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/ClaimResponse");

		// set Status
		claimResponse.setStatus(ClaimResponseStatus.ACTIVE);

		// set type
		claimResponse.setType(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/claim-type", "professional", "PROFESSIONAL")));

		// set Use
		switch (claimResponseUse) {
		case CLAIM:
			claimResponse.setUse(claimResponseUse);
			break;
		case PREAUTHORIZATION:
			claimResponse.setUse(claimResponseUse);
			break;
		case PREDETERMINATION:
			claimResponse.setUse(claimResponseUse);
			break;
		default:
			break;
		}

		// set patient
		claimResponse.getPatient().setReference("Patient/example-01");

		// set created date
		claimResponse.setCreated(new Date());

		// set insurer
		claimResponse.getInsurer().setReference("Organization/example-02");

		// set requestor
		claimResponse.getRequestor().setReference("Organization/example-01");

		// set request
		claimResponse.getRequest().setReference("Claim/preauth-example-01");

		// set outcome
		claimResponse.setOutcome(RemittanceOutcome.COMPLETE);

		// set disposition
		claimResponse.setDisposition(
				"The enclosed services are authorized for your provision within 30 days of this notice.");

		// set PreAuthRef
		claimResponse.setPreAuthRef("18SS12345");

		// set PayeeType
		claimResponse.setPayeeType(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/payeetype", "provider", "Provider")));

		org.hl7.fhir.r4.model.ClaimResponse.ItemComponent item = new org.hl7.fhir.r4.model.ClaimResponse.ItemComponent();
		item.setItemSequence(1);
		item.addAdjudication()
				.setCategory(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/adjudication",
						"eligible", "Eligible Amount")))
				.setAmount(new Money().setCurrency("INR").setValue(250000))
				.setReason(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/adjudication-reason",
						"ar002", "Plan Limit Reached")));

		claimResponse.addItem(item);

		PaymentComponent payment = new PaymentComponent();
		payment.setType(new CodeableConcept(
				new Coding("http://terminology.hl7.org/CodeSystem/ex-paymenttype", "complete", "Complete")));
		payment.setDate(new Date());
		payment.setAmount(new Money().setCurrency("INR").setValue(250000));

		claimResponse.setPayment(payment);

		// set total
		TotalComponent total = new TotalComponent();
		total.setCategory(new CodeableConcept(new Coding().setCode("submitted")));
		total.setAmount(new Money().setCurrency("INR").setValue(250000));
		claimResponse.addTotal(total);

		// set Insurance
		claimResponse.addInsurance().setSequence(1).setFocal(true).setCoverage(new Reference("Coverage/example-01"));

		return claimResponse;

	}

	// Populate Bundle
	public static Bundle populateClaimResponseBundleResource() {

		Bundle claimBundle = new Bundle();

		// set Id - Logical id of this artifact
		claimBundle.setId("ClaimBundle-preauth-example-01");

		// set Meta - Metadata about the resource
		Meta meta = new Meta();
		meta.setVersionId("1");
		meta.addProfile("https://nrces.in/ndhm/fhir/r4/StructureDefinition/ClaimResponseBundle");
		meta.addSecurity(
				new Coding("http://terminology.hl7.org/CodeSystem/v3-Confidentiality", "V", "very restricted"));
		claimBundle.setMeta(meta);

		// set Type - collection
		claimBundle.setType(BundleType.COLLECTION);

		// set Timestamp - When the bundle was assembled
		claimBundle.setTimestamp(new Date());

		// set Entry - Entry in the bundle - will have a resource or information
		List<Bundle.BundleEntryComponent> list = claimBundle.getEntry();
		org.hl7.fhir.r4.model.Claim.Use claimUse = null;
		Use claimResponseUse = null;

		System.out.println(
				"Please Choose the use for the ClaimResponse\nEnter 1 for CLAIM\nEnter 2 for PREAUTHRIZATION\nEnter 3 for PREDETERMINATION");
		Scanner sc = new Scanner(System.in);
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			claimUse = org.hl7.fhir.r4.model.Claim.Use.CLAIM;
			claimResponseUse = Use.CLAIM;
			break;
		case 2:
			claimUse = org.hl7.fhir.r4.model.Claim.Use.PREAUTHORIZATION;
			claimResponseUse = Use.PREAUTHORIZATION;
			break;

		case 3:
			claimUse = org.hl7.fhir.r4.model.Claim.Use.PREDETERMINATION;
			claimResponseUse = Use.PREDETERMINATION;
			break;
		default:
			System.out.println("Wrong input");
			break;
		}
		sc.close();

		BundleEntryComponent bundleEntry1 = new BundleEntryComponent();
		System.out.println();
		bundleEntry1.setFullUrl("ClaimResponse/ClaimResponse-01");
		bundleEntry1.setResource(populateClaimResponse(claimResponseUse));

		BundleEntryComponent bundleEntry2 = new BundleEntryComponent();
		bundleEntry2.setFullUrl("Patient/Patient-01");
		bundleEntry2.setResource(ResourcePopulator.populatePatientResource());

		BundleEntryComponent bundleEntry3 = new BundleEntryComponent();
		bundleEntry3.setFullUrl("Organization/Organization-01");
		bundleEntry3.setResource(ResourcePopulator.populateOrganizationResource());

		BundleEntryComponent bundleEntry4 = new BundleEntryComponent();
		bundleEntry4.setFullUrl("Organization/Organization-02");
		bundleEntry4.setResource(ResourcePopulator.populateOrganizationResource());

		BundleEntryComponent bundleEntry5 = new BundleEntryComponent();
		bundleEntry5.setFullUrl("Coverage/Coverage-01");
		bundleEntry5.setResource(ResourcePopulator.populateCoverageResource());

		BundleEntryComponent bundleEntry6 = new BundleEntryComponent();
		bundleEntry6.setFullUrl("Claim/Claim-example-01");
		bundleEntry6.setResource(ClaimBundleResource.populateClaimResource(claimUse));

		BundleEntryComponent bundleEntry7 = new BundleEntryComponent();
		bundleEntry7.setFullUrl("Coverage/Coverage-example-01");
		bundleEntry7.setResource(ResourcePopulator.populateCoverageResource());

		list.add(bundleEntry1);
		list.add(bundleEntry2);
		list.add(bundleEntry3);
		list.add(bundleEntry4);
		list.add(bundleEntry5);
		list.add(bundleEntry6);
		list.add(bundleEntry7);

		claimBundle.setEntry(list);

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
