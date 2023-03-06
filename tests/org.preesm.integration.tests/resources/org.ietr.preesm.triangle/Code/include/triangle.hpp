#ifndef TRIANGLE_HPP
#define TRIANGLE_HPP

#include "ap_int.h"
#include "hls_stream.h"

template<int M>
void copy(hls::stream<ap_uint<10>> &src, hls::stream<ap_uint<10>> &snk) {
	loop: for (int n = 0; n < M; n++) {
		auto s = src.read();
		snk.write(s);
	}
}

template<int N>
void merge(hls::stream<ap_uint<10>> &src1, hls::stream<ap_uint<10>> &src2, hls::stream<ap_uint<10>> &snk) {
	loop: for (int n = 0; n < N; n++) {
		snk.write(src1.read());
		snk.write(src2.read());
	}
}

#endif //TRIANGLE_HPP
