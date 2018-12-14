package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	
	/**
	 * 
	 */
	private int id;
	private String name;
	private String address;
	private int distance;
	private CreditCard creditCard;
	private Order[] orderSchedule;
	private List<OrderReceipt> Receipts;;
	
	
	private class CreditCard implements Serializable{
		/**
		 * 
		 */
		private int number;
		private int amount;
		
	}
	
	public Customer(int id,String name,String address,int distance,CreditCard creditCard,Order[] orderSchedule){
		this.id=id;
		this.name=name;
		this.address=address;
		this.distance=distance;
		this.creditCard=creditCard;
		this.orderSchedule=orderSchedule;
		this.Receipts=new LinkedList<>();
}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return this.address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return this.distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return this.Receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return this.creditCard.amount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return this.creditCard.number;
	}
	
	public void addReceipt(OrderReceipt r){
		if(this.Receipts==null)
			this.Receipts=new LinkedList<>();
		this.Receipts.add(r);
	}
	
	public Order[] getOrderSchedule() {
		return this.orderSchedule;
	}
	
	public void setCreditAmount(int amount) {
		this.creditCard.amount=amount;
	}
	
}
