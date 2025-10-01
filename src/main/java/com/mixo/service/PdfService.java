package com.mixo.service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerBank;
import com.mixo.model.BorrowerPan;
import com.mixo.model.EmiBreakUp;
import com.mixo.model.Nbfc;
import com.mixo.model.Product;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerBankRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.repository.EmiBreakUpRepository;
import com.mixo.utils.DataProvider;

@Service
public class PdfService implements DataProvider {

	@Autowired
	BorrowerRepository borrowerRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	EmiBreakUpRepository emiBreakUpRepository;

	@Autowired
	BorrowerBankRepository borrowerBankRepository;

	@Autowired
	ProductService productService;

	@Autowired
	NbfcService nbfcService;

	public static final String XXX = "XXX";

	public byte[] generateDynamicLetterheadPdf(String borrowerUid) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
		Borrower borrowerObj = borrower.get();

		Product product = productService.getProductByProductIdAndUid(borrowerObj.getProductId(),
				borrowerObj.getLenderUid());

		if (product.getKfs().equalsIgnoreCase("KFS 1")) {
			return generateDynamicLetterheadPdfV1(borrowerUid);
		}

		if (product.getKfs().equalsIgnoreCase("KFS 2")) {
			return generateDynamicLetterheadPdfV2(borrowerUid);
		}

		return generateDynamicLetterheadPdfV1(borrowerUid);

	}

	private byte[] generateDynamicLetterheadPdfV2(String borrowerUid) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
		Borrower borrowerObj = borrower.get();

		List<EmiBreakUp> emiBreakUpList = emiBreakUpRepository.findByBorrowerUid(borrowerUid);

		BorrowerBank borrowerBank = borrowerBankRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());

		BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(borrowerObj.getLenderUid());

		Product product = productService.getProductByProductIdAndUid(borrowerObj.getProductId(),
				borrowerObj.getLenderUid());
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf, PageSize.A4);

			PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA); // Built-in
			// PdfFont font = PdfFontFactory.createFont("path/to/Roboto-Regular.ttf",
			// PdfEncodings.IDENTITY_H, true); // Custom Font

			document.setFont(font);

			// Attach Event Handler for Letterhead on Every Page
			pdf.addEventHandler(PdfDocumentEvent.START_PAGE,
					new LetterheadEventHandler(nbfc.get().getOrganisationLogoPath()));

			document.add(new Paragraph("KEY FACT STATEMENT").setFontSize(16).setBold().setMarginTop(80)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("Part 1 (Interest rate and fees/charges)").setFontSize(12).setBold()
					.setTextAlignment(TextAlignment.CENTER));

			Table tableKFS = new Table(13); // Adjust column count based on your needs
			tableKFS.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

			// Row 1: Loan proposal/Account No. and Type of Loan
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("1").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Loan proposal/ account No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getLoanAggrement()).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Type of Loan").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 2).add(new Paragraph("PL- EWA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			// Row 2: Sanctioned Loan Amount
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("2").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 7).add(new Paragraph("Sanctioned Loan amount (in Rupees)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanAmount().toString()).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 3: Disbursal Schedule
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("3").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(new Paragraph(
					"Disbursal schedule:\n(i) Disbursement in stages or 100% upfront.\n(ii) If it is stage wise, mention the clause of loan agreement having relevant details")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 6).add(new Paragraph("100%").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			// Row 4: Loan Term (Months)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("4").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(new Paragraph("Loan term (year/months/days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanDays().toString() + " Days").setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			// Row 5: Instalment details (Header)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("5").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph("Instalment details").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 6: Instalment details (Columns)
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Type of instalments").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("Number of EMIs").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("EMI (INR)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3)
					.add(new Paragraph("Commencement of repayment, post sanction").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 6: Instalment details (Columns)
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Bullet").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("1").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiAmount().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 7: Interest Rate
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("6").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(
					new Paragraph("Interest rate (%) and type (fixed or floating or hybrid)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiRate() + " Fixed").setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 8: Additional Information (Floating Interest)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("7").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableKFS.addCell(new Cell(1, 12)
					.add(new Paragraph("Additional Information in case of Floating rate of interest").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("Reference Benchmark").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Benchmark rate (%) (B)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Spread (%) (S)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Final rate (%) R = (B) + (S)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Reset periodicity (Months)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2)
					.add(new Paragraph("Impact of change in the reference benchmark").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 1: Headers
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Reference Benchmark")));
			tableKFS.addCell(new Cell(2, 1).add(new Paragraph("Benchmark rate (%) (B)")));
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Spread (%) (S)")));
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Final rate (%) R = (B) + (S)")));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Reset periodicity (Months)")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(
					"Impact of change in the reference benchmark (for 25 bps change in ‘R’, change in:)")));

			// Row 2: Sub-Headers under "Impact of change..."
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("B")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("S")));
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("EMI (INR)")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("No. of EMIs")));

			// Empty Floating Interest Rate Row (NA)
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			// Add empty row before Row 9 (Fees/Charges) if necessary to ensure proper
			// spacing
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph(" ")).setTextAlignment(TextAlignment.LEFT)); // Empty row

			// Row 9: Fees/Charges
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("8").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph("Fee/ Charges").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tableKFS);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph(" ").setMarginTop(60));
			Table tableKFS2 = new Table(13); // Adjust column count based on your needs
			tableKFS2.setWidth(UnitValue.createPercentValue(100));

			tableKFS2.addCell(new Cell(1, 4).add(new Paragraph("").setBold().setMarginTop(60).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 4).add(new Paragraph("Payable to the RE (A)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 5)
					.add(new Paragraph("Payable to a third party through RE (B)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("One-time/ Recurring").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2)
					.add(new Paragraph("Amount (in INR) or Percentage (%) as applicable").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("One- time/Recurring").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 3).add(new Paragraph("Amount (in INR) or Percentage\r\n" + "(%) as applicable\r\n" + "")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Processing fees").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 2).add(new Paragraph(product.getProcessingFee().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 3).add(new Paragraph(product.getProcessingFee().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Insurance charges").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Valuation fees").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iv)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Any other (please specify)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			// Row 10: Annual Percentage Rate (APR)

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("9").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Annual Percentage Rate (APR) (%)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getApr().toString() + " annually").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("10").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 12).add(new Paragraph("Details of Contingent Charges (in INR or %, as applicable)")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph("Penal charges, if any, in case of delayed payment").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("3% per month on outstanding amount").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(ii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("Other penal charges, if any").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Foreclosure charges, if applicable").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iv)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph("Charges for switching of loans from floating to fixed rate and vice versa")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(v)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Any other charges (please specify)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tableKFS2);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.add(new Paragraph("Part 2 (Other qualitative information)").setBold().setFontSize(12)
					.setMarginTop(60).setTextAlignment(TextAlignment.CENTER));

			Table tableOtherInfo = new Table(12); // Adjust column count based on your needs
			tableOtherInfo.setWidth(UnitValue.createPercentValue(100));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("1").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Clause of Loan agreement relating to engagement of recovery agents").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("Clause 11.3").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("2").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Clause of Loan agreement which details grievance redressal mechanism").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("Clause 13.2").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("3").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Phone number and email id of the nodal grievance redressal officer").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5)
					.add(new Paragraph("Number - +91- 9811169253\r\n" + "E-mail id - helpdesk@mufinfinance.com\r\n")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("4").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"Whether the loan is, or in future maybe, subject to transfer to other REs or securitisation (Yes/ No)")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("No").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("5").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 11).add(new Paragraph(
					"In case of lending under collaborative lending arrangements (e.g., co-lending/ outsourcing), following additional details may be furnished:")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph("Name of the originating RE, along with its funding proportion")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph("Name of the partner RE along with its proportion of funding")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph("Blended rate of interest").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph(NA).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph(NA).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph(borrowerObj.getEmiRate().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("6").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 11)
					.add(new Paragraph("In case of digital loans, following specific disclosures may be furnished:")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"(i)  Cooling off/look-up period, in terms of RE’s board approved policy, during which borrower shall not be charged any penalty on prepayment of loan")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph("3 Days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"(ii)	Details of LSP acting as recovery agent and authorized to approach the borrower").setBold()
					.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 6).add(new Paragraph("NA").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			document.add(tableOtherInfo);

//			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

//			document.add(new Paragraph("Computation of APR").setBold().setFontSize(12).setMarginTop(60)
//					.setTextAlignment(TextAlignment.CENTER));
//
//			Table tableApr = new Table(12); // Adjust column count based on your needs
//			tableApr.setWidth(UnitValue.createPercentValue(100));
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
//			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Parameters").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph("Details").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("1.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("Sanctioned Loan amount (in Rupees) ").setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(String.valueOf(borrowerObj.getLoanAmount())).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("2.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("Loan Term (in years/ months/ days)  ").setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getLoanDays().toString() + " days").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("a)").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8).add(new Paragraph(
//					"No. of instalments for payment of principal, in case of non- equated periodic loans ").setBold()
//					.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph("NA").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("b)").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("Type of EPI\r\n" + "Amount of each EPI (in Rupees) and\r\n"
//							+ "nos. of EPIs (e.g., no. of EMIs in case of monthly instalments)\r\n" + " ").setBold()
//							.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph("Bullet\r\n" + "" + String.valueOf(borrowerObj.getLoanAmount()) + "\r\n" + ""
//							+ borrowerObj.getEmiTime() + "\r\n" + "").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("c)").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("No. of instalments for payment of capitalised interest, if any")
//							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph("NA").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("d)").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Commencement of repayments, post sanction ").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("3.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Interest rate type (fixed or floating or hybrid) ").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph("Fixed").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("4.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Rate of Interest  ").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiRate().toString()).setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("5.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8).add(new Paragraph(
//					"Total Interest Amount to be charged during the entire tenor of the loan as per the rate prevailing on sanction date (in Rupees)")
//					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getTotalInterest().toString()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			// -----------------------
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("6.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("Fee/ Charges payable (in Rupees)").setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getTotalInterest().toString()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("A.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Payable to the Lender").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getTotalInterest().toString()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("B.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Payable to third-party routed through Lender").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3).add(new Paragraph("0").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("7.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 8).add(new Paragraph("Net disbursed amount (in Rupees)").setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getSectionAmount().toString()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("8.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Total amount to be paid by the borrower (in Rupees)").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 3)
//					.add(new Paragraph(borrowerObj.getTotalLoanAmount().toString()).setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("9.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Annual Percentage rate- Effective annualized interest rate (in percentage)")
//							.setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 3).add(new Paragraph(borrowerObj.getApr().toString()).setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("10.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8).add(
//					new Paragraph("Schedule of disbursement as per terms and conditions").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 3).add(new Paragraph("Detailed schedule to be provided").setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//
//			tableApr.addCell(new Cell(1, 1).add(new Paragraph("11.").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(new Cell(1, 8)
//					.add(new Paragraph("Due date of payment of instalment and interest").setBold().setFontSize(11))
//					.setTextAlignment(TextAlignment.LEFT));
//			tableApr.addCell(
//					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate().toString()).setBold().setFontSize(11))
//							.setTextAlignment(TextAlignment.LEFT));
//			// -----------------------
//
//			document.add(tableApr);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.add(new Paragraph("REPAYMENT SCHEDULE *").setBold().setFontSize(12).setMarginTop(60));

			Table repayment = new Table(13); // Adjust column count based on your needs
			repayment.setWidth(UnitValue.createPercentValue(100));

			repayment.addCell(new Cell(1, 3).add(new Paragraph("Installment No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 3).add(new Paragraph("Due Date").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 3).add(new Paragraph("Principal (in Rupees)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 3).add(new Paragraph("Interest (in Rupees)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 3).add(new Paragraph("Installment (in Rupees)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			for (EmiBreakUp emiBreakUp : emiBreakUpList) {

				repayment.addCell(new Cell(1, 3)
						.add(new Paragraph(String.valueOf(emiBreakUp.getInstallmentNo())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 3)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueDate())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 3)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueAmount())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 3)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueAmount())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 3)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueAmount())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
			}

			document.add(repayment);

			document.add(new AreaBreak());
			// Page 2

			document.add(new Paragraph("SANCTION LETTER").setFontSize(18).setMarginTop(50).setBold()
					.setTextAlignment(TextAlignment.CENTER));

			Table table = new Table(2); // Two columns
			table.setWidth(UnitValue.createPercentValue(100)); // 100% width for the table

			// First column - Left-aligned text
			table.addCell(new Cell()
					.add(new Paragraph("Reference No : " + borrowerObj.getLoanAggrement()).setBold().setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
			// Second column - Right-aligned text
			table.addCell(new Cell().add(new Paragraph("Date: " + today).setFontSize(12))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
			table.addCell(new Cell().add(new Paragraph("Borrower Name: " + borrowerObj.getFullName()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
			table.addCell(
					new Cell().add(new Paragraph("Address:" + borrowerObj.getSecondaryAddressLine1()).setFontSize(12))
							.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			document.add(table);

			document.add(new Paragraph("Dear Sir/ Madam, "));
			document.add(new Paragraph("Please refer to your financial assistance application "
					+ borrowerObj.getLoanAggrement()
					+ " for a loan from Mufin Green Finance Limited (“Lender”) made through "
					+ borrowerObj.getLenderName()
					+ " (“LSP”) – a Digital Lending mobile application & Loan Service Provider of the Lender. We are happy to inform you that we have sanctioned a sum of Rs. "
					+ borrowerObj.getLoanAmount()
					+ "/- against your application. The following are the broad terms & conditions governing the financial assistance."));
			document.add(new Paragraph(
					"We look forward to disbursing this loan and request you to complete all the formalities in this regard."));
			document.add(new Paragraph("Annexure 1").setBold());

			Table tablesec = new Table(12); // Adjust column count based on your needs
			tablesec.setWidth(UnitValue.createPercentValue(100));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Borrower Name").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getFullName()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Nature of Facility").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Unsecured Personal Loan	").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Type of Facility").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph("Accrued salary advance facility").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Purpose/End Use").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getPurposeOfLoan()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Loan Tenor (in month/days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getLoanDays().toString() + " Days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Loan Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanAmount().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Net Disbursed Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getSectionAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Total Repayment Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getTotalLoanAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Amount Breakup ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph("Principal:" + borrowerObj.getLoanAmount().toString() + "\t" + "Interest:"
							+ borrowerObj.getTotalInterest().toString() + "\t" + "").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Type of Interest").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Fixed").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Rate of Interest").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiRate().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Type").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Bullet").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Frequency").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("One-time").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("One-time").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiAmount().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Due Date").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Mode of Repayment").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph("To be deducted from the salary of the employee as per payment schedule")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Special Condition (if any)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Security").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Unsecured").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Cooling Off Period (days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("3 days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph("Digital Lending App (DLA)/Loan Service Provider (LSP) Details")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getLenderName()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tablesec);

			document.add(new AreaBreak());

			document.add(new Paragraph("Details of Fees & Charges").setBold().setMarginTop(60));

			Table tablesec1 = new Table(12); // Adjust column count based on your needs
			tablesec1.setWidth(UnitValue.createPercentValue(100));
			Map<String, String> chargeDetails = new LinkedHashMap<>();
			chargeDetails.put("Processing Fee (inc. GST) (₹)", product.getProcessingFee().toString());
			chargeDetails.put("Insurance Charges (inc. GST) (₹)", NA);
			chargeDetails.put("Overdue Interest", "3% per month for default period & delayed instalments");
			chargeDetails.put("Penal Charges", NA);
			chargeDetails.put("Bounce Charges (₹)", "500 per instance + GST");
			chargeDetails.put(PDC_CH_SWAP_CHARGES, NA);
			chargeDetails.put(FULL_PART_PREPAYMENT_CHARGES, NA);
			chargeDetails.put(PDC_CH_SWAP_CHARGES, NA);
			chargeDetails.put(DUPLICATE_STATEMENT_CHARGES, NA);
			chargeDetails.put(EMI_FOLLOW_UP_CHARGES, NA);
			chargeDetails.put(STAMP_DUTY_CHARGES, NA);
			chargeDetails.put(ANY_OTHER_CHARGES, NA);
			chargeDetails.forEach((label, value) -> {
				addCell(tablesec1, label, 11, TextAlignment.LEFT);
				addCell(tablesec1, value, 11, TextAlignment.LEFT);
			});
			document.add(tablesec1);

			document.add(new AreaBreak());

			Table tablesec2 = new Table(10).setMarginTop(60);
			tablesec2.setWidth(UnitValue.createPercentValue(100));
			tablesec2.addCell(new Cell(1, 6).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tablesec2.addCell(new Cell(1, 6).add(new Paragraph("Particulars").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			DataProvider.getSanctionConditionsMap().forEach((serial, condition) -> {
				addCell(tablesec2, String.valueOf(serial), 11, TextAlignment.LEFT);
				addCell(tablesec2, condition, 11, TextAlignment.LEFT);
			});
			document.add(tablesec2);

			document.add(new AreaBreak());

			Table tablesec3 = new Table(10).setMarginTop(60);
			tablesec3.setWidth(UnitValue.createPercentValue(100));
			DataProvider.getSanctionConditionsMaps(XXX, borrowerObj.getLenderName()).forEach((serial, condition) -> {
				addCell(tablesec3, String.valueOf(serial), 11, TextAlignment.LEFT);
				addCell(tablesec3, condition, 11, TextAlignment.LEFT);
			});
			document.add(tablesec3);

			document.add(new Paragraph(CONDITION_LIST_END));
			document.add(new Paragraph("Borrower Name: " + borrowerObj.getFullName()).setMarginTop(20));
			document.add(new Paragraph("________________").setMarginTop(20));
			document.add(new Paragraph("Signature"));

			document.add(new AreaBreak());

			// Page 1
			document.add(new Paragraph("UNDERTAKING FROM BORROWER").setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(60));

			document.add(new Paragraph(declarationFirst(XXX, borrowerObj.getLenderName())));
			document.add(new Paragraph(DECLARATION_SECOND));
			document.add(new AreaBreak());
			document.add(new Paragraph(DECLERATION_THIRD).setMarginTop(60));
			document.add(new Paragraph(DECLERATION_FOURTH));
			document.add(new Paragraph(DECLERATION_FIFTH));
			document.add(new Paragraph(DECLERATION_SIXTH));

			document.add(new AreaBreak());

			document.add(new Paragraph("PERSONAL LOAN AGREEMENT").setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(60));

			document.add(new Paragraph(PERSONAL_LOAN_AGG_1));
			document.add(new Paragraph(
					personalLoanAgg2(borrowerObj.getLenderName(), nbfc.get().getCinNumber(), nbfc.get().getAddress())));
			document.add(new Paragraph(personalLoanAgg3(nbfc.get().getNbfcName(), nbfc.get().getCinNumber(),nbfc.get().getAddress())));
			document.add(new Paragraph(PERSONAL_LOAN_AGG_4));
			document.add(new Paragraph("WHEREAS").setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(10).setMarginBottom(10));
			document.add(new Paragraph(PERSONAL_LOAN_AGG_5));

			document.add(new Paragraph(NOW_THERE).setBold().setFontSize(13).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(40));

			document.add(new Paragraph(defination_1_point_1_and_2(borrowerObj.getFullName())));
			document.add(new Paragraph(defination_1_point_1_to_1_point_9(borrowerObj.getFullName())));
			document.add(new Paragraph(defination_1_point_9_to_1_point_14(borrowerObj.getFullName())).setMarginTop(60));
			document.add(new Paragraph(defination_1_point_15_to_1_point_21(borrowerObj.getFullName())));
			document.add(new Paragraph(defination_1_point_22_to_1_point_29(borrowerObj.getLenderName())).setMarginTop(60));

			document.add(new AreaBreak());

			document.add(new Paragraph(defination_1_point_30_to_1_point_35()).setMarginTop(60));
			document.add(new Paragraph(defination_1_point_36_to_1_point_37()));

			document.add(new AreaBreak());

			document.add(new Paragraph(INTERPRETATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(INTERPRETATION_VAL));
			document.add(new AreaBreak());
			document.add(new Paragraph(PURPOSE_ANDDISBURSEMENT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(PURPOSE_ANDDISBURSEMENT_VAL));
			document.add(new Paragraph(CONDITIONS_PRECEDENT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(CONDITIONS_PRECEDENT_VAL));
			document.add(new AreaBreak());
			document.add(new Paragraph(CONDITIONS_PRECEDENT_VAL2).setMarginTop(60));
			document.add(new Paragraph(INTEREST_AND_REPAYMENT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(INTEREST_AND_REPAYMENT_VAL));

			document.add(new AreaBreak());
			// TODO:TABLE

			document.add(new Paragraph(MORE_OVER).setMarginTop(60));

			document.add(new AreaBreak());
			// TODO:TABLE

			document.add(new Paragraph(MORE_OVER2).setMarginTop(60));

			document.add(new AreaBreak());
			document.add(new Paragraph(CANCELLATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(CANCELLATION_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(REPRESENTATIONS_AND_WARRANTIES).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(REPRESENTATIONS_AND_WARRANTIES_VAL));
			document.add(new AreaBreak());
			document.add(new Paragraph(REPRESENTATIONS_AND_WARRANTIES_VAL2).setMarginTop(60));

			document.add(new Paragraph(COVENANTS_OF_THE_BORROWER).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(COVENANTS_OF_THE_BORROWER_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(COVENANTS_OF_THE_BORROWER_VAL2).setMarginTop(60));
			document.add(new Paragraph(NEGATIVE_COVENANTS).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(NEGATIVE_COVENANTS_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF).setBold().setFontSize(14).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF_VAL));

			document.add(new Paragraph(EVENTS_OF_DEFAULT_AND_CONSEQUENCES_THEREOF_VAL2).setMarginTop(60));

			document.add(new Paragraph(CONSEQUENCES_OF_EVENT_OF_DEFAULT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(CONSEQUENCES_OF_EVENT_OF_DEFAULT_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(INDEMNITY).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(INDEMNITY_VAL));

			document.add(new Paragraph(DISCLOSURES).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(DISCLOSURES_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(ASSIGNMENT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(ASSIGNMENT_VAL));

			document.add(
					new Paragraph(WAIVER).setBold().setFontSize(16).setBold().setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(WAIVER_VAL));

			document.add(new Paragraph(MISCELLANEOUS).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(MISCELLANEOUS_VAL));
			document.add(new Paragraph(MISCELLANEOUS_VAL2));
			document.add(new Paragraph(MISCELLANEOUS_VAL3).setMarginTop(60));

			// TODO: GRIEVANCE_REPORTING_CHANNELS

			document.add(new AreaBreak());
			document.add(new Paragraph(GRIEVANCE_HANDLING_PROCEDURE).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(grievanceHandlingProcedureVal(borrowerObj.getLenderName())));

			Table tableTimeFrame = new Table(12); // Adjust column count based on your needs
			tableTimeFrame.setWidth(UnitValue.createPercentValue(100));

			tableTimeFrame.addCell(new Cell(1, 6).add(new Paragraph("Action").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableTimeFrame.addCell(new Cell(1, 6).add(new Paragraph("Time Frame").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			Map<String, String> timeFrameMap = new LinkedHashMap<>();
			timeFrameMap.put("Acknowledgment", "24 Hours");
			timeFrameMap.put("Escalation to Level 2: " + borrowerObj.getLenderName() + " Grievance Ofﬁcer",
					"After 5 Working days");// TODO
			timeFrameMap.put("Escalation to Level 3: Mufin Green Finance Limited’s Grievance Ofﬁcer ",
					"After 6 Working days");
			timeFrameMap.put("Escalation to Level 4: RBI Sachet Portal", "After 30 Working days");
			timeFrameMap.forEach((label, value) -> {
				addCell(tableTimeFrame, label, 11, TextAlignment.LEFT);
				addCell(tableTimeFrame, value, 11, TextAlignment.LEFT);
			});
			document.add(tableTimeFrame);

			document.add(new AreaBreak());
			document.add(new Paragraph(DOCUMENTATION_AND_MONITORING).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(DOCUMENTATION_AND_MONITORING_VAL));

			document.add(new Paragraph(BENEFITS).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(BENEFITS_VAL));

			document.add(new Paragraph(ACCEPTANCE).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(ACCEPTANCE_VAL));

			document.add(new AreaBreak());
			document.add(new Paragraph(DECLARATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(DECLARATION_VAL));

			document.add(new Paragraph(IN_WRITTEN));

			Table tableWritten = new Table(12); // Adjust column count based on your needs
			tableTimeFrame.setWidth(UnitValue.createPercentValue(100));

			tableWritten.addCell(
					new Cell(1, 4).add(new Paragraph("For Mufin Green Finance Limited").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 4).add(new Paragraph("For Borrower").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 4).add(new Paragraph("For LSP").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 4)
					.add(new Paragraph("Name: Gunjan Jain \r\n Designation: CFO").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(
					new Cell(1, 4).add(new Paragraph("Name: " + borrowerObj.getFullName()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 4)
					.add(new Paragraph("Name: _____________ \r\n Designation: ___________").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));
			document.add(tableWritten);

			document.add(new Paragraph("Schedule I\r\n" + "Details of the Personal Loan – Salary Advance\r\n" + "")
					.setBold().setFontSize(12).setMarginTop(60).setTextAlignment(TextAlignment.CENTER));

			Table tableSchedule = new Table(12); // Adjust column count based on your needs
			tableSchedule.setWidth(UnitValue.createPercentValue(100));
			tableSchedule.addCell(new Cell(1, 2).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableSchedule.addCell(new Cell(1, 4).add(new Paragraph("Particulars").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableSchedule.addCell(new Cell(1, 6).add(new Paragraph("Details").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));

			// Adding borrower details
			addCell(1, 2, tableSchedule, "1. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Borrower", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getFullName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "2. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "PAN", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerPan.getPanNumber(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "3. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Address of the Borrower", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getSecondaryAddressLine1() + " " + borrowerObj.getSecondaryArea(),
					11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "4. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Guarantor", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "5. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Address of the Guarantor", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "6. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Mobile no.", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getMobileNo(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "7. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Email ID", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getEmailId(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "8. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Lender", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "9. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Employer", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			document.add(tableSchedule);

			Table tableSchedule1 = new Table(12).setMarginTop(60);
			tableSchedule1.setWidth(UnitValue.createPercentValue(100));

			addCell(1, 2, tableSchedule1, "10. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Address of the Employer", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, nbfc.get().getAddress(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "11. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Loan Application no.", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanAggrement(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "12. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Sanction Letter", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "13. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Loan Agreement", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "14. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Place of Execution", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Delhi", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "15. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Nature of Loan", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Unsecured Personal Loan – Salary Advance", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "16. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Purpose", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getPurposeOfLoan(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "17. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Sanctioned Loan Amount", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanAmount().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "18. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Rate of Interest (Interest)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getEmiRate().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "19. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Type of Interest", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Fixed", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "20. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Tenor of the facility", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanDays().toString() + " Days", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "21. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Repayment", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Bullet", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "22. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Primary Repayment Mode", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1,
					"Repayable in 1 monthly instalments (EMIs) EMI (Principal+ Interest) payable commencing one month from the date of disbursement.",
					11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "23. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Secondary Repayment Mode", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "24. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Security", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "25. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Disbursement", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "26. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Repayment date (Due Date)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getEmiDate(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "27. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Penal Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getPenalCharges().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "28. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Other Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getOtherCharges().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "29. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Processing fees (₹)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getProcessingFee().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "30. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Stamp duty", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getStampDuty().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "31. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Foreclosure Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "32. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "LSP (Loan Service Provider)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "33. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "DLA (Digital Lending App)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			document.add(tableSchedule1);

			document.add(new AreaBreak());
			document.add(new Paragraph(BANK_ACCOUNT_DETAILS_OF_BORROWER).setFontSize(12).setBold().setMarginTop(60));

			Table tableSecd = new Table(2); // Two columns
			tableSecd.setWidth(UnitValue.createPercentValue(100)); // 100% width for the table

			tableSecd.addCell(new Cell().add(new Paragraph("Bank's Account Holder Name ").setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerObj.getFullName()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(new Cell().add(new Paragraph("Account Number ").setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getBankAccountNo()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Account Type").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getAccountType()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Bank Name ").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getIfscCode()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Branch").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(XXX).setFontSize(12)).setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("IFSC Code ").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getIfscCode()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("MICR").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(XXX).setFontSize(12)).setTextAlignment(TextAlignment.LEFT));

			document.add(tableSecd);

			document.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF", e);
		}

	}

	private byte[] generateDynamicLetterheadPdfV1(String borrowerUid) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
		Borrower borrowerObj = borrower.get();

		List<EmiBreakUp> emiBreakUpList = emiBreakUpRepository.findByBorrowerUid(borrowerUid);

		BorrowerBank borrowerBank = borrowerBankRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());

		BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(borrowerObj.getLenderUid());

		Product product = productService.getProductByProductIdAndUid(borrowerObj.getProductId(),
				borrowerObj.getLenderUid());
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf, PageSize.A4);

			PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA); // Built-in
			// PdfFont font = PdfFontFactory.createFont("path/to/Roboto-Regular.ttf",
			// PdfEncodings.IDENTITY_H, true); // Custom Font

			document.setFont(font);

			// Attach Event Handler for Letterhead on Every Page
			pdf.addEventHandler(PdfDocumentEvent.START_PAGE,
					new LetterheadEventHandler(nbfc.get().getOrganisationLogoPath()));

			document.add(new Paragraph("KEY FACT STATEMENT").setFontSize(16).setBold().setMarginTop(80)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("Part 1 (Interest rate and fees/charges)").setFontSize(12).setBold()
					.setTextAlignment(TextAlignment.CENTER));

			Table tableKFS = new Table(13); // Adjust column count based on your needs
			tableKFS.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

			// Row 1: Loan proposal/Account No. and Type of Loan
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("1").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Loan proposal/ account No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getLoanAggrement()).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Type of Loan").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Personal Loan").setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 2: Sanctioned Loan Amount
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("2").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 7).add(new Paragraph("Sanctioned Loan amount (in Rupees)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanAmount().toString()).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 3: Disbursal Schedule
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("3").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(new Paragraph(
					"Disbursal schedule:\n(i) Disbursement in stages or 100% upfront.\n(ii) If it is stage wise, mention the clause of loan agreement having relevant details")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 6).add(new Paragraph("100%").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			// Row 4: Loan Term (Months)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("4").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(new Paragraph("Loan term (year/months/days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanDays().toString() + " Days").setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			// Row 5: Instalment details (Header)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("5").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph("Instalment details").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 6: Instalment details (Columns)
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Type of instalments").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("Number of EMIs").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("EMI (INR)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3)
					.add(new Paragraph("Commencement of repayment, post sanction").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 6: Instalment details (Columns)
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("EMI").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3)
					.add(new Paragraph(String.valueOf(borrowerObj.getEmiTime())).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiAmount().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 7: Interest Rate
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("6").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 7).add(
					new Paragraph("Interest rate (%) and type (fixed or floating or hybrid)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiRate() + " Fixed").setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 8: Additional Information (Floating Interest)
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("7").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableKFS.addCell(new Cell(1, 12)
					.add(new Paragraph("Additional Information in case of Floating rate of interest").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("Reference Benchmark").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Benchmark rate (%) (B)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Spread (%) (S)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Final rate (%) R = (B) + (S)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2).add(new Paragraph("Reset periodicity (Months)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 2)
					.add(new Paragraph("Impact of change in the reference benchmark").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// Row 1: Headers
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Reference Benchmark")));
			tableKFS.addCell(new Cell(2, 1).add(new Paragraph("Benchmark rate (%) (B)")));
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Spread (%) (S)")));
			tableKFS.addCell(new Cell(2, 2).add(new Paragraph("Final rate (%) R = (B) + (S)")));
			tableKFS.addCell(new Cell(1, 4).add(new Paragraph("Reset periodicity (Months)")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph(
					"Impact of change in the reference benchmark (for 25 bps change in ‘R’, change in:)")));

			// Row 2: Sub-Headers under "Impact of change..."
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("B")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("S")));
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("EMI (INR)")));
			tableKFS.addCell(new Cell(1, 3).add(new Paragraph("No. of EMIs")));

			// Empty Floating Interest Rate Row (NA)
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(2, 2).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 1).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(
					new Cell(1, 3).add(new Paragraph("NA").setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			// Add empty row before Row 9 (Fees/Charges) if necessary to ensure proper
			// spacing
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph(" ")).setTextAlignment(TextAlignment.LEFT)); // Empty row

			// Row 9: Fees/Charges
			tableKFS.addCell(new Cell(1, 1).add(new Paragraph("8").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS.addCell(new Cell(1, 12).add(new Paragraph("Fee/ Charges").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tableKFS);
			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			document.add(new Paragraph(" ").setMarginTop(60));
			Table tableKFS2 = new Table(13); // Adjust column count based on your needs
			tableKFS2.setWidth(UnitValue.createPercentValue(100));

			tableKFS2.addCell(new Cell(1, 4).add(new Paragraph("").setBold().setMarginTop(60).setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 4).add(new Paragraph("Payable to the RE (A)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 5)
					.add(new Paragraph("Payable to a third party through RE (B)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("One-time/ Recurring").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2)
					.add(new Paragraph("Amount (in INR) or Percentage (%) as applicable").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("One- time/Recurring").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 3).add(new Paragraph("Amount (in INR) or Percentage\r\n" + "(%) as applicable\r\n" + "")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Processing fees").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 2).add(new Paragraph(product.getProcessingFee().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("One-time").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 3).add(new Paragraph(product.getProcessingFee().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Insurance charges").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Valuation fees").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iv)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("Any other (please specify)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 2).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 3).add(new Paragraph("N.A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			// Row 10: Annual Percentage Rate (APR)

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("9").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Annual Percentage Rate (APR) (%)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getApr().toString() + " annually").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("10").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 12).add(new Paragraph("Details of Contingent Charges (in INR or %, as applicable)")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(i)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph("Penal charges, if any, in case of delayed payment").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("3% per month on outstanding amount").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(ii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("Other penal charges, if any").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iii)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Foreclosure charges, if applicable").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(iv)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6)
					.add(new Paragraph("Charges for switching of loans from floating to fixed rate and vice versa")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableKFS2.addCell(new Cell(1, 1).add(new Paragraph("(v)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(
					new Cell(1, 6).add(new Paragraph("Any other charges (please specify)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableKFS2.addCell(new Cell(1, 6).add(new Paragraph("").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tableKFS2);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.add(new Paragraph("Part 2 (Other qualitative information)").setBold().setFontSize(12)
					.setMarginTop(60).setTextAlignment(TextAlignment.CENTER));

			Table tableOtherInfo = new Table(12); // Adjust column count based on your needs
			tableOtherInfo.setWidth(UnitValue.createPercentValue(100));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("1").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Clause of Loan agreement relating to engagement of recovery agents").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("Clause 2.7").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("2").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Clause of Loan agreement which details grievance redressal mechanism").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("Clause 12.12").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("3").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6)
					.add(new Paragraph("Phone number and email id of the nodal grievance redressal officer").setBold()
							.setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5)
					.add(new Paragraph("Number - +91- 9811169253\r\n" + "E-mail id - helpdesk@mufinfinance.com\r\n")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("4").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"Whether the loan is, or in future maybe, subject to transfer to other REs or securitisation (Yes/ No)")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 5).add(new Paragraph("No").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("5").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 11).add(new Paragraph(
					"In case of lending under collaborative lending arrangements (e.g., co-lending/ outsourcing), following additional details may be furnished:")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph("Name of the originating RE, along with its funding proportion")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph("Name of the partner RE along with its proportion of funding")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph("Blended rate of interest").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph("NA").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 4).add(new Paragraph("NA").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(
					new Cell(1, 4).add(new Paragraph("NA").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 1).add(new Paragraph("6").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 11)
					.add(new Paragraph("In case of digital loans, following specific disclosures may be furnished:")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"(i)  Cooling off/look-up period, in terms of RE’s board approved policy, during which borrower shall not be charged any penalty on prepayment of loan")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableOtherInfo.addCell(new Cell(1, 6).add(new Paragraph(
					"(ii)	Details of LSP acting as recovery agent and authorized to approach the borrower").setBold()
					.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableOtherInfo
					.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getLenderName()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			document.add(tableOtherInfo);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.add(new Paragraph("Computation of APR").setBold().setFontSize(12).setMarginTop(60)
					.setTextAlignment(TextAlignment.CENTER));

			Table tableApr = new Table(12); // Adjust column count based on your needs
			tableApr.setWidth(UnitValue.createPercentValue(100));
			tableApr.addCell(new Cell(1, 1).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Parameters").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("Details").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("1.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("Sanctioned Loan amount (in Rupees) ").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph(String.valueOf(borrowerObj.getLoanAmount())).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 1).add(new Paragraph("2.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("Loan Term (in years/ months/ days)  ").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph(borrowerObj.getLoanDays().toString() + " days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 1).add(new Paragraph("a)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8).add(new Paragraph(
					"No. of instalments for payment of principal, in case of non- equated periodic loans ").setBold()
					.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 1).add(new Paragraph("b)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("Type of EPI\r\n" + "Amount of each EPI (in Rupees) and\r\n"
							+ "nos. of EPIs (e.g., no. of EMIs in case of monthly instalments)\r\n" + " ").setBold()
							.setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph("EMI\r\n" + "" + String.valueOf(borrowerObj.getLoanAmount()) + "\r\n" + ""
							+ borrowerObj.getEmiTime() + "\r\n" + "").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("c)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("No. of instalments for payment of capitalised interest, if any")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("d)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Commencement of repayments, post sanction ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("3.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Interest rate type (fixed or floating or hybrid) ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("Fixed").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("4.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Rate of Interest  ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiRate().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("5.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8).add(new Paragraph(
					"Total Interest Amount to be charged during the entire tenor of the loan as per the rate prevailing on sanction date (in Rupees)")
					.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph(borrowerObj.getTotalInterest().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			// -----------------------
			tableApr.addCell(new Cell(1, 1).add(new Paragraph("6.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("Fee/ Charges payable (in Rupees)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("0").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("A.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8).add(new Paragraph("Payable to the Lender").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("0").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("B.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Payable to third-party routed through Lender").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3).add(new Paragraph("0").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("7.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 8).add(new Paragraph("Net disbursed amount (in Rupees)").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph(borrowerObj.getSectionAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("8.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Total amount to be paid by the borrower (in Rupees)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 3)
					.add(new Paragraph(borrowerObj.getTotalLoanAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("9.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Annual Percentage rate- Effective annualized interest rate (in percentage)")
							.setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 3).add(new Paragraph(borrowerObj.getApr().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("10.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8).add(
					new Paragraph("Schedule of disbursement as per terms and conditions").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 3).add(new Paragraph("Mention in KFS").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));

			tableApr.addCell(new Cell(1, 1).add(new Paragraph("11.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(new Cell(1, 8)
					.add(new Paragraph("Due date of payment of instalment and interest").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tableApr.addCell(
					new Cell(1, 3).add(new Paragraph(borrowerObj.getEmiDate().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			// -----------------------

			document.add(tableApr);

			document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

			document.add(new Paragraph("REPAYMENT SCHEDULE *").setBold().setFontSize(12).setMarginTop(60));

			Table repayment = new Table(12); // Adjust column count based on your needs
			repayment.setWidth(UnitValue.createPercentValue(100));

			repayment.addCell(new Cell(1, 4).add(new Paragraph("Installment No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 4).add(new Paragraph("Due Date").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			repayment.addCell(new Cell(1, 4).add(new Paragraph("Installment (in Rupees)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			for (EmiBreakUp emiBreakUp : emiBreakUpList) {

				repayment.addCell(new Cell(1, 4)
						.add(new Paragraph(String.valueOf(emiBreakUp.getInstallmentNo())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 4)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueDate())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
				repayment.addCell(new Cell(1, 4)
						.add(new Paragraph(String.valueOf(emiBreakUp.getDueAmount())).setBold().setFontSize(11))
						.setTextAlignment(TextAlignment.LEFT));
			}

			document.add(repayment);

			document.add(new AreaBreak());
			// Page 2

			document.add(new Paragraph("SANCTION LETTER").setFontSize(18).setMarginTop(50).setBold()
					.setTextAlignment(TextAlignment.CENTER));

			Table table = new Table(2); // Two columns
			table.setWidth(UnitValue.createPercentValue(100)); // 100% width for the table

			// First column - Left-aligned text
			table.addCell(new Cell()
					.add(new Paragraph("Reference No : " + borrowerObj.getLoanAggrement()).setBold().setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
			// Second column - Right-aligned text
			table.addCell(new Cell().add(new Paragraph("Date: " + today).setFontSize(12))
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
			table.addCell(new Cell().add(new Paragraph("Borrower Name: " + borrowerObj.getFullName()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
			table.addCell(
					new Cell().add(new Paragraph("Address:" + borrowerObj.getSecondaryAddressLine1()).setFontSize(12))
							.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

			document.add(table);

			document.add(new Paragraph("Dear Sir/ Madam, "));
			document.add(new Paragraph("Please refer to your financial assistance application "
					+ borrowerObj.getLoanAggrement()
					+ " for a loan from Mufin Green Finance Limited (“Lender”) made through "
					+ borrowerObj.getLenderName()
					+ " (“LSP”) – a Digital Lending mobile application & Loan Service Provider of the Lender. We are happy to inform you that we have sanctioned a sum of Rs. "
					+ borrowerObj.getLoanAmount()
					+ "/- against your application. The following are the broad terms & conditions governing the financial assistance."));
			document.add(new Paragraph(
					"We look forward to disbursing this loan and request you to complete all the formalities in this regard."));
			document.add(new Paragraph("Annexure 1").setBold());

			Table tablesec = new Table(12); // Adjust column count based on your needs
			tablesec.setWidth(UnitValue.createPercentValue(100));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Borrower Name").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getFullName()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Nature of Facility").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Unsecured Personal Loan	").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Type of Facility").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph("Accrued salary advance facility").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Purpose/End Use").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getPurposeOfLoan()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Loan Tenor (in month/days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getLoanDays().toString() + " Days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Loan Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getLoanAmount().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Net Disbursed Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getSectionAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Total Repayment Amount ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph(borrowerObj.getTotalLoanAmount().toString()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Amount Breakup ").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6)
					.add(new Paragraph("Principal:" + borrowerObj.getLoanAmount().toString() + "\t" + "Interest:"
							+ borrowerObj.getTotalInterest().toString() + "\t" + "").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Type of Interest").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Fixed").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Rate of Interest").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiRate().toString()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Type").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("EMI").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Frequency").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Monthly").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Repayment Due Date").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getEmiDate()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Mode of Repayment").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Online payments").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Special Condition (if any)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("NA").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Security").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Unsecured").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("Cooling Off Period (days)").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph("3 days").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(
					new Cell(1, 6).add(new Paragraph("Digital Lending App (DLA)/Loan Service Provider (LSP) Details")
							.setBold().setFontSize(11)).setTextAlignment(TextAlignment.LEFT));
			tablesec.addCell(new Cell(1, 6).add(new Paragraph(borrowerObj.getLenderName()).setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.LEFT));

			document.add(tablesec);

			document.add(new AreaBreak());

			document.add(new Paragraph("Details of Fees & Charges").setBold().setMarginTop(60));

			Table tablesec1 = new Table(12); // Adjust column count based on your needs
			tablesec1.setWidth(UnitValue.createPercentValue(100));
			Map<String, String> chargeDetails = new LinkedHashMap<>();
			chargeDetails.put("Processing Fee (inc. GST) (₹)", product.getProcessingFee().toString());
			chargeDetails.put("Insurance Charges (inc. GST) (₹)", NA);
			chargeDetails.put("Overdue Interest", "3% per month for default period & delayed instalments");
			chargeDetails.put("Penal Charges", NA);
			chargeDetails.put("Bounce Charges (₹)", "500 per instance + GST");
			chargeDetails.put(PDC_CH_SWAP_CHARGES, NA);
			chargeDetails.put(FULL_PART_PREPAYMENT_CHARGES, NA);
			chargeDetails.put(PDC_CH_SWAP_CHARGES, NA);
			chargeDetails.put(DUPLICATE_STATEMENT_CHARGES, NA);
			chargeDetails.put(EMI_FOLLOW_UP_CHARGES, NA);
			chargeDetails.put(STAMP_DUTY_CHARGES, NA);
			chargeDetails.put(ANY_OTHER_CHARGES, NA);
			chargeDetails.forEach((label, value) -> {
				addCell(tablesec1, label, 11, TextAlignment.LEFT);
				addCell(tablesec1, value, 11, TextAlignment.LEFT);
			});
			document.add(tablesec1);

			document.add(new AreaBreak());

			Table tablesec2 = new Table(10).setMarginTop(60);
			tablesec2.setWidth(UnitValue.createPercentValue(100));
			tablesec2.addCell(new Cell(1, 6).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tablesec2.addCell(new Cell(1, 6).add(new Paragraph("Particulars").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			DataProvider.getForKFS1SanctionConditionsMap().forEach((serial, condition) -> {
				addCell(tablesec2, String.valueOf(serial), 11, TextAlignment.LEFT);
				addCell(tablesec2, condition, 11, TextAlignment.LEFT);
			});
			document.add(tablesec2);

			document.add(new AreaBreak());

			Table tablesec3 = new Table(10).setMarginTop(60);
			tablesec3.setWidth(UnitValue.createPercentValue(100));
			DataProvider.getFFS1SanctionConditionsMaps(XXX, borrowerObj.getLenderName())
					.forEach((serial, condition) -> {
						addCell(tablesec3, String.valueOf(serial), 11, TextAlignment.LEFT);
						addCell(tablesec3, condition, 11, TextAlignment.LEFT);
					});
			document.add(tablesec3);

			document.add(new Paragraph(CONDITION_LIST_END));
			document.add(new Paragraph("Borrower Name: " + borrowerObj.getFullName()).setMarginTop(20));
			document.add(new Paragraph("________________").setMarginTop(20));
			document.add(new Paragraph("Signature"));

			document.add(new AreaBreak());

			document.add(new Paragraph("PERSONAL LOAN AGREEMENT").setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(60));

			document.add(new Paragraph(PERSONAL_LOAN_AGG_KFS1_1));
			document.add(new Paragraph(
					"Mufin Green Finance Limited, a company incorporated under the provisions of Companies Act, 2013 bearing CIN: U34300DL1985PLC021785, and having its registered office at 201, 2nd Floor, Best Sky Tower Plot No. F-5, Netaji Subhash Place Delhi-110034 (hereinafter referred to as the “Lender”, which expression shall unless repugnant to the context or meaning there of be deemed to mean and include its successors and assigns) of the SECOND PART;"));
//			document.add(new Paragraph(personalLoanAgg3(nbfc.get().getNbfcName(), nbfc.get().getCinNumber())));
			document.add(new Paragraph(PERSONAL_LOAN_AGG_4));
			document.add(new Paragraph("WHEREAS").setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(10).setMarginBottom(10));
//			document.add(new Paragraph(PERSONAL_LOAN_AGG_5));
			document.add(new Paragraph(
					"A.	The Lender is a non-banking financial corporation registered with the RBI (defined hereinafter) under the Banking Regulation Act, 1949 and engaged in the business of providing various financial services/loans facility to other individuals, companies engaged in different kinds of finance and non-finance activities;"));

			document.add(new Paragraph("B.	The Borrower has approached the Lender through the "
					+ borrowerObj.getLenderBrandName()
					+ " Platform for grant of the Loan facility. Basis the information and/or documents submitted by the Borrower through its application for the grant of the Loan facility and in reliance of the acceptance of the terms & conditions of the KFS, the Lender has agreed to grant the Loan on the terms and conditions mentioned in this Agreement"));

			document.add(new AreaBreak());
			document.add(new Paragraph(NOW_THERE).setBold().setFontSize(13).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(40));

			document.add(new Paragraph("1.	DEFINITIONS ").setBold());

			document.add(
					new Paragraph("The terms and expressions contained in this Agreement are defined as under:  "));

//
			document.add(new Paragraph(defination_1_KFS1_point_1_and_3(borrowerObj.getFullName())));

			document.add(new Paragraph(defination_1_KFS1_point_4_and_5(borrowerObj.getLenderBrandName())));
			document.add(new AreaBreak());
			document.add(
					new Paragraph(defination_1_KFS1_point_14_and_5(borrowerObj.getLenderBrandName())).setMarginTop(60));
//			document.add(new Paragraph(defination_1_point_1_to_1_point_9(borrowerObj.getFullName())));
//			document.add(new Paragraph(defination_1_point_9_to_1_point_14(borrowerObj.getFullName())).setMarginTop(60));
//			document.add(new Paragraph(defination_1_point_15_to_1_point_21(borrowerObj.getFullName())));
//			document.add(new Paragraph(defination_1_point_22_to_1_point_29()).setMarginTop(60));

			document.add(new AreaBreak());

			document.add(new Paragraph(TERMS_OF_LOAN).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(new Paragraph(definationKFS1_1_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new AreaBreak());

			document.add(new Paragraph(RATE_OF_INTEREST).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(
					new Paragraph(definationKFS1_1_for_Rate_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new Paragraph(DEFAULT_INTERST).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));

			document.add(new Paragraph(
					definationKFS1_1_for_INSTER_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new AreaBreak());

			document.add(new Paragraph(REPAYMENT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(new Paragraph(
					definationKFS1_1_for_Repayment_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new AreaBreak());

			document.add(new Paragraph(APPROPRIATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(
					new Paragraph(definationKFS1_1_for_I666_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(
					new Paragraph(PREPAYMENT).setBold().setFontSize(16).setBold().setTextAlignment(TextAlignment.LEFT));

			document.add(
					new Paragraph(definationKFS1_1_for_I7777_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new AreaBreak());

			document.add(new Paragraph(REPRESENTATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_35(borrowerObj.getLenderBrandName())));

			document.add(new AreaBreak());

			document.add(
					new Paragraph(definationKFS1_1_for_I8882_point_30_to_1_point_35(borrowerObj.getLenderBrandName()))
							.setMarginTop(60));

			document.add(
					new Paragraph(COVENANTS).setBold().setFontSize(16).setBold().setTextAlignment(TextAlignment.LEFT));
			document.add(new Paragraph(COVENANTS_VAL));
			document.add(new AreaBreak());
			document.add(new Paragraph(COVENANTS_VAL_2).setMarginTop(60));

			document.add(new Paragraph(EVENT_OF_DEFAULT).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_10(borrowerObj.getLenderBrandName())));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_10_2(borrowerObj.getLenderBrandName()))
							.setMarginTop(60));

			document.add(new Paragraph(DISCLOSURESs).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_11(borrowerObj.getLenderBrandName())));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_11_2(borrowerObj.getLenderBrandName()))
							.setMarginTop(60));

			document.add(new Paragraph(MISCELLANEOUSss).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_12(borrowerObj.getLenderBrandName())));

			document.add(
					new Paragraph(definationKFS1_1_for_I888_point_30_to_1_point_12_2(borrowerObj.getLenderBrandName()))
							.setMarginTop(60));

			// TODO: GRIEVANCE_REPORTING_CHANNELS

			document.add(new AreaBreak());
			document.add(new Paragraph(
					grievanceHandlingProcedureValV1(borrowerObj.getLenderName(), borrowerObj.getLenderAuthorityEmail()))
					.setMarginTop(60));

			Table tableTimeFrame = new Table(12); // Adjust column count based on your needs
			tableTimeFrame.setWidth(UnitValue.createPercentValue(100));

			tableTimeFrame.addCell(new Cell(1, 6).add(new Paragraph("Action").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableTimeFrame.addCell(new Cell(1, 6).add(new Paragraph("Time Frame").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			Map<String, String> timeFrameMap = new LinkedHashMap<>();
			timeFrameMap.put("Acknowledgment", "24 Hours");
			timeFrameMap.put("Escalation to Level 2: " + borrowerObj.getLenderName() + " Grievance Ofﬁcer",
					"After 5 Working days");// TODO
			timeFrameMap.put("Escalation to Level 3: Mufin Green Finance Limited’s Grievance Ofﬁcer ",
					"After 6 Working days");
			timeFrameMap.put("Escalation to Level 4: RBI Sachet Portal", "After 30 Working days");
			timeFrameMap.forEach((label, value) -> {
				addCell(tableTimeFrame, label, 11, TextAlignment.LEFT);
				addCell(tableTimeFrame, value, 11, TextAlignment.LEFT);
			});
			document.add(tableTimeFrame);

			document.add(new AreaBreak());
			document.add(new Paragraph(SEVERABILITY).setTextAlignment(TextAlignment.LEFT).setMarginTop(60));

			document.add(new AreaBreak());
			document.add(new Paragraph(DECLARATION).setBold().setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.LEFT).setMarginTop(60));
			document.add(new Paragraph(DECLARATION_VAL));

			document.add(new Paragraph(IN_WRITTEN));

			Table tableWritten = new Table(12); // Adjust column count based on your needs
			tableTimeFrame.setWidth(UnitValue.createPercentValue(100));

			tableWritten.addCell(
					new Cell(1, 6).add(new Paragraph("For Mufin Green Finance Limited").setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 6).add(new Paragraph("For Borrower").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(new Cell(1, 6)
					.add(new Paragraph("Name: Tanvi Jawa \r\n Designation: CFO").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER));

			tableWritten.addCell(
					new Cell(1, 6).add(new Paragraph("Name: " + borrowerObj.getFullName()).setBold().setFontSize(11))
							.setTextAlignment(TextAlignment.CENTER));

			document.add(tableWritten);

			document.add(new Paragraph("Schedule I\r\n" + "Details of the Personal Loan – Salary Advance\r\n" + "")
					.setBold().setFontSize(12).setMarginTop(60).setTextAlignment(TextAlignment.CENTER));

			Table tableSchedule = new Table(12); // Adjust column count based on your needs
			tableSchedule.setWidth(UnitValue.createPercentValue(100));
			tableSchedule.addCell(new Cell(1, 2).add(new Paragraph("Sr. No.").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableSchedule.addCell(new Cell(1, 4).add(new Paragraph("Particulars").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			tableSchedule.addCell(new Cell(1, 6).add(new Paragraph("Details").setBold().setFontSize(11))
					.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));

			// Adding borrower details
			addCell(1, 2, tableSchedule, "1. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Borrower", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getFullName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "2. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "PAN", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerPan.getPanNumber(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "3. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Address of the Borrower", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getSecondaryAddressLine1() + " " + borrowerObj.getSecondaryArea(),
					11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "4. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Guarantor", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "5. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Address of the Guarantor", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "6. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Mobile no.", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getMobileNo(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "7. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Email ID", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getEmailId(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "8. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Lender", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule, "9. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule, "Name of the Employer", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			document.add(tableSchedule);

			Table tableSchedule1 = new Table(12).setMarginTop(60);
			tableSchedule1.setWidth(UnitValue.createPercentValue(100));

			addCell(1, 2, tableSchedule1, "10. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Address of the Employer", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, nbfc.get().getAddress(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "11. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Loan Application no.", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanAggrement(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "12. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Sanction Letter", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "13. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Loan Agreement", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "14. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Place of Execution", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Delhi", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "15. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Nature of Loan", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Personal Loan", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "16. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Purpose", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getPurposeOfLoan(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "17. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Sanctioned Loan Amount", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanAmount().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "18. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Rate of Interest (Interest)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getEmiRate().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "19. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Type of Interest", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Fixed", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "20. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Tenor of the facility", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLoanDays().toString() + " Days", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "21. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Repayment", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "EMI", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "22. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Primary Repayment Mode", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "Repayable in " + borrowerObj.getEmiTime()
					+ " monthly instalments (EMIs) EMI (Principal+ Interest) payable commencing one month from the date of disbursement.",
					11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "23. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Secondary Repayment Mode", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "24. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Security", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "25. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Date of Disbursement", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, today, 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "26. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Repayment date (Due Date)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getEmiDate(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "27. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Penal Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getPenalCharges().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "28. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Other Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getOtherCharges().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "29. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Processing fees (₹)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getProcessingFee().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "30. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Stamp duty", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, product.getStampDuty().toString(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "31. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "Foreclosure Charges", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, "NA", 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "32. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "LSP (Loan Service Provider)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			addCell(1, 2, tableSchedule1, "33. ", 11, TextAlignment.LEFT);
			addCell(1, 4, tableSchedule1, "DLA (Digital Lending App)", 11, TextAlignment.LEFT);
			addCell(1, 6, tableSchedule1, borrowerObj.getLenderName(), 11, TextAlignment.LEFT);

			document.add(tableSchedule1);

			document.add(new AreaBreak());
			document.add(new Paragraph(BANK_ACCOUNT_DETAILS_OF_BORROWER).setFontSize(12).setBold().setMarginTop(60));

			Table tableSecd = new Table(2); // Two columns
			tableSecd.setWidth(UnitValue.createPercentValue(100)); // 100% width for the table

			tableSecd.addCell(new Cell().add(new Paragraph("Bank's Account Holder Name ").setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerObj.getFullName()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(new Cell().add(new Paragraph("Account Number ").setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getBankAccountNo()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Account Type").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getAccountType()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Bank Name ").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getIfscCode()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("Branch").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(XXX).setFontSize(12)).setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("IFSC Code ").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(borrowerBank.getIfscCode()).setFontSize(12))
					.setTextAlignment(TextAlignment.LEFT));

			tableSecd.addCell(
					new Cell().add(new Paragraph("MICR").setFontSize(12)).setTextAlignment(TextAlignment.LEFT));
			tableSecd.addCell(new Cell().add(new Paragraph(XXX).setFontSize(12)).setTextAlignment(TextAlignment.LEFT));

			document.add(tableSecd);

			document.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF", e);
		}

	}

	private static class LetterheadEventHandler implements IEventHandler {
		private String organisationLogoPath;

		public LetterheadEventHandler(String organisationLogoPath) {
			this.organisationLogoPath = organisationLogoPath;
		}

		@Override
		public void handleEvent(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfDocument pdf = docEvent.getDocument();
			PdfPage page = docEvent.getPage();
			PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
			Rectangle pageSize = page.getPageSize();

			try {
				// Load Images (Left and Right logos)
				URL leftLogoUrl = new URL("https://mufingreenfinance.com/wp-content/uploads/2022/08/Mufin-Green-Logo-For-Website-01-01-01-1.png");
				URL rightLogoUrl = new URL(organisationLogoPath);

				// Set header height to accommodate logos, header line, and spacing
				float headerHeight = 0;

				if (leftLogoUrl != null && rightLogoUrl != null) {
					ImageData leftLogoData = ImageDataFactory.create(leftLogoUrl);
					ImageData rightLogoData = ImageDataFactory.create(rightLogoUrl);

					// Use Canvas to place logos
					Canvas headerCanvas = new Canvas(canvas, pageSize);

					// Add Left Logo
					Image leftLogo = new Image(leftLogoData).scaleAbsolute(100, 50);
					leftLogo.setFixedPosition(pageSize.getLeft() + 40, pageSize.getTop() - 70);
					headerCanvas.add(leftLogo);

					// Add Right Logo
					Image rightLogo = new Image(rightLogoData).scaleAbsolute(100, 50);
					rightLogo.setFixedPosition(pageSize.getRight() - 140, pageSize.getTop() - 70);
					headerCanvas.add(rightLogo);

					// Calculate the total header height (logos height + margin + space for header
					// line)
					headerHeight = 90; // Logo height (50) + top margin (70) + space between logos and line (10)

					// Ensure canvas is closed after adding logos
					headerCanvas.close();
				}

				// Add Header Line just below the logos
				canvas.setStrokeColor(ColorConstants.GRAY);
				canvas.moveTo(pageSize.getLeft() + 40, pageSize.getTop() - headerHeight);
				canvas.lineTo(pageSize.getRight() - 40, pageSize.getTop() - headerHeight);
				canvas.stroke();

				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Object> getDynamicData(String borrowerUid) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
		Borrower borrowerObj = borrower.get();
		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(borrowerUid);

		List<EmiBreakUp> emiBreakUpList = emiBreakUpRepository.findByBorrowerUid(borrowerUid);

		BorrowerBank borrowerBank = borrowerBankRepository.findByBorrowerUid(borrowerUid);

		BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrowerUid);

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(borrowerObj.getLenderUid());

		Product product = productService.getProductByProductIdAndUid(borrowerObj.getProductId(),
				borrowerObj.getLenderUid());
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Map<String, Object> data = new HashMap<>();

		data.put("borrower", borrowerObj);
		data.put("borrowerAadhaar", borrowerAadhaar);
		data.put("emiBreakUpList", emiBreakUpList);
		data.put("borrowerBank", borrowerBank);
		data.put("borrowerPan", borrowerPan);
		data.put("nbfc", nbfc);
		data.put("product", product);
		data.put("today", today);
		data.put("formatter", formatter);
		return data;
	}

}
