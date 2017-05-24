***************************************
***             yuv2dat             ***
***************************************

This executable aims at converting a yuv file into a dat file that can be loaded in c6678 memory.
The dat file contains textual hexadecimal bytes that can be loaded in the memory of the c6678 via CCS.

Hexa Data Format:

1651 6 0 0 4
0x04
0x00
0x04
0x00

1651: Magic number
6: data type
0: original address
0: original page number
4: number of elements