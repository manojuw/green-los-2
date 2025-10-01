package com.mixo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mixo.dto.EsignResponseDto;
import com.mixo.dto.UserDTO;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerDoc;
import com.mixo.model.BorrowerNach;
import com.mixo.model.Role;
import com.mixo.repository.BorrowerDocRepository;
import com.mixo.repository.BorrowerNachRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.repository.CustomerRepository;
import com.mixo.service.RoleService;
import com.mixo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BorrowerRepository borrowerRepository;

	@Value("${esignReturnurl}")
	private String esignReturnurl;

	@Value("${esignEnv}")
	private String esignEnv;

	@Value("${enachReturnurl}")
	private String enachReturnurl;

	@Value("${appid}")
	private String appid;
	@Value("${secrettoken}")
	private String secrettoken;
	@Value("${usertoken}")
	private String usertoken;
	@Value("${customerCreationurl}")
	private String customerCreationurl;

	@Value("${getEmiBreakUp}")
	private String getEmiBreakUp;

	@Value("${loanCreationUrl}")
	private String loanCreationUrl;

	@Value("${loanCreationTokenUrl}")
	private String loanCreationTokenUrl;

	@Value("${xapikey}")
	private String xapikey;
	@Value("${generationTokenUrl}")
	private String generationTokenUrl;

	@Value("${docUploadUrl}")
	private String docUploadUrl;

	@Value("${digio_url}")
	private String digio_url;

	@Value("${digio_token}")
	private String digio_token;

	@Value("${legal_download}")
	private String legal_download;

	@Value("${legal_token}")
	private String legal_token;

	@Autowired
	private BorrowerDocRepository borrowerDocRepository;

	@Autowired
	BorrowerNachRepository borrowerNachRepository;

	@RequestMapping("/user")
	public String user(Model model) {
		model.addAttribute("user", new UserDTO());
		List<Role> roles = roleService.findAllPermissions();

		model.addAttribute("roles", roles);
		return "user";
	}

	@RequestMapping("/esignRequest")
	public String eisgnRequest(Model model, @RequestParam("session") String session) {
		String id = session;

		BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(id);
		if (borrowerDoc == null) {
			model.addAttribute("message", "Invalid Request");
			return "esignRequest";

		}
		if (borrowerDoc.isESignStatus()) {
			model.addAttribute("message", "Already Esigned");
			return "esignRequest";
		}

		model.addAttribute("id", esignReturnurl);
		model.addAttribute("env", esignEnv);
		model.addAttribute("user", borrowerDoc);

		return "esignRequest";
	}
	
	@RequestMapping("/esignRequestV2")
	public String eisgnRequestV2(Model model, @RequestParam("session") String session) {
		String id = session;

		BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(id);
		if (borrowerDoc == null) {
			model.addAttribute("message", "Invalid Request");
			return "esignRequest";

		}
		if (borrowerDoc.isESignStatus()) {
			model.addAttribute("message", "Already Esigned");
			return "esignRequest";
		}

		model.addAttribute("webhooks", esignReturnurl);
		model.addAttribute("env", esignEnv);
		model.addAttribute("user", borrowerDoc);

		return "esignRequest";
	}

	@RequestMapping("/enachRequest")
	public String enachRequest(Model model, @RequestParam("session") String session) {
		String id = session;

		BorrowerNach borrowerNach = borrowerNachRepository.findBySubscriptionId(id);
		if (borrowerNach == null) {
			model.addAttribute("message", "Invalid Request");
			return "enachRequest";

		}
		if (borrowerNach.isNachStatus()) {
			model.addAttribute("message", "Already Confirmed");
			return "enachRequest";
		}

		model.addAttribute("id", enachReturnurl);
		model.addAttribute("user", borrowerNach);

		return "enachRequest";
	}

	@RequestMapping("/nachCallback")
	public String nachCallback(Model model, @RequestParam("session") String session,
			@RequestParam Map<String, String> params) {
		String id = session;

		BorrowerNach borrowerNach = borrowerNachRepository.findBySubscriptionId(id);

		if (borrowerNach == null) {
			model.addAttribute("message", "Invalid Request");
			return "enachRequest";

		}
		String status = params.get("cf_status");
		if ("BANK_APPROVAL_PENDING".equals(status) || "SUCCESS".equals(status)) {
			borrowerNach.setNachStatus(true);
			borrowerNach.setSubscriptionStatus(status);
			borrowerNachRepository.save(borrowerNach);

			Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerNach.getBorrowerUid());

			if (borrower.isPresent()) {
				borrower.get().setENachStatus(true);
				borrowerRepository.save(borrower.get());
			}
		}

		if (borrowerNach.isNachStatus()) {
			model.addAttribute("message", "Nach Successfully Confirmed");
			model.addAttribute("esign", borrowerNach);
			return "enachRequest";
		}
		model.addAttribute("esign", borrowerNach);
		model.addAttribute("message", "Nach Failed");

		return "enachRequest";
	}

	@PostMapping("/esignCall")
	public String esignCall(
	        @RequestParam("signingUrl") String signingUrl,
	        @RequestParam("status") String status,
	        @RequestParam("message") String message,
	        Model model) {

	    // 🔑 Find borrowerDoc using accessToken/signingUrl
	    BorrowerDoc borrowerDoc = borrowerDocRepository.findByAccessToken(signingUrl);
	    borrowerDoc.setESignStatus(false);

	    if ("SUCCESS".equalsIgnoreCase(status)) {
	        borrowerDoc.setESignStatus(true);

	        Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerDoc.getBorrowerUid());
	        if (borrower.isPresent()) {
	            Borrower borrowerObj = borrower.get();

	            borrowerObj.setEsignStatus(true);
	            borrowerRepository.save(borrowerObj);

	            try {
	                // Construct API URL for downloading document
	                String url = legal_download + signingUrl;

	                HttpClient client = HttpClient.newHttpClient();

	                HttpRequest request = HttpRequest.newBuilder()
	                        .uri(URI.create(url))
	                        .header("accept", "application/json")
	                        .header("content-type", "application/json")
	                        .header("X-Auth-Token", legal_token)
	                        .GET()
	                        .build();

	                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

	                String base64Pdf = Base64.getEncoder().encodeToString(response.body());

	                JSONObject documentUpload = createJsonForDocx(borrowerObj.getFinanceId(), base64Pdf, "kfs");
	                String respom = callApiForDocUploadCreation(documentUpload);
	                callApiForDocUploadWithHmac(respom, documentUpload);

	            } catch (IOException | InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    borrowerDocRepository.save(borrowerDoc);

	    // ✅ Pass message & borrowerDoc to Thymeleaf page
	    model.addAttribute("message", message);
	    model.addAttribute("esign", borrowerDoc);

	    // ✅ You could also directly redirect:
	    // return "redirect:" + borrowerDoc.getESignRedirectUrl();

	    return "esignRequest";
	}


	@RequestMapping("/eCallback")
	public String handleDigioResponse(@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "digio_doc_id", required = false) String digioDocId,
			@RequestParam(name = "message", required = false) String message, Model model) {

		// Log the response for debugging

		BorrowerDoc borrowerDoc = borrowerDocRepository.findByAccessToken(digioDocId);
		borrowerDoc.setESignStatus(false);
		if (status.equalsIgnoreCase("success")) {
			borrowerDoc.setESignStatus(true);

			Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerDoc.getBorrowerUid());

			if (borrower.isPresent()) {

				Borrower borrowerObj = borrower.get();

				if (borrower.isPresent()) {
					borrower.get().setEsignStatus(true);
					borrowerRepository.save(borrower.get());
				}
				try {

					// Define the API URL
					String url = legal_download + digioDocId;

					// Create HttpClient instance
					HttpClient client = HttpClient.newHttpClient();

					// Create HttpRequest with Authorization header
					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
							.header("accept", "application/json").header("content-type", "application/json")
							.header("X-Auth-Token", legal_token).GET().build();

					// Send request and get response as byte array
					HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

					// Convert PDF bytes to Base64
					String base64Pdf = Base64.getEncoder().encodeToString(response.body());

					// Print Base64 output
					JSONObject documentUpload = createJsonForDocx(borrowerObj.getFinanceId(), base64Pdf, "kfs");
					String respom = callApiForDocUploadCreation(documentUpload);
					callApiForDocUploadWithHmac(respom, documentUpload);

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		borrowerDocRepository.save(borrowerDoc);

		model.addAttribute("message", message);

		model.addAttribute("esign", borrowerDoc);
		return "esignRequest";
	}

	private void callApiForDocUploadWithHmac(String respom, JSONObject documentUpload) {
		try {

			URL url = new URL(docUploadUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", respom.replaceAll("\"", ""));

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(documentUpload.toString().trim());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud DocUpload  Api Response= " + response);

				JSONObject obj = new JSONObject(response);
				log.info("All Cloud DocUpload  Api Response= " + obj);

//				System.out.println("All Cloud DocUpload  Api Response= " + responseStream.);
//				System.out.println("All Cloud DocUpload  Api Response= " + response);
			}
		} catch (Exception e) {
		}

	}

	private String callApiForDocUploadCreation(JSONObject documentUpload) {
		try {

//			log.info("callApiForDocUploadCreation : " + documentUpload.toString());

			URL url = new URL(generationTokenUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("appid", appid);
			httpConn.setRequestProperty("secrettoken", secrettoken);
			httpConn.setRequestProperty("usertoken", usertoken);
			httpConn.setRequestProperty("url", docUploadUrl);
			httpConn.setRequestProperty("x-api-key", xapikey);
			httpConn.setRequestProperty("Content-Type", "application/json");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(documentUpload.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			String response = s.hasNext() ? s.next() : "";
			return response;
		} catch (Exception e) {
		}
		return "";

	}

	private JSONObject createJsonForDocx(int financeId, String base64Pdf, String string) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("EntityId", financeId);
		jsonObject.put("EntityTypeId", "Finance");
		JSONObject UploadDocumentDTO = new JSONObject();
		UploadDocumentDTO.put("DocType", "KeyFactSheet");
		UploadDocumentDTO.put("Order", 1);
		UploadDocumentDTO.put("FileName", "SIGNED_kfs" + LocalDate.now() + ".pdf");
		UploadDocumentDTO.put("UploadDocBase64", base64Pdf);
		jsonObject.put("UploadDocumentDTO", UploadDocumentDTO);
		return jsonObject;
	}

	@PostMapping("/addUser")
	public String addUser(@ModelAttribute("user") UserDTO userDTO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("user", userDTO);
			model.addAttribute("message", result.getAllErrors());
			List<Role> roles = roleService.findAllPermissions();

			model.addAttribute("roles", roles);
			return "user";
		}

		List<Role> roles = roleService.findAllPermissions();

		model.addAttribute("roles", roles);
		String message = userService.saveUser(userDTO); // Save user through the service layer
		model.addAttribute("message", message);
		return "user"; // Redirect to the user list page after successful save
	}

	@RequestMapping("/userList")
	public String userList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();

		model.addAttribute("userList", userService.getAllUsers(userName));
		return "userList";
	}

}
