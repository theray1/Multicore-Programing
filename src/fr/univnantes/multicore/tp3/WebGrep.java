package fr.univnantes.multicore.tp3;

import javax.tools.Tool;
import java.beans.Expression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

public class WebGrep {

	//private final static LinkedList<String> explored = new LinkedList<String>();

	private final static ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<>();

	private final static ConcurrentLinkedQueue<ParsedPage> toRead = new ConcurrentLinkedQueue<ParsedPage>();

	private final static ExecutorService threadPool = Executors.newFixedThreadPool(1000);

	/*
	 *  TODO : the search must be parallelized between the given number of threads
	 */
	private static void explore(String address) {
		if(explored.add(address))
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					//System.out.println(address + " was added to the explored list");
					ParsedPage page = Tools.parsePage(address);
					if(!page.matches().isEmpty()){
							toRead.add(page);
							//System.out.println(address + " was added to the to read list");
							for(String href : page.hrefs()) explore(href);
					}

				}catch (FileNotFoundException e){
					//explore(address);
					//e.printStackTrace();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}


	public static void main(String[] args) throws InterruptedException, IOException {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cetO --threads=1000 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		// TODO Just do it!
		System.err.println("You must search parallelize the application between " + Tools.numberThreads() + " threads!\n");

		//Consumer Thread for printing pages
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(!toRead.isEmpty()){
						Tools.print(toRead.poll());
						//toRead.poll();
						//System.out.println("size of toRead: " + toRead.size());
					}
				}
			}
		});

		// Get the starting URL given in argument
		for(String address : Tools.startingURL()) 
			explore(address);

		if(threadPool.awaitTermination(10, TimeUnit.SECONDS)){
			System.out.println("Termination occured");
		}else {
			System.out.println("Waited 10 seconds for threads to complete execution; didn't happen");
		}

		System.out.println("explored " + explored.size() + " pages");
	}
}
