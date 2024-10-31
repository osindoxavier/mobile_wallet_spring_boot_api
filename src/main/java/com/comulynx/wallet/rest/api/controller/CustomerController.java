package com.comulynx.wallet.rest.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.comulynx.wallet.rest.api.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.comulynx.wallet.rest.api.AppUtilities;
import com.comulynx.wallet.rest.api.model.Account;
import com.comulynx.wallet.rest.api.model.Customer;
import com.comulynx.wallet.rest.api.repository.AccountRepository;
import com.comulynx.wallet.rest.api.repository.CustomerRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private Gson gson = new Gson();

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PasswordUtil passwordUtil;


	@GetMapping("/")
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	/**
	 * Fix Customer Login functionality
	 * 
	 * Login
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<?> customerLogin(@RequestBody String request) {
		try {
			JsonObject response = new JsonObject();

			final JsonObject req = gson.fromJson(request, JsonObject.class);
			String customerId = req.get("customerId").getAsString();
			String customerPIN = req.get("pin").getAsString();

			// Find customer by customerId
			Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerId);

			// Check if customer exists and throw an error "Customer does not exist
			if (!optionalCustomer.isPresent()) {
				return new ResponseEntity<>("Customer does not exist", HttpStatus.NOT_FOUND);
			}

			//Gets customer if exists
			Customer customer = optionalCustomer.get();

			// Check if the provided PIN matches the customer's PIN
			// If password do not match throw an error "Invalid credentials"
			if (!customer.getPin().equals(customerPIN)) {
				return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
			}

			// Find customer by Account
			Optional<Account> optionalAccount = accountRepository.findAccountByCustomerId(customerId);

			if (!optionalAccount.isPresent()) {
				return new ResponseEntity<>("Account not fount", HttpStatus.NOT_FOUND);
			}

			//Gets Account if exists
			Account account = optionalAccount.get();



			// Successful login
			//Creating a Successful login JSON object
			//Customer Name, Customer ID, email and Customer Account
			response.addProperty("firstName", customer.getFirstName());
			response.addProperty("lastName", customer.getLastName());
			response.addProperty("customerId", customer.getCustomerId());
			response.addProperty("email", customer.getEmail());
			response.addProperty("customerAccount", account.getAccountNo());

			// Adds fetched customer details
			return ResponseEntity.ok().body(response.toString());

		} catch (Exception ex) {
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 *  Add required logic
	 *  
	 *  Create Customer
	 *  
	 * @param customer
	 * @return
	 */
	@PostMapping("/")
	public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
		try {
			String customerPIN = customer.getPin();
			String email = customer.getEmail();
			String customerId = customer.getCustomerId();

			//Hash Customer PIN
			String hashedPIN = passwordUtil.hashPassword(customerPIN);
			// Set the hashed PIN back to the customer object
			customer.setPin(hashedPIN);
			//Check if Customer with customerId exists
//			throw a Customer with [?] exists
			if (customerRepository.findByCustomerId(customerId).isPresent()) {
				return new ResponseEntity<>(String.format("Customer with customerId: %s exists.",customerId), HttpStatus.CONFLICT);
			}

			//Check if Customer with provided email
			//throw a Customer with [?] exists
			if (customerRepository.findByEmail(email).isPresent()) {
				return new ResponseEntity<>(String.format("Customer with email: %s exists.",customer.getEmail()), HttpStatus.CONFLICT);
			}

			String accountNo = generateAccountNo(customer.getCustomerId());
			Account account = new Account();
			account.setCustomerId(customer.getCustomerId());
			account.setAccountNo(accountNo);
			account.setBalance(0.0);
			accountRepository.save(account);

			return ResponseEntity.ok().body(customerRepository.save(customer));
		} catch (Exception ex) {
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 *  Add required functionality
	 *  
	 * generate a random but unique Account No (NB: Account No should be unique
	 * in your accounts table)
	 * 
	 */
	private String generateAccountNo(String customerId) {
		// Generate a unique account number using UUID
		// Prepend with ACC
		String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
		String accountNo = "ACC" + uniqueId;

		// Check if Account number is present in the database
		// If present generate another one
		while (accountRepository.findAccountByAccountNo(accountNo).isPresent()) {
			uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
			accountNo = "ACC" + uniqueId;
		}

		return accountNo;
	}
}
