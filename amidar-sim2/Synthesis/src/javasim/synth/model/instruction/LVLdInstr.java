package javasim.synth.model.instruction;

import java.util.LinkedHashSet;

import javasim.synth.SynthData;
import javasim.synth.model.I;
import javasim.synth.model.datum.Datum;
import javasim.synth.model.datum.LRead64Datum;
import javasim.synth.model.datum.LReadDatum;
import javasim.synth.model.datum.LWriteDatum;

/**
 * Local variable load instruction.
 *
 * @author Michael Raitza
 * @version 15.04.2011
 */
public class LVLdInstr extends Instruction {

	public LVLdInstr(I instr, Integer pos) {
		super(instr, pos);
	}

	/**
	 * Evaluates this instruction and creates a new local read datum.
	 * @param data holds the synthesis context as a SynthData object
	 * @see javasim.synth.model.I#eval
	 */
	public void eval(SynthData data) {
		Integer v = (Integer)i().getByteCodeParameter(data) + data.getLVarOffset(addr());
		data.lvar_read(v);
		data.lv_read(v, this.addr());
		Datum d;
		if (i().wdata())
			d = new LRead64Datum(v, this);
		else
			d = new LReadDatum(v, this);
		
		LinkedHashSet<Datum> realPreds = vstack().getRealPredecessorsLV(d);
		Datum df = vstack().local_add(d);
		
		if( df != null && !df.value().equals(d.value()) ){ // this is the case when a method was inlined with a reference as parameter -> call by reference
			vstack().push(df);
			vstack().local_add(df, (Integer)d.value());
			super.eval(data);
			return;
		}
		
		
		
		if (df != null) {
			if(df.type().equals(Datum.Type.MERGER) || df.type().equals(Datum.Type.PIPE)){
				data.dg().add_node(d);
				data.dg().add_simple_sedge(df, d);
				
			} else {
				vstack().local_add(df,realPreds);				//this is basically folding - no need to store and load again. only possible if the previous access was not in another loop (code above)
				d = df;
			}
		} else{
			data.dg().add_node(d);
		}

		vstack().push(d);

		super.eval(data);
	}
}
