echo "$(tput bold)$(tput setaf 3)...deleting /bin $(tput sgr 0)"
rm -rf bin/
echo "$(tput bold)$(tput setaf 3)...executing CMAKE script$(tput sgr 0)"
source CMakeGCC.sh 
echo "$(tput bold)$(tput setaf 3)...make$(tput sgr 0)"
make
cd Release/

echo "$(tput bold)$(tput setaf 3)...deleting previous 'test_matmul_kernels' $(tput sgr 0)"
cd /home/linaro/flexible_GSIZE_matmul/bin/
rm test_matmul_kernels

echo "$(tput bold)$(tput setaf 3)...coping new executable $(tput sgr 0)"
cp /home/linaro/strategy_matrix_artico3/Code/bin/make/Release/test_matmul_kernels .

echo "$(tput bold)$(tput setaf 3)--------------END SCRIPT------------$(tput sgr 0)"

