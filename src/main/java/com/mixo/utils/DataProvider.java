package com.mixo.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public interface DataProvider {

	static Map<Integer, String> getSanctionConditionsMap() {
		Map<Integer, String> conditionsMap = new LinkedHashMap<>();
		conditionsMap.put(1,
				"All the sanction conditions to be fulfilled by the borrowers as per the term sheet and loan agreement.");
		conditionsMap.put(2,
				"The loan amount disbursed by the Lender shall be utilized solely for the purpose it is being sanctioned and no other purpose. The Lender reserves the right to call for any document/information for loan amount utilization by the Borrower.");
		conditionsMap.put(3,
				"The Borrower shall inform The Lender in writing about any change in the permanent /correspondence addresses or contact details (as the case may be) immediately.");
		conditionsMap.put(4,
				"The terms of this Sanction Letter summarize certain basic terms of the Loan facility. It is not the definitive/exhaustive list of all the conditions of the Loan facility. The detailed terms and conditions of the Loan facility are contained in the Loan Agreement.");
		conditionsMap.put(5,
				"The Parties agree that the Loan Agreement shall be read in conjunction with the Sanction Letter and in case of any conflict or inconsistencies, the provisions of the Sanction Letter shall prevail.");
		conditionsMap.put(6,
				"The Borrower shall be required to pay all duties, taxes, costs, stamp duty and statutory levies and such other charges, that may be imposed by the statutory authorities from time to time pertaining to or in connection with the Loan Documents.");
		conditionsMap.put(7,
				"The Borrower expressly agrees and acknowledges that the LSP is only acting as facilitator in obtaining the Loan from the Lender and for performing activities such as assisting in loan application, disbursal, collection etc., as may be authorized by the Lender. The final decision to whether approve or reject any Loan or disburse the Loan (in part or full) or rescind/withdraw the Sanction Letter, with or without assigning any reasons, resides to the sole discretion of the Lender. LSP does not have any influence over the Lender’s decision.");
		conditionsMap.put(8,
				"During the Tenure of the Loan, the Borrower authorizes the Lender/LSP to obtain/access the credit information of the Borrower from time to time from the credit bureaus. Any default in the payment of the Loan may be reported by the Lender to the credit bureau, regulators, statutory authorities and other financial institutions. The Borrower acknowledges and accepts that on receipt of the one-time password, access code or other forms of secure authentication initiated through Lender app, this Sanction Letter and the Loan Agreement annexed as Annexure A shall be deemed to be executed/accepted by the Borrower. It is the sole responsibility of the Borrower to ensure that the one-time password, access code or other forms of secure authentication is not compromised or shared with any other person.");
		conditionsMap.put(9, "Interest will be charged on disbursement amount from the date of first disbursement.");
		conditionsMap.put(10,
				"In case of Prepayment in part or full, prepayment Charges as stated above will be charged from the Borrowers.");
		conditionsMap.put(11,
				"The borrower and/or Guarantor should give their consent for disclosure of credit information to CIBIL/RBI in terms of directions issued by RBI.");
		conditionsMap.put(12, "This sanction letter is valid for a period of 7 working days.");
		return conditionsMap;
	}

	static Map<Integer, String> getForKFS1SanctionConditionsMap() {
		Map<Integer, String> conditionsMap = new LinkedHashMap<>();
		conditionsMap.put(1,
				"The loan amount disbursed by the Lender shall be utilized solely for the purpose it is being sanctioned and no other purpose. The Lender reserves the right to call for any document/information for loan amount utilization by the Borrower.");
		conditionsMap.put(2,
				"The sanction and disbursal of the Loan Amount is subject to execution/acceptance of the Loan Agreement annexed as Annexure A, registration of E-NACH for repayments and other conditions as per Loan Agreement as per Annexure A.");
		conditionsMap.put(3,
				"The Borrower shall inform Lender in writing about any change in the permanent /correspondence addresses or contact details (as the case may be) immediately.");
		conditionsMap.put(4,
				"The terms of this Sanction Letter summarize certain basic terms of the Loan facility. It is not the definitive/exhaustive list of all the condition of the Loan facility. The detailed terms and conditions of the Loan facility is contained in the Loan Agreement annexed as Annexure A.");
		conditionsMap.put(5,
				"The Parties agree that the Loan Agreement shall be read in conjunction with the Sanction Letter and in case of any conflict or inconsistencies, the provisions of the Sanction Letter shall prevail.");
		conditionsMap.put(6,
				"Notwithstanding the issuance of this Sanction Letter and acceptance thereof, the Lender may in its sole discretion decide to not disburse the Loan, rescind/withdraw Sanction Letter, without assigning any reasons");
		conditionsMap.put(7,
				"The Borrower shall be required to pay all duties, taxes, costs, stamp duty and statutory levies and such other charges, that may be imposed by the statutory authorities from time to time pertaining to or in connection with the Loan Documents.");
		conditionsMap.put(8,
				"The Borrower expressly agree and acknowledge that the LSP is only acting as facilitator in obtaining the Loan from the Lender and for performing activities such as assisting in loan application, disbursal, collection etc., as may be authorized by the Lender. The final decision to whether approve or reject any Loan or disburse the Loan (in part or full) or rescind/withdraw the Sanction Letter, with or without assigning any reasons, reside to the sole discretion of the Lender. LSP does not have any influence over the Lender’s decision.");
		conditionsMap.put(9,
				"During the Tenure of the Loan, the Borrower authorizes the Lender to obtain/access the credit information of the Borrower from time to time from the credit bureaus. Any default in the payment of the Loan may be reported by Lender to credit bureau, regulators, statutory authorities and other financial institution.");
		conditionsMap.put(10,
				"The Borrower acknowledges and accepts that on receipt of the one-time password, access code or other forms of secure authentication initiated through the Parfi Platform, this Sanction Letter and the Loan Agreement annexed as Annexure A shall be deemed to be executed/accepted by the Borrower. It is the sole responsibility of the Borrower to ensure that the one-time password, access code or other forms of secure authentication is not compromised or shared with any other person.");
		return conditionsMap;
	}

	static Map<Integer, String> getFFS1SanctionConditionsMaps(String lspName, String lenderName) {
		Map<Integer, String> conditionsMap1 = new LinkedHashMap<>();
		conditionsMap1.put(11,
				"The Lender and the LSP shall have no obligation to verify the authenticity of any transaction or instruction received or purported to have been received from the Borrower through the Parfi Platform or purporting to have been sent by the Borrower other than by means of verification of the one-time password, access code or other forms of secure authentication");

		conditionsMap1.put(12,
				"All the records of the Lender and the LSP with respect to the online request for Loan facility arising out of the use of the Parfi Platform and arising out of the use of the one-time password, access code or other forms of secure authentication shall be conclusive proof of the genuineness and accuracy of the transaction and shall be binding on the Borrower."
						+ lenderName
						+ " (hereinafter referred to as 'Loan Service Provider' or 'LSP'). The terms and conditions of Loan Agreement and/or other documents will prevail upon this letter in case of any contradiction/ conflict/ difference.");
		return conditionsMap1;
	}

	static Map<Integer, String> getSanctionConditionsMaps(String lspName, String lenderName) {
		Map<Integer, String> conditionsMap1 = new LinkedHashMap<>();
		conditionsMap1.put(13,
				"The sanction of loan amount and its terms and conditions are subject to execution of Loan Agreement and other documents and writings with Mufin Green Finance Limited  (hereinafter referred to as 'The lender') and "
						+ lenderName
						+ " (hereinafter referred to as 'Loan Service Provider' or 'LSP'). The terms and conditions of Loan Agreement and/or other documents will prevail upon this letter in case of any contradiction/ conflict/ difference.");
		conditionsMap1.put(14,
				"In case pre–EMI Interest is applicable, the same would be deducted from the net disbursement amount.");
		conditionsMap1.put(15,
				"Disbursement of loan is subject to verification of all the documents provided by the Borrower, verification of residential and office address of Borrower, Co Borrower.");
		return conditionsMap1;
	}

	String CONDITION_LIST_END = "If the terms & conditions as stated above are accepted by you, kindly sign the acceptance copy thereof in token of your acceptance and return to us immediately. We look forward to your acceptance to this letter to expedite the conclusion of this transaction and disbursement there under.";

	default String declarationFirst(String lspName, String lenderName) {
		return "1.	I/We hereby apply on the " + lenderName
				+ " (“LSP”) platform for a Facility in the nature of a accrued salary advance (“Personal Loan”) mentioned in this application. I/We declare that all the particulars and information and details given/filled in this Application Form are true, correct, complete and up-to date in all respects and no information has been withheld. I/We understand that the information given in this application shall form the basis of any loan that "
				+ lenderName
				+ " (the “Lender ”) may decide to grant to me/us and if at any stage of processing this application, it comes to the knowledge of the Lender that, I/we have provided any incorrect or incomplete information, fabricated documents, or fake documents, they will be treated by the Lender as having been manipulated by me/us and the Lender shall have the right to forthwith reject this loan application, cancel / revoke any sanction or further drawdowns or recall any loan granted at any stage of processing the application, without assigning any reason whatsoever and the Lender and its employees/ representatives/ agents / service providers shall not be responsible/liable in any manner whatsoever to me/us for such rejection or any delay in notifying me/us of such rejection (including for any payments which may have been made by me/us to any vendor/ service provider prior to cancellation). I/We understand that the Lender will also be procuring personal information from other sources/agents and I/We have no objection for the same. I/We further confirm that I/we am/are aware of all terms and conditions of availing finance from the Lender. I/We authorize the Lender to make reference and inquire relating to information in this application which the Lender considers necessary, including from the banks where I/we hold bank accounts. I/We authorize the Lender to procure my /our PAN No/copy of my/our PAN Card, other identity/address proof and Bank Account details from time to time, exchange, part with/share all information relating to my/our loan details and repayment history with other banks/financial institutions etc. and periodically obtain / generate bureau reports and such other reports as may be required and shall not hold the Lender liable for use of this information. I/We confirm that there are no criminal or insolvency proceedings against me/us. ";
	}

	String DECLARATION_SECOND = "2.	I/ We declare that I/ We have not made any payment in cash, bearer’s cheques or by any other mode along with or in connection with this Application Form to the person collecting my/our Application Form. I/ We shall not hold the Lender or its employees/representatives/agents/service providers liable for any such payment made by me/us to the person collecting this Application Form. ";

	String DECLERATION_THIRD = "3.	I/We, would like to know through telephonic calls, or SMS on my mobile number mentioned in the Application Form as well as in this undertaking, or through any other communication mode, transactional information, various loan offer schemes or loan promotional schemes or any other promotional schemes which may be provided by the Lender or LSP, and hereby authorize the Lender /LSP. and their employee, agent, associate to do so. I/We confirm that laws in relation to the unsolicited communication referred in “National Do Not Call Registry” (the “NDNC Registry”) as laid down by TELECOM REGULATORY AUTHORITY OF INDIA will not be applicable for such communication/calls/ SMSs received from the Lender /LSP, its employees, agents and/or associates. I/We, acknowledge that the Lender and LSP are independent of each other and I/we will not have any claim against the Lender for any loan or other facility arranged/ provided by LSP, which is not sanctioned/ disbursed by the Lender. I/We acknowledge that the Lender does not in any manner make any representation, promise, statement or endorsement in respect of any other product of services which may be provided by LSP, and will not be responsible or liable in any manner whatsoever for the same. ";

	String DECLERATION_FOURTH = "4.	I/We hereby expressly and irrevocably authorize the Lender to collect, store, share, obtain and authenticate my/our basic personal information (name, address, contact details) and KYC details that is required for availing the Loan Facility. Further, I/we expressly permit the Lender to authorize LSP and its agents to only access, use, verify and authenticate my/our basic personal information (name, address, contact details) and KYC details on behalf of the Lender with my/our explicit consent. ";

	String DECLERATION_FIFTH = "5.	I/we understand that I/we have an option of not providing the information as required in this application form or as may be required by the Lender from time to time, however, I/We do hereby expressly agree that this may affect my/our ability to avail the Loan Facility. ";

	String DECLERATION_SIXTH = "6.	I/We confirm that I/we have read and understood and accepted the terms and conditions provided under the Personal Loan Agreement for the grant of Loan Facility and the Lender shall be entitled to take such legal action as it may deem fit, upon occurrence of any of the Events of Default asset out and agreed by me/us in the under mentioned Agreement.";

	String PDC_CH_SWAP_CHARGES = "PDC/CH Swap Charges";
	String FULL_PART_PREPAYMENT_CHARGES = "Full / Part Prepayment Charges";
	String DUPLICATE_STATEMENT_CHARGES = "Duplicate Statement Charges";
	String EMI_FOLLOW_UP_CHARGES = "EMI Follow up Charges";
	String STAMP_DUTY_CHARGES = "Stamp Duty Charges";
	String ANY_OTHER_CHARGES = "Any Other Charges";
	String NA = "NA";

	String PERSONAL_LOAN_AGG_1 = "This Personal Loan Agreement (Salary Advance) (the “Agreement”) is made on the date and at the place as stated in the Schedule 1 hereto between:\r\n"
			+ "The Borrower(s), whose name and address are stated in Schedule 1 hereto, (hereinafter referred to as ’Borrower’ which expression which expression shall unless repugnant to the context or meaning there of be deemed to mean and include its successors and permitted assigns) of the FIRST PART;\r\n"
			+ "AND\r\n" + "";

	String PERSONAL_LOAN_AGG_KFS1_1 = "This Personal Loan Agreement (the “Agreement”) is made on the date and at the place as stated in the Schedule 1 hereto between:\r\n"
			+ "The Borrower(s), whose name and address are stated in Schedule 1 hereto, (hereinafter referred to as ’Borrower’ which expression which expression shall unless repugnant to the context or meaning there of be deemed to mean and include its successors and permitted assigns) of the FIRST PART;\r\n"
			+ "AND\r\n" + "";

	default String personalLoanAgg2(String lenderName, String cin, String address) {
		return "Mufin Green Finance Limited non-banking financial company,registered with Reserve Bank of India and incorporated under the laws of India and having its registered office DELHI (hereinafter referred to as the “Lender”, which expression shall unless repugnant to the context or meaning there of be deemed to mean and include its successors and assigns) of the SECOND PART;\r\n"
				+ "AND \r\n" + "";
	}

	default String personalLoanAgg3(String lspName, String cin, String address) {
		return lspName + ", a company incorporated under the provision of the Companies Act, 2013 with CIN: " + cin
				+ ", having its registered office at "+address+", (hereinafter referred to as the “Lending Service Provider\" or “LSP” or “"+lspName+"” which expression shall, unless it be repugnant to the meaning or context thereof, mean and include, its successors and permitted assigns) of the THIRD PART.";
	}

	String PERSONAL_LOAN_AGG_4 = "The Borrower(s), Lender and LSP shall be referred to individually as a “Party” and collectively as the “Parties”. \r\n"
			+ "\r\n";

	String PERSONAL_LOAN_AGG_5 = "A.	The Lender is a non-banking financial corporation registered with the RBI (defined hereinafter) under the Banking Regulation Act, 1949 and engaged in the business of providing various financial services/loans facility to other individuals, companies engaged in different kinds of finance and non-finance activities;\r\n"
			+ "B.	The LSP is inter alia engaged in the business of assisting financial institutions promote their financial products to individuals who may or may not be clients of the financial institution, through the use of their platform;\r\n"
			+ "C.	The Borrower(s) has requested the has requested the Lenders to sanction an Accrued Salary Advance Facility for an aggregate amount as mentioned in the Schedule of this Agreement (“Facility”).\r\n"
			+ "D.	Further, replying upon the representations made by the Borrower(s), the Lender has agreed to make available Loan facility to the Borrower(s) upon the terms and conditions mentioned hereinafter.\r\n"
			+ "\r\n";

	String PERSONAL_LOAN_KFS1_AGG_5 = "A.	The Lender is a non-banking financial corporation registered with the RBI (defined hereinafter) under the Banking Regulation Act, 1949 and engaged in the business of providing various financial services/loans facility to other individuals, companies engaged in different kinds of finance and non-finance activities;\r\n"
			+ "B.	The LSP is inter alia engaged in the business of assisting financial institutions promote their financial products to individuals who may or may not be clients of the financial institution, through the use of their platform;\r\n"
			+ "C.	The Borrower(s) has requested the has requested the Lenders to sanction an Accrued Salary Advance Facility for an aggregate amount as mentioned in the Schedule of this Agreement (“Facility”).\r\n"
			+ "D.	Further, replying upon the representations made by the Borrower(s), the Lender has agreed to make available Loan facility to the Borrower(s) upon the terms and conditions mentioned hereinafter.\r\n"
			+ "\r\n";

	String NOW_THERE = "NOW THEREFORE IT IS HEREBY AGREED BY AND BETWEEN THE PARTIES AS FOLLOWS: ";

	default String defination_1_point_1_and_2(String employee) {
		return "1.1.	The terms and expressions contained in this Agreement are defined as under:  \r\n"
				+ "1.1.1.	“Accrued Salary Advance Facility” or “Facility” or “Loan” shall mean the loan offered by the Lender to the Employees of "
				+ employee + " through the Loan Application;\r\n"
				+ "1.1.2.	“Additional Interest” or “Default Interest” or “Penal Interest” means such rate of interest as may be specified in the Schedule hereunder, applicable on outstanding EMI(s), in case of delay/default in payment of such outstanding EMI(s) by the Borrower(s);\r\n"
				+ "";
	}

	default String defination_1_KFS1_point_1_and_3(String employee) {
		return "1.1	“Agreement” means this Agreement, and shall include any schedule, annexure, appendix, any and all amendments, additions, deeds, undertakings, declarations and alteration whether by way of a supplemental agreement or otherwise to the terms contained herein;  \r\n"
				+ "1.2	“Borrower(s)”: shall, mean and include any person specified as Borrower in this agreement and unless repugnant to the context or meaning thereof be deemed to include his / her / its/ their heirs, legal representatives, successors, permitted assigns, executors, receivers, administrators as the case may be;\r\n"
				+ "1.3	“Cooling Off Period”- shall mean the period as during which the Loan Amount can be repaid by the Borrower, without any prepayment/foreclosure charges;;\r\n"
				+ "";
	}

	default String defination_1_KFS1_point_4_and_5(String lenderName) {
		return "1.4	“Digital Lending Application” shall refer to all website (including all associated site links) and/or mobile based application/platform namely "
				+ lenderName + " Platform owned, developed and operated by the LSP from time to time;  \r\n"
				+ "1.5	“Instalment Amount” means the amount to be paid by the Borrower at such frequency for repayment of the interest only or principal Loan Amount along with applicable Rate of Interest (as applicable depending upon the Repayment options opted by the Borrower);\r\n"
				+ "1.6	“Instalment Frequency” means the frequency of the payment of the Instalment Amount for the repayment of the Loan Amount;\r\n"
				+ "1.7	“Rate of Interest” means rate of interest per annum applicable on the Loan Amount\r\n"
				+ "1.8	“Loan Amount” means the loan amount to be availed by the Borrower in single or multiple tranches accordance with this Agreement\r\n"
				+ "1.9	“Loan Documents” means, collectively, this Agreement and KFS and such other documents as may be required to be executed with reference to Loan Amount being availed by the Borrower\r\n"
				+ "1.10	 “Lending Service Provider” shall refer to the party specified as in the KFS;\r\n"
				+ "1.11	 “Material Adverse Change” shall mean any one or more events, conditions or circumstances which, in the opinion of Lender, could reasonably be expected to adversely affect the financial condition of the Borrower or the ability of the Borrower to perform or comply with its material obligations under the Loan Documents;\r\n"
				+ "1.12	 “Payment Instrument” shall mean NACH debit mandate/NEFT/RTGS/cheques/ any instruction(s) issued/registered by the Borrower for repayment of the outstanding dues under this Agreement (including but not limited to Loan Amount along with Rate of Interest, Default Interest and/or any other charges) through electronic mode or otherwise\r\n"
				+ "1.13	 “Default Interest” shall mean additional interest charged by the Lender over and above the Rate of Interest due to the delay in payments by the Borrower and/ or occurrence of breach of other obligations/covenants under the Loan Documents\r\n"
				+ "";
	}

	default String defination_1_KFS1_point_14_and_5(String lenderName) {
		return "1.14	 “Sanction Letter” shall mean letter/document issued by the Lender (either on its own and/or through LSP) to the Borrower which contains the terms and condition of the Loan and which forms part of the Loan Documents;\r\n"
				+ "1.15	 “RBI” Means the Reserve Bank of India, established under the Reserve Bank of India Act, 1934\r\n"
				+ "1.16	 “Tenure” means the period in which loan is to be repaid by the Borrower along with interest as per applicable Rate of Interest and other charges as per the Loan Documents\r\n"
				+ "1.17	“Settlement Amount” means the total amount collected and payable (after adjustment of the applicable service charges) by the LSP to the Borrower on account of transactions carried out on UPI, POS machines and/or or such other products offerings made available to the Borrower through "
				+ lenderName + " Platform\r\n" + "";
	}

	default String defination_1_point_1_to_1_point_9(String employeeName) {
		return "1.1.3.	“Application” shall mean any application which includes mobile applications available on Android and IOS devices, as also web browser-based applications and which is developed by the Lender and offered by "
				+ employeeName + " to it’s Employees for availing Accrued Salary Advance Facility;\r\n"
				+ "1.1.4.	“Application Form” means the Facility application form submitted by the Borrower(s) to the Lenders for applying and availing the Facility, together with all other information particulars, clarifications and declarations, if any, furnished by the Borrower(s) or any other person from time to time in connection with the Facility;\r\n"
				+ "1.1.5.	“Applicable Law” means any statute, national, state, provincial, local, municipal, or other law, treaty, code, regulation, ordinance, rule, judgment, order, decree, bye-law, any act or enactment including but not limited to the Insolvency and Bankruptcy Code, 2016 ( “Code”), Negotiable Instruments Act, 1881 (as amended from time to time) and The Payment and Settlement Systems Act, 2007 (as may be amended from time to time),  approval of any Governmental Authority, directive, guideline, policy, requirement or other governmental restriction or any similar form of decision of or determination by, or any interpretation or administration having the force of law of any of the foregoing by any Governmental Authority having jurisdiction over the matter in question, whether in effect as of the date of this Agreement or at any time thereafter;\r\n"
				+ "1.1.6.	“Borrower(s)” shall mean any and every Employee of " + employeeName
				+ " who has availed the Accrued Salary Advance Facility through the Application; means the borrower as specified in the Schedule herein;\r\n"
				+ "1.1.7.	“Borrower's Dues” means all sums payable by the Borrower to the Lender, including outstanding loan amount, interest, all other charges, costs and expenses; \r\n"
				+ "1.1.8.	“Business Day” shall mean a day of the week on which the Lenders and banks are generally open for business such state or union territory of India where the office of the Lenders, as specified in Schedule, is located;\r\n"
				+ "1.1.9.	“Companies Act” means individually and collectively, such relevant provisions of the Companies Act, 1956, which are still in force and effect and those provisions of the Companies Act, 2013, which have been notified and are in full force and effect, and all amendments, enactments, re-enactments or modifications thereof, from time to time, including the rules and regulations prescribed therein; ";
	}

	default String defination_1_point_9_to_1_point_14(String employeeName) {
		return "1.1.10.	“Conditions Precedent” means the conditions as specified in Clause 3 of this Agreement which are required to be satisfied (unless waived in writing by the Lender) by the Borrower(s) before Disbursement of the Facility;\r\n"
				+ "1.1.11.	“Credit Information Agency” shall mean and include TransUnion CIBIL Limited, CRIF High Mark Credit Information Services Private Limited, Equifax India and Equifax Analytics Private Limited, Experian Credit Information Company of India Private Limited or such other company or any other agency as may be notified or recognized as such by the RBI or any other regulatory authority;\r\n"
				+ "1.1.12.	“Disbursement” means the disbursement by the Lender to the Borrower(s) in accordance with the terms and conditions of this Agreement;\r\n"
				+ "1.1.13.	“Due Date” means the date(s) on which any amounts in respect of the Facility including the principal, interest or other monies fall due under the terms of this Agreement and/or the other Financing Documents and/or as more particularly described under the Schedule hereunder;\r\n"
				+ "1.1.14.	“Electronic Clearing Service” or “ECS” or “NACH” or “ENACH” means the electronic clearing services and electronic payment services, notified by RBI from time to time, being mode(s) of effecting payment transactions using the services of a clearing house or any other platform or mechanism duly authorized in this regard including without limitation the National Electronic Clearing Service;\r\n"
				+ "";
	}

	default String defination_1_point_15_to_1_point_21(String employeeName) {
		return "1.1.15.	“Employer” shall mean the current employer of the Borrower with whom the Borrower is in employment and has availed this Facility; for the purpose of this Agreement Employer shall mean "
				+ employeeName + ". \r\n"
				+ "1.1.16.	“Employee” shall mean the employees of the Company who are in a full-time employment of the Employer and have a fixed salary structure; \r\n"
				+ "1.1.17.	“Encumbrance” shall mean creation of charge, lien, security, quasi security, non-disposal arrangement, claim, option, negative lien, power of sale in favour of a third party, retention of title, right of pre-emption, right of first refusal, lock-in of any nature including as may be stipulated by the Securities Exchange Board of India, or other third party right or security interest or an agreement, arrangement or obligation to create any of the foregoing whether presently or in the future. The term “Encumbrancer” or “Encumbered” shall be accordingly construed;\r\n"
				+ "1.1.18.	“Event of Default” shall have the meaning prescribed to it under Clause 8 herein;\r\n"
				+ "1.1.19.	“Facility Agreement” or “Agreement” means this agreement, all Schedules and amendments to this Agreement;\r\n"
				+ "1.1.20.	“Financing Documents” means this Agreement, the Loan Application, the Schedules, Sanction Letter, Key Fact Statement including any other annexures hereto and any documents executed by the Borrower or as required by the Lender, as amended from time to time; \r\n"
				+ "1.1.21.	“Governmental Approval” shall mean any consent, approval, authorization, waiver, permit, grant, franchise, concession, agreement, license, certificate, exemption, order registration, declaration, filing, report or notice of, with or to any Governmental Authority;\r\n"
				+ "";
	}

	default String defination_1_point_22_to_1_point_29(String lspName) {
		return "1.1.22.	“Governmental Authority” means any:\r\n"
				+ "(a)	government (central, state or otherwise) or sovereign state; and\r\n"
				+ "(b)	any governmental agency, semi-governmental or judicial or quasi-judicial or administrative entity, department or authority, or any political subdivision thereof;\r\n"
				+ "(c)	including, without limitation, any stock exchange or any self-regulatory organization, established under any Applicable Law;\r\n"
				+ "1.1.23.	“Installments” or “EMI” shall mean the monthly payment obligations of the Borrower(s) to repay the Facility Amount and Interest accrued on the outstanding Facility Amount to the Lender, which shall be payable by the Borrower(s) to the Lender, at the intervals speciﬁed in the Repayment Schedule;\r\n"
				+ "1.1.24.	“Interest” or “Rate of Interest” means the interest charged on the Loan Facility as set out in Schedules, subject to variation from time to time as provided herein; \r\n"
				+ "1.1.25.	“Loan Application” means the application in the prescribed form as submitted from time to time by the Borrower to the Lender for seeking financing; \r\n"
				+ "1.1.26.	“Lending Service Provider” shall mean "+lspName+" who is providing its platform to the Lender for the purposes as defined herein in this Agreement;\r\n"
				+ "1.1.27.	“Market Disruption Event” shall mean happening of an event which in the opinion of the Lenders would make it unviable for the Lender to continue the Facility;\r\n"
				+ "1.1.28.	“Material Adverse Effect” means any event or circumstance, which in the opinion of the Lender:\r\n"
				+ "(a)	is likely to materially and adversely affect the Borrower's ability to pay the Borrower's Dues; \r\n"
				+ "(b)	is likely to materially and adversely affect the Borrower's ability to perform or otherwise comply with all or any of its.\r\n"
				+ "(c)	obligations under the Financing Documents; \r\n"
				+ "(d)	is likely to materially or adversely affect the employment, condition (financial or otherwise) or prospects of the Borrower; or \r\n"
				+ "(e)	is likely to result in any Financing Document not being legal, valid and binding on, and enforceable in accordance with its terms against, the Borrower. "
				+ "1.1.29.	“NPA” means Non-Performing Asset as per DOR.STR.REC.3/21.04.048/2023-24 dated April 01, 2023 on Master Circular - Prudential norms on Income Recognition, Asset Classification and Provisioning pertaining to Advances, as amended from time to time. ";
	}

	default String defination_1_point_30_to_1_point_35() {
		return "1.1.30.	“Outstanding Amounts” means at any time all the amounts payable in relation to the Facility by the Borrower(s) to the Lender, pursuant to the terms of the Financing Documents, including the following amounts:\r\n"
				+ "(a)	the principal amount of the Facility and Interest thereon;	\r\n"
				+ "(b)	all outstanding’s as defined under this Agreement from time to time;\r\n"
				+ "(c)	all other moneys, debts and liabilities of the Borrower(s), including indemnities, liquidated damages, costs, Charges, expenses and fees and interest incurred under, arising out of or in connection with the Financing Documents;\r\n"
				+ "(d)	any and all costs, expenses, fees and duties for the enforcement and collection of any amounts due under the Financing Documents, including expenses of enforcement and costs and other expenses set out in this Agreement\r\n"
				+ "1.1.31.	“Penal Charges” means the default charges as prescribed in the Key Fact Statement, Sanction Letter and Loan Agreement which is payable on all amounts that are not paid on their respective Due Dates; \r\n"
				+ "1.1.32.	“Purpose” means the reason for availing the Loan Facility from the Lender. \r\n"
				+ "1.1.33.	“Receivables” shall mean the receivables as more particularly described under the Schedule to this Agreement;\r\n"
				+ "1.1.34.	“Repayment” shall mean the repayment and/or the prepayment of the principal amount of the Facility, interest thereon, commitment and other charges, premium, fees or other dues payable by the Borrower(s) in accordance with this Agreement. The words “Repay”, and “Repaid” shall be construed accordingly;\r\n"
				+ "1.1.35.	“Repayment Schedule” shall mean the manner in which the Repayment shall be made by the Borrower(s) including details like due dates, Instalment amount etc. prepared in line with the details specified in the Schedule to this Agreement as may be revised from time to time and communicated to the Borrower(s). Any revision to the Repayment Schedule in terms of this Agreement shall be the applicable Repayment Schedule and shall be deemed to have been incorporated in the Schedule and Schedule to this effect be deemed to have been modified;\r\n"
				+ "";
	}

	default String definationKFS1_1_point_30_to_1_point_35(String brandName) {
		return "2.1	The Lender shall not make any disbursement until the Loan documents have been duly executed by the Borrower to the satisfaction of the Lender.\r\n"
				+ "2.2	The Borrower undertakes that the Loan Amount shall be utilized for lawful purpose. The Lender reserves the right to call for any document/information for loan amount utilization by the Borrower.\r\n"
				+ "2.3	The disbursement of the Loan Amount to the Borrower shall be made in the bank account of the Borrower or any as per his/her instructions in writing.\r\n"
				+ "2.4	The Borrower undertakes that the Payment Instrument issued/registered by the Borrower for the repayment of the Loan Amount shall not be cancelled/ stopped under any circumstances during the Tenure of the loan or till the entire dues as per this Agreement are fully paid and a discharge/no dues certificate is issued to the Borrower, whichever is later\r\n"
				+ "2.5	The Borrower undertakes to give any other documents/details such as KYC documents, financial documents etc.as the Lender may demand from time to time in order to review its Loan\r\n"
				+ "2.6	The Borrower agrees that the Lender has adopted risk-based pricing which is arrived at after considering broad parameters like customer profile, financials, sources of funds, risk profile of the customer, nature of lending etc. and hence rate of interest may differ across Borrowers\r\n"
				+ "2.7	Under this Loan Agreement, the Lender has authorized the LSP to perform the following activities as part of the facilitation services (“Loan Facilitation Services”) through "
				+ brandName
				+ " Platform: (a) Assisting the borrower making an application for the Loan from the Lender and submission of the required documents for availing the Loan (b) assisting in disbursement of the Loan through (c) assisting in collections for the Loan through "
				+ brandName
				+ " Platform or such other means as may be authorized by the Lender (d) assisting in loan servicing such as providing details of the loans/repayments/due amounts to the Borrower and/or resolving any queries/complaints/clarifications with respect to Loan and/or coordinate with the Lender for providing any information with respect to the Loan (e) act as a recovery agent for contacting the Borrower in case of default in repayment of the Loan (f) such other Loan Facilitation Services as may be agreed between the Lender and LSP from time to time and notified to the Borrower.";
	}

	default String definationKFS1_1_for_Rate_point_30_to_1_point_35(String brandName) {
		return "3.1.	The Borrower shall pay the Rate of Interest as specified in the KFS & Sanction letter on the Loan Amount\r\n"
				+ "3.2.	The Rate of Interest on the Loan Amount shall begin to accrue simultaneously in favour of Lender with the disbursement of the Loan Amount in the bank account specified in sanction letter without concerning itself delay in actual realization in the bank account of the Borrower due to any technical or other issues\r\n"
				+ "3.3.	The Borrower agrees and acknowledges that the Lender shall, at any time and from time to time, be entitled to change the Rate of Interest, at its sole discretion. In such an event the term 'Rate of Interest' shall for all purposes mean the revised interest rate, which shall always be construed as agreed to be paid by the Borrower.\r\n"
				+ "3.4.	The Borrower acknowledges and understands that the Lender may notify the Borrower about any revision in Rate of Interest or any of the fee/ charges through: (a) A letter or email at the address provided by the Borrower(s) to the Lender and/or through LSP; (b) SMS or any other electronic/ telephonic message on the telephone/ mobile number registered by the Borrower(s).\r\n"
				+ "3.5.	Any changes in Rate of Interest and charges are effective prospectively, with effect from the date mentioned in the notice/ communication to the Borrower.\r\n"
				+ "\r\n" + "";
	}

	default String definationKFS1_1_for_Repayment_point_30_to_1_point_35(String brandName) {
		return "5.1.	During the Tenure of the loan, the Borrower shall repay the Loan Amount by making payments of Instalment Amount as per the Instalment Frequency specified in the KFS. The Borrower understands and acknowledges that timely payment of the Instalment Amount shall be the essence of the Loan Documents executed between the Parties\r\n"
				+ "5.2.	The Borrower shall be entitled to a Cooling Off Period as specified in the KFS. During the Cooling Off period, the Borrower shall have the right to foreclose the Loan sanctioned to it, without any prepayment/foreclosure charges as specified in KFS. The Borrower shall be liable to pay the applicable proportionate interest/charges/fees (other than prepayment/foreclosure charges) as per the KFS during the Cooling Off period.\r\n"
				+ "5.3.	Without prejudice and/ or waiver of the Lender’s rights, in the event, that the Settlement Amount is not sufficient for deduction of the Instalment Amount and/or there is any shortfall in Instalment Amount, the Borrower undertakes and agrees\r\n"
				+ "(i)	To pay the outstanding due amount under the Loan Documents\r\n"
				+ "(ii)	To pay Default Interest for such period until all the outstanding due amount are repaid by the Borrower(s)\r\n"
				+ "5.4.	In the event, the aforesaid repayment mode (i.e., deduction from Settlement Amount) is not applicable or available, the Borrower shall make payment of the Instalment Amount as per the Instalment Frequency through any other mode such as NEFT, UPI, debit card, net banking or any such other mode made available through the LSP.\r\n"
				+ "5.5.	If the Borrower fails to pay any amount when due, under the Loan Documents and/or the Borrower’s Payment Instrument is dishonoured, such unpaid amount shall bear Default Interest from the date such amount is due until the date on which such amount is paid in full\r\n"
				+ "5.6.	The Borrower shall be required to pay all duties, taxes, costs, stamp duty and statutory levies and such other charges, that may be imposed by the statutory authorities from time to time pertaining to or in connection with under the Loan Documents.";
	}

	default String definationKFS1_1_for_INSTER_point_30_to_1_point_35(String brandName) {
		return "4.1.	Upon occurrence of any of the Events of Default as specified in Clause 9, the Borrower shall be liable to pay Default Interest as specified in the Sanction letter for any default/delay in repayment of the Loan Amount and/or breach of any of the conditions of the Loan Documents\r\n"
				+ "4.2.	The payment of the Default Interest shall not absolve the Borrower of the other obligations or shall not affect any of the other rights of the Lender, under the Loan Documents\r\n"
				+ "4.3.	The Lender expressly reserves all the other rights that may accrue to it on any default by the Borrower as per the Loan Documents.\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I666_point_30_to_1_point_35(String brandName) {
		return "6.1.	All amounts received from the Borrower shall be applied in the following order of priority: (A) firstly towards fees, costs, charges and expenses of the Lender, (B) secondly, towards any fee, charges, Default Interest and other delay charges, (C) thirdly, towards payment of any outstanding interest on the Loan, (D) fourthly, towards the outstanding principal amount of the Loan and (E) any other loan/dues payable by the Borrower. The Lender may vary the order set out hereinbefore at their discretions.";
	}

	default String definationKFS1_1_for_I7777_point_30_to_1_point_35(String brandName) {
		return "7.1.	Subject to applicable laws, the Borrower may, prepay the whole or any part of the Loan Amount together with applicable interest and other charges as per the KFS.\r\n"
				+ "7.2.	In the event Lender permits any part pre-payment/acceleration of the Loan Amount in terms of this Loan Document, then the Instalment Amount and Tenure for the Loan can be amended /revised in writing by the Lender for giving effect to such prepayment/ acceleration, and such amended/revised Instalment Amount and Tenure shall be binding upon the Borrower";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_35(String brandName) {
		return "8.1.	The Borrower hereby represents and warrants that\r\n"
				+ "(i)	The Borrower is fully empowered to enter into this Agreement and to perform obligations hereunder in the Loan Documents and the Loan Documents duly executed and delivered by the Borrower as may be required, and constitute/ shall constitute a legal, valid and binding obligation of the Borrower, as the case may be, enforceable against the Borrower in accordance with their respective terms\r\n"
				+ "(ii)	the Borrower shall perform all its obligations under this Agreement\r\n"
				+ "(iii)	the Borrower is financially solvent and have adequate net worth to be able to perform their obligations under this Agreement;\r\n"
				+ "(iv)	the execution and delivery of this Agreement and documents to be executed in pursuance hereof, and the performance of the Borrower’s obligations hereunder does not and will not: (i) contravene any applicable law, statute or regulation or any judgment or decree to which the Borrower and/or its assets, businesses and/or undertakings is subject, (ii) conflict with or result in any breach of, any of the terms of or constitute default of any covenants, conditions and stipulations under any existing agreement or contract or binding to which the Borrower is a party or subject, as the case may be\r\n"
				+ "(v)	neither the Borrower, nor any person acting on its behalf, has been engaged in (a) any corrupt / fraudulent practices / collusive / coercive practices in connection with the Borrower’s business or operations (b) money laundering; or (c) the financing of terrorism.\r\n"
				+ "(vi)	The Borrower further acknowledges and confirms that the information and/or documents including but not limited to KYC documents provided to Lender in connection with the loan does not contain any untrue statement of a material fact, nor does it omit to state a material fact necessary in order to make the statements contained therein not misleading in light of the circumstances under which such statements were or are made.\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_10(String brandName) {
		return "The following events and occurrences including but not limited to, shall constitute an Event of Default for purposes of this Agreement\r\n"
				+ "10.1.	Non-payment of Instalment Amount by the Borrower as per the Instalment Frequency, whether wholly or in part.\r\n"
				+ "10.2.	Occurrence of any event leading to Material Adverse Change\r\n"
				+ "10.3.	Death of the Borrower under the Loan Documents\r\n"
				+ "10.4.	Bouncing/Cancellation of the Payment Instrument issued/registered by the Borrower for the repayment of the Loan Amount\r\n"
				+ "10.5.	If there occurs a breach of any representation or warranty made or deemed to be made by the Borrower in or pursuant to this Agreement\r\n"
				+ "10.6.	If insolvency proceedings are initiated against or voluntarily by the Borrower\r\n"
				+ "10.7.	If the Borrower is convicted for any offence under the law adversely affecting Borrower’s ability to repay the Loan Amount, solely in the opinion of the Lender\r\n"
				+ "10.8.	If any order is passed by any governmental, judicial, quasi-judicial or any other authority adversely affecting the operation of the loan or if, at any time, it is, becomes or will become unlawful or contrary to any regulation in any applicable jurisdiction for the Lender to perform any of its obligations as contemplated by this Agreement or to fund or do lending business.\r\n"
				+ "10.9.	Any change in applicable law which may in the sole opinion of Lender impact the Borrower’s ability to fulfil the obligations under this Agreement\r\n"
				+ "10.10.	Any pending or threatened litigation, investigation or proceeding that may have impact Borrower’s business condition (financial or otherwise), operations, performance, properties or prospects of the Borrower or that purports to affect the Agreement, or the transactions contemplated thereby\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_10_2(String brandName) {
		return "10.12.	If the Borrower utilizes the Loan Amount for any unlawful purpose\r\n"
				+ "10.13.	Other Events of Default: In the opinion of the Lender any other default, which adversely affects the fulfilment of obligations by the Borrower under this Agreement and inability of the Borrower to cure any failure within a period of 15 (fifteen) Business Days after the date on which Lender in writing notifies such failure to Borrower as an Event of Default as the case may be.\r\n"
				+ "10.14.	Consequences of Default: Upon the occurrence of an Event of Default under this Clause, the Lender or any agent/service provider of the Lender authorized in this behalf shall serve a notice to the Borrower to remedy such Event of Default, where the same is remediable, and if the Borrower fails to remedy the Event of Default within a period of 7 days from the date of such notice, the Lender shall have the right to initiate one or all of the actions as specified below:\r\n"
				+ "(i)	The Lender may, by a notice in writing, may recall the loan granted to the Borrower and declare the entire amount payable under the Loan Documents together with all interest and charges payable thereto. In such an event, the entre outstanding\r\n"
				+ "(ii)	On occurrence of the Event of Default, the Lender may report the account of the Borrower to RBI and/or credit information companies as Special Mention Account (SMA) / Non-Performing Assets (NPA) (as per the RBI Directions), which may impact its credit score/rating and his/her ability for future borrowing from the financial institutions\r\n"
				+ "(iii)	The Lender shall be entitled at the sole risk and cost of the Borrower to engage one or more person(s)/collection service providers/agents to collect the Borrower’s dues and shall further be entitled to share such information, facts and figures pertaining to the Borrower as the Lender deems fit for the aforesaid purpose. The Lender may also delegate to such person(s) the right and authority to perform and execute all such acts, deeds, matters and things connected herewith, or incidental thereto, as the Lender may deem fit. The Borrower recognizes, accepts, and consents to such delegation.\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_11(String brandName) {
		return "11.1.	The Borrower acknowledges and authorizes the Lender to disclose all information and data relating to Borrower, the Loan Facility, default if any, committed by Borrower to such third parties/ agencies as the Lender may deem appropriate and necessary to disclose and/or as authorized by RBI, including the Credit Information Companies. The Borrower also acknowledges and authorizes such information to be used, processed by the Lender / third parties/RBI as they may deem fit and in accordance with applicable laws. Further in Event of Default, the Lender and such agencies shall have an unqualified right to disclose or publish the name of the Borrower/ co-applicants, as applicable, as 'defaulters' in such manner and through such medium as the Lender / RBI/ other authorized agency in their absolute discretion may think fit, including in newspapers, magazines and social media. \r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_12(String brandName) {
		return "12.1.	Agreement Validity: This Agreement is binding on the Parties hereto on and from the effective date of this agreement and shall be in force and effect till all the monies due and payable under this Agreement are fully paid by the Borrower\r\n"
				+ "12.2.	Continuing Obligations: The liabilities and obligations of the Borrower under or pursuant to this Loan Agreement shall remain in force and effect notwithstanding any act, omission, and event or circumstance whatsoever until the Loan has been repaid in full as per the Loan Documents\r\n"
				+ "12.3.	GOVERNING LAW AND ARBITRATION\r\n"
				+ "(i)	Governing Law- This Agreement shall be governed by and construed and enforced in accordance with the laws of India, without regard to its principles of conflict of laws, the Parties agree to submit to the exclusive jurisdiction of the courts in Delhi, alone\r\n"
				+ "(ii)	Arbitration: Any disputes, differences, controversies and questions directly or indirectly arising at any time hereafter between the Parties or their respective representatives or assigns, arising out of or in connection with this Agreement (or the subject matter of this Agreement), including, without limitation, any question regarding its existence, validity, interpretation, construction, performance, enforcement, rights and liabilities of the Parties, or termination (“Dispute”), shall be referred to a sole arbitrator duly appointed by the Lender. The language of the arbitration shall be English. The seat of the arbitration shall be at New Delhi and the language of proceedings shall be English. The award rendered shall be in writing and shall set out the reasons for the arbitrator’s decision. The costs and expenses of the arbitration shall be borne equally by each Party, with each Party paying for its own fees and costs including attorney fees, except as may be determined by the arbitration tribunal. Any award by the arbitration tribunal shall be final and binding\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_12_2(String brandName) {
		return "12.1.	Agreement Validity: This Agreement is binding on the Parties hereto on and from the effective date of this agreement and shall be in force and effect till all the monies due and payable under this Agreement are fully paid by the Borrower\r\n"
				+ "12.2.	Continuing Obligations: The liabilities and obligations of the Borrower under or pursuant to this Loan Agreement shall remain in force and effect notwithstanding any act, omission, and event or circumstance whatsoever until the Loan has been repaid in full as per the Loan Documents\r\n"
				+ "12.3.	GOVERNING LAW AND ARBITRATION\r\n"
				+ "(i)	Governing Law- This Agreement shall be governed by and construed and enforced in accordance with the laws of India, without regard to its principles of conflict of laws, the Parties agree to submit to the exclusive jurisdiction of the courts in Delhi, alone\r\n"
				+ "(ii)	Arbitration: Any disputes, differences, controversies and questions directly or indirectly arising at any time hereafter between the Parties or their respective representatives or assigns, arising out of or in connection with this Agreement (or the subject matter of this Agreement), including, without limitation, any question regarding its existence, validity, interpretation, construction, performance, enforcement, rights and liabilities of the Parties, or termination (“Dispute”), shall be referred to a sole arbitrator duly appointed by the Lender. The language of the arbitration shall be English. The seat of the arbitration shall be at New Delhi and the language of proceedings shall be English. The award rendered shall be in writing and shall set out the reasons for the arbitrator’s decision. The costs and expenses of the arbitration shall be borne equally by each Party, with each Party paying for its own fees and costs including attorney fees, except as may be determined by the arbitration tribunal. Any award by the arbitration tribunal shall be final and binding\r\n"
				+ "12.4.	SERVICE OF NOTICE \r\n"
				+ "Except as may be otherwise provided herein, all notices, requests, waivers and other communications made pursuant to this Agreement shall be in writing. Such notice shall be served by sending it delivering by hand, mail or courier or to the address of the Borrower available with the Lender as per its records or through electronic mode on the contact details registered or available with the Lender (such as e-mail, SMS, WhatsApp or any other electronic mode). Any notice so served shall be deemed to have been duly given (a) in case of delivery by hand, when hand delivered to the other Party; or (b) when sent by post, where 5 Business Day(s) have elapsed after deposit in the post; or (c) when delivered by courier on the second Business Day after deposit with an overnight delivery service, postage prepaid, with next Business Day delivery guaranteed, provided that the sending Party receives a confirmation of delivery from the delivery service provider d) if by electronic mode, when directed to an electronic mode address (such as e-mail, SMS, WhatsApp) provided by the Borrower. Any notice or communication to the Borrower shall be deemed to be a notice or communication to all the Borrower(s). A Party may change or supplement the addresses mentioned in the schedule 1, or designate additional address, for the purpose of this clause by giving the other Party written notice of the new address in the manner set forth above \r\n"
				+ "12.5.	ENTIRE AGREEMENT\r\n"
				+ "This Agreement (including all Schedules) along with the documents executed or to be executed by the Borrower in favour of the Lender pursuant to this Agreement shall constitute the entire agreement between the Parties hereto with respect to its subject matter. \r\n"
				+ "12.6.	SUPREMACY & AMENDMENT\r\n"
				+ "This Agreement supersedes all discussions and Agreements (whether oral or written, including all correspondence) prior to the date of this Agreement between the Parties with respect to the subject matter of this Agreement. This Agreement may be modified or amended only by a writing duly executed by or on behalf of each of the Parties. \r\n"
				+ "12.7.	LIEN AND SET OFF\r\n"
				+ "Without prejudice to and in addition to any other right or remedy which the Lender may have under the Loan Documents or under the law of contract or any other applicable law, the Lender shall have the lien over any asset/security and/or any or all amounts received by it under other agreements with the Borrower. The Lender shall be entitled to exercise its lien over the assets/security and/or any or all\r\n"
				+ "amount received by it’s under other agreement as mentioned above to set off and recover any or all amounts payable by the Borrower under the Loan Documents. The Borrower hereby expressly acknowledges and affirms the Lender’s lien and right of set off as specified in this clause\r\n"
				+ "12.8.	SUCCESSORS AND ASSIGNS\r\n"
				+ "This Agreement binds and benefits the respective successors and assignees of\r\n"
				+ "the Parties and, in respect of the individuals who are parties, their respective heirs, executors, administrators and legal representatives.\r\n"
				+ "12.9.	NO ASSIGNMENT\r\n"
				+ "The Borrower shall not have any right to assign this Agreement and/or any right or\r\n"
				+ "obligation hereunder or part hereof. The Lender may assign/securitize/transfer/novate its rights under this\r\n"
				+ "Agreement and may notify the Borrower accordingly of such an assignment/securitization if required\r\n"
				+ "12.10.	FORCE MAJEURE\r\n"
				+ "The Borrower shall be liable to perform or fulfil its obligations at all point of time and shall not delay/postpone performance, its obligations in whole or in part, even on the ground of any force majeure event such as acts of God, floods, cyclones, earthquakes, fires, wars, riots, strikes, orders of governmental or other statutory authorities.\r\n"
				+ "12.11.	SANCTION LETTER\r\n"
				+ "The terms of the Sanction Letter, issued by the Lender, shall form part of this Agreement and shall be in addition to and be read in conjunction with the terms of this Agreement. If there are any inconsistencies between the terms of the Sanction Letter and this Agreement then, the terms of Sanction Letter shall prevail to the extent of inconsistencies.\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I888_point_30_to_1_point_11_2(String brandName) {
		return "11.2.	The Borrower shall not hold the Lender responsible for sharing and/or disclosing the information now or in future and also for any consequences suffered by the Borrower and/or other by reason thereof. The provisions of this clause shall survive termination of this Agreement and the repayment of the Borrower's Dues. The Borrower further agrees that Lender may, as it deems appropriate and necessary disclose and furnish to TransUnion CIBIL Limited (formerly known as credit Information Bureau (India) Ltd (“CIBIL”) and any other agency authorized in this behalf by RBI all or any of the following:\r\n"
				+ "(a)	information and data relating to the Borrower;\r\n"
				+ "(b)	the information or data relating to the Facilities availed of/to be availed, by the Borrower; and\r\n"
				+ "(c)	the information and details of the default, if any, committed by the Borrower, in discharge of the indebtedness.\r\n"
				+ "";
	}

	default String definationKFS1_1_for_I8882_point_30_to_1_point_35(String brandName) {
		return "8.2.	Indemnity\r\n"
				+ "(i)	The Borrower shall, without prejudice to any other right of Lender, indemnify and keep indemnified and hold Lender, its officer/employees/agents/affiliates harmless against any and all liabilities, obligations, losses, damages, penalties, actions, judgments, suits, costs expenses or disbursements of any kind or nature whatsoever (including without limitation, fees and disbursements of lawyers) which may be imposed on, incurred by, or asserted against Lender in any claim, litigation, proceeding or investigation instituted or conducted by any governmental agency or instrumentality or any person or entity, and which are related directly or indirectly to this Agreement or any document executed pursuant hereto, whether or not Lender is a party thereto, and shall pay and reimburse to Lender, without any dispute or demure any losses, costs, charges or expenses which Lender shall certify as sustained or suffered or incurred by Lender as a consequence of occurrence of an event of default as per Clause 10, or any other breach of obligations by the Borrower under this Agreement or otherwise in connection with this Agreement (including any interest or fees incurred in funding any unpaid sum).\r\n"
				+ "(ii)	The Borrower shall indemnify and keep the Lender its officer/employees/agents/affiliates indemnified during the Tenure of this Agreement from and against all liabilities, claims, losses, damages and expenses whatsoever in respect of its obligations and liabilities in connection with the payment of amounts due under this Agreement and the Borrower shall be liable to pay such amounts forthwith on demand.";
	}

	default String defination_1_point_36_to_1_point_37() {
		return "1.1.36.	“Repayment Instrument(s)” or “Payment Instrument(s)” shall mean repayment instructions/ instrument(s) including but not limited to NACH, cheque, online remittance, bank transfer and/ or any other payment instrument(s) recognized under Applicable Law provided by the Borrower(s) towards discharge of the due payment of the Instalments including any other dues arising out of this Agreement;\r\n"
				+ "1.1.37.	“SMA” means Special Mention Account as per the RBI circular DOR.STR.REC.3/21.04.048/2023-24 dated April 01, 2023 on Master Circular - Prudential norms on Income Recognition, Asset Classification and Provisioning pertaining to Advances, as amended from time to time.\r\n"
				+ "";
	}

	String INTERPRETATION = "INTERPRETATION";

	String TERMS_OF_LOAN = "2.	TERMS OF LOAN";

	String RATE_OF_INTEREST = "3.	RATE OF INTEREST";

	String DEFAULT_INTERST = "4.	DEFAULT INTEREST";

	String REPAYMENT = "5.	REPAYMENT";

	String APPROPRIATION = "6.	APPROPRIATION";

	String PREPAYMENT = "7.	PREPAYMENT/FORECLOSURE";

	String REPRESENTATION = "8.	REPRESENTATIONS, WARRANTIES & INDEMNITIES OF THE BORROWER";

	String COVENANTS = "9.	COVENANTS";

	String EVENT_OF_DEFAULT = "10.	EVENTS OF DEFAULT";

	String DISCLOSURESs = "11.	DISCLOSURES";

	String MISCELLANEOUSss = "12.	MISCELLANEOUS";

	String COVENANTS_VAL = "9.1.	The Borrower covenants and undertakes that, during the Tenure of the Agreement, the Borrower will, unless Lender waives compliance in writing:\r\n"
			+ "(i)	Obtain and comply with the terms of and do all that is necessary to maintain in full force and effect, all authorizations, approvals, licenses and consents required to enable it to enter into and perform its obligations under this Agreement, and to ensure the legality, validity, enforceability and admissibility in evidence of this Agreement.\r\n"
			+ "(ii)	Maintain proper statement of accounts pertaining to the Fino Bank account of the Borrower and not withdraw any amounts from the abovesaid account unless permitted by the Lender. The Borrower may be required submit to the Lender such financial statements and additional information, as may be required by the Lender from time to time\r\n"
			+ "(iii)	Promptly inform Lender of any occurrence, event or incident of which it becomes aware which might adversely affect the Borrower or affect its ability to perform its obligations under this Agreement and/or in respect of the outstanding amount of the Loan\r\n"
			+ "(iv)	Promptly inform Lender of the occurrence of any Event of Default or of the occurrence of an event which, with the passage of time or the giving of notice would become an Event of Default, and also, where applicable, of the steps being taken to remedy the same, and will, from time to time, if so requested by Lender, confirm to Lender in writing that save as otherwise stated in such information, no default has occurred and/or is continuing.\r\n";

	String COVENANTS_VAL_2 = "(v)	No suit has been filed by any bank/ financial institution against the Borrower or any of the firms/ companies in which Borrower are partners/ directors/member for recovery of any amount\r\n"
			+ "(vi)	No account of the Borrower has been classified as SMA (Special Mention Account) or declared NPA/ settled by giving rebate/ OTS\r\n"
			+ "(vii)	Borrower is not under any caution/defaulter list issued by RBI\r\n"
			+ "(viii)	That so long as any part of the Loan is outstanding and until full and final payment of all moneys owing hereunder the Borrower shall not, without the prior written consent of Lender having been obtained do or omit to do and not permit any act, matter or thing which would cause any of the representations and warranties, under this Agreement, to be untrue, inaccurate or misleading and immediately notify Lender in writing of any such fact or circumstance which might cause any of the representations and warranties set forth in this Agreement to be untrue or misleading\r\n"
			+ "(ix)	The Lender shall, without notice to or without any consent of the Borrower, be absolutely entitled and have full right, power and authority to make disclosure of any information relating to Borrower including personal information, details in relation to documents, Loan, defaults, security, obligations of Borrower, to the credit information companies, RBI and/or other affiliate/agencies/service providers for the purpose of the performance or ensuring performance of the obligation by the Borrower under this Agreement\r\n"
			+ "(x)	The Borrower understands and acknowledges that the Lender shall have the right to disclose the details of the loan, its repayment behaviours, defaults, account status and such other relevant information pertaining to the Borrower to the credit information companies and RBI on regular basis.\r\n"
			+ "9.2.	Upon occurrence of an Event of Default, Lender shall be entitled, by a notice, to:\r\n"
			+ "(i)	declare the Loan, together with accrued interest and other monies, to be immediately due and payable and upon such declaration, the same shall become immediately payable by the Borrower;\r\n"
			+ "(ii)	the Lender may report the account of the Borrower to RBI and/or credit information companies as Special Mention Account (SMA) / Non-Performing Assets (NPA), (as per the RBI Directions), which may impact its credit score/rating and his/her ability for future borrowing from the financial institutions;\r\n"
			+ "(iii)	The Lender shall be entitled at the sole risk and cost of the Borrower to engage one or more person(s)/collection service providers/agents to collect the Borrower’s dues and shall further be entitled to share such information, facts and figures pertaining to the Borrower as the Lender deems fit for the aforesaid purpose. The Lender may also delegate to such person(s) the right and authority to perform and execute all such acts, deeds, matters and things connected herewith, or incidental thereto, as the Lender may deem fit. The Borrower recognizes, accepts, and consents to such delegation.\r\n"
			+ "(iv)	exercise any or all rights and recourses available under the Loan Documents and/or under applicable Law.\r\n"
			+ "9.3.	Consequences of Event of Default\r\n"
			+ "If one or more of the Events of Default occur or are continuing, whether voluntarily or involuntarily, then, without derogation from the rights mentioned in this Agreement and without prejudice to any other right or action that Lenders may be entitled to under law or this Agreement against the Borrower(s), the Lenders shall have the following rights:\r\n"
			+ "(i)	The Lender may, at their sole discretion, terminate the Agreement, either wholly or partially, upon occurrence of any Event of Default.\r\n"
			+ "(ii)	In the event of Event of Default, Lenders may, at their sole discretion, take such other legal remedial action as Lenders may deem fit including enforcing any rights available to the Lenders under applicable laws, regulations or contract.\r\n"
			+ "(iii)	Notwithstanding the foregoing clauses, the Lenders otherwise reserves their right to terminate the Agreement and to recall the Facility without assigning any reasons, by providing a reasonable prior notice.";
	String INTERPRETATION_VAL = "In this Agreement:\r\n"
			+ "1.2.1.	Unless the context otherwise requires, reference to a Recital/Clause and/or a Schedule is to a recital/clause and/or schedule of this Agreement, all of which constitute an integral and operative part of this Agreement.\r\n"
			+ "1.2.2.	Headings to parts, clauses and paragraphs are for convenience only and do not affect the interpretation of this Agreement.\r\n"
			+ "1.2.3.	Reference to any statute or statutory provision or order or regulation made thereunder shall include references to that statute, provision, order or regulation as amended, modified, re-enacted or replaced from time to time whether before or after the date hereof.\r\n"
			+ "1.2.4.	Reference to any document includes an amendment to that document, but disregarding any amendment made in breach of this Agreement.\r\n"
			+ "1.2.5.	Reference to an “amendment” includes a supplement, modification, novation, replacement, or re-enactment and “amended” is to be construed accordingly.\r\n"
			+ "1.2.6.	Unless the context otherwise requires, words denoting the singular shall include the plural and vice versa, and words denoting any gender include all genders.\r\n"
			+ "1.2.7.	Reference to the word “include” or “including” shall be construed without limitation.\r\n"
			+ "1.2.8.	References to a “person” or “Person” shall include that Person’s successors in title, executors, permitted transferees and permitted assignees and references to a Person’s representatives shall be to its officers, employees, legal or other professional advisers, sub-contractors, agents, attorneys, and other duly authorised representatives.\r\n"
			+ "1.2.9.	Words “hereof”, “herein”, “hereto”, “hereunder” or similar terms used with reference to a specific clause in this Agreement shall refer to such clause in this Agreement and when used   otherwise than in connection with specific clauses shall refer to this Agreement as a whole.\r\n"
			+ "1.2.10.	In the computation of periods of time from a specified date to a later specified date, the words “from” and “commencing on” mean “from and including” and “commencing on and including”, respectively, and the words “to”, “until” and “ending on” each mean “to but not including”, “until but not including” and “ending on but not including” respectively.\r\n"
			+ "1.2.11.	Unless otherwise specified, whenever any payment to be made or action to be taken under this Agreement, is required to be made or taken on a day other than a Business Day, such payment shall be made, or action be taken on the immediately following Business Day.\r\n"
			+ "1.2.12.	Where a wider construction is possible, the words “other” and “otherwise” shall not be construed ejusdem generis with any foregoing words.\r\n"
			+ "1.2.13.	A time of day is a reference to India time.\r\n" + "";

	String PURPOSE_ANDDISBURSEMENT = "PURPOSE AND DISBURSEMENT";

	String PURPOSE_ANDDISBURSEMENT_VAL = "2.1.	The Borrower acknowledges and agrees that the Facility is being availed solely for the purpose specified in Schedule I of this Agreement. The Borrower represents and warrants that the Facility shall be utilized exclusively for personal financial needs, specifically as an accrued salary advance, and not for any unlawful or speculative activities. The Borrower further undertakes to ensure that the proceeds of the Facility are used only for the intended purpose and shall not be diverted for any unauthorized or unrelated use. The Borrower shall, forthwith upon the request of the Lender, furnish all such details and evidence as may be required concerning the utilisation of the amount of Accrued Salary Advance Facility.\r\n"
			+ "2.2.	The Borrower also authorizes the Lender to recover the loan from the salary received from their Employer. The Borrower hereby confirms that irrespective of deduction of such charges, the Borrower shall be liable to repay to the Lender the entire loan, and other charges together with applicable taxes as per the Schedule herein.\r\n"
			+ "";

	String CONDITIONS_PRECEDENT = "CONDITIONS PRECEDENT";

	String CONDITIONS_PRECEDENT_VAL = "The Borrower acknowledges and agrees that the disbursement of the Facility by the Lender shall be subject to the satisfaction of the following conditions precedent, as determined by the Lender in its sole discretion:"
			+ "3.1.	The Borrower shall have provided the following documents and information to the satisfaction of the Lender:\r\n"
			+ "(a)	a duly executed copy of this Agreement, including all Schedules annexed hereto;\r\n"
			+ "(b)	a copy of the Borrower's government-issued identity proof and address proof, as required under applicable KYC norms and regulations;\r\n"
			+ "(c)	a certificate or salary slip from the Borrower's Employer evidencing the accrued salary and employment status of the Borrower;\r\n"
			+ "(d)	Any other documents as may be requested by the Lender, including evidence of the Borrower’s authorization for salary deductions by the Employer.\r\n"
			+ "3.2.	The Borrower shall have submitted written authorization to the Employer, duly acknowledged and accepted by the Employer, permitting the Employer to deduct loan repayments directly from the Borrower’s salary.\r\n"
			+ "3.3.	The Borrower’s employment status with the Employer shall have been verified by the Lender or its authorized agents.\r\n"
			+ "3.4.	The Borrower shall have met the Lender’s credit assessment criteria, including submission of any additional financial information requested by the Lender.\r\n"
			+ "3.5.	The Borrower shall comply with all applicable laws, including but not limited to, those related to the utilization of the Facility, repayment obligations, and tax compliance.\r\n"
			+ "3.6.	The Borrower shall not be in default under any other financial obligations, and there shall be no breach of any representations, warranties, or undertakings provided by the Borrower under this Agreement.\r\n";

	String CONDITIONS_PRECEDENT_VAL2 = "3.7.	The Borrower shall continue to remain in employment with the Employer.\r\n"
			+ "3.8.	The Borrower shall have paid any processing fees, charges, or applicable taxes related to the Facility, as specified in Schedule of this Agreement.\r\n"
			+ "";

	String INTEREST_AND_REPAYMENT = "INTEREST AND REPAYMENT";

	String INTEREST_AND_REPAYMENT_VAL = "4.1.	The Borrower agrees that the re-payment of the Facility shall be done by the Employer of the Borrower by deducting the salary of the Borrower on the same date of its credit to the Borrower.\r\n"
			+ "4.2.	The Borrower will pay Interest on the outstanding Facility and all other amounts due as provided in the Schedule herein.\r\n"
			+ "4.3.	The re-payment of the Facility on time is the essence of the contract. The Borrower acknowledges that she/he has understood the method of computation of loan repayment and shall not dispute the same. Interest on loan shall be paid by the Borrower and shall be calculated on day count basis.\r\n"
			+ "4.4.	Notwithstanding anything stated elsewhere in the Financing Documents, upon occurrence of any Material Adverse Effect or any Event of Default, all Borrower's Dues, including EMI, shall be payable by the Borrower to the Lender as and when demanded by the Lender, at its sole discretion and without requirement of any reason being assigned. The Borrower shall pay such amounts forthwith without any delay or demur. \r\n"
			+ "4.5.	The Borrower shall bear all interest, tax, duties, cess and other forms of duties or taxes whether applicable now or in the future, payable under any law at any time in respect of any payments made to the Lender under the Financing Documents. If these are incurred by the Lender, these shall be recoverable from the Borrower and will carry charges as per the Penal Charges mentioned in the Schedule herein.\r\n"
			+ "4.6.	Interest and all other charges shall accrue on a day-to-day basis and shall be computed on the basis of 365 days a year or 366 days in case of a leap year and the actual number of days elapsed. \r\n"
			+ "4.7.	All sums payable by the Borrower to the Lender shall be paid without any deductions whatsoever, other than statutory taxes. Credit/ discharge for payment will be given only on realization of amounts due. \r\n"
			+ "4.8.	The Borrower hereby agrees that in the event there is a default of payment of principal, interest or any other applicable payment due to the Lender, then the facility will be classified in a manner as laid out by "
			+ "Reserve Bank of India (RBI) in Master Circular RBI/2021- 2022/104 DOR.No.STR.REC.55/21.04.048/2021-22 Dated:01-10-2022, which is as follows:";

	String MORE_OVER = "                Moreover, the Borrower agrees upon, that:\r\n"
			+ "(a)	In case of interest payments in respect of term loans, an account will be classified as NPA if the interest applied at specified rests remains overdue for more than 90 days.\r\n"
			+ "(b)	Loan accounts classified as NPAs may be upgraded as ‘standard’ asset only if entire arrears of charges, interest and principal are paid by the Borrower.\r\n"
			+ "(c)	Illustration:\r\n"
			+ "The Lender shall be classifying and/or reporting the Borrower’s loan account as a stressed loan account in accordance with. In this regard, the Borrower must note the following illustrations:\r\n"
			+ "•	If the Due Date for repayment of an EMI is March 31st and the Borrower fails to pay the relevant EMI amount on such date in full before the closure of the day-end process, the loan account of the Borrower shall be classified and reported as overdue with effect from March 31st.\r\n"
			+ "•	If the aforesaid EMI amount continues to remain overdue for a period of 30 days from the Due Date, that is, the Borrower fails to pay the relevant EMI amount in full before the closure of the day-end process on the 30th day from the Due Date, the loan account of the Borrower shall be classified and reported as a special mention account-1 (“SMA-1”) on April 30th.\r\n"
			+ "•	If the aforesaid EMI amount continues to remain overdue for a period of 60 days from the Due Date, that is, the Borrower fails to pay the relevant EMI amount in full before the closure of the day-end process on the 60th day from the Due Date, the loan account of the Borrower shall be classified and reported as a special mention account-2 (“SMA-2”) on May 30th.\r\n"
			+ "•	If the aforesaid EMI amount continues to remain overdue for a period of 90 days from the Due Date, that is, the Borrower) fails to pay the relevant EMI amount in full before the closure of the day-end process on the 90th day from the Due Date, the loan account of the Borrower shall be classified and reported as a non-performing asset (“NPA”) on June 29th.\r\n"
			+ "•	A loan account that has been classified as NPAs by the Lender may be upgraded as “standard” asset only if entire arrears of EMI are paid by the Borrower to the Lender. \r\n"
			+ "4.9.	*Note: Illustrations provided above are only indicative of the classification and reporting practices that will be followed by the Lender and do not dilute the liability of the Borrower to pay the entire Repayment amounts due and payable by the Borrower to the Lender If the Due Date for any payment is not a business day, the amount will be paid on immediately succeeding business day. \r\n"
			+ "4.10.	The Borrower agrees that the re-payment of Accrued Salary Advance Facility shall be done by the Employer of the Borrower by deducting the salary of the Borrower on the same date of its credit to the Borrower.\r\n"
			+ "";

	String MORE_OVER2 = "4.11.	The Borrower agrees and acknowledges that the deduction of Accrued Salary Advance Facility have been voluntarily agreed by the Borrower's for payment of his/her dues and not by way of a security for any purpose whatsoever.\r\n"
			+ "4.12.	Any dispute or difference of any nature whatsoever shall not entitle the Borrower to delay re-payment of Accrued Salary Advance Facility and the Lender shall be entitled recover the dues from the Borrower in the manner it deem fit.\r\n"
			+ "4.13.	Notwithstanding the confirmation of the Borrowers for re-payment of Accrued Salary Advance Facility from his/her salary, the Borrower will be solely responsible for payment of dues to the Lender.\r\n"
			+ "";

	String CANCELLATION = "CANCELLATION";

	String CANCELLATION_VAL = "5.1.	Notwithstanding anything to the contrary contained in this Agreement, the Borrower(s) agrees that the Lender shall at any time, during the currency of the Facility have an unconditional right to terminate and cancel the Facility (whether in part or in full) at his sole discretion on the occurrence of any Event of Default or potential Event of Default or if it becomes unlawful for the Lender to disburse or continue the Facility to the Borrower(s), by giving a notice of 7 (seven) days, which notice shall be final and binding on the Borrower(s). \r\n"
			+ "5.2.	Provided, no notice will be given by the Lender to the Borrower(s), where an Event of Default has already been declared.\r\n"
			+ "5.3.	Notwithstanding anything contrary contained in this Agreement, the Borrower(s) agrees that the Facility shall be Repayable on the happening of a Market Disruption Event and the Lender can, at any time,  at its sole and absolute discretion,  terminate, cancel, withdraw or recall the Facility or any part thereof without any liability and without any obligation to give any reasons whatsoever, whereupon all Outstanding Amounts under the Facility shall immediately become due and payable by the Borrower(s) to the Lender  forthwith upon demand made by the Lender.\r\n"
			+ "5.4.	The Facility is available for utilization solely for the Purpose. If in the opinion of the Lender (which opinion shall be binding and conclusive against the Borrower(s)) the Facility is not used for the Purpose (provided that the Lender shall not be bound to enquire as to, or be responsible for, the use or application of any funds advanced under the Facility), the Lenders shall have the right to declare an Event of Default.\r\n"
			+ "";

	String REPRESENTATIONS_AND_WARRANTIES = "6.	REPRESENTATIONS AND WARRANTIES";

	String REPRESENTATIONS_AND_WARRANTIES_VAL = "6.1.	The Borrower hereby represents, warrants, assures and confirms as applicable to it, that:\r\n"
			+ "6.1.1.	shall observe and perform all its obligations under the Financing Documents;\r\n"
			+ "6.1.2.	shall ensure all the amount(s) due and payable in this Agreement are paid in accordance with the stipulated timelines;\r\n"
			+ "6.1.3.	immediately deliver to the Lender all documents, including bank account statements as may be required by the Lender from time to time. The Borrower also authorizes the Lender to communicate independently with any bank where the Borrower maintains an account and to seek details and statement in respect of such account from the bank;\r\n"
			+ "6.1.4.	immediately notify the Lender of any litigations or legal proceedings against him/her/them;\r\n"
			+ "6.1.5.	promptly notify the Lender of any Material Adverse Effect or Event of Default;\r\n"
			+ "6.1.6.	promptly inform the Lender of any force majeure event or act of God such as earthquake, flood, tempest or typhoon, etc. or other similar happenings;\r\n"
			+ "6.1.7.	notify the Lender in writing of all changes in the location/ address of residence or any change/resignation/termination /closure of employment;\r\n"
			+ "6.1.8.	comply at all times with applicable laws, including but not limited to Prevention of Money Laundering Act, 2002;\r\n"
			+ "6.1.9.	Pay regularly all taxes, assessments, dues, duties, levies and impositions as may, from time to time, be payable to any government or statutory or regulatory body or authority;\r\n"
			+ "6.1.10.	Perform, on request of the Lender, such acts as may be necessary to carry out the intent of the Financing Documents;\r\n"
			+ "6.1.11.	Not use the funds for any speculative or illegal/ unlawful/ antisocial or any other nefarious purpose.\r\n"
			+ "6.2.	Each Borrower further represents and warrants to the Lender as under:\r\n"
			+ "6.2.1.	All the information provided by Borrower in the Financing Documents and any other document, whether or not relevant for the ascertaining the credit worthiness of the Borrower, is true and correct and not misleading in any manner;\r\n"
			+ "6.2.2.	The Borrower is capable of and entitled under all applicable laws to execute and perform the Financing Documents and the transactions thereunder;\r\n"
			+ "6.2.3.	The execution, delivery and performance of obligations under the Financing Documents are within the powers of the Borrower, do not contravene any contract binding on the Borrower, and do not violate any applicable law or regulation;\r\n"
			+ "6.2.4.	No extraordinary circumstances have occurred which shall make it improbable for the Borrower to fulfill its obligations under the Financing Documents;\r\n"
			+ "6.2.5.	The Borrower is above 18 years of age, of sound mind, a resident of India and is competent to enter into an agreement;\r\n"
			+ "6.2.6.	The Borrower declares that he/she is not prohibited by any law from availing this Accrued Salary Advance Facility;\r\n";

	String REPRESENTATIONS_AND_WARRANTIES_VAL2 = "6.2.7.	No event has occurred which shall prejudicially affect the interest of the Lender or affect the financial conditions of Borrower or affect his/her liability to perform all or any of their obligations under the Financing Documents;\r\n"
			+ "6.2.8.	The Borrower has the ability to meet all of his/her obligations as they mature;\r\n"
			+ "6.2.9.	It shall be the Borrowers obligation to keep himself/ herself acquainted with the rules of the Lender, from time to time;\r\n"
			+ "6.2.10.	The Borrower will do all acts, deeds and things, as required by the Lender to give effect to the terms of this Agreement;\r\n"
			+ "6.2.11.	The Borrower has not been engaged in (a) any corrupt/ fraudulent practices/ collusive/ coercive practices; (b) money laundering; or (c) the financing of terrorism;\r\n"
			+ "";

	String REPRESENTATIONS_AND_WARRANTIES_VAL3 = "6.2.12.	Particulars of the residential and official addresses of the Borrower, as last provided by the Borrower to the Lender is valid and subsisting and any one of the said addresses shall deemed to be the valid address for correspondence unless otherwise notified to the Lender by the Borrower in writing; and\r\n"
			+ "6.2.13.	Event of Default, has occurred or is likely to occur.\r\n"
			+ "6.3.	The Borrower gives its consent to the Lender to use/store all the information provided by the Borrower or otherwise procured by the Lender in the manner it deems fit including for the purposes of this Facility and understands and agrees that the Lender may disclose such information to its contractors, agents and any other third parties.\r\n"
			+ "6.4.	The Borrower is aware that the Lender has agreed to extend the Facility to the Borrower based on the KYC documents made available to the Lender directly or through any third party authorized in this regard (such as the Employer, Lending Service Provider or Technology Service Provider as per Digital Lending Guidelines of RBI) and that the Borrower has in no way concealed any relevant information which could have adversely affect the Lender's decision to grant the Facility to the Borrower.\r\n"
			+ "6.5.	The Borrower is fully aware of the KYC Policy and confirms that the information/clarification/ documents/signage provided by the Borrower on the identity, address, PAN and all other material facts "
			+ "are true and correct and the transaction, etc. are bona fide and as stipulated by RBI in relation to the KYC norms. The Borrower further confirm that they have disclosed all facts/information as are required to be disclosed for the adherence and compliance of the provisions related to the KYC Policy and applicable RBI guidelines as amended from time to time.\r\n"
			+ "6.6.	The Borrower agrees that in case he/she wishes to cancel the Accrued Salary Advance Facility, he/she has the right to intimate the Lender of such cancellation within 3 days of application. In the event of such cancellation, the Borrower agrees deposit the amount of Accrued Salary Advance Facility availed by him/her in the below mentioned account of the Lender. Non deposition of such amount within 3 days of application shall be deemed to as availing of Accrued Salary Advance Facility and the Borrower shall be bound by the terms of this agreement from the date of disbursement.\r\n"
			+ "6.7.	Notwithstanding any other rights available to the Lender, the Lender shall be entitled to initiate criminal proceeding or any other appropriate actions against the Borrower if at any time the Lender at its sole discretion has sufficient grounds to believe that the Borrower has made any misrepresentations and/ or submitted any forged documents or data to the Lender.\r\n"
			+ "6.8.	All rights and powers conferred on the Lender under the Financing Documents shall be in addition and supplemental to any rights the Lender has as a creditor against the Borrower under any law for the time being in force and security documents and shall not be in derogation thereof.\r\n"
			+ "";

	String COVENANTS_OF_THE_BORROWER = "7.	COVENANTS OF THE BORROWER";

	String COVENANTS_OF_THE_BORROWER_VAL = "The Borrower covenants with LSP and Lender, that he / she shall:\r\n"
			+ "7.1.	Positive Covenants\r\n"
			+ "7.1.1.	Notify the LSP and the Lender of the occurrence of any event or the existence of any circumstances which constitutes or results in any declarations, representations, warranty, covenants or condition under the Loan Documents being or becoming untrue or incorrect in any respect.\r\n"
			+ "7.1.2.	Inform the LSP and the Lender of any litigation, arbitration or other proceedings, which have a material adverse effect, within a period of two Business Days upon the same being instituted or threatened by any person whatsoever.\r\n"
			+ "7.1.3.	Inform the LSP and the Lender about any proposed action by the Borrower or action taken by any other person under any insolvency/bankruptcy laws against the Borrower.\r\n"
			+ "7.1.4.	If required by the LSP or the Lender at any time, deliver to the LSP and the Lender: (a) copies of all documents issued by the Borrower to all its creditors (or any general class of them) at the same time as they are issued; (b) such information and records (financial or otherwise) about the Borrower’s business, as may be required by Lender from time to time in relation to the Facilities within the period specified by Lender (c) a certificate from the independent practicing Chartered Accountant that the proceeds of the Loan have been utilized for the purpose specified at the time of availing the loan as and when demanded by Lender. If required by LSP or Lender at any time, deliver to LSP and Lender in form and detail satisfactory to Lender and in such number of copies as Lender may request.\r\n"
			+ "7.1.5.	Promptly inform the LSP and the Lender of any distress or other process of court being taken against any of the Borrower’s premises and/or property and/or assets.\r\n"
			+ "7.1.6.	Notify the LSP and the Lender of any material loss or damage which the Borrower may suffer due to any event, circumstance or act of God. Further, the Borrower agrees to intimate the Lender from time to time, details of insurance claims lodged/ filed and received by the Borrower.\r\n"
			+ "7.1.7.	File all relevant tax returns and pay all its taxes/duties promptly when due.\r\n" + "";

	String COVENANTS_OF_THE_BORROWER_VAL2 = "7.1.8.	Comply with such other conditions as may be stipulated by Lender from time to time on account of requirement of any Law.\r\n"
			+ "7.1.9.	If required by the LSP or the Lender at any time, submit to Lender the information etc. as envisaged under applicable Laws.\r\n"
			+ "7.1.10.	Ensure that the transactions entered into pursuant to the Loan Documents do not violate any Sanctions, directly or through persons or entities subject to any Sanctions, which may pertain inter alia, to the purpose and/or end use of the Facility, goods manufactured in or originated from/through certain countries, shipment from/to/using certain countries, ports, vessels, liners and/or due to involvement of certain persons and entities\r\n";

	String NEGATIVE_COVENANTS = "7.2.	Negative Covenants";

	String NEGATIVE_COVENANTS_VAL = "7.2.1.	The Borrower(s) covenants and undertakes that so long as any part of the Facility is outstanding and until full and final payment of all moneys owing hereunder, the Borrower(s) shall not, without the prior written consent of Lender having been obtained, do or omit to do and not permit any act, matter or thing which would cause any of the representations and warranties, under this Agreement, if repeated immediately prior to any Disbursement, to be untrue, inaccurate or misleading and immediately notify Lenders in writing of any such fact or circumstance which might cause any of the representations and warranties set forth in this Agreement to be untrue or misleading.\r\n"
			+ "7.2.2.	The Borrower(s) further undertakes that in the event any information given in the loan application form is found false, the Lender in their sole discretion may (i) refuse to advance/ further advance, and (ii) exercise its right to recall the loan facility, and the full loan amount advanced, if any, with interest and other charges shall become immediately recoverable by the Lender.\r\n"
			+ "";

	String EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF = "8.	EVENTS OF DEFAULT AND CONSEQUENCES THEREOF";

	String EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF_VAL = "8.1.	The occurrence of any one or more of the following events shall constitute an “Event of Default” under these Standard Terms and Conditions:\r\n"
			+ "8.1.1.	The Borrower fails to pay/repay any monies in respect of the Loan Amount including Rate of Interest, default interest or the charges on the Due Dates, whether at stated maturity, by acceleration or otherwise;\r\n"
			+ "8.1.2.	Breach of any statement, representation, warranty, undertaking or confirmation or covenant made herein or Borrower’s proposal / application or in this Terms and Conditions or otherwise on the part of the Borrower/third party in relation to the Borrower;\r\n"
			+ "8.1.3.	Any other event/material change which prejudicially alters Lender’s interest or may have material adverse effect including but not limited to nationalization/expropriation and/or compulsory acquisition of the Borrower’s assets by the authority of government;\r\n"
			+ "8.1.4.	The Borrower has, or there is a reasonable apprehension that the Borrower has or would, voluntarily or involuntarily become the subject of proceedings under any Bankruptcy or insolvency law, or certificate proceedings have been taken or commenced for recovery of any dues from the Borrower/s.\r\n"
			+ "8.1.5.	If any attachment or distress or restraint has been levied on the Borrower's assets or any order/certificate has been passed for recovery of dues and such order or judgment is not vacated, discharged or stayed for a period of 30 days, and such judgments or orders involve in the aggregate a liability which could have a material adverse effect;\r\n"
			+ "8.1.6.	Any legal, quasi-legal, administrative, arbitration, mediation, conciliation or other proceedings, claims, actions or governmental investigations of any nature pending against the Borrower in "
			+ "management of the Borrower or any of its assets which individually or in the aggregate would, if adversely determined, have a material adverse effect.\r\n"
			+ "8.1.7.	The Borrower is unable or has admitted in writing its inability to pay any of its indebtedness as they mature or when due;\r\n"
			+ "8.1.8.	Any consent, authorization, approval or the like, or license of or registration with or declaration to government or statutory or regulatory authority made by the Borrower for entering into the Loan Documents is revoked or terminated and is not in full force and effect;\r\n"
			+ "8.1.9.	The death, lunacy or other disability of the Borrower; if the Borrower ceases or threatens to cease to carry on any of its businesses or gives notice of its intention to do so or if all or any part of the assets of the Borrower required or essential for its business or operations are damaged or destroyed or there occurs any change from the date of submission of the Application Form in the general nature or scope of the business, operations, management or ownership of the Borrower, which could have a material adverse effect;\r\n"
			+ "8.1.10.	It is or becomes improper or unlawful for the Borrower or any person (including Lender or LSP) to perform any of their respective obligations under the Loan Documents;\r\n"
			+ "";

	String EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF_VAL2 = "8.1.11.	If the Borrower ceases to be an Employee with of the Employer;\r\n"
			+ "8.1.12.	One or more events, conditions or circumstances (including any change in law) occur or exist, which in the sole opinion of Lender, could have a material adverse effect.\r\n"
			+ "8.2.	Upon occurrence of an Event of Default, Lender shall be entitled, by a notice, to:\r\n"
			+ "8.2.1.	declare the Loan, together with accrued interest and other monies, to be immediately due and payable and upon such declaration, the same shall become immediately payable by the Borrower;\r\n"
			+ "8.2.2.	the Lender may report the account of the Borrower to RBI and/or credit information companies as Special Mention Account (SMA) / Non-Performing Assets (NPA), (as per the RBI Directions), which may impact its credit score/rating and his/her ability for future borrowing from the financial institutions;\r\n"
			+ "8.2.3.	The Lender shall be entitled at the sole risk and cost of the Borrower to engage one or more person(s)/collection service providers/agents to collect the Borrower’s dues and shall further be entitled to share such information, facts and figures pertaining to the Borrower as the Lender deems fit for the aforesaid purpose. The Lender may also delegate to such person(s) the right and authority to perform and execute all such acts, deeds, matters and things connected herewith, or incidental thereto, as the Lender may deem fit. The Borrower recognizes, accepts, and consents to such delegation.\r\n"
			+ "8.2.4.	exercise any or all rights and recourses available under the Loan Documents and/or under applicable Law.\r\n"
			+ "";

	String CONSEQUENCES_OF_EVENT_OF_DEFAULT = "8.3.	Consequences of Event of Default";

	String CONSEQUENCES_OF_EVENT_OF_DEFAULT_VAL = "If one or more of the Events of Default occur or are continuing, whether voluntarily or involuntarily, then, without derogation from the rights mentioned in this Agreement and without prejudice to any other right or action that Lenders may be entitled to under law or this Agreement against the Borrower(s), the Lenders shall have the following rights:\r\n"
			+ "8.3.1.	The Lender may, at their sole discretion, terminate the Agreement, either wholly or partially, upon occurrence of any Event of Default.\r\n"
			+ "8.3.2.	In the event of Event of Default, Lenders may, at their sole discretion, take such other legal remedial action as Lenders may deem fit including enforcing any rights available to the Lenders under applicable laws, regulations or contract.\r\n"
			+ "8.3.3.	Notwithstanding the foregoing clauses, the Lenders otherwise reserves their right to terminate the Agreement and to recall the Facility without assigning any reasons, by providing a reasonable prior notice.\r\n"
			+ "";

	String INDEMNITY = "9.	INDEMNITY";

	String INDEMNITY_VAL = "The Borrower and LSP hereby agrees to indemnify and keep fully indemnified and hold harmless and save the Lender against : (a) any claims, losses or damages, costs, charges and expenses including litigation expenses whatsoever which may be brought or made against or sustained or incurred by the Lender (and whether paid by LSP/ Lender or not) or which the Lender may become liable under or in respect of the Loan Documents ; (b) action or proceedings made or brought against the Lender; (c) any liability or loss incurred or suffered by it, or agents; (d) every payment made, obligation, liability, loss and damage, penalties, taxes, etc. whatsoever undertaken or incurred or suffered by the Lender (whether directly or indirectly) under or in connection with and/or arising under the Loan Documents; (e) against any liability, loss, damages, costs and expenses (including legal expenses) awarded against or incurred or paid by the Lender as a result of or in connection with the Lender granting the Loan as herein mentioned, without deducting tax in India whether or not such payment attracts withholding tax in India or requires due certification by a qualified chartered accountant";

	String DISCLOSURES = "10.	DISCLOSURES";

	String DISCLOSURES_VAL = "10.1.	The Borrower acknowledges and authorizes the Lender to disclose all information and data relating to Borrower, the Loan Facility, default if any, committed by Borrower to such third parties/ agencies as the Lender may deem appropriate and necessary to disclose and/or as authorized by RBI, including the Credit Information Companies. The Borrower also acknowledges and authorizes such information to be used, processed by the Lender / third parties/RBI as they may deem fit and in accordance with applicable laws. Further in Event of Default, the Lender and such agencies shall have an unqualified right to disclose or publish the name of the Borrower/ co-applicants, as applicable, as 'defaulters' in such manner and through such medium as the Lender / RBI/ other authorized agency in their absolute discretion may think fit, including in newspapers, magazines and social media. \r\n"
			+ "10.2.	The Borrower shall not hold the Lender responsible for sharing and/or disclosing the information now or in future and also for any consequences suffered by the Borrower and/or other by reason thereof. The provisions of this clause shall survive termination of this Agreement and the repayment of the Borrower's Dues. The Borrower further agrees that Lender may, as it deems appropriate and necessary disclose and furnish to TransUnion CIBIL Limited (formerly known as credit Information Bureau (India) Ltd (“CIBIL”) and any other agency authorized in this behalf by RBI all or any of the following:\r\n"
			+ "(a)	information and data relating to the Borrower;\r\n"
			+ "(b)	the information or data relating to the Facilities availed of/to be availed, by the Borrower; and\r\n"
			+ "(c)	the information and details of the default, if any, committed by the Borrower, in discharge of the indebtedness.\r\n"
			+ "";

	String ASSIGNMENT = "11.	ASSIGNMENT";

	String ASSIGNMENT_VAL = "11.1.	The Borrower(s) shall not transfer or assign any of its rights or liabilities under this Agreement to any Person without the prior written consent of the Lender.\r\n"
			+ "11.2.	The Borrower(s) agrees that notwithstanding anything to the contrary contained in any document executed under or in relation to this Agreement, the Lender shall have the right to (in full or in part) assign, transfer, novate and/ or otherwise securitize its rights or obligations under this Agreement and the other Financing Documents and/ or the Outstanding Amounts and/ or enter into indemnity or other arrangements for risk sharing, whether with or without recourse to the Lender, to one or more scheduled commercial banks or any other entity, trust, any association whether located/ placed in India or outside India as permitted under the Applicable Law without any reference or notice to the Borrower(s). However, the Borrower(s) shall not claim any privity of contract with any such entity to whom the outstanding and/ or the rights or obligations under this Agreement or the other documents in relation to the Facility have been assigned/ transferred/ novated/ securitized or with whom the Lenders have entered into indemnity or arrangements for risk sharing.\r\n"
			+ "11.3.	Without prejudice to the right of the Lender to proceed against the Borrower(s) under Applicable Law for recovery of Outstanding Amounts, the Borrower(s) hereby gives consent that the Lender will be entitled, subject to Applicable Law, to recover the dues, assign the debt and/or securities and/ or initiate proceedings under the Applicable Law and/ or Code and the Borrower(s) agree to pay the Lender all cost, Charges and expenses incurred in that connection.\r\n"
			+ "11.4.	The Lender affirms that it does not engage any third-party recovery agents for the purpose of recovering loans granted to the Borrower. All communications and recovery efforts, if necessary, shall be conducted directly by the Lender's authorized personnel in accordance with applicable laws and regulations.\r\n"
			+ "11.5.	The Borrower(s) irrevocably and unconditionally confirms that the Borrower(s) shall continue to be bound by the terms of this Agreement and the other documents in relation to the Facility notwithstanding such transfer or assignment by the Lender.\r\n"
			+ "";

	String WAIVER = "12.	WAIVER";

	String WAIVER_VAL = "No delay in exercising or omission to exercise any right, power or remedy accruing to Lender upon any default or otherwise under the Loan Documents shall impair any such right, power or remedy or shall be construed to be a waiver thereof or any acquiescence in such default, nor shall the action or inaction of Lender in respect of any default or any acquiescence by it in any default, affect or impair any right, power or remedy of Lender in respect of any other default. The rights of the Lender under the Loan Documents may be exercised as often as necessary, are cumulative and not exclusive of their rights under the general law and may be waived only in writing and specifically and the Lenders discretion.";

	String MISCELLANEOUS = "13.	MISCELLANEOUS ";

	String MISCELLANEOUS_VAL = "13.1.	SERVICE OF NOTICE \r\n"
			+ "All notices, requests, demands, waivers or other communications under or in connection with this Terms and Conditions shall be given in writing. Any such notice or other communication will be deemed to have been duly given and being received if: (a) if by personal delivery on the day after such delivery, (b) if by registered mail, on the third Business Day after the mailing thereof, (c) if by next-day or overnight mail/courier or delivery, on the day delivered, (d) if sent by email/facsimile, when sent (on receipt of a confirmation to the correct facsimile number). Provided, however, that no notice or communication to LSP and/or Lender shall be effective unless actually received by LSP and/or Lender. \r\n"
			+ "Notices or communication may be made to: \r\n" + "";

	String MISCELLANEOUS_VAL2 = "13.2.	SEVERABILITY \r\n"
			+ "Every provision contained in this Agreement shall be severable and distinct from every other such provision and if at any time any one or more of such provisions is or becomes invalid, illegal or unenforceable in any respect under any applicable law, the validity, legality and enforceability of the remaining provisions hereof shall not be in any way affected or impaired thereby. \r\n"
			+ "13.3.	ENTIRE AGREEMENT\r\n"
			+ "This Agreement (including the first and the second Schedules) along with the documents executed or to be executed by the Borrower in favour of the Lender pursuant to this Agreement shall constitute the entire agreement between the Parties hereto with respect to its subject matter. \r\n"
			+ "13.4.	COUNTERPARTS\r\n"
			+ "This Agreement may be executed in separate counterparts, each of which, when so executed and delivered to each Party, shall be deemed to be an original, but all such counterparts together shall constitute one and the same instrument only. \r\n"
			+ "13.5.	SUPREMACY & AMENDMENT\r\n"
			+ "This Agreement supersedes all discussions and Agreements (whether oral or written, including all correspondence) prior to the date of this Agreement between the Parties with respect to the subject matter of this Agreement. This Agreement may be modified or amended only by a writing duly executed by or on behalf of each of the Parties. \r\n"
			+ "13.6.	FORCE MAJEURE\r\n"
			+ "The Borrower shall be liable to perform or fulfil its obligations at all point of time and shall not delay/postpone performance, its obligations in whole or in part, even on the ground of any force majeure event such as acts of God, floods, cyclones, earthquakes, fires, wars, riots, strikes, orders of governmental or other statutory authorities.\r\n";

	String MISCELLANEOUS_VAL3 = "13.7.	DISPUTE RESOLUTION AND ARBITRATION\r\n"
			+ "13.7.1.	All disputes (includes default committed by the Borrower/Guarantor as per this Agreement), differences and/or claim arising out of or touching upon this Agreement whether during its subsistence or thereafter shall be settled by arbitration in accordance with the provisions of the Arbitration and Conciliation Act, 1996, or any statutory amendments thereof and shall be referred to Sole Arbitrator nominated by the Lender. The seat, place and venue of Arbitration proceedings shall be at Delhi and the language shall be in English. The award, including any interim award/s given by the Arbitrator shall be final and binding on all Parties concerned. The Arbitrator shall give reasons for the award including interim award/s. The cost of the arbitration shall be equally borne by the Parties. \r\n"
			+ "13.7.2.	It is a term of the Agreement that in the event of such an Arbitrator to whom the matter has been originally referred, resigns or dies or being unable to act for any reason, the Lender, at the time of such death of the arbitrator or of his inability to act as arbitrator, shall appoint another person to act as arbitrator and such a person shall be entitled to proceed with the reference from the stage at which it was left by his predecessor. \r\n"
			+ "13.8.	LAW AND JURISDICTION\r\n"
			+ "This Agreement shall be governed by and construed in accordance with the laws of India and shall be subject to the jurisdiction of the Courts in Delhi city to the exclusion of all other Courts. \r\n"
			+ "13.9.	SANCTION LETTER\r\n"
			+ "The terms of the Sanction Letter, issued by the Lender, shall form part of this Agreement and shall be in addition to and be read in conjunction with the terms of this Agreement. If there are any inconsistencies between the terms of the Sanction Letter and this Agreement then, the terms of Sanction Letter shall prevail to the extent of inconsistencies.\r\n"
			+ "13.10.	GRIEVANCE REDRESSAL AND MECHANISM\r\n"
			+ "To ensure effective resolution of customer grievances, the Parties agree to implement the following grievance redressal mechanism, which shall be adhered to in accordance with the terms of this Agreement:\r\n";

	String GRIEVANCE_REPORTING_CHANNELS = "a)	Grievance Reporting Channels";

	String GRIEVANCE_HANDLING_PROCEDURE = "b)	Grievance Handling Procedure";

	default String grievanceHandlingProcedureVal(String lspName) {
		return "Step 1: Acknowledgment\r\n"
				+ "i.	Acknowledge receipt through SMS/email of the receipt of the complaint within 24 hours\r\n"
				+ "ii.	Provide a unique tracking number for the complaint\r\n" + "\r\n" + "Step 2: Investigation\r\n"
				+ "i.	Investigate the grievances using the available information in the support portal.\r\n"
				+ "ii.	Gather all relevant information and communicate with the customer for clariﬁcation if needed.\r\n"
				+ "\r\n" + "Step 3: Resolution & Escalation\r\n"
				+ "i.	Identify the issue and if the issue is internal to "+lspName+" then the issue to be resolved within 4 working days. Notify the customer about the resolution of the issue and close ticket.\r\n"
				+ "ii.	If the issue is not closed within 4 working days, then escalate to "+lspName+"’s Grievance Ofﬁcer to engage with the customer.\r\n"
				+ "iii.	"+lspName+"’s Grievance Ofﬁcer to either engage with the bank or internally with the "+lspName+" team to resolve the issue within 6 working days.\r\n"
				+ "iv.	 If the issue is not resolved within 6 working days, then "+lspName+" will escalate to the Mufin Green Finance Limited’s Grievance ofﬁce and arrange to provide the Mufin Green Finance Limited Grievance ticket to customer.\r\n"
				+ "v.	If the grievance is not resolved within 30 working days as per the Mufin Green Finance Limited’s Policy, the customer can escalate to RBI Sachet Portal.\r\n"
				+ "\r\n" + "c)	Timeframe for Resolution\r\n" + "As per " + lspName
				+ "’ s Grievance Redressal Framework, the following timeframes have been outlined\r\n" + "";
	}

	default String grievanceHandlingProcedureValV1(String lspName, String authEmail) {
		return "12.12.	GRIEVANCE REDRESSAL AND MECHANISM\r\n"
				+ "To ensure effective resolution of customer grievances, the Parties agree to implement the following grievance redressal mechanism, which shall be adhered to in accordance with the terms of this Agreement:\r\n"
				+ "a)	Grievance Reporting Channels\r\n"
				+ "Customers can report the grievances through following channels: \r\n"
				+ "Level 1: Frontline Support\r\n" + "Email: "+authEmail+" \r\n" + "\r\n"
				+ "Level 2: Grievance Ofﬁcer - HML \r\n" + "Email: helpdesk@mufinfinance.com \r\n" + "\r\n"
				+ "Level 3: Grievance Ofﬁcer – Nodal Officer\r\n" + "Email: psabharwal@mufinfinance.com	\r\n" + "\r\n"
				+ "Level 4: Regulatory – RBI Sachet Portal.\r\n" + "RABI Sachet Portal\r\n" + "\r\n"
				+ "b)	Grievance Handling Procedure\r\n" + "Step 1: Acknowledgment\r\n"
				+ "i.	Acknowledge receipt through SMS/email of the receipt of the complaint within 24 hours\r\n"
				+ "ii.	Provide a unique tracking number for the complaint\r\n" + "\r\n" + "Step 2: Investigation\r\n"
				+ "i.	Investigate the grievances using the available information in the support portal.\r\n"
				+ "ii.	Gather all relevant information and communicate with the customer for clariﬁcation if needed.\r\n"
				+ "\r\n" + "c)	Timeframe for Resolution\r\n"
				+ "As per Grievance Redressal Framework, the following timeframes have been outlined\r\n" + "";
	}

	String DOCUMENTATION_AND_MONITORING = "d)	Documentation and Monitoring";
	
	String SEVERABILITY = "12.13.	SEVERABILITY\r\n"
			+ "Each and every obligation under this Agreement shall be treated as a separate obligation\r\n"
			+ "and shall be severally enforceable as such. To the extent that if any provision of this Agreement, is invalid or\r\n"
			+ "unenforceable or prohibited by law, it shall be treated for all purposes as severed from this Agreement and\r\n"
			+ "ineffective to the extent of such invalidity or unenforceability, without affecting in any way the remaining\r\n"
			+ "provisions hereof, which shall continue to be valid and binding\r\n"
			+ "12.14.	ACCEPTANCE\r\n"
			+ "(a)	The Borrower(s) agrees and acknowledges that he/it has read this Agreement and the duly filled in the schedules/ annexures hereto, and other documents including but not limited to the Sanction Letter. The Borrower(s) declares that the Borrower(s) shall be bound by all the conditions mentioned herein.\r\n"
			+ "(b)	The Borrower(s) further agrees and declares that this Agreement, and other documents including but not limited to the Sanction Letter have been explained to the Borrower(s) in the language understood by the Borrower(s) and that the Borrower(s) has understood the entire meaning of various clauses and the schedules and annexure forming part & parcel of this Agreement as well as of the other documents signed/ executed by the Borrower(s).\r\n"
			+ "";

	String DOCUMENTATION_AND_MONITORING_VAL = "i.	Maintain Records: Lender will ensure that detailed documentation of all grievances, including the nature of the complaint, actions taken, and resolutions provided. These records will be securely stored and easily accessible for future reference and compliance purposes.\r\n"
			+ "ii.	Maintain Records: Lender will produce regular reports summarizing grievance trends, response times, and resolution effectiveness. Will use these insights to identify recurring issues and implement process improvements to enhance overall grievance management.\r\n"
			+ "iii.	Feedback: Lender will actively seek customer feedback on the grievance handling process to enhance its effectiveness and ensure continuous improvement.\r\n"
			+ "e)	Customer Awareness\r\n"
			+ "Lender will publish the grievance redressal mechanism on the company’s website and Include details in loan agreements and FAQs.\r\n"
			+ "f)	Compliance with Regulatory Guidelines\r\n"
			+ "Lender will ensure this framework aligns with the RBI's grievance redressal framework and will periodically review the mechanism to incorporate regulatory updates\r\n"
			+ "";

	String BENEFITS = "13.11.	BENEFITS";

	String BENEFITS_VAL = "The terms and provisions of this Agreement shall be binding upon, and the benefits hereof shall inure to the Borrower(s) successors and permitted assigns and the Lender successors and assigns.";

	String ACCEPTANCE = "ACCEPTANCE";

	String ACCEPTANCE_VAL = "(a)	The Borrower(s) agrees and acknowledges that he/it has read this Agreement and the duly filled in the schedules/ annexures hereto, and other documents including but not limited to the Sanction Letter. The Borrower(s) declares that the Borrower(s) shall be bound by all the conditions mentioned herein.\r\n"
			+ "(b)	The Borrower(s) further agrees and declares that this Agreement, and other documents including but not limited to the Sanction Letter have been explained to the Borrower(s) in the language understood by the Borrower(s) and that the Borrower(s) has understood the entire meaning of various clauses and the schedules and annexure forming part & parcel of this Agreement as well as of the other documents signed/ executed by the Borrower(s).\r\n"
			+ "";

	String DECLARATION = "Declaration/-";

	String DECLARATION_VAL = "I/ We hereby declare that all the clauses of above agreement were read over to me/us and explained to me/ us in my/our vernacular language and after reading and understanding the entire provision and contents of the Credit facility applicable to me/us, all the clauses and implication, and agreeing to abide by all the terms and conditions thereof I/ we have signed the agreement with my/our full knowledge and conscience without any force or undue influences upon me/ us.\r\n"
			+ "\r\n"
			+ "I/ We request you to disburse the Credit Facility amount under the aforesaid Facility Agreement and the Sanction Letter.\r\n"
			+ "\r\n"
			+ "Signed and delivered by/ for and on behalf of the Borrower(s) & Co-Borrower(s) in token of and in witness of them having read (and/or being explained), verified, understood, irrevocably agreed to, accepted, confirmed, and declared "
			+ "all the clauses of this Agreement, the Schedule, all contents thereof including all the terms and conditions contained therein, and having authenticated accuracy and correctness of the same:";

	String IN_WRITTEN = "IN WITNESS WHEREOF, LENDER, BORROWER AND LSP HAVE EXECUTED THIS AS OF THE DAY AND YEAR FIRST ABOVE WRITTEN";

	String BANK_ACCOUNT_DETAILS_OF_BORROWER = "Bank Account Details of Borrower";

	default Table addCell(Table table, String content, int size, TextAlignment textAlignment) {
		return table.addCell(
				new Cell(1, 6).add(new Paragraph(content).setBold().setFontSize(size)).setTextAlignment(textAlignment));
	}

	default Table addCell(int left, int right, Table table, String content, int size, TextAlignment textAlignment) {
		return table.addCell(new Cell(left, right).add(new Paragraph(content).setBold().setFontSize(size))
				.setTextAlignment(textAlignment));
	}
}
