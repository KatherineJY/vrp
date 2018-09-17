package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author jinyul the map of the factory Points
 */

public class FactoryMap {

	private int num;

	private Point material_depot, product_depot, park;

	private List<Point> assemble_starts;

	private List<Point> assemble_ends;

	private Assemble[] assembles;

	public List<Task> all_trans_tasks;

	public FactoryMap() {
		// generate the map by random
		// actually the factory route data is determined
		Random rand = new Random();

		material_depot = createNewPoint(PointType.MATERIAL_DEPOT);
		product_depot = createNewPoint(PointType.PRODUCT_DEPOT);
		park = createNewPoint(PointType.PARK);

		//num = rand.nextInt(Properties.MAXX_NUM) + 1;
		num = Properties.MAXX_NUM;
		assemble_starts = new ArrayList<Point>();
		assemble_ends = new ArrayList<Point>();
		for (int i = 1; i <= num; i++) {
			assemble_starts.add(createNewPoint(PointType.ASSEMBLY_START));
			assemble_ends.add(createNewPoint(PointType.ASSMBLY_END));
		}

		assembles = new Assemble[num + 2];
		assembles[0] = new Assemble(AssemblyType.MATERIAL_DEPOT, material_depot, material_depot);
		for (int i = 1; i <= num; i++)
			assembles[i] = new Assemble(AssemblyType.ASSEMBLY, assemble_starts.get(i - 1), assemble_ends.get(i - 1));
		assembles[num + 1] = new Assemble(AssemblyType.PRODUCT_DEPOT, product_depot, product_depot);
		generateAssembly(assembles[0], new ArrayList<Assemble>(Arrays.asList(assembles)));
		for(int i=1;i<=num;i++)
			if( assembles[i].nexts==null || assembles[i].nexts.size()==0 ) {
				assembles[0].nexts.add(assembles[i]);
				List<Assemble> tmp_available_assembles = new ArrayList<Assemble>(Arrays.asList(assembles));
				tmp_available_assembles.remove(assembles[0]);
				generateAssembly(assembles[i],tmp_available_assembles );
			}

		generateAvailableTasks();
	}

	private void generateAvailableTasks() {
		all_trans_tasks = new ArrayList<Task>();
		for (int i = 0; i <= num; i++)
			for (Assemble assemble : assembles[i].nexts)
				all_trans_tasks.add(new Task(assembles[i].assembly_end, assemble.assembly_start));
	}

	private void generateAssembly(Assemble cur, List<Assemble> available_assembles) {
		if(cur.assembly_type==AssemblyType.PRODUCT_DEPOT || !cur.nexts.isEmpty())
			return;
		Random rand = new Random();
		available_assembles.remove(cur);
		List<Assemble> tmp = new ArrayList<>(available_assembles);
		int available_num = available_assembles.size();
		int task_num = rand.nextInt(available_num) + 1;
		for (int j = 1; j <= task_num; j++) {
			int next_idx = rand.nextInt(available_num);
			Assemble assemble = tmp.get(next_idx);
			cur.nexts.add(assemble);
			tmp.remove(assemble);
			available_num -= 1;
		}
		for (int j = 1; j <= task_num; j++) 
			generateAssembly(cur.nexts.get(j-1), available_assembles);
	}

	private Point createNewPoint(PointType pointType) {
		Random rand = new Random();
		while (true) {
			int x = rand.nextInt(Properties.LONG) + 1;
			int y = rand.nextInt(Properties.WIDTH) + 1;
			if (isNewPoint(x, y))
				return new Point(x, y, pointType);
		}
	}

	private boolean isNewPoint(int x, int y) {
		if (material_depot != null && material_depot.equals(x, y))
			return false;
		if (product_depot != null && product_depot.equals(x, y))
			return false;
		if (park != null && park.equals(x, y))
			return false;

		if (assemble_starts != null) {
			for (Point p : assemble_starts)
				if (p.equals(x, y))
					return false;
		}
		if (assemble_ends != null) {
			for (Point p : assemble_ends)
				if (p.equals(x, y))
					return false;
		}
		return true;
	}

	public int getNum() {
		return num;
	}

	public Point getPark() {
		return park;
	}
	
	public void print() {
		System.out.println();
		System.out.println("============================================");
		System.out.println("========== the information of Map ==========");
		System.out.println("============================================");
		System.out.println();
		System.out.println("NUM = " + num * 2);
		System.out.println();
		System.out.println("Locations of material depot =\t" + material_depot.toString());
		System.out.println();
		System.out.println("Locations of product depot =\t" + product_depot.toString());
		System.out.println();
		System.out.println("Locations of park =\t" + park.toString());
		System.out.println();
		System.out.println("Locations of each assemble start points = ");
		for (int i = 0; i < num; i++) {
			System.out.println(i + "\t" + assemble_starts.get(i).toString());
		}
		System.out.println();
		System.out.println("Locations of each assemble end points = ");
		for (int i = 0; i < num; i++) {
			System.out.println(i + "\t" + assemble_ends.get(i).toString());
		}
		System.out.println();
		System.out.println("All Assemblies:");
		for (int i = 0; i <= num + 1; i++) {
			System.out.println(assembles[i].toString());
		}
		System.out.println();
		System.out.println();
		System.out.println("All Available Tasks:");
		for (Task task : all_trans_tasks) {
			System.out.println(task.from_point.toString() + " --> " + task.to_point.toString());
		}
		System.out.println();
		System.out.println("============================================");
		System.out.println("========== the information of Map ==========");
		System.out.println("============================================");
		System.out.println();
	}

}
