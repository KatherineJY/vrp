package test;

import java.util.ArrayList;

import model.Arrangment;
import model.FactoryMap;
import model.Task;
import model.Vehicle;
import service.TaskGenerator;
import strategy.StrategyB;

public class Test {
	final static int CASE_NUM = 100;

	public static void main(String[] args) { 

		//tasks density
		/*
		for (int case_index = 1; case_index <= CASE_NUM; case_index++) {
			FactoryMap map = new FactoryMap();
			ArrayList<Task> static_tasks = TaskGenerator.generateStaticTasks(map);
			System.out.println(static_tasks.size());
		}
		*/
		
		double percent = 0.0;
		long time = 0;
		long max_time = 0;
		long exceed_time = 0;
		long finished_time = 0;

		for (int case_index = 1; case_index <= CASE_NUM; case_index++) {
			
			//System.out.println("Cases " + case_index + ":");
			FactoryMap map = new FactoryMap();
			//map.print();
//			generate Static tasks
			ArrayList<Task> static_tasks = TaskGenerator.generateStaticTasks(map);
			//TaskGenerator.print_static_task(static_tasks);
			//System.out.println("after_spilt_tasks:");
            static_tasks = TaskGenerator.spiltTask(static_tasks);
            //TaskGenerator.print_static_task(static_tasks);
            
            System.out.println(map.getNum()+"  "+static_tasks.size());
            long start_time=System.currentTimeMillis(); 
           // Arrangment arrangment = StrategyA.arrangeVehicle(static_tasks,map);
            Arrangment arrangment = StrategyB.arrangeVehicle(static_tasks, map);
            long end_time=System.currentTimeMillis(); 
            //Arrangment.print(arrangment);
//          System.out.println(arrangment.exceed_time_tasks*1.0/static_tasks.size());
            
            time += end_time-start_time;
            max_time = Math.max(max_time, end_time-start_time);
            percent += arrangment.exceed_time_tasks*1.0/static_tasks.size();
            exceed_time += arrangment.exceed_time_sum;
            
           ///System.out.println(time);
            //System.out.println(percent);
            int q=0;
            for(Vehicle veh:arrangment.vehicles) {
            	int n = veh.getRoute().size();
            	q +=veh.getRoute().get(n-2).end_time;
            	//System.out.println(veh.getRoute().get(n-2).end_time+"   "+veh.finished_time);
            	//System.out.println(veh.finished_time);
            }
           finished_time += q*1.0/arrangment.vehicles.size(); 	
            	//System.out.println((veh.finished_time-60)*1.0/(23*60));
            System.out.println();
            /*
            
            //dynamic test
            long max_using_time = 0;
            long average_using_time = 0;
            ArrayList<Task> dynamic_tasks = TaskGenerator.generateDynamicTasks(map);
            for( Task task:dynamic_tasks ) {
            	System.out.println(task.toString());
            	long dynamic_t1 = System.currentTimeMillis(); 
            	ArrayList<Task> spilt_tasks = TaskGenerator.spiltTask(task);
            	for( Task after_spilt:spilt_tasks )
            		arrangment = StrategyB.dynamicArrange(arrangment,after_spilt);
            	long dynamic_t2 = System.currentTimeMillis(); 
            	long use_time = dynamic_t2 - dynamic_t1;
            	max_using_time = max_using_time>use_time?max_using_time:use_time;
            	average_using_time += use_time;
            	Arrangment.print(arrangment);
            	System.out.println();
            }
            System.out.println("max using time = \t"+max_using_time);
            System.out.println("average using time = \t"+average_using_time/dynamic_tasks.size());
            */
		}
		System.out.println(percent*1.0/CASE_NUM);
		System.out.println(finished_time*1.0/CASE_NUM);
		
		//System.out.println("percent of time-exceeding tasks = \t"+percent/CASE_NUM);
		//System.out.println("average running time = \t"+time/CASE_NUM);
		//System.out.println("max running time = \t"+max_time);
		//System.out.println("average exceed time = \t"+exceed_time);
		
	}

}
