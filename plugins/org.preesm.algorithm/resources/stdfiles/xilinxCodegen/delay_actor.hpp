#ifndef DELAY_ACTOR_HPP
#define DELAY_ACTOR_HPP

// Provide delay initialization for FPGA implementation

template<int INIT_S, typename in_t, typename out_t> void delayActor(hls::stream<in_t> &input, hls::stream<out_t> &output) {
#pragma HLS PIPELINE II=1 style=flp
  static int init = 0;
  if (init < INIT_S) {
    output.write((in_t) {});
    init++;
  } else {
    output.write(input.read());
  }
}

#endif //DELAY_ACTOR_HPP