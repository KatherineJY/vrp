package model;

import java.util.ArrayList;
import java.util.List;

public class Assemble {
	public AssemblyType assembly_type;
	public Point assembly_start, assembly_end;
	public List<Assemble> nexts;
	
	public Assemble() {}
	
	public Assemble(AssemblyType assembly_type, Point start, Point end) {
		this.assembly_type = assembly_type;
		this.assembly_start = start;
		this.assembly_end = end;
		nexts = new ArrayList<Assemble>();
	}
	
	public String toString() {
		String next_assemblies = "";
		for( Assemble assemble:nexts )
			next_assemblies = next_assemblies + "["+assemble.assembly_start.toString()+" --> "+assemble.assembly_end.toString()+"]\t";
		if (next_assemblies=="") {
			next_assemblies = null;
		}
		return "assemble:\tassemblyType = " + assembly_type + "\tassembly_start = " + assembly_start.toString() + "\tassembly_end = " +
				assembly_end.toString() + "\t next_assemblies are " + next_assemblies;
	}
}
