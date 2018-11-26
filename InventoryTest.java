package bgu.spl.mics.application.passiveObjects;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InventoryTest {
	
	private Inventory inventory;
	@Before
	public void setUp() throws Exception {
		inventory=Inventory.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
		assertEquals(false,this.inventory==null);
		assertEquals(true,this.inventory.equals(Inventory.getInstance()));
	}

	@Test
	public void testLoad() {
		BookInventoryInfo[] books1 = null;
		BookInventoryInfo[] books2=new BookInventoryInfo[2];
		BookInventoryInfo[] books3= {new BookInventoryInfo("Tiras Ham",50,100),
				new BookInventoryInfo("Petel Juice",40,80),
				new BookInventoryInfo("Yael's Home",30,50)};
		this.inventory.load(books1);
		int result=inventory.checkAvailabiltyAndGetPrice("Twilight");
		assertEquals(-1, result);
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,result);
		this.inventory.load(books2);
		result=inventory.checkAvailabiltyAndGetPrice("Twilight");
		assertEquals(-1,result);
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,result);
		this.inventory.load(books3);
		result=inventory.checkAvailabiltyAndGetPrice("Tiras Ham");
		assertEquals(100,result);
		result=inventory.checkAvailabiltyAndGetPrice("Petel Juice");
		assertEquals(80,result);
		result=inventory.checkAvailabiltyAndGetPrice("Yael's Home");
		assertEquals(50,result);
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,result);
	}

	@Test
	public void testTake() {
		BookInventoryInfo[] books= {new BookInventoryInfo("Tiras Ham",2,100)};
		this.inventory.load(books);
		OrderResult order=this.inventory.take("Tiras Ham");
		assertEquals(OrderResult.SUCCESSFULLY_TAKEN,order);
		order=this.inventory.take("Tiras Ham");
		assertEquals(OrderResult.SUCCESSFULLY_TAKEN,order);
		order=this.inventory.take("Tiras Ham");
		assertEquals(OrderResult.NOT_IN_STOCK,order);
	}

	@Test
	public void testCheckAvailabiltyAndGetPrice() {
		BookInventoryInfo[] books1 = null;
		BookInventoryInfo[] books2=new BookInventoryInfo[2];
		BookInventoryInfo[] books3= {new BookInventoryInfo("Tiras Ham",50,100),
				new BookInventoryInfo("Petel Juice",40,80),
				new BookInventoryInfo("Yael's Home",30,50)};
		this.inventory.load(books1);
		int result=inventory.checkAvailabiltyAndGetPrice("Twilight");
		assertEquals(-1, result);
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,"Catch 22");
		this.inventory.load(books2);
		result=inventory.checkAvailabiltyAndGetPrice("Twilight");
		assertEquals(-1, "Twilight");
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,"Catch 22");
		this.inventory.load(books3);
		result=inventory.checkAvailabiltyAndGetPrice("Tiras Ham");
		assertEquals(100, "Tiras Ham");
		result=inventory.checkAvailabiltyAndGetPrice("Petel Juice");
		assertEquals(80,"Petel Juice");
		result=inventory.checkAvailabiltyAndGetPrice("Yael's Home");
		assertEquals(50, "Yael's Home");
		result=inventory.checkAvailabiltyAndGetPrice("Catch 22");
		assertEquals(-1,"Catch 22");

	}

	@Test
	public void testPrintInventoryToFile() {
		String filename="testPrint";
		HashMap<String,Integer> map1=new HashMap<>();
		HashMap<String,Integer> map2=new HashMap<>();
		map2.put("Tiras Ham", 50);
		BookInventoryInfo[] books3= {new BookInventoryInfo("Tiras Ham",50,100)};
		inventory.load(books3);
		inventory.printInventoryToFile(filename);
		try
	      {
	         FileInputStream fis = new FileInputStream("hashmap.ser");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         map1 = (HashMap<String,Integer>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         return;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return;
	      }
		assertEquals(true,map1.equals(map2));
	}

}
