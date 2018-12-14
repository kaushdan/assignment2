package bgu.spl.mics.application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;


import com.google.gson.Gson;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.APIService;
import bgu.spl.mics.application.services.InventoryService;
import bgu.spl.mics.application.services.LogisticsService;
import bgu.spl.mics.application.services.ResourceService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.application.services.TimeService;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
	public static void print(Object obj,String fileName,String errorMessage) {
		try {
		FileOutputStream fileOut=new FileOutputStream(fileName);
		ObjectOutputStream out=new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
		}catch(Exception e) {System.out.println(errorMessage);}
	}
	
    public static void main(String[] args) {
    	boolean check=false;
    	String jsonFile=null;
    	String customersOutput=null;
    	String booksOutput=null;
    	String receiptsOutput=null;
    	String moneyRegisterOutput=null;
    	
    	if(args.length>4) {
	    	 jsonFile=args[0];
	    	 customersOutput=args[1];
	    	 booksOutput=args[2];
	    	 receiptsOutput=args[3];
	    	 moneyRegisterOutput=args[4];
	    	 check=true;
    	}
    	
    	Gson gson=new Gson();
    	BufferedReader br=null;
    	if(check) { 
	    	try {
				br=new BufferedReader(new FileReader(jsonFile));
				Input initialInput=gson.fromJson(br,Input.class);
				
				if(initialInput!=null) {
					
					AtomicInteger count=new AtomicInteger(0);
					 
					Inventory inventory=Inventory.getInstance();
					BookInventoryInfo[] books=initialInput.getInventory();
					inventory.load(books);
					
					ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
					resourcesHolder.load(initialInput.getVehicles());
					
					int selling=initialInput.getSelling();
					int inventoryServicesNum=initialInput.getInventoryService();
					int logistic=initialInput.getLogistics();
					int resourceServiceNum=initialInput.getResourceService();
					Customer[] customers=initialInput.getCustomers();
					int speed=initialInput.getSpeed();
					int duration=initialInput.getDuration();
					int sum=selling+inventoryServicesNum+logistic+resourceServiceNum+customers.length+1;
					MicroService microService;
					String name;
					Thread[] threads=new Thread[sum];
					
					for(int i=0;i<sum-1;i++) {
						if(i>=0 && i<selling) {
							name="selling " + (i+1);
							microService=new SellingService(name,count);
							threads[i]=new Thread(microService);
						}
						else if(i>=selling && i<selling+inventoryServicesNum) {
							name="inventory " + (i-selling+1);
							microService=new InventoryService(name,count);
							threads[i]=new Thread(microService);
						}
						else if(i>=selling+inventoryServicesNum && i<selling+inventoryServicesNum+logistic) {
							name="logistic "+ (i-(selling+inventoryServicesNum)+1);
							microService=new LogisticsService(name,count);
							threads[i]=new Thread(microService);
						}
						else if(i>=selling+inventoryServicesNum+logistic && i<selling+inventoryServicesNum+logistic+resourceServiceNum) {
							name="resource "+ (i-(selling+inventoryServicesNum+logistic)+1);
							microService=new ResourceService(name,count);
							threads[i]=new Thread(microService);
						}
						else if(i>=selling+inventoryServicesNum+logistic+resourceServiceNum && i<selling+inventoryServicesNum+logistic+resourceServiceNum+customers.length) {
							name="API " + (i-(selling+inventoryServicesNum+logistic+resourceServiceNum)+1);
							microService=new APIService(name,customers[i-(selling+inventoryServicesNum+logistic+resourceServiceNum)],count);
							threads[i]=new Thread(microService);
						}
						threads[i].start();
					}
					
					while(count.get() < sum-1) {}
					
					microService =new TimeService("time", speed, duration);
					threads[sum-1]= new Thread(microService);
					threads[sum-1].start();
					
					for(Thread thread: threads) {
						thread.join();
					}
					
					HashMap<Integer,Customer> customersMap=new HashMap<>();
					for(Customer customer: customers) {
						customersMap.put(customer.getId(), customer);
					}
					
					print(customersMap, customersOutput, "failed to print customers map");
					
					inventory.printInventoryToFile(booksOutput);
					
					MoneyRegister.getInstance().printOrderReceipts(receiptsOutput);
					
					print(MoneyRegister.getInstance(),moneyRegisterOutput, "failed to print money register");
					 
					 
			}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			} 
	    }
    }
}
