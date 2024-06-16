#ifndef PACKING_HPP
#define PACKING_HPP

//Provide packing and unpacking primitives to support token packing in FIFOs implemented in BRAM

template<int IN_W, int OUT_W, typename in_t> void packTokens(
		hls::stream<in_t> &input, hls::stream<ap_uint<OUT_W>> &output) {
	static_assert(OUT_W%IN_W == 0, "Output width should be a multiple of the input width");
#pragma HLS PIPELINE II=OUT_W/IN_W

	ap_uint<OUT_W> buffer;
	for (int i = 0; i < OUT_W / IN_W; i++) {
#pragma HLS UNROLL
		buffer.range(IN_W * (i + 1) - 1, IN_W * i) = input.read();
	}
	output.write(buffer);
}

template<int IN_W, int OUT_W, typename out_t> void unpackTokens(
		hls::stream<ap_uint<IN_W>> &input, hls::stream<out_t> &output) {
	static_assert(IN_W%OUT_W == 0, "Input width should be a multiple of the output width");
#pragma HLS PIPELINE II=IN_W/OUT_W

	ap_uint<IN_W> buffer = input.read();
	for (int i = 0; i < IN_W / OUT_W; i++) {
#pragma HLS UNROLL
		output.write(buffer.range(OUT_W * (i + 1) - 1, OUT_W * i));
	}
}

#endif //PACKING_HPP
