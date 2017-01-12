package fr.upmc.datacenter.software.admissioncontroller_old.tests.distributed;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.hardware.computer.stock.Stock;
import fr.upmc.datacenter.hardware.processor.model.Model;

public class StockCVM extends AbstractDistributedCVM {
	
	Stock stock;
	
	public StockCVM(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		stock = new Stock("stock", 10, 4, Model.I7_6700K);
		super.instantiateAndPublish();
	}
	
	public static void	main(String[] args)
	{
		try {
			StockCVM da = new StockCVM(args) ;
			da.deploy() ;
			System.out.println("starting...") ;
			da.start() ;
			Thread.sleep(30000L) ;
			System.out.println("shutting down...") ;
			da.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
}
