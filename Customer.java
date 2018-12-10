package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	/**
     * Retrieves the name of the customer.
     */
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> Receipts;
	private int creditCard;
	private int availableAmountInCreditCard;
	public String getName() {
		// TODO Implement this
		return this.name;
		return null;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		// TODO Implement this
		return this.id
		return 0;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		// TODO Implement this
		return this.adress;
		return null;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		// TODO Implement this
		return this.distance;
		return 0;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		// TODO Implement this
		return this.Receipts;
		return null;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		// TODO Implement this
		return this.availableAmountInCreditCard
		return 0;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		// TODO Implement this
		return this.credit card
		return 0;
	}
	
	/***
	* Set the Amount in credit card	
	*/
	public void setAmountInCreditCard(int amount){
		this.availableAmountinCreditCard=amount;
	}
}
