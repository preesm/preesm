/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.architecture;

/**
 * Architecture predefined Examples
 * 
 * @author mpelcat
 */
public class Examples {

	/**
	 * Generates an archi with 1 C64x
	 * 
	 */
	public static MultiCoreArchitecture get1C64Archi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("1C64Archi");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		archi.addOperator(new Operator("C64x_1", opdef), true);

		return archi;
	}

	/**
	 * Generates an archi with 2 C64x and an EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get2C64Archi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("2C64Archi");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		edma.setMediumProperty(new MediumProperty(1, 2, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));

		Medium m1 = new Medium("edma", edma, intfdef);
		archi.addMedium(m1, op1, op2, true);

		return archi;
	}

	/**
	 * Generates an archi with 2 clusters of (3 c64x linked by 1 edma) The
	 * clusters are linked with rapidIO
	 */
	public static MultiCoreArchitecture get2FaradayArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4C64_6edma");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		MediumDefinition rapidIO = new MediumDefinition("rapidIO");
		edma.setMediumProperty(new MediumProperty(1f, 100, 1));
		rapidIO.setMediumProperty(new MediumProperty(1, 200, 1));

		ArchitectureInterfaceDefinition edmaIntfDef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);
		ArchitectureInterfaceDefinition rapidIOIntfDef = new ArchitectureInterfaceDefinition(
				rapidIO, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("c64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(edmaIntfDef, op1));
		op1.addInterface(new ArchitectureInterface(rapidIOIntfDef, op1));
		Operator op2 = archi.addOperator(new Operator("c64x_3", opdef), false);
		op2.addInterface(new ArchitectureInterface(edmaIntfDef, op2));
		Operator op3 = archi.addOperator(new Operator("c64x_5", opdef), false);
		op3.addInterface(new ArchitectureInterface(edmaIntfDef, op3));

		Operator op4 = archi.addOperator(new Operator("c64x_2", opdef), true);
		op4.addInterface(new ArchitectureInterface(edmaIntfDef, op4));
		op4.addInterface(new ArchitectureInterface(rapidIOIntfDef, op4));
		Operator op5 = archi.addOperator(new Operator("c64x_4", opdef), false);
		op5.addInterface(new ArchitectureInterface(edmaIntfDef, op5));
		Operator op6 = archi.addOperator(new Operator("c64x_6", opdef), false);
		op6.addInterface(new ArchitectureInterface(edmaIntfDef, op6));

		Medium edma_1 = new Medium("edma_Faraday1", edma, edmaIntfDef);
		archi.addMedium(edma_1, op1, op2, true);
		archi.connect(edma_1, op3);
		Medium edma_2 = new Medium("edma_Faraday2", edma, edmaIntfDef);
		archi.addMedium(edma_2, op4, op5, false);
		archi.connect(edma_2, op6);

		Medium rapid = new Medium("rapidIO", rapidIO, rapidIOIntfDef);
		archi.addMedium(rapid, op1, op4, false);

		return archi;
	}

	/**
	 * Generates an archi with 3 C64x and an 3EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get3C64_3edmaArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("3C64_3edma");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		edma.setMediumProperty(new MediumProperty(1, 500, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("C64x_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));

		Medium m1 = new Medium("edma_1", edma, intfdef);
		archi.addMedium(m1, op1, op2, true);
		Medium m2 = new Medium("edma_2", edma, intfdef);
		archi.addMedium(m2, op1, op3, false);
		Medium m3 = new Medium("edma_3", edma, intfdef);
		archi.addMedium(m3, op2, op3, false);

		return archi;
	}

	/**
	 * Generates an archi with 3 C64x and an EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get3C64Archi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("3C64Archi");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		edma.setMediumProperty(new MediumProperty(1, 500, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("C64x_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));

		Medium m = new Medium("edma", edma, intfdef);
		archi.addMedium(m, op1, op2, true);

		archi.connect(m, op3);

		return archi;
	}

	/**
	 * Generates an archi with 4 C64x and an 6EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get4C64_6edmaArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4C64_6edma");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		edma.setMediumProperty(new MediumProperty(1, 500, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("C64x_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));
		Operator op4 = archi.addOperator(new Operator("C64x_4", opdef), false);
		op4.addInterface(new ArchitectureInterface(intfdef, op4));

		Medium m1 = new Medium("edma_1", edma, intfdef);
		archi.addMedium(m1, op1, op2, true);
		Medium m2 = new Medium("edma_2", edma, intfdef);
		archi.addMedium(m2, op1, op3, false);
		Medium m3 = new Medium("edma_3", edma, intfdef);
		archi.addMedium(m3, op1, op4, false);
		Medium m4 = new Medium("edma_4", edma, intfdef);
		archi.addMedium(m4, op2, op3, false);
		Medium m5 = new Medium("edma_5", edma, intfdef);
		archi.addMedium(m5, op2, op4, false);
		Medium m6 = new Medium("edma_6", edma, intfdef);
		archi.addMedium(m6, op3, op4, false);

		return archi;
	}

	/**
	 * Generates an archi with 4 C64x and an EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get4C64Archi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4C64Archi");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		edma.setMediumProperty(new MediumProperty(1, 100, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				edma, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("C64x_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));
		Operator op4 = archi.addOperator(new Operator("C64x_4", opdef), false);
		op4.addInterface(new ArchitectureInterface(intfdef, op4));

		Medium m = new Medium("edma", edma, intfdef);
		archi.addMedium(m, op1, op2, true);

		archi.connect(m, op3);
		archi.connect(m, op4);

		return archi;
	}

	/**
	 * Generates an archi with 2 C64x and a crossbar at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get2PArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("2PArchi");

		OperatorDefinition opdef = new OperatorDefinition("microblaze");
		MediumDefinition bus = new MediumDefinition("opb_bus");
		SwitchDefinition crossbar = new SwitchDefinition("opb_crossbar");
		bus.setMediumProperty(new MediumProperty(1, 100, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				bus, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("p_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("p_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));

		Switch sw1 = archi.addSwitch(new Switch("crossbar_1", crossbar));
		sw1.addInterface(new ArchitectureInterface(intfdef, sw1));

		Medium m1 = new Medium("bus_1", bus, intfdef);
		archi.addMedium(m1, op1, sw1, false);

		Medium m2 = new Medium("bus_2", bus, intfdef);
		archi.addMedium(m2, op2, sw1, false);

		return archi;
	}

	/**
	 * Generates an archi with 3 C64x and a crossbar at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get3PArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("3PArchi");

		OperatorDefinition opdef = new OperatorDefinition("microblaze");
		MediumDefinition bus = new MediumDefinition("opb_bus");
		SwitchDefinition crossbar = new SwitchDefinition("opb_crossbar");
		bus.setMediumProperty(new MediumProperty(1, 100, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				bus, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("p_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("p_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("p_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));

		Switch sw1 = archi.addSwitch(new Switch("crossbar_1", crossbar));
		sw1.addInterface(new ArchitectureInterface(intfdef, sw1));

		Medium m1 = new Medium("bus_1", bus, intfdef);
		archi.addMedium(m1, op1, sw1, false);

		Medium m2 = new Medium("bus_2", bus, intfdef);
		archi.addMedium(m2, op2, sw1, false);

		Medium m3 = new Medium("bus_3", bus, intfdef);
		archi.addMedium(m3, op3, sw1, false);

		return archi;
	}

	/**
	 * Generates an archi with 4 C64x and a crossbar at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get4PArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4PArchi");

		OperatorDefinition opdef = new OperatorDefinition("microblaze");
		MediumDefinition bus = new MediumDefinition("opb_bus");
		SwitchDefinition crossbar = new SwitchDefinition("opb_crossbar");
		bus.setMediumProperty(new MediumProperty(1, 100, 1));

		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(
				bus, ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("p_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("p_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("p_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));
		Operator op4 = archi.addOperator(new Operator("p_4", opdef), false);
		op4.addInterface(new ArchitectureInterface(intfdef, op4));

		Switch sw1 = archi.addSwitch(new Switch("crossbar_1", crossbar));
		sw1.addInterface(new ArchitectureInterface(intfdef, sw1));

		Medium m1 = new Medium("bus_1", bus, intfdef);
		archi.addMedium(m1, op1, sw1, false);

		Medium m2 = new Medium("bus_2", bus, intfdef);
		archi.addMedium(m2, op2, sw1, false);

		Medium m3 = new Medium("bus_3", bus, intfdef);
		archi.addMedium(m3, op3, sw1, false);

		Medium m4 = new Medium("bus_4", bus, intfdef);
		archi.addMedium(m4, op4, sw1, false);

		return archi;
	}
}
