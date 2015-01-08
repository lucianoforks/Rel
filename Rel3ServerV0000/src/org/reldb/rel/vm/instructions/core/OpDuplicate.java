package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpDuplicate extends Instruction {
	public final void execute(Context context) {
		context.duplicate();
	}
}
