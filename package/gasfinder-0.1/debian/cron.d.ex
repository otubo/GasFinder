#
# Regular cron jobs for the gasfinder package
#
0 4	* * *	root	[ -x /usr/bin/gasfinder_maintenance ] && /usr/bin/gasfinder_maintenance
