package bgu.spl.mics.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;

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
    public static void main(String[] args) {
    	String jsonFile=args[0];
    	String customersOutput=args[1];
    	String booksOutput=args[2];
    	String receiptsOutput=args[3];
    	String moneyRegisterOutput=args[4];
    	
    	Gson gson=new Gson();
    	BufferedReader br=null;
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
				
				for(int i=0;i<sum;i++) {
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
					else {
						name="time";
						microService=new TimeService(name,speed,duration,count,selling+inventoryServicesNum+logistic+resourceServiceNum+customers.length);
						threads[i]=new Thread(microService);
					}
				}
				
				for(Thread thread: threads) {
					thread.start();
				}
				
				for(Thread thread: threads) {
					thread.join();
				}
				
				HashMap<Integer,Customer> customersMap=new HashMap<>();
				for(Customer customer: customers) {
					customersMap.put(customer.getId(), customer);
				}
				
				try {
					FileOutputStream fileOut=new FileOutputStream(customersOutput);
					ObjectOutputStream out=new ObjectOutputStream(fileOut);
					out.writeObject(customersMap);
					
					fileOut=new FileOutputStream(moneyRegisterOutput);
					out=new ObjectOutputStream(fileOut);
					out.writeObject(MoneyRegister.getInstance());
					
					out.close();
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				inventory.printInventoryToFile(booksOutput);
				
				MoneyRegister.getInstance().printOrderReceipts(receiptsOutput);
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
