package com.acme.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;

import com.acme.payments.exception.PaymentNotFoundException;
import com.acme.payments.model.Payment;
import com.acme.payments.repository.PaymentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CreatePaymentIntegrationTest {
	
	public static final String PAYMENT_ID = "12274a47-b6c6-41bf-81af-116416653306";
	public static final Float AMOUNT = 70.5f;
	public static final String USD = "USD";
	public static final String US = "US";
	public static final String PAYER_ID = "e8af92bd-1910-421e-8de0-cb3dcf9bf44d";
	public static final String PAYEE_ID = "4c3e304e-ce79-4f53-bb26-4e198e6c780a";
	public static final String PAYMENT_METHOD_ID = "8e28af1b-a3a0-43a9-96cc-57d66dd68294";
	public static final String ORDER_ID = "c1c3ed5e-f500-444c-9207-5a0d532e9fe9";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Test
	void createPaymentWorksThroughAllLayers() throws Exception {
		JSONObject payload = new JSONObject();
		payload.put("payment_id", PAYMENT_ID);
		payload.put("amount", AMOUNT);
		payload.put("currency", USD);
		payload.put("payerId", PAYER_ID);
		payload.put("payeeId", PAYEE_ID);
		payload.put("orderId", ORDER_ID);
		payload.put("paymentMethodId", PAYMENT_METHOD_ID);
		payload.put("payer_country", US);
		
		mockMvc.perform(post("/payments")
				.contentType("application/json")
				.content(payload.toString()))
				.andExpect(status().isCreated());
		
		Payment paymentEntity = paymentRepository.findById(UUID.fromString(PAYMENT_ID))
				.orElseThrow(() -> new PaymentNotFoundException(UUID.fromString(PAYMENT_ID)));
			
		assertThat(paymentEntity.getPaymentMethod().getId().toString()).isEqualTo(PAYMENT_METHOD_ID);
	}
}
