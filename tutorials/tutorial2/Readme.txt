The tutorial demonstrates the hierarchy flattening

This tutorial project contains:

- A PREESM workflow with application flattening, mapping and code generation
- An application with a hierarchical description named LAR
- The architecture model of a PC with 2 cores interconnected by a DMA-like link
- IDL with function prototypes

To launch PREESM on this tutorial:

- Import the tutorial2 project into your Workspace
- Right-click on the workflow. Choose the unique scenario
- Wait for the end of the mapping or stop it from the "Best Latency" editor
- You can change the depth of hierarchy flattening in workflow. 
  -> Right click on HierarchyFlattening block
  -> choose "show parameters" and change the depth
  -> Relaunch the workflow
- You should see that hierarchy flattening depth changes the granularity and thus the mapping precision.
- You can also watch the generated code