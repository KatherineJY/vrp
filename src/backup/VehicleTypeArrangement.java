package backup;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Properties;
import model.Vehicle;


/**
 * @author jinyul
 *
 */
public class VehicleTypeArrangement {	

	public Map<CapacityType, Integer> vehicleTypes = new TreeMap<CapacityType,Integer>();
	
	public List<Vehicle> vehicles;

	public VehicleTypeArrangement() {
		if( Properties.TRAILER_NUM >= Properties.VEHICLE_NUM * Properties.TRAILER_LIMIT ) {
			vehicleTypes.put( CapacityType.FOUR , Properties.VEHICLE_NUM);
		}
		else {
			int vehicle_num = Properties.TRAILER_NUM / Properties.TRAILER_LIMIT;
			int left = Properties.TRAILER_NUM % Properties.TRAILER_LIMIT;
			switch( left ){
			case 0:
				vehicleTypes.put(CapacityType.FOUR, vehicle_num);
				break;
			case 1:
				vehicleTypes.put(CapacityType.FOUR, vehicle_num-2);
				vehicleTypes.put(CapacityType.THREE, 3);
				break;
			case 2:
				vehicleTypes.put(CapacityType.FOUR, vehicle_num-1);
				vehicleTypes.put(CapacityType.THREE, 2);
				break;
			default:
				vehicleTypes.put(CapacityType.FOUR, vehicle_num);
				vehicleTypes.put(CapacityType.THREE, 1);
				break;
			}
		}
	}
	
	public void generateVehicleList() {
		vehicles = new ArrayList<Vehicle>();
		if( vehicleTypes.isEmpty() )
			return;
		for( CapacityType type:vehicleTypes.keySet() ) {
			int num = vehicleTypes.get(type);
			for( int i=1;i<=num;i++ )
				vehicles.add(new Vehicle(type));
		}
	}
	
}
