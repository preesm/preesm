import math

BRAM_16K = 16 * 1024
BRAM_18K = 18 * 1024

def bram_usage(depth, width):
    if (depth * width <= 1024):
        return 0
    
    if (depth <= 512):
        return math.ceil(width / 18)
    
    if (depth <= 4096):
        bram = math.ceil(math.pow(2, math.ceil(math.log2(depth))) * width / BRAM_18K)
        if (depth > 2048):
            if (width == 13 or width == 21 or width == 22 or width > 28):
                bram = math.ceil(bram / 2) * 2
        return bram
    
    if (depth > 4096):
        return math.ceil(math.pow(2, math.ceil(math.log2(depth))) * width / BRAM_16K)

def fifo_max_size(depth, width):
    bram = bram_usage(depth, width)
    if(bram == 0):
        return math.floor(1024 / width)
    return math.pow(2, math.floor(math.log2(bram * BRAM_18K / width)))

def next_smaller_fifo(depth, width):
    bram = bram_usage(depth, width)
    if bram == 0:
        return depth - 1
    candidate_bram = bram
    candidate_depth = depth
    while(candidate_bram >= bram):
        candidate_depth = candidate_depth / 2
        candidate_max_depth = fifo_max_size(candidate_depth, width)
        candidate_bram = bram_usage(candidate_max_depth, width)
    return candidate_max_depth
