package require try
package require cmdline

set options {
	{name.arg	1	"name of xsa file generated"}
}
set usage "script_hls \[options] \nooptions:"

try {
	array set params [::cmdline::getoptions argv $options $usage]
} trap {CMDLINE USAGE} {msg o} {
	puts $msg
	exit 1
}

open_project vivado/vivado.xpr

write_hw_platform -fixed -include_bit -force -file $params(name)

exit