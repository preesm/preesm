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

import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;

/**
 * Architecture predefined Examples
 * 
 * @author mpelcat
 */
public class Examples {

	/**
	 * Generates an archi with 2 C64x and an EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get2C64Archi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("2C64Archi");

		BusReference edmaBusRef = new BusReference("edmaBus");

		Operator op1 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"C64x","C64x_1");
		ArchitectureInterface intf1 = new ArchitectureInterface(edmaBusRef, op1);
		op1.addInterface(intf1);
		
		Operator op2 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"C64x","C64x_2");
		ArchitectureInterface intf2 = new ArchitectureInterface(edmaBusRef, op2);
		op2.addInterface(intf2);

		Medium edma = (Medium) archi.addComponent(ArchitectureComponentType.medium,"edma","edma_1");
		((MediumDefinition)edma.getDefinition()).setParams(1, 2, 1);
		ArchitectureInterface intfEdma = new ArchitectureInterface(edmaBusRef, edma);
		
		archi.connect(edma,intfEdma, op1,intf1);
		archi.connect(edma,intfEdma, op2,intf2);

		return archi;
	}

	/**
	 * Generates an archi with 2 clusters of (3 c64x linked by 1 edma) The
	 * clusters are linked with rapidIO
	 */
	/*public static MultiCoreArchitecture get2FaradayArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4C64_6edma");

		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		BusReference edmaBusRef = new BusReference("edmaBus");
		MediumDefinition rapidIO = new MediumDefinition("rapidIO");
		BusReference rapidIOBusDef = new BusReference("rapidIOBus");
		edma.setMediumProperty(new MediumProperty(1f, 100, 1));
		rapidIO.setMediumProperty(new MediumProperty(1, 200, 1));

		Operator op1 = (Operator)archi.addComponent(opdef,"C64x_1");
		op1.addInterface(new ArchitectureInterface(edmaBusRef, op1));
		op1.addInterface(new ArchitectureInterface(rapidIOIntfDef, op1));
		Operator op3 = (Operator)archi.addComponent(opdef,"C64x_3");
		op2.addInterface(new ArchitectureInterface(edmaBusRef, op2));
		Operator op5 = (Operator)archi.addComponent(opdef,"C64x_5");
		op3.addInterface(new ArchitectureInterface(edmaBusRef, op3));

		Operator op2 = (Operator)archi.addComponent(opdef,"C64x_2");
		op4.addInterface(new ArchitectureInterface(edmaBusRef, op4));
		op4.addInterface(new ArchitectureInterface(rapidIOIntfDef, op4));
		Operator op4 = (Operator)archi.addComponent(opdef,"C64x_4");
		op5.addInterface(new ArchitectureInterface(edmaBusRef, op5));
		Operator op6 = (Operator)archi.addComponent(opdef,"C64x_6");
		op6.addInterface(new ArchitectureInterface(edmaBusRef, op6));

		Medium edma_1 = (Medium) archi.addComponent(edma,"edma_Faraday1");
		archi.connect(edma_1, op3);
		Medium edma_2 = (Medium) archi.addComponent(edma,"edma_Faraday2");
		archi.connect(edma_2, op6);

		Medium rapid = new Medium("rapidIO", rapidIO, rapidIOIntfDef);
		archi.addComponent(ArchitectureComponentType.medium,rapid, op1, op4, false);

		return archi;
	}*/

	/**
	 * Generates an archi with 4 C64x and a crossbar at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get4PArchi() {
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4PArchi");

		MediumDefinition bus = new MediumDefinition("opb_bus");
		BusReference busRef = new BusReference("opb_busDef");
		SwitchDefinition crossbar = new SwitchDefinition("opb_crossbar");
		bus.setParams(1, 100, 1);

		Operator op1 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"microblaze","p_1");
		ArchitectureInterface intf1 = op1.addInterface(new ArchitectureInterface(busRef, op1));
		Operator op2 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"microblaze","p_2");
		ArchitectureInterface intf2 = op2.addInterface(new ArchitectureInterface(busRef, op2));
		Operator op3 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"microblaze","p_3");
		ArchitectureInterface intf3 = op3.addInterface(new ArchitectureInterface(busRef, op3));
		Operator op4 = (Operator)archi.addComponent(ArchitectureComponentType.operator,"microblaze","p_4");
		ArchitectureInterface intf4 = op4.addInterface(new ArchitectureInterface(busRef, op4));

		Switch sw1 = archi.addSwitch(new Switch("crossbar_1", crossbar));
		ArchitectureInterface intfsw = sw1.addInterface(new ArchitectureInterface(busRef, sw1));

		Medium m1 = (Medium) archi.addComponent(ArchitectureComponentType.medium,"opb_bus","bus_1");
		((MediumDefinition)m1.getDefinition()).setParams(1, 100, 1);
		ArchitectureInterface intfm1 = new ArchitectureInterface(busRef, m1);

		Medium m2 = (Medium) archi.addComponent(ArchitectureComponentType.medium,"opb_bus","bus_2");
		ArchitectureInterface intfm2 = new ArchitectureInterface(busRef, m2);

		Medium m3 = (Medium) archi.addComponent(ArchitectureComponentType.medium,"opb_bus","bus_3");
		ArchitectureInterface intfm3 = new ArchitectureInterface(busRef, m3);

		Medium m4 = (Medium) archi.addComponent(ArchitectureComponentType.medium,"opb_bus","bus_4");
		ArchitectureInterface intfm4 = new ArchitectureInterface(busRef, m4);
		
		archi.connect(op1, intf1, m1, intfm1);
		archi.connect(sw1, intfsw, m1, intfm1);

		archi.connect(op2, intf2, m2, intfm2);
		archi.connect(sw1, intfsw, m2, intfm2);

		archi.connect(op3, intf3, m3, intfm3);
		archi.connect(sw1, intfsw, m3, intfm3);

		archi.connect(op4, intf4, m4, intfm4);
		archi.connect(sw1, intfsw, m4, intfm4);

		return archi;
	}
}
