package com.acme.payments.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Payment {
	@Id
	@Column(name = "payment_id")
	@JsonProperty("payment_id")
	private UUID id;
	private Float amount;
	@JsonProperty("currency")
	private String currency;
	@JsonProperty("payer_country")
	private String country;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="payer_id")
	@RestResource(path = "payer", rel = "payer")
	@JsonProperty("payerId")
	private User payer;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="payee_id")
	@RestResource(path = "payee", rel = "payee")
	@JsonProperty("payeeId")
	private User payee;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="payment_method_id")
	@JsonProperty("paymentMethodId")
	private PaymentMethod paymentMethod;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="order_id")
	@JsonProperty("orderId")
	private PaymentOrder order;
	
	Payment() {}
	
	public Payment(String id, 
				   Float amount, 
				   String currency, 
				   String country,
				   String payerId, 
				   String payeeId, 
				   String paymentMethodId,
				   String orderId) {
		 this.id = UUID.fromString(id);
		 this.amount = amount;
		 this.currency = currency;
		 this.country = country;
		 this.payee = new User(payeeId);
		 this.payer = new User(payerId);
		 this.paymentMethod = new PaymentMethod(paymentMethodId);
		 this.order = new PaymentOrder(orderId);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(this.id);
	}

	@Override
	public String toString() {
	    return "Payment{" + "id=" + this.id + 
	    	   ", amount='" + this.amount + 
	    	   ", currency=" + this.currency + 
	    	   "payer=" + this.payer.getId() +
	    	   "payee="+ this.payee.getId() +
	    	   "paymentMethod=" + this.paymentMethod.getId() +"}";
	}

	@Override
	public boolean equals(Object o) {
    	if (this == o)
	        return true;
	    if (!(o instanceof Payment))
	        return false;
	    Payment payment = (Payment) o;
	        return Objects.equals(this.id, payment.id);
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public UUID getId( ) {
		return this.id;
	}
	
	public User getPayer() {
		return this.payer;
	}
	
	public User getPayee() {
		return this.payee;
	}

	public Float getAmount() {
		return this.amount;
	}
	
	public PaymentMethod getPaymentMethod() {
		return this.paymentMethod;
	}
}
