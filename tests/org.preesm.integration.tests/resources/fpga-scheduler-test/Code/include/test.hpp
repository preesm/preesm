#ifndef TEST_HPP
#define TEST_HPP

#include "hls_stream.h"

template<int size>
void process1(hls::stream<int>& in, hls::stream<int>& out) {
    #pragma HLS inline
	for(int i=0 ; i<size ; i++) {
		out.write(in.read());
	}
}

template<int size>
void process1double(hls::stream<int>& in, hls::stream<int>& out1, hls::stream<int>& out2) {
    #pragma HLS inline
	for(int i=0 ; i<size ; i++) {
#pragma HLS pipeline II=1
		int data = in.read();
		out1.write(data);
		out2.write(data);
	}
}

template<int size>
void process2(hls::stream<int>& in, hls::stream<int>& out) {
    #pragma HLS inline
	for(int i=0 ; i<size ; i++) {
		out.write(in.read() * 2);
	}
}

template<int size>
void process2double(hls::stream<int>& in1, hls::stream<int>& in2, hls::stream<int>& out) {
    #pragma HLS inline
	for(int i=0 ; i<size ; i++) {
#pragma HLS pipeline II=1
		out.write(in1.read() + in2.read());
	}
}

#endif
